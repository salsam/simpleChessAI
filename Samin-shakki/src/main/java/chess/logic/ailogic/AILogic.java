package chess.logic.ailogic;

import chess.domain.GameSituation;
import chess.domain.Move;
import chess.domain.board.ChessBoard;
import static chess.domain.board.ChessBoardCopier.copy;
import chess.domain.board.Player;
import static chess.domain.board.Player.getOpponent;
import static chess.logic.ailogic.GameSituationEvaluator.evaluateGameSituation;
import chess.logic.movementlogic.MovementLogic;
import static chess.domain.board.ChessBoardCopier.revertOldSituation;
import chess.domain.board.Square;
import chess.domain.datastructures.MyArrayList;
import chess.domain.pieces.Piece;
import java.util.Random;

/**
 * This class is responsible for calculating AI's next move and then returning
 * it when asked for next move. All values are measured in centipawns that is
 * one hundredth of pawn's value.
 *
 * @author sami
 */
public class AILogic {

    private GameSituation sit;
    private MyArrayList<Move> bestMoves;
    private int[] bestValues;
    private MovementLogic ml;
    private final int plies;

    public AILogic(int plies) {
        this.plies = plies;
        bestValues = new int[plies];
        bestMoves = new MyArrayList();
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
     * corresponding bestValue is initialized to -12456790 representing negative
     * infinity. If leaf isn't reached yet, method will recurse forward by
     * calling submethod makeMoveAndCheckValue for each piece player owns on
     * board.
     *
     * @param depth Recursion depth in turns
     * @param sit Game situation before move.
     * @param maxingPlayer player whose best move is being figured out
     * @return highest value associated with any move.
     */
    private int negaMax(int depth, int alpha, int beta, Player maxingPlayer) {
        if (depth == plies) {
            return evaluateGameSituation(sit, maxingPlayer);
        }

        bestValues[depth] = -123456790;

        for (Piece piece : sit.getChessBoard().getPieces(maxingPlayer)) {
            if (piece.isTaken()) {
                continue;
            }
            makeMoveAndCheckValue(piece, maxingPlayer, depth, alpha, beta);
        }

        return bestValues[depth];
    }

    /**
     * This method will first back up current situation on chessboard before
     * trying to move piece to each possible square recursing deeper into game
     * tree by calling nageMax. After returning to current node in recursion,
     * reverts back to situation before move was made. If alpha value is greater
     * than beta value, we can stop trying to find better values as no such will
     * be found. Uses cut off of 500 centipawns to make evaluation faster.
     *
     * @param piece piece to be moved.
     * @param maxingPlayer player who's maxing value of heuristic this turn.
     * @param depth depth in game tree.
     * @param alpha alpha value for current node in game tree.
     * @param beta beta value for current node in game tree.
     */
    private void makeMoveAndCheckValue(Piece piece, Player maxingPlayer, int depth, int alpha, int beta) {
        ChessBoard backUp = copy(sit.getChessBoard());
        for (Square possibility : ml.possibleMoves(piece, sit.getChessBoard())) {
            ml.move(piece, possibility, sit.getChessBoard());
            alpha = checkForChangeInBestOrAlphaValue(maxingPlayer, depth, alpha, beta, piece, possibility);
            revertOldSituation(sit.getChessBoard(), backUp);
            sit.setContinues(true);

            if (bestValues[0] > 500 || alpha >= beta) {
                break;
            }
        }
    }

    private int checkForChangeInBestOrAlphaValue(
            Player maxingPlayer, int depth, int alpha, int beta, Piece piece, Square possibility) {
        if (!sit.getCheckLogic().checkIfChecked(maxingPlayer)) {
            int value = -negaMax(depth + 1, -beta, -alpha, getOpponent(maxingPlayer));
            if (value >= bestValues[depth]) {
                keepTrackOfBestMoves(depth, value, piece, possibility);
                bestValues[depth] = value;
            }
            alpha = Math.max(alpha, value);
        }
        return alpha;
    }

    private void keepTrackOfBestMoves(int depth, int value, Piece piece, Square possibility) {
        if (depth == 0) {
            if (value > bestValues[depth]) {
                bestMoves.clear();
            }
            bestMoves.add(new Move(piece, possibility));
        }
    }

    /**
     * This method is used to calculate best move for player whose turn it is
     * now in given game situation. @See tryAllPossibleMoves for exact method
     * used.
     *
     * @param situation game situation at the beginning of AI's turn.
     */
    public void findBestMoves(GameSituation situation) {
        ml = situation.getChessBoard().getMovementLogic();
        sit = situation;
        negaMax(0, -123456790, 123456790, situation.whoseTurn());
    }

}
