package chess.domain.board.betterpiece;

import chess.domain.board.BetterPiece;
import chess.domain.board.Player;
import static chess.domain.board.Klass.BISHOP;
import static chess.domain.board.Klass.KING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author sami
 */
public class BishopTest {

    private BetterPiece bishop;

    public BishopTest() {
    }

    @Before
    public void setUp() {
        bishop = new BetterPiece(BISHOP, 2, 3, Player.BLACK, "bp");
    }

    @Test
    public void bishopIsNotEqualToNull() {
        assertFalse(bishop.deepEquals(null));
    }

    @Test
    public void bishopsAreNotEqualToKingsEvenWithSamePieceCode() {
        assertFalse(bishop.deepEquals(new BetterPiece(KING, 2, 3, Player.BLACK, "bp")));
    }

    @Test
    public void bishopsAreEqualIfSamePieceCode() {
        assertTrue(bishop.equals(new BetterPiece(BISHOP, 1, 1, Player.WHITE, "bp")));
    }

    @Test
    public void bishopsAreNotEqualIfDifferentPieceCode() {
        assertFalse(bishop.equals(new BetterPiece(BISHOP, 2, 3, Player.BLACK, "bp1")));
    }

    @Test
    public void cloneIsAlsoBishop() {
        assertEquals(BISHOP, bishop.clone().getKlass());
    }

    @Test
    public void cloneReturnsDifferentBishop() {
        assertFalse(bishop == bishop.clone());
    }

    @Test
    public void cloneReturnsIdenticalBishop() {
        assertTrue(bishop.deepEquals(bishop.clone()));
    }

}
