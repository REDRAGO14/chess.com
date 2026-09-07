package com.game.chess.Service;

import com.game.chess.DTO.MoveCreateRequest;
import com.game.chess.DTO.MoveRequest;
import com.game.chess.DTO.MoveResponse;
import com.game.chess.Model.Game;
import com.game.chess.Model.Move;
import com.game.chess.Model.User;
import com.game.chess.Repository.GameRepository;
import com.game.chess.Repository.MoveRepository;
import com.game.chess.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MoveService {

    @Autowired
    private MoveRepository moveRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Transactional
    public MoveResponse createMove(MoveRequest request) {
        User player = userRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Player not found: " + request.getPlayerId()));

        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Game not found: " + request.getGameId()));

        int nextMoveNumber = moveRepository.countByGameId(request.getGameId()) + 1;

        Move move = new Move();
        move.setGame(game);
        move.setPlayer(player);
        move.setMoveNumber(nextMoveNumber);
        move.setFromSquare(request.getFromSquare());
        move.setToSquare(request.getToSquare());
        move.setPieceMoved(request.getPieceMoved());
        move.setCapturedPiece(request.getCapturedPiece());

        Move saved = moveRepository.save(move);
        return toResponse(saved);
    }

    public List<MoveResponse> getMovesForGame(Long gameId) {
        return moveRepository.findByGameIdOrderByMoveNumberAsc(gameId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private MoveResponse toResponse(Move move) {
        return new MoveResponse(
                move.getId(),
                move.getGame().getId(),
                move.getPlayer().getId(),
                move.getPlayer().getUserName(),
                move.getMoveNumber(),
                move.getFromSquare(),
                move.getToSquare(),
                move.getPieceMoved(),
                move.getCapturedPiece(),
                move.getCreatedAt()
        );
    }
}