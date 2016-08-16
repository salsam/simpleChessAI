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
        assertEquals(0, evaluateGameSituation(situation, Player.WHITE));
        assertEquals(0, evaluateGameSituation(situation, Player.BLACK));
    }

    @Test
    public void standardStartingPositionIsWorthZero() {
        situation = new GameSituation(new StandardBoardInitializer(), new MovementLogic());
        assertEquals(0, evaluateGameSituation(situation, Player.BLACK));
        assertEquals(0, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void gamesituationValuedNormallyWhenOpponentWillBeStaleMatedOnTheirTurn() {
        Queen queen = new Queen(1, 1, Player.WHITE, "wp");
        putPieceOnBoard(situation.getChessBoard(), queen);
        assertEquals(880 + 23, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void gameSitutionValuedZeroWhenStaleMate() {
        Queen queen = new Queen(1, 1, Player.WHITE, "wp");
        putPieceOnBoard(situation.getChessBoard(), queen);
        assertEquals(0, evaluateGameSituation(situation, Player.BLACK));
        assertEquals(0, evaluateGameSituation(situation, Player.BLACK));
    }

    @Test
    public void gameSituationCanHaveNegativeValue() {
        Rook brook = new Rook(0, 7, Player.BLACK, "br");
        Queen bqueen = new Queen(2, 6, Player.BLACK, "bq");
        Pawn wpawn = new Pawn(1, 5, Player.WHITE, "wp");

        putPieceOnBoard(situation.getChessBoard(), wpawn);
        putPieceOnBoard(situation.getChessBoard(), brook);
        putPieceOnBoard(situation.getChessBoard(), bqueen);

        assertEquals(-1313, evaluateGameSituation(situation, Player.WHITE));
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
                .move(wpawn, situation.getChessBoard().getSquare(2, 6), situation);
        assertEquals(150 - 510 + 1 - 14, evaluateGameSituation(situation, Player.WHITE));
    }
}
