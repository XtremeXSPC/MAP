# qtServer - Quality Threshold Clustering Server

> **Modulo**: Server Multi-Client per Quality Threshold Clustering
> **Versione**: 1.0
> **Autore**: Progetto MAP - Metodi Avanzati di Programmazione

---

## Indice

1. [Descrizione Generale](#descrizione-generale)
2. [Architettura Interna](#architettura-interna)
3. [Package](#package)
4. [Dipendenze](#dipendenze)
5. [Interfacce Pubbliche (API)](#interfacce-pubbliche-api)
6. [Interazioni con Altri Moduli](#interazioni-con-altri-moduli)
7. [Build e Compilazione](#build-e-compilazione)
8. [Testing](#testing)
9. [Configurazione](#configurazione)
10. [Note di Manutenzione](#note-di-manutenzione)

---

## Descrizione Generale

### Scopo del Modulo

Il modulo **qtServer** costituisce il nucleo computazionale del sistema Quality Threshold Clustering. Implementa:

- **Algoritmo QT** completo con ottimizzazioni per performance
- **Server multi-client** basato su Socket TCP/IP per clustering distribuito
- **Integrazione database** JDBC per caricamento dati da MySQL
- **Gestione dati** con supporto attributi discreti e continui
- **Serializzazione cluster** per persistenza risultati

### Funzionalità Principali

| Funzionalità | Descrizione | Package |
|--------------|-------------|---------|
| **Clustering QT** | Algoritmo Quality Threshold con garanzia di qualità | `mining` |
| **Server Socket** | Server multi-threaded per client concorrenti | `server` |
| **Gestione Dati** | Caricamento da CSV, database, hardcoded | `data` |
| **Database JDBC** | Connessione MySQL e query su tabelle | `database` |

### Posizione nell'Architettura Generale

```
┌─────────────┐         ┌─────────────┐
│   qtClient  │◄───────►│  qtServer   │
│    (CLI)    │  Socket │  (Core)     │
└─────────────┘         └──────┬──────┘
                               │
┌─────────────┐                │
│    qtGUI    │◄───────────────┘
│  (JavaFX)   │   Direct Call
└─────────────┘

                qtServer
                    │
            ┌───────┼───────┐
            │       │       │
         data   database  mining
```

Il modulo **qtServer** è il componente centrale che può essere utilizzato:
- Come **server standalone** per clustering distribuito (qtClient)
- Come **libreria** integrata direttamente nella GUI (qtGUI)
- Da **utility di testing** e benchmarking (qtExt)

---

## Architettura Interna

### Struttura Package

Il modulo è organizzato in **4 package** con responsabilità ben definite:

```
qtServer/src/
├── data/         # Gestione dataset e attributi
├── database/     # Integrazione JDBC MySQL
├── mining/       # Algoritmo clustering QT
└── server/       # Comunicazione client-server
```

### Pattern di Design Utilizzati

#### 1. Template Method Pattern

**Package**: `data`

- **Abstract Class**: `Attribute`, `Item`
- **Concrete Classes**: `DiscreteAttribute`, `ContinuousAttribute`, `DiscreteItem`, `ContinuousItem`
- **Beneficio**: Astrazione del concetto di attributo con specializzazioni per tipo

```java
// Template method per calcolo distanza
public abstract class Item {
    public abstract double distance(Object a);
}

// Implementazione per attributi discreti (Hamming)
public class DiscreteItem extends Item {
    public double distance(Object a) {
        return this.getValue().equals(a) ? 0 : 1;
    }
}

// Implementazione per attributi continui (Euclidea normalizzata)
public class ContinuousItem extends Item {
    public double distance(Object a) {
        ContinuousAttribute attribute = (ContinuousAttribute) this.getAttribute();
        double scaledThis = attribute.getScaledValue((Double) this.getValue());
        double scaledOther = attribute.getScaledValue((Double) a);
        return Math.abs(scaledThis - scaledOther);
    }
}
```

#### 2. Strategy Pattern

**Package**: `mining`

- **Context**: `QTMiner`
- **Strategy**: Caching abilitato/disabilitato via parametro costruttore
- **Beneficio**: Configurazione runtime delle ottimizzazioni

#### 3. Multi-Threading Pattern

**Package**: `server`

- **Pattern**: Thread-per-Client con `ServerOneClient`
- **Beneficio**: Gestione concorrente di client multipli

```java
// MultiServer accetta connessioni
Socket clientSocket = serverSocket.accept();
// Crea thread dedicato per ogni client
new ServerOneClient(clientSocket);
```

#### 4. Singleton-like Pattern

**Package**: `database`

- **Class**: `DbAccess`
- **Pattern**: Gestione centralizzata della connessione database
- **Beneficio**: Controllo accesso risorse condivise

### Diagramma Organizzazione

Riferimenti ai diagrammi UML dettagliati:

- [`docs/uml/qtServer/data/data_package.puml`](../docs/uml/qtServer/data/data_package.puml)
- [`docs/uml/qtServer/database/database_package.puml`](../docs/uml/qtServer/database/database_package.puml)
- [`docs/uml/qtServer/mining/mining_package.puml`](../docs/uml/qtServer/mining/mining_package.puml)
- [`docs/uml/qtServer/server/server_package.puml`](../docs/uml/qtServer/server/server_package.puml)

---

## Package

### Package `data`

**Scopo**: Rappresentazione e gestione dei dataset, attributi e tuple.

**Classi principali**:

| Classe | Tipo | Responsabilità |
|--------|------|----------------|
| `Attribute` | Abstract | Classe base per attributi |
| `DiscreteAttribute` | Concrete | Attributi categorici (es. "sunny", "rain") |
| `ContinuousAttribute` | Concrete | Attributi numerici con normalizzazione |
| `Item` | Abstract | Coppia (attributo, valore) |
| `DiscreteItem` | Concrete | Item con distanza di Hamming |
| `ContinuousItem` | Concrete | Item con distanza Euclidea |
| `Tuple` | Concrete | Riga del dataset (sequenza di Item) |
| `Data` | Concrete | Gestione completa dataset |

**Interfacce pubbliche**:

```java
// Caricamento dataset
Data data = new Data();                          // Hardcoded PlayTennis
Data data = new Data("playtennis.csv");          // Da file CSV
Data data = new Data("tennis", true);            // Da database MySQL

// Accesso ai dati
int numExamples = data.getNumberOfExamples();
Tuple tuple = data.getItemSet(index);
Object value = data.getValue(exampleIndex, attributeIndex);
```

**Eccezioni**:
- `EmptyDatasetException`: Lanciata quando dataset vuoto
- `InvalidDataFormatException`: Lanciata per errori di formato CSV

**Diagramma UML**: `docs/uml/qtServer/data/data_package.puml`

---

### Package `database`

**Scopo**: Integrazione JDBC con MySQL per caricamento dati da database relazionali.

**Classi principali**:

| Classe | Tipo | Responsabilità |
|--------|------|----------------|
| `DbAccess` | Concrete | Gestione connessione MySQL |
| `TableSchema` | Concrete | Metadata schema tabella |
| `TableData` | Concrete | Lettura dati da tabella |
| `Example` | Concrete | Singola riga da database |
| `QUERY_TYPE` | Enum | Tipo query (MIN, MAX) |

**Interfacce pubbliche**:

```java
// Connessione al database
DbAccess db = new DbAccess();
db.initConnection();

// Lettura schema tabella
TableSchema schema = new TableSchema(db, "playtennis");
int numColumns = schema.getNumberOfAttributes();

// Lettura dati
TableData tableData = new TableData(db);
List<Example> examples = tableData.getDistinctTransazioni("playtennis");

// Chiusura connessione
db.closeConnection();
```

**Eccezioni**:
- `DatabaseConnectionException`: Errore connessione al database
- `EmptySetException`: Query restituisce risultato vuoto
- `NoValueException`: Valore non trovato

**Configurazione**:
- **Server**: localhost (default)
- **Port**: 3306 (default)
- **Database**: MapDB (default)
- **User**: MapUser / Password: map

**Diagramma UML**: `docs/uml/qtServer/database/database_package.puml`

---

### Package `mining`

**Scopo**: Implementazione algoritmo Quality Threshold con ottimizzazioni.

**Classi principali**:

| Classe | Tipo | Responsabilità |
|--------|------|----------------|
| `QTMiner` | Concrete | Algoritmo QT principale |
| `Cluster` | Concrete | Singolo cluster con centroide |
| `ClusterSet` | Concrete | Insieme di cluster |
| `DistanceCache` | Concrete | Cache distanze per ottimizzazioni |
| `SerializableClusteringData` | Concrete | Wrapper serializzabile completo |

**Interfacce pubbliche**:

```java
// Esecuzione clustering
Data data = new Data("dataset.csv");
QTMiner qt = new QTMiner(0.5);  // radius = 0.5
int numClusters = qt.compute(data);
ClusterSet clusters = qt.getC();

// Salvataggio risultati
qt.save("results");  // Crea file results.dmp

// Caricamento risultati
QTMiner loadedQt = new QTMiner("results");
ClusterSet loadedClusters = loadedQt.getC();
```

**Algoritmo Quality Threshold**:

```
ALGORITMO QT(data, radius)
  INPUT: Dataset, Raggio massimo cluster
  OUTPUT: ClusterSet con garanzia di qualità

  1. Inizializzazione
     clusters ← insieme vuoto
     unclustered ← tutti i punti

  2. MENTRE existono punti non clusterizzati FARE
     2.1 bestCluster ← buildCandidateCluster(unclustered)
     2.2 clusters.add(bestCluster)
     2.3 unclustered.remove(bestCluster.points)

  3. RETURN clusters

FUNZIONE buildCandidateCluster(points)
  1. PER OGNI punto p IN points FARE
     1.1 cluster ← {tutti i punti a distanza ≤ radius da p}
     1.2 SE cluster.size > maxSize ALLORA
         bestCluster ← cluster
         maxSize ← cluster.size

  2. RETURN bestCluster
```

**Complessità**:
- **Temporale**: O(k × n²), caso peggiore O(n³)
- **Spaziale**: O(n)

**Ottimizzazioni implementate**:
- **Caching distanze** (opzionale, configurabile)
- **HashSet** per ricerca O(1) membership
- **ArrayList** per accesso diretto O(1)

**Eccezioni**:
- `ClusteringRadiusException`: Radius negativo o non valido
- `IncompatibleClusterException`: Cluster incompatibile con dataset
- `InvalidFileFormatException`: File .dmp malformato

**Diagramma UML**: `docs/uml/qtServer/mining/mining_package.puml`

---

### Package `server`

**Scopo**: Comunicazione client-server tramite Socket TCP/IP per clustering distribuito.

**Classi principali**:

| Classe | Tipo | Responsabilità |
|--------|------|----------------|
| `MultiServer` | Concrete | Server multi-client su porta configurabile |
| `ServerOneClient` | Thread | Thread dedicato per ogni client connesso |

**Interfacce pubbliche**:

```java
// Avvio server (main class)
java -cp qtServer/bin server.MultiServer <port>

// Esempio: porta 8080
java -cp qtServer/bin server.MultiServer 8080
```

**Protocollo di Comunicazione**:

Il protocollo Socket utilizza serializzazione Java di oggetti tramite `ObjectInputStream`/`ObjectOutputStream`.

**Comandi supportati**:

| Comando | Descrizione | Parametri |
|---------|-------------|-----------|
| `0` | Carica dati da file | `String fileName` |
| `1` | Carica dati da database | `String tableName` |
| `2` | Esegui clustering | nessuno |
| `3` | Salva cluster su file | `String fileName` |

**Flusso comunicazione**:

```
Client                          Server
  │                              │
  ├─────── Connessione ─────────>│ (accept)
  │                              │
  │                         [Thread creato]
  │                              │
  ├───── Comando 0/1 ───────────>│ (caricamento dati)
  │<────── Ack/Error ─────────────┤
  │                              │
  ├───── Comando 2 ─────────────>│ (clustering)
  │<────── ClusterSet ────────────┤
  │                              │
  ├───── Comando 3 ─────────────>│ (salvataggio)
  │<────── Ack ───────────────────┤
  │                              │
  └───── Disconnessione ─────────>│ (chiusura thread)
```

**Gestione errori**:
- Eccezioni serializzate e inviate al client
- Log server-side con timestamp
- Chiusura graceful delle connessioni

**Thread Safety**:
- Ogni client ha thread dedicato e dati isolati
- Nessuna condivisione stato tra client
- Thread pool implicito (un thread per connessione)

**Diagramma UML**: `docs/uml/qtServer/server/server_package.puml`

---

## Dipendenze

### Dipendenze Interne

Il modulo qtServer è **standalone** e non dipende da altri moduli del progetto.

**Relazioni package interne**:

```
server ──depends on──> mining, data, database
mining ──depends on──> data
database ──independent──
data ──independent──
```

### Dipendenze Esterne

| Libreria | Versione | Scopo | Obbligatoria |
|----------|----------|-------|--------------|
| **JDK** | 8+ | Runtime Java | SI |
| **MySQL Connector/J** | 8.0+ | Driver JDBC MySQL | NO (solo se usi database) |

**Importazione MySQL Connector**:

```bash
# Aggiungi al classpath
javac -cp .:mysql-connector-java-8.0.28.jar qtServer/src/**/*.java

# Oppure posiziona mysql-connector-java-*.jar in lib/
```

**Nota**: Il sistema funziona anche **senza database** caricando dati da CSV o hardcoded.

---

## Interfacce Pubbliche (API)

### API Entry Point

#### 1. Server Multi-Client (Deployment)

**Classe**: `server.MultiServer`

```bash
# Compila server
cd qtServer
javac -d bin src/**/*.java

# Avvia server su porta 8080
java -cp bin server.MultiServer 8080
```

**Output atteso**:
```
===========================================
QT Server avviato sulla porta 8080
In attesa di connessioni client...
===========================================
```

#### 2. Algoritmo QT (Programmatic Use)

**Classe**: `mining.QTMiner`

```java
import data.Data;
import mining.QTMiner;
import mining.ClusterSet;

// Carica dataset
Data data = new Data("playtennis.csv");

// Esegui clustering
QTMiner miner = new QTMiner(0.5);  // radius = 0.5
int numClusters = miner.compute(data);

// Ottieni risultati
ClusterSet clusters = miner.getC();
System.out.println(clusters.toString(data));

// Salva su file
miner.save("results");
```

#### 3. Database Loading

**Classe**: `database.DbAccess`

```java
import database.DbAccess;
import data.Data;

// Connetti a database
DbAccess db = new DbAccess();
db.initConnection();

// Carica dati da tabella
Data data = new Data(db, "playtennis");

// Chiudi connessione
db.closeConnection();
```

---

## Interazioni con Altri Moduli

### qtServer ← qtClient

**Tipo**: Comunicazione Socket TCP/IP

**Scenario**: Client CLI si connette al server per clustering remoto.

```
qtClient                qtServer
   │                       │
   ├── Socket.connect() ──>│ MultiServer.accept()
   │                       │
   │                   [Thread creato]
   │                       │
   ├── Invia comando ─────>│ ServerOneClient
   │                       ├─> QTMiner.compute()
   │<── Riceve risultati ──┤
   │                       │
   └── Disconnessione ────>│ Thread terminato
```

**Riferimenti**:
- Vedere [`qtClient/README.md`](../qtClient/README.md)
- Sequence diagram: `docs/uml/workflows/client_server_communication_sequence.puml`

### qtServer ← qtGUI

**Tipo**: Chiamata diretta (no Socket)

**Scenario**: GUI utilizza qtServer come libreria integrata per clustering locale.

```
qtGUI                      qtServer
   │                          │
   │ ClusteringService        │
   │    │                     │
   │    ├── new QTMiner()  ──>│ mining.QTMiner
   │    ├── compute()     ───>│
   │    │<── ClusterSet   ────┤
   │    │                     │
   │    └── Aggiorna UI       │
```

**Vantaggi**:
- Nessun overhead di rete
- Velocità massima
- Esecuzione locale semplificata

**Riferimenti**:
- Vedere [`qtGUI/README.md`](../qtGUI/README.md)

### qtServer ← qtExt

**Tipo**: Chiamata diretta per testing e benchmarking

**Scenario**: Test suite e benchmark utilizzano qtServer direttamente.

```
qtExt/tests               qtServer
   │                         │
   │ TestQTAlgorithm         │
   │    ├── new Data()    ──>│ data.Data
   │    ├── new QTMiner() ──>│ mining.QTMiner
   │    ├── compute()    ───>│
   │    └── assert results   │
```

**Riferimenti**:
- Vedere [`qtExt/README.md`](../qtExt/README.md)

---

## Build e Compilazione

### Compilazione Manuale

#### Opzione 1: javac diretto

```bash
# Da directory MAP/
cd qtServer

# Compila tutti i file
javac -d bin src/**/*.java

# Verifica classi generate
ls -R bin/
```

**Output atteso**:
```
bin/
├── data/
│   ├── Attribute.class
│   ├── Data.class
│   ├── DiscreteAttribute.class
│   ├── DiscreteItem.class
│   ├── Tuple.class
│   └── ...
├── database/
│   ├── DbAccess.class
│   ├── TableSchema.class
│   └── ...
├── mining/
│   ├── Cluster.class
│   ├── ClusterSet.class
│   ├── QTMiner.class
│   └── ...
└── server/
    ├── MultiServer.class
    └── ServerOneClient.class
```

#### Opzione 2: Makefile (Raccomandato)

```bash
# Da directory MAP/
make server         # Compila solo qtServer
make all            # Compila tutto (client + server)
make rebuild        # Pulisce e ricompila
```

**Riferimenti**: Vedere [`docs/MAKEFILE_GUIDE.md`](../docs/MAKEFILE_GUIDE.md)

### Creazione JAR

```bash
# Crea JAR eseguibile per server
make server-jar

# Output: qtServer.jar nella root del progetto
ls -lh qtServer.jar
```

**Esecuzione JAR**:

```bash
# Avvia server da JAR
java -jar qtServer.jar 8080
```

### Compilazione con Database Support

Se utilizzi database MySQL:

```bash
# Compila con MySQL Connector nel classpath
javac -cp .:lib/mysql-connector-java-8.0.28.jar \
      -d qtServer/bin \
      qtServer/src/**/*.java

# Esecuzione con classpath
java -cp qtServer/bin:lib/mysql-connector-java-8.0.28.jar \
     server.MultiServer 8080
```

---

## Testing

### Approccio di Testing

Il modulo qtServer è testato tramite:

1. **Unit test** nel modulo [`qtExt/tests/`](../qtExt/tests/)
2. **Integration test** con qtClient
3. **Benchmark** di performance

### Test Disponibili

| Test | File | Scopo |
|------|------|-------|
| Algoritmo QT | `TestQTAlgorithm.java` | Verifica correttezza clustering |
| Operazioni cluster | `TestClusterOperations.java` | Test add/remove/iterate |
| Operazioni dati | `TestDataOperations.java` | Test caricamento CSV/DB |
| Calcoli distanze | `TestDistanceCalculations.java` | Verifica metriche Hamming/Euclidea |
| Attributi continui | `TestContinuousAttributes.java` | Test normalizzazione |

### Esecuzione Test

```bash
# Da directory MAP/
cd qtExt/tests

# Compila test (richiede qtServer compilato)
javac -cp ../../qtServer/bin:. Test*.java

# Esegui singolo test
java -cp ../../qtServer/bin:. TestQTAlgorithm

# Esegui tutti i test
for test in Test*.class; do
    java -cp ../../qtServer/bin:. ${test%.class}
done
```

### Benchmark Performance

```bash
# Esegui benchmark completo
cd qtExt/utility
java -cp ../../qtServer/bin:. RunBenchmark
```

**Metriche misurate**:
- Tempo esecuzione (ms)
- Numero cluster generati
- Distanza media cluster
- Consumo memoria (heap size)

**Riferimenti**: Vedere [`docs/BENCHMARK_RESULTS.md`](../docs/BENCHMARK_RESULTS.md)

---

## Configurazione

### Configurazione Database

**File**: `qtServer/src/database/DbAccess.java`

Modifica i parametri di connessione:

```java
private String SERVER = "localhost";      // Indirizzo server MySQL
private String DATABASE = "MapDB";        // Nome database
private String PORT = "3306";             // Porta MySQL
private String USER_ID = "MapUser";       // Username
private String PASSWORD = "map";          // Password
```

**Setup database**:

```bash
# Crea database e utente
mysql -u root -p < setup_database.sql

# Importa dati da CSV
./import_csv.sh data/playtennis.csv playtennis
```

**Riferimenti**: Vedere `setup_database.sql` nella root del progetto.

### Configurazione Server

**Porta server**: Modificabile tramite argomento linea di comando.

```bash
# Porta default (8080)
java -jar qtServer.jar

# Porta custom (9999)
java -jar qtServer.jar 9999
```

**Costante nel codice**:

```java
// qtServer/src/server/MultiServer.java
private static final int PORT = 8080;  // Porta di default
```

### Parametri Algoritmo QT

**Radius**: Parametro critico che determina la qualità dei cluster.

```java
QTMiner miner = new QTMiner(0.5);  // radius = 0.5
```

**Linee guida radius**:
- **0.0 - 0.3**: Cluster molto stretti, alta purezza, molti cluster piccoli
- **0.4 - 0.6**: Bilanciato (raccomandato per la maggior parte dei casi)
- **0.7 - 1.0**: Cluster aggregati, pochi cluster grandi, minore purezza

**Caching**: Abilitazione ottimizzazioni (sperimentale).

```java
QTMiner miner = new QTMiner(0.5, true);  // Abilita caching distanze
```

**Nota**: Benchmark mostrano che il caching è vantaggioso solo per dataset > 500 tuple.

---

## Note di Manutenzione

### Considerazioni per Modifiche Future

#### 1. Aggiunta Nuovi Tipi di Attributi

Per aggiungere un nuovo tipo di attributo (es. `DateAttribute`):

1. Estendi `Attribute` in `data/`
2. Crea corrispondente `DateItem extends Item`
3. Implementa metodo `distance()` con metrica appropriata
4. Aggiorna `Data.getItemSet()` per riconoscere il nuovo tipo

**Principio**: Template Method Pattern facilita estensioni.

#### 2. Ottimizzazioni Algoritmo QT

Aree di miglioramento identificate:

- **Pruning**: Evitare calcoli distanza per punti ovviamente troppo lontani
- **Spatial indexing**: R-tree per ricerca nearest neighbors
- **Parallelizzazione**: ExecutorService per costruzione cluster candidati in parallelo
- **Euristica selezione centroidi**: Invece di provare tutti i punti, seleziona campione rappresentativo

**Riferimenti**: Vedere `docs/SPRINT_ROADMAP.md` Sprint 3 (Ottimizzazioni Performance).

#### 3. Protocollo Server Estensibile

Per aggiungere nuovi comandi al protocollo Socket:

1. Definisci nuovo codice comando in `ServerOneClient`
2. Implementa logica nel metodo `run()`
3. Documenta comando nel protocollo
4. Aggiorna client (`qtClient`)

**Esempio** (aggiunta comando "GET_STATISTICS"):

```java
// ServerOneClient.java
int command = (int) in.readObject();

switch(command) {
    // ... comandi esistenti ...
    case 4:  // GET_STATISTICS
        ClusterStatistics stats = computeStatistics();
        out.writeObject(stats);
        break;
}
```

#### 4. Gestione Memoria per Dataset Grandi

Attualmente tutto il dataset viene caricato in memoria. Per dataset > 10,000 tuple:

- **Streaming**: Leggere tuple in batch
- **Out-of-core clustering**: Algoritmo modificato per accesso disco
- **Database-backed**: Mantenere dati su DB, caricare solo necessario

**Trade-off**: Performance vs scalabilità.

### Best Practices

#### Serializzazione

Usa sempre `SerializableClusteringData` per salvare cluster completi:

```java
// GOOD: Salva tutto (cluster + data + radius)
miner.save("results");

// BAD: Salva solo ClusterSet (informazioni incomplete)
ClusterSet clusters = miner.getC();
// Serializzazione diretta di clusters perde contesto
```

#### Gestione Connessioni Database

Chiudi sempre le connessioni:

```java
DbAccess db = new DbAccess();
try {
    db.initConnection();
    // ... usa database ...
} finally {
    db.closeConnection();  // SEMPRE chiudere
}
```

#### Thread Safety Server

Ogni thread client gestisce dati propri. Non condividere oggetti tra thread:

```java
// GOOD: Ogni thread ha propria istanza
class ServerOneClient extends Thread {
    private Data data;  // Locale al thread
    private QTMiner miner;  // Locale al thread
}

// BAD: Variabile statica condivisa
class ServerOneClient extends Thread {
    private static Data sharedData;  // EVITARE!
}
```

---

## Riferimenti Aggiuntivi

### Documentazione Sprint

- [`docs/sprints/SPRINT_0.md`](../docs/sprints/SPRINT_0.md) - Struttura base (package data)
- [`docs/sprints/SPRINT_1.md`](../docs/sprints/SPRINT_1.md) - Algoritmo QT (package mining)
- [`docs/sprints/SPRINT_7.md`](../docs/sprints/SPRINT_7.md) - Database JDBC (package database)
- [`docs/sprints/SPRINT_8.md`](../docs/sprints/SPRINT_8.md) - Socket Server (package server)

### Diagrammi UML

- `docs/uml/qtServer/data/data_package.puml`
- `docs/uml/qtServer/database/database_package.puml`
- `docs/uml/qtServer/mining/mining_package.puml`
- `docs/uml/qtServer/server/server_package.puml`

### Guide di Utilizzo

- [`docs/MAKEFILE_GUIDE.md`](../docs/MAKEFILE_GUIDE.md) - Compilazione con Makefile
- [`docs/BENCHMARK_RESULTS.md`](../docs/BENCHMARK_RESULTS.md) - Risultati benchmark performance
- [`CLAUDE.md`](../CLAUDE.md) - Contesto completo progetto

---

**Versione documento**: 1.0
**Data ultimo aggiornamento**: 2025-11-09
**Autore**: Progetto MAP - Quality Threshold Clustering
