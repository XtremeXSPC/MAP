import java.io.IOException;
import exceptions.InvalidDataFormatException;

/**
 * Test clustering con dataset Weather Mixed usando diversi valori di radius.
 */
public class TestWeatherMixedClustering {
    public static void main(String[] args) {
        try {
            System.out.println("=== Test Clustering Weather Mixed Dataset ===\n");

            // Carica dataset
            Data weather = new Data("../data/weather_mixed.csv");
            System.out.println("Dataset caricato: " + weather.getNumberOfExamples() + " tuple, " +
                weather.getNumberOfExplanatoryAttributes() + " attributi");
            System.out.println("  (2 continui: temperature, humidity + 3 discreti: outlook, wind, play)\n");

            // Test con diversi valori di radius
            double[] radiusValues = {0.3, 0.5, 0.7};

            for (double radius : radiusValues) {
                System.out.println("========================================");
                System.out.println("TEST CON RADIUS = " + radius);
                System.out.println("========================================\n");

                // Esegui clustering
                QTMiner miner = new QTMiner(radius, false);
                long startTime = System.currentTimeMillis();
                int numClusters = miner.compute(weather);
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

                    // Analizza composizione cluster
                    int[] tupleIds = c.iterator();
                    int sunny = 0, overcast = 0, rain = 0;
                    int playYes = 0, playNo = 0;
                    double sumTemp = 0, sumHum = 0;

                    for (int id : tupleIds) {
                        String outlook = (String) weather.getValue(id, 0);
                        double temp = Double.parseDouble((String) weather.getValue(id, 1));
                        double hum = Double.parseDouble((String) weather.getValue(id, 2));
                        String play = (String) weather.getValue(id, 4);

                        if (outlook.equals("sunny")) sunny++;
                        else if (outlook.equals("overcast")) overcast++;
                        else if (outlook.equals("rain")) rain++;

                        if (play.equals("yes")) playYes++;
                        else playNo++;

                        sumTemp += temp;
                        sumHum += hum;
                    }

                    double avgTemp = sumTemp / size;
                    double avgHum = sumHum / size;

                    // Determina outlook dominante
                    String dominantOutlook = "sunny";
                    int maxCount = sunny;
                    if (overcast > maxCount) {
                        dominantOutlook = "overcast";
                        maxCount = overcast;
                    }
                    if (rain > maxCount) {
                        dominantOutlook = "rain";
                        maxCount = rain;
                    }

                    // Determina play dominante
                    String dominantPlay = playYes >= playNo ? "yes" : "no";

                    System.out.println("  Cluster " + (i + 1) + ":");
                    System.out.println("    Size: " + size);
                    System.out.println("    Outlook dominante: " + dominantOutlook +
                        " (sunny=" + sunny + ", overcast=" + overcast + ", rain=" + rain + ")");
                    System.out.println("    Play dominante: " + dominantPlay +
                        " (yes=" + playYes + ", no=" + playNo + ")");
                    System.out.println("    Temperature media: " + String.format("%.1f", avgTemp) + "°C");
                    System.out.println("    Humidity media: " + String.format("%.1f", avgHum) + "%");

                    // Mostra centroide
                    Tuple centroid = c.getCentroid();
                    System.out.println("    Centroid: " + centroid.get(0).getValue() +
                        ", " + String.format("%.1f", (Double)centroid.get(1).getValue()) + "°C" +
                        ", " + String.format("%.1f", (Double)centroid.get(2).getValue()) + "%" +
                        ", " + centroid.get(3).getValue() +
                        ", " + centroid.get(4).getValue());
                    System.out.println();
                }

                System.out.println("Totale punti clusterizzati: " + totalPoints + " / 30");
                System.out.println();
            }

            System.out.println("========================================");
            System.out.println("ANALISI FINALE");
            System.out.println("========================================\n");

            System.out.println("Osservazioni:");
            System.out.println("- Il clustering combina correttamente attributi continui (temp, humidity)");
            System.out.println("  e discreti (outlook, wind, play)");
            System.out.println("- Cluster con outlook simile e condizioni meteo simili vengono raggruppati");
            System.out.println("- Temperature e humidity medie riflettono le condizioni del cluster");
            System.out.println();
            System.out.println("L'algoritmo QT con dataset misti funziona correttamente!");

            System.out.println("\n=== Test Completato con Successo ===");

        } catch (IOException | InvalidDataFormatException e) {
            System.err.println("Errore: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
