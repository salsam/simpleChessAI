package chess.logic.ailogic;

import chess.domain.GameSituation;
import chess.domain.board.ChessBoard;
import chess.domain.board.Player;
import chess.domain.pieces.King;
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
        assertEquals(880 + 230, evaluateGameSituation(situation, Player.WHITE));
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

        assertEquals(-1290 - 320, evaluateGameSituation(situation, Player.WHITE));
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
        assertEquals(150 - 510 - 130, evaluateGameSituation(situation, Player.WHITE));
    }

    @Test
    public void checkMateWorth100000000() {
        King wk = new King(0, 7, Player.WHITE, "wk");
        King bk = new King(2, 6, Player.BLACK, "bk");
        Queen bq = new Queen(1, 2, Player.BLACK, "bq");
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
        King wk = new King(0, 7, Player.WHITE, "wk");
        King bk = new King(2, 6, Player.BLACK, "bk");
        Queen bq = new Queen(1, 2, Player.BLACK, "bq");
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
        Rook wr = new Rook(0, 7, Player.WHITE, "wr");
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
        Pawn wpawn = new Pawn(1, 5, Player.WHITE, "wp");
        ChessBoard cb = situation.getChessBoard();
        putPieceOnBoard(cb, wpawn);
        assertEquals(20, GameSituationEvaluator.mobilityValue(situation, Player.WHITE));
        cb.getMovementLogic().move(wpawn, cb.getSquare(1, 6), situation);
        assertEquals(10, GameSituationEvaluator.mobilityValue(situation, Player.WHITE));
    }
}
