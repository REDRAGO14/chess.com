package com.game.chess.DTO;

import com.game.chess.Model.EscrowStatus;

import java.math.BigDecimal;

public record StakeResponse(
        Long escrowId,
        Long gameId,
        BigDecimal amount,
        EscrowStatus status,
        BigDecimal balanceAfter
) {}