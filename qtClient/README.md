# qtClient - Client testuale per Quality Threshold Clustering

> **Modulo**: Interfaccia testuale per l'accesso remoto al backend QT
> **Versione**: 1.0
> **Autore**: Lombardi Costantino

---

## Indice

1. [Descrizione generale](#descrizione-generale)
2. [Collocazione architetturale](#collocazione-architetturale)
3. [Struttura interna](#struttura-interna)
4. [Classi e responsabilita'](#classi-e-responsabilita)
5. [Protocollo di comunicazione](#protocollo-di-comunicazione)
6. [Dipendenze](#dipendenze)
7. [Build ed esecuzione](#build-ed-esecuzione)
8. [Flussi d'uso](#flussi-duso)
9. [Indicazioni di manutenzione](#indicazioni-di-manutenzione)
10. [Riferimenti](#riferimenti)

---

## Descrizione generale

### Finalita' del modulo

Il modulo `qtClient` fornisce una interfaccia a riga di comando per l'uso remoto del
sistema di clustering Quality Threshold. Dal punto di vista funzionale non implementa
l'algoritmo di clustering, ma agisce come strato di interazione con il server
`qtServer`, al quale delega il caricamento dei dati, l'esecuzione del clustering e la
persistenza dei risultati. Il suo ruolo e' quindi quello di rendere accessibile il
backend attraverso un canale testuale semplice, esplicito e facilmente ispezionabile.

Questa scelta progettuale rende `qtClient` particolarmente utile in tre scenari. Il
primo e' quello didattico, nel quale interessa osservare in modo trasparente la
sequenza dei comandi scambiati con il server. Il secondo e' quello di test operativo,
in cui si desidera verificare il comportamento del backend senza introdurre la
complessita' della GUI. Il terzo e' quello di utilizzo remoto minimale, quando e'
sufficiente una sessione testuale per caricare dati da database, eseguire clustering e
salvare un file `.dmp`.

### Funzioni principali

| Funzione                  | Descrizione                                                                        |
| ------------------------- | ---------------------------------------------------------------------------------- |
| Connessione remota        | Apertura di una connessione TCP verso `qtServer` specificando IP e porta           |
| Interazione guidata       | Presentazione di un menu numerico con operazioni coerenti con il protocollo server |
| Validazione input         | Lettura robusta dei parametri utente tramite `keyboardinput.Keyboard`              |
| Gestione errori           | Ricezione di messaggi d'errore dal server e loro presentazione all'utente          |
| Visualizzazione risultati | Stampa testuale del clustering restituito dal backend                              |

### Limiti intenzionali

Il modulo e' volutamente essenziale. Non gestisce grafici, non esegue clustering in
locale, non mantiene copie persistenti dei dataset e non implementa un proprio livello
di business logic. Il suo obiettivo non e' sostituire la GUI, ma offrire un accesso
diretto e lineare ai servizi esposti dal server.

---

## Collocazione architetturale

`qtClient` occupa la posizione di front-end testuale nella variante client-server del
progetto:

```text
┌────────────┐          TCP/IP           ┌────────────┐
│  qtClient  │◄─────────────────────────►│  qtServer  │
│   (CLI)    │                           │  (Backend) │
└─────┬──────┘                           └─────┬──────┘
      │                                        │
      │ usa                                    │
      ▼                                        ▼
┌────────────────────┐                 ┌────────────────────┐
│ keyboardinput      │                 │  data / mining /   │
│ Validazione input  │                 │ database / server  │
└────────────────────┘                 └────────────────────┘
```

Dal punto di vista architetturale il modulo puo' essere definito come un client
"leggero": la logica locale riguarda soprattutto l'acquisizione dell'input, la
serializzazione dei parametri e la stampa delle risposte. Lo stato applicativo
rilevante, invece, risiede sul server, che conserva il dataset corrente e l'ultimo
clustering associati alla singola sessione di connessione.

---

## Struttura interna

Il modulo contiene tre classi distribuite in due aree logiche:

```text
qtClient/src/
├── MainTest.java
├── ServerException.java
└── keyboardinput/
    └── Keyboard.java
```

La scelta di una struttura volutamente compatta riflette la natura del modulo: `qtClient`
non e' un sottosistema articolato in molti package, ma un punto di accesso operativo al
protocollo di `qtServer`.

### Organizzazione logica

| Elemento                 | Ruolo                                                                   |
| ------------------------ | ----------------------------------------------------------------------- |
| `MainTest`               | Entry point del modulo, gestione della connessione e del menu operativo |
| `ServerException`        | Eccezione applicativa per errori restituiti dal server                  |
| `keyboardinput.Keyboard` | Utility di lettura robusta da tastiera                                  |

---

## Classi e responsabilita'

### `MainTest`

`MainTest` e' la classe centrale del modulo. Il costruttore apre una connessione verso
il server indicato dall'utente e inizializza due stream binari:

- `DataOutputStream`, usato per trasmettere comandi e parametri;
- `DataInputStream`, usato per ricevere codici numerici e stringhe dal server.

La classe definisce inoltre una costante `MAX_MESSAGE_BYTES` che limita la dimensione
dei messaggi stringa scambiati sulla connessione. Questa scelta introduce un vincolo
semplice ma utile a evitare letture o scritture anomale su input manifestamente non
validi.

I metodi operativi principali sono i seguenti:

| Metodo                           | Significato                                                               |
| -------------------------------- | ------------------------------------------------------------------------- |
| `menu()`                         | Presenta il menu principale e restituisce una scelta valida tra `0` e `4` |
| `storeTableFromDb()`             | Richiede al server il caricamento di una tabella MySQL                    |
| `learningFromDbTable()`          | Avvia il clustering sul dataset gia' caricato lato server                 |
| `storeClusterInFile()`           | Chiede il salvataggio del clustering corrente in formato `.dmp`           |
| `learningFromFile()`             | Richiede il caricamento di un clustering serializzato                     |
| `writeString()` / `readString()` | Implementano il trasporto di stringhe UTF-8 prefissate da lunghezza       |

Il ciclo principale del `main()` non esegue computazione locale: si limita a
interpretare la scelta dell'utente, inviare il comando corrispondente e stampare
l'esito della richiesta.

### `ServerException`

`ServerException` ha una funzione volutamente circoscritta: separare gli errori di
comunicazione dagli errori applicativi. Quando il server risponde con un messaggio
diverso da `OK`, il client lo trasforma in una eccezione di dominio, distinguendolo
dagli `IOException` o dagli errori di parsing locale.

### `keyboardinput.Keyboard`

La classe `Keyboard` fornisce un piccolo livello di astrazione sull'input da tastiera.
Nel contesto di `qtClient` il suo valore non e' tanto architetturale quanto pratico:
riduce la fragilita' della lettura interattiva e centralizza la validazione dei tipi
numerici e testuali richiesti dal menu.

---

## Protocollo di comunicazione

### Principi generali

Il protocollo tra `qtClient` e `qtServer` e' basato su stream binari e non su object
stream. I comandi vengono trasmessi come interi, i valori numerici come `double` o
`int`, mentre le stringhe vengono inviate come sequenze UTF-8 precedute dalla loro
lunghezza in byte. La coppia `writeString()` / `readString()` implementa esplicitamente
questa convenzione su entrambi i lati della comunicazione.

Questa soluzione riduce la complessita' del protocollo, evita di esporre direttamente
la serializzazione Java sul canale di rete e rende il tracciato di comunicazione piu'
facile da ricostruire dal codice.

### Comandi supportati

| Codice | Operazione                                  | Parametri inviati                   | Risposta attesa                                 |
| ------ | ------------------------------------------- | ----------------------------------- | ----------------------------------------------- |
| `0`    | Caricamento tabella da database             | nome tabella                        | `OK` oppure messaggio d'errore                  |
| `1`    | Esecuzione clustering sul dataset corrente  | `radius`                            | `OK`, numero cluster, rappresentazione testuale |
| `2`    | Salvataggio del clustering corrente         | nessuno                             | `OK` oppure messaggio d'errore                  |
| `3`    | Caricamento di un clustering da file `.dmp` | nome file                           | `OK` oppure messaggio d'errore                  |
| `4`    | Uscita dal client                           | nessun messaggio al server dedicato | chiusura locale della sessione                  |

### Osservazioni sullo stato di sessione

Benche' `qtClient` resti leggero, il flusso complessivo non e' stateless. Una volta
aperta la connessione, il server associa alla sessione almeno tre informazioni
operative: il dataset corrente, l'ultimo `QTMiner` elaborato e il nome dell'eventuale
tabella caricata. Di conseguenza:

- il comando `1` presuppone normalmente l'esecuzione preventiva del comando `0`;
- il comando `2` richiede che sia stato eseguito almeno un clustering;
- il comando `3` puo' reintrodurre nel contesto di sessione un clustering salvato.

### Sequenza tipica

```text
Client                          Server
  │                               │
  ├── connessione TCP ───────────>│
  │                               │
  ├── comando 0 + tableName ─────>│
  │<────────────── "OK" ──────────┤
  │                               │
  ├── comando 1 + radius ────────>│
  │<──── "OK" + nCluster + testo ─┤
  │                               │
  ├── comando 2 ─────────────────>│
  │<────────────── "OK" ──────────┤
  │                               │
  └── chiusura client             │
```

---

## Dipendenze

### Dipendenze interne

Il modulo non dipende da altri package del progetto in fase di compilazione, ma dipende
operativamente dal server per poter essere utilizzato in modo significativo.

### Dipendenze esterne

| Dipendenza               | Ruolo                                 | Obbligatoria            |
| ------------------------ | ------------------------------------- | ----------------------- |
| JDK                      | Compilazione ed esecuzione del client | Si                      |
| `qtServer` in esecuzione | Backend remoto raggiungibile via TCP  | Si, per l'uso operativo |

Il client non usa librerie di terze parti: tutta la comunicazione si basa sulle API
standard di Java (`java.net`, `java.io`, `java.nio.charset`).

---

## Build ed esecuzione

### Compilazione con Makefile di modulo

Dalla directory `qtClient/`:

```bash
make compile
```

Il target produce i `.class` in `qtClient/bin` e prepara il modulo all'esecuzione.

### Creazione del JAR

```bash
make jar
```

Il JAR generato e' `qtClient.jar` e usa `MainTest` come entry point.

### Avvio del client

```bash
make run IP=localhost PORT=8080
```

oppure, dopo la creazione del JAR:

```bash
make run-jar IP=localhost PORT=8080
```

Il modulo richiede sempre due parametri logici: indirizzo del server e porta. Se il
server non e' raggiungibile, il programma termina immediatamente con un messaggio di
errore di connessione.

---

## Flussi d'uso

### Scenario 1: clustering da tabella database

Questo e' il flusso piu' tipico:

1. Avvio di `qtServer`.
2. Avvio di `qtClient` specificando IP e porta.
3. Selezione del comando `0` e inserimento del nome tabella.
4. Selezione del comando `1` e inserimento del `radius`.
5. Lettura del clustering restituito in forma testuale.
6. Eventuale comando `2` per il salvataggio lato server.

### Scenario 2: riapertura di clustering salvato

Nel secondo scenario il database puo' anche non essere necessario:

1. Avvio del server.
2. Avvio del client.
3. Selezione del comando `3`.
4. Inserimento del nome del file `.dmp` senza estensione.
5. Visualizzazione del clustering deserializzato.

Questo flusso e' utile quando si intende consultare o riutilizzare risultati gia'
prodotti senza ripetere il caricamento della tabella e l'esecuzione dell'algoritmo.

### Gestione degli errori piu' comuni

| Situazione                   | Effetto lato client                                           |
| ---------------------------- | ------------------------------------------------------------- |
| IP o porta errati            | Errore in fase di connessione                                 |
| Tabella inesistente          | Messaggio d'errore ritrasmesso dal server                     |
| `radius` non valido          | Richiesta ripetuta oppure errore server, a seconda della fase |
| File `.dmp` assente          | Messaggio di file non trovato                                 |
| Chiusura inattesa del server | Errore I/O o EOF durante la comunicazione                     |

---

## Indicazioni di manutenzione

### Estensione del protocollo

L'aggiunta di un nuovo comando richiede un aggiornamento simmetrico di client e server.
In particolare:

1. va definito un nuovo codice intero nello `switch` del client;
2. va introdotto un ramo corrispondente nello `switch` di `ServerOneClient`;
3. vanno mantenute coerenti serializzazione e deserializzazione dei parametri;
4. va preservata la convenzione `OK` / messaggio d'errore per la risposta.

Una modifica unilaterale del solo client o del solo server introdurrebbe desincronizzazioni
nel protocollo, con errori di lettura difficili da diagnosticare.

### Conservazione delle invarianti del protocollo

Le due utility `writeString()` e `readString()` rappresentano un punto delicato del
modulo. Se si interviene su di esse, e' necessario mantenere:

- la codifica UTF-8;
- il prefisso con lunghezza in byte;
- il controllo sulla dimensione massima dei messaggi;
- il controllo sulla completezza dei byte letti.

### Evoluzioni possibili

Le evoluzioni piu' naturali del modulo non riguardano l'introduzione di complessita'
grafica, ma il miglioramento dell'esperienza testuale: banner piu' informativi,
tracciamento opzionale dei comandi, logging locale e distinzione piu' granulare tra
errori di rete ed errori applicativi. Anche in questi casi, tuttavia, il principio da
preservare resta quello della sobrieta': `qtClient` e' utile proprio perche' espone il
backend in modo diretto e leggibile.

---

## Riferimenti

- [`../qtServer/README.md`](../qtServer/README.md): documentazione del backend remoto
- [`../docs/uml/qtClient/keyboardinput/keyboardinput_package.puml`](../docs/uml/qtClient/keyboardinput/keyboardinput_package.puml)
- [`../docs/uml/workflows/client_server_communication_sequence.puml`](../docs/uml/workflows/client_server_communication_sequence.puml)
- [`src/MainTest.java`](src/MainTest.java)
- [`src/keyboardinput/Keyboard.java`](src/keyboardinput/Keyboard.java)

---
