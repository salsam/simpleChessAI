package chess.domain.datastructures;

import java.util.Map;

/**
 * This class is used for saving search results of AI for search corresponding
 * to transposition key with result of transposition entry.
 *
 * @author sami
 */
public class TranspositionTable {

    private Map<TranspositionKey, TranspositionEntry> table;

    public TranspositionTable() {
        table = new MyHashMap();
    }

    public boolean containsKey(TranspositionKey key, int height) {
        if (!table.containsKey(key)) {
            return false;
        }
        return table.get(key).getHeight() >= height;
    }

    public TranspositionEntry get(TranspositionKey key) {
        return table.get(key);
    }

    /**
     * Puts the given key and entry associated with it to HashMap. For each key
     * only one result with highest depth is saved. If memory cap is reached,
     * removes a random entry to make space for the one being added.
     *
     * @param key key representing chessboard situation at time of search.
     * @param entry entry representing the result of said search.
     */
    public void put(TranspositionKey key, TranspositionEntry entry) {
//        if (table.size() > 100000) {
//            for (TranspositionKey k : table.keySet()) {
//                if (!k.isSaved()) {
//                    table.remove(k);
//                    break;
//                }
//            }
//        }
        if (!table.containsKey(key)) {
            table.put(key, entry);
        } else if (table.get(key).getHeight() < entry.getHeight()) {
            table.put(key, entry);
        }
    }

    /**
     * Makes all pairs saved HashMap eligible for being removed if memory cap is
     * reached.
     */
    public void makePairsUnsaved() {
        table.keySet().stream().forEach((key) -> {
            key.setSaved(false);
        });
    }

}
