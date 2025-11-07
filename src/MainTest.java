import exceptions.InvalidDataFormatException;
import exceptions.InvalidFileFormatException;
import java.io.IOException;
import keyboardinput.Keyboard;

/**
 * Classe di test per l'algoritmo Quality Threshold con menu interattivo. Utilizza la classe
 * Keyboard per gestione robusta dell'input utente (QT03).
 */
public class MainTest {

    /**
     * Metodo main per testare l'algoritmo QT.
     *
     * @param args argomenti della linea di comando
     */
    public static void main(String[] args) {
        boolean continua = true;

        System.out.println("=== QT Clustering System (con Keyboard Input - QT03) ===\n");

        while (continua) {
            printMenu();
            int scelta = getIntInput("Scelta: ", 0, 3);

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
                    System.out.println("\nArrivederci!");
                    continua = false;
                    break;
                default:
                    System.out.println("Opzione non valida. Riprova.");
            }

            if (continua && scelta != 0) {
                System.out.println("\n" + "=".repeat(50) + "\n");
            }
        }
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
        String csvPath = Keyboard.readString().trim();

        try {
            System.out.println("Caricamento in corso...");
            Data data = new Data(csvPath);

            System.out.println("✓ Dataset caricato: " + data.getNumberOfExamples() + " esempi, "
                    + data.getNumberOfExplanatoryAttributes() + " attributi");

            // Mostra attributi rilevati
            System.out.println("\nAttributi rilevati:");
            java.util.List<Attribute> attributes = data.getAttributeSchema();
            for (Attribute attr : attributes) {
                String tipo = (attr instanceof DiscreteAttribute) ? "discrete" : "continuous";
                System.out.println("  - " + attr.getName() + " (" + tipo + ")");

                if (attr instanceof DiscreteAttribute) {
                    DiscreteAttribute dAttr = (DiscreteAttribute) attr;
                    System.out.print("    Valori: {");
                    int count = 0;
                    for (String value : dAttr) {
                        if (count > 0)
                            System.out.print(", ");
                        System.out.print(value);
                        count++;
                        if (count >= 5)
                            break; // Mostra max 5 valori
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
        String dmpPath = Keyboard.readString().trim();

        System.out.println("Il file cluster richiede un dataset di riferimento.");
        System.out.println("1. Usa dataset PlayTennis (hardcoded)");
        System.out.println("2. Carica dataset da CSV");
        int scelta = getIntInput("Scelta dataset: ", 1, 2);

        try {
            Data data;
            if (scelta == 1) {
                data = new Data();
            } else if (scelta == 2) {
                System.out.print("Inserisci path file CSV: ");
                String csvPath = Keyboard.readString().trim();
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
        double radius = getPositiveDoubleInput("Inserisci radius (> 0): ");

        System.out.println("\nComputazione in corso...");
        QTMiner qt = new QTMiner(radius);
        int numClusters = qt.compute(data);

        System.out.println("✓ Trovati " + numClusters + " cluster\n");
        System.out.println("--- Risultati Clustering ---");
        System.out.println(qt.getC().toString(data));

        // Chiedi se salvare
        System.out.print("\nSalvare risultati? (s/n): ");
        String risposta = Keyboard.readWord();
        if (risposta != null) {
            risposta = risposta.trim().toLowerCase();
        }

        if (risposta != null
                && (risposta.equals("s") || risposta.equals("si") || risposta.equals("yes"))) {
            System.out.print("Inserisci nome file output (es. clusters.dmp): ");
            String outputPath = Keyboard.readString().trim();

            try {
                qt.getC().save(outputPath, radius);
                System.out.println("✓ Cluster salvati in " + outputPath);
            } catch (IOException e) {
                System.err.println("✗ Errore salvataggio: " + e.getMessage());
            }
        }
    }

    /**
     * Legge un input intero dall'utente con validazione e range check. Utilizza la classe Keyboard
     * per gestione robusta degli errori.
     *
     * @param prompt messaggio da visualizzare
     * @param min valore minimo accettabile (incluso)
     * @param max valore massimo accettabile (incluso)
     * @return numero intero inserito nel range [min, max]
     */
    private static int getIntInput(String prompt, int min, int max) {
        int value;
        int previousErrorCount = Keyboard.getErrorCount();

        while (true) {
            System.out.print(prompt);
            value = Keyboard.readInt();

            // Controlla se c'è stato un errore di parsing
            if (Keyboard.getErrorCount() > previousErrorCount) {
                System.out.println("✗ Errore: inserisci un numero intero valido.");
                previousErrorCount = Keyboard.getErrorCount();
                continue;
            }

            // Controlla se è nel range valido
            if (value < min || value > max) {
                System.out.println(
                        "✗ Errore: il valore deve essere compreso tra " + min + " e " + max + ".");
                continue;
            }

            // Input valido
            return value;
        }
    }

    /**
     * Legge un input double positivo dall'utente con validazione. Utilizza la classe Keyboard per
     * gestione robusta degli errori. Il valore deve essere strettamente maggiore di 0.
     *
     * @param prompt messaggio da visualizzare
     * @return numero double > 0
     */
    private static double getPositiveDoubleInput(String prompt) {
        double value;
        int previousErrorCount = Keyboard.getErrorCount();

        while (true) {
            System.out.print(prompt);
            value = Keyboard.readDouble();

            // Controlla se c'è stato un errore di parsing (restituisce NaN)
            if (Double.isNaN(value)) {
                System.out.println("✗ Errore: inserisci un numero valido (decimale).");
                previousErrorCount = Keyboard.getErrorCount();
                continue;
            }

            // Controlla se Keyboard ha registrato un errore
            if (Keyboard.getErrorCount() > previousErrorCount) {
                System.out.println("✗ Errore: inserisci un numero valido (decimale).");
                previousErrorCount = Keyboard.getErrorCount();
                continue;
            }

            // Controlla se è positivo (> 0)
            if (value <= 0) {
                System.out
                        .println("✗ Errore: il radius deve essere maggiore di 0 (valore inserito: "
                                + value + ").");
                continue;
            }

            // Input valido
            System.out.println("✓ Radius impostato a: " + value);
            return value;
        }
    }
}
