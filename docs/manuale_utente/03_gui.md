# 3. Interfaccia Grafica (GUI)

## 3.1 Panoramica Interfaccia

La GUI è costruita con JavaFX e segue il pattern MVC (Model-View-Controller).

### Avvio Applicazione GUI

```bash
cd MAP/qtGUI/bin
java application.Main
```

<!-- [IMMAGINE]: Screenshot schermata principale GUI - dimensione: 1200x800 -->
<!-- Mostra: finestra completa con barra laterale sinistra contenente bottoni Home/Clustering/Results/Settings -->

### Layout Generale

```
┌──────────────────────────────────────────────────┐
│  Quality Threshold Clustering           [- □ X]  │
├──────────┬───────────────────────────────────────┤
│          │                                       │
│  Home    │                                       │
│          │                                       │
│ Cluster  │     AREA CONTENUTO PRINCIPALE         │
│          │       (cambia per schermata)          │
│ Results  │                                       │
│          │                                       │
│ Settings │                                       │
│          │                                       │
├──────────┴───────────────────────────────────────┤
│  Status: Ready                      v1.0         │
└──────────────────────────────────────────────────┘
```

### Barra Laterale (Sidebar)

| Bottone        | Funzione                  | Icona       |
| -------------- | ------------------------- | ----------- |
| **Home**       | Caricamento dataset       | Cartella    |
| **Clustering** | Configurazione e avvio    | Grafico     |
| **Results**    | Visualizzazione risultati | Tabella     |
| **Settings**   | Configurazione app        | Ingranaggio |

### Barra di Stato (Status Bar)

- **Sinistra**: Messaggi stato operazioni
- **Destra**: Versione applicazione

---

## 3.2 Schermata Home

La schermata Home permette di caricare dataset da diverse sorgenti.

<!-- [IMMAGINE]: Home screen con dataset selector - dimensione: 800x600 -->
<!-- Mostra: tre card con bottoni "Load CSV", "Load from Database", "Load PlayTennis Example" -->

### Caricamento da File CSV

#### Passo 1: Click "Load CSV File"

Si apre dialog selezione file.

#### Passo 2: Seleziona File CSV

Formato CSV richiesto:
```csv
attribute1,attribute2,attribute3
value1,value2,value3
value4,value5,value6
...
```

**Esempio** (`iris.csv`):
```csv
sepal_length,sepal_width,petal_length,petal_width,species
5.1,3.5,1.4,0.2,setosa
4.9,3.0,1.4,0.2,setosa
7.0,3.2,4.7,1.4,versicolor
...
```

<!-- [IMMAGINE]: Esempio file CSV input - dimensione: 600x300 -->
<!-- Mostra: contenuto file CSV con header evidenziato -->

#### Passo 3: Verifica Caricamento

Dopo caricamento appare:
- **Messaggio**: "Dataset loaded: N tuples, M attributes"
- **Preview**: Tabella con prime 10 righe

<!-- [IMMAGINE]: Dataset preview dialog - dimensione: 800x600 -->
<!-- Mostra: TableView con dati CSV, colonne scrollabili -->

#### Gestione Errori CSV

| Errore                 | Causa                     | Soluzione                     |
| ---------------------- | ------------------------- | ----------------------------- |
| "Invalid CSV format"   | Header mancante           | Aggiungi riga intestazione    |
| "Inconsistent columns" | Righe con colonne diverse | Uniforma numero colonne       |
| "File too large"       | File > 10MB               | Riduci dataset o usa database |

### Caricamento da Database

#### Passo 1: Click "Load from Database"

Si apre dialog configurazione connessione.

<!-- [IMMAGINE]: Database connection dialog - dimensione: 700x500 -->
<!-- Mostra: form con campi Host, Port, Database, User, Password, Table -->

#### Passo 2: Inserisci Credenziali

| Campo        | Esempio      | Descrizione                       |
| ------------ | ------------ | --------------------------------- |
| **DB Type**  | MySQL        | Tipo database (MySQL, PostgreSQL) |
| **Host**     | localhost    | Indirizzo server database         |
| **Port**     | 3306         | Porta MySQL (default 3306)        |
| **Database** | qtclustering | Nome database                     |
| **Username** | root         | Utente database                   |
| **Password** | •••••        | Password utente                   |
| **Table**    | playtennis   | Nome tabella da caricare          |

#### Passo 3: Test Connection

Click "Test Connection" per verificare credenziali.

Output:
- **Successo**: "Connection successful!"
- **Errore**: "Connection failed: <dettaglio errore>"

#### Passo 4: Load Data

Click "Load Data" per importare tabella.

**Processo**:
1. Connessione a database
2. Estrazione schema (metadati colonne)
3. Lettura dati (SELECT * FROM table)
4. Inferenza tipi attributi (discrete/continuous)
5. Costruzione oggetto Data

**Tempo stimato**: 2-10 secondi (dipende da dimensione tabella)

#### Gestione Errori Database

| Errore               | Causa                    | Soluzione                       |
| -------------------- | ------------------------ | ------------------------------- |
| "Access denied"      | Credenziali errate       | Verifica user/password          |
| "Unknown database"   | Database inesistente     | Crea database prima             |
| "Table not found"    | Nome tabella errato      | Verifica nome (case-sensitive!) |
| "Connection timeout" | Server non raggiungibile | Verifica host e firewall        |

### Caricamento Dataset Esempio (PlayTennis)

#### Uso

Click "Load PlayTennis Example" per caricare dataset embedded.

**Caratteristiche**:
- **Tuple**: 14
- **Attributi**: 5 (tutti discreti)
- **Dominio**: Meteo e decisione giocare tennis
- **Tempo caricamento**: Istantaneo (< 1 secondo)

**Attributi**:
- `outlook`: {sunny, overcast, rain}
- `temperature`: {hot, mild, cool}
- `humidity`: {high, normal}
- `wind`: {weak, strong}
- `playtennis`: {yes, no}

#### Quando Usare

- Demo e tutorial
- Test rapido funzionalità
- Apprendimento algoritmo
- Verifica installazione

### Anteprima Dataset

Dopo caricamento (da qualsiasi sorgente), è disponibile "Preview Dataset".

#### Funzionalità Anteprima

| Elemento      | Descrizione                     |
| ------------- | ------------------------------- |
| **TableView** | Griglia dati interattiva        |
| **Colonne**   | Nome attributo + tipo (D/C)     |
| **Righe**     | Max 100 tuple (per performance) |
| **Scroll**    | Orizzontale e verticale         |
| **Sort**      | Click su header colonna         |
| **Filtro**    | Casella ricerca testuale        |

<!-- [IMMAGINE]: Dataset preview dialog - dimensione: 800x600 -->
<!-- Mostra: TableView con colonne "Outlook", "Temperature", etc. e dati -->

#### Statistiche Dataset

Pannello statistiche mostra:
- Numero tuple totali
- Numero attributi
- Tipi attributi (N discreti, M continui)
- Valori distinti per attributo discreto
- Range [min, max] per attributo continuo

---

## 3.3 Schermata Clustering

Schermata per configurare parametri e avviare clustering.

<!-- [IMMAGINE]: Clustering configuration panel - dimensione: 800x600 -->
<!-- Mostra: slider radius, info box, bottone "Start Clustering", progress bar -->

### Configurazione Radius

#### Slider Radius

| Parametro   | Valore | Comportamento                       |
| ----------- | ------ | ----------------------------------- |
| **Min**     | 0.0    | Cluster molto piccoli (max qualità) |
| **Max**     | 1.0    | Cluster molto grandi (min qualità)  |
| **Default** | 0.5    | Bilanciato                          |
| **Step**    | 0.01   | Precisione 2 decimali               |

#### Interpretazione Radius

```
Radius 0.0 → Cluster identici (distanza = 0)
           → Molti cluster piccoli (1-3 tuple)
           → Alta qualità, bassa copertura

Radius 0.5 → Cluster moderati
           → Numero medio cluster (5-10)
           → Bilanciamento qualità/copertura

Radius 1.0 → Cluster aggregati
           → Pochi cluster grandi
           → Bassa qualità, alta copertura
```

#### Info Box Radius

Pannello informativo mostra:
- **Valore corrente**: es. "0.35"
- **Stima cluster**: "5-8 cluster attesi" (euristica)
- **Tempo stimato**: basato su dimensione dataset
- **Raccomandazione**: suggerimento basato su dataset

### Avvio Clustering

#### Passo 1: Verifica Prerequisiti

Prima di "Start Clustering", verifica:
- [ ] Dataset caricato (indicatore verde)
- [ ] Radius impostato (valore visibile)
- [ ] Server connesso (status bar "Connected")

#### Passo 2: Click "Start Clustering"

**Processo**:
1. Validazione configurazione
2. Connessione a server (se non già connesso)
3. Invio dataset e radius
4. Esecuzione algoritmo QT (server-side)
5. Ricezione risultati
6. Navigazione automatica a schermata Results

#### Progress Indicator

Durante esecuzione:
- **Progress Bar**: Indeterminata (algoritmo non prevedibile)
- **Status**: "Clustering in progress..."
- **Tempo trascorso**: Timer in secondi
- **Cancellazione**: Bottone "Cancel" disponibile

<!-- [IMMAGINE]: Clustering in progress - dimensione: 600x200 -->
<!-- Mostra: progress bar animata, timer, bottone cancel -->

#### Tempi di Esecuzione Tipici

| Dataset Size          | Radius | Tempo Atteso  |
| --------------------- | ------ | ------------- |
| 14 tuple (PlayTennis) | 0.0    | < 1 secondo   |
| 100 tuple             | 0.5    | 2-5 secondi   |
| 1000 tuple            | 0.5    | 30-60 secondi |
| 5000 tuple            | 0.5    | 5-10 minuti   |

**Nota**: Complessità O(k × n²) rende algoritmo lento per dataset grandi (> 10000 tuple).

### Configurazioni Avanzate (Opzionale)

Espandi pannello "Advanced Options" per:

| Opzione              | Default | Descrizione                          |
| -------------------- | ------- | ------------------------------------ |
| **Distance Metric**  | Hamming | Metrica distanza (Hamming/Euclidean) |
| **Enable Cache**     | ON      | Cache distanze calcolate (speedup)   |
| **Thread Pool Size** | 4       | Thread paralleli (se supportato)     |
| **Timeout**          | 300s    | Timeout massimo esecuzione           |

### Gestione Errori Clustering

| Errore                 | Causa                 | Soluzione                         |
| ---------------------- | --------------------- | --------------------------------- |
| "No dataset loaded"    | Dataset non caricato  | Carica prima dataset              |
| "Server not reachable" | Server offline        | Avvia server                      |
| "Clustering timeout"   | Dataset troppo grande | Riduci dataset o aumenta timeout  |
| "Out of memory"        | RAM insufficiente     | Aumenta heap JVM o riduci dataset |

---

[Continua nella prossima parte...]

[← Capitolo 2: Installazione](02_installazione.md) | [Continua Capitolo 3 →](03_gui_part2.md)
