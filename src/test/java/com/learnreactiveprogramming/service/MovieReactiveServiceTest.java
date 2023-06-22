package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieReactiveServiceTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();

    private MovieInfoService movieInfoService = new MovieInfoService(webClient);
    private ReviewService reviewService = new ReviewService(webClient);
    private RevenueService revenueService = new RevenueService();

    MovieReactiveService movieReactiveService = new MovieReactiveService(movieInfoService, reviewService, revenueService);

    @Test
    void getAllMovies() {
        // given

        // when
        var moviesFlux = movieReactiveService.getAllMovies().log();

        // then
        StepVerifier.create(moviesFlux)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals(2, movie.getReviewList().size());
                })
                .assertNext(movie -> {
                    assertEquals("The Dark Knight", movie.getMovieInfo().getName());
                    assertEquals(2, movie.getReviewList().size());
                })
                .assertNext(movie -> {
                    assertEquals("Dark Knight Rises", movie.getMovieInfo().getName());
                    assertEquals(2, movie.getReviewList().size());
                })
                .verifyComplete();
    }

    @Test
    void getMovieById() {

        // given
        final var movieId = 100L;

        // when
        var moviesFlux = movieReactiveService.getMovieById(movieId).log();

        // then
        StepVerifier.create(moviesFlux)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals(2, movie.getReviewList().size());
                })
                .verifyComplete();
    }

    @Test
    void getMovieByIdWithRevenue() {

        // given
        final var movieId = 100L;

        // when
        var moviesFlux = movieReactiveService.getMovieByIdWithRevenue(movieId).log();

        // then
        StepVerifier.create(moviesFlux)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals(2, movie.getReviewList().size());
                    assertNotNull(movie.getRevenue());
                })
                .verifyComplete();
    }

    @Test
    void getAllMoviesRestClient() {
        // given

        // when
        var moviesFlux = movieReactiveService.getAllMoviesRestClient().log();

        // then
        StepVerifier.create(moviesFlux)
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    void getMovieByIdRestClient() {

        // given
        final var movieId = 1L;

        // when
        var moviesFlux = movieReactiveService.getMovieByIdRestClient(movieId).log();

        // then
        StepVerifier.create(moviesFlux)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals(1, movie.getReviewList().size());
                })
                .verifyComplete();
    }
}
