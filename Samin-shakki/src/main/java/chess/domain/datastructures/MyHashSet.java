/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chess.domain.datastructures;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * My own implementation of Java's HashSet. LoadFactor of 0.75 is used as it's
 * Java's standard and generally offers good balance between efficiency and
 * memory usage. InitialCapacity set to 16 for for Java standard.
 *
 * @author sami
 * @param <T> type of object being stored in this set.
 */
public class MyHashSet<T extends Object> implements Set<T> {

    private final double loadFactor;
    private int capacity;
    private int size;
    private MyLinkedList<T>[] buckets;

    public MyHashSet() {
        loadFactor = 0.75;
        capacity = 16;
        size = 0;
        buckets = new MyLinkedList[capacity];
        initializeLinkedLists(buckets);
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
    public boolean contains(Object o) {
        return buckets[Math.abs(o.hashCode() % capacity)].contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<T> it = new Iterator<T>() {
            private int soFar = 0;
            private int bucket = 0;
            private Iterator<T> listIt = buckets[0].iterator();

            @Override
            public boolean hasNext() {
                return soFar < size;
            }

            @Override
            public T next() {
                soFar++;

                while (!listIt.hasNext()) {
                    bucket++;
                    listIt = buckets[bucket].iterator();
                }

                return listIt.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };

        return it;
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean add(T e) {
        ensureCapacity();
        int hash = Math.abs(e.hashCode() % capacity);
        if (buckets[hash].contains(e)) {
            return false;
        }
        buckets[hash].add(e);
        size++;
        return true;
    }

    private void ensureCapacity() {
        if (size >= loadFactor * capacity) {
            capacity *= 2;
            rehashValues(capacity / 2);
        }
    }

    private void initializeLinkedLists(MyLinkedList[] linkedListTable) {
        for (int i = 0; i < capacity; i++) {
            linkedListTable[i] = new MyLinkedList();
        }
    }

    @Override
    public boolean remove(Object o) {
        if (buckets[Math.abs(o.hashCode() % capacity)].remove(o)) {
            size--;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> clctn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(Collection<? extends T> clctn) {
        boolean reservedMemory = false;
        boolean valueWasAdded = false;
        int oldCapacity = capacity;
        while (capacity < size + clctn.size()) {
            capacity *= 2;
            reservedMemory = true;
        }

        if (reservedMemory) {
            rehashValues(oldCapacity);
        }

        for (T t : clctn) {
            if (add(t)) {
                valueWasAdded = true;
            }
        }

        return valueWasAdded;
    }

    private void rehashValues(int oldCapacity) {
        MyLinkedList[] newBuckets = new MyLinkedList[capacity];
        initializeLinkedLists(newBuckets);

        for (int i = 0; i < oldCapacity; i++) {
            for (T element : buckets[i]) {
                newBuckets[Math.abs(element.hashCode() % capacity)].add(element);
            }
        }

        buckets = newBuckets;
    }

    @Override
    public boolean retainAll(Collection<?> clctn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAll(Collection<?> clctn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        capacity = 16;
        size = 0;
        buckets = new MyLinkedList[capacity];
        initializeLinkedLists(buckets);
    }

}
