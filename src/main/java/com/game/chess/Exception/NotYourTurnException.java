package com.game.chess.Exception;

public class NotYourTurnException extends RuntimeException {
    public NotYourTurnException(String message) {

        super(message);
    }
}
