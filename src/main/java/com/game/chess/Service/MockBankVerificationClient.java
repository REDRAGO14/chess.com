package com.game.chess.Service;

import com.game.chess.Model.VerificationResult;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class MockBankVerificationClient implements BankVerificationClient {

    static final long SLOW_MILLIS = 5000;

    @Override
    public VerificationResult check(String bankReference, BigDecimal amount) {
        String ref = bankReference == null ? "" : bankReference.trim().toUpperCase();

        if (ref.isEmpty() || ref.startsWith("REJECT")) return VerificationResult.REJECTED;
        if (ref.startsWith("FAIL")) throw new IllegalStateException("Mock bank is down");
        if (ref.startsWith("SLOW")) sleep(SLOW_MILLIS);
        return VerificationResult.VERIFIED;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for the bank");
        }
    }
}