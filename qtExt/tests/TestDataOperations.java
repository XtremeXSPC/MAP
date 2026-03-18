package tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.logging.Logger;

/**
 * Test operazioni su Data: caricamento da CSV, validazione, accesso attributi.
 */
public class TestDataOperations {
    // Logger.
    private static final Logger logger = Logger.getLogger(TestDataOperations.class.getName());

    // Contatori test.
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        logger.info(() -> String.valueOf("=== Test Operazioni Data ===\n"));

        testHardcodedData();
        testCSVLoading();
        testNegativeContinuousCSV();
        testAttributeAccess();
        testTupleAccess();
        testInvalidCSV();

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
     * Test caricamento dati hardcoded (PlayTennis).
     */
    private static void testHardcodedData() {
        logger.info(() -> String.valueOf("Test 1: Caricamento dati hardcoded"));
        try {
            data.Data data = new data.Data();

            assertTrue(data.getNumberOfExamples() > 0, "Dataset non dovrebbe essere vuoto");
            assertTrue(data.getNumberOfExplanatoryAttributes() > 0, "Dovrebbero esserci attributi");

            logger.info(() -> "  ✓ Dataset caricato: " + data.getNumberOfExamples() + " tuple, "
                    + data.getNumberOfExplanatoryAttributes() + " attributi\n");
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    /**
     * Test caricamento da file CSV esistente.
     */
    private static void testCSVLoading() {
        logger.info(() -> String.valueOf("Test 2: Caricamento da CSV"));
        try {
            // Prova a caricare file CSV se esiste.
            File csvFile = new File("../data/weather_mixed.csv");
            if (csvFile.exists()) {
                data.Data data = new data.Data("../data/weather_mixed.csv");

                assertTrue(data.getNumberOfExamples() > 0, "Dataset da CSV non dovrebbe essere vuoto");
                logger.info(() -> String.valueOf("  ✓ CSV caricato: " + data.getNumberOfExamples() + " tuple\n"));
                testsPassed++;
            } else {
                logger.info(() -> String.valueOf("  ⊘ CSV non trovato, test saltato\n"));
                testsPassed++; // Non è un fallimento
            }
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    /**
     * Test inferenza corretta di attributi continui con valori tutti negativi.
     */
    private static void testNegativeContinuousCSV() {
        logger.info(() -> String.valueOf("Test 3: CSV con valori continui negativi"));
        File csvFile = new File("test_negative_values.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write("temperature,pressure\n");
            writer.write("-10,-100\n");
            writer.write("-20,-90\n");
            writer.write("-30,-80\n");
            writer.write("-40,-70\n");
            writer.write("-50,-60\n");
            writer.write("-60,-50\n");
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
            return;
        }

        try {
            data.Data data = new data.Data(csvFile.getPath());
            data.Attribute attr = data.getExplanatoryAttribute(0);

            assertTrue(attr instanceof data.ContinuousAttribute,
                    "L'attributo con soli valori numerici negativi dovrebbe essere continuo");

            data.ContinuousAttribute continuousAttribute = (data.ContinuousAttribute) attr;
            assertTrue(Math.abs(continuousAttribute.getMin() - (-60.0)) < 0.0001, "Valore minimo non corretto");
            assertTrue(Math.abs(continuousAttribute.getMax() - (-10.0)) < 0.0001, "Valore massimo non corretto");

            logger.info(() -> String.valueOf("  ✓ Min/max corretti anche con valori tutti negativi\n"));
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        } finally {
            csvFile.delete();
        }
    }

    /**
     * Test accesso agli attributi.
     */
    private static void testAttributeAccess() {
        logger.info(() -> String.valueOf("Test 4: Accesso attributi"));
        try {
            data.Data data = new data.Data();
            int numAttributes = data.getNumberOfExplanatoryAttributes();

            for (int i = 0; i < numAttributes; i++) {
                data.Attribute attr = data.getExplanatoryAttribute(i);
                assertNotNull(attr, "Attributo " + i + " non dovrebbe essere null");
                assertNotNull(attr.getName(), "Nome attributo non dovrebbe essere null");
            }

            logger.info(() -> String.valueOf("  ✓ Tutti gli attributi accessibili correttamente\n"));
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    /**
     * Test accesso alle tuple.
     */
    private static void testTupleAccess() {
        logger.info(() -> String.valueOf("Test 5: Accesso tuple"));
        try {
            data.Data data = new data.Data();
            int numExamples = data.getNumberOfExamples();

            for (int i = 0; i < numExamples; i++) {
                data.Tuple tuple = data.getItemSet(i);
                assertNotNull(tuple, "Tupla " + i + " non dovrebbe essere null");

                // Verifica che la tupla abbia il numero corretto di item.
                for (int j = 0; j < data.getNumberOfExplanatoryAttributes(); j++) {
                    data.Item item = tuple.get(j);
                    assertNotNull(item, "Item [" + i + "][" + j + "] non dovrebbe essere null");
                }
            }

            logger.info(() -> String.valueOf("  ✓ Tutte le " + numExamples + " tuple accessibili\n"));
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    /**
     * Test gestione CSV non valido.
     */
    @SuppressWarnings("unused")
    private static void testInvalidCSV() {
        logger.info(() -> String.valueOf("Test 6: Gestione CSV non valido"));
        try {
            // Tenta di caricare file inesistente.
            try {
                data.Data data = new data.Data("/percorso/inesistente/file.csv");
                // Se arriviamo qui, il test è fallito (dovrebbe lanciare eccezione).
                logger.info(() -> String.valueOf("  ✗ Dovrebbe lanciare eccezione per file inesistente\n"));
                testsFailed++;
            } catch (Exception e) {
                // Eccezione attesa.
                logger.info(() -> String
                        .valueOf("  ✓ Eccezione correttamente lanciata: " + e.getClass().getSimpleName() + "\n"));
                testsPassed++;
            }
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito inaspettatamente: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    // Utility methods
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Asserzione fallita: " + message);
        }
    }

    private static void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError("Asserzione fallita: " + message);
        }
    }
}
