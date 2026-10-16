package com.game.chess.Service;

import com.game.chess.DTO.MoveRequest;
import com.game.chess.Exception.InvalidMoveException;
import com.game.chess.Exception.NotYourTurnException;
import com.game.chess.Model.Game;
import com.game.chess.Model.User;
import com.game.chess.Repository.MoveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class MoveValidator {

    private static final Set<String> VALID_PIECE_TYPES =
            Set.of("PAWN", "ROOK", "KNIGHT", "BISHOP", "QUEEN", "KING");

    private final MoveRepository moveRepository;

    public void validate(MoveRequest request, Game game, User player) {
        boolean isWhite = game.getWhiteplayer() != null
                && game.getWhiteplayer().getId().equals(player.getId());
        boolean isBlack = game.getBlackplayer() != null
                && game.getBlackplayer().getId().equals(player.getId());

        if (!isWhite && !isBlack) {
            throw new InvalidMoveException(
                    "Player" + player.getId() + "is not part of game" + game.getId()
            );

        }
        int moveSoFar = moveRepository.countByGameId(game.getId());
        boolean whitesTurn = moveSoFar % 2 == 0;
        if (whitesTurn != isWhite) {
            throw new NotYourTurnException(
                    "It is" + (whitesTurn ? "white" : "black") + "'s turn, not player"
                            + player.getId()
            );
        }
        if (request.getFromSquare().equals(request.getToSquare())) {
            throw new InvalidMoveException("Source and destination are the same square");
        }
        String pieceType = request.getPieceMoved() == null ? "" : request.getPieceMoved().toUpperCase();
        if (!VALID_PIECE_TYPES.contains(pieceType)) {
            throw new InvalidMoveException("Unknown piece type: " + request.getPieceMoved());
        }
        boolean isCapture = request.getCapturedPiece() != null &&
                !request.getCapturedPiece().isBlank();


        if (!isLegalShape(pieceType, request.getFromSquare(), request.getToSquare(), isCapture, isWhite)) {
            throw new InvalidMoveException(
                    pieceType + " cannot move from " + request.getFromSquare() + " to " + request.getToSquare());
        }


    }

    private boolean isLegalShape(String pieceType, String from, String to, boolean isCapture, boolean isWhite) {
        int[] f = coords(from);
        int[] t = coords(to);
        int dFile = t[0] - f[0];
        int dRank = t[1] - f[1];

        switch (pieceType) {
            case "PAWN":
                return isLegalPawnShape(isWhite, f, dFile, dRank, isCapture);
            case "KNIGHT":
                return (Math.abs(dFile) == 2 && Math.abs(dRank) == 1)
                        || (Math.abs(dFile) == 1 && Math.abs(dRank) == 2);
            case "BISHOP":
                return Math.abs(dFile) == Math.abs(dRank);
            case "ROOK":
                return dFile == 0 || dRank == 0;
            case "QUEEN":
                return dFile == 0 || dRank == 0 || Math.abs(dFile) == Math.abs(dRank);
            case "KING":
                return Math.abs(dFile) <= 1 && Math.abs(dRank) <= 1;
            default:
                return false;
        }
    }

    private boolean isLegalPawnShape(boolean isWhite, int[] f, int dFile, int dRank, boolean isCapture) {
        int direction = isWhite ? 1 : -1;
        int startRank = isWhite ? 1 : 6;

        if (isCapture) {
            return Math.abs(dFile) == 1 && dRank == direction;
        }
        if (dFile != 0) return false;
        if (dRank == direction) return true;
        return dRank == 2 * direction && f[1] == startRank;
    }

    private int[] coords(String square) {
        int file = square.charAt(0) - 'a';
        int rank = square.charAt(1) - '1';
        return new int[]{file, rank};
    }
}
















