/**
 * Classe che modella l'insieme di transazioni (tuple).
 */
public class Data {
    private Object data[][];
    private int numberOfExamples;
    private Attribute explanatorySet[];

    /**
     * Costruttore della classe Data.
     * Inizializza la matrice data con le transazioni di esempio.
     */
    public Data() {
        // Inizializzazione della matrice data con 14 esempi e 5 attributi
        data = new Object[14][5];

        // Popolamento della matrice con i dati PlayTennis
        data[0] = new String[]{"sunny", "hot", "high", "weak", "no"};
        data[1] = new String[]{"sunny", "hot", "high", "strong", "no"};
        data[2] = new String[]{"overcast", "hot", "high", "weak", "yes"};
        data[3] = new String[]{"rain", "mild", "high", "weak", "yes"};
        data[4] = new String[]{"rain", "cool", "normal", "weak", "yes"};
        data[5] = new String[]{"rain", "cool", "normal", "strong", "no"};
        data[6] = new String[]{"overcast", "cool", "normal", "strong", "yes"};
        data[7] = new String[]{"sunny", "mild", "high", "weak", "no"};
        data[8] = new String[]{"sunny", "cool", "normal", "weak", "yes"};
        data[9] = new String[]{"rain", "mild", "normal", "weak", "yes"};
        data[10] = new String[]{"sunny", "mild", "normal", "strong", "yes"};
        data[11] = new String[]{"overcast", "mild", "high", "strong", "yes"};
        data[12] = new String[]{"overcast", "hot", "normal", "weak", "yes"};
        data[13] = new String[]{"rain", "mild", "high", "strong", "no"};

        // Numero di esempi
        numberOfExamples = 14;

        // Inizializzazione dell'explanatory set con 5 attributi discreti
        explanatorySet = new Attribute[5];

        // Outlook attribute
        String[] outLookValues = new String[]{"overcast", "rain", "sunny"};
        explanatorySet[0] = new DiscreteAttribute("Outlook", 0, outLookValues);

        // Temperature attribute
        String[] temperatureValues = new String[]{"cool", "hot", "mild"};
        explanatorySet[1] = new DiscreteAttribute("Temperature", 1, temperatureValues);

        // Humidity attribute
        String[] humidityValues = new String[]{"high", "normal"};
        explanatorySet[2] = new DiscreteAttribute("Humidity", 2, humidityValues);

        // Wind attribute
        String[] windValues = new String[]{"strong", "weak"};
        explanatorySet[3] = new DiscreteAttribute("Wind", 3, windValues);

        // PlayTennis attribute
        String[] playTennisValues = new String[]{"no", "yes"};
        explanatorySet[4] = new DiscreteAttribute("PlayTennis", 4, playTennisValues);
    }

    /**
     * Restituisce il numero di esempi.
     *
     * @return cardinalità dell'insieme di transazioni
     */
    public int getNumberOfExamples() {
        return numberOfExamples;
    }

    /**
     * Restituisce il numero di attributi.
     *
     * @return cardinalità dell'insieme degli attributi
     */
    public int getNumberOfExplanatoryAttributes() {
        return explanatorySet.length;
    }

    /**
     * Restituisce lo schema degli attributi.
     *
     * @return array di attributi
     */
    public Attribute[] getAttributeSchema() {
        return explanatorySet;
    }

    /**
     * Restituisce il valore di un attributo in una specifica transazione.
     *
     * @param exampleIndex indice di riga
     * @param attributeIndex indice di colonna
     * @return valore dell'attributo
     */
    public Object getValue(int exampleIndex, int attributeIndex) {
        return data[exampleIndex][attributeIndex];
    }

    /**
     * Restituisce una stringa rappresentante lo stato dell'oggetto.
     *
     * @return stringa con lo schema e le transazioni
     */
    @Override
    public String toString() {
        String str = "";

        // Schema della tabella
        for (int i = 0; i < explanatorySet.length; i++) {
            str += explanatorySet[i].getName();
            if (i < explanatorySet.length - 1) {
                str += ",";
            }
        }
        str += "\n";

        // Transazioni
        for (int i = 0; i < numberOfExamples; i++) {
            str += (i + 1) + ":";
            for (int j = 0; j < explanatorySet.length; j++) {
                str += data[i][j];
                if (j < explanatorySet.length - 1) {
                    str += ",";
                }
            }
            str += ",\n";
        }

        return str;
    }

    /**
     * Crea e restituisce un oggetto Tuple che modella la i-esima riga in data.
     *
     * @param index indice di riga
     * @return tupla corrispondente alla riga
     */
    public Tuple getItemSet(int index) {
        Tuple tuple = new Tuple(explanatorySet.length);
        for (int i = 0; i < explanatorySet.length; i++) {
            tuple.add(new DiscreteItem((DiscreteAttribute) explanatorySet[i],
                    (String) data[index][i]), i);
        }
        return tuple;
    }

    /**
     * Metodo main per testare la classe Data.
     *
     * @param args argomenti della linea di comando
     */
    public static void main(String[] args) {
        Data trainingSet = new Data();
        System.out.println(trainingSet);
    }
}
