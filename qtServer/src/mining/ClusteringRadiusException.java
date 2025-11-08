package mining;

/**
 * Eccezione lanciata quando il raggio di clustering specificato non è valido. Il raggio
 * deve essere un valore non negativo.
 */
public class ClusteringRadiusException extends Exception {

    /**
     * Costruisce una nuova eccezione con il messaggio specificato.
     *
     * @param message descrizione dettagliata dell'errore
     */
    public ClusteringRadiusException(String message) {
        super(message);
    }

    /**
     * Costruisce una nuova eccezione per un raggio non valido.
     *
     * @param radius il valore del raggio non valido
     */
    public ClusteringRadiusException(double radius) {
        super("Raggio di clustering non valido: " + radius + ". Il raggio deve essere >= 0");
    }
}
