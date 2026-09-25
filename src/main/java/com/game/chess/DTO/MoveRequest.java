package com.game.chess.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MoveRequest {

    @NotNull(message = "gameId is required")
    private Long gameId;

    @NotNull(message = "playerId is required")
    private Long playerId;

    @NotBlank(message = "fromSquare is required")
    @Pattern(regexp = "^[a-h][1-8]$", message = "fromSquare must look like 'e2'")
    private String fromSquare;

    @NotBlank(message = "toSquare is required")
    @Pattern(regexp = "^[a-h][1-8]$", message = "toSquare must look like 'e4'")
    private String toSquare;

    @NotBlank(message = "pieceMoved is required")
    private String pieceMoved;

    private String capturedPiece;
}