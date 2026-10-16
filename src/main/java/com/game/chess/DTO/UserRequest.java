package com.game.chess.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest (
        @NotBlank(message = "Username is required")
        @Size(max = 16, min = 3 , message = "USER NAME MUST BE BETWEEN 3 TO 16 CHARACTER")
        String userName,
        @NotBlank(message = "Email is required")
        @Email(message = "FILL VALID EMAIL")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 6 , message = "PASSWORD MUST GREATER THAN 6")
        String password
){

}
