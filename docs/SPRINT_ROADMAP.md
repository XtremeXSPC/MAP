# Sprint Roadmap - Quality Threshold Clustering Project

> **Documento di pianificazione:** Roadmap completa degli Sprint di sviluppo per il progetto MAP (Quality Threshold Clustering Algorithm)

---

## 📊 Panoramica Generale

### Stato del Progetto

| Sprint | Nome | Stato | Durata | Completamento |
|--------|------|-------|--------|---------------|
| Sprint 0 | Struttura Base | ✅ Completato | 1 settimana | 100% |
| Sprint 1 | Algoritmo QT | ✅ Completato | 1 settimana | 100% |
| Sprint 2 | Persistenza e I/O | 🔜 Pianificato | 1 settimana | 0% |
| Sprint 3 | Ottimizzazioni Performance | 🔜 Pianificato | 2 settimane | 0% |
| Sprint 4 | Supporto Attributi Continui | 🔜 Pianificato | 1 settimana | 0% |
| Sprint 5 | Interfaccia Grafica | 🔜 Pianificato | 2 settimane | 0% |
| Sprint 6 | Metriche Qualità | 🔜 Pianificato | 1 settimana | 0% |
| Sprint 7 | Database Integration (JDBC) | 🔜 Pianificato | 2 settimane | 0% |
| Sprint 8 | Comunicazione Client-Server | 🔜 Pianificato | 2 settimane | 0% |

### Timeline Stimata
- **Fase 1 (Core):** Sprint 0-1 (✅ Completata)
- **Fase 2 (Estensioni Base):** Sprint 2-4 (🔜 Prossimi 4 settimane)
- **Fase 3 (Interfacce):** Sprint 5-6 (🔜 3 settimane)
- **Fase 4 (Integrazione):** Sprint 7-8 (🔜 4 settimane)

**Durata Totale Stimata:** 12-14 settimane

---

## 🎯 Sprint Completati

### ✅ Sprint 0 - Struttura Base del Progetto

**Durata:** 1 settimana
**Stato:** Completato
**Documentazione:** [`docs/sprints/SPRINT_0.md`](sprints/SPRINT_0.md)

#### Obiettivi
Implementare le classi fondamentali per rappresentare dati e struttura base del sistema di clustering Quality Threshold.

#### Classi Implementate
- ✅ `Attribute` (abstract) - Attributo generico
- ✅ `DiscreteAttribute` - Attributi categorici
- ✅ `ContinuousAttribute` - Attributi numerici
- ✅ `Item` (abstract) - Coppia attributo-valore
- ✅ `DiscreteItem` - Item con valore discreto
- ✅ `Tuple` - Riga del dataset (sequenza di item)
- ✅ `ArraySet` - Insieme di interi senza duplicati
- ✅ `Data` - Gestione dataset PlayTennis (14 tuple, 5 attributi)

#### Risultati Chiave
- ✅ Struttura modulare con design pattern Template Method
- ✅ Calcolo distanza di Hamming per dati categorici
- ✅ Dataset PlayTennis hardcoded e pronto per l'uso
- ✅ Tutte le classi compilano senza errori

#### Metriche
- **Story Points:** 22
- **Classi:** 8
- **Linee di codice:** ~500 LOC

---

### ✅ Sprint 1 - Algoritmo Quality Threshold

**Durata:** 1 settimana
**Stato:** Completato
**Documentazione:** [`docs/sprints/SPRINT_1.md`](sprints/SPRINT_1.md)
**Prerequisiti:** Sprint 0

#### Obiettivi
Implementare l'algoritmo di clustering Quality Threshold (QT) completo, incluse le classi per gestire cluster individuali e insiemi di cluster.

#### Classi Implementate
- ✅ `Cluster` - Singolo cluster con centroide e tuple
- ✅ `ClusterSet` - Insieme di cluster (risultato algoritmo)
- ✅ `QTMiner` - Implementazione algoritmo QT principale
- ✅ `MainTest` - Entry point per testing

#### Funzionalità Implementate
- ✅ Algoritmo QT completo con garanzia di qualità
- ✅ Costruzione cluster candidati basata su radius
- ✅ Calcolo distanze medie da centroidi
- ✅ Output dettagliato con centroidi e tuple
- ✅ Input interattivo per parametro radius

#### Risultati Chiave
- ✅ Algoritmo deterministico funzionante
- ✅ Tutte le tuple vengono clusterizzate
- ✅ Vincolo radius rispettato per ogni cluster
- ✅ Testing con diversi valori di radius (0, 0.5, 1.0)

#### Metriche
- **Story Points:** 18
- **Classi:** 4
- **Complessità Algoritmica:** O(k × n²), caso peggiore O(n³)
- **Linee di codice:** ~400 LOC

#### Esempio Output
```
Numero di cluster: 11

Cluster 1:
Centroid=(sunny hot high weak no )
Examples:
[sunny hot high weak no ] dist=0.0
[sunny hot high strong no ] dist=0.2
[sunny mild high weak no ] dist=0.2
AvgDistance=0.133
```

---

## 🔜 Sprint Pianificati

### Sprint 2 - Persistenza e I/O

**Durata:** 1 settimana
**Stato:** Pianificato
**Priorità:** 🔴 Alta
**Prerequisiti:** Sprint 1

#### Obiettivi
Implementare funzionalità di persistenza per salvare e caricare cluster da file, e supporto per dataset esterni.

#### Funzionalità Pianificate

##### 1. Serializzazione Cluster
- Salvare ClusterSet su file (formato JSON o Java Serialization)
- Caricare ClusterSet da file esistente
- Metodi: `saveClusterSet(String filename)`, `loadClusterSet(String filename)`

##### 2. Gestione Dataset Esterni
- Caricamento dataset da file CSV
- Parsing automatico di schema attributi
- Validazione formato dati

##### 3. Gestione Errori I/O
- Try-catch per FileNotFoundException
- Validazione integrità dati caricati
- Messaggi di errore user-friendly

#### Classi da Modificare/Creare
- `Data` - Aggiungere costruttore con path file CSV
- `QTMiner` - Aggiungere metodi save/load
- `CSVParser` (nuova) - Parsing file CSV
- `ClusterSerializer` (nuova) - Serializzazione cluster

#### Tecnologie
- Java Serialization (ObjectOutputStream/ObjectInputStream)
- Alternativa: JSON con org.json o Gson
- BufferedReader per CSV parsing

#### Criteri di Successo
- [ ] Cluster salvati e ricaricati correttamente
- [ ] Dataset esterni caricati con successo
- [ ] Gestione errori robusta
- [ ] Test con file CSV di esempio
- [ ] Documentazione formato file

#### Story Points Stimati: 13

---

### Sprint 3 - Ottimizzazioni Performance

**Durata:** 2 settimane
**Stato:** Pianificato
**Priorità:** 🔴 Alta
**Prerequisiti:** Sprint 1, 2

#### Obiettivi
Ottimizzare l'algoritmo QT per gestire dataset più grandi (> 1000 tuple) migliorando complessità e prestazioni.

#### Ottimizzazioni Pianificate

##### 1. Pruning Algoritmo
- Evitare calcoli distanza per tuple ovviamente troppo lontane
- Implementare bounds superiori/inferiori
- Ridurre iterazioni inutili in buildCandidateCluster

##### 2. Caching Distanze
- Memorizzare distanze già calcolate (matrice simmetrica)
- Invalidazione cache quando necessario
- Trade-off memoria vs tempo

##### 3. Strutture Dati Efficienti
- Sostituire ArraySet con HashSet per O(1) lookup
- Considerare R-tree per ricerca spaziale
- ArrayList invece di array nativi dove appropriato

##### 4. Parallelizzazione
- Thread pool per costruzione cluster candidati paralleli
- Java ExecutorService per gestione thread
- Sincronizzazione accesso a strutture condivise

#### Miglioramenti Attesi
- Complessità da O(n³) → O(n² log n)
- Riduzione 40-60% tempo esecuzione per n > 100
- Supporto dataset fino a 10,000 tuple

#### Classi da Modificare
- `QTMiner` - Algoritmo ottimizzato
- `ArraySet` → `ClusterSet` con HashSet
- `DistanceCache` (nuova) - Caching distanze
- `ParallelQTMiner` (nuova) - Versione parallelizzata

#### Metriche da Misurare
- Tempo esecuzione (ms) per diversi n
- Memoria utilizzata
- Speedup parallelizzazione

#### Criteri di Successo
- [ ] Algoritmo funziona con dataset 1000+ tuple
- [ ] Riduzione tempo esecuzione >40%
- [ ] Test di benchmarking documentati
- [ ] Profiling e analisi performance

#### Story Points Stimati: 21

---

### Sprint 4 - Supporto Attributi Continui

**Durata:** 1 settimana
**Stato:** Pianificato
**Priorità:** 🟡 Media
**Prerequisiti:** Sprint 0, 1

#### Obiettivi
Estendere il sistema per supportare attributi numerici/continui usando distanza Euclidea.

#### Funzionalità Pianificate

##### 1. ContinuousItem
- Implementare sottoclasse di Item per valori continui
- Distanza Euclidea: |value1 - value2|
- Normalizzazione nel range [0, 1]

##### 2. Normalizzazione Valori
- Min-max normalization: (x - min) / (max - min)
- Gestione automatica range attributi
- Supporto in ContinuousAttribute

##### 3. Dataset Misti
- Gestire dataset con attributi discreti + continui
- Distanza combinata: weighted average
- Pesi configurabili per attributi

##### 4. Dataset di Test
- Iris dataset (4 attributi continui)
- Wine dataset (13 attributi continui)
- Confronto risultati con algoritmi standard

#### Classi da Implementare/Modificare
- `ContinuousItem` (nuova) - Implementare distance()
- `ContinuousAttribute` - Completare implementazione
- `Data` - Supporto per dataset misti
- `Tuple` - Distanza combinata discreto/continuo

#### Formula Distanza Mista
```
distance = (w_discrete * d_hamming + w_continuous * d_euclidean) / (w_discrete + w_continuous)
```

#### Criteri di Successo
- [ ] ContinuousItem implementato e testato
- [ ] Normalizzazione funzionante
- [ ] Test con Iris dataset
- [ ] Confronto con k-means su dati continui
- [ ] Documentazione formule distanza

#### Story Points Stimati: 13

---

### Sprint 5 - Interfaccia Grafica (GUI)

**Durata:** 2 settimane
**Stato:** Pianificato
**Priorità:** 🟡 Media
**Prerequisiti:** Sprint 1, 2

#### Obiettivi
Creare un'interfaccia grafica Swing per input parametri e visualizzazione cluster.

#### Funzionalità Pianificate

##### 1. GUI Input Parametri
- Form per selezione file dataset
- Slider per parametro radius
- Pulsante "Run Clustering"
- Tabella visualizzazione dataset

##### 2. Visualizzazione Cluster 2D
- Scatter plot con colori diversi per cluster
- PCA per riduzione dimensionalità a 2D
- Legenda cluster
- Zoom e pan

##### 3. Visualizzazione 3D (Opzionale)
- Scatter plot 3D con Java3D o JavaFX
- Rotazione interattiva
- Selezione cluster

##### 4. Grafici Statistici
- Grafico dimensioni cluster (bar chart)
- Grafico distanze medie (line chart)
- Distribuzione attributi per cluster
- Uso di JFreeChart library

##### 5. Export Immagini
- Salvataggio grafici come PNG/SVG
- Export dati cluster come CSV
- Report PDF con clustering results

#### Classi da Creare
- `QTMinerGUI` (main frame)
- `DatasetPanel` - Visualizzazione dataset
- `ParametersPanel` - Input parametri
- `ClusterVisualizationPanel` - Visualizzazione 2D
- `StatisticsPanel` - Grafici statistici

#### Tecnologie
- Swing per GUI components
- JFreeChart per grafici
- Opzionale: JavaFX per visualizzazioni moderne

#### Mockup GUI
```
┌─────────────────────────────────────────────┐
│ QT Clustering Tool                    [_][□][X]│
├─────────────────────────────────────────────┤
│ Dataset: [playtennis.csv ▼] [Browse...]    │
│ Radius:  [====•=========] 0.5               │
│ [Run Clustering]                            │
├─────────────────┬───────────────────────────┤
│ Dataset (14x5)  │ Cluster Visualization     │
│ Outlook Temp... │     ••  ••                │
│ sunny   hot ... │   ••      ••              │
│ sunny   hot ... │     ••  ••                │
│ ...             │   ••                      │
├─────────────────┴───────────────────────────┤
│ Statistics                                  │
│ Clusters: 5 | Avg Size: 2.8 | Avg Dist: 0.3│
└─────────────────────────────────────────────┘
```

#### Criteri di Successo
- [ ] GUI funzionante e user-friendly
- [ ] Visualizzazione 2D cluster
- [ ] Grafici statistici con JFreeChart
- [ ] Export immagini funzionante
- [ ] Usability testing con utenti

#### Story Points Stimati: 21

---

### Sprint 6 - Metriche Qualità Clustering

**Durata:** 1 settimana
**Stato:** Pianificato
**Priorità:** 🟡 Media
**Prerequisiti:** Sprint 1

#### Obiettivi
Implementare metriche standard per valutare la qualità del clustering e confrontare con altri algoritmi.

#### Metriche da Implementare

##### 1. Silhouette Coefficient
- Misura quanto un punto è simile al suo cluster vs altri cluster
- Valore in [-1, 1]: 1 = ottimo, 0 = indifferente, -1 = mal clusterizzato
- Formula: s(i) = (b(i) - a(i)) / max(a(i), b(i))
  - a(i) = distanza media intra-cluster
  - b(i) = distanza media al cluster più vicino

##### 2. Davies-Bouldin Index
- Misura rapporto tra dispersione intra-cluster e separazione inter-cluster
- Valori bassi indicano clustering migliore
- Formula: DB = (1/k) Σ max((σ_i + σ_j) / d(c_i, c_j))

##### 3. Calinski-Harabasz Index (Variance Ratio Criterion)
- Rapporto tra dispersione between-cluster e within-cluster
- Valori alti indicano clustering migliore
- Formula: CH = (SSB / (k-1)) / (SSW / (n-k))

##### 4. Inertia (Within-Cluster Sum of Squares)
- Somma delle distanze al quadrato da centroidi
- Valori bassi indicano cluster compatti
- Formula: Inertia = Σ Σ distance²(point, centroid)

##### 5. Adjusted Rand Index (se ground truth disponibile)
- Confronto con clustering noto
- Valore in [-1, 1]: 1 = perfetto match

#### Classi da Creare
- `ClusteringMetrics` (nuova)
  - `calculateSilhouette(ClusterSet, Data): double`
  - `calculateDaviesBouldin(ClusterSet, Data): double`
  - `calculateCalinskiHarabasz(ClusterSet, Data): double`
  - `calculateInertia(ClusterSet, Data): double`

#### Funzionalità Aggiuntive

##### Confronto con K-Means
- Implementare k-means base per confronto
- Eseguire entrambi algoritmi su stesso dataset
- Confrontare metriche qualità

##### Report Automatico
- Generare report con tutte le metriche
- Suggerire valore ottimale di radius
- Grafici elbow method per selezione parametri

#### Output Esempio
```
Clustering Quality Metrics:
- Silhouette Coefficient: 0.72 (Good)
- Davies-Bouldin Index: 0.45 (Lower is better)
- Calinski-Harabasz Index: 245.6 (Higher is better)
- Inertia: 12.34

Comparison with K-Means (k=5):
                    QT      K-Means
Silhouette:         0.72    0.68
Davies-Bouldin:     0.45    0.52
Runtime (ms):       120     85
```

#### Criteri di Successo
- [ ] Tutte le 5 metriche implementate
- [ ] Test con dataset standard (Iris, Wine)
- [ ] Confronto con k-means documentato
- [ ] Report automatico funzionante
- [ ] Grafici per interpretazione metriche

#### Story Points Stimati: 13

---

### Sprint 7 - Database Integration (JDBC)

**Durata:** 2 settimane
**Stato:** Pianificato
**Priorità:** 🟢 Bassa
**Prerequisiti:** Sprint 1, 2

#### Obiettivi
Integrare supporto per database relazionali usando JDBC per lettura dati e salvataggio risultati.

#### Funzionalità Pianificate

##### 1. Connessione Database
- Supporto MySQL, PostgreSQL, SQLite
- Connection pooling con HikariCP
- Configurazione tramite properties file

##### 2. Lettura Dati da DB
- Query SELECT per caricare dataset
- Mapping automatico colonne → Attribute
- Inferenza tipo attributi (discrete/continuous)

##### 3. Salvataggio Risultati
- Creare tabelle per cluster e membership
- Schema:
  ```sql
  CREATE TABLE clusters (
    cluster_id INT PRIMARY KEY,
    centroid TEXT,
    size INT,
    avg_distance DOUBLE
  );

  CREATE TABLE cluster_membership (
    tuple_id INT,
    cluster_id INT,
    distance_to_centroid DOUBLE,
    PRIMARY KEY (tuple_id, cluster_id)
  );
  ```

##### 4. Query Analitiche
- Stored procedure per statistiche cluster
- View per aggregazioni
- Index per performance

#### Classi da Creare
- `DatabaseConfig` - Configurazione connessione
- `DatabaseLoader` - Caricamento dati
- `ClusterRepository` - Persistenza cluster
- `ConnectionPool` - Gestione connessioni

#### Tecnologie
- JDBC API (java.sql.*)
- HikariCP per connection pooling
- MySQL Connector/J, PostgreSQL JDBC driver
- Opzionale: JPA/Hibernate per ORM

#### File Configurazione (db.properties)
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/qtclustering
db.username=root
db.password=password
db.pool.size=10
```

#### Esempio Utilizzo
```java
DatabaseLoader loader = new DatabaseLoader("db.properties");
Data data = loader.loadDataFromQuery("SELECT * FROM tennis_data");

QTMiner qt = new QTMiner(0.5);
qt.compute(data);

ClusterRepository repo = new ClusterRepository("db.properties");
repo.saveClusters(qt.getC(), data);
```

#### Criteri di Successo
- [ ] Connessione a MySQL/PostgreSQL funzionante
- [ ] Caricamento dataset da DB
- [ ] Salvataggio cluster in DB
- [ ] Connection pooling implementato
- [ ] Test con database di test
- [ ] Documentazione schema DB

#### Story Points Stimati: 21

---

### Sprint 8 - Comunicazione Client-Server (Socket)

**Durata:** 2 settimane
**Stato:** Pianificato
**Priorità:** 🟢 Bassa
**Prerequisiti:** Sprint 1, 2

#### Obiettivi
Implementare architettura client-server per clustering distribuito usando Socket Java.

#### Architettura Pianificata

##### 1. Server Multi-Client
- ServerSocket su porta configurabile (es. 8080)
- Thread pool per gestire client concorrenti
- Protocollo comunicazione testuale/binario

##### 2. Protocollo Comunicazione
```
Client → Server:
  LOAD_DATA <dataset_name>
  SET_RADIUS <value>
  RUN_CLUSTERING
  GET_RESULTS
  DISCONNECT

Server → Client:
  OK <message>
  ERROR <error_message>
  DATA <serialized_data>
  CLUSTERS <serialized_clusters>
```

##### 3. Client GUI/CLI
- Client Swing per interazione grafica
- Client CLI per scripting
- Gestione connessione e riconnessione

##### 4. Load Balancing (Opzionale)
- Distribuire calcolo cluster su più server
- Master-slave architecture
- Aggregazione risultati

#### Classi da Creare

**Server:**
- `QTServer` - Server principale
- `ClientHandler` - Thread per gestire singolo client
- `ProtocolHandler` - Parsing comandi
- `ClusteringService` - Esecuzione algoritmo

**Client:**
- `QTClient` - Client base
- `QTClientGUI` - Client con GUI Swing
- `QTClientCLI` - Client command-line
- `ServerConnection` - Gestione connessione

#### Diagramma Architettura
```
┌────────────┐         ┌─────────────────┐
│ Client 1   │────────►│                 │
│ (GUI)      │  Socket │                 │
└────────────┘         │   QT Server     │
                       │  (Port 8080)    │
┌────────────┐         │                 │
│ Client 2   │────────►│  Thread Pool    │
│ (CLI)      │  Socket │                 │
└────────────┘         │  QTMiner        │
                       │  Data           │
┌────────────┐         │  ClusterSet     │
│ Client N   │────────►│                 │
└────────────┘         └─────────────────┘
```

#### Esempio Sessione Client-CLI
```bash
$ java QTClientCLI localhost 8080
Connected to QT Server at localhost:8080

> LOAD_DATA playtennis.csv
OK: Dataset loaded (14 tuples, 5 attributes)

> SET_RADIUS 0.5
OK: Radius set to 0.5

> RUN_CLUSTERING
Computing clusters...
OK: Clustering completed (5 clusters found)

> GET_RESULTS
Cluster 1: size=3, avg_distance=0.13
  Centroid: (sunny, hot, high, weak, no)
  ...

> DISCONNECT
Disconnected from server
```

#### Funzionalità Avanzate

##### 1. Autenticazione
- Login con username/password
- Session token per richieste multiple

##### 2. Caching Server-Side
- Cache dataset caricati di frequente
- Cache risultati clustering per parametri comuni

##### 3. Notifiche Asincrone
- Notificare client quando clustering completo
- Progress bar per operazioni lunghe

##### 4. Logging e Monitoring
- Log richieste client
- Statistiche: numero richieste, tempo medio risposta
- Dashboard amministrativa

#### Criteri di Successo
- [ ] Server multi-client funzionante
- [ ] Protocollo comunicazione implementato
- [ ] Client GUI e CLI funzionanti
- [ ] Gestione errori di rete
- [ ] Test con 10+ client concorrenti
- [ ] Documentazione protocollo
- [ ] Load testing e benchmarking

#### Story Points Stimati: 21

---

## 📈 Diagramma Dipendenze Sprint

```
Sprint 0 (Base)
   │
   ├──► Sprint 1 (Algoritmo QT)
   │       │
   │       ├──► Sprint 2 (Persistenza I/O)
   │       │       │
   │       │       ├──► Sprint 3 (Ottimizzazioni)
   │       │       │       │
   │       │       │       └──► Sprint 7 (JDBC)
   │       │       │
   │       │       ├──► Sprint 5 (GUI)
   │       │       │
   │       │       └──► Sprint 8 (Client-Server)
   │       │
   │       ├──► Sprint 4 (Attributi Continui)
   │       │
   │       └──► Sprint 6 (Metriche Qualità)
   │
   └──► Sprint 4 (Attributi Continui)
```

### Legenda:
- **Sprint 0-1:** Core (prerequisiti per tutto)
- **Sprint 2:** Abilitatore per Sprint 3, 5, 7, 8
- **Sprint 3-4-6:** Possono procedere in parallelo dopo Sprint 1
- **Sprint 5-7-8:** Richiedono Sprint 2

---

## 🎯 Priorità e Raccomandazioni

### Percorso Consigliato

#### Fase 1: Fondamentali (4 settimane)
1. ✅ Sprint 0 - Struttura Base
2. ✅ Sprint 1 - Algoritmo QT
3. **Sprint 2 - Persistenza I/O** ← Prossimo
4. **Sprint 4 - Attributi Continui**

**Rationale:** Completare funzionalità core prima di interfacce e integrazioni

#### Fase 2: Miglioramenti (3 settimane)
5. **Sprint 3 - Ottimizzazioni Performance**
6. **Sprint 6 - Metriche Qualità**

**Rationale:** Migliorare qualità e performance algoritmo

#### Fase 3: Interfacce (4 settimane)
7. **Sprint 5 - GUI**
8. **Sprint 7 - JDBC** o **Sprint 8 - Client-Server**

**Rationale:** Aggiungere interfacce utente e integrazioni esterne

### Priorità per Ambito Accademico

**Obbligatori:**
- ✅ Sprint 0 - Struttura Base
- ✅ Sprint 1 - Algoritmo QT
- 🔴 Sprint 2 - Persistenza I/O

**Fortemente Raccomandati:**
- 🟡 Sprint 4 - Attributi Continui
- 🟡 Sprint 6 - Metriche Qualità

**Opzionali/Bonus:**
- 🟢 Sprint 3 - Ottimizzazioni
- 🟢 Sprint 5 - GUI
- 🟢 Sprint 7 - JDBC
- 🟢 Sprint 8 - Client-Server

### Priorità per Progetto Reale

**Must-Have:**
- ✅ Sprint 0-1
- 🔴 Sprint 2 - Persistenza I/O
- 🔴 Sprint 3 - Ottimizzazioni Performance
- 🔴 Sprint 6 - Metriche Qualità

**Should-Have:**
- 🟡 Sprint 4 - Attributi Continui
- 🟡 Sprint 5 - GUI

**Nice-to-Have:**
- 🟢 Sprint 7 - JDBC
- 🟢 Sprint 8 - Client-Server

---

## 📊 Metriche Complessive Progetto

### Story Points Totali
| Fase | Sprint | Story Points | Percentuale |
|------|--------|--------------|-------------|
| Core | 0 | 22 | 15.3% |
| Core | 1 | 18 | 12.5% |
| Estensioni | 2 | 13 | 9.0% |
| Estensioni | 3 | 21 | 14.6% |
| Estensioni | 4 | 13 | 9.0% |
| Interfacce | 5 | 21 | 14.6% |
| Interfacce | 6 | 13 | 9.0% |
| Integrazione | 7 | 21 | 14.6% |
| Integrazione | 8 | 21 | 14.6% |
| **TOTALE** | | **144** | **100%** |

### Classi Implementate/Pianificate
- **Attualmente:** 12 classi (Sprint 0-1)
- **Sprint 2-4:** +8 classi
- **Sprint 5-8:** +15 classi
- **Totale previsto:** ~35 classi

### Linee di Codice Stimate
- **Attualmente:** ~900 LOC
- **Fine Fase 2:** ~2,500 LOC
- **Fine Progetto:** ~5,000 LOC

---

## 🛠️ Strumenti e Tecnologie per Sprint

| Sprint | Tecnologie Principali | Librerie/Framework |
|--------|----------------------|-------------------|
| 0-1 | Java vanilla, OOP | Nessuna |
| 2 | Java I/O, Serialization | org.json, Gson (opt) |
| 3 | Multithreading, Collections | Java ExecutorService |
| 4 | Math, Statistics | Apache Commons Math (opt) |
| 5 | Swing, JavaFX | JFreeChart, Java2D |
| 6 | Statistics | Apache Commons Math |
| 7 | JDBC, SQL | HikariCP, MySQL Connector |
| 8 | Socket, Networking | Netty (opt) |

---

## 📝 Note di Sviluppo

### Convenzioni Generali
- **Linguaggio:** Java (JDK 8+)
- **Paradigma:** Object-Oriented Programming
- **Documentazione:** Javadoc per tutti i metodi pubblici
- **Testing:** JUnit per unit test (da Sprint 2)
- **Version Control:** Git con branch per ogni sprint

### Standard di Codice
- **Naming:** camelCase per metodi/variabili, PascalCase per classi
- **Visibilità:** public per API, package-private per classi interne
- **Commenti:** Javadoc + inline comments per logica complessa
- **Formattazione:** 4 spazi indentazione, max 120 caratteri per riga

### Gestione Repository
```
MAP/
├── src/           # Codice sorgente
├── docs/          # Documentazione
│   ├── sprints/   # Documentazione sprint individuali
│   └── SPRINT_ROADMAP.md  # Questo file
├── test/          # Test unitari (da Sprint 2)
├── data/          # Dataset di esempio (da Sprint 2)
├── lib/           # Librerie esterne (da Sprint 2+)
└── README.md      # Panoramica progetto
```

---

## 🔄 Processo di Sviluppo

### Per Ogni Sprint

#### 1. Pianificazione
- Leggere documentazione sprint in `docs/sprints/SPRINT_X.md`
- Identificare classi e metodi da implementare
- Stimare complessità

#### 2. Implementazione
- Seguire design pattern esistenti
- Scrivere codice incrementalmente
- Testare ogni classe individualmente

#### 3. Testing
- Compilare: `javac *.java`
- Eseguire: `java MainTest`
- Verificare output atteso

#### 4. Documentazione
- Aggiornare javadoc
- Documentare modifiche significative
- Aggiornare README se necessario

#### 5. Commit
```bash
git add .
git commit -m "Complete Sprint X: [descrizione]"
git push
```

#### 6. Retrospettiva
- Aggiornare sezione "Retrospettiva" in sprint doc
- Identificare action items per sprint futuri

---

## 📚 Risorse e Riferimenti

### Algoritmi
- **Quality Threshold:** Heyer et al. (1999) "Exploring Expression Data"
- **K-Means:** Lloyd's algorithm
- **Distanza Hamming:** Per attributi discreti/categorici

### Dataset Standard
- **PlayTennis:** Dataset attuale (14 tuple)
- **Iris:** 150 tuple, 4 attributi continui
- **Wine:** 178 tuple, 13 attributi continui
- **UCI ML Repository:** https://archive.ics.uci.edu/ml/

### Java Documentation
- **JDK 8:** https://docs.oracle.com/javase/8/docs/api/
- **Swing Tutorial:** https://docs.oracle.com/javase/tutorial/uiswing/
- **JDBC Tutorial:** https://docs.oracle.com/javase/tutorial/jdbc/

---

## ❓ FAQ

### Q: Quale sprint dovremmo implementare dopo Sprint 1?
**A:** Sprint 2 (Persistenza I/O) è altamente raccomandato in quanto abilita molti altri sprint e aggiunge funzionalità critiche per uso reale.

### Q: Possiamo implementare sprint in ordine diverso?
**A:** Sì, rispettando le dipendenze nel diagramma. Ad esempio, Sprint 4 e Sprint 6 possono essere fatti prima di Sprint 2 se desiderato.

### Q: Quali sprint sono obbligatori per corso accademico?
**A:** Tipicamente Sprint 0-1 (core) + almeno 1-2 tra Sprint 2, 4, 6. Verificare con docente.

### Q: Quanto tempo ci vuole per completare tutti gli sprint?
**A:** Stima: 12-14 settimane lavorando part-time (10-15 ore/settimana). Sprint 0-1 già completati (2 settimane).

### Q: Servono librerie esterne?
**A:** Sprint 0-1 no. Sprint 2+ potrebbero usare librerie per JSON, GUI, JDBC. Tutte opzionali e sostituibili.

### Q: Come gestiamo dataset più grandi?
**A:** Sprint 2 (caricamento file) + Sprint 3 (ottimizzazioni). Combinati permettono dataset 1000+ tuple.

---

## 📅 Changelog Roadmap

| Data | Versione | Modifiche |
|------|----------|-----------|
| 2025-11-06 | 1.0 | Creazione roadmap completa Sprint 0-8 |

---

**Fine Roadmap**

Per dettagli implementativi di ogni sprint, consultare:
- [`docs/sprints/SPRINT_0.md`](sprints/SPRINT_0.md) - Struttura Base
- [`docs/sprints/SPRINT_1.md`](sprints/SPRINT_1.md) - Algoritmo QT
- Sprint futuri: Da creare al momento dell'implementazione
