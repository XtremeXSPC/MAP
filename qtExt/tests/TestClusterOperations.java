package tests;

import java.io.File;
import java.util.logging.Logger;
import mining.Cluster;
import mining.ClusterSet;
import mining.QTMiner;

/**
 * Test operazioni su Cluster e ClusterSet: creazione, iterazione, serializzazione.
 */
public class TestClusterOperations {
    // Logger.
    private static final Logger logger = Logger.getLogger(TestClusterOperations.class.getName());

    // Contatori test.
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        logger.info(() -> String.valueOf("=== Test Operazioni Cluster ===\n"));

        testClusterCreation();
        testClusterSetIteration();
        testClusterIteration();
        testClusterSerialization();
        testDumpExtensionNormalization();
        testClusterComparison();

        logger.info(() -> String.valueOf("\n=== Risultati Test ==="));
        logger.info(() -> String.valueOf("Test passati: " + testsPassed));
        logger.info(() -> String.valueOf("Test falliti: " + testsFailed));

        if (testsFailed == 0) {
            logger.info(() -> String.valueOf("\n✓ Tutti i test sono passati!"));
        } else {
            logger.info(() -> String.valueOf("\n✗ Alcuni test sono falliti"));
            System.exit(1);
        }
    }

    /**
     * Test creazione cluster base.
     */
    private static void testClusterCreation() {
        logger.info(() -> String.valueOf("Test 1: Creazione cluster"));
        try {
            data.Data data = new data.Data();
            data.Tuple centroid = data.getItemSet(0);
            Cluster cluster = new Cluster(centroid);

            assertNotNull(cluster, "Cluster non dovrebbe essere null");
            assertEqual(cluster.getSize(), 0, "Cluster nuovo dovrebbe essere vuoto");

            cluster.addData(0);
            cluster.addData(1);
            assertEqual(cluster.getSize(), 2, "Cluster dovrebbe avere 2 tuple");

            logger.info(() -> String.valueOf("  ✓ Cluster creato e tuple aggiunte correttamente\n"));
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    /**
     * Test iterazione su ClusterSet.
     */
    private static void testClusterSetIteration() {
        logger.info(() -> String.valueOf("Test 2: Iterazione su ClusterSet"));
        try {
            data.Data data = new data.Data();
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
            logger.info("  ✓ Iterati " + count + " cluster correttamente\n");
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    /**
     * Test iterazione su singolo Cluster.
     */
    private static void testClusterIteration() {
        logger.info(() -> String.valueOf("Test 3: Iterazione su Cluster"));
        try {
            data.Data data = new data.Data();
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
            logger.info("  ✓ Iterate " + count + " tuple nel cluster\n");
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    /**
     * Test serializzazione e deserializzazione cluster.
     */
    @SuppressWarnings("unused")
    private static void testClusterSerialization() {
        logger.info(() -> String.valueOf("Test 4: Serializzazione cluster"));
        try {
            data.Data data = new data.Data();
            double radius = 2.0;
            QTMiner miner = new QTMiner(radius);
            int originalNumClusters = miner.compute(data);
            ClusterSet originalClusters = miner.getC();

            // Serializza.
            String filename = "test_clusters.dmp";
            originalClusters.save(filename, radius);

            // Verifica file creato.
            File dumpFile = new File(filename);
            assertTrue(dumpFile.exists(), "File .dmp dovrebbe essere creato");

            // Deserializza.
            ClusterSet loadedClusters = new ClusterSet(filename, data);

            // Conta cluster caricati.
            int loadedCount = 0;
            for (Cluster c : loadedClusters) {
                loadedCount++;
            }

            assertEqual(loadedCount, originalNumClusters, "Numero cluster caricati dovrebbe corrispondere");

            // Cleanup.
            dumpFile.delete();

            logger.info(() -> String
                    .valueOf("  ✓ Serializzazione/deserializzazione OK (" + originalNumClusters + " cluster)\n"));
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    /**
     * Test normalizzazione automatica dell'estensione .dmp.
     */
    private static void testDumpExtensionNormalization() {
        logger.info(() -> String.valueOf("Test 5: Normalizzazione estensione .dmp"));

        File expectedFile = new File("test_complete_save.dmp");
        File duplicateFile = new File("test_complete_save.dmp.dmp");

        try {
            data.Data data = new data.Data();
            QTMiner miner = new QTMiner(2.0);
            miner.compute(data);

            miner.saveComplete("test_complete_save.dmp");

            assertTrue(expectedFile.exists(), "Il file .dmp atteso dovrebbe esistere");
            assertTrue(!duplicateFile.exists(), "Non dovrebbe essere creato un file .dmp.dmp");

            QTMiner loadedWithExtension = new QTMiner("test_complete_save.dmp");
            QTMiner loadedWithoutExtension = new QTMiner("test_complete_save");

            assertNotNull(loadedWithExtension.getData(), "Il caricamento con estensione dovrebbe includere il dataset");
            assertNotNull(loadedWithoutExtension.getData(),
                    "Il caricamento senza estensione dovrebbe includere il dataset");

            logger.info(() -> String.valueOf("  ✓ Gestione estensione .dmp corretta in save/load\n"));
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        } finally {
            expectedFile.delete();
            duplicateFile.delete();
        }
    }

    /**
     * Test ordinamento cluster per dimensione.
     */
    private static void testClusterComparison() {
        logger.info(() -> String.valueOf("Test 6: Ordinamento cluster per dimensione"));
        try {
            data.Data data = new data.Data();
            QTMiner miner = new QTMiner(2.0);
            miner.compute(data);
            ClusterSet clusters = miner.getC();

            // I cluster dovrebbero essere ordinati in ordine decrescente di dimensione.
            int previousSize = Integer.MAX_VALUE;
            for (Cluster cluster : clusters) {
                int currentSize = cluster.getSize();
                assertTrue(currentSize <= previousSize,
                        "Cluster dovrebbero essere ordinati per dimensione decrescente");
                previousSize = currentSize;
            }

            logger.info(() -> String.valueOf("  ✓ Cluster correttamente ordinati per dimensione\n"));
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    // Utility methods.
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
