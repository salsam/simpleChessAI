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
    private MyLinkedList<Pair>[] indices;
    private int capacity;
    private int size;
    private double loadFactor;

    public MyHashMap() {
        size = 0;
        capacity = 16;
        keys = (K[]) new Object[capacity];
        values = (V[]) new Object[capacity];
        indices = new MyLinkedList[capacity];
        initializeLinkedLists(indices);
        loadFactor = 0.75;
    }

    private void initializeLinkedLists(MyLinkedList[] linkedListTable) {
        for (int i = 0; i < capacity; i++) {
            linkedListTable[i] = new MyLinkedList();
        }
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
        return indices[o.hashCode() % capacity].stream()
                .anyMatch((p) -> (p.getFirst().equals(o)));
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
        return indices[hash].stream()
                .filter((pair) -> pair.getFirst().equals(o))
                .map(p -> values[(int) p.getSecond()])
                .findFirst()
                .get();
    }

    @Override
    public Object put(Object k, Object v) {
        ensureCapacity();
        int hash = k.hashCode() % capacity;
        int oldIndex = -1;

        for (int i = 0; i < indices[hash].size(); i++) {
            if (indices[hash].get(i).getFirst().equals(k)) {
                oldIndex = i;
                break;
            }
        }

        if (oldIndex != -1) {
            values[oldIndex] = (V) v;
        } else {
            keys[size] = (K) k;
            values[size] = (V) v;
            indices[hash].add(new Pair(k, size + 1));
            size++;
        }
        return true;
    }

    private void ensureCapacity() {
        if (size >= loadFactor * capacity) {
            capacity *= 2;
            K[] newKeys = (K[]) new Object[capacity];
            V[] newValues = (V[]) new Object[capacity];
            MyLinkedList<Pair>[] newIndices = new MyLinkedList[capacity];
            initializeLinkedLists(newIndices);

            rehashToNewIndices(newKeys, newValues, newIndices);

            indices = newIndices;
            keys = newKeys;
            values = newValues;
        }
    }

    private void rehashToNewIndices(K[] newKeys, V[] newValues, MyLinkedList<Pair>[] newIndices) {
        int hash;
        for (int i = 0; i < size; i++) {
            newKeys[i] = keys[i];
            newValues[i] = values[i];
            hash = keys[i].hashCode() % capacity;
            newIndices[hash].add(new Pair(keys[i], i));
        }
    }

    @Override
    public Object remove(Object o) {
        int hash = o.hashCode() % capacity;
        for (Pair p : indices[hash]) {
            if (p.getFirst().equals(o)) {
                keys[(int) p.getSecond()] = null;
                values[(int) p.getSecond()] = null;
                indices[hash].remove(p);
                return true;
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
        capacity = 16;
        size = 0;
        indices = new MyLinkedList[capacity];
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
