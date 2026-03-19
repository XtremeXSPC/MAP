package gui.controllers;

//===---------------------------------------------------------------------------===//
// Importazioni Java standard.
import data.Data;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import database.DbAccess;
import gui.dialogs.DatasetPreviewDialog;
import gui.models.ClusteringConfiguration;
import gui.services.DataImportService.DataSource;
import gui.utils.ApplicationContext;
import gui.utils.DatasetLoader;
// Importazioni JavaFX.
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
//===---------------------------------------------------------------------------===//

/**
 * Controller per la vista Home.
 * <p>
 * Questa classe gestisce:
 * <ul>
 *   <li>Selezione della sorgente dati (hardcoded, Iris, CSV, database)</li>
 *   <li>Validazione dei parametri di input (radius e campi database)</li>
 *   <li>Anteprima del dataset tramite dialog dedicato</li>
 *   <li>Costruzione della {@link ClusteringConfiguration} per il clustering</li>
 *   <li>Navigazione verso la vista di esecuzione clustering</li>
 * </ul>
 * <p>
 * Il controller viene inizializzato automaticamente da JavaFX quando viene
 * caricato il file FXML associato. I campi annotati con {@code @FXML} vengono
 * iniettati dal framework.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.0.0
 * @see gui.models.ClusteringConfiguration
 * @see gui.utils.ApplicationContext
 */
public class HomeController {

    // Logger per la classe HomeController.
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    //===------------------------------ CONSTANTS ------------------------------===//

    // Costanti per valori di default del database.
    private static final String DEFAULT_DB_HOST = "localhost";
    private static final String DEFAULT_DB_NAME = "MapDB";
    private static final String DEFAULT_DB_USER = "MapUser";
    private static final String DEFAULT_DB_PASSWORD = "map";
    private static final int DEFAULT_DB_PORT = 3306;

    //===--------------------------- INSTANCE FIELDS ---------------------------===//

    // File CSV selezionato dall'utente.
    private File selectedCsvFile;

    //===---------------------------- FXML CONTROLS ----------------------------===//

    // Selezione dataset.
    @FXML
    private ComboBox<String> dataSourceComboBox;
    @FXML
    private HBox csvFileSection;
    @FXML
    private TextField csvFilePathField;
    @FXML
    private Button btnBrowseFile;
    @FXML
    private VBox databaseSection;
    @FXML
    private TextField dbHostField;
    @FXML
    private TextField dbPortField;
    @FXML
    private TextField dbNameField;
    @FXML
    private TextField dbUserField;
    @FXML
    private PasswordField dbPasswordField;
    @FXML
    private TextField tableNameField;
    @FXML
    private Button btnPreviewDataset;

    // Parametri clustering.
    @FXML
    private TextField radiusField;
    @FXML
    private Label radiusValidationLabel;

    // Opzioni.
    @FXML
    private CheckBox enableCachingCheckBox;
    @FXML
    private CheckBox verboseLoggingCheckBox;

    // Pulsanti azione.
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnStartClustering;

    // Validazione.
    @FXML
    private HBox validationSummary;
    @FXML
    private Label validationMessageLabel;

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruttore di default.
     * <p>
     * Invocato da JavaFX durante il caricamento FXML. Non inizializzare
     * qui i componenti, poiche' non sono ancora iniettati.
     *
     * @see #initialize()
     */
    public HomeController() {
        // Costruttore vuoto - l'inizializzazione avviene nel metodo initialize()
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Inizializza il controller dopo l'iniezione FXML.
     * <p>
     * Configura:
     * <ul>
     *   <li>Selezione sorgente dati</li>
     *   <li>Validazione radius e campi database</li>
     *   <li>Gestione eventi dei pulsanti</li>
     * </ul>
     * <p>
     * Non chiamare questo metodo manualmente.
     */
    @FXML
    public void initialize() {
        logger.info("Inizializzazione HomeController...");

        setupDataSourceSelection();
        setupRadiusValidation();
        setupButtons();

        // Imposta la sorgente dati predefinita.
        dataSourceComboBox.getSelectionModel().selectFirst();

        logger.info("HomeController inizializzato con successo");
    }

    /**
     * Restituisce il valore del radius selezionato.
     *
     * @return valore del radius o {@link Double#NaN} se non valido
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

    //===--------------------------- PRIVATE METHODS ---------------------------===//

    /**
     * Configura il comportamento della combo box per la selezione della sorgente dati.
     */
    private void setupDataSourceSelection() {
        dataSourceComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            handleDataSourceChange(newValue);
            validateForm();
        });
    }

    /**
     * Configura la validazione del campo radius e dei campi database.
     * <p>
     * Ogni modifica sui campi rilevanti attiva la validazione del form.
     */
    private void setupRadiusValidation() {
        radiusField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateRadius(newValue);
            validateForm();
        });

        // Aggiungi listener per campi database per rivalidare quando cambiano.
        tableNameField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        dbNameField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        dbUserField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        dbPasswordField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        dbHostField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        dbPortField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
    }

    /**
     * Valida l'input del radius.
     * <p>
     * Il valore deve essere numerico, non negativo e <= 1.0
     * (distanza normalizzata).
     *
     * @param value valore del radius
     * @return true se valido
     */
    private boolean validateRadius(String value) {
        if (value == null || value.trim().isEmpty()) {
            radiusValidationLabel.setText("Il radius è obbligatorio");
            return false;
        }

        // Controlla se è un numero valido.
        try {
            double radius = Double.parseDouble(value.trim());
            if (radius < 0) {
                radiusValidationLabel.setText("Il radius deve essere non negativo");
                return false;
            }
            if (radius > 1.0) {
                radiusValidationLabel.setText("Il radius deve essere <= 1.0 (distanza normalizzata)");
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
     * Configura i gestori eventi dei pulsanti della vista.
     */
    private void setupButtons() {
        btnBrowseFile.setOnAction(e -> handleBrowseFile());
        btnPreviewDataset.setOnAction(e -> handlePreviewDataset());
        btnCancel.setOnAction(e -> handleCancel());
        btnStartClustering.setOnAction(e -> handleStartClustering());
    }

    /**
     * Valida l'intero modulo e aggiorna lo stato della UI.
     * <p>
     * La validazione include:
     * <ul>
     *   <li>Sorgente dati selezionata</li>
     *   <li>Parametri database (se necessari)</li>
     *   <li>Radius valido</li>
     * </ul>
     *
     * @return true se il modulo è valido
     */
    private boolean validateForm() {
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        // Valida la sorgente dati.
        String dataSource = dataSourceComboBox.getValue();
        if (dataSource == null) {
            isValid = false;
            errors.append("Selezionare una sorgente dati. ");
        } else if (dataSource.contains("CSV") && selectedCsvFile == null) {
            isValid = false;
            errors.append("Selezionare un file CSV. ");
        } else if (dataSource.contains("Database")) {
            // Valida campi obbligatori database.
            if (dbNameField.getText() == null || dbNameField.getText().trim().isEmpty()) {
                isValid = false;
                errors.append("Inserire il nome del database. ");
            }
            if (dbUserField.getText() == null || dbUserField.getText().trim().isEmpty()) {
                isValid = false;
                errors.append("Inserire username database. ");
            }
            if (dbPasswordField.getText() == null || dbPasswordField.getText().isEmpty()) {
                isValid = false;
                errors.append("Inserire password database. ");
            }
            if (tableNameField.getText() == null || tableNameField.getText().trim().isEmpty()) {
                isValid = false;
                errors.append("Inserire nome tabella. ");
            }
        }

        // Valida il radius.
        if (!validateRadius(radiusField.getText())) {
            isValid = false;
            errors.append("Inserire un radius valido. ");
        }

        // Aggiorna l'UI in base alla validazione.
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

    //===--------------------------- EVENT HANDLERS ----------------------------===//

    /**
     * Gestisce il cambiamento della selezione della sorgente dati.
     * <p>
     * Mostra o nasconde le sezioni pertinenti (CSV o Database)
     * in base al tipo selezionato.
     *
     * @param dataSource sorgente dati selezionata
     */
    private void handleDataSourceChange(String dataSource) {
        logger.info("Sorgente dati cambiata in: {}", dataSource);

        // Nascondi tutte le sezioni opzionali.
        csvFileSection.setVisible(false);
        csvFileSection.setManaged(false);
        databaseSection.setVisible(false);
        databaseSection.setManaged(false);

        // Mostra la sezione rilevante in base alla selezione.
        if (dataSource.contains("CSV")) {
            csvFileSection.setVisible(true);
            csvFileSection.setManaged(true);
        } else if (dataSource.contains("Database")) {
            databaseSection.setVisible(true);
            databaseSection.setManaged(true);
        }
    }

    /**
     * Gestisce il clic del pulsante "Sfoglia" per file CSV.
     */
    private void handleBrowseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona File CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File CSV", "*.csv"));

        File file = fileChooser.showOpenDialog(btnBrowseFile.getScene().getWindow());
        if (file != null) {
            selectedCsvFile = file;
            csvFilePathField.setText(file.getAbsolutePath());
            logger.info("File CSV selezionato: {}", file.getAbsolutePath());
            validateForm();
        }
    }

    /**
     * Gestisce il clic del pulsante "Anteprima dataset".
     * <p>
     * Carica il dataset in base alla sorgente selezionata e
     * mostra il dialog di preview.
     */
    private void handlePreviewDataset() {
        logger.info("Anteprima dataset richiesta");

        String dataSourceStr = dataSourceComboBox.getValue();
        if (dataSourceStr == null) {
            showError("Sorgente Dati Non Selezionata",
                    "Seleziona una sorgente dati prima di visualizzare l'anteprima.");
            return;
        }

        Data data = null;
        DbAccess db = null;

        try {
            if (dataSourceStr.contains("Hardcoded")) {
                // Dataset hardcoded PlayTennis.
                data = new Data();
                logger.info("Caricato dataset hardcoded per preview");

            } else if (dataSourceStr.contains("Dataset Standard")) {
                // Dataset standard Iris.
                data = DatasetLoader.loadIrisDataset();
                logger.info("Caricato dataset Iris per preview");

            } else if (dataSourceStr.contains("CSV")) {
                // CSV non ancora supportato.
                showError("CSV Non Supportato", "La preview da file CSV sarà implementata in futuro.");
                return;

            } else if (dataSourceStr.contains("Database")) {
                // Valida parametri database.
                String dbHost = dbHostField.getText() != null && !dbHostField.getText().trim().isEmpty()
                        ? dbHostField.getText().trim()
                        : DEFAULT_DB_HOST;
                int dbPort = parseDbPort(dbPortField.getText());
                String dbName = dbNameField.getText() != null && !dbNameField.getText().trim().isEmpty()
                        ? dbNameField.getText().trim()
                        : DEFAULT_DB_NAME;
                String dbUser = dbUserField.getText() != null && !dbUserField.getText().trim().isEmpty()
                        ? dbUserField.getText().trim()
                        : DEFAULT_DB_USER;
                String dbPassword = dbPasswordField.getText() != null && !dbPasswordField.getText().isEmpty()
                        ? dbPasswordField.getText()
                        : DEFAULT_DB_PASSWORD;
                String tableName = tableNameField.getText();

                if (tableName == null || tableName.trim().isEmpty()) {
                    showError("Nome Tabella Mancante", "Inserisci il nome della tabella database.");
                    return;
                }

                // Connetti al database.
                logger.info("Connessione database per preview: {}:{}/{} con utente: {}", dbHost, dbPort, dbName,
                        dbUser);

                db = new DbAccess(dbHost, String.valueOf(dbPort), dbName, dbUser, dbPassword);
                data = new Data(db, tableName.trim());

                logger.info("Caricato dataset da database per preview: {}", tableName);
            }

            if (data != null) {
                // Mostra dialog preview.
                DatasetPreviewDialog previewDialog = new DatasetPreviewDialog(data);
                previewDialog.show();
            }

        } catch (Exception e) {
            logger.error("Errore durante caricamento dataset per preview", e);
            showError("Errore Caricamento Dataset",
                    "Impossibile caricare il dataset per l'anteprima:\n" + e.getMessage());
        } finally {
            // Chiudi connessione database se aperta.
            if (db != null) {
                try {
                    db.closeConnection();
                    logger.info("Connessione database chiusa dopo preview");
                } catch (Exception e) {
                    logger.warn("Errore durante chiusura connessione database", e);
                }
            }
        }
    }

    /**
     * Gestisce il clic del pulsante annulla.
     * <p>
     * Ripristina il form ai valori predefiniti.
     */
    private void handleCancel() {
        logger.info("Annulla cliccato");

        // Pulisce il modulo.
        dataSourceComboBox.getSelectionModel().selectFirst();
        radiusField.clear();
        csvFilePathField.clear();
        selectedCsvFile = null;

        // Reset campi database ai valori predefiniti.
        dbHostField.setText(DEFAULT_DB_HOST);
        dbPortField.setText(String.valueOf(DEFAULT_DB_PORT));
        dbNameField.setText(DEFAULT_DB_NAME);
        dbUserField.setText(DEFAULT_DB_USER);
        dbPasswordField.setText(DEFAULT_DB_PASSWORD);
        tableNameField.clear();

        enableCachingCheckBox.setSelected(true);
        verboseLoggingCheckBox.setSelected(false);

        validateForm();
    }

    /**
     * Gestisce il clic del pulsante "Avvia clustering".
     * <p>
     * Valida il form, costruisce la configurazione e salva
     * lo stato in {@link ApplicationContext}.
     */
    private void handleStartClustering() {
        if (!validateForm()) {
            logger.warn("Validazione modulo fallita");
            return;
        }

        // Crea la configurazione clustering.
        ClusteringConfiguration config = buildConfiguration();

        if (config == null || !config.isValid()) {
            logger.error("Configurazione clustering non valida");
            showError("Configurazione Non Valida",
                    "Impossibile creare la configurazione clustering. Verifica i parametri inseriti.");
            return;
        }

        logger.info("Avvio clustering con parametri:");
        logger.info("  Sorgente dati: {}", config.getDataSource());
        logger.info("  Radius: {}", config.getRadius());
        logger.info("  Caching abilitato: {}", config.isEnableCaching());
        logger.info("  Logging verboso: {}", config.isVerboseLogging());

        // Salva la configurazione nel contesto applicazione.
        ApplicationContext.getInstance().setCurrentConfiguration(config);

        // Naviga alla vista clustering.
        navigateToClusteringView();
    }

    /**
     * Costruisce la configurazione clustering dai valori del form.
     * <p>
     * In base alla sorgente dati, imposta i parametri specifici
     * per CSV o Database.
     *
     * @return configurazione clustering o null se errore
     */
    private ClusteringConfiguration buildConfiguration() {
        try {
            // Determina data source.
            DataSource dataSource;
            String dataSourceStr = dataSourceComboBox.getValue();

            if (dataSourceStr.contains("Hardcoded")) {
                dataSource = DataSource.HARDCODED;
            } else if (dataSourceStr.contains("Dataset Standard")) {
                dataSource = DataSource.IRIS;
            } else if (dataSourceStr.contains("CSV")) {
                dataSource = DataSource.CSV;
            } else if (dataSourceStr.contains("Database")) {
                dataSource = DataSource.DATABASE;
            } else {
                logger.error("Sorgente dati non riconosciuta: {}", dataSourceStr);
                return null;
            }

            // Ottieni radius.
            double radius = Double.parseDouble(radiusField.getText().trim());

            // Crea configurazione.
            ClusteringConfiguration config = new ClusteringConfiguration(dataSource, radius);

            // Imposta parametri CSV.
            if (dataSource == DataSource.CSV && selectedCsvFile != null) {
                config.setCsvFilePath(selectedCsvFile.getAbsolutePath());
            }

            // Imposta parametri Database.
            if (dataSource == DataSource.DATABASE) {
                config.setDbHost(dbHostField.getText() != null && !dbHostField.getText().trim().isEmpty()
                        ? dbHostField.getText().trim()
                        : DEFAULT_DB_HOST);

                config.setDbPort(parseDbPort(dbPortField.getText()));

                config.setDbName(dbNameField.getText() != null && !dbNameField.getText().trim().isEmpty()
                        ? dbNameField.getText().trim()
                        : DEFAULT_DB_NAME);

                config.setDbUser(dbUserField.getText() != null && !dbUserField.getText().trim().isEmpty()
                        ? dbUserField.getText().trim()
                        : DEFAULT_DB_USER);

                config.setDbPassword(dbPasswordField.getText() != null && !dbPasswordField.getText().isEmpty()
                        ? dbPasswordField.getText()
                        : DEFAULT_DB_PASSWORD);

                config.setDbTableName(tableNameField.getText().trim());
            }

            // Imposta opzioni.
            config.setEnableCaching(enableCachingCheckBox.isSelected());
            config.setVerboseLogging(verboseLoggingCheckBox.isSelected());

            return config;

        } catch (Exception e) {
            logger.error("Errore durante creazione configurazione", e);
            return null;
        }
    }

    /**
     * Naviga alla vista clustering.
     * <p>
     * Carica la vista FXML e sostituisce il contenuto della
     * content area della finestra principale.
     */
    private void navigateToClusteringView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/clustering.fxml"));
            Parent clusteringView = loader.load();

            // Ottieni la scena corrente e sostituisci il contenuto.
            Scene currentScene = btnStartClustering.getScene();
            Parent root = currentScene.getRoot();
            Node contentArea = root.lookup("#contentArea");

            // Sostituisci il contenuto con la vista clustering.
            if (contentArea instanceof StackPane stackPane) {
                stackPane.getChildren().setAll(clusteringView);
            } else {
                logger.warn("contentArea non trovato, fallback a setRoot");
                currentScene.setRoot(clusteringView);
            }

            logger.info("Navigazione a vista clustering completata");

        } catch (IOException e) {
            logger.error("Errore durante navigazione a vista clustering", e);
            showError("Errore Navigazione", "Impossibile caricare la vista clustering:\n" + e.getMessage());
        }
    }

    /**
     * Parsa la porta del database dal testo fornito.
     *
     * @param portText testo contenente la porta
     * @return porta parsata o porta di default se parsing fallisce
     */
    private int parseDbPort(String portText) {
        try {
            return Integer.parseInt(portText.trim());
        } catch (NumberFormatException e) {
            return DEFAULT_DB_PORT;
        }
    }

    /**
     * Mostra un dialogo di errore con titolo e messaggio specificati.
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

//===---------------------------------------------------------------------------===//
