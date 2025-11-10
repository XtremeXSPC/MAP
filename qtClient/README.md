# qtClient - Quality Threshold Clustering Client

> **Modulo**: Client CLI per Clustering Remoto
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
8. [Utilizzo](#utilizzo)
9. [Note di Manutenzione](#note-di-manutenzione)

---

## Descrizione Generale

### Scopo del Modulo

Il modulo **qtClient** è un'applicazione client CLI (Command-Line Interface) per il clustering remoto tramite connessione Socket al server `qtServer`. Permette di:

- Connettersi al server QT su rete TCP/IP
- Caricare dataset da file o database remoto
- Eseguire clustering Quality Threshold
- Ricevere e visualizzare risultati
- Salvare cluster su file server-side

### Funzionalità Principali

| Funzionalità           | Descrizione                                      |
| ---------------------- | ------------------------------------------------ |
| **Connessione remota** | Socket TCP/IP al server qtServer                 |
| **Input robusto**      | Package `keyboardinput.Keyboard` per validazione |
| **Menu interattivo**   | Interfaccia testuale guidata                     |
| **Gestione errori**    | Eccezioni server catturate e mostrate all'utente |

### Posizione nell'Architettura Generale

```
┌────────────┐          Socket          ┌────────────┐
│  qtClient  │◄─────────────────────────►│  qtServer  │
│   (CLI)    │   TCP/IP (port 8080)     │  (Core)    │
└────────────┘                           └────────────┘
      │
      │ usa
      ▼
┌────────────────┐
│ keyboardinput/ │
│  Keyboard.java │
└────────────────┘
```

**Caratteristiche architetturali**:

- **Thin client**: Logica minima, delega computazione al server
- **Stateless**: Nessuna persistenza locale (dati gestiti da server)
- **Interattivo**: Input guidato con validazione robusta

---

## Architettura Interna

### Struttura Package

Il modulo è composto da **2 package**:

```
qtClient/src/
├── (default package)     # Entry point e comunicazione
│   ├── MainTest.java
│   └── ServerException.java
│
└── keyboardinput/        # Input utente robusto
    └── Keyboard.java
```

### Pattern di Design Utilizzati

#### 1. Client-Server Pattern

**Classe**: `MainTest`

- **Client**: Invia richieste al server tramite `ObjectOutputStream`
- **Server**: qtServer risponde tramite `ObjectInputStream`
- **Protocollo**: Serializzazione oggetti Java

#### 2. Exception Handling Pattern

**Classe**: `ServerException`

- Wrapping di errori server-side
- Propagazione messaggi di errore al client
- Gestione graceful di fallimenti di rete

#### 3. Facade Pattern

**Classe**: `Keyboard`

- Astrae dettagli parsing e conversioni input
- Gestione automatica eccezioni di formato
- API semplificata per lettura dati da tastiera

### Diagramma Organizzazione

Riferimento ai diagrammi UML dettagliati:

- [`docs/uml/qtClient/keyboardinput/keyboardinput_package.puml`](../docs/uml/qtClient/keyboardinput/keyboardinput_package.puml)
- [`docs/uml/workflows/client_server_communication_sequence.puml`](../docs/uml/workflows/client_server_communication_sequence.puml)

---

## Package

### Package `(default)` - Comunicazione Client-Server

**Classi principali**:

| Classe            | Tipo      | Responsabilità                                    |
| ----------------- | --------- | ------------------------------------------------- |
| `MainTest`        | Concrete  | Entry point client, gestione comunicazione Socket |
| `ServerException` | Exception | Eccezione per errori server-side                  |

#### Classe `MainTest`

**Scopo**: Gestire connessione Socket e interazione utente.

**Interfacce pubbliche**:

```java
public MainTest(String ip, int port) throws IOException
```

Costruttore che stabilisce connessione al server.

**Metodi principali**:

```java
private int menu()                      // Menu scelta operazione
private void storeTableFromDb()         // Comando 0: Carica dati da DB
private String learningFromDbTable()    // Comando 1: Esegui clustering
private void storeClusterInFile()       // Comando 2: Salva cluster
private String learningFromFile()       // Comando 3: Carica e cluster
```

**Protocollo di comunicazione**:

| Comando             | Codice | Parametri                           | Risposta Server                    |
| ------------------- | ------ | ----------------------------------- | ---------------------------------- |
| **Load from DB**    | 0      | `String tableName`                  | "OK" / "ERROR: <msg>"              |
| **Learn from DB**   | 1      | `double radius`                     | "OK" + numClusters + clusterString |
| **Save clusters**   | 2      | nessuno                             | "OK" / "ERROR: <msg>"              |
| **Learn from file** | 3      | `String tableName`, `double radius` | "OK" + clusterString               |

**Flusso tipico**:

```
1. Connessione al server (costruttore)
2. Menu scelta operazione (1 o 2)
3. Se opzione 1: learningFromFile()
   - Invia comando 3
   - Specifica nome tabella
   - Specifica radius
   - Riceve risultati clustering
4. Se opzione 2: storeTableFromDb() + learningFromDbTable()
   - Invia comando 0 (carica dati)
   - Invia comando 1 (esegui clustering)
   - Riceve risultati
5. Opzionale: storeClusterInFile()
   - Invia comando 2 (salva risultati)
```

#### Classe `ServerException`

**Scopo**: Rappresentare errori provenienti dal server.

```java
public class ServerException extends Exception {
    public ServerException(String message) {
        super(message);
    }
}
```

**Utilizzo**:

```java
String result = (String) in.readObject();
if (!result.equals("OK")) {
    throw new ServerException(result);
}
```

---

### Package `keyboardinput` - Input Robusto

**Classe**: `Keyboard`

**Scopo**: Fornire metodi robusti per lettura input da tastiera con gestione automatica errori.

**Metodi pubblici**:

```java
public static int readInt()           // Leggi intero
public static double readDouble()     // Leggi double
public static String readString()     // Leggi riga completa
public static String readWord()       // Leggi singola parola
public static char readChar()         // Leggi carattere
public static boolean readBoolean()   // Leggi booleano

// Gestione errori
public static int getErrorCount()
public static void resetErrorCount()
public static boolean getPrintErrors()
public static void setPrintErrors(boolean flag)
```

**Caratteristiche**:

- **Validazione automatica**: Riprova input se formato errato
- **Parsing robusto**: Gestione `NumberFormatException`, `IOException`, etc.
- **Contatore errori**: Tracking errori per debugging
- **Logging configurabile**: `setPrintErrors(true/false)`

**Esempio utilizzo**:

```java
import keyboardinput.Keyboard;

// Lettura intero con validazione automatica
System.out.print("Scegli opzione (1/2): ");
int choice = Keyboard.readInt();  // Riprova se input non è intero

// Lettura double con controllo range
double radius;
do {
    System.out.print("Inserisci radius (> 0): ");
    radius = Keyboard.readDouble();
} while (radius <= 0);

// Lettura stringa
System.out.print("Nome tabella: ");
String tableName = Keyboard.readString();
```

**Vantaggi rispetto a `Scanner`**:

- No crash su input malformato
- Retry automatico
- Gestione EOF robusta
- Contatore errori per QA

**Riferimenti**: Package derivato da Lewis & Loftus "Java Software Solutions".

**Diagramma UML**: `docs/uml/qtClient/keyboardinput/keyboardinput_package.puml`

---

## Dipendenze

### Dipendenze Interne

Il modulo qtClient **non dipende** da altri moduli del progetto a compile-time.

**Dipendenza runtime**:

- **qtServer** deve essere in esecuzione sulla porta specificata

### Dipendenze Esterne

| Libreria | Versione | Scopo        | Obbligatoria |
| -------- | -------- | ------------ | ------------ |
| **JDK**  | 8+       | Runtime Java | SI           |

**Nota**: Nessuna libreria esterna richiesta.

---

## Interfacce Pubbliche (API)

### Entry Point

**Classe**: `MainTest`

**Esecuzione**:

```bash
# Da file compilati
java -cp qtClient/bin MainTest <ip> <port>

# Esempio: Connessione a localhost:8080
java -cp qtClient/bin MainTest localhost 8080

# Da JAR
java -jar qtClient.jar <ip> <port>
```

**Parametri linea di comando**:

| Parametro | Tipo   | Descrizione         | Esempio                      |
| --------- | ------ | ------------------- | ---------------------------- |
| `ip`      | String | Indirizzo IP server | `localhost`, `192.168.1.100` |
| `port`    | int    | Porta server        | `8080`, `9999`               |

**Sessione tipica**:

```
$ java -cp qtClient/bin MainTest localhost 8080

addr = localhost/127.0.0.1
Socket[addr=localhost/127.0.0.1,port=8080,localport=54321]

(1) Load clusters from file
(2) Load data from db
(1/2): 2

Table name: playtennis
OK

Radius: 0.5

Number of Clusters: 5

Cluster 1:
Centroid=(sunny hot high weak no)
Examples: 3
AvgDistance=0.133

Cluster 2:
Centroid=(overcast hot high weak yes)
Examples: 4
AvgDistance=0.0

...
```

---

## Interazioni con Altri Moduli

### qtClient → qtServer

**Tipo**: Comunicazione Socket TCP/IP

**Diagramma di Sequenza**:

```
MainTest (Client)              MultiServer (Server)
     │                              │
     ├── new Socket(ip, port) ─────>│ accept()
     │                              │
     │                         [Thread creato]
     │                              │
     ├── writeObject(command) ──────>│ readObject()
     ├── writeObject(params) ───────>│ processCommand()
     │                              │
     │                              ├─> QTMiner.compute()
     │                              │
     │<─── writeObject("OK") ────────┤
     │<─── writeObject(results) ─────┤
     │                              │
     └── Visualizza risultati       │
```

**Protocollo dettagliato**:

Vedere [`docs/uml/workflows/client_server_communication_sequence.puml`](../docs/uml/workflows/client_server_communication_sequence.puml)

**Gestione errori di rete**:

```java
try {
    MainTest client = new MainTest(ip, port);
    // ... interazione ...
} catch (IOException e) {
    System.err.println("Errore connessione server: " + e.getMessage());
    System.err.println("Verificare che il server sia avviato su " + ip + ":" + port);
}
```

**Timeout**: Attualmente non configurato (blocking I/O). Per aggiungere timeout:

```java
socket.setSoTimeout(30000);  // 30 secondi
```

---

## Build e Compilazione

### Compilazione Manuale

#### Opzione 1: javac diretto

```bash
# Da directory MAP/
cd qtClient

# Compila tutti i file
javac -d bin src/**/*.java

# Verifica classi generate
ls -R bin/
```

**Output atteso**:

```
bin/
├── MainTest.class
├── ServerException.class
└── keyboardinput/
    └── Keyboard.class
```

#### Opzione 2: Makefile (Raccomandato)

```bash
# Da directory MAP/
make client         # Compila solo qtClient
make all            # Compila client + server
make rebuild        # Pulisce e ricompila
```

**Riferimenti**: Vedere [`docs/MAKEFILE_GUIDE.md`](../docs/MAKEFILE_GUIDE.md)

### Creazione JAR

```bash
# Crea JAR eseguibile per client
make client-jar

# Output: qtClient.jar nella root del progetto
ls -lh qtClient.jar
```

**Esecuzione JAR**:

```bash
java -jar qtClient.jar localhost 8080
```

---

## Utilizzo

### Scenario 1: Clustering da Database

**Prerequisiti**:

- Server qtServer avviato e connesso a database MySQL
- Tabella dati presente nel database (es. `playtennis`)

**Passi**:

```bash
# 1. Avvia server (terminale separato)
java -jar qtServer.jar 8080

# 2. Avvia client
java -jar qtClient.jar localhost 8080

# 3. Menu client
(1) Load clusters from file
(2) Load data from db
(1/2): 2

# 4. Specifica nome tabella
Table name: playtennis

# 5. Specifica radius
Radius: 0.5

# 6. Ricevi risultati
Number of Clusters: 5
[Visualizzazione cluster...]
```

### Scenario 2: Clustering da File (opzione 1)

**Prerequisiti**:

- Server qtServer avviato
- File dataset presente sul server (es. `playtennis.csv`)

**Passi**:

```bash
# 1. Avvia client
java -jar qtClient.jar localhost 8080

# 2. Menu client
(1) Load clusters from file
(2) Load data from db
(1/2): 1

# 3. Specifica nome file (senza estensione)
Table Name: playtennis

# 4. Specifica radius
Radius: 0.3

# 5. Ricevi risultati
[Visualizzazione cluster...]
```

### Scenario 3: Connessione Remota

**Server su macchina remota**:

```bash
# Server su IP 192.168.1.100 porta 9999
# (su macchina server)
java -jar qtServer.jar 9999

# Client locale
# (su macchina client)
java -jar qtClient.jar 192.168.1.100 9999
```

**Troubleshooting connessioni remote**:

1. **Verifica firewall**: Porta deve essere aperta
2. **Verifica server in ascolto**: `netstat -an | grep 9999`
3. **Test connettività**: `telnet 192.168.1.100 9999`

### Gestione Errori Comuni

#### Errore: "Connection refused"

**Causa**: Server non in esecuzione o porta errata.

**Soluzione**:

```bash
# Verifica server avviato
ps aux | grep MultiServer

# Avvia server se necessario
java -jar qtServer.jar 8080
```

#### Errore: "ERROR: Table not found"

**Causa**: Nome tabella errato o database non connesso.

**Soluzione**:

```bash
# Verifica tabella esiste
mysql -u MapUser -p
> USE MapDB;
> SHOW TABLES;
> SELECT * FROM playtennis LIMIT 5;
```

#### Errore: "NumberFormatException"

**Causa**: Input non numerico dove atteso numero.

**Soluzione**: Classe `Keyboard` gestisce automaticamente, richiede input valido.

---

## Note di Manutenzione

### Considerazioni per Modifiche Future

#### 1. Aggiunta Nuovi Comandi

Per aggiungere nuovo comando al protocollo:

**Client-side** (`MainTest.java`):

```java
// Aggiungi nuovo metodo
private void newCommand() throws IOException, ClassNotFoundException {
    out.writeObject(4);  // Nuovo codice comando

    // Invia parametri
    out.writeObject(param1);
    out.writeObject(param2);

    // Ricevi risposta
    String result = (String) in.readObject();
    if (!result.equals("OK")) {
        throw new ServerException(result);
    }
}

// Aggiungi al menu
private int menu() {
    System.out.println("(1) Load clusters from file");
    System.out.println("(2) Load data from db");
    System.out.println("(3) New command");  // <-- Nuova opzione
    // ...
}
```

**Server-side** (`ServerOneClient.java`):

```java
// Aggiungi case nello switch
case 4:  // Nuovo comando
    // Ricevi parametri
    Object param1 = in.readObject();
    Object param2 = in.readObject();

    // Esegui logica
    processNewCommand(param1, param2);

    // Rispondi
    out.writeObject("OK");
    break;
```

#### 2. Miglioramenti UI/UX

**Menu più descrittivo**:

```java
private void printBanner() {
    System.out.println("╔════════════════════════════════════╗");
    System.out.println("║  QT Clustering Client v1.0        ║");
    System.out.println("║  Connesso a: " + serverAddress    ║");
    System.out.println("╚════════════════════════════════════╝");
}
```

**Progress indicators**:

```java
System.out.print("Clustering in corso");
for (int i = 0; i < 3; i++) {
    Thread.sleep(500);
    System.out.print(".");
}
System.out.println(" Completato!");
```

#### 3. Logging e Debugging

**Aggiungere logging dettagliato**:

```java
import java.util.logging.*;

private static final Logger LOGGER = Logger.getLogger(MainTest.class.getName());

// In metodi critici
LOGGER.info("Connessione a " + ip + ":" + port);
LOGGER.fine("Comando inviato: " + command);
LOGGER.severe("Errore comunicazione: " + e.getMessage());
```

#### 4. Gestione Connessioni Persistent

**Attualmente**: Una connessione per sessione.

**Miglioramento**: Connection pooling per riutilizzo.

```java
public class ConnectionPool {
    private Queue<Socket> pool = new LinkedList<>();

    public Socket getConnection() { /* ... */ }
    public void releaseConnection(Socket s) { /* ... */ }
}
```

### Best Practices

#### Chiusura Risorse

Sempre chiudere stream e socket:

```java
try (Socket socket = new Socket(ip, port);
     ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
     ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

    // Uso socket...

} catch (IOException e) {
    // Gestione errore
}
// Chiusura automatica con try-with-resources
```

#### Validazione Input

Usare sempre `Keyboard` invece di `Scanner`:

```java
// GOOD: Validazione robusta
double radius = Keyboard.readDouble();
while (radius <= 0) {
    System.err.println("Radius deve essere > 0");
    radius = Keyboard.readDouble();
}

// BAD: Può crashare
Scanner sc = new Scanner(System.in);
double radius = sc.nextDouble();  // Crash se input non valido
```

#### Gestione Eccezioni Server

Cattura e mostra messaggi di errore chiari:

```java
try {
    String result = learningFromDbTable();
    System.out.println(result);
} catch (ServerException e) {
    System.err.println("Errore server: " + e.getMessage());
    System.err.println("Verificare configurazione database su server");
} catch (IOException e) {
    System.err.println("Errore comunicazione: " + e.getMessage());
    System.err.println("Server potrebbe essere disconnesso");
}
```

---

## Riferimenti Aggiuntivi

### Documentazione Relativa

- [`qtServer/README.md`](../qtServer/README.md) - Documentazione server
- [`docs/sprints/SPRINT_4.md`](../docs/sprints/SPRINT_4.md) - Keyboard Input (QT03)
- [`docs/sprints/SPRINT_8.md`](../docs/sprints/SPRINT_8.md) - Socket Client-Server (QT08)

### Diagrammi UML

- `docs/uml/qtClient/keyboardinput/keyboardinput_package.puml`
- `docs/uml/workflows/client_server_communication_sequence.puml`

### Guide di Utilizzo

- [`docs/MAKEFILE_GUIDE.md`](../docs/MAKEFILE_GUIDE.md) - Compilazione con Makefile
- [`CLAUDE.md`](../CLAUDE.md) - Contesto completo progetto

---

**Versione documento**: 1.0
**Data ultimo aggiornamento**: 2025-11-09
**Autore**: Progetto MAP - Quality Threshold Clustering
