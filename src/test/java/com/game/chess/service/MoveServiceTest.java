package com.game.chess.service;

import com.game.chess.DTO.MoveRequest;
import com.game.chess.DTO.MoveResponse;
import com.game.chess.Exception.InvalidGameStateException;
import com.game.chess.Exception.InvalidMoveException;
import com.game.chess.Exception.NotAGameParticipantException;
import com.game.chess.Exception.NotYourTurnException;
import com.game.chess.Model.*;
import com.game.chess.Repository.GameRepository;
import com.game.chess.Repository.MoveRepository;
import com.game.chess.Repository.UserRepository;
import com.game.chess.Service.MoveHistoryService;
import com.game.chess.Service.MoveService;
import com.github.bhlangonijr.chesslib.move.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional; // Note: java.util.Optional

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Prevents UnnecessaryStubbingException
class MoveServiceTest {

    @Mock MoveRepository moveRepository;
    @Mock UserRepository userRepository;
    @Mock GameRepository gameRepository;
    @Mock
    MoveHistoryService moveHistoryService;

    @InjectMocks
    MoveService moveService;

    @BeforeEach
    void setUp() {
        // Common lenient stubs for saves and SAN generation
        lenient().when(moveRepository.countByGameId(any())).thenReturn(0);
        lenient().when(gameRepository.save(any(Game.class))).thenAnswer(i -> i.getArgument(0));
        lenient().when(moveRepository.save(any(GameMove.class))).thenAnswer(i -> i.getArgument(0));
        lenient().when(moveHistoryService.toSan(anyString(), any(Move.class))).thenReturn("e4");
    }

    private User user(Long id, String name) {
        User u = new User();
        u.setId(id);
        u.setUserName(name);
        return u;
    }

    private Game activeGame() {
        Game g = new Game();
        g.setId(10L);
        g.setWhiteplayer(user(1L, "abebe"));
        g.setBlackplayer(user(2L, "almaz"));
        g.setStatus(GameStatus.ACTIVE);
        g.setFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"); // <-- Add this!
        return g;
    }

    private MoveRequest req(long playerId, String from, String to) {
        MoveRequest r = new MoveRequest();
        r.setGameId(10L);
        r.setPlayerId(playerId);
        r.setFromSquare(from);
        r.setToSquare(to);
        return r;
    }

    private void stubLookups(Game g, User player) {
        when(gameRepository.findById(10L)).thenReturn(Optional.of(g));
        when(userRepository.findById(player.getId())).thenReturn(Optional.of(player));
    }

    @Test
    void whiteCanPlayE2E4() {
        Game game = activeGame();
        stubLookups(game, game.getWhiteplayer());

        MoveResponse res = moveService.createMove(req(1L, "e2", "e4"));

        assertEquals("WHITE_PAWN", res.pieceMoved());
        assertNull(res.capturedPiece());
        assertEquals(1, res.moveNumber());
        assertEquals("e4", res.san()); // Verifies SAN notation response

        assertTrue(game.getFen().startsWith("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b"));
        assertEquals(GameStatus.ACTIVE, game.getStatus());
    }

    @Test
    void blackCannotMoveFirst() {
        Game game = activeGame();
        stubLookups(game, game.getBlackplayer());

        assertThrows(NotYourTurnException.class, () -> moveService.createMove(req(2L, "e7", "e5")));
    }

    @Test
    void illegalPawnJumpIsRejected() {
        Game game = activeGame();
        stubLookups(game, game.getWhiteplayer());

        // Trying to move a Rook diagonally (a1 to b2) is universally illegal in chess
        assertThrows(InvalidMoveException.class, () -> moveService.createMove(req(1L, "a1", "b2")));
    }
    @Test
    void movingFromAnEmptySquareIsRejected() {
        Game game = activeGame();
        stubLookups(game, game.getWhiteplayer());

        assertThrows(InvalidMoveException.class, () -> moveService.createMove(req(1L, "e4", "e5")));
    }

    @Test
    void moveOnNonActiveGameIsRejected() {
        Game game = activeGame();
        game.setStatus(GameStatus.FINISHED);
        stubLookups(game, game.getWhiteplayer());

        assertThrows(InvalidGameStateException.class, () -> moveService.createMove(req(1L, "e2", "e4")));
    }

    @Test
    void strangerCannotMove() {
        Game game = activeGame();
        stubLookups(game, user(99L, "stranger"));

        assertThrows(NotAGameParticipantException.class, () -> moveService.createMove(req(99L, "e2", "e4")));
    }

    @Test
    void checkmateFinishesTheGame() {
        Game game = activeGame();
        game.setFen("rnbqkbnr/pppp1ppp/8/4p3/6P1/5P2/PPPPP2P/RNBQKBNR b KQkq - 0 2");
        stubLookups(game, game.getBlackplayer());
        when(moveRepository.countByGameId(10L)).thenReturn(3);

        moveService.createMove(req(2L, "d8", "h4"));

        assertEquals(GameStatus.FINISHED, game.getStatus());
    }
}