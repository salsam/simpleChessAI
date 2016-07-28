package chess.logic.ailogic;

import chess.domain.GameSituation;
import chess.domain.Move;
import chess.domain.board.ChessBoard;
import static chess.domain.board.ChessBoardCopier.copy;
import static chess.domain.board.ChessBoardCopier.makeFirstChessBoardDeeplyEqualToSecond;
import chess.domain.board.Player;
import static chess.domain.board.Player.getOpponent;
import static chess.logic.ailogic.GameSituationEvaluator.evaluateGameSituation;
import chess.logic.movementlogic.MovementLogic;

/**
 * This class is responsible for calculating AI's next move and then returning
 * it when asked for next move.
 *
 * @author sami
 */
public class AILogic {

    private Move bestMove;
    private int[] bestValues;
    private final int plies = 2;

    public AILogic() {
        bestValues = new int[plies];
    }

    public Move getBestMove() {
        return bestMove;
    }

    public int tryAllPossibleMoves(int depth, GameSituation situation, Player player) {
        if (depth == plies) {
            return evaluateGameSituation(situation, player);
        } else if (situation.getCheckLogic().checkIfCheckedAndMated(player)) {
            return -123456789;
        } else if (situation.getCheckLogic().checkIfCheckedAndMated(getOpponent(player))) {
            return 123456789;
        }

        if (depth % 2 == 0) {
            playerChoosesMoveWithHighestValue(situation, depth, player);
        } else {
            opponentChoosesMoveToMinimizePlayersValue(situation, depth, player);
        }

        return bestValues[depth];
    }

    private void playerChoosesMoveWithHighestValue(GameSituation situation, int depth, Player player) {
        ChessBoard backUp = copy(situation.getChessBoard());
        MovementLogic ml = situation.getChessBoard().getMovementLogic();
        bestValues[depth] = -123456789;

        situation.getChessBoard().getPieces(situation.whoseTurn()).stream()
                .filter(piece -> !piece.isTaken())
                .forEach(piece -> {
                    ml.possibleMoves(piece, situation.getChessBoard()).stream().forEach(possibility -> {
                        ml.move(piece, possibility, situation.getChessBoard());
                        int value = tryAllPossibleMoves(depth + 1, situation, player);
                        if (value > bestValues[depth]) {
                            bestValues[depth] = value;
                            if (depth == 0) {
                                bestMove = new Move(piece, possibility);
                            }
                        }
                        makeFirstChessBoardDeeplyEqualToSecond(situation.getChessBoard(), backUp);
                    });
                });
    }

    private void opponentChoosesMoveToMinimizePlayersValue(GameSituation situation, int depth, Player player) {
        ChessBoard backUp = copy(situation.getChessBoard());
        MovementLogic ml = situation.getChessBoard().getMovementLogic();
        bestValues[depth] = 123456789;

        situation.getChessBoard().getPieces(situation.whoseTurn()).stream()
                .filter(piece -> !piece.isTaken())
                .forEach(piece -> {
                    ml.possibleMoves(piece, situation.getChessBoard()).stream()
                            .forEach(possibility -> {
                                ml.move(piece, possibility, situation.getChessBoard());
                                bestValues[depth] = Math.min(bestValues[depth],
                                        tryAllPossibleMoves(depth + 1, situation, player));
                                makeFirstChessBoardDeeplyEqualToSecond(situation.getChessBoard(), backUp);
                            });
                });
    }

    public void findBestMove(GameSituation situation) {
        tryAllPossibleMoves(0, situation, situation.whoseTurn());
    }

}
