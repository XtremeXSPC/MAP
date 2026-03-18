# qtExt - Test, benchmark e strumenti di supporto

> **Modulo**: Verifica sperimentale e utility del progetto
> **Versione**: 1.0
> **Autore**: Lombardi Costantino

---

## Indice

1. [Descrizione generale](#descrizione-generale)
2. [Collocazione nel progetto](#collocazione-nel-progetto)
3. [Struttura interna](#struttura-interna)
4. [Package e responsabilita'](#package-e-responsabilita)
5. [Dipendenze](#dipendenze)
6. [Compilazione ed esecuzione](#compilazione-ed-esecuzione)
7. [Interpretazione dei risultati](#interpretazione-dei-risultati)
8. [Indicazioni di manutenzione](#indicazioni-di-manutenzione)
9. [Riferimenti](#riferimenti)

---

## Descrizione generale

### Finalita' del modulo

Il modulo `qtExt` raccoglie il materiale di supporto alla verifica del backend QT. Non
costituisce un componente operativo per l'utente finale, ma un ambiente di lavoro utile
per controllare la correttezza dell'algoritmo, osservare il comportamento delle
strutture dati e produrre misure sperimentali sulle prestazioni del sistema.

Dal punto di vista metodologico, `qtExt` svolge tre funzioni distinte. La prima e'
quella di test funzionale: i programmi nel package `tests` verificano proprieta' di
base dell'algoritmo e delle classi di supporto. La seconda e' quella di benchmarking:
le utility nel package `utility` confrontano l'esecuzione del clustering in diverse
configurazioni e su dataset di dimensioni differenti. La terza e' quella di
generazione di dati sintetici, utile quando si desidera costruire input riproducibili
per stress test o analisi comparative.

### Ambito coperto

| Ambito            | Descrizione                                                                           |
| ----------------- | ------------------------------------------------------------------------------------- |
| Test algoritmici  | Verifica della correttezza di `QTMiner`, del rispetto del `radius` e del determinismo |
| Test sui dati     | Controllo di caricamento, attributi continui, distanze e comportamenti di supporto    |
| Benchmark         | Misurazione di tempi, numero di cluster, uso della cache e memoria                    |
| Dataset sintetici | Produzione di file CSV di dimensione controllata per esperimenti ripetibili           |

### Carattere del modulo

Il modulo non introduce nuove funzionalita' di business nel progetto. Il suo valore e'
strumentale: rende esplicite le ipotesi di correttezza del backend e consente di
documentare sperimentalmente l'impatto di alcune scelte implementative, in particolare
quelle legate alla cache delle distanze e all'uso di strutture dati piu' efficienti.

---

## Collocazione nel progetto

`qtExt` dipende logicamente da `qtServer`, del quale riutilizza classi e package durante
la compilazione e l'esecuzione:

```text
┌─────────────┐
│    qtExt    │
│ test/bench  │
└──────┬──────┘
       │
       ▼
┌────────────────────────────┐
│         qtServer           │
│ data / mining / database   │
└────────────────────────────┘
```

Il modulo non ha senso in autonomia: i test compilano contro i binari del server e le
utility di benchmark richiamano direttamente `Data`, `QTMiner` e `DistanceCache`.

---

## Struttura interna

La struttura e' organizzata in due aree principali:

```text
qtExt/
├── tests/
│   ├── TestClusterOperations.java
│   ├── TestContinuousAttributes.java
│   ├── TestDataOperations.java
│   ├── TestDistanceCalculations.java
│   ├── TestIteratorsComparators.java
│   └── TestQTAlgorithm.java
└── utility/
    ├── DatasetGenerator.java
    ├── QTBenchmark.java
    └── RunBenchmark.java
```

Questa articolazione riflette una separazione netta tra verifica della correttezza e
analisi sperimentale delle prestazioni.

---

## Package e responsabilita'

### Package `tests`

Il package `tests` contiene programmi Java eseguibili direttamente da linea di comando.
Non si tratta di una suite JUnit tradizionale: ciascuna classe possiede un proprio
`main()` e usa asserzioni e messaggi esplicativi per evidenziare l'esito dei controlli.
Questa soluzione e' semplice, coerente con il contesto didattico del progetto e
compatibile con i Makefile del repository.

Le classi presenti coprono diversi aspetti del backend:

| Classe                     | Focus principale                                  |
| -------------------------- | ------------------------------------------------- |
| `TestQTAlgorithm`          | correttezza generale dell'algoritmo QT            |
| `TestClusterOperations`    | operazioni elementari su `Cluster` e `ClusterSet` |
| `TestDataOperations`       | caricamento e rappresentazione dei dataset        |
| `TestDistanceCalculations` | verifiche sulle misure di distanza                |
| `TestContinuousAttributes` | gestione degli attributi continui                 |
| `TestIteratorsComparators` | iteratori, confronto e supporto strutturale       |

In particolare, `TestQTAlgorithm` controlla:

- comportamento con `radius` molto piccolo;
- aggregazione con `radius` maggiore;
- copertura completa delle tuple;
- rispetto del vincolo di distanza dal centroide;
- determinismo dell'algoritmo a parita' di input.

### Package `utility`

Il package `utility` raccoglie strumenti di natura sperimentale.

| Classe             | Ruolo                                                              |
| ------------------ | ------------------------------------------------------------------ |
| `QTBenchmark`      | esecuzione di benchmark singoli o comparativi                      |
| `RunBenchmark`     | orchestrazione di una campagna di benchmark su dataset predefiniti |
| `DatasetGenerator` | generazione di file CSV sintetici per prove di scalabilita'        |

#### `QTBenchmark`

`QTBenchmark` e' la classe di riferimento per la misurazione delle prestazioni.
L'API principale e':

```java
public static BenchmarkResult runBenchmark(String datasetPath, double radius, boolean enableOptimizations)
```

Il risultato della misura comprende almeno i seguenti campi:

- nome del dataset;
- numero di tuple e di attributi;
- `radius` usato;
- presenza o meno delle ottimizzazioni;
- tempo di esecuzione;
- numero di cluster prodotti;
- statistiche della cache delle distanze;
- stima della memoria utilizzata.

La classe espone inoltre un metodo `runComparison()` che esegue il benchmark in due
varianti, con e senza ottimizzazioni, e un metodo `compareResults()` che costruisce un
report comparativo testuale.

#### `RunBenchmark`

`RunBenchmark` definisce una campagna sperimentale piu' ampia, basata sui dataset
sintetici `synthetic_small.csv`, `synthetic_medium.csv` e `synthetic_large.csv`. Il
programma produce sia una stampa di dettaglio sui log sia un report finale salvato in
`../docs/BENCHMARK_RESULTS.md`.

#### `DatasetGenerator`

`DatasetGenerator` produce dataset CSV artificiali con cardinalita' e numero di
attributi configurabili. I valori discreti sono generati a partire da insiemi simbolici
predefiniti, mentre la procedura e' governata da un `seed`, cosi' da rendere le prove
ripetibili. Il metodo `generateTestSuite()` costruisce automaticamente una piccola
famiglia di dataset di dimensioni crescenti.

---

## Dipendenze

### Dipendenze interne

`qtExt` richiede i binari di `qtServer`, che vengono richiamati in compilazione e in
esecuzione tramite classpath.

```text
qtExt ──depends on──> qtServer
  │                     │
  ├─ tests          ──> data, mining, database
  └─ utility        ──> data, mining
```

### Dipendenze esterne

| Dipendenza                | Ruolo                                            | Obbligatoria           |
| ------------------------- | ------------------------------------------------ | ---------------------- |
| JDK                       | Compilazione ed esecuzione                       | Si                     |
| Driver JDBC di `qtServer` | Necessario quando i test coinvolgono il database | Dipende dallo scenario |

Il modulo non introduce librerie esterne aggiuntive: riusa quelle gia' richieste dal
backend.

---

## Compilazione ed esecuzione

### Compilazione

Dalla directory `qtExt/`:

```bash
make compile
```

Il Makefile assicura prima la compilazione di `qtServer`, quindi compila test e utility
in `qtExt/bin`.

### Esecuzione della suite di test

```bash
make test
```

Sono inoltre disponibili target piu' specifici:

```bash
make test-distance
make test-qt
make test-cluster
make test-data
make test-iterators
make test-continuous
```

Il target `test-continuous` e' segnalato come interattivo nel Makefile e puo' quindi
richiedere un contesto leggermente diverso dagli altri test automatici.

### Esecuzione dei benchmark

Per eseguire le utility di benchmark:

```bash
cd qtExt/utility
javac -cp ../../qtServer/bin:../../qtServer/JDBC/mysql-connector-java-9.5.0.jar:. *.java
java -cp ../../qtServer/bin:../../qtServer/JDBC/mysql-connector-java-9.5.0.jar:. utility.RunBenchmark
```

In alternativa, se il modulo e' gia' stato compilato tramite Makefile, si possono
richiamare direttamente le classi presenti in `qtExt/bin`.

### Generazione dei dataset sintetici

`DatasetGenerator` puo' essere eseguito in due modalita':

- senza argomenti, per generare la suite standard;
- con argomenti, per generare un dataset personalizzato.

Esempio:

```bash
cd qtExt/utility
javac -cp ../../qtServer/bin:. DatasetGenerator.java
java -cp ../../qtServer/bin:. utility.DatasetGenerator custom.csv 500 8 12345
```

---

## Interpretazione dei risultati

### Sulla lettura dei benchmark

Le misure prodotte da `QTBenchmark` e `RunBenchmark` non hanno valore assoluto
universale: dipendono dal dataset, dal `radius`, dall'ambiente di esecuzione e dal
carico della JVM. Esse sono tuttavia molto utili in termini comparativi, cioe' quando
si vogliono confrontare due varianti dello stesso algoritmo o due dimensioni di input.

### Indicatori piu' rilevanti

| Indicatore                  | Significato                                   |
| --------------------------- | --------------------------------------------- |
| `executionTimeMs`           | tempo complessivo della fase di clustering    |
| `numClusters`               | cardinalita' finale della partizione prodotta |
| `distanceCalculations`      | numero di distanze effettivamente calcolate   |
| `cacheHits` / `cacheMisses` | efficacia della cache delle distanze          |
| `memoryUsedMB`              | stima della memoria utilizzata nel benchmark  |

### Criteri di lettura

Un aumento del tempo di esecuzione al crescere del numero di tuple e' atteso. Piu'
interessante e' osservare se le ottimizzazioni producono un miglioramento coerente su
dataset medi e grandi, e se il tasso di hit della cache giustifica il costo addizionale
di memoria. In altre parole, il benchmark va letto come strumento di analisi
comparativa, non come certificazione assoluta di performance.

---

## Indicazioni di manutenzione

### Aggiunta di un nuovo test

Per introdurre un nuovo test nel package `tests` e' opportuno mantenere la stessa
impostazione delle classi esistenti:

1. creare una classe dedicata con `main()`;
2. isolare i casi di prova in metodi leggibili;
3. usare asserzioni o controlli espliciti con messaggi diagnostici;
4. evitare dipendenze nascoste tra un test e l'altro.

### Aggiunta di un nuovo benchmark

Nel package `utility`, l'inserimento di un nuovo benchmark dovrebbe seguire una logica
simile:

1. identificare con chiarezza il dataset e il `radius`;
2. definire se il confronto e' tra baseline e versione ottimizzata;
3. salvare i risultati in un formato facilmente confrontabile nel tempo;
4. mantenere riproducibilita' del test tramite dataset stabili o `seed` fissati.

### Principio generale

La manutenzione del modulo non dovrebbe trasformare `qtExt` in una seconda applicazione
del progetto. Il suo valore consiste proprio nell'essere un laboratorio tecnico
compatto, orientato alla verifica del backend e non all'estensione delle funzionalita'
utente.

---

## Riferimenti

- [`../qtServer/README.md`](../qtServer/README.md)
- [`../docs/uml/qtExt/tests/tests_package.puml`](../docs/uml/qtExt/tests/tests_package.puml)
- [`../docs/uml/qtExt/utility/utility_package.puml`](../docs/uml/qtExt/utility/utility_package.puml)
- [`tests/TestQTAlgorithm.java`](tests/TestQTAlgorithm.java)
- [`utility/QTBenchmark.java`](utility/QTBenchmark.java)
- [`utility/RunBenchmark.java`](utility/RunBenchmark.java)
- [`utility/DatasetGenerator.java`](utility/DatasetGenerator.java)

---
