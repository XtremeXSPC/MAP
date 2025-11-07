import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Sistema di benchmarking per l'algoritmo Quality Threshold.
 * Misura tempo esecuzione, calcoli distanza, uso memoria.
 */
public class QTBenchmark {

    /**
     * Risultato di un singolo benchmark.
     */
    public static class BenchmarkResult {
        public String datasetName;
        public int numTuples;
        public int numAttributes;
        public double radius;
        public boolean optimizationsEnabled;

        public long executionTimeMs;
        public int numClusters;
        public int distanceCalculations;
        public int cacheHits;
        public int cacheMisses;
        public double cacheHitRate;
        public long memoryUsedMB;

        @Override
        public String toString() {
            return String.format(
                "Dataset: %s (%d tuples, %d attrs) | Radius: %.2f | Opt: %s\n" +
                "  Time: %d ms | Clusters: %d | Calculations: %d\n" +
                "  Cache: hits=%d, misses=%d, rate=%.1f%% | Memory: %d MB",
                datasetName, numTuples, numAttributes, radius,
                optimizationsEnabled ? "ON" : "OFF",
                executionTimeMs, numClusters, distanceCalculations,
                cacheHits, cacheMisses, cacheHitRate * 100,
                memoryUsedMB
            );
        }
    }

    /**
     * Esegue benchmark su un dataset con parametri specificati.
     *
     * @param datasetPath path dataset CSV
     * @param radius raggio clustering
     * @param enableOptimizations flag ottimizzazioni
     * @return risultato benchmark
     */
    public static BenchmarkResult runBenchmark(String datasetPath, double radius,
                                               boolean enableOptimizations) {
        BenchmarkResult result = new BenchmarkResult();

        try {
            // Carica dataset
            Data data;
            if (datasetPath.equals("hardcoded")) {
                data = new Data();
                result.datasetName = "PlayTennis";
            } else {
                data = new Data(datasetPath);
                result.datasetName = new File(datasetPath).getName();
            }

            result.numTuples = data.getNumberOfExamples();
            result.numAttributes = data.getNumberOfExplanatoryAttributes();
            result.radius = radius;
            result.optimizationsEnabled = enableOptimizations;

            // Misura memoria prima
            Runtime runtime = Runtime.getRuntime();
            runtime.gc();  // Suggest garbage collection
            long memBefore = runtime.totalMemory() - runtime.freeMemory();

            // Esegui clustering e misura tempo
            long startTime = System.currentTimeMillis();

            QTMiner miner = new QTMiner(radius, enableOptimizations);
            result.numClusters = miner.compute(data);

            long endTime = System.currentTimeMillis();
            result.executionTimeMs = endTime - startTime;

            // Raccogli statistiche cache
            DistanceCache cache = miner.getDistanceCache();
            if (cache != null) {
                result.distanceCalculations = cache.getCalculations();
                result.cacheHits = cache.getHitCount();
                result.cacheMisses = cache.getMissCount();
                result.cacheHitRate = cache.getHitRate();
            } else {
                result.distanceCalculations = 0;
                result.cacheHits = 0;
                result.cacheMisses = 0;
                result.cacheHitRate = 0.0;
            }

            // Misura memoria dopo
            runtime.gc();
            long memAfter = runtime.totalMemory() - runtime.freeMemory();
            result.memoryUsedMB = (memAfter - memBefore) / (1024 * 1024);

        } catch (Exception e) {
            System.err.println("Errore benchmark: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Esegue benchmark comparativo con/senza ottimizzazioni.
     *
     * @param datasetPath path dataset
     * @param radius raggio
     * @return array con 2 risultati [without_opt, with_opt]
     */
    public static BenchmarkResult[] runComparison(String datasetPath, double radius) {
        System.out.println("Benchmark: " + datasetPath + " (radius=" + radius + ")");

        System.out.print("  Running WITHOUT optimizations... ");
        BenchmarkResult withoutOpt = runBenchmark(datasetPath, radius, false);
        System.out.println("Done (" + withoutOpt.executionTimeMs + " ms)");

        System.out.print("  Running WITH optimizations... ");
        BenchmarkResult withOpt = runBenchmark(datasetPath, radius, true);
        System.out.println("Done (" + withOpt.executionTimeMs + " ms)");

        return new BenchmarkResult[] { withoutOpt, withOpt };
    }

    /**
     * Genera report comparativo tra due risultati.
     *
     * @param baseline risultato baseline (senza ottimizzazioni)
     * @param optimized risultato ottimizzato
     * @return stringa report
     */
    public static String compareResults(BenchmarkResult baseline, BenchmarkResult optimized) {
        double speedup = (double) baseline.executionTimeMs / optimized.executionTimeMs;
        double timeReduction = ((baseline.executionTimeMs - optimized.executionTimeMs) * 100.0)
                                / baseline.executionTimeMs;

        int calcReduction = baseline.distanceCalculations > 0
            ? ((baseline.distanceCalculations - optimized.distanceCalculations) * 100)
              / baseline.distanceCalculations
            : 0;

        StringBuilder report = new StringBuilder();
        report.append("\n");
        report.append("=".repeat(70)).append("\n");
        report.append("PERFORMANCE COMPARISON REPORT\n");
        report.append("=".repeat(70)).append("\n\n");

        report.append(String.format("Dataset: %s (%d tuples, %d attributes)\n",
            baseline.datasetName, baseline.numTuples, baseline.numAttributes));
        report.append(String.format("Radius: %.2f\n", baseline.radius));
        report.append(String.format("Clusters Found: %d\n\n", baseline.numClusters));

        report.append("-".repeat(70)).append("\n");
        report.append(String.format("%-30s %15s %15s %12s\n",
            "Metric", "Baseline", "Optimized", "Improvement"));
        report.append("-".repeat(70)).append("\n");

        report.append(String.format("%-30s %12d ms %12d ms %10.1f%%\n",
            "Execution Time",
            baseline.executionTimeMs,
            optimized.executionTimeMs,
            timeReduction));

        report.append(String.format("%-30s %15.2fx\n",
            "Speedup", speedup));

        if (optimized.distanceCalculations > 0) {
            report.append(String.format("%-30s %15d %15d %10d%%\n",
                "Distance Calculations",
                baseline.distanceCalculations,
                optimized.distanceCalculations,
                calcReduction));

            report.append(String.format("%-30s %15s %15d\n",
                "Cache Hits",
                "N/A",
                optimized.cacheHits));

            report.append(String.format("%-30s %15s %12.1f%%\n",
                "Cache Hit Rate",
                "N/A",
                optimized.cacheHitRate * 100));
        }

        report.append(String.format("%-30s %12d MB %12d MB\n",
            "Memory Used",
            baseline.memoryUsedMB,
            optimized.memoryUsedMB));

        report.append("-".repeat(70)).append("\n\n");

        // Valutazione
        report.append("EVALUATION:\n");
        if (speedup >= 2.0) {
            report.append("  ✓ EXCELLENT - 2x+ speedup achieved!\n");
        } else if (speedup >= 1.5) {
            report.append("  ✓ GOOD - Significant performance improvement\n");
        } else if (speedup >= 1.2) {
            report.append("  ✓ MODERATE - Noticeable improvement\n");
        } else {
            report.append("  ⚠ MINIMAL - Limited improvement\n");
        }

        if (optimized.cacheHitRate >= 0.8) {
            report.append("  ✓ Cache efficiency: Excellent (>80% hit rate)\n");
        } else if (optimized.cacheHitRate >= 0.5) {
            report.append("  ✓ Cache efficiency: Good (>50% hit rate)\n");
        }

        report.append("\n");
        report.append("=".repeat(70)).append("\n");

        return report.toString();
    }

    /**
     * Salva report su file.
     *
     * @param report contenuto report
     * @param filename nome file output
     */
    public static void saveReport(String report, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(report);
            System.out.println("Report salvato in: " + filename);
        } catch (IOException e) {
            System.err.println("Errore salvataggio report: " + e.getMessage());
        }
    }

    /**
     * Main per eseguire benchmark standalone.
     */
    public static void main(String[] args) {
        System.out.println("QT Algorithm - Performance Benchmark Suite");
        System.out.println("=".repeat(70));
        System.out.println();

        // Test 1: PlayTennis (small dataset)
        BenchmarkResult[] results1 = runComparison("hardcoded", 0.3);
        String report1 = compareResults(results1[0], results1[1]);
        System.out.println(report1);

        // Test 2: Weather dataset (if exists)
        if (new File("../data/weather.csv").exists()) {
            BenchmarkResult[] results2 = runComparison("../data/weather.csv", 0.5);
            String report2 = compareResults(results2[0], results2[1]);
            System.out.println(report2);
            saveReport(report2, "benchmark_weather.txt");
        }

        System.out.println("\nBenchmark suite completed!");
    }
}
