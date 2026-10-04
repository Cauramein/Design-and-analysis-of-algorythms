import org.junit.jupiter.api.Test;
import java.util.LinkedList;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class MyLinkedListTest {

    @Test
    public void testBoundaryConditions() {
        MyLinkedList list = new MyLinkedList();
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        list.add(100);
        assertEquals(100, list.get(0));
        assertEquals(100, list.remove(0));
        assertEquals(0, list.size());
    }

    @Test
    public void testRandomizedEquivalence() {
        MyLinkedList myList = new MyLinkedList();
        LinkedList<Integer> expected = new LinkedList<>();
        Random rand = new Random(202);

        for (int i = 0; i < 2000; i++) {
            int op = rand.nextInt(4);
            int val = rand.nextInt(500);
            if (op == 0 || expected.isEmpty()) {
                myList.add(val);
                expected.add(val);
            } else if (op == 1) {
                int idx = rand.nextInt(expected.size());
                myList.add(idx, val);
                expected.add(idx, val);
            } else if (op == 2) {
                int idx = rand.nextInt(expected.size());
                assertEquals(expected.remove(idx).intValue(), myList.remove(idx));
            } else {
                int idx = rand.nextInt(expected.size());
                assertEquals(expected.get(idx).intValue(), myList.get(idx));
            }
            assertEquals(expected.size(), myList.size());
        }
    }
}