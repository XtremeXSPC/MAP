package gui.dialogs;

//===---------------------------------------------------------------------------===//
// Importazioni Java standard.
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import data.Attribute;
import data.ContinuousAttribute;
import data.Data;
import data.DiscreteAttribute;
// Importazioni JavaFX.
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
//===---------------------------------------------------------------------------===//

/**
 * Dialog per visualizzare un'anteprima del dataset.
 * <p>
 * Questa classe gestisce:
 * <ul>
 *   <li>Creazione di uno {@link Stage} modale con layout a schede</li>
 *   <li>Tab "Dati" con una {@link TableView} delle prime righe</li>
 *   <li>Tab "Statistiche" con tipo e dettagli degli attributi</li>
 *   <li>Condivisione degli stili con la finestra principale</li>
 * </ul>
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.0.0
 */
public class DatasetPreviewDialog {

    //===------------------------------ CONSTANTS ------------------------------===//

    // Logger per la classe DatasetPreviewDialog.
    private static final Logger logger = LoggerFactory.getLogger(DatasetPreviewDialog.class);
    private static final int MAX_PREVIEW_ROWS = 20;

    //===--------------------------- INSTANCE FIELDS ---------------------------===//

    // Finestra del dialog.
    private final Stage stage;
    private final Data data;

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruisce il dialog e inizializza la UI.
     *
     * @param data dataset da visualizzare
     */
    public DatasetPreviewDialog(Data data) {
        this.data = data;
        this.stage = new Stage();
        setupDialog();
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Mostra il dialog in modalita' bloccante.
     */
    public void show() {
        stage.showAndWait();
    }

    //===--------------------------- PRIVATE METHODS ---------------------------===//

    /**
     * Configura lo stage e costruisce l'interfaccia.
     */
    private void setupDialog() {
        stage.setTitle("Anteprima Dataset");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setWidth(900);
        stage.setHeight(600);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("dialog-pane"); // Apply dialog-pane style
        root.setPadding(new Insets(20)); // Increased padding

        // Header con informazioni dataset.
        VBox header = createHeader();
        root.setTop(header);

        // TabPane centrale.
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().add(createDataTab());
        tabPane.getTabs().add(createStatsTab());

        root.setCenter(tabPane);

        // Footer con pulsante chiudi.
        HBox footer = createFooter();
        root.setBottom(footer);

        Scene scene = new Scene(root);
        // Aggiungi foglio di stile se necessario, altrimenti usa quello globale.
        if (stage.getOwner() != null && stage.getOwner().getScene() != null) {
            scene.getStylesheets().addAll(stage.getOwner().getScene().getStylesheets());
        }

        stage.setScene(scene);
    }

    /**
     * Crea l'header con informazioni sul dataset.
     *
     * @return VBox contenente titolo e info riassuntive
     */
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(0, 0, 15, 0));

        Label titleLabel = new Label("Anteprima Dataset");
        titleLabel.getStyleClass().add("title-label"); // Use CSS class instead of inline style

        int totalRows = data.getNumberOfExamples();
        int totalCols = data.getNumberOfExplanatoryAttributes();

        Label infoLabel = new Label(String.format("Dataset: %d righe × %d colonne", totalRows, totalCols));
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        header.getChildren().addAll(titleLabel, infoLabel);
        return header;
    }

    /**
     * Crea il tab con i dati grezzi (prime righe del dataset).
     *
     * @return Tab con TableView dei dati
     */
    private Tab createDataTab() {
        Tab tab = new Tab("Dati");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10, 0, 0, 0));

        int displayedRows = Math.min(data.getNumberOfExamples(), MAX_PREVIEW_ROWS);
        Label infoLabel = new Label("Visualizzazione prime " + displayedRows + " righe");
        infoLabel.setStyle("-fx-font-style: italic; -fx-font-size: 11px;");

        TableView<ObservableList<String>> tableView = createTableView();
        VBox.setVgrow(tableView, Priority.ALWAYS);

        content.getChildren().addAll(infoLabel, tableView);
        tab.setContent(content);

        return tab;
    }

    /**
     * Crea la TableView con i dati del dataset.
     * <p>
     * Le colonne sono generate dinamicamente dagli attributi.
     *
     * @return TableView con righe di preview
     */
    private TableView<ObservableList<String>> createTableView() {
        TableView<ObservableList<String>> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);

        try {
            // Crea colonne da attributi.
            int numAttributes = data.getNumberOfExplanatoryAttributes();
            for (int i = 0; i < numAttributes; i++) {
                final int colIndex = i;
                Attribute attribute = data.getExplanatoryAttribute(i);

                TableColumn<ObservableList<String>, String> column = new TableColumn<>(attribute.getName());
                column.setCellValueFactory(param -> {
                    ObservableList<String> row = param.getValue();
                    if (row != null && colIndex < row.size()) {
                        return new SimpleStringProperty(row.get(colIndex));
                    }
                    return new SimpleStringProperty("");
                });

                // Right-align numeric columns (heuristic: if attribute is Continuous).
                if (attribute instanceof ContinuousAttribute) {
                    column.setStyle("-fx-alignment: CENTER-RIGHT;");
                }

                tableView.getColumns().add(column);
            }

            // Popola righe.
            ObservableList<ObservableList<String>> rows = FXCollections.observableArrayList();
            int numRows = Math.min(data.getNumberOfExamples(), MAX_PREVIEW_ROWS);

            for (int i = 0; i < numRows; i++) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int j = 0; j < numAttributes; j++) {
                    Object value = data.getValue(i, j);
                    row.add(value != null ? value.toString() : "null");
                }
                rows.add(row);
            }

            tableView.setItems(rows);

        } catch (Exception e) {
            logger.error("Errore durante creazione preview dataset", e);
            tableView.setPlaceholder(new Label("Errore: " + e.getMessage()));
        }

        return tableView;
    }

    /**
     * Crea il tab con le statistiche degli attributi.
     *
     * @return Tab con tabella di statistiche
     */
    private Tab createStatsTab() {
        Tab tab = new Tab("Statistiche");

        TableView<AttributeStats> statsTable = new TableView<>();
        statsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);

        TableColumn<AttributeStats, String> nameCol = new TableColumn<>("Attributo");
        nameCol.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().name));

        TableColumn<AttributeStats, String> typeCol = new TableColumn<>("Tipo");
        typeCol.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().type));

        TableColumn<AttributeStats, String> detailsCol = new TableColumn<>("Dettagli");
        detailsCol.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().details));

        statsTable.getColumns().addAll(Arrays.asList(nameCol, typeCol, detailsCol));

        // Calcola statistiche.
        ObservableList<AttributeStats> stats = FXCollections.observableArrayList();
        int numAttributes = data.getNumberOfExplanatoryAttributes();

        for (int i = 0; i < numAttributes; i++) {
            Attribute attr = data.getExplanatoryAttribute(i);
            String type = "";
            String details = "";

            if (attr instanceof ContinuousAttribute) {
                ContinuousAttribute ca = (ContinuousAttribute) attr;
                type = "Continuo";
                details = String.format("Min: %.2f, Max: %.2f", ca.getMin(), ca.getMax());
            } else if (attr instanceof DiscreteAttribute) {
                DiscreteAttribute da = (DiscreteAttribute) attr;
                type = "Discreto";
                details = String.format("%d valori distinti", da.getNumberOfDistinctValues());
            }

            stats.add(new AttributeStats(attr.getName(), type, details));
        }

        statsTable.setItems(stats);

        VBox content = new VBox(statsTable);
        content.setPadding(new Insets(15, 0, 0, 0)); // Increased top padding
        VBox.setVgrow(statsTable, Priority.ALWAYS);

        tab.setContent(content);
        return tab;
    }

    /**
     * Classe interna per visualizzare le statistiche nella tabella.
     */
    private static class AttributeStats {
        final String name;
        final String type;
        final String details;

        AttributeStats(String name, String type, String details) {
            this.name = name;
            this.type = type;
            this.details = details;
        }
    }

    /**
     * Crea il footer con pulsante di chiusura.
     *
     * @return HBox con il pulsante "Chiudi"
     */
    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(15, 0, 0, 0));
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button closeButton = new Button("Chiudi");
        closeButton.setOnAction(e -> stage.close());
        closeButton.setPrefWidth(100);

        footer.getChildren().add(closeButton);
        return footer;
    }
}

//===---------------------------------------------------------------------------===//
