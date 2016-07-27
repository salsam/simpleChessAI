package chess.domain;

import chess.logic.movementlogic.MovementLogic;
import chess.domain.board.ChessBoard;
import chess.domain.board.Player;
import static chess.logic.chessboardinitializers.ChessBoardInitializer.putPieceOnBoard;
import chess.logic.chessboardinitializers.EmptyBoardInitializer;
import chess.domain.pieces.King;
import chess.domain.pieces.Pawn;
import static chess.logic.chessboardinitializers.ChessBoardInitializer.putPieceOnBoard;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author sami
 */
public class GameSituationTest {

    private GameSituation game;

    public GameSituationTest() {
    }

    @Before
    public void setUp() {
        game = new GameSituation(new EmptyBoardInitializer(), new MovementLogic());
        ChessBoard board = game.getChessBoard();
        putPieceOnBoard(board, new King(0, 0, Player.WHITE, "wk"));
    }

    @Test
    public void whoseTurnReturnsWhiteIfOddTurnAndBlackIfEvenTurn() {
        assertEquals(Player.WHITE, game.whoseTurn());
        game.nextTurn();
        assertEquals(Player.BLACK, game.whoseTurn());
        game.nextTurn();
        assertEquals(Player.WHITE, game.whoseTurn());
    }
    
    @Test
    public void playersPawnsBecomeNoLongerCapturableEnPassantOnPlayersNextTurn() {
        Pawn whitePawn = new Pawn(4, 4, Player.WHITE, "wp");
        Pawn blackPawn = new Pawn(4, 6, Player.BLACK, "bp");
        ChessBoard board = game.getChessBoard();
        putPieceOnBoard(board, whitePawn);
        putPieceOnBoard(board, blackPawn);
        board.getMovementLogic().move(whitePawn, board.getSquare(4, 6), board);
        assertTrue(whitePawn.getMovedTwoSquaresLastTurn());
        game.nextTurn();
        game.nextTurn();
        assertFalse(whitePawn.getMovedTwoSquaresLastTurn());
    }

}
