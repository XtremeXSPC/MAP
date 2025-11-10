# qtGUI - Quality Threshold Clustering GUI

> **Modulo**: Interfaccia Grafica JavaFX
> **Versione**: 1.0
> **Autore**: Progetto MAP - Metodi Avanzati di Programmazione

---

## Indice

1. [Descrizione Generale](#descrizione-generale)
2. [Architettura Interna](#architettura-interna)
3. [Package](#package)
4. [Dipendenze](#dipendenze)
5. [Interfacce Pubbliche](#interfacce-pubbliche)
6. [Interazioni con Altri Moduli](#interazioni-con-altri-moduli)
7. [Build e Compilazione](#build-e-compilazione)
8. [Utilizzo](#utilizzo)
9. [Configurazione](#configurazione)
10. [Note di Manutenzione](#note-di-manutenzione)

---

## Descrizione Generale

### Scopo del Modulo

Il modulo **qtGUI** fornisce un'interfaccia grafica moderna e user-friendly per il sistema Quality Threshold Clustering. Implementa:

- **GUI JavaFX** con architettura MVC (Model-View-Controller)
- **Visualizzazione interattiva** cluster con scatter chart 2D
- **Dark/Light theme** con transizioni fluide
- **Import dati** da CSV, database MySQL, file serializzati
- **Export multipli formati** (CSV, TXT, ZIP) per risultati
- **Statistics dashboard** con grafici e metriche dettagliate
- **Keyboard shortcuts** per operazioni comuni

### Funzionalità Principali

| Funzionalità            | Descrizione                                | Package                      |
| ----------------------- | ------------------------------------------ | ---------------------------- |
| **Import Dati**         | Caricamento da CSV, DB, file, Iris dataset | `services.DataImportService` |
| **Clustering**          | Esecuzione QT con progress tracking        | `services.ClusteringService` |
| **Visualizzazione**     | Scatter chart 2D con convex hull plotting  | `charts`                     |
| **Convex Hull**         | Inviluppi convessi per cluster (NEW)       | `charts.ConvexHullCalculator`|
| **Dataset Standard**    | Iris dataset UCI integrato (NEW)           | `utils.DatasetLoader`        |
| **Export**              | CSV, TXT, ZIP, PNG HD dei risultati        | `services.ExportService`     |
| **Theming**             | Dark/Light mode con persistenza            | `utils.ThemeManager`         |
| **Navigazione**         | Multi-view con state management            | `controllers`                |

### Posizione nell'Architettura Generale

```
┌─────────────┐
│    qtGUI    │
│  (JavaFX)   │
└──────┬──────┘
       │
       │ Direct Call (no Socket)
       │
       ▼
┌─────────────┐
│  qtServer   │
│  Packages:  │
│  - data     │
│  - mining   │
│  - database │
└─────────────┘
```

**Caratteristiche**:

- **Thick client**: Logica business integrata localmente
- **Stateful**: Persistenza configurazioni e preferenze
- **Reattivo**: Pattern Observer per aggiornamenti UI

---

## Architettura Interna

### Struttura Package

Il modulo è organizzato in **7 package** seguendo pattern MVC:

```
qtGUI/src/main/java/gui/
├── MainApp.java          # Entry point JavaFX
├── Launcher.java         # Launcher wrapper
│
├── charts/               # Visualizzazioni grafiche
├── controllers/          # MVC Controllers
├── dialogs/              # Dialoghi modali
├── models/               # Modelli dati
├── services/             # Business logic
└── utils/                # Utilità (theme, context)
```

### Pattern di Design Utilizzati

#### 1. Model-View-Controller (MVC)

**Separazione responsabilità**:

```
┌──────────┐         ┌──────────────┐         ┌──────────┐
│   View   │<───────>│  Controller  │<───────>│  Model   │
│  (FXML)  │         │    (Java)    │         │  (Data)  │
└──────────┘         └──────────────┘         └──────────┘
```

**Package mapping**:

- **View**: File FXML in `resources/views/`
- **Controller**: Classi in `controllers/`
- **Model**: Classi in `models/` + `services/`

#### 2. Singleton Pattern

**Classi**: `ThemeManager`, `ApplicationContext`

```java
// ThemeManager - Gestione globale tema
ThemeManager themeManager = ThemeManager.getInstance();
themeManager.setTheme(Theme.DARK);

// ApplicationContext - Stato applicazione
ApplicationContext context = ApplicationContext.getInstance();
context.setCurrentResult(result);
```

#### 3. Service Layer Pattern

Astrae business logic da controller:

```java
ClusteringService service = new ClusteringService();
ClusterSet clusters = service.runClustering(data, radius);
```

#### 4. Observer Pattern

**JavaFX Properties**: Binding reattivo per aggiornamenti UI.

```java
progressBar.progressProperty().bind(task.progressProperty());
```

### Diagramma Organizzazione

Riferimenti ai diagrammi UML:

- [`docs/uml/qtGUI/controllers/controllers_package.puml`](../docs/uml/qtGUI/controllers/controllers_package.puml)
- [`docs/uml/qtGUI/services/services_package.puml`](../docs/uml/qtGUI/services/services_package.puml)
- [`docs/uml/qtGUI/models/models_package.puml`](../docs/uml/qtGUI/models/models_package.puml)
- [`docs/uml/qtGUI/views/charts_dialogs_utils.puml`](../docs/uml/qtGUI/views/charts_dialogs_utils.puml)

---

## Nuove Funzionalità v1.1

### Convex Hull Plotting

Visualizzazione innovativa dei cluster utilizzando **inviluppi convessi** (convex hulls) anziché scatter plot tradizionale.

**Caratteristiche**:
- **Poligoni colorati**: Ogni cluster racchiuso in area delimitata
- **Percezione immediata**: Forma e densità cluster visibili a colpo d'occhio
- **Modalità toggle**: Possibilità di alternare tra convex hull e scatter classico
- **Algoritmo Graham Scan**: Complessità O(n log n), implementazione efficiente

**Vantaggi rispetto scatter tradizionale**:
- Nessuna confusione da linee intrecciate
- Delimitazione chiara dei confini cluster
- Identificazione immediata di overlap tra cluster
- Migliore per presentazioni e pubblicazioni

**Implementazione**:
```java
// Classe ConvexHullCalculator
List<Point2D> hull = ConvexHullCalculator.grahamScan(points);
// Restituisce vertici ordinati in senso antiorario
```

**File coinvolti**:
- `gui/charts/ConvexHullCalculator.java` - Algoritmo Graham Scan
- `gui/charts/ClusterScatterChart.java` - Integrazione plotting
- `gui/charts/ChartViewer.java` - Toggle UI
- `gui/utils/Point2D.java` - Geometria 2D

### Dataset Standard: Iris

Integrazione dataset **Iris** (Fisher, 1936) come benchmark standard per clustering.

**Caratteristiche Iris**:
- **150 tuple**: 50 setosa, 50 versicolor, 50 virginica
- **4 attributi continui**: sepal length/width, petal length/width
- **3 cluster reali**: Separabilità nota (setosa isolata, altre parzialmente sovrapposte)
- **Licenza**: CC BY 4.0 - UCI Machine Learning Repository

**Utilizzo**:
1. Home → Sorgente Dati: Seleziona "Dataset Standard (Iris)"
2. Clustering → Radius consigliato: 0.4-0.6
3. Results → Visualize → Assi consigliati: petal_length vs petal_width

**File coinvolti**:
- `resources/datasets/iris.csv` - Dataset CSV (150 righe)
- `gui/utils/DatasetLoader.java` - Utility caricamento
- `gui/services/DataImportService.java` - Integrazione enum IRIS
- `gui/controllers/HomeController.java` - UI selezione
- `resources/views/home.fxml` - ComboBox aggiornata

**Applicazioni**:
- Benchmark algoritmo QT
- Tutorial e demo
- Validazione implementazione
- Confronto con altri algoritmi clustering

---

## Package

### Package `controllers` - MVC Controllers

**Scopo**: Coordinamento tra vista e modello, gestione eventi utente.

**Classi principali**:

| Controller             | Responsabilità                                  | Vista Associata   |
| ---------------------- | ----------------------------------------------- | ----------------- |
| `MainController`       | Finestra principale, menu, toolbar, navigazione | `main.fxml`       |
| `HomeController`       | Schermata home, selezione dataset               | `home.fxml`       |
| `ClusteringController` | Configurazione parametri, avvio clustering      | `clustering.fxml` |
| `ResultsController`    | Visualizzazione risultati, scatter chart        | `results.fxml`    |
| `SettingsController`   | Impostazioni applicazione, tema, database       | `settings.fxml`   |

**Pattern di navigazione**:

```java
public void showView(String viewName) {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + viewName + ".fxml"));
    Parent view = loader.load();
    contentArea.getChildren().setAll(view);
}
```

**Diagramma UML**: `docs/uml/qtGUI/controllers/controllers_package.puml`

---

### Package `services` - Business Logic

**Classi principali**:

| Service             | Responsabilità                                    |
| ------------------- | ------------------------------------------------- |
| `ClusteringService` | Esecuzione clustering QT, salvataggio/caricamento |
| `DataImportService` | Import dati da CSV, database, file, Iris **(UPD)**|
| `ExportService`     | Export risultati in CSV, TXT, ZIP                 |

**API**:

```java
// ClusteringService
public ClusterSet runClustering(Data data, double radius)
public void saveClustering(String fileName, ClusteringResult result)

// DataImportService (UPDATED)
public enum DataSource { HARDCODED, IRIS, CSV, DATABASE }  // NEW: IRIS
public Data loadHardcodedData()
public Data loadIrisData()  // NEW
public Data loadDataFromCSV(String filePath)
public Data loadDataFromDatabase(...)

// ExportService
public void exportToCsv(String filePath, ClusteringResult result)
public void exportToTextReport(String filePath, ClusteringResult result)
public void exportToZip(String zipFilePath, ClusteringResult result)
```

**Formato export**:

- **CSV**: Formato tabulare (ClusterID, TupleID, Distance, Attributi)
- **TXT**: Report con statistiche
- **ZIP**: Pacchetto completo (.dmp + CSV + TXT + README)

**Diagramma UML**: `docs/uml/qtGUI/services/services_package.puml`

---

### Package `models` - Data Models

**Classi**:

| Model                     | Responsabilità                          |
| ------------------------- | --------------------------------------- |
| `ClusteringResult`        | Wrapper risultato clustering + metadata |
| `ClusteringConfiguration` | Configurazione parametri clustering     |

**Diagramma UML**: `docs/uml/qtGUI/models/models_package.puml`

---

### Package `charts` - Visualizzazioni

**Classi**:

- `ClusterScatterChart`: Scatter plot 2D con supporto convex hull
- `ChartViewer`: Container visualizzazione interattiva
- `ConvexHullCalculator`: Algoritmo Graham Scan O(n log n) **(NEW)**

**Funzionalità**:

- **Convex Hull Plotting** (NEW):
  - Visualizzazione cluster con inviluppi convessi
  - Poligoni colorati per delimitare aree cluster
  - Toggle modalità convex hull vs scatter classico
  - Gestione automatica cluster < 3 punti
- Scatter plot con colori per cluster
- Selezione assi dinamica (X/Y)
- Tooltip informativi
- Legenda cluster
- Export PNG/PNG HD (800x600, 1920x1440)

**Algoritmo Graham Scan**:
- Input: Lista di punti 2D
- Output: Vertici convex hull ordinati (senso antiorario)
- Complessità: O(n log n)
- Componenti:
  - Trova pivot (punto Y minima)
  - Ordina punti per angolo polare
  - Costruisce hull con stack e CCW test

**Diagramma UML**: `docs/uml/qtGUI/views/charts_dialogs_utils.puml`

---

### Package `dialogs` - Dialoghi Modali

**Classi**:

- `AboutDialog`: Info applicazione
- `DatasetPreviewDialog`: Anteprima dataset
- `StatisticsDialog`: Dashboard statistiche con 4 tab

**Diagramma UML**: `docs/uml/qtGUI/views/charts_dialogs_utils.puml`

---

### Package `utils` - Utilità

**Classi**:

| Utility              | Pattern   | Scopo                                   |
| -------------------- | --------- | --------------------------------------- |
| `ApplicationContext` | Singleton | Stato globale applicazione              |
| `ThemeManager`       | Singleton | Gestione Dark/Light theme               |
| `ColorPalette`       | Utility   | Palette colori coerente                 |
| `DatasetLoader`      | Utility   | Caricamento dataset standard **(NEW)**  |
| `Point2D`            | Value     | Punto 2D per geometria **(NEW)**        |

**DatasetLoader** (NEW):
- `loadIrisDataset()`: Carica Iris da resources (150 tuple, 4 continui)
- `getAvailableDatasets()`: Lista dataset predefiniti
- Gestione file temporanei per parsing CSV da JAR

**Point2D** (NEW):
- Classe immutabile per coordinate 2D
- `distanceTo()`: Distanza euclidea
- `polarAngleFrom()`: Angolo polare rispetto a pivot
- Utilizzata da ConvexHullCalculator

**Diagramma UML**: `docs/uml/qtGUI/views/charts_dialogs_utils.puml`

---

## Dipendenze

### Dipendenze Interne

```
qtGUI ──depends on──> qtServer
  │                     │
  ├─ services       ──> mining (QTMiner)
  ├─ services       ──> data (Data)
  └─ services       ──> database (DbAccess)
```

### Dipendenze Esterne

| Libreria    | Versione | Scopo         | Obbligatoria |
| ----------- | -------- | ------------- | ------------ |
| **JDK**     | 11+      | Runtime Java  | SI           |
| **JavaFX**  | 17+      | Framework GUI | SI           |
| **SLF4J**   | 1.7+     | Logging       | SI           |
| **Logback** | 1.2+     | Logging impl  | SI           |

---

## Interfacce Pubbliche

### Entry Point

```bash
# Esecuzione
java --module-path $JAVAFX_PATH --add-modules javafx.controls,javafx.fxml \
     -cp qtGUI/bin:qtServer/bin \
     gui.Launcher
```

---

## Interazioni con Altri Moduli

### qtGUI → qtServer

**Tipo**: Chiamata diretta (no Socket)

**Vantaggi**:

- Nessun overhead rete
- Performance massime
- Debug semplificato

---

## Build e Compilazione

### Compilazione Manuale

```bash
export JAVAFX_PATH="/path/to/javafx-sdk-17/lib"

# Compila qtServer (dipendenza)
cd qtServer && javac -d bin src/**/*.java

# Compila qtGUI
cd ../qtGUI
javac --module-path $JAVAFX_PATH --add-modules javafx.controls,javafx.fxml \
      -cp ../qtServer/bin -d bin src/main/java/**/*.java
```

### Makefile

```bash
make gui      # Compila qtGUI
make all      # Compila tutto
```

---

## Utilizzo

### Workflow Tipico

1. **Avvia**: `java -jar qtGUI.jar`
2. **Load Dataset**: Home → Load from CSV
3. **Configure**: Clustering → Set radius → Run
4. **View Results**: Results → Scatter chart + Statistics
5. **Export**: Export → Choose format

### Keyboard Shortcuts

| Shortcut | Azione                  |
| -------- | ----------------------- |
| `Ctrl+N` | Nuovo clustering        |
| `Ctrl+O` | Apri file               |
| `Ctrl+S` | Salva risultati         |
| `Ctrl+T` | Toggle Dark/Light theme |
| `F1`     | Help                    |

---

## Configurazione

### Database

Settings → Database tab:

- Server: `localhost`
- Port: `3306`
- Database: `MapDB`
- User/Password: `MapUser/map`

### Persistenza Preferenze

- Tema (Dark/Light)
- Font size
- Config database
- File recenti

---

## Note di Manutenzione

### Aggiunta Nuova Vista

1. Crea `newview.fxml` in `resources/views/`
2. Crea `NewViewController.java`
3. Registra in `MainController.showView()`

### Performance Tips

- Task background per clustering lungo
- Lazy loading viste FXML
- Caching risultati

---

## Riferimenti

### Documentazione Sprint

- [`docs/gui_sprints/`](../docs/gui_sprints/) - Sprint GUI completi

### Diagrammi UML

- `docs/uml/qtGUI/controllers/controllers_package.puml`
- `docs/uml/qtGUI/services/services_package.puml`
- `docs/uml/qtGUI/models/models_package.puml`
- `docs/uml/qtGUI/views/charts_dialogs_utils.puml`

---

**Versione**: 1.0
**Data**: 2025-11-09
**Autore**: Progetto MAP
