package gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Main controller for the application window.
 * Manages navigation between different views and main window components.
 */
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    // MenuBar items
    @FXML private MenuItem menuNew;
    @FXML private MenuItem menuOpen;
    @FXML private MenuItem menuSave;
    @FXML private MenuItem menuSaveAs;
    @FXML private MenuItem menuExit;
    @FXML private MenuItem menuSettings;
    @FXML private CheckMenuItem menuShowToolbar;
    @FXML private CheckMenuItem menuShowStatusBar;
    @FXML private MenuItem menuHelp;
    @FXML private MenuItem menuAbout;

    // ToolBar
    @FXML private ToolBar toolbar;
    @FXML private Button btnNew;
    @FXML private Button btnOpen;
    @FXML private Button btnSave;
    @FXML private Button btnRun;
    @FXML private Button btnExport;

    // Content area and status bar
    @FXML private StackPane contentArea;
    @FXML private HBox statusBar;
    @FXML private Label statusLabel;
    @FXML private Label progressLabel;

    /**
     * Initialize the controller.
     * Called automatically after FXML loading.
     */
    @FXML
    public void initialize() {
        logger.info("Initializing MainController...");

        setupMenuHandlers();
        setupToolbarHandlers();
        setupStatusBar();

        logger.info("MainController initialized successfully");
    }

    /**
     * Setup menu item event handlers.
     */
    private void setupMenuHandlers() {
        // File menu
        menuNew.setOnAction(e -> handleNewAnalysis());
        menuOpen.setOnAction(e -> handleOpen());
        menuSave.setOnAction(e -> handleSave());
        menuSaveAs.setOnAction(e -> handleSaveAs());
        menuExit.setOnAction(e -> handleExit());

        // Edit menu
        menuSettings.setOnAction(e -> handleSettings());

        // View menu
        menuShowToolbar.setOnAction(e -> toolbar.setVisible(menuShowToolbar.isSelected()));
        menuShowStatusBar.setOnAction(e -> statusBar.setVisible(menuShowStatusBar.isSelected()));

        // Help menu
        menuHelp.setOnAction(e -> handleHelp());
        menuAbout.setOnAction(e -> handleAbout());
    }

    /**
     * Setup toolbar button event handlers.
     */
    private void setupToolbarHandlers() {
        btnNew.setOnAction(e -> handleNewAnalysis());
        btnOpen.setOnAction(e -> handleOpen());
        btnSave.setOnAction(e -> handleSave());
        btnRun.setOnAction(e -> handleRunClustering());
        btnExport.setOnAction(e -> handleExport());
    }

    /**
     * Setup status bar initial state.
     */
    private void setupStatusBar() {
        updateStatus("Ready");
    }

    /**
     * Update status bar message.
     *
     * @param message status message to display
     */
    public void updateStatus(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }

    /**
     * Update progress label in status bar.
     *
     * @param progress progress message
     */
    public void updateProgress(String progress) {
        Platform.runLater(() -> progressLabel.setText(progress));
    }

    /**
     * Navigate to a specific view by loading its FXML.
     *
     * @param fxmlFile FXML file name (e.g., "home.fxml")
     */
    public void navigateTo(String fxmlFile) {
        try {
            logger.info("Navigating to view: {}", fxmlFile);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + fxmlFile));
            Parent view = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            updateStatus("Loaded view: " + fxmlFile);
        } catch (IOException e) {
            logger.error("Failed to load view: {}", fxmlFile, e);
            showError("Failed to load view", "Could not load " + fxmlFile);
        }
    }

    // Event handlers

    private void handleNewAnalysis() {
        logger.info("New Analysis clicked");
        navigateTo("home.fxml");
        updateStatus("Ready to start new clustering analysis");
    }

    private void handleOpen() {
        logger.info("Open clicked");
        // TODO: Implement file chooser for opening .dmp files
        updateStatus("Open clustering file...");
    }

    private void handleSave() {
        logger.info("Save clicked");
        // TODO: Implement save functionality
        updateStatus("Save clustering...");
    }

    private void handleSaveAs() {
        logger.info("Save As clicked");
        // TODO: Implement save as functionality
        updateStatus("Save clustering as...");
    }

    private void handleExit() {
        logger.info("Exit clicked");
        Platform.exit();
    }

    private void handleSettings() {
        logger.info("Settings clicked");
        navigateTo("settings.fxml");
    }

    private void handleRunClustering() {
        logger.info("Run Clustering clicked");
        navigateTo("clustering.fxml");
    }

    private void handleExport() {
        logger.info("Export clicked");
        // TODO: Implement export functionality
        updateStatus("Export results...");
    }

    private void handleHelp() {
        logger.info("Help clicked");
        showInfo("Help", "QT Clustering GUI Documentation\n\nFor more information, visit the documentation.");
    }

    private void handleAbout() {
        logger.info("About clicked");
        showInfo("About QT Clustering",
                "QT Clustering GUI v1.0.0\n\n" +
                "Quality Threshold Clustering Algorithm\n" +
                "Developed for MAP Course\n\n" +
                "JavaFX GUI Application");
    }

    // Utility methods for dialogs

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
