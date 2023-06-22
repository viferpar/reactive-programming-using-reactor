package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.MovieException;
import com.learnreactiveprogramming.exception.NetworkException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieReactiveServiceMockTest {

    @Mock
    private MovieInfoService movieInfoService;
    @Mock
    private ReviewService reviewService;

    @InjectMocks
    MovieReactiveService reactiveService;

    @Test
    void getAllMovies() {
        // given
        when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong())).thenCallRealMethod();

        // when
        var moviesFlux = reactiveService.getAllMovies();

        // then
        StepVerifier.create(moviesFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void getAllMoviesError() {
        // given
        var errorMessage = "Exception occured in ReviewService";
        when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new RuntimeException(errorMessage));

        // when
        var moviesFlux = reactiveService.getAllMovies();

        // then
        StepVerifier.create(moviesFlux)
                //.expectError(MovieException.class)
                .expectErrorMessage(errorMessage)
                .verify();
    }

    @Test
    void getAllMoviesErrorRetry() {
        // given
        var errorMessage = "Exception occured in ReviewService";
        when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new RuntimeException(errorMessage));

        // when
        var moviesFlux = reactiveService.getAllMoviesRetry();

        // then
        StepVerifier.create(moviesFlux)
                //.expectError(MovieException.class)
                .expectErrorMessage(errorMessage)
                .verify();

        verify(reviewService, times(4)).retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMoviesErrorRetryWhenNetworkException() {
        // given
        var errorMessage = "Exception occured in ReviewService";
        when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new NetworkException(errorMessage));

        // when
        var moviesFlux = reactiveService.getAllMoviesRetryWhen();

        // then
        StepVerifier.create(moviesFlux)
                //.expectError(MovieException.class)
                .expectErrorMessage(errorMessage)
                .verify();

        verify(reviewService, times(4)).retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMoviesErrorRetryWhenRuntimeException() {
        // given
        var errorMessage = "Exception occured in ReviewService";
        when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new RuntimeException(errorMessage));

        // when
        var moviesFlux = reactiveService.getAllMoviesRetryWhen();

        // then
        StepVerifier.create(moviesFlux)
                //.expectError(MovieException.class)
                .expectErrorMessage(errorMessage)
                .verify();

        verify(reviewService, times(1)).retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMoviesRepeat() {
        // given
        var errorMessage = "Exception occured in ReviewService";
        when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong())).thenCallRealMethod();

        // when
        var moviesFlux = reactiveService.getAllMoviesRepeat();

        // then
        StepVerifier.create(moviesFlux)
                .expectNextCount(6)
                .thenCancel()
                .verify();

        verify(reviewService, times(6)).retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMoviesRepeatN() {
        // given
        var errorMessage = "Exception occured in ReviewService";
        when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong())).thenCallRealMethod();
        var noOfTimes = 2L;

        // when
        var moviesFlux = reactiveService.getAllMoviesRepeatN(noOfTimes);

        // then
        StepVerifier.create(moviesFlux)
                .expectNextCount(9)
                .verifyComplete();

        verify(reviewService, times(9)).retrieveReviewsFlux(isA(Long.class));
    }
}
