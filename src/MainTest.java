import exceptions.InvalidDataFormatException;
import exceptions.InvalidFileFormatException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Classe di test per l'algoritmo Quality Threshold con menu interattivo.
 */
public class MainTest {

    private static Scanner scanner = new Scanner(System.in);

    /**
     * Metodo main per testare l'algoritmo QT.
     *
     * @param args argomenti della linea di comando
     */
    public static void main(String[] args) {
        boolean continua = true;

        while (continua) {
            printMenu();
            int scelta = getIntInput("Scelta: ");

            switch (scelta) {
                case 1:
                    usaDatasetHardcoded();
                    break;
                case 2:
                    caricaDatasetCSV();
                    break;
                case 3:
                    caricaClusterSalvato();
                    break;
                case 0:
                    System.out.println("Arrivederci!");
                    continua = false;
                    break;
                default:
                    System.out.println("Opzione non valida. Riprova.");
            }

            if (continua && scelta != 0) {
                System.out.println("\n" + "=".repeat(50) + "\n");
            }
        }

        scanner.close();
    }

    /**
     * Stampa il menu principale.
     */
    private static void printMenu() {
        System.out.println("=== QT Clustering System ===");
        System.out.println("1. Usa dataset PlayTennis (hardcoded)");
        System.out.println("2. Carica dataset da CSV");
        System.out.println("3. Carica cluster salvato");
        System.out.println("0. Esci");
        System.out.println();
    }

    /**
     * Opzione 1: Usa dataset PlayTennis hardcoded.
     */
    private static void usaDatasetHardcoded() {
        try {
            System.out.println("\n--- Dataset PlayTennis (hardcoded) ---");
            Data data = new Data();
            System.out.println(data);

            eseguiClustering(data);
        } catch (Exception e) {
            System.err.println("Errore: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Opzione 2: Carica dataset da file CSV.
     */
    private static void caricaDatasetCSV() {
        System.out.println("\n--- Carica Dataset da CSV ---");
        System.out.print("Inserisci path file CSV: ");
        String csvPath = scanner.nextLine().trim();

        try {
            System.out.println("Caricamento in corso...");
            Data data = new Data(csvPath);

            System.out.println("✓ Dataset caricato: " + data.getNumberOfExamples() + " esempi, "
                    + data.getNumberOfExplanatoryAttributes() + " attributi");

            // Mostra attributi rilevati
            System.out.println("\nAttributi rilevati:");
            Attribute[] attributes = data.getAttributeSchema();
            for (int i = 0; i < attributes.length; i++) {
                Attribute attr = attributes[i];
                String tipo = (attr instanceof DiscreteAttribute) ? "discrete" : "continuous";
                System.out.println("  - " + attr.getName() + " (" + tipo + ")");

                if (attr instanceof DiscreteAttribute) {
                    DiscreteAttribute dAttr = (DiscreteAttribute) attr;
                    System.out.print("    Valori: {");
                    for (int j = 0; j < Math.min(5, dAttr.getNumberOfDistinctValues()); j++) {
                        System.out.print(data.getValue(j, i));
                        if (j < Math.min(4, dAttr.getNumberOfDistinctValues() - 1)) {
                            System.out.print(", ");
                        }
                    }
                    if (dAttr.getNumberOfDistinctValues() > 5) {
                        System.out.print(", ...");
                    }
                    System.out.println("}");
                }
            }

            System.out.println();
            eseguiClustering(data);

        } catch (IOException e) {
            System.err.println("✗ Errore I/O: " + e.getMessage());
        } catch (InvalidDataFormatException e) {
            System.err.println("✗ Errore formato dati: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✗ Errore: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Opzione 3: Carica cluster precedentemente salvato.
     */
    private static void caricaClusterSalvato() {
        System.out.println("\n--- Carica Cluster Salvato ---");
        System.out.print("Inserisci path file .dmp: ");
        String dmpPath = scanner.nextLine().trim();

        System.out.println("Il file cluster richiede un dataset di riferimento.");
        System.out.println("1. Usa dataset PlayTennis (hardcoded)");
        System.out.println("2. Carica dataset da CSV");
        int scelta = getIntInput("Scelta dataset: ");

        try {
            Data data;
            if (scelta == 1) {
                data = new Data();
            } else if (scelta == 2) {
                System.out.print("Inserisci path file CSV: ");
                String csvPath = scanner.nextLine().trim();
                data = new Data(csvPath);
                System.out.println("✓ Dataset caricato: " + data.getNumberOfExamples() + " esempi");
            } else {
                System.out.println("Scelta non valida.");
                return;
            }

            System.out.println("Caricamento cluster da file...");
            ClusterSet clusterSet = new ClusterSet(dmpPath, data);

            System.out.println("✓ Caricato: " + clusterSet.getNumClusters() + " cluster");
            System.out.println("\n--- Cluster Caricati ---");
            System.out.println(clusterSet.toString(data));

        } catch (IOException e) {
            System.err.println("✗ Errore I/O: " + e.getMessage());
        } catch (InvalidFileFormatException e) {
            System.err.println("✗ Errore formato file: " + e.getMessage());
        } catch (InvalidDataFormatException e) {
            System.err.println("✗ Errore formato dati: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✗ Errore: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Esegue il clustering sul dataset fornito.
     *
     * @param data dataset su cui eseguire clustering
     */
    private static void eseguiClustering(Data data) {
        double radius = getDoubleInput("Inserisci radius: ");

        System.out.println("\nComputazione in corso...");
        QTMiner qt = new QTMiner(radius);
        int numClusters = qt.compute(data);

        System.out.println("✓ Trovati " + numClusters + " cluster\n");
        System.out.println("--- Risultati Clustering ---");
        System.out.println(qt.getC().toString(data));

        // Chiedi se salvare
        System.out.print("\nSalvare risultati? (s/n): ");
        String risposta = scanner.nextLine().trim().toLowerCase();

        if (risposta.equals("s") || risposta.equals("si") || risposta.equals("yes")) {
            System.out.print("Inserisci nome file output (es. clusters.dmp): ");
            String outputPath = scanner.nextLine().trim();

            try {
                qt.getC().save(outputPath, radius);
                System.out.println("✓ Cluster salvati in " + outputPath);
            } catch (IOException e) {
                System.err.println("✗ Errore salvataggio: " + e.getMessage());
            }
        }
    }

    /**
     * Legge un input intero dall'utente con validazione.
     *
     * @param prompt messaggio da visualizzare
     * @return numero intero inserito
     */
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Errore: inserisci un numero intero valido.");
            }
        }
    }

    /**
     * Legge un input double dall'utente con validazione.
     *
     * @param prompt messaggio da visualizzare
     * @return numero double inserito
     */
    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Errore: inserisci un numero valido.");
            }
        }
    }
}
