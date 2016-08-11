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

    private MyArrayList<Move> bestMoves;
    private int[] bestValues;
    private MovementLogic ml;
    private Player maxingPlayer;
    private final int plies = 3;

    public AILogic() {
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
     * This method is used to calculate best move for player in given game
     * situation using maximin-algorithm. Helper function
     * playerChoosesMoveWithHighestValue is called for when it's player's turn
     * and he'll choose move with highest associated value. On the other hand
     * helper function opponentChoosesMoveToMinimizePlayersValue is called when
     * it's opponents turn and opponent will choose move with lowest associated
     * value, that is best move after player's movement according to heuristic.
     *
     * @param depth Recursion depth in turns
     * @param situation Game situation before move.
     * @param maxingPlayer player whose best move is being figured out
     * @return highest value associated with any move.
     */
    private int tryAllPossibleMoves(int depth, GameSituation situation) {
        if (depth == plies) {
            if (depth % 2 == 0) {
                return evaluateGameSituation(situation, maxingPlayer, situation.whoseTurn());
            }
            return evaluateGameSituation(situation, maxingPlayer, getOpponent(situation.whoseTurn()));
        }

        if (depth % 2 == 0) {
            playerChoosesMoveWithHighestValue(situation, depth);
        } else {
            opponentChoosesMoveToMinimizePlayersValue(situation, depth);
        }
        return bestValues[depth];
    }

    private void playerChoosesMoveWithHighestValue(GameSituation sit, int depth) {
        ChessBoard backUp = copy(sit.getChessBoard());
        bestValues[depth] = -123456790;

        sit.getChessBoard().getPieces(maxingPlayer).stream()
                .filter(piece -> !piece.isTaken())
                .forEach(piece -> {
                    for (Square possibility : ml.possibleMoves(piece, sit.getChessBoard())) {
                        moveToTargetPositionAndCheckIfValueLargestSoFar(sit, depth, piece, possibility);
                        revertOldSituation(sit.getChessBoard(), backUp);
                        sit.setContinues(true);
                        if (depth > 0 && bestValues[depth] > bestValues[depth - 1]) {
                            return;
                        }
                    }
                });
    }

    private void moveToTargetPositionAndCheckIfValueLargestSoFar(GameSituation sit, int depth, Piece piece, Square possibility) {
        ml.move(piece, possibility, sit.getChessBoard());
        if (!sit.getCheckLogic().checkIfChecked(maxingPlayer)) {
            int value = tryAllPossibleMoves(depth + 1, sit);
            if (value >= bestValues[depth]) {
                if (depth == 0) {
                    if (value > bestValues[depth]) {
                        bestMoves.clear();
                    }
                    bestMoves.add(new Move(piece, possibility));
                }
                bestValues[depth] = value;
            }
        }
    }

    private void opponentChoosesMoveToMinimizePlayersValue(GameSituation sit, int depth) {
        ChessBoard backUp = copy(sit.getChessBoard());
        bestValues[depth] = 123456790;

        sit.getChessBoard().getPieces(getOpponent(maxingPlayer)).stream()
                .filter(piece -> !piece.isTaken())
                .forEach(piece -> {
                    for (Square possibility : ml.possibleMoves(piece, sit.getChessBoard())) {
                        moveToTargetPositionAndCheckIfValueSmallestSoFar(sit, backUp, depth, piece, possibility);
                        revertOldSituation(sit.getChessBoard(), backUp);
                        sit.setContinues(true);
                        if (bestValues[depth] < bestValues[depth - 1]) {
                            return;
                        }
                    }
                });
    }

    private void moveToTargetPositionAndCheckIfValueSmallestSoFar(GameSituation sit, ChessBoard backUp, int depth, Piece piece, Square possibility) {
        ml.move(piece, possibility, sit.getChessBoard());
        if (!sit.getCheckLogic().checkIfChecked(getOpponent(maxingPlayer))) {
            int value = tryAllPossibleMoves(depth + 1, sit);
            if (value <= bestValues[depth]) {
                if (depth == 0) {
                    if (value < bestValues[depth]) {
                        bestMoves.clear();
                    }
                    bestMoves.add(new Move(piece, possibility));
                }
                bestValues[depth] = value;
            }
        }
    }

    /**
     * This method is used to calculate best move for player whose turn it is
     * now in given game situation. @See tryAllPossibleMoves for exact method
     * used.
     *
     * @param situation game situation at the beginning of AI's turn.
     */
    public void findBestMove(GameSituation situation) {
        ml = situation.getChessBoard().getMovementLogic();
        maxingPlayer = situation.whoseTurn();
        tryAllPossibleMoves(0, situation);
    }

}
