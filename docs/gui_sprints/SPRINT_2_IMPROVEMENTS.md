# Sprint 2 - Miglioramenti da Implementare

> **Documento**: Criticità e miglioramenti identificati durante la revisione Sprint 2
> **Data revisione**: 2025-11-08
> **Stato**: Da affrontare in Sprint 3+

---

## Criticità MEDIA (da risolvere in Sprint 3)

### 1. Validazione parametri in ClusteringResult

**File**: `qtGUI/src/main/java/gui/models/ClusteringResult.java:42`

**Problema**:
Il costruttore di ClusteringResult non valida i parametri null.

**Codice attuale**:
```java
public ClusteringResult(ClusterSet clusterSet, Data data, double radius,
                       long executionTimeMs, QTMiner miner) {
    this.clusterSet = clusterSet;
    this.data = data;
    // ... no null checks
}
```

**Fix consigliato**:
```java
public ClusteringResult(ClusterSet clusterSet, Data data, double radius,
                       long executionTimeMs, QTMiner miner) {
    this.clusterSet = Objects.requireNonNull(clusterSet, "ClusterSet non può essere null");
    this.data = Objects.requireNonNull(data, "Data non può essere null");
    this.miner = Objects.requireNonNull(miner, "QTMiner non può essere null");
    // ...
}
```

**Impatto**: Medio - Previene NullPointerException silenziose
**Priorità**: Sprint 3

---

### 2. Sicurezza password database

**File**: `qtGUI/src/main/java/gui/models/ClusteringConfiguration.java:109`

**Problema**:
Password database memorizzata come `String` invece di `char[]`.

**Codice attuale**:
```java
private String dbPassword;
```

**Motivazione problema**:
- String sono immutabili e rimangono in memoria fino al garbage collection
- Visibili in heap dumps
- Non possono essere "pulite" dopo l'uso

**Fix consigliato**:
```java
private char[] dbPassword;

public void setDbPassword(char[] password) {
    if (this.dbPassword != null) {
        Arrays.fill(this.dbPassword, ' '); // Pulisci vecchia password
    }
    this.dbPassword = password != null ? password.clone() : null;
}

public char[] getDbPassword() {
    return dbPassword != null ? dbPassword.clone() : null;
}

// Metodo di cleanup
public void clearPassword() {
    if (dbPassword != null) {
        Arrays.fill(dbPassword, ' ');
    }
}
```

**Impatto**: Medio - Migliora sicurezza credenziali
**Priorità**: Sprint 3 (se si integra database reale)

---

### 3. Validazione radius in setter

**File**: `qtGUI/src/main/java/gui/models/ClusteringConfiguration.java:64`

**Problema**:
Il setter di radius non valida che il valore sia >= 0.

**Codice attuale**:
```java
public void setRadius(double radius) {
    this.radius = radius;
}
```

**Fix consigliato**:
```java
public void setRadius(double radius) {
    if (radius < 0) {
        throw new IllegalArgumentException("Radius deve essere non negativo, ricevuto: " + radius);
    }
    this.radius = radius;
}
```

**Impatto**: Medio - Fail-fast su input errati
**Priorità**: Sprint 3

---

### 4. Validazione porta database

**File**: `qtGUI/src/main/java/gui/models/ClusteringConfiguration.java:86`

**Problema**:
Nessun controllo che la porta sia nel range valido 1-65535.

**Codice attuale**:
```java
public void setDbPort(int dbPort) {
    this.dbPort = dbPort;
}
```

**Fix consigliato**:
```java
public void setDbPort(int dbPort) {
    if (dbPort < 1 || dbPort > 65535) {
        throw new IllegalArgumentException("Porta database deve essere tra 1 e 65535, ricevuto: " + dbPort);
    }
    this.dbPort = dbPort;
}
```

**Impatto**: Basso - Errori connessione più chiari
**Priorità**: Sprint 3

---

## Criticità BASSA (miglioramenti opzionali)

### 5. Test connessione database prima del clustering

**File**: `qtGUI/src/main/java/gui/controllers/HomeController.java:318`

**Suggerimento**:
Aggiungere un test di connessione prima di avviare il clustering per dare feedback immediato all'utente.

**Implementazione proposta**:
```java
if (dataSource == DataSource.DATABASE) {
    DataImportService dataService = ApplicationContext.getInstance().getDataImportService();
    boolean connected = dataService.testDatabaseConnection(
        config.getDbHost(), config.getDbPort(), config.getDbName(),
        config.getDbUser(), config.getDbPassword()
    );

    if (!connected) {
        showError("Connessione Database Fallita",
                 "Impossibile connettersi al database. Verificare i parametri.");
        return;
    }
}
```

**Priorità**: Sprint 4+ (opzionale)

---

### 6. Validazione esistenza file CSV

**File**: `qtGUI/src/main/java/gui/controllers/HomeController.java:313`

**Suggerimento**:
Verificare che il file CSV esista ancora prima di procedere.

**Implementazione proposta**:
```java
if (dataSource == DataSource.CSV && selectedCsvFile != null) {
    if (!selectedCsvFile.exists()) {
        showError("File Non Trovato",
                 "Il file CSV selezionato non esiste più: " + selectedCsvFile.getAbsolutePath());
        return;
    }
    config.setCsvFilePath(selectedCsvFile.getAbsolutePath());
}
```

**Priorità**: Sprint 4+ (opzionale)

---

### 7. Progress tracking reale

**File**: `qtGUI/src/main/java/gui/controllers/ClusteringController.java:213`

**Problema**:
Le percentuali di progresso sono hardcoded e non riflettono il progresso reale dell'algoritmo.

**Codice attuale**:
```java
updateProgress(40, 100); // Percentuali fisse
```

**Suggerimento per Sprint 3+**:
- Modificare QTMiner per supportare callback di progresso
- Aggiungere listener per aggiornamenti iterazione per iterazione
- Calcolare percentuale reale: (cluster_trovati / tuple_totali) * 100

**Esempio interfaccia**:
```java
public interface ClusteringProgressListener {
    void onProgress(int clustersTrovati, int tupleClusterizzate, int tupleTotali);
}

// In QTMiner
public void setProgressListener(ClusteringProgressListener listener) { ... }
```

**Priorità**: Sprint 4+ (enhancement)

---

## Timestamp Accurato in ClusteringResult

**Nota tecnica**:
Attualmente il timestamp viene creato nel costruttore di ClusteringResult, non al momento effettivo del clustering.

**Impatto**: Minimo - differenza di pochi millisecondi
**Priorità**: Molto bassa

**Fix opzionale** (se necessario):
Passare il timestamp come parametro al costruttore invece di generarlo internamente.

---

## Riepilogo Priorità

| Priorità | Numero criticità | Tempo stimato | Sprint consigliato |
|----------|------------------|---------------|-------------------|
| MEDIA    | 4                | 2-3 ore       | Sprint 3          |
| BASSA    | 3                | 1-2 ore       | Sprint 4+         |

**Totale**: 7 miglioramenti, ~4-5 ore sviluppo

---

## Note Implementazione

### Quando implementare:

**Sprint 3** (Visualizzazione 2D):
- Fix #1-4 (criticità MEDIA)
- Applicare durante refactoring generale

**Sprint 4+** (Export/Salvataggio):
- Fix #5-7 (criticità BASSA)
- Implementare se tempo disponibile

### Ordine consigliato:

1. **Fix #3** (validazione radius) - più semplice, alto impatto
2. **Fix #4** (validazione porta) - simile a #3
3. **Fix #1** (validazione ClusteringResult) - richiede import Objects
4. **Fix #2** (sicurezza password) - più complesso, richiede refactoring
5. **Fix #5-7** - solo se tempo disponibile

---

## Conclusioni

Le criticità ALTA sono state risolte in Sprint 2. Le criticità MEDIA e BASSA non bloccano la funzionalità ma migliorano robustezza e sicurezza del codice.

**Raccomandazione**: Implementare fix #1-4 durante Sprint 3 come parte del normale sviluppo.

---

**Fine documento**
