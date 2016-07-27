package chess.domain.board;

import chess.domain.board.ChessBoardCopier;
import chess.domain.board.ChessBoard;
import chess.domain.board.Player;
import chess.logic.chessboardinitializers.ChessBoardInitializer;
import static chess.logic.chessboardinitializers.ChessBoardInitializer.putPieceOnBoard;
import chess.logic.chessboardinitializers.StandardBoardInitializer;
import chess.logic.movementlogic.MovementLogic;
import chess.domain.pieces.Queen;
import java.util.Arrays;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sami
 */
public class ChessBoardCopierTest {

    private static ChessBoard board;
    private static ChessBoardInitializer init;

    public ChessBoardCopierTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        init = new StandardBoardInitializer();
        board = new ChessBoard(new MovementLogic());
    }

    @Before
    public void setUp() {
        init.initialize(board);
    }

    @Test
    public void copyCreatesChessBoardWithIdenticalTable() {
        init.initialize(board);
        ChessBoard copy = ChessBoardCopier.copy(board);

        assertTrue(Arrays.deepEquals(board.getTable(), copy.getTable()));
    }

    @Test
    public void copyCreatesChessBoardWithPieceListsThatContainAllPieces() {
        ChessBoard copy = ChessBoardCopier.copy(board);

        for (Player player : Player.values()) {
            board.getPieces(player).stream().forEach(piece -> {
                assertTrue(copy.getPieces(player).contains(piece));
            });
        }
    }

    @Test
    public void copyCreatesChessBoardWithPieceListsThatContainNoExtraPieces() {
        ChessBoard copy = ChessBoardCopier.copy(board);

        for (Player player : Player.values()) {
            copy.getPieces(player).stream().forEach(piece -> {
                assertTrue(board.getPieces(player).contains(piece));
            });
        }
    }

    @Test
    public void copyHasBothKingsInCorrectSpot() {
        ChessBoard copy = ChessBoardCopier.copy(board);

        for (Player player : Player.values()) {
            assertTrue(copy.getKings().get(player).equals(board.getKings().get(player)));
        }
    }

    @Test
    public void copyAndOriginalAreNotSame() {
        init.initialize(board);
        assertFalse(ChessBoardCopier.copy(board) == board);
    }

    @Test
    public void copyCreatesANewChessBoard() {
        init.initialize(board);
        ChessBoard copy = ChessBoardCopier.copy(board);

        Queen queen = new Queen(4, 4, Player.BLACK, "bp1");
        putPieceOnBoard(board, queen);

        assertTrue(board.getSquare(4, 4).containsAPiece());
        assertFalse(copy.getSquare(4, 4).containsAPiece());

        board.getMovementLogic().move(queen, board.getSquare(4, 1), board);
        assertEquals(Player.BLACK, board.getSquare(4, 1).getPiece().getOwner());
        assertEquals(Player.WHITE, copy.getSquare(4, 1).getPiece().getOwner());
    }
}
