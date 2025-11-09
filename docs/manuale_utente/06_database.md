# 6. Configurazione Database

## 6.1 Setup MySQL

### Installazione (Riepilogo)

**Ubuntu/Debian**:
```bash
sudo apt install mysql-server
sudo systemctl start mysql
sudo mysql_secure_installation
```

**macOS**:
```bash
brew install mysql
brew services start mysql
mysql_secure_installation
```

**Windows**: Download installer da mysql.com

---

## 6.2 Creazione Tabelle

### Schema Base

```sql
CREATE DATABASE qtclustering;
USE qtclustering;

CREATE TABLE example_dataset (
    id INT AUTO_INCREMENT PRIMARY KEY,
    attribute1 VARCHAR(50),
    attribute2 VARCHAR(50),
    attribute3 VARCHAR(50),
    class_label VARCHAR(50)
);
```

### Requisiti Tabella

- **Primary key**: Raccomandato ma non obbligatorio
- **Tipi colonna**: VARCHAR per discreti, INT/DOUBLE per continui
- **NULL values**: Evitare (gestione limitata)
- **Encoding**: UTF-8

---

## 6.3 Import Dati

### Da CSV a MySQL

```bash
mysql -u root -p qtclustering

LOAD DATA INFILE '/path/to/data.csv'
INTO TABLE example_dataset
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS;
```

### Script SQL

```sql
INSERT INTO playtennis
(outlook, temperature, humidity, wind, playtennis)
VALUES
('sunny', 'hot', 'high', 'weak', 'no'),
('sunny', 'hot', 'high', 'strong', 'no'),
...;
```

---

## 6.4 Connessione dall'Applicazione

### File db.properties

```properties
db.url=jdbc:mysql://localhost:3306/qtclustering
db.user=root
db.password=your_password
db.driver=com.mysql.cj.jdbc.Driver
```

### Variabili Ambiente (Sicuro)

```bash
export DB_URL=jdbc:mysql://localhost:3306/qtclustering
export DB_USER=root
export DB_PASSWORD=secret
```

Codice:
```java
String url = System.getenv("DB_URL");
String user = System.getenv("DB_USER");
String password = System.getenv("DB_PASSWORD");
```

### Test Connessione

```java
java database.DbAccess
```

Output atteso:
```
Connection successful!
Database: qtclustering
```

---

[← Capitolo 5: Workflow](05_workflow.md) | [Capitolo 7: Import/Export →](07_import_export.md)
