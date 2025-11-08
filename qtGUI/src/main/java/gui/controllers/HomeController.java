package gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Controller for the Home view.
 * Handles dataset selection and clustering parameter configuration.
 */
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    // Dataset selection
    @FXML private ComboBox<String> dataSourceComboBox;
    @FXML private HBox csvFileSection;
    @FXML private TextField csvFilePathField;
    @FXML private Button btnBrowseFile;
    @FXML private VBox databaseSection;
    @FXML private TextField tableNameField;
    @FXML private TextField dbHostField;
    @FXML private TextField dbPortField;
    @FXML private Button btnPreviewDataset;

    // Clustering parameters
    @FXML private TextField radiusField;
    @FXML private Label radiusValidationLabel;

    // Options
    @FXML private CheckBox enableCachingCheckBox;
    @FXML private CheckBox verboseLoggingCheckBox;

    // Action buttons
    @FXML private Button btnCancel;
    @FXML private Button btnStartClustering;

    // Validation
    @FXML private HBox validationSummary;
    @FXML private Label validationMessageLabel;

    private File selectedCsvFile;

    /**
     * Initialize the controller.
     * Called automatically after FXML loading.
     */
    @FXML
    public void initialize() {
        logger.info("Initializing HomeController...");

        setupDataSourceSelection();
        setupRadiusValidation();
        setupButtons();

        // Set default data source
        dataSourceComboBox.getSelectionModel().selectFirst();

        logger.info("HomeController initialized successfully");
    }

    /**
     * Setup data source combo box behavior.
     */
    private void setupDataSourceSelection() {
        dataSourceComboBox.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                handleDataSourceChange(newValue);
                validateForm();
            }
        );
    }

    /**
     * Handle data source selection change.
     *
     * @param dataSource selected data source
     */
    private void handleDataSourceChange(String dataSource) {
        logger.info("Data source changed to: {}", dataSource);

        // Hide all optional sections
        csvFileSection.setVisible(false);
        csvFileSection.setManaged(false);
        databaseSection.setVisible(false);
        databaseSection.setManaged(false);

        // Show relevant section based on selection
        if (dataSource.contains("CSV")) {
            csvFileSection.setVisible(true);
            csvFileSection.setManaged(true);
        } else if (dataSource.contains("Database")) {
            databaseSection.setVisible(true);
            databaseSection.setManaged(true);
        }
    }

    /**
     * Setup radius field validation.
     */
    private void setupRadiusValidation() {
        radiusField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateRadius(newValue);
            validateForm();
        });
    }

    /**
     * Validate radius input.
     *
     * @param value radius value
     * @return true if valid
     */
    private boolean validateRadius(String value) {
        if (value == null || value.trim().isEmpty()) {
            radiusValidationLabel.setText("Radius is required");
            return false;
        }

        try {
            double radius = Double.parseDouble(value.trim());
            if (radius < 0) {
                radiusValidationLabel.setText("Radius must be non-negative");
                return false;
            }
            radiusValidationLabel.setText("");
            return true;
        } catch (NumberFormatException e) {
            radiusValidationLabel.setText("Invalid number format");
            return false;
        }
    }

    /**
     * Setup button event handlers.
     */
    private void setupButtons() {
        btnBrowseFile.setOnAction(e -> handleBrowseFile());
        btnPreviewDataset.setOnAction(e -> handlePreviewDataset());
        btnCancel.setOnAction(e -> handleCancel());
        btnStartClustering.setOnAction(e -> handleStartClustering());
    }

    /**
     * Validate the entire form.
     *
     * @return true if form is valid
     */
    private boolean validateForm() {
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        // Validate data source
        String dataSource = dataSourceComboBox.getValue();
        if (dataSource == null) {
            isValid = false;
            errors.append("Please select a data source. ");
        } else if (dataSource.contains("CSV") && selectedCsvFile == null) {
            isValid = false;
            errors.append("Please select a CSV file. ");
        } else if (dataSource.contains("Database")) {
            if (tableNameField.getText() == null || tableNameField.getText().trim().isEmpty()) {
                isValid = false;
                errors.append("Please enter a table name. ");
            }
        }

        // Validate radius
        if (!validateRadius(radiusField.getText())) {
            isValid = false;
            errors.append("Please enter a valid radius. ");
        }

        // Update UI based on validation
        btnStartClustering.setDisable(!isValid);

        if (!isValid && errors.length() > 0) {
            validationMessageLabel.setText(errors.toString());
            validationSummary.setVisible(true);
            validationSummary.setManaged(true);
        } else {
            validationSummary.setVisible(false);
            validationSummary.setManaged(false);
        }

        return isValid;
    }

    /**
     * Handle browse file button click.
     */
    private void handleBrowseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showOpenDialog(btnBrowseFile.getScene().getWindow());
        if (file != null) {
            selectedCsvFile = file;
            csvFilePathField.setText(file.getAbsolutePath());
            logger.info("Selected CSV file: {}", file.getAbsolutePath());
            validateForm();
        }
    }

    /**
     * Handle preview dataset button click.
     */
    private void handlePreviewDataset() {
        logger.info("Preview dataset clicked");

        // TODO: Implement dataset preview dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dataset Preview");
        alert.setHeaderText("Dataset Preview");
        alert.setContentText("Dataset preview functionality will be implemented in Sprint 2.");
        alert.showAndWait();
    }

    /**
     * Handle cancel button click.
     */
    private void handleCancel() {
        logger.info("Cancel clicked");

        // Clear form
        dataSourceComboBox.getSelectionModel().selectFirst();
        radiusField.clear();
        csvFilePathField.clear();
        selectedCsvFile = null;
        enableCachingCheckBox.setSelected(true);
        verboseLoggingCheckBox.setSelected(false);

        validateForm();
    }

    /**
     * Handle start clustering button click.
     */
    private void handleStartClustering() {
        if (!validateForm()) {
            logger.warn("Form validation failed");
            return;
        }

        logger.info("Starting clustering with parameters:");
        logger.info("  Data source: {}", dataSourceComboBox.getValue());
        logger.info("  Radius: {}", radiusField.getText());
        logger.info("  Enable caching: {}", enableCachingCheckBox.isSelected());
        logger.info("  Verbose logging: {}", verboseLoggingCheckBox.isSelected());

        if (selectedCsvFile != null) {
            logger.info("  CSV file: {}", selectedCsvFile.getAbsolutePath());
        }

        // TODO: Navigate to clustering view and start clustering process
        // This will be implemented in Sprint 2
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Clustering");
        alert.setHeaderText("Start Clustering");
        alert.setContentText("Clustering functionality will be implemented in Sprint 2.\n\n" +
                           "Parameters captured:\n" +
                           "- Data Source: " + dataSourceComboBox.getValue() + "\n" +
                           "- Radius: " + radiusField.getText() + "\n" +
                           "- Caching: " + enableCachingCheckBox.isSelected());
        alert.showAndWait();
    }

    /**
     * Get the selected radius value.
     *
     * @return radius value
     */
    public double getRadius() {
        return Double.parseDouble(radiusField.getText().trim());
    }

    /**
     * Get the selected data source type.
     *
     * @return data source type
     */
    public String getDataSourceType() {
        return dataSourceComboBox.getValue();
    }

    /**
     * Check if caching is enabled.
     *
     * @return true if caching enabled
     */
    public boolean isCachingEnabled() {
        return enableCachingCheckBox.isSelected();
    }

    /**
     * Get the selected CSV file.
     *
     * @return CSV file or null if not selected
     */
    public File getSelectedCsvFile() {
        return selectedCsvFile;
    }
}
