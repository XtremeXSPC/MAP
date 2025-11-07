package tests;

import data.Data;
import mining.ClusterSet;
import mining.QTMiner;

/**
 * Test completo dell'algoritmo QT con diversi valori di radius.
 * Verifica correttezza dei cluster prodotti e proprietà dell'algoritmo.
 */
public class TestQTAlgorithm {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=== Test Algoritmo QT ===\n");

        testRadiusZero();
        testRadiusLarge();
        testAllTuplesClustered();
        testClusterQuality();
        testDeterminism();

        System.out.println("\n=== Risultati Test ===");
        System.out.println("Test passati: " + testsPassed);
        System.out.println("Test falliti: " + testsFailed);

        if (testsFailed == 0) {
            System.out.println("\n✓ Tutti i test sono passati!");
        } else {
            System.out.println("\n✗ Alcuni test sono falliti");
        }
    }

    /**
     * Test con radius=0: ogni tupla dovrebbe formare un cluster separato.
     */
    private static void testRadiusZero() {
        System.out.println("Test 1: Radius = 0 (cluster precisi)");
        try {
            Data data = new Data();
            QTMiner miner = new QTMiner(0.0);
            int numClusters = miner.compute(data);

            // Con radius=0, tuple identiche formano un cluster, altre stanno da sole
            // Nel dataset PlayTennis dovremmo avere molti cluster piccoli
            assertTrue(numClusters > 0, "Dovrebbero esserci cluster");
            assertTrue(numClusters <= data.getNumberOfExamples(),
                      "Non più cluster che tuple");

            System.out.println("  ✓ Prodotti " + numClusters + " cluster\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    /**
     * Test con radius elevato: dovrebbero esserci pochi cluster grandi.
     */
    private static void testRadiusLarge() {
        System.out.println("Test 2: Radius elevato (cluster aggregati)");
        try {
            Data data = new Data();
            QTMiner miner = new QTMiner(5.0);
            int numClusters = miner.compute(data);

            // Con radius alto, le tuple dovrebbero aggregarsi in pochi cluster
            assertTrue(numClusters > 0, "Dovrebbe esserci almeno un cluster");

            System.out.println("  ✓ Prodotti " + numClusters + " cluster (aggregazione OK)\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    /**
     * Test che tutte le tuple vengano clusterizzate.
     */
    private static void testAllTuplesClustered() {
        System.out.println("Test 3: Tutte le tuple clusterizzate");
        try {
            Data data = new Data();
            QTMiner miner = new QTMiner(2.0);
            miner.compute(data);
            ClusterSet clusters = miner.getC();

            // Conta tuple totali nei cluster
            int totalTuples = 0;
            for (mining.Cluster cluster : clusters) {
                totalTuples += cluster.getSize();
            }

            assertEqual(totalTuples, data.getNumberOfExamples(),
                       "Tutte le tuple devono essere clusterizzate");

            System.out.println("  ✓ Tutte le " + totalTuples + " tuple sono clusterizzate\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    /**
     * Test qualità cluster: ogni tupla deve essere entro radius dal centroide.
     */
    private static void testClusterQuality() {
        System.out.println("Test 4: Qualità cluster (radius rispettato)");
        try {
            Data data = new Data();
            double radius = 1.5;
            QTMiner miner = new QTMiner(radius);
            miner.compute(data);
            ClusterSet clusters = miner.getC();

            boolean allWithinRadius = true;
            for (mining.Cluster cluster : clusters) {
                data.Tuple centroid = cluster.getCentroid();
                for (Integer tupleId : cluster) {
                    data.Tuple tuple = data.getItemSet(tupleId);
                    double distance = centroid.getDistance(tuple);

                    if (distance > radius + 0.0001) { // tolleranza numerica
                        allWithinRadius = false;
                        System.out.println("  ✗ Tupla " + tupleId + " a distanza " +
                                         distance + " > " + radius);
                    }
                }
            }

            assertTrue(allWithinRadius, "Tutte le tuple devono essere entro radius");
            System.out.println("  ✓ Tutte le tuple rispettano il radius\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    /**
     * Test determinismo: stesso input produce stesso output.
     */
    private static void testDeterminism() {
        System.out.println("Test 5: Determinismo algoritmo");
        try {
            Data data = new Data();
            double radius = 2.0;

            QTMiner miner1 = new QTMiner(radius);
            int numClusters1 = miner1.compute(data);

            QTMiner miner2 = new QTMiner(radius);
            int numClusters2 = miner2.compute(data);

            assertEqual(numClusters1, numClusters2,
                       "Stesso radius deve produrre stesso numero cluster");

            System.out.println("  ✓ Algoritmo deterministico (" + numClusters1 +
                             " cluster in entrambe le esecuzioni)\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    // Utility methods per asserzioni
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Asserzione fallita: " + message);
        }
    }

    private static void assertEqual(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " - Atteso: " + expected +
                                   ", Ottenuto: " + actual);
        }
    }
}
