package chess.logic.movementlogic;

import chess.domain.board.ChessBoard;
import chess.domain.board.Player;
import chess.domain.board.Square;
import chess.logic.chessboardinitializers.ChessBoardInitializer;
import chess.logic.chessboardinitializers.EmptyBoardInitializer;
import chess.domain.pieces.*;
import java.util.Set;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sami
 */
public class MovementLogicTest {

    private static MovementLogic ml;
    private static ChessBoardInitializer emptyinit;
    private ChessBoard board;

    public MovementLogicTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        ml = new MovementLogic();
        emptyinit = new EmptyBoardInitializer();
    }

    @Before
    public void setUp() {
        board = new ChessBoard(ml);
    }

    @Test
    public void possibleMovesByPlayerContainsAllMovesPlayersPieceCanMake() {
        emptyinit.initialize(board);
        ChessBoardInitializer.putPieceOnBoard(board, new Queen(4, 3, Player.BLACK, "bq"));
        ChessBoardInitializer.putPieceOnBoard(board, new Rook(2, 2, Player.BLACK, "br"));
        ChessBoardInitializer.putPieceOnBoard(board, new Pawn(4, 1, Player.BLACK, "bp"));
        ChessBoardInitializer.putPieceOnBoard(board, new Bishop(7, 1, Player.BLACK, "bb"));
        ChessBoardInitializer.putPieceOnBoard(board, new King(4, 1, Player.BLACK, "bk"));
        ChessBoardInitializer.putPieceOnBoard(board, new Knight(7, 1, Player.BLACK, "bn"));
        Set<Square> possibleMoves = ml.possibleMovesByPlayer(Player.BLACK, board);
        assertTrue(board.getPieces(Player.BLACK).stream()
                .allMatch(bp -> ml.possibleMoves(bp, board).stream().allMatch(move -> possibleMoves.contains(move))));
    }
}
