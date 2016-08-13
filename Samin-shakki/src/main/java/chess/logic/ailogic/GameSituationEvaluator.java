package chess.logic.ailogic;

import chess.domain.GameSituation;
import chess.domain.board.Player;
import static chess.domain.board.Player.getOpponent;
import chess.domain.datastructures.MyHashMap;
import chess.domain.pieces.*;
import java.util.Map;

/**
 * This class offers method evaluateGameSituation which is used to evaluate the
 * value of given game situation for the player. Material values are based on
 * Hans Berliner. All values are measured in centipawns (one hundredth of pawn's
 * value).
 *
 * @author sami
 */
public class GameSituationEvaluator {

    private static Map<Class, Integer> values;
    private static Map<Class, Integer[][]> positionalValues;

    private static void initValues() {
        values = new MyHashMap();
        values.put(Pawn.class, 100);
        values.put(Knight.class, 320);
        values.put(Bishop.class, 333);
        values.put(Rook.class, 510);
        values.put(Queen.class, 880);
        values.put(King.class, 40000);
    }

    private static void initPositionValues() {
        positionalValues = new MyHashMap();
        initBishopPositionalValues();
        initKingPositionalValues();
        initKnightPositionalValues();
        initPawnPositionalValues();
        initQueenPositionalValues();
        initRookPositionalValues();
    }

    private static void initBishopPositionalValues() {
        Integer[][] bishopValues = {{-20, - 10, -10, -10, -10, -10, -10, -20},
        {-10, 5, 0, 0, 0, 0, 5, -10}, {-10, 10, 10, 10, 10, 10, 10, -10},
        {-10, 0, 10, 10, 10, 10, 0, -10}, {-10, 5, 5, 10, 10, 5, 5, -10},
        {-10, 0, 5, 10, 10, 5, 0, -10}, {-10, 0, 0, 0, 0, 0, 0, -10},
        {-20, - 10, -10, -10, -10, -10, -10, -20}};
        positionalValues.put(Bishop.class, bishopValues);
    }

    private static void initKingPositionalValues() {
        Integer[][] kingValues = {{20, 30, 10, 0, 0, 10, 30, 20},
        {20, 20, 0, 0, 0, 0, 20, 20}, {-10, -20, -20, -20, -20, -20, -20, -10},
        {-20, -30, -30, -40, -40, -30, -30, -20}, {-30, -40, -40, -50, -50, -40, -40, -30},
        {-30, -40, -40, -50, -50, -40, -40, -30}, {-30, -40, -40, -50, -50, -40, -40, -30},
        {-30, -40, -40, -50, -50, -40, -40, -30}};
        positionalValues.put(King.class, kingValues);
    }

    private static void initKnightPositionalValues() {
        Integer[][] knightValues = {{-50, -40, -30, -30, -30, -30, -40, -50},
        {-40, -20, 0, 5, 5, 0, -20, -40}, {-30, 5, 10, 15, 15, 10, 5, -30},
        {-30, 0, 15, 20, 20, 15, 0, -30}, {-30, 5, 15, 20, 20, 15, 5, -30},
        {-30, 0, 10, 15, 15, 10, 0, -30}, {-40, -20, 0, 0, 0, 0, -20, -40},
        {-50, -40, -30, -30, -30, -30, -40, -50}};
        positionalValues.put(Knight.class, knightValues);
    }

    private static void initPawnPositionalValues() {
        Integer[][] pawnValues = {{0, 0, 0, 0, 0, 0, 0, 0},
        {5, 10, 10, -20, -20, 10, 10, 5},
        {5, -5, -10, 0, 0, -10, -5, 5}, {0, 0, 0, 20, 20, 0, 0, 0},
        {5, 5, 10, 25, 25, 10, 5, 5}, {10, 10, 20, 30, 30, 20, 10, 10},
        {50, 50, 50, 50, 50, 50, 50, 50}, {0, 0, 0, 0, 0, 0, 0, 0}};
        positionalValues.put(Pawn.class, pawnValues);
    }

    private static void initQueenPositionalValues() {
        Integer[][] queenValues = {{-20, -10, -10, -5, -5, -10, -10, -20},
        {-10, 0, 5, 0, 0, 0, 0, -10}, {-10, 5, 5, 5, 5, 5, 0, -10},
        {0, 0, 5, 5, 5, 5, 0, -5}, {-5, 0, 5, 5, 5, 5, 0, -5},
        {-10, 0, 5, 5, 5, 5, 0, - 10}, {-10, 0, 0, 0, 0, 0, 0, -10},
        {-20, -10, -10, -5, -5, -10, -10, -20}};
        positionalValues.put(Queen.class, queenValues);
    }

    private static void initRookPositionalValues() {
        Integer[][] rookValues = {{0, 0, 0, 5, 5, 0, 0, 0},
        {-5, 0, 0, 0, 0, 0, 0, -5}, {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5}, {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5}, {5, 10, 10, 10, 10, 10, 10, 5},
        {0, 0, 0, 0, 0, 0, 0, 0}};
        positionalValues.put(Rook.class, rookValues);
    }

    private static int getPositionalValue(Piece piece) {
        if (piece.getOwner() == Player.WHITE) {
            return positionalValues.get(piece.getClass())[piece.getRow()][piece.getColumn()];
        }
        return positionalValues.get(piece.getClass())[7 - piece.getRow()][7 - piece.getColumn()];
    }

    /**
     * Calculates value of given game situation from given player's point of
     * view. Uses multiple helper functions to calculate different partial
     * values like mobility (amount of possible moves player can make) or
     * material value of situation (flat material value of owned pieces plus
     * their positional values).
     *
     * @param sit current game situation
     * @param player player from whose point of view value is calculated.
     * @return value of given game situation from given player's point of view.
     */
    public static int evaluateGameSituation(GameSituation sit, Player player) {
        int value = 0;
        if (sit.getCheckLogic().checkIfCheckedAndMated(player)) {
            return 123456789;
        } else if (sit.getCheckLogic().stalemate(player)) {
            return 0;
        }

        value += materialValue(sit, player);
        value += mobilityValue(sit, player);
        return value;
    }

    private static int materialValue(GameSituation situation, Player player) {
        if (values == null) {
            initValues();
        }

        if (positionalValues == null) {
            initPositionValues();
        }

        int value = situation.getChessBoard().getPieces(player).stream()
                .filter(piece -> !piece.isTaken())
                .mapToInt(piece -> values.get(piece.getClass()) + getPositionalValue(piece))
                .sum();

        value -= situation.getChessBoard().getPieces(getOpponent(player)).stream()
                .filter(piece -> !piece.isTaken())
                .mapToInt(piece -> values.get(piece.getClass()) + getPositionalValue(piece))
                .sum();
        return value;
    }

    private static int mobilityValue(GameSituation sit, Player player) {
        int value = sit.getChessBoard().getPieces(player).stream()
                .filter(p -> !p.isTaken())
                .mapToInt(p -> {
                    return sit.getChessBoard().getMovementLogic().possibleMoves(p, sit.getChessBoard()).size();
                }).sum();

        value -= sit.getChessBoard().getPieces(getOpponent(player)).stream()
                .filter(p -> !p.isTaken())
                .mapToInt(p -> {
                    return sit.getChessBoard().getMovementLogic().possibleMoves(p, sit.getChessBoard()).size();
                }).sum();

        return value;
    }
}
