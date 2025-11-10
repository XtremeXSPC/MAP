# Piano di Implementazione: Convex Hull Plotting + Dataset Standard

> **Progetto:** Quality Threshold Clustering Algorithm (MAP)
> **Data creazione:** 2025-11-10
> **Versione:** 1.0.0
> **Autore:** Claude + Lombardi Costantino

---

## Indice

1. [Panoramica Generale](#panoramica-generale)
2. [Obiettivi](#obiettivi)
3. [Architettura Proposta](#architettura-proposta)
4. [Fase 1: Dataset Standard](#fase-1-dataset-standard)
5. [Fase 2: Algoritmo Convex Hull](#fase-2-algoritmo-convex-hull)
6. [Fase 3: Integrazione Plotting](#fase-3-integrazione-plotting)
7. [Fase 4: Testing e Validazione](#fase-4-testing-e-validazione)
8. [Fase 5: Documentazione](#fase-5-documentazione)
9. [Rollback e Compatibilità](#rollback-e-compatibilità)
10. [Timeline e Priorità](#timeline-e-priorità)

---

## Panoramica Generale

### Problema Attuale

Il sistema di visualizzazione cluster presenta le seguenti limitazioni:

1. **Plotting inefficace:**
   - Linee tra punti creano confusione visiva
   - Difficile intuire distribuzione e densità cluster
   - Illeggibile con dataset > 50 punti

2. **Dataset limitato:**
   - Solo PlayTennis (14 tuple, 5 attributi discreti)
   - Non rappresentativo per validare algoritmo QT
   - Impossibile testare con attributi continui

### Soluzione Proposta

1. **Convex Hull Plotting:**
   - Visualizzazione insiemistica (area cluster)
   - Riempimento semi-trasparente
   - Punti visibili all'interno
   - Centroidi evidenziati

2. **Dataset Standard UCI:**
   - Iris Dataset (150 tuple, 4 continui, 3 cluster)
   - Benchmark riconosciuto per clustering
   - Supporto attributi continui
   - Validazione algoritmo QT

---

## Obiettivi

### Obiettivi Primari

1. Implementare algoritmo **Convex Hull** (Graham Scan)
2. Integrare convex hull in visualizzazione XChart
3. Aggiungere **Iris Dataset** come dataset di test
4. Validare correttezza algoritmo QT su dataset standard

### Obiettivi Secondari

1. Mantenere backward compatibility con plotting attuale
2. Documentare nuove classi e metodi
3. Aggiungere test unitari (futuro)
4. Creare esempi d'uso

### Non-Obiettivi (fuori scope)

- Altri dataset UCI (Wine, Seeds) → Fase successiva
- 3D plotting → Futuro
- Clustering gerarchico → Futuro
- Ottimizzazioni performance algoritmo QT → Futuro

---

## Architettura Proposta

### Nuove Classi da Creare

```
qtGUI/src/main/java/
├── gui/
│   ├── charts/
│   │   ├── ClusterScatterChart.java         [MODIFICARE]
│   │   └── ConvexHullCalculator.java        [NUOVO]
│   └── utils/
│       └── Point2D.java                      [NUOVO]
│
└── resources/
    └── datasets/
        └── iris.csv                          [NUOVO]
```

### Diagramma Classi Aggiornato

```
┌─────────────────────────┐
│  ClusterScatterChart    │
│─────────────────────────│
│ - result                │
│ - data                  │
│ - clusterSet            │
│ - xAttributeIndex       │
│ - yAttributeIndex       │
│─────────────────────────│
│ + createChart()         │
│ + setAxes(int, int)     │
│ - addClusterSeries()    │ [MODIFICARE]
│ - addConvexHullSeries() │ [NUOVO]
│ - addCentroidsSeries()  │
└────────────┬────────────┘
             │ usa
             ▼
   ┌──────────────────────┐
   │ ConvexHullCalculator │
   │──────────────────────│
   │ + grahamScan()       │ [NUOVO]
   │ - polarAngle()       │ [NUOVO]
   │ - ccw()              │ [NUOVO]
   └──────────────────────┘
             │ usa
             ▼
        ┌──────────┐
        │ Point2D  │
        │──────────│
        │ + x      │
        │ + y      │
        │──────────│
        │ + dist() │
        └──────────┘
```

---

## Fase 1: Dataset Standard

### Obiettivo

Aggiungere Iris Dataset come dataset di test per validare algoritmo QT.

### Step 1.1: Preparazione File CSV

**Task:** Creare file `qtGUI/src/main/resources/datasets/iris.csv`

**Formato CSV:**

```csv
sepal_length,sepal_width,petal_length,petal_width,species
5.1,3.5,1.4,0.2,setosa
4.9,3.0,1.4,0.2,setosa
...
```

**Caratteristiche:**

- 150 righe (tuple)
- 5 colonne (4 attributi continui + 1 target discreto)
- 3 cluster reali (setosa: 50, versicolor: 50, virginica: 50)

**Sorgente:** UCI Machine Learning Repository

- URL: <https://archive.ics.uci.edu/dataset/53/iris>
- Licenza: CC BY 4.0

**File da creare:**

```
/Volumes/LCS.Data/MAP/qtGUI/src/main/resources/datasets/iris.csv
```

**Checklist:**

- [ ] Creare directory `resources/datasets/`
- [ ] Scaricare/generare iris.csv
- [ ] Validare formato CSV (header + 150 righe)
- [ ] Testare parsing con `Data(String csvPath)`

### Step 1.2: Classe DatasetLoader Helper

**Task:** Creare utility per caricare dataset da resources

**File:** `qtGUI/src/main/java/gui/utils/DatasetLoader.java`

```java
package gui.utils;

import data.Data;
import data.InvalidDataFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Utility per caricare dataset standard da resources.
 */
public class DatasetLoader {

    /**
     * Carica Iris dataset da resources.
     * @return Data object con Iris dataset
     * @throws IOException se file non trovato o errore I/O
     * @throws InvalidDataFormatException se formato CSV invalido
     */
    public static Data loadIrisDataset() throws IOException, InvalidDataFormatException {
        // Copia file da resources a file temporaneo
        InputStream resourceStream = DatasetLoader.class
            .getResourceAsStream("/datasets/iris.csv");

        if (resourceStream == null) {
            throw new IOException("Iris dataset non trovato in resources");
        }

        // Crea file temporaneo
        Path tempFile = Files.createTempFile("iris", ".csv");
        Files.copy(resourceStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

        // Carica dataset
        Data data = new Data(tempFile.toString());

        // Cleanup
        Files.deleteIfExists(tempFile);

        return data;
    }

    /**
     * Lista dataset disponibili.
     * @return array di nomi dataset
     */
    public static String[] getAvailableDatasets() {
        return new String[]{"Iris", "PlayTennis (Hardcoded)"};
    }
}
```

**Checklist:**

- [ ] Creare classe `DatasetLoader`
- [ ] Implementare `loadIrisDataset()`
- [ ] Gestire IOException per resource mancante
- [ ] Testare caricamento dataset

### Step 1.3: Testing Dataset

**Task:** Validare che Iris dataset funzioni con algoritmo QT

**Test manuale:**

1. Caricare Iris via CSV import
2. Eseguire clustering con radius = 0.3
3. Verificare numero cluster (atteso: 2-4)
4. Controllare che centroidi siano calcolati correttamente

**Output atteso:**

```
Dataset caricato: 150 tuple, 4 attributi
Cluster trovati: 3-4 (dipende da radius)
Tempo esecuzione: < 500ms
```

**Checklist:**

- [ ] Test import CSV Iris
- [ ] Test clustering QT su Iris
- [ ] Verificare correttezza centroidi
- [ ] Log performance tempo esecuzione

### Step 1.4: Aggiornamento UI HomeController

**Task:** Aggiungere opzione "Dataset Standard" in UI

**File:** `qtGUI/src/main/java/gui/controllers/HomeController.java`

**Modifica ComboBox sorgenti dati:**

```java
dataSourceComboBox.getItems().addAll(
    "Dataset Hardcoded (PlayTennis)",
    "Dataset Standard (Iris)",      // [NUOVO]
    "File CSV Personalizzato",
    "Database MySQL"
);
```

**Gestione caricamento:**

```java
case "Dataset Standard (Iris)":
    data = DatasetLoader.loadIrisDataset();
    break;
```

**Checklist:**

- [ ] Aggiungere voce "Dataset Standard (Iris)" in ComboBox
- [ ] Modificare `handleDataSourceChange()`
- [ ] Aggiornare validazione form
- [ ] Testare in UI

### Documentazione Fase 1

**File da aggiornare:**

- [ ] `README.md` - Sezione "Dataset Supportati"
- [ ] `CLAUDE.md` - Aggiungere Iris in "Dataset"
- [ ] Javadoc classi modificate

---

## Fase 2: Algoritmo Convex Hull

### Obiettivo

Implementare algoritmo Graham Scan per calcolare inviluppo convesso di punti 2D.

### Step 2.1: Classe Point2D

**Task:** Creare classe per punti 2D con operazioni geometriche

**File:** `qtGUI/src/main/java/gui/utils/Point2D.java`

```java
package gui.utils;

/**
 * Rappresenta un punto in spazio 2D con operazioni geometriche.
 */
public class Point2D {
    private final double x;
    private final double y;

    /**
     * Costruttore.
     * @param x coordinata X
     * @param y coordinata Y
     */
    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    /**
     * Calcola distanza euclidea da altro punto.
     * @param other altro punto
     * @return distanza
     */
    public double distanceTo(Point2D other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calcola angolo polare rispetto a punto di riferimento.
     * @param pivot punto pivot
     * @return angolo in radianti [-π, π]
     */
    public double polarAngleFrom(Point2D pivot) {
        return Math.atan2(y - pivot.y, x - pivot.x);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point2D)) return false;
        Point2D other = (Point2D) obj;
        return Double.compare(x, other.x) == 0 &&
               Double.compare(y, other.y) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(x) * 31 + Double.hashCode(y);
    }
}
```

**Checklist:**

- [ ] Creare classe `Point2D`
- [ ] Implementare costruttore e getter
- [ ] Implementare `distanceTo()`
- [ ] Implementare `polarAngleFrom()`
- [ ] Override `equals()`, `hashCode()`, `toString()`
- [ ] Javadoc completo

### Step 2.2: Classe ConvexHullCalculator

**Task:** Implementare algoritmo Graham Scan

**File:** `qtGUI/src/main/java/gui/charts/ConvexHullCalculator.java`

```java
package gui.charts;

import gui.utils.Point2D;
import java.util.*;

/**
 * Calcola l'inviluppo convesso (convex hull) di un insieme di punti 2D
 * usando l'algoritmo Graham Scan.
 *
 * Complessità: O(n log n)
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 */
public class ConvexHullCalculator {

    /**
     * Calcola convex hull usando algoritmo Graham Scan.
     *
     * @param points lista di punti (minimo 3)
     * @return lista ordinata di punti che formano convex hull (senso antiorario)
     * @throws IllegalArgumentException se points ha meno di 3 elementi
     */
    public static List<Point2D> grahamScan(List<Point2D> points) {
        if (points == null || points.size() < 3) {
            throw new IllegalArgumentException(
                "Convex hull richiede almeno 3 punti"
            );
        }

        // 1. Trova punto con Y minima (pivot)
        Point2D pivot = findLowestPoint(points);

        // 2. Ordina punti per angolo polare rispetto al pivot
        List<Point2D> sorted = new ArrayList<>(points);
        sorted.sort((p1, p2) -> {
            if (p1.equals(pivot)) return -1;
            if (p2.equals(pivot)) return 1;

            double angle1 = p1.polarAngleFrom(pivot);
            double angle2 = p2.polarAngleFrom(pivot);

            int angleCompare = Double.compare(angle1, angle2);
            if (angleCompare != 0) return angleCompare;

            // Se stesso angolo, prendi il più vicino
            return Double.compare(
                pivot.distanceTo(p1),
                pivot.distanceTo(p2)
            );
        });

        // 3. Graham Scan: costruisci hull usando stack
        Deque<Point2D> hull = new ArrayDeque<>();
        hull.push(sorted.get(0));
        hull.push(sorted.get(1));

        for (int i = 2; i < sorted.size(); i++) {
            Point2D top = hull.pop();

            // Rimuovi punti che creano svolta a destra (clockwise)
            while (!hull.isEmpty() &&
                   ccw(hull.peek(), top, sorted.get(i)) <= 0) {
                top = hull.pop();
            }

            hull.push(top);
            hull.push(sorted.get(i));
        }

        return new ArrayList<>(hull);
    }

    /**
     * Trova punto con coordinata Y minima (più in basso).
     * In caso di pareggio, sceglie quello con X minima.
     *
     * @param points lista di punti
     * @return punto più basso
     */
    private static Point2D findLowestPoint(List<Point2D> points) {
        Point2D lowest = points.get(0);

        for (Point2D p : points) {
            if (p.getY() < lowest.getY() ||
                (p.getY() == lowest.getY() && p.getX() < lowest.getX())) {
                lowest = p;
            }
        }

        return lowest;
    }

    /**
     * Test orientazione di tre punti (Counter-Clockwise test).
     *
     * @param p1 primo punto
     * @param p2 secondo punto
     * @param p3 terzo punto
     * @return > 0 se svolta antioraria (CCW),
     *         < 0 se svolta oraria (CW),
     *         = 0 se collineari
     */
    private static double ccw(Point2D p1, Point2D p2, Point2D p3) {
        return (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) -
               (p2.getY() - p1.getY()) * (p3.getX() - p1.getX());
    }
}
```

**Checklist:**

- [ ] Creare classe `ConvexHullCalculator`
- [ ] Implementare `grahamScan()`
- [ ] Implementare `findLowestPoint()`
- [ ] Implementare `ccw()` (counter-clockwise test)
- [ ] Gestire caso < 3 punti (IllegalArgumentException)
- [ ] Gestire punti collineari
- [ ] Javadoc completo con esempio

### Step 2.3: Testing Convex Hull

**Task:** Validare correttezza algoritmo Graham Scan

**Test Case 1: Quadrato**

```java
List<Point2D> square = Arrays.asList(
    new Point2D(0, 0),
    new Point2D(1, 0),
    new Point2D(1, 1),
    new Point2D(0, 1),
    new Point2D(0.5, 0.5)  // punto interno
);

List<Point2D> hull = ConvexHullCalculator.grahamScan(square);
// Atteso: 4 punti (vertici quadrato)
```

**Test Case 2: Triangolo**

```java
List<Point2D> triangle = Arrays.asList(
    new Point2D(0, 0),
    new Point2D(2, 0),
    new Point2D(1, 2)
);

List<Point2D> hull = ConvexHullCalculator.grahamScan(triangle);
// Atteso: 3 punti (vertici triangolo)
```

**Test Case 3: Punti collineari**

```java
List<Point2D> line = Arrays.asList(
    new Point2D(0, 0),
    new Point2D(1, 1),
    new Point2D(2, 2)
);

// Atteso: Gestione corretta (hull = tutti i punti o solo estremi)
```

**Checklist:**

- [ ] Test quadrato (5 punti → 4 hull)
- [ ] Test triangolo (3 punti → 3 hull)
- [ ] Test linea collineare
- [ ] Test caso limite (3 punti esatti)
- [ ] Verifica ordine punti (antiorario)

### Documentazione Fase 2

**File da aggiornare:**

- [ ] Javadoc `ConvexHullCalculator`
- [ ] Javadoc `Point2D`
- [ ] Commenti inline per algoritmo Graham Scan

---

## Fase 3: Integrazione Plotting

### Obiettivo

Integrare convex hull in visualizzazione XChart cluster.

### Step 3.1: Modifica ClusterScatterChart

**Task:** Aggiungere metodo per disegnare convex hull

**File:** `qtGUI/src/main/java/gui/charts/ClusterScatterChart.java`

**Nuovo metodo:**

```java
/**
 * Aggiunge serie convex hull per un cluster.
 *
 * @param chart grafico a cui aggiungere
 * @param cluster cluster da visualizzare
 * @param clusterIndex indice cluster
 */
private void addConvexHullSeries(XYChart chart, Cluster cluster,
                                  int clusterIndex) {
    String seriesName = "Hull " + (clusterIndex + 1);

    int[] tupleIds = cluster.getTupleIDs();

    // Converti tuple in Point2D
    List<Point2D> points = new ArrayList<>();
    for (int tupleId : tupleIds) {
        Tuple tuple = data.getItemSet(tupleId);
        double x = getNumericValue(tuple.get(xAttributeIndex));
        double y = getNumericValue(tuple.get(yAttributeIndex));
        points.add(new Point2D(x, y));
    }

    // Calcola convex hull (gestisci caso < 3 punti)
    if (points.size() < 3) {
        // Se 1-2 punti, non disegnare hull
        return;
    }

    List<Point2D> hull = ConvexHullCalculator.grahamScan(points);

    // Chiudi poligono (aggiungi primo punto alla fine)
    hull.add(hull.get(0));

    // Estrai coordinate per XChart
    List<Double> hullX = new ArrayList<>();
    List<Double> hullY = new ArrayList<>();
    for (Point2D p : hull) {
        hullX.add(p.getX());
        hullY.add(p.getY());
    }

    // Aggiungi serie hull al grafico
    XYSeries hullSeries = chart.addSeries(seriesName, hullX, hullY);

    // Configura stile
    Color clusterColor = ColorPalette.getColor(clusterIndex);
    hullSeries.setLineColor(clusterColor);
    hullSeries.setLineWidth(2.0f);
    hullSeries.setMarker(SeriesMarkers.NONE);  // No marker sui vertici hull
    hullSeries.setShowInLegend(false);         // Nascondi da legenda

    // TODO: Aggiungere riempimento semi-trasparente
    // (XChart non supporta nativamente fill per serie XY)
    // Soluzione alternativa: usare CustomXYDataset o disegno manuale
}
```

**Modifica metodo `addClusterSeries()`:**

```java
private void addClusterSeries(XYChart chart, Cluster cluster, int clusterIndex) {
    // ... codice esistente ...

    // Configura marker
    series.setMarker(SeriesMarkers.CIRCLE);
    series.setMarkerColor(clusterColor);

    // [NUOVO] Riduci dimensione marker (punti più piccoli con hull)
    series.setMarkerSize(6);  // era 8

    // [NUOVO] Rimuovi linea tra punti
    series.setLineStyle(SeriesLineStyle.NONE);
}
```

**Modifica metodo `createChart()`:**

```java
public XYChart createChart() {
    // ... codice esistente per setup chart ...

    for (int i = 0; i < clusterList.size(); i++) {
        Cluster cluster = clusterList.get(i);

        // [NUOVO] Prima disegna hull, poi punti
        addConvexHullSeries(chart, cluster, i);
        addClusterSeries(chart, cluster, i);
    }

    // Centroidi per ultimi (in primo piano)
    addCentroidsSeries(chart, clusterList);

    return chart;
}
```

**Checklist:**

- [ ] Creare metodo `addConvexHullSeries()`
- [ ] Modificare `addClusterSeries()` (rimuovi linee, riduci marker)
- [ ] Aggiornare `createChart()` (ordine disegno: hull → punti → centroidi)
- [ ] Gestire caso cluster < 3 punti (skip hull)
- [ ] Testare con PlayTennis (radius 0.3)

### Step 3.2: Riempimento Poligono (Opzionale)

**Problema:** XChart non supporta nativamente fill per serie XY.

**Soluzioni possibili:**

#### Opzione A: Custom Renderer (consigliato)

```java
// Usare XChartPanel con custom painting
chart.getStyler().setPlotContentSize(0.95); // più spazio

// Dopo creazione chart, accedi a Graphics2D per disegnare fill manualmente
// (richiede override di XChartPanel)
```

#### Opzione B: Background Annotation

```java
// Usare annotazioni custom (se supportato da XChart 3.x)
// Non ideale: annotazioni statiche, non scalano con zoom
```

#### Opzione C: Switch a JFreeChart

- **Pro:** Supporto nativo `XYPolygonAnnotation` con fill
- **Contro:** Riscrittura completa `ClusterScatterChart`

**Decisione:** Implementare Opzione A se tempo sufficiente, altrimenti posticipare a fase successiva.

**Checklist:**

- [ ] Ricercare API XChart per custom rendering
- [ ] Prototipo riempimento poligono
- [ ] Valutare trade-off tempo/risultato
- [ ] Documentare limitazione se non implementato

### Step 3.3: Toggle Stile Plotting

**Task:** Aggiungere opzione per alternare tra stile vecchio/nuovo

**File:** `qtGUI/src/main/java/gui/controllers/ResultsController.java` (o equivalente)

**Aggiunta checkbox UI:**

```xml
<!-- In results.fxml -->
<CheckBox fx:id="convexHullModeCheckBox"
          text="Visualizza Convex Hull"
          selected="true"/>
```

**Controller:**

```java
@FXML
private CheckBox convexHullModeCheckBox;

private void handleUpdateChart() {
    ClusterScatterChart chartManager = new ClusterScatterChart(result);
    chartManager.setAxes(xIndex, yIndex);
    chartManager.setConvexHullMode(convexHullModeCheckBox.isSelected());

    XYChart chart = chartManager.createChart();
    // ... mostra chart ...
}
```

**Modifica `ClusterScatterChart`:**

```java
private boolean convexHullMode = true;  // default: nuovo stile

public void setConvexHullMode(boolean enabled) {
    this.convexHullMode = enabled;
}

public XYChart createChart() {
    // ...

    for (int i = 0; i < clusterList.size(); i++) {
        Cluster cluster = clusterList.get(i);

        if (convexHullMode) {
            addConvexHullSeries(chart, cluster, i);
        }

        addClusterSeries(chart, cluster, i);
    }

    // ...
}
```

**Checklist:**

- [ ] Aggiungere campo `convexHullMode` in `ClusterScatterChart`
- [ ] Aggiungere metodo `setConvexHullMode(boolean)`
- [ ] Condizionare disegno hull su flag
- [ ] Aggiungere checkbox in UI (se presente ResultsController)
- [ ] Testare toggle tra modalità

### Documentazione Fase 3

**File da aggiornare:**

- [ ] Javadoc `ClusterScatterChart` (nuovi metodi)
- [ ] README.md - Sezione "Visualizzazione"
- [ ] Screenshot aggiornati (prima/dopo)

---

## Fase 4: Testing e Validazione

### Obiettivo

Validare che tutte le modifiche funzionino correttamente.

### Step 4.1: Test Unitari (Futuro)

**Framework:** JUnit 5

**Test da creare:**

```
qtGUI/src/test/java/
├── gui/
│   ├── charts/
│   │   └── ConvexHullCalculatorTest.java
│   └── utils/
│       ├── Point2DTest.java
│       └── DatasetLoaderTest.java
```

**Esempio test:**

```java
@Test
public void testGrahamScanSquare() {
    List<Point2D> points = Arrays.asList(
        new Point2D(0, 0),
        new Point2D(1, 0),
        new Point2D(1, 1),
        new Point2D(0, 1),
        new Point2D(0.5, 0.5)
    );

    List<Point2D> hull = ConvexHullCalculator.grahamScan(points);

    assertEquals(4, hull.size(), "Square hull should have 4 vertices");
}
```

**Checklist:**

- [ ] Setup JUnit 5 in pom.xml (se non presente)
- [ ] Creare `ConvexHullCalculatorTest`
- [ ] Creare `Point2DTest`
- [ ] Creare `DatasetLoaderTest`
- [ ] Eseguire test con Maven: `mvn test`

### Step 4.2: Test Integrazione

**Scenario 1: Iris Dataset + Convex Hull**

1. Avvia applicazione
2. Seleziona "Dataset Standard (Iris)"
3. Imposta radius = 0.5
4. Esegui clustering
5. Visualizza grafico con convex hull

**Output atteso:**

- 2-4 cluster (dipende da radius)
- Convex hull visibile per ogni cluster
- Punti all'interno di hull
- Centroidi evidenziati
- Nessun errore console

**Scenario 2: PlayTennis + Convex Hull**

1. Seleziona "Dataset Hardcoded (PlayTennis)"
2. Radius = 0.3
3. Clustering + visualizzazione

**Output atteso:**

- 5-7 cluster
- Hull per cluster > 3 punti
- Nessun hull per cluster < 3 punti

**Scenario 3: Toggle Modalità Plotting**

1. Esegui clustering
2. Toggle checkbox "Visualizza Convex Hull"
3. Verifica cambio stile

**Checklist:**

- [ ] Test Iris + convex hull
- [ ] Test PlayTennis + convex hull
- [ ] Test toggle modalità (se implementato)
- [ ] Test export PNG (qualità immagine)
- [ ] Test errori (dataset vuoto, < 3 punti, etc.)

### Step 4.3: Test Performance

**Metriche da verificare:**

1. **Tempo caricamento Iris:**
   - Atteso: < 100ms
   - Misura: log tempo in `DatasetLoader.loadIrisDataset()`

2. **Tempo clustering Iris (radius 0.5):**
   - Atteso: < 2s (150 tuple, O(n²))
   - Misura: log in `ClusteringController`

3. **Tempo calcolo convex hull (per cluster 50 punti):**
   - Atteso: < 50ms
   - Complessità: O(n log n)

4. **Tempo rendering grafico:**
   - Atteso: < 500ms
   - Misura: tempo tra `createChart()` e visualizzazione

**Checklist:**

- [ ] Misurare tempo caricamento Iris
- [ ] Misurare tempo clustering QT
- [ ] Misurare tempo convex hull
- [ ] Misurare tempo rendering
- [ ] Documentare performance in log

### Documentazione Fase 4

**File da aggiornare:**

- [ ] README.md - Sezione "Testing"
- [ ] Risultati test (screenshot, log)

---

## Fase 5: Documentazione

### Obiettivo

Aggiornare tutta la documentazione con nuove funzionalità.

### Step 5.1: README.md

**Sezioni da aggiornare:**

```markdown
## Dataset Supportati

### Dataset Hardcoded
- **PlayTennis**: 14 tuple, 5 attributi discreti

### Dataset Standard (Nuovo)
- **Iris**: 150 tuple, 4 attributi continui, 3 cluster reali
  - Source: UCI ML Repository
  - Licenza: CC BY 4.0

### Dataset Personalizzati
- Caricamento da CSV
- Caricamento da database MySQL

## Visualizzazione Cluster

### Convex Hull Plotting (Nuovo)
Il sistema visualizza cluster usando inviluppi convessi:
- Area cluster evidenziata con riempimento semi-trasparente
- Punti visibili all'interno dell'area
- Centroidi marcati con croce nera
- Toggle tra modalità convex hull e scatter classico

### Screenshot
[Inserire immagini prima/dopo]
```

**Checklist:**

- [ ] Aggiornare sezione "Dataset"
- [ ] Aggiornare sezione "Visualizzazione"
- [ ] Aggiungere screenshot convex hull
- [ ] Aggiornare "Getting Started" (esempio Iris)

### Step 5.2: CLAUDE.md

**Sezioni da aggiornare:**

```markdown
## Dataset

### PlayTennis (Hardcoded)
- 14 tuple, 5 attributi discreti

### Iris Dataset (Standard)
- 150 tuple, 4 attributi continui
- Cluster reali: 3 (setosa, versicolor, virginica)
- Caricamento: `DatasetLoader.loadIrisDataset()`

## Algoritmo Convex Hull

### Graham Scan
Algoritmo per calcolare inviluppo convesso di punti 2D.

Complessità: O(n log n)

File: `gui/charts/ConvexHullCalculator.java`

Metodo principale:
```java
List<Point2D> hull = ConvexHullCalculator.grahamScan(points);
```

## Classi Implementate

### ConvexHullCalculator

Calcola convex hull con Graham Scan.

### Point2D

Punto 2D con operazioni geometriche.

### DatasetLoader

Utility per caricare dataset standard.

```

**Checklist:**
- [ ] Aggiornare sezione "Dataset"
- [ ] Aggiungere sezione "Algoritmo Convex Hull"
- [ ] Aggiornare "Classi Implementate"
- [ ] Aggiornare diagramma classi

### Step 5.3: Javadoc

**Classi da documentare:**
- [ ] `ConvexHullCalculator` - Javadoc completo
- [ ] `Point2D` - Javadoc completo
- [ ] `DatasetLoader` - Javadoc completo
- [ ] `ClusterScatterChart` - Aggiornare con nuovi metodi

**Standard Javadoc:**
```java
/**
 * Breve descrizione classe/metodo.
 *
 * <p>Descrizione dettagliata con esempi d'uso.</p>
 *
 * <pre>{@code
 * // Esempio codice
 * List<Point2D> hull = ConvexHullCalculator.grahamScan(points);
 * }</pre>
 *
 * @param parametro descrizione parametro
 * @return descrizione valore ritorno
 * @throws EccezioneType quando si verifica
 * @see ClasseRelazionata
 * @since 1.1.0
 */
```

**Checklist:**

- [ ] Javadoc `ConvexHullCalculator`
- [ ] Javadoc `Point2D`
- [ ] Javadoc `DatasetLoader`
- [ ] Javadoc metodi modificati in `ClusterScatterChart`
- [ ] Generare HTML javadoc: `mvn javadoc:javadoc`

### Step 5.4: Changelog

**File:** `CHANGELOG.md` (da creare se non esiste)

```markdown
# Changelog

## [1.1.0] - 2025-11-10

### Added
- **Convex Hull Plotting**: Visualizzazione cluster con inviluppo convesso
- **Iris Dataset**: Dataset standard UCI per testing clustering
- Classe `ConvexHullCalculator` con algoritmo Graham Scan
- Classe `Point2D` per geometria 2D
- Classe `DatasetLoader` per caricare dataset standard
- Toggle modalità plotting (convex hull vs scatter classico)

### Changed
- `ClusterScatterChart`: Aggiunto supporto convex hull
- `HomeController`: Aggiunta opzione "Dataset Standard (Iris)"
- `DataImportService`: Parsing CSV completato
- Validazione radius: limite massimo 1.0

### Fixed
- Radius validation: ora previene valori > 1.0
- CSV import: implementazione completa parsing

### Performance
- Graham Scan: O(n log n) per convex hull
- Iris dataset: clustering in < 2s (150 tuple)
```

**Checklist:**

- [ ] Creare/aggiornare `CHANGELOG.md`
- [ ] Documentare tutte le modifiche
- [ ] Versioning semantico (1.1.0)

---

## Rollback e Compatibilità

### Backward Compatibility

**Garantita:**

- [ ] Dataset PlayTennis continua a funzionare
- [ ] Stile plotting classico disponibile (toggle)
- [ ] Export PNG invariato
- [ ] API pubbliche `ClusterScatterChart` backward compatible

**Breaking Changes:**

- Nessuno (tutte aggiunte, no rimozioni)

### Piano Rollback

**Se convex hull non funziona:**

1. Disabilitare flag `convexHullMode` (default = false)
2. Rimuovere chiamata `addConvexHullSeries()`
3. Sistema torna a funzionare come prima

**Se Iris dataset causa problemi:**

1. Rimuovere voce "Dataset Standard" da UI
2. Mantenere solo PlayTennis + CSV + DB

**Branch Git:**

```bash
# Prima di iniziare Fase 1
git checkout -b feature/convex-hull-plotting

# Commit frequenti
git commit -m "feat: add Point2D class"
git commit -m "feat: implement Graham Scan algorithm"
git commit -m "feat: integrate convex hull in plotting"

# Se rollback necessario
git checkout main
```

---

## Timeline e Priorità

### Timeline Stimata

| Fase       | Descrizione           | Tempo Stimato | Priorità |
| ---------- | --------------------- | ------------- | -------- |
| Fase 1     | Dataset Standard      | 2-3 ore       | Alta     |
| Fase 2     | Algoritmo Convex Hull | 3-4 ore       | Alta     |
| Fase 3     | Integrazione Plotting | 4-5 ore       | Alta     |
| Fase 4     | Testing               | 2-3 ore       | Media    |
| Fase 5     | Documentazione        | 2-3 ore       | Media    |
| **Totale** |                       | **13-18 ore** |          |

### Priorità

**Must Have (Priorità Alta):**

- [x] Fase 1: Iris Dataset
- [x] Fase 2: Algoritmo Graham Scan
- [x] Fase 3: Integrazione base convex hull

**Should Have (Priorità Media):**

- [x] Toggle modalità plotting
- [x] Riempimento poligono semi-trasparente
- [ ] Testing completo
- [x] Documentazione aggiornata

**Nice to Have (Priorità Bassa):**

- [ ] Test unitari JUnit
- [ ] Altri dataset UCI (Wine, Seeds)
- [ ] Export PDF/SVG
- [ ] 3D plotting (futuro)

### Milestone

**Milestone 1: Proof of Concept**

- Iris dataset caricato
- Graham Scan funzionante
- Convex hull disegnato (senza fill)
- **Data target:** Giorno 1

**Milestone 2: Feature Complete**

- Tutte le fasi 1-3 completate
- Toggle modalità
- Testing base
- **Data target:** Giorno 2-3

**Milestone 3: Production Ready**

- Documentazione completa
- Testing estensivo
- Performance validata
- **Data target:** Giorno 4-5

---

## Riferimenti

### Algoritmo Graham Scan

- **Paper originale:** Graham, R.L. (1972). "An efficient algorithm for determining the convex hull of a finite planar set"
- **Complessità:** O(n log n)
- **Wikipedia:** <https://en.wikipedia.org/wiki/Graham_scan>

### Iris Dataset

- **UCI Repository:** <https://archive.ics.uci.edu/dataset/53/iris>
- **Licenza:** CC BY 4.0
- **Paper:** Fisher, R.A. (1936). "The use of multiple measurements in taxonomic problems"

### XChart Library

- **Docs:** <https://knowm.org/open-source/xchart/>
- **GitHub:** <https://github.com/knowm/XChart>
- **Versione:** 3.8.x

---

## Note Implementative

### Decisioni Architetturali

1. **Perché Graham Scan?**
   - Complessità O(n log n) ottimale
   - Implementazione relativamente semplice
   - Ben documentato e testato

2. **Perché XChart invece di JFreeChart?**
   - Già usato nel progetto
   - API più semplice
   - Prestazioni migliori
   - Footprint più leggero

3. **Perché Iris invece di Wine?**
   - Dataset più piccolo (150 vs 178)
   - Cluster più separati (test più semplice)
   - Standard de facto per clustering

### Limitazioni Conosciute

1. **XChart non supporta fill poligono:**
   - Workaround: custom rendering
   - Alternativa: switch a JFreeChart

2. **Convex hull non ideale per cluster concavi:**
   - Alpha shapes sarebbero migliori
   - Troppo complesso per MVP

3. **Performance con dataset > 1000 tuple:**
   - Clustering QT: O(n³) worst case
   - Ottimizzazioni future necessarie

---

## Checklist Finale

### Prima di iniziare

- [ ] Backup codice corrente
- [ ] Creare branch `feature/convex-hull-plotting`
- [ ] Leggere intero piano implementazione
- [ ] Validare prerequisiti (Java, Maven, JavaFX)

### Durante implementazione

- [ ] Commit frequenti con messaggi descrittivi
- [ ] Test dopo ogni fase
- [ ] Log errori e problemi
- [ ] Aggiornare documentazione in parallelo

### Prima del merge

- [ ] Tutte le checklist completate
- [ ] Test integrazione passati
- [ ] Documentazione aggiornata
- [ ] Code review (se in team)
- [ ] Performance accettabile

---

## Appendice A: Dataset Iris CSV

### Formato File

```csv
sepal_length,sepal_width,petal_length,petal_width,species
5.1,3.5,1.4,0.2,setosa
4.9,3.0,1.4,0.2,setosa
4.7,3.2,1.3,0.2,setosa
...
```

### Statistiche

| Attributo    | Min | Max | Media | Std Dev |
| ------------ | --- | --- | ----- | ------- |
| sepal_length | 4.3 | 7.9 | 5.84  | 0.83    |
| sepal_width  | 2.0 | 4.4 | 3.05  | 0.43    |
| petal_length | 1.0 | 6.9 | 3.76  | 1.76    |
| petal_width  | 0.1 | 2.5 | 1.20  | 0.76    |

### Distribuzione Cluster

| Species    | Count | Percentuale |
| ---------- | ----- | ----------- |
| setosa     | 50    | 33.3%       |
| versicolor | 50    | 33.3%       |
| virginica  | 50    | 33.3%       |

---

## Appendice B: Algoritmo Graham Scan Pseudocodice

```
ALGORITMO: GRAHAM_SCAN
INPUT: P = {p1, p2, ..., pn} (insieme punti 2D)
OUTPUT: H = convex hull di P

1. Trova p0 = punto con Y minima (pivot)

2. Ordina P per angolo polare rispetto a p0
   - Angle(pi) = atan2(pi.y - p0.y, pi.x - p0.x)
   - Se stesso angolo, ordina per distanza da p0

3. Inizializza stack S
   S.push(P[0])
   S.push(P[1])

4. PER i = 2 TO n-1 FARE
   4.1. MENTRE |S| > 1 AND ccw(S[top-1], S[top], P[i]) <= 0 FARE
        S.pop()
   4.2. S.push(P[i])

5. RESTITUISCI S (hull ordinato in senso antiorario)

FUNZIONE: ccw(p1, p2, p3)
RETURN (p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x)
// > 0: svolta sinistra (CCW)
// < 0: svolta destra (CW)
// = 0: collineari
```

---

## Appendice C: Esempio Completo Utilizzo

```java
// 1. Carica Iris dataset
Data irisData = DatasetLoader.loadIrisDataset();
System.out.println("Loaded: " + irisData.getNumberOfExamples() + " tuples");

// 2. Esegui clustering
QTMiner miner = new QTMiner(0.5);  // radius = 0.5
int numClusters = miner.compute(irisData);
ClusterSet clusters = miner.getC();
System.out.println("Found " + numClusters + " clusters");

// 3. Crea risultato
ClusteringResult result = new ClusteringResult(
    clusters, irisData, 0.5, executionTime, miner
);

// 4. Visualizza con convex hull
ClusterScatterChart chartManager = new ClusterScatterChart(result);
chartManager.setAxes(0, 2);  // sepal_length vs petal_length
chartManager.setConvexHullMode(true);

XYChart chart = chartManager.createChart();

// 5. Salva immagine
File output = new File("iris_clusters.png");
chartManager.saveAsPNG(output, 1200, 900);
System.out.println("Chart saved to: " + output.getAbsolutePath());
```

---

**Fine documento**

**Versione:** 1.0.0
**Ultima modifica:** 2025-11-10
**Status:** Ready for Implementation
