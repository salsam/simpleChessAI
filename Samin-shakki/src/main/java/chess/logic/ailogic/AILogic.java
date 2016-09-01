package chess.logic.ailogic;

import chess.domain.GameSituation;
import chess.domain.Move;
import chess.domain.board.ChessBoard;
import static chess.domain.board.ChessBoardCopier.copy;
import static chess.domain.board.ChessBoardCopier.undoMove;
import chess.domain.board.Player;
import static chess.domain.board.Player.getOpponent;
import static chess.logic.ailogic.GameSituationEvaluator.evaluateGameSituation;
import chess.logic.movementlogic.MovementLogic;
import chess.domain.board.Square;
import chess.domain.datastructures.MyArrayList;
import chess.domain.datastructures.MyHashMap;
import chess.domain.datastructures.Pair;
import chess.domain.datastructures.TranspositionKey;
import chess.domain.pieces.Piece;
import java.util.Map;
import java.util.Random;

/**
 * This class is responsible for calculating AI's next move and then returning
 * it when asked for next move. All values are measured in centipawns that is
 * one hundredth of pawn's value. Uses negamax sped up with alpha-beta pruning
 * and transposition tables. Alpha-beta pruning also is sped up by principal
 * variation heuristic simplified for depth of 3.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Negamax">Negamax</a>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Alpha-beta_pruning">Alpha-beta
 * pruning</a>
 *
 * @see
 * <a href="https://chessprogramming.wikispaces.com/Principal+variation">Principal
 * variation</a>
 *
 * @see
 * <a href="https://en.wikipedia.org/wiki/Transposition_table">Transposition
 * table</a>
 *
 * @author sami
 */
public class AILogic {

    private GameSituation sit;
    private MovementLogic ml;
    private MyArrayList<Move> bestMoves;
    private int[] bestValues;
    private final int plies;
    private int maxDepth;
    private int oldestIndex;
    private Random random;
    private Pair<Integer, Move[]> lastPrincipalVariation;
    private Move[] principalMoves;
    private Move[] killerCandidates;
    private Move[][] killerMoves;
    private Map<TranspositionKey, Integer> transpositionTable;
    private long sum = 0;
    private int count = 0;

    public AILogic(int plies) {
        this.plies = plies;
        bestValues = new int[plies + 1];
        bestMoves = new MyArrayList();
        killerCandidates = new Move[plies];
        killerMoves = new Move[plies][3];
        oldestIndex = 0;
        principalMoves = new Move[plies];
        random = new Random();
        transpositionTable = new MyHashMap();
    }

    public int[] getBestValues() {
        return bestValues;
    }

    public Move[] getKillerCandidates() {
        return killerCandidates;
    }

    public void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }

    public Map<TranspositionKey, Integer> getTranspositionTable() {
        return transpositionTable;
    }

    public void setSituation(GameSituation sit) {
        this.sit = sit;
        this.ml = sit.getChessBoard().getMovementLogic();
    }

    public void setStart(long start) {
        this.start = start;
    }

    /**
     * Sets new time limit for ai. This will be how long ai can think of next
     * move (measured in milliseconds).
     *
     * @param newTimeLimit new time limit for chosen ai.
     */
    public void setTimeLimit(long newTimeLimit) {
        this.timeLimit = newTimeLimit;
    }

    /**
     * Returns a random move with highest associated value.
     *
     * @return random best move
     */
    public Move getBestMove() {
        return bestMoves.get(random.nextInt(bestMoves.size()));
    }

    /**
     * This method is used to calculate best move for ai in given game situation
     * using negaMax sped up with alpha-beta pruning. For each depth
     * corresponding bestValue is initialized to -12456789 representing negative
     * infinity. If leaf isn't reached yet, method will recurse forward by
     * calling submethod tryAllPossibleMoves.
     *
     * @param height Height from leaf nodes.
     * @param alpha current alpha value.
     * @param maxingPlayer player whose best move is being figured out
     * @param beta current beta value.
     * @return highest value associated with any move.
     */
    private int negaMax(int depth, int alpha, int beta, Player maxingPlayer) {
        TranspositionKey key = new TranspositionKey(
                height, maxingPlayer, sit.getBoardHash());
        if (transpositionTable.containsKey(key)) {
            return transpositionTable.get(key);
        } else if (transpositionTable.containsKey(key.opposingKey())) {
            return -transpositionTable.get(key.opposingKey());
        }

        if (height == 0) {
            int value = evaluateGameSituation(sit, maxingPlayer);
            transpositionTable.put(key, value);
            return value;
        }
        tryAllPossibleMoves(height, alpha, maxingPlayer, beta);

        return bestValues[height];
    }

    private void tryAllPossibleMovesForMaxingPlayer(int depth, int alpha, Player maxingPlayer, int beta) {
        bestValues[depth] = -123456789;
        ChessBoard backUp = copy(sit.getChessBoard());
        alpha = testPrincipalMove(height, maxingPlayer, alpha, beta, backUp);
        alpha = testKillerMoves(height, maxingPlayer, alpha, beta, backUp);

        for (Piece piece : sit.getChessBoard().getPieces(maxingPlayer)) {
            if (piece.isTaken()) {
                continue;
            }

            Square from = sit.getChessBoard().getSquare(piece.getColumn(), piece.getRow());
            for (Square possibility : ml.possibleMoves(piece, sit.getChessBoard())) {
                if (moveHasBeenTestedAlready(depth, piece, possibility)) {
                    continue;
                }
                alpha = makeAMoveAndCheckForChangeInAlphaValue(piece, possibility, alpha, maxingPlayer, depth, beta, backUp, from);

                if (alpha >= beta) {
                    saveNewKillerMove(depth);
                    break;
                }
                killerCandidates[maxDepth - depth] = new Move(piece, possibility);
            }
            killerCandidates[searchDepth - height] = new Move(piece, possibility);
        }
        return alpha;
    }

    private int makeAMoveAndCheckForChangeInAlphaValue(Piece piece, Square possibility,
            int alpha, Player maxingPlayer, int depth, int beta, ChessBoard backUp, Square from) {
        ml.move(piece, possibility, sit);
        if (!possibility.containsAPiece()
                || !backUp.getSquare(from.getColumn(), from.getRow()).containsAPiece()) {
            System.out.println("square that was moved to is empty! seachDepth:"
                    + searchDepth + " height:" + height);
            System.out.println("backUp: " + backUp.getSquare(from.getColumn(),
                    from.getRow()).getPiece() + " at square: ("
                    + from.getColumn() + "," + from.getRow() + ")");
            System.out.println("backUp: " + backUp.getSquare(possibility.getColumn(),
                    possibility.getRow()).getPiece() + " at square: ("
                    + possibility.getColumn() + "," + possibility.getRow() + ")");
            backUp.printTable();
            System.out.println("");
            sit.getChessBoard().printTable();
            if (piece.isTaken()) {
                System.out.println("piece is taken!" + piece + " at square ("
                        + piece.getColumn() + "," + piece.getRow() + ")");
            }
        }
        alpha = checkForChange(
                maxingPlayer, height, alpha, beta, piece, possibility);
        undoMove(backUp, sit, from, piece);
        sit.setContinues(true);
        return alpha;
    }

    /**
     * After testing principal variation the next likely moves to be best are
     * killer moves so they are tested next. Killer moves are moves that caused
     * a (alpha-)beta-cutoff in a node at same depth and they are changed every
     * time new one is found.
     *
     * @param backUp backup of situation on chessboard before move.
     * @param height recursion depth left counted in turns.
     * @param maxingPlayer player whose turn it is.
     * @param alpha current alpha value.
     * @param beta current beta value.
     * @return alpha value after testing killer moves.
     */
    private int testKillerMoves(int height, Player maxingPlayer, int alpha, int beta, ChessBoard backUp) {
        for (int i = 0; i < 3; i++) {
            Move killer = killerMoves[searchDepth - height][i];
            if (killer == null) {
                continue;
            }
            Piece piece = killer.getPiece();
            Square from = sit.getChessBoard().getSquare(piece.getColumn(), piece.getRow());
            Square to = killer.getTarget();

            if (piece.equals(from.getPiece())) {
                if (ml.possibleMoves(piece, sit.getChessBoard()).contains(to)) {
                    alpha = testAMove(piece, to, alpha, maxingPlayer, height, beta, backUp, from);
                }
            }
        }

        return alpha;
    }

    /**
     * Tries making principal move first. Principal move is assumed to be best
     * move because of earlier iteration thus testing it first should in most
     * cases increase alpha value asap causing alpha-beta pruning to cut more
     * branches.
     *
     * @param backUp backup of situation on chessboard before move.
     * @param height recursion depth left in turns.
     * @param maxingPlayer player whose turn it is.
     * @param alpha current alpha value.
     * @param beta current beta value.
     * @return alpha value after testing principal move.
     */
    private int testPrincipalMove(int height, Player maxingPlayer, int alpha, int beta, ChessBoard backUp) {
        if (principalMoves[searchDepth - height] != null) {
            Piece piece = principalMoves[searchDepth - height].getPiece();
            Square from = sit.getChessBoard().getSquare(piece.getColumn(), piece.getRow());
            Square to = principalMoves[searchDepth - height].getTarget();

            if (piece.equals(from.getPiece())) {
                if (ml.possibleMoves(piece, sit.getChessBoard()).contains(to)) {
                    alpha = testAMove(piece, to, alpha, maxingPlayer, height, beta, backUp, from);
                }
            }
        }

        return alpha;
    }

    private void saveNewKillerMove(int depth) {
        if (killerCandidates[maxDepth - depth] != null
                && !moveHasBeenTestedAlready(depth, killerCandidates[maxDepth - depth].getPiece(),
                        killerCandidates[maxDepth - depth].getTarget())) {
            killerMoves[maxDepth - depth][oldestIndex] = killerCandidates[maxDepth - depth];
            oldestIndex = (oldestIndex + 1) % 3;
            killerCandidates[searchDepth - height] = null;
        }
    }

    private boolean moveHasBeenTestedAlready(int depth, Piece piece, Square possibility) {
        Move tested = new Move(piece, possibility);
        for (int i = 0; i < 3; i++) {
            if (tested.equals(killerMoves[searchDepth - height][i])) {
                return true;
            }
        }

        return tested.equals(principalMoves[searchDepth - height]);
    }

    /**
     * Checks if movement was legal and then recurses forward by calling
     * negamax. Updates bestValue for depth and alpha value if necessary. In
     * negamax call alpha and beta are swapped and their signs are changed to
     * use formula max(a,b)=-min(-a,-b) thus preventing need of separate max and
     * min methods. This is also why value is set to -negamax. Saves current
     * board situation with given iteration depth left in transposition table
     * for future use.
     *
     * @param maxingPlayer player who's maxing value of situation this turn.
     * @param height height in game tree.
     * @param alpha previous alpha value.
     * @param beta beta value.
     * @param piece piece that was moved.
     * @param possibility square that piece was moved to.
     * @return new alpha value.
     */
    public int checkForChange(Player maxingPlayer, int height,
            int alpha, int beta, Piece piece, Square possibility) {

        if (sit.getCheckLogic().checkIfChecked(maxingPlayer)) {
            return alpha;
        }
        int value = -negaMax(height - 1, -beta, -alpha, getOpponent(maxingPlayer));
        TranspositionKey key = new TranspositionKey(height, maxingPlayer, sit.getBoardHash());
        transpositionTable.put(key, value);

        if (value >= bestValues[height]) {
            keepTrackOfBestMoves(height, value, piece, possibility);
            bestValues[height] = value;
        }
        if (value > alpha) {
            alpha = value;
            principalMoves[searchDepth - height] = new Move(piece, possibility);
        }
        return alpha;
    }

    /**
     * Saves best first moves in an arraylist and if better move is found clears
     * the list of previous moves.
     *
     * @param height depth in game tree.
     * @param value value of situation.
     * @param piece piece that was moved.
     * @param possibility square piece was moved to.
     */
    private void keepTrackOfBestMoves(int height, int value, Piece piece, Square possibility) {
        if (height == searchDepth) {
            if (value > bestValues[height]) {
                bestMoves.clear();
            }
            bestMoves.add(new Move(piece, possibility));
        }
    }

    /**
     * This method is used to calculate best move for player whose turn it is
     * now in given game situation. Uses iterative deepening to speed up
     * alpha-beta thus looping over search depths from 0 to wanted depth. If
     * value of best move so far has greater absolute value than 20000, we know
     * that either player will inevitably win the game in i moves and thus
     * there's no need to loop further.
     *
     * @param situation game situation at the beginning of AI's turn.
     */
    public void findBestMoves(GameSituation situation) {
        long start = System.currentTimeMillis();
        sit = situation;
        ml = sit.getChessBoard().getMovementLogic();
        setPrinciplVariationAsMatchingPartOfLastComputation();
        for (int i = 0; i <= plies; i++) {
            maxDepth = i;
            negaMax(i, -123456789, 123456789, situation.whoseTurn());
            if (Math.abs(bestValues[i]) > 20000) {
                break;
            }

        }
        lastPrincipalVariation = new Pair(sit.getTurn(), principalMoves);
        long used = System.currentTimeMillis() - start;
        sum += used;
        count++;
        System.out.println("used: " + used + ", avg: " + sum / count + ", count: " + count);
    }

    /**
     * Sets matching part of last principal variation as start for current one.
     * So current principal variation will be moves in last one minus the moves
     * that - probably - have been made. If turnsSince is negative (we've reset
     * game board), all killer moves and principal moves are cleared to avoid
     * wrong information.
     */
    private void salvageLastPrincipalVariation() {
        if (lastPrincipalVariation != null) {
            int turnsSince = sit.getTurn() - lastPrincipalVariation.getFirst();
            for (int i = 0; i < plies; i++) {
                if (i < plies - turnsSince) {
                    principalMoves[i] = lastPrincipalVariation.getSecond()[i + turnsSince];
                    for (int j = 0; j < 3; j++) {
                        killerMoves[i][j] = killerMoves[i + turnsSince][j];
                    }
                } else {
                    for (int j = 0; j < 3; j++) {
                        killerMoves[i][j] = null;
                    }
                    principalMoves[i] = null;
                }
            }
        }
    }

}
