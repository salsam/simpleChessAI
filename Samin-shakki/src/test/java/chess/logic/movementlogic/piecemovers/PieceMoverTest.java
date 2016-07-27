package chess.logic.movementlogic.piecemovers;

import chess.domain.board.ChessBoard;
import chess.domain.board.Player;
import chess.logic.chessboardinitializers.ChessBoardInitializer;
import static chess.logic.chessboardinitializers.ChessBoardInitializer.putPieceOnBoard;
import chess.logic.chessboardinitializers.StandardBoardInitializer;
import chess.logic.movementlogic.MovementLogic;
import chess.domain.pieces.Pawn;
import chess.domain.pieces.Piece;
import chess.domain.pieces.Queen;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author sami
 */
public class PieceMoverTest {

    private Piece piece;
    private Piece pawn;
    private static ChessBoard board;
    private static ChessBoardInitializer init;

    public PieceMoverTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        board = new ChessBoard(new MovementLogic());
        init = new StandardBoardInitializer();
    }

    @Before
    public void setUp() {
        init.initialize(board);
        piece = new Queen(3, 4, Player.WHITE, "wq");
        pawn = new Pawn(3, 6, Player.BLACK, "bp");
        putPieceOnBoard(board, pawn);
        putPieceOnBoard(board, piece);
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
        board.getMovementLogic().move(piece, board.getSquare(3, 5), board);
        assertEquals(board.getSquare(3, 5), board.getSquare(piece.getColumn(), piece.getRow()));
    }

    @Test
    public void movingRemovesPieceFromPreviousSquare() {
        board.getMovementLogic().move(piece, board.getSquare(3, 5), board);
        assertEquals(null, board.getSquare(3, 4).getPiece());
    }

    @Test
    public void movingAddsPieceToTargetSquare() {
        board.getMovementLogic().move(piece, board.getSquare(3, 5), board);
        assertEquals(piece, board.getSquare(3, 5).getPiece());
    }

}
