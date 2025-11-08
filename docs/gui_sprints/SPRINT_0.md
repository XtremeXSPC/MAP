# Sprint 0 GUI - Setup e Configurazione

## Obiettivo

Configurare l'ambiente di sviluppo JavaFX e creare la struttura base del progetto qtGUI con tutte le dipendenze necessarie per lo sviluppo dell'interfaccia grafica.

## Durata

3-5 ore

---

## Backlog dello Sprint

### 1. Setup Build System (Maven)

**Priorità:** Critica
**Story Points:** 3

#### Descrizione

Creare il progetto Maven per qtGUI con tutte le dipendenze JavaFX e librerie necessarie per l'applicazione grafica.

#### Criteri di Accettazione

- [x] Creare file `pom.xml` con configurazione Maven
- [x] Configurare JavaFX 21.0.1+ (Controls, FXML)
- [x] Aggiungere dipendenza XChart 3.8.5 per visualizzazioni
- [x] Aggiungere ControlsFX 11.2.0 per componenti UI avanzati
- [x] Configurare SLF4J 2.0.9 + Logback 1.4.14 per logging
- [x] Aggiungere JUnit 5.10+ e TestFX 4.0.18 per testing futuro
- [x] Configurare JavaFX Maven Plugin per esecuzione
- [x] Configurare Maven Shade Plugin per packaging JAR

#### Dettagli Implementativi

```xml
<project>
    <groupId>com.map</groupId>
    <artifactId>qtGUI</artifactId>
    <version>1.0.0</version>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <javafx.version>21.0.1</javafx.version>
    </properties>

    <dependencies>
        <!-- JavaFX, XChart, ControlsFX, Logging, Testing -->
    </dependencies>
</project>
```

**File:** `qtGUI/pom.xml`

---

### 2. Struttura Directory Progetto

**Priorità:** Critica
**Story Points:** 2

#### Descrizione

Creare la struttura completa delle directory del progetto seguendo le best practice Maven e JavaFX.

#### Criteri di Accettazione

- [x] Creare `src/main/java/gui/` per classi principali
- [x] Creare `src/main/java/gui/controllers/` per controller FXML
- [x] Creare `src/main/java/gui/models/` per modelli dati
- [x] Creare `src/main/java/gui/services/` per logica business
- [x] Creare `src/main/java/gui/charts/` per componenti visualizzazione
- [x] Creare `src/main/java/gui/utils/` per utility
- [x] Creare `src/main/resources/views/` per file FXML
- [x] Creare `src/main/resources/styles/` per CSS
- [x] Creare `src/main/resources/icons/` per icone
- [x] Creare `src/test/java/` per test unitari

#### Dettagli Implementativi

```
qtGUI/
├── src/
│   ├── main/
│   │   ├── java/gui/
│   │   │   ├── controllers/
│   │   │   ├── models/
│   │   │   ├── services/
│   │   │   ├── charts/
│   │   │   └── utils/
│   │   └── resources/
│   │       ├── views/
│   │       ├── styles/
│   │       └── icons/
│   └── test/java/
├── pom.xml
└── README.md
```

---

### 3. Configurazione Module System (JPMS)

**Priorità:** Alta
**Story Points:** 2

#### Descrizione

Configurare il Java Platform Module System (JPMS) per gestire correttamente le dipendenze JavaFX e garantire la compatibilità con Java 21.

#### Criteri di Accettazione

- [x] Creare file `module-info.java`
- [x] Dichiarare requires per JavaFX (controls, fxml)
- [x] Dichiarare requires per librerie terze (XChart, ControlsFX, logging)
- [x] Esportare package gui e sottopacchetti
- [x] Aprire package controllers a javafx.fxml per injection

#### Dettagli Implementativi

```java
module qtGUI {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.knowm.xchart;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires org.controlsfx.controls;

    exports gui;
    exports gui.controllers;
    exports gui.models;
    exports gui.services;

    opens gui.controllers to javafx.fxml;
}
```

**File:** `qtGUI/src/main/java/module-info.java`

---

### 4. Implementazione MainApp (Entry Point)

**Priorità:** Critica
**Story Points:** 3

#### Descrizione

Implementare la classe principale JavaFX che funge da punto di ingresso dell'applicazione, gestendo il ciclo di vita e il caricamento della finestra principale.

#### Criteri di Accettazione

- [x] Estendere `javafx.application.Application`
- [x] Implementare metodo `start(Stage)` per inizializzazione
- [x] Caricare layout FXML principale
- [x] Applicare foglio di stile CSS
- [x] Configurare dimensioni finestra (1200x800, min 800x600)
- [x] Implementare metodo `stop()` per pulizia risorse
- [x] Aggiungere logging per eventi applicazione
- [x] Gestire eccezioni durante caricamento

#### Dettagli Implementativi

```java
public class MainApp extends Application {
    private static final String APP_TITLE = "QT Clustering - Quality Threshold Algorithm";
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;

    @Override
    public void start(Stage stage) {
        // Carica FXML, applica CSS, configura stage
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

**File:** `qtGUI/src/main/java/gui/MainApp.java`

---

### 5. Hello World Test

**Priorità:** Alta
**Story Points:** 1

#### Descrizione

Creare una finestra Hello World di base per verificare che l'intero setup funzioni correttamente.

#### Criteri di Accettazione

- [x] Creare FXML minimale con Label di test
- [x] Testare caricamento FXML
- [x] Testare applicazione CSS
- [x] Verificare build con `mvn clean compile`
- [x] Verificare esecuzione con `mvn javafx:run`

#### Dettagli Implementativi

```xml
<!-- main.fxml iniziale -->
<VBox xmlns:fx="http://javafx.com/fxml">
    <Label text="QT Clustering GUI - Hello World" />
</VBox>
```

---

## Review dello Sprint

### Obiettivi Raggiunti

✅ **Setup Build System**
- Progetto Maven configurato correttamente
- Tutte le dipendenze JavaFX caricate
- Plugin Maven funzionanti (compiler, javafx, shade)

✅ **Struttura Progetto**
- Directory structure completa creata
- Organizzazione package seguendo best practice
- Separazione logica controllers/models/services

✅ **Module System**
- `module-info.java` configurato
- Gestione corretta dipendenze JPMS
- Export e opens configurati per FXML

✅ **MainApp**
- Applicazione JavaFX funzionante
- Ciclo di vita gestito correttamente
- Logging integrato
- Gestione errori implementata

✅ **Verifica Funzionamento**
- Build Maven completato con successo
- Applicazione eseguibile via `mvn javafx:run`
- Hello World visualizzato correttamente

### Problemi Riscontrati

⚠️ **Dipendenze Maven**
- Maven richiede connessione internet per scaricare dipendenze
- Prima esecuzione richiede download ~50MB di librerie
- **Soluzione:** Documentato in README.md

⚠️ **JavaFX Module System**
- Configurazione iniziale complessa per JPMS
- Necessario specificare requires espliciti
- **Soluzione:** module-info.java ben documentato

### Metriche

| Metrica | Valore |
|---------|--------|
| File creati | 3 |
| Directory create | 10 |
| Righe codice | ~80 |
| Dipendenze configurate | 8 |
| Tempo effettivo | 4 ore |

### Lesson Learned

1. **Maven JavaFX Plugin:** Fondamentale per sviluppo, evita configurazione manuale module-path
2. **JPMS Complexity:** Richiede attenzione per gestire dipendenze non-modularizzate
3. **Logging Setup:** SLF4J + Logback configurato da subito facilita debugging
4. **Structure First:** Creare struttura completa directory da inizio evita refactoring

---

## Prossimi Passi (Sprint 1)

Sprint 1 si concentrerà su:

1. **Main Window Layout**
   - MenuBar completa
   - ToolBar con azioni rapide
   - StatusBar per messaggi
   - Sistema navigazione

2. **View FXML**
   - Home view (selezione dataset)
   - Clustering view (progress feedback)
   - Results view (visualizzazione cluster)
   - Settings view (configurazione)

3. **Controller Implementation**
   - MainController per navigazione
   - Controller specifici per ogni view
   - Input validation
   - Event handling

---

## Deliverables

- ✅ Progetto Maven qtGUI funzionante
- ✅ Struttura directory completa
- ✅ module-info.java configurato
- ✅ MainApp.java implementato
- ✅ Hello World eseguibile
- ✅ pom.xml con tutte le dipendenze
- ✅ Build script verificato

---

**Data Completamento:** 2025-11-08
**Prossimo Sprint:** Sprint 1 - UI Base e Navigazione
