package gui.controllers;

import data.Data;
import gui.models.ClusteringConfiguration;
import gui.models.ClusteringResult;
import gui.services.ClusteringService;
import gui.services.DataImportService;
import gui.utils.ApplicationContext;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import mining.Cluster;
import mining.ClusterSet;
import mining.QTMiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

/**
 * Controller per la vista Clustering.
 * Gestisce l'esecuzione del processo di clustering con feedback sul progresso.
 */
public class ClusteringController {

    private static final Logger logger = LoggerFactory.getLogger(ClusteringController.class);

    // Componenti di progresso
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;
    @FXML private Label progressPercentLabel;

    // Etichette di stato
    @FXML private Label currentStepLabel;
    @FXML private Label clustersFoundLabel;
    @FXML private Label tuplesClusteredLabel;
    @FXML private Label elapsedTimeLabel;

    // Area log
    @FXML private TextArea logTextArea;

    // Pulsanti
    @FXML private Button btnCancel;
    @FXML private Button btnViewResults;

    // Footer di stato
    @FXML private HBox statusFooter;
    @FXML private Label statusMessageLabel;

    private Task<Void> clusteringTask;
    private Instant startTime;
    private volatile boolean isCancelled = false; // volatile per visibilità tra thread
    private QTMiner miner; // Conserva il miner per creare ClusteringResult

    /**
     * Inizializza il controller.
     * Chiamato automaticamente dopo il caricamento FXML.
     */
    @FXML
    public void initialize() {
        logger.info("Inizializzazione ClusteringController...");

        setupButtons();
        startClustering();

        logger.info("ClusteringController inizializzato con successo");
    }

    /**
     * Configura i gestori eventi dei pulsanti.
     */
    private void setupButtons() {
        btnCancel.setOnAction(e -> handleCancel());
        btnViewResults.setOnAction(e -> handleViewResults());
    }

    /**
     * Avvia il processo di clustering.
     */
    private void startClustering() {
        startTime = Instant.now();
        isCancelled = false;

        // Crea il task di clustering
        clusteringTask = createClusteringTask();

        // Collega il progresso
        progressBar.progressProperty().bind(clusteringTask.progressProperty());
        progressLabel.textProperty().bind(clusteringTask.messageProperty());

        // Gestisce il completamento del task
        clusteringTask.setOnSucceeded(event -> handleClusteringSuccess());
        clusteringTask.setOnFailed(event -> handleClusteringFailure());
        clusteringTask.setOnCancelled(event -> handleClusteringCancelled());

        // Avvia il task in un thread in background
        Thread thread = new Thread(clusteringTask);
        thread.setDaemon(true);
        thread.start();

        // Avvia l'aggiornatore del tempo trascorso
        startElapsedTimeUpdater();

        logger.info("Task di clustering avviato");
    }

    /**
     * Crea il task di clustering con integrazione backend reale.
     *
     * @return task di clustering
     */
    private Task<Void> createClusteringTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Recupera configurazione da ApplicationContext
                ClusteringConfiguration config =
                        ApplicationContext.getInstance().getCurrentConfiguration();

                if (config == null) {
                    throw new IllegalStateException("Configurazione clustering non trovata");
                }

                updateMessage("Inizializzazione clustering...");
                updateProgress(0, 100);

                Platform.runLater(() -> {
                    appendLog("=== INIZIO CLUSTERING ===");
                    appendLog("Configurazione:");
                    appendLog("  Sorgente: " + config.getDataSource());
                    appendLog("  Radius: " + config.getRadius());
                    appendLog("");
                });

                // Carica dati
                updateMessage("Caricamento dataset...");
                updateProgress(10, 100);

                DataImportService dataService =
                        ApplicationContext.getInstance().getDataImportService();

                Data data;
                try {
                    data = switch (config.getDataSource()) {
                        case HARDCODED -> {
                            Platform.runLater(() -> appendLog("Caricamento dataset hardcoded (PlayTennis)..."));
                            yield dataService.loadHardcodedData();
                        }
                        case CSV -> {
                            Platform.runLater(() -> appendLog("Caricamento dataset da CSV: " +
                                    config.getCsvFilePath()));
                            yield dataService.loadDataFromCSV(config.getCsvFilePath());
                        }
                        case DATABASE -> {
                            Platform.runLater(() -> appendLog("Connessione a database: " +
                                    config.getDbHost() + ":" + config.getDbPort()));
                            yield dataService.loadDataFromDatabase(
                                    config.getDbTableName(),
                                    config.getDbHost(),
                                    config.getDbPort(),
                                    config.getDbName(),
                                    config.getDbUser(),
                                    config.getDbPassword()
                            );
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

                // Esegui clustering
                updateMessage("Esecuzione clustering Quality Threshold...");
                updateProgress(30, 100);

                Platform.runLater(() -> {
                    appendLog("Avvio algoritmo QT...");
                    currentStepLabel.setText("Costruzione cluster...");
                });

                ClusteringService clusteringService =
                        ApplicationContext.getInstance().getClusteringService();

                long startTimeMs = System.currentTimeMillis();

                try {
                    // Crea QTMiner
                    miner = new QTMiner(config.getRadius());

                    // Esegui compute (questo è il lavoro principale)
                    updateProgress(40, 100);
                    int numClusters = miner.compute(data);
                    ClusterSet clusterSet = miner.getC();

                    long executionTimeMs = System.currentTimeMillis() - startTimeMs;

                    if (isCancelled()) {
                        return null;
                    }

                    // Aggiorna progresso
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

                        // Mostra dettagli cluster (primi 5)
                        int i = 0;
                        for (Cluster cluster : clusterSet) {
                            if (i >= 5) break;
                            appendLog("Cluster " + (i + 1) + ": " + cluster.getSize() + " tuple");
                            i++;
                        }
                        if (finalNumClusters > 5) {
                            appendLog("... e altri " + (finalNumClusters - 5) + " cluster");
                        }
                    });

                    // Crea ClusteringResult
                    ClusteringResult result = new ClusteringResult(
                            clusterSet,
                            data,
                            config.getRadius(),
                            executionTimeMs,
                            miner
                    );

                    // Salva nel contesto
                    ApplicationContext.getInstance().setCurrentResult(result);

                    updateProgress(100, 100);
                    updateMessage("Clustering completato con successo");

                    Platform.runLater(() -> {
                        appendLog("");
                        appendLog("========================================");
                        appendLog("CLUSTERING COMPLETATO CON SUCCESSO");
                        appendLog("========================================");
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
     *
     * @param message messaggio di log
     */
    private void appendLog(String message) {
        Platform.runLater(() -> {
            logTextArea.appendText(message + "\n");
            logTextArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    /**
     * Aggiorna l'etichetta della percentuale di progresso.
     *
     * @param percent percentuale di progresso (0-100)
     */
    private void updateProgressPercent(int percent) {
        Platform.runLater(() -> progressPercentLabel.setText(percent + "%"));
    }

    /**
     * Gestisce il successo del clustering.
     */
    private void handleClusteringSuccess() {
        logger.info("Clustering completato con successo");

        updateProgressPercent(100);

        statusMessageLabel.setText("Clustering completato con successo!");
        statusMessageLabel.setStyle("-fx-text-fill: #27ae60;");
        statusFooter.setVisible(true);
        statusFooter.setManaged(true);

        btnCancel.setVisible(false);
        btnCancel.setManaged(false);
        btnViewResults.setVisible(true);
        btnViewResults.setManaged(true);

        appendLog("\nPronto per visualizzare i risultati.");
    }

    /**
     * Gestisce il fallimento del clustering.
     */
    private void handleClusteringFailure() {
        logger.error("Clustering fallito", clusteringTask.getException());

        statusMessageLabel.setText("Clustering fallito: " + clusteringTask.getException().getMessage());
        statusMessageLabel.setStyle("-fx-text-fill: #e74c3c;");
        statusFooter.setVisible(true);
        statusFooter.setManaged(true);

        appendLog("\nERRORE: Clustering fallito!");
        appendLog("Motivo: " + clusteringTask.getException().getMessage());

        showError("Clustering Fallito",
                 "Si è verificato un errore durante il clustering:\n" + clusteringTask.getException().getMessage());
    }

    /**
     * Gestisce l'annullamento del clustering.
     */
    private void handleClusteringCancelled() {
        logger.info("Clustering annullato dall'utente");

        statusMessageLabel.setText("Clustering annullato dall'utente");
        statusMessageLabel.setStyle("-fx-text-fill: #f39c12;");
        statusFooter.setVisible(true);
        statusFooter.setManaged(true);

        appendLog("\nClustering annullato dall'utente.");
    }

    /**
     * Gestisce il clic del pulsante annulla.
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
            showError("Errore Navigazione",
                    "Impossibile caricare la vista risultati:\n" + e.getMessage());
        }
    }

    /**
     * Mostra un dialogo di errore.
     *
     * @param title   titolo del dialogo
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
