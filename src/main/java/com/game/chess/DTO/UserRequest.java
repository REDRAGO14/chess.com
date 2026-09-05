package com.game.chess.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserRequest (
        @Size(max = 16, min = 3 , message = "USER NAME MUST BE BETWEEN 3 TO 16 CHARACTER")
        String userName,
        @Email(message = "FILL VALID EMAIL")
        String email,
        @Size(min = 6 , message = "PASSWORD MUST GREATER THAN 6")
        String password
){}
