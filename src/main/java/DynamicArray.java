public class DynamicArray implements IntList {
    private int[] data;
    private int size;
    private OpCounter counter = new OpCounter();

    public DynamicArray() {
        this(10);
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1) initialCapacity = 1;
        this.data = new int[initialCapacity];
        this.size = 0;
    }

    @Override
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

    @Override
    public void add(int x) {
        ensureCapacity(size + 1);
        data[size] = x;
        counter.moves++;
        size++;
    }

    @Override
    public void add(int index, int x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ensureCapacity(size + 1);
        for (int i = size; i > index; i--) {
            counter.steps++;
            data[i] = data[i - 1];
            counter.moves++;
        }
        data[index] = x;
        counter.moves++;
        size++;
    }

    @Override
    public int remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        counter.steps++;
        int removed = data[index];
        for (int i = index; i < size - 1; i++) {
            counter.steps++;
            data[i] = data[i + 1];
            counter.moves++;
        }
        size--;
        return removed;
    }

    @Override
    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        counter.steps++;
        return data[index];
    }

    @Override
    public boolean contains(int x) {
        for (int i = 0; i < size; i++) {
            counter.steps++;
            counter.comparisons++;
            if (data[i] == x) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }
}