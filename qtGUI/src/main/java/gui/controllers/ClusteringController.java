package gui.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private boolean isCancelled = false;

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
     * Crea il task di clustering (simulato per lo Sprint 1).
     * Nello Sprint 2, questo sarà integrato con il QTMiner effettivo.
     *
     * @return task di clustering
     */
    private Task<Void> createClusteringTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Processo di clustering simulato per lo Sprint 1
                // Questo sarà sostituito con l'integrazione del QTMiner effettivo nello Sprint 2

                int totalSteps = 10;
                int totalTuples = 14; // Dimensione dataset PlayTennis

                for (int step = 0; step < totalSteps; step++) {
                    if (isCancelled()) {
                        updateMessage("Annullato dall'utente");
                        break;
                    }

                    // Aggiorna il progresso
                    updateProgress(step, totalSteps);
                    updateMessage("Costruzione cluster candidato " + (step + 1) + "/" + totalSteps);

                    // Aggiorna i dettagli sul thread UI
                    final int currentStep = step + 1;
                    Platform.runLater(() -> {
                        currentStepLabel.setText("Costruzione cluster " + currentStep);
                        clustersFoundLabel.setText(String.valueOf(currentStep));
                        tuplesClusteredLabel.setText(Math.min(currentStep * 2, totalTuples) + " / " + totalTuples);

                        appendLog("Passo " + currentStep + ": Costruzione cluster candidato...");
                        appendLog("  Valutazione tuple per cluster " + currentStep);
                        appendLog("  Trovato cluster con " + (currentStep % 3 + 1) + " tuple");
                    });

                    // Simula il lavoro
                    Thread.sleep(500);
                }

                if (!isCancelled()) {
                    updateProgress(totalSteps, totalSteps);
                    updateMessage("Clustering completato con successo");

                    Platform.runLater(() -> {
                        appendLog("\n========================================");
                        appendLog("Clustering completato con successo!");
                        appendLog("Totale cluster trovati: " + totalSteps);
                        appendLog("========================================");
                    });
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

        // TODO: Navigare alla vista risultati
        // Questo sarà implementato quando la navigazione sarà completata
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Risultati");
        alert.setHeaderText("Visualizza Risultati");
        alert.setContentText("La navigazione alla vista risultati sarà implementata nello Sprint 1.11.");
        alert.showAndWait();
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
