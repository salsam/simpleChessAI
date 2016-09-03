package chess.domain.datastructures;

/**
 * This enum contains all possible types for search results at certain node in
 * game tree. Alpha means value of node is at least search value. Beta means
 * value of node is at most search value. Exact means value of node is exactly
 * search value.
 *
 * @author sami
 */
public enum Type {
    ALPHA, EXACT, BETA;

    /**
     * Returns the opposite type of search result.
     *
     * @param type type of which opposite we are searching for.
     * @return opposite type.
     */
    public static Type getOppositeType(Type type) {
        if (type == ALPHA) {
            return BETA;
        } else if (type == BETA) {
            return ALPHA;
        }
        return EXACT;
    }
}
