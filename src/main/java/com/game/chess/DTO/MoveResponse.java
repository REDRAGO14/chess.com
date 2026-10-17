package com.game.chess.DTO;

import com.game.chess.Model.GameMove;
import java.time.LocalDateTime;

public record MoveResponse(
        Long id,
        Long gameId,
        Long playerId,
        String userName,
        int moveNumber,
        String fromSquare,
        String toSquare,
        String san,           // Human-readable notation[cite: 2]
        String pieceMoved,
        String capturedPiece,
        LocalDateTime createdAt
) {
    /**
     * Maps a raw GameMove database entity into a clean MoveResponse DTO.
     */
    public static MoveResponse from(GameMove move) {
        return new MoveResponse(
                move.getId(),
                move.getGame().getId(),
                move.getPlayer().getId(),
                move.getPlayer().getUserName(),
                move.getMoveNumber(),
                move.getFromSquare(),
                move.getToSquare(),
                move.getSan(),
                move.getPieceMoved(),
                move.getCapturedPiece(),
                move.getCreatedAt()
        );
    }
}