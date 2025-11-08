package gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller per la vista Results.
 * Visualizza i risultati del clustering con albero dei cluster e informazioni dettagliate.
 */
public class ResultsController {

    private static final Logger logger = LoggerFactory.getLogger(ResultsController.class);

    // Intestazione e riepilogo
    @FXML private Label summaryLabel;

    // Etichette statistiche
    @FXML private Label totalClustersLabel;
    @FXML private Label totalTuplesLabel;
    @FXML private Label radiusLabel;
    @FXML private Label avgClusterSizeLabel;
    @FXML private Label largestClusterLabel;
    @FXML private Label smallestClusterLabel;

    // TreeView e dettagli
    @FXML private TreeView<String> clusterTreeView;
    @FXML private TextArea summaryTextArea;
    @FXML private TextArea tuplesTextArea;
    @FXML private TextArea statisticsTextArea;

    // Pulsanti
    @FXML private Button btnVisualize;
    @FXML private Button btnExport;
    @FXML private Button btnSave;
    @FXML private Button btnNewAnalysis;
    @FXML private Button btnExpandAll;
    @FXML private Button btnCollapseAll;
    @FXML private Button btnCopyDetails;

    // Footer
    @FXML private Label statusLabel;
    @FXML private Label timestampLabel;

    /**
     * Inizializza il controller.
     * Chiamato automaticamente dopo il caricamento FXML.
     */
    @FXML
    public void initialize() {
        logger.info("Inizializzazione ResultsController...");

        setupTreeView();
        setupButtons();
        loadSampleData(); // Per la demo dello Sprint 1
        updateTimestamp();

        logger.info("ResultsController inizializzato con successo");
    }

    /**
     * Configura la TreeView con listener di selezione.
     */
    private void setupTreeView() {
        clusterTreeView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    handleClusterSelection(newValue);
                }
            }
        );
    }

    /**
     * Configura i gestori eventi dei pulsanti.
     */
    private void setupButtons() {
        btnVisualize.setOnAction(e -> handleVisualize());
        btnExport.setOnAction(e -> handleExport());
        btnSave.setOnAction(e -> handleSave());
        btnNewAnalysis.setOnAction(e -> handleNewAnalysis());
        btnExpandAll.setOnAction(e -> expandAllNodes());
        btnCollapseAll.setOnAction(e -> collapseAllNodes());
        btnCopyDetails.setOnAction(e -> handleCopyDetails());
    }

    /**
     * Carica dati di clustering di esempio (per la dimostrazione dello Sprint 1).
     * Nello Sprint 2, questo sarà sostituito con i risultati di clustering effettivi.
     */
    private void loadSampleData() {
        // Statistiche di esempio
        totalClustersLabel.setText("11");
        totalTuplesLabel.setText("14");
        radiusLabel.setText("0.0");
        avgClusterSizeLabel.setText("1.27");
        largestClusterLabel.setText("3");
        smallestClusterLabel.setText("1");

        summaryLabel.setText("Clustering completato con 11 cluster da 14 tuple (radius: 0.0)");

        // Costruisce albero di esempio
        TreeItem<String> rootItem = new TreeItem<>("Risultati Clustering");
        rootItem.setExpanded(true);

        // Cluster di esempio
        for (int i = 1; i <= 11; i++) {
            TreeItem<String> clusterItem = new TreeItem<>("Cluster " + i);

            // Tuple di esempio nel cluster
            int tupleCount = (i % 3) + 1;
            for (int j = 0; j < tupleCount; j++) {
                TreeItem<String> tupleItem = new TreeItem<>("Tupla " + ((i - 1) * 2 + j + 1));
                clusterItem.getChildren().add(tupleItem);
            }

            rootItem.getChildren().add(clusterItem);
        }

        clusterTreeView.setRoot(rootItem);
        clusterTreeView.setShowRoot(false);

        statusLabel.setText("11 cluster caricati con successo");
    }

    /**
     * Gestisce la selezione di un cluster nell'albero.
     *
     * @param item elemento dell'albero selezionato
     */
    private void handleClusterSelection(TreeItem<String> item) {
        String value = item.getValue();
        logger.info("Cluster selezionato: {}", value);

        if (value.startsWith("Cluster ")) {
            // Estrae il numero del cluster
            String clusterNum = value.replace("Cluster ", "");

            // Aggiorna la scheda riepilogo
            summaryTextArea.setText(
                "Riepilogo Cluster " + clusterNum + "\n" +
                "================================\n\n" +
                "Centroide: (sunny, hot, high, weak, no)\n" +
                "Dimensione: " + item.getChildren().size() + " tuple\n" +
                "Distanza Media: 0.133\n\n" +
                "Questo cluster contiene tuple con caratteristiche simili."
            );

            // Aggiorna la scheda tuple
            StringBuilder tuples = new StringBuilder();
            tuples.append("Tuple nel Cluster ").append(clusterNum).append("\n");
            tuples.append("================================\n\n");

            int tupleNum = 1;
            for (TreeItem<String> child : item.getChildren()) {
                tuples.append(tupleNum++).append(". ")
                      .append(child.getValue())
                      .append(": (sunny, hot, high, weak, no) - distanza: 0.0\n");
            }

            tuplesTextArea.setText(tuples.toString());

            // Aggiorna la scheda statistiche
            statisticsTextArea.setText(
                "Statistiche Cluster " + clusterNum + "\n" +
                "================================\n\n" +
                "Numero di tuple: " + item.getChildren().size() + "\n" +
                "Distanza minima: 0.0\n" +
                "Distanza massima: 0.2\n" +
                "Distanza media: 0.133\n" +
                "Distanza mediana: 0.1\n\n" +
                "Metriche di qualità:\n" +
                "- Coesione: 0.867 (alta)\n" +
                "- Separazione: 0.654 (moderata)"
            );

            statusLabel.setText("Visualizzazione dettagli per Cluster " + clusterNum);

        } else if (value.startsWith("Tupla ")) {
            // Mostra dettagli tupla
            summaryTextArea.setText(
                "Dettagli " + value + "\n" +
                "================================\n\n" +
                "Attributi:\n" +
                "- Outlook: sunny\n" +
                "- Temperature: hot\n" +
                "- Humidity: high\n" +
                "- Wind: weak\n" +
                "- PlayTennis: no\n\n" +
                "Distanza dal centroide: 0.0"
            );

            tuplesTextArea.clear();
            statisticsTextArea.clear();

            statusLabel.setText("Visualizzazione dettagli per " + value);
        }
    }

    /**
     * Espande tutti i nodi dell'albero.
     */
    private void expandAllNodes() {
        TreeItem<String> root = clusterTreeView.getRoot();
        if (root != null) {
            expandTreeView(root);
        }
        statusLabel.setText("Tutti i cluster espansi");
    }

    /**
     * Espande ricorsivamente l'elemento dell'albero e i suoi figli.
     *
     * @param item elemento dell'albero da espandere
     */
    private void expandTreeView(TreeItem<String> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);
            for (TreeItem<String> child : item.getChildren()) {
                expandTreeView(child);
            }
        }
    }

    /**
     * Comprime tutti i nodi dell'albero.
     */
    private void collapseAllNodes() {
        TreeItem<String> root = clusterTreeView.getRoot();
        if (root != null) {
            collapseTreeView(root);
        }
        statusLabel.setText("Tutti i cluster compressi");
    }

    /**
     * Comprime ricorsivamente l'elemento dell'albero e i suoi figli.
     *
     * @param item elemento dell'albero da comprimere
     */
    private void collapseTreeView(TreeItem<String> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(false);
            for (TreeItem<String> child : item.getChildren()) {
                collapseTreeView(child);
            }
        }
    }

    /**
     * Aggiorna l'etichetta del timestamp con l'ora corrente.
     */
    private void updateTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        timestampLabel.setText("Generato: " + LocalDateTime.now().format(formatter));
    }

    /**
     * Gestisce il clic del pulsante visualizza.
     */
    private void handleVisualize() {
        logger.info("Visualizza cliccato");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Visualizzazione");
        alert.setHeaderText("Visualizzazione Cluster");
        alert.setContentText("La visualizzazione 2D/3D sarà implementata nello Sprint 3.");
        alert.showAndWait();
    }

    /**
     * Gestisce il clic del pulsante esporta.
     */
    private void handleExport() {
        logger.info("Esporta cliccato");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Esporta");
        alert.setHeaderText("Esporta Risultati");
        alert.setContentText("La funzionalità di esportazione (CSV, PDF, PNG) sarà implementata nello Sprint 4.");
        alert.showAndWait();
    }

    /**
     * Gestisce il clic del pulsante salva.
     */
    private void handleSave() {
        logger.info("Salva cliccato");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Salva");
        alert.setHeaderText("Salva Clustering");
        alert.setContentText("La funzionalità di salvataggio (file .dmp) sarà implementata nello Sprint 4.");
        alert.showAndWait();
    }

    /**
     * Gestisce il clic del pulsante nuova analisi.
     */
    private void handleNewAnalysis() {
        logger.info("Nuova Analisi cliccato");

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Nuova Analisi");
        confirmAlert.setHeaderText("Avviare una nuova analisi di clustering?");
        confirmAlert.setContentText("I risultati correnti saranno cancellati.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Tornare alla vista home
                logger.info("Utente ha confermato nuova analisi");
                statusLabel.setText("Avvio nuova analisi...");
            }
        });
    }

    /**
     * Gestisce il clic del pulsante copia dettagli.
     */
    private void handleCopyDetails() {
        String content = summaryTextArea.getText();

        if (content != null && !content.isEmpty()) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(content);
            clipboard.setContent(clipboardContent);

            statusLabel.setText("Dettagli copiati negli appunti");
            logger.info("Dettagli cluster copiati negli appunti");
        }
    }
}
