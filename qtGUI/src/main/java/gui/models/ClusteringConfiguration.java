package gui.models;

import gui.services.DataImportService.DataSource;

/**
 * Modello che rappresenta la configurazione per un'esecuzione del clustering.
 * Incapsula tutti i parametri di input selezionati dall'utente.
 *
 * @author MAP Team
 * @version 1.0.0
 * @since Sprint 2
 */
public class ClusteringConfiguration {

    private DataSource dataSource;
    private double radius;

    // Parametri per CSV
    private String csvFilePath;

    // Parametri per Database
    private String dbHost;
    private int dbPort;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private String dbTableName;

    // Opzioni
    private boolean enableCaching;
    private boolean verboseLogging;

    /**
     * Costruttore con parametri essenziali.
     *
     * @param dataSource sorgente dati (HARDCODED, CSV, DATABASE)
     * @param radius raggio massimo cluster
     */
    public ClusteringConfiguration(DataSource dataSource, double radius) {
        this.dataSource = dataSource;
        this.radius = radius;
        this.enableCaching = false;
        this.verboseLogging = false;

        // Valori default database
        this.dbHost = "localhost";
        this.dbPort = 3306;
    }

    // === Getters e Setters ===

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public String getCsvFilePath() {
        return csvFilePath;
    }

    public void setCsvFilePath(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    public String getDbHost() {
        return dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public int getDbPort() {
        return dbPort;
    }

    public void setDbPort(int dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbTableName() {
        return dbTableName;
    }

    public void setDbTableName(String dbTableName) {
        this.dbTableName = dbTableName;
    }

    public boolean isEnableCaching() {
        return enableCaching;
    }

    public void setEnableCaching(boolean enableCaching) {
        this.enableCaching = enableCaching;
    }

    public boolean isVerboseLogging() {
        return verboseLogging;
    }

    public void setVerboseLogging(boolean verboseLogging) {
        this.verboseLogging = verboseLogging;
    }

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
                return dbName != null && !dbName.trim().isEmpty() &&
                       dbUser != null && !dbUser.trim().isEmpty() &&
                       dbTableName != null && !dbTableName.trim().isEmpty();
            case HARDCODED:
            default:
                return true;
        }
    }

    /**
     * @return descrizione testuale della configurazione
     */
    public String getDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append(String.format("Sorgente: %s\n", dataSource));
        desc.append(String.format("Radius: %.3f\n", radius));

        if (dataSource == DataSource.CSV) {
            desc.append(String.format("File CSV: %s\n", csvFilePath));
        } else if (dataSource == DataSource.DATABASE) {
            desc.append(String.format("Database: %s@%s:%d/%s\n",
                    dbUser, dbHost, dbPort, dbName));
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

    @Override
    public String toString() {
        return String.format("ClusteringConfiguration[source=%s, radius=%.3f]",
                dataSource, radius);
    }
}
