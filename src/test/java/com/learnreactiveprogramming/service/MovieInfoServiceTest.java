package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieInfoServiceTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();

    MovieInfoService movieInfoService = new MovieInfoService(webClient);

    @Test
    void retrieveAllMovieInfoRestClient() {
        var movies = movieInfoService.retrieveAllMovieInfoRestClient().log();

        StepVerifier.create(movies)
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    void retrieveAllMovieInfoByIdRestClient() {
        var movies = movieInfoService.retrieveAllMovieInfoByIdRestClient(1L).log();

        StepVerifier.create(movies)
                .expectNextMatches(movieInfo -> movieInfo.getName().equals("Batman Begins"))
                .verifyComplete();
    }
}
