package gui.dialogs;

//===---------------------------------------------------------------------------===//
// Importazioni Java standard.
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.map.stdgui.StdChart;
import com.map.stdgui.StdDataView;
import com.map.stdgui.StdView;
import com.map.stdgui.StdWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import data.Data;
import data.Tuple;
import gui.models.ClusteringResult;
import mining.Cluster;
import mining.ClusterSet;
//===---------------------------------------------------------------------------===//

/**
 * Dialog per visualizzare statistiche dettagliate sui risultati del clustering.
 * <p>
 * Questa classe gestisce:
 * <ul>
 *   <li>Statistiche globali (numero cluster, dimensioni, tempi)</li>
 *   <li>Grafico a barre sulla distribuzione delle dimensioni</li>
 *   <li>Istogramma delle distanze intra-cluster</li>
 *   <li>Tabella riepilogativa con centroidi e distanze</li>
 * </ul>
 * <p>
 * L'interfaccia e' organizzata in schede per favorire la consultazione.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.0.0
 */
public class StatisticsDialog {

    //===------------------------------ CONSTANTS ------------------------------===//

    // Logger per la classe StatisticsDialog.
    private static final Logger logger = LoggerFactory.getLogger(StatisticsDialog.class);

    //===--------------------------- INSTANCE FIELDS ---------------------------===//

    // Finestra del dialog.
    private final StdWindow window;
    private final ClusteringResult result;

    //===---------------------------- INNER CLASSES ----------------------------===//

    /**
     * Classe interna per contenere il riepilogo di un cluster nella tabella.
     */
    private static class ClusterSummary {
        final int id;
        final int size;
        final double avgDistance;
        final double minDistance;
        final double maxDistance;
        final String centroidPreview;

        ClusterSummary(int id, int size, double avgDistance, double minDistance, double maxDistance,
                String centroidPreview) {
            this.id = id;
            this.size = size;
            this.avgDistance = avgDistance;
            this.minDistance = minDistance;
            this.maxDistance = maxDistance;
            this.centroidPreview = centroidPreview;
        }
    }

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruisce il dialog delle statistiche e inizializza la UI.
     *
     * @param result risultato del clustering
     */
    public StatisticsDialog(ClusteringResult result) {
        if (result == null) {
            throw new IllegalArgumentException("ClusteringResult non può essere null");
        }

        this.result = result;
        this.window = createDialog();
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Mostra il dialog.
     */
    public void show() {
        window.show();
    }

    //===--------------------------- PRIVATE METHODS ---------------------------===//

    /**
     * Crea il dialog e costruisce l'interfaccia.
     *
     * @return finestra del dialog
     */
    private StdWindow createDialog() {
        List<StdDataView.TabView> tabs = List.of(
                new StdDataView.TabView("Statistiche Generali", createGeneralStatsView()),
                new StdDataView.TabView("Distribuzione Dimensioni", createSizeDistributionChart()),
                new StdDataView.TabView("Distribuzione Distanze", createDistanceDistributionChart()),
                new StdDataView.TabView("Tabella Riepilogativa", createClusterTable()));

        logger.info("StatisticsDialog inizializzato");
        return StdDataView.tabs("Statistiche Clustering", tabs, 900, 700, "Chiudi").modal(true);
    }

    /**
     * Crea la vista con le statistiche generali.
     *
     * @return vista con statistiche globali
     */
    private StdView createGeneralStatsView() {
        // Calcola statistiche.
        ClusterSet clusterSet = result.getClusterSet();
        Data data = result.getData();

        List<Cluster> clusters = new ArrayList<>();
        for (Cluster c : clusterSet) {
            clusters.add(c);
        }

        int numClusters = clusters.size();
        int numTuples = result.getNumTuples();
        double radius = result.getRadius();

        // Statistiche dimensioni.
        int maxSize = 0;
        int minSize = 0;
        double sumSize = 0;

        // Statistiche distanze.
        double globalMinDist = 0;
        double globalMaxDist = 0;
        double globalSumDist = 0;
        int totalDistances = 0;

        if (!clusters.isEmpty()) {
            minSize = Integer.MAX_VALUE;
            globalMinDist = Double.MAX_VALUE;

            for (Cluster cluster : clusters) {
                int size = cluster.getSize();
                sumSize += size;
                if (size > maxSize)
                    maxSize = size;
                if (size < minSize)
                    minSize = size;

                // Calcola distanze.
                Tuple centroid = cluster.getCentroid();
                int[] tupleIds = cluster.getTupleIDs();

                for (int tupleId : tupleIds) {
                    double dist = centroid.getDistance(data.getItemSet(tupleId));
                    globalSumDist += dist;
                    totalDistances++;
                    if (dist < globalMinDist)
                        globalMinDist = dist;
                    if (dist > globalMaxDist)
                        globalMaxDist = dist;
                }
            }
        }

        double avgSize = numClusters > 0 ? (sumSize / numClusters) : 0.0;
        double globalAvgDist = totalDistances > 0 ? (globalSumDist / totalDistances) : 0.0;

        List<List<String>> rows = new ArrayList<>();

        // Sezione: Informazioni Generali.
        addStatsHeader(rows, "Informazioni Generali");
        addStatsRow(rows, "Data/Ora:", result.getFormattedTimestamp());
        addStatsRow(rows, "Tempo Esecuzione:", result.getFormattedExecutionTime());
        addStatsRow(rows, "Radius:", String.format("%.6f", radius));
        addStatsSpacer(rows);

        // Sezione: Cluster.
        addStatsHeader(rows, "Statistiche Cluster");
        addStatsRow(rows, "Numero Cluster:", String.valueOf(numClusters));
        addStatsRow(rows, "Numero Tuple Totali:", String.valueOf(numTuples));
        String avgSizeText = numClusters > 0 ? String.format("%.2f", avgSize) : "N/A";
        String maxSizeText = numClusters > 0 ? String.valueOf(maxSize) + " tuple" : "N/A";
        String minSizeText = numClusters > 0 ? String.valueOf(minSize) + " tuple" : "N/A";

        addStatsRow(rows, "Dimensione Media Cluster:", avgSizeText);
        addStatsRow(rows, "Cluster Più Grande:", maxSizeText);
        addStatsRow(rows, "Cluster Più Piccolo:", minSizeText);
        addStatsSpacer(rows);

        // Sezione: Distanze.
        addStatsHeader(rows, "Statistiche Distanze");
        String avgDistText = totalDistances > 0 ? String.format("%.6f", globalAvgDist) : "N/A";
        String minDistText = totalDistances > 0 ? String.format("%.6f", globalMinDist) : "N/A";
        String maxDistText = totalDistances > 0 ? String.format("%.6f", globalMaxDist) : "N/A";

        addStatsRow(rows, "Distanza Media Globale:", avgDistText);
        addStatsRow(rows, "Distanza Minima:", minDistText);
        addStatsRow(rows, "Distanza Massima:", maxDistText);
        addStatsSpacer(rows);

        // Sezione: Dataset.
        addStatsHeader(rows, "Informazioni Dataset");
        addStatsRow(rows, "Numero Attributi:", String.valueOf(data.getNumberOfExplanatoryAttributes()));

        StringBuilder attrNames = new StringBuilder();
        for (int i = 0; i < data.getNumberOfExplanatoryAttributes(); i++) {
            if (i > 0)
                attrNames.append(", ");
            attrNames.append(data.getExplanatoryAttribute(i).getName());
        }
        addStatsRow(rows, "Attributi:", attrNames.toString());

        return StdDataView.tableView("Statistiche Generali Clustering",
                new StdDataView.TableModel(List.of("Metrica", "Valore"), rows));
    }

    /**
     * Crea il grafico a barre per la distribuzione delle dimensioni dei cluster.
     *
     * @return vista del grafico a barre della dimensione cluster
     */
    private StdView createSizeDistributionChart() {
        List<StdChart.BarPoint> points = new ArrayList<>();
        int clusterIndex = 0;
        for (Cluster cluster : result.getClusterSet()) {
            clusterIndex++;
            points.add(new StdChart.BarPoint("Cluster " + clusterIndex, cluster.getSize()));
        }

        return StdChart.barChartView("Distribuzione Dimensioni Cluster", "Cluster", "Numero Tuple",
                List.of(new StdChart.BarSeries("Dimensione", points)));
    }

    /**
     * Crea l'istogramma per la distribuzione delle distanze intra-cluster.
     *
     * @return vista dell'istogramma delle distanze
     */
    private StdView createDistanceDistributionChart() {
        // Raccogli tutte le distanze.
        List<Double> distances = new ArrayList<>();
        Data data = result.getData();

        for (Cluster cluster : result.getClusterSet()) {
            Tuple centroid = cluster.getCentroid();
            int[] tupleIds = cluster.getTupleIDs();

            for (int tupleId : tupleIds) {
                double dist = centroid.getDistance(data.getItemSet(tupleId));
                distances.add(dist);
            }
        }

        // Crea bins per istogramma.
        int numBins = 10;
        double minDist = distances.stream().min(Double::compare).orElse(0.0);
        double maxDist = distances.stream().max(Double::compare).orElse(1.0);
        double binWidth = (maxDist - minDist) / numBins;

        if (binWidth <= 0) {
            binWidth = 0.1;
        }

        // Conta frequenze per bin.
        Map<Integer, Integer> histogram = new HashMap<>();
        for (double dist : distances) {
            int bin = (int) ((dist - minDist) / binWidth);
            if (bin >= numBins)
                bin = numBins - 1;
            histogram.put(bin, histogram.getOrDefault(bin, 0) + 1);
        }

        List<StdChart.BarPoint> points = new ArrayList<>();
        for (int i = 0; i < numBins; i++) {
            double binStart = minDist + i * binWidth;
            double binEnd = binStart + binWidth;
            String label = String.format("%.3f-%.3f", binStart, binEnd);
            points.add(new StdChart.BarPoint(label, histogram.getOrDefault(i, 0)));
        }

        return StdChart.barChartView("Distribuzione Distanze dal Centroide", "Range Distanza", "Frequenza",
                List.of(new StdChart.BarSeries("Frequenza", points)));
    }

    /**
     * Crea la tabella riepilogativa dei cluster.
     *
     * @return vista tabellare con riepilogo cluster
     */
    private StdView createClusterTable() {
        List<List<String>> rows = new ArrayList<>();
        Data data = result.getData();
        int clusterId = 0;

        for (Cluster cluster : result.getClusterSet()) {
            clusterId++;

            Tuple centroid = cluster.getCentroid();
            int[] tupleIds = cluster.getTupleIDs();

            double minDist = 0;
            double maxDist = 0;
            double sumDist = 0;
            double avgDist = 0;

            if (tupleIds.length > 0) {
                minDist = Double.MAX_VALUE;

                for (int tupleId : tupleIds) {
                    double dist = centroid.getDistance(data.getItemSet(tupleId));
                    if (dist < minDist)
                        minDist = dist;
                    if (dist > maxDist)
                        maxDist = dist;
                    sumDist += dist;
                }

                avgDist = sumDist / tupleIds.length;
            }

            // Crea preview centroide (primi 50 caratteri)
            String centroidStr = centroid.toString();
            String centroidPreview = centroidStr.length() > 50 ? centroidStr.substring(0, 47) + "..." : centroidStr;

            ClusterSummary summary =
                    new ClusterSummary(clusterId, cluster.getSize(), avgDist, minDist, maxDist, centroidPreview);
            rows.add(List.of(String.valueOf(summary.id), String.valueOf(summary.size),
                    String.format("%.6f", summary.avgDistance), String.format("%.6f", summary.minDistance),
                    String.format("%.6f", summary.maxDistance), summary.centroidPreview));
        }

        return StdDataView.tableView("Tabella Riepilogativa",
                new StdDataView.TableModel(
                        List.of("Cluster ID", "Dimensione", "Dist. Media", "Dist. Min", "Dist. Max", "Centroide"),
                        rows));
    }

    /**
     * Aggiunge un'intestazione di sezione alle righe statistiche.
     *
     * @param rows righe delle statistiche
     * @param text testo intestazione
     */
    private void addStatsHeader(List<List<String>> rows, String text) {
        rows.add(List.of(text, ""));
    }

    /**
     * Aggiunge una riga di statistiche alla tabella.
     *
     * @param rows righe delle statistiche
     * @param label etichetta della metrica
     * @param value valore formattato
     */
    private void addStatsRow(List<List<String>> rows, String label, String value) {
        rows.add(List.of(label, value));
    }

    /**
     * Aggiunge una riga vuota per separare le sezioni.
     *
     * @param rows righe delle statistiche
     */
    private void addStatsSpacer(List<List<String>> rows) {
        rows.add(List.of("", ""));
    }
}

//===---------------------------------------------------------------------------===//
