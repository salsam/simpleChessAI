package chess.domain.pieces;

import chess.domain.board.Player;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sami
 */
public class KingTest {

    private King king;

    public KingTest() {
    }

    @Before
    public void setUp() {
        king = new King(7, 5, Player.WHITE, "wk");
    }

    @Test
    public void kingsAreNotEqualIfDifferentPieceCode() {
        assertFalse(king.equals(new King(7, 5, Player.WHITE, "wk1")));
    }

    @Test
    public void kingsAreEqualIfOneHasBeenMovedAndOtherHasNotButSamePieceCode() {
        king.setHasBeenMoved(true);
        assertTrue(king.equals(new King(7, 5, Player.WHITE, "wk")));
    }

    @Test
    public void kingsAreEqualIfSamePieceCodeEvenInDifferentLocations() {
        assertTrue(king.equals(new King(3, 5, Player.BLACK, "wk")));
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
