package gui.dialogs;

import data.Data;
import data.Attribute;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialog per visualizzare anteprima dataset.
 * Mostra le prime righe del dataset in una TableView.
 */
public class DatasetPreviewDialog {

    private static final Logger logger = LoggerFactory.getLogger(DatasetPreviewDialog.class);
    private static final int MAX_PREVIEW_ROWS = 20;

    private final Stage stage;
    private final Data data;

    /**
     * Costruttore.
     *
     * @param data dataset da visualizzare
     */
    public DatasetPreviewDialog(Data data) {
        this.data = data;
        this.stage = new Stage();
        setupDialog();
    }

    /**
     * Configura il dialog.
     */
    private void setupDialog() {
        stage.setTitle("Anteprima Dataset");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setWidth(900);
        stage.setHeight(600);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // Header con informazioni dataset
        VBox header = createHeader();
        root.setTop(header);

        // Tabella con dati
        TableView<ObservableList<String>> tableView = createTableView();
        VBox.setVgrow(tableView, Priority.ALWAYS);
        root.setCenter(tableView);

        // Footer con pulsante chiudi
        HBox footer = createFooter();
        root.setBottom(footer);

        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    /**
     * Crea header con informazioni dataset.
     */
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(0, 0, 15, 0));

        Label titleLabel = new Label("Anteprima Dataset");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        int totalRows = data.getNumberOfExamples();
        int totalCols = data.getNumberOfExplanatoryAttributes();
        int displayedRows = Math.min(totalRows, MAX_PREVIEW_ROWS);

        Label infoLabel = new Label(String.format(
            "Dataset: %d righe × %d colonne | Visualizzate: prime %d righe",
            totalRows, totalCols, displayedRows
        ));
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        header.getChildren().addAll(titleLabel, infoLabel);
        return header;
    }

    /**
     * Crea TableView con dati dataset.
     */
    private TableView<ObservableList<String>> createTableView() {
        TableView<ObservableList<String>> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        try {
            // Crea colonne da attributi
            int numAttributes = data.getNumberOfExplanatoryAttributes();
            for (int i = 0; i < numAttributes; i++) {
                final int colIndex = i;
                Attribute attribute = data.getExplanatoryAttribute(i);

                TableColumn<ObservableList<String>, String> column = new TableColumn<>(attribute.getName());
                column.setCellValueFactory(param -> {
                    ObservableList<String> row = param.getValue();
                    if (row != null && colIndex < row.size()) {
                        return new javafx.beans.property.SimpleStringProperty(row.get(colIndex));
                    }
                    return new javafx.beans.property.SimpleStringProperty("");
                });

                tableView.getColumns().add(column);
            }

            // Popola righe
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

            logger.info("Preview dataset creata: {} righe visualizzate", numRows);

        } catch (Exception e) {
            logger.error("Errore durante creazione preview dataset", e);
            showErrorInTable(tableView, "Errore durante caricamento dati: " + e.getMessage());
        }

        return tableView;
    }

    /**
     * Mostra messaggio di errore nella tabella.
     */
    private void showErrorInTable(TableView<ObservableList<String>> tableView, String message) {
        tableView.setPlaceholder(new Label(message));
    }

    /**
     * Crea footer con pulsante chiudi.
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

    /**
     * Mostra il dialog.
     */
    public void show() {
        stage.showAndWait();
    }
}
