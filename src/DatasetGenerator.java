import java.io.*;
import java.util.Random;

/**
 * Generatore di dataset sintetici per testing performance. Crea file CSV con dimensioni e
 * caratteristiche configurabili.
 */
public class DatasetGenerator {

    private static final String[] CATEGORIES_A = {"A1", "A2", "A3", "A4", "A5"};
    private static final String[] CATEGORIES_B = {"B1", "B2", "B3"};
    private static final String[] CATEGORIES_C = {"C1", "C2", "C3", "C4"};
    private static final String[] CATEGORIES_D = {"D1", "D2"};
    private static final String[] CATEGORIES_E = {"yes", "no"};

    /**
     * Genera dataset sintetico con parametri specificati.
     *
     * @param filename nome file output
     * @param numTuples numero tuple da generare
     * @param numAttributes numero attributi
     * @param seed seed random per riproducibilità
     */
    public static void generateDataset(String filename, int numTuples, int numAttributes,
            long seed) {
        Random random = new Random(seed);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Scrivi header
            for (int i = 0; i < numAttributes; i++) {
                if (i > 0)
                    writer.write(",");
                writer.write("Attr" + (i + 1));
            }
            writer.write("\n");

            // Genera tuple
            for (int i = 0; i < numTuples; i++) {
                for (int j = 0; j < numAttributes; j++) {
                    if (j > 0)
                        writer.write(",");

                    // Seleziona categoria basata sull'indice attributo
                    String[] categories = selectCategories(j);
                    int index = random.nextInt(categories.length);
                    writer.write(categories[index]);
                }
                writer.write("\n");
            }

            System.out.println("Generated: " + filename + " (" + numTuples + " tuples, "
                    + numAttributes + " attributes)");

        } catch (IOException e) {
            System.err.println("Error generating dataset: " + e.getMessage());
        }
    }

    /**
     * Seleziona set di categorie per attributo.
     */
    private static String[] selectCategories(int attrIndex) {
        switch (attrIndex % 5) {
            case 0:
                return CATEGORIES_A;
            case 1:
                return CATEGORIES_B;
            case 2:
                return CATEGORIES_C;
            case 3:
                return CATEGORIES_D;
            case 4:
                return CATEGORIES_E;
            default:
                return CATEGORIES_A;
        }
    }

    /**
     * Genera suite completa di dataset per testing.
     */
    public static void generateTestSuite() {
        System.out.println("Generating synthetic test datasets...\n");

        // Small dataset
        generateDataset("../data/synthetic_small.csv", 50, 5, 12345);

        // Medium dataset
        generateDataset("../data/synthetic_medium.csv", 200, 8, 12346);

        // Large dataset
        generateDataset("../data/synthetic_large.csv", 1000, 10, 12347);

        // XLarge dataset
        generateDataset("../data/synthetic_xlarge.csv", 5000, 12, 12348);

        System.out.println("\nTest suite generation completed!");
    }

    /**
     * Main per eseguire il generatore standalone.
     */
    public static void main(String[] args) {
        if (args.length >= 2) {
            // Uso personalizzato
            String filename = args[0];
            int numTuples = Integer.parseInt(args[1]);
            int numAttributes = args.length > 2 ? Integer.parseInt(args[2]) : 5;
            long seed = args.length > 3 ? Long.parseLong(args[3]) : System.currentTimeMillis();

            generateDataset(filename, numTuples, numAttributes, seed);
        } else {
            // Genera suite completa
            generateTestSuite();
        }
    }
}
