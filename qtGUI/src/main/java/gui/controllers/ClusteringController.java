package gui.controllers;

//===---------------------------------------------------------------------------===//
// Importazioni Java standard.
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import com.map.stdgui.StdAsync;
import com.map.stdgui.StdChart;
import com.map.stdgui.StdDialog;
import com.map.stdgui.StdGui;
import com.map.stdgui.StdJob;
import com.map.stdgui.StdProgress;
import com.map.stdgui.StdView;
import com.map.stdgui.StdWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import data.Data;
import gui.models.ClusteringConfiguration;
import gui.models.ClusteringResult;
import gui.services.DataImportService;
import gui.utils.ApplicationContext;
// Importazioni JavaFX.
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
// Importazioni del backend di mining.
import mining.Cluster;
import mining.ClusterSet;
import mining.QTMiner;
//===---------------------------------------------------------------------------===//

/**
 * Controller per la vista Clustering.
 * <p>
 * Questa classe gestisce:
 * <ul>
 *   <li>Esecuzione dell'algoritmo QT in background tramite {@link StdAsync}</li>
 *   <li>Aggiornamento di progresso, stato e log in tempo reale</li>
 *   <li>Creazione del grafico di distribuzione cluster</li>
 *   <li>Gestione di successo, errore o annullamento del processo</li>
 *   <li>Navigazione alla vista risultati al termine</li>
 * </ul>
 * <p>
 * Il controller viene inizializzato automaticamente da JavaFX quando viene
 * caricato il file FXML associato. I campi {@code @FXML} sono iniettati dal framework.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @see gui.models.ClusteringConfiguration
 * @see gui.models.ClusteringResult
 * @see gui.services.DataImportService
 */
public class ClusteringController {

    // Logger per la classe ClusteringController.
    private static final Logger logger = LoggerFactory.getLogger(ClusteringController.class);

    //===--------------------------- INSTANCE FIELDS ---------------------------===//

    // Job di clustering in esecuzione.
    private StdJob<Void> clusteringJob;
    private StdJob<Void> elapsedTimeJob;
    private Instant startTime;
    private volatile boolean isCancelled = false; // volatile per visibilità tra thread.
    private QTMiner miner; // Conserva il miner per creare ClusteringResult.
    private final Object logLock = new Object();
    private final StringBuilder logBuffer = new StringBuilder();
    private boolean logFlushScheduled = false;

    //===---------------------------- FXML CONTROLS ----------------------------===//
    // Componenti di progresso.
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label progressLabel;
    @FXML
    private Label progressPercentLabel;

    // Etichette di stato.
    @FXML
    private Label currentStepLabel;
    @FXML
    private Label clustersFoundLabel;
    @FXML
    private Label tuplesClusteredLabel;
    @FXML
    private Label elapsedTimeLabel;

    // Area log.
    @FXML
    private TextArea logTextArea;

    // Chart container.
    @FXML
    private Label chartPlaceholder;

    // Pulsanti.
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnViewResults;

    // Footer di stato.
    @FXML
    private HBox statusFooter;
    @FXML
    private Label statusMessageLabel;

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruttore di default.
     * <p>
     * Invocato da JavaFX durante il caricamento FXML. L'inizializzazione
     * dei componenti avviene nel metodo {@link #initialize()}.
     *
     * @see #initialize()
     */
    public ClusteringController() {
        // Costruttore vuoto - l'inizializzazione avviene nel metodo initialize().
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Inizializza il controller dopo l'iniezione FXML.
     * <p>
     * Configura i pulsanti e avvia immediatamente il clustering.
     */
    @FXML
    public void initialize() {
        logger.info("Inizializzazione ClusteringController...");

        setupButtons();
        startClustering();

        logger.info("ClusteringController inizializzato con successo");
    }

    //===--------------------------- PRIVATE METHODS ---------------------------===//

    /**
     * Configura i gestori eventi dei pulsanti della vista.
     */
    private void setupButtons() {
        btnCancel.setOnAction(e -> handleCancel());
        btnViewResults.setOnAction(e -> handleViewResults());
    }

    /**
     * Avvia il processo di clustering e collega i callback di progresso.
     * <p>
     * Crea il job, aggancia i listener di completamento e lo avvia
     * tramite l'astrazione riusabile StdAsync.
     */
    private void startClustering() {
        startTime = Instant.now();
        isCancelled = false;

        if (progressIndicator != null) {
            progressIndicator.setVisible(true);
        }

        // Crea il job di clustering e collega progresso/completamento.
        clusteringJob = StdAsync.submit("qtgui-clustering", this::runClustering)
                .onProgress(this::handleClusteringProgress)
                .onSuccess(ignored -> handleClusteringSuccess())
                .onFailure(this::handleClusteringFailure)
                .onCancel(this::handleClusteringCancelled);

        // Avvia l'aggiornatore del tempo trascorso.
        startElapsedTimeUpdater();

        logger.info("Job di clustering avviato");
    }

    /**
     * Esegue il clustering con integrazione backend reale.
     * <p>
     * Il job:
     * <ol>
     *   <li>Carica il dataset dalla sorgente configurata</li>
     *   <li>Esegue {@link QTMiner#compute(Data)}</li>
     *   <li>Costruisce un {@link ClusteringResult}</li>
     *   <li>Aggiorna log e progresso</li>
     * </ol>
     *
     * @param progress sink di progresso gestito da StdAsync
     * @return nessun risultato diretto
     */
    private Void runClustering(StdAsync.ProgressSink progress) throws Exception {
        // Recupera configurazione da ApplicationContext.
        ClusteringConfiguration config = ApplicationContext.getInstance().getCurrentConfiguration();

        if (config == null) {
            throw new IllegalStateException("Configurazione clustering non trovata");
        }

        progress.update(0.0, "Inizializzazione clustering...");
        appendLog("# ====== INIZIO CLUSTERING ====== #");
        appendLog("Configurazione:");
        appendLog("  Sorgente: " + config.getDataSource());
        appendLog("  Radius: " + config.getRadius());
        appendLog("");

        // Carica dati.
        progress.update(0.1, "Caricamento dataset...");

        DataImportService dataService = ApplicationContext.getInstance().getDataImportService();

        Data data;
        try {
            data = switch (config.getDataSource()) {
                case HARDCODED -> {
                    appendLog("Caricamento dataset hardcoded (PlayTennis)...");
                    yield dataService.loadHardcodedData();
                }
                case IRIS -> {
                    appendLog("Caricamento dataset standard Iris (150 tuple)...");
                    yield dataService.loadIrisData();
                }
                case CSV -> {
                    appendLog("Caricamento dataset da CSV: " + config.getCsvFilePath());
                    yield dataService.loadDataFromCSV(config.getCsvFilePath());
                }
                case DATABASE -> {
                    appendLog("Connessione a database: " + config.getDbHost() + ":" + config.getDbPort());
                    yield dataService.loadDataFromDatabase(config.getDbTableName(), config.getDbHost(),
                            config.getDbPort(), config.getDbName(), config.getDbUser(), config.getDbPassword());
                }
            };

            final int numExamples = data.getNumberOfExamples();
            final int numAttributes = data.getNumberOfExplanatoryAttributes();

            StdGui.later(() -> {
                appendLog("Dataset caricato con successo:");
                appendLog("  Tuple: " + numExamples);
                appendLog("  Attributi: " + numAttributes);
                appendLog("");
                tuplesClusteredLabel.setText("0 / " + numExamples);
            });

        } catch (Exception e) {
            appendLog("ERRORE: " + e.getMessage());
            throw new RuntimeException("Errore durante caricamento dataset: " + e.getMessage(), e);
        }

        if (cancellationRequested()) {
            return null;
        }

        // Esegui clustering.
        progress.update(0.3, "Esecuzione clustering Quality Threshold...");

        StdGui.later(() -> {
            appendLog("Avvio algoritmo QT...");
            currentStepLabel.setText("Costruzione cluster...");
        });

        long startTimeMs = System.currentTimeMillis();

        try {
            // Crea QTMiner.
            miner = new QTMiner(config.getRadius());

            // Esegui compute (questo è il lavoro principale).
            progress.update(0.4, "Esecuzione clustering Quality Threshold...");
            int numClusters = miner.compute(data);
            ClusterSet clusterSet = miner.getC();

            long executionTimeMs = System.currentTimeMillis() - startTimeMs;

            if (cancellationRequested()) {
                return null;
            }

            // Aggiorna progresso.
            progress.update(0.9, "Finalizzazione risultati...");

            final int finalNumClusters = numClusters;
            final int totalTuples = data.getNumberOfExamples();

            StdGui.later(() -> {
                appendLog("");
                appendLog("Clustering completato!");
                appendLog("  Cluster trovati: " + finalNumClusters);
                appendLog("  Tempo esecuzione: " + executionTimeMs + "ms");
                appendLog("");

                clustersFoundLabel.setText(String.valueOf(finalNumClusters));
                tuplesClusteredLabel.setText(totalTuples + " / " + totalTuples);

                // Mostra dettagli cluster (primi 5).
                int i = 0;
                for (Cluster cluster : clusterSet) {
                    if (i >= 5)
                        break;
                    appendLog("Cluster " + (i + 1) + ": " + cluster.getSize() + " tuple");
                    i++;
                }
                if (finalNumClusters > 5) {
                    appendLog("... e altri " + (finalNumClusters - 5) + " cluster");
                }
            });

            // Crea ClusteringResult.
            ClusteringResult result = new ClusteringResult(clusterSet, data, config.getRadius(), executionTimeMs, miner);

            // Salva nel contesto.
            ApplicationContext.getInstance().setCurrentResult(result);

            progress.update(1.0, "Clustering completato con successo");

            appendLog("");
            appendLog("# ============================================== #");
            appendLog("# ----- CLUSTERING COMPLETATO CON SUCCESSO ----- #");
            appendLog("# ============================================== #");

        } catch (Exception e) {
            appendLog("");
            appendLog("ERRORE durante clustering:");
            appendLog("  " + e.getMessage());
            throw new RuntimeException("Errore durante clustering: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Avvia il job di aggiornamento del tempo trascorso.
     * <p>
     * L'aggiornamento avviene ogni secondo finche' il job non termina.
     */
    private void startElapsedTimeUpdater() {
        elapsedTimeJob = StdAsync.submit("qtgui-clustering-clock", () -> {
            while (!isCancelled && clusteringJob != null && !clusteringJob.isDone()) {
                StdGui.later(() -> {
                    Duration elapsed = Duration.between(startTime, Instant.now());
                    long hours = elapsed.toHours();
                    long minutes = elapsed.toMinutesPart();
                    long seconds = elapsed.toSecondsPart();
                    elapsedTimeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            return null;
        });
    }

    /**
     * Aggiunge un messaggio all'area di log.
     * <p>
     * Usa un buffer per ridurre le chiamate al thread JavaFX.
     *
     * @param message messaggio di log
     */
    private void appendLog(String message) {
        synchronized (logLock) {
            logBuffer.append(message).append("\n");
            if (logFlushScheduled) {
                return;
            }
            logFlushScheduled = true;
        }

        StdGui.later(() -> {
            String pending;
            synchronized (logLock) {
                pending = logBuffer.toString();
                logBuffer.setLength(0);
                logFlushScheduled = false;
            }
            logTextArea.appendText(pending);
            logTextArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    /**
     * Crea il grafico a barre della distribuzione dei cluster.
     * <p>
     * Ogni barra rappresenta la dimensione di un cluster.
     */
    private void createClusterDistributionChart() {
        if (miner == null || miner.getC() == null)
            return;

        List<StdChart.BarPoint> points = new ArrayList<>();

        int i = 1;
        for (Cluster cluster : miner.getC()) {
            points.add(new StdChart.BarPoint("C" + i, cluster.getSize()));
            i++;
        }

        StdView chartView = StdChart.barChartView("Distribuzione Dimensioni Cluster", "Cluster", "Numero di Tuple",
                List.of(new StdChart.BarSeries("Tuple", points)));
        StdWindow.current().replaceRegion("chartContainer", chartView);
    }

    //===--------------------------- HANDLER METHODS ---------------------------===//

    /**
     * Aggiorna i controlli di progresso dal job StdAsync.
     *
     * @param progress stato corrente del job
     */
    private void handleClusteringProgress(StdProgress progress) {
        double value = progress.value();
        if (Double.isNaN(value) || value < 0.0) {
            value = 0.0;
        }

        progressBar.setProgress(value);
        progressPercentLabel.setText(String.format("%.0f%%", value * 100));

        if (progress.message() != null) {
            progressLabel.setText(progress.message());
        }
        if (progressIndicator != null) {
            progressIndicator.setProgress(value);
        }
    }

    /**
     * Gestisce il successo del clustering.
     * <p>
     * Aggiorna la UI, abilita il pulsante risultati e genera il grafico.
     */
    private void handleClusteringSuccess() {
        logger.info("Clustering completato con successo");
        stopElapsedTimeUpdater();

        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
        }

        statusMessageLabel.setText("Clustering completato con successo!");
        statusMessageLabel.setStyle("-fx-text-fill: #27ae60;");
        statusFooter.setVisible(true);
        statusFooter.setManaged(true);

        btnCancel.setVisible(false);
        btnCancel.setManaged(false);
        btnViewResults.setVisible(true);
        btnViewResults.setManaged(true);

        appendLog("\nPronto per visualizzare i risultati.");

        // Crea e mostra il grafico.
        createClusterDistributionChart();
    }

    /**
     * Gestisce il fallimento del clustering.
     * <p>
     * Mostra un messaggio di errore e aggiorna la UI.
     *
     * @param error errore del job
     */
    private void handleClusteringFailure(Throwable error) {
        logger.error("Clustering fallito", error);
        stopElapsedTimeUpdater();

        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
        }

        statusMessageLabel.setText("Clustering fallito: " + error.getMessage());
        statusMessageLabel.setStyle("-fx-text-fill: #e74c3c;");
        statusFooter.setVisible(true);
        statusFooter.setManaged(true);

        appendLog("\nERRORE: Clustering fallito!");
        appendLog("Motivo: " + error.getMessage());

        if (chartPlaceholder != null) {
            chartPlaceholder.setText("Clustering fallito.");
            chartPlaceholder.setStyle("-fx-text-fill: #e74c3c;");
        }

        showError("Clustering Fallito",
                "Si è verificato un errore durante il clustering:\n" + error.getMessage());
    }

    /**
     * Gestisce l'annullamento del clustering.
     * <p>
     * Aggiorna la UI e lascia la vista in stato consistente.
     */
    private void handleClusteringCancelled() {
        logger.info("Clustering annullato dall'utente");
        stopElapsedTimeUpdater();

        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
        }

        statusMessageLabel.setText("Clustering annullato dall'utente");
        statusMessageLabel.setStyle("-fx-text-fill: #f39c12;");
        statusFooter.setVisible(true);
        statusFooter.setManaged(true);

        appendLog("\nClustering annullato dall'utente.");

        if (chartPlaceholder != null) {
            chartPlaceholder.setText("Clustering annullato.");
        }
    }

    /**
     * Gestisce il clic del pulsante annulla.
     * <p>
     * Richiede conferma all'utente e annulla il job se in esecuzione.
     */
    private void handleCancel() {
        logger.info("Pulsante annulla cliccato");

        if (StdDialog.confirm("Annulla Clustering", "Sei sicuro di voler annullare il processo di clustering?",
                "Il progresso sarà perso.")) {
            isCancelled = true;
            if (clusteringJob != null && !clusteringJob.isDone()) {
                clusteringJob.cancel();
            }
            appendLog("\nUtente ha richiesto l'annullamento...");
        }
    }

    /**
     * Gestisce il clic del pulsante visualizza risultati.
     * <p>
     * Carica la vista risultati e la imposta come root della scena.
     */
    private void handleViewResults() {
        logger.info("Visualizza risultati cliccato");

        try {
            StdWindow.current().replaceRoot(StdView.load("/views/results.fxml"));
            logger.info("Navigazione a vista results completata");

        } catch (RuntimeException e) {
            logger.error("Errore durante navigazione a vista results", e);
            showError("Errore Navigazione", "Impossibile caricare la vista risultati:\n" + e.getMessage());
        }
    }

    //===--------------------------- PRIVATE HELPERS ---------------------------===//

    private boolean cancellationRequested() {
        return isCancelled || Thread.currentThread().isInterrupted();
    }

    private void stopElapsedTimeUpdater() {
        if (elapsedTimeJob != null && !elapsedTimeJob.isDone()) {
            elapsedTimeJob.cancel();
        }
    }

    /**
     * Mostra un dialogo di errore con titolo e messaggio specificati.
     *
     * @param title titolo del dialogo
     * @param message messaggio di errore
     */
    private void showError(String title, String message) {
        StdDialog.error(title, message);
    }
}

//===---------------------------------------------------------------------------===//
