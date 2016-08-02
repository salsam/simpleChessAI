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

    public Move getBestMove() {
        Random rnd = new Random();
        return bestMoves.get(rnd.nextInt(bestMoves.size()));
    }

    public int tryAllPossibleMoves(int depth, GameSituation situation, Player maxingPlayer) {
        if (depth == plies) {
            return evaluateGameSituation(situation, maxingPlayer);
        }

        if (depth % 2 == 0) {
            if (situation.getCheckLogic().checkIfCheckedAndMated(maxingPlayer)) {
                return -123456789;
            }
            playerChoosesMoveWithHighestValue(situation, depth, maxingPlayer);
        } else {
            if (situation.getCheckLogic().checkIfCheckedAndMated(getOpponent(maxingPlayer))) {
                return 123456789;
            }
            opponentChoosesMoveToMinimizePlayersValue(situation, depth, maxingPlayer);
        }
        return bestValues[depth];
    }

    private void playerChoosesMoveWithHighestValue(GameSituation situation, int depth, Player player) {
        ChessBoard backUp = copy(situation.getChessBoard());
        MovementLogic ml = situation.getChessBoard().getMovementLogic();
        bestValues[depth] = -123456790;

        situation.getChessBoard().getPieces(player).stream()
                .filter(piece -> !piece.isTaken())
                .forEach(piece -> {
                    ml.possibleMoves(piece, situation.getChessBoard()).stream()
                            .forEach(possibility -> {
                                ml.move(piece, possibility, situation.getChessBoard());
                                if (!situation.getCheckLogic().checkIfChecked(player)) {
                                    int value = tryAllPossibleMoves(depth + 1, situation, player);
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
                                revertOldSituation(situation.getChessBoard(), backUp);
                                situation.setContinues(true);
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

    public void findBestMove(GameSituation situation) {
        tryAllPossibleMoves(0, situation, situation.whoseTurn());
    }

}
