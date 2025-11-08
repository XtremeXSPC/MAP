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
 * Controller for the Clustering view.
 * Manages the clustering process execution with progress feedback.
 */
public class ClusteringController {

    private static final Logger logger = LoggerFactory.getLogger(ClusteringController.class);

    // Progress components
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;
    @FXML private Label progressPercentLabel;

    // Status labels
    @FXML private Label currentStepLabel;
    @FXML private Label clustersFoundLabel;
    @FXML private Label tuplesClusteredLabel;
    @FXML private Label elapsedTimeLabel;

    // Log area
    @FXML private TextArea logTextArea;

    // Buttons
    @FXML private Button btnCancel;
    @FXML private Button btnViewResults;

    // Status footer
    @FXML private HBox statusFooter;
    @FXML private Label statusMessageLabel;

    private Task<Void> clusteringTask;
    private Instant startTime;
    private boolean isCancelled = false;

    /**
     * Initialize the controller.
     * Called automatically after FXML loading.
     */
    @FXML
    public void initialize() {
        logger.info("Initializing ClusteringController...");

        setupButtons();
        startClustering();

        logger.info("ClusteringController initialized successfully");
    }

    /**
     * Setup button event handlers.
     */
    private void setupButtons() {
        btnCancel.setOnAction(e -> handleCancel());
        btnViewResults.setOnAction(e -> handleViewResults());
    }

    /**
     * Start the clustering process.
     */
    private void startClustering() {
        startTime = Instant.now();
        isCancelled = false;

        // Create clustering task
        clusteringTask = createClusteringTask();

        // Bind progress
        progressBar.progressProperty().bind(clusteringTask.progressProperty());
        progressLabel.textProperty().bind(clusteringTask.messageProperty());

        // Handle task completion
        clusteringTask.setOnSucceeded(event -> handleClusteringSuccess());
        clusteringTask.setOnFailed(event -> handleClusteringFailure());
        clusteringTask.setOnCancelled(event -> handleClusteringCancelled());

        // Start task in background thread
        Thread thread = new Thread(clusteringTask);
        thread.setDaemon(true);
        thread.start();

        // Start elapsed time updater
        startElapsedTimeUpdater();

        logger.info("Clustering task started");
    }

    /**
     * Create the clustering task (simulated for Sprint 1).
     * In Sprint 2, this will integrate with actual QTMiner.
     *
     * @return clustering task
     */
    private Task<Void> createClusteringTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Simulated clustering process for Sprint 1
                // This will be replaced with actual QTMiner integration in Sprint 2

                int totalSteps = 10;
                int totalTuples = 14; // PlayTennis dataset size

                for (int step = 0; step < totalSteps; step++) {
                    if (isCancelled()) {
                        updateMessage("Cancelled by user");
                        break;
                    }

                    // Update progress
                    updateProgress(step, totalSteps);
                    updateMessage("Building candidate cluster " + (step + 1) + "/" + totalSteps);

                    // Update details on UI thread
                    final int currentStep = step + 1;
                    Platform.runLater(() -> {
                        currentStepLabel.setText("Building cluster " + currentStep);
                        clustersFoundLabel.setText(String.valueOf(currentStep));
                        tuplesClusteredLabel.setText(Math.min(currentStep * 2, totalTuples) + " / " + totalTuples);

                        appendLog("Step " + currentStep + ": Building candidate cluster...");
                        appendLog("  Evaluating tuples for cluster " + currentStep);
                        appendLog("  Found cluster with " + (currentStep % 3 + 1) + " tuples");
                    });

                    // Simulate work
                    Thread.sleep(500);
                }

                if (!isCancelled()) {
                    updateProgress(totalSteps, totalSteps);
                    updateMessage("Clustering completed successfully");

                    Platform.runLater(() -> {
                        appendLog("\n========================================");
                        appendLog("Clustering completed successfully!");
                        appendLog("Total clusters found: " + totalSteps);
                        appendLog("========================================");
                    });
                }

                return null;
            }
        };
    }

    /**
     * Start elapsed time updater thread.
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
     * Append a message to the log area.
     *
     * @param message log message
     */
    private void appendLog(String message) {
        Platform.runLater(() -> {
            logTextArea.appendText(message + "\n");
            logTextArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    /**
     * Update progress percentage label.
     *
     * @param percent progress percentage (0-100)
     */
    private void updateProgressPercent(int percent) {
        Platform.runLater(() -> progressPercentLabel.setText(percent + "%"));
    }

    /**
     * Handle clustering success.
     */
    private void handleClusteringSuccess() {
        logger.info("Clustering completed successfully");

        updateProgressPercent(100);

        statusMessageLabel.setText("Clustering completed successfully!");
        statusMessageLabel.setStyle("-fx-text-fill: #27ae60;");
        statusFooter.setVisible(true);
        statusFooter.setManaged(true);

        btnCancel.setVisible(false);
        btnCancel.setManaged(false);
        btnViewResults.setVisible(true);
        btnViewResults.setManaged(true);

        appendLog("\nReady to view results.");
    }

    /**
     * Handle clustering failure.
     */
    private void handleClusteringFailure() {
        logger.error("Clustering failed", clusteringTask.getException());

        statusMessageLabel.setText("Clustering failed: " + clusteringTask.getException().getMessage());
        statusMessageLabel.setStyle("-fx-text-fill: #e74c3c;");
        statusFooter.setVisible(true);
        statusFooter.setManaged(true);

        appendLog("\nERROR: Clustering failed!");
        appendLog("Reason: " + clusteringTask.getException().getMessage());

        showError("Clustering Failed",
                 "An error occurred during clustering:\n" + clusteringTask.getException().getMessage());
    }

    /**
     * Handle clustering cancellation.
     */
    private void handleClusteringCancelled() {
        logger.info("Clustering cancelled by user");

        statusMessageLabel.setText("Clustering cancelled by user");
        statusMessageLabel.setStyle("-fx-text-fill: #f39c12;");
        statusFooter.setVisible(true);
        statusFooter.setManaged(true);

        appendLog("\nClustering cancelled by user.");
    }

    /**
     * Handle cancel button click.
     */
    private void handleCancel() {
        logger.info("Cancel button clicked");

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Cancel Clustering");
        confirmAlert.setHeaderText("Are you sure you want to cancel the clustering process?");
        confirmAlert.setContentText("Progress will be lost.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                isCancelled = true;
                if (clusteringTask != null && clusteringTask.isRunning()) {
                    clusteringTask.cancel();
                }
                appendLog("\nUser requested cancellation...");
            }
        });
    }

    /**
     * Handle view results button click.
     */
    private void handleViewResults() {
        logger.info("View results clicked");

        // TODO: Navigate to results view
        // This will be implemented when navigation is completed
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Results");
        alert.setHeaderText("View Results");
        alert.setContentText("Navigation to results view will be implemented in Sprint 1.11.");
        alert.showAndWait();
    }

    /**
     * Show error dialog.
     *
     * @param title   dialog title
     * @param message error message
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
