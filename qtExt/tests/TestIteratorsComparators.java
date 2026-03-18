package tests;

import java.util.logging.Logger;
import mining.Cluster;
import mining.ClusterSet;
import mining.QTMiner;

/**
 * Test Iteratori e Comparatori - Dimostra le funzionalità di iterazione e ordinamento su
 * ClusterSet, Cluster e DiscreteAttribute.
 */
public class TestIteratorsComparators {
    // Logger.
    private static final Logger logger = Logger.getLogger(TestIteratorsComparators.class.getName());

    public static void main(String[] args) {
        logger.info(() -> String.valueOf("=== Test Iteratori e Comparatori ===\n"));

        // Crea dataset.
        data.Data data = new data.Data();
        logger.info(() -> String.valueOf("Dataset caricato: " + data.getNumberOfExamples() + " tuple\n"));

        // Stampa dataset con numerazione 0-based (come specifica QT05).
        logger.info(() -> String.valueOf(data));

        // Test clustering con radius=2 (come esempio specifica).
        logger.info(() -> String.valueOf("Insert radius (>0)="));
        double radius = 2.0;
        logger.info(() -> String.valueOf(radius));

        QTMiner miner = new QTMiner(radius);
        int numClusters = miner.compute(data);

        logger.info(() -> String.valueOf("Number of clusters:" + numClusters));

        // Ottengo cluster (ordinati automaticamente per dimensione).
        ClusterSet clusters = miner.getC();

        // DEMO 1: Enhanced for-loop su ClusterSet (Iterable<Cluster>).
        logger.info(() -> String.valueOf("\n=== DEMO 1: Enhanced for-loop su ClusterSet ==="));
        int index = 1;
        for (Cluster cluster : clusters) {
            logger.info(index++ + ":Centroid=" + cluster.toString());
            logger.info("  Size: " + cluster.getSize());
        }

        // DEMO 2: Enhanced for-loop su Cluster (Iterable<Integer>).
        logger.info(() -> String.valueOf("\n=== DEMO 2: Enhanced for-loop su Cluster ==="));
        index = 1;
        for (Cluster cluster : clusters) {
            StringBuilder line = new StringBuilder();
            line.append(index++).append(":Tuple IDs: [");
            boolean first = true;
            for (Integer tupleId : cluster) {
                if (!first) {
                    line.append(", ");
                }
                line.append(tupleId);
                first = false;
            }
            line.append("]");
            logger.info(line.toString());
        }

        // DEMO 3: Output dettagliato (come specifica QT05 pagina 2).
        logger.info(() -> String.valueOf("\n=== Output Completo (formato specifica QT05) ==="));
        logger.info(() -> String.valueOf(clusters.toString(data)));

        // DEMO 4: Iteratore su DiscreteAttribute.
        logger.info(() -> String.valueOf("=== DEMO 4: Iteratore su DiscreteAttribute ==="));
        logger.info(() -> String.valueOf("Valori attributo 'Outlook' (TreeSet ordinato):"));
        data.DiscreteAttribute outlook = (data.DiscreteAttribute) data.getExplanatoryAttribute(0);
        StringBuilder valuesLine = new StringBuilder();
        valuesLine.append("  {");
        boolean first = true;
        for (String value : outlook) {
            if (!first) {
                valuesLine.append(", ");
            }
            valuesLine.append(value);
            first = false;
        }
        valuesLine.append("}");
        logger.info(valuesLine.toString());

        logger.info(() -> String.valueOf("\n=== Test Completato ==="));
    }
}
