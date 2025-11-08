package gui.services;

import data.Data;
import mining.ClusterSet;
import mining.QTMiner;
import mining.ClusteringRadiusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Servizio per l'esecuzione del clustering Quality Threshold.
 * Fornisce un wrapper per QTMiner con gestione errori e logging.
 *
 * <p>Il servizio supporta:
 * <ul>
 *   <li>Esecuzione clustering con radius specificato</li>
 *   <li>Salvataggio risultati su file .dmp</li>
 *   <li>Caricamento risultati da file .dmp</li>
 *   <li>Logging dettagliato operazioni</li>
 *   <li>Gestione eccezioni clustering</li>
 * </ul>
 *
 * @author MAP Team
 * @version 1.0.0
 * @since Sprint 2
 */
public class ClusteringService {

    private static final Logger logger = LoggerFactory.getLogger(ClusteringService.class);

    /**
     * Esegue il clustering Quality Threshold sul dataset fornito.
     *
     * @param data il dataset su cui eseguire il clustering
     * @param radius il raggio massimo dei cluster (threshold qualità)
     * @return l'insieme dei cluster scoperti
     * @throws ClusteringRadiusException se il radius è negativo
     * @throws IllegalArgumentException se data è null
     */
    public ClusterSet runClustering(Data data, double radius) throws ClusteringRadiusException {
        if (data == null) {
            logger.error("Tentativo di eseguire clustering con dataset null");
            throw new IllegalArgumentException("Il dataset non può essere null");
        }

        if (radius < 0) {
            logger.error("Radius negativo: {}", radius);
            throw new ClusteringRadiusException("Il radius deve essere non negativo: " + radius);
        }

        logger.info("Inizio clustering con radius={} su dataset con {} tuple",
                radius, data.getNumberOfExamples());

        long startTime = System.currentTimeMillis();

        try {
            // Crea QTMiner e esegue clustering
            QTMiner miner = new QTMiner(radius);
            int numClusters = miner.compute(data);

            long elapsedTime = System.currentTimeMillis() - startTime;

            logger.info("Clustering completato: {} cluster trovati in {}ms",
                    numClusters, elapsedTime);

            return miner.getC();

        } catch (ClusteringRadiusException e) {
            logger.error("Errore durante clustering: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Errore inatteso durante clustering", e);
            throw new RuntimeException("Errore durante clustering: " + e.getMessage(), e);
        }
    }

    /**
     * Salva i risultati del clustering su file.
     *
     * @param filePath percorso del file .dmp dove salvare
     * @param miner l'oggetto QTMiner contenente i risultati
     * @throws IOException se si verifica un errore durante il salvataggio
     */
    public void saveClusteringResults(String filePath, QTMiner miner) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Il percorso file non può essere vuoto");
        }

        if (miner == null) {
            throw new IllegalArgumentException("QTMiner non può essere null");
        }

        logger.info("Salvataggio risultati clustering in: {}", filePath);

        try {
            miner.save(filePath);
            logger.info("Risultati salvati con successo");
        } catch (FileNotFoundException e) {
            logger.error("File non trovato: {}", filePath, e);
            throw new IOException("Impossibile creare il file: " + filePath, e);
        } catch (IOException e) {
            logger.error("Errore I/O durante salvataggio", e);
            throw e;
        }
    }

    /**
     * Carica i risultati del clustering da file.
     *
     * @param filePath percorso del file .dmp da caricare
     * @return l'oggetto QTMiner con i risultati caricati
     * @throws FileNotFoundException se il file non esiste
     * @throws IOException se si verifica un errore durante il caricamento
     * @throws ClassNotFoundException se le classi serializzate non sono trovate
     */
    public QTMiner loadClusteringResults(String filePath)
            throws IOException, ClassNotFoundException {

        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Il percorso file non può essere vuoto");
        }

        logger.info("Caricamento risultati clustering da: {}", filePath);

        try {
            QTMiner miner = QTMiner.load(filePath);
            logger.info("Risultati caricati con successo");
            return miner;

        } catch (FileNotFoundException e) {
            logger.error("File non trovato: {}", filePath, e);
            throw e;
        } catch (IOException e) {
            logger.error("Errore I/O durante caricamento", e);
            throw e;
        } catch (ClassNotFoundException e) {
            logger.error("Classe non trovata durante deserializzazione", e);
            throw e;
        }
    }

    /**
     * Restituisce statistiche sommarie sui risultati del clustering.
     *
     * @param clusterSet l'insieme dei cluster
     * @param data il dataset originale
     * @return una stringa con le statistiche formattate
     */
    public String getClusteringStatistics(ClusterSet clusterSet, Data data) {
        if (clusterSet == null || data == null) {
            return "Nessuna statistica disponibile";
        }

        int numClusters = clusterSet.getSize();
        int numTuples = data.getNumberOfExamples();

        // Gestisci caso cluster vuoto
        if (numClusters == 0) {
            return "Nessun cluster trovato (dataset vuoto o radius troppo restrittivo)";
        }

        // Calcola dimensione media cluster
        double avgClusterSize = (double) numTuples / numClusters;

        // Trova cluster più grande e più piccolo
        int maxSize = 0;
        int minSize = Integer.MAX_VALUE;

        for (int i = 0; i < numClusters; i++) {
            int size = clusterSet.get(i).getSize();
            if (size > maxSize) maxSize = size;
            if (size < minSize) minSize = size;
        }

        StringBuilder stats = new StringBuilder();
        stats.append("Statistiche Clustering\n");
        stats.append("======================\n\n");
        stats.append(String.format("Numero totale cluster: %d\n", numClusters));
        stats.append(String.format("Numero totale tuple: %d\n", numTuples));
        stats.append(String.format("Dimensione media cluster: %.2f\n", avgClusterSize));
        stats.append(String.format("Cluster più grande: %d tuple\n", maxSize));
        stats.append(String.format("Cluster più piccolo: %d tuple\n", minSize));

        return stats.toString();
    }
}
