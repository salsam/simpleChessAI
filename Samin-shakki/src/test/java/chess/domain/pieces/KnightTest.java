package chess.domain.pieces;

import chess.domain.pieces.Knight;
import chess.domain.board.Player;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sami
 */
public class KnightTest {

    private Knight knight;

    public KnightTest() {
    }

    @Before
    public void setUp() {
        knight = new Knight(2, 3, Player.BLACK, "bn");
    }

    @Test
    public void cloneReturnsDifferentKnight() {
        assertFalse(knight == knight.clone());
    }

    @Test
    public void cloneReturnsIdenticalKnight() {
        assertTrue(knight.equals(knight.clone()));
    }
}
