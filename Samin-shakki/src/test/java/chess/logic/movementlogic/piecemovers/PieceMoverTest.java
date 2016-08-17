package chess.logic.movementlogic.piecemovers;

import chess.domain.GameSituation;
import chess.domain.board.ChessBoard;
import chess.domain.board.Player;
import chess.logic.chessboardinitializers.ChessBoardInitializer;
import static chess.logic.chessboardinitializers.ChessBoardInitializer.putPieceOnBoard;
import chess.logic.movementlogic.MovementLogic;
import chess.domain.pieces.Pawn;
import chess.domain.pieces.Piece;
import chess.domain.pieces.Queen;
import chess.logic.chessboardinitializers.EmptyBoardInitializer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 *
 * @author sami
 */
public class PieceMoverTest {

    private Piece piece;
    private Piece pawn;
    private static ChessBoard board;
    private static ChessBoardInitializer init;
    private static GameSituation sit;

    public PieceMoverTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        init = new EmptyBoardInitializer();
        sit = new GameSituation(init, new MovementLogic());
        board = sit.getChessBoard();
    }

    @Before
    public void setUp() {
        init.initialize(board);
        piece = new Queen(3, 4, Player.WHITE, "wq");
        pawn = new Pawn(3, 6, Player.BLACK, "bp");
        putPieceOnBoard(board, pawn);
        putPieceOnBoard(board, piece);
        sit.reHashBoard(true);
    }

    @Test
    public void getOwnerReturnCorrectPlayer() {
        assertEquals(Player.WHITE, piece.getOwner());
        assertEquals(Player.BLACK, pawn.getOwner());
    }

    @Test
    public void locationCorrectAfterCreation() {
        assertEquals(board.getSquare(3, 4), board.getSquare(piece.getColumn(), piece.getRow()));
    }

    @Test
    public void movingChangesLocationCorrectly() {
        board.getMovementLogic().move(piece, board.getSquare(3, 5), sit);
        assertEquals(board.getSquare(3, 5), board.getSquare(piece.getColumn(), piece.getRow()));
    }

    @Test
    public void movingRemovesPieceFromPreviousSquare() {
        board.getMovementLogic().move(piece, board.getSquare(3, 5), sit);
        assertEquals(null, board.getSquare(3, 4).getPiece());
    }

    @Test
    public void movingAddsPieceToTargetSquare() {
        board.getMovementLogic().move(piece, board.getSquare(3, 5), sit);
        assertEquals(piece, board.getSquare(3, 5).getPiece());
    }

    @Test
    public void movingToEmptySquareUpdatesHashCorrectly() {
        board.getMovementLogic().move(piece, board.getSquare(3, 5), sit);
        ChessBoard comp = new ChessBoard(new MovementLogic());
        putPieceOnBoard(comp, piece);
        putPieceOnBoard(comp, pawn);
        assertEquals(sit.getHasher().hash(comp), sit.getBoardHash());
    }

    @Test
    public void takingAPieceUpdatesHashCorrectly() {
        board.getMovementLogic().move(piece, board.getSquare(3, 6), sit);
        ChessBoard comp = new ChessBoard(new MovementLogic());
        putPieceOnBoard(comp, piece);
        assertEquals(sit.getHasher().hash(comp), sit.getBoardHash());
    }
}
