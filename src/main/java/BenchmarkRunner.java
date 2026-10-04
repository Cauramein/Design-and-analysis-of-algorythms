import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class BenchmarkRunner {
    private static final int[] SIZES = {100, 1000, 10000, 100000};
    private static final int RUNS = 5;
    private static final int WARMUP_RUNS = 3;

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        new File("results").mkdirs();

        System.out.println("Starting JIT Warmup Phase...");
        warmupJIT();
        System.out.println("Warmup complete. Executing calibrated benchmarks...");

        try (PrintWriter writer = new PrintWriter(new FileWriter("results/results.csv"))) {
            writer.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");

            for (int n : SIZES) {
                System.out.println("Processing n = " + n + "...");
                runW1(writer, n);
                runW2(writer, n);
                runW3(writer, n, "head");
                runW3(writer, n, "middle");
                runW4(writer, n);
            }
            System.out.println("Benchmark finished successfully. Results written to results/results.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void warmupJIT() {
        for (int w = 0; w < 10000; w++) {
            DynamicArray da = new DynamicArray(16);
            MyLinkedList ll = new MyLinkedList();
            MinHeap mh = new MinHeap(16);

            for (int i = 0; i < 50; i++) {
                da.add(i);
                ll.add(i);
                mh.insert(i);
            }
            da.get(10);
            ll.get(10);
            da.contains(25);
            ll.contains(25);
            da.add(0, -1);
            ll.add(0, -1);
            da.remove(0);
            ll.remove(0);
            mh.extractMin();
        }
    }

    private static double median(double[] m) {
        Arrays.sort(m);
        return m[m.length / 2];
    }

    private static void runW1(PrintWriter writer, int n) {
        String[] structs = {"DynamicArray", "MyLinkedList"};
        for (String s : structs) {
            double[] times = new double[RUNS];
            OpCounter finalCounter = new OpCounter();

            for (int r = 0; r < WARMUP_RUNS + RUNS; r++) {
                IntList list = s.equals("DynamicArray") ? new DynamicArray(n) : new MyLinkedList();
                Random initRand = new Random(42);
                for (int i = 0; i < n; i++) list.add(initRand.nextInt());

                OpCounter c = new OpCounter();
                list.setOpCounter(c);
                Random queryRand = new Random(1337);

                long start = System.nanoTime();
                for (int q = 0; q < 10000; q++) {
                    list.get(queryRand.nextInt(n));
                }
                long elapsed = System.nanoTime() - start;

                if (r >= WARMUP_RUNS) {
                    double ms = Math.max(elapsed / 1e6, 0.001);
                    times[r - WARMUP_RUNS] = ms;
                    finalCounter = c;
                }
            }
            writer.printf(Locale.US, "W1,-,%s,%d,%.4f,%d,%d,%d\n", s, n, median(times),
                    finalCounter.steps, finalCounter.moves, finalCounter.comparisons);
        }
    }

    private static void runW2(PrintWriter writer, int n) {
        String[] structs = {"DynamicArray", "MyLinkedList"};
        for (String s : structs) {
            double[] times = new double[RUNS];
            OpCounter finalCounter = new OpCounter();

            for (int r = 0; r < WARMUP_RUNS + RUNS; r++) {
                IntList list = s.equals("DynamicArray") ? new DynamicArray(n) : new MyLinkedList();
                Random initRand = new Random(42);
                int[] existing = new int[n];
                for (int i = 0; i < n; i++) {
                    existing[i] = initRand.nextInt(1_000_000);
                    list.add(existing[i]);
                }

                OpCounter c = new OpCounter();
                list.setOpCounter(c);
                Random queryRand = new Random(999);

                long start = System.nanoTime();
                for (int q = 0; q < 1000; q++) {
                    int target = (q % 2 == 0) ? existing[queryRand.nextInt(n)] : 1_000_000 + queryRand.nextInt(1_000_000);
                    list.contains(target);
                }
                long elapsed = System.nanoTime() - start;

                if (r >= WARMUP_RUNS) {
                    double ms = Math.max(elapsed / 1e6, 0.001);
                    times[r - WARMUP_RUNS] = ms;
                    finalCounter = c;
                }
            }
            writer.printf(Locale.US, "W2,-,%s,%d,%.4f,%d,%d,%d\n", s, n, median(times),
                    finalCounter.steps, finalCounter.moves, finalCounter.comparisons);
        }
    }

    private static void runW3(PrintWriter writer, int n, String variant) {
        String[] structs = {"DynamicArray", "MyLinkedList"};
        for (String s : structs) {
            double[] times = new double[RUNS];
            OpCounter finalCounter = new OpCounter();

            for (int r = 0; r < WARMUP_RUNS + RUNS; r++) {
                IntList list = s.equals("DynamicArray") ? new DynamicArray(n + 2500) : new MyLinkedList();
                Random initRand = new Random(42);
                for (int i = 0; i < n; i++) list.add(initRand.nextInt());

                OpCounter c = new OpCounter();
                list.setOpCounter(c);
                long start = System.nanoTime();

                for (int op = 0; op < 1000; op++) {
                    int idx = variant.equals("head") ? 0 : list.size() / 2;
                    list.add(idx, op);
                }
                for (int op = 0; op < 1000; op++) {
                    int idx = variant.equals("head") ? 0 : list.size() / 2;
                    list.remove(idx);
                }
                long elapsed = System.nanoTime() - start;

                if (r >= WARMUP_RUNS) {
                    double ms = Math.max(elapsed / 1e6, 0.001);
                    times[r - WARMUP_RUNS] = ms;
                    finalCounter = c;
                }
            }
            writer.printf(Locale.US, "W3,%s,%s,%d,%.4f,%d,%d,%d\n", variant, s, n, median(times),
                    finalCounter.steps, finalCounter.moves, finalCounter.comparisons);
        }
    }

    private static void runW4(PrintWriter writer, int n) {
        double[] times = new double[RUNS];
        OpCounter finalCounter = new OpCounter();

        for (int r = 0; r < WARMUP_RUNS + RUNS; r++) {
            MinHeap heap = new MinHeap(n);
            Random initRand = new Random(42);
            int[] vals = new int[n];
            for (int i = 0; i < n; i++) vals[i] = initRand.nextInt();

            OpCounter c = new OpCounter();
            heap.setOpCounter(c);

            long start = System.nanoTime();
            for (int v : vals) heap.insert(v);
            int prev = Integer.MIN_VALUE;
            for (int i = 0; i < n; i++) {
                int min = heap.extractMin();
                if (min < prev) throw new AssertionError("Heap sort violation");
                prev = min;
            }
            long elapsed = System.nanoTime() - start;

            if (r >= WARMUP_RUNS) {
                double ms = Math.max(elapsed / 1e6, 0.001);
                times[r - WARMUP_RUNS] = ms;
                finalCounter = c;
            }
        }
        writer.printf(Locale.US, "W4,-,MinHeap,%d,%.4f,%d,%d,%d\n", n, median(times),
                finalCounter.steps, finalCounter.moves, finalCounter.comparisons);
    }
}