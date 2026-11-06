package com.game.chess.Service;

import com.game.chess.Model.VerificationResult;
import java.math.BigDecimal;

@FunctionalInterface
public interface BankVerificationClient {
    VerificationResult check(String bankReference, BigDecimal amount);
}