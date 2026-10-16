package com.game.chess;

// 1. Imports go HERE: after "package", before "class"
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// 2. The class
class ChesslibSmokeTest {

    // 3. The test method
    @Test
    void chesslibWorks() {
        Board board = new Board();
        System.out.println(board.getFen());   // prints the starting position

        // e2 -> e4 is a legal first move
        assertTrue(board.isMoveLegal(new Move(Square.E2, Square.E4), true));

        // e2 -> e5 is NOT legal (a pawn cannot jump 3 squares)
        assertFalse(board.legalMoves().contains(new Move(Square.E2, Square.E5)));
    }
}