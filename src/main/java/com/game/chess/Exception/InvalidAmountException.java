package com.game.chess.Exception;
// InvalidAmountException.java (HTTP 400)
public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) {
        super(message);
    }
}