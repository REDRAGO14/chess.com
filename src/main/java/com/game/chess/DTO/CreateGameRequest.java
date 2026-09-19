package com.game.chess.DTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateGameRequest {

    @NotNull(message = "White Player Id is required")
    @Positive(message = "White Player Id must be positive")
    private long whitePlayerId;

    @NotNull(message = "Black player ID is required")
    @Positive(message = "Black Player ID must be positive")
    private long blackPlayerId;

    public CreateGameRequest(long whitePlayerId, long blackPlayerId) {
      this.whitePlayerId = whitePlayerId;
      this.blackPlayerId = blackPlayerId;
    }


}
