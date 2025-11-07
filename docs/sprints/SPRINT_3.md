# Sprint 3 - Ottimizzazioni Performance

## Obiettivo

Ottimizzare l'algoritmo Quality Threshold per gestire dataset più grandi (> 1000 tuple) migliorando complessità computazionale e prestazioni generali.

## Durata

2 settimane

## Prerequisiti

- Sprint 0 completato (classi base)
- Sprint 1 completato (algoritmo QT)
- Sprint 2 completato (persistenza e I/O)

---

## Backlog dello Sprint

### 1. Caching Distanze

**Priorità:** Alta
**Story Points:** 8

#### Descrizione

Implementare un sistema di caching per memorizzare le distanze già calcolate tra tuple, evitando ricalcoli ripetuti.

#### Analisi Problema

Nel codice attuale, `getDistance(Tuple obj)` viene chiamata ripetutamente per le stesse coppie di tuple:
- In `buildCandidateCluster()`: per ogni tupla centroide, calcoliamo distanze da tutte le altre
- Se algoritmo fa k iterazioni e ci sono n tuple, calcoliamo ~O(k × n²) distanze
- Molte distanze vengono ricalcolate identicamente

#### Soluzione

Creare classe `DistanceCache` che:
- Memorizza distanze in matrice simmetrica (solo metà superiore)
- Usa coordinate (i,j) con i < j per evitare duplicati
- Invalidazione automatica se dataset cambia

#### Criteri di Accettazione

- [ ] Classe `DistanceCache` implementata
- [ ] Integrazione con `QTMiner`
- [ ] Riduzione tempo esecuzione >30% per n > 50
- [ ] Trade-off memoria accettabile (< 100MB per 1000 tuple)
- [ ] Test con vari dataset size

#### Dettagli Implementativi

```java
class DistanceCache {
    private Double[][] cache;
    private Data data;
    private boolean enabled;

    // Costruttore
    DistanceCache(Data data, boolean enabled);

    // Get con lazy computation
    double getDistance(int tupleId1, int tupleId2);

    // Clear cache
    void clear();

    // Statistics
    int getHitCount();
    int getMissCount();
    double getHitRate();
}
```

**Memoria richiesta:**
- n tuple → n(n-1)/2 distanze
- 1000 tuple → ~500,000 double = 4MB
- 5000 tuple → ~12.5M double = 100MB

**Ottimizzazione:**
- Cache solo distanze < radius + margine (es. 2×radius)
- Usa HashMap<Long, Double> invece di array 2D per sparsità

---

### 2. Strutture Dati Efficienti

**Priorità:** Alta
**Story Points:** 5

#### Descrizione

Sostituire strutture dati inefficienti con implementazioni più performanti dalla Java Collections Framework.

#### Modifiche Pianificate

##### 2.1 ArraySet → HashSet

**Problema attuale:**
- `ArraySet` usa array nativo con crescita dinamica
- `get(int item)`: O(n) - scansione lineare
- `add(int item)`: O(n) - verifica duplicati
- `delete(int item)`: O(n) - ricerca + shift

**Soluzione:**
```java
// Prima (ArraySet)
boolean get(int item) {
    for (int i = 0; i < size; i++) {
        if (set[i] == item) return true;
    }
    return false;
}  // O(n)

// Dopo (HashSet)
HashSet<Integer> set = new HashSet<>();
boolean contains(int item) {
    return set.contains(item);
}  // O(1) average
```

##### 2.2 Array Dinamici → ArrayList

**Modificare:**
- `ClusterSet.C[]` → `ArrayList<Cluster>`
- `Data.data[][]` → Mantiene array (accesso indicizzato necessario)

#### Criteri di Accettazione

- [ ] `ArraySet` sostituita con `HashSet<Integer>` in `Cluster`
- [ ] `ClusterSet.C[]` sostituita con `ArrayList<Cluster>`
- [ ] Test regressione: stessi risultati clustering
- [ ] Miglioramento performance operazioni O(n) → O(1)

#### Impatto Stimato

| Operazione | Prima | Dopo | Miglioramento |
|------------|-------|------|---------------|
| Cluster.contain() | O(n) | O(1) | ~100x per n=100 |
| ClusterSet.add() | O(n) copy | O(1) amortized | ~10x |
| ArraySet.get() | O(n) | O(1) | ~100x |

---

### 3. Pruning Algoritmo

**Priorità:** Media
**Story Points:** 8

#### Descrizione

Evitare calcoli di distanza inutili utilizzando bounds e criteri di early stopping.

#### Ottimizzazioni Pianificate

##### 3.1 Radius-Based Pruning

In `buildCandidateCluster()`:
- Se cluster corrente ha già n elementi
- E trovato cluster migliore con m > n elementi
- Possiamo smettere di considerare questo centroide

```java
// Pseudocodice
if (candidateCluster.getSize() < bestCluster.getSize() - remainingTuples) {
    break;  // Impossibile battere bestCluster
}
```

##### 3.2 Triangle Inequality Pruning

Se distanza tra centroidi troppo grande, skip completamente:
- `d(A, B) ≥ 2×radius` → B sicuramente non in cluster di A

```java
// Prima di calcolare distanza punto-centroide
double centroidDistance = cache.getDistance(centroidId1, centroidId2);
if (centroidDistance >= 2 * radius) {
    continue;  // Skip questo centroide
}
```

##### 3.3 Early Stopping nel Loop

Se cluster candidato è molto grande, probabile sia l'ottimo:
- Se `size > 0.8 × n` → probabilmente miglior cluster possibile

#### Criteri di Accettazione

- [ ] Radius-based pruning implementato
- [ ] Triangle inequality applicato dove possibile
- [ ] Early stopping con threshold configurabile
- [ ] Metriche: numero calcoli distanza evitati
- [ ] Riduzione tempo >20% su dataset grandi

---

### 4. Benchmarking e Profiling

**Priorità:** Alta
**Story Points:** 5

#### Descrizione

Implementare sistema di benchmarking per misurare performance e identificare bottleneck.

#### Funzionalità

##### 4.1 Classe Benchmark

```java
class QTBenchmark {
    // Misura tempo esecuzione
    BenchmarkResult runBenchmark(Data data, double radius);

    // Genera report comparativo
    String compareResults(List<BenchmarkResult> results);

    // Salva risultati su file
    void saveResults(String filename);
}

class BenchmarkResult {
    int numTuples;
    int numAttributes;
    double radius;
    long executionTimeMs;
    int numClusters;
    int distanceCalculations;
    int cacheHits;
    long memoryUsedMB;
}
```

##### 4.2 Dataset di Test

Creare dataset sintetici di varie dimensioni:
- Small: 50 tuple, 5 attributi
- Medium: 200 tuple, 10 attributi
- Large: 1000 tuple, 15 attributi
- XLarge: 5000 tuple, 20 attributi

##### 4.3 Report Automatico

```
Performance Benchmark Report
===========================

Dataset: weather_large.csv (1000 tuples, 10 attributes)
Radius: 0.5

Before Optimizations:
- Execution Time: 8,520 ms
- Distance Calculations: 1,000,000
- Memory Used: 45 MB

After Optimizations:
- Execution Time: 3,210 ms (62% faster ✓)
- Distance Calculations: 520,000 (48% reduction)
- Cache Hits: 480,000 (92% hit rate)
- Memory Used: 68 MB

Breakdown:
- Caching: 35% time saved
- Data Structures: 15% time saved
- Pruning: 12% time saved
```

#### Criteri di Accettazione

- [ ] Classe `QTBenchmark` implementata
- [ ] 4 dataset di test creati
- [ ] Report comparativo generato
- [ ] Grafici performance (opzionale)
- [ ] Documentazione risultati in SPRINT_3.md

---

## Architettura Ottimizzata

### Nuove Classi

```
src/
├── optimization/
│   ├── DistanceCache.java
│   ├── QTBenchmark.java
│   └── BenchmarkResult.java
│
└── (classi esistenti modificate)
```

### Modifiche Classi Esistenti

#### QTMiner.java

```java
public class QTMiner {
    private ClusterSet C;
    private double radius;
    private DistanceCache distanceCache;  // Nuovo
    private boolean enableOptimizations;   // Nuovo

    // Nuovo costruttore
    public QTMiner(double radius, boolean enableOptimizations);

    // Metodo compute ottimizzato
    public int compute(Data data);

    // Statistiche performance
    public PerformanceStats getStats();
}
```

#### Cluster.java

```java
class Cluster {
    private Tuple centroid;
    private HashSet<Integer> clusteredData;  // Modificato da ArraySet

    // Metodi aggiornati per HashSet
    boolean addData(int id);
    boolean contain(int id);
    void removeTuple(int id);
    int[] iterator();  // Converte Set → array
}
```

#### ClusterSet.java

```java
public class ClusterSet {
    private ArrayList<Cluster> C;  // Modificato da Cluster[]

    // Metodi aggiornati
    public void add(Cluster c);
    public Cluster get(int i);
    public int getNumClusters();
}
```

---

## Testing

### Test Performance

#### Test 1: Miglioramento Tempo Esecuzione

**Setup:**
- Dataset: 500 tuple, 10 attributi
- Radius: 0.5
- Misura: tempo esecuzione 10 run, media

**Criterio successo:** Tempo ridotto >40%

#### Test 2: Scalabilità

**Setup:**
- Dataset crescenti: 100, 500, 1000, 5000 tuple
- Radius: 0.5
- Misura: tempo vs. numero tuple

**Criterio successo:** Crescita sub-quadratica

#### Test 3: Caching Effectiveness

**Setup:**
- Dataset: 1000 tuple
- Con/senza cache
- Misura: hit rate, tempo risparmiato

**Criterio successo:** Hit rate >80%, tempo -30%

#### Test 4: Memoria

**Setup:**
- Dataset: 5000 tuple
- Misura: memoria heap usata

**Criterio successo:** < 200MB per 5000 tuple

---

### Test Regressione

Verificare che ottimizzazioni non cambiano risultati:

```java
@Test
public void testResultsUnchanged() {
    Data data = new Data("data/playtennis.csv");

    // Algoritmo originale
    QTMiner original = new QTMiner(0.5, false);
    int numClusters1 = original.compute(data);
    ClusterSet clusters1 = original.getC();

    // Algoritmo ottimizzato
    QTMiner optimized = new QTMiner(0.5, true);
    int numClusters2 = optimized.compute(data);
    ClusterSet clusters2 = optimized.getC();

    // Assert stessi risultati
    assertEquals(numClusters1, numClusters2);
    assertClustersEqual(clusters1, clusters2);
}
```

---

## Deliverables

### Codice

- [ ] `src/optimization/DistanceCache.java` - Caching distanze
- [ ] `src/optimization/QTBenchmark.java` - Benchmarking
- [ ] `src/optimization/BenchmarkResult.java` - Risultati benchmark
- [ ] `QTMiner.java` - Algoritmo ottimizzato
- [ ] `Cluster.java` - HashSet invece di ArraySet
- [ ] `ClusterSet.java` - ArrayList invece di array

### Dataset

- [ ] `data/synthetic_small.csv` (50 tuple)
- [ ] `data/synthetic_medium.csv` (200 tuple)
- [ ] `data/synthetic_large.csv` (1000 tuple)
- [ ] `data/synthetic_xlarge.csv` (5000 tuple)

### Documentazione

- [ ] `SPRINT_3.md` - Questo documento
- [ ] Report benchmarking in `docs/BENCHMARK_RESULTS.md`
- [ ] Javadoc per nuove classi
- [ ] README aggiornato con istruzioni performance

### Test

- [ ] Test regressione (risultati invariati)
- [ ] Test performance (4 scenari)
- [ ] Test caching (hit rate, correttezza)
- [ ] Test memoria (leak check)

---

## Metriche di Successo

### Obiettivi Performance

| Metrica | Baseline | Obiettivo | Stretch Goal |
|---------|----------|-----------|--------------|
| Tempo (n=1000) | ~8s | <3.5s (56%) | <2s (75%) |
| Memoria (n=1000) | ~45MB | <80MB | <60MB |
| Scalabilità | O(n³) | O(n² log n) | O(n²) |
| Cache hit rate | N/A | >80% | >90% |

### Calcoli Evitati

**Baseline (no optimizations):**
- 1000 tuple, 10 cluster → ~10M calcoli distanza

**Con ottimizzazioni:**
- Caching: -50% calcoli ripetuti
- Pruning: -20% calcoli evitabili
- **Totale:** -70% calcoli → ~3M invece di 10M

---

## Analisi Complessità

### Baseline (Sprint 1)

```
compute():
  for each iteration (k volte):              O(k)
    buildCandidateCluster():
      for each tuple i:                      O(n)
        if not clustered:
          for each tuple j:                  O(n)
            calculate distance                O(m)  # m = num attributi

Totale: O(k × n² × m)
Caso peggiore (k=n): O(n³ × m)
```

### Ottimizzato (Sprint 3)

```
compute():
  initialize cache                            O(1)

  for each iteration (k volte):               O(k)
    buildCandidateCluster():
      for each tuple i:                       O(n)
        if not clustered:
          check pruning conditions             O(1)
          if not pruned:
            for each tuple j:                  O(n')  # n' << n
              get distance from cache          O(1)

Totale: O(k × n × n')
Con pruning efficace: O(k × n × log n)
```

**Miglioramento teorico:** Da O(n³) a O(n² log n)

---

## Rischi e Mitigazioni

### Rischio 1: Cache Troppo Grande

**Probabilità:** Media
**Impatto:** Alto (OutOfMemoryError)

**Mitigazione:**
- Limite dimensione cache configurabile
- Eviction policy LRU se supera limite
- HashMap sparsa invece di matrice densa
- Calcolare solo distanze < 2×radius

---

### Rischio 2: Pruning Eccessivo

**Probabilità:** Bassa
**Impatto:** Alto (risultati incorretti)

**Mitigazione:**
- Test regressione esaustivi
- Verificare matematicamente correttezza bounds
- Flag per disabilitare pruning e confrontare risultati
- Logging dettagliato decisioni pruning

---

### Rischio 3: Overhead Strutture Dati

**Probabilità:** Bassa
**Impatto:** Medio (performance peggiore)

**Mitigazione:**
- Benchmarking prima/dopo ogni modifica
- Profiling con VisualVM o JProfiler
- Rollback se performance peggiora
- Opzione per disabilitare ottimizzazioni

---

## Note Implementative

### Backwards Compatibility

Mantenere costruttore originale per compatibilità:

```java
// Vecchio costruttore (compatibilità)
public QTMiner(double radius) {
    this(radius, true);  // Ottimizzazioni abilitate di default
}

// Nuovo costruttore
public QTMiner(double radius, boolean enableOptimizations) {
    this.radius = radius;
    this.enableOptimizations = enableOptimizations;
    this.C = new ClusterSet();
}
```

### Configurazione Performance

File `qt.properties`:
```properties
# Ottimizzazioni
optimizations.enabled=true
optimizations.cache.enabled=true
optimizations.pruning.enabled=true

# Limiti
cache.max.size.mb=100
pruning.early.stop.threshold=0.8

# Benchmarking
benchmark.enabled=false
benchmark.output.file=benchmark_results.txt
```

---

## Prossimi Passi (Sprint 4+)

Dopo Sprint 3, possibili ulteriori ottimizzazioni:

### Sprint 4 Enhancements

- **Parallelizzazione:** Thread pool per costruzione cluster parallela
- **GPU Acceleration:** Calcolo distanze su GPU (CUDA/OpenCL)
- **Approximate Algorithms:** Trade-off qualità/velocità

### Sprint 5+ Enhancements

- **Incremental Clustering:** Aggiungere tuple senza ricalcolare tutto
- **Online Clustering:** Streaming data support
- **Distributed QT:** Clustering su cluster di macchine

---

## Riferimenti

### Algoritmi Ottimizzazione

- **Triangle Inequality:** Used in metric trees (R-tree, M-tree)
- **Caching:** Memoization pattern
- **Pruning:** Branch and bound techniques

### Performance Analysis

- **Big O Notation:** Analisi complessità asintotica
- **Profiling:** VisualVM, Java Flight Recorder
- **Benchmarking:** JMH (Java Microbenchmark Harness)

### Java Collections

- **HashMap:** O(1) average lookup
- **ArrayList:** Dynamic arrays with amortized O(1) append
- **HashSet:** Hash-based set implementation

---

## Definition of Done

Story completato quando:

1. ✅ Codice implementato secondo criteri accettazione
2. ✅ Test regressione passati (risultati invariati)
3. ✅ Benchmark mostra miglioramenti target
4. ✅ Memoria sotto limite specificato
5. ✅ Javadoc completo per nuove classi
6. ✅ Documentazione aggiornata (README, SPRINT_3.md)
7. ✅ Code review (self-review per progetto accademico)
8. ✅ Report performance generato e salvato

---

**Fine Sprint 3 Documentation**

**Versione:** 1.0
**Data Creazione:** 2025-11-07
**Autore:** Claude AI Assistant
**Status:** 🚧 In Sviluppo
