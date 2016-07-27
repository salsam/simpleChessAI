package chess.logic.movementlogic.piecemovers;

import chess.domain.board.ChessBoard;
import chess.domain.board.Player;
import chess.domain.board.Square;
import chess.logic.chessboardinitializers.ChessBoardInitializer;
import static chess.logic.chessboardinitializers.ChessBoardInitializer.putPieceOnBoard;
import chess.logic.chessboardinitializers.EmptyBoardInitializer;
import chess.logic.movementlogic.MovementLogic;
import chess.domain.pieces.Pawn;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author sami
 */
public class PawnMoverTest {

    private Pawn pawn;
    private static ChessBoard board;
    private static ChessBoardInitializer init;
    private static PawnMover pawnMover;

    public PawnMoverTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        init = new EmptyBoardInitializer();
        board = new ChessBoard(new MovementLogic());
        pawnMover = new PawnMover();
    }

    @Before
    public void setUp() {
        init.initialize(board);
        pawn = new Pawn(2, 1, Player.WHITE, "wp");
        putPieceOnBoard(board, pawn);
    }

    @Test
    public void startingcolumnCorrect() {
        assertEquals(2, pawn.getColumn());
    }

    @Test
    public void startingrowCorrect() {
        assertEquals(1, pawn.getRow());
    }

    @Test
    public void pawnCannotStayStillWhenMoving() {
        assertFalse(pawnMover.possibleMoves(pawn, board).contains(new Square(2, 1)));
    }

    @Test
    public void pawnCannotMoveThreeStepsUp() {
        assertFalse(pawnMover.possibleMoves(pawn, board).contains(new Square(2, 4)));
    }

    @Test
    public void pawnCannotMoveSideways() {
        assertFalse(pawnMover.possibleMoves(pawn, board).contains(new Square(1, 1)));
    }

    @Test
    public void pawnCanMoveTwoStepsUpFromStartingLocation() {
        assertTrue(pawnMover.possibleMoves(pawn, board).contains(new Square(2, 3)));
    }

    @Test
    public void pawnCannotMoveTwoStepsAfterMovingOnce() {
        pawn = new Pawn(2, 2, Player.WHITE, "wp");
        pawnMover.move(pawn, board.getSquare(2, 3), board);
        assertFalse(pawnMover.possibleMoves(pawn, board).contains(new Square(2, 5)));
    }

    @Test
    public void pawnCanTakeAPieceDiagonallyForwardToRight() {
        Pawn enemyPawn = new Pawn(3, 2, Player.BLACK, "ep");
        putPieceOnBoard(board, enemyPawn);
        assertTrue(pawnMover.possibleMoves(pawn, board).contains(new Square(3, 2)));
    }

    @Test
    public void pawnCanTakeAPieceDiagonallyForwardToLeft() {
        Pawn enemyPawn = new Pawn(1, 2, Player.BLACK, "ep");
        putPieceOnBoard(board, enemyPawn);
        assertTrue(pawnMover.possibleMoves(pawn, board).contains(new Square(1, 2)));
    }

    @Test
    public void pawnCannotTakeOwnPieceDiagonallyForward() {
        Pawn enemyPawn = new Pawn(3, 2, Player.WHITE, "ep");
        putPieceOnBoard(board, enemyPawn);
        assertFalse(pawnMover.possibleMoves(pawn, board).contains(new Square(3, 2)));
    }

    @Test
    public void pawnCannotMoveOverTheEdge() {
        pawn = new Pawn(0, 7, Player.WHITE, "wp");
        putPieceOnBoard(board, pawn);
        assertFalse(pawnMover.possibleMoves(pawn, board).contains(new Square(0, 8)));
    }

    @Test
    public void pawnCanEnPassantOpposingPawnThatMovedTwoSquaresLastTurn() {
        Pawn opposingPawn = new Pawn(3, 3, Player.BLACK, "op");
        putPieceOnBoard(board, opposingPawn);
        pawnMover.move(opposingPawn, board.getSquare(3, 1), board);
        assertTrue(pawnMover.possibleMoves(pawn, board).contains(new Square(3, 2)));
    }

    @Test
    public void whenEnPassantingOpposingPawnIsRemovedFromBoard() {
        Pawn opposingPawn = new Pawn(3, 3, Player.BLACK, "op");
        putPieceOnBoard(board, opposingPawn);
        pawnMover.move(opposingPawn, board.getSquare(3, 1), board);
        pawnMover.move(pawn, board.getSquare(3, 2), board);
        assertFalse(board.getSquare(3, 1).containsAPiece());
    }
}
