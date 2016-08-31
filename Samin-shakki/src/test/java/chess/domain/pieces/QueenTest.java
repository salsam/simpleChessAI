package chess.domain.pieces;

import chess.domain.board.Player;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sami
 */
public class QueenTest {

    private Queen queen;

    public QueenTest() {
    }

    @Before
    public void setUp() {
        queen = new Queen(2, 3, Player.BLACK, "bq");
    }

    @Test
    public void queensAreEqualIfSamePieceCode() {
        assertTrue(queen.equals(new Queen(7, 6, Player.WHITE, "bq")));
    }

    @Test
    public void queensAreNotEqualIfDifferentPieceCode() {
        assertFalse(queen.equals(new Queen(2, 3, Player.BLACK, "bq1")));
    }

    @Test
    public void cloneReturnsDifferentQueen() {
        assertFalse(queen == queen.clone());
    }

    @Test
    public void cloneReturnsIdenticalQueen() {
        assertTrue(queen.equals(queen.clone()));
    }
}
