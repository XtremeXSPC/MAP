package gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the Results view.
 * Displays clustering results with cluster tree and detailed information.
 */
public class ResultsController {

    private static final Logger logger = LoggerFactory.getLogger(ResultsController.class);

    // Header and summary
    @FXML private Label summaryLabel;

    // Statistics labels
    @FXML private Label totalClustersLabel;
    @FXML private Label totalTuplesLabel;
    @FXML private Label radiusLabel;
    @FXML private Label avgClusterSizeLabel;
    @FXML private Label largestClusterLabel;
    @FXML private Label smallestClusterLabel;

    // TreeView and details
    @FXML private TreeView<String> clusterTreeView;
    @FXML private TextArea summaryTextArea;
    @FXML private TextArea tuplesTextArea;
    @FXML private TextArea statisticsTextArea;

    // Buttons
    @FXML private Button btnVisualize;
    @FXML private Button btnExport;
    @FXML private Button btnSave;
    @FXML private Button btnNewAnalysis;
    @FXML private Button btnExpandAll;
    @FXML private Button btnCollapseAll;
    @FXML private Button btnCopyDetails;

    // Footer
    @FXML private Label statusLabel;
    @FXML private Label timestampLabel;

    /**
     * Initialize the controller.
     * Called automatically after FXML loading.
     */
    @FXML
    public void initialize() {
        logger.info("Initializing ResultsController...");

        setupTreeView();
        setupButtons();
        loadSampleData(); // For Sprint 1 demo
        updateTimestamp();

        logger.info("ResultsController initialized successfully");
    }

    /**
     * Setup TreeView with selection listener.
     */
    private void setupTreeView() {
        clusterTreeView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    handleClusterSelection(newValue);
                }
            }
        );
    }

    /**
     * Setup button event handlers.
     */
    private void setupButtons() {
        btnVisualize.setOnAction(e -> handleVisualize());
        btnExport.setOnAction(e -> handleExport());
        btnSave.setOnAction(e -> handleSave());
        btnNewAnalysis.setOnAction(e -> handleNewAnalysis());
        btnExpandAll.setOnAction(e -> expandAllNodes());
        btnCollapseAll.setOnAction(e -> collapseAllNodes());
        btnCopyDetails.setOnAction(e -> handleCopyDetails());
    }

    /**
     * Load sample clustering data (for Sprint 1 demonstration).
     * In Sprint 2, this will be replaced with actual clustering results.
     */
    private void loadSampleData() {
        // Sample statistics
        totalClustersLabel.setText("11");
        totalTuplesLabel.setText("14");
        radiusLabel.setText("0.0");
        avgClusterSizeLabel.setText("1.27");
        largestClusterLabel.setText("3");
        smallestClusterLabel.setText("1");

        summaryLabel.setText("Clustering completed with 11 clusters from 14 tuples (radius: 0.0)");

        // Build sample tree
        TreeItem<String> rootItem = new TreeItem<>("Clustering Results");
        rootItem.setExpanded(true);

        // Sample clusters
        for (int i = 1; i <= 11; i++) {
            TreeItem<String> clusterItem = new TreeItem<>("Cluster " + i);

            // Sample tuples in cluster
            int tupleCount = (i % 3) + 1;
            for (int j = 0; j < tupleCount; j++) {
                TreeItem<String> tupleItem = new TreeItem<>("Tuple " + ((i - 1) * 2 + j + 1));
                clusterItem.getChildren().add(tupleItem);
            }

            rootItem.getChildren().add(clusterItem);
        }

        clusterTreeView.setRoot(rootItem);
        clusterTreeView.setShowRoot(false);

        statusLabel.setText("11 clusters loaded successfully");
    }

    /**
     * Handle cluster selection in tree.
     *
     * @param item selected tree item
     */
    private void handleClusterSelection(TreeItem<String> item) {
        String value = item.getValue();
        logger.info("Cluster selected: {}", value);

        if (value.startsWith("Cluster ")) {
            // Extract cluster number
            String clusterNum = value.replace("Cluster ", "");

            // Update summary tab
            summaryTextArea.setText(
                "Cluster " + clusterNum + " Summary\n" +
                "================================\n\n" +
                "Centroid: (sunny, hot, high, weak, no)\n" +
                "Size: " + item.getChildren().size() + " tuples\n" +
                "Average Distance: 0.133\n\n" +
                "This cluster contains tuples with similar characteristics."
            );

            // Update tuples tab
            StringBuilder tuples = new StringBuilder();
            tuples.append("Tuples in Cluster ").append(clusterNum).append("\n");
            tuples.append("================================\n\n");

            int tupleNum = 1;
            for (TreeItem<String> child : item.getChildren()) {
                tuples.append(tupleNum++).append(". ")
                      .append(child.getValue())
                      .append(": (sunny, hot, high, weak, no) - distance: 0.0\n");
            }

            tuplesTextArea.setText(tuples.toString());

            // Update statistics tab
            statisticsTextArea.setText(
                "Cluster " + clusterNum + " Statistics\n" +
                "================================\n\n" +
                "Number of tuples: " + item.getChildren().size() + "\n" +
                "Minimum distance: 0.0\n" +
                "Maximum distance: 0.2\n" +
                "Average distance: 0.133\n" +
                "Median distance: 0.1\n\n" +
                "Quality metrics:\n" +
                "- Cohesion: 0.867 (high)\n" +
                "- Separation: 0.654 (moderate)"
            );

            statusLabel.setText("Viewing details for Cluster " + clusterNum);

        } else if (value.startsWith("Tuple ")) {
            // Show tuple details
            summaryTextArea.setText(
                value + " Details\n" +
                "================================\n\n" +
                "Attributes:\n" +
                "- Outlook: sunny\n" +
                "- Temperature: hot\n" +
                "- Humidity: high\n" +
                "- Wind: weak\n" +
                "- PlayTennis: no\n\n" +
                "Distance from centroid: 0.0"
            );

            tuplesTextArea.clear();
            statisticsTextArea.clear();

            statusLabel.setText("Viewing details for " + value);
        }
    }

    /**
     * Expand all tree nodes.
     */
    private void expandAllNodes() {
        TreeItem<String> root = clusterTreeView.getRoot();
        if (root != null) {
            expandTreeView(root);
        }
        statusLabel.setText("All clusters expanded");
    }

    /**
     * Recursively expand tree item and children.
     *
     * @param item tree item to expand
     */
    private void expandTreeView(TreeItem<String> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);
            for (TreeItem<String> child : item.getChildren()) {
                expandTreeView(child);
            }
        }
    }

    /**
     * Collapse all tree nodes.
     */
    private void collapseAllNodes() {
        TreeItem<String> root = clusterTreeView.getRoot();
        if (root != null) {
            collapseTreeView(root);
        }
        statusLabel.setText("All clusters collapsed");
    }

    /**
     * Recursively collapse tree item and children.
     *
     * @param item tree item to collapse
     */
    private void collapseTreeView(TreeItem<String> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(false);
            for (TreeItem<String> child : item.getChildren()) {
                collapseTreeView(child);
            }
        }
    }

    /**
     * Update timestamp label with current time.
     */
    private void updateTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        timestampLabel.setText("Generated: " + LocalDateTime.now().format(formatter));
    }

    /**
     * Handle visualize button click.
     */
    private void handleVisualize() {
        logger.info("Visualize clicked");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Visualization");
        alert.setHeaderText("Cluster Visualization");
        alert.setContentText("2D/3D visualization will be implemented in Sprint 3.");
        alert.showAndWait();
    }

    /**
     * Handle export button click.
     */
    private void handleExport() {
        logger.info("Export clicked");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export");
        alert.setHeaderText("Export Results");
        alert.setContentText("Export functionality (CSV, PDF, PNG) will be implemented in Sprint 4.");
        alert.showAndWait();
    }

    /**
     * Handle save button click.
     */
    private void handleSave() {
        logger.info("Save clicked");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Save");
        alert.setHeaderText("Save Clustering");
        alert.setContentText("Save functionality (.dmp files) will be implemented in Sprint 4.");
        alert.showAndWait();
    }

    /**
     * Handle new analysis button click.
     */
    private void handleNewAnalysis() {
        logger.info("New Analysis clicked");

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("New Analysis");
        confirmAlert.setHeaderText("Start a new clustering analysis?");
        confirmAlert.setContentText("Current results will be cleared.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Navigate back to home view
                logger.info("User confirmed new analysis");
                statusLabel.setText("Starting new analysis...");
            }
        });
    }

    /**
     * Handle copy details button click.
     */
    private void handleCopyDetails() {
        String content = summaryTextArea.getText();

        if (content != null && !content.isEmpty()) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(content);
            clipboard.setContent(clipboardContent);

            statusLabel.setText("Details copied to clipboard");
            logger.info("Cluster details copied to clipboard");
        }
    }
}
