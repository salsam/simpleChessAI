package chess.domain.datastructures;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * My own take on Java's HashMap. LoadFactor and initial capacity set to Java's
 * standard 0.75 and 16.
 *
 * @author sami
 * @param <K> Class of keys.
 * @param <V> Class of values.
 */
public class MyHashMap<K extends Object, V extends Object> implements Map {

    private K[] keys;
    private V[] values;
    private MyLinkedList<Integer>[] indices;
    private MyLimitedStack<Integer> freedIndices;
    private int capacity;
    private int size;
    private final double loadFactor;

    public MyHashMap() {
        size = 0;
        capacity = 16;
        keys = (K[]) new Object[capacity];
        values = (V[]) new Object[capacity];
        freedIndices = new MyLimitedStack(capacity);
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
        return indices[Math.abs(o.hashCode() % capacity)].stream()
                .anyMatch((index) -> keys[index].equals(o));
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
        int hash = Math.abs(o.hashCode() % capacity);
        if (!containsKey(o)) {
            return null;
        }

        return indices[hash].stream()
                .filter(index -> keys[index].equals(o))
                .map(index -> values[index])
                .findFirst()
                .get();
    }

    @Override
    public Object put(Object key, Object value) {
        ensureCapacity();
        int hash = Math.abs(key.hashCode() % capacity);
        int oldIndex = findOldIndex(hash, key);
        int newIndex = size;

        if (oldIndex != -1) {
            values[oldIndex] = (V) value;
        } else {
            if (!freedIndices.isEmpty()) {
                newIndex = freedIndices.pop();
            }
            keys[newIndex] = (K) key;
            values[newIndex] = (V) value;
            indices[hash].add(newIndex);
            size++;
        }
        return true;
    }

    private int findOldIndex(int hash, Object key) {
        int oldIndex = -1;
        for (Integer index : indices[hash]) {
            if (keys[index].equals(key)) {
                oldIndex = index;
                break;
            }
        }
        return oldIndex;
    }

    private void ensureCapacity() {
        if (size >= loadFactor * capacity) {
            capacity *= 2;
            K[] newKeys = (K[]) new Object[capacity];
            V[] newValues = (V[]) new Object[capacity];
            MyLinkedList<Integer>[] newIndices = new MyLinkedList[capacity];
            initializeLinkedLists(newIndices);

            rehashToNewIndices(newKeys, newValues, newIndices);

            freedIndices = new MyLimitedStack(capacity);
            indices = newIndices;
            keys = newKeys;
            values = newValues;
        }
    }

    private void rehashToNewIndices(K[] newKeys, V[] newValues, MyLinkedList<Integer>[] newIndices) {
        for (int i = 0; i < size; i++) {
            newKeys[i] = keys[i];
            newValues[i] = values[i];
            newIndices[Math.abs(keys[i].hashCode() % capacity)].add(i);
        }
    }

    @Override
    public Object remove(Object o) {
        int hash = Math.abs(o.hashCode() % capacity);
        V ret = null;

        for (Integer index : indices[hash]) {
            if (keys[index].equals(o)) {
                ret = values[index];
                keys[index] = null;
                values[index] = null;
                indices[hash].remove(index);

                size--;
                freedIndices.push(index);
                return ret;
            }
        }

        return ret;
    }

    @Override
    public void putAll(Map map) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        capacity = 16;
        size = 0;
        freedIndices = new MyLimitedStack(capacity);
        indices = new MyLinkedList[capacity];
        keys = (K[]) new Object[capacity];
        values = (V[]) new Object[capacity];
    }

    @Override
    public Set keySet() {
        Set<K> ks = new HashSet();
        for (int i = 0; i < size; i++) {
            if (keys[i] != null) {
                ks.add(keys[i]);
            }
        }
        return ks;
    }

    @Override
    public Collection values() {
        Collection<V> vals = new MyHashSet();
        for (int i = 0; i < size; i++) {
            if (values[i] != null) {
                vals.add(values[i]);
            }
        }
        return vals;
    }

    @Override
    public Set entrySet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
