package chess.domain;

import chess.logic.movementlogic.MovementLogic;
import chess.domain.board.ChessBoard;
import chess.logic.chessboardinitializers.ChessBoardInitializer;
import chess.domain.board.Player;
import chess.domain.board.Square;
import chess.domain.datastructures.MyHashMap;
import chess.domain.pieces.Pawn;
import chess.logic.ailogic.ZobristHasher;
import chess.logic.gamelogic.CheckingLogic;
import chess.logic.gamelogic.LegalityChecker;
import java.util.Map;

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

    /**
     * Used to count how many times given situation has occurred on board.
     */
    private Map<Long, Integer> chessBoardSituationCounter;

    /**
     * Used to hash chessboard situations.
     */
    private ZobristHasher hasher;

    /**
     * Hash of current board situation.
     */
    private long boardHash;

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
        chessBoardSituationCounter = new MyHashMap();
        continues = true;
        hasher = new ZobristHasher();
        boardHash = hasher.hash(board);
        ais = new boolean[2];
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

    public boolean[] getAis() {
        return this.ais;
    }

    public ZobristHasher getHasher() {
        return hasher;
    }

    public int getTurn() {
        return this.turn;
    }

    public void setBlackAI(boolean isAi) {
        this.ais[0] = isAi;
    }

    public void setWhiteAI(boolean isAi) {
        this.ais[1] = isAi;
    }

    public long getBoardHash() {
        return boardHash;
    }

    public int getCountOfCurrentSituation() {
        if (!chessBoardSituationCounter.containsKey(boardHash)) {
            return 0;
        }
        return chessBoardSituationCounter.get(boardHash);
    }

    /**
     * Decreases the amount of times current board situation has been met by 1.
     */
    public void decrementCountOfCurrentBoardSituation() {
        if (!chessBoardSituationCounter.containsKey(boardHash)) {
            return;
        }
        chessBoardSituationCounter.put(boardHash,
                chessBoardSituationCounter.get(boardHash) - 1);
    }

    /**
     * Increases the amount of times current board situation has been met by 1.
     */
    public void incrementCountOfCurrentBoardSituation() {
        if (!chessBoardSituationCounter.containsKey(boardHash)) {
            chessBoardSituationCounter.put(boardHash, 0);
        }
        chessBoardSituationCounter.put(boardHash,
                chessBoardSituationCounter.get(boardHash) + 1);
    }

    public void reHashBoard(boolean increment) {
        long oldHash = boardHash;
        boardHash = hasher.hash(board);
        if (increment && oldHash != boardHash) {
            incrementCountOfCurrentBoardSituation();
        }
    }

    public void updateHashForMoving(Square from, Square to) {
        boardHash = hasher.getHashAfterMove(boardHash, board, from, to);
    }

    public void updateHashForTakingPiece(Square location) {
        boardHash = hasher.getHashAfterPieceIsTaken(boardHash, board, location);
    }

    public void updateHashForUndoingMove(ChessBoard backup, Square from, Square to) {
        boardHash = hasher.getHashBeforeMove(boardHash, board, backup, from, to);
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
        continues = true;
        init.initialize(board);
        chessBoardSituationCounter.clear();
        reHashBoard(true);
        turn = 1;
    }

}
