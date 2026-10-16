package com.game.chess.Controller;

import com.game.chess.DTO.CreateGameRequest;
import com.game.chess.DTO.GameResponse;
import com.game.chess.DTO.JoinGameRequest;
import com.game.chess.DTO.ResignGameRequest;
import com.game.chess.DTO.StartGameRequest;
import com.game.chess.Service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;


    @PostMapping
    public ResponseEntity<GameResponse> createGame(@Valid @RequestBody CreateGameRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.createGame(request));
    }


    @PostMapping("/{id}/join")
    public ResponseEntity<GameResponse> joinGame(@PathVariable Long id,
                                                 @Valid @RequestBody JoinGameRequest request) {
        return ResponseEntity.ok(gameService.joinGame(id, request));
    }


    @PostMapping("/{id}/start")
    public ResponseEntity<GameResponse> startGame(@PathVariable Long id,
                                                  @Valid @RequestBody StartGameRequest request) {
        return ResponseEntity.ok(gameService.startGame(id, request));
    }


    @PostMapping("/{id}/resign")
    public ResponseEntity<GameResponse> resignGame(@PathVariable Long id,
                                                   @Valid @RequestBody ResignGameRequest request) {
        return ResponseEntity.ok(gameService.resignGame(id, request));
    }


    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getGame(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getGame(id));
    }
}
