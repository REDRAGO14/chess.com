package com.game.chess.Exception;

public class InvalidMoveException extends RuntimeException {
    public InvalidMoveException(String message) {

        super(message);
    }
}
