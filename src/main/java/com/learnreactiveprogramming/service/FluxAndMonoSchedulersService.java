package com.learnreactiveprogramming.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

@Slf4j
public class FluxAndMonoSchedulersService {

    static List<String> namesList = List.of("alex", "ben", "chloe");
    static List<String> namesList1 = List.of("adam", "jill", "jack");

    public Flux<String> explorePublishOn() {
        var namesFlux = Flux.fromIterable(namesList)
                .publishOn(Schedulers.boundedElastic())
                .map(this::upperCase)
                .map(s -> {
                    log.info("Name is: {}", s);
                    return s;
                })
                .log();

        var namesFlux1 = Flux.fromIterable(namesList1)
                .publishOn(Schedulers.boundedElastic())
                .map(this::upperCase)
                .map(s -> {
                    log.info("Name is: {}", s);
                    return s;
                })
                .log();

        return namesFlux.mergeWith(namesFlux1);
    }

    public Flux<String> exploreSuscribeOn() {
        var namesFlux = flux1(namesList)
                .subscribeOn(Schedulers.boundedElastic())
                .log();

        var namesFlux1 = flux1(namesList1)
                .subscribeOn(Schedulers.boundedElastic())
                .map(s -> {
                    log.info("Name is: {}", s);
                    return s;
                })
                .log();

        return namesFlux.mergeWith(namesFlux1);
    }

    public ParallelFlux<String> exploreParallel() {
        return Flux.fromIterable(namesList)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(this::upperCase)
                .log();
    }

    public Flux<String> exploreParallelUsingFlatmap() {
        return Flux.fromIterable(namesList)
                .flatMap(name -> Mono.just(name)
                        .map(this::upperCase)
                        .subscribeOn(Schedulers.parallel()))
                .log();
    }

    public Flux<String> exploreParallelUsingFlatmapSequential() {
        return Flux.fromIterable(namesList)
                .flatMapSequential(name -> Mono.just(name)
                        .map(this::upperCase)
                        .subscribeOn(Schedulers.parallel()))
                .log();
    }

    public Flux<String> exploreParallelUsingFlatmap1() {
        var namesFlux = Flux.fromIterable(namesList)
                .flatMap(name -> Mono.just(name)
                        .map(this::upperCase)
                        .subscribeOn(Schedulers.parallel()))
                .log();

        var namesFlux1 = Flux.fromIterable(namesList1)
                .flatMap(name -> Mono.just(name)
                        .map(this::upperCase)
                        .subscribeOn(Schedulers.parallel()))
                .log();

        return namesFlux.mergeWith(namesFlux1);
    }

    private Flux<String> flux1(List<String> namesList) {
        return Flux.fromIterable(namesList)
                .map(this::upperCase);
    }

    private String upperCase(String name) {
        delay(1000);
        return name.toUpperCase();
    }

}
