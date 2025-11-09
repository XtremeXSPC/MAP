# 2. Installazione e Configurazione

## 2.1 Requisiti di Sistema

### Requisiti Hardware Minimi

| Componente | Requisito Minimo | Raccomandato |
|------------|------------------|--------------|
| CPU | Dual-core 1.5 GHz | Quad-core 2.5 GHz |
| RAM | 2 GB | 4 GB |
| Spazio Disco | 500 MB | 1 GB |
| Risoluzione | 1280x720 | 1920x1080 |

### Requisiti Software

| Software | Versione Minima | Note |
|----------|-----------------|------|
| Java JDK | 11 | JavaFX incluso da Java 11+ |
| Sistema Operativo | Windows 10, macOS 10.14, Linux (kernel 4.4+) | |
| MySQL (opzionale) | 5.7 | Per persistenza database |
| MySQL Connector/J | 8.0 | Driver JDBC incluso nel progetto |

### Verifica Java Installato

```bash
java -version
```

Output atteso (esempio):
```
java version "11.0.12" 2021-07-20 LTS
Java(TM) SE Runtime Environment 18.9 (build 11.0.12+8-LTS-237)
Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.12+8-LTS-237, mixed mode)
```

Se Java non è installato, procedi al paragrafo 2.2.

---

## 2.2 Installazione Java

### Windows

#### Opzione 1: Oracle JDK (Raccomandato)

1. Visita: https://www.oracle.com/java/technologies/downloads/
2. Scarica **Java SE Development Kit 11** (o superiore)
3. Esegui installer `.exe`
4. Segui wizard installazione (default va bene)
5. Aggiungi a PATH:
   - Pannello di Controllo → Sistema → Variabili d'ambiente
   - Aggiungi a `Path`: `C:\Program Files\Java\jdk-11\bin`

#### Opzione 2: OpenJDK

```powershell
# Tramite Chocolatey (richiede Chocolatey installato)
choco install openjdk11
```

### macOS

#### Opzione 1: Homebrew (Raccomandato)

```bash
# Installa Homebrew se non presente
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Installa OpenJDK 11
brew install openjdk@11

# Link a /usr/local/bin
sudo ln -sfn /usr/local/opt/openjdk@11/libexec/openjdk.jdk \
     /Library/Java/JavaVirtualMachines/openjdk-11.jdk
```

#### Opzione 2: Oracle JDK

1. Scarica DMG da Oracle
2. Monta immagine disco
3. Trascina JDK in Applications
4. Verifica installazione: `java -version`

### Linux (Ubuntu/Debian)

```bash
# Aggiorna indice pacchetti
sudo apt update

# Installa OpenJDK 11
sudo apt install openjdk-11-jdk

# Verifica installazione
java -version
javac -version
```

### Linux (Fedora/CentOS/RHEL)

```bash
# Installa OpenJDK 11
sudo dnf install java-11-openjdk-devel

# Seleziona versione default (se multiple)
sudo alternatives --config java
```

---

## 2.3 Compilazione del Progetto

### Struttura Directory

Assicurati di avere la seguente struttura:

```
MAP/
├── qtServer/
│   ├── src/
│   └── bin/
├── qtClient/
│   ├── src/
│   └── bin/
├── qtGUI/
│   ├── src/
│   └── bin/
└── qtExt/
    ├── src/
    └── bin/
```

### Compilazione Modulo qtServer

```bash
cd MAP/qtServer

# Crea directory bin se non esiste
mkdir -p bin

# Compila tutti i file sorgente
javac -d bin -sourcepath src src/**/*.java

# Verifica compilazione
ls bin/
# Output atteso: data/ database/ mining/ server/
```

### Compilazione Modulo qtClient

```bash
cd MAP/qtClient

mkdir -p bin

javac -d bin -sourcepath src \
      -cp ../qtServer/bin \
      src/**/*.java

# Verifica
ls bin/
# Output atteso: qtClient/ keyboardinput/
```

### Compilazione Modulo qtGUI

**Nota**: qtGUI richiede JavaFX incluso in JDK 11+

```bash
cd MAP/qtGUI

mkdir -p bin

javac -d bin -sourcepath src \
      -cp ../qtServer/bin \
      src/**/*.java

# Copia risorse FXML e CSS
cp -r src/application/fxml bin/application/
cp -r src/application/css bin/application/

# Verifica
ls bin/
# Output atteso: application/ controllers/ services/ models/ charts/ dialogs/ utils/
```

### Compilazione Modulo qtExt (Opzionale)

```bash
cd MAP/qtExt

mkdir -p bin

javac -d bin -sourcepath src \
      -cp ../qtServer/bin \
      src/**/*.java

# Verifica
ls bin/
# Output atteso: tests/ utility/
```

### Script di Build Automatico

Per semplificare, puoi creare script di build:

**build.sh (Linux/macOS)**:
```bash
#!/bin/bash

echo "Building qtServer..."
cd qtServer && mkdir -p bin && javac -d bin -sourcepath src src/**/*.java

echo "Building qtClient..."
cd ../qtClient && mkdir -p bin && javac -d bin -sourcepath src -cp ../qtServer/bin src/**/*.java

echo "Building qtGUI..."
cd ../qtGUI && mkdir -p bin && javac -d bin -sourcepath src -cp ../qtServer/bin src/**/*.java
cp -r src/application/fxml bin/application/ 2>/dev/null || true
cp -r src/application/css bin/application/ 2>/dev/null || true

echo "Build completed successfully!"
```

**build.bat (Windows)**:
```batch
@echo off

echo Building qtServer...
cd qtServer
if not exist bin mkdir bin
javac -d bin -sourcepath src src\**\*.java

echo Building qtClient...
cd ..\qtClient
if not exist bin mkdir bin
javac -d bin -sourcepath src -cp ..\qtServer\bin src\**\*.java

echo Building qtGUI...
cd ..\qtGUI
if not exist bin mkdir bin
javac -d bin -sourcepath src -cp ..\qtServer\bin src\**\*.java
xcopy /E /I /Y src\application\fxml bin\application\fxml
xcopy /E /I /Y src\application\css bin\application\css

echo Build completed successfully!
```

Rendi eseguibile (Linux/macOS):
```bash
chmod +x build.sh
./build.sh
```

---

## 2.4 Configurazione Database (Opzionale)

### Installazione MySQL

#### Windows

1. Scarica MySQL Installer da: https://dev.mysql.com/downloads/installer/
2. Esegui installer
3. Scegli "Developer Default"
4. Imposta password root
5. Completa installazione

#### macOS

```bash
brew install mysql

# Avvia servizio
brew services start mysql

# Configura password root
mysql_secure_installation
```

#### Linux (Ubuntu/Debian)

```bash
sudo apt update
sudo apt install mysql-server

# Avvia servizio
sudo systemctl start mysql
sudo systemctl enable mysql

# Configura
sudo mysql_secure_installation
```

### Creazione Database e Tabella

```bash
# Accedi a MySQL
mysql -u root -p
```

```sql
-- Crea database
CREATE DATABASE qtclustering;

USE qtclustering;

-- Crea tabella playtennis (esempio)
CREATE TABLE playtennis (
    id INT AUTO_INCREMENT PRIMARY KEY,
    outlook VARCHAR(20),
    temperature VARCHAR(20),
    humidity VARCHAR(20),
    wind VARCHAR(20),
    playtennis VARCHAR(3)
);

-- Inserisci dati esempio
INSERT INTO playtennis (outlook, temperature, humidity, wind, playtennis) VALUES
('sunny', 'hot', 'high', 'weak', 'no'),
('sunny', 'hot', 'high', 'strong', 'no'),
('overcast', 'hot', 'high', 'weak', 'yes'),
('rain', 'mild', 'high', 'weak', 'yes'),
('rain', 'cool', 'normal', 'weak', 'yes'),
('rain', 'cool', 'normal', 'strong', 'no'),
('overcast', 'cool', 'normal', 'strong', 'yes'),
('sunny', 'mild', 'high', 'weak', 'no'),
('sunny', 'cool', 'normal', 'weak', 'yes'),
('rain', 'mild', 'normal', 'weak', 'yes'),
('sunny', 'mild', 'normal', 'strong', 'yes'),
('overcast', 'mild', 'high', 'strong', 'yes'),
('overcast', 'hot', 'normal', 'weak', 'yes'),
('rain', 'mild', 'high', 'strong', 'no');

-- Verifica
SELECT * FROM playtennis;
```

### Configurazione File properties

Il server legge configurazione DB da file properties. Crea:

**qtServer/src/database/db.properties**:
```properties
# MySQL Configuration
db.url=jdbc:mysql://localhost:3306/qtclustering
db.user=root
db.password=your_password_here
db.driver=com.mysql.cj.jdbc.Driver

# Connection Pool (opzionale)
db.pool.minSize=5
db.pool.maxSize=20
```

**IMPORTANTE**: Non committare password su Git! Aggiungi a `.gitignore`:
```
**/db.properties
```

### Verifica Connessione

```bash
cd MAP/qtServer/bin

# Test connessione database
java database.DbAccess
```

Output atteso:
```
Testing database connection...
Connection successful!
Database: qtclustering
Tables found: playtennis
```

---

## 2.5 Avvio del Server

### Avvio Manuale

```bash
cd MAP/qtServer/bin

java server.MultiServer
```

Output:
```
QT Clustering Server v1.0
Starting server on port 8080...
Server started successfully!
Waiting for clients...
```

Il server è ora in ascolto sulla porta **8080** e accetta connessioni multiple.

### Avvio in Background (Linux/macOS)

```bash
# Con output su file log
nohup java server.MultiServer > server.log 2>&1 &

# Verifica processo
ps aux | grep MultiServer

# Visualizza log in tempo reale
tail -f server.log
```

### Avvio come Servizio (Avanzato)

#### Systemd (Linux)

Crea file `/etc/systemd/system/qtserver.service`:

```ini
[Unit]
Description=QT Clustering Server
After=network.target

[Service]
Type=simple
User=youruser
WorkingDirectory=/path/to/MAP/qtServer/bin
ExecStart=/usr/bin/java server.MultiServer
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

Comandi:
```bash
# Ricarica systemd
sudo systemctl daemon-reload

# Abilita avvio automatico
sudo systemctl enable qtserver

# Avvia servizio
sudo systemctl start qtserver

# Verifica stato
sudo systemctl status qtserver
```

### Configurazione Porta Custom

Se porta 8080 è occupata, modifica in `MultiServer.java`:

```java
// Cambia da:
private static final int PORT = 8080;

// A (esempio):
private static final int PORT = 9000;
```

Poi ricompila:
```bash
javac -d bin -sourcepath src src/server/MultiServer.java
```

### Test Connessione Server

Verifica che il server accetti connessioni:

```bash
# Linux/macOS
telnet localhost 8080

# Windows (PowerShell)
Test-NetConnection -ComputerName localhost -Port 8080
```

Se connessione riuscita, il server è operativo.

---

## Checklist Post-Installazione

- [ ] Java JDK 11+ installato e funzionante
- [ ] Progetto compilato senza errori
- [ ] MySQL installato (se si usa database)
- [ ] Database `qtclustering` creato
- [ ] Tabella `playtennis` popolata con dati
- [ ] File `db.properties` configurato
- [ ] Server avviato e in ascolto su porta 8080
- [ ] Connessione server testata

---

## Risoluzione Problemi Comuni

### Errore: "javac: command not found"

**Causa**: Java JDK non installato o non nel PATH

**Soluzione**:
```bash
# Verifica installazione
which javac

# Se vuoto, reinstalla JDK o aggiungi a PATH
export PATH=$PATH:/path/to/jdk/bin  # Linux/macOS
```

### Errore: "package javax.swing does not exist"

**Causa**: Versione Java troppo vecchia

**Soluzione**: Aggiorna a Java 11+

### Errore: "Cannot connect to database"

**Causa**: MySQL non avviato o credenziali errate

**Soluzione**:
```bash
# Verifica servizio MySQL
sudo systemctl status mysql  # Linux
brew services list  # macOS

# Testa credenziali
mysql -u root -p
```

### Errore: "Port 8080 already in use"

**Causa**: Altra applicazione usa porta 8080

**Soluzione**:
```bash
# Trova processo
lsof -i :8080  # Linux/macOS
netstat -ano | findstr :8080  # Windows

# Termina processo o usa porta diversa
```

---

[← Capitolo 1: Introduzione](01_introduzione.md) | [Capitolo 3: Interfaccia Grafica (GUI) →](03_gui.md)
