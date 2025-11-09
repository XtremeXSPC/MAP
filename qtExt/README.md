# qtExt - Quality Threshold Testing & Utilities

> **Modulo**: Testing e Benchmarking
> **Versione**: 1.0
> **Autore**: Progetto MAP - Metodi Avanzati di Programmazione

---

## Indice

1. [Descrizione Generale](#descrizione-generale)
2. [Architettura Interna](#architettura-interna)
3. [Package](#package)
4. [Dipendenze](#dipendenze)
5. [Utilizzo](#utilizzo)
6. [Interpretazione Risultati](#interpretazione-risultati)
7. [Note di Manutenzione](#note-di-manutenzione)

---

## Descrizione Generale

### Scopo del Modulo

Il modulo **qtExt** fornisce strumenti per testing e benchmarking del sistema Quality Threshold Clustering. Implementa:

- **Test suite completa** per validazione funzionalità
- **Benchmark performance** per analisi scalabilità
- **Dataset generator** per generazione dati sintetici
- **Metriche qualità clustering** per valutazione risultati

### Funzionalità Principali

| Funzionalità | Descrizione | Package |
|--------------|-------------|---------|
| **Testing** | Suite completa test unitari e integrazione | `tests` |
| **Benchmarking** | Misurazione performance e scalabilità | `utility.QTBenchmark` |
| **Data Generation** | Generazione dataset sintetici | `utility.DatasetGenerator` |

### Posizione nell'Architettura Generale

```
┌─────────────┐
│    qtExt    │
│  (Testing)  │
└──────┬──────┘
       │
       │ Direct Call
       │
       ▼
┌─────────────┐
│  qtServer   │
│  Testing:   │
│  - data     │
│  - mining   │
│  - database │
└─────────────┘
```

---

## Architettura Interna

### Struttura Package

```
qtExt/
├── tests/                    # Test suite
│   ├── TestQTAlgorithm.java
│   ├── TestClusterOperations.java
│   ├── TestDataOperations.java
│   ├── TestDistanceCalculations.java
│   ├── TestContinuousAttributes.java
│   └── TestIteratorsComparators.java
│
└── utility/                  # Utility e benchmark
    ├── QTBenchmark.java
    ├── RunBenchmark.java
    └── DatasetGenerator.java
```

---

## Package

### Package `tests` - Test Suite

**Scopo**: Validazione funzionalità qtServer.

**Classi test**:

| Test | Scopo |
|------|-------|
| `TestQTAlgorithm` | Verifica correttezza algoritmo QT |
| `TestClusterOperations` | Test operazioni su Cluster |
| `TestDataOperations` | Test caricamento dati (CSV, DB) |
| `TestDistanceCalculations` | Verifica metriche distanza |
| `TestContinuousAttributes` | Test attributi continui |
| `TestIteratorsComparators` | Test iteratori e comparatori |

**Approccio testing**:
- Test unitari per singole classi
- Test di integrazione per flussi completi
- Asserzioni su output attesi

**Esecuzione**:

```bash
# Singolo test
cd qtExt/tests
javac -cp ../../qtServer/bin:. TestQTAlgorithm.java
java -cp ../../qtServer/bin:. TestQTAlgorithm

# Tutti i test
for test in Test*.class; do
    java -cp ../../qtServer/bin:. ${test%.class}
done
```

**Diagramma UML**: `docs/uml/qtExt/tests/tests_package.puml`

---

### Package `utility` - Benchmarking

**Classi**:

| Utility | Scopo |
|---------|-------|
| `QTBenchmark` | Framework benchmarking performance |
| `RunBenchmark` | Runner per esecuzione benchmark |
| `DatasetGenerator` | Generazione dataset sintetici |

#### QTBenchmark

**API**:

```java
public static BenchmarkResult runBenchmark(
    String datasetPath,
    double radius,
    boolean enableOptimizations
)
```

**Metriche misurate**:
- `executionTimeMs`: Tempo esecuzione (ms)
- `numClusters`: Numero cluster generati
- `distanceCalculations`: Calcoli distanza effettuati
- `cacheHits/cacheMisses`: Efficacia caching
- `memoryUsedMB`: Consumo memoria (MB)

**Esempio utilizzo**:

```java
BenchmarkResult result = QTBenchmark.runBenchmark("data/iris.csv", 0.5, true);
System.out.println(result);
```

#### RunBenchmark

**Scopo**: Eseguire suite completa benchmark su dataset multipli.

**Esecuzione**:

```bash
cd qtExt/utility
javac -cp ../../qtServer/bin:. RunBenchmark.java
java -cp ../../qtServer/bin:. RunBenchmark
```

**Output**:

```
=== Benchmark Results ===

Dataset: PlayTennis (14 tuple, 5 attributi) | Raggio: 0.50 | Ott: OFF
  Tempo: 15 ms | Cluster: 5 | Calcoli: 91
  Cache: hit=0, miss=0, tasso=0.0% | Memoria: 2 MB

Dataset: Iris (150 tuple, 5 attributi) | Raggio: 0.50 | Ott: ON
  Tempo: 342 ms | Cluster: 11 | Calcoli: 11175
  Cache: hit=8924, miss=2251, tasso=79.9% | Memoria: 8 MB

...
```

#### DatasetGenerator

**Scopo**: Generare dataset sintetici per testing scalabilità.

**API**:

```java
public static Data generateRandomDataset(
    int numTuples,
    int numDiscreteAttributes,
    int numContinuousAttributes,
    int numDistinctValues
)
```

**Esempio**:

```java
// Genera dataset 1000 tuple, 5 attributi discreti, 2 continui
Data data = DatasetGenerator.generateRandomDataset(1000, 5, 2, 10);
```

**Diagramma UML**: `docs/uml/qtExt/utility/utility_package.puml`

---

## Dipendenze

### Dipendenze Interne

```
qtExt ──depends on──> qtServer
  │                     │
  ├─ tests          ──> data, mining, database
  └─ utility        ──> data, mining
```

### Dipendenze Esterne

| Libreria | Versione | Scopo | Obbligatoria |
|----------|----------|-------|--------------|
| **JDK** | 8+ | Runtime Java | SI |

**Nota**: Nessuna libreria esterna richiesta.

---

## Utilizzo

### Esecuzione Test Suite

```bash
# Compila tests
cd qtExt/tests
javac -cp ../../qtServer/bin:. Test*.java

# Esegui tutti i test
for test in Test*.class; do
    echo "Running ${test%.class}..."
    java -cp ../../qtServer/bin:. ${test%.class}
    echo "---"
done
```

### Esecuzione Benchmark

```bash
# Compila utility
cd qtExt/utility
javac -cp ../../qtServer/bin:. *.java

# Esegui benchmark completo
java -cp ../../qtServer/bin:. RunBenchmark
```

**Output salvato in**: `benchmark_results.txt`

### Generazione Dataset Sintetici

```java
import utility.DatasetGenerator;

// Genera dataset grande per stress testing
Data largeDataset = DatasetGenerator.generateRandomDataset(
    5000,  // tuple
    8,     // attributi discreti
    3,     // attributi continui
    15     // valori distinti per discreti
);

// Esegui clustering
QTMiner miner = new QTMiner(0.5);
miner.compute(largeDataset);
```

---

## Interpretazione Risultati

### Metriche Benchmark

#### Execution Time

**Interpretazione**:
- < 100 ms: Eccellente (dataset piccoli)
- 100-500 ms: Buono (dataset medi)
- 500-2000 ms: Accettabile (dataset grandi)
- > 2000 ms: Lento (ottimizzazioni necessarie)

#### Cache Hit Rate

**Interpretazione**:
- > 80%: Cache molto efficace
- 50-80%: Cache moderatamente efficace
- < 50%: Cache poco efficace (overhead > beneficio)

**Raccomandazione**:
- Disabilita caching se hit rate < 40%
- Dataset < 500 tuple: Caching non consigliato

#### Memory Usage

**Interpretazione**:
- Crescita lineare O(n): Normale
- Crescita quadratica O(n²): Problematica (leak possibile)

### Analisi Scalabilità

**Test scalabilità**:

```bash
# Benchmark con dataset crescenti
for size in 100 500 1000 2000 5000; do
    Data data = DatasetGenerator.generateRandomDataset(size, 5, 2, 10);
    BenchmarkResult result = QTBenchmark.runBenchmark(data, 0.5, false);
    echo "$size tuple: ${result.executionTimeMs} ms"
done
```

**Output atteso**:

```
100 tuple: 25 ms
500 tuple: 124 ms
1000 tuple: 496 ms (crescita quadratica)
2000 tuple: 1984 ms
5000 tuple: 12400 ms
```

**Complessità verificata**: O(n²) per buildCandidateCluster.

---

## Note di Manutenzione

### Aggiunta Nuovo Test

**Passi**:

1. Crea `TestNewFeature.java` in `tests/`
2. Implementa metodo `main()` con test cases
3. Usa asserzioni per validazione:

```java
public class TestNewFeature {
    public static void main(String[] args) {
        System.out.println("Testing new feature...");

        // Test case 1
        assert condition1 : "Test 1 failed";

        // Test case 2
        assert condition2 : "Test 2 failed";

        System.out.println("All tests passed!");
    }
}
```

4. Compila ed esegui con `-ea` (enable assertions):

```bash
java -ea -cp ../../qtServer/bin:. TestNewFeature
```

### Aggiunta Nuovo Benchmark

**Passi**:

1. Aggiungi dataset in `data/`
2. Modifica `RunBenchmark.main()`:

```java
// In RunBenchmark.java
results.add(QTBenchmark.runBenchmark("data/newdataset.csv", 0.5, false));
```

3. Esegui e analizza risultati

### Best Practices Testing

1. **Test isolati**: Ogni test indipendente
2. **Dataset fissi**: Usa dataset deterministici per riproducibilità
3. **Asserzioni chiare**: Messaggi di errore descrittivi
4. **Cleanup**: Chiudi risorse (file, DB) dopo test

---

## Riferimenti

### Documentazione Relata

- [`qtServer/README.md`](../qtServer/README.md) - Modulo testato
- [`docs/BENCHMARK_RESULTS.md`](../docs/BENCHMARK_RESULTS.md) - Risultati benchmark

### Diagrammi UML

- `docs/uml/qtExt/tests/tests_package.puml`
- `docs/uml/qtExt/utility/utility_package.puml`

---

**Versione**: 1.0
**Data**: 2025-11-09
**Autore**: Progetto MAP
