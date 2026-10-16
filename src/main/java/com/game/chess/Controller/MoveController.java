package com.game.chess.Controller;

import com.game.chess.DTO.MoveRequest;
import com.game.chess.DTO.MoveResponse;
import com.game.chess.Service.MoveService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moves")
public class MoveController {

    @Autowired
    private MoveService moveService;

    @PostMapping("")
    public ResponseEntity<?> createMove(@Valid @RequestBody MoveRequest request) {
        try {
            MoveResponse response = moveService.createMove(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Move number conflict — please retry.");
        }
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<MoveResponse>> getMovesForGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(moveService.getMovesForGame(gameId));
    }
}