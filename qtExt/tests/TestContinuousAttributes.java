package tests;

import java.util.logging.Logger;
import mining.ClusterSet;
import mining.QTMiner;

/**
 * Test Attributi Continui - Dimostra il clustering con attributi continui usando generics
 * e RTTI per discriminare tra DiscreteItem e ContinuousItem.
 */
public class TestContinuousAttributes {
    // Logger.
    private static final Logger logger = Logger.getLogger(TestContinuousAttributes.class.getName());

    public static void main(String[] args) {
        logger.info(() -> String.valueOf("=== Test Attributi Continui ===\n"));

        // Crea dataset con Temperature continuo.
        data.Data data = new data.Data();

        // Stampa dataset (dovrebbe mostrare temperature numeriche).
        logger.info(() -> String.valueOf(data));

        // Test clustering con radius=2 (come specifica QT06 pagina 3).
        logger.info(() -> String.valueOf("Insert radius (>0):2"));
        double radius = 2.0;

        QTMiner miner = new QTMiner(radius);
        int numClusters = miner.compute(data);

        logger.info("Number of clusters:" + numClusters);
        ClusterSet clusters = miner.getC();
        logger.info(clusters.toString(data));

        // Test con radius=3 (come specifica QT06 pagina 3-4).
        logger.info(() -> String.valueOf("New execution?(y/n)y"));
        logger.info(() -> String.valueOf("Insert radius (>0):3"));
        radius = 3.0;

        miner = new QTMiner(radius);
        numClusters = miner.compute(data);

        logger.info("Number of clusters:" + numClusters);
        clusters = miner.getC();
        logger.info(clusters.toString(data));

        logger.info(() -> String.valueOf("New execution?(y/n)n"));

        logger.info(() -> String.valueOf("\n=== Test Completato ==="));
    }
}
