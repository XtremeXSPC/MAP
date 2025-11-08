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
 * Controller per la vista Home.
 * Gestisce la selezione del dataset e la configurazione dei parametri di clustering.
 */
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    // Selezione dataset
    @FXML private ComboBox<String> dataSourceComboBox;
    @FXML private HBox csvFileSection;
    @FXML private TextField csvFilePathField;
    @FXML private Button btnBrowseFile;
    @FXML private VBox databaseSection;
    @FXML private TextField tableNameField;
    @FXML private TextField dbHostField;
    @FXML private TextField dbPortField;
    @FXML private Button btnPreviewDataset;

    // Parametri clustering
    @FXML private TextField radiusField;
    @FXML private Label radiusValidationLabel;

    // Opzioni
    @FXML private CheckBox enableCachingCheckBox;
    @FXML private CheckBox verboseLoggingCheckBox;

    // Pulsanti azione
    @FXML private Button btnCancel;
    @FXML private Button btnStartClustering;

    // Validazione
    @FXML private HBox validationSummary;
    @FXML private Label validationMessageLabel;

    private File selectedCsvFile;

    /**
     * Inizializza il controller.
     * Chiamato automaticamente dopo il caricamento FXML.
     */
    @FXML
    public void initialize() {
        logger.info("Inizializzazione HomeController...");

        setupDataSourceSelection();
        setupRadiusValidation();
        setupButtons();

        // Imposta la sorgente dati predefinita
        dataSourceComboBox.getSelectionModel().selectFirst();

        logger.info("HomeController inizializzato con successo");
    }

    /**
     * Configura il comportamento della combo box per la selezione della sorgente dati.
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
     * Gestisce il cambiamento della selezione della sorgente dati.
     *
     * @param dataSource sorgente dati selezionata
     */
    private void handleDataSourceChange(String dataSource) {
        logger.info("Sorgente dati cambiata in: {}", dataSource);

        // Nascondi tutte le sezioni opzionali
        csvFileSection.setVisible(false);
        csvFileSection.setManaged(false);
        databaseSection.setVisible(false);
        databaseSection.setManaged(false);

        // Mostra la sezione rilevante in base alla selezione
        if (dataSource.contains("CSV")) {
            csvFileSection.setVisible(true);
            csvFileSection.setManaged(true);
        } else if (dataSource.contains("Database")) {
            databaseSection.setVisible(true);
            databaseSection.setManaged(true);
        }
    }

    /**
     * Configura la validazione del campo radius.
     */
    private void setupRadiusValidation() {
        radiusField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateRadius(newValue);
            validateForm();
        });
    }

    /**
     * Valida l'input del radius.
     *
     * @param value valore del radius
     * @return true se valido
     */
    private boolean validateRadius(String value) {
        if (value == null || value.trim().isEmpty()) {
            radiusValidationLabel.setText("Il radius è obbligatorio");
            return false;
        }

        try {
            double radius = Double.parseDouble(value.trim());
            if (radius < 0) {
                radiusValidationLabel.setText("Il radius deve essere non negativo");
                return false;
            }
            radiusValidationLabel.setText("");
            return true;
        } catch (NumberFormatException e) {
            radiusValidationLabel.setText("Formato numero non valido");
            return false;
        }
    }

    /**
     * Configura i gestori eventi dei pulsanti.
     */
    private void setupButtons() {
        btnBrowseFile.setOnAction(e -> handleBrowseFile());
        btnPreviewDataset.setOnAction(e -> handlePreviewDataset());
        btnCancel.setOnAction(e -> handleCancel());
        btnStartClustering.setOnAction(e -> handleStartClustering());
    }

    /**
     * Valida l'intero modulo.
     *
     * @return true se il modulo è valido
     */
    private boolean validateForm() {
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        // Valida la sorgente dati
        String dataSource = dataSourceComboBox.getValue();
        if (dataSource == null) {
            isValid = false;
            errors.append("Selezionare una sorgente dati. ");
        } else if (dataSource.contains("CSV") && selectedCsvFile == null) {
            isValid = false;
            errors.append("Selezionare un file CSV. ");
        } else if (dataSource.contains("Database")) {
            if (tableNameField.getText() == null || tableNameField.getText().trim().isEmpty()) {
                isValid = false;
                errors.append("Inserire un nome tabella. ");
            }
        }

        // Valida il radius
        if (!validateRadius(radiusField.getText())) {
            isValid = false;
            errors.append("Inserire un radius valido. ");
        }

        // Aggiorna l'UI in base alla validazione
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
     * Gestisce il clic del pulsante sfoglia file.
     */
    private void handleBrowseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona File CSV");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("File CSV", "*.csv")
        );

        File file = fileChooser.showOpenDialog(btnBrowseFile.getScene().getWindow());
        if (file != null) {
            selectedCsvFile = file;
            csvFilePathField.setText(file.getAbsolutePath());
            logger.info("File CSV selezionato: {}", file.getAbsolutePath());
            validateForm();
        }
    }

    /**
     * Gestisce il clic del pulsante anteprima dataset.
     */
    private void handlePreviewDataset() {
        logger.info("Anteprima dataset cliccato");

        // TODO: Implementare dialogo anteprima dataset
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Anteprima Dataset");
        alert.setHeaderText("Anteprima Dataset");
        alert.setContentText("La funzionalità di anteprima dataset sarà implementata nello Sprint 2.");
        alert.showAndWait();
    }

    /**
     * Gestisce il clic del pulsante annulla.
     */
    private void handleCancel() {
        logger.info("Annulla cliccato");

        // Pulisce il modulo
        dataSourceComboBox.getSelectionModel().selectFirst();
        radiusField.clear();
        csvFilePathField.clear();
        selectedCsvFile = null;
        enableCachingCheckBox.setSelected(true);
        verboseLoggingCheckBox.setSelected(false);

        validateForm();
    }

    /**
     * Gestisce il clic del pulsante avvia clustering.
     */
    private void handleStartClustering() {
        if (!validateForm()) {
            logger.warn("Validazione modulo fallita");
            return;
        }

        logger.info("Avvio clustering con parametri:");
        logger.info("  Sorgente dati: {}", dataSourceComboBox.getValue());
        logger.info("  Radius: {}", radiusField.getText());
        logger.info("  Caching abilitato: {}", enableCachingCheckBox.isSelected());
        logger.info("  Logging verboso: {}", verboseLoggingCheckBox.isSelected());

        if (selectedCsvFile != null) {
            logger.info("  File CSV: {}", selectedCsvFile.getAbsolutePath());
        }

        // TODO: Navigare alla vista clustering e avviare il processo di clustering
        // Questo sarà implementato nello Sprint 2
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Clustering");
        alert.setHeaderText("Avvia Clustering");
        alert.setContentText("La funzionalità di clustering sarà implementata nello Sprint 2.\n\n" +
                           "Parametri acquisiti:\n" +
                           "- Sorgente Dati: " + dataSourceComboBox.getValue() + "\n" +
                           "- Radius: " + radiusField.getText() + "\n" +
                           "- Caching: " + enableCachingCheckBox.isSelected());
        alert.showAndWait();
    }

    /**
     * Restituisce il valore del radius selezionato.
     *
     * @return valore del radius
     */
    public double getRadius() {
        try {
            return Double.parseDouble(radiusField.getText().trim());
        } catch (NumberFormatException e) {
            logger.warn("Valore radius non valido: '{}'", radiusField.getText(), e);
            return Double.NaN;
        }
    }

    /**
     * Restituisce il tipo di sorgente dati selezionato.
     *
     * @return tipo di sorgente dati
     */
    public String getDataSourceType() {
        return dataSourceComboBox.getValue();
    }

    /**
     * Verifica se il caching è abilitato.
     *
     * @return true se il caching è abilitato
     */
    public boolean isCachingEnabled() {
        return enableCachingCheckBox.isSelected();
    }

    /**
     * Restituisce il file CSV selezionato.
     *
     * @return file CSV o null se non selezionato
     */
    public File getSelectedCsvFile() {
        return selectedCsvFile;
    }
}
