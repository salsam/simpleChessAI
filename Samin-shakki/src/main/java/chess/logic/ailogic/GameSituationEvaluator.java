package chess.logic.ailogic;

import chess.domain.GameSituation;
import chess.domain.board.Player;
import static chess.domain.board.Player.getOpponent;
import chess.domain.pieces.Bishop;
import chess.domain.pieces.King;
import chess.domain.pieces.Knight;
import chess.domain.pieces.Pawn;
import chess.domain.pieces.Piece;
import chess.domain.pieces.Queen;
import chess.domain.pieces.Rook;

/**
 * This class offers method evaluateGameSituation which is used to evaluate the
 * value of given game situation for the player.
 *
 * @author sami
 */
public class GameSituationEvaluator {

    private static final int pawnValue = 100;
    private static final int knightValue = 320;
    private static final int bishopValue = 333;
    private static final int rookValue = 510;
    private static final int queenValue = 880;
    private static final int kingValue = 200000;

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
        } else if (piece.getClass() == King.class) {
            return kingValue;
        }
        return 0;
    }

    public static int evaluateGameSituation(GameSituation situation, Player player) {
//        if (situation.getCheckLogic().checkIfCheckedAndMated(player)) {
//            return -123456789;
//        }
//
//        if (situation.getCheckLogic().checkIfCheckedAndMated(getOpponent(player))) {
//            return 123456789;
//        }
        return calculateMaterialValueForGameSituation(situation, player);
    }

    private static int calculateMaterialValueForGameSituation(GameSituation situation, Player player) {
        int value;
        value = situation.getChessBoard().getPieces(player).stream().
                filter(piece -> !piece.isTaken()).mapToInt(piece -> getValue(piece))
                .sum();
        value -= situation.getChessBoard().getPieces(getOpponent(player)).stream()
                .filter(piece -> !piece.isTaken()).mapToInt(piece -> getValue(piece))
                .sum();
        return value;
    }
}
