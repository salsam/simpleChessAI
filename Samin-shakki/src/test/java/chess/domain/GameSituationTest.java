package chess.domain;

import chess.logic.movementlogic.MovementLogic;
import chess.domain.board.ChessBoard;
import chess.domain.board.Player;
import chess.domain.board.Square;
import chess.logic.chessboardinitializers.EmptyBoardInitializer;
import chess.domain.pieces.King;
import chess.domain.pieces.Pawn;
import chess.logic.ailogic.ZobristHasher;
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
        board.getMovementLogic().move(whitePawn, board.getSquare(4, 6), game);
        assertTrue(whitePawn.getMovedTwoSquaresLastTurn());
        game.nextTurn();
        game.nextTurn();
        assertFalse(whitePawn.getMovedTwoSquaresLastTurn());
    }

    @Test
    public void turnCounterStartsFrom1() {
        assertEquals(1, game.getTurn());
    }

    @Test
    public void firstWhitesTurn() {
        assertEquals(Player.WHITE, game.whoseTurn());
    }

    @Test
    public void nextTurnIncreasesTurnByOne() {
        game.nextTurn();
        assertEquals(2, game.getTurn());
        game.nextTurn();
        assertEquals(3, game.getTurn());
    }

    @Test
    public void whiteAndBlackMovePiecesInTurns() {
        assertEquals(Player.WHITE, game.whoseTurn());
        game.nextTurn();
        assertEquals(Player.BLACK, game.whoseTurn());
        game.nextTurn();
        assertEquals(Player.WHITE, game.whoseTurn());
        game.nextTurn();
        assertEquals(Player.BLACK, game.whoseTurn());
        game.nextTurn();
        assertEquals(Player.WHITE, game.whoseTurn());
    }

    @Test
    public void resetResetsTurnCounter() {
        game.nextTurn();
        game.reset();
        assertEquals(1, game.getTurn());
        assertEquals(Player.WHITE, game.whoseTurn());
    }

    @Test
    public void resetResetsSituationOnBoard() {
        game.reset();
        assertFalse(game.getChessBoard().getSquare(0, 0).containsAPiece());
    }

    @Test
    public void resetMakesGameContinueAgain() {
        game.setContinues(false);
        game.reset();
        assertTrue(game.getContinues());
    }

    @Test
    public void startingSituationHasBeenMetOnce() {
        assertEquals(1, game.getCountOfCurrentSituation());
    }

    @Test
    public void decreaseCountOfCurrentSituationDecreasesCountByOne() {
        game.decrementCountOfCurrentBoardSituation();
        assertEquals(0, game.getCountOfCurrentSituation());
    }

    @Test
    public void incrementCountOfCurrentSituationIncreasesCountByOne() {
        game.incrementCountOfCurrentBoardSituation();
        assertEquals(2, game.getCountOfCurrentSituation());
    }

//    @Test
//    public void boardHashDoesNotChangeWhenPieceMovedStill() {
//        long oldHash = game.getBoardHash();
//        Square fromTo = game.getChessBoard().getSquare(0, 0);
//        game.updateHashForMove(fromTo, fromTo);
//        assertEquals(oldHash, game.getBoardHash());
//    }
//
//    @Test
//    public void updateHashForMoveUpdatesHashCorrect() {
//        long oldHash = game.getBoardHash();
//        Square from = game.getChessBoard().getSquare(0, 0);
//        Square to = game.getChessBoard().getSquare(0, 1);
//        game.updateHashForMove(from, to);
//        game.updateHashForMove(to, from);
//        assertEquals(oldHash, game.getBoardHash());
//    }
}
