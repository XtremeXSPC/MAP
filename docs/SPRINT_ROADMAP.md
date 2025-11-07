# Sprint Roadmap - Quality Threshold Clustering Project

> **Documento di pianificazione:** Roadmap completa degli Sprint di sviluppo per il progetto MAP (Quality Threshold Clustering Algorithm)

---

## 📊 Panoramica Generale

### Stato del Progetto

| Sprint | Nome | QT Module | Stato | Durata | Completamento |
|--------|------|-----------|-------|--------|---------------|
| Sprint 0 | Struttura Base | QT01 | ✅ Completato | 1 settimana | 100% |
| Sprint 1 | Algoritmo QT | QT01/QT02 | ✅ Completato | 1 settimana | 100% |
| Sprint 2 | Persistenza e I/O | QT04 | ✅ Completato | 1 settimana | 100% |
| Sprint 3 | Supporto Attributi Continui | - | ✅ Completato | 1 settimana | 100% |
| Sprint 4 | Keyboard Input | QT03 | ✅ Completato | 1 settimana | 100% |
| Sprint 5 | Contenitori e Iteratori | QT05 | 🔜 Pianificato | 1 settimana | 0% |
| Sprint 6 | Generics e RTTI | QT06 | 🔜 Pianificato | 1 settimana | 0% |
| Sprint 7 | Database Integration (JDBC) | QT07 | 🔜 Pianificato | 2 settimane | 0% |
| Sprint 8 | Comunicazione Client-Server | QT08 | 🔜 Pianificato | 2 settimane | 0% |
| Estensione | Interfaccia Grafica (GUI) | - | 🔜 Opzionale | 2 settimane | 0% |
| Estensione | Ottimizzazioni Performance | - | 🔜 Opzionale | 2 settimane | 0% |
| Estensione | Metriche Qualità | - | 🔜 Opzionale | 1 settimana | 0% |

### Timeline Stimata
- **Fase 1 (Core - Obbligatorio):** Sprint 0-3 (✅ Completata - QT01, QT02, QT04)
- **Fase 2 (Requisiti Corso - Priorità Alta):** Sprint 4-6 (🔴 In Corso - Sprint 4 completato, Sprint 5-6 pianificati)
- **Fase 3 (Integrazione - Requisiti Corso):** Sprint 7-8 (🔜 4 settimane - QT07, QT08)
- **Fase 4 (Estensioni - Opzionale):** GUI, Ottimizzazioni, Metriche (🔜 5 settimane)

**Durata Totale Stimata (Core + Corso):** 10-12 settimane
**Con Estensioni:** 15-17 settimane

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

### ✅ Sprint 2 - Persistenza e I/O

**Durata:** 1 settimana
**Stato:** Completato
**Documentazione:** [`docs/sprints/SPRINT_2.md`](sprints/SPRINT_2.md)
**Prerequisiti:** Sprint 1

#### Obiettivi
Implementare funzionalità di persistenza per salvare e caricare risultati del clustering, e supportare dataset esterni in formato CSV.

#### Funzionalità Implementate

##### 1. Eccezioni Custom
- ✅ `InvalidFileFormatException` - File .dmp malformati
- ✅ `InvalidDataFormatException` - CSV con errori di formato
- ✅ `IncompatibleClusterException` - Cluster incompatibili con dataset

##### 2. Serializzazione Cluster
- ✅ `ClusterSet.save(filename, radius)` - Salva cluster in formato .dmp testuale
- ✅ Formato human-readable con metadata (radius, timestamp, numero cluster)
- ✅ Ogni cluster include centroide e ID tuple

##### 3. Caricamento Cluster
- ✅ `ClusterSet(filename, data)` - Ricostruisce cluster da file .dmp
- ✅ Validazioni robuste (file esistente, formato valido, compatibilità)
- ✅ Messaggi di errore chiari e informativi

##### 4. Supporto Dataset CSV
- ✅ `Data(csvFilename)` - Parsing automatico di file CSV
- ✅ Inferenza automatica tipi attributi (discrete vs continuous)
- ✅ Gestione valori mancanti (`?`, `NA`, celle vuote)
- ✅ Validazione formato (numero colonne consistente)

##### 5. Menu Interattivo
- ✅ Tre modalità operative: dataset hardcoded, CSV loading, cluster loading
- ✅ Input validato con gestione errori
- ✅ Opzione salvataggio risultati dopo clustering
- ✅ Loop continuo per operazioni multiple

##### 6. Dataset di Esempio
- ✅ `data/playtennis.csv` - Dataset originale (14 tuple)
- ✅ `data/weather.csv` - Dataset esteso (50 tuple)
- ✅ `data/test_small.csv` - Mini dataset per test (3 tuple)

#### Classi Modificate/Create
- ✅ `src/exceptions/` - Package con 3 eccezioni custom
- ✅ `ClusterSet` - Metodi save() e costruttore da file
- ✅ `Data` - Costruttore CSV con parsing e inferenza tipi
- ✅ `MainTest` - Menu interattivo completo

#### Tecnologie Utilizzate
- Java I/O (BufferedReader/BufferedWriter)
- File format custom testuale (.dmp)
- CSV parsing con split e validazioni
- Try-with-resources per gestione stream

#### Risultati Chiave
- ✅ Cluster salvati e ricaricati correttamente
- ✅ Dataset CSV caricati con successo
- ✅ Gestione errori robusta (nessun crash)
- ✅ Test funzionali tutti passati
- ✅ Documentazione completa formato file

#### Metriche
- **Story Points:** 13 (come stimato)
- **Classi:** 3 nuove + 3 modificate
- **Linee di codice:** ~1,350 LOC
- **Test:** 4 scenari funzionali verificati

#### Esempio File .dmp
```
---
METADATA
radius=0.3
numClusters=14
timestamp=2025-11-07T09:02:21
---
CLUSTER 0
centroid=sunny,hot,high,weak,no
tupleIDs=0
---
...
```

---

## 🎯 Sprint Completati

### ✅ Sprint 4 - Keyboard Input (QT03)

**Durata:** 1 settimana
**Stato:** ✅ Completato
**QT Module:** QT03
**Data Completamento:** 2025-11-07
**Prerequisiti:** Sprint 1, 2
**Documentazione:** [`docs/sprints/SPRINT_4.md`](sprints/SPRINT_4.md)

#### Obiettivi
Integrare la classe `Keyboard.java` per gestione robusta dell'input utente da tastiera, sostituendo gli input Scanner rudimentali con una soluzione enterprise-grade conforme alle specifiche del corso.

#### Funzionalità da Implementare

##### 1. Integrazione Classe Keyboard
- ✅ Classe `Keyboard.java` già disponibile in `Project/QT03/keyboardinput/`
- Copiare o importare package `keyboardinput` nel progetto src
- Metodi disponibili:
  - `readInt()`: Lettura interi con validazione
  - `readDouble()`: Lettura double con validazione
  - `readString()`: Lettura stringhe complete
  - `readWord()`: Lettura parola singola
  - `readChar()`: Lettura carattere
  - `readBoolean()`: Lettura booleani

##### 2. Refactoring MainTest
- Sostituire tutti i `Scanner` con `Keyboard`
- Aggiungere validazione input radius (> 0)
- Aggiungere retry automatico per input invalidi
- Migliorare messaggi di errore per l'utente

##### 3. Menu Interattivo Avanzato
- Loop menu principale con gestione errori
- Validazione scelte utente
- Gestione EOF (Ctrl+D) e errori input
- Contatore errori con `Keyboard.getErrorCount()`

##### 4. Gestione Errori Input
- Try-catch per input malformati
- Messaggi utente chiari e informativi
- Retry automatico senza crash
- Log errori per debugging

#### Esempio Refactoring

**Prima (Scanner):**
```java
Scanner scanner = new Scanner(System.in);
System.out.print("Inserisci radius: ");
double radius = scanner.nextDouble(); // Può crashare!
```

**Dopo (Keyboard):**
```java
import keyboardinput.Keyboard;

double radius;
do {
    System.out.print("Inserisci radius (> 0): ");
    radius = Keyboard.readDouble();
    if (radius <= 0) {
        System.out.println("Errore: radius deve essere > 0");
    }
} while (radius <= 0 || Double.isNaN(radius));
```

#### Classi da Modificare
- `MainTest.java` - Refactoring completo input/output
- Aggiungere package `keyboardinput/` al progetto

#### Tecnologie
- Java I/O (BufferedReader, StringTokenizer)
- Exception handling robusto
- Package import e gestione

#### Risultati Attesi
- ✅ Nessun crash per input malformati
- ✅ Validazione automatica input
- ✅ Messaggi errore user-friendly
- ✅ Codice conforme a specifiche QT03

#### Risultati Ottenuti
- ✅ Package keyboardinput integrato in `src/keyboardinput/`
- ✅ MainTest completamente refactorato con Keyboard
- ✅ Validazione robusta input radius (> 0) con retry automatico
- ✅ Test completi con input invalidi (testo, negativi, zero)
- ✅ Menu interattivo robusto con validazione range
- ✅ Zero crash durante test stress con input casuali
- ✅ Messaggi di errore user-friendly con icone (✗ ✓)
- ✅ Documentazione completa Sprint 4

#### Story Points: 8/8 (100%)

---

## 🔜 Sprint Pianificati

### Sprint 5 - Contenitori, Iteratori, Comparatori (QT05)

**Durata:** 1 settimana
**Stato:** Pianificato
**QT Module:** QT05
**Prerequisiti:** Sprint 4
**Documentazione:** `Project/QT05/Specifica_QT05_Contenitori-Iteratori-Comparatori.pdf`

#### Obiettivi
Implementare pattern Iterator per attraversamento cluster e Comparator per ordinamento cluster secondo diversi criteri (dimensione, distanza media, etc.).

#### Funzionalità Pianificate

##### 1. Pattern Iterator per Cluster
- Implementare `Iterator<Integer>` in `Cluster`
- Metodo `iterator()` restituisce iterator su tuple IDs
- Supporto `hasNext()`, `next()`, `remove()` (optional)
- Foreach loop su cluster: `for (int tupleId : cluster)`

##### 2. Pattern Iterator per ClusterSet
- Implementare `Iterable<Cluster>` in `ClusterSet`
- Attraversamento cluster: `for (Cluster c : clusterSet)`
- Supporto enhanced for-loop

##### 3. Comparator per Cluster
- `ClusterSizeComparator` - Ordina per dimensione
- `ClusterDistanceComparator` - Ordina per distanza media
- `ClusterCentroidComparator` - Ordina per centroide (lessicografico)

##### 4. Sorting e Filtering
- Metodo `ClusterSet.sort(Comparator<Cluster>)`
- Metodo `ClusterSet.filter(Predicate<Cluster>)`
- Esempio: Mostra solo cluster con size > 3

#### Classi da Modificare/Creare
- `Cluster` - Implementa `Iterable<Integer>`
- `ClusterSet` - Implementa `Iterable<Cluster>`
- `ClusterSizeComparator` (nuova)
- `ClusterDistanceComparator` (nuova)
- `MainTest` - Dimostra sorting e iterazione

#### Esempio Utilizzo
```java
// Iterazione cluster
for (Cluster cluster : clusterSet) {
    System.out.println("Cluster size: " + cluster.getSize());
    for (int tupleId : cluster) {
        System.out.println("  Tuple " + tupleId);
    }
}

// Sorting
clusterSet.sort(new ClusterSizeComparator());
System.out.println("Cluster ordinati per dimensione");
```

#### Criteri di Successo
- [ ] Pattern Iterator implementato
- [ ] Enhanced for-loop funzionante
- [ ] Almeno 2 Comparator implementati
- [ ] Sorting cluster dimostrato
- [ ] Test con dataset multipli

#### Story Points Stimati: 13

---

### Sprint 6 - Generics e RTTI (QT06)

**Durata:** 1 settimana
**Stato:** Pianificato
**QT Module:** QT06
**Prerequisiti:** Sprint 5
**Documentazione:** `Project/QT06/Specifica_QT06_Generics-RTTI.pdf`

#### Obiettivi
Refactoring classi esistenti usando Java Generics per type safety e implementare Run-Time Type Information (RTTI) per gestione dinamica tipi attributi.

#### Funzionalità Pianificate

##### 1. Refactoring con Generics
- `ArraySet<T>` invece di `ArraySet` (attualmente solo int)
- `Cluster<T>` parametrizzato sul tipo tuple
- `ClusterSet<T>` generico
- Eliminare cast espliciti

##### 2. RTTI per Attributi
- Uso di `instanceof` per determinare tipo attributo runtime
- `Class<?>` per reflection su Item types
- Metodo `Attribute.getType(): Class<?>`
- Dynamic dispatch basato su tipo

##### 3. Type-Safe Collections
- Sostituire array nativi con `ArrayList<T>`
- Uso di `List<Tuple>`, `Set<Integer>`, `Map<String, Cluster>`
- Type safety garantita dal compilatore

##### 4. Wildcard e Bounds
- Metodi con bounded wildcards: `<T extends Comparable<T>>`
- Upper/lower bounds per flessibilità
- Esempio: `public <T extends Number> void process(List<T> items)`

#### Classi da Refactorare
- `ArraySet` → `ArraySet<T>`
- `Cluster` → `Cluster<T extends Tuple>`
- `ClusterSet` → `ClusterSet<T>`
- `Data` - Uso Generics per explanatorySet

#### Vantaggi Attesi
- Type safety a compile-time
- Eliminazione cast espliciti
- Codice più manutenibile e leggibile
- Migliore integrazione con Collections Framework

#### Criteri di Successo
- [ ] Almeno 3 classi refactorate con Generics
- [ ] Zero warning "unchecked" dal compilatore
- [ ] RTTI dimostrato con instanceof
- [ ] Test regressione passati
- [ ] Documentazione Generics aggiornata

#### Story Points Stimati: 13

---

### Sprint 3 (Opzionale) - Ottimizzazioni Performance

**Durata:** 2 settimane
**Stato:** Pianificato (Estensione)
**Priorità:** 🟢 Bassa (Dopo Sprint 8)
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

### ✅ Sprint 3 - Supporto Attributi Continui

**Durata:** 1 settimana
**Stato:** ✅ Completato
**Priorità:** 🟡 Media
**Prerequisiti:** Sprint 0, 1
**Documentazione:** [`docs/sprints/SPRINT_3.md`](sprints/SPRINT_3.md) (ex SPRINT_4.md)

#### Obiettivi
Estendere il sistema per supportare attributi numerici/continui usando distanza Euclidea.

#### Funzionalità Implementate

##### 1. ✅ ContinuousItem
- ✅ Implementata sottoclasse di Item per valori continui
- ✅ Distanza Euclidea normalizzata: |scaledValue1 - scaledValue2|
- ✅ Normalizzazione nel range [0, 1]
- ✅ Javadoc completo con formula ed esempi

##### 2. ✅ Normalizzazione Valori
- ✅ Min-max normalization: (x - min) / (max - min)
- ✅ Gestione automatica range attributi
- ✅ Supporto in ContinuousAttribute.getScaledValue()

##### 3. ✅ Dataset Misti
- ✅ Gestione dataset con attributi discreti + continui
- ✅ Distanza combinata tramite media aritmetica
- ✅ Fix critico in Data.getItemSet() per type detection

##### 4. ✅ Dataset di Test
- ✅ Iris dataset (150 tuple, 4 attributi continui + 1 discreto)
- ✅ Weather Mixed dataset (30 tuple, 2 continui + 3 discreti)
- ✅ Test di validazione completi per entrambi

#### Classi Implementate/Modificate
- ✅ `ContinuousItem` (nuova) - Distanza Euclidea normalizzata
- ✅ `Data.getItemSet()` - Fix bug per supporto attributi continui
- ✅ `TestIris.java` - Validazione caricamento Iris
- ✅ `TestWeatherMixed.java` - Validazione caricamento Mixed
- ✅ `TestIrisClustering.java` - Clustering con radius multipli
- ✅ `TestWeatherMixedClustering.java` - Clustering dataset misto

#### Formula Distanza Mista Implementata
```
distance = Σ(item[i].distance(...)) / n
dove:
  - DiscreteItem.distance() → 0 o 1 (Hamming)
  - ContinuousItem.distance() → [0, 1] (Euclidea normalizzata)
  - Media aritmetica compatibile perché entrambe in [0, 1]
```

#### Risultati Ottenuti

**Test Iris (150 tuple, 4 continui):**
- ✅ Radius 0.5: 11 cluster, **100% purezza**
- ✅ Separazione perfetta delle 3 specie:
  - Cluster 1: 48 setosa (100% puro)
  - Cluster 2: 41 versicolor (100% puro)
  - Cluster 3: 38 virginica (100% puro)
- ✅ Distanze intra-specie < inter-specie verificato

**Test Weather Mixed (30 tuple, 2 continui + 3 discreti):**
- ✅ Radius 0.5: 8 cluster sensati
- ✅ Cluster raggruppano condizioni meteo simili
- ✅ Attributi continui (temp, humidity) combinati con discreti
- ✅ Pattern meteo identificati correttamente

#### Criteri di Successo
- [x] ContinuousItem implementato e testato ✅
- [x] Normalizzazione funzionante ✅
- [x] Test con Iris dataset ✅
- [x] Clustering su dati continui eccellente ✅
- [x] Documentazione formule distanza ✅

#### Story Points Completati: 13/13

#### File Deliverables
- `src/ContinuousItem.java` (+102 LOC)
- `src/Data.java` (modificato, +30 LOC)
- `data/iris.csv` (150 tuple)
- `data/weather_mixed.csv` (30 tuple)
- `src/TestIris.java` (+95 LOC)
- `src/TestWeatherMixed.java` (+108 LOC)
- `src/TestIrisClustering.java` (+125 LOC)
- `src/TestWeatherMixedClustering.java` (+137 LOC)
- `docs/sprints/SPRINT_4.md` (+672 LOC)

---

### Estensione - Interfaccia Grafica (GUI)

**Durata:** 2 settimane
**Stato:** Pianificato (Estensione Opzionale)
**Priorità:** 🟢 Bassa (Dopo Sprint 8)
**Prerequisiti:** Sprint 1, 2, 4

#### Obiettivi
Creare un'interfaccia grafica Swing per input parametri e visualizzazione cluster come estensione del progetto.

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

### Estensione - Metriche Qualità Clustering

**Durata:** 1 settimana
**Stato:** Pianificato (Estensione Opzionale)
**Priorità:** 🟢 Bassa (Dopo Sprint 8)
**Prerequisiti:** Sprint 1, 3

#### Obiettivi
Implementare metriche standard per valutare la qualità del clustering e confrontare con altri algoritmi come estensione del progetto.

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

### Sprint 7 - Database Integration (JDBC) - QT07

**Durata:** 2 settimane
**Stato:** Pianificato
**QT Module:** QT07
**Priorità:** 🟡 Alta (Requisito Corso)
**Prerequisiti:** Sprint 2, 4
**Riferimento:** `Project/QT07/JDBC/`

#### Obiettivi
Integrare supporto per database relazionali usando JDBC per lettura dati e salvataggio risultati, seguendo le specifiche del modulo QT07 del corso.

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

### Sprint 8 - Comunicazione Client-Server (Socket) - QT08

**Durata:** 2 settimane
**Stato:** Pianificato
**QT Module:** QT08
**Priorità:** 🟡 Alta (Requisito Corso)
**Prerequisiti:** Sprint 2, 4, 7
**Riferimento:** `Project/QT08/Socket/MainTest.java`

#### Obiettivi
Implementare architettura client-server per clustering distribuito usando Socket Java, seguendo le specifiche del modulo QT08 del corso. Il client deve usare la classe Keyboard per input robusto.

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

### Percorso Obbligatorio (Requisiti Corso)
```
Sprint 0 (Base - QT01) ✅
   │
   ├──► Sprint 1 (Algoritmo QT - QT01/QT02) ✅
   │       │
   │       ├──► Sprint 2 (Persistenza I/O - QT04) ✅
   │       │       │
   │       │       ├──► Sprint 3 (Attributi Continui) ✅
   │       │       │
   │       │       └──► Sprint 4 (Keyboard Input - QT03) ✅
   │       │               │
   │       │               ├──► Sprint 5 (Iteratori - QT05) ← 🔴 PROSSIMO
   │       │               │       │
   │       │               │       └──► Sprint 6 (Generics - QT06)
   │       │               │
   │       │               └──► Sprint 7 (JDBC - QT07)
   │       │                       │
   │       │                       └──► Sprint 8 (Socket - QT08)
```

### Percorso Estensioni (Opzionali)
```
Sprint 8 (completato)
   │
   ├──► Estensione: GUI
   │
   ├──► Estensione: Ottimizzazioni Performance
   │
   └──► Estensione: Metriche Qualità
```

### Legenda:
- **🔴 Sprint 5:** Priorità massima attuale (QT05 - Iteratori)
- **🟡 Sprint 6-8:** Requisiti corso (QT06, QT07, QT08)
- **🟢 Estensioni:** Opzionali dopo completamento corso
- **✅ Sprint 0-4:** Completati (52% del corso)

---

## 🎯 Priorità e Raccomandazioni

### 🎓 Percorso Consigliato per Corso MAP

#### Fase 1: Core Obbligatorio ✅ COMPLETATA
1. ✅ Sprint 0 - Struttura Base (QT01)
2. ✅ Sprint 1 - Algoritmo QT (QT01/QT02)
3. ✅ Sprint 2 - Persistenza I/O (QT04 Eccezioni)
4. ✅ Sprint 3 - Attributi Continui (Estensione funzionale)

**Completamento:** 100% (4/4 sprint)

#### Fase 2: Requisiti Corso Intermedi 🔴 IN CORSO
5. ✅ Sprint 4 - Keyboard Input (QT03) - COMPLETATO
6. 🔴 **Sprint 5 - Iteratori e Comparatori (QT05)** ← PROSSIMO
7. 🟡 Sprint 6 - Generics e RTTI (QT06)

**Completamento:** 33% (1/3 sprint)
**Durata stimata:** 2 settimane

#### Fase 3: Requisiti Corso Avanzati 🔜 PIANIFICATO
8. 🟡 Sprint 7 - JDBC (QT07)
9. 🟡 Sprint 8 - Socket Client-Server (QT08)

**Completamento:** 0% (0/2 sprint)
**Durata stimata:** 4 settimane

#### Fase 4: Estensioni Opzionali 🟢 OPZIONALE
- Interfaccia Grafica (GUI)
- Ottimizzazioni Performance
- Metriche Qualità Clustering

**Durata stimata:** 5 settimane (se tutte implementate)

---

### 📋 Priorità per Ambito Accademico

#### 🔴 Obbligatori per Corso (Priorità Massima)
- ✅ Sprint 0-4: Base, Algoritmo, Persistenza, Attributi, Keyboard
- 🔴 **Sprint 5: Iteratori/Comparatori (QT05)** ← PROSSIMO
- 🟡 Sprint 6: Generics/RTTI (QT06)
- 🟡 Sprint 7: JDBC (QT07)
- 🟡 Sprint 8: Socket (QT08)

**Rationale:** Questi sprint coprono tutti i moduli QT del corso (QT01-QT08)

#### 🟢 Opzionali/Bonus (Dopo completamento corso)
- GUI (interfaccia grafica)
- Ottimizzazioni Performance (dataset grandi)
- Metriche Qualità (comparazione algoritmi)

**Rationale:** Estensioni utili ma non richieste dal corso

---

### 🚀 Ordine di Implementazione Raccomandato

```
ADESSO:
  └─ Sprint 5 (QT05 - Iteratori) ← 🔴 INIZIA QUI

POI:
  └─ Sprint 6 (QT06 - Generics)

INFINE:
  ├─ Sprint 7 (QT07 - JDBC)
  └─ Sprint 8 (QT08 - Socket)

OPZIONALE:
  ├─ GUI
  ├─ Ottimizzazioni
  └─ Metriche
```

**Nota:** Sprint 7 e 8 possono essere implementati in parallelo da team diversi se necessario.

---

## 📊 Metriche Complessive Progetto

### Story Points Totali

#### Sprint Obbligatori (Corso)
| Sprint | Nome | QT Module | Story Points | Stato |
|--------|------|-----------|--------------|-------|
| 0 | Struttura Base | QT01 | 22 | ✅ Completato |
| 1 | Algoritmo QT | QT01/02 | 18 | ✅ Completato |
| 2 | Persistenza I/O | QT04 | 13 | ✅ Completato |
| 3 | Attributi Continui | - | 13 | ✅ Completato |
| 4 | Keyboard Input | QT03 | 8 | ✅ Completato |
| 5 | Iteratori/Comparatori | QT05 | 13 | 🔜 Pianificato |
| 6 | Generics/RTTI | QT06 | 13 | 🔜 Pianificato |
| 7 | JDBC | QT07 | 21 | 🔜 Pianificato |
| 8 | Socket | QT08 | 21 | 🔜 Pianificato |
| **Subtotale Corso** | | | **142** | **52% completato** |

#### Sprint Opzionali (Estensioni)
| Estensione | Story Points | Priorità |
|------------|--------------|----------|
| GUI | 21 | 🟢 Bassa |
| Ottimizzazioni | 21 | 🟢 Bassa |
| Metriche Qualità | 13 | 🟢 Bassa |
| **Subtotale Estensioni** | **55** | |

**TOTALE PROGETTO:** 197 Story Points

### Classi Implementate/Pianificate
- **✅ Completate (Sprint 0-4):** 15 classi principali + 3 eccezioni + 6 test + 1 package (Keyboard) = 25 file
- **🔜 Sprint 5-6:** +5 classi (Iterators, Comparators, Generics refactoring)
- **🔜 Sprint 7-8:** +10 classi (JDBC, Socket, Server/Client)
- **🟢 Estensioni:** +15 classi (GUI, metriche, ottimizzazioni)
- **Totale previsto:** ~55 classi/file

### Linee di Codice (LOC)
- **✅ Attualmente (Sprint 0-4):** ~3,240 LOC
  - Sprint 0-3: ~2,850 LOC
  - Sprint 4: +390 LOC (Keyboard: 267, MainTest: +35, Test: 50, Docs: 635)
- **🔜 Fine Sprint 5-6:** ~3,800 LOC
- **🔜 Fine Sprint 7-8:** ~5,300 LOC
- **🟢 Con Estensioni:** ~7,800 LOC

### Completamento Progetto
```
Corso (Obbligatorio):    ████████░░░░░░░░ 52% (5/9 sprint)
Estensioni (Opzionale):  ░░░░░░░░░░░░░░░░  0% (0/3 sprint)
```

**Tempo rimanente stimato:**
- Completamento corso: 6 settimane (4 sprint)
- Con estensioni: 11 settimane totali

---

## 🛠️ Strumenti e Tecnologie per Sprint

| Sprint | Nome | QT Module | Tecnologie Principali | Librerie |
|--------|------|-----------|----------------------|----------|
| 0-1 | Base + Algoritmo | QT01/02 | Java vanilla, OOP | Nessuna |
| 2 | Persistenza I/O | QT04 | Java I/O, Exceptions | Nessuna |
| 3 | Attributi Continui | - | Math, Statistics | Nessuna |
| 4 | Keyboard Input | QT03 | BufferedReader, StringTokenizer | keyboardinput package |
| 5 | Iteratori/Comparatori | QT05 | Iterator, Iterable, Comparator | Java Collections |
| 6 | Generics/RTTI | QT06 | Generics, Reflection, instanceof | Nessuna |
| 7 | JDBC | QT07 | JDBC, SQL, Connection Pooling | MySQL/PostgreSQL Connector |
| 8 | Socket | QT08 | Socket, ServerSocket, ObjectStreams | Nessuna |
| GUI | Estensione | - | Swing, JavaFX (opt) | JFreeChart |
| Ottimizzazioni | Estensione | - | Multithreading, Collections | ExecutorService |
| Metriche | Estensione | - | Statistics | Apache Commons Math (opt) |

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
| 2025-11-07 | 1.1 | Sprint 2 completato, Sprint 3 in corso |
| 2025-11-07 | 1.2 | Sprint 3 e Sprint 4 (Attributi Continui) completati |
| 2025-11-07 | 2.0 | **MAJOR UPDATE**: Allineamento completo alle specifiche del corso<br>- Riorganizzati sprint per riflettere moduli QT01-QT08<br>- Sprint 4 rinominato a Sprint 3 (Attributi Continui)<br>- Nuovo Sprint 4: Keyboard Input (QT03) - PRIORITÀ MASSIMA<br>- Sprint 5: Contenitori/Iteratori (QT05)<br>- Sprint 6: Generics/RTTI (QT06)<br>- Sprint 7-8: JDBC e Socket (QT07-QT08)<br>- GUI, Ottimizzazioni, Metriche → Estensioni opzionali<br>- Aggiornate dipendenze e priorità<br>- Focus su requisiti corso invece di estensioni |
| 2025-11-07 | 2.1 | **Sprint 4 Completato**: Keyboard Input (QT03)<br>- Package keyboardinput integrato (267 LOC)<br>- MainTest refactorato con Keyboard<br>- Validazione robusta radius > 0<br>- Zero crash con input invalidi<br>- Test completi con input stress<br>- Documentazione SPRINT_4.md completa<br>- Aggiornate metriche: 52% completamento corso<br>- Story points: 74/142 (52%)<br>- Prossimo: Sprint 5 - Iteratori (QT05) |

---

**Fine Roadmap**

Per dettagli implementativi di ogni sprint, consultare:
- [`docs/sprints/SPRINT_0.md`](sprints/SPRINT_0.md) - Struttura Base (QT01)
- [`docs/sprints/SPRINT_1.md`](sprints/SPRINT_1.md) - Algoritmo QT (QT01/QT02)
- [`docs/sprints/SPRINT_2.md`](sprints/SPRINT_2.md) - Persistenza e I/O (QT04 Eccezioni)
- [`docs/sprints/SPRINT_3.md`](sprints/SPRINT_3.md) - Supporto Attributi Continui (ex SPRINT_4.md)
- Sprint 4-8: Da creare al momento dell'implementazione
  - Sprint 4: Keyboard Input (QT03)
  - Sprint 5: Contenitori/Iteratori (QT05)
  - Sprint 6: Generics/RTTI (QT06)
  - Sprint 7: JDBC (QT07)
  - Sprint 8: Socket (QT08)

Per riferimenti alle specifiche del corso:
- `Project/QT03/Specifica_QT03_Package.pdf`
- `Project/QT04/Specifica_QT04_Eccezioni.pdf`
- `Project/QT05/Specifica_QT05_Contenitori-Iteratori-Comparatori.pdf`
- `Project/QT06/Specifica_QT06_Generics-RTTI.pdf`
- `Project/QT07/JDBC/` - Esempi codice JDBC
- `Project/QT08/Socket/` - Esempi codice Socket
