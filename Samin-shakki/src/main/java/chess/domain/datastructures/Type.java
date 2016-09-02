package chess.domain.datastructures;

/**
 *
 * @author sami
 */
public enum Type {
    ALPHA, EXACT, BETA;

    public static Type getOppositeType(Type type) {
        if (type == ALPHA) {
            return BETA;
        } else if (type == BETA) {
            return ALPHA;
        }
        return EXACT;
    }
}
