package com.game.chess.DTO;

import com.game.chess.Model.VerificationResult;

public record BankVerifyResponse(
        VerificationResult
        result,
        String circuitState,
        long millis) {}