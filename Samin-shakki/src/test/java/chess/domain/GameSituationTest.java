package chess.domain;

import chess.logic.movementlogic.MovementLogic;
import chess.domain.board.ChessBoard;
import chess.domain.board.Player;
import chess.domain.board.Square;
import chess.logic.chessboardinitializers.EmptyBoardInitializer;
import chess.domain.pieces.King;
import chess.domain.pieces.Pawn;
import static chess.logic.chessboardinitializers.ChessBoardInitializer.putPieceOnBoard;
import chess.logic.chessboardinitializers.StandardBoardInitializer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;

/**
 *
 * @author sami
 */
public class GameSituationTest {

    private GameSituation game;
    private static StandardBoardInitializer stdinit;

    public GameSituationTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        stdinit = new StandardBoardInitializer();
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

    @Test
    public void boardHashUptoDateAfterInitialization() {
        game = new GameSituation(stdinit, new MovementLogic());
        assertEquals(game.getHasher().hash(game.getChessBoard()), game.getBoardHash());
    }

    @Test
    public void boardHashRemainsSameWhenRehashingIfNoChanges() {
        game = new GameSituation(stdinit, new MovementLogic());
        long oldHash = game.getBoardHash();
        game.reHashBoard();
        assertEquals(oldHash, game.getBoardHash());
    }

    @Test
    public void reHashBoardChangesHashIfBoardSituationChanged() {
        long oldHash = game.getBoardHash();
        Pawn whitePawn = new Pawn(4, 4, Player.WHITE, "wp");
        Pawn blackPawn = new Pawn(4, 6, Player.BLACK, "bp");
        ChessBoard board = game.getChessBoard();
        putPieceOnBoard(board, whitePawn);
        putPieceOnBoard(board, blackPawn);
        game.reHashBoard();
        assertNotEquals(oldHash, game.getBoardHash());
    }

    @Test
    public void boardHashCorrectAfterReHashing() {
        game.reset();
        Pawn whitePawn = new Pawn(4, 4, Player.WHITE, "wp");
        Pawn blackPawn = new Pawn(4, 6, Player.BLACK, "bp");
        ChessBoard board = game.getChessBoard();
        ChessBoard comp = new ChessBoard(new MovementLogic());
        putPieceOnBoard(board, whitePawn);
        putPieceOnBoard(board, blackPawn);
        putPieceOnBoard(comp, whitePawn);
        putPieceOnBoard(comp, blackPawn);
        game.reHashBoard();
        assertEquals(game.getHasher().hash(comp), game.getBoardHash());
    }

    @Test
    public void hashIsUpdatedCorrectlyForMove() {
        game.reHashBoard();
        Square from = game.getChessBoard().getSquare(0, 0);
        Square to = game.getChessBoard().getSquare(1, 0);
        game.updateHashForMove(from, to);
        long updatedHash = game.getBoardHash();
        MovementLogic ml = game.getChessBoard().getMovementLogic();
        King wk = (King) from.getPiece();
        ml.move(wk, to, game);
        game.reHashBoard();
        assertEquals(game.getBoardHash(), updatedHash);
    }
}
