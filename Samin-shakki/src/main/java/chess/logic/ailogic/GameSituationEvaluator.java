package chess.logic.ailogic;

import chess.domain.GameSituation;
import chess.domain.board.Player;
import static chess.domain.board.Player.getOpponent;
import chess.domain.pieces.Bishop;
import chess.domain.pieces.Knight;
import chess.domain.pieces.Pawn;
import chess.domain.pieces.Piece;
import chess.domain.pieces.Queen;
import chess.domain.pieces.Rook;

/**
 *
 * @author sami
 */
public class GameSituationEvaluator {

    private static final int pawnValue = 1;
    private static final int knightValue = 3;
    private static final int bishopValue = 3;
    private static final int rookValue = 5;
    private static final int queenValue = 9;

    private static int getValue(Piece piece) {
        if (piece.getClass() == Pawn.class) {
            return pawnValue;
        } else if (piece.getClass() == Knight.class) {
            return knightValue;
        } else if (piece.getClass() == Bishop.class) {
            return bishopValue;
        } else if (piece.getClass() == Rook.class) {
            return rookValue;
        } else if (piece.getClass() == Queen.class) {
            return queenValue;
        }
        return 0;
    }

    public static int evaluateGameSituation(GameSituation situation, Player player) {
        int value = 0;
        if (situation.getCheckLogic().checkIfChecked(player)
                && situation.getCheckLogic().checkMate(player)) {
            return -123456789;
        }

        if (situation.getCheckLogic().checkIfChecked(getOpponent(player))
                && situation.getCheckLogic().checkMate(getOpponent(player))) {
            return 123456789;
        }

        value = situation.getChessBoard().getPieces(player).stream().
                filter(piece -> !piece.isTaken()).mapToInt(piece -> getValue(piece))
                .sum();
        value -= situation.getChessBoard().getPieces(getOpponent(player)).stream()
                .filter(piece -> !piece.isTaken()).mapToInt(piece -> getValue(piece))
                .sum();
        return value;
    }
}
