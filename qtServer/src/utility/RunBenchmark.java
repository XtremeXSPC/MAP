package utility;

/**
 * Esegue benchmark completo su dataset di varie dimensioni.
 */
public class RunBenchmark {

    public static void main(String[] args) {
        System.out.println("BENCHMARK PRESTAZIONI COMPLETO");
        System.out.println("=".repeat(80));
        System.out.println();

        StringBuilder fullReport = new StringBuilder();
        fullReport.append("Algoritmo QT - Report Prestazioni Completo\n");
        fullReport.append("Generato: ").append(java.time.LocalDateTime.now()).append("\n");
        fullReport.append("=".repeat(80)).append("\n\n");

        // Test 1: Small dataset (50 tuples)
        System.out.println("\n[1/4] Test dataset PICCOLO (50 tuple)...");
        QTBenchmark.BenchmarkResult[] small = QTBenchmark.runComparison("../data/synthetic_small.csv", 0.4);
        String reportSmall = QTBenchmark.compareResults(small[0], small[1]);
        System.out.println(reportSmall);
        fullReport.append(reportSmall).append("\n\n");

        // Test 2: Medium dataset (200 tuples)
        System.out.println("\n[2/4] Test dataset MEDIO (200 tuple)...");
        QTBenchmark.BenchmarkResult[] medium = QTBenchmark.runComparison("../data/synthetic_medium.csv", 0.4);
        String reportMedium = QTBenchmark.compareResults(medium[0], medium[1]);
        System.out.println(reportMedium);
        fullReport.append(reportMedium).append("\n\n");

        // Test 3: Large dataset (1000 tuples)
        System.out.println("\n[3/4] Test dataset GRANDE (1000 tuple)...");
        QTBenchmark.BenchmarkResult[] large = QTBenchmark.runComparison("../data/synthetic_large.csv", 0.4);
        String reportLarge = QTBenchmark.compareResults(large[0], large[1]);
        System.out.println(reportLarge);
        fullReport.append(reportLarge).append("\n\n");

        // Summary
        System.out.println("\n" + "=".repeat(80));
        System.out.println("RIEPILOGO");
        System.out.println("=".repeat(80));

        double speedupSmall = (double) small[0].executionTimeMs / small[1].executionTimeMs;
        double speedupMedium = (double) medium[0].executionTimeMs / medium[1].executionTimeMs;
        double speedupLarge = (double) large[0].executionTimeMs / large[1].executionTimeMs;

        System.out.printf("\nDim. Dataset   | Tempo Base    | Tempo Ottim.   | Speedup | Hit Rate\n");
        System.out.println("-".repeat(80));
        System.out.printf("Piccolo (50)   | %10d ms | %11d ms | %6.2fx | %7.1f%%\n", small[0].executionTimeMs,
                small[1].executionTimeMs, speedupSmall, small[1].cacheHitRate * 100);
        System.out.printf("Medio (200)    | %10d ms | %11d ms | %6.2fx | %7.1f%%\n", medium[0].executionTimeMs,
                medium[1].executionTimeMs, speedupMedium, medium[1].cacheHitRate * 100);
        System.out.printf("Grande (1000)  | %10d ms | %11d ms | %6.2fx | %7.1f%%\n", large[0].executionTimeMs,
                large[1].executionTimeMs, speedupLarge, large[1].cacheHitRate * 100);

        fullReport.append("\nTABELLA RIEPILOGO\n");
        fullReport.append("-".repeat(80)).append("\n");
        fullReport.append(String.format("%-15s | %13s | %14s | %7s | %9s\n", "Dim. Dataset", "Tempo Base",
                "Tempo Ottim.", "Speedup", "Hit Rate"));
        fullReport.append("-".repeat(80)).append("\n");
        fullReport.append(String.format("Piccolo (50)    | %10d ms | %11d ms | %6.2fx | %8.1f%%\n",
                small[0].executionTimeMs, small[1].executionTimeMs, speedupSmall, small[1].cacheHitRate * 100));
        fullReport.append(String.format("Medio (200)     | %10d ms | %11d ms | %6.2fx | %8.1f%%\n",
                medium[0].executionTimeMs, medium[1].executionTimeMs, speedupMedium, medium[1].cacheHitRate * 100));
        fullReport.append(String.format("Grande (1000)   | %10d ms | %11d ms | %6.2fx | %8.1f%%\n",
                large[0].executionTimeMs, large[1].executionTimeMs, speedupLarge, large[1].cacheHitRate * 100));

        System.out.println("\n" + "=".repeat(80));
        System.out.println("CONCLUSIONI:");
        System.out.println("=".repeat(80));

        if (speedupLarge >= 1.5) {
            System.out.println("✓ Le ottimizzazioni mostrano miglioramenti SIGNIFICATIVI su dataset grandi");
            fullReport.append("\n✓ Le ottimizzazioni mostrano miglioramenti SIGNIFICATIVI su dataset grandi\n");
        } else if (speedupLarge >= 1.2) {
            System.out.println("✓ Le ottimizzazioni mostrano miglioramenti MODERATI su dataset grandi");
            fullReport.append("\n✓ Le ottimizzazioni mostrano miglioramenti MODERATI su dataset grandi\n");
        } else {
            System.out.println(
                    "⚠ Le ottimizzazioni mostrano miglioramenti LIMITATI - necessaria ulteriore ottimizzazione");
            fullReport.append("\n⚠ Le ottimizzazioni mostrano miglioramenti LIMITATI\n");
        }

        if (large[1].cacheHitRate >= 0.5) {
            System.out.println("✓ Cache funzionante efficacemente (>50% hit rate su dataset grande)");
            fullReport.append("✓ Cache funzionante efficacemente\n");
        }

        System.out.println("\nLe ottimizzazioni delle strutture dati (HashSet/ArrayList) forniscono:");
        System.out.println("  - O(n) → O(1) per controlli appartenenza cluster");
        System.out.println("  - O(n) → O(1) per aggiunte ai cluster");
        System.out.println("  - Prestazioni consistenti su tutti i dataset");

        fullReport.append("\nLe ottimizzazioni delle strutture dati forniscono miglioramenti O(n) → O(1).\n");

        // Salva report completo
        QTBenchmark.saveReport(fullReport.toString(), "../docs/BENCHMARK_RESULTS.md");

        System.out.println("\n" + "=".repeat(80));
        System.out.println("Report completo salvato in: docs/BENCHMARK_RESULTS.md");
        System.out.println("Benchmark completato con successo!");
    }
}
