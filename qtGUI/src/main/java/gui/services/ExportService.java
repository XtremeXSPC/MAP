package gui.services;

//===---------------------------------------------------------------------------===//
// Importazioni Java standard.
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import data.Data;
import data.Tuple;
import gui.models.ClusteringResult;
import mining.Cluster;
import mining.ClusterSet;
//===---------------------------------------------------------------------------===//

/**
 * Servizio per l'esportazione dei risultati di clustering in vari formati.
 * <p>
 * Supporta esportazione in CSV, TXT (report) e ZIP (pacchetto completo).
 *
 * <p>Formati supportati:
 * <ul>
 *   <li>CSV: Dati tabulari con ClusterID, TupleID, Distance</li>
 *   <li>TXT: Report testuale dettagliato con statistiche</li>
 *   <li>ZIP: Archivio contenente .dmp + CSV + report TXT</li>
 * </ul>
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExportService {

    // Logger per la classe ExportService.
    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruttore di default.
     * Crea una nuova istanza del servizio di esportazione.
     */
    public ExportService() {
        // Costruttore vuoto.
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Esporta i risultati del clustering in formato CSV.
     * Il file CSV contiene le colonne: ClusterID, TupleID, Distance, Attributes...
     *
     * @param filePath percorso del file CSV di destinazione
     * @param result risultato del clustering da esportare
     * @throws IOException se si verifica un errore durante l'esportazione
     * @throws IllegalArgumentException se i parametri sono null o invalidi
     */
    public void exportToCsv(String filePath, ClusteringResult result) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Il percorso file non può essere vuoto");
        }
        if (result == null) {
            throw new IllegalArgumentException("ClusteringResult non può essere null");
        }

        logger.info("Esportazione CSV in: {}", filePath);

        ClusterSet clusterSet = result.getClusterSet();
        Data data = result.getData();

        try (BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            // Scrivi intestazione CSV.
            writer.write("ClusterID,TupleID,DistanceFromCentroid");

            // Aggiungi nomi attributi.
            for (int i = 0; i < data.getNumberOfExplanatoryAttributes(); i++) {
                writer.write(",");
                writer.write(data.getExplanatoryAttribute(i).getName());
            }
            writer.newLine();

            // Itera sui cluster.
            int clusterIndex = 0;
            for (Cluster cluster : clusterSet) {
                clusterIndex++;
                Tuple centroid = cluster.getCentroid();
                int[] tupleIds = cluster.getTupleIDs();

                // Itera sulle tuple del cluster.
                for (int tupleId : tupleIds) {
                    Tuple tuple = data.getItemSet(tupleId);
                    double distance = centroid.getDistance(tuple);

                    // Scrivi riga: ClusterID, TupleID, Distance.
                    writer.write(String.format("%d,%d,%.6f", clusterIndex, tupleId, distance));

                    // Scrivi valori attributi.
                    for (int i = 0; i < data.getNumberOfExplanatoryAttributes(); i++) {
                        writer.write(",");
                        writer.write(escapeCSV(tuple.get(i).getValue().toString()));
                    }
                    writer.newLine();
                }
            }

            logger.info("Esportazione CSV completata: {} righe", getTotalTuples(clusterSet));

        } catch (IOException e) {
            logger.error("Errore durante esportazione CSV", e);
            throw new IOException("Impossibile esportare in CSV: " + e.getMessage(), e);
        }
    }

    /**
     * Esporta un report testuale dettagliato dei risultati del clustering.
     *
     * @param filePath percorso del file TXT di destinazione
     * @param result risultato del clustering da esportare
     * @throws IOException se si verifica un errore durante l'esportazione
     * @throws IllegalArgumentException se i parametri sono null o invalidi
     */
    public void exportToTextReport(String filePath, ClusteringResult result) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Il percorso file non può essere vuoto");
        }
        if (result == null) {
            throw new IllegalArgumentException("ClusteringResult non può essere null");
        }

        logger.info("Esportazione report TXT in: {}", filePath);

        ClusterSet clusterSet = result.getClusterSet();
        Data data = result.getData();

        try (BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            // Intestazione report.
            writer.write("# ======================================================== #\n");
            writer.write("# --- QUALITY THRESHOLD CLUSTERING - REPORT RISULTATI ---  #\n");
            writer.write("# ======================================================== #\n\n");

            // Informazioni generali.
            writer.write("Data/Ora generazione: " + result.getFormattedTimestamp() + "\n");
            writer.write("Radius utilizzato: " + String.format("%.6f", result.getRadius()) + "\n");
            writer.write("Tempo esecuzione: " + result.getFormattedExecutionTime() + "\n\n");

            writer.write("# ----------------- STATISTICHE GLOBALI ------------------ #\n");
            writer.write("\n");
            writer.write("Numero totale cluster: " + result.getNumClusters() + "\n");
            writer.write("Numero totale tuple: " + result.getNumTuples() + "\n");
            writer.write("Numero attributi: " + data.getNumberOfExplanatoryAttributes() + "\n");

            // Calcola statistiche aggiuntive.
            List<Cluster> clusters = new ArrayList<>();
            for (Cluster c : clusterSet) {
                clusters.add(c);
            }

            if (!clusters.isEmpty()) {
                int maxSize = 0;
                int minSize = Integer.MAX_VALUE;
                int totalSize = 0;

                for (Cluster cluster : clusters) {
                    int size = cluster.getSize();
                    totalSize += size;
                    if (size > maxSize)
                        maxSize = size;
                    if (size < minSize)
                        minSize = size;
                }

                double avgSize = (double) totalSize / clusters.size();

                writer.write("Dimensione media cluster: " + String.format("%.2f", avgSize) + "\n");
                writer.write("Cluster più grande: " + maxSize + " tuple\n");
                writer.write("Cluster più piccolo: " + minSize + " tuple\n\n");
            }

            // Dettaglio cluster.
            writer.write("# ======================================================== #\n");
            writer.write("# ------------------ DETTAGLIO CLUSTER ------------------- #\n");
            writer.write("# ======================================================== #\n\n");

            int clusterIndex = 0;
            for (Cluster cluster : clusterSet) {
                clusterIndex++;
                writer.write("# --------------------------------------------------- #\n");
                writer.write("CLUSTER " + clusterIndex + "\n");
                writer.write("# --------------------------------------------------- #\n");
                writer.write("Dimensione: " + cluster.getSize() + " tuple\n");
                writer.write("Centroide: " + cluster.getCentroid().toString() + "\n\n");

                // Statistiche distanze.
                Tuple centroid = cluster.getCentroid();
                int[] tupleIds = cluster.getTupleIDs();

                double minDist = Double.MAX_VALUE;
                double maxDist = 0;
                double sumDist = 0;

                for (int tupleId : tupleIds) {
                    double dist = centroid.getDistance(data.getItemSet(tupleId));
                    if (dist < minDist)
                        minDist = dist;
                    if (dist > maxDist)
                        maxDist = dist;
                    sumDist += dist;
                }

                double avgDist = sumDist / tupleIds.length;

                writer.write("Distanza minima dal centroide: " + String.format("%.6f", minDist) + "\n");
                writer.write("Distanza massima dal centroide: " + String.format("%.6f", maxDist) + "\n");
                writer.write("Distanza media dal centroide: " + String.format("%.6f", avgDist) + "\n\n");

                // Elenca tuple (limita a prime 20 se cluster troppo grande).
                int maxTuplesToShow = Math.min(tupleIds.length, 20);
                writer.write("Tuple (mostrando " + maxTuplesToShow + " di " + tupleIds.length + "):\n");

                for (int i = 0; i < maxTuplesToShow; i++) {
                    int tupleId = tupleIds[i];
                    Tuple tuple = data.getItemSet(tupleId);
                    double distance = centroid.getDistance(tuple);

                    writer.write(String.format("  [%d] Tupla %d - distanza: %.6f\n", i + 1, tupleId, distance));
                    writer.write("      " + tuple.toString() + "\n");
                }

                if (tupleIds.length > maxTuplesToShow) {
                    writer.write("  ... e altre " + (tupleIds.length - maxTuplesToShow) + " tuple\n");
                }

                writer.write("\n");
            }

            // Footer.
            writer.write("# ======================================================== #\n");
            writer.write("# -------- Fine Report - QT Clustering GUI v1.0.0 -------- #\n");
            writer.write("# ======================================================== #\n");

            logger.info("Esportazione report TXT completata");

        } catch (IOException e) {
            logger.error("Errore durante esportazione report TXT", e);
            throw new IOException("Impossibile esportare report: " + e.getMessage(), e);
        }
    }

    /**
     * Esporta un pacchetto completo contenente tutti i formati:
     * - file .dmp (clustering serializzato)
     * - file CSV con i dati
     * - report TXT dettagliato
     *
     * @param zipFilePath percorso del file ZIP di destinazione
     * @param result risultato del clustering da esportare
     * @throws IOException se si verifica un errore durante l'esportazione
     * @throws IllegalArgumentException se i parametri sono null o invalidi
     */
    public void exportToZip(String zipFilePath, ClusteringResult result) throws IOException {
        if (zipFilePath == null || zipFilePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Il percorso file ZIP non può essere vuoto");
        }
        if (result == null) {
            throw new IllegalArgumentException("ClusteringResult non può essere null");
        }

        logger.info("Esportazione pacchetto ZIP in: {}", zipFilePath);

        // Crea timestamp per nomi file.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String baseName = "clustering_" + timestamp;

        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath))) {

            // 1. Aggiungi file .dmp
            String dmpFileName = baseName + ".dmp";
            logger.info("Aggiungendo file DMP: {}", dmpFileName);

            // Crea file temporaneo per .dmp
            File tempDmpFile = File.createTempFile("clustering_", ".dmp");
            try {
                ClusteringService clusteringService = new ClusteringService();
                clusteringService.saveClusteringResults(tempDmpFile.getAbsolutePath(), result.getMiner());

                addFileToZip(zipOut, tempDmpFile, dmpFileName);
            } finally {
                tempDmpFile.delete();
            }

            // 2. Aggiungi file CSV.
            String csvFileName = baseName + ".csv";
            logger.info("Aggiungendo file CSV: {}", csvFileName);

            File tempCsvFile = File.createTempFile("clustering_", ".csv");
            try {
                exportToCsv(tempCsvFile.getAbsolutePath(), result);
                addFileToZip(zipOut, tempCsvFile, csvFileName);
            } finally {
                tempCsvFile.delete();
            }

            // 3. Aggiungi report TXT.
            String txtFileName = baseName + "_report.txt";
            logger.info("Aggiungendo report TXT: {}", txtFileName);

            File tempTxtFile = File.createTempFile("clustering_", ".txt");
            try {
                exportToTextReport(tempTxtFile.getAbsolutePath(), result);
                addFileToZip(zipOut, tempTxtFile, txtFileName);
            } finally {
                tempTxtFile.delete();
            }

            // 4. Aggiungi file README con informazioni.
            String readmeFileName = "README.txt";
            logger.info("Aggiungendo README: {}", readmeFileName);
            addReadmeToZip(zipOut, readmeFileName, result);

            logger.info("Esportazione pacchetto ZIP completata con successo");

        } catch (IOException e) {
            logger.error("Errore durante creazione ZIP", e);
            throw new IOException("Impossibile creare file ZIP: " + e.getMessage(), e);
        }
    }

    //===--------------------------- PRIVATE HELPERS ---------------------------===//

    /**
     * Aggiunge un file al file ZIP.
     *
     * @param zipOut stream ZIP di output
     * @param file file da aggiungere
     * @param entryName nome dell'entry nel ZIP
     * @throws IOException se si verifica un errore
     */
    private void addFileToZip(ZipOutputStream zipOut, File file, String entryName) throws IOException {
        ZipEntry zipEntry = new ZipEntry(entryName);
        zipOut.putNextEntry(zipEntry);

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zipOut.write(buffer, 0, length);
            }
        }

        zipOut.closeEntry();
    }

    /**
     * Aggiunge un file README informativo al ZIP.
     *
     * @param zipOut stream ZIP di output
     * @param entryName nome dell'entry README
     * @param result risultato del clustering
     * @throws IOException se si verifica un errore
     */
    private void addReadmeToZip(ZipOutputStream zipOut, String entryName, ClusteringResult result) throws IOException {
        ZipEntry zipEntry = new ZipEntry(entryName);
        zipOut.putNextEntry(zipEntry);

        StringBuilder readme = new StringBuilder();
        readme.append("QT CLUSTERING - PACCHETTO ESPORTAZIONE\n");
        readme.append("# ========================================= #\n\n");
        readme.append("Questo archivio contiene i risultati completi di un'analisi di clustering.\n\n");
        readme.append("CONTENUTO:\n");
        readme.append("------------------------------\n");
        readme.append("- .dmp file: Clustering serializzato (formato Java, ricaricabile nell'applicazione)\n");
        readme.append("- .csv file: Dati in formato tabellare (apribile con Excel, LibreOffice, etc.)\n");
        readme.append("- _report.txt: Report dettagliato con statistiche e dettagli cluster\n\n");
        readme.append("INFORMAZIONI CLUSTERING:\n");
        readme.append("------------------------------\n");
        readme.append("Data/Ora: " + result.getFormattedTimestamp() + "\n");
        readme.append("Radius: " + result.getRadius() + "\n");
        readme.append("Cluster trovati: " + result.getNumClusters() + "\n");
        readme.append("Tuple totali: " + result.getNumTuples() + "\n");
        readme.append("Tempo esecuzione: " + result.getFormattedExecutionTime() + "\n\n");
        readme.append("PER RICARICARE IL CLUSTERING:\n");
        readme.append("------------------------------\n");
        readme.append("1. Apri QT Clustering GUI\n");
        readme.append("2. File > Apri\n");
        readme.append("3. Seleziona il file .dmp\n\n");
        readme.append("Generato da: QT Clustering GUI v1.0.0\n");
        readme.append("# ========================================= #\n");

        zipOut.write(readme.toString().getBytes(StandardCharsets.UTF_8));
        zipOut.closeEntry();
    }

    /**
     * Escapa caratteri speciali per formato CSV.
     *
     * @param value valore da escapare
     * @return valore escapato
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }

        // Se contiene virgola, virgolette o newline, racchiudi tra virgolette
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            // Duplica le virgolette
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }

        return value;
    }

    /**
     * Conta il numero totale di tuple in tutti i cluster.
     *
     * @param clusterSet insieme dei cluster
     * @return numero totale di tuple
     */
    private int getTotalTuples(ClusterSet clusterSet) {
        int total = 0;
        for (Cluster cluster : clusterSet) {
            total += cluster.getSize();
        }
        return total;
    }
}

//===---------------------------------------------------------------------------===//
