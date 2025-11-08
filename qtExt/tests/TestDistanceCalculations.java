package tests;

import data.*;

/**
 * Test calcolo distanze tra Item e Tuple.
 */
public class TestDistanceCalculations {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=== Test Calcolo Distanze ===\n");

        testDiscreteItemDistance();
        testContinuousItemDistance();
        testTupleDistance();
        testDistanceSymmetry();
        testDistanceRange();

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
     * Test distanza tra DiscreteItem.
     */
    private static void testDiscreteItemDistance() {
        System.out.println("Test 1: Distanza DiscreteItem");
        try {
            String[] values = { "sunny", "rain", "overcast" };
            DiscreteAttribute attr = new DiscreteAttribute("Outlook", 0, values);

            DiscreteItem item1 = new DiscreteItem(attr, "sunny");
            DiscreteItem item2 = new DiscreteItem(attr, "sunny");
            DiscreteItem item3 = new DiscreteItem(attr, "rain");

            // Distanza tra item uguali dovrebbe essere 0
            assertEqual(item1.distance(item2.getValue()), 0.0, "Distanza tra item uguali dovrebbe essere 0");

            // Distanza tra item diversi dovrebbe essere 1
            assertEqual(item1.distance(item3.getValue()), 1.0, "Distanza tra item diversi dovrebbe essere 1");

            System.out.println("  ✓ Distanze DiscreteItem corrette\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    /**
     * Test distanza tra ContinuousItem.
     */
    private static void testContinuousItemDistance() {
        System.out.println("Test 2: Distanza ContinuousItem");
        try {
            ContinuousAttribute attr = new ContinuousAttribute("Temperature", 0, 0.0, 40.0);

            ContinuousItem item1 = new ContinuousItem(attr, 20.0);
            ContinuousItem item2 = new ContinuousItem(attr, 20.0);
            ContinuousItem item3 = new ContinuousItem(attr, 30.0);

            // Distanza tra item uguali dovrebbe essere 0
            assertEqual(item1.distance(item2.getValue()), 0.0, "Distanza tra item uguali dovrebbe essere 0");

            // Distanza tra item diversi dovrebbe essere normalizzata
            double distance = item1.distance(item3.getValue());
            assertTrue(distance >= 0.0 && distance <= 1.0, "Distanza normalizzata dovrebbe essere in [0,1]");

            // Distance dovrebbe essere (30-20)/(40-0) = 10/40 = 0.25
            assertTrue(Math.abs(distance - 0.25) < 0.0001, "Distanza calcolata non corretta");

            System.out.println("  ✓ Distanze ContinuousItem corrette (normalizzate)\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    /**
     * Test distanza tra Tuple.
     */
    private static void testTupleDistance() {
        System.out.println("Test 3: Distanza tra Tuple");
        try {
            Data data = new Data();
            data.Tuple tuple1 = data.getItemSet(0);
            data.Tuple tuple2 = data.getItemSet(0); // stessa tupla
            data.Tuple tuple3 = data.getItemSet(1); // tupla diversa

            // Distanza di una tupla con se stessa dovrebbe essere 0
            assertEqual(tuple1.getDistance(tuple2), 0.0, "Distanza tupla con se stessa dovrebbe essere 0");

            // Distanza tra tuple diverse dovrebbe essere > 0 e <= 1
            double distance = tuple1.getDistance(tuple3);
            assertTrue(distance >= 0.0 && distance <= 1.0, "Distanza normalizzata dovrebbe essere in [0,1]");

            System.out.println("  ✓ Distanze tra Tuple corrette\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    /**
     * Test simmetria distanza: d(a,b) = d(b,a).
     */
    private static void testDistanceSymmetry() {
        System.out.println("Test 4: Simmetria distanza");
        try {
            Data data = new Data();
            data.Tuple tuple1 = data.getItemSet(0);
            data.Tuple tuple2 = data.getItemSet(5);

            double dist1to2 = tuple1.getDistance(tuple2);
            double dist2to1 = tuple2.getDistance(tuple1);

            assertTrue(Math.abs(dist1to2 - dist2to1) < 0.0001, "Distanza dovrebbe essere simmetrica");

            System.out.println("  ✓ Distanza simmetrica verificata\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    /**
     * Test range distanze normalizzate.
     */
    private static void testDistanceRange() {
        System.out.println("Test 5: Range distanze [0,1]");
        try {
            Data data = new Data();
            int numExamples = data.getNumberOfExamples();

            boolean allInRange = true;
            for (int i = 0; i < numExamples; i++) {
                for (int j = i + 1; j < numExamples; j++) {
                    data.Tuple tuple1 = data.getItemSet(i);
                    data.Tuple tuple2 = data.getItemSet(j);
                    double distance = tuple1.getDistance(tuple2);

                    if (distance < 0.0 || distance > 1.0) {
                        allInRange = false;
                        System.out.println("  ✗ Distanza fuori range: tuple[" + i + "][" + j + "] = " + distance);
                    }
                }
            }

            assertTrue(allInRange, "Tutte le distanze devono essere in [0,1]");
            System.out.println("  ✓ Tutte le distanze nel range [0,1]\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("  ✗ Test fallito: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    // Utility methods
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
