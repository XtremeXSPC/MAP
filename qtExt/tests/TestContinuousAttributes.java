package tests;

import data.Data;
import mining.ClusterSet;
import mining.QTMiner;

/**
 * Test Attributi Continui - Dimostra il clustering con attributi continui usando generics
 * e RTTI per discriminare tra DiscreteItem e ContinuousItem.
 */
public class TestContinuousAttributes {
    public static void main(String[] args) {
        System.out.println("=== Test Attributi Continui ===\n");

        // Crea dataset con Temperature continuo
        Data data = new Data();

        // Stampa dataset (dovrebbe mostrare temperature numeriche)
        System.out.println(data);

        // Test clustering con radius=2 (come specifica QT06 pagina 3)
        System.out.println("Insert radius (>0):2");
        double radius = 2.0;

        QTMiner miner = new QTMiner(radius);
        int numClusters = miner.compute(data);

        System.out.println("Number of clusters:" + numClusters);
        ClusterSet clusters = miner.getC();
        System.out.println(clusters.toString(data));

        // Test con radius=3 (come specifica QT06 pagina 3-4)
        System.out.println("New execution?(y/n)y");
        System.out.println("Insert radius (>0):3");
        radius = 3.0;

        miner = new QTMiner(radius);
        numClusters = miner.compute(data);

        System.out.println("Number of clusters:" + numClusters);
        clusters = miner.getC();
        System.out.println(clusters.toString(data));

        System.out.println("New execution?(y/n)n");

        System.out.println("\n=== Test Completato ===");
    }
}
