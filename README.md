# MAP - Quality Threshold Clustering

Progetto universitario sviluppato nell'ambito del corso di Metodi Avanzati di Programmazione.
Il repository raccoglie una implementazione in Java dell'algoritmo di clustering Quality Threshold,
organizzata in moduli distinti per il backend computazionale, l'interfaccia grafica desktop,
il client testuale e gli strumenti di verifica sperimentale.

L'obiettivo del progetto non e' soltanto fornire una realizzazione dell'algoritmo QT,
ma mostrare come un medesimo nucleo applicativo possa essere riusato in contesti
diversi: esecuzione locale tramite GUI, accesso remoto tramite protocollo socket
e analisi tecnico-sperimentale mediante test e benchmark dedicati.

---

## Visione d'insieme

Il Quality Threshold Clustering e' un algoritmo di clustering non supervisionato che
costruisce i gruppi a partire da un vincolo di qualita', espresso tramite il parametro
`radius`, invece di richiedere in ingresso un numero prefissato di cluster.
Nel progetto tale algoritmo e' integrato in una architettura modulare che distingue chiaramente:

- il backend responsabile della modellazione dei dati e della computazione;
- l'interfaccia grafica per l'uso locale;
- il client testuale per la modalita' distribuita;
- il modulo di test e benchmark per la verifica del comportamento del sistema.

## Articolazione modulare

Il progetto e' organizzato in quattro moduli principali:

| Modulo     | Ruolo                                                                                                 | Tecnologie principali |
| ---------- | ----------------------------------------------------------------------------------------------------- | --------------------- |
| `qtServer` | backend dell'algoritmo QT, gestione dataset, JDBC, server socket multi-client                         | Java, JDBC, socket    |
| `qtGUI`    | applicazione desktop per eseguire clustering in locale, visualizzare risultati ed esportare artefatti | JavaFX, Maven, XChart |
| `qtClient` | client testuale che dialoga con `qtServer` tramite TCP                                                | Java                  |
| `qtExt`    | test, benchmark e utility di supporto                                                                 | Java                  |

### Osservazione architetturale

Il modulo `qtServer` e' il centro logico del progetto. `qtClient` lo usa come backend
remoto, mentre `qtGUI` e `qtExt` ne riutilizzano le classi direttamente all'interno del
medesimo processo. Questa distinzione e' importante anche sul piano operativo: la GUI
non richiede l'avvio del server socket, mentre la coppia `qtClient`/`qtServer` definisce
la modalita' client-server vera e propria.

## Requisiti e dipendenze

Per compilare ed eseguire il progetto in modo coerente si assumono i seguenti
prerequisiti:

- `JDK 21`, adottato come riferimento uniforme per l'intero repository;
- `make`, usato per orchestrare build, esecuzione, test e generazione della documentazione;
- `Maven`, richiesto dal modulo `qtGUI`;
- `MySQL`, necessario solo negli scenari in cui si desidera caricare dati da database.

### Note tecniche

- `qtGUI` include i sorgenti di `qtServer` nel proprio classpath di build tramite Maven, quindi esegue il clustering in locale.
- `qtServer` e `qtExt` fanno riferimento al driver JDBC locale `qtServer/JDBC/mysql-connector-java-9.5.0.jar`.
- L'accesso al database non e' obbligatorio per tutti gli scenari: il progetto puo' essere usato anche con dataset hardcoded, CSV e file `.dmp`.

## Build del progetto

Il `Makefile` posto nella radice del repository funge da punto di accesso unificato ai principali flussi di lavoro.

### Compilazione completa

```bash
make all
```

Questo target delega la compilazione ai Makefile dei singoli moduli e costruisce:

- `qtClient`
- `qtServer`
- `qtExt`
- `qtGUI`

### Compilazione per modulo

```bash
make client
make server
make ext
make gui
```

### Creazione dei JAR

```bash
make jar
```

Il comando produce i JAR dei moduli che supportano il packaging diretto:

- `qtClient/qtClient.jar`
- `qtServer/qtServer.jar`
- `qtGUI/target/qtGUI-1.0.0.jar`

### Esecuzione dei test

```bash
make test
```

Il target invoca il modulo `qtExt` e avvia la suite di verifica disponibile nel repository.

## Esecuzione dei principali scenari

### Uso locale tramite GUI

```bash
make run-gui
```

Questo e' lo scenario piu' diretto per l'utente. La GUI carica i package del backend all'interno della propria build e puo'
quindi eseguire il clustering senza avviare preventivamente il server socket.

### Uso remoto tramite client e server

Avvio del server:

```bash
make run-server PORT=8080
```

Avvio del client:

```bash
make run-client IP=localhost PORT=8080
```

In questo caso `qtServer` mantiene lo stato di sessione per ogni connessione e `qtClient` si comporta come front-end testuale
per il caricamento dei dati, l'esecuzione del clustering e il salvataggio dei risultati.

### Esecuzione da JAR

Per i moduli che lo supportano sono disponibili anche target dedicati:

```bash
make run-server-jar PORT=8080
make run-client-jar IP=localhost PORT=8080
make run-gui-jar
```

## Sorgenti dati e persistenza

Il progetto supporta piu' sorgenti dati, con accessi differenti a seconda del modulo utilizzato.

### Sorgenti supportate

- dataset hardcoded `PlayTennis`;
- dataset `Iris` incluso nelle risorse della GUI;
- file CSV esterni;
- tabelle MySQL;
- file serializzati `.dmp` per la riapertura di clustering gia' eseguiti.

### Script e artefatti di supporto

Nella radice del repository sono presenti anche:

- `setup_database.sql`, utile per predisporre tabelle di esempio nel database `MapDB`;
- `import_csv.sh`, script di supporto per l'importazione di dataset CSV.

L'uso dei file `.dmp` merita un'attenzione particolare: essi non rappresentano soltanto una forma di export,
ma la persistenza nativa del clustering, comprensiva, nel formato piu' recente, dell'insieme dei cluster, del dataset e del `radius`.

## Struttura del repository

La struttura del repository puo' essere riassunta nel seguente modo:

```text
MAP/
├── Makefile
├── README.md
├── data/                      # Dataset di esempio, sintetici e di benchmark
├── docs/
│   ├── manuale_utente/        # Manuale utente LaTeX modulare
│   │   ├── main.tex
│   │   ├── preamble.tex
│   │   ├── chapters/
│   │   └── gui_images/
│   └── uml/                   # Diagrammi UML del progetto
├── make/                      # Configurazione comune per i Makefile
├── qtClient/                  # Client testuale
├── qtExt/                     # Test, benchmark e utility
├── qtGUI/                     # Applicazione JavaFX
├── qtServer/                  # Backend e server socket
├── import_csv.sh
└── setup_database.sql
```

La presenza di `bin/` e `target/` in alcuni moduli dipende dalle operazioni di build e non modifica il fatto che la struttura
concettuale del progetto resti centrata sui quattro moduli principali e sulla documentazione a supporto.

## Documentazione disponibile

Il repository include piu' livelli di documentazione tecnica:

- README principale del progetto, che fornisce una visione d'insieme;
- README specifici di modulo:
  - `qtServer/README.md`
  - `qtGUI/README.md`
  - `qtClient/README.md`
  - `qtExt/README.md`
- manuale utente in LaTeX sotto `docs/manuale_utente/`;
- diagrammi UML sotto `docs/uml/`.

Il manuale utente e' organizzato in forma modulare: `main.tex` costituisce il punto di ingresso, `preamble.tex` raccoglie
il preambolo comune e la directory `chapters/` contiene i capitoli del documento.

## Comandi utili

Oltre ai target principali gia' citati, il repository espone anche:

```bash
make help
make javadoc
make uml
make clean
make validate
```

Questi comandi consentono rispettivamente di:

- consultare l'elenco dei target disponibili;
- generare la JavaDoc;
- produrre i diagrammi UML;
- pulire gli artefatti di build;
- verificare la struttura del progetto.

---
