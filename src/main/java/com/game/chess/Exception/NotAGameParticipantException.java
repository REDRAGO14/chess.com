package com.game.chess.Exception;

public class NotAGameParticipantException extends RuntimeException {
    public NotAGameParticipantException(String message) {
        super(message);
    }
}
