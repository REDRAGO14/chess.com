package com.game.chess.DTO;

import com.game.chess.Model.PaymentStatus;
import com.game.chess.Model.PaymentTransaction;

import java.math.BigDecimal;

public record PaymentStatusResponse(String txRef, PaymentStatus status, BigDecimal amount) {

    public static PaymentStatusResponse from(PaymentTransaction tx) {
        return new PaymentStatusResponse(tx.getTxRef(), tx.getStatus(), tx.getAmount());
    }
}