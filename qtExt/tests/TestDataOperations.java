package tests;

import data.*;
import java.io.File;

/**
 * Test operazioni su Data: caricamento da CSV, validazione, accesso attributi.
 */
public class TestDataOperations {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=== Test Operazioni Data ===\n");

        testHardcodedData();
        testCSVLoading();
        testAttributeAccess();
        testTupleAccess();
        testInvalidCSV();

        System.out.println("\n=== Risultati Test ===");
        System.out.println("Test passati: " + testsPassed);
        System.out.println("Test falliti: " + testsFailed);

        if (testsFailed == 0) {
            System.out.println("\n✓ Tutti i test sono passati!");
        } else {
            System.out.println("\n✗ Alcuni test sono falliti");
        }
    }

    /**
     * Test caricamento dati hardcoded (PlayTennis).
     */
    private static void testHardcodedData() {
        System.out.println("Test 1: Caricamento dati hardcoded");
        try {
            Data data = new Data();

            assertTrue(data.getNumberOfExamples() > 0, "Dataset non dovrebbe essere vuoto");
            assertTrue(data.getNumberOfExplanatoryAttributes() > 0, "Dovrebbero esserci attributi");

            System.out.println("  ✓ Dataset caricato: " + data.getNumberOfExamples() + " tuple, "
                    + data.getNumberOfExplanatoryAttributes() + " attributi\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    /**
     * Test caricamento da file CSV esistente.
     */
    private static void testCSVLoading() {
        System.out.println("Test 2: Caricamento da CSV");
        try {
            // Prova a caricare file CSV se esiste
            File csvFile = new File("../data/weather_mixed.csv");
            if (csvFile.exists()) {
                Data data = new Data("../data/weather_mixed.csv");

                assertTrue(data.getNumberOfExamples() > 0, "Dataset da CSV non dovrebbe essere vuoto");
                System.out.println("  ✓ CSV caricato: " + data.getNumberOfExamples() + " tuple\n");
                testsPassed++;
            } else {
                System.out.println("  ⊘ CSV non trovato, test saltato\n");
                testsPassed++; // Non è un fallimento
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    /**
     * Test accesso agli attributi.
     */
    private static void testAttributeAccess() {
        System.out.println("Test 3: Accesso attributi");
        try {
            Data data = new Data();
            int numAttributes = data.getNumberOfExplanatoryAttributes();

            for (int i = 0; i < numAttributes; i++) {
                Attribute attr = data.getExplanatoryAttribute(i);
                assertNotNull(attr, "Attributo " + i + " non dovrebbe essere null");
                assertNotNull(attr.getName(), "Nome attributo non dovrebbe essere null");
            }

            System.out.println("  ✓ Tutti gli attributi accessibili correttamente\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    /**
     * Test accesso alle tuple.
     */
    private static void testTupleAccess() {
        System.out.println("Test 4: Accesso tuple");
        try {
            Data data = new Data();
            int numExamples = data.getNumberOfExamples();

            for (int i = 0; i < numExamples; i++) {
                data.Tuple tuple = data.getItemSet(i);
                assertNotNull(tuple, "Tupla " + i + " non dovrebbe essere null");

                // Verifica che la tupla abbia il numero corretto di item
                for (int j = 0; j < data.getNumberOfExplanatoryAttributes(); j++) {
                    Item item = tuple.get(j);
                    assertNotNull(item, "Item [" + i + "][" + j + "] non dovrebbe essere null");
                }
            }

            System.out.println("  ✓ Tutte le " + numExamples + " tuple accessibili\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    /**
     * Test gestione CSV non valido.
     */
    @SuppressWarnings("unused")
    private static void testInvalidCSV() {
        System.out.println("Test 5: Gestione CSV non valido");
        try {
            // Tenta di caricare file inesistente
            try {
                Data data = new Data("/percorso/inesistente/file.csv");
                // Se arriviamo qui, il test è fallito (dovrebbe lanciare eccezione)
                System.out.println("  ✗ Dovrebbe lanciare eccezione per file inesistente\n");
                testsFailed++;
            } catch (Exception e) {
                // Eccezione attesa
                System.out.println("  ✓ Eccezione correttamente lanciata: " + e.getClass().getSimpleName() + "\n");
                testsPassed++;
            }
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito inaspettatamente: " + e.getMessage() + "\n");
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
