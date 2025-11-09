package gui.charts;

import gui.models.ClusteringResult;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Finestra di visualizzazione per scatter plot 2D dei cluster.
 * Mostra il grafico XChart integrato in una finestra JavaFX.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 */
public class ChartViewer {

    private static final Logger logger = LoggerFactory.getLogger(ChartViewer.class);

    private final ClusteringResult result;
    private final ClusterScatterChart chartManager;
    private final Stage stage;

    private ComboBox<String> xAxisComboBox;
    private ComboBox<String> yAxisComboBox;
    private SwingNode chartNode;

    /**
     * Crea una finestra di visualizzazione per i risultati di clustering.
     *
     * @param result risultati del clustering
     */
    public ChartViewer(ClusteringResult result) {
        this.result = result;
        this.chartManager = new ClusterScatterChart(result);
        this.stage = new Stage();

        initializeUI();
    }

    /**
     * Inizializza l'interfaccia utente della finestra.
     */
    private void initializeUI() {
        stage.setTitle("Visualizzazione Cluster 2D");
        stage.initModality(Modality.NONE);

        BorderPane root = new BorderPane();

        // Top: Toolbar con selezione assi
        VBox topPanel = createTopPanel();
        root.setTop(topPanel);

        // Center: Grafico
        chartNode = new SwingNode();
        updateChart();
        root.setCenter(chartNode);

        // Bottom: Pulsanti azioni
        HBox bottomPanel = createBottomPanel();
        root.setBottom(bottomPanel);

        Scene scene = new Scene(root, 900, 700);
        stage.setScene(scene);

        logger.info("ChartViewer inizializzato");
    }

    /**
     * Crea il pannello superiore con i controlli per la selezione degli assi.
     *
     * @return VBox contenente i controlli
     */
    private VBox createTopPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f0f0f0;");

        Label titleLabel = new Label("Seleziona Attributi da Visualizzare:");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        HBox axisSelectionBox = new HBox(15);
        axisSelectionBox.setPadding(new Insets(5, 0, 0, 0));

        // ComboBox per asse X
        Label xLabel = new Label("Asse X:");
        xAxisComboBox = new ComboBox<>();
        String[] attributeNames = chartManager.getAttributeNames();
        xAxisComboBox.getItems().addAll(attributeNames);
        xAxisComboBox.setValue(attributeNames[chartManager.getXAttributeIndex()]);
        xAxisComboBox.setOnAction(e -> onAxisChanged());

        // ComboBox per asse Y
        Label yLabel = new Label("Asse Y:");
        yAxisComboBox = new ComboBox<>();
        yAxisComboBox.getItems().addAll(attributeNames);
        yAxisComboBox.setValue(attributeNames[chartManager.getYAttributeIndex()]);
        yAxisComboBox.setOnAction(e -> onAxisChanged());

        Button refreshButton = new Button("Aggiorna Grafico");
        refreshButton.setOnAction(e -> updateChart());

        axisSelectionBox.getChildren().addAll(xLabel, xAxisComboBox, yLabel, yAxisComboBox, refreshButton);

        panel.getChildren().addAll(titleLabel, axisSelectionBox);
        return panel;
    }

    /**
     * Crea il pannello inferiore con i pulsanti di azione.
     *
     * @return HBox contenente i pulsanti
     */
    private HBox createBottomPanel() {
        HBox panel = new HBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f0f0f0;");

        Button exportButton = new Button("Esporta PNG");
        exportButton.setOnAction(e -> handleExport());

        Button exportHDButton = new Button("Esporta PNG HD");
        exportHDButton.setOnAction(e -> handleExportHD());

        Button closeButton = new Button("Chiudi");
        closeButton.setOnAction(e -> stage.close());

        Label infoLabel = new Label(String.format("Cluster: %d | Tuple: %d | Radius: %.3f", result.getNumClusters(),
                result.getNumTuples(), result.getRadius()));
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        // Spacer per allineare info a sinistra e pulsanti a destra
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        panel.getChildren().addAll(infoLabel, spacer, exportButton, exportHDButton, closeButton);

        return panel;
    }

    /**
     * Gestisce il cambio di selezione degli assi.
     */
    private void onAxisChanged() {
        int xIndex = xAxisComboBox.getSelectionModel().getSelectedIndex();
        int yIndex = yAxisComboBox.getSelectionModel().getSelectedIndex();

        if (xIndex >= 0 && yIndex >= 0) {
            chartManager.setAxes(xIndex, yIndex);
            logger.info("Assi cambiati: X={}, Y={}", xIndex, yIndex);
        }
    }

    /**
     * Aggiorna il grafico visualizzato.
     */
    private void updateChart() {
        try {
            XYChart chart = chartManager.createChart();

            // Crea pannello Swing e integra in JavaFX
            SwingUtilities.invokeLater(() -> {
                JPanel chartPanel = new XChartPanel<>(chart);
                chartNode.setContent(chartPanel);
            });

            logger.info("Grafico aggiornato");

        } catch (Exception e) {
            logger.error("Errore durante creazione grafico", e);
            showError("Errore Visualizzazione", "Impossibile creare il grafico: " + e.getMessage());
        }
    }

    /**
     * Gestisce l'esportazione del grafico come PNG (risoluzione standard).
     */
    private void handleExport() {
        exportChart(800, 600);
    }

    /**
     * Gestisce l'esportazione del grafico come PNG HD (alta risoluzione).
     */
    private void handleExportHD() {
        exportChart(1920, 1080);
    }

    /**
     * Esporta il grafico con le dimensioni specificate.
     *
     * @param width larghezza in pixel
     * @param height altezza in pixel
     */
    private void exportChart(int width, int height) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salva Grafico");
        fileChooser.setInitialFileName("cluster_chart.png");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini PNG", "*.png"));

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                chartManager.saveAsPNG(file, width, height);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Esportazione Completata");
                alert.setHeaderText(null);
                alert.setContentText("Grafico salvato con successo:\n" + file.getAbsolutePath());
                alert.showAndWait();

                logger.info("Grafico esportato: {}", file.getAbsolutePath());

            } catch (IOException e) {
                logger.error("Errore durante esportazione grafico", e);
                showError("Errore Esportazione", "Impossibile salvare il grafico: " + e.getMessage());
            }
        }
    }

    /**
     * Mostra un dialogo di errore.
     *
     * @param title titolo del dialogo
     * @param message messaggio di errore
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Mostra la finestra di visualizzazione.
     */
    public void show() {
        stage.show();
        logger.info("ChartViewer mostrato");
    }

    /**
     * @return lo stage della finestra
     */
    public Stage getStage() {
        return stage;
    }
}
