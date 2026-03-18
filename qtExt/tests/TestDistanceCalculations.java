package tests;

import java.util.logging.Logger;

/**
 * Test calcolo distanze tra Item e Tuple.
 */
public class TestDistanceCalculations {
    // Logger.
    private static final Logger logger = Logger.getLogger(TestDistanceCalculations.class.getName());

    // Contatori test.
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        logger.info(() -> String.valueOf("=== Test Calcolo Distanze ===\n"));

        testDiscreteItemDistance();
        testContinuousItemDistance();
        testTupleDistance();
        testDistanceSymmetry();
        testDistanceRange();

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
     * Test distanza tra DiscreteItem.
     */
    private static void testDiscreteItemDistance() {
        logger.info(() -> String.valueOf("Test 1: Distanza DiscreteItem"));
        try {
            String[] values = { "sunny", "rain", "overcast" };
            data.DiscreteAttribute attr = new data.DiscreteAttribute("Outlook", 0, values);

            data.DiscreteItem item1 = new data.DiscreteItem(attr, "sunny");
            data.DiscreteItem item2 = new data.DiscreteItem(attr, "sunny");
            data.DiscreteItem item3 = new data.DiscreteItem(attr, "rain");

            // Distanza tra item uguali dovrebbe essere 0.
            assertEqual(item1.distance(item2.getValue()), 0.0, "Distanza tra item uguali dovrebbe essere 0");

            // Distanza tra item diversi dovrebbe essere 1.
            assertEqual(item1.distance(item3.getValue()), 1.0, "Distanza tra item diversi dovrebbe essere 1");

            logger.info(() -> String.valueOf("  ✓ Distanze DiscreteItem corrette\n"));
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    /**
     * Test distanza tra ContinuousItem.
     */
    private static void testContinuousItemDistance() {
        logger.info(() -> String.valueOf("Test 2: Distanza ContinuousItem"));
        try {
            data.ContinuousAttribute attr = new data.ContinuousAttribute("Temperature", 0, 0.0, 40.0);

            data.ContinuousItem item1 = new data.ContinuousItem(attr, 20.0);
            data.ContinuousItem item2 = new data.ContinuousItem(attr, 20.0);
            data.ContinuousItem item3 = new data.ContinuousItem(attr, 30.0);

            // Distanza tra item uguali dovrebbe essere 0.
            assertEqual(item1.distance(item2.getValue()), 0.0, "Distanza tra item uguali dovrebbe essere 0");

            // Distanza tra item diversi dovrebbe essere normalizzata.
            double distance = item1.distance(item3.getValue());
            assertTrue(distance >= 0.0 && distance <= 1.0, "Distanza normalizzata dovrebbe essere in [0,1]");

            // Distance dovrebbe essere (30-20)/(40-0) = 10/40 = 0.25.
            assertTrue(Math.abs(distance - 0.25) < 0.0001, "Distanza calcolata non corretta");

            logger.info(() -> String.valueOf("  ✓ Distanze ContinuousItem corrette (normalizzate)\n"));
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    /**
     * Test distanza tra Tuple.
     */
    private static void testTupleDistance() {
        logger.info(() -> String.valueOf("Test 3: Distanza tra Tuple"));
        try {
            data.Data data = new data.Data();
            data.Tuple tuple1 = data.getItemSet(0);
            data.Tuple tuple2 = data.getItemSet(0); // Stessa tupla.
            data.Tuple tuple3 = data.getItemSet(1); // Tupla diversa.

            // Distanza di una tupla con se stessa dovrebbe essere 0.
            assertEqual(tuple1.getDistance(tuple2), 0.0, "Distanza tupla con se stessa dovrebbe essere 0");

            // Distanza tra tuple diverse dovrebbe essere > 0 e <= 1.
            double distance = tuple1.getDistance(tuple3);
            assertTrue(distance >= 0.0 && distance <= 1.0, "Distanza normalizzata dovrebbe essere in [0,1]");

            logger.info(() -> String.valueOf("  ✓ Distanze tra Tuple corrette\n"));
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    /**
     * Test simmetria distanza: d(a,b) = d(b,a).
     */
    private static void testDistanceSymmetry() {
        logger.info(() -> String.valueOf("Test 4: Simmetria distanza"));
        try {
            data.Data data = new data.Data();
            data.Tuple tuple1 = data.getItemSet(0);
            data.Tuple tuple2 = data.getItemSet(5);

            double dist1to2 = tuple1.getDistance(tuple2);
            double dist2to1 = tuple2.getDistance(tuple1);

            assertTrue(Math.abs(dist1to2 - dist2to1) < 0.0001, "Distanza dovrebbe essere simmetrica");

            logger.info(() -> String.valueOf("  ✓ Distanza simmetrica verificata\n"));
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    /**
     * Test range distanze normalizzate.
     */
    private static void testDistanceRange() {
        logger.info(() -> String.valueOf("Test 5: Range distanze [0,1]"));
        try {
            data.Data data = new data.Data();
            int numExamples = data.getNumberOfExamples();

            boolean allInRange = true;
            for (int i = 0; i < numExamples; i++) {
                for (int j = i + 1; j < numExamples; j++) {
                    data.Tuple tuple1 = data.getItemSet(i);
                    data.Tuple tuple2 = data.getItemSet(j);
                    double distance = tuple1.getDistance(tuple2);

                    if (distance < 0.0 || distance > 1.0) {
                        allInRange = false;
                        logger.info("  ✗ Distanza fuori range: tuple[" + i + "][" + j + "] = " + distance);
                    }
                }
            }

            assertTrue(allInRange, "Tutte le distanze devono essere in [0,1]");
            logger.info(() -> String.valueOf("  ✓ Tutte le distanze nel range [0,1]\n"));
            testsPassed++;
        } catch (Exception e) {
            logger.info(() -> String.valueOf("  ✗ Test fallito: " + e.getMessage() + "\n"));
            testsFailed++;
        }
    }

    // Utility methods.
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Asserzione fallita: " + message);
        }
    }

    private static void assertEqual(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > 0.0001) {
            throw new AssertionError(message + " - Atteso: " + expected + ", Ottenuto: " + actual);
        }
    }
}
