package database;

/**
 * Eccezione lanciata quando fallisce la connessione al database.
 *
 * @author Appice A.
 * @version 1.0
 */
public class DatabaseConnectionException extends Exception {

    /**
     * Costruttore che crea un'eccezione con un messaggio specifico.
     *
     * @param message messaggio descrittivo dell'errore
     */
    public DatabaseConnectionException(String message) {
        super(message);
    }
}
