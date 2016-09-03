package chess.domain.datastructures;

import java.util.Map;

/**
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

    public void makePairsUnsaved() {
        table.keySet().stream().forEach((key) -> {
            key.setSaved(false);
        });
    }

}
