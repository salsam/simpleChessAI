package chess.logic.inputprocessing;

import chess.domain.board.Player;
import chess.domain.board.Square;
import chess.logic.chessboardinitializers.ChessBoardInitializer;
import chess.logic.chessboardinitializers.EmptyBoardInitializer;
import chess.logic.chessboardinitializers.StandardBoardInitializer;
import chess.domain.GameSituation;
import chess.logic.movementlogic.MovementLogic;
import chess.domain.pieces.King;
import chess.domain.pieces.Pawn;
import chess.domain.pieces.Piece;
import chess.domain.pieces.Queen;
import chess.domain.pieces.Rook;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JLabel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sami
 */
public class InputProcessorTest {

    private InputProcessor inputProcessor;
    private static JLabel output;
    private static GameSituation game;
    private static ChessBoardInitializer init;

    public InputProcessorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        init = new StandardBoardInitializer();
        game = new GameSituation(init, new MovementLogic());
        output = new JLabel("");
    }

    @Before
    public void setUp() {
        game.reset();
        inputProcessor = new InputProcessor();
        inputProcessor.setTextArea(output);
        output.setText("");
    }

    @Test
    public void ifNoPieceIsSelectedChosenAndPossibilitiesAreNull() {
        assertEquals(null, inputProcessor.getPossibilities());
        assertEquals(null, inputProcessor.getChosen());
    }

    @Test
    public void ifNoPieceIsChosenSelectPieceOnTargetSquare() {
        inputProcessor.processClick(1, 1, game);
        assertEquals(game.getChessBoard().getSquare(1, 1).getPiece(), inputProcessor.getChosen());
    }

    @Test
    public void ifNoPieceIsSelectedAndTargetSquareContainsNoPieceIrOpposingPieceNoPieceIsSelected() {
        inputProcessor.processClick(4, 4, game);
        assertEquals(null, inputProcessor.getChosen());
        inputProcessor.processClick(6, 6, game);
        assertEquals(null, inputProcessor.getChosen());
    }

    @Test
    public void whenPieceIsSelectedItsPossibleMovesAreSavedInFieldPossibilities() {
        inputProcessor.processClick(1, 1, game);
        assertTrue(inputProcessor.getPossibilities().contains(new Square(1, 2)));
        assertTrue(inputProcessor.getPossibilities().contains(new Square(1, 3)));
    }

    @Test
    public void ifAnotherPieceOwnedPieceIsClickedItIsSetAsChosen() {
        inputProcessor.processClick(1, 1, game);
        inputProcessor.processClick(1, 0, game);

        assertTrue(inputProcessor.getPossibilities().contains(new Square(0, 2)));
        assertTrue(inputProcessor.getPossibilities().contains(new Square(2, 2)));
        assertFalse(inputProcessor.getPossibilities().contains(new Square(1, 2)));
        assertFalse(inputProcessor.getPossibilities().contains(new Square(1, 3)));
    }

    @Test
    public void ifPieceIsChosenAndThenAPossibilityIsClickedMoveToThatSquare() {
        inputProcessor.processClick(1, 1, game);
        Piece piece = game.getChessBoard().getSquare(1, 1).getPiece();
        inputProcessor.processClick(1, 2, game);
        assertFalse(game.getChessBoard().getSquare(1, 1).containsAPiece());
        assertTrue(game.getChessBoard().getSquare(1, 2).containsAPiece());
        assertEquals(piece, game.getChessBoard().getSquare(1, 2).getPiece());
    }

    @Test
    public void ifPawnIsMovedToOpposingEndOfBoardItIsPromotedToQueenOnSameSquare() {
        ChessBoardInitializer.putPieceOnBoard(game.getChessBoard(), new Pawn(1, 6, Player.WHITE, "wp9"));
        inputProcessor.processClick(1, 6, game);
        inputProcessor.processClick(0, 7, game);

        assertTrue(game.getChessBoard().getSquare(0, 7).containsAPiece());
        assertTrue(game.getChessBoard().getSquare(0, 7).getPiece().getClass() == Queen.class);
        Queen q = (Queen) game.getChessBoard().getSquare(0, 7).getPiece();
        assertEquals(Player.WHITE, q.getOwner());
        assertEquals("wq9", q.getPieceCode());
    }

    @Test
    public void ifPawnIsMovedToOpposingEndOfBoardItIsReplacedByQueen() {
        ChessBoardInitializer.putPieceOnBoard(game.getChessBoard(), new Pawn(1, 6, Player.WHITE, "wp9"));
        inputProcessor.processClick(1, 6, game);
        inputProcessor.processClick(0, 7, game);
        assertTrue(game.getChessBoard().getPieces(Player.WHITE).stream()
                .noneMatch(whitePiece -> whitePiece.getPieceCode().equals("wp9")));

        assertTrue(game.getChessBoard().getPieces(Player.WHITE).stream()
                .anyMatch(whitePiece -> whitePiece.getPieceCode().equals("wq9")));
    }

    @Test
    public void outputTellsWhoseTurnItIsCorrectly() {
        inputProcessor.processClick(1, 1, game);
        inputProcessor.processClick(1, 2, game);
        assertEquals("BLACK's turn.", output.getText());
        inputProcessor.processClick(1, 6, game);
        inputProcessor.processClick(1, 5, game);
        assertEquals("WHITE's turn.", output.getText());
    }

    @Test
    public void outputTellsIfPlayerIsChecked() {
        EmptyBoardInitializer emptyinit = new EmptyBoardInitializer();
        emptyinit.initialize(game.getChessBoard());
        ChessBoardInitializer.putPieceOnBoard(game.getChessBoard(), new King(4, 0, Player.WHITE, "wk"));
        ChessBoardInitializer.putPieceOnBoard(game.getChessBoard(), new Pawn(4, 2, Player.BLACK, "bp1"));
        inputProcessor.processClick(4, 0, game);
        inputProcessor.processClick(5, 0, game);
        inputProcessor.processClick(4, 2, game);
        inputProcessor.processClick(4, 1, game);

        assertEquals("WHITE's turn. Check!", output.getText());
    }

    @Test
    public void outputTellsIfGameHasEndedInCheckMate() {
        Map<String, JFrame> frames = new HashMap<>();
        frames.put("endingScreen", new JFrame());
        frames.get("endingScreen").setVisible(false);
        inputProcessor.setFrames(frames);
        EmptyBoardInitializer emptyinit = new EmptyBoardInitializer();
        emptyinit.initialize(game.getChessBoard());
        ChessBoardInitializer.putPieceOnBoard(game.getChessBoard(), new King(1, 0, Player.WHITE, "wk"));
        ChessBoardInitializer.putPieceOnBoard(game.getChessBoard(), new Queen(4, 1, Player.BLACK, "bq"));
        ChessBoardInitializer.putPieceOnBoard(game.getChessBoard(), new Rook(7, 1, Player.BLACK, "br1"));
        inputProcessor.processClick(1, 0, game);
        inputProcessor.processClick(0, 0, game);
        inputProcessor.processClick(4, 1, game);
        inputProcessor.processClick(1, 1, game);

        assertEquals("Checkmate! BLACK won!", output.getText());
    }

    @Test
    public void outputTellsIfGameHasEndedInStaleMate() {
        Map<String, JFrame> frames = new HashMap<>();
        frames.put("endingScreen", new JFrame());
        frames.get("endingScreen").setVisible(false);
        inputProcessor.setFrames(frames);
        EmptyBoardInitializer emptyinit = new EmptyBoardInitializer();
        emptyinit.initialize(game.getChessBoard());
        ChessBoardInitializer.putPieceOnBoard(game.getChessBoard(), new King(1, 0, Player.WHITE, "wk"));
        ChessBoardInitializer.putPieceOnBoard(game.getChessBoard(), new Queen(1, 7, Player.BLACK, "bq"));
        ChessBoardInitializer.putPieceOnBoard(game.getChessBoard(), new Rook(7, 1, Player.BLACK, "br1"));
        inputProcessor.processClick(1, 0, game);
        inputProcessor.processClick(0, 0, game);
        inputProcessor.processClick(1, 7, game);
        inputProcessor.processClick(1, 6, game);

        assertEquals("Stalemate! Game ended as a draw!", output.getText());
    }
}
