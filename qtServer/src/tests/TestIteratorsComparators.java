package tests;

import data.Data;
import data.DiscreteAttribute;
import mining.Cluster;
import mining.ClusterSet;
import mining.QTMiner;

/**
 * Test Iteratori e Comparatori - Dimostra le funzionalità di iterazione
 * e ordinamento su ClusterSet, Cluster e DiscreteAttribute.
 */
public class TestIteratorsComparators {
    public static void main(String[] args) {
        System.out.println("=== Test Iteratori e Comparatori ===\n");

        // Crea dataset
        Data data = new Data();
        System.out.println("Dataset caricato: " + data.getNumberOfExamples() + " tuple\n");

        // Stampa dataset con numerazione 0-based (come specifica QT05)
        System.out.println(data);

        // Test clustering con radius=2 (come esempio specifica)
        System.out.println("Insert radius (>0)=");
        double radius = 2.0;
        System.out.println(radius);

        QTMiner miner = new QTMiner(radius);
        int numClusters = miner.compute(data);

        System.out.println("Number of clusters:" + numClusters);

        // Ottengo cluster (ordinati automaticamente per dimensione)
        ClusterSet clusters = miner.getC();

        // DEMO 1: Enhanced for-loop su ClusterSet (Iterable<Cluster>)
        System.out.println("\n=== DEMO 1: Enhanced for-loop su ClusterSet ===");
        int index = 1;
        for (Cluster cluster : clusters) {
            System.out.println(index++ + ":Centroid=" + cluster.toString());
            System.out.println("  Size: " + cluster.getSize());
        }

        // DEMO 2: Enhanced for-loop su Cluster (Iterable<Integer>)
        System.out.println("\n=== DEMO 2: Enhanced for-loop su Cluster ===");
        index = 1;
        for (Cluster cluster : clusters) {
            System.out.print(index++ + ":Tuple IDs: [");
            boolean first = true;
            for (Integer tupleId : cluster) {
                if (!first)
                    System.out.print(", ");
                System.out.print(tupleId);
                first = false;
            }
            System.out.println("]");
        }

        // DEMO 3: Output dettagliato (come specifica QT05 pagina 2)
        System.out.println("\n=== Output Completo (formato specifica QT05) ===");
        System.out.println(clusters.toString(data));

        // DEMO 4: Iteratore su DiscreteAttribute
        System.out.println("=== DEMO 4: Iteratore su DiscreteAttribute ===");
        System.out.println("Valori attributo 'Outlook' (TreeSet ordinato):");
        DiscreteAttribute outlook = (DiscreteAttribute) data.getExplanatoryAttribute(0);
        System.out.print("  {");
        boolean first = true;
        for (String value : outlook) {
            if (!first)
                System.out.print(", ");
            System.out.print(value);
            first = false;
        }
        System.out.println("}");

        System.out.println("\n=== Test Completato ===");
    }
}
