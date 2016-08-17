package chess.logic.ailogic;

import chess.domain.GameSituation;
import chess.domain.Move;
import static chess.domain.board.ChessBoardCopier.copy;
import chess.domain.board.*;
import chess.domain.pieces.*;
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
        ai = new AILogic(3);
    }

    @Before
    public void setUp() {
        situation.reset();
    }

    @Test
    public void findBestMoveChoosesAMove() {
        Pawn pawn = new Pawn(1, 1, Player.WHITE, "wp1");
        putPieceOnBoard(situation.getChessBoard(), pawn);
        ai.findBestMoves(situation);
        assertFalse(ai.getBestMove() == null);
    }

    @Test
    public void bestMoveCorrectWithOnePiece() {
        Pawn pawn = new Pawn(1, 1, Player.WHITE, "wp1");
        putPieceOnBoard(situation.getChessBoard(), pawn);
        ai.findBestMoves(situation);
        assertTrue(ai.getBestMove().getTarget().equals(situation.getChessBoard().getSquare(1, 2))
                || ai.getBestMove().getTarget().equals(situation.getChessBoard().getSquare(1, 3)));
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
        MovementLogic ml = situation.getChessBoard().getMovementLogic();
        ChessBoard cb = situation.getChessBoard();
        Pawn wp = new Pawn(1, 6, Player.WHITE, "wp");
        putPieceOnBoard(cb, wp);
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

}
