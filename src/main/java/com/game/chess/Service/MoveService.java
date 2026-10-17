package com.game.chess.Service;

import com.game.chess.DTO.MoveRequest;
import com.game.chess.DTO.MoveResponse;
import com.game.chess.Model.Game;
import com.game.chess.Model.GameMove;
import com.game.chess.Model.User;
import com.game.chess.Repository.MoveRepository;
import com.game.chess.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.game.chess.Repository.GameRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MoveService {

    private final MoveRepository moveRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    @Transactional
    public MoveResponse createMove(MoveRequest request) {
        User player = userRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Player not found: " + request.getPlayerId()));

        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Game not found: " + request.getGameId()));

        int nextMoveNumber = moveRepository.countByGameId(request.getGameId()) + 1;

        GameMove gameMove = new GameMove();
        gameMove.setGame(game);
        gameMove.setPlayer(player);
        gameMove.setMoveNumber(nextMoveNumber);
        gameMove.setFromSquare(request.getFromSquare());
        gameMove.setToSquare(request.getToSquare());
        gameMove.setPieceMoved(request.getPieceMoved());
        gameMove.setCapturedPiece(request.getCapturedPiece());

        GameMove saved = moveRepository.save(gameMove);
        return toResponse(saved);
    }

    public List<MoveResponse> getMovesForGame(Long gameId) {
        return moveRepository.findByGameIdOrderByMoveNumberAsc(gameId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private MoveResponse toResponse(GameMove gameMove) {
        return new MoveResponse(
                gameMove.getId(),
                gameMove.getGame().getId(),
                gameMove.getPlayer().getId(),
                gameMove.getPlayer().getUserName(),
                gameMove.getMoveNumber(),
                gameMove.getFromSquare(),
                gameMove.getToSquare(),
                gameMove.getPieceMoved(),
                gameMove.getCapturedPiece(),
                gameMove.getCreatedAt()
        );
    }
}