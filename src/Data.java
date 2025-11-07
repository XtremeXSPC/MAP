import exceptions.InvalidDataFormatException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe che modella l'insieme di transazioni (tuple).
 */
public class Data {
    private Object data[][];
    private int numberOfExamples;
    private Attribute explanatorySet[];

    /**
     * Costruttore della classe Data. Inizializza la matrice data con le transazioni di esempio.
     */
    public Data() {
        // Inizializzazione della matrice data con 14 esempi e 5 attributi
        data = new Object[14][5];

        // Popolamento della matrice con i dati PlayTennis
        data[0] = new String[] {"sunny", "hot", "high", "weak", "no"};
        data[1] = new String[] {"sunny", "hot", "high", "strong", "no"};
        data[2] = new String[] {"overcast", "hot", "high", "weak", "yes"};
        data[3] = new String[] {"rain", "mild", "high", "weak", "yes"};
        data[4] = new String[] {"rain", "cool", "normal", "weak", "yes"};
        data[5] = new String[] {"rain", "cool", "normal", "strong", "no"};
        data[6] = new String[] {"overcast", "cool", "normal", "strong", "yes"};
        data[7] = new String[] {"sunny", "mild", "high", "weak", "no"};
        data[8] = new String[] {"sunny", "cool", "normal", "weak", "yes"};
        data[9] = new String[] {"rain", "mild", "normal", "weak", "yes"};
        data[10] = new String[] {"sunny", "mild", "normal", "strong", "yes"};
        data[11] = new String[] {"overcast", "mild", "high", "strong", "yes"};
        data[12] = new String[] {"overcast", "hot", "normal", "weak", "yes"};
        data[13] = new String[] {"rain", "mild", "high", "strong", "no"};

        // Numero di esempi
        numberOfExamples = 14;

        // Inizializzazione dell'explanatory set con 5 attributi discreti
        explanatorySet = new Attribute[5];

        // Outlook attribute
        String[] outLookValues = new String[] {"overcast", "rain", "sunny"};
        explanatorySet[0] = new DiscreteAttribute("Outlook", 0, outLookValues);

        // Temperature attribute
        String[] temperatureValues = new String[] {"cool", "hot", "mild"};
        explanatorySet[1] = new DiscreteAttribute("Temperature", 1, temperatureValues);

        // Humidity attribute
        String[] humidityValues = new String[] {"high", "normal"};
        explanatorySet[2] = new DiscreteAttribute("Humidity", 2, humidityValues);

        // Wind attribute
        String[] windValues = new String[] {"strong", "weak"};
        explanatorySet[3] = new DiscreteAttribute("Wind", 3, windValues);

        // PlayTennis attribute
        String[] playTennisValues = new String[] {"no", "yes"};
        explanatorySet[4] = new DiscreteAttribute("PlayTennis", 4, playTennisValues);
    }

    /**
     * Costruttore che carica dataset da file CSV.
     *
     * @param csvFilename path del file CSV da caricare
     * @throws IOException se si verificano errori di I/O
     * @throws InvalidDataFormatException se il formato dei dati non è valido
     */
    public Data(String csvFilename) throws IOException, InvalidDataFormatException {
        parseCSV(csvFilename);
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
     * Restituisce l'attributo in posizione index.
     *
     * @param index indice dell'attributo
     * @return attributo in posizione index
     */
    public Attribute getExplanatoryAttribute(int index) {
        return explanatorySet[index];
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
     * <p>Questo metodo gestisce sia attributi discreti che continui, creando
     * il tipo appropriato di Item per ciascun attributo:</p>
     * <ul>
     *   <li>DiscreteAttribute → DiscreteItem</li>
     *   <li>ContinuousAttribute → ContinuousItem</li>
     * </ul>
     *
     * @param index indice di riga
     * @return tupla corrispondente alla riga
     * @throws NumberFormatException se un valore continuo non può essere parsato come Double
     */
    public Tuple getItemSet(int index) {
        Tuple tuple = new Tuple(explanatorySet.length);

        for (int i = 0; i < explanatorySet.length; i++) {
            Attribute attr = explanatorySet[i];
            Object value = data[index][i];

            if (attr instanceof DiscreteAttribute) {
                // Attributo discreto → crea DiscreteItem
                tuple.add(new DiscreteItem((DiscreteAttribute) attr, (String) value), i);

            } else if (attr instanceof ContinuousAttribute) {
                // Attributo continuo → crea ContinuousItem
                Double numValue;

                // Gestione conversione: String → Double o già Double
                if (value instanceof String) {
                    numValue = Double.parseDouble((String) value);
                } else {
                    numValue = (Double) value;
                }

                tuple.add(new ContinuousItem((ContinuousAttribute) attr, numValue), i);
            }
        }

        return tuple;
    }

    /**
     * Parsa un file CSV e popola il dataset.
     *
     * @param filename path del file CSV
     * @throws IOException se si verificano errori di I/O
     * @throws InvalidDataFormatException se il formato non è valido
     */
    private void parseCSV(String filename) throws IOException, InvalidDataFormatException {
        File file = new File(filename);

        // Validazioni
        if (!file.exists()) {
            throw new FileNotFoundException("File non trovato: " + filename);
        }
        if (!file.canRead()) {
            throw new IOException("File non leggibile: " + filename);
        }

        List<String[]> rows = new ArrayList<>();
        String[] headers = null;
        int lineNumber = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            // Leggi header (prima riga)
            if ((line = reader.readLine()) != null) {
                lineNumber++;
                headers = line.split(",");
                for (int i = 0; i < headers.length; i++) {
                    headers[i] = headers[i].trim();
                }
            } else {
                throw new InvalidDataFormatException("File CSV vuoto");
            }

            // Leggi dati
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Skip righe vuote
                if (line.isEmpty())
                    continue;

                String[] values = line.split(",");

                // Trim valori
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].trim();
                }

                // Validazione numero colonne
                if (values.length != headers.length) {
                    throw new InvalidDataFormatException("Numero colonne inconsistente: atteso "
                            + headers.length + ", trovato " + values.length, lineNumber);
                }

                rows.add(values);
            }
        }

        // Validazione dati caricati
        if (rows.isEmpty()) {
            throw new InvalidDataFormatException("Nessun dato trovato nel file CSV");
        }

        // Inizializza strutture dati
        numberOfExamples = rows.size();
        int numAttributes = headers.length;

        // Costruisci array di valori per colonna (per inferenza tipo)
        List<List<String>> columnValues = new ArrayList<>();
        for (int i = 0; i < numAttributes; i++) {
            columnValues.add(new ArrayList<>());
        }

        for (String[] row : rows) {
            for (int i = 0; i < numAttributes; i++) {
                columnValues.get(i).add(row[i]);
            }
        }

        // Inferisci tipi attributi
        explanatorySet = new Attribute[numAttributes];
        for (int i = 0; i < numAttributes; i++) {
            explanatorySet[i] = inferAttributeType(headers[i], i, columnValues.get(i));
        }

        // Popola matrice dati
        data = new Object[numberOfExamples][numAttributes];
        for (int i = 0; i < numberOfExamples; i++) {
            for (int j = 0; j < numAttributes; j++) {
                data[i][j] = rows.get(i)[j];
            }
        }
    }

    /**
     * Inferisce il tipo di un attributo dai suoi valori. Se tutti i valori sono numerici, crea
     * ContinuousAttribute, altrimenti crea DiscreteAttribute.
     *
     * @param name nome dell'attributo
     * @param index indice dell'attributo
     * @param values lista di valori dell'attributo
     * @return attributo inferito
     */
    private Attribute inferAttributeType(String name, int index, List<String> values) {
        boolean allNumeric = true;
        Set<String> distinctValues = new HashSet<>();
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (String value : values) {
            // Gestione valori mancanti
            if (value.equals("?") || value.isEmpty() || value.equalsIgnoreCase("NA")) {
                continue;
            }

            distinctValues.add(value);

            // Prova a parsare come numero
            try {
                double numValue = Double.parseDouble(value);
                min = Math.min(min, numValue);
                max = Math.max(max, numValue);
            } catch (NumberFormatException e) {
                allNumeric = false;
            }
        }

        // Decisione tipo attributo
        if (allNumeric && distinctValues.size() > 5) {
            // Attributo continuo
            return new ContinuousAttribute(name, index, min, max);
        } else {
            // Attributo discreto
            String[] distinctArray = distinctValues.toArray(new String[0]);
            return new DiscreteAttribute(name, index, distinctArray);
        }
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
