package tests;

import data.Data;
import mining.Cluster;
import mining.ClusterSet;
import mining.QTMiner;
import java.io.File;

/**
 * Test operazioni su Cluster e ClusterSet: creazione, iterazione, serializzazione.
 */
public class TestClusterOperations {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=== Test Operazioni Cluster ===\n");

        testClusterCreation();
        testClusterSetIteration();
        testClusterIteration();
        testClusterSerialization();
        testClusterComparison();

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
     * Test creazione cluster base.
     */
    private static void testClusterCreation() {
        System.out.println("Test 1: Creazione cluster");
        try {
            Data data = new Data();
            data.Tuple centroid = data.getItemSet(0);
            Cluster cluster = new Cluster(centroid);

            assertNotNull(cluster, "Cluster non dovrebbe essere null");
            assertEqual(cluster.getSize(), 0, "Cluster nuovo dovrebbe essere vuoto");

            cluster.addData(0);
            cluster.addData(1);
            assertEqual(cluster.getSize(), 2, "Cluster dovrebbe avere 2 tuple");

            System.out.println("  ✓ Cluster creato e tuple aggiunte correttamente\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    /**
     * Test iterazione su ClusterSet.
     */
    private static void testClusterSetIteration() {
        System.out.println("Test 2: Iterazione su ClusterSet");
        try {
            Data data = new Data();
            QTMiner miner = new QTMiner(2.0);
            miner.compute(data);
            ClusterSet clusters = miner.getC();

            int count = 0;
            for (Cluster cluster : clusters) {
                assertNotNull(cluster, "Cluster nell'iterazione non dovrebbe essere null");
                assertTrue(cluster.getSize() > 0, "Cluster dovrebbe contenere tuple");
                count++;
            }

            assertTrue(count > 0, "Dovrebbero esserci cluster da iterare");
            System.out.println("  ✓ Iterati " + count + " cluster correttamente\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    /**
     * Test iterazione su singolo Cluster.
     */
    private static void testClusterIteration() {
        System.out.println("Test 3: Iterazione su Cluster");
        try {
            Data data = new Data();
            data.Tuple centroid = data.getItemSet(0);
            Cluster cluster = new Cluster(centroid);

            cluster.addData(0);
            cluster.addData(1);
            cluster.addData(2);

            int count = 0;
            for (Integer tupleId : cluster) {
                assertNotNull(tupleId, "Tuple ID non dovrebbe essere null");
                assertTrue(tupleId >= 0, "Tuple ID dovrebbe essere >= 0");
                count++;
            }

            assertEqual(count, 3, "Dovrebbero esserci 3 tuple");
            System.out.println("  ✓ Iterate " + count + " tuple nel cluster\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    /**
     * Test serializzazione e deserializzazione cluster.
     */
    private static void testClusterSerialization() {
        System.out.println("Test 4: Serializzazione cluster");
        try {
            Data data = new Data();
            double radius = 2.0;
            QTMiner miner = new QTMiner(radius);
            int originalNumClusters = miner.compute(data);
            ClusterSet originalClusters = miner.getC();

            // Serializza
            String filename = "test_clusters.dmp";
            originalClusters.save(filename, radius);

            // Verifica file creato
            File dumpFile = new File(filename);
            assertTrue(dumpFile.exists(), "File .dmp dovrebbe essere creato");

            // Deserializza
            ClusterSet loadedClusters = new ClusterSet(filename, data);

            // Conta cluster caricati
            int loadedCount = 0;
            for (Cluster c : loadedClusters) {
                loadedCount++;
            }

            assertEqual(loadedCount, originalNumClusters, "Numero cluster caricati dovrebbe corrispondere");

            // Cleanup
            dumpFile.delete();

            System.out.println("  ✓ Serializzazione/deserializzazione OK (" + originalNumClusters + " cluster)\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    /**
     * Test ordinamento cluster per dimensione.
     */
    private static void testClusterComparison() {
        System.out.println("Test 5: Ordinamento cluster per dimensione");
        try {
            Data data = new Data();
            QTMiner miner = new QTMiner(2.0);
            miner.compute(data);
            ClusterSet clusters = miner.getC();

            // I cluster dovrebbero essere ordinati in ordine decrescente di dimensione
            int previousSize = Integer.MAX_VALUE;
            for (Cluster cluster : clusters) {
                int currentSize = cluster.getSize();
                assertTrue(currentSize <= previousSize,
                        "Cluster dovrebbero essere ordinati per dimensione decrescente");
                previousSize = currentSize;
            }

            System.out.println("  ✓ Cluster correttamente ordinati per dimensione\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    // Utility methods
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Asserzione fallita: " + message);
        }
    }

    private static void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError("Asserzione fallita: " + message);
        }
    }

    private static void assertEqual(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " - Atteso: " + expected + ", Ottenuto: " + actual);
        }
    }
}
