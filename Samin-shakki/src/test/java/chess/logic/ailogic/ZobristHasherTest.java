package chess.logic.ailogic;

import chess.domain.GameSituation;
import chess.domain.board.ChessBoard;
import chess.domain.board.Player;
import chess.domain.board.Square;
import chess.domain.pieces.Pawn;
import chess.logic.chessboardinitializers.ChessBoardInitializer;
import chess.logic.chessboardinitializers.EmptyBoardInitializer;
import chess.logic.movementlogic.MovementLogic;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sami
 */
public class ZobristHasherTest {

    private static ZobristHasher zb;
    private static MovementLogic ml;
    private static GameSituation sit;
    private static ChessBoardInitializer init;
    private static ChessBoard cb;

    public ZobristHasherTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        init = new EmptyBoardInitializer();
        ml = new MovementLogic();
        sit = new GameSituation(init, ml);
        cb = sit.getChessBoard();
        zb = new ZobristHasher();
    }

    @Before
    public void setUp() {
        sit.reset();
    }

    @Test
    public void emptyBoardsHaveSameHash() {
        ChessBoard board = new ChessBoard(ml);
        assertEquals(zb.hash(board), zb.hash(cb));
    }

    @Test
    public void updateHashDoesNothingIfNeitherSquareHasAPiece() {
        long oldHash = zb.hash(cb);
        Square from = cb.getSquare(0, 0);
        Square to = cb.getSquare(1, 1);
        assertEquals(oldHash, zb.updateHash(oldHash, cb, from, to));
    }

    @Test
    public void hashIsDifferentWithDifferentBoardSituations() {
        long oldHash = zb.hash(cb);
        ChessBoardInitializer.putPieceOnBoard(cb, new Pawn(0, 0, Player.WHITE, "wp"));
        assertNotEquals(oldHash, zb.hash(cb));
    }

    @Test
    public void hashIsDifferentWithSamePieceInDifferentLocation() {
        Pawn wp = new Pawn(1, 1, Player.WHITE, "wp");
        ChessBoardInitializer.putPieceOnBoard(cb, wp);
        long oldHash = zb.hash(cb);
        assertNotEquals(oldHash,
                zb.updateHash(oldHash, sit.getChessBoard(),
                        cb.getSquare(1, 1), cb.getSquare(1, 2)));
    }
}
