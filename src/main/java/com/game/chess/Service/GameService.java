package com.game.chess.Service;


import com.game.chess.Config.ChessConstants;
import com.game.chess.DTO.CreateGameRequest;
import com.game.chess.DTO.GameResponse;
import com.game.chess.DTO.JoinGameRequest;
import com.game.chess.DTO.MoveRequest;
import com.game.chess.DTO.MoveResponse;
import com.game.chess.DTO.ResignGameRequest;
import com.game.chess.DTO.StartGameRequest;
import com.game.chess.Exception.GameNotFoundException;
import com.game.chess.Exception.InvalidGameStateException;
import com.game.chess.Exception.NotAGameParticipantException;
import com.game.chess.Exception.UserNotFound;
import com.game.chess.Model.Game;
import com.game.chess.Model.GameStatus;
import com.game.chess.Model.User;
import com.game.chess.Repository.GameRepository;
import com.game.chess.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final MoveService moveService;

@Transactional
    public GameResponse createGame(CreateGameRequest request) {
        User creator = findUserOrThrow(request.getUserId());
    // Check if the user is already in a WAITING or ACTIVE game
    boolean isInGame = gameRepository.existsActiveGameForUser(
            creator.getId(),
            List.of(GameStatus.WAITING, GameStatus.READY,GameStatus.ACTIVE)
    );

    if (isInGame) {
        throw new InvalidGameStateException("User is already in an active or waiting game.");
    }
        Game game = new Game();
        game.setWhiteplayer(creator);
        game.setStatus(GameStatus.WAITING);
        game.setFen(ChessConstants.STRING_FEN);

        return GameResponse.from(gameRepository.save(game));
    }

    @Transactional
    public GameResponse joinGame(Long gameId, JoinGameRequest request) {
        Game game = findGameOrThrow(gameId);

        if (game.getStatus() != GameStatus.WAITING) {
            throw new InvalidGameStateException(
                    "Game " + gameId + " is not open for joining (status: " + game.getStatus() + ")");
        }


        if (game.getWhiteplayer().getId().equals(request.getUserId())) {
            throw new InvalidGameStateException("You are already in this game");
        }

        User joiner = findUserOrThrow(request.getUserId());
        // Check if the user is already in a WAITING or ACTIVE game
        boolean isInGame = gameRepository.existsActiveGameForUser(
                joiner.getId(),
                List.of(GameStatus.WAITING, GameStatus.READY, GameStatus.ACTIVE)
        );

        if (isInGame) {
            throw new InvalidGameStateException("User is already in an active or waiting game.");
        }
        game.setBlackplayer(joiner);
        game.setStatus(GameStatus.READY);

        return GameResponse.from(gameRepository.save(game));
    }

    @Transactional
    public GameResponse startGame(Long gameId, StartGameRequest request) {
        Game game = findGameOrThrow(gameId);


        if (!isParticipant(game, request.getUserId())) {
            throw new NotAGameParticipantException("You are not a player in game " + gameId);
        }


        if (game.getStatus() == GameStatus.WAITING) {
            throw new InvalidGameStateException("Cannot start: two players are required");
        }
        if (game.getStatus() != GameStatus.READY) {
            throw new InvalidGameStateException(
                    "Game already started (status: " + game.getStatus() + ")");
        }

        game.setStatus(GameStatus.ACTIVE);
        return GameResponse.from(gameRepository.save(game));
    }


    @Transactional
    public GameResponse resignGame(Long gameId, ResignGameRequest request) {
        Game game = findGameOrThrow(gameId);

        if (!isParticipant(game, request.getUserId())) {
            throw new NotAGameParticipantException("You are not a player in game " + gameId);
        }
        if (game.getStatus() != GameStatus.ACTIVE) {
            throw new InvalidGameStateException(
                    "Only an ACTIVE game can be resigned (status: " + game.getStatus() + ")");
        }

        game.setStatus(GameStatus.FINISHED);
        return GameResponse.from(gameRepository.save(game));
    }


    @Transactional(readOnly = true)
    public GameResponse getGame(Long gameId) {
        return GameResponse.from(findGameOrThrow(gameId));
    }



    public MoveResponse makeMove(MoveRequest request) {
        return moveService.createMove(request);
    }

    private Game findGameOrThrow(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game " + gameId + " not found"));
    }

    private User findUserOrThrow(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("User " + userId + " not found"));
    }


    private boolean isParticipant(Game game, Long userId) {
        boolean isWhite = game.getWhiteplayer().getId().equals(userId);
        boolean isBlack = game.getBlackplayer() != null && game.getBlackplayer().getId().equals(userId);
        return isWhite || isBlack;
    }
}