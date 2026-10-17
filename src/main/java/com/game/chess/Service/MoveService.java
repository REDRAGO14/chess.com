package com.game.chess.Service;

import com.game.chess.DTO.MoveRequest;
import com.game.chess.DTO.MoveResponse;
import com.game.chess.Exception.GameNotFoundException;
import com.game.chess.Exception.InvalidGameStateException;
import com.game.chess.Exception.InvalidMoveException;
import com.game.chess.Exception.NotAGameParticipantException;
import com.game.chess.Exception.NotYourTurnException;
import com.game.chess.Exception.UserNotFound;
import com.game.chess.Model.Game;
import com.game.chess.Model.GameStatus;
import com.game.chess.Model.GameMove;
import com.game.chess.Model.User;
import com.game.chess.Repository.GameRepository;
import com.game.chess.Repository.MoveRepository;
import com.game.chess.Repository.UserRepository;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.Rank;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MoveService {

    private final MoveRepository moveRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final MoveHistoryService moveHistoryService;


    @Transactional
    public MoveResponse createMove(MoveRequest request) {

        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new GameNotFoundException("Game " + request.getGameId() + " not found"));
        User player = userRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new UserNotFound("Player " + request.getPlayerId() + " not found"));

        if (game.getStatus() != GameStatus.ACTIVE) {
            throw new InvalidGameStateException("Game is not active (status: " + game.getStatus() + ")");
        }


        boolean isWhite = game.getWhiteplayer().getId().equals(player.getId());
        boolean isBlack = game.getBlackplayer() != null && game.getBlackplayer().getId().equals(player.getId());
        if (!isWhite && !isBlack) {
            throw new NotAGameParticipantException("You are not a player in game " + game.getId());
        }


        Board board = new Board();
        board.loadFromFen(game.getFen());


        boolean whiteToMove = board.getSideToMove() == Side.WHITE;
        if (whiteToMove != isWhite) {
            throw new NotYourTurnException("It is not your turn");
        }


        Square from = parseSquare(request.getFromSquare());
        Square to = parseSquare(request.getToSquare());
        Piece piece = board.getPiece(from);
        if (piece == Piece.NONE) {
            throw new InvalidMoveException("There is no piece on " + from);
        }


        com.github.bhlangonijr.chesslib.move.Move chessMove = buildChessMove(piece, from, to, request.getPromotion());
        if (!board.isMoveLegal(chessMove, true)) {
            throw new InvalidMoveException("Illegal move: " + from + " to " + to);
        }

        // 1. COMPUTE SAN NOTATION BEFORE THE MOVE HAPPENS
        String sanNotation = moveHistoryService.toSan(game.getFen(), chessMove); // <--- Added this line
        Piece captured = board.getPiece(to);
        boolean pawnChangedFile = piece.getPieceType() == PieceType.PAWN && from.getFile() != to.getFile();
        if (captured == Piece.NONE && pawnChangedFile) {

            captured = piece.getPieceSide() == Side.WHITE ? Piece.BLACK_PAWN : Piece.WHITE_PAWN;
        }


        board.doMove(chessMove);
        game.setFen(board.getFen());


        if (board.isMated() || board.isDraw() || board.isStaleMate()) {
            game.setStatus(GameStatus.FINISHED);
        }


        GameMove move = new GameMove();
        move.setGame(game);
        move.setPlayer(player);
        move.setMoveNumber(moveRepository.countByGameId(game.getId()) + 1);
        move.setFromSquare(request.getFromSquare());
        move.setToSquare(request.getToSquare());
        move.setSan(sanNotation); // <--- Saved your human-readable notation here!
        move.setPieceMoved(piece.name());
        move.setCapturedPiece(captured == Piece.NONE ? null : captured.name());

        gameRepository.save(game);
        GameMove saved = moveRepository.save(move);
        return toResponse(saved);
    }


    @Transactional(readOnly = true)
    public List<MoveResponse> getMovesForGame(Long gameId) {
        return moveRepository.findByGameIdOrderByMoveNumberAsc(gameId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }



    private Square parseSquare(String text) {
        try {
            return Square.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMoveException("Invalid square: " + text);
        }
    }


    private com.github.bhlangonijr.chesslib.move.Move buildChessMove(Piece piece, Square from, Square to, String promotion) {
        boolean white = piece.getPieceSide() == Side.WHITE;
        boolean isPawn = piece.getPieceType() == PieceType.PAWN;
        boolean lastRank = (white && to.getRank() == Rank.RANK_8) || (!white && to.getRank() == Rank.RANK_1);

        if (isPawn && lastRank) {
            String choice = promotion == null ? "Q" : promotion.toUpperCase();   // default: queen
            Piece promoted;
            switch (choice) {
                case "R" -> promoted = white ? Piece.WHITE_ROOK : Piece.BLACK_ROOK;
                case "B" -> promoted = white ? Piece.WHITE_BISHOP : Piece.BLACK_BISHOP;
                case "N" -> promoted = white ? Piece.WHITE_KNIGHT : Piece.BLACK_KNIGHT;
                default -> promoted = white ? Piece.WHITE_QUEEN : Piece.BLACK_QUEEN;
            }
            return new com.github.bhlangonijr.chesslib.move.Move(from, to, promoted);
        }
        return new com.github.bhlangonijr.chesslib.move.Move(from, to);
    }

    private MoveResponse toResponse(GameMove move) {
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