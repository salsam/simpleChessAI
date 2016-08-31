package chess.domain.pieces;

import chess.domain.board.Player;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sami
 */
public class PawnTest {

    private Pawn pawn;

    public PawnTest() {
    }

    @Before
    public void setUp() {
        pawn = new Pawn(7, 5, Player.WHITE, "wp");
    }

    @Test
    public void pawnsAreNotEqualIfDifferentPieceCode() {
        assertFalse(pawn.equals(new Pawn(7, 5, Player.WHITE, "wp1")));
    }

    @Test
    public void pawnsAreEqualIfSamePieceCode() {
        assertTrue(pawn.equals(new Pawn(1, 5, Player.BLACK, "wp")));
    }

    @Test
    public void cloneReturnsDifferentPawn() {
        assertFalse(pawn == pawn.clone());
    }

    @Test
    public void cloneReturnsIdenticalPawn() {
        assertTrue(pawn.equals(pawn.clone()));
    }
}
