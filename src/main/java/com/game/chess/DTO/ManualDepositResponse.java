package com.game.chess.DTO;

import com.game.chess.Model.DepositStatus;

// ManualDepositResponse.java
public record ManualDepositResponse(
        Long depositId,
        DepositStatus status,
        String message
) {}