package chess.domain;

import chess.logic.movementlogic.MovementLogic;
import chess.domain.board.ChessBoard;
import chess.logic.chessboardinitializers.ChessBoardInitializer;
import chess.domain.board.Player;
import chess.domain.pieces.Pawn;
import chess.logic.ailogic.AILogic;
import chess.logic.gamelogic.CheckingLogic;
import chess.logic.gamelogic.LegalityChecker;

/**
 * This class is responsible for keeping track of current game situation. Class
 * offers methods to check if game has ended and whether or not one player is
 * checked. Class also offers methods to keep track of current turn and start
 * next turn.
 *
 * @author sami
 */
public class GameSituation {

    /**
     * ChessBoard of this GameSituation.
     */
    private ChessBoard board;
    /**
     * initializer that is used to initialize board when starting a new game.
     */
    private ChessBoardInitializer init;
    /**
     * Turn is a number that is used to keep track of what turn number is now.
     */
    private int turn;
    /**
     * legalityChecker is used to check if certain movements are legal on board.
     */
    private LegalityChecker legalityChecker;
    /**
     * checkingLogic is CheckingLogic used to determine whether or not a player
     * is checked.
     */
    private CheckingLogic checkLogic;
    /**
     * continues tells if this game has ended or not.
     */
    private boolean continues;

    /**
     * Keeps track of which players are ais. ais[0] is black and ais[1] is
     * white. If ais[0]=1, then black is ai etc.
     */
    private boolean[] ais;

    private AILogic ai;

    /**
     * Creates a new game with given movement logic and chessboard initializer.
     *
     * @param init chessboard initializer to be used for this game
     * @param movementLogic movement logic to be used for this game
     */
    public GameSituation(ChessBoardInitializer init, MovementLogic movementLogic) {
        this.board = new ChessBoard(movementLogic);
        this.init = init;
        this.init.initialize(board);
        turn = 1;
        legalityChecker = new LegalityChecker(board);
        checkLogic = new CheckingLogic(this);
        continues = true;
        ais = new boolean[2];
        ai = new AILogic();
    }

    /**
     * Returns player whose turn is now.
     *
     * @return white if it is whites turn else black.
     */
    public Player whoseTurn() {
        if (turn % 2 == 1) {
            return Player.WHITE;
        } else {
            return Player.BLACK;
        }
    }

    public boolean getContinues() {
        return continues;
    }

    public void setContinues(boolean continues) {
        this.continues = continues;
    }

    public LegalityChecker getChecker() {
        return legalityChecker;
    }

    public ChessBoard getChessBoard() {
        return this.board;
    }

    public CheckingLogic getCheckLogic() {
        return checkLogic;
    }

    public boolean getBlackAi() {
        return this.ais[0];
    }

    public void setBlackAI(boolean isAi) {
        this.ais[0] = isAi;
    }

    public boolean getWhiteAi() {
        return this.ais[1];
    }

    public void setWhiteAI(boolean isAi) {
        this.ais[1] = isAi;
    }

    public void setCheckLogic(CheckingLogic checkLogic) {
        this.checkLogic = checkLogic;
    }

    /**
     * Sets the given chessBoard in the field board and updates LegalityChecker
     * to check that board instead of old board.
     *
     * @param chessBoard new chessboard
     */
    public void setChessBoard(ChessBoard chessBoard) {
        this.board = chessBoard;
        this.legalityChecker.setBoard(chessBoard);
    }

    /**
     * Updates the squares that current player threatens and adds 1 to turn
     * counter in field turn. Thus changing the player whose turn is now. After
     * turn has changed also makes next player's pawns no longer possible to
     * capture en passant as one turn has passed.
     */
    public void nextTurn() {
        board.updateThreatenedSquares(whoseTurn());
        turn++;
        makePawnsUnEnPassantable(whoseTurn());
        if (ais[turn % 2]) {
            ai.findBestMove(this);
            Move move = ai.getBestMove();
            board.getMovementLogic().move(move.getPiece(), move.getTarget(), board);
            nextTurn();
        }
    }

    /**
     * Changes the field movedTwoSquaresLastTurn to false for every pawn player
     * owns thus making them no longer possible to be captured en passant.
     *
     * @param player player
     */
    public void makePawnsUnEnPassantable(Player player) {
        board.getPieces(player).stream().forEach(piece -> {
            if (piece.getClass() == Pawn.class) {
                Pawn pawn = (Pawn) piece;
                pawn.setMovedTwoSquaresLastTurn(false);
            }
        });
    }

    /**
     * Resets the game situation to beginning of the game.
     */
    public void reset() {
        init.initialize(board);
        turn = 1;
        continues = true;
    }

}
