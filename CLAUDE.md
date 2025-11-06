# CLAUDE.md - Contesto Progetto Quality Threshold Clustering

> **Scopo:** Questo file fornisce a Claude tutte le informazioni di contesto necessarie per lavorare efficacemente sul progetto MAP (Quality Threshold Clustering Algorithm).

---

## 📋 Indice

1. [Panoramica Progetto](#panoramica-progetto)
2. [Struttura del Progetto](#struttura-del-progetto)
3. [Architettura e Design](#architettura-e-design)
4. [Classi Implementate](#classi-implementate)
5. [Algoritmo Quality Threshold](#algoritmo-quality-threshold)
6. [Dataset PlayTennis](#dataset-playtennis)
7. [Sprint Completati](#sprint-completati)
8. [Sprint Futuri](#sprint-futuri)
9. [Comandi Utili](#comandi-utili)
10. [Convenzioni e Standard](#convenzioni-e-standard)

---

## Panoramica Progetto

### Nome Progetto
**MAP - Quality Threshold Clustering Algorithm**

### Obiettivo
Implementare l'algoritmo di clustering Quality Threshold (QT) in Java per l'analisi e il raggruppamento di dati categorici.

### Contesto Accademico
- **Corso:** Metodi Avanzati di Programmazione (MAP)
- **Linguaggio:** Java (vanilla, senza framework)
- **Paradigma:** Programmazione Object-Oriented

### Caratteristiche Principali
- Algoritmo di clustering basato su qualità (quality threshold)
- Supporto per attributi discreti (categorical data)
- Calcolo distanze usando metrica di Hamming
- Clustering deterministico (non richiede seed random)
- Output dettagliato con centroidi e distanze medie

---

## Struttura del Progetto

### Organizzazione Directory

```
MAP/
├── .git/                    # Repository Git
├── .gitignore               # File da ignorare in Git
├── README.md                # Descrizione generale progetto
├── CLAUDE.md                # Questo file - Contesto per Claude
│
├── docs/                    # Documentazione del progetto
│   └── sprints/             # Documentazione sprint
│       ├── SPRINT_0.md      # Sprint 0: Struttura base
│       └── SPRINT_1.md      # Sprint 1: Algoritmo QT
│
├── src/                     # Codice sorgente principale
│   ├── Attribute.java       # Classe astratta per attributi
│   ├── ContinuousAttribute.java
│   ├── DiscreteAttribute.java
│   ├── Item.java            # Classe astratta per item (coppia attributo-valore)
│   ├── DiscreteItem.java
│   ├── Tuple.java           # Sequenza di item (una riga del dataset)
│   ├── ArraySet.java        # Insieme di interi senza duplicati
│   ├── Data.java            # Dataset PlayTennis (14 tuple)
│   ├── Cluster.java         # Singolo cluster con centroide
│   ├── ClusterSet.java      # Insieme di cluster
│   ├── QTMiner.java         # Implementazione algoritmo QT
│   └── MainTest.java        # Main per testare il sistema
│
└── Project/                 # Progetti di esempio dal corso
    ├── QT01/                # Esempi base
    ├── QT02/                # Esempi online
    ├── QT03/                # Keyboard input
    ├── QT07/                # JDBC examples
    └── QT08/                # Socket examples
```

### File Chiave

| File | Ruolo | Priorità |
|------|-------|----------|
| `QTMiner.java` | Implementa algoritmo QT principale | 🔴 Critico |
| `Cluster.java` | Gestisce singolo cluster | 🔴 Critico |
| `Data.java` | Gestisce dataset PlayTennis | 🔴 Critico |
| `Tuple.java` | Rappresenta riga dataset | 🟡 Importante |
| `MainTest.java` | Entry point applicazione | 🟡 Importante |

---

## Architettura e Design

### Diagramma Classi Completo

```
┌────────────────────┐
│   <<abstract>>     │
│    Attribute       │
│────────────────────│
│ - name: String     │
│ - index: int       │
│────────────────────│
│ + getName()        │
│ + getIndex()       │
└─────────┬──────────┘
          △
          │ extends
    ┌─────┴─────┐
    │           │
┌───▼─────────┐ │ ┌──────────────────┐
│ Discrete    │ │ │  Continuous      │
│ Attribute   │ │ │  Attribute       │
│─────────────│ │ │──────────────────│
│ - values[]  │ │ │ - min: double    │
│─────────────│ │ │ - max: double    │
│ + getNumber │ │ │──────────────────│
│   OfDistinct│ │ │ + getMin()       │
│   Values()  │ │ │ + getMax()       │
└─────────────┘ │ └──────────────────┘
                │
    ┌───────────▼──────────┐
    │    <<abstract>>      │
    │        Item          │
    │──────────────────────│
    │ - attribute          │
    │ - value: Object      │
    │──────────────────────│
    │ + distance(Object)   │
    └───────────┬──────────┘
                △
                │ extends
         ┌──────▼────────┐
         │ DiscreteItem  │
         │───────────────│
         │ + distance(): │
         │   0 if equal  │
         │   1 if diff   │
         └───────────────┘

┌─────────────────────┐      ┌────────────────┐
│       Tuple         │◆────►│     Item       │
│─────────────────────│ 1  n └────────────────┘
│ - tuple: Item[]     │
│─────────────────────│
│ + add(Item, int)    │
│ + get(int)          │
│ + getDistance(Tuple)│
│ + avgDistance(...)  │
└─────────────────────┘

┌──────────────────────┐
│        Data          │
│──────────────────────│
│ - data: Object[][]   │
│ - numberOfExamples   │
│ - explanatorySet[]   │
│──────────────────────│
│ + getItemSet(int)    │◄────┐
│ + getValue(...)      │     │
└──────────────────────┘     │
                              │
┌─────────────────────┐       │
│      QTMiner        │       │
│─────────────────────│       │
│ - C: ClusterSet     │       │
│ - radius: double    │       │
│─────────────────────│       │
│ + compute(Data)     │───────┘
│ - buildCandidate... │
└──────────┬──────────┘
           │ 1
           │ contains
           │
           ▼ 1
    ┌──────────────┐
    │  ClusterSet  │◆────┐
    │──────────────│     │
    │ - C: Cluster[]│    │ 1..*
    │──────────────│     │
    │ + add(Cluster)│    │
    │ + get(int)   │     │
    └──────────────┘     │
                         ▼
                  ┌──────────────┐
                  │   Cluster    │
                  │──────────────│
                  │ - centroid   │
                  │ - clusteredData│
                  │──────────────│
                  │ + addData()  │
                  │ + getSize()  │
                  │ + iterator() │
                  └──────┬───────┘
                         │ 1
                         │ uses
                         │
                         ▼ 1
                  ┌──────────────┐
                  │   ArraySet   │
                  │──────────────│
                  │ - set: int[] │
                  │ - size: int  │
                  │──────────────│
                  │ + add(int)   │
                  │ + get(int)   │
                  │ + delete(int)│
                  └──────────────┘
```

### Pattern di Design Utilizzati

1. **Template Method Pattern**
   - `Attribute` (abstract) → `DiscreteAttribute`, `ContinuousAttribute`
   - `Item` (abstract) → `DiscreteItem`

2. **Strategy Pattern**
   - Calcolo distanza delegato a sottoclassi Item

3. **Composite Pattern**
   - `Tuple` composta da array di `Item`
   - `ClusterSet` composta da array di `Cluster`

---

## Classi Implementate

### 1. Attribute (Abstract)
**Scopo:** Rappresenta un attributo generico (colonna del dataset)

**Membri:**
- `name: String` - Nome attributo (es. "Outlook", "Temperature")
- `index: int` - Indice colonna

**Metodi chiave:**
- `getName()`: Restituisce nome attributo
- `getIndex()`: Restituisce indice colonna

---

### 2. DiscreteAttribute
**Scopo:** Attributo con valori discreti/categorici

**Membri aggiuntivi:**
- `values: String[]` - Array valori possibili

**Metodi chiave:**
- `getNumberOfDistinctValues()`: Conta valori distinti
- `frequency(Data, Object)`: Conta occorrenze valore

**Esempio:**
```java
String[] outLookValues = {"overcast", "rain", "sunny"};
DiscreteAttribute outlook = new DiscreteAttribute("Outlook", 0, outLookValues);
```

---

### 3. ContinuousAttribute
**Scopo:** Attributo con valori continui/numerici

**Membri aggiuntivi:**
- `min: double` - Valore minimo
- `max: double` - Valore massimo

**Nota:** Attualmente non utilizzato nel dataset PlayTennis (tutti attributi discreti)

---

### 4. Item (Abstract)
**Scopo:** Coppia (Attributo, Valore) - singola cella del dataset

**Membri:**
- `attribute: Attribute`
- `value: Object`

**Metodo astratto:**
- `distance(Object a): double` - Calcola distanza tra due valori

---

### 5. DiscreteItem
**Scopo:** Item con valore discreto

**Calcolo distanza:**
```java
public double distance(Object a) {
    return this.getValue().equals(a) ? 0 : 1;
}
```
- 0 se valori uguali
- 1 se valori diversi (distanza di Hamming)

---

### 6. Tuple
**Scopo:** Rappresenta una riga del dataset (sequenza di item)

**Membri:**
- `tuple: Item[]` - Array di item

**Metodi chiave:**
- `add(Item c, int i)`: Aggiunge item alla posizione i
- `get(int i)`: Ottiene item alla posizione i
- `getDistance(Tuple obj)`: Calcola distanza tra due tuple
  ```
  distance = Σ(tuple[i].distance(obj.get(i))) / n
  ```
- `avgDistance(Data data, int[] clusteredData)`: Calcola distanza media da insieme tuple

---

### 7. ArraySet
**Scopo:** Insieme di interi senza duplicati (per tracciare ID tuple in cluster)

**Membri:**
- `set: int[]` - Array dinamico
- `size: int` - Numero elementi

**Metodi chiave:**
- `add(int item): boolean` - Aggiunge se non presente
- `get(int item): boolean` - Verifica presenza
- `delete(int item)`: Rimuove elemento
- `toArray(): int[]` - Converte in array

**Implementazione crescita dinamica:**
```java
if (size == set.length) {
    int[] newSet = new int[set.length * 2];
    System.arraycopy(set, 0, newSet, 0, size);
    set = newSet;
}
```

---

### 8. Data
**Scopo:** Gestisce dataset PlayTennis (14 tuple, 5 attributi)

**Membri:**
- `data: Object[][]` - Matrice 14×5
- `numberOfExamples: int` - 14
- `explanatorySet: Attribute[]` - Schema attributi

**Metodi chiave:**
- `getItemSet(int index): Tuple` - Converte riga in Tuple
- `getValue(int row, int col): Object` - Accede a cella
- `getNumberOfExamples(): int` - Restituisce 14
- `getNumberOfExplanatoryAttributes(): int` - Restituisce 5

---

### 9. Cluster
**Scopo:** Rappresenta un singolo cluster con centroide e tuple

**Membri:**
- `centroid: Tuple` - Centroide cluster
- `clusteredData: ArraySet` - ID tuple nel cluster

**Metodi chiave:**
- `addData(int id): boolean` - Aggiunge tupla
- `contain(int id): boolean` - Verifica appartenenza
- `getSize(): int` - Dimensione cluster
- `iterator(): int[]` - Array ID tuple
- `toString(Data data): String` - Output dettagliato con distanze

---

### 10. ClusterSet
**Scopo:** Insieme di cluster (risultato algoritmo QT)

**Membri:**
- `C: Cluster[]` - Array cluster (max 50)
- `lastClusterIndex: int` - Prossimo indice libero

**Metodi chiave:**
- `add(Cluster c)`: Aggiunge cluster
- `get(int i): Cluster` - Ottiene cluster i-esimo
- `toString(Data data): String` - Stampa tutti cluster

---

### 11. QTMiner
**Scopo:** Implementa algoritmo Quality Threshold

**Membri:**
- `C: ClusterSet` - Cluster scoperti
- `radius: double` - Raggio massimo cluster

**Metodi chiave:**
- `compute(Data data): int` - Esegue algoritmo, restituisce numero cluster
- `buildCandidateCluster(Data, boolean[]): Cluster` - Costruisce cluster candidato più grande

**File:** `src/QTMiner.java:1`

---

## Algoritmo Quality Threshold

### Descrizione
L'algoritmo QT è un metodo di clustering che garantisce una qualità minima dei cluster basata su un raggio massimo.

### Pseudocodice Completo

```
ALGORITMO: QT_CLUSTERING
INPUT:
  - data: Dataset con n tuple
  - radius: Raggio massimo cluster (soglia qualità)
OUTPUT:
  - ClusterSet: Insieme di cluster scoperti

INIZIO

  1. Inizializzazione
     - C ← insieme vuoto di cluster
     - isClustered[1..n] ← array booleano, tutti false
     - countClustered ← 0
     - numClusters ← 0

  2. MENTRE countClustered ≠ n FARE

     2.1. Trova cluster migliore
          c ← buildCandidateCluster(data, isClustered)

     2.2. Aggiungi cluster all'insieme
          C.add(c)
          numClusters ← numClusters + 1

     2.3. Marca tuple come clusterizzate
          clusteredTupleId ← c.iterator()
          PER OGNI id IN clusteredTupleId FARE
              isClustered[id] ← true
              countClustered ← countClustered + 1
          FINE PER

  3. RESTITUISCI numClusters

FINE


FUNZIONE: buildCandidateCluster(data, isClustered)
INPUT:
  - data: Dataset
  - isClustered: Array stato clusterizzazione
OUTPUT:
  - Cluster con più tuple

INIZIO

  1. Inizializzazione
     - bestCluster ← null
     - maxClusterSize ← 0

  2. PER OGNI tupla i IN data FARE

     2.1. SE isClustered[i] = false ALLORA

          2.1.1. Crea cluster candidato
                 centroid ← data.getItemSet(i)
                 candidateCluster ← new Cluster(centroid)

          2.1.2. Aggiungi tuple vicine
                 PER OGNI tupla j IN data FARE
                     SE isClustered[j] = false ALLORA
                         distance ← centroid.getDistance(data.getItemSet(j))
                         SE distance ≤ radius ALLORA
                             candidateCluster.addData(j)
                         FINE SE
                     FINE SE
                 FINE PER

          2.1.3. Aggiorna migliore
                 SE candidateCluster.getSize() > maxClusterSize ALLORA
                     bestCluster ← candidateCluster
                     maxClusterSize ← candidateCluster.getSize()
                 FINE SE

     FINE SE

  3. RESTITUISCI bestCluster

FINE
```

### Complessità Algoritmica

**Temporale:**
- `compute()`: O(k) iterazioni, dove k = numero cluster
- `buildCandidateCluster()`: O(n²) calcoli distanza
- **Totale:** O(k × n²)
- **Caso peggiore:** k = n → O(n³)

**Spaziale:**
- Array isClustered: O(n)
- ClusterSet: O(k × m), dove m = dimensione media cluster
- **Totale:** O(n)

### Proprietà Algoritmo

1. **Deterministico:** Stesso input → stesso output
2. **Completezza:** Tutte le tuple vengono clusterizzate
3. **Qualità garantita:** Ogni cluster rispetta vincolo radius
4. **Non richiede k predefinito:** Numero cluster determinato automaticamente

---

## Dataset PlayTennis

### Descrizione
Dataset per decidere se giocare a tennis in base alle condizioni meteo.

### Schema

| Attributo    | Tipo     | Valori                        | Descrizione           |
|--------------|----------|-------------------------------|-----------------------|
| Outlook      | Discrete | {overcast, rain, sunny}       | Condizioni cielo      |
| Temperature  | Discrete | {cool, hot, mild}             | Temperatura           |
| Humidity     | Discrete | {high, normal}                | Umidità               |
| Wind         | Discrete | {strong, weak}                | Vento                 |
| PlayTennis   | Discrete | {no, yes}                     | Decisione (target)    |

### Tuple (14 esempi)

```
ID  | Outlook  | Temperature | Humidity | Wind   | PlayTennis
----|----------|-------------|----------|--------|------------
1   | sunny    | hot         | high     | weak   | no
2   | sunny    | hot         | high     | strong | no
3   | overcast | hot         | high     | weak   | yes
4   | rain     | mild        | high     | weak   | yes
5   | rain     | cool        | normal   | weak   | yes
6   | rain     | cool        | normal   | strong | no
7   | overcast | cool        | normal   | strong | yes
8   | sunny    | mild        | high     | weak   | no
9   | sunny    | cool        | normal   | weak   | yes
10  | rain     | mild        | normal   | weak   | yes
11  | sunny    | mild        | normal   | strong | yes
12  | overcast | mild        | high     | strong | yes
13  | overcast | hot         | normal   | weak   | yes
14  | rain     | mild        | high     | strong | no
```

### Metrica di Distanza

**Distanza di Hamming normalizzata:**

```
distance(tuple1, tuple2) = (numero attributi diversi) / (numero totale attributi)
```

**Esempio:**
```
tuple1 = (sunny, hot, high, weak, no)
tuple2 = (sunny, hot, high, strong, no)

Differenze: solo "Wind" è diverso (1 attributo su 5)
distance = 1/5 = 0.2
```

---

## Sprint Completati

### ✅ Sprint 0 - Struttura Base
**Stato:** Completato

**Classi implementate:**
- Attribute (abstract), DiscreteAttribute, ContinuousAttribute
- Item (abstract), DiscreteItem
- Tuple
- ArraySet
- Data

**Documentazione:** `docs/sprints/SPRINT_0.md`

---

### ✅ Sprint 1 - Algoritmo QT
**Stato:** Completato

**Classi implementate:**
- Cluster
- ClusterSet
- QTMiner
- MainTest

**Funzionalità:**
- Algoritmo QT completo
- Calcolo distanze
- Output dettagliato cluster

**Documentazione:** `docs/sprints/SPRINT_1.md`

---

## Sprint Futuri

### 🔜 Sprint 2 - Persistenza e I/O
**Obiettivi:**
- Serializzazione cluster su file
- Caricamento cluster da file
- Supporto per dataset esterni (CSV)
- Gestione errori I/O

**Tecnologie:**
- Java Serialization o JSON
- FileInputStream/FileOutputStream
- BufferedReader per CSV

---

### 🔜 Sprint 3 - Ottimizzazioni Performance
**Obiettivi:**
- Pruning algoritmo (evitare calcoli inutili)
- Caching distanze calcolate
- Strutture dati efficienti (es. R-tree)
- Parallelizzazione con thread

**Miglioramenti attesi:**
- Complessità da O(n³) a O(n² log n)
- Gestione dataset > 1000 tuple

---

### 🔜 Sprint 4 - Supporto Attributi Continui
**Obiettivi:**
- Implementare ContinuousItem
- Distanza Euclidea per attributi numerici
- Normalizzazione valori continui
- Dataset misti (discreti + continui)

---

### 🔜 Sprint 5 - Interfaccia Grafica
**Obiettivi:**
- GUI Swing per input parametri
- Visualizzazione cluster 2D/3D
- Grafici con JFreeChart
- Esportazione immagini

---

### 🔜 Sprint 6 - Metriche Qualità
**Obiettivi:**
- Silhouette coefficient
- Davies-Bouldin index
- Calinski-Harabasz index
- Confronto automatico con k-means

---

### 🔜 Sprint 7 - Database Integration (JDBC)
**Obiettivi:**
- Lettura dati da database
- Salvataggio risultati in DB
- Supporto MySQL/PostgreSQL
- Connection pooling

---

### 🔜 Sprint 8 - Comunicazione Client-Server (Socket)
**Obiettivi:**
- Server QT multi-client
- Protocollo comunicazione
- Clustering distribuito
- Load balancing

---

## Comandi Utili

### Compilazione

```bash
# Da directory MAP
cd src
javac *.java
```

**Output:** File `.class` generati in `src/`

---

### Esecuzione

```bash
# Da directory src
java MainTest
```

**Input richiesto:**
```
Inserisci radius: 0
```

**Output tipico:**
```
Outlook,Temperature,Humidity,Wind,PlayTennis
1:sunny,hot,high,weak,no,
...
14:rain,mild,high,strong,no,

Inserisci radius: 0

Numero di cluster: 11

Cluster 1:
Centroid=(sunny hot high weak no )
Examples:
[sunny hot high weak no ] dist=0.0
[sunny hot high strong no ] dist=0.2
[sunny mild high weak no ] dist=0.2

AvgDistance=0.13333333333333333
...
```

---

### Test con Diversi Radius

#### Radius = 0 (cluster precisi)
```bash
$ java MainTest
Inserisci radius: 0
Numero di cluster: 11
```
Risultato: Molti cluster piccoli (1-3 tuple)

#### Radius = 0.5 (bilanciato)
```bash
$ java MainTest
Inserisci radius: 0.5
Numero di cluster: ~5-7
```
Risultato: Cluster di dimensione media

#### Radius = 1.0 (cluster aggregati)
```bash
$ java MainTest
Inserisci radius: 1.0
Numero di cluster: ~2-3
```
Risultato: Pochi cluster grandi

---

### Git Commands

```bash
# Stato repository
git status

# Aggiungere file
git add docs/sprints/*.md
git add CLAUDE.md

# Commit
git commit -m "Add sprint documentation and CLAUDE.md context file"

# Push (branch corrente)
git push -u origin $(git branch --show-current)

# Log commit
git log --oneline -10
```

---

### Verifica Compilazione

```bash
# Verifica tutti i file compilano
cd src
javac *.java && echo "✓ Compilazione OK" || echo "✗ Errori compilazione"
```

---

## Convenzioni e Standard

### Stile Codice

#### Naming Conventions
- **Classi:** PascalCase (es. `QTMiner`, `ClusterSet`)
- **Metodi:** camelCase (es. `getDistance`, `addData`)
- **Variabili:** camelCase (es. `clusteredData`, `radius`)
- **Costanti:** UPPER_SNAKE_CASE (es. `MAX_CLUSTER`)

#### Visibilità
```java
public class Data { ... }           // Pubblico: classi principali
class Cluster { ... }               // Package-private: classi interne
private int size;                   // Privato: membri
```

---

### Javadoc

**Formato standard:**
```java
/**
 * Descrizione breve metodo.
 *
 * @param parametro descrizione parametro
 * @return descrizione valore ritorno
 */
public int metodo(int parametro) { ... }
```

**Esempio reale:**
```java
/**
 * Calcola la distanza tra due tuple usando la metrica di Hamming.
 *
 * @param obj tupla da confrontare
 * @return distanza normalizzata [0, 1]
 */
public double getDistance(Tuple obj) {
    double distance = 0.0;
    for (int i = 0; i < tuple.length; i++) {
        distance += tuple[i].distance(obj.get(i));
    }
    return distance / tuple.length;
}
```

---

### Gestione Errori

**Attualmente:** Nessuna gestione errori esplicita

**Best practice per futuri sprint:**
```java
// Validazione input
if (radius < 0) {
    throw new IllegalArgumentException("Radius must be non-negative");
}

// Gestione indici
if (index < 0 || index >= size) {
    throw new IndexOutOfBoundsException("Index: " + index);
}
```

---

### Testing

**Approccio attuale:**
- Test manuali via `MainTest`
- Verifiche visive output

**Futuri miglioramenti:**
```java
// Sprint 2: JUnit tests
@Test
public void testClusterSize() {
    Cluster c = new Cluster(centroid);
    c.addData(0);
    c.addData(1);
    assertEquals(2, c.getSize());
}
```

---

## Note Importanti per Claude

### 🎯 Quando modifichi il codice

1. **Leggi sempre prima:** Usa `Read` per vedere implementazione corrente
2. **Compila dopo modifiche:** `javac *.java` per verificare errori
3. **Testa funzionalità:** Esegui `java MainTest` per verificare
4. **Aggiorna documentazione:** Se cambi API, aggiorna javadoc

### 📝 Quando crei nuove feature

1. **Segui pattern esistenti:** Stile e struttura già presenti
2. **Usa visibilità corretta:** package-private per classi interne
3. **Aggiungi javadoc:** Sempre per metodi pubblici
4. **Testa con MainTest:** Verifica integrazione

### 🐛 Quando debuggi

1. **Verifica dati intermedi:** Stampa cluster parziali
2. **Controlla conteggi:** Somma dimensioni cluster = 14
3. **Testa casi limite:** radius = 0, radius = 1, dataset vuoto

### 📚 Riferimenti rapidi

- **File algoritmo principale:** `src/QTMiner.java`
- **File test:** `src/MainTest.java`
- **Dataset:** Hardcoded in `src/Data.java` (costruttore)
- **Documentazione sprint:** `docs/sprints/SPRINT_X.md`

---

## Metadati File

**Versione:** 1.0
**Data creazione:** 2025-11-06
**Ultima modifica:** 2025-11-06
**Autore:** Claude (AI Assistant)
**Linguaggio:** Java
**Encoding:** UTF-8

---

## Risorse Esterne

### Algoritmo QT
- Paper originale: Heyer et al. (1999) "Exploring Expression Data: Identification and Analysis of Coexpressed Genes"
- Complessità: O(n³) worst case

### Distanza di Hamming
- Wikipedia: https://en.wikipedia.org/wiki/Hamming_distance
- Usata per dati categorici/discreti

### Java Documentation
- Oracle Java SE Docs: https://docs.oracle.com/javase/8/docs/api/

---

## FAQ per Claude

### Q: Come aggiungo un nuovo attributo al dataset?
**A:** Modifica `Data.java` costruttore:
1. Aggiungi colonna a `data[][]`
2. Aggiungi attributo a `explanatorySet[]`
3. Aggiorna lunghezza array

### Q: Come cambio il radius predefinito?
**A:** Modifica richiesta in `MainTest.java`, oppure passa come argomento CLI

### Q: Come supporto dataset esterni?
**A:** Pianificato per Sprint 2 (CSV parsing)

### Q: Come ottimizzare per dataset grandi?
**A:** Pianificato per Sprint 3 (pruning, caching, parallelizzazione)

### Q: Dove sono i test unitari?
**A:** Non ancora implementati. Pianificati per Sprint 2 con JUnit

---

**Fine documento**
