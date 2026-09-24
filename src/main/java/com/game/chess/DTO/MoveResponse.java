package com.game.chess.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoveResponse {
    private Long id;
    private Long gameId;
    private Long playerId;
    private String playerUsername;
    private Integer moveNumber;
    private String fromSquare;
    private String toSquare;
    private String pieceMoved;
    private String capturedPiece;
    private LocalDateTime createdAt;
}