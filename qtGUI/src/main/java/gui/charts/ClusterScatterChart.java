package gui.charts;

import data.Data;
import data.Item;
import data.Tuple;
import gui.models.ClusteringResult;
import gui.utils.ColorPalette;
import mining.Cluster;
import mining.ClusterSet;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe per la creazione e gestione di scatter plot 2D dei cluster.
 * Utilizza XChart per la visualizzazione grafica.
 *
 * @author MAP Team
 * @version 1.0.0
 * @since Sprint 3
 */
public class ClusterScatterChart {

    private static final Logger logger = LoggerFactory.getLogger(ClusterScatterChart.class);

    private final ClusteringResult result;
    private final Data data;
    private final ClusterSet clusterSet;

    private int xAttributeIndex;
    private int yAttributeIndex;

    /**
     * Crea un gestore di scatter chart per i risultati di clustering.
     *
     * @param result risultati del clustering
     */
    public ClusterScatterChart(ClusteringResult result) {
        this.result = result;
        this.data = result.getData();
        this.clusterSet = result.getClusterSet();

        // Default: usa i primi due attributi
        this.xAttributeIndex = 0;
        this.yAttributeIndex = Math.min(1, data.getNumberOfExplanatoryAttributes() - 1);
    }

    /**
     * Imposta gli indici degli attributi da visualizzare sugli assi.
     *
     * @param xIndex indice attributo asse X
     * @param yIndex indice attributo asse Y
     */
    public void setAxes(int xIndex, int yIndex) {
        if (xIndex < 0 || xIndex >= data.getNumberOfExplanatoryAttributes()) {
            throw new IllegalArgumentException("Indice X non valido: " + xIndex);
        }
        if (yIndex < 0 || yIndex >= data.getNumberOfExplanatoryAttributes()) {
            throw new IllegalArgumentException("Indice Y non valido: " + yIndex);
        }

        this.xAttributeIndex = xIndex;
        this.yAttributeIndex = yIndex;
    }

    /**
     * Crea lo scatter plot 2D.
     *
     * @return oggetto XYChart pronto per la visualizzazione
     */
    public XYChart createChart() {
        logger.info("Creazione scatter plot con attributi X={}, Y={}",
            xAttributeIndex, yAttributeIndex);

        String xLabel = data.getExplanatoryAttribute(xAttributeIndex).getName();
        String yLabel = data.getExplanatoryAttribute(yAttributeIndex).getName();

        // Crea chart con configurazione base
        XYChart chart = new XYChartBuilder()
            .width(800)
            .height(600)
            .title("Visualizzazione Cluster (Radius: " + String.format("%.3f", result.getRadius()) + ")")
            .xAxisTitle(xLabel)
            .yAxisTitle(yLabel)
            .build();

        // Configura stile
        configureStyling(chart);

        // Aggiungi serie per ogni cluster
        List<Cluster> clusterList = new ArrayList<>();
        for (Cluster c : clusterSet) {
            clusterList.add(c);
        }

        for (int i = 0; i < clusterList.size(); i++) {
            Cluster cluster = clusterList.get(i);
            addClusterSeries(chart, cluster, i);
        }

        // Aggiungi serie per i centroidi
        addCentroidsSeries(chart, clusterList);

        logger.info("Scatter plot creato con {} cluster", clusterList.size());
        return chart;
    }

    /**
     * Configura lo stile del grafico.
     *
     * @param chart grafico da configurare
     */
    private void configureStyling(XYChart chart) {
        Styler styler = chart.getStyler();

        styler.setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        styler.setChartTitleVisible(true);
        styler.setChartBackgroundColor(Color.WHITE);
        styler.setPlotBackgroundColor(Color.WHITE);
        styler.setPlotGridLinesVisible(true);
        styler.setPlotGridLinesColor(new Color(220, 220, 220));
        styler.setLegendVisible(true);
        styler.setLegendPosition(Styler.LegendPosition.OutsideE);
        styler.setMarkerSize(8);
    }

    /**
     * Aggiunge una serie di dati per un cluster.
     *
     * @param chart grafico a cui aggiungere la serie
     * @param cluster cluster da visualizzare
     * @param clusterIndex indice del cluster
     */
    private void addClusterSeries(XYChart chart, Cluster cluster, int clusterIndex) {
        String seriesName = "Cluster " + (clusterIndex + 1);

        int[] tupleIds = cluster.getTupleIDs();
        List<Double> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();

        for (int tupleId : tupleIds) {
            Tuple tuple = data.getItemSet(tupleId);

            double x = getNumericValue(tuple.get(xAttributeIndex));
            double y = getNumericValue(tuple.get(yAttributeIndex));

            xData.add(x);
            yData.add(y);
        }

        // Aggiungi serie al grafico
        XYSeries series = chart.addSeries(seriesName, xData, yData);

        // Configura colore del cluster
        Color clusterColor = ColorPalette.getColor(clusterIndex);
        series.setMarkerColor(clusterColor);
        series.setLineColor(clusterColor);
        series.setMarker(SeriesMarkers.CIRCLE);
    }

    /**
     * Aggiunge una serie per i centroidi di tutti i cluster.
     *
     * @param chart grafico a cui aggiungere la serie
     * @param clusterList lista di cluster
     */
    private void addCentroidsSeries(XYChart chart, List<Cluster> clusterList) {
        List<Double> centroidXData = new ArrayList<>();
        List<Double> centroidYData = new ArrayList<>();

        for (Cluster cluster : clusterList) {
            Tuple centroid = cluster.getCentroid();

            double x = getNumericValue(centroid.get(xAttributeIndex));
            double y = getNumericValue(centroid.get(yAttributeIndex));

            centroidXData.add(x);
            centroidYData.add(y);
        }

        // Aggiungi serie centroidi
        XYSeries centroidSeries = chart.addSeries("Centroidi", centroidXData, centroidYData);
        centroidSeries.setMarkerColor(Color.BLACK);
        centroidSeries.setLineColor(Color.BLACK);
        centroidSeries.setMarker(SeriesMarkers.CROSS); // Marker diverso per centroidi
        centroidSeries.setMarkerSize(12); // Più grandi
    }

    /**
     * Converte un Item in valore numerico per la visualizzazione.
     * Per attributi discreti, usa un mapping ordinale.
     *
     * @param item item da convertire
     * @return valore numerico
     */
    private double getNumericValue(Item item) {
        Object value = item.getValue();

        // Se è già un numero, usalo direttamente
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        // Se è una stringa, usa hashCode come valore ordinale
        // Nota: questo è un approccio semplificato. Per attributi discreti
        // sarebbe meglio usare un mapping esplicito basato sui valori distinti
        if (value instanceof String) {
            String str = (String) value;
            // Usa un hash deterministico per consistenza
            return Math.abs(str.hashCode() % 1000) / 10.0;
        }

        logger.warn("Tipo di valore non supportato: {}, uso default 0.0", value.getClass());
        return 0.0;
    }

    /**
     * Salva il grafico come immagine PNG.
     *
     * @param outputFile file di output
     * @throws IOException se si verifica un errore durante il salvataggio
     */
    public void saveAsPNG(File outputFile) throws IOException {
        XYChart chart = createChart();
        BitmapEncoder.saveBitmap(chart, outputFile.getAbsolutePath(), BitmapEncoder.BitmapFormat.PNG);
        logger.info("Grafico salvato in: {}", outputFile.getAbsolutePath());
    }

    /**
     * Salva il grafico come immagine PNG con dimensioni personalizzate.
     *
     * @param outputFile file di output
     * @param width larghezza in pixel
     * @param height altezza in pixel
     * @throws IOException se si verifica un errore durante il salvataggio
     */
    public void saveAsPNG(File outputFile, int width, int height) throws IOException {
        XYChart chart = createChart();
        chart.setWidth(width);
        chart.setHeight(height);

        BitmapEncoder.saveBitmap(chart, outputFile.getAbsolutePath(), BitmapEncoder.BitmapFormat.PNG);
        logger.info("Grafico salvato in: {} ({}x{})", outputFile.getAbsolutePath(), width, height);
    }

    /**
     * Restituisce i nomi degli attributi disponibili per la visualizzazione.
     *
     * @return array di nomi attributi
     */
    public String[] getAttributeNames() {
        int numAttributes = data.getNumberOfExplanatoryAttributes();
        String[] names = new String[numAttributes];

        for (int i = 0; i < numAttributes; i++) {
            names[i] = data.getExplanatoryAttribute(i).getName();
        }

        return names;
    }

    /**
     * @return indice dell'attributo corrente sull'asse X
     */
    public int getXAttributeIndex() {
        return xAttributeIndex;
    }

    /**
     * @return indice dell'attributo corrente sull'asse Y
     */
    public int getYAttributeIndex() {
        return yAttributeIndex;
    }
}
