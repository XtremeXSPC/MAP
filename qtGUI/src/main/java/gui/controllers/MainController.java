package gui.controllers;

//===---------------------------------------------------------------------------===//
// Importazioni Java standard.
import com.map.stdgui.StdAsync;
import com.map.stdgui.StdView;
import com.map.stdgui.StdWindow;
import java.nio.file.Path;
import java.io.File;
import com.map.stdgui.StdDialog;
import com.map.stdgui.StdFileDialog;
import com.map.stdgui.StdGui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gui.dialogs.AboutDialog;
import gui.models.ClusteringResult;
import gui.services.ClusteringService;
import gui.utils.ApplicationContext;
// Importazioni JavaFX.
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
// Importazioni del backend di mining.
import mining.QTMiner;
//===---------------------------------------------------------------------------===//

/**
 * Controller principale per la finestra dell'applicazione GUI QT Clustering.
 * <p>
 * Questa classe gestisce:
 * <ul>
 *   <li>Navigazione tra le diverse viste dell'applicazione (Home, Clustering, Results, Settings)</li>
 *   <li>Menu principale e barra degli strumenti</li>
 *   <li>Operazioni su file (apertura e salvataggio clustering)</li>
 *   <li>Barra di stato e messaggi di progresso</li>
 *   <li>Dialoghi informativi e di errore</li>
 * </ul>
 * <p>
 * Il controller viene inizializzato automaticamente da JavaFX quando viene caricato il file FXML
 * corrispondente (<code>main-view.fxml</code>). Tutti i componenti annotati con {@code @FXML} vengono
 * iniettati automaticamente dal framework.
 *
 * @author MAP Project Team
 * @version 1.0.0
 * @see gui.utils.ApplicationContext
 * @see gui.services.ClusteringService
 * @see gui.models.ClusteringResult
 */
public class MainController {

    // Logger per la classe MainController.
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    //===---------------------------- FXML CONTROLS ----------------------------===//

    // Elementi MenuBar.
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

    // ToolBar.
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

    // Area contenuti e barra di stato.
    @FXML
    private StackPane contentArea;
    @FXML
    private HBox statusBar;
    @FXML
    private Label statusLabel;
    @FXML
    private Label progressLabel;

    //===------------------------------ CONSTANTS ------------------------------===//

    // Costanti per messaggi.
    private static final String ERROR_SAVING = "Errore durante salvataggio";
    private static final String ERROR_LOADING = "Errore durante caricamento file";
    private static final String NO_RESULTS_TITLE = "Nessun Risultato";
    private static final String NO_RESULTS_MSG =
            "Non ci sono risultati di clustering da salvare.\n" + "Esegui prima un clustering dalla schermata Home.";
    private static final String NO_RESULTS_EXPORT_MSG =
            "Non ci sono risultati da esportare.\n" + "Esegui prima un clustering dalla schermata Home.";
    private static final String FILE_CLUSTERING = "File Clustering";
    private static final String CLUSTERING_EXT = "*.dmp";
    private static final String DMP_EXT = ".dmp";
    private static final String ERROR_TITLE = "Errore";
    private static final String SAVE_ERROR_MSG = "Impossibile salvare il clustering: ";
    private static final String LOAD_ERROR_MSG = "Impossibile caricare il file clustering: ";

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruttore di default.
     * <p>
     * Il costruttore viene invocato automaticamente da JavaFX durante il caricamento del file FXML.
     * L'inizializzazione effettiva dei componenti viene eseguita nel metodo {@link #initialize()},
     * che viene chiamato dopo l'iniezione delle dipendenze FXML.
     * <p>
     * <strong>Nota:</strong> Non inizializzare componenti FXML in questo costruttore, poiché
     * non sono ancora stati iniettati da JavaFX.
     *
     * @see #initialize()
     */
    public MainController() {
        // Costruttore vuoto.
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Inizializza il controller dopo l'iniezione delle dipendenze FXML.
     * <p>
     * Questo metodo viene chiamato automaticamente da JavaFX dopo che tutti i campi annotati
     * con {@code @FXML} sono stati iniettati. Esegue la configurazione iniziale di:
     * <ul>
     *   <li>Gestori eventi per menu e toolbar</li>
     *   <li>Stato iniziale della barra di stato</li>
     *   <li>Visibilità dei componenti UI</li>
     * </ul>
     * <p>
     * <strong>Importante:</strong> Non chiamare questo metodo manualmente.
     *
     * @see #setupMenuHandlers()
     * @see #setupToolbarHandlers()
     * @see #setupStatusBar()
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
     * Aggiorna il messaggio della barra di stato.
     * <p>
     * Questo metodo è thread-safe e può essere chiamato da qualsiasi thread.
     * L'aggiornamento viene eseguito nel JavaFX Application Thread tramite
     * {@link Platform#runLater(Runnable)}.
     * <p>
     *
     * @param message il messaggio di stato da visualizzare nella barra di stato.
     *                Se {@code null}, viene visualizzata una stringa vuota.
     * @see #updateProgress(String)
     * @see Platform#runLater(Runnable)
     */
    public void updateStatus(String message) {
        StdGui.later(() -> statusLabel.setText(message == null ? "" : message));
    }

    /**
     * Aggiorna l'etichetta di progresso nella barra di stato.
     * <p>
     * Questo metodo è thread-safe e può essere chiamato da qualsiasi thread.
     * Tipicamente utilizzato durante operazioni di lunga durata per fornire
     * feedback visivo all'utente.
     * <p>
     *
     * @param progress il messaggio di progresso da visualizzare.
     *                 Se {@code null}, viene visualizzata una stringa vuota.
     * @see #updateStatus(String)
     * @see Platform#runLater(Runnable)
     */
    public void updateProgress(String progress) {
        StdGui.later(() -> progressLabel.setText(progress == null ? "" : progress));
    }

    /**
     * Naviga verso una vista specifica caricando il relativo file FXML tramite
     * {@link StdView}.
     * <p>
     * Questo metodo:
     * <ol>
     *   <li>Carica il file FXML dalla cartella <code>/views/</code></li>
     *   <li>Rimuove il contenuto corrente dall'area di visualizzazione</li>
     *   <li>Inserisce la nuova vista nell'area contenuti</li>
     *   <li>Aggiorna il messaggio di stato</li>
     * </ol>
     * <p>
     * <strong>Viste disponibili:</strong>
     * <ul>
     *   <li><code>home.fxml</code> - Schermata principale con configurazione parametri</li>
     *   <li><code>clustering.fxml</code> - Esecuzione del clustering</li>
     *   <li><code>results.fxml</code> - Visualizzazione risultati</li>
     *   <li><code>settings.fxml</code> - Impostazioni applicazione</li>
     * </ul>
     * <p>
     * Se il caricamento fallisce, viene mostrato un dialogo di errore all'utente.
     *
     * @param fxmlFile il nome del file FXML da caricare (es. "home.fxml").
     *                 Deve trovarsi nella cartella <code>/views/</code> del classpath.
     * @throws NullPointerException se {@code fxmlFile} è {@code null}
     * @see StdView
     * @see #showError(String, String)
     */
    public void navigateTo(String fxmlFile) {
        try {
            logger.info("Navigazione verso la vista: {}", fxmlFile);
            StdWindow.current().replaceRegion("contentArea", StdView.load("/views/" + fxmlFile));
            updateStatus("Vista caricata: " + fxmlFile);
        } catch (RuntimeException e) {
            logger.error("Impossibile caricare la vista: {}", fxmlFile, e);
            showError("Impossibile caricare la vista", "Impossibile caricare " + fxmlFile);
        }
    }

    //===--------------------------- PRIVATE METHODS ---------------------------===//

    /**
     * Configura i gestori eventi per tutte le voci di menu dell'applicazione.
     * <p>
     * Associa i seguenti handler ai rispettivi elementi di menu:
     * <ul>
     *   <li><strong>Menu File:</strong> Nuovo, Apri, Salva, Salva con nome, Esci</li>
     *   <li><strong>Menu Modifica:</strong> Impostazioni</li>
     *   <li><strong>Menu Visualizza:</strong> Mostra/Nascondi Toolbar e StatusBar</li>
     *   <li><strong>Menu Aiuto:</strong> Documentazione e Informazioni</li>
     * </ul>
     *
     * @see #handleNewAnalysis()
     * @see #handleOpen()
     * @see #handleSave()
     */
    private void setupMenuHandlers() {
        // Menu File.
        menuNew.setOnAction(e -> handleNewAnalysis());
        menuOpen.setOnAction(e -> handleOpen());
        menuSave.setOnAction(e -> handleSave());
        menuSaveAs.setOnAction(e -> handleSaveAs());
        menuExit.setOnAction(e -> handleExit());

        // Menu Edit.
        menuSettings.setOnAction(e -> handleSettings());

        // Menu View.
        menuShowToolbar.setOnAction(e -> toolbar.setVisible(menuShowToolbar.isSelected()));
        menuShowStatusBar.setOnAction(e -> statusBar.setVisible(menuShowStatusBar.isSelected()));

        // Menu Help.
        menuHelp.setOnAction(e -> handleHelp());
        menuAbout.setOnAction(e -> handleAbout());
    }

    /**
     * Configura i gestori eventi per i pulsanti della barra degli strumenti.
     * <p>
     * Collega i seguenti pulsanti ai rispettivi handler:
     * <ul>
     *   <li><strong>Nuovo:</strong> Avvia nuova analisi di clustering</li>
     *   <li><strong>Apri:</strong> Carica risultati da file .dmp</li>
     *   <li><strong>Salva:</strong> Salva clustering corrente</li>
     *   <li><strong>Esegui:</strong> Avvia processo di clustering</li>
     *   <li><strong>Esporta:</strong> Esporta risultati in vari formati</li>
     * </ul>
     *
     * @see #setupMenuHandlers()
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
     * <p>
     * Imposta il messaggio di stato predefinito a "Pronto", indicando che
     * l'applicazione è pronta per ricevere input dall'utente.
     *
     * @see #updateStatus(String)
     */
    private void setupStatusBar() {
        updateStatus("Pronto");
    }

    //===--------------------------- EVENT HANDLERS ----------------------------===//

    /**
     * Gestisce l'evento "Nuova Analisi" dal menu o toolbar.
     * <p>
     * Naviga alla schermata Home per permettere all'utente di configurare
     * una nuova analisi di clustering. Resetta eventuali configurazioni precedenti.
     *
     * @see #navigateTo(String)
     */
    private void handleNewAnalysis() {
        logger.info("Nuova Analisi cliccato");
        navigateTo("home.fxml");
        updateStatus("Pronto per avviare nuova analisi di clustering");
    }

    /**
     * Gestisce l'evento "Apri" per caricare risultati di clustering salvati.
     * <p>
     * Mostra un file chooser per selezionare un file <code>.dmp</code> e carica
     * i risultati del clustering in background utilizzando un {@link Task}.
     * <p>
     * Il metodo gestisce due formati:
     * <ul>
     *   <li><strong>Formato completo:</strong> Include cluster e dataset originale</li>
     *   <li><strong>Formato legacy:</strong> Include solo i cluster (mostra warning)</li>
     * </ul>
     * <p>
     * In caso di successo, i risultati vengono caricati in {@link ApplicationContext}
     * e viene mostrata la vista dei risultati.
     *
     * @see ClusteringService#loadClusteringResults(String)
     * @see ApplicationContext#setCurrentResult(ClusteringResult)
     * @see Task
     */
    private void handleOpen() {
        logger.info("Apri cliccato");

        try {
            Path selectedPath =
                    StdFileDialog.openFile("Apri Clustering", new StdFileDialog.Filter(FILE_CLUSTERING, CLUSTERING_EXT))
                            .orElse(null);
            File file = selectedPath == null ? null : selectedPath.toFile();

            if (file != null) {
                updateStatus("Caricamento file: " + file.getName() + "...");

                ClusteringService clusteringService = ApplicationContext.getInstance().getClusteringService();

                // Rimuovi estensione .dmp dal percorso (QTMiner la aggiunge automaticamente).
                String filePath = file.getAbsolutePath();
                if (filePath.endsWith(DMP_EXT)) {
                    filePath = filePath.substring(0, filePath.length() - 4);
                }
                final String sanitizedFilePath = filePath;

                StdAsync.submit("qtgui-load-clustering", () -> clusteringService.loadClusteringResults(sanitizedFilePath))
                        .onSuccess(miner -> {
                            if (miner.getData() != null) {
                                ClusteringResult result =
                                        new ClusteringResult(miner.getC(), miner.getData(), miner.getRadius(), 0, miner);
                                ApplicationContext.getInstance().setCurrentResult(result);

                                navigateTo("results.fxml");
                                updateStatus("Clustering caricato: " + file.getName());

                                logger.info("File clustering completo caricato: {} ({} cluster, radius={})",
                                        file.getAbsolutePath(), result.getNumClusters(), result.getRadius());

                            } else {
                                StdDialog.warning("Formato File Incompleto", "File in formato legacy",
                                        "Il file " + file.getName() + " contiene solo i cluster (formato vecchio).\n\n"
                                                + "Per visualizzare i risultati completi,\n"
                                                + "salva nuovamente il clustering dopo averlo eseguito.");

                                logger.warn("Clustering caricato ma dataset non disponibile (file legacy)");
                                updateStatus("File caricato (formato incompleto)");
                            }
                        }).onFailure(error -> {
                            logger.error("Errore durante apertura file clustering", error);
                            updateStatus(ERROR_LOADING);
                            showError("Errore Caricamento", LOAD_ERROR_MSG + error.getMessage());
                        });
            }

        } catch (Exception e) {
            logger.error("Errore durante apertura file clustering", e);
            updateStatus(ERROR_LOADING);
            showError("Errore Caricamento", LOAD_ERROR_MSG + e.getMessage());
        }
    }

    /**
     * Gestisce l'evento "Salva" per salvare i risultati di clustering correnti.
     * <p>
     * Verifica che esistano risultati da salvare, quindi mostra un file chooser
     * per selezionare la destinazione. Il salvataggio viene eseguito in background
     * utilizzando un {@link Task} per non bloccare l'interfaccia utente.
     * <p>
     * Il file viene salvato in formato <code>.dmp</code> (formato serializzato Java)
     * e include sia i cluster che il dataset originale per permettere la
     * visualizzazione completa dei risultati in seguito.
     *
     * @see ClusteringService#saveClusteringResults(String, QTMiner)
     * @see ApplicationContext#getCurrentResult()
     * @see Task
     */
    private void handleSave() {
        logger.info("Salva cliccato");

        ClusteringResult result = ApplicationContext.getInstance().getCurrentResult();
        if (result == null) {
            showWarning(NO_RESULTS_TITLE, NO_RESULTS_MSG);
            return;
        }

        try {
            Path selectedPath = StdFileDialog.saveFile("Salva Clustering", "clustering.dmp",
                    new StdFileDialog.Filter(FILE_CLUSTERING, CLUSTERING_EXT)).orElse(null);
            File file = selectedPath == null ? null : selectedPath.toFile();

            if (file != null) {
                updateStatus("Salvataggio in corso...");

                // Rimuovi estensione .dmp dal percorso (QTMiner la aggiunge automaticamente).
                String filePath = file.getAbsolutePath();
                if (filePath.endsWith(DMP_EXT)) {
                    filePath = filePath.substring(0, filePath.length() - 4);
                }
                final String sanitizedFilePath = filePath;

                ClusteringService clusteringService = ApplicationContext.getInstance().getClusteringService();

                StdAsync.submit("qtgui-save-clustering", () -> {
                    clusteringService.saveClusteringResults(sanitizedFilePath, result.getMiner());
                    return null;
                }).onSuccess(ignored -> {
                    updateStatus("Clustering salvato: " + file.getName());
                    showInfo("Salvataggio Completato",
                            "Clustering salvato con successo in:\n" + file.getAbsolutePath());
                    logger.info("Clustering salvato: {}", file.getAbsolutePath());
                }).onFailure(error -> {
                    logger.error(ERROR_SAVING, error);
                    updateStatus(ERROR_SAVING);
                    showError(ERROR_TITLE, SAVE_ERROR_MSG + error.getMessage());
                });
            }

        } catch (Exception e) {
            logger.error(ERROR_SAVING, e);
            updateStatus(ERROR_SAVING);
            showError(ERROR_TITLE, SAVE_ERROR_MSG + e.getMessage());
        }
    }

    /**
     * Gestisce l'evento "Salva con nome".
     * <p>
     * Delega al metodo {@link #handleSave()}, poiché il file chooser
     * richiede sempre la destinazione del file.
     *
     * @see #handleSave()
     */
    private void handleSaveAs() {
        logger.info("Salva con nome cliccato");
        // Stesso comportamento di handleSave (File Chooser chiede sempre destinazione).
        handleSave();
    }

    /**
     * Gestisce l'evento "Esci" per chiudere l'applicazione.
     * <p>
     * Termina l'applicazione JavaFX in modo pulito chiamando {@link Platform#exit()}.
     * Tutti i thread daemon vengono terminati automaticamente.
     *
     * @see Platform#exit()
     */
    private void handleExit() {
        logger.info("Esci cliccato");
        StdGui.exit();
    }

    /**
     * Gestisce l'evento "Impostazioni" dal menu.
     * <p>
     * Naviga alla vista delle impostazioni dove l'utente può configurare
     * preferenze dell'applicazione come tema, percorsi predefiniti, ecc.
     *
     * @see #navigateTo(String)
     */
    private void handleSettings() {
        logger.info("Impostazioni cliccato");
        navigateTo("settings.fxml");
    }

    /**
     * Gestisce l'evento "Esegui Clustering" dalla toolbar.
     * <p>
     * Verifica che esista una configurazione valida in {@link ApplicationContext}.
     * Se presente, naviga alla vista di clustering; altrimenti reindirizza
     * alla schermata Home per configurare i parametri.
     *
     * @see ApplicationContext#getCurrentConfiguration()
     * @see #navigateTo(String)
     */
    private void handleRunClustering() {
        logger.info("Esegui Clustering cliccato");

        // Verifica se esiste una configurazione valida.
        if (ApplicationContext.getInstance().getCurrentConfiguration() == null) {
            logger.warn("Nessuna configurazione trovata, reindirizzo a home per configurazione");
            updateStatus("Configura prima i parametri di clustering");
            navigateTo("home.fxml");
        } else {
            navigateTo("clustering.fxml");
        }
    }

    /**
     * Gestisce l'evento "Esporta" dalla toolbar.
     * <p>
     * Verifica che esistano risultati da esportare, quindi naviga alla
     * schermata dei risultati dove l'utente può scegliere il formato
     * di esportazione (CSV, TXT, ZIP) tramite il pulsante dedicato.
     *
     * @see ApplicationContext#getCurrentResult()
     * @see #navigateTo(String)
     */
    private void handleExport() {
        logger.info("Esporta cliccato");

        ClusteringResult result = ApplicationContext.getInstance().getCurrentResult();
        if (result == null) {
            showWarning(NO_RESULTS_TITLE, NO_RESULTS_EXPORT_MSG);
            return;
        }

        // Naviga a schermata Results dove l'utente può scegliere il formato export.
        navigateTo("results.fxml");
        updateStatus("Usa il pulsante Esporta nella schermata Risultati");

        // Mostra hint
        showInfo("Esportazione", "Vai alla schermata Risultati e usa il pulsante 'Esporta'\n"
                + "per scegliere il formato di esportazione (CSV, TXT, ZIP).");
    }

    /**
     * Gestisce l'evento "Aiuto" dal menu.
     * <p>
     * Mostra una finestra di dialogo con informazioni sulla documentazione
     * dell'applicazione. In future versioni potrebbe aprire la documentazione
     * completa in un browser.
     *
     * @see #showInfo(String, String)
     */
    private void handleHelp() {
        logger.info("Aiuto cliccato");
        showInfo("Aiuto", "Documentazione GUI QT Clustering\n\nPer maggiori informazioni, consulta la documentazione.");
    }

    /**
     * Gestisce l'evento "Informazioni" dal menu.
     * <p>
     * Tenta di aprire il dialogo {@link AboutDialog}. In caso di errore,
     * mostra un dialogo informativo semplice con le informazioni di base
     * sull'applicazione come fallback.
     *
     * @see AboutDialog
     * @see #showInfo(String, String)
     */
    private void handleAbout() {
        logger.info("Informazioni cliccato");

        try {
            AboutDialog aboutDialog = new AboutDialog();
            aboutDialog.show();
            logger.info("AboutDialog aperto");
        } catch (Exception e) {
            logger.error("Errore durante apertura About dialog", e);
            // Fallback al dialog semplice.
            showInfo("Informazioni su QT Clustering",
                    "QT Clustering GUI v1.0.0\n\n" + "Algoritmo Quality Threshold Clustering\n"
                            + "Sviluppato per il corso MAP\n\n" + "Applicazione GUI JavaFX");
        }
    }

    //===--------------------------- DIALOG HELPERS ----------------------------===//

    /**
     * Mostra un dialogo informativo all'utente tramite {@link StdDialog}.
     * <p>
     * I dettagli JavaFX della finestra modale sono incapsulati nella libreria
     * riutilizzabile.
     *
     * @param title il titolo della finestra di dialogo
     * @param message il messaggio da visualizzare
     * @see StdDialog
     */
    private void showInfo(String title, String message) {
        StdDialog.info(title, message);
    }

    /**
     * Mostra un dialogo di errore all'utente tramite {@link StdDialog}.
     * <p>
     * Utilizzato per comunicare errori critici che richiedono l'attenzione
     * dell'utente.
     *
     * @param title il titolo della finestra di dialogo
     * @param message il messaggio di errore dettagliato
     * @see StdDialog
     */
    private void showError(String title, String message) {
        StdDialog.error(title, message);
    }

    /**
     * Mostra un dialogo di avviso all'utente tramite {@link StdDialog}.
     * <p>
     * Utilizzato per situazioni che richiedono attenzione ma non sono errori
     * critici.
     *
     * @param title il titolo della finestra di dialogo
     * @param message il messaggio di avviso
     * @see StdDialog
     */
    private void showWarning(String title, String message) {
        StdDialog.warning(title, message);
    }
}

//===---------------------------------------------------------------------------===//
