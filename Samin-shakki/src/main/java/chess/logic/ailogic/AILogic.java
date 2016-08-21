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
        transpositionTable = new MyHashMap();
    }

    /**
     * Returns a random move with highest associated value.
     *
     * @return random best move
     */
    public Move getBestMove() {
        Random rnd = new Random();
        return bestMoves.get(rnd.nextInt(bestMoves.size()));
    }

    /**
     * This method is used to calculate best move for ai in given game situation
     * using negaMax sped up with alpha-beta pruning. For each depth
     * corresponding bestValue is initialized to -12456789 representing negative
     * infinity. If leaf isn't reached yet, method will recurse forward by
     * calling submethod makeMoveAndCheckValue for each piece player owns on
     * board.
     *
     * Alpha-beta pruning will cut branches that can't possibly give better
     * values than earlier. For example if highest value so far for node n is 10
     * and now we find that a child of n's child m has value 5 we don't have to
     * search through rest of m's children. Since m's value is at least -5 and
     * now this means function may at most return 5 to node n which is strictly
     * less than 10.
     *
     * @param depth Recursion depth left in turns
     * @param sit Game situation before move.
     * @param maxingPlayer player whose best move is being figured out
     * @return highest value associated with any move.
     */
    private int negaMax(int depth, int alpha, int beta, Player maxingPlayer) {
        TranspositionKey key = new TranspositionKey(
                depth, maxingPlayer, maxingPlayer, sit.getBoardHash());
        if (transpositionTable.containsKey(key)) {
            return transpositionTable.get(key);
        }

        if (depth == 0) {
            int value = evaluateGameSituation(sit, maxingPlayer);
            transpositionTable.put(key, value);
            transpositionTable.put(key.opposingKey(), -value);
            return value;
        }
        tryAllPossibleMovesForMaxingPlayer(depth, alpha, maxingPlayer, beta);

        return bestValues[depth];
    }

    private void tryAllPossibleMovesForMaxingPlayer(int depth, int alpha, Player maxingPlayer, int beta) {
        bestValues[depth] = -123456789;
        ChessBoard backUp = copy(sit.getChessBoard());
        alpha = testPrincipalMove(depth, maxingPlayer, alpha, beta, backUp);
        alpha = testKillerMoves(depth, maxingPlayer, alpha, beta, backUp);

        for (Piece piece : sit.getChessBoard().getPieces(maxingPlayer)) {
            if (piece.isTaken()) {
                continue;
            }
            makeaMoveAndCheckValue(backUp, piece, maxingPlayer, depth, alpha, beta);
        }
    }

    /**
     * After testing principal variation the next likely moves to be best are
     * killer moves so they are tested next. Killer moves are moves that caused
     * a (alpha-)beta-cutoff in a node at same depth and they are changed every
     * time new one is found.
     *
     * @param backUp backup of situation on chessboard before move.
     * @param depth recursion depth left counted in turns.
     * @param maxingPlayer player whose turn it is.
     * @param alpha current alpha value.
     * @param beta current beta value.
     * @return alpha value after testing killer moves.
     */
    private int testKillerMoves(int depth, Player maxingPlayer, int alpha, int beta, ChessBoard backUp) {
        for (int i = 0; i < 3; i++) {
            Move killer = killerMoves[maxDepth - depth][i];
            if (killer == null) {
                continue;
            }
            Piece piece = killer.getPiece();
            Square from = sit.getChessBoard().getSquare(piece.getColumn(), piece.getRow());
            Square to = killer.getTarget();

            if (piece.equals(from.getPiece())) {
                if (ml.possibleMoves(piece, sit.getChessBoard()).contains(to)) {
                    ml.move(piece, to, sit);
                    alpha = checkForChangeInBestOrAlphaValue(
                            maxingPlayer, depth, alpha, beta, piece, to);
                    undoMove(backUp, sit, from, to);
                    sit.setContinues(true);
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
     * @param depth recursion depth left in turns.
     * @param maxingPlayer player whose turn it is.
     * @param alpha current alpha value.
     * @param beta current beta value.
     * @return alpha value after testing principal move.
     */
    private int testPrincipalMove(int depth, Player maxingPlayer, int alpha, int beta, ChessBoard backUp) {
        if (principalMoves[maxDepth - depth] != null) {
            Piece piece = principalMoves[maxDepth - depth].getPiece();
            Square from = sit.getChessBoard().getSquare(piece.getColumn(), piece.getRow());
            Square to = principalMoves[maxDepth - depth].getTarget();

            if (piece.equals(from.getPiece())) {
                if (ml.possibleMoves(piece, sit.getChessBoard()).contains(to)) {
                    ml.move(piece, to, sit);
                    alpha = checkForChangeInBestOrAlphaValue(
                            maxingPlayer, depth, alpha, beta, piece, to);
                    undoMove(backUp, sit, from, to);
                    sit.setContinues(true);
                }
            }
        }

        return alpha;
    }

    /**
     * This method will first back up current situation on chessboard before
     * trying to move piece to each possible square recursing deeper into game
     * tree by calling negaMax. After returning to current node in recursion,
     * reverts back to situation before move was made. If alpha value is greater
     * than beta value, we can stop trying to find better values as no such will
     * be found.
     *
     * @param backUp backup of situation on chessboard before move.
     * @param piece piece to be moved.
     * @param maxingPlayer player who's maxing value of heuristic this turn.
     * @param depth depth in game tree.
     * @param alpha alpha value for current node in game tree.
     * @param beta beta value for current node in game tree.
     */
    private void makeaMoveAndCheckValue(ChessBoard backUp, Piece piece,
            Player maxingPlayer, int depth, int alpha, int beta) {

        Square from = sit.getChessBoard().getSquare(piece.getColumn(), piece.getRow());
        for (Square possibility : ml.possibleMoves(piece, sit.getChessBoard())) {
            if (moveHasBeenTestedAlready(depth, piece, possibility)) {
                continue;
            }
            ml.move(piece, possibility, sit);
            alpha = checkForChangeInBestOrAlphaValue(
                    maxingPlayer, depth, alpha, beta, piece, possibility);
            undoMove(backUp, sit, from, possibility);
            sit.setContinues(true);

            if (alpha >= beta) {
                if (killerCandidates[maxDepth - depth] != null) {
                    killerMoves[maxDepth - depth][oldestIndex] = killerCandidates[maxDepth - depth];
                    oldestIndex = (oldestIndex + 1) % 3;
                    killerCandidates[maxDepth - depth] = null;
                }
                break;
            }
            killerCandidates[maxDepth - depth] = new Move(piece, possibility);
        }
    }

    private boolean moveHasBeenTestedAlready(int depth, Piece piece, Square possibility) {
        Move tested = new Move(piece, possibility);
        for (int i = 0; i < 3; i++) {
            if (tested.equals(killerMoves[maxDepth - depth][i])) {
                return true;
            }
        }

        return tested.equals(principalMoves[maxDepth - depth]);
    }

    /**
     * Checks if movement was legal and then recurses forward by calling
     * negamax. Updates bestValue for depth and alpha value if necessary. In
     * negamax call alpha and beta are swapped and their signs are changed to
     * use formula max(a,b)=-min(-a,-b) thus preventing need of different max
     * and min methods. This is also why value is set to -negamax.
     *
     * @param maxingPlayer player who's maxing value of situation this turn.
     * @param depth depth in game tree.
     * @param alpha previous alpha value.
     * @param beta beta value.
     * @param piece piece that was moved.
     * @param possibility square that piece was moved to.
     * @return new alpha value.
     */
    private int checkForChangeInBestOrAlphaValue(
            Player maxingPlayer, int depth, int alpha, int beta, Piece piece, Square possibility) {
        if (sit.getCheckLogic().checkIfChecked(maxingPlayer)) {
            return alpha;
        }
        int value = -negaMax(depth - 1, -beta, -alpha, getOpponent(maxingPlayer));
        TranspositionKey key = new TranspositionKey(depth, maxingPlayer, maxingPlayer, value);
        transpositionTable.put(key, value);
        transpositionTable.put(key.opposingKey(), -value);
        if (value >= bestValues[depth]) {
            keepTrackOfBestMoves(depth, value, piece, possibility);
            bestValues[depth] = value;
        }
        if (value > alpha) {
            alpha = value;
            principalMoves[maxDepth - depth] = new Move(piece, possibility);
        }
        return alpha;
    }

    /**
     * Saves best first moves in an arraylist and if better move is found clears
     * the list of previous moves.
     *
     * @param depth depth in game tree.
     * @param value value of situation.
     * @param piece piece that was moved.
     * @param possibility square piece was moved to.
     */
    private void keepTrackOfBestMoves(int depth, int value, Piece piece, Square possibility) {
        if (depth == maxDepth) {
            if (value > bestValues[depth]) {
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
     * that - probably - have been made.
     */
    private void setPrinciplVariationAsMatchingPartOfLastComputation() {
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
