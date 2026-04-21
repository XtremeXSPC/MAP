package gui.charts;

//===---------------------------------------------------------------------------===//
// Importazioni Java standard.
import com.map.stdgui.StdDialog;
import com.map.stdgui.StdFileDialog;
import com.map.stdgui.StdSwingView;
import com.map.stdgui.StdToolWindow;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Path;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gui.models.ClusteringResult;
//===---------------------------------------------------------------------------===//

/**
 * Finestra di visualizzazione per scatter plot 2D dei cluster.
 * <p>
 * Questa classe gestisce:
 * <ul>
 *   <li>Creazione di una finestra dedicata tramite StdToolWindow</li>
 *   <li>Selezione degli assi e aggiornamento del grafico</li>
 *   <li>Toggle della modalita' Convex Hull per i cluster</li>
 *   <li>Integrazione di XChart tramite StdSwingView</li>
 *   <li>Esportazione del grafico in PNG (standard e HD)</li>
 * </ul>
 * <p>
 * La generazione del grafico e' delegata a {@link ClusterScatterChart}.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.0.0
 * @see ClusterScatterChart
 */
public class ChartViewer {

    //===---------------------------- DATA MEMBERS -----------------------------===//

    // Logger per la classe ChartViewer.
    private static final Logger logger = LoggerFactory.getLogger(ChartViewer.class);

    // Risultati del clustering da visualizzare.
    private final ClusteringResult result;
    private final ClusterScatterChart chartManager;
    private final StdSwingView chartView;
    private final StdToolWindow window;

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Crea una finestra di visualizzazione per i risultati di clustering.
     * <p>
     * L'interfaccia viene inizializzata immediatamente, ma la finestra
     * viene mostrata solo quando viene chiamato {@link #show()}.
     *
     * @param result risultati del clustering da visualizzare
     */
    public ChartViewer(ClusteringResult result) {
        this.result = result;
        this.chartManager = new ClusterScatterChart(result);
        this.chartView = StdSwingView.create("cluster-scatter-chart");
        this.window = createWindow();

        updateChart();
    }

    //===-------------------------- UI INITIALIZATION --------------------------===//

    /**
     * Crea la finestra e configura controlli, contenuto centrale e footer.
     *
     * @return finestra di visualizzazione
     */
    private StdToolWindow createWindow() {
        List<String> attributeNames = Arrays.asList(chartManager.getAttributeNames());
        String footerText = String.format("Cluster: %d | Tuple: %d | Radius: %.3f", result.getNumClusters(),
                result.getNumTuples(), result.getRadius());

        logger.info("ChartViewer inizializzato");
        return StdToolWindow.create("Visualizzazione Cluster 2D", "Seleziona Attributi da Visualizzare:",
                List.of(
                        new StdToolWindow.Choice("Asse X:", attributeNames, chartManager.getXAttributeIndex(),
                                this::onXAxisChanged),
                        new StdToolWindow.Choice("Asse Y:", attributeNames, chartManager.getYAttributeIndex(),
                                this::onYAxisChanged)),
                List.of(new StdToolWindow.Toggle("Convex Hull", true, enabled -> {
                    chartManager.setConvexHullMode(enabled);
                    updateChart();
                })),
                List.of(new StdToolWindow.Action("Aggiorna Grafico", this::updateChart)), chartView.view(), footerText,
                List.of(new StdToolWindow.Action("Esporta PNG", this::handleExport),
                        new StdToolWindow.Action("Esporta PNG HD", this::handleExportHD)),
                "Chiudi", 900, 700);
    }

    //===--------------------------- EVENT HANDLERS ----------------------------===//

    /**
     * Gestisce il cambio di selezione dell'asse X.
     * <p>
     * Aggiorna gli indici degli assi nel chart manager. L'aggiornamento
     * del grafico avviene tramite il pulsante di refresh o altri trigger.
     *
     * @param xIndex nuovo indice asse X
     */
    private void onXAxisChanged(int xIndex) {
        setAxes(xIndex, chartManager.getYAttributeIndex());
    }

    /**
     * Gestisce il cambio di selezione dell'asse Y.
     *
     * @param yIndex nuovo indice asse Y
     */
    private void onYAxisChanged(int yIndex) {
        setAxes(chartManager.getXAttributeIndex(), yIndex);
    }

    /**
     * Imposta gli assi del grafico quando gli indici sono validi.
     *
     * @param xIndex indice asse X
     * @param yIndex indice asse Y
     */
    private void setAxes(int xIndex, int yIndex) {
        if (xIndex < 0 || yIndex < 0) {
            return;
        }

        chartManager.setAxes(xIndex, yIndex);
        logger.info("Assi cambiati: X={}, Y={}", xIndex, yIndex);
    }

    /**
     * Rigenera il grafico e lo visualizza nel contenitore Swing.
     */
    private void updateChart() {
        try {
            XYChart chart = chartManager.createChart();
            chartView.setContent(() -> new XChartPanel<>(chart));

            logger.info("Grafico aggiornato");

        } catch (Exception e) {
            logger.error("Errore durante creazione grafico", e);
            showError("Errore Visualizzazione", "Impossibile creare il grafico: " + e.getMessage());
        }
    }

    /**
     * Esporta il grafico in PNG a risoluzione standard.
     */
    private void handleExport() {
        exportChart(800, 600);
    }

    /**
     * Esporta il grafico in PNG ad alta risoluzione (HD).
     */
    private void handleExportHD() {
        exportChart(1920, 1080);
    }


    //===--------------------------- UTILITY METHODS ---------------------------===//

    /**
     * Esporta il grafico in PNG con dimensioni specificate.
     *
     * @param width larghezza in pixel
     * @param height altezza in pixel
     */
    private void exportChart(int width, int height) {
        Path selectedPath = StdFileDialog.saveFile("Salva Grafico", "cluster_chart.png",
                new StdFileDialog.Filter("Immagini PNG", "*.png")).orElse(null);
        File file = selectedPath == null ? null : selectedPath.toFile();
        if (file != null) {
            try {
                chartManager.saveAsPNG(file, width, height);

                StdDialog.info("Esportazione Completata", "Grafico salvato con successo:\n" + file.getAbsolutePath());

                logger.info("Grafico esportato: {}", file.getAbsolutePath());

            } catch (IOException e) {
                logger.error("Errore durante esportazione grafico", e);
                showError("Errore Esportazione", "Impossibile salvare il grafico: " + e.getMessage());
            }
        }
    }

    /**
     * Mostra un dialogo di errore con titolo e messaggio specificati.
     *
     * @param title titolo del dialogo
     * @param message messaggio di errore da visualizzare
     */
    private void showError(String title, String message) {
        StdDialog.error(title, message);
    }

    /**
     * Mostra la finestra di visualizzazione.
     */
    public void show() {
        window.show();
        logger.info("ChartViewer mostrato");
    }
}

//===---------------------------------------------------------------------------===//
