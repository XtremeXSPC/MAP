package gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Controller for the Settings view.
 * Manages application configuration and preferences.
 */
public class SettingsController {

    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);
    private static final String SETTINGS_FILE = "qtgui.properties";

    // Appearance
    @FXML private ComboBox<String> themeComboBox;
    @FXML private ComboBox<String> fontSizeComboBox;
    @FXML private CheckBox showWelcomeCheckBox;

    // Performance
    @FXML private CheckBox enableCachingCheckBox;
    @FXML private Spinner<Integer> threadPoolSpinner;
    @FXML private TextField memoryLimitField;
    @FXML private CheckBox verboseLoggingCheckBox;

    // Clustering Defaults
    @FXML private TextField defaultRadiusField;
    @FXML private ComboBox<String> defaultDataSourceComboBox;
    @FXML private CheckBox autoStartClusteringCheckBox;

    // Export Settings
    @FXML private ComboBox<String> exportFormatComboBox;
    @FXML private TextField exportDirectoryField;
    @FXML private Button btnBrowseExportDir;
    @FXML private CheckBox includeTimestampCheckBox;

    // Database
    @FXML private TextField dbHostField;
    @FXML private TextField dbPortField;
    @FXML private TextField dbNameField;
    @FXML private TextField dbUsernameField;
    @FXML private PasswordField dbPasswordField;
    @FXML private Button btnTestConnection;

    // Buttons
    @FXML private Button btnResetDefaults;
    @FXML private Button btnCancel;
    @FXML private Button btnSaveSettings;

    // Status
    @FXML private HBox statusFooter;
    @FXML private Label statusMessageLabel;

    private Properties settings;

    /**
     * Initialize the controller.
     * Called automatically after FXML loading.
     */
    @FXML
    public void initialize() {
        logger.info("Initializing SettingsController...");

        settings = new Properties();

        setupSpinners();
        setupButtons();
        loadSettings();

        logger.info("SettingsController initialized successfully");
    }

    /**
     * Setup spinner controls.
     */
    private void setupSpinners() {
        SpinnerValueFactory<Integer> valueFactory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 16, 4);
        threadPoolSpinner.setValueFactory(valueFactory);
    }

    /**
     * Setup button event handlers.
     */
    private void setupButtons() {
        btnBrowseExportDir.setOnAction(e -> handleBrowseExportDirectory());
        btnTestConnection.setOnAction(e -> handleTestConnection());
        btnResetDefaults.setOnAction(e -> handleResetDefaults());
        btnCancel.setOnAction(e -> handleCancel());
        btnSaveSettings.setOnAction(e -> handleSaveSettings());
    }

    /**
     * Load settings from properties file.
     */
    private void loadSettings() {
        try {
            File settingsFile = new File(SETTINGS_FILE);
            if (settingsFile.exists()) {
                try (FileInputStream fis = new FileInputStream(settingsFile)) {
                    settings.load(fis);
                    applySettings();
                    logger.info("Settings loaded from {}", SETTINGS_FILE);
                }
            } else {
                // Apply defaults
                applyDefaults();
                logger.info("No settings file found, using defaults");
            }
        } catch (IOException e) {
            logger.error("Failed to load settings", e);
            applyDefaults();
        }
    }

    /**
     * Apply settings to UI controls.
     */
    private void applySettings() {
        // Appearance
        themeComboBox.setValue(settings.getProperty("theme", "Light"));
        fontSizeComboBox.setValue(settings.getProperty("fontSize", "Medium (14px)"));
        showWelcomeCheckBox.setSelected(Boolean.parseBoolean(settings.getProperty("showWelcome", "true")));

        // Performance
        enableCachingCheckBox.setSelected(Boolean.parseBoolean(settings.getProperty("enableCaching", "true")));
        threadPoolSpinner.getValueFactory().setValue(Integer.parseInt(settings.getProperty("threadPoolSize", "4")));
        memoryLimitField.setText(settings.getProperty("memoryLimit", "512"));
        verboseLoggingCheckBox.setSelected(Boolean.parseBoolean(settings.getProperty("verboseLogging", "false")));

        // Clustering Defaults
        defaultRadiusField.setText(settings.getProperty("defaultRadius", "0.5"));
        defaultDataSourceComboBox.setValue(settings.getProperty("defaultDataSource", "Hardcoded (PlayTennis)"));
        autoStartClusteringCheckBox.setSelected(Boolean.parseBoolean(settings.getProperty("autoStart", "false")));

        // Export
        exportFormatComboBox.setValue(settings.getProperty("exportFormat", "CSV"));
        exportDirectoryField.setText(settings.getProperty("exportDirectory", System.getProperty("user.home")));
        includeTimestampCheckBox.setSelected(Boolean.parseBoolean(settings.getProperty("includeTimestamp", "true")));

        // Database
        dbHostField.setText(settings.getProperty("dbHost", "localhost"));
        dbPortField.setText(settings.getProperty("dbPort", "3306"));
        dbNameField.setText(settings.getProperty("dbName", "MapDB"));
        dbUsernameField.setText(settings.getProperty("dbUsername", "MapUser"));
        // Password not loaded from file for security
    }

    /**
     * Apply default values to UI controls.
     */
    private void applyDefaults() {
        themeComboBox.setValue("Light");
        fontSizeComboBox.setValue("Medium (14px)");
        showWelcomeCheckBox.setSelected(true);

        enableCachingCheckBox.setSelected(true);
        threadPoolSpinner.getValueFactory().setValue(4);
        memoryLimitField.setText("512");
        verboseLoggingCheckBox.setSelected(false);

        defaultRadiusField.setText("0.5");
        defaultDataSourceComboBox.setValue("Hardcoded (PlayTennis)");
        autoStartClusteringCheckBox.setSelected(false);

        exportFormatComboBox.setValue("CSV");
        exportDirectoryField.setText(System.getProperty("user.home"));
        includeTimestampCheckBox.setSelected(true);

        dbHostField.setText("localhost");
        dbPortField.setText("3306");
        dbNameField.setText("MapDB");
        dbUsernameField.setText("MapUser");
        dbPasswordField.clear();
    }

    /**
     * Handle browse export directory button.
     */
    private void handleBrowseExportDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Export Directory");

        String currentDir = exportDirectoryField.getText();
        if (currentDir != null && !currentDir.isEmpty()) {
            File dir = new File(currentDir);
            if (dir.exists() && dir.isDirectory()) {
                directoryChooser.setInitialDirectory(dir);
            }
        }

        File selectedDirectory = directoryChooser.showDialog(btnBrowseExportDir.getScene().getWindow());
        if (selectedDirectory != null) {
            exportDirectoryField.setText(selectedDirectory.getAbsolutePath());
            logger.info("Export directory set to: {}", selectedDirectory.getAbsolutePath());
        }
    }

    /**
     * Handle test database connection button.
     */
    private void handleTestConnection() {
        logger.info("Testing database connection...");

        String host = dbHostField.getText();
        String port = dbPortField.getText();
        String dbName = dbNameField.getText();
        String username = dbUsernameField.getText();
        String password = dbPasswordField.getText();

        // TODO: Implement actual database connection test in Sprint 2
        // For now, show a simulated result

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Database Connection Test");
        alert.setHeaderText("Connection Test");
        alert.setContentText(
            "Database connection test will be implemented in Sprint 2.\n\n" +
            "Configuration:\n" +
            "Host: " + host + "\n" +
            "Port: " + port + "\n" +
            "Database: " + dbName + "\n" +
            "Username: " + username
        );
        alert.showAndWait();
    }

    /**
     * Handle reset to defaults button.
     */
    private void handleResetDefaults() {
        logger.info("Reset to defaults clicked");

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Reset Settings");
        confirmAlert.setHeaderText("Reset all settings to defaults?");
        confirmAlert.setContentText("This action cannot be undone.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                applyDefaults();
                showStatus("Settings reset to defaults", true);
                logger.info("Settings reset to defaults");
            }
        });
    }

    /**
     * Handle cancel button.
     */
    private void handleCancel() {
        logger.info("Cancel clicked - reloading settings");
        loadSettings();
        showStatus("Settings reloaded", false);
    }

    /**
     * Handle save settings button.
     */
    private void handleSaveSettings() {
        logger.info("Save settings clicked");

        // Validate inputs
        if (!validateSettings()) {
            return;
        }

        // Collect settings from UI
        settings.setProperty("theme", themeComboBox.getValue());
        settings.setProperty("fontSize", fontSizeComboBox.getValue());
        settings.setProperty("showWelcome", String.valueOf(showWelcomeCheckBox.isSelected()));

        settings.setProperty("enableCaching", String.valueOf(enableCachingCheckBox.isSelected()));
        settings.setProperty("threadPoolSize", String.valueOf(threadPoolSpinner.getValue()));
        settings.setProperty("memoryLimit", memoryLimitField.getText());
        settings.setProperty("verboseLogging", String.valueOf(verboseLoggingCheckBox.isSelected()));

        settings.setProperty("defaultRadius", defaultRadiusField.getText());
        settings.setProperty("defaultDataSource", defaultDataSourceComboBox.getValue());
        settings.setProperty("autoStart", String.valueOf(autoStartClusteringCheckBox.isSelected()));

        settings.setProperty("exportFormat", exportFormatComboBox.getValue());
        settings.setProperty("exportDirectory", exportDirectoryField.getText());
        settings.setProperty("includeTimestamp", String.valueOf(includeTimestampCheckBox.isSelected()));

        settings.setProperty("dbHost", dbHostField.getText());
        settings.setProperty("dbPort", dbPortField.getText());
        settings.setProperty("dbName", dbNameField.getText());
        settings.setProperty("dbUsername", dbUsernameField.getText());
        // Note: Password not saved to file for security

        // Save to file
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            settings.store(fos, "QT Clustering GUI Settings");
            showStatus("Settings saved successfully", true);
            logger.info("Settings saved to {}", SETTINGS_FILE);
        } catch (IOException e) {
            logger.error("Failed to save settings", e);
            showError("Failed to save settings", "Could not write settings file: " + e.getMessage());
        }
    }

    /**
     * Validate settings inputs.
     *
     * @return true if all inputs are valid
     */
    private boolean validateSettings() {
        // Validate default radius
        try {
            double radius = Double.parseDouble(defaultRadiusField.getText());
            if (radius < 0) {
                showError("Invalid Input", "Default radius must be non-negative");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Invalid Input", "Default radius must be a valid number");
            return false;
        }

        // Validate memory limit
        try {
            int memory = Integer.parseInt(memoryLimitField.getText());
            if (memory < 128) {
                showError("Invalid Input", "Memory limit must be at least 128 MB");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Invalid Input", "Memory limit must be a valid integer");
            return false;
        }

        // Validate database port
        try {
            int port = Integer.parseInt(dbPortField.getText());
            if (port < 1 || port > 65535) {
                showError("Invalid Input", "Database port must be between 1 and 65535");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Invalid Input", "Database port must be a valid integer");
            return false;
        }

        return true;
    }

    /**
     * Show status message.
     *
     * @param message status message
     * @param success true if success, false if warning
     */
    private void showStatus(String message, boolean success) {
        statusMessageLabel.setText(message);
        statusMessageLabel.getStyleClass().clear();
        statusMessageLabel.getStyleClass().add(success ? "label-success" : "label-warning");
        statusFooter.setVisible(true);
        statusFooter.setManaged(true);

        // Hide after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    statusFooter.setVisible(false);
                    statusFooter.setManaged(false);
                });
            } catch (InterruptedException e) {
                // Ignore
            }
        }).start();
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
