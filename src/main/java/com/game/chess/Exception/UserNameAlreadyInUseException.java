package com.game.chess.Exception;

public class UserNameAlreadyInUseException extends RuntimeException{
    public UserNameAlreadyInUseException(String msg){
        super(msg);
    }
}
