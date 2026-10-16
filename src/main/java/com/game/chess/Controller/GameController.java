package com.game.chess.Controller;



import com.game.chess.Model.Game;
import com.game.chess.Repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
public class GameController {

    @Autowired
    private GameRepository gameRepository;
    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {
        return gameRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("")
    public ResponseEntity<Game> createGame() {
        Game saved = gameRepository.save(new Game());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}