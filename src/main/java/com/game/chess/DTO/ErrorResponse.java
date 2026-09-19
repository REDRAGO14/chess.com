package com.game.chess.DTO;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        int status,
        String errorCode,
        String msg,
        String path,
        Instant timeStamp,
        Map<String, String> validationErrors
) {
    public ErrorResponse(int status, String errorCode, String msg, String path){
        this(status,errorCode,msg,path,Instant.now(),null);
    }
    public ErrorResponse(int status, String errorCode, String msg, String path, Map<String , String> validationErrors){
        this(status,errorCode,msg,path,Instant.now(),validationErrors);
    }
}
