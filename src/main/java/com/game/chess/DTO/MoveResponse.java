package com.game.chess.DTO;

import com.game.chess.Model.GameMove;
import java.time.LocalDateTime;

public record MoveResponse(
        Long id,
        Long playerId,
        int moveNumber,
        String san,
        String fromSquare,
        String toSquare,
        LocalDateTime createdAt
) {
    /**
     * Maps a raw GameMove database entity into a clean MoveResponse DTO.
     */
    public static MoveResponse from(GameMove move) {
        return new MoveResponse(
                move.getId(),
                move.getPlayer().getId(),
                move.getMoveNumber(),
                move.getSan(),
                move.getFromSquare(),
                move.getToSquare(),
                move.getCreatedAt()
        );
    }
}