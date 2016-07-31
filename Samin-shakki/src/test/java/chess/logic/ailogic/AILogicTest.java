package chess.logic.ailogic;

import chess.domain.GameSituation;
import chess.domain.Move;
import chess.domain.board.Player;
import chess.domain.board.Square;
import chess.domain.pieces.King;
import chess.domain.pieces.Knight;
import chess.domain.pieces.Pawn;
import chess.domain.pieces.Queen;
import static chess.logic.chessboardinitializers.ChessBoardInitializer.putPieceOnBoard;
import chess.logic.chessboardinitializers.EmptyBoardInitializer;
import chess.logic.movementlogic.MovementLogic;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sami
 */
public class AILogicTest {

    private static GameSituation situation;
    private static AILogic ai;

    public AILogicTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        situation = new GameSituation(new EmptyBoardInitializer(), new MovementLogic());
        ai = new AILogic();
    }

    @Before
    public void setUp() {
        situation.reset();
    }

    @Test
    public void findBestMoveChoosesAMove() {
        Pawn pawn = new Pawn(1, 1, Player.WHITE, "wp1");
        putPieceOnBoard(situation.getChessBoard(), pawn);
        ai.findBestMove(situation);
        assertFalse(ai.getBestMove() == null);
    }

    @Test
    public void bestMoveCorrectWithOnePiece() {
        Pawn pawn = new Pawn(1, 1, Player.WHITE, "wp1");
        putPieceOnBoard(situation.getChessBoard(), pawn);
        ai.findBestMove(situation);
        System.out.println(ai.getBestMove().getTarget());
        assertTrue(ai.getBestMove().getTarget().equals(situation.getChessBoard().getSquare(1, 2))
                || ai.getBestMove().getTarget().equals(situation.getChessBoard().getSquare(1, 3)));
        assertEquals(pawn, ai.getBestMove().getPiece());
    }

    @Test
    public void checkMateIsBetterThanTakingKnight() {
        King bk = new King(0, 7, Player.BLACK, "bk");
        Knight bn = new Knight(1, 0, Player.BLACK, "bn");
        Queen wq = new Queen(1, 3, Player.WHITE, "wq");
        King wk = new King(2, 5, Player.WHITE, "wk");

        putPieceOnBoard(situation.getChessBoard(), wk);
        putPieceOnBoard(situation.getChessBoard(), bk);
        putPieceOnBoard(situation.getChessBoard(), bn);
        putPieceOnBoard(situation.getChessBoard(), wq);

        ai.findBestMove(situation);
        System.out.println(ai.getBestMove().getTarget());
        System.out.println(ai.getBestMove().getPiece());
        assertEquals(new Move(wq, situation.getChessBoard().getSquare(1, 6)), ai.getBestMove());
    }

    @Test
    public void aiWillNotInitiateLosingTrades() {
        King bk = new King(0, 1, Player.BLACK, "bk");
        Knight bn = new Knight(1, 0, Player.BLACK, "bn");
        Queen wq = new Queen(1, 3, Player.WHITE, "wq");

        putPieceOnBoard(situation.getChessBoard(), bk);
        putPieceOnBoard(situation.getChessBoard(), bn);
        putPieceOnBoard(situation.getChessBoard(), wq);

        ai.findBestMove(situation);
        System.out.println(ai.getBestMove().getTarget());
        assertNotEquals(new Move(wq, new Square(1, 0)), ai.getBestMove());
    }
}
