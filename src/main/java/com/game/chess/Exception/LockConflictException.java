package com.game.chess.Exception;

// LockConflictException.java (HTTP 409)
public class LockConflictException extends RuntimeException {
    public LockConflictException(String message) {
        super(message);
    }
}