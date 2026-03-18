package tests;

import java.util.logging.Logger;
import mining.ClusterSet;
import mining.QTMiner;

/**
 * Test completo dell'algoritmo QT con diversi valori di radius.
 * Verifica correttezza dei cluster prodotti e proprietà dell'algoritmo.
 */
public class TestQTAlgorithm {
    // Logger.
    private static final Logger logger = Logger.getLogger(TestQTAlgorithm.class.getName());

    // Contatori test.
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        logger.info(() -> String.valueOf("=== Test Algoritmo QT ===\n"));

        testRadiusZero();
        testRadiusLarge();
        testAllTuplesClustered();
        testClusterQuality();
        testDeterminism();

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
     * Test con radius=0: ogni tupla dovrebbe formare un cluster separato.
     */
    private static void testRadiusZero() {
        logger.info(() -> String.valueOf("Test 1: Radius = 0 (cluster precisi)"));
        try {
            data.Data data = new data.Data();
            QTMiner miner = new QTMiner(0.0);
            int numClusters = miner.compute(data);

            // Con radius=0, tuple identiche formano un cluster, altre stanno da sole.
            // Nel dataset PlayTennis dovremmo avere molti cluster piccoli.
            assertTrue(numClusters > 0, "Dovrebbero esserci cluster");
            assertTrue(numClusters <= data.getNumberOfExamples(), "Non più cluster che tuple");

            logger.info(() -> String.valueOf("  ✓ Prodotti " + numClusters + " cluster\n"));
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    /**
     * Test con radius elevato: dovrebbero esserci pochi cluster grandi.
     */
    private static void testRadiusLarge() {
        logger.info(() -> String.valueOf("Test 2: Radius elevato (cluster aggregati)"));
        try {
            data.Data data = new data.Data();
            QTMiner miner = new QTMiner(5.0);
            int numClusters = miner.compute(data);

            // Con radius alto, le tuple dovrebbero aggregarsi in pochi cluster.
            assertTrue(numClusters > 0, "Dovrebbe esserci almeno un cluster");

            logger.info(() -> String.valueOf("  ✓ Prodotti " + numClusters + " cluster (aggregazione OK)\n"));
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    /**
     * Test che tutte le tuple vengano clusterizzate.
     */
    private static void testAllTuplesClustered() {
        logger.info(() -> String.valueOf("Test 3: Tutte le tuple clusterizzate"));
        try {
            data.Data data = new data.Data();
            QTMiner miner = new QTMiner(2.0);
            miner.compute(data);
            ClusterSet clusters = miner.getC();

            // Conta tuple totali nei cluster.
            int totalTuples = 0;
            for (mining.Cluster cluster : clusters) {
                totalTuples += cluster.getSize();
            }

            assertEqual(totalTuples, data.getNumberOfExamples(), "Tutte le tuple devono essere clusterizzate");

            logger.info("  ✓ Tutte le " + totalTuples + " tuple sono clusterizzate\n");
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    /**
     * Test qualità cluster: ogni tupla deve essere entro radius dal centroide.
     */
    private static void testClusterQuality() {
        logger.info(() -> String.valueOf("Test 4: Qualità cluster (radius rispettato)"));
        try {
            data.Data data = new data.Data();
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
                        logger.info(() -> String
                                .valueOf("  ✗ Tupla " + tupleId + " a distanza " + distance + " > " + radius));
                    }
                }
            }

            assertTrue(allWithinRadius, "Tutte le tuple devono essere entro radius");
            logger.info(() -> String.valueOf("  ✓ Tutte le tuple rispettano il radius\n"));
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    /**
     * Test determinismo: stesso input produce stesso output.
     */
    private static void testDeterminism() {
        logger.info(() -> String.valueOf("Test 5: Determinismo algoritmo"));
        try {
            data.Data data = new data.Data();
            double radius = 2.0;

            QTMiner miner1 = new QTMiner(radius);
            int numClusters1 = miner1.compute(data);

            QTMiner miner2 = new QTMiner(radius);
            int numClusters2 = miner2.compute(data);

            assertEqual(numClusters1, numClusters2, "Stesso radius deve produrre stesso numero cluster");

            logger.info(() -> String.valueOf(
                    "  ✓ Algoritmo deterministico (" + numClusters1 + " cluster in entrambe le esecuzioni)\n"));
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    // Utility methods per asserzioni.
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Asserzione fallita: " + message);
        }
    }

    private static void assertEqual(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " - Atteso: " + expected + ", Ottenuto: " + actual);
        }
    }
}
