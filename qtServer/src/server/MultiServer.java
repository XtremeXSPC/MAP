package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Server multi-client per Quality Threshold clustering (QT08).
 * Gestisce connessioni multiple tramite thread dedicati.
 *
 * @author MAP corso
 * @version 1.0
 */
public class MultiServer {
    private static final int PORT = 8080;

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
                System.err.println("Porta non valida: " + args[0]);
                System.err.println("Uso porta default: " + PORT);
                port = PORT;
            }
        }

        new MultiServer(port);
    }

    /**
     * Costruttore del server multi-client.
     * Inizializza la porta e avvia il server.
     *
     * @param port porta su cui ascoltare
     */
    public MultiServer(int port) {
        run(port);
    }

    /**
     * Ciclo principale del server.
     * Attende connessioni e crea un thread per ogni client.
     *
     * @param port porta su cui ascoltare
     */
    private void run(int port) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("===========================================");
            System.out.println("QT Server avviato sulla porta " + port);
            System.out.println("In attesa di connessioni client...");
            System.out.println("===========================================");

            while (true) {
                try {
                    // Attende connessione client (bloccante)
                    Socket clientSocket = serverSocket.accept();

                    System.out.println("\n[" + getTimestamp() + "] Nuova connessione da: "
                                     + clientSocket.getInetAddress().getHostAddress());

                    // Crea thread dedicato per il client
                    new ServerOneClient(clientSocket);

                } catch (IOException e) {
                    System.err.println("Errore accettazione connessione: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Impossibile avviare il server sulla porta " + port);
            System.err.println("Errore: " + e.getMessage());
            System.exit(1);

        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.err.println("Errore chiusura server socket: " + e.getMessage());
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
