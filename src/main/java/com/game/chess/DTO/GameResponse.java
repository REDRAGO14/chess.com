package com.game.chess.DTO;

import com.game.chess.Model.Game;
import com.game.chess.Model.GameStatus;

import java.time.LocalDateTime;

public record GameResponse(
        Long id,
        GameStatus status,
        String fen,
        Long WhitePlayerId,
        String WhitePlayerName,
        Long blackPlayerId,
        String blackPlayerName,
        LocalDateTime createdAt

) {
    public static GameResponse from(Game game) {

        return new GameResponse(
                game.getId(),
                game.getStatus(),
                game.getFen(),
                game.getWhiteplayer().getId(),
                game.getWhiteplayer().getUserName(),
                game.getBlackplayer() == null ? null : game.getBlackplayer().getId(),
                game.getBlackplayer() == null ? null : game.getBlackplayer().getUserName(),
                game.getCreatedAt()


        );
    }
}
