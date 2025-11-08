package gui.controllers;

import data.Data;
import data.Tuple;
import gui.models.ClusteringResult;
import gui.utils.ApplicationContext;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import mining.Cluster;
import mining.ClusterSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    // Dati clustering
    private ClusteringResult clusteringResult;
    private ClusterSet clusterSet;
    private Data data;
    private List<Cluster> clusterList; // Lista per accesso indicizzato

    /**
     * Inizializza il controller.
     * Chiamato automaticamente dopo il caricamento FXML.
     */
    @FXML
    public void initialize() {
        logger.info("Inizializzazione ResultsController...");

        setupTreeView();
        setupButtons();
        loadClusteringResults(); // Carica risultati reali da ApplicationContext
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
     * Carica i risultati del clustering da ApplicationContext.
     */
    private void loadClusteringResults() {
        // Recupera risultati dal contesto
        clusteringResult = ApplicationContext.getInstance().getCurrentResult();

        if (clusteringResult == null) {
            logger.error("Nessun risultato clustering disponibile");
            showError("Dati Non Disponibili",
                    "Nessun risultato di clustering trovato. Eseguire prima un clustering.");
            return;
        }

        clusterSet = clusteringResult.getClusterSet();
        data = clusteringResult.getData();

        // Converti ClusterSet in List per accesso indicizzato
        clusterList = new ArrayList<>();
        for (Cluster c : clusterSet) {
            clusterList.add(c);
        }

        int numClusters = clusteringResult.getNumClusters();
        int numTuples = clusteringResult.getNumTuples();
        double radius = clusteringResult.getRadius();

        // Calcola statistiche
        double avgSize = (double) numTuples / numClusters;
        int maxSize = 0;
        int minSize = Integer.MAX_VALUE;

        for (Cluster cluster : clusterList) {
            int size = cluster.getSize();
            if (size > maxSize) maxSize = size;
            if (size < minSize) minSize = size;
        }

        // Aggiorna etichette statistiche
        totalClustersLabel.setText(String.valueOf(numClusters));
        totalTuplesLabel.setText(String.valueOf(numTuples));
        radiusLabel.setText(String.format("%.3f", radius));
        avgClusterSizeLabel.setText(String.format("%.2f", avgSize));
        largestClusterLabel.setText(String.valueOf(maxSize));
        smallestClusterLabel.setText(String.valueOf(minSize));

        summaryLabel.setText(String.format("Clustering completato con %d cluster da %d tuple (radius: %.3f)",
                numClusters, numTuples, radius));

        // Costruisce albero cluster
        TreeItem<String> rootItem = new TreeItem<>("Risultati Clustering");
        rootItem.setExpanded(true);

        for (int i = 0; i < clusterList.size(); i++) {
            Cluster cluster = clusterList.get(i);
            TreeItem<String> clusterItem = new TreeItem<>("Cluster " + (i + 1) +
                    " (" + cluster.getSize() + " tuple)");

            // Aggiungi tuple del cluster
            int[] tupleIds = cluster.getTupleIDs();
            for (int tupleId : tupleIds) {
                TreeItem<String> tupleItem = new TreeItem<>("Tupla " + tupleId);
                clusterItem.getChildren().add(tupleItem);
            }

            rootItem.getChildren().add(clusterItem);
        }

        clusterTreeView.setRoot(rootItem);
        clusterTreeView.setShowRoot(false);

        statusLabel.setText(numClusters + " cluster caricati con successo");
        logger.info("Risultati clustering caricati: {} cluster, {} tuple", numClusters, numTuples);
    }

    /**
     * Gestisce la selezione di un cluster nell'albero.
     *
     * @param item elemento dell'albero selezionato
     */
    private void handleClusterSelection(TreeItem<String> item) {
        if (clusterSet == null || data == null) {
            return;
        }

        String value = item.getValue();
        logger.info("Elemento selezionato: {}", value);

        if (value.startsWith("Cluster ")) {
            try {
                // Estrae il numero del cluster (formato: "Cluster X (Y tuple)")
                String clusterNumStr = value.substring(8); // Rimuovi "Cluster "
                int spacePos = clusterNumStr.indexOf(' ');
                if (spacePos > 0) {
                    clusterNumStr = clusterNumStr.substring(0, spacePos);
                }

                int clusterIndex = Integer.parseInt(clusterNumStr) - 1;
                Cluster cluster = clusterList.get(clusterIndex);

            // Ottieni centroide
            Tuple centroid = cluster.getCentroid();

            // Aggiorna la scheda riepilogo
            summaryTextArea.setText(cluster.toString(data));

            // Aggiorna la scheda tuple
            StringBuilder tuples = new StringBuilder();
            tuples.append("Tuple nel Cluster ").append(clusterIndex + 1).append("\n");
            tuples.append("=".repeat(50)).append("\n\n");

            int[] tupleIds = cluster.getTupleIDs();
            for (int i = 0; i < tupleIds.length; i++) {
                int tupleId = tupleIds[i];
                Tuple tuple = data.getItemSet(tupleId);
                double distance = centroid.getDistance(tuple);

                tuples.append(String.format("%d. Tupla %d - distanza: %.3f\n",
                        i + 1, tupleId, distance));
                tuples.append("   ").append(tuple.toString()).append("\n\n");
            }

            tuplesTextArea.setText(tuples.toString());

            // Aggiorna la scheda statistiche
            StringBuilder stats = new StringBuilder();
            stats.append("Statistiche Cluster ").append(clusterIndex + 1).append("\n");
            stats.append("=".repeat(50)).append("\n\n");
            stats.append("Numero di tuple: ").append(cluster.getSize()).append("\n");
            stats.append("Centroide:\n  ").append(centroid.toString()).append("\n\n");

            // Calcola distanze
            double minDist = Double.MAX_VALUE;
            double maxDist = 0;
            double sumDist = 0;

            for (int tupleId : tupleIds) {
                double dist = centroid.getDistance(data.getItemSet(tupleId));
                if (dist < minDist) minDist = dist;
                if (dist > maxDist) maxDist = dist;
                sumDist += dist;
            }

            double avgDist = sumDist / tupleIds.length;

            stats.append(String.format("Distanza minima: %.3f\n", minDist));
            stats.append(String.format("Distanza massima: %.3f\n", maxDist));
            stats.append(String.format("Distanza media: %.3f\n", avgDist));

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
                // Mostra dettagli tupla
                int tupleId = Integer.parseInt(value.substring(6));
                Tuple tuple = data.getItemSet(tupleId);

                StringBuilder details = new StringBuilder();
                details.append("Dettagli Tupla ").append(tupleId).append("\n");
                details.append("=".repeat(50)).append("\n\n");
                details.append("Valori attributi:\n");

                for (int i = 0; i < data.getNumberOfExplanatoryAttributes(); i++) {
                    details.append("  - ").append(data.getExplanatoryAttribute(i).getName())
                           .append(": ").append(tuple.get(i).getValue()).append("\n");
                }

                // Trova il cluster di appartenenza
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

    /**
     * Mostra un dialogo di errore.
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
