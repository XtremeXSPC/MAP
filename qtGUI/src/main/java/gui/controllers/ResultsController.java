package gui.controllers;

//===---------------------------------------------------------------------------===//
// Importazioni Java standard.
import data.Data;
import data.Tuple;
import com.map.stdgui.StdClipboard;
import com.map.stdgui.StdDialog;
import com.map.stdgui.StdFileDialog;
import com.map.stdgui.StdTree;
import com.map.stdgui.StdView;
import com.map.stdgui.StdWindow;
import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gui.charts.ChartViewer;
import gui.dialogs.StatisticsDialog;
import gui.models.ClusteringResult;
import gui.services.ClusteringService;
import gui.services.ExportService;
import gui.utils.ApplicationContext;
// Importazioni JavaFX.
import javafx.fxml.FXML;
import javafx.scene.control.*;
import mining.Cluster;
import mining.ClusterSet;
//===---------------------------------------------------------------------------===//

/**
 * Controller per la vista Results.
 * <p>
 * Questa classe gestisce:
 * <ul>
 *   <li>Caricamento dei risultati da {@link ApplicationContext}</li>
 *   <li>Costruzione dell'albero cluster/tuple con {@link StdTree}</li>
 *   <li>Visualizzazione di riepiloghi e statistiche dettagliate</li>
 *   <li>Esportazione e salvataggio risultati</li>
 *   <li>Visualizzazione grafica e statistiche avanzate</li>
 * </ul>
 * <p>
 * Il controller viene inizializzato automaticamente da JavaFX quando viene
 * caricato il file FXML associato. I campi {@code @FXML} sono iniettati dal framework.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @see gui.models.ClusteringResult
 * @see gui.services.ExportService
 */
public class ResultsController {

    // Logger per la classe ResultsController.
    private static final Logger logger = LoggerFactory.getLogger(ResultsController.class);

    //===--------------------------- INSTANCE FIELDS ---------------------------===//

    // Dati clustering correnti.
    private ClusteringResult clusteringResult;
    private ClusterSet clusterSet;
    private Data data;
    private List<Cluster> clusterList; // Lista per accesso indicizzato.
    private StdTree.Tree resultTree;

    //===---------------------------- FXML CONTROLS ----------------------------===//

    // Intestazione e riepilogo.
    @FXML
    private Label summaryLabel;

    // Etichette statistiche.
    @FXML
    private Label totalClustersLabel;
    @FXML
    private Label totalTuplesLabel;
    @FXML
    private Label radiusLabel;
    @FXML
    private Label avgClusterSizeLabel;
    @FXML
    private Label largestClusterLabel;
    @FXML
    private Label smallestClusterLabel;

    // TreeView e dettagli.
    @FXML
    private TreeView<String> clusterTreeView;
    @FXML
    private TextArea summaryTextArea;
    @FXML
    private TextArea tuplesTextArea;
    @FXML
    private TextArea statisticsTextArea;

    // Pulsanti.
    @FXML
    private Button btnVisualize;
    @FXML
    private Button btnStatistics;
    @FXML
    private Button btnExport;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnNewAnalysis;
    @FXML
    private Button btnExpandAll;
    @FXML
    private Button btnCollapseAll;
    @FXML
    private Button btnCopyDetails;

    // Footer.
    @FXML
    private Label statusLabel;
    @FXML
    private Label timestampLabel;

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruttore di default.
     * <p>
     * Invocato da JavaFX durante il caricamento FXML. L'inizializzazione
     * dei componenti avviene in {@link #initialize()}.
     */
    public ResultsController() {
        // Costruttore vuoto - l'inizializzazione avviene nel metodo initialize()
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Inizializza il controller dopo l'iniezione FXML.
     * <p>
     * Configura l'albero, i pulsanti e carica i risultati correnti.
     */
    @FXML
    public void initialize() {
        logger.info("Inizializzazione ResultsController...");

        setupTreeView();
        setupButtons();
        loadClusteringResults(); // Carica risultati reali da ApplicationContext.
        updateTimestamp();

        logger.info("ResultsController inizializzato con successo");
    }

    //===--------------------------- PRIVATE METHODS ---------------------------===//

    /**
     * Configura la TreeView con listener di selezione.
     * <p>
     * La selezione di un nodo aggiorna il pannello dei dettagli.
     */
    private void setupTreeView() {
        resultTree = StdTree.bind(clusterTreeView);
        resultTree.onSelect(this::handleClusterSelection);
    }

    /**
     * Configura i gestori eventi dei pulsanti.
     */
    private void setupButtons() {
        btnVisualize.setOnAction(e -> handleVisualize());
        if (btnStatistics != null) {
            btnStatistics.setOnAction(e -> handleStatistics());
        }
        btnExport.setOnAction(e -> handleExport());
        btnSave.setOnAction(e -> handleSave());
        btnNewAnalysis.setOnAction(e -> handleNewAnalysis());
        btnExpandAll.setOnAction(e -> expandAllNodes());
        btnCollapseAll.setOnAction(e -> collapseAllNodes());
        btnCopyDetails.setOnAction(e -> handleCopyDetails());
    }

    /**
     * Carica i risultati del clustering da {@link ApplicationContext}.
     * <p>
     * Popola le statistiche in alto e costruisce l'albero dei cluster.
     */
    private void loadClusteringResults() {
        // Recupera risultati dal contesto.
        clusteringResult = ApplicationContext.getInstance().getCurrentResult();

        if (clusteringResult == null) {
            logger.error("Nessun risultato clustering disponibile");
            showError("Dati Non Disponibili", "Nessun risultato di clustering trovato. Eseguire prima un clustering.");
            return;
        }

        clusterSet = clusteringResult.getClusterSet();
        data = clusteringResult.getData();

        // Converti ClusterSet in List per accesso indicizzato.
        clusterList = new ArrayList<>();
        for (Cluster c : clusterSet) {
            clusterList.add(c);
        }

        int numClusters = clusteringResult.getNumClusters();
        int numTuples = clusteringResult.getNumTuples();
        double radius = clusteringResult.getRadius();

        // Calcola statistiche (gestione cluster vuoti).
        double avgSize = 0.0;
        int maxSize = 0;
        int minSize = 0;

        if (numClusters > 0) {
            avgSize = (double) numTuples / numClusters;
            minSize = Integer.MAX_VALUE;

            for (Cluster cluster : clusterList) {
                int size = cluster.getSize();
                if (size > maxSize)
                    maxSize = size;
                if (size < minSize)
                    minSize = size;
            }
        }

        // Aggiorna etichette statistiche.
        totalClustersLabel.setText(String.valueOf(numClusters));
        totalTuplesLabel.setText(String.valueOf(numTuples));
        radiusLabel.setText(String.format("%.3f", radius));
        avgClusterSizeLabel.setText(String.format("%.2f", avgSize));
        largestClusterLabel.setText(String.valueOf(maxSize));
        smallestClusterLabel.setText(String.valueOf(minSize));

        if (numClusters > 0) {
            summaryLabel.setText(String.format("Clustering completato con %d cluster da %d tuple (radius: %.3f)",
                    numClusters, numTuples, radius));
        } else {
            summaryLabel.setText(String.format("Clustering completato con 0 cluster (radius: %.3f)", radius));
        }

        // Costruisce albero cluster.
        List<StdTree.Node> clusterNodes = new ArrayList<>();

        for (int i = 0; i < clusterList.size(); i++) {
            Cluster cluster = clusterList.get(i);
            List<StdTree.Node> tupleNodes = new ArrayList<>();

            // Aggiungi tuple del cluster.
            int[] tupleIds = cluster.getTupleIDs();
            for (int tupleId : tupleIds) {
                tupleNodes.add(new StdTree.Node("Tupla " + tupleId));
            }

            clusterNodes.add(new StdTree.Node("Cluster " + (i + 1) + " (" + cluster.getSize() + " tuple)",
                    tupleNodes));
        }

        resultTree.root(new StdTree.Node("Risultati Clustering", clusterNodes), false);

        if (numClusters > 0) {
            statusLabel.setText(numClusters + " cluster caricati con successo");
        } else {
            statusLabel.setText("Nessun cluster trovato");
        }
        logger.info("Risultati clustering caricati: {} cluster, {} tuple", numClusters, numTuples);
    }

    //===--------------------------- EVENT HANDLERS ----------------------------===//

    /**
     * Gestisce la selezione di un elemento nell'albero.
     * <p>
     * Se viene selezionato un cluster, mostra riepilogo, tuple e statistiche.
     * Se viene selezionata una tupla, mostra i dettagli della singola tupla.
     *
     * @param value etichetta dell'albero selezionata
     */
    private void handleClusterSelection(String value) {
        if (clusterSet == null || data == null) {
            return;
        }

        logger.info("Elemento selezionato: {}", value);

        if (value.startsWith("Cluster ")) {
            try {
                // Estrae il numero del cluster (formato: "Cluster X (Y tuple)").
                String clusterNumStr = value.substring(8); // Rimuovi "Cluster "
                int spacePos = clusterNumStr.indexOf(' ');
                if (spacePos > 0) {
                    clusterNumStr = clusterNumStr.substring(0, spacePos);
                }

                int clusterIndex = Integer.parseInt(clusterNumStr) - 1;
                Cluster cluster = clusterList.get(clusterIndex);

                // Ottieni centroide.
                Tuple centroid = cluster.getCentroid();

                // Aggiorna la scheda riepilogo.
                summaryTextArea.setText(cluster.toString(data));

                // Aggiorna la scheda tuple.
                StringBuilder tuples = new StringBuilder();
                tuples.append("Tuple nel Cluster ").append(clusterIndex + 1).append("\n");
                tuples.append("=".repeat(50)).append("\n\n");

                int[] tupleIds = cluster.getTupleIDs();
                for (int i = 0; i < tupleIds.length; i++) {
                    int tupleId = tupleIds[i];
                    Tuple tuple = data.getItemSet(tupleId);
                    double distance = centroid.getDistance(tuple);

                    tuples.append(String.format("%d. Tupla %d - distanza: %.3f\n", i + 1, tupleId, distance));
                    tuples.append("   ").append(tuple.toString()).append("\n\n");
                }

                tuplesTextArea.setText(tuples.toString());

                // Aggiorna la scheda statistiche.
                StringBuilder stats = new StringBuilder();
                stats.append("Statistiche Cluster ").append(clusterIndex + 1).append("\n");
                stats.append("=".repeat(50)).append("\n\n");
                stats.append("Numero di tuple: ").append(cluster.getSize()).append("\n");
                stats.append("Centroide:\n  ").append(centroid.toString()).append("\n\n");

                // Calcola distanze.
                double minDist = Double.MAX_VALUE;
                double maxDist = 0;
                double sumDist = 0;

                for (int tupleId : tupleIds) {
                    double dist = centroid.getDistance(data.getItemSet(tupleId));
                    if (dist < minDist)
                        minDist = dist;
                    if (dist > maxDist)
                        maxDist = dist;
                    sumDist += dist;
                }

                double avgDist = sumDist / tupleIds.length;

                stats.append(String.format("Distanza minima: %.3f%n", minDist));
                stats.append(String.format("Distanza massima: %.3f%n", maxDist));
                stats.append(String.format("Distanza media: %.3f%n", avgDist));

                statisticsTextArea.setText(stats.toString());

                statusLabel.setText("Visualizzazione dettagli per Cluster " + (clusterIndex + 1));

            } catch (NumberFormatException e) {
                logger.error("Errore parsing numero cluster da: {}", value, e);
                statusLabel.setText("Errore: formato cluster non valido");
                showError("Errore", "Impossibile interpretare il numero del cluster selezionato.");
            } catch (IndexOutOfBoundsException e) {
                logger.error("Indice cluster non valido: {}", value, e);
                statusLabel.setText("Errore: indice cluster non valido");
                showError("Errore", "Il cluster selezionato non esiste.");
            }

        } else if (value.startsWith("Tupla ")) {
            try {
                // Mostra dettagli tupla.
                int tupleId = Integer.parseInt(value.substring(6));
                Tuple tuple = data.getItemSet(tupleId);

                StringBuilder details = new StringBuilder();
                details.append("Dettagli Tupla ").append(tupleId).append("\n");
                details.append("=".repeat(50)).append("\n\n");
                details.append("Valori attributi:\n");

                for (int i = 0; i < data.getNumberOfExplanatoryAttributes(); i++) {
                    details.append("  - ").append(data.getExplanatoryAttribute(i).getName()).append(": ")
                            .append(tuple.get(i).getValue()).append("\n");
                }

                // Trova il cluster di appartenenza.
                for (int i = 0; i < clusterList.size(); i++) {
                    Cluster c = clusterList.get(i);
                    if (c.contain(tupleId)) {
                        Tuple centroid = c.getCentroid();
                        double distance = centroid.getDistance(tuple);
                        details.append("\nCluster di appartenenza: ").append(i + 1).append("\n");
                        details.append(String.format("Distanza dal centroide: %.3f\n", distance));
                        break;
                    }
                }

                summaryTextArea.setText(details.toString());
                tuplesTextArea.clear();
                statisticsTextArea.clear();

                statusLabel.setText("Visualizzazione dettagli per " + value);

            } catch (NumberFormatException e) {
                logger.error("Errore parsing numero tupla da: {}", value, e);
                statusLabel.setText("Errore: formato tupla non valido");
                showError("Errore", "Impossibile interpretare il numero della tupla selezionata.");
            } catch (IndexOutOfBoundsException e) {
                logger.error("Indice tupla non valido: {}", value, e);
                statusLabel.setText("Errore: indice tupla non valido");
                showError("Errore", "La tupla selezionata non esiste.");
            }
        }
    }

    //===--------------------------- TREE OPERATIONS ---------------------------===//

    /**
     * Espande tutti i nodi dell'albero.
     */
    private void expandAllNodes() {
        if (resultTree != null) {
            resultTree.expandAll();
        }
        statusLabel.setText("Tutti i cluster espansi");
    }

    /**
     * Comprime tutti i nodi dell'albero.
     */
    private void collapseAllNodes() {
        if (resultTree != null) {
            resultTree.collapseAll();
        }
        statusLabel.setText("Tutti i cluster compressi");
    }

    /**
     * Aggiorna l'etichetta del timestamp con l'ora corrente.
     */
    private void updateTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        timestampLabel.setText("Generato: " + LocalDateTime.now().format(formatter));
    }

    //===--------------------------- ACTION METHODS ----------------------------===//

    /**
     * Gestisce il clic del pulsante statistiche.
     * <p>
     * Apre un dialog con statistiche avanzate sui risultati.
     */
    private void handleStatistics() {
        logger.info("Statistiche cliccato");

        if (clusteringResult == null) {
            showError("Dati Non Disponibili", "Nessun risultato di clustering disponibile per le statistiche.");
            return;
        }

        try {
            StatisticsDialog statsDialog = new StatisticsDialog(clusteringResult);
            statsDialog.show();

            statusLabel.setText("Finestra statistiche aperta");
            logger.info("StatisticsDialog aperto con successo");

        } catch (Exception e) {
            logger.error("Errore durante apertura statistiche", e);
            showError("Errore Statistiche",
                    "Si è verificato un errore durante l'apertura delle statistiche: " + e.getMessage());
        }
    }

    /**
     * Gestisce il clic del pulsante visualizza.
     * <p>
     * Apre la finestra di visualizzazione 2D dei cluster.
     */
    private void handleVisualize() {
        logger.info("Visualizza cliccato");

        if (clusteringResult == null) {
            showError("Dati Non Disponibili", "Nessun risultato di clustering disponibile per la visualizzazione.");
            return;
        }

        try {
            // Controlla se ci sono almeno 2 attributi per la visualizzazione 2D.
            if (data.getNumberOfExplanatoryAttributes() < 2) {
                StdDialog.warning("Visualizzazione Non Disponibile", "Dataset Insufficiente",
                        "Sono necessari almeno 2 attributi per la visualizzazione 2D.\n"
                                + "Il dataset corrente ha solo " + data.getNumberOfExplanatoryAttributes()
                                + " attributo/i.");
                return;
            }

            // Apri finestra di visualizzazione.
            ChartViewer chartViewer = new ChartViewer(clusteringResult);
            chartViewer.show();

            statusLabel.setText("Finestra di visualizzazione aperta");
            logger.info("ChartViewer aperto con successo");

        } catch (Exception e) {
            logger.error("Errore durante apertura visualizzazione", e);
            showError("Errore Visualizzazione",
                    "Si è verificato un errore durante l'apertura della visualizzazione: " + e.getMessage());
        }
    }

    /**
     * Gestisce il clic del pulsante esporta.
     * <p>
     * Richiede il formato e delega l'export al servizio dedicato.
     */
    private void handleExport() {
        logger.info("Esporta cliccato");

        if (clusteringResult == null) {
            showError("Dati Non Disponibili", "Nessun risultato di clustering disponibile per l'esportazione.");
            return;
        }

        // Mostra dialog di scelta formato export.
        Optional<String> result = StdDialog.choose("Esporta Risultati", "Scegli il formato di esportazione", "Formato:",
                "CSV", List.of("CSV", "TXT (Report)", "ZIP (Completo)"));
        result.ifPresent(format -> {
            try {
                StdFileDialog.Filter filter = switch (format) {
                    case "CSV" -> new StdFileDialog.Filter("File CSV", "*.csv");
                    case "TXT (Report)" -> new StdFileDialog.Filter("File TXT", "*.txt");
                    case "ZIP (Completo)" -> new StdFileDialog.Filter("File ZIP", "*.zip");
                    default -> throw new IllegalArgumentException("Formato esportazione non supportato: " + format);
                };

                Path selectedPath =
                        StdFileDialog.saveFile("Salva Esportazione", getDefaultExportFileName(format), filter)
                                .orElse(null);

                if (selectedPath != null) {
                    exportToFile(format, selectedPath.toFile());
                }
            } catch (Exception e) {
                logger.error("Errore durante esportazione", e);
                showError("Errore Esportazione", "Errore durante l'esportazione: " + e.getMessage());
            }
        });
    }

    //===--------------------------- EXPORT HELPERS ----------------------------===//

    /**
     * Esporta i risultati nel formato e file specificati.
     *
     * @param format formato selezionato
     * @param file file di destinazione
     */
    private void exportToFile(String format, File file) {
        try {
            ExportService exportService = ApplicationContext.getInstance().getExportService();

            statusLabel.setText("Esportazione in corso...");

            if (format.equals("CSV")) {
                exportService.exportToCsv(file.getAbsolutePath(), clusteringResult);
                statusLabel.setText("Esportazione CSV completata");
                showInfo("Esportazione Completata", "Risultati esportati in formato CSV:\n" + file.getAbsolutePath());

            } else if (format.equals("TXT (Report)")) {
                exportService.exportToTextReport(file.getAbsolutePath(), clusteringResult);
                statusLabel.setText("Esportazione report TXT completata");
                showInfo("Esportazione Completata", "Report esportato in formato TXT:\n" + file.getAbsolutePath());

            } else if (format.equals("ZIP (Completo)")) {
                exportService.exportToZip(file.getAbsolutePath(), clusteringResult);
                statusLabel.setText("Esportazione pacchetto ZIP completata");
                showInfo("Esportazione Completata", "Pacchetto completo esportato:\n" + file.getAbsolutePath()
                        + "\n\nContenuto: .dmp, .csv, report.txt, README.txt");
            }

            logger.info("Esportazione completata: {} in {}", format, file.getAbsolutePath());

        } catch (Exception e) {
            logger.error("Errore durante esportazione file", e);
            statusLabel.setText("Errore durante esportazione");
            showError("Errore", "Impossibile completare l'esportazione: " + e.getMessage());
        }
    }

    /**
     * Genera il nome file predefinito per l'esportazione.
     *
     * @param format formato selezionato
     * @return nome file con estensione coerente al formato
     */
    private String getDefaultExportFileName(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);

        if (format.equals("CSV")) {
            return "clustering_" + timestamp + ".csv";
        } else if (format.equals("TXT (Report)")) {
            return "clustering_report_" + timestamp + ".txt";
        } else if (format.equals("ZIP (Completo)")) {
            return "clustering_export_" + timestamp + ".zip";
        }

        return "clustering_" + timestamp;
    }

    /**
     * Gestisce il clic del pulsante salva.
     * <p>
     * Salva i risultati in formato serializzato (.dmp).
     */
    private void handleSave() {
        logger.info("Salva cliccato");

        if (clusteringResult == null) {
            showError("Dati Non Disponibili", "Nessun risultato di clustering disponibile per il salvataggio.");
            return;
        }

        try {
            Path selectedPath = StdFileDialog.saveFile("Salva Clustering", getDefaultSaveFileName(),
                    new StdFileDialog.Filter("File Clustering", "*.dmp")).orElse(null);
            File file = selectedPath == null ? null : selectedPath.toFile();

            if (file != null) {
                // Rimuovi estensione .dmp dal percorso (QTMiner la aggiunge automaticamente).
                String filePath = file.getAbsolutePath();
                if (filePath.endsWith(".dmp")) {
                    filePath = filePath.substring(0, filePath.length() - 4);
                }

                ClusteringService clusteringService = ApplicationContext.getInstance().getClusteringService();
                clusteringService.saveClusteringResults(filePath, clusteringResult.getMiner());

                statusLabel.setText("Clustering salvato in: " + file.getName());
                showInfo("Salvataggio Completato", "Clustering salvato con successo in:\n" + file.getAbsolutePath()
                        + "\n\nPuoi ricaricare questo file usando File > Apri");

                logger.info("Clustering salvato in: {}", file.getAbsolutePath());
            }

        } catch (Exception e) {
            logger.error("Errore durante salvataggio", e);
            statusLabel.setText("Errore durante salvataggio");
            showError("Errore", "Impossibile salvare il clustering: " + e.getMessage());
        }
    }

    /**
     * Genera il nome file predefinito per il salvataggio.
     * <p>
     * Nota: Non include l'estensione .dmp perche' viene aggiunta dal FileChooser.
     *
     * @return nome file base per il salvataggio
     */
    private String getDefaultSaveFileName() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        return "clustering_" + timestamp;
    }

    /**
     * Gestisce il clic del pulsante nuova analisi.
     * <p>
     * Richiede conferma e resetta lo stato applicativo.
     */
    private void handleNewAnalysis() {
        logger.info("Nuova Analisi cliccato");

        if (StdDialog.confirm("Nuova Analisi", "Avviare una nuova analisi di clustering?",
                "I risultati correnti saranno cancellati.")) {
            logger.info("Utente ha confermato nuova analisi");

            ApplicationContext.getInstance().setCurrentConfiguration(null);
            ApplicationContext.getInstance().setCurrentResult(null);
            navigateToHome();
        }
    }

    /**
     * Gestisce il clic del pulsante copia dettagli.
     * <p>
     * Copia negli appunti il contenuto del pannello riepilogo.
     */
    private void handleCopyDetails() {
        String content = summaryTextArea.getText();

        if (content != null && !content.isEmpty()) {
            StdClipboard.putText(content);
            statusLabel.setText("Dettagli copiati negli appunti");
            logger.info("Dettagli cluster copiati negli appunti");
        }
    }

    /**
     * Naviga alla schermata home.
     * <p>
     * Ricarica la vista principale e la imposta come root della scena.
     */
    private void navigateToHome() {
        try {
            StdWindow.current().replaceContent(StdView.load("/views/main.fxml"));
            statusLabel.setText("Ritorno alla schermata iniziale...");
            logger.info("Navigazione a home completata");

        } catch (RuntimeException e) {
            logger.error("Errore durante navigazione a home", e);
            showError("Errore", "Si è verificato un errore durante la navigazione: " + e.getMessage());
        }
    }

    //===--------------------------- PRIVATE HELPERS ---------------------------===//

    /**
     * Mostra un dialogo di errore.
     *
     * @param title   titolo del dialogo
     * @param message messaggio di errore
     */
    private void showError(String title, String message) {
        StdDialog.error(title, message);
    }

    /**
     * Mostra un dialogo informativo.
     *
     * @param title   titolo del dialogo
     * @param message messaggio informativo
     */
    private void showInfo(String title, String message) {
        StdDialog.info(title, message);
    }
}

//===---------------------------------------------------------------------------===//
