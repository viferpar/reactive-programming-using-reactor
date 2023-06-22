package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;
import reactor.tools.agent.ReactorDebugAgent;

import java.time.Duration;
import java.util.List;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        //given

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        //then
        StepVerifier.create(namesFlux)
                //.expectNext("alex", "ben", "chloe")
                //.expectNextCount(3)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void nameMono() {
        //given

        //when
        var nameMono = fluxAndMonoGeneratorService.nameMono();

        //then
        StepVerifier.create(nameMono)
                // .expectNextCount(1)
                .expectNext("alex")
                .verifyComplete();
    }

    @Test
    void namesFluxMap() {
        //given
        var stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxMap(stringLength);

        //then
        StepVerifier.create(namesFlux)
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFluxInmutability() {
        //given

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxInmutability();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("alex", "ben", "chloe")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMap() {
        //given
        var stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMap(stringLength);

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A","L","E","X","C","H","L","O","E")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMapAsync() {
        //given
        var stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMapAsync(stringLength);

        //then
        StepVerifier.create(namesFlux)
                //.expectNext("A","L","E","X","C","H","L","O","E")
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFluxFlatConcatMap() {
        //given
        var stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatConcatMap(stringLength);

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A","L","E","X","C","H","L","O","E")
                //.expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFluxFlatConcatMapVirtualTimer() {
        //given
        VirtualTimeScheduler.getOrSet();
        var stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatConcatMap(stringLength);

        //then
        StepVerifier.withVirtualTime(() -> namesFlux)
                .thenAwait(Duration.ofSeconds(10))
                .expectNext("A","L","E","X","C","H","L","O","E")
                //.expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesMonoFlatMap() {
        //given
        var stringLength = 3;

        //when
        var namesMono = fluxAndMonoGeneratorService.namesMonoFlatMap(stringLength);

        //then
        StepVerifier.create(namesMono)
                .expectNext(List.of("A","L","E","X"))
                .verifyComplete();
    }

    @Test
    void namesMonoFlatMapMany() {
        //given
        var stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesMonoFlatMapMany(stringLength);

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A","L","E","X")
                .verifyComplete();
    }

    @Test
    void namesFluxTransform() {
        //given
        var stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxTransform(stringLength);

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A","L","E","X","C","H","L","O","E")
                .verifyComplete();
    }

    @Test
    void namesFluxTransform1() {
        //given
        var stringLength = 6;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxTransform(stringLength);

        //then
        StepVerifier.create(namesFlux)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFluxTransformSwitchIfEmpty() {
        //given
        var stringLength = 6;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxTransformSwitchIfEmpty(stringLength);

        //then
        StepVerifier.create(namesFlux)
                .expectNext("D","E","F","A","U","L","T")
                .verifyComplete();
    }

    @Test
    void exploreConcat() {
        //given

        //when
        var concatFlux = fluxAndMonoGeneratorService.exploreConcat();

        //then
        StepVerifier.create(concatFlux)
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    void exploreConcatWith() {
        //given

        //when
        var concatFlux = fluxAndMonoGeneratorService.exploreConcatWith();

        //then
        StepVerifier.create(concatFlux)
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    void exploreConcatWithMono() {
        //given

        //when
        var concatFlux = fluxAndMonoGeneratorService.exploreConcatWithMono();

        //then
        StepVerifier.create(concatFlux)
                .expectNext("A","B")
                .verifyComplete();
    }

    @Test
    void exploreMerge() {
        //given

        //when
        var concatFlux = fluxAndMonoGeneratorService.exploreMerge();

        //then
        StepVerifier.create(concatFlux)
                .expectNext("A","D","B","E","C","F")
                .verifyComplete();
    }

    @Test
    void exploreMergeWith() {
        //given

        //when
        var concatFlux = fluxAndMonoGeneratorService.exploreMergeWith();

        //then
        StepVerifier.create(concatFlux)
                .expectNext("A","D","B","E","C","F")
                .verifyComplete();
    }

    @Test
    void exploreMergeWithMono() {
        //given

        //when
        var concatMono = fluxAndMonoGeneratorService.exploreMergeWithMono();

        //then
        StepVerifier.create(concatMono)
                .expectNext("A","B")
                .verifyComplete();
    }

    @Test
    void exploreMergeSequential() {
        //given

        //when
        var concatFlux = fluxAndMonoGeneratorService.exploreMergeSequential();

        //then
        StepVerifier.create(concatFlux)
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    void exploreZip() {
        //given

        //when
        var concatFlux = fluxAndMonoGeneratorService.exploreZip();

        //then
        StepVerifier.create(concatFlux)
                .expectNext("AD","BE","CF")
                .verifyComplete();
    }

    @Test
    void exploreZip2() {
        //given

        //when
        var concatFlux = fluxAndMonoGeneratorService.exploreZip2();

        //then
        StepVerifier.create(concatFlux)
                .expectNext("AD14","BE25","CF36")
                .verifyComplete();
    }

    @Test
    void exploreZipWith() {
        //given

        //when
        var concatFlux = fluxAndMonoGeneratorService.exploreZipWith();

        //then
        StepVerifier.create(concatFlux)
                .expectNext("AD","BE","CF")
                .verifyComplete();
    }

    @Test
    void exploreZipWithMono() {
        //given

        //when
        var concatMono = fluxAndMonoGeneratorService.exploreZipWithMono();

        //then
        StepVerifier.create(concatMono)
                .expectNext("AB")
                .verifyComplete();
    }

    @Test
    void exceptionFlux() {
        //given

        //when
        var concatFlux = fluxAndMonoGeneratorService.exceptionFlux();

        //then
        StepVerifier.create(concatFlux)
                .expectNext("A","B","C")
                //.expectError(RuntimeException.class)
                .expectErrorMessage("Exception ocurred")
                .verify();
    }

    @Test
    void exploreOnErrorReturn() {
        //given

        //when
        var concatFlux = fluxAndMonoGeneratorService.exploreOnErrorReturn();

        //then
        StepVerifier.create(concatFlux)
                .expectNext("A","B","C","D")
                .verifyComplete();
    }

    @Test
    void exploreOnErrorResumeIllegalArgumentException() {
        //given

        //when
        var concatFlux = fluxAndMonoGeneratorService.exploreOnErrorResume(new IllegalArgumentException("Illegal argument exception"));

        //then
        StepVerifier.create(concatFlux)
                .expectNext("A","B","C", "D","E","F")
                .verifyComplete();
    }

    @Test
    void exploreOnErrorResumeOtherException() {
        //given

        //when
        var concatFlux = fluxAndMonoGeneratorService.exploreOnErrorResume(new RuntimeException("Error occured"));

        //then
        StepVerifier.create(concatFlux)
                .expectNext("A","B","C")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void exploreOnErrorContinue() {
        //given

        //when
        var concatFlux = fluxAndMonoGeneratorService.exploreOnErrorContinue();

        //then
        StepVerifier.create(concatFlux)
                .expectNext("A", "C", "D")
                .verifyComplete();
    }

    @Test
    void exploreOnErrorMap() {
        //given

        //when
        var concatFlux = fluxAndMonoGeneratorService.exploreOnErrorMap();

        //then
        StepVerifier.create(concatFlux)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void exploreDoOnError() {
        //given

        //when
        var concatFlux = fluxAndMonoGeneratorService.exploreDoOnError();

        //then
        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C")
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void exploreGenerate() {
        //given

        //when
        var concatFlux = fluxAndMonoGeneratorService.exploreGenerate().log();

        //then
        StepVerifier.create(concatFlux)
                .expectNextCount(10)
                .verifyComplete();
    }
    @Test
    void exploreCreate() {
        //given

        //when
        var flux = fluxAndMonoGeneratorService.exploreCreate().log();

        //then
        StepVerifier.create(flux)
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void exploreCreateMono() {
        //given

        //when
        var mono = fluxAndMonoGeneratorService.exploreCreateMono().log();

        //then
        StepVerifier.create(mono)
                .expectNext("alex")
                .verifyComplete();
    }

    @Test
    void exploreHandle() {
        //given

        //when
        var flux = fluxAndMonoGeneratorService.exploreHandle().log();

        //then
        StepVerifier.create(flux)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void exploreOnErrorMapDebug() {
        //given
        Hooks.onOperatorDebug();
        var e = new RuntimeException("Not a valid state");

        //when
        var flux = fluxAndMonoGeneratorService.exploreOnErrorMapDebug(e).log();

        //then
        StepVerifier.create(flux)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void exploreOnErrorMapReactorDebugAgent() {
        //given
        ReactorDebugAgent.init();
        ReactorDebugAgent.processExistingClasses();
        var e = new RuntimeException("Not a valid state");

        //when
        var flux = fluxAndMonoGeneratorService.exploreOnErrorMapReactorDebugAgent(e).log();

        //then
        StepVerifier.create(flux)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }
}
