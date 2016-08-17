package chess.domain.board;

import chess.domain.GameSituation;
import chess.logic.movementlogic.MovementLogic;
import chess.domain.pieces.Queen;
import chess.logic.chessboardinitializers.ChessBoardInitializer;
import static chess.logic.chessboardinitializers.ChessBoardInitializer.putPieceOnBoard;
import chess.logic.chessboardinitializers.StandardBoardInitializer;
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

    private static GameSituation sit;
    private static ChessBoardInitializer init;

    public ChessBoardCopierTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        init = new StandardBoardInitializer();
        sit = new GameSituation(init, new MovementLogic());
    }

    @Before
    public void setUp() {
        init.initialize(sit.getChessBoard());
    }

    @Test
    public void copyCreatesChessBoardWithIdenticalTable() {
        init.initialize(sit.getChessBoard());
        ChessBoard copy = ChessBoardCopier.copy(sit.getChessBoard());

        assertTrue(Arrays.deepEquals(sit.getChessBoard().getTable(), copy.getTable()));
    }

    @Test
    public void copyCreatesChessBoardWithPieceListsThatContainAllPieces() {
        ChessBoard copy = ChessBoardCopier.copy(sit.getChessBoard());

        for (Player player : Player.values()) {
            sit.getChessBoard().getPieces(player).stream().forEach(piece -> {
                assertTrue(copy.getPieces(player).contains(piece));
            });
        }
    }

    @Test
    public void copyCreatesChessBoardWithPieceListsThatContainNoExtraPieces() {
        ChessBoard copy = ChessBoardCopier.copy(sit.getChessBoard());

        for (Player player : Player.values()) {
            copy.getPieces(player).stream().forEach(piece -> {
                assertTrue(sit.getChessBoard().getPieces(player).contains(piece));
            });
        }
    }

    @Test
    public void copyHasBothKingsInCorrectSpot() {
        ChessBoard copy = ChessBoardCopier.copy(sit.getChessBoard());

        for (Player player : Player.values()) {
            assertTrue(copy.getKings().get(player).equals(sit.getChessBoard().getKings().get(player)));
        }
    }

    @Test
    public void copyAndOriginalAreNotSame() {
        init.initialize(sit.getChessBoard());
        assertFalse(ChessBoardCopier.copy(sit.getChessBoard()) == sit.getChessBoard());
    }

    @Test
    public void copyCreatesANewChessBoard() {
        init.initialize(sit.getChessBoard());
        ChessBoard copy = ChessBoardCopier.copy(sit.getChessBoard());

        Queen queen = new Queen(4, 4, Player.BLACK, "bp1");
        putPieceOnBoard(sit.getChessBoard(), queen);

        assertTrue(sit.getChessBoard().getSquare(4, 4).containsAPiece());
        assertFalse(copy.getSquare(4, 4).containsAPiece());

        sit.getChessBoard().getMovementLogic().move(queen, sit.getChessBoard().getSquare(4, 1), sit);
        assertEquals(Player.BLACK, sit.getChessBoard().getSquare(4, 1).getPiece().getOwner());
        assertEquals(Player.WHITE, copy.getSquare(4, 1).getPiece().getOwner());
    }

    @Test
    public void undoMoveReturnsChessBoardToSituationBeforeMoveWasMade() {
        ChessBoard backup = ChessBoardCopier.copy(sit.getChessBoard());
        ChessBoard cb = sit.getChessBoard();
        MovementLogic ml = cb.getMovementLogic();
        Square from = cb.getSquare(1, 0);
        Square to = cb.getSquare(2, 2);
        ml.move(from.getPiece(), to, sit);
        assertFalse(from.containsAPiece());
        assertTrue(to.containsAPiece());
        assertTrue(backup.getSquare(from.getColumn(), from.getRow()).containsAPiece());
        ChessBoardCopier.undoMove(backup, sit, from, to);
        assertTrue(Arrays.deepEquals(backup.getTable(), cb.getTable()));
    }

    @Test
    public void undoMoveReturnsBoardHashToSituationBeforeMoveWasMade() {
        ChessBoard backup = ChessBoardCopier.copy(sit.getChessBoard());
        ChessBoard cb = sit.getChessBoard();
        long oldHash = sit.getBoardHash();
        MovementLogic ml = cb.getMovementLogic();
        Square from = cb.getSquare(1, 0);
        Square to = cb.getSquare(2, 2);
        ml.move(from.getPiece(), to, sit);
        assertNotEquals(oldHash, sit.getBoardHash());
        ChessBoardCopier.undoMove(backup, sit, from, to);
        assertEquals(oldHash, sit.getBoardHash());
    }
}
