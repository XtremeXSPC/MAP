package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server multi-client per Quality Threshold clustering (QT08).
 * Gestisce connessioni multiple tramite thread dedicati.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 */
public class MultiServer {
    // Porta di default.
    private static final int PORT = 8080;

    // Logger.
    private static final Logger logger = Logger.getLogger(MultiServer.class.getName());

    /**
     * Entry point del server.
     *
     * @param args argomenti da linea di comando (opzionale: porta)
     */
    public static void main(String[] args) {
        int port = PORT;

        // Opzionale: leggi porta da args
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, () -> "Porta non valida: " + args[0]);
                logger.log(Level.INFO, () -> "Uso porta default: " + PORT);
                port = PORT;
            }
        }

        new MultiServer(port);
    }

    /**
     * Costruttore del server multi-client. Inizializza la porta e avvia il server.
     *
     * @param port porta su cui ascoltare
     */
    public MultiServer(int port) {
        run(port);
    }

    /**
     * Ciclo principale del server. Attende connessioni e crea un thread per ogni client.
     *
     * @param port porta su cui ascoltare
     */
    private void run(int port) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            logger.info("# ============================================== #");
            logger.log(Level.INFO, () -> "    QT Server avviato sulla porta " + port);
            logger.info("    In attesa di connessioni client...");
            logger.info("# ============================================== #");

            while (true) {
                try {
                    // Attende connessione client (bloccante).
                    Socket clientSocket = serverSocket.accept();

                    logger.log(Level.INFO, () -> "\n[" + getTimestamp() + "] Nuova connessione da: "
                            + clientSocket.getInetAddress().getHostAddress());

                    // Crea thread dedicato per il client.
                    new ServerOneClient(clientSocket);

                } catch (IOException e) {
                    logger.log(Level.SEVERE, e, () -> "Errore accettazione connessione: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, e,
                    () -> "Impossibile avviare il server sulla porta " + port + ". Errore: " + e.getMessage());
            System.exit(1);

        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e, () -> "Errore chiusura server socket: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Restituisce timestamp corrente formattato.
     *
     * @return stringa timestamp HH:mm:ss
     */
    private String getTimestamp() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
