package chess.domain.datastructures;

/**
 * My implementation of stack for use in HashMap.
 *
 * @author sami
 * @param <T> type of objects being stored in this stack.
 */
public class MyLimitedStack<T extends Object> {

    private T[] array;
    private int head;

    public MyLimitedStack(int capacity) {
        array = (T[]) new Object[capacity];
        head = -1;
    }

    public boolean isEmpty() {
        return head == -1;
    }

    public T pop() {
        if (head == -1) {
            return null;
        }
        return array[head--];
    }

    public void push(T element) {
        if (head >= array.length) {
            throw new IndexOutOfBoundsException("You have passed capacity of this stack.");
        }
        array[++head] = element;
    }
}
