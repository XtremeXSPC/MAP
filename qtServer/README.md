# qtServer - Backend e server socket per QT Clustering

> **Modulo**: Nucleo computazionale del progetto
> **Versione**: 1.0
> **Autore**: Lombardi Costantino

---

## Indice

1. [Descrizione generale](#descrizione-generale)
2. [Posizione nell'architettura complessiva](#posizione-nellarchitettura-complessiva)
3. [Struttura del modulo](#struttura-del-modulo)
4. [Package e responsabilita'](#package-e-responsabilita)
5. [Interfacce e punti di accesso principali](#interfacce-e-punti-di-accesso-principali)
6. [Protocollo server](#protocollo-server)
7. [Dipendenze](#dipendenze)
8. [Build, esecuzione e artefatti](#build-esecuzione-e-artefatti)
9. [Relazioni con gli altri moduli](#relazioni-con-gli-altri-moduli)
10. [Indicazioni di manutenzione](#indicazioni-di-manutenzione)
11. [Riferimenti](#riferimenti)

---

## Descrizione generale

### Finalita' del modulo

Il modulo `qtServer` costituisce il centro logico e computazionale dell'intero progetto.
In esso convergono la rappresentazione dei dataset, l'implementazione dell'algoritmo
Quality Threshold, l'integrazione con il database MySQL e l'infrastruttura di
comunicazione necessaria alla modalita' client-server. Tutti gli altri moduli del
repository, pur con finalita' differenti, fanno riferimento diretto o indiretto a
questo nucleo.

Dal punto di vista funzionale il modulo copre quattro esigenze fondamentali:

| Ambito                  | Descrizione                                                   |
| ----------------------- | ------------------------------------------------------------- |
| Modellazione dati       | Rappresentazione di attributi, tuple e insiemi di esempi      |
| Clustering              | Esecuzione dell'algoritmo QT e gestione dei cluster prodotti  |
| Accesso ai dati esterni | Import da database relazionale tramite JDBC                   |
| Modalita' remota        | Esposizione del backend attraverso un server TCP multi-client |

### Ruolo nel progetto

`qtServer` puo' essere usato in tre forme differenti:

1. come backend locale incorporato nella GUI;
2. come server di rete per il client testuale;
3. come libreria di riferimento per test e benchmark nel modulo `qtExt`.

Questa triplice funzione spiega perche' il modulo abbia una struttura piu' articolata
degli altri e perche' la sua documentazione debba essere letta anche come guida alla
comprensione del progetto nel suo complesso.

---

## Posizione nell'architettura complessiva

Il rapporto tra `qtServer` e gli altri moduli puo' essere rappresentato nel modo
seguente:

```text
┌──────────────┐         TCP/IP         ┌──────────────┐
│   qtClient   │◄──────────────────────►│   qtServer   │
│    (CLI)     │                        │    (core)    │
└──────────────┘                        └──────┬───────┘
                                               │
                                               │ chiamate dirette
                                               ▼
┌──────────────┐                        ┌──────────────┐
│     qtExt    │                        │    qtGUI     │
│  test/bench  │                        │   (JavaFX)   │
└──────────────┘                        └──────────────┘
```

Ne segue una distinzione concettuale importante:

- `qtClient` interagisce con `qtServer` come processo remoto tramite protocollo TCP;
- `qtGUI` e `qtExt` riusano le classi del modulo come libreria locale.

---

## Struttura del modulo

La struttura dei sorgenti e' organizzata in quattro package principali:

```text
qtServer/src/
├── data/
├── database/
├── mining/
└── server/
```

La suddivisione non e' puramente nominale: ogni package corrisponde a una responsabilita'
precisa e relativamente stabile.

### Visione sintetica

| Package    | Compito                                                  |
| ---------- | -------------------------------------------------------- |
| `data`     | Modellazione degli attributi e dei dataset               |
| `database` | Adattamento del modello relazionale al modello interno   |
| `mining`   | Algoritmo QT, cluster, cache delle distanze, persistenza |
| `server`   | Ascolto socket, gestione client e protocollo applicativo |

---

## Package e responsabilita'

### Package `data`

Il package `data` definisce il modello astratto dei dati su cui opera il clustering.
Qui vengono introdotte le nozioni di attributo, item, tupla e dataset.

### Classi principali

| Classe                                                | Ruolo                                          |
| ----------------------------------------------------- | ---------------------------------------------- |
| `Attribute`                                           | Classe base per gli attributi                  |
| `DiscreteAttribute`                                   | Attributi categorici                           |
| `ContinuousAttribute`                                 | Attributi numerici con normalizzazione min-max |
| `Item`                                                | Associazione tra attributo e valore            |
| `DiscreteItem`                                        | Item con distanza discreta                     |
| `ContinuousItem`                                      | Item con distanza numerica normalizzata        |
| `Tuple`                                               | Sequenza ordinata di item                      |
| `Data`                                                | Contenitore dell'intero dataset                |
| `EmptyDatasetException`, `InvalidDataFormatException` | Eccezioni specifiche del package               |

### Caricamento dei dati

La classe `Data` supporta tre vie di costruzione:

```java
Data d1 = new Data();                   // Dataset hardcoded PlayTennis
Data d2 = new Data("dataset.csv");      // Caricamento da CSV
Data d3 = new Data("playtennis", true); // Caricamento da database
```

Nel caso CSV il parser richiede:

- una riga di intestazione;
- un numero coerente di colonne per ogni riga;
- un formato semplice basato su virgole.

L'inferenza dei tipi viene eseguita automaticamente: una colonna viene trattata come
continua solo se i valori non mancanti sono numerici e sufficientemente vari; in caso
contrario resta discreta.

### Osservazione progettuale

Il package `data` e' pensato come fondamento comune sia del clustering sia delle forme
di persistenza. Per questa ragione le classi principali implementano o supportano la
serializzazione, rendendo possibile il salvataggio dei risultati in formato `.dmp`.

---

### Package `database`

Il package `database` traduce una tabella MySQL in una struttura compatibile con il
modello interno del package `data`.

### Classi principali

| Classe               | Ruolo                                        |
| -------------------- | -------------------------------------------- |
| `DbAccess`           | Gestione della connessione JDBC              |
| `TableSchema`        | Lettura dei metadati della tabella           |
| `TableData`          | Estrazione delle righe e dei valori distinti |
| `Example`            | Rappresentazione di una riga del database    |
| `QUERY_TYPE`         | Selezione del tipo di aggregazione           |
| `Eccezioni dedicate` | Gestione dei casi anomali in fase di accesso |

### Aspetti rilevanti

`DbAccess` merita attenzione per due motivi:

1. puo' essere costruito sia con parametri di default sia con parametri espliciti;
2. applica controlli sugli identificatori SQL tramite `requireSafeIdentifier()` e
   `quoteIdentifier()`, riducendo il rischio di nomi tabella o colonna non validi.

`TableSchema` ignora inoltre le colonne auto-increment, tipicamente usate come chiavi
tecniche, mentre `TableData` costruisce il dataset mediante `SELECT DISTINCT`. Questa
scelta fa si' che il modello interno venga alimentato solo da attributi descrittivi e
non da identificatori amministrativi.

### Parametri di connessione

In assenza di override, il costruttore di default di `DbAccess` assume:

- host `localhost`;
- porta `3306`;
- database `MapDB`;
- utente `MapUser`;
- password `map`.

I valori possono tuttavia essere sovrascritti tramite variabili d'ambiente o proprieta'
di sistema, rendendo il package piu' flessibile in contesti diversi.

---

### Package `mining`

Il package `mining` contiene l'algoritmo QT e le strutture direttamente coinvolte nella
costruzione, memorizzazione e serializzazione dei cluster.

### Classi principali

| Classe                       | Ruolo                                                  |
| ---------------------------- | ------------------------------------------------------ |
| `QTMiner`                    | Classe principale dell'algoritmo                       |
| `Cluster`                    | Singolo cluster con centroide e tuple associate        |
| `ClusterSet`                 | Insieme dei cluster prodotti                           |
| `DistanceCache`              | Cache sparsa delle distanze tra tuple                  |
| `SerializableClusteringData` | Contenitore serializzabile di cluster, dati e `radius` |
| `Eccezioni dedicate`         | Gestione di casi anomali nel processo di mining        |

### `QTMiner`

`QTMiner` costituisce il punto d'ingresso naturale all'algoritmo:

```java
QTMiner miner = new QTMiner(radius);
int numClusters = miner.compute(data);
ClusterSet result = miner.getC();
```

E' disponibile anche un costruttore che abilita esplicitamente le ottimizzazioni:

```java
QTMiner miner = new QTMiner(radius, true);
```

In questo caso viene attivata una `DistanceCache` che memorizza in modo sparso una parte
delle distanze gia' calcolate.

### Persistenza e compatibilita'

`QTMiner` supporta due forme di salvataggio:

- `save()`, mantenuto per retrocompatibilita' ma deprecato;
- `saveComplete()`, che salva `ClusterSet`, `Data` e `radius`.

Il costruttore `QTMiner(String fileName)` e' in grado di caricare sia il formato
completo sia il formato legacy. Questo rende il package piu' robusto nei confronti di
artefatti generati in versioni precedenti del progetto.

### Filtri di deserializzazione

Durante il caricamento da file `.dmp`, `QTMiner` applica anche un filtro sugli oggetti
deserializzati, limitando profondita', numero di riferimenti, lunghezza degli array e
insiemi di classi accettate. Per un progetto didattico si tratta di una precauzione
particolarmente apprezzabile, perche' mostra attenzione verso una serializzazione meno
naive del consueto.

---

### Package `server`

Il package `server` espone il backend via socket TCP e definisce il protocollo usato dal
client testuale.

### Classi principali

| Classe            | Ruolo                                                |
| ----------------- | ---------------------------------------------------- |
| `MultiServer`     | Server principale in ascolto sulla porta configurata |
| `ServerOneClient` | Thread dedicato a una singola connessione            |

### Modello di concorrenza

`MultiServer` adotta una strategia thread-per-client:

1. apre una `ServerSocket`;
2. attende una connessione;
3. crea un nuovo `ServerOneClient` per il socket accettato;
4. torna in attesa di nuove connessioni.

Ogni thread conserva il proprio stato di sessione, costituito almeno da:

- dataset corrente;
- ultimo `QTMiner` calcolato;
- nome della tabella caricata.

### Caratteristiche del protocollo

Il protocollo non usa `ObjectInputStream` e `ObjectOutputStream`. La comunicazione
avviene invece tramite:

- `DataInputStream`;
- `DataOutputStream`;
- stringhe UTF-8 trasmesse con lunghezza prefissata.

Questa soluzione rende il canale di rete piu' semplice e controllabile rispetto a una
serializzazione Java generalizzata.

---

## Interfacce e punti di accesso principali

### Entry point del modulo

Dal punto di vista operativo, il modulo espone due entry point principali:

1. `server.MultiServer`, per l'uso come server di rete;
2. `mining.QTMiner`, per l'uso come libreria locale.

### Esempi essenziali

Avvio del server:

```bash
java -cp qtServer/bin:qtServer/JDBC/mysql-connector-java-9.5.0.jar server.MultiServer 8080
```

Uso come libreria:

```java
Data data = new Data();
QTMiner miner = new QTMiner(0.5);
miner.compute(data);
ClusterSet clusterSet = miner.getC();
```

Uso con caricamento da file:

```java
QTMiner miner = new QTMiner("playtennis_20260318_120000");
ClusterSet clusterSet = miner.getC();
Data originalData = miner.getData();
```

---

## Protocollo server

### Comandi gestiti da `ServerOneClient`

| Codice | Operazione                            | Parametri attesi | Risposta                                        |
| ------ | ------------------------------------- | ---------------- | ----------------------------------------------- |
| `0`    | Caricamento tabella da database       | nome tabella     | `OK` oppure messaggio d'errore                  |
| `1`    | Clustering sulla tabella corrente     | `radius`         | `OK`, numero cluster, rappresentazione testuale |
| `2`    | Salvataggio del clustering corrente   | nessuno          | `OK` oppure messaggio d'errore                  |
| `3`    | Caricamento clustering da file `.dmp` | nome file        | `OK` oppure messaggio d'errore                  |

### Sequenza tipica

```text
Client                           ServerOneClient
  │                                    │
  ├── int: 0 + string: tableName ─────>│
  │<────────────── string: OK ─────────┤
  │                                    │
  ├── int: 1 + double: radius ────────>│
  │<──── string: OK + int + string ────┤
  │                                    │
  ├── int: 2 ─────────────────────────>│
  │<────────────── string: OK ─────────┤
```

### Gestione degli errori

Gli errori non vengono propagati come eccezioni serializzate sul canale di rete, ma
tradotti in messaggi stringa espliciti. Questa scelta semplifica il protocollo e riduce
l'accoppiamento tra client e server.

### Validazione degli input

Il package `server` include controlli sui nomi dei file `.dmp` e si appoggia alle
validazioni del package `database` per la parte JDBC. In questo modo il modulo combina:

- controllo sintattico degli input;
- validazione dei nomi di tabella e colonna;
- filtri di deserializzazione sui file caricati.

---

## Dipendenze

### Dipendenze interne

`qtServer` e' autonomo dal punto di vista della struttura del progetto: non richiede gli
altri moduli per compilarsi, ma costituisce esso stesso la base degli altri componenti.

### Dipendenze esterne

| Dipendenza        | Ruolo                                 |
| ----------------- | ------------------------------------- |
| JDK               | Compilazione ed esecuzione del modulo |
| MySQL Connector/J | Accesso JDBC alle tabelle relazionali |

Il driver JDBC e' atteso in `qtServer/JDBC/mysql-connector-java-9.5.0.jar`, come
indicato dal Makefile del modulo.

---

## Build, esecuzione e artefatti

### Compilazione

Dalla directory `qtServer/`:

```bash
make compile
```

Il target compila tutti i file Java presenti in `src/` e colloca i `.class` in
`qtServer/bin`.

### Creazione del JAR

```bash
make jar
```

Il file prodotto e' `qtServer.jar`, con `server.MultiServer` come main class.

### Avvio del server

```bash
make run PORT=8080
```

oppure:

```bash
make run-jar PORT=8080
```

### Artefatti rilevanti

| Artefatto                                      | Significato                      |
| ---------------------------------------------- | -------------------------------- |
| `qtServer/bin`                                 | Classi compilate del backend     |
| `qtServer.jar`                                 | JAR eseguibile del modulo        |
| `qtServer/JDBC/mysql-connector-java-9.5.0.jar` | Driver richiesto dai target Make |

---

## Relazioni con gli altri moduli

### `qtClient`

Il client testuale usa `qtServer` come processo remoto e dipende dal protocollo
implementato in `server.ServerOneClient`. La documentazione dei due moduli deve dunque
restare coerente soprattutto nella descrizione dei codici comando e delle strutture
trasmesse sul canale di rete.

### `qtGUI`

La GUI non si collega al server socket, ma integra i package del backend direttamente
nella propria build. Le classi piu' riutilizzate in questo contesto appartengono ai
package `data`, `database` e `mining`.

### `qtExt`

Il modulo di test e benchmark dipende da `qtServer` come libreria locale. In
particolare, i benchmark usano `QTMiner` e `DistanceCache`, mentre i test funzionali
riutilizzano `Data`, `ClusterSet` e le classi correlate.

---

## Indicazioni di manutenzione

### Principio di coesione dei package

Le modifiche future dovrebbero preservare la separazione corrente:

- `data` per il modello dei dati;
- `database` per l'adattamento relazionale;
- `mining` per l'algoritmo;
- `server` per l'esposizione remota.

L'introduzione di dipendenze circolari tra questi package renderebbe il modulo molto
meno leggibile e comprometterebbe la sua utilita' come backend condiviso.

### Evoluzione del protocollo di rete

Ogni cambiamento ai comandi del server richiede attenzione particolare, perche'
coinvolge `qtClient`. Se si aggiunge un nuovo comando o si modifica il formato di una
risposta, occorre aggiornare simultaneamente:

1. `ServerOneClient`;
2. il client testuale;
3. la documentazione tecnica dei due moduli.

### Cautela su JDBC e serializzazione

Due aree meritano controlli accurati in fase di manutenzione:

- il package `database`, perche' influenza direttamente la robustezza dell'import da DB;
- i metodi di caricamento e salvataggio in `QTMiner`, perche' coinvolgono oggetti
  serializzati e retrocompatibilita' dei file `.dmp`.

In particolare, eventuali revisioni future non dovrebbero eliminare senza adeguata sostituzione:

- la validazione degli identificatori SQL;
- i filtri di deserializzazione;
- la distinzione tra formato legacy e formato completo.

---

## Riferimenti

- [`../qtClient/README.md`](../qtClient/README.md)
- [`../qtGUI/README.md`](../qtGUI/README.md)
- [`../qtExt/README.md`](../qtExt/README.md)
- [`../docs/uml/qtServer/data/data_package.puml`](../docs/uml/qtServer/data/data_package.puml)
- [`../docs/uml/qtServer/database/database_package.puml`](../docs/uml/qtServer/database/database_package.puml)
- [`../docs/uml/qtServer/mining/mining_package.puml`](../docs/uml/qtServer/mining/mining_package.puml)
- [`../docs/uml/qtServer/server/server_package.puml`](../docs/uml/qtServer/server/server_package.puml)
- [`src/mining/QTMiner.java`](src/mining/QTMiner.java)
- [`src/database/DbAccess.java`](src/database/DbAccess.java)
- [`src/server/ServerOneClient.java`](src/server/ServerOneClient.java)

---
