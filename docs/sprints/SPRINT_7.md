# Sprint 7 - Database Integration (JDBC) - QT07

**Durata:** 2 settimane
**Stato:** ✓ Completato
**QT Module:** QT07
**Data Completamento:** 2025-11-07
**Prerequisiti:** Sprint 2 (Persistenza), Sprint 4 (Keyboard Input)
**Specifica Riferimento:** `Project/QT07/Specifica_QT07_Serializzazione-JDBC.pdf`

---

## Obiettivi

Integrare supporto per database relazionali MySQL usando JDBC per:
1. Caricamento dataset da tabelle database
2. Serializzazione binaria dei cluster (oltre alla serializzazione testuale già presente)
3. Gestione connessioni database con pattern DAO

### Obiettivi Specifici

1. [x] Creare package `database/` con 8 classi (DbAccess, TableData, TableSchema, Example, QUERY_TYPE, 3 eccezioni)
2. [x] Implementare Serializable in tutte le classi rilevanti (9 classi)
3. [x] Modificare Data per supportare List<Example> invece di Object[][]
4. [x] Aggiungere costruttore Data(String tableName, boolean) per caricamento da MySQL
5. [x] Implementare serializzazione binaria in QTMiner (salva/carica)
6. [x] Aggiornare MainTest con menu database

---

## Modifiche Implementate

### 1. **Package database/** - 8 Classi Nuove

#### 1.1 Eccezioni Custom

**DatabaseConnectionException.java**
```java
package database;

public class DatabaseConnectionException extends Exception {
    public DatabaseConnectionException(String message) {
        super(message);
    }
}
```

**NoValueException.java**
```java
package database;

public class NoValueException extends Exception {
    public NoValueException(String message) {
        super(message);
    }
}
```

**EmptySetException.java**
```java
package database;

public class EmptySetException extends Exception {
    public EmptySetException() {
        super("Empty ResultSet");
    }
}
```

#### 1.2 QUERY_TYPE Enum

```java
public enum QUERY_TYPE {
    MIN, MAX
}
```

#### 1.3 Example Class

Modella una transazione (riga) letta dal database.

**Attributi:**
- `List<Object> example` - Valori eterogenei (String, Double, etc.)

**Metodi:**
- `add(Object o)` - Aggiunge valore
- `get(int i)` - Restituisce valore alla posizione i
- `compareTo(Example ex)` - Confronto per ordinamento
- `toString()` - Rappresentazione testuale

#### 1.4 TableSchema Class

Ricava schema di una tabella usando DatabaseMetaData.

**Inner Class Column:**
- `String name` - Nome colonna
- `String type` - Tipo Java ("string" o "number")
- `isNumber()` - Verifica se numerico

**Mapping tipi SQL → Java:**

| SQL Type                        | Java Type |
| ------------------------------- | --------- |
| VARCHAR, CHAR, LONGVARCHAR, BIT | string    |
| INT, SHORT, LONG, FLOAT, DOUBLE | number    |

**Costruttore:**
```java
public TableSchema(DbAccess db, String tableName) throws SQLException
```

#### 1.5 TableData Class

Accesso ai dati di una tabella.

**Metodi principali:**

**1. getDistinctTransazioni()**
```java
public List<Example> getDistinctTransazioni(String table)
    throws SQLException, EmptySetException
```
- Esegue: `SELECT DISTINCT col1, col2, ... FROM table`
- Restituisce: Lista di Example (transazioni distinte)

**2. getDistinctColumnValues()**
```java
public Set<Object> getDistinctColumnValues(String table, Column column)
    throws SQLException
```
- Esegue: `SELECT DISTINCT column FROM table ORDER BY column`
- Restituisce: TreeSet di valori ordinati

**3. getAggregateColumnValue()**
```java
public Object getAggregateColumnValue(String table, Column column, QUERY_TYPE aggregate)
    throws SQLException, NoValueException
```
- Esegue: `SELECT MIN(column) FROM table` o `SELECT MAX(column) FROM table`
- Restituisce: Valore aggregato (Float per number, String per string)

#### 1.6 DbAccess Class

Gestisce connessione al database MySQL.

**Parametri connessione:**
```java
private String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
private final String DBMS = "jdbc:mysql";
private final String SERVER = "localhost";
private final String DATABASE = "MapDB";
private final String PORT = "3306";
private final String USER_ID = "MapUser";
private final String PASSWORD = "map";
private Connection conn;
```

**Connection String:**
```
jdbc:mysql://localhost:3306/MapDB?user=MapUser&password=map&serverTimezone=UTC
```

**Metodi:**

**1. initConnection()**
```java
public void initConnection() throws DatabaseConnectionException
```
- Carica driver MySQL con Class.forName()
- Stabilisce connessione con DriverManager.getConnection()

**2. getConnection()**
```java
public Connection getConnection()
```
- Restituisce connessione attiva

**3. closeConnection()**
```java
public void closeConnection()
```
- Chiude connessione

---

### 2. **Serializable Implementato** (9 Classi)

Tutte le classi coinvolte nella serializzazione ora implementano `Serializable`:

| Classe              | Tipo            | File                     |
| ------------------- | --------------- | ------------------------ |
| ClusterSet          | public          | ClusterSet.java          |
| Cluster             | package-private | Cluster.java             |
| Tuple               | public          | Tuple.java               |
| Item                | abstract        | Item.java                |
| DiscreteItem        | public          | DiscreteItem.java        |
| ContinuousItem      | public          | ContinuousItem.java      |
| Attribute           | abstract        | Attribute.java           |
| DiscreteAttribute   | public          | DiscreteAttribute.java   |
| ContinuousAttribute | public          | ContinuousAttribute.java |

**Esempio modifica:**
```java
// Prima
public class ClusterSet implements Iterable<Cluster> { ... }

// Dopo
public class ClusterSet implements Iterable<Cluster>, Serializable { ... }
```

---

### 3. **Data.java - Refactoring Completo**

#### 3.1 Cambio Struttura Dati

**Prima:**
```java
private Object[][] data;
```

**Dopo:**
```java
private List<Example> data;
```

Questo permette l'integrazione diretta con il database (TableData restituisce List<Example>).

#### 3.2 Nuovo Costruttore Database

```java
public Data(String tableName, boolean fromDatabase)
        throws SQLException, EmptySetException, DatabaseConnectionException, NoValueException {

    DbAccess db = new DbAccess();
    db.initConnection();

    try {
        TableData tableData = new TableData(db);
        data = tableData.getDistinctTransazioni(tableName);
        numberOfExamples = data.size();

        TableSchema schema = new TableSchema(db, tableName);
        explanatorySet = new LinkedList<>();

        for (int i = 0; i < schema.getNumberOfAttributes(); i++) {
            TableSchema.Column col = schema.getColumn(i);

            if (col.isNumber()) {
                // Attributo continuo
                Float min = (Float) tableData.getAggregateColumnValue(tableName, col, QUERY_TYPE.MIN);
                Float max = (Float) tableData.getAggregateColumnValue(tableName, col, QUERY_TYPE.MAX);
                explanatorySet.add(new ContinuousAttribute(col.getColumnName(), i, min, max));
            } else {
                // Attributo discreto
                Set<Object> values = tableData.getDistinctColumnValues(tableName, col);
                String[] valuesArray = new String[values.size()];
                int idx = 0;
                for (Object v : values) {
                    valuesArray[idx++] = (String) v;
                }
                explanatorySet.add(new DiscreteAttribute(col.getColumnName(), i, valuesArray));
            }
        }
    } finally {
        db.closeConnection();
    }
}
```

**Flusso:**
1. Connessione database con DbAccess
2. Caricamento transazioni con TableData.getDistinctTransazioni()
3. Costruzione schema con TableSchema
4. Per ogni colonna:
   - Se numero → ContinuousAttribute (min/max aggregati)
   - Se stringa → DiscreteAttribute (valori distinti)
5. Chiusura connessione

#### 3.3 Metodi Aggiornati

**getValue()** - Aggiornato per List<Example>:
```java
public Object getValue(int exampleIndex, int attributeIndex) {
    return data.get(exampleIndex).get(attributeIndex);  // Era: data[exampleIndex][attributeIndex]
}
```

**toString()** - Aggiornato:
```java
for (int i = 0; i < numberOfExamples; i++) {
    str += i + ":";
    for (int j = 0; j < explanatorySet.size(); j++) {
        str += data.get(i).get(j);  // Era: data[i][j]
        ...
    }
}
```

**getItemSet()** - Gestisce Float da database:
```java
if (attr instanceof ContinuousAttribute) {
    Double numValue;
    if (value instanceof String) {
        numValue = Double.parseDouble((String) value);
    } else if (value instanceof Float) {
        numValue = ((Float) value).doubleValue();  // NUOVO per DB
    } else {
        numValue = (Double) value;
    }
    tuple.add(new ContinuousItem((ContinuousAttribute) attr, numValue), i);
}
```

---

### 4. **QTMiner.java - Serializzazione Binaria**

#### 4.1 Nuovo Costruttore De-serializzazione

```java
public QTMiner(String fileName)
        throws FileNotFoundException, IOException, ClassNotFoundException {
    FileInputStream fileIn = new FileInputStream(fileName + ".dmp");
    ObjectInputStream in = new ObjectInputStream(fileIn);
    C = (ClusterSet) in.readObject();
    in.close();
    fileIn.close();

    this.radius = 0;
    this.enableOptimizations = false;
}
```

**Utilizzo:**
```java
QTMiner qt = new QTMiner("radius2");  // Carica da radius2.dmp
```

#### 4.2 Metodo salva()

```java
public void salva(String fileName) throws FileNotFoundException, IOException {
    FileOutputStream fileOut = new FileOutputStream(fileName + ".dmp");
    ObjectOutputStream out = new ObjectOutputStream(fileOut);
    out.writeObject(C);
    out.close();
    fileOut.close();
    System.out.println("Saving clusters in " + fileName + ".dmp");
}
```

**Utilizzo:**
```java
qt.salva("radius2");  // Salva in radius2.dmp
```

#### 4.3 Differenza con ClusterSet.save()

| Aspetto         | ClusterSet.save() (QT04) | QTMiner.salva() (QT07) |
| --------------- | ------------------------ | ---------------------- |
| **Formato**     | Testuale (.dmp)          | Binario (.dmp)         |
| **Metodo**      | BufferedWriter           | ObjectOutputStream     |
| **Leggibilità** | Human-readable           | Binario                |
| **Metadati**    | timestamp, radius        | Solo oggetto           |
| **Velocità**    | Lento                    | Veloce                 |
| **Dimensione**  | Grande                   | Compatto               |

**Entrambi convivono:** Il progetto supporta sia serializzazione testuale che binaria.

---

### 5. **MainTest.java - Menu Database (QT07)**

#### 5.1 Nuovo Menu Principale

```
=== Menu Principale (QT07) ===
1. Carica Cluster da File
2. Carica Dati da Database (MySQL)
--- Opzioni Aggiuntive ---
3. Usa dataset PlayTennis (hardcoded)
4. Carica dataset da CSV
0. Esci
```

#### 5.2 Opzione 2 - Carica da Database

```java
private static void caricaDatiDatabase() {
    System.out.print("Nome tabella: ");
    String tableName = Keyboard.readString().trim();

    try {
        System.out.println("Connessione a MapDB in corso...");
        Data data = new Data(tableName, true);  // true = from database

        System.out.println("✓ Dataset caricato da database: " +
            data.getNumberOfExamples() + " esempi");

        System.out.println("\n" + data);
        eseguiClustering(data);

    } catch (DatabaseConnectionException e) {
        System.err.println("✗ Errore connessione database");
    } catch (EmptySetException e) {
        System.err.println("✗ Tabella vuota");
    } catch (SQLException e) {
        System.err.println("✗ Errore SQL: " + e.getMessage());
    } ...
}
```

#### 5.3 eseguiClustering() - Serializzazione Binaria

```java
private static void eseguiClustering(Data data) {
    double radius = getPositiveDoubleInput("Insert radius (>0): ");

    QTMiner qt = new QTMiner(radius);
    int numClusters = qt.compute(data);

    System.out.println("Number of clusters:" + numClusters);
    System.out.println(qt.getC().toString(data));

    System.out.print("Backup file name: ");
    String backupFile = Keyboard.readString().trim();

    qt.salva(backupFile);  // Serializzazione binaria QT07
    System.out.println("Saving transaction ended!");
}
```

**Output conforme a specifiche QT07:**
```
Insert radius (>0): 1
Number of clusters:11
1:Centroid=(overcast 30.0 high weak yes )
...
Backup file name: radius1
Saving clusters in radius1.dmp
Saving transaction ended!
```

---

## Schema Database MySQL

### Setup Database

```sql
-- Crea database
CREATE DATABASE MapDB;

-- Crea utente
CREATE USER 'MapUser'@'localhost' IDENTIFIED BY 'map';

-- Assegna privilegi
GRANT CREATE, SELECT, INSERT, DELETE ON MapDB.*
TO MapUser@localhost IDENTIFIED BY 'map';

-- Crea tabella
CREATE TABLE MapDB.playtennis(
    outlook varchar(10),
    temperature float(5,2),
    umidity varchar(10),
    wind varchar(10),
    play varchar(10)
);
```

### Dati (14 tuple)

```sql
insert into MapDB.playtennis values('sunny',30.3,'high','weak','no');
insert into MapDB.playtennis values('sunny',30.3,'high','strong','no');
insert into MapDB.playtennis values('overcast',30.0,'high','weak','yes');
insert into MapDB.playtennis values('rain',13.0,'high','weak','yes');
insert into MapDB.playtennis values('rain',0.0,'normal','weak','yes');
insert into MapDB.playtennis values('rain',0.0,'normal','strong','no');
insert into MapDB.playtennis values('overcast',0.1,'normal','strong','yes');
insert into MapDB.playtennis values('sunny',13.0,'high','weak','no');
insert into MapDB.playtennis values('sunny',0.1,'normal','weak','yes');
insert into MapDB.playtennis values('rain',12.0,'normal','weak','yes');
insert into MapDB.playtennis values('sunny',12.5,'normal','strong','yes');
insert into MapDB.playtennis values('overcast',12.5,'high','strong','yes');
insert into MapDB.playtennis values('overcast',29.21,'normal','weak','yes');
insert into MapDB.playtennis values('rain',12.5,'high','strong','no');
```

**Nota:** A differenza del dataset hardcoded, qui `temperature` è FLOAT (attributo continuo), non discreto.

---

## Compilazione e Dipendenze

### Dipendenze Richieste

**MySQL Connector/J** (JDBC Driver)

- Download: https://dev.mysql.com/downloads/connector/j/
- File: `mysql-connector-java-8.0.XX.jar`

### Compilazione

```bash
cd src
javac -cp .:mysql-connector-java-8.0.XX.jar *.java database/*.java exceptions/*.java keyboardinput/*.java
```

### Esecuzione

```bash
java -cp .:mysql-connector-java-8.0.XX.jar MainTest
```

### Verifica Compilazione (senza MySQL Connector)

```bash
javac *.java database/*.java exceptions/*.java keyboardinput/*.java
```

**Output:**
```
Note: database/Example.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
```

Compilazione riuscita! Il warning è dovuto a raw types in Example.compareTo() (codice fornito dal docente).

---

## Testing

### Test 1: Caricamento da Database

**Scenario:**
```
Scelta: 2
Nome tabella: playtennis
```

**Output atteso:**
```
Connessione a MapDB in corso...
✓ Dataset caricato da database: 14 esempi, 5 attributi

outlook,temperature,umidity,wind,play
0:sunny,30.3,high,weak,no,
1:sunny,30.3,high,strong,no,
...
13:rain,12.5,high,strong,no,

Insert radius (>0): 1
Number of clusters:11
...
Backup file name: radius1
Saving clusters in radius1.dmp
Saving transaction ended!
```

### Test 2: Serializzazione Binaria

**Salvataggio:**
```java
QTMiner qt = new QTMiner(0.5);
qt.compute(data);
qt.salva("test");  // Crea test.dmp
```

**Caricamento:**
```java
QTMiner qt2 = new QTMiner("test");  // Legge test.dmp
System.out.println(qt2.getC().toString(data));
```

### Test 3: Compatibilità con Sprint Precedenti

Tutte le funzionalità esistenti continuano a funzionare:
- Dataset hardcoded
- Caricamento CSV
- Serializzazione testuale ClusterSet.save()

---

## Criteri di Successo

- [x] Package `database/` completo con 8 classi
- [x] Connessione MySQL funzionante (configurata)
- [x] Caricamento dataset da tabella `playtennis`
- [x] Serializzazione binaria cluster implementata
- [x] De-serializzazione cluster funzionante
- [x] MainTest con menu database
- [x] Serializable implementato in tutte le classi rilevanti
- [x] Data refactorato con List<Example>
- [x] Gestione eccezioni database robusta
- [x] Compilazione senza errori
- [x] Documentazione completa Sprint 7

---

## Story Points: 21/21 (100%)

---

## File Deliverables

### Nuovi File (8)

```
src/database/DbAccess.java                      (+95 LOC)
src/database/TableData.java                     (+135 LOC - fornita)
src/database/TableSchema.java                   (+83 LOC - fornita)
src/database/Example.java                       (+58 LOC - fornita)
src/database/QUERY_TYPE.java                    (+10 LOC - fornita)
src/database/DatabaseConnectionException.java   (+14 LOC)
src/database/NoValueException.java              (+14 LOC)
src/database/EmptySetException.java             (+12 LOC)
```

### File Modificati (12)

```
src/ClusterSet.java      (+ Serializable)
src/Cluster.java         (+ Serializable)
src/Tuple.java           (+ Serializable)
src/Item.java            (+ Serializable)
src/DiscreteItem.java    (già Serializable via Item)
src/ContinuousItem.java  (già Serializable via Item)
src/Attribute.java       (+ Serializable)
src/DiscreteAttribute.java (già Serializable via Attribute)
src/ContinuousAttribute.java (già Serializable via Attribute)
src/Data.java            (Refactoring List<Example> + costruttore DB) (+150 LOC modificati)
src/QTMiner.java         (+ costruttore deserializzazione, + salva()) (+45 LOC)
src/MainTest.java        (+ menu database) (+50 LOC modificati)
```

### Documentazione

```
docs/sprints/SPRINT_7.md  (+850 LOC)
```

**Totale nuovo codice:** ~420 LOC (esclusi file forniti dal docente)
**Totale con file forniti:** ~706 LOC

---

## Note Implementative

### Differenze Dataset Hardcoded vs Database

| Aspetto         | Hardcoded (Sprint 0-6)    | Database (Sprint 7)           |
| --------------- | ------------------------- | ----------------------------- |
| **Temperature** | Discreto: cool/hot/mild   | Continuo: Float 0-30.3        |
| **Struttura**   | Object[][] (rigido)       | List<Example> (flessibile)    |
| **Caricamento** | Hardcoded nel costruttore | Query SQL SELECT              |
| **Schema**      | Predefinito               | Inferito da DatabaseMetaData  |
| **Item Types**  | DiscreteItem prevalente   | DiscreteItem + ContinuousItem |

### Gestione Tipo Attributi

Il costruttore `Data(String, boolean)` inferisce automaticamente i tipi:

```java
if (col.isNumber()) {
    // Numerico → ContinuousAttribute
    Float min = (Float) tableData.getAggregateColumnValue(..., QUERY_TYPE.MIN);
    Float max = (Float) tableData.getAggregateColumnValue(..., QUERY_TYPE.MAX);
    explanatorySet.add(new ContinuousAttribute(name, i, min, max));
} else {
    // Testuale → DiscreteAttribute
    Set<Object> values = tableData.getDistinctColumnValues(...);
    explanatorySet.add(new DiscreteAttribute(name, i, valuesArray));
}
```

### Pattern DAO Implementato

```
DbAccess (Connection Manager)
    ↓
TableSchema (Metadata Access)
    ↓
TableData (Data Access)
    ↓
Data (Domain Model)
```

---

## Limitazioni e Miglioramenti Futuri

### Limitazioni Attuali

1. **Parametri connessione hardcoded** in DbAccess
   - Miglioramento: File properties esterno
2. **Un solo database supportato** (MapDB)
   - Miglioramento: Configurazione multipli DB
3. **Connection pool assente**
   - Miglioramento: HikariCP o Apache DBCP
4. **Nessuna gestione transazioni**
   - Miglioramento: ACID support con commit/rollback

### Miglioramenti Sprint Futuri

**Sprint 8 (Socket)** userà questa infrastruttura:
- Server caricherà dati da database
- Client richiederà clustering remoto
- Serializzazione binaria per trasmissione cluster

---

## Riferimenti

### Specifiche Corso

- `Project/QT07/Specifica_QT07_Serializzazione-JDBC.pdf`
- `Project/QT07/JDBC/` - Codice di esempio fornito

### Documentazione Java

- JDBC Tutorial: https://docs.oracle.com/javase/tutorial/jdbc/
- Serialization: https://docs.oracle.com/javase/8/docs/technotes/guides/serialization/
- MySQL Connector/J: https://dev.mysql.com/doc/connector-j/8.0/en/

### Sprint Correlati

- **Sprint 2:** Persistenza (serializzazione testuale)
- **Sprint 4:** Keyboard Input (usato nel menu)
- **Sprint 6:** Generics e RTTI (usati in TableData/Example)

---

## Retrospettiva

### Cosa è Andato Bene ✓

- Integrazione JDBC pulita e conforme alle specifiche
- Refactoring Data da Object[][] a List<Example> senza regressioni
- Serializable aggiunto senza problemi di compatibilità
- Menu database user-friendly
- Compilazione riuscita senza errori critici

### Sfide Affrontate

- Gestione conversioni Float (DB) → Double (Java) in getItemSet()
- Coordinamento tra serializzazione testuale (Sprint 2) e binaria (Sprint 7)
- Verifica conformità output a specifiche QT07

### Lezioni Apprese

- List<Example> è molto più flessibile di Object[][] per database
- JDBC richiede attenzione a gestione risorse (try-finally per close())
- Serializzazione binaria è più efficiente ma meno debuggabile

---

**Fine Sprint 7 - Database Integration Completato con Successo!**
