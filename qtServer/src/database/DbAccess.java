package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestisce l'accesso al database MySQL tramite JDBC. Implementa i metodi per
 * inizializzare, ottenere e chiudere una connessione.
 *
 * @author MAP corso
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
    private final String SERVER = "localhost";

    /**
     * Nome del database
     */
    private final String DATABASE = "MapDB";

    /**
     * Porta del server MySQL
     */
    private final String PORT = "3306";

    /**
     * Nome utente per accesso al database
     */
    private final String USER_ID = "MapUser";

    /**
     * Password utente
     */
    private final String PASSWORD = "map";

    /**
     * Connessione al database
     */
    private Connection conn;

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

            // Costruisce la connection string
            String connectionString = DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE + "?user=" + USER_ID
                    + "&password=" + PASSWORD + "&serverTimezone=UTC";

            // Stabilisce la connessione
            conn = DriverManager.getConnection(connectionString);

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
