package gui.controllers;

// import data.Data;
import gui.dialogs.AboutDialog;
import gui.models.ClusteringResult;
import gui.services.ClusteringService;
import gui.utils.ApplicationContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import mining.QTMiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Controller principale per la finestra dell'applicazione. 
 * Gestisce la navigazione tra le diverse viste e i componenti della finestra principale.
 */
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    // Elementi MenuBar
    @FXML
    private MenuItem menuNew;
    @FXML
    private MenuItem menuOpen;
    @FXML
    private MenuItem menuSave;
    @FXML
    private MenuItem menuSaveAs;
    @FXML
    private MenuItem menuExit;
    @FXML
    private MenuItem menuSettings;
    @FXML
    private CheckMenuItem menuShowToolbar;
    @FXML
    private CheckMenuItem menuShowStatusBar;
    @FXML
    private MenuItem menuHelp;
    @FXML
    private MenuItem menuAbout;

    // ToolBar
    @FXML
    private ToolBar toolbar;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnOpen;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnRun;
    @FXML
    private Button btnExport;

    // Area contenuti e barra di stato
    @FXML
    private StackPane contentArea;
    @FXML
    private HBox statusBar;
    @FXML
    private Label statusLabel;
    @FXML
    private Label progressLabel;

    /**
     * Inizializza il controller. Chiamato automaticamente dopo il caricamento FXML.
     */
    @FXML
    public void initialize() {
        logger.info("Inizializzazione MainController...");

        setupMenuHandlers();
        setupToolbarHandlers();
        setupStatusBar();

        logger.info("MainController inizializzato con successo");
    }

    /**
     * Configura i gestori eventi per le voci di menu.
     */
    private void setupMenuHandlers() {
        // Menu File
        menuNew.setOnAction(e -> handleNewAnalysis());
        menuOpen.setOnAction(e -> handleOpen());
        menuSave.setOnAction(e -> handleSave());
        menuSaveAs.setOnAction(e -> handleSaveAs());
        menuExit.setOnAction(e -> handleExit());

        // Menu Edit
        menuSettings.setOnAction(e -> handleSettings());

        // Menu View
        menuShowToolbar.setOnAction(e -> toolbar.setVisible(menuShowToolbar.isSelected()));
        menuShowStatusBar.setOnAction(e -> statusBar.setVisible(menuShowStatusBar.isSelected()));

        // Menu Help
        menuHelp.setOnAction(e -> handleHelp());
        menuAbout.setOnAction(e -> handleAbout());
    }

    /**
     * Configura i gestori eventi per i pulsanti della toolbar.
     */
    private void setupToolbarHandlers() {
        btnNew.setOnAction(e -> handleNewAnalysis());
        btnOpen.setOnAction(e -> handleOpen());
        btnSave.setOnAction(e -> handleSave());
        btnRun.setOnAction(e -> handleRunClustering());
        btnExport.setOnAction(e -> handleExport());
    }

    /**
     * Configura lo stato iniziale della barra di stato.
     */
    private void setupStatusBar() {
        updateStatus("Pronto");
    }

    /**
     * Aggiorna il messaggio della barra di stato.
     *
     * @param message messaggio di stato da visualizzare
     */
    public void updateStatus(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }

    /**
     * Aggiorna l'etichetta di progresso nella barra di stato.
     *
     * @param progress messaggio di progresso
     */
    public void updateProgress(String progress) {
        Platform.runLater(() -> progressLabel.setText(progress));
    }

    /**
     * Naviga verso una vista specifica caricando il suo file FXML.
     *
     * @param fxmlFile nome del file FXML (es. "home.fxml")
     */
    public void navigateTo(String fxmlFile) {
        try {
            logger.info("Navigazione verso la vista: {}", fxmlFile);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + fxmlFile));
            Parent view = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            updateStatus("Vista caricata: " + fxmlFile);
        } catch (IOException e) {
            logger.error("Impossibile caricare la vista: {}", fxmlFile, e);
            showError("Impossibile caricare la vista", "Impossibile caricare " + fxmlFile);
        }
    }

    // Gestori eventi

    private void handleNewAnalysis() {
        logger.info("Nuova Analisi cliccato");
        navigateTo("home.fxml");
        updateStatus("Pronto per avviare nuova analisi di clustering");
    }

    private void handleOpen() {
        logger.info("Apri cliccato");

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Apri Clustering");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File Clustering", "*.dmp"));

            File file = fileChooser.showOpenDialog(statusLabel.getScene().getWindow());

            if (file != null) {
                updateStatus("Caricamento file: " + file.getName() + "...");

                ClusteringService clusteringService = ApplicationContext.getInstance().getClusteringService();

                // Rimuovi estensione .dmp dal percorso (QTMiner la aggiunge automaticamente)
                String filePath = file.getAbsolutePath();
                if (filePath.endsWith(".dmp")) {
                    filePath = filePath.substring(0, filePath.length() - 4);
                }

                // Carica il QTMiner dal file
                QTMiner miner = clusteringService.loadClusteringResults(filePath);

                // Verifica se il file contiene dati completi
                if (miner.getData() != null) {
                    // File formato completo: crea ClusteringResult e mostra risultati
                    // Tempo esecuzione non disponibile da file
                    ClusteringResult result =
                            new ClusteringResult(miner.getC(), miner.getData(), miner.getRadius(), 0, miner);

                    // Salva in ApplicationContext per renderlo disponibile alle altre viste
                    ApplicationContext.getInstance().setCurrentResult(result);

                    // Naviga alla vista risultati
                    navigateTo("results.fxml");
                    updateStatus("Clustering caricato: " + file.getName());

                    logger.info("File clustering completo caricato: {} ({} cluster, radius={})", file.getAbsolutePath(),
                            result.getNumClusters(), result.getRadius());

                } else {
                    // File formato legacy (solo ClusterSet): mostra avviso
                    Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                    warningAlert.setTitle("Formato File Incompleto");
                    warningAlert.setHeaderText("File in formato legacy");
                    warningAlert.setContentText(
                            "Il file " + file.getName() + " contiene solo i cluster (formato vecchio).\n\n"
                                    + "Per visualizzare i risultati completi,\n"
                                    + "salva nuovamente il clustering dopo averlo eseguito.");
                    warningAlert.showAndWait();

                    logger.warn("Clustering caricato ma dataset non disponibile (file legacy)");
                    updateStatus("File caricato (formato incompleto)");
                }
            }

        } catch (Exception e) {
            logger.error("Errore durante apertura file clustering", e);
            updateStatus("Errore durante caricamento file");
            showError("Errore Caricamento", "Impossibile caricare il file clustering: " + e.getMessage());
        }
    }

    private void handleSave() {
        logger.info("Salva cliccato");

        ClusteringResult result = ApplicationContext.getInstance().getCurrentResult();
        if (result == null) {
            showWarning("Nessun Risultato", "Non ci sono risultati di clustering da salvare.\n"
                    + "Esegui prima un clustering dalla schermata Home.");
            return;
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salva Clustering");
            fileChooser.setInitialFileName("clustering.dmp");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File Clustering", "*.dmp"));

            File file = fileChooser.showSaveDialog(statusLabel.getScene().getWindow());

            if (file != null) {
                updateStatus("Salvataggio in corso...");

                // Rimuovi estensione .dmp dal percorso (QTMiner la aggiunge automaticamente)
                String filePath = file.getAbsolutePath();
                if (filePath.endsWith(".dmp")) {
                    filePath = filePath.substring(0, filePath.length() - 4);
                }

                ClusteringService clusteringService = ApplicationContext.getInstance().getClusteringService();
                clusteringService.saveClusteringResults(filePath, result.getMiner());

                updateStatus("Clustering salvato: " + file.getName());
                showInfo("Salvataggio Completato", "Clustering salvato con successo in:\n" + file.getAbsolutePath());

                logger.info("Clustering salvato: {}", file.getAbsolutePath());
            }

        } catch (Exception e) {
            logger.error("Errore durante salvataggio", e);
            updateStatus("Errore durante salvataggio");
            showError("Errore", "Impossibile salvare il clustering: " + e.getMessage());
        }
    }

    private void handleSaveAs() {
        logger.info("Salva con nome cliccato");
        // Stesso comportamento di handleSave (File Chooser chiede sempre destinazione)
        handleSave();
    }

    private void handleExit() {
        logger.info("Esci cliccato");
        Platform.exit();
    }

    private void handleSettings() {
        logger.info("Impostazioni cliccato");
        navigateTo("settings.fxml");
    }

    private void handleRunClustering() {
        logger.info("Esegui Clustering cliccato");

        // Verifica se esiste una configurazione valida
        if (ApplicationContext.getInstance().getCurrentConfiguration() == null) {
            logger.warn("Nessuna configurazione trovata, reindirizzo a home per configurazione");
            updateStatus("Configura prima i parametri di clustering");
            navigateTo("home.fxml");
        } else {
            navigateTo("clustering.fxml");
        }
    }

    private void handleExport() {
        logger.info("Esporta cliccato");

        ClusteringResult result = ApplicationContext.getInstance().getCurrentResult();
        if (result == null) {
            showWarning("Nessun Risultato",
                    "Non ci sono risultati da esportare.\n" + "Esegui prima un clustering dalla schermata Home.");
            return;
        }

        // Naviga a schermata Results dove l'utente può scegliere il formato export
        navigateTo("results.fxml");
        updateStatus("Usa il pulsante Esporta nella schermata Risultati");

        // Mostra hint
        showInfo("Esportazione", "Vai alla schermata Risultati e usa il pulsante 'Esporta'\n"
                + "per scegliere il formato di esportazione (CSV, TXT, ZIP).");
    }

    private void handleHelp() {
        logger.info("Aiuto cliccato");
        showInfo("Aiuto", "Documentazione GUI QT Clustering\n\nPer maggiori informazioni, consulta la documentazione.");
    }

    private void handleAbout() {
        logger.info("Informazioni cliccato");

        try {
            AboutDialog aboutDialog = new AboutDialog();
            aboutDialog.show();
            logger.info("AboutDialog aperto");
        } catch (Exception e) {
            logger.error("Errore durante apertura About dialog", e);
            // Fallback al dialog semplice
            showInfo("Informazioni su QT Clustering",
                    "QT Clustering GUI v1.0.0\n\n" + "Algoritmo Quality Threshold Clustering\n"
                            + "Sviluppato per il corso MAP\n\n" + "Applicazione GUI JavaFX");
        }
    }

    // Metodi di utilità per i dialoghi

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

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
