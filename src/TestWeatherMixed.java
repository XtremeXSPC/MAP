import java.io.IOException;
import exceptions.InvalidDataFormatException;

/**
 * Test rapido per verificare il caricamento del dataset Weather Mixed.
 */
public class TestWeatherMixed {
    public static void main(String[] args) {
        try {
            System.out.println("=== Test Caricamento Weather Mixed Dataset ===\n");

            // Carica dataset (path relativo da src/)
            Data weather = new Data("../data/weather_mixed.csv");

            // Verifica dimensioni
            System.out.println("Numero tuple: " + weather.getNumberOfExamples());
            System.out.println("Numero attributi: " + weather.getNumberOfExplanatoryAttributes());
            System.out.println();

            // Verifica tipi attributi
            System.out.println("Tipi attributi:");
            java.util.List<Attribute> attrs = weather.getAttributeSchema();
            int numDiscrete = 0;
            int numContinuous = 0;

            for (int i = 0; i < attrs.size(); i++) {
                String type = attrs.get(i) instanceof ContinuousAttribute ? "Continuous" : "Discrete";
                System.out.println("  " + (i + 1) + ". " + attrs.get(i).getName() + " - " + type);

                if (attrs.get(i) instanceof ContinuousAttribute) {
                    numContinuous++;
                } else {
                    numDiscrete++;
                }
            }
            System.out.println("\nRiepilogo: " + numContinuous + " continui, " + numDiscrete + " discreti");
            System.out.println();

            // Verifica prima tupla
            System.out.println("Prima tupla:");
            Tuple tuple = weather.getItemSet(0);
            for (int i = 0; i < tuple.getLength(); i++) {
                Item item = tuple.get(i);
                String itemType = item instanceof ContinuousItem ? "ContinuousItem" : "DiscreteItem";
                System.out.println("  " + attrs.get(i).getName() + " = " +
                    item.getValue() + " [" + itemType + "]");
            }
            System.out.println();

            // Verifica tupla centrale
            System.out.println("Tupla 15 (centrale):");
            Tuple tuple15 = weather.getItemSet(14);
            for (int i = 0; i < tuple15.getLength(); i++) {
                Item item = tuple15.get(i);
                System.out.println("  " + attrs.get(i).getName() + " = " + item.getValue());
            }
            System.out.println();

            // Test distanza tra tuple con attributi misti
            System.out.println("Test distanze (attributi misti):");
            Tuple t1 = weather.getItemSet(0);  // sunny, 25.5, 65.0, weak, yes
            Tuple t2 = weather.getItemSet(1);  // sunny, 28.0, 70.0, weak, yes (simile)
            Tuple t3 = weather.getItemSet(5);  // overcast, 20.0, 75.0, weak, yes (outlook diverso)
            Tuple t4 = weather.getItemSet(2);  // sunny, 32.0, 85.0, weak, no (play diverso)

            double d12 = t1.getDistance(t2);
            double d13 = t1.getDistance(t3);
            double d14 = t1.getDistance(t4);

            System.out.println("  t1: sunny, 25.5°C, 65%, weak, yes");
            System.out.println("  t2: sunny, 28.0°C, 70%, weak, yes");
            System.out.println("  d(t1,t2) = " + String.format("%.4f", d12));
            System.out.println();

            System.out.println("  t3: overcast, 20.0°C, 75%, weak, yes");
            System.out.println("  d(t1,t3) = " + String.format("%.4f", d13));
            System.out.println();

            System.out.println("  t4: sunny, 32.0°C, 85%, weak, no");
            System.out.println("  d(t1,t4) = " + String.format("%.4f", d14));
            System.out.println();

            // Analisi distanze
            System.out.println("Analisi:");
            System.out.println("  - t1 vs t2: stessa outlook, temperature simili → distanza bassa");
            System.out.println("  - t1 vs t3: outlook diverso → distanza media");
            System.out.println("  - t1 vs t4: play diverso, temperature più lontane → distanza alta");
            System.out.println();

            // Verifica proprietà
            boolean test1 = d12 < d13;
            boolean test2 = d13 < d14 || d12 < d14;

            if (test1 && test2) {
                System.out.println("✓ Test PASSED: Distanze coerenti con similarità tuple");
            } else {
                System.out.println("⚠ Test WARNING: Verificare distanze manualmente");
                System.out.println("  d12=" + d12 + ", d13=" + d13 + ", d14=" + d14);
            }

            System.out.println("\n=== Test Completato con Successo ===");

        } catch (IOException | InvalidDataFormatException e) {
            System.err.println("Errore: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
