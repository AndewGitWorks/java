package services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Сервис бенчмаркинга
 * Позволяет сравнить производительность одно- и многопоточного выполнения вычислений
 */
public class BenchmarkService {

    public static class BenchmarkResult {
        private final int operations;
        private final int threadCount;
        private final long sequentialMillis;
        private final long parallelMillis;

        public BenchmarkResult(int operations, int threadCount,
                               long sequentialMillis, long parallelMillis) {
            this.operations = operations;
            this.threadCount = threadCount;
            this.sequentialMillis = sequentialMillis;
            this.parallelMillis = parallelMillis;
        }

        public int getOperations() {
            return operations;
        }

        public int getThreadCount() {
            return threadCount;
        }

        public long getSequentialMillis() {
            return sequentialMillis;
        }

        public long getParallelMillis() {
            return parallelMillis;
        }

        public double getSpeedup() {
            return parallelMillis == 0 ? 0 : (double) sequentialMillis / parallelMillis;
        }

        public String format() {
            return String.format(
                    "Операций: %d\n" +
                    "Потоков: %d\n" +
                    "Последовательно: %d мс\n" +
                    "Многопоточно: %d мс\n" +
                    "Ускорение: %.2fx",
                    operations,
                    threadCount,
                    sequentialMillis,
                    parallelMillis,
                    getSpeedup()
            );
        }

        @Override
        public String toString() {
            return format();
        }
    }

    /**
     * Запускает сравнение производительности последовательного и многопоточного режимов
     */
    public BenchmarkResult compareBenchmark(int operations, int threadCount) {
        long sequential = benchmarkSequential(operations);
        long parallel = benchmarkParallel(operations, threadCount);
        return new BenchmarkResult(operations, threadCount, sequential, parallel);
    }

    /**
     * Последовательное выполнение вычислительной нагрузки
     */
    public long benchmarkSequential(int operations) {
        long start = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            performHeavyCalculation(i);
        }
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    }

    /**
     * Многопоточное выполнение вычислительной нагрузки
     */
    public long benchmarkParallel(int operations, int threadCount) {
        ExecutorService executor = Executors.newFixedThreadPool(Math.max(1, threadCount));
        try {
            List<Callable<Void>> tasks = new ArrayList<>();
            for (int i = 0; i < operations; i++) {
                final int index = i;
                tasks.add(() -> {
                    performHeavyCalculation(index);
                    return null;
                });
            }

            long start = System.nanoTime();
            List<Future<Void>> futures = executor.invokeAll(tasks);
            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    throw new RuntimeException("Ошибка при выполнении задачи бенчмарка", e.getCause());
                }
            }
            return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * Симулирует тяжелую вычислительную операцию
     */
    private void performHeavyCalculation(int seed) {
        double value = seed + 1.0;
        for (int i = 0; i < 18_000; i++) {
            value = Math.sqrt(value * 1.0001 + i);
            value = value % 10000;
        }
    }
}
