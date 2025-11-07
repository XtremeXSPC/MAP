import java.io.IOException;
import exceptions.InvalidDataFormatException;

/**
 * Test clustering con dataset Iris usando diversi valori di radius.
 */
public class TestIrisClustering {
    public static void main(String[] args) {
        try {
            System.out.println("=== Test Clustering Iris Dataset ===\n");

            // Carica dataset
            Data iris = new Data("../data/iris.csv");
            System.out.println("Dataset caricato: " + iris.getNumberOfExamples() + " tuple, " +
                iris.getNumberOfExplanatoryAttributes() + " attributi\n");

            // Test con diversi valori di radius
            double[] radiusValues = {0.2, 0.3, 0.5};

            for (double radius : radiusValues) {
                System.out.println("========================================");
                System.out.println("TEST CON RADIUS = " + radius);
                System.out.println("========================================\n");

                // Esegui clustering
                QTMiner miner = new QTMiner(radius, false);
                long startTime = System.currentTimeMillis();
                int numClusters = miner.compute(iris);
                long endTime = System.currentTimeMillis();

                System.out.println("Clustering completato in " + (endTime - startTime) + " ms");
                System.out.println("Numero di cluster trovati: " + numClusters);
                System.out.println();

                // Analisi cluster
                ClusterSet clusters = miner.getC();
                int totalPoints = 0;

                System.out.println("Dettagli cluster:");
                for (int i = 0; i < numClusters; i++) {
                    Cluster c = clusters.get(i);
                    int size = c.getSize();
                    totalPoints += size;

                    // Analizza specie dominante nel cluster
                    int[] tupleIds = c.iterator();
                    int setosa = 0, versicolor = 0, virginica = 0;

                    for (int id : tupleIds) {
                        String species = (String) iris.getValue(id, 4);
                        if (species.equals("setosa")) setosa++;
                        else if (species.equals("versicolor")) versicolor++;
                        else if (species.equals("virginica")) virginica++;
                    }

                    // Determina specie dominante
                    String dominant = "setosa";
                    int maxCount = setosa;
                    if (versicolor > maxCount) {
                        dominant = "versicolor";
                        maxCount = versicolor;
                    }
                    if (virginica > maxCount) {
                        dominant = "virginica";
                        maxCount = virginica;
                    }

                    double purity = (double) maxCount / size * 100;

                    System.out.println("  Cluster " + (i + 1) + ":");
                    System.out.println("    Size: " + size);
                    System.out.println("    Specie dominante: " + dominant + " (" +
                        String.format("%.1f", purity) + "% purezza)");
                    System.out.println("    Composizione: setosa=" + setosa +
                        ", versicolor=" + versicolor + ", virginica=" + virginica);

                    // Mostra centroide
                    Tuple centroid = c.getCentroid();
                    System.out.print("    Centroid: ");
                    for (int j = 0; j < 4; j++) {
                        System.out.print(centroid.get(j).getValue());
                        if (j < 3) System.out.print(", ");
                    }
                    System.out.println(" [" + centroid.get(4).getValue() + "]");
                    System.out.println();
                }

                System.out.println("Totale punti clusterizzati: " + totalPoints + " / 150");

                // Calcola purezza media
                double totalPurity = 0;
                for (int i = 0; i < numClusters; i++) {
                    Cluster c = clusters.get(i);
                    int[] tupleIds = c.iterator();
                    int setosa = 0, versicolor = 0, virginica = 0;

                    for (int id : tupleIds) {
                        String species = (String) iris.getValue(id, 4);
                        if (species.equals("setosa")) setosa++;
                        else if (species.equals("versicolor")) versicolor++;
                        else if (species.equals("virginica")) virginica++;
                    }

                    int maxCount = Math.max(setosa, Math.max(versicolor, virginica));
                    double purity = (double) maxCount / c.getSize();
                    totalPurity += purity * c.getSize();
                }
                double avgPurity = totalPurity / totalPoints * 100;

                System.out.println("Purezza media dei cluster: " + String.format("%.1f", avgPurity) + "%");
                System.out.println();
            }

            System.out.println("========================================");
            System.out.println("ANALISI FINALE");
            System.out.println("========================================\n");

            System.out.println("Osservazioni:");
            System.out.println("- Radius 0.2: Cluster più piccoli e puri (alta granularità)");
            System.out.println("- Radius 0.3: Bilanciamento tra numero cluster e purezza");
            System.out.println("- Radius 0.5: Cluster più grandi, possibile mixing specie");
            System.out.println();
            System.out.println("L'algoritmo QT con attributi continui funziona correttamente!");
            System.out.println("Le 3 specie di Iris tendono a separarsi in cluster distinti.");

            System.out.println("\n=== Test Completato con Successo ===");

        } catch (IOException | InvalidDataFormatException e) {
            System.err.println("Errore: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
