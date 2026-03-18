package gui.charts;

//===---------------------------------------------------------------------------===//
// Importazioni Java standard.
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// Importazioni progetto.
import data.Data;
import data.Item;
import data.Tuple;
import gui.models.ClusteringResult;
import gui.utils.ColorPalette;
import gui.utils.Point2D;
import mining.Cluster;
import mining.ClusterSet;
//===---------------------------------------------------------------------------===//

/**
 * Gestore per la creazione di "scatter plot 2D" dei cluster.
 * <p>
 * Questa classe gestisce:
 * <ul>
 *   <li>Creazione del grafico XChart con titolo, assi e serie</li>
 *   <li>Render delle serie per cluster e centroidi</li>
 *   <li>Modalita' Convex Hull per l'inviluppo dei cluster</li>
 *   <li>Mapping ordinale per attributi discreti</li>
 *   <li>Esportazione del grafico in PNG</li>
 * </ul>
 * <p>
 * Nota: per attributi discreti viene creato un mapping stabile per indice di
 * attributo, cosi' da mantenere la stessa codifica numerica tra grafici.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.0.0
 * @see ChartViewer
 */
public class ClusterScatterChart {

    //===---------------------------- DATA MEMBERS -----------------------------===//

    // Logger per la classe ClusterScatterChart.
    private static final Logger logger = LoggerFactory.getLogger(ClusterScatterChart.class);

    //
    // Risultati del clustering da visualizzare.
    private final ClusteringResult result;
    private final Data data;
    private final ClusterSet clusterSet;
    private final Map<Integer, Map<String, Integer>> discreteMappings = new HashMap<>();

    // Indici degli attributi da visualizzare sugli assi.
    private int xAttributeIndex;
    private int yAttributeIndex;
    private boolean convexHullMode = true; // default: Convex Hull.

    //===--------------------------- PUBLIC METHODS ----------------------------===//

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
     * Restituisce l'indice dell'attributo attualmente visualizzato sull'asse X.
     *
     * @return indice dell'attributo corrente sull'asse X
     */
    public int getXAttributeIndex() {
        return xAttributeIndex;
    }

    /**
     * Restituisce l'indice dell'attributo attualmente visualizzato sull'asse Y.
     *
     * @return indice dell'attributo corrente sull'asse Y
     */
    public int getYAttributeIndex() {
        return yAttributeIndex;
    }

    /**
     * Imposta la modalita' di visualizzazione Convex Hull.
     *
     * @param enabled true per abilitare il Convex Hull, false per stile classico
     */
    public void setConvexHullMode(boolean enabled) {
        this.convexHullMode = enabled;
    }

    /**
     * Imposta gli indici degli attributi da visualizzare sugli assi.
     * <p>
     * Valida che gli indici siano coerenti con il numero di attributi del dataset.
     *
     * @param xIndex indice attributo asse X
     * @param yIndex indice attributo asse Y
     * @throws IllegalArgumentException se uno dei due indici non e' valido
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
     * Crea lo scatter plot 2D con le serie dei cluster e dei centroidi.
     * <p>
     * Il grafico include:
     * <ul>
     *   <li>Una serie per ogni cluster</li>
     *   <li>Una serie dei centroidi</li>
     *   <li>Eventuale overlay Convex Hull</li>
     * </ul>
     *
     * @return oggetto {@link XYChart} pronto per la visualizzazione
     */
    @SuppressWarnings("exports")
    public XYChart createChart() {
        logger.info("Creazione scatter plot con attributi X={}, Y={}", xAttributeIndex, yAttributeIndex);

        String xLabel = data.getExplanatoryAttribute(xAttributeIndex).getName();
        String yLabel = data.getExplanatoryAttribute(yAttributeIndex).getName();

        // Crea chart con configurazione base.
        XYChart chart = new XYChartBuilder().width(800).height(600)
                .title("Visualizzazione Cluster (Radius: " + String.format("%.3f", result.getRadius()) + ")")
                .xAxisTitle(xLabel).yAxisTitle(yLabel).build();

        // Configura stile.
        configureStyling(chart);

        // Aggiungi serie per ogni cluster.
        List<Cluster> clusterList = new ArrayList<>();
        for (Cluster c : clusterSet) {
            clusterList.add(c);
        }

        for (int i = 0; i < clusterList.size(); i++) {
            Cluster cluster = clusterList.get(i);

            // Prima disegna convex hull (se abilitato), poi punti.
            if (convexHullMode) {
                addConvexHullSeries(chart, cluster, i);
            }

            addClusterSeries(chart, cluster, i);
        }

        // Aggiungi serie per i centroidi (in primo piano).
        addCentroidsSeries(chart, clusterList);

        logger.info("Scatter plot creato con {} cluster", clusterList.size());
        return chart;
    }

    /**
     * Crea un gestore di scatter chart a partire dai risultati di clustering.
     * <p>
     * Per default, imposta gli assi sui primi due attributi disponibili.
     *
     * @param result risultati del clustering
     */
    public ClusterScatterChart(ClusteringResult result) {
        this.result = result;
        this.data = result.getData();
        this.clusterSet = result.getClusterSet();

        // Default: usa i primi due attributi.
        this.xAttributeIndex = 0;
        this.yAttributeIndex = Math.min(1, data.getNumberOfExplanatoryAttributes() - 1);
    }

    /**
     * Salva il grafico corrente come immagine PNG.
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
     * <p>
     * Questa variante ricostruisce il grafico con le dimensioni specificate
     * prima di esportare.
     *
     * @param outputFile file di output
     * @param width larghezza in pixel
     * @param height altezza in pixel
     * @throws IOException se si verifica un errore durante il salvataggio
     */
    public void saveAsPNG(File outputFile, int width, int height) throws IOException {
        String xLabel = data.getExplanatoryAttribute(xAttributeIndex).getName();
        String yLabel = data.getExplanatoryAttribute(yAttributeIndex).getName();

        // Create chart with base configuration and specified dimensions.
        XYChart chart = new XYChartBuilder().width(width).height(height)
                .title("Visualizzazione Cluster (Radius: " + String.format("%.3f", result.getRadius()) + ")")
                .xAxisTitle(xLabel).yAxisTitle(yLabel).build();

        // Configure styling.
        configureStyling(chart);

        // Add series for each cluster.
        List<Cluster> clusterList = new ArrayList<>();
        for (Cluster c : clusterSet) {
            clusterList.add(c);
        }

        for (int i = 0; i < clusterList.size(); i++) {
            Cluster cluster = clusterList.get(i);

            // Prima disegna convex hull (se abilitato), poi punti.
            if (convexHullMode) {
                addConvexHullSeries(chart, cluster, i);
            }

            addClusterSeries(chart, cluster, i);
        }

        // Add series for centroids.
        addCentroidsSeries(chart, clusterList);

        BitmapEncoder.saveBitmap(chart, outputFile.getAbsolutePath(), BitmapEncoder.BitmapFormat.PNG);
        logger.info("Grafico salvato in: {} ({}x{})", outputFile.getAbsolutePath(), width, height);
    }

    //===--------------------------- PRIVATE METHODS ---------------------------===//

    /**
     * Configura lo stile del grafico (legenda, marker, posizione).
     *
     * @param chart grafico da configurare
     */
    private void configureStyling(XYChart chart) {
        Styler styler = chart.getStyler();

        styler.setLegendVisible(true);
        styler.setLegendPosition(Styler.LegendPosition.OutsideE);
        styler.setMarkerSize(8);
    }

    /**
     * Converte un {@link Item} in valore numerico per la visualizzazione.
     * <p>
     * Per attributi discreti, usa un mapping ordinale per indice di attributo
     * in modo da mantenere una codifica coerente tra chiamate.
     *
     * @param item item da convertire
     * @param attributeIndex indice attributo di riferimento
     * @return valore numerico
     */
    private double getNumericValue(Item item, int attributeIndex) {
        Object value = item.getValue();

        // Se è già un numero, usalo direttamente.
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        // Se è una stringa, usa un mapping ordinale stabile per attributo.
        if (value instanceof String) {
            String str = (String) value;
            Map<String, Integer> mapping =
                    discreteMappings.computeIfAbsent(attributeIndex, key -> new LinkedHashMap<>());
            Integer mapped = mapping.computeIfAbsent(str, key -> mapping.size());
            return mapped.doubleValue();
        }

        logger.warn("Tipo di valore non supportato: {}, uso default 0.0", value.getClass());
        return 0.0;
    }

    //===------------------------- ADD SERIES METHODS --------------------------===//

    /**
     * Aggiunge una serie di punti per un cluster specifico.
     * <p>
     * I punti vengono trasformati in coordinate numeriche, con mapping
     * ordinale per attributi discreti.
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

            double x = getNumericValue(tuple.get(xAttributeIndex), xAttributeIndex);
            double y = getNumericValue(tuple.get(yAttributeIndex), yAttributeIndex);

            xData.add(x);
            yData.add(y);
        }

        // Aggiungi serie al grafico.
        XYSeries series = chart.addSeries(seriesName, xData, yData);

        // Configura colore del cluster.
        Color clusterColor = ColorPalette.getColor(clusterIndex);
        series.setMarkerColor(clusterColor);
        series.setLineColor(clusterColor);
        series.setMarker(SeriesMarkers.CIRCLE);

        // In modalità convex hull, rimuovi linee tra punti.
        if (convexHullMode) {
            series.setLineStyle(SeriesLines.NONE);
        }
    }

    /**
     * Aggiunge una serie per i centroidi di tutti i cluster.
     * <p>
     * I centroidi sono disegnati con marker distintivo.
     *
     * @param chart grafico a cui aggiungere la serie
     * @param clusterList lista di cluster
     */
    private void addCentroidsSeries(XYChart chart, List<Cluster> clusterList) {
        List<Double> centroidXData = new ArrayList<>();
        List<Double> centroidYData = new ArrayList<>();

        for (Cluster cluster : clusterList) {
            Tuple centroid = cluster.getCentroid();

            double x = getNumericValue(centroid.get(xAttributeIndex), xAttributeIndex);
            double y = getNumericValue(centroid.get(yAttributeIndex), yAttributeIndex);

            centroidXData.add(x);
            centroidYData.add(y);
        }

        // Aggiungi serie centroidi.
        XYSeries centroidSeries = chart.addSeries("Centroidi", centroidXData, centroidYData);
        centroidSeries.setMarkerColor(Color.BLACK);
        centroidSeries.setLineColor(Color.BLACK);
        centroidSeries.setMarker(SeriesMarkers.CROSS); // Marker diverso per centroidi.
        centroidSeries.setLineStyle(SeriesLines.NONE); // Rimuovi linee tra centroidi.
    }

    /**
     * Aggiunge una serie per visualizzare il Convex Hull di un cluster.
     * <p>
     * Il Convex Hull (inviluppo convesso) e' la piu' piccola regione convessa
     * che contiene tutti i punti del cluster. Viene disegnato come un poligono
     * chiuso che delimita l'area del cluster.
     * <p>
     * Se il cluster ha meno di 3 punti, l'inviluppo non puo' essere calcolato
     * e nessuna serie viene aggiunta.
     *
     * @param chart grafico a cui aggiungere la serie
     * @param cluster cluster da visualizzare
     * @param clusterIndex indice del cluster
     */
    private void addConvexHullSeries(XYChart chart, Cluster cluster, int clusterIndex) {
        String seriesName = "Hull " + (clusterIndex + 1);

        int[] tupleIds = cluster.getTupleIDs();

        // Converti tuple in Point2D.
        List<Point2D> points = new ArrayList<>();
        for (int tupleId : tupleIds) {
            Tuple tuple = data.getItemSet(tupleId);
            double x = getNumericValue(tuple.get(xAttributeIndex), xAttributeIndex);
            double y = getNumericValue(tuple.get(yAttributeIndex), yAttributeIndex);
            points.add(new Point2D(x, y));
        }

        // Calcola convex hull (gestisci caso < 3 punti).
        if (points.size() < 3) {
            // Se 1-2 punti, non disegnare hull.
            logger.debug("Cluster {} ha solo {} punti, skip convex hull", clusterIndex + 1, points.size());
            return;
        }

        try {
            List<Point2D> hull = ConvexHullCalculator.grahamScan(points);

            // Chiudi poligono (aggiungi primo punto alla fine).
            hull.add(hull.get(0));

            // Estrai coordinate per XChart.
            List<Double> hullX = new ArrayList<>();
            List<Double> hullY = new ArrayList<>();
            for (Point2D p : hull) {
                hullX.add(p.getX());
                hullY.add(p.getY());
            }

            // Aggiungi serie hull al grafico.
            XYSeries hullSeries = chart.addSeries(seriesName, hullX, hullY);

            // Configura stile.
            Color clusterColor = ColorPalette.getColor(clusterIndex);

            // NOTA: XChart Area rendering richiede coordinate X in ordine crescente,
            // ma i punti del convex hull sono in ordine antiorario (non ordinati per X).
            // Per ora, usiamo solo il bordo (Line style) senza riempimento.

            // POSSIBILE MIGLIORIA: Implementare riempimento custom usando Java2D Graphics2D.
            hullSeries.setLineColor(clusterColor);
            hullSeries.setLineWidth(2.0f);
            hullSeries.setMarker(SeriesMarkers.NONE); // No marker sui vertici hull.
            hullSeries.setShowInLegend(false); // Nascondi da legenda.

            logger.debug("Convex hull aggiunto per cluster {}: {} vertici", clusterIndex + 1, hull.size() - 1);

        } catch (IllegalArgumentException e) {
            logger.warn("Impossibile calcolare convex hull per cluster {}: {}", clusterIndex + 1, e.getMessage());
        }
    }
}

//===---------------------------------------------------------------------------===//
