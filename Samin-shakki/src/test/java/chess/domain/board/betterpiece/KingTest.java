package chess.domain.board.betterpiece;

import chess.domain.board.BetterPiece;
import chess.domain.board.Player;
import static chess.domain.board.Klass.KING;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sami
 */
public class KingTest {

    private BetterPiece king;

    public KingTest() {
    }

    @Before
    public void setUp() {
        king = new BetterPiece(KING, 7, 5, Player.WHITE, "wk");
    }

    @Test
    public void kingsAreNotEqualIfDifferentPieceCode() {
        assertFalse(king.equals(new BetterPiece(KING, 7, 5, Player.WHITE, "wk1")));
    }

    @Test
    public void kingsAreEqualIfOneHasBeenMovedAndOtherHasNotButSamePieceCode() {
        king.setHasBeenMoved(true);
        assertTrue(king.equals(new BetterPiece(KING, 7, 5, Player.WHITE, "wk")));
    }

    @Test
    public void kingsAreEqualIfSamePieceCodeEvenInDifferentLocations() {
        assertTrue(king.equals(new BetterPiece(KING, 3, 5, Player.BLACK, "wk")));
    }

    @Test
    public void cloneReturnsDifferentKing() {
        assertFalse(king == king.clone());
    }

    @Test
    public void cloneReturnsIdenticalKing() {
        assertTrue(king.equals(king.clone()));
    }
}
