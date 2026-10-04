# In-Memory Workload Engine & Data Structures (DAA Assignment 2)

**Course:** Design and Analysis of Algorithms  
**Instructor:** Taubakabyl Nurlybek  
**Student:** Shyngys Zaikenov (Group: SE-2523)

---

## Project Overview
This project is an in-memory workload engine evaluating custom, low-level data structures implemented completely from scratch without java.util collections:
- DynamicArray: Primitive int-based dynamically resizing array with 2x capacity growth.
- MyLinkedList: Primitive int-based doubly-linked list with head and tail pointers.
- MinHeap: Array-based binary min-heap supporting bubble-up, bubble-down, and Floyd's bottom-up O(n) buildHeap.

Every structure includes honest operational counters (steps, moves, comparisons) integrated directly into method calls to analyze algorithmic cost versus physical execution time.

---

## System Requirements
- Java: JDK 17 or higher
- Build System: Apache Maven 3.8+
- IDE: IntelliJ IDEA

---

## Build, Test, and Execution Instructions

### 1. Compile the Project & Run JUnit 5 Tests
To run all correctness, randomized property, edge case, and heap invariant tests:
```bash
mvn clean test
```

### 2. Run Benchmarks
To run the automated 4-workload benchmark with JVM JIT warmup and median measurement across 5 runs:
```bash
mvn compile exec:java -Dexec.mainClass="BenchmarkRunner"
```
Output: Generates results/results.csv.

### 3. Generate Evaluation Plots
To generate high-resolution PNG charts comparing execution time and physical operational counts side-by-side:
```bash
mvn compile exec:java -Dexec.mainClass="PlotGenerator"
```
Output: Saves 5 two-panel PNG charts directly into results/plots/.

---

## Repository & Branch Structure
- main: Release-ready code tagged with v1.0.
- feature/metrics: OpCounter instrumentation and common IntList interface.
- feature/array: DynamicArray implementation and JUnit tests.
- feature/list: MyLinkedList implementation and JUnit tests.
- feature/heap: MinHeap implementation and JUnit tests.

---

## GitHub Repository
- URL: https://github.com/Cauramein/Design-and-analysis-of-algorythms
- Branch: master
- Release Tag: v1.0