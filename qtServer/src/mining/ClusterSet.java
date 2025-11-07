package mining;

import mining.InvalidFileFormatException;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import data.*;

/**
 * Classe che rappresenta un insieme di cluster. Usa TreeSet per ordinamento automatico
 * per dimensione e implementa Iterable per iterazione. Implementa Serializable per
 * permettere la serializzazione binaria.
 */
public class ClusterSet implements Iterable<Cluster>, Serializable {
    private Set<Cluster> C;

    /**
     * Costruttore della classe ClusterSet.
     */
    public ClusterSet() {
        C = new TreeSet<>();
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
        C = new TreeSet<>();
        loadFromFile(filename, data);
    }

    /**
     * Aggiunge un cluster all'insieme. TreeSet garantisce ordinamento automatico per
     * dimensione cluster.
     *
     * @param c cluster da aggiungere
     */
    public void add(Cluster c) {
        C.add(c);
    }

    /**
     * Restituisce un iteratore sui cluster. I cluster sono ordinati automaticamente per
     * dimensione (crescente).
     *
     * @return iteratore sui cluster
     */
    @Override
    public Iterator<Cluster> iterator() {
        return C.iterator();
    }

    /**
     * Restituisce una stringa con i centroidi di tutti i cluster. I cluster sono numerati
     * sequenzialmente (ordinati per dimensione).
     *
     * @return stringa con i centroidi
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        int index = 1;
        for (Cluster cluster : this) {
            str.append(index++).append(":").append(cluster.toString()).append("\n");
        }
        return str.toString();
    }

    /**
     * Restituisce una stringa dettagliata con lo stato di ciascun cluster. I cluster sono
     * numerati sequenzialmente (ordinati per dimensione).
     *
     * @param data insieme di dati
     * @return stringa dettagliata dei cluster
     */
    public String toString(Data data) {
        StringBuilder str = new StringBuilder();
        int index = 1;
        for (Cluster cluster : this) {
            str.append(index++).append(":").append(cluster.toString(data)).append("\n");
        }
        return str.toString();
    }

    /**
     * Restituisce il numero di cluster nell'insieme.
     *
     * @return numero di cluster
     */
    public int getNumClusters() {
        return C.size();
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
            writer.write("numClusters=" + C.size() + "\n");

            // Timestamp
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            writer.write("timestamp=" + now.format(formatter) + "\n");
            writer.write("---\n");

            // Scrivi ogni cluster (ordinati per dimensione)
            int index = 0;
            for (Cluster cluster : this) {
                writer.write("CLUSTER " + index + "\n");

                // Centroide
                Tuple centroid = cluster.getCentroid();
                writer.write("centroid=");
                for (int j = 0; j < centroid.getLength(); j++) {
                    if (j > 0)
                        writer.write(",");
                    writer.write(centroid.get(j).getValue().toString());
                }
                writer.write("\n");

                // ID tuple
                int[] tupleIDs = cluster.getTupleIDs();
                writer.write("tupleIDs=");
                for (int j = 0; j < tupleIDs.length; j++) {
                    if (j > 0)
                        writer.write(",");
                    writer.write(String.valueOf(tupleIDs[j]));
                }
                writer.write("\n");
                writer.write("---\n");
                index++;
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
                if (line.isEmpty())
                    continue;

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
                        "Numero cluster inconsistente: atteso " + expectedClusters + ", caricato " + clustersLoaded);
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
    private Cluster parseCluster(BufferedReader reader, Data data) throws IOException, InvalidFileFormatException {
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
                throw new InvalidFileFormatException("Numero attributi centroide (" + centroidValues.length
                        + ") non compatibile con dataset (" + data.getNumberOfExplanatoryAttributes() + ")");
            }
            Attribute attr = data.getExplanatoryAttribute(i);
            DiscreteItem item = new DiscreteItem((DiscreteAttribute) attr, centroidValues[i]);
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
                            "ID tupla non valido: " + id + " (dataset ha " + data.getNumberOfExamples() + " esempi)");
                }
                cluster.addData(id);
            } catch (NumberFormatException e) {
                throw new InvalidFileFormatException("ID tupla non numerico: " + idStr);
            }
        }

        return cluster;
    }
}
