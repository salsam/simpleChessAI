package chess.logic.ailogic;

import chess.domain.GameSituation;
import chess.domain.board.Player;
import chess.domain.board.Square;
import chess.domain.pieces.Pawn;
import static chess.logic.chessboardinitializers.ChessBoardInitializer.putPieceOnBoard;
import chess.logic.chessboardinitializers.EmptyBoardInitializer;
import chess.logic.movementlogic.MovementLogic;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sami
 */
public class AILogicTest {

    private static GameSituation game;
    private static AILogic ai;

    public AILogicTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        game = new GameSituation(new EmptyBoardInitializer(), new MovementLogic());
        ai = new AILogic();
    }

    @Before
    public void setUp() {
        game.reset();
    }

    @Test
    public void findBestMoveAlwaysChoosesAMove() {
        Pawn pawn = new Pawn(1, 1, Player.WHITE, "wp1");
        putPieceOnBoard(game.getChessBoard(), pawn);
        ai.findBestMove(game);
        assertFalse(ai.getBestMove() == null);
    }

    @Test
    public void bestMoveCorrectWithOnePiece() {
        Pawn pawn = new Pawn(1, 1, Player.WHITE, "wp1");
        putPieceOnBoard(game.getChessBoard(), pawn);
        ai.findBestMove(game);
        assertEquals(new Square(1, 2), ai.getBestMove().getTarget());
        assertEquals(pawn, ai.getBestMove().getPiece());
    }
}
