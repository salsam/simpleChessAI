package chess.domain.datastructures;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * My own take on Java's HashMap.
 *
 * @author sami
 * @param <K> Class of keys.
 * @param <V> Class of values.
 */
public class MyHashMap<K extends Object, V extends Object> implements Map {

    private K[] keys;
    private V[] values;
    private int[] indices;
    private int capacity;
    private int size;
    private double loadFactor;

    public MyHashMap() {
        size = 0;
        capacity = 16;
        keys = (K[]) new Object[capacity];
        values = (V[]) new Object[capacity];
        indices = new int[capacity];
        loadFactor = 0.75;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object o) {
        int hash = o.hashCode() % capacity;

        while (indices[hash] != 0) {
            if (keys[indices[hash] - 1].equals(o)) {
                return true;
            }
            hash++;
        }

        return false;
    }

    @Override
    public boolean containsValue(Object o) {
        for (int i = 0; i < size; i++) {
            if (values[i] != null && values[i].equals(o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object get(Object o) {
        int hash = o.hashCode() % capacity;

        while (indices[hash] != 0) {
            if (keys[indices[hash] - 1].equals(o)) {
                return values[indices[hash] - 1];
            }
            hash++;
        }

        return null;
    }

    @Override
    public Object put(Object k, Object v) {
        if (size >= loadFactor * capacity) {
            reserveMoreMemory();
        }
        int hash = k.hashCode() % capacity;
        int oldIndex = -1;

        while (indices[hash] != 0) {
            if (keys[indices[hash] - 1].equals(k)) {
                oldIndex = indices[hash] - 1;
                break;
            }
            hash++;
            if (hash == capacity) {
                hash = 0;
            }
        }

        if (oldIndex != -1) {
            values[oldIndex] = (V) v;
        } else {
            keys[size] = (K) k;
            values[size] = (V) v;
            indices[hash] = size + 1;
            size++;
        }
        return true;
    }

    private void reserveMoreMemory() {
        capacity *= 2;
        K[] newKeys = (K[]) new Object[capacity];
        V[] newValues = (V[]) new Object[capacity];
        int[] newIndices = new int[capacity];
        int hash;

        for (int i = 0; i < size; i++) {
            newKeys[i] = keys[i];
            newValues[i] = values[i];
            hash = keys[i].hashCode() % capacity;
            while (newIndices[hash] != 0) {
                hash++;
                if (hash == capacity) {
                    hash = 0;
                }
            }
            newIndices[hash] = i;
        }

        indices = newIndices;
        keys = newKeys;
        values = newValues;
    }

    @Override
    public Object remove(Object o) {
        int hash = o.hashCode() % capacity;
        int start = hash;

        while (indices[hash] != 0) {
            if (keys[indices[hash] - 1].equals(o)) {
                keys[indices[hash] - 1] = null;
                values[indices[hash] - 1] = null;
                indices[hash] = 0;
                return true;
            }
            hash++;
            if (hash == capacity) {
                hash = 0;
            } else if (hash == start) {
                break;
            }
        }
        return false;
    }

    @Override
    public void putAll(Map map) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        indices = new int[capacity];
        keys = (K[]) new Object[capacity];
        values = (V[]) new Object[capacity];
    }

    @Override
    public Set keySet() {
        Set<K> ks = new HashSet();
        for (int i = 0; i < size; i++) {
            ks.add(keys[i]);
        }
        return ks;
    }

    @Override
    public Collection values() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set entrySet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
