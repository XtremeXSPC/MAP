# 📘 Guida al Makefile - Quality Threshold Clustering

> Guida rapida all'utilizzo del Makefile per compilare, distribuire ed eseguire il progetto QT Client/Server.

---

## Quick Start

```bash
# Compila tutto
make all

# Crea i JAR per la distribuzione
make jar

# Pulisci i file compilati
make clean
```

---

## 📋 Comandi Disponibili

### Compilazione

| Comando        | Descrizione                       |
| -------------- | --------------------------------- |
| `make all`     | Compila sia client che server     |
| `make client`  | Compila solo qtClient             |
| `make server`  | Compila solo qtServer             |
| `make rebuild` | Pulisce tutto e ricompila da zero |

**Esempio:**

```bash
# Compila solo il server
make server

# Ricompila tutto da zero
make rebuild
```

### Creazione JAR

| Comando           | Descrizione                  | Output          |
| ----------------- | ---------------------------- | --------------- |
| `make jar`        | Crea JAR per client e server | `*.jar` in root |
| `make client-jar` | Crea solo qtClient.jar       | `qtClient.jar`  |
| `make server-jar` | Crea solo qtServer.jar       | `qtServer.jar`  |

**I JAR includono:**

- Tutte le classi compilate
- Manifest con la main class corretta
- Sono pronti per essere distribuiti ed eseguiti

**Esempio:**

```bash
# Crea entrambi i JAR
make jar

# Verifica creazione
ls -lh *.jar
```

### Esecuzione

#### Da file compilati (.class)

```bash
# Avvia server sulla porta 8080 (default)
make run-server

# Avvia server su porta personalizzata
make run-server PORT=9999

# Avvia client (localhost:8080 default)
make run-client

# Avvia client con IP e porta personalizzati
make run-client IP=192.168.1.100 PORT=9999
```

#### Da file JAR

```bash
# Avvia server da JAR
make run-server-jar PORT=8080

# Avvia client da JAR
make run-client-jar IP=localhost PORT=8080
```

**Oppure direttamente con java:**

```bash
# Server
java -jar qtServer.jar 8080

# Client
java -jar qtClient.jar localhost 8080
```

### Pulizia

| Comando             | Descrizione                          |
| ------------------- | ------------------------------------ |
| `make clean`        | Rimuove tutti i file compilati e JAR |
| `make clean-client` | Rimuove solo bin del client          |
| `make clean-server` | Rimuove solo bin del server          |
| `make clean-jar`    | Rimuove solo i file JAR              |

**Esempio:**

```bash
# Pulizia completa
make clean

# Rimuovi solo i JAR (mantiene .class)
make clean-jar
```

### Utility

| Comando             | Descrizione                           |
| ------------------- | ------------------------------------- |
| `make help`         | Mostra menu di aiuto completo         |
| `make info`         | Mostra informazioni su stato progetto |
| `make check-deps`   | Verifica disponibilità Java/javac/jar |
| `make list-sources` | Lista tutti i file sorgente .java     |

---

## 🎯 Casi d'Uso Comuni

### Scenario 1: Primo utilizzo del progetto

```bash
# 1. Verifica dipendenze
make check-deps

# 2. Compila tutto
make all

# 3. Verifica stato
make info

# 4. Crea JAR per distribuzione
make jar
```

### Scenario 2: Sviluppo rapido (compile & test)

```bash
# Durante lo sviluppo del server:
make server && make run-server PORT=8080

# Durante lo sviluppo del client:
make client && make run-client IP=localhost PORT=8080
```

### Scenario 3: Deploy in produzione

```bash
# 1. Pulisci tutto
make clean

# 2. Ricompila da zero
make rebuild

# 3. Crea JAR
make jar

# 4. Distribuisci qtServer.jar e qtClient.jar
scp qtServer.jar user@server:/path/
scp qtClient.jar user@client:/path/
```

### Scenario 4: Test su rete locale

**Terminale 1 (Server):**

```bash
make run-server PORT=9999
```

**Terminale 2 (Client):**

```bash
make run-client IP=192.168.1.10 PORT=9999
```

### Scenario 5: Debugging

```bash
# Vedi tutti i file sorgente
make list-sources

# Verifica stato compilazione
make info

# Ricompila tutto con output completo
make clean && make all
```

---

## 🔧 Configurazione Avanzata

### Variabili Modificabili

Puoi sovrascrivere le variabili del Makefile dalla linea di comando:

```bash
# Usa un compilatore Java specifico
make all JAVAC=/usr/local/jdk-17/bin/javac

# Cambia directory bin
make client CLIENT_BIN=qtClient/build

# Esegui con parametri personalizzati
make run-server PORT=8888
make run-client IP=10.0.0.5 PORT=8888
```

### Variabili Principali

| Variabile    | Default        | Descrizione               |
| ------------ | -------------- | ------------------------- |
| `JAVAC`      | `javac`        | Compilatore Java          |
| `JAVA`       | `java`         | Runtime Java              |
| `JAR`        | `jar`          | Tool JAR                  |
| `CLIENT_SRC` | `qtClient/src` | Directory sorgenti client |
| `CLIENT_BIN` | `qtClient/bin` | Directory binari client   |
| `SERVER_SRC` | `qtServer/src` | Directory sorgenti server |
| `SERVER_BIN` | `qtServer/bin` | Directory binari server   |
| `IP`         | `localhost`    | IP server per client      |
| `PORT`       | `8080`         | Porta server              |

---

## 📂 Struttura Directory

```path
MAP/
├── Makefile                 # Il Makefile principale
├── qtClient.jar             # JAR client (dopo make jar)
├── qtServer.jar             # JAR server (dopo make jar)
│
├── qtClient/
│   ├── src/                 # Sorgenti client
│   │   ├── MainTest.java
│   │   ├── ServerException.java
│   │   └── keyboardinput/
│   └── bin/                 # File .class compilati (autogenerato)
│
└── qtServer/
    ├── src/                 # Sorgenti server
    │   ├── data/
    │   ├── mining/
    │   ├── database/
    │   ├── server/
    │   ├── tests/
    │   └── utility/
    └── bin/                 # File .class compilati (autogenerato)
```

---

## 🐛 Risoluzione Problemi

### Errore: "javac: command not found"

**Problema:** Java non installato o non nel PATH

**Soluzione:**

```bash
# Verifica installazione Java
java -version
javac -version

# Su macOS (installa Java se necessario)
brew install openjdk

# Aggiungi al PATH (nel .zshrc o .bashrc)
export PATH="/usr/local/opt/openjdk/bin:$PATH"
```

### Errore di compilazione

**Problema:** File sorgente modificati con errori

**Soluzione:**

```bash
# Pulisci e ricompila
make clean
make all

# Verifica errori specifici
javac qtServer/src/**/*.java
```

### JAR non eseguibile

**Problema:** Manifest non contiene main class

**Soluzione:**
Il Makefile usa il flag `-e` di `jar` per includere automaticamente la main class. Verifica con:

```bash
# Controlla manifest
jar tf qtServer.jar | head -10
jar xf qtServer.jar META-INF/MANIFEST.MF
cat META-INF/MANIFEST.MF
```

### Server non accetta connessioni

**Problema:** Porta già in uso o firewall

**Soluzione:**

```bash
# Verifica porta libera
lsof -i :8080

# Usa porta diversa
make run-server PORT=9999

# Controlla firewall (macOS)
sudo /usr/libexec/ApplicationFirewall/socketfilterfw --getglobalstate
```

---

## 💡 Tips & Tricks

### 1. Compilazione Incrementale

Il Makefile usa file marker (`.compiled`) per evitare ricompilazioni inutili:

```bash
# Compila solo se sorgenti modificati
make all   # Veloce se già compilato

# Forza ricompilazione
touch qtServer/src/server/MultiServer.java
make server
```

### 2. Workflow Efficiente

```bash
# Alias utili (aggiungi a ~/.zshrc o ~/.bashrc)
alias qts='make run-server PORT=8080'
alias qtc='make run-client IP=localhost PORT=8080'
alias qtj='make jar'
alias qtclean='make clean'
```

### 3. Test Rapidi

```bash
# Test compilazione senza eseguire
make all

# Test JAR creation
make jar && java -jar qtServer.jar --help
```

### 4. Output Colorato

Il Makefile usa codici ANSI per colorare l'output:

- 🔵 **Blu**: Operazioni in corso
- 🟢 **Verde**: Successo
- 🟡 **Giallo**: Warnings o info

Per disabilitare i colori:

```bash
make all 2>&1 | cat
```

---

## 📖 Comandi Make Avanzati

### Esecuzione Parallela

```bash
# Compila client e server in parallelo (se supportato)
make -j2 client server
```

### Verbose Mode

```bash
# Mostra tutti i comandi eseguiti
make all VERBOSE=1

# Oppure rimuovi @ dal Makefile temporaneamente
```

### Dry Run

```bash
# Mostra cosa verrà eseguito senza farlo
make -n all
```

---

## 🔗 Link Utili

- **Java Documentation:** <https://docs.oracle.com/javase/8/docs/>
- **Make Manual:** <https://www.gnu.org/software/make/manual/>
- **Java JAR Guide:** <https://docs.oracle.com/javase/tutorial/deployment/jar/>

---

## 📝 Note Finali

### Best Practices

1. **Sempre `make clean` prima di commit importanti**

   ```bash
   make clean && git add -A && git commit -m "..."
   ```

2. **Testa i JAR prima della distribuzione**

   ```bash
   make jar
   java -jar qtServer.jar 8080 &
   java -jar qtClient.jar localhost 8080
   ```

3. **Usa `make info` per verificare lo stato**

   ```bash
   make info
   ```

### Limiti Attuali

- ❌ Non supporta dipendenze esterne (JAR di terze parti)
- ❌ Non genera Javadoc automaticamente
- ❌ Non esegue test automatici (JUnit)

**Questi saranno implementati negli sprint futuri!**

---

## 📞 Supporto

Per problemi o domande:

1. Esegui `make help` per comandi disponibili
2. Esegui `make check-deps` per verificare ambiente
3. Esegui `make info` per diagnosticare stato progetto

---

**Versione:** 1.0
**Data:** 2025-11-08
**Compatibilità:** Java 8+, GNU Make 3.81+

---
