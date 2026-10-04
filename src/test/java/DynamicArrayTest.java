import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class DynamicArrayTest {

    @Test
    public void testEmptyAndSingleElement() {
        DynamicArray arr = new DynamicArray(2);
        assertEquals(0, arr.size());
        assertThrows(IndexOutOfBoundsException.class, () -> arr.get(0));
        arr.add(42);
        assertEquals(1, arr.size());
        assertEquals(42, arr.get(0));
        assertEquals(42, arr.remove(0));
        assertEquals(0, arr.size());
    }

    @Test
    public void testBoundsAndEdgeIndices() {
        DynamicArray arr = new DynamicArray();
        arr.add(10);
        arr.add(20);
        arr.add(0, 5);
        arr.add(3, 30);
        assertEquals(5, arr.get(0));
        assertEquals(30, arr.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> arr.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> arr.get(4));
        assertThrows(IndexOutOfBoundsException.class, () -> arr.add(5, 99));
    }

    @Test
    public void testRandomizedConsistencyWithJavaUtil() {
        DynamicArray myArr = new DynamicArray();
        ArrayList<Integer> expected = new ArrayList<>();
        Random rand = new Random(101);

        for (int i = 0; i < 2000; i++) {
            int op = rand.nextInt(4);
            int val = rand.nextInt(500);
            if (op == 0 || expected.isEmpty()) {
                myArr.add(val);
                expected.add(val);
            } else if (op == 1) {
                int idx = rand.nextInt(expected.size());
                myArr.add(idx, val);
                expected.add(idx, val);
            } else if (op == 2) {
                int idx = rand.nextInt(expected.size());
                assertEquals(expected.remove(idx).intValue(), myArr.remove(idx));
            } else {
                int idx = rand.nextInt(expected.size());
                assertEquals(expected.get(idx).intValue(), myArr.get(idx));
            }
            assertEquals(expected.size(), myArr.size());
        }
    }
}