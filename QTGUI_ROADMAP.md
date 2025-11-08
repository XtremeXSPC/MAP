# qtGUI - Sprint Roadmap
## Implementazione GUI JavaFX per Quality Threshold Clustering

**Progetto:** MAP - Quality Threshold Clustering Algorithm
**Versione:** 1.0
**Data creazione:** 2025-11-08
**Tecnologie:** JavaFX 21+, XChart, Maven/Gradle

---

## Indice

1. [Panoramica Progetto](#panoramica-progetto)
2. [Architettura Proposta](#architettura-proposta)
3. [Sprint Roadmap](#sprint-roadmap)
4. [Tecnologie e Dipendenze](#tecnologie-e-dipendenze)
5. [Deliverables Finali](#deliverables-finali)
6. [Metriche di Successo](#metriche-di-successo)

---

## Panoramica Progetto

### Obiettivo

Sviluppare un'interfaccia grafica moderna con JavaFX per l'algoritmo Quality Threshold, permettendo:
- Input interattivo dei parametri (radius, dataset)
- Esecuzione clustering con feedback visivo
- Visualizzazione risultati 2D/3D
- Salvataggio/caricamento cluster
- Export risultati (CSV, immagini)

### Scope

**In-Scope:**
- GUI desktop multipiattaforma (Windows/Mac/Linux)
- Integrazione con codice esistente qtServer
- Visualizzazione 2D scatter plot dei cluster
- Visualizzazione 3D opzionale (se tempo permette)
- Import dataset da CSV
- Export risultati clustering

**Out-of-Scope:**
- Applicazione mobile Android/iOS
- Web application
- Machine learning avanzato (feature selection, etc.)
- Clustering real-time su stream di dati

### Durata Stimata

**Totale:** 3-4 settimane (60-80 ore sviluppo)
- Sprint 0: Setup (3-5 ore)
- Sprint 1: UI Base (15-20 ore)
- Sprint 2: Integrazione Backend (10-15 ore)
- Sprint 3: Visualizzazione 2D (12-18 ore)
- Sprint 4: Features Avanzate (15-20 ore)
- Sprint 5: Testing e Deployment (5-10 ore)

---

## Architettura Proposta

### Struttura Directory

```
MAP/
├── qtServer/              # Backend esistente
│   ├── src/
│   │   ├── mining/        # QTMiner, Cluster, ClusterSet
│   │   ├── data/          # Data, Tuple, Item
│   │   └── database/      # DbAccess
│   └── bin/
│
├── qtGUI/                 # Nuovo modulo GUI
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   ├── gui/
│   │       │   │   ├── MainApp.java
│   │       │   │   ├── controllers/
│   │       │   │   │   ├── HomeController.java
│   │       │   │   │   ├── ClusteringController.java
│   │       │   │   │   ├── ResultsController.java
│   │       │   │   │   └── SettingsController.java
│   │       │   │   ├── models/
│   │       │   │   │   ├── ClusteringTask.java
│   │       │   │   │   └── VisualizationConfig.java
│   │       │   │   ├── services/
│   │       │   │   │   ├── ClusteringService.java
│   │       │   │   │   ├── DataImportService.java
│   │       │   │   │   └── ExportService.java
│   │       │   │   ├── charts/
│   │       │   │   │   ├── ClusterScatterChart.java
│   │       │   │   │   ├── Cluster3DViewer.java (opzionale)
│   │       │   │   │   └── ChartExporter.java
│   │       │   │   └── utils/
│   │       │   │       ├── ColorPalette.java
│   │       │   │       ├── ValidationUtils.java
│   │       │   │       └── FileChooserHelper.java
│   │       │   └── module-info.java
│   │       └── resources/
│   │           ├── views/
│   │           │   ├── main.fxml
│   │           │   ├── home.fxml
│   │           │   ├── clustering.fxml
│   │           │   ├── results.fxml
│   │           │   └── settings.fxml
│   │           ├── styles/
│   │           │   ├── application.css
│   │           │   └── charts.css
│   │           └── icons/
│   │               └── app-icon.png
│   ├── pom.xml / build.gradle
│   └── README.md
│
├── docs/
│   └── sprints/
│       └── GUI_SPRINT_X.md
│
└── QTGUI_ROADMAP.md    # Questo file
```

### Pattern Architetturali

**MVC (Model-View-Controller)**
- **Model:** ClusteringTask, VisualizationConfig
- **View:** File FXML (home.fxml, results.fxml)
- **Controller:** HomeController, ResultsController

**Service Layer**
- ClusteringService: Wrapper per QTMiner
- DataImportService: Caricamento CSV/DB
- ExportService: Salvataggio risultati

**Dependency Injection**
- Controllers ricevono services via constructor injection
- Configurazione centralizzata in MainApp

---

## Sprint Roadmap

### Sprint 0: Setup e Configurazione (3-5 ore)

**Obiettivo:** Configurare ambiente sviluppo JavaFX

#### Task

**0.1 Setup Build System**
- [ ] Creare progetto Maven/Gradle per qtGUI
- [ ] Configurare dipendenze JavaFX 21+
- [ ] Aggiungere dipendenze charting (XChart/JFreeChart)
- [ ] Configurare module-info.java per JPMS
- [ ] Integrare qtServer come dependency (JAR locale)

**0.2 Struttura Progetto**
- [ ] Creare directory structure (controllers, views, services)
- [ ] Setup Scene Builder (opzionale, per FXML visual editing)
- [ ] Configurare IDE (IntelliJ/Eclipse) per JavaFX
- [ ] Creare MainApp.java con stage iniziale

**0.3 Hello World**
- [ ] Implementare finestra base con JavaFX
- [ ] Testare caricamento FXML
- [ ] Verificare applicazione CSS
- [ ] Testare build e run

#### Deliverables
- Progetto qtGUI configurato e funzionante
- Hello World window eseguibile
- Build script funzionante (Maven/Gradle)

#### Acceptance Criteria
- `mvn javafx:run` lancia applicazione JavaFX
- Finestra base si apre senza errori
- Dipendenze qtServer caricate correttamente

---

### Sprint 1: UI Base e Navigation (15-20 ore)

**Obiettivo:** Implementare interfaccia utente principale con navigazione

#### Task

**1.1 Main Window Layout**
- [ ] Creare main.fxml con BorderPane layout
- [ ] Implementare MenuBar (File, Edit, View, Help)
- [ ] Aggiungere ToolBar con pulsanti rapidi
- [ ] Creare StatusBar per messaggi utente
- [ ] Implementare MainController con navigation logic

**1.2 Home View (Dataset Selection)**
- [ ] Creare home.fxml con form input
- [ ] ComboBox per selezione dataset (Hardcoded/CSV/Database)
- [ ] TextField per radius input con validazione
- [ ] Button "Browse" per selezione file CSV
- [ ] CheckBox per opzioni (enable caching, etc.)
- [ ] Button "Start Clustering" (disabled finché input valido)
- [ ] Implementare HomeController con input validation

**1.3 Clustering View (Progress Feedback)**
- [ ] Creare clustering.fxml con ProgressBar
- [ ] Label dinamica con stato corrente ("Building candidate cluster X/Y...")
- [ ] Button "Cancel" per interrompere clustering
- [ ] Implementare ClusteringController
- [ ] Integrare JavaFX Task per threading

**1.4 Results View (Output Display)**
- [ ] Creare results.fxml con SplitPane
- [ ] Left pane: TreeView con elenco cluster
- [ ] Right pane: TextArea con dettagli cluster selezionato
- [ ] Button toolbar (Export, Visualize, Save, New Analysis)
- [ ] Implementare ResultsController

**1.5 Settings View (Configuration)**
- [ ] Creare settings.fxml con preferenze
- [ ] Sezione "Appearance" (tema, font size)
- [ ] Sezione "Performance" (caching, thread count)
- [ ] Sezione "Export" (default format, directory)
- [ ] Button "Save Settings" e "Reset to Defaults"
- [ ] Implementare SettingsController con Properties file

**1.6 Navigation e Routing**
- [ ] Implementare scene switching (Home → Clustering → Results)
- [ ] Breadcrumb navigation o back button
- [ ] Hotkeys (Ctrl+N per new, Ctrl+O per open, etc.)
- [ ] Dialog per conferme (es. "Discard current analysis?")

#### Deliverables
- UI completa con tutte le view funzionanti
- Navigazione fluida tra schermate
- Input validation implementata

#### Acceptance Criteria
- Utente può navigare tra tutte le view
- Form validation impedisce input non validi
- UI responsive (no freeze durante operazioni)

---

### Sprint 2: Integrazione Backend (10-15 ore)

**Obiettivo:** Collegare GUI a qtServer (QTMiner, Data, etc.)

#### Task

**2.1 ClusteringService**
- [ ] Creare wrapper per QTMiner
- [ ] Implementare metodo `runClustering(Data, radius)` asincrono
- [ ] Gestire eccezioni (ClusteringRadiusException, etc.)
- [ ] Logging operazioni clustering
- [ ] Progress callback per aggiornare UI

**2.2 DataImportService**
- [ ] Implementare caricamento hardcoded dataset (Data())
- [ ] Implementare parsing CSV con validazione
- [ ] Implementare connessione database MySQL (opzionale)
- [ ] Gestione errori (InvalidDataFormatException, EmptyDatasetException)
- [ ] Preview dataset prima del clustering (prime 10 righe)

**2.3 Task Threading**
- [ ] Creare ClusteringTask extends JavaFX Task
- [ ] Implementare progress updates (updateProgress, updateMessage)
- [ ] Gestire cancellazione task
- [ ] Binding ProgressBar a Task progress property
- [ ] Gestire successo/fallimento con callbacks

**2.4 Data Binding**
- [ ] Collegare HomeController input a ClusteringService
- [ ] Populate TreeView in ResultsController con ClusterSet
- [ ] Bind TextArea details a cluster selezionato
- [ ] Update StatusBar con numero cluster/tuple

**2.5 Error Handling**
- [ ] Dialog modale per errori clustering
- [ ] Alert per input non validi
- [ ] Logging errori su file (logs/qtgui.log)
- [ ] Graceful degradation (se DB non disponibile, usa CSV)

#### Deliverables
- GUI completamente integrata con backend
- Clustering eseguibile da GUI con feedback real-time
- Gestione errori robusta

#### Acceptance Criteria
- Utente può eseguire clustering e vedere risultati
- Progress bar si aggiorna durante clustering
- Errori mostrati con dialogs comprensibili
- Applicazione non crasha su input invalidi

---

### Sprint 3: Visualizzazione 2D (12-18 ore)

**Obiettivo:** Implementare scatter plot 2D dei cluster

#### Task

**3.1 Libreria Charting Setup**
- [ ] Decidere libreria (XChart vs JFreeChart vs JavaFX Charts)
- [ ] Integrare dipendenza nel build
- [ ] Creare ClusterScatterChart wrapper class

**3.2 Dimensionality Reduction**
- [ ] Implementare PCA (Principal Component Analysis) per ridurre a 2D
- [ ] Oppure: Selezione manuale 2 attributi da visualizzare
- [ ] ComboBox in UI per scegliere assi X/Y (se multi-dimensionale)

**3.3 Scatter Plot Base**
- [ ] Creare scatter plot con serie per ogni cluster
- [ ] Assegnare colore univoco a ogni cluster (ColorPalette)
- [ ] Mostrare centroidi come marker diversi (stelle/croci)
- [ ] Legenda con cluster names (Cluster 1, Cluster 2, ...)
- [ ] Assi con label attributi selezionati

**3.4 Interattività**
- [ ] Tooltip su hover mostrando dettagli punto (tuple ID, valori)
- [ ] Click su punto per highlight in TreeView
- [ ] Zoom e pan sul grafico
- [ ] Toggle visibilità cluster (checkbox in legenda)

**3.5 Customizzazione**
- [ ] Button "Settings" per aprire dialog configurazione
- [ ] Selezione dimensioni attributi (X, Y)
- [ ] Opzioni colori (palette preset o custom)
- [ ] Dimensione marker, trasparenza
- [ ] Grid lines on/off

**3.6 Export Grafico**
- [ ] Button "Export Chart" → PNG/SVG
- [ ] FileChooser per selezione percorso
- [ ] Risoluzione configurabile (800x600, 1920x1080, custom)

#### Deliverables
- Scatter plot 2D funzionante
- Visualizzazione interattiva dei cluster
- Export immagini implementato

#### Acceptance Criteria
- Utente vede scatter plot con cluster colorati
- Interazione (hover, click, zoom) funziona
- Grafico esportabile come immagine

---

### Sprint 4: Features Avanzate (15-20 ore)

**Obiettivo:** Aggiungere funzionalità extra e polish

#### Task

**4.1 Salvataggio/Caricamento Cluster**
- [ ] Menu File → Save Clustering (.dmp)
- [ ] Menu File → Load Clustering (.dmp)
- [ ] Validation formato file
- [ ] Mostrare metadata (radius, timestamp, num clusters)

**4.2 Export Risultati**
- [ ] Export cluster in CSV (formato: ClusterID, TupleID, Distance)
- [ ] Export summary report (TXT/PDF con statistiche)
- [ ] Export tutto (ZIP con .dmp + CSV + grafico PNG)

**4.3 Cluster Comparison**
- [ ] View per comparare due clustering diversi
- [ ] Side-by-side scatter plots
- [ ] Tabella differenze (cluster spostati, split, merged)
- [ ] Metrics: Adjusted Rand Index, NMI (opzionale)

**4.4 Statistics Dashboard**
- [ ] Panel con statistiche globali:
  - Numero cluster
  - Dimensione media cluster
  - Cluster più grande/più piccolo
  - Distanza media intra-cluster
- [ ] Bar chart distribuzione dimensioni cluster
- [ ] Histogram distanze

**4.5 Visualizzazione 3D (Opzionale)**
- [ ] Integrare FXyz3D library
- [ ] Creare Cluster3DViewer con PCA a 3 componenti
- [ ] Scatter plot 3D con rotazione mouse
- [ ] Toggle 2D/3D view

**4.6 User Experience Polish**
- [ ] Animazioni transizioni view (fade in/out)
- [ ] Icons personalizzate (cluster icon, chart icon)
- [ ] Dark mode / Light mode toggle
- [ ] Keyboard shortcuts (F5 refresh, Ctrl+E export)
- [ ] Tooltips su tutti i controlli
- [ ] About dialog con credits e versione

**4.7 Performance Optimization**
- [ ] Lazy loading per dataset grandi (>1000 tuple)
- [ ] Virtual scrolling in TreeView
- [ ] Caching rendering grafico
- [ ] Progress caching per restart clustering

#### Deliverables
- Funzionalità avanzate implementate
- UI polished e professionale
- Performance ottimizzate

#### Acceptance Criteria
- Salvataggio/caricamento cluster funziona
- Export multipli formati disponibili
- UI fluida e responsive anche con dataset grandi

---

### Sprint 5: Testing e Deployment (5-10 ore)

**Obiettivo:** Testing completo e packaging applicazione

#### Task

**5.1 Unit Testing**
- [ ] Test ClusteringService con mock data
- [ ] Test DataImportService (CSV parsing edge cases)
- [ ] Test ExportService (file creation, format validation)
- [ ] Test ValidationUtils (input sanitization)

**5.2 Integration Testing**
- [ ] Test flow completo: Home → Clustering → Results → Export
- [ ] Test error handling (file non trovato, DB down, etc.)
- [ ] Test cancellazione clustering in progress
- [ ] Test multi-dataset (CSV, hardcoded, DB)

**5.3 UI Testing**
- [ ] Test manuale tutti i controlli
- [ ] Test responsiveness (resize window)
- [ ] Test navigation (back/forward, breadcrumbs)
- [ ] Test su diverse risoluzioni (1920x1080, 1366x768)

**5.4 Cross-Platform Testing**
- [ ] Test su Windows 10/11
- [ ] Test su macOS (Intel e Apple Silicon)
- [ ] Test su Linux (Ubuntu/Fedora)

**5.5 Packaging**
- [ ] Creare JAR eseguibile con Maven/Gradle
- [ ] Configurare jpackage per installer nativi:
  - Windows: EXE/MSI
  - macOS: DMG/PKG
  - Linux: DEB/RPM
- [ ] Includere JRE embedded (jlink)
- [ ] Testare installer su macchine pulite

**5.6 Documentazione**
- [ ] README.md con istruzioni installazione/uso
- [ ] User manual (PDF) con screenshots
- [ ] Developer documentation (Javadoc)
- [ ] Video demo (5-10 min) funzionalità principali

**5.7 Release**
- [ ] Git tag versione (v1.0.0)
- [ ] GitHub Release con installer binari
- [ ] Changelog dettagliato
- [ ] Pubblicazione su repository Maven (opzionale)

#### Deliverables
- Applicazione testata e stabile
- Installer per Windows/Mac/Linux
- Documentazione completa

#### Acceptance Criteria
- Tutti i test passano
- Installer funzionano su tutte le piattaforme
- Documentazione chiara e completa
- Applicazione pronta per distribuzione

---

## Tecnologie e Dipendenze

### Core Technologies

| Tecnologia | Versione | Scopo |
|------------|----------|-------|
| Java | 21+ | Linguaggio base |
| JavaFX | 21.0.1+ | UI framework |
| Maven/Gradle | Latest | Build automation |

### Librerie UI

| Libreria | Versione | Scopo |
|----------|----------|-------|
| ControlsFX | 11.2.0 | Controlli avanzati UI |
| MaterialFX | 11.16.1 | Material Design components |
| FontAwesomeFX | 8.9 | Icons |

### Librerie Charting

**Opzione A: XChart (Consigliata)**
- Leggera, facile da usare
- Scatter, bar, histogram built-in
- Export PNG/SVG nativo
- Versione: 3.8.5

**Opzione B: JFreeChart**
- Più feature-rich ma pesante
- Customizzazione estrema
- Versione: 1.5.4

**Opzione C: JavaFX Charts (Built-in)**
- Zero dipendenze esterne
- Limitata ma sufficiente per 2D base
- Integrazione perfetta con JavaFX CSS

### Librerie 3D (Opzionale)

| Libreria | Versione | Scopo |
|----------|----------|-------|
| FXyz3D | 0.6.0 | 3D shapes e scatter plot |
| JPro3D | - | Alternative 3D framework |

### Testing

| Libreria | Versione | Scopo |
|----------|----------|-------|
| JUnit | 5.10+ | Unit testing |
| TestFX | 4.0.18 | JavaFX UI testing |
| Mockito | 5.8+ | Mocking |

### Logging

| Libreria | Versione | Scopo |
|----------|----------|-------|
| SLF4J | 2.0.9 | Logging facade |
| Logback | 1.4.14 | Implementation |

### Build e Packaging

| Tool | Scopo |
|------|-------|
| Maven Shade Plugin | Uber JAR creation |
| jpackage (JDK 21) | Native installers |
| jlink (JDK 21) | Custom JRE runtime |

---

## Esempio pom.xml (Maven)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.map</groupId>
    <artifactId>qtGUI</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>QT Clustering GUI</name>
    <description>JavaFX GUI for Quality Threshold Clustering</description>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <javafx.version>21.0.1</javafx.version>
        <xchart.version>3.8.5</xchart.version>
    </properties>

    <dependencies>
        <!-- JavaFX -->
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>${javafx.version}</version>
        </dependency>

        <!-- Charting -->
        <dependency>
            <groupId>org.knowm.xchart</groupId>
            <artifactId>xchart</artifactId>
            <version>${xchart.version}</version>
        </dependency>

        <!-- UI Enhancements -->
        <dependency>
            <groupId>org.controlsfx</groupId>
            <artifactId>controlsfx</artifactId>
            <version>11.2.0</version>
        </dependency>

        <!-- qtServer backend (local JAR) -->
        <dependency>
            <groupId>com.map</groupId>
            <artifactId>qtServer</artifactId>
            <version>1.0.0</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/../qtServer/qtServer.jar</systemPath>
        </dependency>

        <!-- Logging -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.9</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.4.14</version>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.1</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testfx</groupId>
            <artifactId>testfx-junit5</artifactId>
            <version>4.0.18</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Maven Compiler -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                </configuration>
            </plugin>

            <!-- JavaFX Maven Plugin -->
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <configuration>
                    <mainClass>gui.MainApp</mainClass>
                </configuration>
            </plugin>

            <!-- Shade Plugin (Uber JAR) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer
                                    implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>gui.MainApp</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## Deliverables Finali

### Sprint 0
- [x] Progetto JavaFX configurato
- [x] Build system funzionante
- [x] Hello World eseguibile

### Sprint 1
- [x] UI completa con 5 view (Main, Home, Clustering, Results, Settings)
- [x] Navigazione fluida
- [x] Input validation
- [x] Form controls funzionanti

### Sprint 2
- [x] Integrazione backend completa
- [x] Clustering eseguibile da GUI
- [x] Progress feedback real-time
- [x] Error handling robusto

### Sprint 3
- [x] Scatter plot 2D interattivo
- [x] Visualizzazione cluster con colori
- [x] Zoom, pan, tooltip
- [x] Export grafico PNG/SVG

### Sprint 4
- [x] Salvataggio/caricamento .dmp
- [x] Export CSV/TXT/PDF
- [x] Statistics dashboard
- [x] UI polish (dark mode, animations, icons)
- [ ] Visualizzazione 3D (opzionale)

### Sprint 5
- [x] Test suite completo
- [x] Installer Windows/Mac/Linux
- [x] Documentazione utente e developer
- [x] Release v1.0.0

---

## Metriche di Successo

### Funzionalità Core (Must-Have)

| Feature | Priorità | Status |
|---------|----------|--------|
| Esecuzione clustering da GUI | P0 | ⬜ |
| Input radius con validazione | P0 | ⬜ |
| Caricamento dataset (hardcoded/CSV) | P0 | ⬜ |
| Visualizzazione risultati testuali | P0 | ⬜ |
| Scatter plot 2D | P0 | ⬜ |
| Export risultati (.dmp, CSV) | P1 | ⬜ |
| Progress bar clustering | P1 | ⬜ |
| Error handling dialogs | P1 | ⬜ |

### Features Avanzate (Nice-to-Have)

| Feature | Priorità | Status |
|---------|----------|--------|
| Visualizzazione 3D | P2 | ⬜ |
| Cluster comparison | P2 | ⬜ |
| Statistics dashboard | P2 | ⬜ |
| Dark mode | P2 | ⬜ |
| Database integration | P3 | ⬜ |
| PDF export | P3 | ⬜ |

### Qualità

| Metrica | Target | Attuale |
|---------|--------|---------|
| Code coverage | >70% | - |
| UI response time | <200ms | - |
| Clustering feedback delay | <500ms | - |
| Installer size | <100MB | - |
| Startup time | <3s | - |

### User Experience

| Criterio | Target |
|----------|--------|
| Facilità d'uso | Utente non esperto completa clustering in <5 click |
| Error messages | Sempre comprensibili e actionable |
| Performance | Nessun freeze UI durante clustering |
| Cross-platform | Funziona identico su Windows/Mac/Linux |

---

## Rischi e Mitigazioni

### Rischio 1: JavaFX Dependency Hell
**Probabilità:** Media
**Impatto:** Alto
**Mitigazione:**
- Usare Maven/Gradle con versioni esplicite
- Testare build su macchina pulita
- Documentare setup in README dettagliato

### Rischio 2: Performance con Dataset Grandi
**Probabilità:** Alta
**Impatto:** Medio
**Mitigazione:**
- Implementare lazy loading
- Usare virtual scrolling
- Caching risultati clustering
- Limitare rendering grafico a 1000 punti (sampling)

### Rischio 3: 3D Visualization Complexity
**Probabilità:** Alta
**Impatto:** Basso
**Mitigazione:**
- Rendere feature opzionale (Sprint 4)
- Usare libreria matura (FXyz3D)
- Fallback a 2D se problemi

### Rischio 4: Cross-Platform Bugs
**Probabilità:** Media
**Impatto:** Medio
**Mitigazione:**
- Testing continuo su tutte le piattaforme
- Usare JavaFX API standard (no OS-specific code)
- GitHub Actions CI/CD multi-OS

---

## Next Steps

### Immediate Actions (Week 1)

1. **Setup Maven Project**
   ```bash
   cd MAP
   mvn archetype:generate -DgroupId=com.map -DartifactId=qtGUI
   cd qtGUI
   # Configurare pom.xml con dipendenze JavaFX
   ```

2. **Create Basic Structure**
   ```bash
   mkdir -p src/main/{java/gui,resources/views}
   touch src/main/java/gui/MainApp.java
   touch src/main/resources/views/main.fxml
   ```

3. **Implement Hello World**
   - MainApp.java con Stage e Scene
   - main.fxml con Label "QT Clustering GUI"
   - Test: `mvn javafx:run`

4. **Git Integration**
   ```bash
   git checkout -b feature/qt-gui
   git add qtGUI/
   git commit -m "Sprint 0: Initial qtGUI setup"
   ```

### Sprint Planning Template

Ogni sprint dovrebbe seguire questo template:

```markdown
# Sprint X: [Nome Sprint]

## Obiettivi
- [ ] Goal 1
- [ ] Goal 2

## Tasks
### Task 1: [Nome]
- [ ] Subtask 1.1
- [ ] Subtask 1.2

## Daily Progress
- **Day 1:** ...
- **Day 2:** ...

## Blockers
- Issue 1: [Descrizione] → [Soluzione]

## Demo
- Screenshot/Video funzionalità completate

## Retrospective
- What went well: ...
- What to improve: ...
```

---

## Resources

### Learning Resources

**JavaFX Tutorials:**
- [Official JavaFX Documentation](https://openjfx.io/)
- [JavaFX Tutorial - Jenkov.com](https://jenkov.com/tutorials/javafx/index.html)
- [Genuine Coder - JavaFX Course](https://www.youtube.com/watch?v=9XJicRt_FaI)

**Scene Builder:**
- [Gluon Scene Builder](https://gluonhq.com/products/scene-builder/)
- [Scene Builder Tutorial](https://docs.oracle.com/javase/8/scene-builder-2/get-started-tutorial/overview.htm)

**XChart Documentation:**
- [XChart Wiki](https://knowm.org/open-source/xchart/)
- [XChart Examples](https://knowm.org/open-source/xchart/xchart-example-code/)

**Packaging:**
- [jpackage Guide](https://docs.oracle.com/en/java/javase/21/jpackage/packaging-overview.html)
- [JavaFX Deployment](https://openjfx.io/openjfx-docs/#install-java)

### Tools

| Tool | Purpose | Link |
|------|---------|------|
| IntelliJ IDEA | IDE (recommended) | https://www.jetbrains.com/idea/ |
| Scene Builder | Visual FXML editor | https://gluonhq.com/products/scene-builder/ |
| Maven | Build automation | https://maven.apache.org/ |
| Git | Version control | https://git-scm.com/ |

---

## Conclusion

Questa roadmap fornisce un piano dettagliato per implementare qtGUI in 3-4 settimane.

**Key Success Factors:**
1. **Incrementalità:** Ogni sprint produce deliverable funzionante
2. **Testing continuo:** Non accumulare debito tecnico
3. **Flessibilità:** Sprint 4-5 possono essere adattati in base a tempo disponibile
4. **Focus su MVP:** Priorità P0/P1 prima di P2/P3

**Prossimo passo:** Iniziare Sprint 0 con setup progetto Maven e Hello World JavaFX.

---

**Versione:** 1.0
**Ultima modifica:** 2025-11-08
**Autore:** Claude (AI Assistant)
**Licenza:** Progetto accademico MAP
