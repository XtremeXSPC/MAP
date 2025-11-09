# 4. Interfaccia Testuale (TUI)

## 4.1 Avvio Client CLI

### Prerequisiti

- Server qtServer avviato e in ascolto su porta 8080
- Database MySQL configurato (per opzioni 0 e 1)

### Comando Avvio

```bash
cd MAP/qtClient/bin
java qtClient.MainTest
```

### Output Iniziale

```
===============================================
  QT CLUSTERING CLIENT v1.0
===============================================

Connecting to server: localhost:8080
Connection established!

===============================================
           MENU OPERAZIONI
===============================================

0) Carica dati da database
1) Esegui clustering da database
2) Salva risultati clustering su file
3) Carica clustering da file ed esegui
4) Esci

===============================================
Scegli un'opzione [0-4]:
```

<!-- [IMMAGINE]: Esempio output CLI client - dimensione: 800x400 -->
<!-- Mostra: menu testuale con le 5 opzioni numerate -->

---

## 4.2 Menu Comandi

### Opzione 0: Carica Dati da Database

#### Funzionalità

Carica un dataset da una tabella MySQL nel server.

#### Procedura

```
Scegli un'opzione [0-4]: 0

Inserisci il nome della tabella: playtennis
```

#### Output Successo

```
Caricamento dataset in corso...

OK - Dataset caricato con successo!

Dataset Information:
--------------------
Table: playtennis
Tuples: 14
Attributes: 5

Data Preview:
Outlook,Temperature,Humidity,Wind,PlayTennis
1:sunny,hot,high,weak,no,
2:sunny,hot,high,strong,no,
3:overcast,hot,high,weak,yes,
4:rain,mild,high,weak,yes,
...
14:rain,mild,high,strong,no,

===============================================
```

#### Output Errore

```
ERROR: Table 'playtennis' doesn't exist in database 'qtclustering'

Possibili cause:
- Nome tabella errato (case-sensitive!)
- Database non configurato
- Connessione database fallita

Ritorno al menu principale...
```

#### Note

- **Prerequisito**: Database deve essere avviato e accessibile
- **Persistenza**: Dati restano in memoria server fino a nuovo caricamento
- **Uso tipico**: Eseguire una volta, poi opzione 1 più volte con radius diversi

---

### Opzione 1: Esegui Clustering da Database

#### Funzionalità

Esegue algoritmo QT sui dati precedentemente caricati con opzione 0.

#### Prerequisito

**IMPORTANTE**: Devi prima eseguire opzione 0 per caricare dataset!

#### Procedura

```
Scegli un'opzione [0-4]: 1

Inserisci il valore di radius [0.0 - 1.0]: 0.0
```

#### Output Successo

```
Esecuzione clustering in corso...
Radius: 0.0

OK - Clustering completato!

===============================================
         RISULTATI CLUSTERING
===============================================

Numero di cluster scoperti: 11

-----------------------------------------------
Cluster 0:
  Centroid=(sunny hot high weak no )
  Numero tuple: 3

  Examples:
  [sunny hot high weak no ] dist=0.0
  [sunny hot high strong no ] dist=0.2
  [sunny mild high weak no ] dist=0.2

  AvgDistance: 0.13333333333333333

-----------------------------------------------
Cluster 1:
  Centroid=(overcast hot high weak yes )
  Numero tuple: 1

  Examples:
  [overcast hot high weak yes ] dist=0.0

  AvgDistance: 0.0

-----------------------------------------------
Cluster 2:
  Centroid=(rain mild high weak yes )
  Numero tuple: 2

  Examples:
  [rain mild high weak yes ] dist=0.0
  [rain mild high strong no ] dist=0.4

  AvgDistance: 0.2

-----------------------------------------------
...

===============================================
Statistiche Globali:
  - Cluster totali: 11
  - Tuple clusterizzate: 14
  - Radius utilizzato: 0.0
  - Avg cluster size: 1.27
  - Max cluster size: 3
  - Min cluster size: 1
===============================================
```

<!-- [IMMAGINE]: Esempio sessione completa TUI - dimensione: 800x600 -->
<!-- Mostra: output completo con tutti gli 11 cluster del playtennis con radius=0.0 -->

#### Interpretazione Output

Per ogni cluster viene mostrato:

| Campo | Significato | Esempio |
|-------|-------------|---------|
| **Centroid** | Tupla centrale del cluster | (sunny hot high weak no) |
| **Numero tuple** | Dimensione cluster | 3 |
| **Examples** | Tutte le tuple nel cluster | Lista con distanze |
| **dist** | Distanza da centroide | 0.0 = identica, 0.2 = 1 attr diverso su 5 |
| **AvgDistance** | Distanza media nel cluster | Indica compattezza |

#### Test con Diversi Radius

**Radius = 0.0** (clustering preciso):
```
Numero cluster: 11 (molti cluster piccoli)
Avg cluster size: 1.27
Max AvgDistance: 0.2
```

**Radius = 0.5** (bilanciato):
```
Numero cluster: 5-7 (cluster medi)
Avg cluster size: 2-3
Max AvgDistance: 0.4
```

**Radius = 1.0** (aggregato):
```
Numero cluster: 2-3 (pochi cluster grandi)
Avg cluster size: 5-7
Max AvgDistance: 0.8
```

#### Output Errore

```
ERROR: No data loaded. Please use option 0 first.

Devi prima caricare un dataset!
1) Torna al menu
2) Esegui opzione 0 ora
```

---

### Opzione 2: Salva Risultati su File

#### Funzionalità

Serializza risultati clustering su file `.dat` per riutilizzo successivo.

#### Prerequisito

Clustering deve essere stato eseguito con opzione 1.

#### Procedura

```
Scegli un'opzione [0-4]: 2

Inserisci il nome del file (senza estensione): results_radius0
```

**Nota**: Estensione `.dat` viene aggiunta automaticamente.

#### Output Successo

```
Salvataggio in corso...

OK - Clustering salvato con successo!

File: results_radius0.dat
Dimensione: 2.4 KB
Percorso: /path/to/qtServer/bin/results_radius0.dat

Puoi ricaricare questo file con opzione 3.
```

#### Formato File

File `.dat` contiene serializzazione Java di oggetto `ClusterSet`:

```java
// Contenuto (binario, non leggibile come testo)
ObjectOutputStream serialization of:
- ClusterSet object
- Array of Cluster objects
- Each Cluster contains:
  - Centroid (Tuple)
  - ArraySet of tuple indices
```

#### Gestione File

```bash
# Verifica file creato
ls -lh results_radius0.dat

# Output esempio
-rw-r--r-- 1 user user 2.4K Nov 9 14:30 results_radius0.dat
```

#### Output Errore

```
ERROR: No clustering to save. Execute option 1 first.

oppure

ERROR: Cannot write file. Permission denied.
```

#### Casi d'Uso

- **Backup**: Salvare risultati per analisi futura
- **Condivisione**: Inviare file a colleghi
- **Batch**: Salvare clustering con radius diversi
  - `results_r0.0.dat`
  - `results_r0.5.dat`
  - `results_r1.0.dat`

---

### Opzione 3: Carica Clustering da File

#### Funzionalità

Carica e visualizza clustering salvato precedentemente con opzione 2.

#### Procedura

```
Scegli un'opzione [0-4]: 3

Inserisci il valore di radius [0.0 - 1.0]: 0.0
Inserisci il nome del file (senza estensione): results_radius0
```

**Nota**: Radius è richiesto ma ignorato (valore già salvato nel file).

#### Output Successo

```
Caricamento file in corso...

OK - Clustering caricato con successo!

File: results_radius0.dat

===============================================
         RISULTATI CLUSTERING (da file)
===============================================

Numero di cluster scoperti: 11

[... output identico a opzione 1 ...]

===============================================
```

#### Output Errore

```
ERROR: File not found: results_radius0.dat

Verifica:
- Nome file corretto
- File nella directory bin/
- Estensione .dat non inserita manualmente
```

oppure

```
ERROR: Invalid file format. File corrupted or not a valid clustering file.
```

#### Differenza tra Opzione 1 e 3

| Aspetto | Opzione 1 | Opzione 3 |
|---------|-----------|-----------|
| Sorgente | Database → Clustering nuovo | File → Clustering salvato |
| Tempo | Lento (esegue algoritmo) | Veloce (legge file) |
| Requisito | Database + dati caricati | File `.dat` esistente |
| Modificabile | SI (cambia radius) | NO (radius fissato) |

---

### Opzione 4: Esci

```
Scegli un'opzione [0-4]: 4

Chiusura connessione al server...
Arrivederci!

Connection closed.
```

Termina client e chiude socket.

---

## 4.3 Esempi Sessioni Complete

### Sessione 1: Clustering Rapido con PlayTennis

**Obiettivo**: Testare clustering su dataset esempio

```bash
$ java qtClient.MainTest

Scegli un'opzione [0-4]: 0
Inserisci il nome della tabella: playtennis
OK - Dataset caricato con successo!

Scegli un'opzione [0-4]: 1
Inserisci il valore di radius: 0.0
OK - Clustering completato!
Numero di cluster scoperti: 11

Scegli un'opzione [0-4]: 2
Inserisci il nome del file: playtennis_r0
OK - Clustering salvato con successo!

Scegli un'opzione [0-4]: 4
Arrivederci!
```

**Tempo totale**: ~30 secondi

---

### Sessione 2: Confronto Radius Diversi

**Obiettivo**: Analizzare impatto radius su numero cluster

```bash
$ java qtClient.MainTest

# Carica dati una volta
Opzione: 0
Tabella: playtennis
OK - Dataset caricato!

# Test radius 0.0
Opzione: 1
Radius: 0.0
Risultato: 11 cluster
Opzione: 2
File: playtennis_r0.0
OK - Salvato!

# Test radius 0.5 (NOTA: dati già in memoria!)
Opzione: 1
Radius: 0.5
Risultato: 5 cluster
Opzione: 2
File: playtennis_r0.5
OK - Salvato!

# Test radius 1.0
Opzione: 1
Radius: 1.0
Risultato: 2 cluster
Opzione: 2
File: playtennis_r1.0
OK - Salvato!

Opzione: 4
```

**Risultati**:
| Radius | Cluster | Avg Size | Avg Distance |
|--------|---------|----------|--------------|
| 0.0 | 11 | 1.27 | 0.05 |
| 0.5 | 5 | 2.80 | 0.31 |
| 1.0 | 2 | 7.00 | 0.65 |

**Tempo totale**: ~2 minuti

---

### Sessione 3: Analisi File Salvati

**Obiettivo**: Caricare e confrontare clustering salvati

```bash
$ java qtClient.MainTest

# Carica primo file
Opzione: 3
Radius: 0.0  # (ignorato)
File: playtennis_r0.0
Output: 11 cluster visualizzati

# Carica secondo file
Opzione: 3
Radius: 0.5
File: playtennis_r0.5
Output: 5 cluster visualizzati

# Confronto visivo
# Osservazione: cluster più grandi con radius maggiore

Opzione: 4
```

**Tempo totale**: ~1 minuto

---

## 4.4 Tips & Tricks TUI

### Redirect Output su File

Salva output sessione per analisi successiva:

```bash
java qtClient.MainTest > session_output.txt 2>&1
```

### Script Batch (Automatizzazione)

Crea file `commands.txt`:
```
0
playtennis
1
0.0
2
playtennis_r0
4
```

Esegui:
```bash
java qtClient.MainTest < commands.txt > results.txt
```

### Verifica Server Prima di Iniziare

```bash
# Testa connessione server
nc -zv localhost 8080

# Output atteso
Connection to localhost 8080 port [tcp/*] succeeded!
```

### Confronto Rapido Metriche

```bash
# Estrai solo statistiche
java qtClient.MainTest < commands.txt | grep "Numero di cluster\|AvgDistance"
```

---

### Limitazioni TUI vs GUI

| Funzionalità | TUI | GUI |
|--------------|-----|-----|
| Visualizzazione grafica | NO | SI (scatter chart) |
| Export CSV/JSON/PNG | NO | SI |
| Caricamento CSV | NO | SI |
| Anteprima dataset | Limitata | Completa (TableView) |
| Configurazione avanzata | NO | SI |
| Salvataggio file | SI (.dat) | SI (multipli formati) |
| Velocità | Alta | Media |
| Curva apprendimento | Bassa | Media |

**Raccomandazione**: Usa TUI per batch e automazione, GUI per analisi esplorativa.

---

[← Capitolo 3: GUI](03_gui.md) | [Capitolo 5: Workflow Comuni →](05_workflow.md)
