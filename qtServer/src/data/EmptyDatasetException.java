package data;

/**
 * Eccezione lanciata quando si tenta di eseguire operazioni su un dataset vuoto.
 */
public class EmptyDatasetException extends Exception {

    /**
     * Costruisce una nuova eccezione con il messaggio specificato.
     *
     * @param message descrizione dettagliata dell'errore
     */
    public EmptyDatasetException(String message) {
        super(message);
    }

    /**
     * Costruisce una nuova eccezione con messaggio predefinito.
     */
    public EmptyDatasetException() {
        super("Il dataset è vuoto o non contiene esempi validi");
    }
}
