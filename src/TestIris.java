import java.io.IOException;
import exceptions.InvalidDataFormatException;

/**
 * Test rapido per verificare il caricamento del dataset Iris.
 */
public class TestIris {
    public static void main(String[] args) {
        try {
            System.out.println("=== Test Caricamento Iris Dataset ===\n");

            // Carica dataset (path relativo da src/)
            Data iris = new Data("../data/iris.csv");

            // Verifica dimensioni
            System.out.println("Numero tuple: " + iris.getNumberOfExamples());
            System.out.println("Numero attributi: " + iris.getNumberOfExplanatoryAttributes());
            System.out.println();

            // Verifica tipi attributi
            System.out.println("Tipi attributi:");
            java.util.List<Attribute> attrs = iris.getAttributeSchema();
            int index = 1;
            for (Attribute attr : attrs) {
                String type = attr instanceof ContinuousAttribute ? "Continuous" : "Discrete";
                System.out.println("  " + (index++) + ". " + attr.getName() + " - " + type);

                if (attr instanceof ContinuousAttribute) {
                    ContinuousAttribute cattr = (ContinuousAttribute) attr;
                    double testValue = 5.0;
                    System.out.println("     (scaled(5.0) = "
                            + String.format("%.3f", cattr.getScaledValue(testValue)) + ")");
                }
            }
            System.out.println();

            // Verifica prima tupla
            System.out.println("Prima tupla (setosa):");
            Tuple tuple = iris.getItemSet(0);
            for (int i = 0; i < tuple.getLength(); i++) {
                Item item = tuple.get(i);
                String itemType =
                        item instanceof ContinuousItem ? "ContinuousItem" : "DiscreteItem";
                System.out.println("  " + attrs.get(i).getName() + " = " + item.getValue() + " ["
                        + itemType + "]");
            }
            System.out.println();

            // Verifica tupla versicolor (51)
            System.out.println("Tupla 51 (versicolor):");
            Tuple tuple51 = iris.getItemSet(50);
            for (int i = 0; i < tuple51.getLength(); i++) {
                Item item = tuple51.get(i);
                System.out.println("  " + attrs.get(i).getName() + " = " + item.getValue());
            }
            System.out.println();

            // Verifica tupla virginica (101)
            System.out.println("Tupla 101 (virginica):");
            Tuple tuple101 = iris.getItemSet(100);
            for (int i = 0; i < tuple101.getLength(); i++) {
                Item item = tuple101.get(i);
                System.out.println("  " + attrs.get(i).getName() + " = " + item.getValue());
            }
            System.out.println();

            // Test distanza tra tuple
            System.out.println("Test distanze:");
            Tuple t1 = iris.getItemSet(0); // setosa
            Tuple t2 = iris.getItemSet(1); // setosa (simile)
            Tuple t3 = iris.getItemSet(50); // versicolor
            Tuple t4 = iris.getItemSet(100); // virginica

            double d12 = t1.getDistance(t2);
            double d13 = t1.getDistance(t3);
            double d14 = t1.getDistance(t4);

            System.out.println("  Distanza setosa-setosa: " + String.format("%.4f", d12));
            System.out.println("  Distanza setosa-versicolor: " + String.format("%.4f", d13));
            System.out.println("  Distanza setosa-virginica: " + String.format("%.4f", d14));
            System.out.println();

            // Verifica che distanza intra-specie < inter-specie
            if (d12 < d13 && d12 < d14) {
                System.out.println("✓ Test PASSED: Distanza intra-specie < inter-specie");
            } else {
                System.out.println("✗ Test FAILED: Distanze non corrette");
            }

            System.out.println("\n=== Test Completato con Successo ===");

        } catch (IOException | InvalidDataFormatException e) {
            System.err.println("Errore: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
