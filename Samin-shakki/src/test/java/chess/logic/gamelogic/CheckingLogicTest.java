package chess.logic.gamelogic;

import chess.domain.GameSituation;
import chess.domain.board.ChessBoard;
import chess.domain.board.ChessBoardCopier;
import chess.domain.board.Player;
import chess.domain.pieces.King;
import chess.domain.pieces.Pawn;
import chess.domain.pieces.Queen;
import chess.domain.pieces.Rook;
import chess.logic.chessboardinitializers.ChessBoardInitializer;
import static chess.logic.chessboardinitializers.ChessBoardInitializer.putPieceOnBoard;
import chess.logic.chessboardinitializers.EmptyBoardInitializer;
import chess.logic.chessboardinitializers.StandardBoardInitializer;
import chess.logic.movementlogic.MovementLogic;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sami
 */
public class CheckingLogicTest {

    private static CheckingLogic cl;
    private static GameSituation game;
    private static MovementLogic ml;
    private static EmptyBoardInitializer emptyinit;

    public CheckingLogicTest() {
        emptyinit = new EmptyBoardInitializer();
        ml = new MovementLogic();
        game = new GameSituation(emptyinit, ml);
        cl = new CheckingLogic(game);
    }

    @Before
    public void setUp() {
        game.reset();
        putPieceOnBoard(game.getChessBoard(), new King(0, 0, Player.WHITE, "wk"));
    }

    @Test
    public void checkIfCheckedReturnFalseIfKingIsNotChecked() {
        assertFalse(cl.checkIfChecked(Player.WHITE));
    }

    @Test
    public void checkIfCheckedReturnsTrueIfKingIsChecked() {
        putPieceOnBoard(game.getChessBoard(), new Queen(1, 1, Player.BLACK, "bq"));
        assertTrue(cl.checkIfChecked(Player.WHITE));
    }

    @Test
    public void checkMateIsFalseIfNotChecked() {
        assertFalse(cl.checkMate(Player.WHITE));
    }

    @Test
    public void checkMateTrueIfKingCheckedAndCheckCannotBePrevented() {
        putPieceOnBoard(game.getChessBoard(), new Queen(1, 1, Player.BLACK, "bq"));
        putPieceOnBoard(game.getChessBoard(), new King(2, 2, Player.BLACK, "bk"));
        assertTrue(cl.checkMate(Player.WHITE));
    }

    @Test
    public void checkMateFalseIfKingCheckedButCheckingPieceCanBeTaken() {
        putPieceOnBoard(game.getChessBoard(), new Queen(1, 1, Player.BLACK, "bq"));
        assertFalse(cl.checkMate(Player.WHITE));
    }

    @Test
    public void chessBoardIsNotAffectedByCheckingIfKingIsCheckMated() {
        putPieceOnBoard(game.getChessBoard(), new Queen(6, 6, Player.BLACK, "bq"));
        putPieceOnBoard(game.getChessBoard(), new Rook(1, 6, Player.BLACK, "br1"));
        putPieceOnBoard(game.getChessBoard(), new Rook(6, 1, Player.BLACK, "br2"));
        putPieceOnBoard(game.getChessBoard(), new Rook(4, 1, Player.WHITE, "wr"));
        ChessBoard copy = ChessBoardCopier.copy(game.getChessBoard());
        assertFalse(cl.checkMate(Player.WHITE));
        assertTrue(ChessBoardCopier.chessBoardsAreDeeplyEqual(copy, game.getChessBoard()));
    }

    @Test
    public void chessBoardIsNotAffectedByCheckingIfKingIsCheckMatedInComplexSituation() {
        ChessBoardInitializer stdinit = new StandardBoardInitializer();
        stdinit.initialize(game.getChessBoard());
        MovementLogic mvl = game.getChessBoard().getMovementLogic();

        mvl.move(game.getChessBoard().getSquare(4, 6).getPiece(), game.getChessBoard().getSquare(4, 5), game);
        mvl.move(game.getChessBoard().getSquare(5, 1).getPiece(), game.getChessBoard().getSquare(5, 2), game);
        mvl.move(game.getChessBoard().getSquare(5, 7).getPiece(), game.getChessBoard().getSquare(1, 3), game);
        mvl.move(game.getChessBoard().getSquare(3, 1).getPiece(), game.getChessBoard().getSquare(3, 2), game);
        mvl.move(game.getChessBoard().getSquare(3, 7).getPiece(), game.getChessBoard().getSquare(7, 3), game);
        mvl.move(game.getChessBoard().getSquare(5, 2).getPiece(), game.getChessBoard().getSquare(5, 3), game);
        mvl.move(game.getChessBoard().getSquare(7, 3).getPiece(), game.getChessBoard().getSquare(4, 0), game);
        game.getChessBoard().updateThreatenedSquares(Player.WHITE);
        ChessBoard backUp = ChessBoardCopier.copy(game.getChessBoard());
        assertTrue(cl.checkMate(Player.BLACK));

        assertTrue(ChessBoardCopier.chessBoardsAreDeeplyEqual(backUp, game.getChessBoard()));
    }

    @Test
    public void checkMateFalseIfCheckCanBeBlocked() {
        putPieceOnBoard(game.getChessBoard(), new Queen(6, 6, Player.BLACK, "bq"));
        putPieceOnBoard(game.getChessBoard(), new Rook(1, 6, Player.BLACK, "br1"));
        putPieceOnBoard(game.getChessBoard(), new Rook(6, 1, Player.BLACK, "br2"));
        putPieceOnBoard(game.getChessBoard(), new Rook(4, 1, Player.WHITE, "wr"));
        assertFalse(cl.checkMate(Player.WHITE));
    }

    @Test
    public void checkMateFalseIfKingCanMoveToUnthreatenedSquare() {
        putPieceOnBoard(game.getChessBoard(), new Pawn(1, 1, Player.BLACK, "bp1"));
        putPieceOnBoard(game.getChessBoard(), new Pawn(2, 2, Player.BLACK, "bp2"));
        assertFalse(cl.checkMate(Player.WHITE));
    }

    @Test
    public void checkMateFalseInComplexSituationWhereKingThreatenedByProtectedPieceButCanBeAvoided() {
        game = new GameSituation(new StandardBoardInitializer(), ml);
        King whiteKing = game.getChessBoard().getKings().get(Player.WHITE);
        Pawn whitePawn = (Pawn) game.getChessBoard().getSquare(5, 1).getPiece();
        Pawn blackPawn1 = (Pawn) game.getChessBoard().getSquare(5, 6).getPiece();
        Pawn blackPawn2 = (Pawn) game.getChessBoard().getSquare(4, 6).getPiece();

        ml.move(whitePawn, game.getChessBoard().getSquare(5, 2), game);
        ml.move(blackPawn1, game.getChessBoard().getSquare(5, 4), game);
        ml.move(whiteKing, game.getChessBoard().getSquare(5, 1), game);
        ml.move(blackPawn2, game.getChessBoard().getSquare(4, 4), game);
        ml.move(whiteKing, game.getChessBoard().getSquare(6, 2), game);
        ml.move(blackPawn1, game.getChessBoard().getSquare(5, 3), game);
        assertFalse(cl.checkMate(Player.WHITE));
        game = new GameSituation(emptyinit, ml);
    }

    @Test
    public void staleMateTrueIfKingNotCheckedAndThereIsNoLegalMoves() {
        putPieceOnBoard(game.getChessBoard(), new Rook(1, 7, Player.BLACK, "br1"));
        putPieceOnBoard(game.getChessBoard(), new Rook(7, 1, Player.BLACK, "br2"));
        game.getChessBoard().updateThreatenedSquares(Player.BLACK);
        assertTrue(cl.stalemate(Player.WHITE));
    }

    @Test
    public void staleMateFalseIfKingCanMoveLegally() {
        assertFalse(cl.stalemate(Player.WHITE));
    }

    @Test
    public void staleMateFalseIfThereIsSomeOtherPieceThatCanMoveLegally() {
        putPieceOnBoard(game.getChessBoard(), new Rook(1, 7, Player.BLACK, "br1"));
        putPieceOnBoard(game.getChessBoard(), new Rook(7, 1, Player.BLACK, "br2"));
        putPieceOnBoard(game.getChessBoard(), new Pawn(4, 4, Player.WHITE, "wp"));
        game.getChessBoard().updateThreatenedSquares(Player.BLACK);
        assertFalse(cl.stalemate(Player.WHITE));
    }
}
