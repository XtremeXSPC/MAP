# 3. Interfaccia Grafica (GUI)

## 3.1 Panoramica Interfaccia

La GUI è costruita con JavaFX e segue il pattern MVC (Model-View-Controller).

### Avvio Applicazione GUI

```bash
cd MAP/qtGUI/bin
java application.Main
```

<!-- [IMMAGINE]: Screenshot schermata principale GUI - dimensione: 1200x800 -->
<!-- Mostra: finestra completa con barra laterale sinistra contenente bottoni Home/Clustering/Results/Settings -->

### Layout Generale

```
┌──────────────────────────────────────────────────┐
│  Quality Threshold Clustering           [- □ X]  │
├──────────┬───────────────────────────────────────┤
│          │                                       │
│  Home    │                                       │
│          │                                       │
│ Cluster  │     AREA CONTENUTO PRINCIPALE         │
│          │       (cambia per schermata)          │
│ Results  │                                       │
│          │                                       │
│ Settings │                                       │
│          │                                       │
├──────────┴───────────────────────────────────────┤
│  Status: Ready                      v1.0         │
└──────────────────────────────────────────────────┘
```

### Barra Laterale (Sidebar)

| Bottone        | Funzione                  | Icona       |
| -------------- | ------------------------- | ----------- |
| **Home**       | Caricamento dataset       | Cartella    |
| **Clustering** | Configurazione e avvio    | Grafico     |
| **Results**    | Visualizzazione risultati | Tabella     |
| **Settings**   | Configurazione app        | Ingranaggio |

### Barra di Stato (Status Bar)

- **Sinistra**: Messaggi stato operazioni
- **Destra**: Versione applicazione

---

## 3.2 Schermata Home

La schermata Home permette di caricare dataset da diverse sorgenti.

<!-- [IMMAGINE]: Home screen con dataset selector - dimensione: 800x600 -->
<!-- Mostra: tre card con bottoni "Load CSV", "Load from Database", "Load PlayTennis Example" -->

### Caricamento da File CSV

#### Passo 1: Click "Load CSV File"

Si apre dialog selezione file.

#### Passo 2: Seleziona File CSV

Formato CSV richiesto:
```csv
attribute1,attribute2,attribute3
value1,value2,value3
value4,value5,value6
...
```

**Esempio** (`iris.csv`):
```csv
sepal_length,sepal_width,petal_length,petal_width,species
5.1,3.5,1.4,0.2,setosa
4.9,3.0,1.4,0.2,setosa
7.0,3.2,4.7,1.4,versicolor
...
```

<!-- [IMMAGINE]: Esempio file CSV input - dimensione: 600x300 -->
<!-- Mostra: contenuto file CSV con header evidenziato -->

#### Passo 3: Verifica Caricamento

Dopo caricamento appare:
- **Messaggio**: "Dataset loaded: N tuples, M attributes"
- **Preview**: Tabella con prime 10 righe

<!-- [IMMAGINE]: Dataset preview dialog - dimensione: 800x600 -->
<!-- Mostra: TableView con dati CSV, colonne scrollabili -->

#### Gestione Errori CSV

| Errore                 | Causa                     | Soluzione                     |
| ---------------------- | ------------------------- | ----------------------------- |
| "Invalid CSV format"   | Header mancante           | Aggiungi riga intestazione    |
| "Inconsistent columns" | Righe con colonne diverse | Uniforma numero colonne       |
| "File too large"       | File > 10MB               | Riduci dataset o usa database |

### Caricamento da Database

#### Passo 1: Click "Load from Database"

Si apre dialog configurazione connessione.

<!-- [IMMAGINE]: Database connection dialog - dimensione: 700x500 -->
<!-- Mostra: form con campi Host, Port, Database, User, Password, Table -->

#### Passo 2: Inserisci Credenziali

| Campo        | Esempio      | Descrizione                       |
| ------------ | ------------ | --------------------------------- |
| **DB Type**  | MySQL        | Tipo database (MySQL, PostgreSQL) |
| **Host**     | localhost    | Indirizzo server database         |
| **Port**     | 3306         | Porta MySQL (default 3306)        |
| **Database** | qtclustering | Nome database                     |
| **Username** | root         | Utente database                   |
| **Password** | •••••        | Password utente                   |
| **Table**    | playtennis   | Nome tabella da caricare          |

#### Passo 3: Test Connection

Click "Test Connection" per verificare credenziali.

Output:
- **Successo**: "Connection successful!"
- **Errore**: "Connection failed: <dettaglio errore>"

#### Passo 4: Load Data

Click "Load Data" per importare tabella.

**Processo**:
1. Connessione a database
2. Estrazione schema (metadati colonne)
3. Lettura dati (SELECT * FROM table)
4. Inferenza tipi attributi (discrete/continuous)
5. Costruzione oggetto Data

**Tempo stimato**: 2-10 secondi (dipende da dimensione tabella)

#### Gestione Errori Database

| Errore               | Causa                    | Soluzione                       |
| -------------------- | ------------------------ | ------------------------------- |
| "Access denied"      | Credenziali errate       | Verifica user/password          |
| "Unknown database"   | Database inesistente     | Crea database prima             |
| "Table not found"    | Nome tabella errato      | Verifica nome (case-sensitive!) |
| "Connection timeout" | Server non raggiungibile | Verifica host e firewall        |

### Caricamento Dataset Esempio (PlayTennis)

#### Uso

Click "Load PlayTennis Example" per caricare dataset embedded.

**Caratteristiche**:
- **Tuple**: 14
- **Attributi**: 5 (tutti discreti)
- **Dominio**: Meteo e decisione giocare tennis
- **Tempo caricamento**: Istantaneo (< 1 secondo)

**Attributi**:
- `outlook`: {sunny, overcast, rain}
- `temperature`: {hot, mild, cool}
- `humidity`: {high, normal}
- `wind`: {weak, strong}
- `playtennis`: {yes, no}

#### Quando Usare

- Demo e tutorial
- Test rapido funzionalità
- Apprendimento algoritmo
- Verifica installazione

### Anteprima Dataset

Dopo caricamento (da qualsiasi sorgente), è disponibile "Preview Dataset".

#### Funzionalità Anteprima

| Elemento      | Descrizione                     |
| ------------- | ------------------------------- |
| **TableView** | Griglia dati interattiva        |
| **Colonne**   | Nome attributo + tipo (D/C)     |
| **Righe**     | Max 100 tuple (per performance) |
| **Scroll**    | Orizzontale e verticale         |
| **Sort**      | Click su header colonna         |
| **Filtro**    | Casella ricerca testuale        |

<!-- [IMMAGINE]: Dataset preview dialog - dimensione: 800x600 -->
<!-- Mostra: TableView con colonne "Outlook", "Temperature", etc. e dati -->

#### Statistiche Dataset

Pannello statistiche mostra:
- Numero tuple totali
- Numero attributi
- Tipi attributi (N discreti, M continui)
- Valori distinti per attributo discreto
- Range [min, max] per attributo continuo

---

## 3.3 Schermata Clustering

Schermata per configurare parametri e avviare clustering.

<!-- [IMMAGINE]: Clustering configuration panel - dimensione: 800x600 -->
<!-- Mostra: slider radius, info box, bottone "Start Clustering", progress bar -->

### Configurazione Radius

#### Slider Radius

| Parametro   | Valore | Comportamento                       |
| ----------- | ------ | ----------------------------------- |
| **Min**     | 0.0    | Cluster molto piccoli (max qualità) |
| **Max**     | 1.0    | Cluster molto grandi (min qualità)  |
| **Default** | 0.5    | Bilanciato                          |
| **Step**    | 0.01   | Precisione 2 decimali               |

#### Interpretazione Radius

```
Radius 0.0 → Cluster identici (distanza = 0)
           → Molti cluster piccoli (1-3 tuple)
           → Alta qualità, bassa copertura

Radius 0.5 → Cluster moderati
           → Numero medio cluster (5-10)
           → Bilanciamento qualità/copertura

Radius 1.0 → Cluster aggregati
           → Pochi cluster grandi
           → Bassa qualità, alta copertura
```

#### Info Box Radius

Pannello informativo mostra:
- **Valore corrente**: es. "0.35"
- **Stima cluster**: "5-8 cluster attesi" (euristica)
- **Tempo stimato**: basato su dimensione dataset
- **Raccomandazione**: suggerimento basato su dataset

### Avvio Clustering

#### Passo 1: Verifica Prerequisiti

Prima di "Start Clustering", verifica:
- [ ] Dataset caricato (indicatore verde)
- [ ] Radius impostato (valore visibile)
- [ ] Server connesso (status bar "Connected")

#### Passo 2: Click "Start Clustering"

**Processo**:
1. Validazione configurazione
2. Connessione a server (se non già connesso)
3. Invio dataset e radius
4. Esecuzione algoritmo QT (server-side)
5. Ricezione risultati
6. Navigazione automatica a schermata Results

#### Progress Indicator

Durante esecuzione:
- **Progress Bar**: Indeterminata (algoritmo non prevedibile)
- **Status**: "Clustering in progress..."
- **Tempo trascorso**: Timer in secondi
- **Cancellazione**: Bottone "Cancel" disponibile

<!-- [IMMAGINE]: Clustering in progress - dimensione: 600x200 -->
<!-- Mostra: progress bar animata, timer, bottone cancel -->

#### Tempi di Esecuzione Tipici

| Dataset Size          | Radius | Tempo Atteso  |
| --------------------- | ------ | ------------- |
| 14 tuple (PlayTennis) | 0.0    | < 1 secondo   |
| 100 tuple             | 0.5    | 2-5 secondi   |
| 1000 tuple            | 0.5    | 30-60 secondi |
| 5000 tuple            | 0.5    | 5-10 minuti   |

**Nota**: Complessità O(k × n²) rende algoritmo lento per dataset grandi (> 10000 tuple).

### Configurazioni Avanzate (Opzionale)

Espandi pannello "Advanced Options" per:

| Opzione              | Default | Descrizione                          |
| -------------------- | ------- | ------------------------------------ |
| **Distance Metric**  | Hamming | Metrica distanza (Hamming/Euclidean) |
| **Enable Cache**     | ON      | Cache distanze calcolate (speedup)   |
| **Thread Pool Size** | 4       | Thread paralleli (se supportato)     |
| **Timeout**          | 300s    | Timeout massimo esecuzione           |

### Gestione Errori Clustering

| Errore                 | Causa                 | Soluzione                         |
| ---------------------- | --------------------- | --------------------------------- |
| "No dataset loaded"    | Dataset non caricato  | Carica prima dataset              |
| "Server not reachable" | Server offline        | Avvia server                      |
| "Clustering timeout"   | Dataset troppo grande | Riduci dataset o aumenta timeout  |
| "Out of memory"        | RAM insufficiente     | Aumenta heap JVM o riduci dataset |

---

## 3.4 Schermata Risultati

La schermata Risultati visualizza i cluster ottenuti dall'algoritmo QT e fornisce strumenti avanzati per l'analisi e l'interpretazione dei dati.

### Panoramica Interfaccia Risultati

Dopo il completamento del clustering, l'applicazione naviga automaticamente alla schermata Results, che presenta:

- **Pannello sinistro**: TreeView gerarchica dei cluster
- **Pannello centrale**: Area dettagli con tab multiple
- **Pannello superiore**: Riepilogo statistiche cluster
- **Barra azioni**: Bottoni per visualizzazione, statistiche, export

<!-- [IMMAGINE]: Results screen overview - dimensione: 1200x800 -->
<!-- Mostra: layout completo con TreeView a sinistra, tab al centro, statistiche in alto -->

### TreeView Cluster Gerarchici

L'albero dei cluster organizza i risultati in modo intuitivo:

```
└─ Clustering Results (Radius: 0.35)
   ├─ Cluster 1 (8 tuple) ✓
   │  ├─ Centroide: (sunny, mild, high, weak, no)
   │  ├─ Tuple 1: (sunny, hot, high, weak, no)
   │  ├─ Tuple 8: (sunny, mild, high, weak, no)
   │  └─ ...
   ├─ Cluster 2 (4 tuple)
   └─ Cluster 3 (2 tuple)
```

#### Funzionalità TreeView

| Azione                   | Comportamento                                    |
| ------------------------ | ------------------------------------------------ |
| **Click su cluster**     | Mostra dettagli nel pannello centrale           |
| **Double-click tuple**   | Evidenzia nei grafici                            |
| **Right-click**          | Menu contestuale (copia, export singolo cluster) |
| **Espandi/Comprimi All** | Bottoni dedicati nella toolbar                   |

<!-- [IMMAGINE]: Cluster TreeView con menu contestuale - dimensione: 400x600 -->
<!-- Mostra: TreeView espanso con menu right-click su cluster -->

### Tab Dettagli Cluster

Il pannello centrale presenta 3 tab per analisi dettagliate:

#### Tab "Summary" - Riepilogo Cluster

Informazioni generali sul cluster selezionato:

- **ID Cluster**: Numero identificativo
- **Dimensione**: Numero tuple appartenenti
- **Centroide**: Valori prototipo cluster
- **Average Distance**: Distanza media intra-cluster
- **Compattezza**: Metrica qualità (1.0 = ottima, 0.0 = dispersa)

**Esempio output**:
```
Cluster ID: 3
Dimensione: 12 tuple
Centroide: (overcast, mild, normal, weak, yes)
Distanza Media: 0.183
Compattezza: 0.817
```

<!-- [IMMAGINE]: Summary tab details - dimensione: 800x400 -->
<!-- Mostra: pannello Summary con statistiche formattate -->

#### Tab "Tuples" - Elenco Tuple

Tabella interattiva con tutte le tuple del cluster:

| Tuple ID | Outlook  | Temperature | Humidity | Wind   | PlayTennis | Distance to Centroid |
| -------- | -------- | ----------- | -------- | ------ | ---------- | -------------------- |
| 3        | overcast | hot         | high     | weak   | yes        | 0.20                 |
| 7        | overcast | cool        | normal   | strong | yes        | 0.25                 |
| ...      | ...      | ...         | ...      | ...    | ...        | ...                  |

**Funzionalità tabella**:
- **Ordinamento**: Click su header colonna
- **Filtro**: Campo ricerca testuale
- **Highlight**: Celle diverse da centroide evidenziate
- **Export**: Esporta solo questo cluster come CSV

<!-- [IMMAGINE]: Tuples table with sorting - dimensione: 900x500 -->
<!-- Mostra: TableView con righe ordinate per distanza -->

#### Tab "Statistics" - Statistiche Dettagliate

Dashboard con grafici e metriche avanzate:

**1. Distribuzione Attributi**
- Grafici a barre per attributi discreti
- Istogrammi per attributi continui
- Percentuali valori dominanti

**2. Qualità Cluster**
- Silhouette coefficient (se disponibile)
- Cohesion score
- Separation index

**3. Confronto con Altri Cluster**
- Distanze inter-cluster
- Overlap analysis

<!-- [IMMAGINE]: Statistics dashboard - dimensione: 1000x700 -->
<!-- Mostra: 3-4 grafici organizzati in grid layout -->

### Pannello Statistiche Globali

Il pannello superiore mostra metriche aggregate su tutti i cluster:

| Metrica                | Valore    | Descrizione                                 |
| ---------------------- | --------- | ------------------------------------------- |
| **Total Clusters**     | 7         | Numero cluster trovati                      |
| **Total Tuples**       | 14        | Tuple totali nel dataset                    |
| **Radius**             | 0.35      | Parametro radius utilizzato                 |
| **Avg Cluster Size**   | 2.0       | Dimensione media cluster                    |
| **Largest Cluster**    | 5 tuple   | Cluster più grande                          |
| **Smallest Cluster**   | 1 tuple   | Cluster più piccolo                         |
| **Execution Time**     | 1.2s      | Tempo impiegato dall'algoritmo              |
| **Overall Cohesion**   | 0.782     | Compattezza media (1.0 = massima qualità)   |

<!-- [IMMAGINE]: Global statistics panel - dimensione: 1100x150 -->
<!-- Mostra: pannello orizzontale con 8 card metriche -->

### Visualizzazione Grafica - Convex Hull Plot

Il nuovo sistema di visualizzazione utilizza **inviluppi convessi** (convex hulls) per rappresentare i cluster in modo chiaro ed efficace.

#### Caratteristiche Visualizzazione

Rispetto alla visualizzazione classica a punti sparsi, il convex hull offre:

**Vantaggi**:
- **Delimitazione chiara**: Ogni cluster è racchiuso in un poligono colorato
- **Percezione immediata**: Forma e estensione del cluster visibili a colpo d'occhio
- **Meno confusione**: Niente linee intrecciate tra punti
- **Densità visibile**: Cluster compatti hanno hull piccoli, cluster dispersi hanno hull grandi

<!-- [IMMAGINE]: Convex hull visualization comparison - dimensione: 1200x500 -->
<!-- Mostra: split-screen con visualizzazione classica (sinistra) e convex hull (destra) -->

#### Avvio Visualizzazione

**Passo 1**: Nella schermata Results, click su bottone **"Visualize"**

**Passo 2**: Si apre finestra dedicata con:
- Grafico scatter plot principale
- Toolbar controlli assi
- Checkbox "Convex Hull" (abilitata di default)
- Bottoni export immagine

<!-- [IMMAGINE]: Chart viewer window - dimensione: 900x700 -->
<!-- Mostra: finestra con scatter plot, toolbar in alto, bottoni in basso -->

#### Configurazione Assi

La toolbar superiore permette di selezionare quali attributi visualizzare:

| Controllo        | Funzione                                       |
| ---------------- | ---------------------------------------------- |
| **Asse X**       | ComboBox per scegliere attributo (es. Sepal Length) |
| **Asse Y**       | ComboBox per scegliere attributo (es. Petal Width)   |
| **Convex Hull**  | Checkbox per abilitare/disabilitare hull             |
| **Refresh**      | Bottone per aggiornare grafico con nuovi assi        |

**Esempio workflow**:
1. Seleziona "sepal_length" su asse X
2. Seleziona "petal_width" su asse Y
3. Click "Refresh" → grafico si aggiorna immediatamente
4. Toggle checkbox "Convex Hull" per confrontare stili

<!-- [IMMAGINE]: Axis selector toolbar - dimensione: 800x100 -->
<!-- Mostra: toolbar con 2 ComboBox, checkbox, bottone refresh -->

#### Interpretazione Grafico

Il grafico mostra:

**Elementi visivi**:
- **Poligoni colorati**: Convex hull di ogni cluster
- **Punti interni**: Tuple appartenenti al cluster (marker piccoli, cerchi)
- **Centroidi**: Marcati con croce nera di dimensione maggiore
- **Legenda**: Elenco cluster con colori corrispondenti

**Colori cluster**: Palette automatica con 12 colori distinti

| Cluster | Colore        | RGB           |
| ------- | ------------- | ------------- |
| 1       | Blu           | (0, 0, 255)   |
| 2       | Rosso         | (255, 0, 0)   |
| 3       | Verde         | (0, 200, 0)   |
| 4       | Arancione     | (255, 165, 0) |
| 5       | Viola         | (128, 0, 128) |
| ...     | ...           | ...           |

<!-- [IMMAGINE]: Scatter plot with convex hulls - dimensione: 800x600 -->
<!-- Mostra: grafico completo con 3-4 cluster, hull colorati, punti e centroidi evidenti -->

#### Export Grafici

La visualizzazione può essere esportata in alta qualità:

| Formato    | Risoluzione | Qualità | Uso Consigliato    |
| ---------- | ----------- | ------- | ------------------ |
| **PNG**    | 800x600     | Media   | Preview, slide     |
| **PNG HD** | 1920x1440   | Alta    | Pubblicazioni, HD  |

**Passo export**:
1. Click "Esporta PNG" o "Esporta PNG HD"
2. Scegli percorso di salvataggio
3. Conferma → file salvato con timestamp

<!-- [IMMAGINE]: Export dialog - dimensione: 500x300 -->
<!-- Mostra: file chooser con preview nome file generato automaticamente -->

#### Casi d'Uso Visualizzazione

**Scenario 1: Dataset Iris (4 attributi continui)**
- Seleziona assi: sepal_length vs petal_length
- Risultato: Separazione netta tra cluster setosa/versicolor/virginica
- Convex hull evidenzia sovrapposizione tra versicolor e virginica

**Scenario 2: Dataset PlayTennis (5 attributi discreti)**
- Attributi convertiti in valori ordinali per plotting
- Convex hull mostra raggruppamenti condizioni meteo
- Utile per identificare pattern (es. "rain + normal humidity + weak wind → play")

<!-- [IMMAGINE]: Iris convex hull example - dimensione: 800x600 -->
<!-- Mostra: Iris dataset con 3 hull ben separati (setosa isolata, versicolor/virginica parzialmente sovrapposte) -->

### Dataset Standard: Iris

L'applicazione include il **dataset Iris**, uno standard internazionale per benchmark di clustering.

#### Caratteristiche Iris Dataset

| Proprietà            | Valore                                              |
| -------------------- | --------------------------------------------------- |
| **Tuple**            | 150                                                 |
| **Attributi**        | 4 continui + 1 categorico (species)                |
| **Cluster Reali**    | 3 (setosa, versicolor, virginica)                   |
| **Separabilità**     | Setosa completamente separata, altre due parzialmente sovrapposte |
| **Origine**          | Fisher (1936), UCI Machine Learning Repository      |
| **Licenza**          | CC BY 4.0 (pubblico dominio)                        |

**Attributi**:
1. `sepal_length`: Lunghezza sepalo (cm) - range [4.3, 7.9]
2. `sepal_width`: Larghezza sepalo (cm) - range [2.0, 4.4]
3. `petal_length`: Lunghezza petalo (cm) - range [1.0, 6.9]
4. `petal_width`: Larghezza petalo (cm) - range [0.1, 2.5]
5. `species`: Specie iris (categorico) - {setosa, versicolor, virginica}

#### Caricamento Iris Dataset

**Home Screen → Selezione Sorgente**:

Nella schermata Home, la ComboBox "Sorgente Dati" presenta ora una nuova opzione:

```
┌───────────────────────────────────┐
│ Sorgente:  [▼]                    │
├───────────────────────────────────┤
│ • Hardcoded (PlayTennis)          │
│ • Dataset Standard (Iris)   ← NEW │
│ • File CSV                        │
│ • Database                        │
└───────────────────────────────────┘
```

**Passo per passo**:
1. Seleziona "Dataset Standard (Iris)" dalla ComboBox
2. Click "Anteprima Dataset" per visualizzare dati (opzionale)
3. Imposta parametro radius (consigliato: 0.4-0.6 per Iris)
4. Click "Avvia Clustering"

<!-- [IMMAGINE]: Home screen con Iris selezionato - dimensione: 800x600 -->
<!-- Mostra: ComboBox con "Dataset Standard (Iris)" selezionato, preview button abilitato -->

#### Clustering Iris: Risultati Attesi

Con radius ottimale (0.5), l'algoritmo QT produce:

**Risultato tipico**:
- **Cluster trovati**: 3-4
- **Mapping cluster reali**:
  - Cluster 1 → Setosa (50 tuple, compatto)
  - Cluster 2 → Versicolor (30-40 tuple)
  - Cluster 3 → Virginica (30-40 tuple)
  - Cluster 4 (opzionale) → Overlap versicolor/virginica

**Tempo esecuzione**: 1-3 secondi (150 tuple, complessità O(n²))

<!-- [IMMAGINE]: Iris clustering results - dimensione: 900x700 -->
<!-- Mostra: Results screen con 3 cluster, TreeView espanso, grafico convex hull -->

#### Interpretazione Iris con Convex Hull

Visualizzazione consigliata per Iris:

**Assi migliori**:
- X: `petal_length` (massima separabilità)
- Y: `petal_width` (correlato con petal_length)

**Cosa aspettarsi**:
- **Setosa**: Hull piccolo, isolato in basso-sinistra (petali piccoli)
- **Versicolor**: Hull medio, zona centrale
- **Virginica**: Hull più grande, in alto-destra (petali grandi)
- **Sovrapposizione**: Bordi hull versicolor/virginica si toccano → ambiguità classificazione

<!-- [IMMAGINE]: Iris convex hull petal_length vs petal_width - dimensione: 800x600 -->
<!-- Mostra: grafico con setosa ben separata, versicolor e virginica con hull parzialmente sovrapposti -->

#### Uso Didattico Iris

Il dataset Iris è ideale per:

**Apprendimento**:
- Comprendere come radius influenza numero cluster
- Sperimentare con visualizzazioni diverse (cambiando assi)
- Confrontare cluster QT con classi reali (ground truth)

**Validazione Algoritmo**:
- Benchmark performance QT
- Test correttezza implementazione
- Confronto con altri algoritmi (k-means, DBSCAN)

**Dimostrazione**:
- Presentazioni accademiche
- Tutorial clustering
- Esempi documentazione

### Azioni Risultati

La barra azioni nella schermata Results offre diversi bottoni per interagire con i risultati:

| Bottone         | Funzione                                  | Shortcut   |
| --------------- | ----------------------------------------- | ---------- |
| **Visualize**   | Apre finestra scatter plot convex hull    | `Ctrl+G`   |
| **Statistics**  | Mostra dashboard statistiche avanzate     | `Ctrl+I`   |
| **Export**      | Esporta risultati (CSV, TXT, ZIP)         | `Ctrl+E`   |
| **Save**        | Salva clustering (file .dmp serializzato) | `Ctrl+S`   |
| **New Analysis**| Torna a Home per nuova analisi            | `Ctrl+N`   |

<!-- [IMMAGINE]: Results action bar - dimensione: 800x80 -->
<!-- Mostra: toolbar con 5 bottoni, icone e tooltip -->

### Export Risultati

Il sistema offre 4 formati di export:

#### Export CSV - Formato Tabulare

**File generato**: `clusters_YYYYMMDD_HHMMSS.csv`

**Struttura**:
```csv
ClusterID,TupleID,DistanceToCentroid,Attribute1,Attribute2,...
1,0,0.00,sunny,hot,high,weak,no
1,7,0.20,sunny,mild,high,weak,no
2,2,0.00,overcast,hot,high,weak,yes
...
```

**Uso**: Import in Excel, R, Python per analisi ulteriori

#### Export TXT - Report Testuale

**File generato**: `report_YYYYMMDD_HHMMSS.txt`

**Contenuto**:
```
============================================
  QUALITY THRESHOLD CLUSTERING REPORT
============================================

CONFIGURATION
-------------
Radius: 0.35
Dataset: PlayTennis (14 tuples, 5 attributes)
Execution Time: 0.85 seconds

RESULTS SUMMARY
---------------
Total Clusters: 7
Average Cluster Size: 2.0
Largest Cluster: 5 tuples (Cluster 1)
Smallest Cluster: 1 tuple (Cluster 6)

CLUSTER DETAILS
---------------

Cluster 1 (5 tuples)
--------------------
Centroid: (sunny, mild, high, weak, no)
Avg Distance: 0.13
Tuples:
  - Tuple 0: (sunny, hot, high, weak, no) [dist: 0.20]
  - Tuple 7: (sunny, mild, high, weak, no) [dist: 0.00]
  ...
```

**Uso**: Documentazione, report testuali, archiviazione

#### Export ZIP - Pacchetto Completo

**File generato**: `clustering_complete_YYYYMMDD_HHMMSS.zip`

**Contenuto ZIP**:
```
clustering_complete_20251109_143052.zip
├── clusters.csv         # Dati tabulari
├── report.txt           # Report testuale
├── clustering.dmp       # File serializzato (per reload)
└── README.txt           # Istruzioni uso file
```

**Uso**: Archiviazione completa, condivisione, backup

#### Export DMP - Serializzazione Java

**File generato**: `clustering_YYYYMMDD_HHMMSS.dmp`

**Contenuto**: Oggetto `ClusteringResult` serializzato con Java Serialization

**Uso**: Ricaricamento futuro risultati senza rieseguire clustering

**Come ricaricare**:
1. Home → "Load Clustering from File"
2. Seleziona file .dmp
3. Risultati ripristinati istantaneamente

<!-- [IMMAGINE]: Export formats comparison - dimensione: 800x400 -->
<!-- Mostra: 4 card con icone formati, descrizione breve, dimensione file tipo -->

### Gestione Errori Visualizzazione

| Errore                       | Causa                         | Soluzione                                 |
| ---------------------------- | ----------------------------- | ----------------------------------------- |
| "Insufficient attributes"    | Dataset con < 2 attributi     | Serve minimo 2 attributi per scatter plot |
| "Convex hull computation failed" | Cluster < 3 punti         | Normale, hull skippato per cluster piccoli |
| "Export failed"              | Permessi scrittura negati     | Verifica permessi cartella destinazione   |
| "Chart rendering timeout"    | Dataset molto grande (> 5000) | Riduci dataset o usa filtri               |

---

## 3.5 Schermata Impostazioni

La schermata Settings permette di configurare preferenze applicazione e parametri avanzati.

<!-- [IMMAGINE]: Settings screen overview - dimensione: 900x700 -->
<!-- Mostra: finestra Settings con tab Appearance, Database, Advanced -->

### Tab "Appearance" - Aspetto

Personalizzazione interfaccia grafica:

| Setting         | Opzioni              | Descrizione                      |
| --------------- | -------------------- | -------------------------------- |
| **Theme**       | Light / Dark         | Tema colori applicazione         |
| **Font Size**   | Small / Medium / Large | Dimensione testo globale      |
| **Language**    | English / Italian    | Lingua interfaccia (futuro)      |

<!-- [IMMAGINE]: Appearance tab - dimensione: 700x500 -->
<!-- Mostra: toggle theme, slider font size, combo language -->

### Tab "Database" - Configurazione Database

Parametri connessione database predefiniti:

| Campo       | Default   | Descrizione               |
| ----------- | --------- | ------------------------- |
| **Host**    | localhost | Indirizzo server MySQL    |
| **Port**    | 3306      | Porta MySQL               |
| **Database**| MapDB     | Nome database default     |
| **Username**| MapUser   | Utente default            |
| **Password**| •••       | Password (salvata cifrata)|

**Test Connection**: Bottone per verificare credenziali senza caricare dati.

<!-- [IMMAGINE]: Database settings tab - dimensione: 700x500 -->
<!-- Mostra: form con campi database, bottone "Test Connection" -->

### Tab "Advanced" - Opzioni Avanzate

Configurazioni per utenti esperti:

| Setting                  | Default | Descrizione                         |
| ------------------------ | ------- | ----------------------------------- |
| **Enable Caching**       | ON      | Cache distanze per speedup          |
| **Verbose Logging**      | OFF     | Log dettagliato in console          |
| **Max Dataset Size**     | 10000   | Limite tuple caricabili             |
| **Timeout Clustering**   | 300s    | Timeout massimo esecuzione          |

<!-- [IMMAGINE]: Advanced settings tab - dimensione: 700x500 -->
<!-- Mostra: checkboxes, spinner numerici per limiti -->

### Persistenza Preferenze

Le impostazioni vengono salvate automaticamente in:
```
~/.qtclustering/preferences.properties
```

Persistono tra sessioni applicazione.

---

## 3.6 Workflow Completo Esempio: Clustering Iris con Convex Hull

Vediamo un esempio pratico completo dall'avvio all'export.

### Scenario

**Obiettivo**: Analizzare dataset Iris con QT, visualizzare cluster con convex hull, esportare grafici HD.

### Step-by-Step

#### Step 1: Avvio Applicazione
```bash
cd MAP/qtGUI/bin
java application.Main
```

**Output**: Finestra GUI si apre, schermata Home visibile.

#### Step 2: Caricamento Dataset Iris

1. **Home screen**: Click sulla ComboBox "Sorgente"
2. **Selezione**: Scegli "Dataset Standard (Iris)"
3. **Preview** (opzionale): Click "Anteprima Dataset"
   - Appare dialog con 150 righe, 5 colonne
   - Verifica dati corretti
4. **Chiudi** preview

#### Step 3: Configurazione Parametri

1. **Radius**: Imposta slider a **0.5**
   - Info box mostra: "~3-4 cluster attesi, tempo stimato: 2s"
2. **Advanced** (opzionale): Verifica cache abilitata

#### Step 4: Avvio Clustering

1. **Click** "Avvia Clustering"
2. **Progress**: Barra animata appare
   - Messaggio: "Caricamento dataset Iris (150 tuple)..."
   - Messaggio: "Clustering in progress..."
3. **Completamento**: Dopo ~2 secondi, navigazione automatica a Results

#### Step 5: Esplorazione Risultati

**TreeView**:
- Espandi Cluster 1 → 50 tuple (setosa)
- Espandi Cluster 2 → 48 tuple (versicolor)
- Espandi Cluster 3 → 52 tuple (virginica)

**Statistiche**:
- Total Clusters: 3
- Avg Cluster Size: 50.0
- Overall Cohesion: 0.893

#### Step 6: Visualizzazione Convex Hull

1. **Click** "Visualize" button
2. **Finestra** Chart Viewer si apre
3. **Configurazione assi**:
   - X: Seleziona "petal_length"
   - Y: Seleziona "petal_width"
   - Click "Aggiorna Grafico"
4. **Visualizzazione**:
   - Hull blu (setosa): piccolo, isolato in basso-sx
   - Hull rosso (versicolor): medio, centrale
   - Hull verde (virginica): grande, alto-dx
   - Centroidi marcati con croce nera

#### Step 7: Confronto Stili

1. **Deseleziona** checkbox "Convex Hull"
2. **Osservazione**: Scatter classico appare
   - Nuvola punti senza delimitazione
   - Meno intuitivo
3. **Riseleziona** checkbox → torna convex hull

#### Step 8: Export Grafico HD

1. **Click** "Esporta PNG HD"
2. **File Chooser**: Scegli percorso `~/Documents/iris_clusters_hd.png`
3. **Conferma**: File salvato (1920x1440 px)

#### Step 9: Export Risultati Completi

1. **Torna** a schermata Results
2. **Click** "Export" button
3. **Dialog** export appare
4. **Selezione**:
   - Format: ZIP (completo)
   - Path: `~/Documents/iris_clustering.zip`
5. **Conferma**: ZIP generato con:
   - clusters.csv
   - report.txt
   - clustering.dmp
   - README.txt

#### Step 10: Salvataggio Clustering

1. **Click** "Save" button
2. **Path**: `~/Documents/iris_result.dmp`
3. **Conferma**: File .dmp salvato (per futuro reload)

### Risultato Finale

**File generati**:
```
~/Documents/
├── iris_clusters_hd.png        # Grafico 1920x1440
├── iris_clustering.zip          # Pacchetto completo
└── iris_result.dmp              # Clustering serializzato
```

**Tempo totale workflow**: ~5 minuti

---

## 3.7 Tips & Best Practices

### Scelta Radius Ottimale

| Dataset Size | Attributi | Radius Consigliato | Cluster Attesi |
| ------------ | --------- | ------------------ | -------------- |
| < 50         | Discreti  | 0.2 - 0.4          | 5-10           |
| 50-200       | Continui  | 0.4 - 0.6          | 3-8            |
| 200-1000     | Misti     | 0.5 - 0.7          | 5-15           |

**Rule of thumb**: Parti da radius 0.5, poi aggiusta basandoti su numero cluster ottenuto.

### Performance

| Dimensione Dataset | Tempo Clustering | RAM Richiesta |
| ------------------ | ---------------- | ------------- |
| < 100 tuple        | < 5s             | 256 MB        |
| 100-500            | 5-30s            | 512 MB        |
| 500-2000           | 30s-5min         | 1 GB          |
| > 2000             | > 5min           | 2+ GB         |

**Nota**: Complessità O(k × n²) rende algoritmo lento per grandi dataset.

### Interpretazione Convex Hull

**Hull compatto** (area piccola):
- Cluster coeso e omogeneo
- Alta qualità, punti simili
- Esempio: Cluster setosa in Iris

**Hull disperso** (area grande):
- Cluster eterogeneo
- Bassa qualità, punti dissimili
- Potrebbe indicare necessità di radius più basso

**Hull sovrapposti**:
- Cluster ambigui, confini sfumati
- Normale per alcune partizioni
- Esempio: Versicolor/Virginica in Iris

### Keyboard Shortcuts Utili

| Shortcut    | Azione                      |
| ----------- | --------------------------- |
| `Ctrl+N`    | Nuova analisi               |
| `Ctrl+O`    | Apri dataset/clustering     |
| `Ctrl+S`    | Salva risultati             |
| `Ctrl+E`    | Export                      |
| `Ctrl+G`    | Visualizza grafico          |
| `Ctrl+I`    | Statistiche                 |
| `Ctrl+T`    | Toggle tema dark/light      |
| `F5`        | Refresh vista corrente      |
| `Esc`       | Chiudi dialog               |

---

[← Capitolo 2: Installazione](02_installazione.md) | [Capitolo 4: TUI →](04_tui.md)
