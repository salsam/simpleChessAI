package chess.logic.ailogic;

import chess.domain.GameSituation;
import chess.domain.Move;
import static chess.domain.board.ChessBoardCopier.copy;
import chess.domain.board.*;
import chess.domain.datastructures.TranspositionKey;
import chess.domain.pieces.*;
import static chess.logic.ailogic.GameSituationEvaluator.evaluateGameSituation;
import static chess.logic.chessboardinitializers.ChessBoardInitializer.putPieceOnBoard;
import chess.logic.chessboardinitializers.*;
import chess.logic.movementlogic.MovementLogic;
import static org.junit.Assert.*;
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
        ai.setSituation(situation);
        ai.setSearchDepth(3);
    }

    @Test
    public void findBestMoveChoosesAMove() {
        Pawn pawn = new Pawn(1, 7, Player.WHITE, "wp1");
        putPieceOnBoard(situation.getChessBoard(), pawn);
        situation.reHashBoard(true);
        ai.findBestMoves(situation);
        assertFalse(ai.getBestMove() == null);
    }

    @Test
    public void bestMoveCorrectWithOnePiece() {
        Pawn pawn = new Pawn(0, 7, Player.WHITE, "wp1");
        Pawn bpawn = new Pawn(7, 0, Player.BLACK, "bp1");
        putPieceOnBoard(situation.getChessBoard(), bpawn);
        putPieceOnBoard(situation.getChessBoard(), pawn);
        situation.reHashBoard(true);
        ai.findBestMoves(situation);
        assertTrue(ai.getBestMove().getTarget().equals(situation.getChessBoard().getSquare(0, 6))
                || ai.getBestMove().getTarget().equals(situation.getChessBoard().getSquare(0, 5)));
        assertEquals(pawn, ai.getBestMove().getPiece());
    }

    @Test
    public void checkMateIsBetterThanTakingKnight() {
        King bk = new King(0, 7, Player.BLACK, "bk");
        Knight bn = new Knight(7, 3, Player.BLACK, "bn");
        Queen wq = new Queen(1, 3, Player.WHITE, "wq");
        King wk = new King(2, 5, Player.WHITE, "wk");

        putPieceOnBoard(situation.getChessBoard(), wk);
        putPieceOnBoard(situation.getChessBoard(), bk);
        putPieceOnBoard(situation.getChessBoard(), bn);
        putPieceOnBoard(situation.getChessBoard(), wq);

        situation.reHashBoard(true);

        ai.findBestMoves(situation);
        assertEquals(new Move(wq, situation.getChessBoard().getSquare(1, 6)), ai.getBestMove());
    }

    @Test
    public void aiWillNotInitiateLosingTrades() {
        Bishop bb = new Bishop(0, 1, Player.BLACK, "bb");
        Knight bn = new Knight(1, 0, Player.BLACK, "bn");
        Queen wq = new Queen(1, 3, Player.WHITE, "wq");

        putPieceOnBoard(situation.getChessBoard(), bb);
        putPieceOnBoard(situation.getChessBoard(), bn);
        putPieceOnBoard(situation.getChessBoard(), wq);

        ai.findBestMoves(situation);
        assertNotEquals(new Move(wq, new Square(1, 0)), ai.getBestMove());
    }

    @Test
    public void findBestMoveDoesNotChangeChessBoard() {
        situation = new GameSituation(new StandardBoardInitializer(), new MovementLogic());
        ChessBoard backUp = copy(situation.getChessBoard());
        ai.findBestMoves(situation);

        for (Player player : Player.values()) {
            backUp.getPieces(player).stream().forEach((piece) -> {
                assertTrue(situation.getChessBoard().getPieces(player).contains(piece));
            });
        }
        situation = new GameSituation(new EmptyBoardInitializer(), new MovementLogic());

    }

    @Test
    public void findBestMoveDoesNotChangePawns() {
        situation = new GameSituation(new StandardBoardInitializer(), new MovementLogic());
        MovementLogic ml = situation.getChessBoard().getMovementLogic();
        ChessBoard cb = situation.getChessBoard();
        ml.move(cb.getSquare(1, 1).getPiece(), cb.getSquare(1, 3), situation);
        ChessBoard backUp = copy(situation.getChessBoard());
        ai.findBestMoves(situation);

        for (Player player : Player.values()) {
            for (Piece p : backUp.getPieces(player)) {
                for (Piece newP : situation.getChessBoard().getPieces(player)) {
                    if (p.equals(newP) && p.getClass() == Pawn.class) {
                        Pawn np = (Pawn) newP;
                        Pawn op = (Pawn) p;
                        assertEquals(op.getHasBeenMoved(), np.getHasBeenMoved());
                    }
                }
            }
        }
        situation = new GameSituation(new EmptyBoardInitializer(), new MovementLogic());
    }

    @Test
    public void pawnRemainsPawnIfPromotionIsTested() {
        ChessBoard cb = situation.getChessBoard();
        Pawn wp = new Pawn(1, 4, Player.WHITE, "wp");
        putPieceOnBoard(cb, wp);
        MovementLogic ml = cb.getMovementLogic();
        ml.move(wp, cb.getSquare(1, 5), situation);
        ChessBoard backUp = copy(situation.getChessBoard());
        ai.findBestMoves(situation);

        for (Player player : Player.values()) {
            for (Piece p : backUp.getPieces(player)) {
                for (Piece newP : situation.getChessBoard().getPieces(player)) {
                    if (p.equals(newP)) {
                        assertEquals(Pawn.class, p.getClass());
                        assertEquals(p.getClass(), newP.getClass());
                        assertEquals(p.getColumn(), newP.getColumn());
                        assertEquals(p.getRow(), newP.getRow());
                        assertEquals(p.getOwner(), newP.getOwner());
                    }
                }
            }
        }
    }

    @Test
    public void pawnRemainsPawnInComplexSituation() {
        ChessBoard cb = situation.getChessBoard();
        Pawn wp = new Pawn(1, 4, Player.WHITE, "wp");
        Queen wq = new Queen(2, 1, Player.WHITE, "wq1");
        King bk = new King(0, 0, Player.BLACK, "bk");

        putPieceOnBoard(cb, bk);
        putPieceOnBoard(cb, wp);
        putPieceOnBoard(cb, wq);
        situation.reHashBoard(true);

        ChessBoard backUp = copy(situation.getChessBoard());
        ai.findBestMoves(situation);

        for (Player player : Player.values()) {
            for (Piece p : backUp.getPieces(player)) {
                for (Piece newP : situation.getChessBoard().getPieces(player)) {
                    if (p.equals(newP)) {
                        assertEquals(p.getClass(), newP.getClass());
                        assertEquals(p.getColumn(), newP.getColumn());
                        assertEquals(p.getRow(), newP.getRow());
                        assertEquals(p.getOwner(), newP.getOwner());
                    }
                }
            }
        }
    }

    @Test
    public void negamaxForHeightZeroReturnsValueOfSituation() {
        ChessBoard cb = situation.getChessBoard();
        Rook wr = new Rook(1, 4, Player.WHITE, "wr");
        Pawn bp = new Pawn(5, 6, Player.BLACK, "bp");
        putPieceOnBoard(cb, wr);
        putPieceOnBoard(cb, bp);
        situation.reHashBoard(true);
        ai.setSituation(situation);
        ai.setStart(System.currentTimeMillis());
        assertEquals(evaluateGameSituation(situation, Player.WHITE), ai.negaMax(0, -123456798, 123456789, Player.WHITE));
    }

    @Test
    public void negamaxReturnsMinus123456789IfTimeLimitIsReached() {
        ChessBoard cb = situation.getChessBoard();
        Rook wr = new Rook(1, 4, Player.WHITE, "wr");
        Pawn bp = new Pawn(5, 6, Player.BLACK, "bp");
        putPieceOnBoard(cb, wr);
        putPieceOnBoard(cb, bp);
        situation.reHashBoard(true);
        ai.setStart(System.currentTimeMillis() - 1000);
        assertEquals(-123456789, ai.negaMax(0, -12345679, 12345678, Player.WHITE));

    }

    @Test
    public void checkForChangeIncreasesAlphaIfBetterValueFound() {
        ChessBoard cb = situation.getChessBoard();
        Pawn wp = new Pawn(1, 4, Player.WHITE, "wp");
        Pawn bp = new Pawn(5, 6, Player.BLACK, "bp");
        putPieceOnBoard(cb, wp);
        putPieceOnBoard(cb, bp);
        situation.reHashBoard(true);

        ai.getBestValues()[1] = -100;
        ai.setSituation(situation);
        ai.setStart(System.currentTimeMillis());
        int newAlpha = ai.checkForChange(wp, cb.getSquare(1, 4), 1, Player.WHITE, -123456789, -123456789, 123456789);
        assertEquals(evaluateGameSituation(situation, Player.WHITE), newAlpha);
        assertEquals(evaluateGameSituation(situation, Player.WHITE), ai.getBestValues()[1]);
    }

    @Test
    public void alphaIsNotChangedIfWorseValueFound() {
        ChessBoard cb = situation.getChessBoard();
        Pawn wp = new Pawn(1, 4, Player.WHITE, "wp");
        Pawn bp = new Pawn(5, 6, Player.BLACK, "bp");
        putPieceOnBoard(cb, wp);
        putPieceOnBoard(cb, bp);
        situation.reHashBoard(true);

        ai.getBestValues()[1] = 100;
        ai.setSituation(situation);
        ai.setStart(System.currentTimeMillis());
        int newAlpha = ai.checkForChange(wp, cb.getSquare(1, 4), 1, Player.WHITE, 100, 100, 123456789);
        assertEquals(100, newAlpha);
        assertEquals(100, ai.getBestValues()[1]);
    }

    @Test
    public void moveIsNotEvaluatedIfItIsIllegal() {
        ChessBoard cb = situation.getChessBoard();
        MovementLogic ml = cb.getMovementLogic();
        King wk = new King(1, 0, Player.WHITE, "wk");
        Bishop wb = new Bishop(1, 4, Player.WHITE, "wp");
        Rook br = new Rook(1, 7, Player.BLACK, "br");
        putPieceOnBoard(cb, wk);
        putPieceOnBoard(cb, wb);
        putPieceOnBoard(cb, br);
        situation.reHashBoard(true);
        ml.move(wb, cb.getSquare(2, 3), situation);

        ai.setSituation(situation);
        ai.getBestValues()[1] = -250;
        ai.setStart(System.currentTimeMillis());
        int newAlpha = ai.checkForChange(wb, cb.getSquare(1, 4), 1, Player.WHITE, -250, -250, 123456789);
        assertEquals(-250, newAlpha);
        assertEquals(-250, ai.getBestValues()[1]);
    }

//    @Test
//    public void checkForChangeSavesFoundValueInTranspositionTable() {
//        ChessBoard cb = situation.getChessBoard();
//        Pawn wp = new Pawn(1, 4, Player.WHITE, "wp");
//        Pawn bp = new Pawn(5, 6, Player.BLACK, "bp");
//        putPieceOnBoard(cb, wp);
//        putPieceOnBoard(cb, bp);
//        situation.reHashBoard(true);
//
//        ai.setSituation(situation);
//        ai.setStart(System.currentTimeMillis());
//        ai.getTranspositionTable().clear();
//        assertTrue(ai.getTranspositionTable().isEmpty());
//        ai.checkForChange(Player.WHITE, 1, -123456789, 123456789, wp, cb.getSquare(1, 3));
//        TranspositionKey key = new TranspositionKey(1, Player.WHITE, situation.getBoardHash());
//        assertFalse(ai.getTranspositionTable().isEmpty());
//        assertTrue(ai.getTranspositionTable().containsKey(key));
//        assertEquals(evaluateGameSituation(situation, Player.WHITE), (int) ai.getTranspositionTable().get(key));
//    }
    @Test
    public void testAMoveReturnsAlphaIfTimeIsUpAndDoesNotChangeBoard() {
        ChessBoard cb = situation.getChessBoard();
        Rook wr = new Rook(1, 4, Player.WHITE, "wr");
        Pawn bp = new Pawn(5, 6, Player.BLACK, "bp");
        putPieceOnBoard(cb, wr);
        putPieceOnBoard(cb, bp);
        situation.reHashBoard(true);
        ChessBoard backUp = copy(cb);
        ai.setStart(System.currentTimeMillis() - 1000);
        assertEquals(-12345, ai.testAMove(wr, cb.getSquare(1, 6), cb.getSquare(1, 4), Player.WHITE, 1, -12345, -12345, 123456789, backUp));
        assertTrue(ChessBoardCopier.chessBoardsAreDeeplyEqual(cb, backUp));
    }

    @Test
    public void boardDoesNotChangeWhenTestingAMove() {
        ChessBoard cb = situation.getChessBoard();
        Rook wr = new Rook(1, 4, Player.WHITE, "wr");
        Pawn bp = new Pawn(5, 6, Player.BLACK, "bp");
        putPieceOnBoard(cb, wr);
        putPieceOnBoard(cb, bp);
        situation.reHashBoard(true);
        ChessBoard backUp = copy(cb);
        ai.setStart(System.currentTimeMillis());
        ai.setSituation(situation);
        ai.testAMove(wr, cb.getSquare(1, 6), cb.getSquare(1, 4), Player.WHITE, 1, -12345, -12345, 1234567, backUp);
        assertTrue(ChessBoardCopier.chessBoardsAreDeeplyEqual(cb, backUp));
    }

    @Test
    public void testAMoveReturnsValueOfSituationAfterMakingChosenMove() {
        ChessBoard cb = situation.getChessBoard();
        Rook wr = new Rook(1, 4, Player.WHITE, "wr");
        Pawn bp = new Pawn(5, 6, Player.BLACK, "bp");
        putPieceOnBoard(cb, wr);
        putPieceOnBoard(cb, bp);
        situation.reHashBoard(true);
        ChessBoard backUp = copy(cb);
        ai.setSituation(situation);
        ai.setStart(System.currentTimeMillis());
        assertEquals(470, ai.testAMove(wr, cb.getSquare(1, 6), cb.getSquare(1, 4), Player.WHITE, 1, -12345, -12345, 1234567, backUp));
    }

    @Test
    public void testAMoveRevertsPossiblePromotion() {
        ChessBoard cb = situation.getChessBoard();
        Pawn wp = new Pawn(1, 6, Player.WHITE, "wp");
        Pawn bp = new Pawn(5, 6, Player.BLACK, "bp");
        putPieceOnBoard(cb, wp);
        putPieceOnBoard(cb, bp);
        situation.reHashBoard(true);
        ai.setStart(System.currentTimeMillis());
        ai.setSituation(situation);
        ChessBoard backUp = copy(cb);
        ai.testAMove(wp, cb.getSquare(1, 7), cb.getSquare(1, 6), Player.WHITE, 1, -12345, -12345, 1234567, backUp);
        assertTrue(ChessBoardCopier.chessBoardsAreDeeplyEqual(cb, backUp));
        assertEquals(Pawn.class, cb.getSquare(1, 6).getPiece().getClass());
    }

    @Test
    public void testAMoveRevertsPossiblePromotionEvenIfMovesMadeAfterIt() {
        ChessBoard cb = situation.getChessBoard();
        Pawn wp = new Pawn(1, 6, Player.WHITE, "wp");
        Pawn bp = new Pawn(5, 6, Player.BLACK, "bp");
        putPieceOnBoard(cb, wp);
        putPieceOnBoard(cb, bp);
        situation.reHashBoard(true);
        ai.setStart(System.currentTimeMillis());
        ai.setSituation(situation);
        ai.setSearchDepth(2);
        ChessBoard backUp = copy(cb);
        ai.testAMove(wp, cb.getSquare(1, 7), cb.getSquare(1, 6), Player.WHITE, 2, -12345, -12345, 1234567, backUp);
        assertTrue(ChessBoardCopier.chessBoardsAreDeeplyEqual(cb, backUp));
        assertEquals(Pawn.class, cb.getSquare(1, 6).getPiece().getClass());
    }

    @Test
    public void tryMovingPieceStopsIfTimeLimitReached() {
        ChessBoard cb = situation.getChessBoard();
        Rook wr = new Rook(1, 4, Player.WHITE, "wr");
        Pawn bp = new Pawn(5, 6, Player.BLACK, "bp");
        putPieceOnBoard(cb, wr);
        putPieceOnBoard(cb, bp);
        situation.reHashBoard(true);
        ChessBoard backUp = copy(cb);
        ai.setSituation(situation);
        ai.setStart(System.currentTimeMillis() - 1000);
        assertEquals(-12345, ai.tryMovingPiece(1, 1, wr, cb.getSquare(1, 4), -12345, -12345, 123456, Player.WHITE, backUp));
    }

    @Test
    public void tryMovingPieceReturnsHighestValueForSituationAfterMovingPiece() {
        ChessBoard cb = situation.getChessBoard();
        Rook wr = new Rook(1, 4, Player.WHITE, "wr");
        Pawn bp = new Pawn(5, 6, Player.BLACK, "bp");
        putPieceOnBoard(cb, wr);
        putPieceOnBoard(cb, bp);
        situation.reHashBoard(true);
        ChessBoard backUp = copy(cb);
        ai.setSituation(situation);
        ai.setStart(System.currentTimeMillis());
        assertEquals(500, ai.tryMovingPiece(1, 1, wr, cb.getSquare(1, 4), -12345, -12345, 123456, Player.WHITE, backUp));
    }

    @Test
    public void whenMoveDoesNotProduceBetaCutOffItIsSavedAsKillerCandidate() {
        ChessBoard cb = situation.getChessBoard();
        Pawn wp = new Pawn(1, 6, Player.WHITE, "wp");
        Pawn bp = new Pawn(5, 6, Player.BLACK, "bp");
        putPieceOnBoard(cb, wp);
        putPieceOnBoard(cb, bp);
        situation.reHashBoard(true);
        ChessBoard backUp = copy(cb);

        ai.setSituation(situation);
        ai.setSearchDepth(1);
        ai.getKillerCandidates()[0] = null;

        ai.setStart(System.currentTimeMillis());
        ai.tryMovingPiece(1, 1, wp, cb.getSquare(1, 6), -12345, -12345, 123456, Player.WHITE, backUp);
        assertNotEquals(null, ai.getKillerCandidates()[0]);
    }

    @Test
    public void whenMoveDoesProduceBetaCutOffItIsNotSavedAsKillerCandidate() {
        ChessBoard cb = situation.getChessBoard();
        Pawn wp = new Pawn(1, 2, Player.WHITE, "wp");
        Pawn bp = new Pawn(5, 1, Player.BLACK, "bp");
        putPieceOnBoard(cb, wp);
        putPieceOnBoard(cb, bp);
        situation.reHashBoard(true);
        ChessBoard backUp = copy(cb);

        ai.setSituation(situation);
        ai.setSearchDepth(1);
        ai.getKillerCandidates()[0] = null;

        ai.setStart(System.currentTimeMillis());
        ai.tryMovingPiece(1, 1, wp, cb.getSquare(1, 2), -12345, -12345, 0, Player.WHITE, backUp);
        assertEquals(null, ai.getKillerCandidates()[0]);
    }
}
