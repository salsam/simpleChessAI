package chess.logic.ailogic;

import chess.domain.GameSituation;
import chess.domain.board.ChessBoard;
import chess.domain.board.Player;
import chess.domain.board.BetterPiece;
import static chess.domain.board.Klass.*;
import static chess.logic.ailogic.GameSituationEvaluator.evaluateGameSituation;
import chess.logic.chessboardinitializers.BetterChessBoardInitializer;
import static chess.logic.chessboardinitializers.ChessBoardInitializer.putPieceOnBoard;
import chess.logic.chessboardinitializers.EmptyBoardInitializer;
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
        situation = new GameSituation(new BetterChessBoardInitializer(), new MovementLogic());
        assertEquals(0, evaluateGameSituation(situation, Player.BLACK));
        assertEquals(0, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void gamesituationValuedNormallyWhenOpponentWillBeStaleMatedOnTheirTurn() {
        BetterPiece queen = new BetterPiece(QUEEN, 1, 1, Player.WHITE, "wp");
        putPieceOnBoard(situation.getChessBoard(), queen);
        assertEquals(880 + 230, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void gameSitutionValuedZeroWhenStaleMate() {
        BetterPiece queen = new BetterPiece(QUEEN, 1, 1, Player.WHITE, "wp");
        putPieceOnBoard(situation.getChessBoard(), queen);
        assertEquals(0, evaluateGameSituation(situation, Player.BLACK));
        assertEquals(0, evaluateGameSituation(situation, Player.BLACK));
    }

    @Test
    public void gameSituationCanHaveNegativeValue() {
        BetterPiece brook = new BetterPiece(ROOK, 0, 0, Player.BLACK, "br");
        BetterPiece bqueen = new BetterPiece(QUEEN, 2, 1, Player.BLACK, "bq");
        BetterPiece wpawn = new BetterPiece(PAWN, 1, 2, Player.WHITE, "wp");

        putPieceOnBoard(situation.getChessBoard(), wpawn);
        putPieceOnBoard(situation.getChessBoard(), brook);
        putPieceOnBoard(situation.getChessBoard(), bqueen);

        assertEquals(-1615, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void takenPiecesAreNotAccountedFor() {
        BetterPiece brook = new BetterPiece(ROOK, 0, 7, Player.BLACK, "br");
        BetterPiece bqueen = new BetterPiece(QUEEN, 2, 6, Player.BLACK, "bq");
        BetterPiece wpawn = new BetterPiece(PAWN, 1, 5, Player.WHITE, "wp");

        putPieceOnBoard(situation.getChessBoard(), wpawn);
        putPieceOnBoard(situation.getChessBoard(), brook);
        putPieceOnBoard(situation.getChessBoard(), bqueen);

        situation.getChessBoard().getMovementLogic()
                .move(wpawn, situation.getChessBoard().getSquare(2, 6), situation);
        assertEquals(-530, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void checkMateWorth100000000() {
        BetterPiece wk = new BetterPiece(KING, 0, 7, Player.WHITE, "wk");
        BetterPiece bk = new BetterPiece(KING, 2, 6, Player.BLACK, "bk");
        BetterPiece bq = new BetterPiece(QUEEN, 1, 2, Player.BLACK, "bq");
        ChessBoard cb = situation.getChessBoard();
        MovementLogic ml = cb.getMovementLogic();

        putPieceOnBoard(cb, wk);
        putPieceOnBoard(cb, bk);
        putPieceOnBoard(cb, bq);
        situation.reHashBoard(true);

        ml.move(bq, cb.getSquare(1, 6), situation);

        assertEquals(-100000000, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void thirdRepetitionOfBoardSituationIsWorthZeroEvenWhenMated() {
        BetterPiece wk = new BetterPiece(KING, 0, 7, Player.WHITE, "wk");
        BetterPiece bk = new BetterPiece(KING, 2, 6, Player.BLACK, "bk");
        BetterPiece bq = new BetterPiece(QUEEN, 1, 2, Player.BLACK, "bq");
        ChessBoard cb = situation.getChessBoard();
        MovementLogic ml = cb.getMovementLogic();

        putPieceOnBoard(cb, wk);
        putPieceOnBoard(cb, bk);
        putPieceOnBoard(cb, bq);
        situation.reHashBoard(true);

        ml.move(bq, cb.getSquare(1, 6), situation);
        ml.move(bq, cb.getSquare(1, 2), situation);
        ml.move(bq, cb.getSquare(1, 6), situation);
        ml.move(bq, cb.getSquare(1, 2), situation);
        ml.move(bq, cb.getSquare(1, 6), situation);

        assertEquals(0, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void thirdRepetitionOfBoardSituationIsWorthZero() {
        BetterPiece wr = new BetterPiece(ROOK, 0, 7, Player.WHITE, "wr");
        putPieceOnBoard(situation.getChessBoard(), wr);
        situation.reHashBoard(true);
        ChessBoard cb = situation.getChessBoard();
        MovementLogic ml = cb.getMovementLogic();
        ml.move(wr, cb.getSquare(0, 6), situation);
        ml.move(wr, cb.getSquare(0, 7), situation);
        ml.move(wr, cb.getSquare(0, 6), situation);
        ml.move(wr, cb.getSquare(0, 7), situation);

        assertEquals(0, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void mobilityValueCorrectForPawn() {
        BetterPiece wpawn = new BetterPiece(PAWN, 1, 5, Player.WHITE, "wp");
        ChessBoard cb = situation.getChessBoard();
        putPieceOnBoard(cb, wpawn);
        assertEquals(20, GameSituationEvaluator.mobilityValue(situation, Player.WHITE));
        cb.getMovementLogic().move(wpawn, cb.getSquare(1, 6), situation);
        assertEquals(10, GameSituationEvaluator.mobilityValue(situation, Player.WHITE));
    }
}
