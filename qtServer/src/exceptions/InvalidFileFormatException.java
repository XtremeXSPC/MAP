package exceptions;

/**
 * Eccezione lanciata quando il formato di un file non è valido. Usata principalmente per
 * file .dmp (cluster dump files).
 */
public class InvalidFileFormatException extends Exception {

    /**
     * Costruisce una nuova eccezione con il messaggio specificato.
     *
     * @param message descrizione dettagliata dell'errore di formato
     */
    public InvalidFileFormatException(String message) {
        super(message);
    }

    /**
     * Costruisce una nuova eccezione con messaggio e causa.
     *
     * @param message descrizione dettagliata dell'errore di formato
     * @param cause causa originale dell'eccezione
     */
    public InvalidFileFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
