package data;

import database.*;
import exceptions.InvalidDataFormatException;
import data.*;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Classe che modella l'insieme di transazioni (tuple).
 * Supporta caricamento da: hardcoded data, file CSV, database MySQL.
 */
public class Data {
    private List<Example> data;
    private int numberOfExamples;
    private List<Attribute> explanatorySet;

    /**
     * Costruttore della classe Data. Inizializza con dati hardcoded PlayTennis.
     */
    public Data() {
        // Inizializza data come ArrayList di Example
        data = new ArrayList<>();

        // Popolamento dei dati PlayTennis (QT06 format: Temperature continuo)
        data.add(createExample(new Object[] {"sunny", 30.3, "high", "weak", "no"}));
        data.add(createExample(new Object[] {"sunny", 30.3, "high", "strong", "no"}));
        data.add(createExample(new Object[] {"overcast", 30.0, "high", "weak", "yes"}));
        data.add(createExample(new Object[] {"rain", 13.0, "high", "weak", "yes"}));
        data.add(createExample(new Object[] {"rain", 0.0, "normal", "weak", "yes"}));
        data.add(createExample(new Object[] {"rain", 0.0, "normal", "strong", "no"}));
        data.add(createExample(new Object[] {"overcast", 0.1, "normal", "strong", "yes"}));
        data.add(createExample(new Object[] {"sunny", 13.0, "high", "weak", "no"}));
        data.add(createExample(new Object[] {"sunny", 0.1, "normal", "weak", "yes"}));
        data.add(createExample(new Object[] {"rain", 12.0, "normal", "weak", "yes"}));
        data.add(createExample(new Object[] {"sunny", 12.5, "normal", "strong", "yes"}));
        data.add(createExample(new Object[] {"overcast", 12.5, "high", "strong", "yes"}));
        data.add(createExample(new Object[] {"overcast", 29.21, "normal", "weak", "yes"}));
        data.add(createExample(new Object[] {"rain", 12.5, "high", "strong", "no"}));

        numberOfExamples = data.size();

        // Inizializzazione explanatorySet con LinkedList
        explanatorySet = new LinkedList<>();

        // Outlook attribute
        String[] outLookValues = new String[] {"overcast", "rain", "sunny"};
        explanatorySet.add(new DiscreteAttribute("Outlook", 0, outLookValues));

        // Temperature attribute (QT06: ora continuo)
        explanatorySet.add(new ContinuousAttribute("Temperature", 1, 3.2, 38.7));

        // Humidity attribute
        String[] humidityValues = new String[] {"high", "normal"};
        explanatorySet.add(new DiscreteAttribute("Humidity", 2, humidityValues));

        // Wind attribute
        String[] windValues = new String[] {"strong", "weak"};
        explanatorySet.add(new DiscreteAttribute("Wind", 3, windValues));

        // PlayTennis attribute
        String[] playTennisValues = new String[] {"no", "yes"};
        explanatorySet.add(new DiscreteAttribute("PlayTennis", 4, playTennisValues));
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
     * Costruttore che carica dataset da tabella database MySQL (QT07).
     *
     * @param tableName nome della tabella nel database MapDB
     * @throws SQLException in caso di errore SQL
     * @throws EmptySetException se la tabella è vuota
     * @throws DatabaseConnectionException se la connessione al database fallisce
     * @throws NoValueException se valori aggregati non trovati
     */
    public Data(String tableName, boolean fromDatabase)
            throws SQLException, EmptySetException, DatabaseConnectionException, NoValueException {
        if (!fromDatabase) {
            throw new IllegalArgumentException("Usare costruttore Data(String) per CSV");
        }

        // Connessione al database
        DbAccess db = new DbAccess();
        db.initConnection();

        try {
            // Carica transazioni dal database
            TableData tableData = new TableData(db);
            data = tableData.getDistinctTransazioni(tableName);
            numberOfExamples = data.size();

            // Costruisci schema attributi
            TableSchema schema = new TableSchema(db, tableName);
            explanatorySet = new LinkedList<>();

            for (int i = 0; i < schema.getNumberOfAttributes(); i++) {
                TableSchema.Column col = schema.getColumn(i);

                if (col.isNumber()) {
                    // Attributo continuo: ricava min e max
                    Float minObj = (Float) tableData.getAggregateColumnValue(tableName, col, QUERY_TYPE.MIN);
                    Float maxObj = (Float) tableData.getAggregateColumnValue(tableName, col, QUERY_TYPE.MAX);
                    double min = minObj.doubleValue();
                    double max = maxObj.doubleValue();
                    explanatorySet.add(new ContinuousAttribute(col.getColumnName(), i, min, max));
                } else {
                    // Attributo discreto: ricava valori distinti
                    Set<Object> values = tableData.getDistinctColumnValues(tableName, col);
                    String[] valuesArray = new String[values.size()];
                    int idx = 0;
                    for (Object v : values) {
                        valuesArray[idx++] = (String) v;
                    }
                    explanatorySet.add(new DiscreteAttribute(col.getColumnName(), i, valuesArray));
                }
            }
        } finally {
            db.closeConnection();
        }
    }

    /**
     * Helper per creare un Example da un array di Object.
     *
     * @param values array di valori
     * @return Example popolato
     */
    private Example createExample(Object[] values) {
        Example ex = new Example();
        for (Object v : values) {
            ex.add(v);
        }
        return ex;
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
        return explanatorySet.size();
    }

    /**
     * Restituisce lo schema degli attributi.
     *
     * @return lista di attributi
     */
    public List<Attribute> getAttributeSchema() {
        return explanatorySet;
    }

    /**
     * Restituisce l'attributo in posizione index.
     *
     * @param index indice dell'attributo
     * @return attributo in posizione index
     */
    public Attribute getExplanatoryAttribute(int index) {
        return explanatorySet.get(index);
    }

    /**
     * Restituisce il valore di un attributo in una specifica transazione.
     *
     * @param exampleIndex indice di riga
     * @param attributeIndex indice di colonna
     * @return valore dell'attributo
     */
    public Object getValue(int exampleIndex, int attributeIndex) {
        return data.get(exampleIndex).get(attributeIndex);
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
        for (int i = 0; i < explanatorySet.size(); i++) {
            str += explanatorySet.get(i).getName();
            if (i < explanatorySet.size() - 1) {
                str += ",";
            }
        }
        str += "\n";

        // Transazioni
        for (int i = 0; i < numberOfExamples; i++) {
            str += i + ":";
            for (int j = 0; j < explanatorySet.size(); j++) {
                str += data.get(i).get(j);
                if (j < explanatorySet.size() - 1) {
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
     * <p>
     * Questo metodo gestisce sia attributi discreti che continui, creando il tipo appropriato di
     * Item per ciascun attributo:
     * </p>
     * <ul>
     * <li>DiscreteAttribute → DiscreteItem</li>
     * <li>ContinuousAttribute → ContinuousItem</li>
     * </ul>
     *
     * @param index indice di riga
     * @return tupla corrispondente alla riga
     * @throws NumberFormatException se un valore continuo non può essere parsato come Double
     */
    public Tuple getItemSet(int index) {
        Tuple tuple = new Tuple(explanatorySet.size());

        for (int i = 0; i < explanatorySet.size(); i++) {
            Attribute attr = explanatorySet.get(i);
            Object value = data.get(index).get(i);

            if (attr instanceof DiscreteAttribute) {
                // Attributo discreto → crea DiscreteItem
                tuple.add(new DiscreteItem((DiscreteAttribute) attr, (String) value), i);

            } else if (attr instanceof ContinuousAttribute) {
                // Attributo continuo → crea ContinuousItem
                Double numValue;

                // Gestione conversione: String → Double o già Double
                if (value instanceof String) {
                    numValue = Double.parseDouble((String) value);
                } else if (value instanceof Float) {
                    numValue = ((Float) value).doubleValue();
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
        explanatorySet = new LinkedList<>();
        for (int i = 0; i < numAttributes; i++) {
            explanatorySet.add(inferAttributeType(headers[i], i, columnValues.get(i)));
        }

        // Popola lista Example
        data = new ArrayList<>();
        for (String[] row : rows) {
            data.add(createExample(row));
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
