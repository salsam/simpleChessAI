package chess.domain.board.betterpiece;

import chess.domain.board.BetterPiece;
import chess.domain.board.Player;
import static chess.domain.board.Klass.QUEEN;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sami
 */
public class QueenTest {

    private BetterPiece queen;

    public QueenTest() {
    }

    @Before
    public void setUp() {
        queen = new BetterPiece(QUEEN, 2, 3, Player.BLACK, "bq");
    }

    @Test
    public void queensAreEqualIfSamePieceCode() {
        assertTrue(queen.equals(new BetterPiece(QUEEN, 7, 6, Player.WHITE, "bq")));
    }

    @Test
    public void queensAreNotEqualIfDifferentPieceCode() {
        assertFalse(queen.equals(new BetterPiece(QUEEN, 2, 3, Player.BLACK, "bq1")));
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
