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
 * memery usage. InitialCapacity set to 16 for for Java standard.
 *
 * @author sami
 * @param <T> type of object being stored in this set.
 */
public class MyHashSet<T extends Object> implements Set<T> {

    private final double loadFactor;
    private int capacity;
    private int size;
    private MyLinkedList<T>[] values;

    public MyHashSet() {
        loadFactor = 0.75;
        capacity = 16;
        size = 0;
        values = new MyLinkedList[capacity];
        initializeLinkedLists(values);
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
        return values[Math.abs(o.hashCode() % capacity)].contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        if (values[hash].contains(e)) {
            return false;
        }
        values[hash].add(e);
        size++;
        return true;
    }

    private void ensureCapacity() {
        if (size >= loadFactor * capacity) {
            capacity *= 2;
            MyLinkedList[] newValues = new MyLinkedList[capacity];
            initializeLinkedLists(newValues);
            Node<T> node;
            for (int i = 0; i < capacity / 2; i++) {
                node = values[i].getFirst();

                while (node != null) {
                    newValues[Math.abs(node.getValue().hashCode() % capacity)].add(node.getValue());
                    node = node.getNext();
                }
            }

            values = newValues;
        }
    }

    private void initializeLinkedLists(MyLinkedList[] newValues) {
        for (int i = 0; i < capacity; i++) {
            newValues[i] = new MyLinkedList();
        }
    }

    @Override
    public boolean remove(Object o) {
        if (values[Math.abs(o.hashCode() % capacity)].remove(o)) {
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        values = new MyLinkedList[capacity];
    }

}
