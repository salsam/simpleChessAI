package chess.logic.ailogic;

import chess.domain.GameSituation;
import chess.domain.board.Player;
import chess.domain.pieces.Bishop;
import chess.domain.pieces.King;
import chess.domain.pieces.Knight;
import chess.domain.pieces.Pawn;
import chess.domain.pieces.Queen;
import chess.domain.pieces.Rook;
import static chess.logic.ailogic.GameSituationEvaluator.evaluateGameSituation;
import static chess.logic.chessboardinitializers.ChessBoardInitializer.putPieceOnBoard;
import chess.logic.chessboardinitializers.EmptyBoardInitializer;
import chess.logic.chessboardinitializers.StandardBoardInitializer;
import chess.logic.movementlogic.MovementLogic;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sami
 */
public class GameSituationEvaluatorTest {

    private static GameSituation situation;
    private static EmptyBoardInitializer init;

    public GameSituationEvaluatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        init = new EmptyBoardInitializer();
        situation = new GameSituation(init, new MovementLogic());
    }

    @Before
    public void setUp() {
        situation.reset();
        init.initialize(situation.getChessBoard());
    }

    @Test
    public void emptyChessBoardValuedZero() {
        assertEquals(0, evaluateGameSituation(situation, Player.BLACK));
        assertEquals(0, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void standardStartingPositionIsWorthZero() {
        situation = new GameSituation(new StandardBoardInitializer(), new MovementLogic());
        assertEquals(0, evaluateGameSituation(situation, Player.BLACK));
        assertEquals(0, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void oneWhitePawnValuedOneHundredForWhiteAndMinusOneHundredForBlack() {
        Pawn pawn = new Pawn(1, 1, Player.WHITE, "wp");
        putPieceOnBoard(situation.getChessBoard(), pawn);
        assertEquals(102, evaluateGameSituation(situation, Player.WHITE));
        assertEquals(-102, evaluateGameSituation(situation, Player.BLACK));
    }

    @Test
    public void gameSituationCanHaveNegativeValue() {
        Rook brook = new Rook(0, 7, Player.BLACK, "br");
        Queen bqueen = new Queen(2, 6, Player.BLACK, "bq");
        Pawn wpawn = new Pawn(1, 5, Player.WHITE, "wp");

        putPieceOnBoard(situation.getChessBoard(), wpawn);
        putPieceOnBoard(situation.getChessBoard(), brook);
        putPieceOnBoard(situation.getChessBoard(), bqueen);

        assertEquals(-1323, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void takenPiecesAreNotAccountedFor() {
        Rook brook = new Rook(0, 7, Player.BLACK, "br");
        Queen bqueen = new Queen(2, 6, Player.BLACK, "bq");
        Pawn wpawn = new Pawn(1, 5, Player.WHITE, "wp");

        putPieceOnBoard(situation.getChessBoard(), wpawn);
        putPieceOnBoard(situation.getChessBoard(), brook);
        putPieceOnBoard(situation.getChessBoard(), bqueen);

        situation.getChessBoard().getMovementLogic()
                .move(wpawn, situation.getChessBoard().getSquare(2, 6), situation.getChessBoard());
        assertEquals(-423, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void kingThatCanMoveToAllDirectionsIsValued40008() {
        King wk = new King(2, 5, Player.WHITE, "wk");

        putPieceOnBoard(situation.getChessBoard(), wk);

        assertEquals(40008, evaluateGameSituation(situation, Player.WHITE));
        assertEquals(-40008, evaluateGameSituation(situation, Player.BLACK));
    }

    @Test
    public void kingsMobilityValueIsThreeInCorners() {
        King wk = new King(0, 0, Player.WHITE, "wk");
        putPieceOnBoard(situation.getChessBoard(), wk);
        assertEquals(40003, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void knightsHaveMobilityValueEightInCenter() {
        Knight wn = new Knight(3, 4, Player.WHITE, "wn");
        putPieceOnBoard(situation.getChessBoard(), wn);
        assertEquals(328, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void bishopsHaveMobilityValue13InCenter() {
        Bishop wb = new Bishop(3, 4, Player.WHITE, "wb");
        putPieceOnBoard(situation.getChessBoard(), wb);
        assertEquals(346, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void queenHasMobilityValue27InCenter() {
        Queen wq = new Queen(3, 4, Player.WHITE, "wq");
        putPieceOnBoard(situation.getChessBoard(), wq);
        assertEquals(907, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void pawnHasMobilityValueTwoOnFirstMovementButOneAfterWards() {
        Pawn wp = new Pawn(3, 4, Player.WHITE, "wq");
        putPieceOnBoard(situation.getChessBoard(), wp);
        assertEquals(102, evaluateGameSituation(situation, Player.WHITE));
        situation.getChessBoard().getMovementLogic()
                .move(wp, situation.getChessBoard().getSquare(3, 6), situation.getChessBoard());
        assertEquals(101, evaluateGameSituation(situation, Player.WHITE));
    }
}
