package com.game.chess.Exception;

// InsufficientBalanceException.java (HTTP 400)
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}