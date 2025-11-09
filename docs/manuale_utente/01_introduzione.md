# 1. Introduzione

## 1.1 Cos'è il Quality Threshold Clustering?

Il **Quality Threshold (QT) Clustering** è un algoritmo di clustering che garantisce una qualità minima dei cluster basata su un parametro di soglia chiamato **radius** (raggio).

### Caratteristiche Principali

- **Clustering Deterministico**: A differenza di algoritmi come K-Means, QT produce sempre gli stessi risultati con gli stessi input
- **Qualità Garantita**: Ogni cluster rispetta il vincolo di raggio massimo specificato
- **Numero Cluster Automatico**: Non richiede di specificare k (numero cluster) a priori
- **Completezza**: Tutte le tuple vengono assegnate a un cluster

### Differenza con Altri Algoritmi

| Caratteristica | QT Clustering | K-Means | DBSCAN |
|----------------|---------------|---------|--------|
| K predefinito | NO | SI | NO |
| Deterministico | SI | NO | SI |
| Qualità garantita | SI | NO | SI |
| Gestione rumore | NO | NO | SI |
| Complessità | O(k×n²) | O(k×n×i) | O(n log n) |

### Quando Usare QT

Il QT Clustering è particolarmente adatto quando:

- La qualità dei cluster è più importante del numero
- Si preferisce un approccio deterministico
- Il dataset ha dimensioni moderate (< 10000 tuple)
- Gli attributi sono principalmente categorici/discreti

---

## 1.2 Panoramica del Sistema

Il sistema è composto da 4 moduli principali:

### Architettura del Sistema

```
┌─────────────────────────────────────────────────────────┐
│                    UTENTE FINALE                        │
└────────────┬────────────────────────────┬───────────────┘
             │                            │
             │ Interfaccia                │ Interfaccia
             │ Grafica (GUI)              │ Testuale (TUI)
             ▼                            ▼
    ┌─────────────────┐          ┌──────────────────┐
    │    qtGUI        │          │    qtClient      │
    │  (JavaFX App)   │          │   (CLI App)      │
    └────────┬────────┘          └────────┬─────────┘
             │                            │
             │ Socket TCP/IP              │
             │ (Object Streams)           │
             └────────────┬───────────────┘
                          ▼
                ┌───────────────────┐
                │    qtServer       │
                │  (Multi-thread)   │
                └─────────┬─────────┘
                          │
          ┌───────────────┼───────────────┐
          │               │               │
          ▼               ▼               ▼
    ┌──────────┐   ┌──────────┐   ┌──────────┐
    │  Mining  │   │   Data   │   │ Database │
    │ (QTMiner)│   │  (I/O)   │   │  (JDBC)  │
    └──────────┘   └──────────┘   └──────────┘
```

### Moduli del Sistema

#### 1. qtServer (Core)
- **Ruolo**: Server multi-thread che esegue l'algoritmo QT
- **Componenti**:
  - `QTMiner`: Implementazione algoritmo
  - `Data`: Gestione dataset
  - `DbAccess`: Connessione database
  - `MultiServer`: Server socket
- **Porta**: 8080 (default)

#### 2. qtClient (TUI)
- **Ruolo**: Client testuale per interazione via terminale
- **Funzionalità**:
  - Caricamento dati da database
  - Esecuzione clustering remoto
  - Salvataggio/caricamento risultati
- **Input**: Keyboard interattivo

#### 3. qtGUI (GUI)
- **Ruolo**: Interfaccia grafica JavaFX
- **Funzionalità**:
  - Import dati da CSV/Database
  - Configurazione parametri clustering
  - Visualizzazione risultati con scatter chart
  - Export in formati multipli (CSV, TXT, JSON, PNG, ZIP)
  - Temi Light/Dark mode
- **Tecnologie**: JavaFX, FXML, CSS

#### 4. qtExt (Testing)
- **Ruolo**: Suite testing e benchmarking
- **Componenti**:
  - Test unitari
  - Benchmark performance
  - Dataset generator

---

## 1.3 Quick Start

### Scenario 1: Utilizzo GUI (Raccomandato per Principianti)

#### Passo 1: Avvia il Server

```bash
cd qtServer/bin
java server.MultiServer
```

Output atteso:
```
Server started on port 8080
Waiting for clients...
```

#### Passo 2: Avvia la GUI

```bash
cd qtGUI/bin
java application.Main
```

<!-- [IMMAGINE]: Screenshot schermata principale GUI - dimensione: 1200x800 -->
<!-- Mostra: finestra principale con menu laterale (Home, Clustering, Results, Settings) -->

#### Passo 3: Carica Dataset di Esempio

1. Nella schermata **Home**, click su "Load PlayTennis Example"
2. Verifica che appaia "Dataset loaded: 14 tuples, 5 attributes"

<!-- [IMMAGINE]: Home screen con dataset selector - dimensione: 800x600 -->
<!-- Mostra: bottoni "Load CSV", "Load from DB", "Load Example" e anteprima dataset -->

#### Passo 4: Configura e Avvia Clustering

1. Vai alla schermata **Clustering**
2. Imposta **Radius**: 0.0 (clustering preciso)
3. Click "Start Clustering"

<!-- [IMMAGINE]: Clustering configuration panel - dimensione: 800x600 -->
<!-- Mostra: slider radius, bottone start, progress bar -->

#### Passo 5: Visualizza Risultati

1. Attendi completamento (pochi secondi)
2. Visualizza scatter chart con cluster colorati
3. Esplora tabella dettagliata cluster

<!-- [IMMAGINE]: Results view con scatter chart - dimensione: 1200x800 -->
<!-- Mostra: scatter chart a sinistra, tabella cluster a destra -->

#### Passo 6: Esporta (Opzionale)

1. Click "Export Results"
2. Scegli formato (es. ZIP per tutto)
3. Salva file

**Tempo totale**: ~5 minuti

---

### Scenario 2: Utilizzo TUI (Avanzato)

#### Passo 1: Avvia il Server
```bash
cd qtServer/bin
java server.MultiServer
```

#### Passo 2: Avvia il Client
```bash
cd qtClient/bin
java qtClient.MainTest
```

<!-- [IMMAGINE]: Esempio output CLI client - dimensione: 800x400 -->
<!-- Mostra: menu testuale con opzioni 0-3 -->

#### Passo 3: Carica Dati da Database
```
Scegli opzione: 0
Inserisci nome tabella: playtennis
```

Output:
```
Dataset loaded successfully from database
14 tuples, 5 attributes
```

#### Passo 4: Esegui Clustering
```
Scegli opzione: 1
Inserisci radius: 0.0
```

Output:
```
Clustering completed: 11 clusters found

Cluster 1:
Centroid=(sunny hot high weak no)
Size: 3 tuples
AvgDistance: 0.133
...
```

<!-- [IMMAGINE]: Esempio sessione completa TUI - dimensione: 800x600 -->
<!-- Mostra: output completo di una sessione con tutti i cluster -->

#### Passo 5: Salva Risultati
```
Scegli opzione: 2
Inserisci nome file: results_radius0.dat
```

Output:
```
Clustering saved successfully to results_radius0.dat
```

**Tempo totale**: ~3 minuti (richiede database configurato)

---

### Prossimi Passi

Dopo aver completato il Quick Start, consulta:

- **Capitolo 3**: Per approfondire tutte le funzionalità della GUI
- **Capitolo 5**: Per workflow avanzati (CSV, database custom, batch)
- **Capitolo 8**: Per interpretare correttamente i risultati

---

### Convenzioni del Manuale

In questo manuale troverai:

- **Grassetto**: Termini chiave, nomi interfacce, bottoni
- `Monospace`: Codice, comandi, path file, output
- **[IMMAGINE]**: Placeholder per screenshot (da aggiungere)
- Tabelle per confronti e riferimenti rapidi
- Esempi pratici per ogni funzionalità

---

[Prossimo: Capitolo 2 - Installazione e Configurazione →](02_installazione.md)
