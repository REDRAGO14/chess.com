package com.game.chess.Service;

import com.game.chess.Model.VerificationResult;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.timelimiter.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
public class ResilientVerificationService {

    private final BankVerificationClient bankClient;
    private final CircuitBreaker circuitBreaker;
    private final TimeLimiter timeLimiter;
    private final ExecutorService executor;

    public ResilientVerificationService(BankVerificationClient bankClient,
                                        CircuitBreaker bankCircuitBreaker,
                                        TimeLimiter bankTimeLimiter,
                                        @Qualifier("bankVerificationExecutor") ExecutorService executor) {
        this.bankClient = bankClient;
        this.circuitBreaker = bankCircuitBreaker;
        this.timeLimiter = bankTimeLimiter;
        this.executor = executor;
    }

    public VerificationResult verify(String bankReference, BigDecimal amount) {


        Callable<VerificationResult> limitedCall = () -> timeLimiter.executeFutureSupplier(
                () -> executor.submit((Callable<VerificationResult>) () -> bankClient.check(bankReference, amount)));

        try {

            return circuitBreaker.executeCallable(limitedCall);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return verificationFallback(e);
        } catch (Exception e) {

            return verificationFallback(e);
        }
    }


    private VerificationResult verificationFallback(Exception cause) {
        log.warn("Bank verification unavailable ({}): sending the deposit to admin review",
                cause.getClass().getSimpleName());
        return VerificationResult.UNAVAILABLE;
    }

    public String circuitState() {
        return circuitBreaker.getState().name();
    }
}