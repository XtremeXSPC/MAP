# 9. Troubleshooting

## 9.1 Errori Comuni

### Errore: "Connection refused: connect"

**Sintomo**:
```
java.net.ConnectException: Connection refused: connect
```

**Causa**: Server non avviato o porta errata

**Soluzione**:
1. Verifica server in esecuzione:
   ```bash
   ps aux | grep MultiServer  # Linux/macOS
   netstat -ano | findstr :8080  # Windows
   ```

2. Avvia server se non attivo:
   ```bash
   cd qtServer/bin
   java server.MultiServer
   ```

3. Verifica porta corretta (default 8080)

---

### Errore: "ClassNotFoundException"

**Sintomo**:
```
java.lang.ClassNotFoundException: server.MultiServer
```

**Causa**: Compilazione incompleta o directory errata

**Soluzione**:
1. Verifica directory bin:
   ```bash
   ls qtServer/bin/server/
   # Deve contenere: MultiServer.class
   ```

2. Ricompila se necessario:
   ```bash
   cd qtServer
   javac -d bin -sourcepath src src/**/*.java
   ```

3. Verifica di eseguire da directory `bin/`

---

### Errore: "Table doesn't exist"

**Sintomo**:
```
ERROR: Table 'playtennis' doesn't exist in database
```

**Causa**: Tabella non creata o nome errato

**Soluzione**:
1. Verifica tabelle esistenti:
   ```sql
   SHOW TABLES;
   ```

2. Attenzione: Nome tabella case-sensitive su Linux!
   ```
   playtennis ≠ PlayTennis ≠ PLAYTENNIS
   ```

3. Crea tabella se mancante (vedi Capitolo 6)

---

### Errore: "OutOfMemoryError"

**Sintomo**:
```
java.lang.OutOfMemoryError: Java heap space
```

**Causa**: Dataset troppo grande per heap JVM

**Soluzione**:
1. Aumenta memoria heap:
   ```bash
   java -Xmx2G server.MultiServer  # 2GB
   java -Xmx4G server.MultiServer  # 4GB
   ```

2. Riduci dimensione dataset (filtraggio/sampling)

3. Valuta clustering gerarchico per dataset > 10000 tuple

---

### Errore: "Clustering timeout"

**Sintomo**:
```
ERROR: Clustering operation timed out after 300 seconds
```

**Causa**: Dataset grande + radius basso = complessità O(n³)

**Soluzione**:
1. Aumenta timeout (GUI Settings):
   ```
   Timeout: 300s → 600s
   ```

2. Aumenta radius (meno iterazioni):
   ```
   Radius: 0.0 → 0.5
   ```

3. Abilita cache distanze (se disponibile)

4. Usa subset del dataset per test

---

## 9.2 Log e Diagnostica

### Log Server

**Abilitazione**:
```bash
# Redirect output su file
java server.MultiServer > server.log 2>&1
```

**Contenuto log**:
```
[2025-11-09 14:30:15] Server started on port 8080
[2025-11-09 14:30:22] Client connected: /127.0.0.1:54321
[2025-11-09 14:30:25] Loading table: playtennis
[2025-11-09 14:30:26] Clustering started: radius=0.5
[2025-11-09 14:30:31] Clustering completed: 7 clusters
```

### Log Client

**Output verboso**:
```bash
java -Dverbose=true qtClient.MainTest
```

### Debugging

**JVM Debug Mode**:
```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
     server.MultiServer
```

Connetti debugger (Eclipse, IntelliJ) alla porta 5005.

---

## 9.3 Performance Issues

### Problema: Clustering Lento

**Diagnosi**:
```
Dataset: 5000 tuple, 10 attributi
Radius: 0.1
Tempo: 15 minuti (troppo!)
```

**Ottimizzazioni**:

1. **Aumenta radius**:
   ```
   Radius 0.1 → 0.5
   Speedup: 5-10x
   ```

2. **Abilita cache distanze**:
   ```java
   // In QTMiner
   private boolean enableCache = true;
   ```

3. **Sampling dataset**:
   ```
   5000 tuple → 1000 tuple (random sample)
   Speedup: 25x
   ```

4. **Parallelizzazione** (se implementata):
   ```
   Thread pool: 1 → 4 thread
   Speedup: 2-3x
   ```

---

### Problema: GUI Lenta/Bloccata

**Causa**: Operazione pesante su JavaFX Application Thread

**Soluzione**: Verifica che clustering usi JavaFX Task (background)

**Codice corretto**:
```java
Task<ClusteringResult> task = new Task<>() {
    @Override
    protected ClusteringResult call() {
        // Clustering qui (background)
        return result;
    }
};
task.setOnSucceeded(e -> {
    // Update UI (main thread)
    updateResults(task.getValue());
});
ExecutorService.submit(task);
```

---

### Problema: Database Lento

**Diagnosi**:
```
Caricamento 10000 tuple: 30 secondi
```

**Ottimizzazioni**:

1. **Indici database**:
   ```sql
   CREATE INDEX idx_attr1 ON mytable(attribute1);
   ```

2. **Fetch size**:
   ```java
   statement.setFetchSize(1000);
   ```

3. **Connection pooling**:
   ```java
   HikariCP pool con minSize=5, maxSize=20
   ```

---

## 9.4 Checklist Debug

Quando qualcosa non funziona:

- [ ] Server avviato?
  ```bash
  ps aux | grep MultiServer
  ```

- [ ] Database raggiungibile?
  ```bash
  mysql -u root -p -e "SHOW DATABASES;"
  ```

- [ ] Compilazione OK?
  ```bash
  javac -version && ls bin/
  ```

- [ ] Porte libere?
  ```bash
  lsof -i :8080
  ```

- [ ] Log errori?
  ```bash
  tail -f server.log
  ```

- [ ] Memoria sufficiente?
  ```bash
  free -h  # Linux
  ```

---

## 9.5 Problemi Noti

### Limitazione: Attributi Continui

**Status**: Parzialmente supportato

**Workaround**: Discretizzazione manuale
```python
# Python preprocessing
bins = [0, 10, 20, 30, 40]
df['age_binned'] = pd.cut(df['age'], bins=bins, labels=['young', 'adult', 'middle', 'senior'])
```

### Limitazione: Dataset > 10K tuple

**Status**: Performance degradate

**Workaround**:
- Sampling statistico
- Pre-clustering con algoritmo più veloce (K-Means)
- Clustering gerarchico

### Bug: Caratteri Speciali CSV

**Status**: Encoding issues con UTF-8

**Workaround**:
```bash
# Converti CSV in ASCII
iconv -f UTF-8 -t ASCII//TRANSLIT input.csv > output.csv
```

---

[← Capitolo 8: Interpretazione](08_interpretazione.md) | [Capitolo 10: FAQ →](10_faq.md)
