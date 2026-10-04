public class MyLinkedList implements IntList {
    private static class Node {
        int val;
        Node prev;
        Node next;

        Node(int val) {
            this.val = val;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private OpCounter counter = new OpCounter();

    @Override
    public void setOpCounter(OpCounter counter) {
        this.counter = (counter != null) ? counter : new OpCounter();
    }

    private Node getNode(int index) {
        Node curr;
        if (index < (size >> 1)) {
            curr = head;
            for (int i = 0; i < index; i++) {
                counter.steps++;
                curr = curr.next;
            }
        } else {
            curr = tail;
            for (int i = size - 1; i > index; i--) {
                counter.steps++;
                curr = curr.prev;
            }
        }
        return curr;
    }

    @Override
    public void add(int x) {
        Node newNode = new Node(x);
        if (head == null) {
            head = newNode;
            tail = newNode;
            counter.moves += 2;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
            counter.moves += 3;
        }
        size++;
    }

    @Override
    public void add(int index, int x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (index == size) {
            add(x);
            return;
        }
        Node newNode = new Node(x);
        if (index == 0) {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
            counter.moves += 3;
        } else {
            Node succ = getNode(index);
            Node pred = succ.prev;
            newNode.next = succ;
            newNode.prev = pred;
            pred.next = newNode;
            succ.prev = newNode;
            counter.moves += 4;
        }
        size++;
    }

    @Override
    public int remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node target;
        if (index == 0) {
            target = head;
            head = head.next;
            counter.moves++;
            if (head != null) {
                head.prev = null;
                counter.moves++;
            } else {
                tail = null;
                counter.moves++;
            }
        } else if (index == size - 1) {
            target = tail;
            tail = tail.prev;
            tail.next = null;
            counter.moves += 2;
        } else {
            target = getNode(index);
            Node pred = target.prev;
            Node succ = target.next;
            pred.next = succ;
            succ.prev = pred;
            counter.moves += 2;
        }
        size--;
        counter.steps++;
        return target.val;
    }

    @Override
    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node n = getNode(index);
        counter.steps++;
        return n.val;
    }

    @Override
    public boolean contains(int x) {
        Node curr = head;
        while (curr != null) {
            counter.steps++;
            counter.comparisons++;
            if (curr.val == x) {
                return true;
            }
            curr = curr.next;
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }
}