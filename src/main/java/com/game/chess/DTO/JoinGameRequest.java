package com.game.chess.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JoinGameRequest {

    @NotNull(message = "UserId is required")
    @Positive(message = "UserId must be positive")

    private Long userId;

}
