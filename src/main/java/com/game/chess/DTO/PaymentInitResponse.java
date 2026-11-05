package com.game.chess.DTO;

public record PaymentInitResponse(
        String txRef, String checkoutUrl) {}