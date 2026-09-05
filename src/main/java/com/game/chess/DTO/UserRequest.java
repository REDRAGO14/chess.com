package com.game.chess.DTO;

public record UserRequest (
        String userName,
        String email,
        String password
){}
