package gui.controllers;

//===---------------------------------------------------------------------------===//
// Importazioni Java standard.
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import database.DbAccess;
import gui.utils.ThemeManager;
// Importazioni JavaFX.
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
//===---------------------------------------------------------------------------===//

/**
 * Controller per la vista Settings.
 * <p>
 * Questa classe gestisce:
 * <ul>
 *   <li>Caricamento e salvataggio delle preferenze su file properties</li>
 *   <li>Applicazione live di tema e dimensione font</li>
 *   <li>Validazione degli input (radius, memoria, porta DB)</li>
 *   <li>Test di connessione al database in background</li>
 *   <li>Feedback utente tramite barra di stato</li>
 * </ul>
 * <p>
 * Il controller viene inizializzato automaticamente da JavaFX quando viene
 * caricato il file FXML associato. I campi {@code @FXML} sono iniettati dal framework.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.0.0
 * @see gui.utils.ThemeManager
 */
public class SettingsController {

    // Logger per la classe SettingsController.
    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);
    private static final String SETTINGS_FILE = "qtgui.properties";

    //===--------------------------- INSTANCE FIELDS ---------------------------===//

    // Proprietà delle impostazioni.
    private Properties settings;
    private PauseTransition statusHideTimer;

    //===---------------------------- FXML CONTROLS ----------------------------===//

    // Aspetto.
    @FXML
    private ComboBox<String> themeComboBox;
    @FXML
    private ComboBox<String> fontSizeComboBox;
    @FXML
    private CheckBox showWelcomeCheckBox;

    // Prestazioni.
    @FXML
    private CheckBox enableCachingCheckBox;
    @FXML
    private Spinner<Integer> threadPoolSpinner;
    @FXML
    private TextField memoryLimitField;
    @FXML
    private CheckBox verboseLoggingCheckBox;

    // Impostazioni predefinite clustering.
    @FXML
    private TextField defaultRadiusField;
    @FXML
    private ComboBox<String> defaultDataSourceComboBox;
    @FXML
    private CheckBox autoStartClusteringCheckBox;

    // Impostazioni esportazione.
    @FXML
    private ComboBox<String> exportFormatComboBox;
    @FXML
    private TextField exportDirectoryField;
    @FXML
    private Button btnBrowseExportDir;
    @FXML
    private CheckBox includeTimestampCheckBox;

    // Database.
    @FXML
    private TextField dbHostField;
    @FXML
    private TextField dbPortField;
    @FXML
    private TextField dbNameField;
    @FXML
    private TextField dbUsernameField;
    @FXML
    private PasswordField dbPasswordField;
    @FXML
    private Button btnTestConnection;

    // Pulsanti.
    @FXML
    private Button btnResetDefaults;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSaveSettings;

    // Stato.
    @FXML
    private HBox statusFooter;
    @FXML
    private Label statusMessageLabel;

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruttore di default.
     * <p>
     * Invocato da JavaFX durante il caricamento FXML. L'inizializzazione
     * dei componenti avviene in {@link #initialize()}.
     */
    public SettingsController() {
        // Costruttore vuoto - l'inizializzazione avviene nel metodo initialize()
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Inizializza il controller dopo l'iniezione FXML.
     * <p>
     * Configura spinner, listener e carica le impostazioni persistite.
     */
    @FXML
    public void initialize() {
        logger.info("Inizializzazione SettingsController...");

        settings = new Properties();

        setupSpinners();
        setupButtons();
        loadSettings();
        setupLiveListeners(); // Aggiungi listener per applicazione live delle modifiche.

        logger.info("SettingsController inizializzato con successo");
    }

    //===--------------------------- PRIVATE METHODS ---------------------------===//

    /**
     * Configura i controlli spinner.
     */
    private void setupSpinners() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 16, 4);
        threadPoolSpinner.setValueFactory(valueFactory);
    }

    /**
     * Configura i listener per l'applicazione in tempo reale delle modifiche.
     * <p>
     * Applica immediatamente tema e dimensione font quando l'utente cambia selezione.
     */
    private void setupLiveListeners() {
        // Listener per il cambio tema.
        if (themeComboBox != null) {
            themeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    ThemeManager.getInstance().setThemeByName(newValue);
                    logger.info("Tema cambiato a: {}", newValue);
                }
            });
        }

        // Listener per il cambio dimensione font.
        if (fontSizeComboBox != null) {
            fontSizeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    ThemeManager.getInstance().setFontSizeByName(newValue);
                    logger.info("Dimensione font cambiata a: {}", newValue);
                }
            });
        }
    }

    /**
     * Configura i gestori eventi dei pulsanti.
     */
    private void setupButtons() {
        btnBrowseExportDir.setOnAction(e -> handleBrowseExportDirectory());
        btnTestConnection.setOnAction(e -> handleTestConnection());
        btnResetDefaults.setOnAction(e -> handleResetDefaults());
        btnCancel.setOnAction(e -> handleCancel());
        btnSaveSettings.setOnAction(e -> handleSaveSettings());
    }

    /**
     * Carica le impostazioni dal file properties.
     * <p>
     * Se il file non esiste o fallisce il caricamento, applica i valori predefiniti.
     */
    private void loadSettings() {
        try {
            File settingsFile = new File(SETTINGS_FILE);
            if (settingsFile.exists()) {
                try (FileInputStream fis = new FileInputStream(settingsFile)) {
                    settings.load(fis);
                    applySettings();
                    logger.info("Impostazioni caricate da {}", SETTINGS_FILE);
                }
            } else {
                // Applica valori predefiniti.
                applyDefaults();
                logger.info("Nessun file di impostazioni trovato, uso valori predefiniti");
            }
        } catch (IOException e) {
            logger.error("Impossibile caricare le impostazioni", e);
            applyDefaults();
        }
    }

    /**
     * Applica le impostazioni ai controlli dell'interfaccia.
     */
    private void applySettings() {
        // Aspetto.
        themeComboBox.setValue(settings.getProperty("theme", "Light"));
        fontSizeComboBox.setValue(settings.getProperty("fontSize", "Medium (14px)"));
        showWelcomeCheckBox.setSelected(Boolean.parseBoolean(settings.getProperty("showWelcome", "true")));

        // Prestazioni.
        enableCachingCheckBox.setSelected(Boolean.parseBoolean(settings.getProperty("enableCaching", "true")));
        try {
            threadPoolSpinner.getValueFactory().setValue(Integer.parseInt(settings.getProperty("threadPoolSize", "4")));
        } catch (NumberFormatException e) {
            logger.warn("Valore threadPoolSize non valido: '{}', utilizzo valore predefinito (4)",
                    settings.getProperty("threadPoolSize"));
            threadPoolSpinner.getValueFactory().setValue(4);
        }
        memoryLimitField.setText(settings.getProperty("memoryLimit", "512"));
        verboseLoggingCheckBox.setSelected(Boolean.parseBoolean(settings.getProperty("verboseLogging", "false")));

        // Impostazioni predefinite clustering.
        defaultRadiusField.setText(settings.getProperty("defaultRadius", "0.5"));
        defaultDataSourceComboBox.setValue(settings.getProperty("defaultDataSource", "Hardcoded (PlayTennis)"));
        autoStartClusteringCheckBox.setSelected(Boolean.parseBoolean(settings.getProperty("autoStart", "false")));

        // Esportazione.
        exportFormatComboBox.setValue(settings.getProperty("exportFormat", "CSV"));
        exportDirectoryField.setText(settings.getProperty("exportDirectory", System.getProperty("user.home")));
        includeTimestampCheckBox.setSelected(Boolean.parseBoolean(settings.getProperty("includeTimestamp", "true")));

        // Database.
        dbHostField.setText(settings.getProperty("dbHost", "localhost"));
        dbPortField.setText(settings.getProperty("dbPort", "3306"));
        dbNameField.setText(settings.getProperty("dbName", "MapDB"));
        dbUsernameField.setText(settings.getProperty("dbUsername", "MapUser"));
        // Password non caricata dal file per sicurezza.
    }

    /**
     * Applica i valori predefiniti ai controlli dell'interfaccia.
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

    //===--------------------------- EVENT HANDLERS ----------------------------===//

    /**
     * Gestisce il pulsante sfoglia directory esportazione.
     */
    private void handleBrowseExportDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleziona Directory Esportazione");

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
            logger.info("Directory esportazione impostata a: {}", selectedDirectory.getAbsolutePath());
        }
    }

    /**
     * Gestisce il pulsante test connessione database.
     * <p>
     * Esegue il test su thread separato e aggiorna la UI al termine.
     */
    private void handleTestConnection() {
        logger.info("Test connessione database richiesto");

        // Valida i campi.
        String host = dbHostField.getText();
        String portStr = dbPortField.getText();
        String dbName = dbNameField.getText();
        String username = dbUsernameField.getText();
        String password = dbPasswordField.getText();

        if (host == null || host.trim().isEmpty()) {
            showError("Campo Obbligatorio", "Inserisci l'host del database.");
            return;
        }

        if (dbName == null || dbName.trim().isEmpty()) {
            showError("Campo Obbligatorio", "Inserisci il nome del database.");
            return;
        }

        if (username == null || username.trim().isEmpty()) {
            showError("Campo Obbligatorio", "Inserisci lo username del database.");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
            if (port < 1 || port > 65535) {
                showError("Porta Non Valida", "La porta deve essere tra 1 e 65535.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Porta Non Valida", "Inserisci un numero di porta valido.");
            return;
        }

        // Disabilita il pulsante durante il test.
        btnTestConnection.setDisable(true);
        btnTestConnection.setText("Test in corso...");

        // Esegui test in background thread.
        Task<Boolean> testTask = new Task<Boolean>() {
            private String errorMessage = "";

            @Override
            protected Boolean call() {
                DbAccess db = null;
                try {
                    logger.info("Tentativo connessione a: {}:{}/{} con utente: {}", host.trim(), port, dbName.trim(),
                            username.trim());

                    db = new DbAccess(host.trim(), String.valueOf(port), dbName.trim(), username.trim(), password);

                    // Se arriviamo qui, la connessione è riuscita.
                    logger.info("Connessione database riuscita!");
                    return true;

                } catch (database.DatabaseConnectionException e) {
                    errorMessage = e.getMessage();
                    logger.error("Errore connessione database", e);
                    return false;
                } finally {
                    if (db != null) {
                        try {
                            db.closeConnection();
                        } catch (Exception e) {
                            logger.warn("Errore chiusura connessione test", e);
                        }
                    }
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    btnTestConnection.setDisable(false);
                    btnTestConnection.setText("Test Connessione");

                    if (getValue()) {
                        // Connessione riuscita.
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Test Connessione");
                        alert.setHeaderText("Connessione Riuscita");
                        alert.setContentText("La connessione al database è stata stabilita con successo!\n\n"
                                + "Configurazione:\n" + "Host: " + host + "\n" + "Porta: " + port + "\n" + "Database: "
                                + dbName + "\n" + "Username: " + username);
                        alert.showAndWait();
                        showStatus("Connessione database riuscita", true);
                    } else {
                        // Connessione fallita.
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Test Connessione");
                        alert.setHeaderText("Connessione Fallita");
                        alert.setContentText("Impossibile connettersi al database.\n\n" + "Errore:\n" + errorMessage
                                + "\n\n" + "Verifica i parametri di connessione e assicurati che:\n"
                                + "- Il server MySQL sia in esecuzione\n" + "- Le credenziali siano corrette\n"
                                + "- Il database esista\n" + "- Non ci siano firewall che bloccano la connessione");
                        alert.showAndWait();
                        showStatus("Connessione database fallita", false);
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    btnTestConnection.setDisable(false);
                    btnTestConnection.setText("Test Connessione");
                    showError("Errore Test",
                            "Errore imprevisto durante test connessione:\n" + getException().getMessage());
                });
            }
        };

        // Avvia task in background.
        Thread testThread = new Thread(testTask);
        testThread.setDaemon(true);
        testThread.start();
    }

    /**
     * Gestisce il pulsante ripristina predefiniti.
     */
    private void handleResetDefaults() {
        logger.info("Ripristina predefiniti cliccato");

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Ripristina Impostazioni");
        confirmAlert.setHeaderText("Ripristinare tutte le impostazioni ai valori predefiniti?");
        confirmAlert.setContentText("Questa azione non può essere annullata.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                applyDefaults();
                showStatus("Impostazioni ripristinate ai valori predefiniti", true);
                logger.info("Impostazioni ripristinate ai valori predefiniti");
            }
        });
    }

    /**
     * Gestisce il pulsante annulla.
     * <p>
     * Ricarica le impostazioni senza salvarle.
     */
    private void handleCancel() {
        logger.info("Annulla cliccato - ricaricamento impostazioni");
        loadSettings();
        showStatus("Impostazioni ricaricate", false);
    }

    /**
     * Gestisce il pulsante salva impostazioni.
     * <p>
     * Valida i campi e persiste le preferenze su file.
     */
    private void handleSaveSettings() {
        logger.info("Salva impostazioni cliccato");

        // Valida gli input.
        if (!validateSettings()) {
            return;
        }

        // Raccoglie le impostazioni dall'interfaccia.
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
        // Nota: Password non salvata nel file per sicurezza.

        // Salva su file
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            settings.store(fos, "QT Clustering GUI Settings");
            showStatus("Impostazioni salvate con successo", true);
            logger.info("Impostazioni salvate in {}", SETTINGS_FILE);
        } catch (IOException e) {
            logger.error("Impossibile salvare le impostazioni", e);
            showError("Impossibile salvare le impostazioni",
                    "Impossibile scrivere il file delle impostazioni: " + e.getMessage());
        }
    }

    //===----------------------------- VALIDATION ------------------------------===//

    /**
     * Valida gli input delle impostazioni.
     *
     * @return true se tutti gli input sono validi
     */
    private boolean validateSettings() {
        // Valida il radius predefinito.
        try {
            double radius = Double.parseDouble(defaultRadiusField.getText());
            if (radius < 0) {
                showError("Input Non Valido", "Il radius predefinito deve essere non negativo");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Input Non Valido", "Il radius predefinito deve essere un numero valido");
            return false;
        }

        // Valida il limite di memoria.
        try {
            int memory = Integer.parseInt(memoryLimitField.getText());
            if (memory < 128) {
                showError("Input Non Valido", "Il limite di memoria deve essere almeno 128 MB");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Input Non Valido", "Il limite di memoria deve essere un intero valido");
            return false;
        }

        // Valida la porta del database.
        try {
            int port = Integer.parseInt(dbPortField.getText());
            if (port < 1 || port > 65535) {
                showError("Input Non Valido", "La porta del database deve essere tra 1 e 65535");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Input Non Valido", "La porta del database deve essere un intero valido");
            return false;
        }

        return true;
    }

    //===--------------------------- PRIVATE HELPERS ---------------------------===//

    /**
     * Mostra un messaggio di stato.
     * <p>
     * Il messaggio viene nascosto automaticamente dopo alcuni secondi.
     *
     * @param message messaggio di stato
     * @param success true se successo, false se avviso
     */
    private void showStatus(String message, boolean success) {
        statusMessageLabel.setText(message);
        statusMessageLabel.getStyleClass().clear();
        statusMessageLabel.getStyleClass().add(success ? "label-success" : "label-warning");
        statusFooter.setVisible(true);
        statusFooter.setManaged(true);

        // Nasconde dopo 3 secondi.
        if (statusHideTimer != null) {
            statusHideTimer.stop();
        }
        statusHideTimer = new PauseTransition(Duration.seconds(3));
        statusHideTimer.setOnFinished(event -> {
            statusFooter.setVisible(false);
            statusFooter.setManaged(false);
        });
        statusHideTimer.play();
    }

    /**
     * Mostra un dialogo di errore.
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
