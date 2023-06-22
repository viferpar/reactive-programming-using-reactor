package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoSchedulersServiceTest {

    FluxAndMonoSchedulersService fluxAndMonoSchedulersService = new FluxAndMonoSchedulersService();

    @Test
    void explorePublishOn() {
        //given

        //when
        var flux = fluxAndMonoSchedulersService.explorePublishOn();

        //then
        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void exploreSuscribeOn() {
        //given

        //when
        var flux = fluxAndMonoSchedulersService.exploreSuscribeOn();

        //then
        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void exploreParallel() {
        //given

        //when
        var flux = fluxAndMonoSchedulersService.exploreParallel();

        //then
        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void exploreParallelUsingFlatmap() {
        //given

        //when
        var flux = fluxAndMonoSchedulersService.exploreParallelUsingFlatmap();

        //then
        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void exploreParallelUsingFlatmap1() {
        //given

        //when
        var flux = fluxAndMonoSchedulersService.exploreParallelUsingFlatmap1();

        //then
        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void exploreParallelUsingFlatmapSequential() {
        //given

        //when
        var flux = fluxAndMonoSchedulersService.exploreParallelUsingFlatmapSequential();

        //then
        StepVerifier.create(flux)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }
}
