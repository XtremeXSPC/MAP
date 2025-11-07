package exceptions;

/**
 * Eccezione lanciata quando un cluster caricato da file non è compatibile
 * con il dataset corrente (es. numero attributi diverso, ID tuple non validi).
 */
public class IncompatibleClusterException extends Exception {

    /**
     * Costruisce una nuova eccezione con il messaggio specificato.
     *
     * @param message descrizione dettagliata dell'incompatibilità
     */
    public IncompatibleClusterException(String message) {
        super(message);
    }

    /**
     * Costruisce una nuova eccezione con messaggio e causa.
     *
     * @param message descrizione dettagliata dell'incompatibilità
     * @param cause causa originale dell'eccezione
     */
    public IncompatibleClusterException(String message, Throwable cause) {
        super(message, cause);
    }
}
