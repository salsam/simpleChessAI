package chess.logic.ailogic;

import chess.domain.GameSituation;
import chess.domain.board.Player;
import static chess.domain.board.Player.getOpponent;
import chess.domain.pieces.Bishop;
import chess.domain.pieces.King;
import chess.domain.pieces.Knight;
import chess.domain.pieces.Pawn;
import chess.domain.pieces.Queen;
import chess.domain.pieces.Rook;
import java.util.HashMap;
import java.util.Map;

/**
 * This class offers method evaluateGameSituation which is used to evaluate the
 * value of given game situation for the player. All values are measured in
 * centipawns (one hundredth of pawn's value).
 *
 * @author sami
 */
public class GameSituationEvaluator {

    private static Map<Class, Integer> values;
    private static Map<Class, Integer[][]> positionValues;

    private static void initValues() {
        values = new HashMap();
        values.put(Pawn.class, 100);
        values.put(Knight.class, 320);
        values.put(Bishop.class, 333);
        values.put(Rook.class, 510);
        values.put(Queen.class, 880);
        values.put(King.class, 40000);
    }

    private static void initPositionValues() {

    }

    /**
     * Calculates value of given game situation from given player's point of
     * view. Uses multiple helper functions to calculate different partial
     * values like mobility or material value of situation.
     *
     * @param sit current game situation
     * @param player player from whose point of view value is calculated
     * @return value of given game situation from given player's point of view
     */
    public static int evaluateGameSituation(GameSituation sit, Player player) {
        int value = 0;
//        if (situation.getCheckLogic().checkIfCheckedAndMated(player)) {
//            return -123456789;
//        }
//
//        if (situation.getCheckLogic().checkIfCheckedAndMated(getOpponent(player))) {
//            return 123456789;
//        }

        value += materialValue(sit, player);
        value += mobilityValue(sit, player);
        return value;
    }

    private static int materialValue(GameSituation situation, Player player) {
        if (values == null) {
            initValues();
        }

        if (positionValues == null) {
            initPositionValues();
        }

        int value = situation.getChessBoard().getPieces(player).stream()
                .filter(piece -> !piece.isTaken())
                .mapToInt(piece -> values.get(piece.getClass()) + positionValues.get(piece.getClass())[piece.getColumn()][piece.getRow()])
                .sum();
        value -= situation.getChessBoard().getPieces(getOpponent(player)).stream()
                .filter(piece -> !piece.isTaken())
                .mapToInt(piece -> values.get(piece.getClass()) + positionValues.get(piece.getClass())[piece.getColumn()][piece.getRow()])
                .sum();
        return value;
    }

    private static int mobilityValue(GameSituation sit, Player player) {
        int value = sit.getChessBoard().getPieces(player).stream().filter(p -> !p.isTaken())
                .mapToInt(p -> {
                    return sit.getChessBoard().getMovementLogic().possibleMoves(p, sit.getChessBoard()).size();
                }).sum();
        value -= sit.getChessBoard().getPieces(getOpponent(player)).stream().filter(p -> !p.isTaken())
                .mapToInt(p -> {
                    return sit.getChessBoard().getMovementLogic().possibleMoves(p, sit.getChessBoard()).size();
                }).sum();

        return value;
    }
}
