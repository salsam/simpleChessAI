package chess.logic.ailogic;

import chess.domain.GameSituation;
import chess.domain.board.ChessBoard;
import static chess.domain.board.ChessBoardCopier.copy;
import chess.domain.board.Player;
import chess.domain.board.Square;
import chess.domain.pieces.Knight;
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
        assertEquals(oldHash, zb.getHashAfterMove(oldHash, cb, from, to));
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
                zb.getHashAfterMove(oldHash, sit.getChessBoard(),
                        cb.getSquare(1, 1), cb.getSquare(1, 2)));
    }
    
    @Test
    public void takenPiecesAreNotAccountedForHashing() {
        long oldHash = zb.hash(cb);
        Pawn wp = new Pawn(1, 1, Player.WHITE, "wp");
        ChessBoardInitializer.putPieceOnBoard(cb, wp);
        wp.setTaken(true);
        assertEquals(oldHash, zb.hash(cb));
    }
    
    @Test
    public void hashCorrectAfterAMoveIsMade() {
        Knight wn = new Knight(1, 0, Player.WHITE, "wn");
        ChessBoardInitializer.putPieceOnBoard(cb, wn);
        Square from = cb.getSquare(1, 0);
        Square to = cb.getSquare(2, 2);
        long hash = zb.getHashAfterMove(zb.hash(cb), cb, from, to);
        ml.move(wn, to, sit);
        assertEquals(zb.hash(cb), hash);
    }
    
    @Test
    public void hashBeforeMoveCorrect() {
        Knight wn = new Knight(1, 0, Player.WHITE, "wn");
        ChessBoardInitializer.putPieceOnBoard(cb, wn);
        ChessBoard backup = copy(cb);
        long before = zb.hash(cb);
        Square from = cb.getSquare(1, 0);
        Square to = cb.getSquare(2, 2);
        ml.move(wn, to, sit);
        long hash = zb.getHashBeforeMove(zb.hash(cb), cb, backup, from, to);
        assertEquals(before, hash);
    }
    
    @Test
    public void hashingAMoveAndUndoingItMakesHashRemainSame() {
        Knight wn = new Knight(1, 0, Player.WHITE, "wn");
        ChessBoardInitializer.putPieceOnBoard(cb, wn);
        ChessBoard backup = copy(cb);
        long oldHash = zb.hash(cb);
        Square from = cb.getSquare(1, 0);
        Square to = cb.getSquare(2, 2);
        long after = zb.getHashAfterMove(oldHash, cb, from, to);
        ml.move(wn, to, sit);
        long before = zb.getHashBeforeMove(after, cb, backup, from, to);
        assertEquals(oldHash, before);
    }
}
