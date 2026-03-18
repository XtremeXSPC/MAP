package database;

/**
 * Eccezione lanciata quando una query SQL restituisce un ResultSet vuoto.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 */
public class EmptySetException extends Exception {

    /**
     * Costruttore che crea un'eccezione con messaggio predefinito.
     */
    public EmptySetException() {
        super("Empty ResultSet");
    }
}
