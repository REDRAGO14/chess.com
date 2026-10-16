package com.game.chess.Service;

import com.game.chess.Model.Game;
import com.game.chess.Repository.GameRepository;
import com.github.bhlangonijr.chesslib.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChessBoardService {

    private final GameRepository gameRepository;
    /**
     * Loads a Game from MySQL and initializes a Chesslib Board object from its FEN string.
     */
    public Board loadBoardFromGame(Long gameId) {
        // 1. Fetch game from database
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new UsernameNotFoundException("Game " + gameId + " not found"));

        // 2. Initialize the chesslib Board object
        Board board = new Board();

        // 3. Load the FEN string stored in the database into the board
        board.loadFromFen(game.getFen());

        return board;
    }


}