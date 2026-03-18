package gui.controllers;

//===---------------------------------------------------------------------------===//
// Importazioni Java standard.
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import data.Data;
import gui.models.ClusteringConfiguration;
import gui.models.ClusteringResult;
import gui.services.DataImportService;
import gui.utils.ApplicationContext;
// Importazioni JavaFX.
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
// Importazioni del backend di mining.
import mining.Cluster;
import mining.ClusterSet;
import mining.QTMiner;
//===---------------------------------------------------------------------------===//

/**
 * Controller per la vista Clustering.
 * <p>
 * Questa classe gestisce:
 * <ul>
 *   <li>Esecuzione dell'algoritmo QT in background tramite {@link Task}</li>
 *   <li>Aggiornamento di progresso, stato e log in tempo reale</li>
 *   <li>Creazione del grafico di distribuzione cluster</li>
 *   <li>Gestione di successo, errore o annullamento del processo</li>
 *   <li>Navigazione alla vista risultati al termine</li>
 * </ul>
 * <p>
 * Il controller viene inizializzato automaticamente da JavaFX quando viene
 * caricato il file FXML associato. I campi {@code @FXML} sono iniettati dal framework.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.0.0
 * @see gui.models.ClusteringConfiguration
 * @see gui.models.ClusteringResult
 * @see gui.services.DataImportService
 */
public class ClusteringController {

    // Logger per la classe ClusteringController.
    private static final Logger logger = LoggerFactory.getLogger(ClusteringController.class);

    //===--------------------------- INSTANCE FIELDS ---------------------------===//

    // Task di clustering in esecuzione.
    private Task<Void> clusteringTask;
    private Instant startTime;
    private volatile boolean isCancelled = false; // volatile per visibilità tra thread.
    private QTMiner miner; // Conserva il miner per creare ClusteringResult.
    private final Object logLock = new Object();
    private final StringBuilder logBuffer = new StringBuilder();
    private boolean logFlushScheduled = false;

    //===---------------------------- FXML CONTROLS ----------------------------===//
    // Componenti di progresso.
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label progressLabel;
    @FXML
    private Label progressPercentLabel;

    // Etichette di stato.
    @FXML
    private Label currentStepLabel;
    @FXML
    private Label clustersFoundLabel;
    @FXML
    private Label tuplesClusteredLabel;
    @FXML
    private Label elapsedTimeLabel;

    // Area log.
    @FXML
    private TextArea logTextArea;

    // Chart container.
    @FXML
    private VBox chartContainer;
    @FXML
    private Label chartPlaceholder;

    // Pulsanti.
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnViewResults;

    // Footer di stato.
    @FXML
    private HBox statusFooter;
    @FXML
    private Label statusMessageLabel;

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruttore di default.
     * <p>
     * Invocato da JavaFX durante il caricamento FXML. L'inizializzazione
     * dei componenti avviene nel metodo {@link #initialize()}.
     *
     * @see #initialize()
     */
    public ClusteringController() {
        // Costruttore vuoto - l'inizializzazione avviene nel metodo initialize().
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Inizializza il controller dopo l'iniezione FXML.
     * <p>
     * Configura i pulsanti e avvia immediatamente il clustering.
     */
    @FXML
    public void initialize() {
        logger.info("Inizializzazione ClusteringController...");

        setupButtons();
        startClustering();

        logger.info("ClusteringController inizializzato con successo");
    }

    //===--------------------------- PRIVATE METHODS ---------------------------===//

    /**
     * Configura i gestori eventi dei pulsanti della vista.
     */
    private void setupButtons() {
        btnCancel.setOnAction(e -> handleCancel());
        btnViewResults.setOnAction(e -> handleViewResults());
    }

    /**
     * Avvia il processo di clustering e collega i binding di progresso.
     * <p>
     * Crea il task, aggancia i listener di completamento e lo avvia
     * su un thread in background.
     */
    private void startClustering() {
        startTime = Instant.now();
        isCancelled = false;

        if (progressIndicator != null) {
            progressIndicator.setVisible(true);
        }

        // Crea il task di clustering.
        clusteringTask = createClusteringTask();

        // Collega il progresso.
        progressBar.progressProperty().bind(clusteringTask.progressProperty());
        progressLabel.textProperty().bind(clusteringTask.messageProperty());
        progressPercentLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("%.0f%%", clusteringTask.getProgress() * 100), clusteringTask.progressProperty()));
        if (progressIndicator != null) {
            progressIndicator.progressProperty().bind(clusteringTask.progressProperty());
        }

        // Gestisce il completamento del task.
        clusteringTask.setOnSucceeded(event -> handleClusteringSuccess());
        clusteringTask.setOnFailed(event -> handleClusteringFailure());
        clusteringTask.setOnCancelled(event -> handleClusteringCancelled());

        // Avvia il task in un thread in background.
        Thread thread = new Thread(clusteringTask);
        thread.setDaemon(true);
        thread.start();

        // Avvia l'aggiornatore del tempo trascorso.
        startElapsedTimeUpdater();

        logger.info("Task di clustering avviato");
    }

    /**
     * Crea il task di clustering con integrazione backend reale.
     * <p>
     * Il task:
     * <ol>
     *   <li>Carica il dataset dalla sorgente configurata</li>
     *   <li>Esegue {@link QTMiner#compute(Data)}</li>
     *   <li>Costruisce un {@link ClusteringResult}</li>
     *   <li>Aggiorna log e progresso</li>
     * </ol>
     *
     * @return task di clustering
     */
    private Task<Void> createClusteringTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Recupera configurazione da ApplicationContext.
                ClusteringConfiguration config = ApplicationContext.getInstance().getCurrentConfiguration();

                if (config == null) {
                    throw new IllegalStateException("Configurazione clustering non trovata");
                }

                updateMessage("Inizializzazione clustering...");
                updateProgress(0, 100);

                Platform.runLater(() -> {
                    appendLog("# ====== INIZIO CLUSTERING ====== #");
                    appendLog("Configurazione:");
                    appendLog("  Sorgente: " + config.getDataSource());
                    appendLog("  Radius: " + config.getRadius());
                    appendLog("");
                });

                // Carica dati.
                updateMessage("Caricamento dataset...");
                updateProgress(10, 100);

                DataImportService dataService = ApplicationContext.getInstance().getDataImportService();

                Data data;
                try {
                    data = switch (config.getDataSource()) {
                        case HARDCODED -> {
                            Platform.runLater(() -> appendLog("Caricamento dataset hardcoded (PlayTennis)..."));
                            yield dataService.loadHardcodedData();
                        }
                        case IRIS -> {
                            Platform.runLater(() -> appendLog("Caricamento dataset standard Iris (150 tuple)..."));
                            yield dataService.loadIrisData();
                        }
                        case CSV -> {
                            Platform.runLater(
                                    () -> appendLog("Caricamento dataset da CSV: " + config.getCsvFilePath()));
                            yield dataService.loadDataFromCSV(config.getCsvFilePath());
                        }
                        case DATABASE -> {
                            Platform.runLater(() -> appendLog(
                                    "Connessione a database: " + config.getDbHost() + ":" + config.getDbPort()));
                            yield dataService.loadDataFromDatabase(config.getDbTableName(), config.getDbHost(),
                                    config.getDbPort(), config.getDbName(), config.getDbUser(), config.getDbPassword());
                        }
                    };

                    final int numExamples = data.getNumberOfExamples();
                    final int numAttributes = data.getNumberOfExplanatoryAttributes();

                    Platform.runLater(() -> {
                        appendLog("Dataset caricato con successo:");
                        appendLog("  Tuple: " + numExamples);
                        appendLog("  Attributi: " + numAttributes);
                        appendLog("");
                        tuplesClusteredLabel.setText("0 / " + numExamples);
                    });

                } catch (Exception e) {
                    Platform.runLater(() -> appendLog("ERRORE: " + e.getMessage()));
                    throw new RuntimeException("Errore durante caricamento dataset: " + e.getMessage(), e);
                }

                if (isCancelled()) {
                    return null;
                }

                // Esegui clustering.
                updateMessage("Esecuzione clustering Quality Threshold...");
                updateProgress(30, 100);

                Platform.runLater(() -> {
                    appendLog("Avvio algoritmo QT...");
                    currentStepLabel.setText("Costruzione cluster...");
                });

                long startTimeMs = System.currentTimeMillis();

                try {
                    // Crea QTMiner.
                    miner = new QTMiner(config.getRadius());

                    // Esegui compute (questo è il lavoro principale).
                    updateProgress(40, 100);
                    int numClusters = miner.compute(data);
                    ClusterSet clusterSet = miner.getC();

                    long executionTimeMs = System.currentTimeMillis() - startTimeMs;

                    if (isCancelled()) {
                        return null;
                    }

                    // Aggiorna progresso.
                    updateProgress(90, 100);

                    final int finalNumClusters = numClusters;
                    final int totalTuples = data.getNumberOfExamples();

                    Platform.runLater(() -> {
                        appendLog("");
                        appendLog("Clustering completato!");
                        appendLog("  Cluster trovati: " + finalNumClusters);
                        appendLog("  Tempo esecuzione: " + executionTimeMs + "ms");
                        appendLog("");

                        clustersFoundLabel.setText(String.valueOf(finalNumClusters));
                        tuplesClusteredLabel.setText(totalTuples + " / " + totalTuples);

                        // Mostra dettagli cluster (primi 5).
                        int i = 0;
                        for (Cluster cluster : clusterSet) {
                            if (i >= 5)
                                break;
                            appendLog("Cluster " + (i + 1) + ": " + cluster.getSize() + " tuple");
                            i++;
                        }
                        if (finalNumClusters > 5) {
                            appendLog("... e altri " + (finalNumClusters - 5) + " cluster");
                        }
                    });

                    // Crea ClusteringResult.
                    ClusteringResult result =
                            new ClusteringResult(clusterSet, data, config.getRadius(), executionTimeMs, miner);

                    // Salva nel contesto.
                    ApplicationContext.getInstance().setCurrentResult(result);

                    updateProgress(100, 100);
                    updateMessage("Clustering completato con successo");

                    Platform.runLater(() -> {
                        appendLog("");
                        appendLog("# ============================================== #");
                        appendLog("# ----- CLUSTERING COMPLETATO CON SUCCESSO ----- #");
                        appendLog("# ============================================== #");
                    });

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        appendLog("");
                        appendLog("ERRORE durante clustering:");
                        appendLog("  " + e.getMessage());
                    });
                    throw new RuntimeException("Errore durante clustering: " + e.getMessage(), e);
                }

                return null;
            }
        };
    }

    /**
     * Avvia il thread di aggiornamento del tempo trascorso.
     * <p>
     * L'aggiornamento avviene ogni secondo finche' il task non termina.
     */
    private void startElapsedTimeUpdater() {
        Thread timeThread = new Thread(() -> {
            while (!isCancelled && !clusteringTask.isDone()) {
                Platform.runLater(() -> {
                    Duration elapsed = Duration.between(startTime, Instant.now());
                    long hours = elapsed.toHours();
                    long minutes = elapsed.toMinutesPart();
                    long seconds = elapsed.toSecondsPart();
                    elapsedTimeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        timeThread.setDaemon(true);
        timeThread.start();
    }

    /**
     * Aggiunge un messaggio all'area di log.
     * <p>
     * Usa un buffer per ridurre le chiamate al thread JavaFX.
     *
     * @param message messaggio di log
     */
    private void appendLog(String message) {
        synchronized (logLock) {
            logBuffer.append(message).append("\n");
            if (logFlushScheduled) {
                return;
            }
            logFlushScheduled = true;
        }

        Platform.runLater(() -> {
            String pending;
            synchronized (logLock) {
                pending = logBuffer.toString();
                logBuffer.setLength(0);
                logFlushScheduled = false;
            }
            logTextArea.appendText(pending);
            logTextArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    /**
     * Crea il grafico a barre della distribuzione dei cluster.
     * <p>
     * Ogni barra rappresenta la dimensione di un cluster.
     */
    private void createClusterDistributionChart() {
        if (miner == null || miner.getC() == null)
            return;

        ClusterSet clusterSet = miner.getC();

        // Assi.
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Cluster");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Numero di Tuple");

        // Chart.
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Distribuzione Dimensioni Cluster");
        barChart.setLegendVisible(false);
        barChart.setAnimated(true);

        // Serie dati.
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tuple");

        int i = 1;
        for (Cluster c : clusterSet) {
            series.getData().add(new XYChart.Data<>("C" + i, c.getSize()));
            i++;
        }

        barChart.getData().add(series);

        // Aggiungi al container.
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(barChart);
    }

    //===--------------------------- HANDLER METHODS ---------------------------===//

    /**
     * Gestisce il successo del clustering.
     * <p>
     * Aggiorna la UI, abilita il pulsante risultati e genera il grafico.
     */
    private void handleClusteringSuccess() {
        logger.info("Clustering completato con successo");

        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
        }

        statusMessageLabel.setText("Clustering completato con successo!");
        statusMessageLabel.setStyle("-fx-text-fill: #27ae60;");
        statusFooter.setVisible(true);
        statusFooter.setManaged(true);

        btnCancel.setVisible(false);
        btnCancel.setManaged(false);
        btnViewResults.setVisible(true);
        btnViewResults.setManaged(true);

        appendLog("\nPronto per visualizzare i risultati.");

        // Crea e mostra il grafico.
        createClusterDistributionChart();
    }

    /**
     * Gestisce il fallimento del clustering.
     * <p>
     * Mostra un messaggio di errore e aggiorna la UI.
     */
    private void handleClusteringFailure() {
        logger.error("Clustering fallito", clusteringTask.getException());

        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
        }

        statusMessageLabel.setText("Clustering fallito: " + clusteringTask.getException().getMessage());
        statusMessageLabel.setStyle("-fx-text-fill: #e74c3c;");
        statusFooter.setVisible(true);
        statusFooter.setManaged(true);

        appendLog("\nERRORE: Clustering fallito!");
        appendLog("Motivo: " + clusteringTask.getException().getMessage());

        if (chartPlaceholder != null) {
            chartPlaceholder.setText("Clustering fallito.");
            chartPlaceholder.setStyle("-fx-text-fill: #e74c3c;");
        }

        showError("Clustering Fallito",
                "Si è verificato un errore durante il clustering:\n" + clusteringTask.getException().getMessage());
    }

    /**
     * Gestisce l'annullamento del clustering.
     * <p>
     * Aggiorna la UI e lascia la vista in stato consistente.
     */
    private void handleClusteringCancelled() {
        logger.info("Clustering annullato dall'utente");

        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
        }

        statusMessageLabel.setText("Clustering annullato dall'utente");
        statusMessageLabel.setStyle("-fx-text-fill: #f39c12;");
        statusFooter.setVisible(true);
        statusFooter.setManaged(true);

        appendLog("\nClustering annullato dall'utente.");

        if (chartPlaceholder != null) {
            chartPlaceholder.setText("Clustering annullato.");
        }
    }

    /**
     * Gestisce il clic del pulsante annulla.
     * <p>
     * Richiede conferma all'utente e annulla il task se in esecuzione.
     */
    private void handleCancel() {
        logger.info("Pulsante annulla cliccato");

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Annulla Clustering");
        confirmAlert.setHeaderText("Sei sicuro di voler annullare il processo di clustering?");
        confirmAlert.setContentText("Il progresso sarà perso.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                isCancelled = true;
                if (clusteringTask != null && clusteringTask.isRunning()) {
                    clusteringTask.cancel();
                }
                appendLog("\nUtente ha richiesto l'annullamento...");
            }
        });
    }

    /**
     * Gestisce il clic del pulsante visualizza risultati.
     * <p>
     * Carica la vista risultati e la imposta come root della scena.
     */
    private void handleViewResults() {
        logger.info("Visualizza risultati cliccato");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/results.fxml"));
            Parent resultsView = loader.load();

            Scene currentScene = btnViewResults.getScene();
            currentScene.setRoot(resultsView);

            logger.info("Navigazione a vista results completata");

        } catch (IOException e) {
            logger.error("Errore durante navigazione a vista results", e);
            showError("Errore Navigazione", "Impossibile caricare la vista risultati:\n" + e.getMessage());
        }
    }

    //===--------------------------- PRIVATE HELPERS ---------------------------===//

    /**
     * Mostra un dialogo di errore con titolo e messaggio specificati.
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
}

//===---------------------------------------------------------------------------===//
