package gui.dialogs;

import data.Data;
import data.Tuple;
import gui.models.ClusteringResult;
import gui.utils.ColorPalette;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mining.Cluster;
import mining.ClusterSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dialog per visualizzare statistiche dettagliate sui risultati del clustering.
 * Include:
 * - Statistiche globali (numero cluster, dimensioni, etc.)
 * - Grafico a barre distribuzione dimensioni cluster
 * - Istogramma distribuzione distanze intra-cluster
 * - Tabella riepilogativa cluster
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 */
public class StatisticsDialog {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsDialog.class);

    private final Stage stage;
    private final ClusteringResult result;
    private final ColorPalette colorPalette;

    /**
     * Costruisce il dialog delle statistiche.
     *
     * @param result risultato del clustering
     */
    public StatisticsDialog(ClusteringResult result) {
        if (result == null) {
            throw new IllegalArgumentException("ClusteringResult non può essere null");
        }

        this.result = result;
        this.colorPalette = new ColorPalette();
        this.stage = new Stage();

        initializeDialog();
    }

    /**
     * Inizializza il dialog e costruisce l'interfaccia.
     */
    private void initializeDialog() {
        stage.setTitle("Statistiche Clustering");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setWidth(900);
        stage.setHeight(700);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Crea tab pane con diverse viste
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Tab 1: Statistiche generali
        Tab statsTab = new Tab("Statistiche Generali");
        statsTab.setContent(createGeneralStatsPane());

        // Tab 2: Distribuzione dimensioni
        Tab sizeDistTab = new Tab("Distribuzione Dimensioni");
        sizeDistTab.setContent(createSizeDistributionChart());

        // Tab 3: Distribuzione distanze
        Tab distDistTab = new Tab("Distribuzione Distanze");
        distDistTab.setContent(createDistanceDistributionChart());

        // Tab 4: Tabella cluster
        Tab tableTab = new Tab("Tabella Riepilogativa");
        tableTab.setContent(createClusterTable());

        tabPane.getTabs().addAll(statsTab, sizeDistTab, distDistTab, tableTab);

        root.setCenter(tabPane);

        // Bottoni
        Button closeButton = new Button("Chiudi");
        closeButton.setOnAction(e -> stage.close());

        VBox bottomBox = new VBox(10);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));
        bottomBox.getChildren().add(closeButton);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);

        logger.info("StatisticsDialog inizializzato");
    }

    /**
     * Crea il pannello con le statistiche generali.
     *
     * @return pannello statistiche
     */
    private VBox createGeneralStatsPane() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(15));

        // Titolo
        Label titleLabel = new Label("Statistiche Generali Clustering");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Calcola statistiche
        ClusterSet clusterSet = result.getClusterSet();
        Data data = result.getData();

        List<Cluster> clusters = new ArrayList<>();
        for (Cluster c : clusterSet) {
            clusters.add(c);
        }

        int numClusters = clusters.size();
        int numTuples = result.getNumTuples();
        double radius = result.getRadius();

        // Statistiche dimensioni
        int maxSize = 0;
        int minSize = Integer.MAX_VALUE;
        double sumSize = 0;

        // Statistiche distanze
        double globalMinDist = Double.MAX_VALUE;
        double globalMaxDist = 0;
        double globalSumDist = 0;
        int totalDistances = 0;

        for (Cluster cluster : clusters) {
            int size = cluster.getSize();
            sumSize += size;
            if (size > maxSize)
                maxSize = size;
            if (size < minSize)
                minSize = size;

            // Calcola distanze
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

        double avgSize = sumSize / numClusters;
        double globalAvgDist = globalSumDist / totalDistances;

        // Crea griglia con statistiche
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        int row = 0;

        // Sezione: Informazioni Generali
        addStatsHeader(grid, row++, "Informazioni Generali");
        addStatsRow(grid, row++, "Data/Ora:", result.getFormattedTimestamp());
        addStatsRow(grid, row++, "Tempo Esecuzione:", result.getFormattedExecutionTime());
        addStatsRow(grid, row++, "Radius:", String.format("%.6f", radius));

        row++; // Spazio vuoto

        // Sezione: Cluster
        addStatsHeader(grid, row++, "Statistiche Cluster");
        addStatsRow(grid, row++, "Numero Cluster:", String.valueOf(numClusters));
        addStatsRow(grid, row++, "Numero Tuple Totali:", String.valueOf(numTuples));
        addStatsRow(grid, row++, "Dimensione Media Cluster:", String.format("%.2f", avgSize));
        addStatsRow(grid, row++, "Cluster Più Grande:", String.valueOf(maxSize) + " tuple");
        addStatsRow(grid, row++, "Cluster Più Piccolo:", String.valueOf(minSize) + " tuple");

        row++; // Spazio vuoto

        // Sezione: Distanze
        addStatsHeader(grid, row++, "Statistiche Distanze");
        addStatsRow(grid, row++, "Distanza Media Globale:", String.format("%.6f", globalAvgDist));
        addStatsRow(grid, row++, "Distanza Minima:", String.format("%.6f", globalMinDist));
        addStatsRow(grid, row++, "Distanza Massima:", String.format("%.6f", globalMaxDist));

        row++; // Spazio vuoto

        // Sezione: Dataset
        addStatsHeader(grid, row++, "Informazioni Dataset");
        addStatsRow(grid, row++, "Numero Attributi:", String.valueOf(data.getNumberOfExplanatoryAttributes()));

        StringBuilder attrNames = new StringBuilder();
        for (int i = 0; i < data.getNumberOfExplanatoryAttributes(); i++) {
            if (i > 0)
                attrNames.append(", ");
            attrNames.append(data.getExplanatoryAttribute(i).getName());
        }
        addStatsRow(grid, row++, "Attributi:", attrNames.toString());

        vbox.getChildren().addAll(titleLabel, new Separator(), grid);

        return vbox;
    }

    /**
     * Crea il grafico a barre per la distribuzione delle dimensioni dei cluster.
     *
     * @return grafico a barre
     */
    private BarChart<String, Number> createSizeDistributionChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Cluster");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Numero Tuple");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Distribuzione Dimensioni Cluster");
        barChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Dimensione");

        int clusterIndex = 0;
        for (Cluster cluster : result.getClusterSet()) {
            clusterIndex++;
            series.getData().add(new XYChart.Data<>("Cluster " + clusterIndex, cluster.getSize()));
        }

        barChart.getData().add(series);

        return barChart;
    }

    /**
     * Crea l'istogramma per la distribuzione delle distanze intra-cluster.
     *
     * @return istogramma
     */
    private BarChart<String, Number> createDistanceDistributionChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Range Distanza");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Frequenza");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Distribuzione Distanze dal Centroide");
        barChart.setLegendVisible(false);

        // Raccogli tutte le distanze
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

        // Crea bins per istogramma
        int numBins = 10;
        double minDist = distances.stream().min(Double::compare).orElse(0.0);
        double maxDist = distances.stream().max(Double::compare).orElse(1.0);
        double binWidth = (maxDist - minDist) / numBins;

        if (binWidth <= 0) {
            binWidth = 0.1;
        }

        // Conta frequenze per bin
        Map<Integer, Integer> histogram = new HashMap<>();
        for (double dist : distances) {
            int bin = (int) ((dist - minDist) / binWidth);
            if (bin >= numBins)
                bin = numBins - 1;
            histogram.put(bin, histogram.getOrDefault(bin, 0) + 1);
        }

        // Aggiungi dati al grafico
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Frequenza");

        for (int i = 0; i < numBins; i++) {
            double binStart = minDist + i * binWidth;
            double binEnd = binStart + binWidth;
            String label = String.format("%.3f-%.3f", binStart, binEnd);
            series.getData().add(new XYChart.Data<>(label, histogram.getOrDefault(i, 0)));
        }

        barChart.getData().add(series);

        return barChart;
    }

    /**
     * Crea la tabella riepilogativa dei cluster.
     *
     * @return tabella cluster
     */
    private TableView<ClusterSummary> createClusterTable() {
        TableView<ClusterSummary> tableView = new TableView<>();

        // Colonna ID
        TableColumn<ClusterSummary, Integer> idCol = new TableColumn<>("Cluster ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().id));
        idCol.setPrefWidth(100);

        // Colonna Dimensione
        TableColumn<ClusterSummary, Integer> sizeCol = new TableColumn<>("Dimensione");
        sizeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().size));
        sizeCol.setPrefWidth(100);

        // Colonna Distanza Media
        TableColumn<ClusterSummary, String> avgDistCol = new TableColumn<>("Dist. Media");
        avgDistCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.format("%.6f", data.getValue().avgDistance)));
        avgDistCol.setPrefWidth(120);

        // Colonna Distanza Min
        TableColumn<ClusterSummary, String> minDistCol = new TableColumn<>("Dist. Min");
        minDistCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.format("%.6f", data.getValue().minDistance)));
        minDistCol.setPrefWidth(120);

        // Colonna Distanza Max
        TableColumn<ClusterSummary, String> maxDistCol = new TableColumn<>("Dist. Max");
        maxDistCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.format("%.6f", data.getValue().maxDistance)));
        maxDistCol.setPrefWidth(120);

        // Colonna Centroide (abbreviato)
        TableColumn<ClusterSummary, String> centroidCol = new TableColumn<>("Centroide");
        centroidCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().centroidPreview));
        centroidCol.setPrefWidth(250);

        tableView.getColumns().add(idCol);
        tableView.getColumns().add(sizeCol);
        tableView.getColumns().add(avgDistCol);
        tableView.getColumns().add(minDistCol);
        tableView.getColumns().add(maxDistCol);
        tableView.getColumns().add(centroidCol);

        // Popola tabella
        Data data = result.getData();
        int clusterId = 0;

        for (Cluster cluster : result.getClusterSet()) {
            clusterId++;

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

            // Crea preview centroide (primi 50 caratteri)
            String centroidStr = centroid.toString();
            String centroidPreview = centroidStr.length() > 50 ? centroidStr.substring(0, 47) + "..." : centroidStr;

            ClusterSummary summary =
                    new ClusterSummary(clusterId, cluster.getSize(), avgDist, minDist, maxDist, centroidPreview);

            tableView.getItems().add(summary);
        }

        return tableView;
    }

    /**
     * Aggiunge un'intestazione di sezione alla griglia statistiche.
     */
    private void addStatsHeader(GridPane grid, int row, String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        grid.add(label, 0, row, 2, 1);
    }

    /**
     * Aggiunge una riga di statistiche alla griglia.
     */
    private void addStatsRow(GridPane grid, int row, String label, String value) {
        Label keyLabel = new Label(label);
        keyLabel.setStyle("-fx-font-weight: bold;");

        Label valueLabel = new Label(value);

        grid.add(keyLabel, 0, row);
        grid.add(valueLabel, 1, row);
    }

    /**
     * Mostra il dialog.
     */
    public void show() {
        stage.show();
    }

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
}
