package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.Revenue;
import com.learnreactiveprogramming.domain.Review;
import com.learnreactiveprogramming.exception.MovieException;
import com.learnreactiveprogramming.exception.NetworkException;
import com.learnreactiveprogramming.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.List;

@Slf4j
public class MovieReactiveService {

    private MovieInfoService movieInfoService;
    private ReviewService reviewService;
    private RevenueService revenueService;

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService, RevenueService revenueService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
        this.revenueService = revenueService;
    }

    public Flux<Movie> getAllMovies() {
        var moviesInfo = movieInfoService.retrieveMoviesFlux();
        return moviesInfo
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviews = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();
                    return reviews.map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(ex -> {
                            log.error("Exception is: ", ex);
                            throw new MovieException(ex.getMessage());
                        }
                )
                .log();
    }

    public Flux<Movie> getAllMoviesRestClient() {
        var moviesInfo = movieInfoService.retrieveAllMovieInfoRestClient();
        return moviesInfo
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviews = reviewService.retrieveReviewsFluxRestClient(movieInfo.getMovieInfoId()).collectList();
                    return reviews.map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(ex -> {
                            log.error("Exception is: ", ex);
                            throw new MovieException(ex.getMessage());
                        }
                )
                .log();
    }

    public Flux<Movie> getAllMoviesRetry() {
        var moviesInfo = movieInfoService.retrieveMoviesFlux();
        return moviesInfo
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviews = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();
                    return reviews.map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(ex -> {
                            log.error("Exception is: ", ex);
                            throw new MovieException(ex.getMessage());
                        }
                )
                .retry(3)
                .log();
    }

    public Flux<Movie> getAllMoviesRetryWhen() {
        var moviesInfo = movieInfoService.retrieveMoviesFlux();
        return moviesInfo
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviews = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();
                    return reviews.map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(ex -> {
                            log.error("Exception is: ", ex);
                            if (ex instanceof NetworkException) {
                                throw new MovieException(ex.getMessage());
                            } else {
                                throw new ServiceException(ex.getMessage());
                            }
                        }
                )
                .retryWhen(getRetryBackoffSpec())
                .log();
    }

    public Flux<Movie> getAllMoviesRepeat() {
        var moviesInfo = movieInfoService.retrieveMoviesFlux();
        return moviesInfo
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviews = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();
                    return reviews.map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(ex -> {
                            log.error("Exception is: ", ex);
                            if (ex instanceof NetworkException) {
                                throw new MovieException(ex.getMessage());
                            } else {
                                throw new ServiceException(ex.getMessage());
                            }
                        }
                )
                .repeat()
                .log();
    }

    public Flux<Movie> getAllMoviesRepeatN(long n) {
        var moviesInfo = movieInfoService.retrieveMoviesFlux();
        return moviesInfo
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviews = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();
                    return reviews.map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(ex -> {
                            log.error("Exception is: ", ex);
                            if (ex instanceof NetworkException) {
                                throw new MovieException(ex.getMessage());
                            } else {
                                throw new ServiceException(ex.getMessage());
                            }
                        }
                )
                .repeat(n)
                .log();
    }

    private RetryBackoffSpec getRetryBackoffSpec() {
        return Retry.backoff(3, Duration.ofMillis(500))
                .filter(MovieException.class::isInstance)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                    throw Exceptions.propagate(retrySignal.failure());
                });
    }

    public Mono<Movie> getMovieById(long movieId) {
        var movieMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewsMono = reviewService.retrieveReviewsFlux(movieId).collectList();

        return Mono.zip(movieMono, reviewsMono, Movie::new);
    }

    public Mono<Movie> getMovieByIdRestClient(long movieId) {
        var movieMono = movieInfoService.retrieveAllMovieInfoByIdRestClient(movieId);
        var reviewsMono = reviewService.retrieveReviewsFluxRestClient(movieId).collectList();

        return Mono.zip(movieMono, reviewsMono, Movie::new);
    }

    public Mono<Movie> getMovieByIdWithRevenue(long movieId) {

        var movieMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewsMono = reviewService.retrieveReviewsFlux(movieId).collectList();

        var revenueMono = Mono.fromCallable(() -> revenueService.getRevenue(movieId))
                .subscribeOn(Schedulers.boundedElastic());

        return movieMono
                .zipWith(reviewsMono, Movie::new)
                .zipWith(revenueMono, (movie, revenue) -> {
                    movie.setRevenue(revenue);
                    return movie;
                });


    }

}
