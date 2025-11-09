package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestisce l'accesso al database MySQL tramite JDBC. Implementa i metodi per
 * inizializzare, ottenere e chiudere una connessione.
 *
 * @author Appice A.
 * @version 1.0
 */
public class DbAccess {
    /**
     * Nome completo della classe del driver MySQL
     */
    private String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";

    /**
     * Identificatore del DBMS
     */
    private final String DBMS = "jdbc:mysql";

    /**
     * Indirizzo del server MySQL
     */
    private String SERVER = "localhost";

    /**
     * Nome del database
     */
    private String DATABASE = "MapDB";

    /**
     * Porta del server MySQL
     */
    private String PORT = "3306";

    /**
     * Nome utente per accesso al database
     */
    private String USER_ID = "MapUser";

    /**
     * Password utente
     */
    private String PASSWORD = "map";

    /**
     * Connessione al database
     */
    private Connection conn;

    /**
     * Costruttore di default.
     * Usa parametri di connessione predefiniti (localhost:3306/MapDB, user=MapUser, password=map).
     * I valori possono essere sovrascritti usando gli altri costruttori.
     * Richiede chiamata esplicita a initConnection().
     */
    public DbAccess() {
        // Usa valori di default già inizializzati
    }

    /**
     * Costruttore con JDBC URL completo. Inizializza automaticamente la connessione.
     *
     * @param jdbcUrl URL completo JDBC (es. "jdbc:mysql://localhost:3306/MapDB")
     * @param user username per l'accesso
     * @param password password per l'accesso
     * @throws DatabaseConnectionException se la connessione fallisce o l'URL è malformato
     */
    public DbAccess(String jdbcUrl, String user, String password) throws DatabaseConnectionException {
        // Parse JDBC URL: jdbc:mysql://server:port/database[?params]
        try {
            String[] parts = jdbcUrl.replace("jdbc:mysql://", "").split("/");
            String[] serverPart = parts[0].split(":");

            this.SERVER = serverPart[0];
            this.PORT = serverPart.length > 1 ? serverPart[1] : "3306";

            // Rimuove parametri query dal nome database (es. "MapDB?serverTimezone=UTC" -> "MapDB")
            if (parts.length > 1) {
                String dbPart = parts[1];
                this.DATABASE = dbPart.contains("?") ? dbPart.split("\\?")[0] : dbPart;
            } else {
                this.DATABASE = "MapDB";
            }

            this.USER_ID = user;
            this.PASSWORD = password;

            initConnection();
        } catch (Exception e) {
            throw new DatabaseConnectionException("JDBC URL malformato: " + jdbcUrl + " - " + e.getMessage());
        }
    }

    /**
     * Costruttore con parametri di connessione personalizzati separati. Inizializza
     * automaticamente la connessione.
     *
     * @param server indirizzo server MySQL
     * @param port porta server MySQL
     * @param database nome database
     * @param user username per l'accesso
     * @param password password per l'accesso
     * @throws DatabaseConnectionException se la connessione fallisce
     */
    public DbAccess(String server, String port, String database, String user, String password)
            throws DatabaseConnectionException {
        this.SERVER = server;
        this.PORT = port;
        this.DATABASE = database;
        this.USER_ID = user;
        this.PASSWORD = password;
        initConnection();
    }

    /**
     * Inizializza la connessione al database MySQL. Carica il driver MySQL e stabilisce la
     * connessione usando i parametri configurati.
     *
     * @throws DatabaseConnectionException se il driver non è trovato o la connessione
     *         fallisce
     */
    public void initConnection() throws DatabaseConnectionException {
        try {
            // Carica il driver MySQL
            Class.forName(DRIVER_CLASS_NAME);

            // Costruisce la connection string (senza credenziali per sicurezza)
            String connectionString = DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE + "?serverTimezone=UTC";

            // Stabilisce la connessione con credenziali separate
            conn = DriverManager.getConnection(connectionString, USER_ID, PASSWORD);

        } catch (ClassNotFoundException e) {
            throw new DatabaseConnectionException("Driver MySQL non trovato: " + e.getMessage());
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Impossibile connettersi al database: " + e.getMessage());
        }
    }

    /**
     * Restituisce la connessione al database.
     *
     * @return oggetto Connection
     */
    public Connection getConnection() {
        return conn;
    }

    /**
     * Chiude la connessione al database. Non solleva eccezioni se la connessione è già
     * chiusa.
     */
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }
}
