public class MinHeap {
    private int[] data;
    private int size;
    private OpCounter counter = new OpCounter();

    public MinHeap() {
        this(16);
    }

    public MinHeap(int initialCapacity) {
        this.data = new int[Math.max(initialCapacity, 1)];
        this.size = 0;
    }

    public void setOpCounter(OpCounter counter) {
        this.counter = (counter != null) ? counter : new OpCounter();
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > data.length) {
            int newCapacity = Math.max(data.length * 2, minCapacity);
            int[] newData = new int[newCapacity];
            for (int i = 0; i < size; i++) {
                counter.steps++;
                newData[i] = data[i];
                counter.moves++;
            }
            data = newData;
        }
    }

    private void swap(int i, int j) {
        counter.steps += 2;
        int tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
        counter.moves += 2;
    }

    public void insert(int x) {
        ensureCapacity(size + 1);
        data[size] = x;
        counter.moves++;
        bubbleUp(size);
        size++;
    }

    private void bubbleUp(int index) {
        int curr = index;
        while (curr > 0) {
            int parent = (curr - 1) / 2;
            counter.steps += 2;
            counter.comparisons++;
            if (data[curr] < data[parent]) {
                swap(curr, parent);
                curr = parent;
            } else {
                break;
            }
        }
    }

    public int peekMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }
        counter.steps++;
        return data[0];
    }

    public int extractMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }
        counter.steps++;
        int minVal = data[0];
        data[0] = data[size - 1];
        counter.steps++;
        counter.moves++;
        size--;
        if (size > 0) {
            bubbleDown(0);
        }
        return minVal;
    }

    public void bubbleDown(int index) {
        int curr = index;
        while ((curr * 2 + 1) < size) {
            int left = curr * 2 + 1;
            int right = curr * 2 + 2;
            int smallest = left;

            if (right < size) {
                counter.steps += 2;
                counter.comparisons++;
                if (data[right] < data[left]) {
                    smallest = right;
                }
            }

            counter.steps += 2;
            counter.comparisons++;
            if (data[smallest] < data[curr]) {
                swap(curr, smallest);
                curr = smallest;
            } else {
                break;
            }
        }
    }

    public void buildHeap(int[] array) {
        this.data = new int[array.length];
        this.size = array.length;
        for (int i = 0; i < array.length; i++) {
            counter.steps++;
            this.data[i] = array[i];
            counter.moves++;
        }
        for (int i = (size / 2) - 1; i >= 0; i--) {
            bubbleDown(i);
        }
    }

    public int size() {
        return size;
    }

    public int[] getInternalArray() {
        return data;
    }
}