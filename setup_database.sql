-- ============================================================================
-- Setup Database MapDB per Quality Threshold Clustering.
-- ============================================================================
-- Questo script crea le tabelle per i dataset CSV nella directory data/
--
-- Uso: mysql -u LCS-MAP MapDB < setup_database.sql
-- ============================================================================

USE MapDB;

-- Drop tabelle esistenti (se presenti).
DROP TABLE IF EXISTS playtennis;
DROP TABLE IF EXISTS weather_mixed;
DROP TABLE IF EXISTS iris;

-- ============================================================================
-- Tabella: playtennis
-- Dataset: PlayTennis classico (tutti attributi discreti)
-- File CSV: data/playtennis.csv
-- ============================================================================

CREATE TABLE playtennis (
    id INT AUTO_INCREMENT PRIMARY KEY,
    Outlook VARCHAR(20) NOT NULL,
    Temperature VARCHAR(20) NOT NULL,
    Humidity VARCHAR(20) NOT NULL,
    Wind VARCHAR(20) NOT NULL,
    PlayTennis VARCHAR(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- Tabella: weather_mixed
-- Dataset: Weather con attributi misti (discreti + continui)
-- File CSV: data/weather_mixed.csv
-- ============================================================================

CREATE TABLE weather_mixed (
    id INT AUTO_INCREMENT PRIMARY KEY,
    outlook VARCHAR(20) NOT NULL,
    temperature DOUBLE NOT NULL,
    humidity DOUBLE NOT NULL,
    wind VARCHAR(20) NOT NULL,
    play VARCHAR(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- Tabella: iris
-- Dataset: Iris (tutti attributi continui + classe)
-- File CSV: data/iris.csv
-- ============================================================================

CREATE TABLE iris (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sepal_length DOUBLE NOT NULL,
    sepal_width DOUBLE NOT NULL,
    petal_length DOUBLE NOT NULL,
    petal_width DOUBLE NOT NULL,
    species VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Verifica tabelle create.

SHOW TABLES;
