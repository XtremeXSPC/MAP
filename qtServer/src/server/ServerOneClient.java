package server;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import data.Data;
import database.DatabaseConnectionException;
import database.EmptySetException;
import database.NoValueException;

/**
 * Thread dedicato alla gestione di un singolo client (QT08).
 * Gestisce il protocollo di comunicazione e le richieste QT.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 */
public class ServerOneClient extends Thread {
    private static final int MAX_MESSAGE_BYTES = 1_000_000;

    // Logger.
    private static final Logger logger = Logger.getLogger(ServerOneClient.class.getName());

    // Socket e stream di comunicazione.
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    // Stato sessione.
    private Data data; // Dataset corrente.
    private mining.QTMiner qtMiner; // Ultimo clustering.
    private String currentTableName; // Nome tabella caricata.

    /**
     * Costruttore del gestore client.
     *
     * @param socket socket connessione con il client
     * @throws IOException se si verificano errori I/O
     */
    public ServerOneClient(Socket socket) throws IOException {
        this.socket = socket;

        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.out.flush();
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

        // Avvia il thread.
        start();
    }

    /**
     * Ciclo principale di gestione richieste client. Override del metodo run() di Thread.
     */
    @Override
    public void run() {
        String clientAddress = socket.getInetAddress().getHostAddress();
        logger.log(Level.INFO, () -> "[" + clientAddress + "] Thread avviato");

        try {
            while (true) {
                // Leggi comando dal client.
                int command = in.readInt();

                logger.log(Level.INFO, () -> "[" + clientAddress + "] Comando ricevuto: " + command);

                // Gestisci comando.
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
                        writeString("Comando non riconosciuto: " + command);
                        out.flush();
                }
            }

        } catch (EOFException e) {
            // Client ha chiuso la connessione.
            logger.log(Level.INFO, () -> "[" + clientAddress + "] Disconnessione client");

        } catch (IOException e) {
            logger.log(Level.SEVERE, e, () -> "[" + clientAddress + "] Errore comunicazione: " + e.getMessage());

        } finally {
            // Chiudi risorse.
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, e, () -> "Errore chiusura connessione: " + e.getMessage());
            }

            logger.log(Level.INFO, () -> "[" + clientAddress + "] Thread terminato");
        }
    }

    /**
     * COMANDO 0: Carica tabella da database.
     *
     * Protocollo: IN: tableName (String) OUT: "OK" oppure messaggio errore.
     */
    private void handleStoreTable() throws IOException {
        try {
            // Leggi nome tabella.
            String tableName = readString();
            logger.log(Level.INFO, () -> "  -> Caricamento tabella: " + tableName);

            // Carica da database.
            data = new Data(tableName, true);
            currentTableName = tableName;

            // Reset clustering precedente.
            qtMiner = null;

            logger.info(() -> "  ✓ Tabella caricata: " + data.getNumberOfExamples() + " esempi, "
                    + data.getNumberOfExplanatoryAttributes() + " attributi");

            // Risposta positiva.
            writeString("OK");
            out.flush();

        } catch (SQLException e) {
            String errorMsg = "Errore SQL: " + e.getMessage();
            logger.log(Level.SEVERE, e, () -> "  ✗ " + errorMsg);
            writeString(errorMsg);
            out.flush();

        } catch (EmptySetException e) {
            String errorMsg = "Tabella vuota o inesistente";
            logger.log(Level.SEVERE, e, () -> "  ✗ " + errorMsg);
            writeString(errorMsg);
            out.flush();

        } catch (DatabaseConnectionException e) {
            String errorMsg = "Impossibile connettersi al database: " + e.getMessage();
            logger.log(Level.SEVERE, e, () -> "  ✗ " + errorMsg);
            writeString(errorMsg);
            out.flush();

        } catch (NoValueException e) {
            String errorMsg = "Errore caricamento valori: " + e.getMessage();
            logger.log(Level.SEVERE, e, () -> "  ✗ " + errorMsg);
            writeString(errorMsg);
            out.flush();
        } catch (IllegalArgumentException e) {
            String errorMsg = "Input non valido: " + e.getMessage();
            logger.log(Level.WARNING, e, () -> "  ✗ " + errorMsg);
            writeString(errorMsg);
            out.flush();
        }
    }

    /**
     * COMANDO 1: Esegue clustering su tabella caricata.
     *
     * Protocollo: IN: radius (Double) OUT: "OK" + numClusters (Integer) + clusterSetString
     * (String) oppure messaggio errore.
     */
    private void handleLearnFromTable() throws IOException {
        try {
            // Verifica precondizioni.
            if (data == null) {
                writeString("Nessun dataset caricato. Usa comando 0 prima");
                out.flush();
                return;
            }

            // Leggi radius.
            double radius = in.readDouble();
            logger.log(Level.INFO, () -> "  -> Clustering con radius: " + radius);

            // Validazione radius.
            if (radius < 0) {
                writeString("Radius deve essere >= 0");
                out.flush();
                return;
            }

            // Esegue clustering.
            qtMiner = new mining.QTMiner(radius);
            int numClusters = qtMiner.compute(data);

            logger.log(Level.INFO, () -> "  ✓ Clustering completato: " + numClusters + " cluster");

            // Risposta.
            writeString("OK");
            out.writeInt(numClusters);
            writeString(qtMiner.getC().toString(data));
            out.flush();

        } catch (Exception e) {
            String errorMsg = "Errore durante clustering: " + e.getMessage();
            logger.log(Level.SEVERE, e, () -> "  ✗ " + errorMsg);
            writeString(errorMsg);
            out.flush();
        }
    }

    /**
     * COMANDO 2: Serializza cluster su file.
     *
     * Protocollo: IN: nessuno (usa stato interno) OUT: "OK" oppure messaggio errore.
     */
    private void handleStoreClusters() throws IOException {
        try {
            // Verifica precondizioni.
            if (qtMiner == null) {
                writeString("Nessun cluster da salvare. Usa comando 1 prima");
                out.flush();
                return;
            }

            // Genera nome file.
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = currentTableName + "_" + timestamp;

            logger.log(Level.INFO, () -> "  -> Salvataggio cluster in: " + fileName + ".dmp");

            // Serializza.
            qtMiner.saveComplete(fileName);

            logger.log(Level.INFO, "  ✓ Cluster salvati");

            // Risposta.
            writeString("OK");
            out.flush();

        } catch (FileNotFoundException e) {
            String errorMsg = "Errore creazione file: " + e.getMessage();
            logger.log(Level.SEVERE, e, () -> "  ✗ " + errorMsg);
            writeString(errorMsg);
            out.flush();

        } catch (IOException e) {
            String errorMsg = "Errore I/O durante salvataggio: " + e.getMessage();
            logger.log(Level.SEVERE, e, () -> "  ✗ " + errorMsg);
            writeString(errorMsg);
            out.flush();
        }
    }

    /**
     * COMANDO 3: Carica clustering da file .dmp esistente.
     *
     * Protocollo: IN: fileName (String) + radius (Double - ignorato) OUT: "OK" + clusterSetString
     * (String) oppure messaggio errore.
     */
    private void handleLearnFromFile() throws IOException {
        try {
            // Leggi parametri.
            String fileName = normalizeServerFileBaseName(readString());

            logger.log(Level.INFO, () -> "  -> Caricamento clustering da file: " + fileName);

            // Carica clustering da file (QTMiner aggiunge automaticamente .dmp).
            qtMiner = new mining.QTMiner(fileName);

            // Recupera i dati associati dal file.
            data = qtMiner.getData();

            logger.log(Level.INFO, () -> "  ✓ Clustering caricato: " + qtMiner.getC().getNumClusters() + " cluster");

            // Risposta.
            writeString("OK");
            if (data != null) {
                writeString(qtMiner.getC().toString(data));
            } else {
                writeString(qtMiner.getC().toString()
                        + "\nNota: file legacy caricato senza dataset associato. Dettaglio tuple non disponibile.\n");
            }
            out.flush();

        } catch (FileNotFoundException e) {
            String errorMsg = "File non trovato: " + e.getMessage();
            logger.log(Level.SEVERE, e, () -> "  ✗ " + errorMsg);
            writeString(errorMsg);
            out.flush();
        } catch (IllegalArgumentException e) {
            String errorMsg = "Nome file non valido: " + e.getMessage();
            logger.log(Level.WARNING, e, () -> "  ✗ " + errorMsg);
            writeString(errorMsg);
            out.flush();
        } catch (Exception e) {
            String errorMsg = "Errore caricamento file: " + e.getMessage();
            logger.log(Level.SEVERE, e, () -> "  ✗ " + errorMsg);
            writeString(errorMsg);
            out.flush();
        }
    }

    private void writeString(String value) throws IOException {
        byte[] bytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        if (bytes.length > MAX_MESSAGE_BYTES) {
            throw new IOException("Messaggio troppo grande: " + bytes.length + " bytes");
        }
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    private String readString() throws IOException {
        int length = in.readInt();
        if (length < 0 || length > MAX_MESSAGE_BYTES) {
            throw new IOException("Lunghezza messaggio non valida: " + length);
        }
        byte[] bytes = in.readNBytes(length);
        if (bytes.length != length) {
            throw new EOFException("Messaggio incompleto ricevuto dal client");
        }
        return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
    }

    private String normalizeServerFileBaseName(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("nome file nullo");
        }

        String trimmed = fileName.trim();
        if (trimmed.endsWith(".dmp")) {
            trimmed = trimmed.substring(0, trimmed.length() - 4);
        }

        if (trimmed.isEmpty() || ".".equals(trimmed) || "..".equals(trimmed)
                || !trimmed.matches("[A-Za-z0-9][A-Za-z0-9_.-]*")) {
            throw new IllegalArgumentException("usare solo lettere, numeri, '.', '_' o '-'");
        }

        return trimmed;
    }
}
