package com.game.chess.Config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ResilienceConfig {



    @Bean
    public CircuitBreaker bankCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .permittedNumberOfCallsInHalfOpenState(2)
                .build();
        return CircuitBreaker.of("bankVerification", config);
    }


    @Bean
    public TimeLimiter bankTimeLimiter(@Value("${bank.verification.timeout-seconds:3}") long timeoutSeconds) {
        return TimeLimiter.of(TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(timeoutSeconds))
                .cancelRunningFuture(true)
                .build());
    }



    @Bean(destroyMethod = "shutdownNow")
    public ExecutorService bankVerificationExecutor() {
        AtomicInteger counter = new AtomicInteger();
        return Executors.newFixedThreadPool(8, runnable -> {
            Thread thread = new Thread(runnable, "bank-verification-" + counter.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        });
    }
}