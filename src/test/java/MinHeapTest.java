import org.junit.jupiter.api.Test;
import java.util.PriorityQueue;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class MinHeapTest {

    private void assertHeapProperty(MinHeap heap) {
        int[] arr = heap.getInternalArray();
        int n = heap.size();
        for (int i = 0; i <= (n - 2) / 2; i++) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            if (left < n) assertTrue(arr[i] <= arr[left], "Heap property breached at left child");
            if (right < n) assertTrue(arr[i] <= arr[right], "Heap property breached at right child");
        }
    }

    @Test
    public void testEmptyHeapThrows() {
        MinHeap heap = new MinHeap();
        assertThrows(IllegalStateException.class, heap::peekMin);
        assertThrows(IllegalStateException.class, heap::extractMin);
    }

    @Test
    public void testHeapPropertyAndSortedExtraction() {
        MinHeap heap = new MinHeap();
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        Random rand = new Random(303);

        for (int i = 0; i < 500; i++) {
            int v = rand.nextInt(1000);
            heap.insert(v);
            pq.add(v);
            assertHeapProperty(heap);
        }

        while (!pq.isEmpty()) {
            assertEquals(pq.poll().intValue(), heap.extractMin());
            assertHeapProperty(heap);
        }
    }

    @Test
    public void testBuildHeapLinearTime() {
        int[] data = {19, 3, 2, 8, 17, 1, 25, 100, 0, 4};
        MinHeap heap = new MinHeap();
        heap.buildHeap(data);
        assertHeapProperty(heap);
        int prev = Integer.MIN_VALUE;
        while (heap.size() > 0) {
            int cur = heap.extractMin();
            assertTrue(cur >= prev);
            prev = cur;
        }
    }
}