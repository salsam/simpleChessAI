package chess.domain.pieces;

import chess.domain.board.Player;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sami
 */
public class RookTest {

    private Rook rook;

    public RookTest() {
    }

    @Before
    public void setUp() {
        rook = new Rook(7, 5, Player.WHITE, "wr");
    }

    @Test
    public void rooksAreEqualIfSamePieceCode() {
        assertTrue(rook.equals(new Rook(4, 5, Player.BLACK, "wr")));
    }

    @Test
    public void rooksAreNotEqualIfDifferentPieceCode() {
        assertFalse(rook.equals(new Rook(7, 5, Player.WHITE, "wr1")));
    }

    @Test
    public void cloneReturnsDifferentRook() {
        assertFalse(rook == rook.clone());
    }

    @Test
    public void cloneReturnsIdenticalRook() {
        assertTrue(rook.equals(rook.clone()));
    }
}
