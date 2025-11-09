package gui.models;

import data.Data;
import mining.ClusterSet;
import mining.QTMiner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Modello che incapsula i risultati di un'esecuzione del clustering.
 * Contiene:
 * <ul>
 *   <li>L'insieme dei cluster scoperti (ClusterSet)</li>
 *   <li>Il dataset originale</li>
 *   <li>I parametri usati (radius)</li>
 *   <li>Metadati (timestamp, tempo esecuzione)</li>
 * </ul>
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 */
public class ClusteringResult {

    private final ClusterSet clusterSet;
    private final Data data;
    private final double radius;
    private final LocalDateTime timestamp;
    private final long executionTimeMs;
    private final QTMiner miner; // Conserviamo il miner per salvataggio

    /**
     * Costruisce un risultato di clustering.
     *
     * @param clusterSet l'insieme dei cluster scoperti
     * @param data il dataset originale
     * @param radius il radius usato
     * @param executionTimeMs tempo di esecuzione in millisecondi
     * @param miner l'oggetto QTMiner (per salvataggio)
     */
    public ClusteringResult(ClusterSet clusterSet, Data data, double radius, long executionTimeMs, QTMiner miner) {
        this.clusterSet = Objects.requireNonNull(clusterSet, "ClusterSet non può essere null");
        this.data = Objects.requireNonNull(data, "Data non può essere null");
        this.miner = Objects.requireNonNull(miner, "QTMiner non può essere null");

        if (radius < 0) {
            throw new IllegalArgumentException("Radius deve essere non negativo, ricevuto: " + radius);
        }
        if (executionTimeMs < 0) {
            throw new IllegalArgumentException("Execution time deve essere non negativo, ricevuto: " + executionTimeMs);
        }

        this.radius = radius;
        this.executionTimeMs = executionTimeMs;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * @return l'insieme dei cluster
     */
    public ClusterSet getClusterSet() {
        return clusterSet;
    }

    /**
     * @return il dataset originale
     */
    public Data getData() {
        return data;
    }

    /**
     * @return il radius usato
     */
    public double getRadius() {
        return radius;
    }

    /**
     * @return il timestamp di generazione
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * @return il tempo di esecuzione in millisecondi
     */
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    /**
     * @return il tempo di esecuzione formattato (es. "1.5s", "250ms")
     */
    public String getFormattedExecutionTime() {
        if (executionTimeMs < 1000) {
            return executionTimeMs + "ms";
        } else if (executionTimeMs < 60000) {
            return String.format("%.2fs", executionTimeMs / 1000.0);
        } else {
            long seconds = executionTimeMs / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            return String.format("%dm %ds", minutes, seconds);
        }
    }

    /**
     * @return il timestamp formattato
     */
    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return timestamp.format(formatter);
    }

    /**
     * @return il numero di cluster trovati
     */
    public int getNumClusters() {
        return clusterSet != null ? clusterSet.getNumClusters() : 0;
    }

    /**
     * @return il numero totale di tuple
     */
    public int getNumTuples() {
        return data != null ? data.getNumberOfExamples() : 0;
    }

    /**
     * @return l'oggetto QTMiner (per operazioni avanzate)
     */
    public QTMiner getMiner() {
        return miner;
    }

    /**
     * @return riepilogo testuale del risultato
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Risultati Clustering Quality Threshold\n");
        summary.append("======================================\n\n");
        summary.append(String.format("Data/Ora: %s\n", getFormattedTimestamp()));
        summary.append(String.format("Radius: %.3f\n", radius));
        summary.append(String.format("Cluster trovati: %d\n", getNumClusters()));
        summary.append(String.format("Tuple totali: %d\n", getNumTuples()));
        summary.append(String.format("Tempo esecuzione: %s\n", getFormattedExecutionTime()));

        if (clusterSet != null && data != null) {
            // Calcola dimensione media
            double avgSize = (double) getNumTuples() / getNumClusters();
            summary.append(String.format("Dimensione media cluster: %.2f\n", avgSize));
        }

        return summary.toString();
    }

    @Override
    public String toString() {
        return String.format("ClusteringResult[clusters=%d, radius=%.3f, time=%s]", getNumClusters(), radius,
                getFormattedExecutionTime());
    }
}
