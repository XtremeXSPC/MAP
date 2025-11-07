import java.io.IOException;

/**
 * Eccezione sollevata dal server e propagata al client (QT08).
 * Incapsula errori lato server (DB, clustering, I/O).
 *
 * @author MAP corso
 * @version 1.0
 */
public class ServerException extends IOException {

    /**
     * Costruttore con messaggio.
     *
     * @param message messaggio di errore dal server
     */
    public ServerException(String message) {
        super(message);
    }

    /**
     * Costruttore con messaggio e causa.
     *
     * @param message messaggio di errore
     * @param cause eccezione originale
     */
    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
