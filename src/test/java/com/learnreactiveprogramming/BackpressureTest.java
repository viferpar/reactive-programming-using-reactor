package com.learnreactiveprogramming;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class BackpressureTest {

    @Test
    void testBackPressure() {
        var numberRange = Flux.range(1, 100).log();

        //numberRange.subscribe(num -> log.info("Number is: {}", num));

        numberRange.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(2);
            }

            @Override
            protected void hookOnNext(Integer value) {
                log.info("hookOnNext: {}", value);
                if (value == 2) {
                    cancel();
                }
            }

            @Override
            protected void hookOnComplete() {
                // super.hookOnComplete();
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                // super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                log.info("Inside OnCancel");
            }
        });
    }

    @Test
    void testBackPressure1() throws InterruptedException {
        var numberRange = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);
        numberRange.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(2);
            }

            @Override
            protected void hookOnNext(Integer value) {
                log.info("hookOnNext: {}", value);
                if (value % 2 == 0 || value < 50) {
                    request(2);
                } else {
                    cancel();
                }
            }

            @Override
            protected void hookOnCancel() {
                log.info("Inside OnCancel");
                latch.countDown();
            }
        });

        assertTrue(latch.await(5L, TimeUnit.SECONDS));
    }

    @Test
    void testBackPressureDrop() throws InterruptedException {
        var numberRange = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);
        numberRange
                .onBackpressureDrop(item -> {
                    log.info("Dropped item is: {}", item);
                })
                .subscribe(new BaseSubscriber<>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(2);
            }

            @Override
            protected void hookOnNext(Integer value) {
                log.info("hookOnNext: {}", value);
                if(value == 2){
                    hookOnCancel();
                }
            }

            @Override
            protected void hookOnCancel() {
                log.info("Inside OnCancel");
                latch.countDown();
            }
        });

        assertTrue(latch.await(5L, TimeUnit.SECONDS));
    }

    @Test
    void testBackPressureBuffer() throws InterruptedException {
        var numberRange = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);
        numberRange
                .onBackpressureBuffer(10, item -> {
                    log.info("Last buffered element is: {}", item);
                })
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(2);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        log.info("hookOnNext: {}", value);
                        if (value < 50) {
                            request(1);
                        } else {
                            hookOnCancel();
                        }
                    }

                    @Override
                    protected void hookOnCancel() {
                        log.info("Inside OnCancel");
                        latch.countDown();
                    }
                });

        assertTrue(latch.await(5L, TimeUnit.SECONDS));
    }

    @Test
    void testBackPressureError() throws InterruptedException {
        var numberRange = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);
        numberRange
                .onBackpressureError()
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(2);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        log.info("hookOnNext: {}", value);
                        if (value < 50) {
                            request(1);
                        } else {
                            hookOnCancel();
                        }
                    }

                    @Override
                    protected void hookOnCancel() {
                        log.info("Inside OnCancel");
                        latch.countDown();
                    }

                    @Override
                    protected void hookOnError(Throwable error) {
                        log.error("Exception is: ", error);
                    }
                });

        assertTrue(latch.await(5L, TimeUnit.SECONDS));
    }
}
