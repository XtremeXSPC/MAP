package server;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import data.Data;
import database.*;
import mining.*;

/**
 * Thread dedicato alla gestione di un singolo client (QT08).
 * Gestisce il protocollo di comunicazione e le richieste QT.
 *
 * @author MAP corso
 * @version 1.0
 */
public class ServerOneClient extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    // Stato sessione
    private Data data;                  // Dataset corrente
    private mining.QTMiner qtMiner;     // Ultimo clustering
    private String currentTableName;    // Nome tabella caricata

    /**
     * Costruttore del gestore client.
     *
     * @param socket socket connessione con il client
     * @throws IOException se si verificano errori I/O
     */
    public ServerOneClient(Socket socket) throws IOException {
        this.socket = socket;

        // IMPORTANTE: ObjectOutputStream PRIMA di ObjectInputStream
        // per evitare deadlock durante handshake
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(socket.getInputStream());

        // Avvia il thread
        start();
    }

    /**
     * Ciclo principale di gestione richieste client.
     * Override del metodo run() di Thread.
     */
    @Override
    public void run() {
        String clientAddress = socket.getInetAddress().getHostAddress();
        System.out.println("[" + clientAddress + "] Thread avviato");

        try {
            while (true) {
                // Leggi comando dal client
                Integer command = (Integer) in.readObject();

                System.out.println("[" + clientAddress + "] Comando ricevuto: " + command);

                // Gestisci comando
                switch (command) {
                    case 0:
                        handleStoreTable();
                        break;

                    case 1:
                        handleLearnFromTable();
                        break;

                    case 2:
                        handleStoreClusters();
                        break;

                    case 3:
                        handleLearnFromFile();
                        break;

                    default:
                        out.writeObject("Comando non riconosciuto: " + command);
                }
            }

        } catch (EOFException e) {
            // Client ha chiuso la connessione
            System.out.println("[" + clientAddress + "] Disconnessione client");

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[" + clientAddress + "] Errore comunicazione: "
                             + e.getMessage());

        } finally {
            // Chiudi risorse
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.err.println("Errore chiusura connessione: " + e.getMessage());
            }

            System.out.println("[" + clientAddress + "] Thread terminato");
        }
    }

    /**
     * COMANDO 0: Carica tabella da database.
     *
     * Protocollo:
     *   IN:  tableName (String)
     *   OUT: "OK" oppure messaggio errore
     */
    private void handleStoreTable() throws IOException, ClassNotFoundException {
        try {
            // Leggi nome tabella
            String tableName = (String) in.readObject();
            System.out.println("  → Caricamento tabella: " + tableName);

            // Carica da database
            data = new Data(tableName, true);
            currentTableName = tableName;

            // Reset clustering precedente
            qtMiner = null;

            System.out.println("  ✓ Tabella caricata: " + data.getNumberOfExamples()
                             + " esempi, " + data.getNumberOfExplanatoryAttributes()
                             + " attributi");

            // Risposta positiva
            out.writeObject("OK");

        } catch (SQLException e) {
            String errorMsg = "Errore SQL: " + e.getMessage();
            System.err.println("  ✗ " + errorMsg);
            out.writeObject(errorMsg);

        } catch (EmptySetException e) {
            String errorMsg = "Tabella vuota o inesistente";
            System.err.println("  ✗ " + errorMsg);
            out.writeObject(errorMsg);

        } catch (DatabaseConnectionException e) {
            String errorMsg = "Impossibile connettersi al database: " + e.getMessage();
            System.err.println("  ✗ " + errorMsg);
            out.writeObject(errorMsg);

        } catch (NoValueException e) {
            String errorMsg = "Errore caricamento valori: " + e.getMessage();
            System.err.println("  ✗ " + errorMsg);
            out.writeObject(errorMsg);
        }
    }

    /**
     * COMANDO 1: Esegue clustering su tabella caricata.
     *
     * Protocollo:
     *   IN:  radius (Double)
     *   OUT: "OK" + numClusters (Integer) + clusterSetString (String)
     *        oppure messaggio errore
     */
    private void handleLearnFromTable() throws IOException, ClassNotFoundException {
        try {
            // Verifica precondizioni
            if (data == null) {
                out.writeObject("Nessun dataset caricato. Usa comando 0 prima");
                return;
            }

            // Leggi radius
            Double radius = (Double) in.readObject();
            System.out.println("  → Clustering con radius: " + radius);

            // Validazione radius
            if (radius < 0) {
                out.writeObject("Radius deve essere >= 0");
                return;
            }

            // Esegue clustering
            qtMiner = new mining.QTMiner(radius);
            int numClusters = qtMiner.compute(data);

            System.out.println("  ✓ Clustering completato: " + numClusters + " cluster");

            // Risposta
            out.writeObject("OK");
            out.writeObject(numClusters);
            out.writeObject(qtMiner.getC().toString(data));

        } catch (Exception e) {
            String errorMsg = "Errore durante clustering: " + e.getMessage();
            System.err.println("  ✗ " + errorMsg);
            out.writeObject(errorMsg);
        }
    }

    /**
     * COMANDO 2: Serializza cluster su file.
     *
     * Protocollo:
     *   IN:  nessuno (usa stato interno)
     *   OUT: "OK" oppure messaggio errore
     */
    private void handleStoreClusters() throws IOException {
        try {
            // Verifica precondizioni
            if (qtMiner == null) {
                out.writeObject("Nessun cluster da salvare. Usa comando 1 prima");
                return;
            }

            // Genera nome file
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = currentTableName + "_" + timestamp;

            System.out.println("  → Salvataggio cluster in: " + fileName + ".dmp");

            // Serializza
            qtMiner.salva(fileName);

            System.out.println("  ✓ Cluster salvati");

            // Risposta
            out.writeObject("OK");

        } catch (FileNotFoundException e) {
            String errorMsg = "Errore creazione file: " + e.getMessage();
            System.err.println("  ✗ " + errorMsg);
            out.writeObject(errorMsg);

        } catch (IOException e) {
            String errorMsg = "Errore I/O durante salvataggio: " + e.getMessage();
            System.err.println("  ✗ " + errorMsg);
            out.writeObject(errorMsg);
        }
    }

    /**
     * COMANDO 3: Operazione completa (carica + clustering + salva).
     *
     * Protocollo:
     *   IN:  tableName (String) + radius (Double)
     *   OUT: "OK" + clusterSetString (String)
     *        oppure messaggio errore
     */
    private void handleLearnFromFile() throws IOException, ClassNotFoundException {
        try {
            // Leggi parametri
            String tableName = (String) in.readObject();
            Double radius = (Double) in.readObject();

            System.out.println("  → Operazione completa: tabella=" + tableName
                             + ", radius=" + radius);

            // 1. Carica tabella
            data = new Data(tableName, true);
            currentTableName = tableName;
            System.out.println("    [1/3] Tabella caricata: " + data.getNumberOfExamples()
                             + " esempi");

            // 2. Clustering
            qtMiner = new mining.QTMiner(radius);
            int numClusters = qtMiner.compute(data);
            System.out.println("    [2/3] Clustering completato: " + numClusters
                             + " cluster");

            // 3. Serializza
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = tableName + "_" + timestamp;
            qtMiner.salva(fileName);
            System.out.println("    [3/3] Cluster salvati in: " + fileName + ".dmp");

            // Risposta
            out.writeObject("OK");
            out.writeObject(qtMiner.getC().toString(data));

            System.out.println("  ✓ Operazione completata");

        } catch (SQLException | EmptySetException | DatabaseConnectionException
                 | NoValueException e) {
            String errorMsg = "Errore: " + e.getMessage();
            System.err.println("  ✗ " + errorMsg);
            out.writeObject(errorMsg);
        }
    }
}
