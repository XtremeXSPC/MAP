package gui.dialogs;

//===---------------------------------------------------------------------------===//
// Importazioni Java standard.
import java.util.ArrayList;
import java.util.List;
import com.map.stdgui.StdDataView;
import com.map.stdgui.StdView;
import com.map.stdgui.StdWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import data.Attribute;
import data.ContinuousAttribute;
import data.Data;
import data.DiscreteAttribute;
//===---------------------------------------------------------------------------===//

/**
 * Dialog per visualizzare un'anteprima del dataset.
 * <p>
 * Questa classe gestisce:
 * <ul>
 *   <li>Creazione di una finestra modale con layout a schede</li>
 *   <li>Tab "Dati" con le prime righe del dataset</li>
 *   <li>Tab "Statistiche" con tipo e dettagli degli attributi</li>
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
    private final StdWindow window;
    private final Data data;

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruisce il dialog e inizializza la UI.
     *
     * @param data dataset da visualizzare
     */
    public DatasetPreviewDialog(Data data) {
        this.data = data;
        this.window = createDialog();
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Mostra il dialog in modalita' bloccante.
     */
    public void show() {
        window.showAndWait();
    }

    //===--------------------------- PRIVATE METHODS ---------------------------===//

    /**
     * Crea la finestra del dialog e costruisce l'interfaccia.
     *
     * @return finestra del dialog
     */
    private StdWindow createDialog() {
        List<StdDataView.TabView> tabs = List.of(
                new StdDataView.TabView("Dati", createDataView()),
                new StdDataView.TabView("Statistiche", createStatsView()));

        return StdDataView.tabs("Anteprima Dataset", tabs, 900, 600, "Chiudi").modal(true);
    }

    /**
     * Crea la vista con i dati grezzi (prime righe del dataset).
     *
     * @return vista tabellare dei dati
     */
    private StdView createDataView() {
        try {
            int numAttributes = data.getNumberOfExplanatoryAttributes();
            int displayedRows = Math.min(data.getNumberOfExamples(), MAX_PREVIEW_ROWS);

            List<String> columns = new ArrayList<>();
            for (int i = 0; i < numAttributes; i++) {
                columns.add(data.getExplanatoryAttribute(i).getName());
            }

            List<List<String>> rows = new ArrayList<>();
            for (int i = 0; i < displayedRows; i++) {
                List<String> row = new ArrayList<>();
                for (int j = 0; j < numAttributes; j++) {
                    Object value = data.getValue(i, j);
                    row.add(value != null ? value.toString() : "null");
                }
                rows.add(row);
            }

            String title = String.format("Dataset: %d righe x %d colonne - visualizzazione prime %d righe",
                    data.getNumberOfExamples(), numAttributes, displayedRows);
            return StdDataView.tableView(title, new StdDataView.TableModel(columns, rows));

        } catch (Exception e) {
            logger.error("Errore durante creazione preview dataset", e);
            return StdView.text("Errore Anteprima", "Errore: " + e.getMessage());
        }
    }

    /**
     * Crea la vista con le statistiche degli attributi.
     *
     * @return vista tabellare delle statistiche
     */
    private StdView createStatsView() {
        List<List<String>> rows = new ArrayList<>();
        int numAttributes = data.getNumberOfExplanatoryAttributes();

        for (int i = 0; i < numAttributes; i++) {
            Attribute attr = data.getExplanatoryAttribute(i);
            String type = "";
            String details = "";

            if (attr instanceof ContinuousAttribute ca) {
                type = "Continuo";
                details = String.format("Min: %.2f, Max: %.2f", ca.getMin(), ca.getMax());
            } else if (attr instanceof DiscreteAttribute da) {
                type = "Discreto";
                details = String.format("%d valori distinti", da.getNumberOfDistinctValues());
            }

            rows.add(List.of(attr.getName(), type, details));
        }

        return StdDataView.tableView("Statistiche Attributi",
                new StdDataView.TableModel(List.of("Attributo", "Tipo", "Dettagli"), rows));
    }
}

//===---------------------------------------------------------------------------===//
