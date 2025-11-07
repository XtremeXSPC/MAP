# Sprint 1 - Algoritmo Quality Threshold

## Obiettivo

Implementare l'algoritmo di clustering Quality Threshold (QT) completo, incluse le classi per gestire cluster individuali e insiemi di cluster.

## Durata

1 settimana

## Prerequisiti

- Sprint 0 completato (classi base: Data, Tuple, ArraySet, Attribute, Item)

---

## Backlog dello Sprint

### 1. Classe `Cluster`

**Priorità:** Alta
**Story Points:** 5

#### Descrizione

Modella un singolo cluster con un centroide e un insieme di tuple clusterizzate.

#### Criteri di Accettazione

- [ ] Implementare costruttore che accetta una tupla come centroide
- [ ] Gestire insieme di tuple clusterizzate tramite ArraySet
- [ ] Implementare metodo `addData()` per aggiungere tuple al cluster
- [ ] Implementare metodo `contain()` per verificare appartenenza
- [ ] Implementare metodo `removeTuple()` per rimuovere tuple
- [ ] Implementare metodo `getSize()` per ottenere dimensione cluster
- [ ] Implementare metodo `iterator()` per ottenere array di ID
- [ ] Implementare metodo `toString()` base con centroide
- [ ] Implementare metodo `toString(Data)` dettagliato con tuple e distanze

#### Dettagli Implementativi

```java
class Cluster {
    private Tuple centroid;
    private ArraySet clusteredData;

    Cluster(Tuple centroid);
    Tuple getCentroid();
    boolean addData(int id);
    boolean contain(int id);
    void removeTuple(int id);
    int getSize();
    int[] iterator();
    String toString();
    String toString(Data data);
}
```

#### Algoritmo toString(Data)

```
Per ogni tupla nel cluster:
    1. Stampare valori attributi
    2. Calcolare e stampare distanza dal centroide
    3. Calcolare distanza media di tutte le tuple dal centroide
```

**File:** `src/Cluster.java`

---

### 2. Classe `ClusterSet`

**Priorità:** Alta
**Story Points:** 3

#### Descrizione

Modella un insieme di cluster (risultato dell'algoritmo QT).

#### Criteri di Accettazione

- [ ] Implementare array dinamico di cluster
- [ ] Implementare metodo `add()` per aggiungere cluster
- [ ] Implementare metodo `get()` per ottenere cluster per indice
- [ ] Implementare metodo `toString()` per visualizzare tutti i cluster

#### Dettagli Implementativi

```java
class ClusterSet {
    private Cluster C[];
    private int lastClusterIndex = 0;
    private final int MAX_CLUSTER = 50;

    ClusterSet();
    void add(Cluster c);
    Cluster get(int i);
    String toString();
    String toString(Data data);
}
```

**File:** `src/ClusterSet.java`

---

### 3. Classe `QTMiner`

**Priorità:** Alta
**Story Points:** 8

#### Descrizione

Implementa l'algoritmo Quality Threshold per il clustering. Questa è la classe principale che coordina l'intero processo di clustering.

#### Criteri di Accettazione

- [ ] Implementare costruttore che accetta il raggio (radius)
- [ ] Implementare metodo `compute()` che esegue l'algoritmo QT
- [ ] Implementare metodo `buildCandidateCluster()` per costruire cluster candidati
- [ ] Gestire array booleano per tracciare tuple già clusterizzate
- [ ] Iterare fino a quando tutte le tuple sono clusterizzate
- [ ] Restituire numero di cluster scoperti

#### Dettagli Implementativi

```java
public class QTMiner {
    private ClusterSet C;
    private double radius;

    public QTMiner(double radius);
    public ClusterSet getC();
    public int compute(Data data);
    private Cluster buildCandidateCluster(Data data, boolean isClustered[]);
}
```

#### Algoritmo `compute(Data data)`

```
Input: Dataset con n tuple, raggio radius
Output: ClusterSet con cluster scoperti

1. Inizializzare array isClustered[n] a false
2. Inizializzare contatore countClustered = 0

3. MENTRE countClustered ≠ n:
   a. c = buildCandidateCluster(data, isClustered)
   b. Aggiungere c a ClusterSet
   c. Per ogni tupla in c:
      - Marcare tupla come clusterizzata (isClustered[id] = true)
   d. countClustered += dimensione di c

4. Restituire numero di cluster
```

#### Algoritmo `buildCandidateCluster(Data data, boolean[] isClustered)`

```
Input: Dataset, stato clusterizzazione
Output: Cluster candidato con più tuple

1. Inizializzare bestCluster = null, maxSize = 0

2. PER OGNI tupla i non ancora clusterizzata:
   a. Creare cluster candidato con centroide = tupla i
   b. PER OGNI tupla j non ancora clusterizzata:
      - Calcolare distanza tra tupla j e centroide
      - SE distanza ≤ radius:
          * Aggiungere j al cluster candidato
   c. SE dimensione cluster candidato > maxSize:
      - bestCluster = cluster candidato
      - maxSize = dimensione cluster candidato

3. Restituire bestCluster
```

**File:** `src/QTMiner.java`

---

### 4. Classe `MainTest`

**Priorità:** Alta
**Story Points:** 2

#### Descrizione

Classe main per testare l'intero sistema di clustering con input interattivo.

#### Criteri di Accettazione

- [ ] Caricare dataset tramite classe Data
- [ ] Richiedere raggio da input utente (Keyboard)
- [ ] Eseguire algoritmo QT
- [ ] Stampare numero di cluster scoperti
- [ ] Stampare dettagli di ogni cluster (centroide, tuple, distanze)

#### Dettagli Implementativi

```java
public class MainTest {
    public static void main(String[] args) {
        Data data = new Data();
        System.out.println(data);

        double radius = leggiRaggio();

        QTMiner qt = new QTMiner(radius);
        int numCluster = qt.compute(data);

        System.out.println("Numero di cluster: " + numCluster);
        System.out.println(qt.getC().toString(data));
    }
}
```

**File:** `src/MainTest.java`

---

## Diagramma delle Classi Complete

```
┌────────────────────┐
│       Data         │
│────────────────────│
│ - data[][]         │
│ - numberOfExamples │
│ - explanatorySet[] │
└─────────┬──────────┘
          │ usa
          │
          ▼
    ┌─────────────┐
    │    Tuple    │
    │─────────────│
    │ - tuple[]   │
    └──────┬──────┘
           │
           │ composto da
           ▼
      ┌────────┐
      │  Item  │
      └────────┘

┌─────────────────┐
│    QTMiner      │
│─────────────────│
│ - C: ClusterSet │
│ - radius: double│
│─────────────────│
│ + compute(Data) │
│ - buildCandidate│
└────────┬────────┘
         │ contiene
         ▼
    ┌────────────────┐
    │  ClusterSet    │◆────┐
    │────────────────│     │
    │ - C: Cluster[] │     │ 1..*
    └────────────────┘     │
                           ▼
                    ┌──────────────┐
                    │   Cluster    │
                    │──────────────│
                    │ - centroid   │
                    │ - clusteredData│
                    └──────────────┘
                           │ usa
                           ▼
                    ┌──────────────┐
                    │   ArraySet   │
                    └──────────────┘
```

---

## Flusso di Esecuzione

### Diagramma di Sequenza - Algoritmo QT

```
MainTest          QTMiner        Data         Cluster      ArraySet
   |                |              |              |            |
   |--new QTMiner-->|              |              |            |
   |                |              |              |            |
   |--compute(data)-|              |              |            |
   |                |              |              |            |
   |                |--getNumberOfExamples()----->|            |
   |                |<----------14----------------|            |
   |                |              |              |            |
   |   LOOP [tutte le tuple non clusterizzate]    |            |
   |                |              |              |            |
   |                |-buildCandidateCluster(...)  |            |
   |                |  |           |              |            |
   |                |  |--getItemSet(i)---------->|            |
   |                |  |<-------Tuple-------------|            |
   |                |  |           |              |            |
   |                |  |--new Cluster(centroid)---|-->|        |
   |                |  |           |              |<--|        |
   |                |  |           |              |            |
   |                |  | LOOP [calcola distanze]  |            |
   |                |  |           |              |            |
   |                |  |--getDistance(tuple)----->|            |
   |                |  |<-----distance------------|            |
   |                |  |           |              |            |
   |                |  |--addData(j)------------->|            |
   |                |  |           |              |--add(j)--->|
   |                |  |           |              |<--true-----|
   |                |  |           |              |            |
   |                |<-bestCluster----------------|            |
   |                |              |              |            |
   |                |-C.add(cluster)              |            |
   |                |              |              |            |
   |<--numClusters--|              |              |            |
   |                |              |              |            |
```

---

## Esempio di Esecuzione

### Input

```
Dataset: PlayTennis (14 tuple, 5 attributi)
Radius: 0
```

### Output Atteso

```
Numero di cluster: 11

Cluster 1:
Centroid=(sunny hot high weak no )
Examples:
[sunny hot high weak no ] dist=0.0
[sunny hot high strong no ] dist=0.2
[sunny mild high weak no ] dist=0.2
AvgDistance=0.133

Cluster 2:
Centroid=(overcast hot high weak yes )
Examples:
[overcast hot high weak yes ] dist=0.0
[overcast cool normal strong yes ] dist=0.8
[overcast mild high strong yes ] dist=0.4
[overcast hot normal weak yes ] dist=0.4
AvgDistance=0.4

...
```

---

## Test e Validazione

### Test Funzionali

#### Test 1: Cluster con Radius = 0

**Input:** `radius = 0`
**Atteso:** Ogni tupla forma un cluster separato (14 cluster totali)

#### Test 2: Cluster con Radius = 1

**Input:** `radius = 1`
**Atteso:** Cluster più grandi, numero cluster < 14

#### Test 3: Verifica Centroidi

**Test:** Verificare che ogni cluster abbia un centroide valido
**Atteso:** Centroide è una tupla del dataset

#### Test 4: Verifica Completezza

**Test:** Sommare le dimensioni di tutti i cluster
**Atteso:** Somma = 14 (numero totale tuple)

### Test di Unità

```java
// Test ArraySet
ArraySet set = new ArraySet();
assert set.add(1) == true;
assert set.add(1) == false;  // duplicato
assert set.get(1) == true;
assert set.size() == 1;

// Test Cluster
Data data = new Data();
Tuple centroid = data.getItemSet(0);
Cluster c = new Cluster(centroid);
c.addData(0);
c.addData(1);
assert c.getSize() == 2;

// Test ClusterSet
ClusterSet cs = new ClusterSet();
cs.add(c);
assert cs.get(0).getSize() == 2;
```

---

## Definizione di "Done"

- [ ] Tutte le classi compilano senza errori
- [ ] L'algoritmo QT produce cluster corretti
- [ ] MainTest esegue con successo con radius = 0
- [ ] MainTest esegue con successo con radius = 1
- [ ] Tutte le tuple sono clusterizzate (nessuna rimane esclusa)
- [ ] La somma delle dimensioni dei cluster = numero tuple totali
- [ ] Javadoc completo per tutte le classi
- [ ] Codice committato nel repository

---

## Complessità Algoritmica

### Analisi Temporale

**compute(Data data):**

- Ciclo esterno: O(k) iterazioni, dove k = numero cluster
- Chiamata a buildCandidateCluster() per iterazione

**buildCandidateCluster(Data data, boolean[] isClustered):**

- Ciclo esterno: O(n) tuple non clusterizzate
- Ciclo interno: O(n) calcoli di distanza
- Complessità: O(n²)

**Complessità Totale:** O(k × n²)

- Nel caso peggiore k = n (ogni tupla è un cluster): O(n³)
- Nel caso medio k << n: O(n²)

### Analisi Spaziale

- Array isClustered: O(n)
- ClusterSet: O(k × m), dove m = dimensione media cluster
- Spazio totale: O(n)

---

## Note Tecniche

### Scelta del Radius

- **Radius piccolo (0-0.3):** Molti cluster piccoli, alta precisione
- **Radius medio (0.4-0.7):** Bilanciamento tra precisione e aggregazione
- **Radius grande (0.8-1.0):** Pochi cluster grandi, bassa precisione

### Ottimizzazioni Future

1. **Pruning:** Evitare di considerare tuple troppo distanti
2. **Caching:** Memorizzare distanze già calcolate
3. **Parallelizzazione:** Costruire cluster candidati in parallelo
4. **Strutture Dati:** Usare R-tree per ricerca spaziale efficiente

---

## Struttura File Completa

```
MAP/
├── src/
│   ├── Attribute.java
│   ├── ContinuousAttribute.java
│   ├── DiscreteAttribute.java
│   ├── Item.java
│   ├── DiscreteItem.java
│   ├── Tuple.java
│   ├── ArraySet.java
│   ├── Data.java
│   ├── Cluster.java          ← NUOVO
│   ├── ClusterSet.java       ← NUOVO
│   ├── QTMiner.java          ← NUOVO
│   └── MainTest.java         ← NUOVO
└── docs/
    └── sprints/
        ├── SPRINT_0.md
        └── SPRINT_1.md
```

---

## Rischi e Mitigazioni

| Rischio                                  | Probabilità | Impatto | Mitigazione                                                            |
| ---------------------------------------- | ----------- | ------- | ---------------------------------------------------------------------- |
| Complessità O(n³) per dataset grandi     | Alta        | Alto    | Documentare limitazioni, pianificare ottimizzazioni in Sprint 3        |
| Bug nel tracciamento tuple clusterizzate | Media       | Alto    | Test approfonditi con conteggi e verifiche                             |
| Overflow in ArraySet                     | Bassa       | Medio   | Testare con radius variabile                                           |
| Cluster vuoti                            | Bassa       | Alto    | Verificare che buildCandidateCluster restituisca sempre cluster valido |

---

## Retrospettiva

### Cosa è andato bene

- Algoritmo QT implementato correttamente
- Separazione chiara tra Cluster e ClusterSet
- Riuso efficace delle classi dello Sprint 0
- Output dettagliato con distanze medie

### Cosa migliorare

- Complessità algoritmica elevata per dataset grandi
- Nessuna validazione input radius
- ArraySet potrebbe usare ArrayList
- Manca gestione errori (es. dataset vuoto)

### Action Items per Sprint Futuri

- [ ] **Sprint 2:** Aggiungere persistenza cluster su file
- [ ] **Sprint 3:** Ottimizzare algoritmo con pruning e caching
- [ ] **Sprint 4:** Aggiungere supporto per attributi continui
- [ ] **Sprint 5:** Implementare interfaccia grafica per visualizzazione cluster
- [ ] **Sprint 6:** Aggiungere metriche di qualità clustering (silhouette, Davies-Bouldin)

---

## Comandi di Compilazione ed Esecuzione

### Compilazione

```bash
cd /home/user/MAP/src
javac *.java
```

### Esecuzione

```bash
java MainTest
```

### Esempio di Sessione

```
$ java MainTest
Outlook,Temperature,Humidity,Wind,PlayTennis
1:sunny,hot,high,weak,no,
2:sunny,hot,high,strong,no,
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

## Riferimenti

### Algoritmo Quality Threshold

- Paper originale: Heyer et al., "Exploring Expression Data: Identification and Analysis of Coexpressed Genes" (1999)
- Complessità: O(n³) nel caso peggiore
- Vantaggi: Non richiede numero cluster predefinito, garantisce qualità minima cluster

### Metriche di Distanza

- **Distanza Hamming:** Usata per attributi discreti
- Formula: d(x,y) = Σ(x[i] ≠ y[i]) / n
- Valori: [0, 1], dove 0 = identici, 1 = completamente diversi
