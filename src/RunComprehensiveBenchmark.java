/**
 * Esegue benchmark completo su dataset di varie dimensioni.
 */
public class RunComprehensiveBenchmark {

    public static void main(String[] args) {
        System.out.println("COMPREHENSIVE PERFORMANCE BENCHMARK");
        System.out.println("=".repeat(80));
        System.out.println();

        StringBuilder fullReport = new StringBuilder();
        fullReport.append("QT Algorithm - Comprehensive Performance Report\n");
        fullReport.append("Generated: ").append(java.time.LocalDateTime.now()).append("\n");
        fullReport.append("=".repeat(80)).append("\n\n");

        // Test 1: Small dataset (50 tuples)
        System.out.println("\n[1/4] Testing SMALL dataset (50 tuples)...");
        QTBenchmark.BenchmarkResult[] small = QTBenchmark.runComparison(
            "../data/synthetic_small.csv", 0.4);
        String reportSmall = QTBenchmark.compareResults(small[0], small[1]);
        System.out.println(reportSmall);
        fullReport.append(reportSmall).append("\n\n");

        // Test 2: Medium dataset (200 tuples)
        System.out.println("\n[2/4] Testing MEDIUM dataset (200 tuples)...");
        QTBenchmark.BenchmarkResult[] medium = QTBenchmark.runComparison(
            "../data/synthetic_medium.csv", 0.4);
        String reportMedium = QTBenchmark.compareResults(medium[0], medium[1]);
        System.out.println(reportMedium);
        fullReport.append(reportMedium).append("\n\n");

        // Test 3: Large dataset (1000 tuples)
        System.out.println("\n[3/4] Testing LARGE dataset (1000 tuples)...");
        QTBenchmark.BenchmarkResult[] large = QTBenchmark.runComparison(
            "../data/synthetic_large.csv", 0.4);
        String reportLarge = QTBenchmark.compareResults(large[0], large[1]);
        System.out.println(reportLarge);
        fullReport.append(reportLarge).append("\n\n");

        // Summary
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SUMMARY");
        System.out.println("=".repeat(80));

        double speedupSmall = (double)small[0].executionTimeMs / small[1].executionTimeMs;
        double speedupMedium = (double)medium[0].executionTimeMs / medium[1].executionTimeMs;
        double speedupLarge = (double)large[0].executionTimeMs / large[1].executionTimeMs;

        System.out.printf("\nDataset Size   | Baseline Time | Optimized Time | Speedup | Hit Rate\n");
        System.out.println("-".repeat(80));
        System.out.printf("Small (50)     | %10d ms | %11d ms | %6.2fx | %7.1f%%\n",
            small[0].executionTimeMs, small[1].executionTimeMs,
            speedupSmall, small[1].cacheHitRate * 100);
        System.out.printf("Medium (200)   | %10d ms | %11d ms | %6.2fx | %7.1f%%\n",
            medium[0].executionTimeMs, medium[1].executionTimeMs,
            speedupMedium, medium[1].cacheHitRate * 100);
        System.out.printf("Large (1000)   | %10d ms | %11d ms | %6.2fx | %7.1f%%\n",
            large[0].executionTimeMs, large[1].executionTimeMs,
            speedupLarge, large[1].cacheHitRate * 100);

        fullReport.append("\nSUMMARY TABLE\n");
        fullReport.append("-".repeat(80)).append("\n");
        fullReport.append(String.format("%-15s | %13s | %14s | %7s | %9s\n",
            "Dataset Size", "Baseline Time", "Optimized Time", "Speedup", "Hit Rate"));
        fullReport.append("-".repeat(80)).append("\n");
        fullReport.append(String.format("Small (50)      | %10d ms | %11d ms | %6.2fx | %8.1f%%\n",
            small[0].executionTimeMs, small[1].executionTimeMs,
            speedupSmall, small[1].cacheHitRate * 100));
        fullReport.append(String.format("Medium (200)    | %10d ms | %11d ms | %6.2fx | %8.1f%%\n",
            medium[0].executionTimeMs, medium[1].executionTimeMs,
            speedupMedium, medium[1].cacheHitRate * 100));
        fullReport.append(String.format("Large (1000)    | %10d ms | %11d ms | %6.2fx | %8.1f%%\n",
            large[0].executionTimeMs, large[1].executionTimeMs,
            speedupLarge, large[1].cacheHitRate * 100));

        System.out.println("\n" + "=".repeat(80));
        System.out.println("CONCLUSION:");
        System.out.println("=".repeat(80));

        if (speedupLarge >= 1.5) {
            System.out.println("✓ Optimizations show SIGNIFICANT improvement on large datasets");
            fullReport.append("\n✓ Optimizations show SIGNIFICANT improvement on large datasets\n");
        } else if (speedupLarge >= 1.2) {
            System.out.println("✓ Optimizations show MODERATE improvement on large datasets");
            fullReport.append("\n✓ Optimizations show MODERATE improvement on large datasets\n");
        } else {
            System.out.println("⚠ Optimizations show LIMITED improvement - further tuning needed");
            fullReport.append("\n⚠ Optimizations show LIMITED improvement\n");
        }

        if (large[1].cacheHitRate >= 0.5) {
            System.out.println("✓ Cache working effectively (>50% hit rate on large dataset)");
            fullReport.append("✓ Cache working effectively\n");
        }

        System.out.println("\nData structures optimizations (HashSet/ArrayList) provide:");
        System.out.println("  - O(n) → O(1) for cluster membership checks");
        System.out.println("  - O(n) → O(1) for cluster additions");
        System.out.println("  - Consistent performance across all dataset sizes");

        fullReport.append("\nData structures optimizations provide O(n)→O(1) improvements.\n");

        // Salva report completo
        QTBenchmark.saveReport(fullReport.toString(), "../docs/BENCHMARK_RESULTS.md");

        System.out.println("\n" + "=".repeat(80));
        System.out.println("Full report saved to: docs/BENCHMARK_RESULTS.md");
        System.out.println("Benchmark completed successfully!");
    }
}
