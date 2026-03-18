package data;

/**
 * Eccezione lanciata quando i dati in un file CSV non sono validi o inconsistenti.
 * Include il numero di riga dove si è verificato l'errore per facilitare il debug.
 */
public class InvalidDataFormatException extends Exception {

    private final int lineNumber;

    /**
     * Costruisce una nuova eccezione con messaggio e numero di riga.
     *
     * @param message descrizione dettagliata dell'errore nei dati
     * @param lineNumber numero di riga (1-based) dove si è verificato l'errore
     */
    public InvalidDataFormatException(String message, int lineNumber) {
        super(message + " (riga " + lineNumber + ")");
        this.lineNumber = lineNumber;
    }

    /**
     * Costruisce una nuova eccezione senza numero di riga specifico.
     *
     * @param message descrizione dettagliata dell'errore nei dati
     */
    public InvalidDataFormatException(String message) {
        super(message);
        this.lineNumber = -1;
    }

    /**
     * Restituisce il numero di riga dove si è verificato l'errore.
     *
     * @return numero di riga (1-based), o -1 se non specificato
     */
    public int getLineNumber() {
        return lineNumber;
    }
}
