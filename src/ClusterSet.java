import exceptions.InvalidFileFormatException;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe che rappresenta un insieme di cluster.
 */
public class ClusterSet {
    private Cluster C[] = new Cluster[0];

    /**
     * Costruttore della classe ClusterSet.
     */
    public ClusterSet() {
    }

    /**
     * Costruttore che carica un insieme di cluster da file.
     *
     * @param filename path del file .dmp da caricare
     * @param data dataset di riferimento per ricostruire i centroidi
     * @throws IOException se si verificano errori di I/O
     * @throws InvalidFileFormatException se il formato del file non è valido
     */
    public ClusterSet(String filename, Data data) throws IOException, InvalidFileFormatException {
        loadFromFile(filename, data);
    }

    /**
     * Aggiunge un cluster all'insieme.
     *
     * @param c cluster da aggiungere
     */
    public void add(Cluster c) {
        Cluster tempC[] = new Cluster[C.length + 1];
        for (int i = 0; i < C.length; i++)
            tempC[i] = C[i];
        tempC[C.length] = c;
        C = tempC;
    }

    /**
     * Restituisce il cluster in posizione i.
     *
     * @param i posizione del cluster
     * @return cluster in posizione i
     */
    public Cluster get(int i) {
        return C[i];
    }

    /**
     * Restituisce una stringa con i centroidi di tutti i cluster.
     *
     * @return stringa con i centroidi
     */
    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < C.length; i++) {
            if (C[i] != null) {
                str += i + ":" + C[i].toString() + "\n";
            }
        }
        return str;
    }

    /**
     * Restituisce una stringa dettagliata con lo stato di ciascun cluster.
     *
     * @param data insieme di dati
     * @return stringa dettagliata dei cluster
     */
    public String toString(Data data) {
        String str = "";
        for (int i = 0; i < C.length; i++) {
            if (C[i] != null) {
                str += i + ":" + C[i].toString(data) + "\n";
            }
        }
        return str;
    }

    /**
     * Restituisce il numero di cluster nell'insieme.
     *
     * @return numero di cluster
     */
    public int getNumClusters() {
        return C.length;
    }

    /**
     * Salva l'insieme di cluster su file in formato .dmp.
     *
     * @param filename path del file di destinazione
     * @param radius raggio usato per il clustering
     * @throws IOException se si verificano errori di I/O
     */
    public void save(String filename, double radius) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Scrivi metadata
            writer.write("---\n");
            writer.write("METADATA\n");
            writer.write("radius=" + radius + "\n");
            writer.write("numClusters=" + C.length + "\n");

            // Timestamp
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            writer.write("timestamp=" + now.format(formatter) + "\n");
            writer.write("---\n");

            // Scrivi ogni cluster
            for (int i = 0; i < C.length; i++) {
                if (C[i] != null) {
                    writer.write("CLUSTER " + i + "\n");

                    // Centroide
                    Tuple centroid = C[i].getCentroid();
                    writer.write("centroid=");
                    for (int j = 0; j < centroid.getLength(); j++) {
                        if (j > 0) writer.write(",");
                        writer.write(centroid.get(j).getValue().toString());
                    }
                    writer.write("\n");

                    // ID tuple
                    int[] tupleIDs = C[i].iterator();
                    writer.write("tupleIDs=");
                    for (int j = 0; j < tupleIDs.length; j++) {
                        if (j > 0) writer.write(",");
                        writer.write(String.valueOf(tupleIDs[j]));
                    }
                    writer.write("\n");
                    writer.write("---\n");
                }
            }
        }
    }

    /**
     * Carica un insieme di cluster da file .dmp.
     *
     * @param filename path del file da caricare
     * @param data dataset di riferimento
     * @throws IOException se si verificano errori di I/O
     * @throws InvalidFileFormatException se il formato non è valido
     */
    private void loadFromFile(String filename, Data data) throws IOException, InvalidFileFormatException {
        File file = new File(filename);

        // Validazioni
        if (!file.exists()) {
            throw new FileNotFoundException("File non trovato: " + filename);
        }
        if (!file.canRead()) {
            throw new IOException("File non leggibile: " + filename);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean metadataFound = false;
            int expectedClusters = 0;
            int clustersLoaded = 0;

            // Parsing
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip linee vuote
                if (line.isEmpty()) continue;

                // Metadata section
                if (line.equals("METADATA")) {
                    metadataFound = true;
                    continue;
                }

                // Leggi metadata
                if (metadataFound && line.startsWith("numClusters=")) {
                    try {
                        expectedClusters = Integer.parseInt(line.substring("numClusters=".length()));
                    } catch (NumberFormatException e) {
                        throw new InvalidFileFormatException("Numero cluster non valido: " + line);
                    }
                    continue;
                }

                // Cluster section
                if (line.startsWith("CLUSTER ")) {
                    Cluster cluster = parseCluster(reader, data);
                    if (cluster != null) {
                        add(cluster);
                        clustersLoaded++;
                    }
                }
            }

            // Validazione finale
            if (!metadataFound) {
                throw new InvalidFileFormatException("Header METADATA mancante");
            }
            if (expectedClusters > 0 && clustersLoaded != expectedClusters) {
                throw new InvalidFileFormatException(
                    "Numero cluster inconsistente: atteso " + expectedClusters +
                    ", caricato " + clustersLoaded);
            }
        }
    }

    /**
     * Parsa un singolo cluster dal file.
     *
     * @param reader reader del file
     * @param data dataset di riferimento
     * @return cluster parsato
     * @throws IOException se si verificano errori di I/O
     * @throws InvalidFileFormatException se il formato non è valido
     */
    private Cluster parseCluster(BufferedReader reader, Data data)
            throws IOException, InvalidFileFormatException {
        String centroidLine = reader.readLine();
        String tupleIDsLine = reader.readLine();

        if (centroidLine == null || tupleIDsLine == null) {
            throw new InvalidFileFormatException("Cluster incompleto");
        }

        // Parsa centroide
        if (!centroidLine.startsWith("centroid=")) {
            throw new InvalidFileFormatException("Formato centroide non valido: " + centroidLine);
        }
        String centroidData = centroidLine.substring("centroid=".length());
        String[] centroidValues = centroidData.split(",");

        // Crea tupla centroide
        Tuple centroid = new Tuple(centroidValues.length);
        for (int i = 0; i < centroidValues.length; i++) {
            // Recupera attributo da data
            if (i >= data.getNumberOfExplanatoryAttributes()) {
                throw new InvalidFileFormatException(
                    "Numero attributi centroide (" + centroidValues.length +
                    ") non compatibile con dataset (" + data.getNumberOfExplanatoryAttributes() + ")");
            }
            Attribute attr = data.getExplanatoryAttribute(i);
            DiscreteItem item = new DiscreteItem((DiscreteAttribute)attr, centroidValues[i]);
            centroid.add(item, i);
        }

        // Crea cluster
        Cluster cluster = new Cluster(centroid);

        // Parsa tuple IDs
        if (!tupleIDsLine.startsWith("tupleIDs=")) {
            throw new InvalidFileFormatException("Formato tupleIDs non valido: " + tupleIDsLine);
        }
        String tupleIDsData = tupleIDsLine.substring("tupleIDs=".length());
        String[] tupleIDs = tupleIDsData.split(",");

        for (String idStr : tupleIDs) {
            try {
                int id = Integer.parseInt(idStr.trim());
                if (id < 0 || id >= data.getNumberOfExamples()) {
                    throw new InvalidFileFormatException(
                        "ID tupla non valido: " + id + " (dataset ha " +
                        data.getNumberOfExamples() + " esempi)");
                }
                cluster.addData(id);
            } catch (NumberFormatException e) {
                throw new InvalidFileFormatException("ID tupla non numerico: " + idStr);
            }
        }

        return cluster;
    }
}
