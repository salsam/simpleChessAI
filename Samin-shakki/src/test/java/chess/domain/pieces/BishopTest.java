package chess.domain.pieces;

import chess.domain.pieces.Bishop;
import chess.domain.pieces.King;
import chess.domain.board.Player;
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

    private Bishop bishop;

    public BishopTest() {
    }

    @Before
    public void setUp() {
        bishop = new Bishop(2, 3, Player.BLACK, "bp");
    }

    @Test
    public void bishopIsNotEqualToNull() {
        assertFalse(bishop.equals(null));
    }

    @Test
    public void bishopsAreNotEqualToKingsEvenWithSamePieceCode() {
        assertFalse(bishop.equals(new King(2, 3, Player.BLACK, "bp")));
    }

    @Test
    public void bishopsAreEqualIfSamePieceCode() {
        assertTrue(bishop.equals(new Bishop(1, 1, Player.WHITE, "bp")));
    }

    @Test
    public void bishopsAreNotEqualIfDifferentPieceCode() {
        assertFalse(bishop.equals(new Bishop(2, 3, Player.BLACK, "bp1")));
    }

    @Test
    public void cloneIsAlsoBishop() {
        assertEquals(Bishop.class, bishop.clone().getClass());
    }

    @Test
    public void cloneReturnsDifferentBishop() {
        assertFalse(bishop == bishop.clone());
    }

    @Test
    public void cloneReturnsIdenticalBishop() {
        assertTrue(bishop.equals(bishop.clone()));
    }

}
