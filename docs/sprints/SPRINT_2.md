# Sprint 2 - Persistenza e I/O

## Obiettivo

Implementare funzionalità di persistenza per salvare e caricare risultati del clustering, e supportare dataset esterni in formato CSV.

## Durata

1 settimana

## Prerequisiti

- Sprint 0 completato (classi base: Data, Tuple, ArraySet, Attribute, Item)
- Sprint 1 completato (algoritmo QT: Cluster, ClusterSet, QTMiner)

---

## Backlog dello Sprint

### 1. Serializzazione Cluster su File

**Priorità:** Alta
**Story Points:** 5

#### Descrizione

Implementare la capacità di salvare i risultati del clustering su file per analisi future o condivisione.

#### Criteri di Accettazione

- [ ] Implementare metodo `save(String filename)` in `ClusterSet`
- [ ] Salvare struttura cluster in formato leggibile
- [ ] Includere centroidi e ID tuple per ogni cluster
- [ ] Includere metadata (radius, numero cluster, timestamp)
- [ ] Gestire eccezioni I/O con messaggi informativi
- [ ] Supportare path assoluti e relativi

#### Dettagli Implementativi

**Formato file output:** `.dmp` (dump file)

```
Esempio playtennis.dmp:
---
METADATA
radius=0.0
numClusters=11
timestamp=2025-11-07T10:30:00
---
CLUSTER 0
centroid=sunny,hot,high,weak,no
tupleIDs=0,1,7
---
CLUSTER 1
centroid=overcast,hot,high,weak,yes
tupleIDs=2
...
```

**Metodi da aggiungere:**

```java
// In ClusterSet
public void save(String filename) throws IOException;
```

---

### 2. Caricamento Cluster da File

**Priorità:** Alta
**Story Points:** 5

#### Descrizione

Implementare la capacità di caricare risultati di clustering precedentemente salvati.

#### Criteri di Accettazione

- [ ] Implementare costruttore `ClusterSet(String filename)`
- [ ] Parsare file `.dmp` e ricostruire cluster
- [ ] Validare formato file e compatibilità
- [ ] Gestire file corrotti o mancanti
- [ ] Lanciare eccezioni appropriate con messaggi chiari
- [ ] Verificare integrità dati caricati

#### Dettagli Implementativi

```java
// In ClusterSet
public ClusterSet(String filename) throws IOException, InvalidFileFormatException;

// Nuova classe per gestire errori formato
class InvalidFileFormatException extends Exception {
    public InvalidFileFormatException(String message);
}
```

**Validazioni richieste:**

- File esiste e leggibile
- Formato header valido
- Numero cluster consistente
- ID tuple validi (non negativi, senza duplicati tra cluster)

---

### 3. Supporto Dataset Esterni (CSV)

**Priorità:** Alta
**Story Points:** 8

#### Descrizione

Permettere il caricamento di dataset da file CSV invece di usare solo il dataset PlayTennis hardcoded.

#### Criteri di Accettazione

- [ ] Creare nuovo costruttore `Data(String csvFilename)`
- [ ] Parsare file CSV con intestazioni
- [ ] Rilevare automaticamente tipo attributi (discrete vs continuous)
- [ ] Supportare valori mancanti (es. "?", "NA", celle vuote)
- [ ] Validare consistenza dati (numero colonne uguale in tutte le righe)
- [ ] Gestire file grandi (>1000 righe) efficientemente
- [ ] Creare file CSV di esempio per testing

#### Dettagli Implementativi

**Formato CSV atteso:**

```csv
Outlook,Temperature,Humidity,Wind,PlayTennis
sunny,hot,high,weak,no
sunny,hot,high,strong,no
overcast,hot,high,weak,yes
...
```

**Convenzioni:**

- Prima riga: nomi attributi (header)
- Separatore: virgola (`,`)
- Encoding: UTF-8
- Valori mancanti: `?` o cella vuota
- Tipi inferiti: se tutti valori numerici → ContinuousAttribute, altrimenti → DiscreteAttribute

**Metodi da aggiungere:**

```java
// In Data
public Data(String csvFilename) throws IOException, InvalidDataFormatException;
private void parseCSV(String filename) throws IOException;
private Attribute inferAttributeType(String name, int index, List<String> values);
```

**File CSV di esempio da creare:**

- `data/playtennis.csv` - Dataset originale in formato CSV
- `data/weather.csv` - Dataset esempio con 50 tuple

---

### 4. Gestione Errori I/O

**Priorità:** Media
**Story Points:** 3

#### Descrizione

Implementare gestione robusta degli errori per tutte le operazioni I/O.

#### Criteri di Accettazione

- [ ] Creare eccezioni custom per scenari specifici
- [ ] Validare input utente (path, nomi file, parametri)
- [ ] Messaggi errore chiari e informativi
- [ ] Logging operazioni I/O (opzionale, con flag)
- [ ] Try-with-resources per gestione stream
- [ ] Documentare eccezioni in Javadoc

#### Dettagli Implementativi

**Eccezioni custom da creare:**

```java
// Formato file non valido
class InvalidFileFormatException extends Exception {
    public InvalidFileFormatException(String message);
}

// Dati inconsistenti
class InvalidDataFormatException extends Exception {
    public InvalidDataFormatException(String message, int lineNumber);
    public int getLineNumber();
}

// Cluster non compatibile con dataset
class IncompatibleClusterException extends Exception {
    public IncompatibleClusterException(String message);
}
```

**Validazioni da implementare:**

- File esiste: `if (!file.exists()) throw new FileNotFoundException(...)`
- File leggibile: `if (!file.canRead()) throw new IOException(...)`
- Estensione corretta: `.csv` per dataset, `.dmp` per cluster
- Dimensioni file ragionevoli: < 100MB

---

### 5. Aggiornamento MainTest

**Priorità:** Media
**Story Points:** 3

#### Descrizione

Aggiornare `MainTest` per supportare nuove funzionalità I/O con menu interattivo.

#### Criteri di Accettazione

- [ ] Menu opzioni: (1) Dataset hardcoded, (2) Carica CSV, (3) Carica cluster salvato
- [ ] Opzione per salvare cluster dopo computazione
- [ ] Gestire input utente con validazione
- [ ] Mostrare statistiche dataset caricato
- [ ] Permettere esecuzione multipla senza riavvio

#### Dettagli Implementativi

**Menu proposto:**

```
=== QT Clustering System ===
1. Usa dataset PlayTennis (hardcoded)
2. Carica dataset da CSV
3. Carica cluster salvato
0. Esci

Scelta:
```

**Flusso opzione 2 (CSV):**

```
Inserisci path file CSV: data/weather.csv
Caricamento in corso...
✓ Dataset caricato: 50 esempi, 6 attributi
Inserisci radius: 0.5
Computazione in corso...
✓ Trovati 7 cluster

Salvare risultati? (s/n): s
Inserisci nome file output: weather_clusters.dmp
✓ Cluster salvati in weather_clusters.dmp
```

---

## Architettura Modificata

### Nuove Classi

```
src/
├── exceptions/
│   ├── InvalidFileFormatException.java
│   ├── InvalidDataFormatException.java
│   └── IncompatibleClusterException.java
│
└── (classi esistenti con metodi aggiunti)
```

### Diagramma Dipendenze I/O

```
┌─────────────┐
│  MainTest   │
└──────┬──────┘
       │
       ├──────► ClusterSet.save(filename)
       │              │
       │              ▼
       │        FileWriter
       │        BufferedWriter
       │
       ├──────► ClusterSet(filename)
       │              │
       │              ▼
       │        FileReader
       │        BufferedReader
       │
       └──────► Data(csvFilename)
                      │
                      ▼
                FileReader
                BufferedReader
                CSV Parser
```

---

## Testing

### Test Funzionali

#### Test 1: Salvataggio e Caricamento Cluster

```bash
$ java MainTest
Scelta: 1  # PlayTennis
Inserisci radius: 0
# Output cluster...
Salvare? s
File: test_clusters.dmp
✓ Salvato

# Nuovo run
$ java MainTest
Scelta: 3  # Carica
File: test_clusters.dmp
✓ Caricato: 11 cluster, radius=0.0
# Mostra cluster identici al primo run
```

**Criteri successo:**

- File `.dmp` creato correttamente
- Caricamento ricostruisce cluster identici
- Metadata corretti

---

#### Test 2: Caricamento CSV

Creare `data/test_small.csv`:

```csv
A1,A2,A3
x,1,high
y,2,low
x,1,high
```

```bash
$ java MainTest
Scelta: 2
File: data/test_small.csv
✓ Caricato: 3 esempi, 3 attributi
Attributi rilevati:
  - A1 (discrete): {x, y}
  - A2 (discrete): {1, 2}
  - A3 (discrete): {high, low}
```

**Criteri successo:**

- Parsing corretto
- Tipi attributi inferiti correttamente
- Gestione duplicati valori

---

#### Test 3: Gestione Errori

**Test 3a: File non esistente**

```bash
$ java MainTest
Scelta: 2
File: nonexistent.csv
✗ Errore: File non trovato: nonexistent.csv
```

**Test 3b: CSV malformato**
Creare `data/bad.csv`:

```csv
A,B
1,2
3
```

```bash
$ java MainTest
Scelta: 2
File: data/bad.csv
✗ Errore: Numero colonne inconsistente alla riga 3 (atteso 2, trovato 1)
```

**Test 3c: Formato .dmp corrotto**

```bash
$ java MainTest
Scelta: 3
File: corrupted.dmp
✗ Errore: Formato file non valido: header METADATA mancante
```

---

### Test Performance

**Dataset medio (500 righe):**

- Tempo caricamento CSV: < 1s
- Tempo salvataggio cluster: < 500ms
- Tempo caricamento cluster: < 300ms

**Dataset grande (5000 righe):**

- Tempo caricamento CSV: < 5s
- Memoria usata: < 100MB

---

## Deliverables

### Codice

- [ ] `ClusterSet` modificato con `save()` e costruttore da file
- [ ] `Data` modificato con costruttore da CSV
- [ ] Package `exceptions` con 3 eccezioni custom
- [ ] `MainTest` con menu interattivo
- [ ] File CSV di esempio in `data/`

### Documentazione

- [ ] Javadoc completo per nuovi metodi
- [ ] File `SPRINT_2.md` (questo documento)
- [ ] `README.md` aggiornato con istruzioni I/O
- [ ] Esempi file `.dmp` commentati

### Testing

- [ ] Tutti i test funzionali passati
- [ ] Test performance verificati
- [ ] Gestione errori testata manualmente

---

## Definition of Done

Uno story è considerato completato quando:

1. [x] **Codice implementato** secondo criteri di accettazione
2. [x] **Compilazione senza errori/warning**
3. [x] **Javadoc presente** per metodi pubblici
4. [x] **Gestione eccezioni** implementata
5. [x] **Test manuali eseguiti** con successo
6. [x] **Documentazione aggiornata**
7. [x] **Codice reviewed** (self-review per progetto accademico)

---

## Rischi e Mitigazioni

### Rischio 1: Parsing CSV Complesso

**Probabilità:** Media
**Impatto:** Alto

**Mitigazione:**

- Iniziare con parser semplice (split su virgola)
- Gestire casi comuni (virgole in stringhe) in iterazioni successive
- Usare librerie se necessario (es. OpenCSV) - ma non per questo sprint

---

### Rischio 2: File .dmp Non Retrocompatibili

**Probabilità:** Bassa
**Impatto:** Medio

**Mitigazione:**

- Includere numero versione formato in header
- Documentare formato in CLAUDE.md
- Pianificare migrazione formato in sprint futuri

---

### Rischio 3: Performance con Dataset Grandi

**Probabilità:** Media
**Impatto:** Medio

**Mitigazione:**

- Testare con dataset 1000+ righe
- Profilare codice se necessario
- Ottimizzazioni rimandate a Sprint 3 se non critiche

---

## Note Implementative

### Formato File .dmp

**Vantaggi formato testuale:**

- Leggibile da umani
- Facile debug
- Versionabile in Git
- Modificabile con editor testo

**Alternative considerate:**

- Java Serialization: non human-readable
- JSON: richiede libreria esterna
- XML: troppo verboso

**Decisione:** Formato custom testuale per semplicità

---

### Inferenza Tipo Attributi

**Logica:**

```java
boolean isContinuous(List<String> values) {
    for (String v : values) {
        try {
            Double.parseDouble(v);
        } catch (NumberFormatException e) {
            return false;  // Trovato non-numero → discrete
        }
    }
    return true;  // Tutti numeri → continuous
}
```

**Limitazioni:**

- Attributo con valori `{"1", "2", "3"}` sarà continuous, non discrete
- Soluzione futura: permettere specifica esplicita tipi in header CSV

---

## Metriche di Successo Sprint

**Obiettivo sprint raggiunto se:**

- [x] Possibile salvare/caricare cluster da file
- [x] Possibile caricare dataset CSV esterni
- [x] Almeno 2 dataset CSV di esempio funzionanti
- [x] Gestione errori robusta (nessun crash)
- [x] Tutti i test funzionali passati

**Metriche quantitative:**

- Code coverage metodi pubblici: > 80%
- Tempo caricamento CSV (100 righe): < 500ms
- Zero NullPointerException in esecuzione normale

---

## Riferimenti

### Java I/O API

- `java.io.FileReader` / `FileWriter`
- `java.io.BufferedReader` / `BufferedWriter`
- `java.io.IOException`
- `java.nio.file.Files` (opzionale per operazioni moderne)

### Best Practices

- Try-with-resources per chiusura automatica stream
- Buffering per performance
- Validazione input prima di processare
- Messaggi errore informativi

---

## Prossimi Passi (Sprint 3)

Funzionalità pianificate per sprint successivo:

- Ottimizzazione performance algoritmo QT
- Supporto attributi continuous nell'algoritmo
- Unit testing con JUnit
- Metriche qualità clustering

---

**Fine Sprint 2 Documentation**

**Versione:** 1.0
**Data Creazione:** 2025-11-07
**Autore:** Claude AI Assistant
**Status:** 📝 Pianificato → 🚧 In Sviluppo
