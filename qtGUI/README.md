# qtGUI - Interfaccia grafica JavaFX per Quality Threshold Clustering

> **Modulo**: Applicazione desktop per l'uso locale del sistema QT
> **Versione**: 1.0
> **Autore**: Lombardi Costantino

---

## Indice

1. [Descrizione generale](#descrizione-generale)
2. [Collocazione architetturale](#collocazione-architetturale)
3. [Struttura del modulo](#struttura-del-modulo)
4. [Componenti principali](#componenti-principali)
5. [Funzionalita' rilevanti del modulo](#funzionalita-rilevanti-del-modulo)
6. [Dipendenze](#dipendenze)
7. [Build ed esecuzione](#build-ed-esecuzione)
8. [Configurazione e persistenza](#configurazione-e-persistenza)
9. [Indicazioni di manutenzione](#indicazioni-di-manutenzione)
10. [Riferimenti](#riferimenti)

---

## Descrizione generale

### Finalita' del modulo

Il modulo `qtGUI` costituisce l'interfaccia grafica del progetto per il corso di MAP.
La sua funzione principale e' consentire l'esecuzione del clustering Quality Threshold
in ambiente desktop, accompagnando l'utente nelle fasi di selezione della sorgente dati,
configurazione del `radius`, avvio dell'elaborazione, consultazione dei risultati e
salvataggio degli artefatti prodotti.

A differenza della modalita' client-server basata sulla coppia `qtClient`/`qtServer`,
la GUI non utilizza il protocollo socket per l'esecuzione ordinaria del clustering. Il
modulo incorpora infatti i sorgenti del backend nella propria build Maven e richiama
direttamente le classi di `qtServer`. Dal punto di vista architetturale, `qtGUI` si
comporta quindi come un front-end locale, che riutilizza il nucleo computazionale del
progetto senza dipendere dal server di rete.

### Ambito funzionale

Le capacita' principali del modulo possono essere riassunte come segue:

| Ambito                  | Descrizione                                                            |
| ----------------------- | ---------------------------------------------------------------------- |
| Acquisizione dati       | Caricamento da `PlayTennis`, dataset `Iris`, file CSV e database MySQL |
| Esecuzione locale       | Invocazione diretta di `QTMiner` tramite servizi applicativi           |
| Consultazione risultati | Vista ad albero, pannelli di dettaglio, dialoghi statistici            |
| Visualizzazione grafica | Scatter plot 2D, modalita' convex hull, esportazione PNG               |
| Persistenza             | Salvataggio e ricaricamento di clustering in formato `.dmp`            |
| Esportazione            | Generazione di file CSV, report TXT e pacchetto ZIP                    |
| Personalizzazione       | Gestione di tema, font, parametri database e cartella di export        |

### Principio progettuale

La GUI adotta una separazione esplicita tra presentazione, logica applicativa e modelli di stato.
Questa impostazione, pur restando entro i confini di una applicazione didattica, rende il modulo
sufficientemente leggibile anche dal punto di vista ingegneristico: i controller governano il
flusso dell'interfaccia, i servizi incapsulano le operazioni sostanziali e i modelli fungono da
contenitori di configurazioni e risultati.

---

## Collocazione architetturale

L'inserimento di `qtGUI` nell'architettura complessiva del progetto puo' essere
schematizzato nel modo seguente:

```text
┌──────────────┐
│    qtGUI     │
│   (JavaFX)   │
└──────┬───────┘
       │
       │ chiamate dirette
       ▼
┌────────────────────────────┐
│          qtServer          │
│  data / mining / database  │
└────────────────────────────┘
```

Ne derivano due conseguenze importanti:

1. la GUI puo' essere eseguita senza avviare `qtServer` come processo separato;
2. l'accesso al database, quando richiesto, avviene localmente attraverso le classi del
   package `database`, non attraverso il protocollo di rete.

Questa impostazione giustifica la presenza, nel `pom.xml`, del
`build-helper-maven-plugin`, che include i sorgenti di `../qtServer/src` nel processo di
build del modulo.

---

## Struttura del modulo

La struttura principale del codice e' la seguente:

```text
qtGUI/src/main/
├── java/
│   ├── gui/
│   │   ├── MainApp.java
│   │   ├── Launcher.java
│   │   ├── charts/
│   │   ├── controllers/
│   │   ├── dialogs/
│   │   ├── models/
│   │   ├── services/
│   │   └── utils/
│   └── module-info.java
└── resources/
    ├── datasets/
    ├── styles/
    └── views/
```

### Significato delle aree principali

| Area                  | Contenuto                                                          |
| --------------------- | ------------------------------------------------------------------ |
| `controllers/`        | Coordinamento delle viste FXML e degli eventi utente               |
| `services/`           | Logica di importazione dati, clustering, persistenza ed export     |
| `models/`             | Rappresentazioni di configurazioni e risultati                     |
| `charts/`             | Visualizzazione 2D e calcolo dell'inviluppo convesso               |
| `dialogs/`            | Finestre modali di supporto                                        |
| `utils/`              | Componenti trasversali, tema, contesto applicativo, dataset loader |
| `resources/views/`    | Layout FXML delle schermate                                        |
| `resources/styles/`   | Fogli di stile CSS dell'applicazione                               |
| `resources/datasets/` | Dataset inclusi nelle risorse, in particolare `iris.csv`           |

### Entry point

Il modulo presenta due entry point strettamente collegati:

- `gui.MainApp`, che estende `javafx.application.Application`;
- `gui.Launcher`, usato come punto di ingresso del JAR prodotto dal packaging.

`MainApp` si occupa della creazione dello stage principale, del caricamento di
`main.fxml`, della configurazione di `StdTheme` (dal modulo `stdgui`) e della
registrazione degli acceleratori globali.

---

## Componenti principali

### Controller

I controller governano la navigazione e il comportamento delle schermate:

| Controller             | Responsabilita' principale                                              |
| ---------------------- | ----------------------------------------------------------------------- |
| `MainController`       | Coordinamento della finestra principale e delle viste interne           |
| `HomeController`       | Selezione della sorgente dati e validazione dei parametri iniziali      |
| `ClusteringController` | Esecuzione del clustering e monitoraggio del progresso                  |
| `ResultsController`    | Presentazione del risultato, export e accesso alle viste di dettaglio   |
| `SettingsController`   | Gestione delle preferenze applicative e dei parametri di configurazione |

Dal punto di vista del flusso utente, `HomeController` e `ResultsController` sono i due
punti piu' rilevanti: il primo costruisce la configurazione di input, il secondo rende
consultabile e persistente l'output del backend.

### Service layer

I servizi costituiscono il nucleo applicativo della GUI:

| Service             | Funzione                                                     |
| ------------------- | ------------------------------------------------------------ |
| `ClusteringService` | Esecuzione di `QTMiner`, salvataggio e caricamento di `.dmp` |
| `DataImportService` | Importazione da dataset hardcoded, `Iris`, CSV e database    |
| `ExportService`     | Esportazione in CSV, TXT e ZIP                               |

Questa separazione evita di disperdere la logica del dominio nei controller e rende piu'
trasparente il rapporto tra interfaccia e backend.

### Modelli

I modelli principali sono:

| Modello                   | Ruolo                                                               |
| ------------------------- | ------------------------------------------------------------------- |
| `ClusteringConfiguration` | Rappresentazione strutturata dei parametri di input                 |
| `ClusteringResult`        | Contenitore di `ClusterSet`, `Data`, timestamp, `radius` e metadati |

`ClusteringResult` e' particolarmente importante perche' funge da punto di raccordo tra
clustering, visualizzazione, statistiche ed esportazione.

### Utilita' trasversali

Tra le classi di supporto meritano particolare attenzione:

| Utility              | Funzione                                                           |
| -------------------- | ------------------------------------------------------------------ |
| `ApplicationContext` | Stato applicativo condiviso e accesso ai servizi comuni            |
| `DatasetLoader`      | Caricamento del dataset `Iris` dalle risorse                       |
| `Point2D`            | Rappresentazione elementare dei punti usati nei grafici            |
| `ColorPalette`       | Definizione di colori coerenti per la rappresentazione dei cluster |

La gestione del tema e della dimensione dei font e' delegata a
`com.map.stdgui.StdTheme`, configurato una volta in `MainApp` e agganciato alla
finestra principale via `attach(StdWindow)`.

### Dialoghi e viste specializzate

Il modulo include inoltre componenti mirati alla consultazione dei dati:

| Componente             | Ruolo                                                   |
| ---------------------- | ------------------------------------------------------- |
| `DatasetPreviewDialog` | Anteprima strutturata del dataset selezionato           |
| `StatisticsDialog`     | Riepilogo statistico del clustering                     |
| `AboutDialog`          | Informazioni descrittive sull'applicazione              |
| `ChartViewer`          | Finestra di visualizzazione ed esportazione del grafico |

---

## Funzionalita' rilevanti del modulo

### Sorgenti dati supportate

Il caricamento dei dati avviene tramite `DataImportService`, che supporta quattro casi:

1. dataset hardcoded `PlayTennis`;
2. dataset `Iris` incluso nelle risorse;
3. file CSV esterni;
4. tabelle MySQL raggiunte via JDBC.

Il dataset `Iris` viene caricato da `DatasetLoader`, che estrae temporaneamente la
risorsa e la rende compatibile con il parser CSV esistente nel backend. Per i CSV
esterni valgono invece i limiti del parser implementato in `data.Data`, gia' noti al
resto del progetto.

### Esecuzione del clustering

`ClusteringService` crea un'istanza di `QTMiner`, esegue `compute(data)` e costruisce la
rappresentazione applicativa del risultato. Il servizio valida i parametri in ingresso e
traduce eventuali problemi in eccezioni gestibili dalla GUI.

L'esecuzione avviene in locale: non viene aperta alcuna connessione socket al server.
Questo aspetto e' centrale per comprendere il ruolo del modulo e la sua differenza rispetto a `qtClient`.

### Visualizzazione dei risultati

La rappresentazione dei risultati e' articolata su piu' livelli:

- riepilogo numerico del clustering;
- struttura ad albero dei cluster e delle tuple;
- pannelli di dettaglio;
- dialoghi statistici;
- grafico 2D selezionabile per coppie di attributi.

Il grafico e' gestito principalmente dalle classi del package `charts`.

### Convex hull

Una delle estensioni piu' interessanti del modulo e' la possibilita' di affiancare allo
scatter plot una rappresentazione basata sull'inviluppo convesso dei punti di ciascun
cluster. Il calcolo e' affidato a `ConvexHullCalculator`, che implementa una variante
del Graham Scan.

Questa scelta non modifica l'algoritmo QT, ma offre una visualizzazione piu' interpretativa
della distribuzione dei punti, utile soprattutto nei casi in cui il solo insieme di marker
renda difficile percepire la forma globale dei cluster.

### Esportazione e persistenza

Il modulo supporta due famiglie di output:

| Tipo                     | Formato                    |
| ------------------------ | -------------------------- |
| Persistenza interna      | `.dmp`                     |
| Esportazione documentale | `CSV`, `TXT`, `ZIP`, `PNG` |

`ExportService` gestisce i tre formati testuali/archivistici:

- `CSV`, come rappresentazione tabellare delle tuple clusterizzate;
- `TXT`, come report descrittivo con metadati e statistiche;
- `ZIP`, come pacchetto completo contenente `.dmp`, CSV, report e README interno.

Per la visualizzazione grafica, `ChartViewer` e `ClusterScatterChart` permettono il
salvataggio del grafico in PNG standard o ad alta risoluzione.

### Shortcuts globali

`MainApp` registra i seguenti acceleratori a livello di scena:

- `Ctrl+N`, `Ctrl+O`, `Ctrl+S`, `Ctrl+E`, `Ctrl+Q`
- `F1`, `F5`

Nel codice attuale l'unica azione con effetto operativo immediato e' `Ctrl+Q`, che
chiude l'applicazione. Gli altri shortcut costituiscono soprattutto un'infrastruttura di
estensione e un punto di aggancio per comportamenti futuri.

---

## Dipendenze

### Dipendenze interne

Il modulo dipende funzionalmente dai package di `qtServer`, inclusi nella build Maven:

```text
qtGUI ── depends on ──> qtServer
  │
  ├─ services       ──> data
  ├─ services       ──> mining
  └─ services       ──> database
```

### Dipendenze esterne

Le dipendenze principali dichiarate nel `pom.xml` sono:

| Libreria                             | Ruolo                                                 |
| ------------------------------------ | ----------------------------------------------------- |
| JavaFX (`controls`, `fxml`, `swing`) | Infrastruttura grafica                                |
| XChart                               | Generazione dei grafici                               |
| ControlsFX                           | Componenti UI aggiuntivi                              |
| SLF4J + Logback                      | Logging                                               |
| MySQL Connector/J                    | Accesso JDBC, opzionale a seconda della sorgente dati |

Il modulo richiede inoltre una toolchain coerente con `JDK 21`, esplicitamente dichiarata nella configurazione Maven.

---

## Build ed esecuzione

### Build con Maven

Dalla directory `qtGUI/`:

```bash
mvn clean compile
```

L'esecuzione in sviluppo puo' avvenire con:

```bash
mvn javafx:run
```

Per il packaging:

```bash
mvn package
```

Il processo di packaging usa il `maven-shade-plugin` per costruire un JAR eseguibile con
entry point `gui.Launcher`.

### Integrazione con il Makefile di progetto

Il repository espone anche target di comodo:

```bash
make gui
make gui-jar
make run-gui
```

Questi target astraggono parte del flusso Maven e mantengono una interfaccia uniforme
con gli altri moduli del progetto.

### Artefatti principali

| Artefatto           | Posizione                      |
| ------------------- | ------------------------------ |
| Classi compilate    | `qtGUI/target/classes`         |
| JAR del modulo      | `qtGUI/target/qtGUI-1.0.0.jar` |
| Risorse applicative | `qtGUI/src/main/resources`     |

---

## Configurazione e persistenza

### Parametri utente

Il modulo consente di configurare almeno i seguenti aspetti:

- parametri di connessione al database;
- tema dell'interfaccia;
- dimensione dei font;
- directory di esportazione;
- formato di export preferito.

### Persistenza delle preferenze

Le preferenze vengono salvate in un file di proprieta' (`qtgui.properties`) e ricaricate
tramite `com.map.stdgui.StdTheme` e i controller delle impostazioni. Questa persistenza
consente alla GUI di presentarsi in modo coerente tra esecuzioni successive senza
richiedere una riconfigurazione completa.

### Relazione con i file `.dmp`

Il salvataggio dei clustering in formato `.dmp` non rappresenta soltanto una esportazione,
ma una vera forma di persistenza applicativa. Un file di questo tipo puo'essere riaperto e
ricondotto a un `QTMiner` comprensivo di `ClusterSet`, dataset e `radius`, salvo il caso
dei file legacy piu' vecchi.

---

## Indicazioni di manutenzione

### Aggiunta di una nuova vista

L'estensione dell'interfaccia segue normalmente una procedura in quattro passi:

1. creazione del file FXML in `resources/views/`;
2. implementazione del controller associato;
3. inserimento della navigazione nel controller principale;
4. eventuale registrazione di servizi o modelli nel contesto applicativo.

Questa organizzazione consente di far evolvere la GUI senza compromettere la
separazione tra layout, logica e stato.

### Evoluzione dei servizi

I package `services` e `models` sono i punti piu' delicati in caso di modifiche
funzionali, perche' rappresentano l'interfaccia interna tra GUI e backend. Se si
introducono nuovi formati di input o output, nuovi passi di workflow o nuove viste di
risultato, e' consigliabile espandere prima i servizi e solo successivamente i controller.

### Cautela sugli shortcut

La presenza degli acceleratori globali non implica che ogni scorciatoia abbia gia' un effetto semantico
completo. In eventuali revisioni future e' opportuno evitare di documentare come operative combinazioni
che nel codice svolgono ancora soltanto funzioni di logging o preparazione infrastrutturale.

---

## Riferimenti

- [`../qtServer/README.md`](../qtServer/README.md)
- [`../docs/uml/qtGUI/controllers/controllers_package.puml`](../docs/uml/qtGUI/controllers/controllers_package.puml)
- [`../docs/uml/qtGUI/services/services_package.puml`](../docs/uml/qtGUI/services/services_package.puml)
- [`../docs/uml/qtGUI/models/models_package.puml`](../docs/uml/qtGUI/models/models_package.puml)
- [`../docs/uml/qtGUI/views/charts_dialogs_utils.puml`](../docs/uml/qtGUI/views/charts_dialogs_utils.puml)
- [`src/main/java/gui/MainApp.java`](src/main/java/gui/MainApp.java)
- [`src/main/java/gui/services/ClusteringService.java`](src/main/java/gui/services/ClusteringService.java)
- [`src/main/java/gui/services/DataImportService.java`](src/main/java/gui/services/DataImportService.java)
- [`src/main/java/gui/services/ExportService.java`](src/main/java/gui/services/ExportService.java)

---
