package gui.models;

//===---------------------------------------------------------------------------===//
// Importazioni modelli.
import gui.services.DataImportService.DataSource;
//===---------------------------------------------------------------------------===//

/**
 * Modello che rappresenta la configurazione per un'esecuzione di clustering.
 * <p>
 * Questa classe incapsula:
 * <ul>
 *   <li>Sorgente dati selezionata</li>
 *   <li>Parametro di radius</li>
 *   <li>Parametri CSV (percorso file)</li>
 *   <li>Parametri database (host, porta, nome, utente, tabella)</li>
 *   <li>Opzioni aggiuntive (caching, logging verboso)</li>
 * </ul>
 * <p>
 * La validazione di base e' centralizzata in {@link #isValid()}.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.0.0
 * @see gui.services.DataImportService.DataSource
 */
public class ClusteringConfiguration {

    //===--------------------------- INSTANCE FIELDS ---------------------------===//

    // Sorgente dati.
    private DataSource dataSource;
    private double radius;

    // Parametri per CSV.
    private String csvFilePath;

    // Parametri per Database.
    private String dbHost;
    private int dbPort;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private String dbTableName;

    // Opzioni.
    private boolean enableCaching;
    private boolean verboseLogging;

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruttore con parametri essenziali.
     * <p>
     * Inizializza i valori di default per i parametri database.
     *
     * @param dataSource sorgente dati (HARDCODED, CSV, DATABASE)
     * @param radius raggio massimo cluster
     */
    public ClusteringConfiguration(DataSource dataSource, double radius) {
        this.dataSource = dataSource;
        this.radius = radius;
        this.enableCaching = false;
        this.verboseLogging = false;

        // Valori default database.
        this.dbHost = "localhost";
        this.dbPort = 3306;
    }

    //===--------------------------- GETTER E SETTER ---------------------------===//

    /**
     * Restituisce la sorgente dati configurata.
     *
     * @return la sorgente dati
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Imposta la sorgente dati.
     *
     * @param dataSource la sorgente dati
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Restituisce il radius (raggio massimo del cluster).
     *
     * @return il radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Imposta il radius (raggio massimo del cluster).
     *
     * @param radius il radius (deve essere non negativo)
     * @throws IllegalArgumentException se radius è negativo
     */
    public void setRadius(double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius deve essere non negativo, ricevuto: " + radius);
        }
        this.radius = radius;
    }

    /**
     * Restituisce il percorso del file CSV.
     *
     * @return il percorso del file CSV
     */
    public String getCsvFilePath() {
        return csvFilePath;
    }

    /**
     * Imposta il percorso del file CSV.
     *
     * @param csvFilePath il percorso del file CSV
     */
    public void setCsvFilePath(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    /**
     * Restituisce l'host del database.
     *
     * @return l'host del database
     */
    public String getDbHost() {
        return dbHost;
    }

    /**
     * Imposta l'host del database.
     *
     * @param dbHost l'host del database
     */
    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    /**
     * Restituisce la porta del database.
     *
     * @return la porta del database
     */
    public int getDbPort() {
        return dbPort;
    }

    /**
     * Imposta la porta del database.
     *
     * @param dbPort la porta del database (deve essere tra 1 e 65535)
     * @throws IllegalArgumentException se la porta non è valida
     */
    public void setDbPort(int dbPort) {
        if (dbPort < 1 || dbPort > 65535) {
            throw new IllegalArgumentException("Porta database deve essere tra 1 e 65535, ricevuto: " + dbPort);
        }
        this.dbPort = dbPort;
    }

    /**
     * Restituisce il nome del database.
     *
     * @return il nome del database
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * Imposta il nome del database.
     *
     * @param dbName il nome del database
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * Restituisce l'username del database.
     *
     * @return l'username del database
     */
    public String getDbUser() {
        return dbUser;
    }

    /**
     * Imposta l'username del database.
     *
     * @param dbUser l'username del database
     */
    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    /**
     * Restituisce la password del database.
     *
     * @return la password del database
     */
    public String getDbPassword() {
        return dbPassword;
    }

    /**
     * Imposta la password del database.
     *
     * @param dbPassword la password del database
     */
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    /**
     * Restituisce il nome della tabella del database.
     *
     * @return il nome della tabella
     */
    public String getDbTableName() {
        return dbTableName;
    }

    /**
     * Imposta il nome della tabella del database.
     *
     * @param dbTableName il nome della tabella
     */
    public void setDbTableName(String dbTableName) {
        this.dbTableName = dbTableName;
    }

    /**
     * Verifica se il caching è abilitato.
     *
     * @return true se il caching è abilitato
     */
    public boolean isEnableCaching() {
        return enableCaching;
    }

    /**
     * Imposta se abilitare il caching.
     *
     * @param enableCaching true per abilitare il caching
     */
    public void setEnableCaching(boolean enableCaching) {
        this.enableCaching = enableCaching;
    }

    /**
     * Verifica se il logging verboso è abilitato.
     *
     * @return true se il logging verboso è abilitato
     */
    public boolean isVerboseLogging() {
        return verboseLogging;
    }

    /**
     * Imposta se abilitare il logging verboso.
     *
     * @param verboseLogging true per abilitare il logging verboso
     */
    public void setVerboseLogging(boolean verboseLogging) {
        this.verboseLogging = verboseLogging;
    }

    //===---------------------------- QUERY METHODS ----------------------------===//

    /**
     * Valida la configurazione.
     *
     * @return true se la configurazione è valida
     */
    public boolean isValid() {
        if (radius < 0) {
            return false;
        }

        switch (dataSource) {
            case CSV:
                return csvFilePath != null && !csvFilePath.trim().isEmpty();
            case DATABASE:
                return dbName != null && !dbName.trim().isEmpty() && dbUser != null && !dbUser.trim().isEmpty()
                        && dbTableName != null && !dbTableName.trim().isEmpty();
            case HARDCODED:
            default:
                return true;
        }
    }

    /**
     * Restituisce una descrizione testuale della configurazione.
     * <p>
     * Utile per logging e riepiloghi UI.
     *
     * @return descrizione testuale della configurazione
     */
    public String getDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append(String.format("Sorgente: %s\n", dataSource));
        desc.append(String.format("Radius: %.3f\n", radius));

        if (dataSource == DataSource.CSV) {
            desc.append(String.format("File CSV: %s\n", csvFilePath));
        } else if (dataSource == DataSource.DATABASE) {
            desc.append(String.format("Database: %s@%s:%d/%s\n", dbUser, dbHost, dbPort, dbName));
            desc.append(String.format("Tabella: %s\n", dbTableName));
        }

        if (enableCaching) {
            desc.append("Caching: Abilitato\n");
        }

        if (verboseLogging) {
            desc.append("Logging verboso: Abilitato\n");
        }

        return desc.toString();
    }

    /**
     * Restituisce una rappresentazione compatta della configurazione.
     *
     * @return stringa con sorgente e radius
     */
    @Override
    public String toString() {
        return String.format("ClusteringConfiguration[source=%s, radius=%.3f]", dataSource, radius);
    }
}

//===---------------------------------------------------------------------------===//
