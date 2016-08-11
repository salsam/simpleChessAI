package chess.domain.datastructures;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sami
 */
public class MyHashMapTest {

    private MyHashMap mhm;

    public MyHashMapTest() {
    }

    @Before
    public void setUp() {
        mhm = new MyHashMap();
    }

    @Test
    public void hashMapEmptyAndSizeZeroAfterInitialization() {
        assertTrue(mhm.isEmpty());
        assertEquals(0, mhm.size());
    }

    @Test
    public void containsKeyFalseWhenEmpty() {
        assertFalse(mhm.containsKey(1));
    }

    @Test
    public void containsVallueFalseWhenEmpty() {
        assertFalse(mhm.containsValue(1));
    }

    @Test
    public void sizeGoesUpWhenAddingKeyValuePairsToMap() {
        mhm.put(1, 2);
        assertEquals(1, mhm.size());

        mhm.put(2, 3);
        assertEquals(2, mhm.size());

        mhm.put(3, 4);
        assertEquals(3, mhm.size());
    }

    @Test
    public void sizeDoesNotGoUpWhenChangingValueOfExistingKey() {
        mhm.put(1, 2);
        mhm.put(1, 3);
        assertEquals(1, mhm.size());
    }

    @Test
    public void containsKeyFalseIfMapDoesNotContainsKey() {
        mhm.put(1, 2);
        mhm.put(2, 3);
        mhm.put(3, 4);

        assertFalse(mhm.containsKey(4));
    }

    @Test
    public void containsKeyTrueIfKeyHasBeenAddedToMap() {
        mhm.put(1, 2);
        mhm.put(2, 3);
        mhm.put(3, 4);

        assertTrue(mhm.containsKey(1));
        assertTrue(mhm.containsKey(2));
        assertTrue(mhm.containsKey(3));
    }

    @Test
    public void containsValueFalseIfMapDoesNotContainValue() {
        mhm.put(1, 2);
        mhm.put(2, 3);
        mhm.put(3, 4);

        assertFalse(mhm.containsValue(1));
    }

    @Test
    public void containsValueTrueIfValueHasBeenAddedToMap() {
        mhm.put(1, 2);
        mhm.put(2, 3);
        mhm.put(3, 4);

        assertTrue(mhm.containsValue(4));
        assertTrue(mhm.containsValue(2));
        assertTrue(mhm.containsValue(3));
    }

    @Test
    public void containsValueFalseIfAssociatedKeyMappedToNewValue() {
        mhm.put(1, 2);
        mhm.put(1, 42);
        assertFalse(mhm.containsValue(2));
    }

    @Test
    public void getReturnsNullIfNoValueIfMapDoesNotContainGivenKey() {
        assertEquals(null, mhm.get(42));
    }

    @Test
    public void getReturnsValueAssociatedWithGivenKey() {
        mhm.put(1, 2);
        mhm.put(2, -273);
        mhm.put(-273, 42);

        assertEquals(2, mhm.get(1));
        assertEquals(-273, mhm.get(2));
        assertEquals(42, mhm.get(-273));
    }
}
