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
import java.util.ArrayList;
import java.util.Random;

/**
 * This class is responsible for calculating AI's next move and then returning
 * it when asked for next move.
 *
 * @author sami
 */
public class AILogic {

    private ArrayList<Move> bestMoves;
    private int[] bestValues;
    private final int plies = 2;

    public AILogic() {
        bestValues = new int[plies];
        bestMoves = new ArrayList();
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
     * value that is best move after player's movement.
     *
     * @param depth Recursion depth in turns
     * @param situation Game situation before move.
     * @param maxingPlayer player whose best move is being figured out
     * @return highest value associated with any move.
     */
    private int tryAllPossibleMoves(int depth, GameSituation situation, Player maxingPlayer) {
        if (depth == plies) {
            return evaluateGameSituation(situation, maxingPlayer);
        }

        if (depth % 2 == 0) {
            if (situation.getCheckLogic().checkIfCheckedAndMated(maxingPlayer)) {
                return -123456789;
            } else if (situation.getCheckLogic().stalemate(maxingPlayer)) {
                return 0;
            }
            playerChoosesMoveWithHighestValue(situation, depth, maxingPlayer);
        } else {
            if (situation.getCheckLogic().checkIfCheckedAndMated(getOpponent(maxingPlayer))) {
                return 123456789;
            } else if (situation.getCheckLogic().stalemate(getOpponent(maxingPlayer))) {
                return 0;
            }
            opponentChoosesMoveToMinimizePlayersValue(situation, depth, maxingPlayer);
        }
        return bestValues[depth];
    }

    private void playerChoosesMoveWithHighestValue(GameSituation sit, int depth, Player player) {
        ChessBoard backUp = copy(sit.getChessBoard());
        MovementLogic ml = sit.getChessBoard().getMovementLogic();
        bestValues[depth] = -123456790;

        sit.getChessBoard().getPieces(player).stream()
                .filter(piece -> !piece.isTaken())
                .forEach(piece -> {
                    ml.possibleMoves(piece, sit.getChessBoard()).stream()
                            .forEach(possibility -> {
                                ml.move(piece, possibility, sit.getChessBoard());
                                if (!sit.getCheckLogic().checkIfChecked(player)) {
                                    int value = tryAllPossibleMoves(depth + 1, sit, player);
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
                                revertOldSituation(sit.getChessBoard(), backUp);
                                sit.setContinues(true);
                            });
                });
    }

    private void opponentChoosesMoveToMinimizePlayersValue(GameSituation situation, int depth, Player player) {
        ChessBoard backUp = copy(situation.getChessBoard());
        MovementLogic ml = situation.getChessBoard().getMovementLogic();
        bestValues[depth] = 123456790;

        situation.getChessBoard().getPieces(getOpponent(player)).stream()
                .filter(piece -> !piece.isTaken())
                .forEach(piece -> {
                    ml.possibleMoves(piece, situation.getChessBoard()).stream()
                            .forEach(possibility -> {
                                ml.move(piece, possibility, situation.getChessBoard());
                                if (!situation.getCheckLogic().checkIfChecked(getOpponent(player))) {
                                    int value = tryAllPossibleMoves(depth + 1, situation, player);
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
                                revertOldSituation(situation.getChessBoard(), backUp);
                                situation.setContinues(true);
                            });
                });
    }

    /**
     * This method is used to calculate best move for player whose turn it is
     * now in given game situation. @See tryAllPossibleMoves for exact method
     * used.
     *
     * @param situation game situation at the beginning of AI's turn.
     */
    public void findBestMove(GameSituation situation) {
        tryAllPossibleMoves(0, situation, situation.whoseTurn());
    }

}
