package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

@Slf4j
public class FluxAndMonoGeneratorService {

    public static final Random RANDOM = new Random();
    static List<String> namesList = List.of("alex", "ben", "chloe");

    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

        fluxAndMonoGeneratorService.namesFlux()
                .subscribe(name -> log.info("Name is: {}", name));

        fluxAndMonoGeneratorService.nameMono()
                .subscribe(name -> log.info("Name is: {}", name));
    }

    public static List<String> names() {
        delay(1000);
        return namesList;
    }

    public Flux<String> namesFlux() {
        return Flux.fromIterable(namesList).log();
    }

    public Mono<String> nameMono() {
        return Mono.just("alex").log();
    }

    public Flux<String> namesFluxMap(int stringLength) {
        return Flux.fromIterable(namesList)
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .map(s -> String.format("%s-%s", s.length(), s))
                .doOnNext(name -> log.info("Name is: {}", name))
                .doOnSubscribe(s -> log.info("Subscription is: {}", s))
                .doOnComplete(() -> log.info("Inside the complete callback"))
                .doFinally(signal -> log.info("The signal is: {}", signal))
                .log();
    }

    public Flux<String> namesFluxInmutability() {
        var namesFlux = Flux.fromIterable(namesList).log();
        namesFlux().map(String::toUpperCase);
        return namesFlux;
    }

    public Flux<String> namesFluxFlatMap(int stringLength) {
        return Flux.fromIterable(namesList)
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitString)
                .log();
    }

    public Flux<String> namesFluxFlatMapAsync(int stringLength) {
        return Flux.fromIterable(namesList)
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringWithDelay)
                .log();
    }

    public Flux<String> namesFluxFlatConcatMap(int stringLength) {
        return Flux.fromIterable(namesList)
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .concatMap(this::splitStringWithDelay)
                .log();
    }

    public Mono<List<String>> namesMonoFlatMap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono)
                .log();
    }

    public Flux<String> namesMonoFlatMapMany(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(this::splitString)
                .log();
    }

    public Flux<String> namesFluxTransform(int stringLength) {
        UnaryOperator<Flux<String>> filterMap = name -> name
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);
        return Flux.fromIterable(namesList)
                .transform(filterMap)
                .flatMap(this::splitString)
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> namesFluxTransformSwitchIfEmpty(int stringLength) {
        UnaryOperator<Flux<String>> filterMap = name -> name
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitString);

        var defaultFlux = Flux.just("default").transform(filterMap);

        return Flux.fromIterable(namesList)
                .transform(filterMap)
                .switchIfEmpty(defaultFlux)
                .log();
    }

    public Flux<String> splitString(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public Mono<List<String>> splitStringMono(String name) {
        var charArray = name.split("");
        return Mono.just(List.of(charArray));
    }

    public Flux<String> splitStringWithDelay(String name) {
        var charArray = name.split("");
        var delay = RANDOM.nextInt(1000);
        return Flux.fromArray(charArray).delayElements(Duration.ofMillis(delay));
    }

    public Flux<String> exploreConcat() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return Flux.concat(abcFlux, defFlux).log();
    }

    public Flux<String> exploreConcatWith() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return abcFlux.concatWith(defFlux).log();
    }

    public Flux<String> exploreConcatWithMono() {
        var aMono = Mono.just("A");
        var bMono = Mono.just("B");

        return aMono.concatWith(bMono).log();
    }

    public Flux<String> exploreMerge() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(150));

        return Flux.merge(abcFlux, defFlux).log();
    }

    public Flux<String> exploreMergeWith() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(150));

        return abcFlux.mergeWith(defFlux);
    }

    public Flux<String> exploreMergeWithMono() {
        var abcMono = Mono.just("A");
        var defMono = Mono.just("B");

        return abcMono.mergeWith(defMono);
    }

    public Flux<String> exploreMergeSequential() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(150));

        return Flux.mergeSequential(abcFlux, defFlux).log();
    }

    public Flux<String> exploreZip() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return Flux.zip(abcFlux, defFlux, (first, second) -> first + second).log();
    }

    public Flux<String> exploreZip2() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        var oneTwoThree = Flux.just("1", "2", "3");
        var fourFiveSix = Flux.just("4", "5", "6");

        return Flux.zip(abcFlux, defFlux, oneTwoThree, fourFiveSix)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4())
                .log();
    }

    public Flux<String> exploreZipWith() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return abcFlux.zipWith(defFlux, (first, second) -> first + second).log();
    }

    public Mono<String> exploreZipWithMono() {
        var abcMono = Mono.just("A");
        var defMono = Mono.just("B");

        return abcMono.zipWith(defMono)
                .map(t2 -> t2.getT1() + t2.getT2())
                .log();
    }

    public Flux<String> exceptionFlux() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception ocurred")))
                .concatWith(Flux.just("D")).log();
    }

    public Flux<String> exploreOnErrorReturn() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalArgumentException("Exception ocurred")))
                .onErrorReturn("D");
    }

    public Flux<String> exploreOnErrorResume(Exception e) {
        var recoveryFlux = Flux.just("D", "E", "F");
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(e))
                .onErrorResume(ex -> {
                    log.error("Exception is: ", ex);
                    if (ex instanceof IllegalArgumentException) {
                        return recoveryFlux;
                    } else {
                        return Flux.error(ex);
                    }
                });
    }

    public Flux<String> exploreOnErrorContinue() {
        return Flux.just("A", "B", "C")
                .map(name -> {
                    if (name.equals("B")) {
                        throw new IllegalArgumentException("Exception occured");
                    }
                    return name;
                })
                .concatWith(Flux.just("D"))
                .onErrorContinue((ex, name) -> {
                    log.error("Exception is: ", ex);
                    log.info("Name is: {}", name);
                });
    }

    public Flux<String> exploreOnErrorMap() {
        return Flux.just("A", "B", "C")
                .map(name -> {
                    if (name.equals("B")) {
                        throw new IllegalArgumentException("Exception occured");
                    }
                    return name;
                })
                .concatWith(Flux.just("D"))
                .onErrorMap(ex -> {
                    log.error("Exception is: ", ex);
                    return new ReactorException(ex, ex.getMessage());
                });
    }

    public Flux<String> exploreDoOnError() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalArgumentException("Exception ocurred")))
                .doOnError(ex -> log.error("Exception is: ", ex));
    }

    public Flux<Integer> exploreGenerate() {
        return Flux.generate(() -> 1, (state, sink) -> {
            sink.next(state * 2);

            if (state == 10) {
                sink.complete();
            }

            return state + 1;
        });
    }

    public Flux<String> exploreCreate() {
//        return Flux.create(sink -> {
//            names().forEach(sink::next);
//            sink.complete();
//        });
        return Flux.create(sink -> {
            CompletableFuture.supplyAsync(FluxAndMonoGeneratorService::names)
                    .thenAccept(names -> names.forEach(sink::next))
                    .thenRun(() -> sendEvents(sink));
        });
    }

    private void sendEvents(FluxSink<String> sink) {
        CompletableFuture.supplyAsync(FluxAndMonoGeneratorService::names)
                .thenAccept(names -> names.forEach(name -> {
                    sink.next(name);
                    sink.next(name);
                }))
                .thenRun(sink::complete);
    }

    public Mono<String> exploreCreateMono() {
        return Mono.create(sink -> sink.success("alex"));
    }

    public Flux<String> exploreHandle() {
        return namesFlux().handle((name, sink) -> {
            if (name.length() > 3) {
                sink.next(name.toUpperCase());
            }
        });
    }

    public Flux<String> exploreOnErrorMapDebug(Exception e) {
        return Flux.just("A")
                .concatWith(Flux.error(e))
                .checkpoint("errorSpot")
                .onErrorMap(ex -> {
                    log.error("Exception is: ", ex);
                    return new ReactorException(ex, ex.getMessage());
                });
    }

    public Flux<String> exploreOnErrorMapReactorDebugAgent(Exception e) {
        return Flux.just("A")
                .concatWith(Flux.error(e))
                .onErrorMap(ex -> {
                    log.error("Exception is: ", ex);
                    return new ReactorException(ex, ex.getMessage());
                });
    }
}
