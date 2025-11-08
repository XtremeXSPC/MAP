package database;

/**
 * Eccezione lanciata quando un valore richiesto non esiste nel ResultSet.
 *
 * @author Appice A.
 * @version 1.0
 */
public class NoValueException extends Exception {

    /**
     * Costruttore che crea un'eccezione con un messaggio specifico.
     *
     * @param message messaggio descrittivo dell'errore
     */
    public NoValueException(String message) {
        super(message);
    }
}
