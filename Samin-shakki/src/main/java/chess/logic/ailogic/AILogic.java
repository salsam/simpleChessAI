package chess.logic.ailogic;

import chess.domain.GameSituation;
import chess.domain.Move;
import chess.domain.board.ChessBoard;
import static chess.domain.board.ChessBoardCopier.copy;
import static chess.domain.board.ChessBoardCopier.makeFirstChessBoardDeeplyEqualToSecond;
import chess.domain.board.Player;
import chess.domain.board.Square;
import static chess.logic.ailogic.GameSituationEvaluator.evaluateGameSituation;
import chess.logic.movementlogic.MovementLogic;

/**
 *
 * @author sami
 */
public class AILogic {

    private Move bestMove;
    private Move current;
    private int[] bestValues;
    private final int plies = 2;

    public AILogic() {
        bestValues = new int[plies];
    }

    public Move getBestMove() {
        return bestMove;
    }

    public int[] getBestValues() {
        return bestValues;
    }

    public int tryAllPossibleMoves(int depth, GameSituation situation, Player player) {
        if (depth == plies) {
            return evaluateGameSituation(situation, player);
        }

        if (depth % 2 == 0) {
            ChessBoard backUp = copy(situation.getChessBoard());
            MovementLogic ml = situation.getChessBoard().getMovementLogic();
            bestValues[depth] = -123456789;

            situation.getChessBoard().getPieces(situation.whoseTurn()).stream()
                    .filter(piece -> !piece.isTaken())
                    .forEach(piece -> {
                        ml.possibleMoves(piece, situation.getChessBoard()).stream().forEach(possibility -> {
                            ml.move(piece, possibility, situation.getChessBoard());
                            bestValues[depth] = Math.max(bestValues[depth], tryAllPossibleMoves(depth + 1, situation, player));
                            makeFirstChessBoardDeeplyEqualToSecond(situation.getChessBoard(), backUp);
                        });
                    });
        }

        return bestValues[depth];
    }

    public void findBestMove(GameSituation situation) {
        tryAllPossibleMoves(0, situation, situation.whoseTurn());
    }

}
