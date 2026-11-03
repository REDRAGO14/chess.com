package com.game.chess.Service;

import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import org.springframework.stereotype.Service;

@Service
public class MoveHistoryService {

    /**
     * Computes Standard Algebraic Notation (SAN) from a position's FEN and a move.
     * Must be calculated *before* the move is applied to the board[cite: 2].
     */
    public String toSan(String fenBeforeMove, Move move) {
        MoveList list = new MoveList(fenBeforeMove);
        list.add(move);
        return list.toSanArray()[0]; // Returns notation like "e4", "Nf3", "Qxf7#"[cite: 2]
    }
}