# Fix Critico: Integrazione Database GUI

> **Data**: 2025-11-08
> **Priorita**: ALTISSIMA
> **Stato**: Risolto

---

## Problema Identificato

L'applicazione GUI non permetteva all'utente di inserire le credenziali del database, rendendo impossibile la connessione a database con configurazioni diverse da quelle hardcoded.

### Sintomi

1. **Campi mancanti nella GUI**: La sezione Database in `home.fxml` non conteneva campi per:
   - Nome database (dbName)
   - Username (dbUser)
   - Password (dbPassword)

2. **Valori hardcoded**: In `HomeController.java:332-334` i valori erano fissati:
   ```java
   config.setDbName("MapDB");
   config.setDbUser("MapUser");
   config.setDbPassword("map");
   ```

3. **Impossibilita di connessione**: Gli utenti con database diversi o credenziali diverse non potevano eseguire clustering da database.

---

## Analisi Root Cause

### File Coinvolti

**1. home.fxml (linee 62-76)**
```xml
<!-- Database Configuration (visible when Database is selected) -->
<VBox fx:id="databaseSection" spacing="10" visible="false" managed="false">
    <HBox spacing="10" alignment="CENTER_LEFT">
        <Label text="Table Name:" minWidth="120" />
        <TextField fx:id="tableNameField" prefWidth="250" promptText="Enter table name..." />
    </HBox>
    <HBox spacing="10" alignment="CENTER_LEFT">
        <Label text="DB Host:" minWidth="120" />
        <TextField fx:id="dbHostField" prefWidth="250" text="localhost" />
    </HBox>
    <HBox spacing="10" alignment="CENTER_LEFT">
        <Label text="DB Port:" minWidth="120" />
        <TextField fx:id="dbPortField" prefWidth="100" text="3306" />
    </HBox>
    <!-- MANCANO: dbName, dbUser, dbPassword -->
</VBox>
```

**Problema**: Solo 3 campi su 6 necessari per la connessione database.

**2. HomeController.java (linee 32-39)**
```java
// Dichiarazioni FXML
@FXML private VBox databaseSection;
@FXML private TextField tableNameField;
@FXML private TextField dbHostField;
@FXML private TextField dbPortField;
// MANCANO: dbNameField, dbUserField, dbPasswordField
```

**Problema**: Mancano dichiarazioni @FXML per i campi.

**3. HomeController.java (linee 318-335)**
```java
// Imposta parametri Database
if (dataSource == DataSource.DATABASE) {
    config.setDbHost(...);
    config.setDbPort(...);

    // HARDCODED - Problema principale
    config.setDbName("MapDB");
    config.setDbUser("MapUser");
    config.setDbPassword("map");

    config.setDbTableName(...);
}
```

**Problema**: Valori hardcoded invece di lettura da GUI.

**4. HomeController.java (linee 170-179)**
```java
} else if (dataSource.contains("Database")) {
    if (tableNameField.getText() == null || tableNameField.getText().trim().isEmpty()) {
        isValid = false;
        errors.append("Inserire un nome tabella. ");
    }
    // MANCANO: validazioni per dbName, dbUser, dbPassword
}
```

**Problema**: Validazione incompleta.

---

## Soluzione Implementata

### 1. Aggiunta Campi GUI (home.fxml)

**File**: `qtGUI/src/main/resources/views/home.fxml:62-88`

```xml
<!-- Database Configuration (visible when Database is selected) -->
<VBox fx:id="databaseSection" spacing="10" visible="false" managed="false">
    <HBox spacing="10" alignment="CENTER_LEFT">
        <Label text="DB Host:" minWidth="120" />
        <TextField fx:id="dbHostField" prefWidth="250" text="localhost" />
    </HBox>
    <HBox spacing="10" alignment="CENTER_LEFT">
        <Label text="DB Port:" minWidth="120" />
        <TextField fx:id="dbPortField" prefWidth="100" text="3306" />
    </HBox>

    <!-- NUOVO: Campo Database Name -->
    <HBox spacing="10" alignment="CENTER_LEFT">
        <Label text="Database Name:" minWidth="120" />
        <TextField fx:id="dbNameField" prefWidth="250" text="MapDB" promptText="e.g., MapDB" />
    </HBox>

    <!-- NUOVO: Campo Username -->
    <HBox spacing="10" alignment="CENTER_LEFT">
        <Label text="Username:" minWidth="120" />
        <TextField fx:id="dbUserField" prefWidth="250" text="MapUser" promptText="e.g., MapUser" />
    </HBox>

    <!-- NUOVO: Campo Password -->
    <HBox spacing="10" alignment="CENTER_LEFT">
        <Label text="Password:" minWidth="120" />
        <PasswordField fx:id="dbPasswordField" prefWidth="250" text="map" promptText="Database password" />
    </HBox>

    <HBox spacing="10" alignment="CENTER_LEFT">
        <Label text="Table Name:" minWidth="120" />
        <TextField fx:id="tableNameField" prefWidth="250" promptText="Enter table name..." />
    </HBox>
</VBox>
```

**Modifiche**:
- Aggiunto `dbNameField` (TextField)
- Aggiunto `dbUserField` (TextField)
- Aggiunto `dbPasswordField` (PasswordField per sicurezza)
- Riordinato campi: Host, Port, Database Name, Username, Password, Table Name

---

### 2. Dichiarazioni FXML (HomeController.java)

**File**: `qtGUI/src/main/java/gui/controllers/HomeController.java:33-39`

```java
@FXML private VBox databaseSection;
@FXML private TextField dbHostField;
@FXML private TextField dbPortField;
@FXML private TextField dbNameField;          // NUOVO
@FXML private TextField dbUserField;          // NUOVO
@FXML private PasswordField dbPasswordField;  // NUOVO
@FXML private TextField tableNameField;
```

**Modifiche**:
- Aggiunto `@FXML private TextField dbNameField;`
- Aggiunto `@FXML private TextField dbUserField;`
- Aggiunto `@FXML private PasswordField dbPasswordField;`

---

### 3. Lettura Valori da GUI (HomeController.java)

**File**: `qtGUI/src/main/java/gui/controllers/HomeController.java:320-341`

```java
// Imposta parametri Database
if (dataSource == DataSource.DATABASE) {
    config.setDbHost(dbHostField.getText() != null && !dbHostField.getText().trim().isEmpty()
            ? dbHostField.getText().trim() : "localhost");

    try {
        config.setDbPort(Integer.parseInt(dbPortField.getText().trim()));
    } catch (NumberFormatException e) {
        config.setDbPort(3306); // Default MySQL port
    }

    // NUOVO: Legge dbName da GUI invece di hardcoded
    config.setDbName(dbNameField.getText() != null && !dbNameField.getText().trim().isEmpty()
            ? dbNameField.getText().trim() : "MapDB");

    // NUOVO: Legge dbUser da GUI invece di hardcoded
    config.setDbUser(dbUserField.getText() != null && !dbUserField.getText().trim().isEmpty()
            ? dbUserField.getText().trim() : "MapUser");

    // NUOVO: Legge dbPassword da GUI invece di hardcoded
    config.setDbPassword(dbPasswordField.getText() != null && !dbPasswordField.getText().isEmpty()
            ? dbPasswordField.getText() : "map");

    config.setDbTableName(tableNameField.getText().trim());
}
```

**Modifiche**:
- Rimossi valori hardcoded
- Aggiunta lettura da campi GUI con fallback a valori predefiniti
- Gestione casi null/empty per robustezza

---

### 4. Validazione Campi (HomeController.java)

**File**: `qtGUI/src/main/java/gui/controllers/HomeController.java:177-195`

```java
} else if (dataSource.contains("Database")) {
    // Valida campi obbligatori database

    // NUOVO: Validazione dbName
    if (dbNameField.getText() == null || dbNameField.getText().trim().isEmpty()) {
        isValid = false;
        errors.append("Inserire il nome del database. ");
    }

    // NUOVO: Validazione dbUser
    if (dbUserField.getText() == null || dbUserField.getText().trim().isEmpty()) {
        isValid = false;
        errors.append("Inserire username database. ");
    }

    // NUOVO: Validazione dbPassword
    if (dbPasswordField.getText() == null || dbPasswordField.getText().isEmpty()) {
        isValid = false;
        errors.append("Inserire password database. ");
    }

    if (tableNameField.getText() == null || tableNameField.getText().trim().isEmpty()) {
        isValid = false;
        errors.append("Inserire nome tabella. ");
    }
}
```

**Modifiche**:
- Aggiunta validazione per dbName (obbligatorio)
- Aggiunta validazione per dbUser (obbligatorio)
- Aggiunta validazione per dbPassword (obbligatorio)
- Messaggi di errore chiari e specifici

---

### 5. Reset Campi (HomeController.java)

**File**: `qtGUI/src/main/java/gui/controllers/HomeController.java:254-275`

```java
private void handleCancel() {
    logger.info("Annulla cliccato");

    // Pulisce il modulo
    dataSourceComboBox.getSelectionModel().selectFirst();
    radiusField.clear();
    csvFilePathField.clear();
    selectedCsvFile = null;

    // NUOVO: Reset campi database ai valori predefiniti
    dbHostField.setText("localhost");
    dbPortField.setText("3306");
    dbNameField.setText("MapDB");
    dbUserField.setText("MapUser");
    dbPasswordField.setText("map");
    tableNameField.clear();

    enableCachingCheckBox.setSelected(true);
    verboseLoggingCheckBox.setSelected(false);

    validateForm();
}
```

**Modifiche**:
- Aggiunto reset campi database ai valori predefiniti
- Garantisce stato consistente dopo "Cancel"

---

## Testing

### Test Case 1: Valori Predefiniti
**Input**:
- Selezionare "Database" come sorgente
- Non modificare alcun campo

**Expected**:
- dbHost: "localhost"
- dbPort: 3306
- dbName: "MapDB"
- dbUser: "MapUser"
- dbPassword: "map"
- tableNameField: vuoto

**Validazione**: Pulsante "Start Clustering" disabilitato fino a inserimento table name.

---

### Test Case 2: Credenziali Custom
**Input**:
- dbHost: "192.168.1.100"
- dbPort: 3307
- dbName: "MyDatabase"
- dbUser: "admin"
- dbPassword: "secret123"
- tableNameField: "customers"

**Expected**:
- Configurazione usa tutti i valori custom
- Connessione tentata con nuove credenziali
- Validazione passa con tutti campi compilati

---

### Test Case 3: Campi Vuoti
**Input**:
- Svuotare campo dbName

**Expected**:
- Validazione fallisce
- Messaggio errore: "Inserire il nome del database."
- Pulsante "Start Clustering" disabilitato

---

### Test Case 4: Reset con Cancel
**Input**:
- Modificare tutti i campi database
- Cliccare "Cancel"

**Expected**:
- Tutti i campi database tornano ai valori predefiniti
- tableNameField svuotato

---

## File Modificati

| File | Linee Modificate | Tipo Modifica |
|------|------------------|---------------|
| `qtGUI/src/main/resources/views/home.fxml` | 62-88 | Aggiunta campi GUI |
| `qtGUI/src/main/java/gui/controllers/HomeController.java` | 33-39 | Dichiarazioni FXML |
| `qtGUI/src/main/java/gui/controllers/HomeController.java` | 177-195 | Validazione |
| `qtGUI/src/main/java/gui/controllers/HomeController.java` | 254-275 | Reset campi |
| `qtGUI/src/main/java/gui/controllers/HomeController.java` | 320-341 | Lettura GUI |

**Totale linee modificate/aggiunte**: ~50

---

## Impatto

### Prima del Fix
- Impossibile connettersi a database con credenziali diverse da MapDB/MapUser/map
- Nessuna flessibilita nella configurazione database
- TODO presente da Sprint 2 (linea 330-331)

### Dopo il Fix
- Utente puo inserire qualsiasi configurazione database
- Validazione garantisce campi obbligatori compilati
- Password field nasconde caratteri per sicurezza
- Valori predefiniti facilitano setup rapido
- Reset funziona correttamente

---

## Sicurezza

### PasswordField vs TextField

**Motivazione uso PasswordField**:
- Caratteri nascosti durante digitazione
- Protezione da shoulder surfing
- Standard UI per credenziali sensibili

**Nota**: La password è comunque memorizzata in plain text in `ClusteringConfiguration`. Per produzione considerare:
- Encryption at rest
- Uso di credential manager
- Prompt runtime invece di storage

---

## Backward Compatibility

Il fix mantiene backward compatibility:
- Valori predefiniti corrispondono ai vecchi hardcoded
- Database esistente (MapDB) funziona senza modifiche
- Codice backend (DbAccess, DataImportService) inalterato

---

## Prossimi Passi

### Sprint 4+
1. **Test Connessione Button**: Aggiungere pulsante "Test Connection" per verificare credenziali prima del clustering
2. **Credential Manager**: Salvare credenziali database in modo sicuro (keystore)
3. **Connection Pooling**: Riutilizzare connessioni database per performance
4. **Timeout Configuration**: Permettere configurazione timeout connessione
5. **SSL/TLS**: Supportare connessioni database cifrate

---

## Conclusioni

Fix critico completato con successo. L'applicazione ora permette connessioni database flessibili con validazione robusta e UX migliorata.

**Priorita**: RISOLTA
**Stato**: Pronto per commit e test

---

**Versione**: 1.0
**Data**: 2025-11-08
**Autore**: Claude AI Assistant

---

**Fine Documento**
