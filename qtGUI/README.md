# qtGUI - Interfaccia Grafica per Quality Threshold Clustering

Interfaccia grafica JavaFX per l'algoritmo Quality Threshold Clustering.

## Panoramica

qtGUI è un'applicazione desktop moderna e user-friendly che fornisce un'interfaccia grafica per eseguire e visualizzare operazioni di clustering Quality Threshold. Costruita con JavaFX 21, offre un flusso di lavoro intuitivo dalla selezione del dataset alla visualizzazione dei risultati.

## Funzionalità

### Implementate (Sprint 0 e 1)

- Interfaccia utente moderna basata su JavaFX
- Sistema di navigazione multi-vista (Home, Clustering, Risultati, Impostazioni)
- Selezione dataset (Hardcoded, CSV, Database)
- Configurazione parametri di clustering con validazione
- Feedback di progresso in tempo reale durante il clustering
- Visualizzazione risultati con vista ad albero dei cluster
- Impostazioni applicazione configurabili
- UI responsiva e rifinita con stile CSS personalizzato

### Pianificate (Sprint Futuri)

- Integrazione backend con qtServer (Sprint 2)
- Visualizzazione scatter plot 2D (Sprint 3)
- Funzionalità di esportazione (CSV, PDF, PNG) (Sprint 4)
- Salvataggio/Caricamento risultati clustering (file .dmp) (Sprint 4)
- Dashboard statistiche (Sprint 4)
- Visualizzazione 3D (Sprint 4 - opzionale)

## Requisiti

- Java 21 o superiore
- Maven 3.9+
- JavaFX 21+
- Connessione internet (per scaricare le dipendenze Maven)

## Struttura del Progetto

```
qtGUI/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── gui/
│   │   │   │   ├── MainApp.java              # Punto di ingresso applicazione
│   │   │   │   ├── controllers/
│   │   │   │   │   ├── MainController.java   # Controller finestra principale
│   │   │   │   │   ├── HomeController.java   # Selezione dataset
│   │   │   │   │   ├── ClusteringController.java  # Feedback progresso
│   │   │   │   │   ├── ResultsController.java     # Visualizzazione risultati
│   │   │   │   │   └── SettingsController.java    # Configurazione
│   │   │   │   ├── models/                   # Modelli dati (futuro)
│   │   │   │   ├── services/                 # Logica business (futuro)
│   │   │   │   ├── charts/                   # Visualizzazione (futuro)
│   │   │   │   └── utils/                    # Utilità (futuro)
│   │   │   └── module-info.java              # Descrittore modulo Java
│   │   └── resources/
│   │       ├── views/
│   │       │   ├── main.fxml                 # Layout principale
│   │       │   ├── home.fxml                 # Vista home
│   │       │   ├── clustering.fxml           # Vista clustering
│   │       │   ├── results.fxml              # Vista risultati
│   │       │   └── settings.fxml             # Vista impostazioni
│   │       ├── styles/
│   │       │   └── application.css           # Foglio di stile applicazione
│   │       └── icons/                        # Icone (futuro)
│   └── test/
│       └── java/                             # Test unitari (futuro)
├── pom.xml                                   # Configurazione Maven
└── README.md                                 # Questo file
```

## Compilazione ed Esecuzione

### Prerequisiti

Assicurarsi che Maven e Java 21 siano installati:

```bash
java --version
# Dovrebbe mostrare Java 21 o superiore

mvn --version
# Dovrebbe mostrare Maven 3.9+
```

### Compilare il Progetto

```bash
cd qtGUI
mvn clean compile
```

### Eseguire l'Applicazione

```bash
mvn javafx:run
```

### Pacchettizzare come JAR

```bash
mvn clean package
```

Questo crea un JAR eseguibile in `target/qtGUI-1.0.0.jar`.

### Eseguire il JAR

```bash
java -jar target/qtGUI-1.0.0.jar
```

## Utilizzo

### 1. Vista Home - Selezione Dataset

- Selezionare sorgente dati: Hardcoded (PlayTennis), File CSV o Database
- Configurare raggio di clustering (es. 0.5)
- Abilitare/disabilitare caching distanze
- Cliccare "Avvia Clustering" per iniziare

### 2. Vista Clustering - Monitoraggio Progresso

- Barra di progresso in tempo reale
- Informazioni sul passo corrente
- Contatore cluster trovati
- Contatore tuple clusterizzate
- Tracciamento tempo trascorso
- Log attività con progresso dettagliato

### 3. Vista Risultati - Analisi Cluster

- Vista ad albero con tutti i cluster
- Dettagli cluster (centroide, dimensione, distanza media)
- Elenco tuple per ogni cluster
- Statistiche per ogni cluster
- Opzioni esportazione e salvataggio (futuro)

### 4. Vista Impostazioni - Configurazione

- Aspetto (tema, dimensione font)
- Prestazioni (caching, thread, memoria)
- Impostazioni predefinite clustering (raggio, sorgente dati)
- Impostazioni esportazione (formato, directory)
- Configurazione database

## Navigazione

- **Menu File**:
  - Nuova Analisi: Avvia una nuova sessione di clustering
  - Apri: Carica clustering salvato (futuro)
  - Salva / Salva con Nome: Salva risultati clustering (futuro)
  - Esci: Chiudi applicazione

- **Menu Modifica**:
  - Impostazioni: Configura preferenze applicazione

- **Menu Visualizza**:
  - Mostra/Nascondi Barra degli Strumenti
  - Mostra/Nascondi Barra di Stato

- **Menu Aiuto**:
  - Documentazione
  - Informazioni

## Scorciatoie da Tastiera

- `Ctrl+N`: Nuova Analisi
- `Ctrl+O`: Apri
- `Ctrl+S`: Salva
- `Ctrl+E`: Esporta (futuro)
- `F5`: Aggiorna (futuro)

## Configurazione

Le impostazioni sono salvate in `qtgui.properties` nella directory dell'applicazione.

Valori predefiniti:

- Tema: Chiaro
- Dimensione Font: Media (14px)
- Raggio: 0.5
- Caching: Abilitato
- Dimensione Pool Thread: 4
- Limite Memoria: 512 MB

## Dipendenze

- JavaFX 21.0.1 (Controls, FXML)
- XChart 3.8.5 (Grafici - uso futuro)
- ControlsFX 11.2.0 (Miglioramenti UI)
- SLF4J 2.0.9 + Logback 1.4.14 (Logging)
- JUnit 5.10.1 + TestFX 4.0.18 (Testing - futuro)

## Stato di Sviluppo

### Completato

- [x] Sprint 0: Setup e configurazione
  - [x] Struttura progetto Maven
  - [x] Dipendenze JavaFX
  - [x] Configurazione modulo (module-info.java)
  - [x] Struttura directory

- [x] Sprint 1: Base UI e Navigazione
  - [x] Layout finestra principale (MenuBar, ToolBar, StatusBar)
  - [x] Vista home con selezione dataset
  - [x] Vista clustering con feedback progresso
  - [x] Vista risultati con albero cluster
  - [x] Vista impostazioni con configurazione
  - [x] Sistema di navigazione
  - [x] Stile CSS

### In Corso / Pianificato

- [ ] Sprint 2: Integrazione Backend
  - [ ] Wrapper ClusteringService per QTMiner
  - [ ] DataImportService (CSV, Database)
  - [ ] JavaFX Task per clustering asincrono
  - [ ] Gestione errori e logging

- [ ] Sprint 3: Visualizzazione 2D
  - [ ] Implementazione scatter plot
  - [ ] PCA per riduzione dimensionalità
  - [ ] Grafico interattivo (zoom, pan, tooltip)
  - [ ] Esportazione grafico (PNG, SVG)

- [ ] Sprint 4: Funzionalità Avanzate
  - [ ] Salva/Carica clustering (file .dmp)
  - [ ] Esporta risultati (CSV, PDF)
  - [ ] Dashboard statistiche
  - [ ] Confronto cluster
  - [ ] Visualizzazione 3D (opzionale)
  - [ ] Tema modalità scura

- [ ] Sprint 5: Testing e Deployment
  - [ ] Test unitari
  - [ ] Test di integrazione
  - [ ] Testing cross-platform
  - [ ] Installer nativi (jpackage)

## Problemi Noti

- La compilazione Maven richiede connessione internet per scaricare le dipendenze
- La funzionalità di clustering è attualmente simulata (Sprint 1)
- Integrazione backend con qtServer in sospeso (Sprint 2)
- Visualizzazione grafico in sospeso (Sprint 3)

## Risoluzione Problemi

### Maven Non Può Scaricare le Dipendenze

Se vedi errori di rete durante `mvn compile`:

- Assicurati di avere connettività internet
- Controlla le impostazioni firewall/proxy
- Verifica l'accesso al repository Maven: <https://repo.maven.apache.org/maven2>

### Errore Runtime JavaFX

Se vedi "Error: JavaFX runtime components are missing":

- Assicurati che le dipendenze JavaFX siano configurate correttamente in pom.xml
- Esegui tramite `mvn javafx:run` invece del comando `java` diretto
- Verifica che sia utilizzato Java 21+

### Caricamento FXML Fallito

Se le viste non si caricano:

- Controlla che i file FXML siano in `src/main/resources/views/`
- Verifica che i nomi delle classi controller negli FXML corrispondano alle classi effettive
- Controlla le istruzioni exports e opens in module-info.java

## Contributi

Questo è un progetto accademico per il corso MAP (Metodi Avanzati di Programmazione).

### Stile del Codice

- Seguire le convenzioni di nomenclatura Java
- Usare Javadoc per i metodi pubblici
- Mantenere i controller focalizzati sulla logica UI
- Separare la logica business nei servizi

### Messaggi di Commit

- Usare messaggi di commit chiari e descrittivi
- Riferire numeri di sprint e task
- Esempio: "Sprint 1.3: Aggiungi vista home con selezione dataset"

## Licenza

Progetto accademico per il corso MAP.

## Autori

- Sviluppato con l'assistenza di Claude (AI Assistant)
- Corso: Metodi Avanzati di Programmazione (MAP)
- Anno: 2025

## Cronologia Versioni

- **v1.0.0** (2025-11-08): Sprint 0 e 1 completati
  - Setup iniziale JavaFX
  - Base UI completa con tutte le viste
  - Sistema di navigazione implementato

## Supporto

Per problemi relativi a:

- Integrazione qtServer: Vedere `../qtServer/README.md`
- Setup generale progetto: Vedere `../README.md`
- Roadmap e pianificazione: Vedere `../QTGUI_ROADMAP.md`

## Prossimi Passi

1. Completare Sprint 2: Integrare con backend qtServer
2. Implementare ClusteringService per eseguire effettivamente QTMiner
3. Aggiungere DataImportService per caricamento CSV e Database
4. Testare workflow di clustering end-to-end

---

**Ultimo Aggiornamento**: 2025-11-08
**Sprint Corrente**: 1 (Completato)
**Prossimo Sprint**: 2 (Integrazione Backend)
