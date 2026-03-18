import java.io.FileNotFoundException;
import java.io.EOFException;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import keyboardinput.Keyboard;

/**
 * Classe principale per il client di clustering.
 * Gestisce la comunicazione con il server per operazioni di clustering su dati da file o database.
 */
public class MainTest {
    private static final int MAX_MESSAGE_BYTES = 1_000_000;

    /**
     * Stream di output per inviare dati al server
     */
    private DataOutputStream out;

    /**
     * Stream di input per ricevere dati dal server
     */
    private DataInputStream in;

    /**
     * Costruttore che inizializza la connessione al server.
     *
     * @param ip Indirizzo IP del server
     * @param port Porta del server
     * @throws IOException Se si verifica un errore di connessione
     */
    public MainTest(String ip, int port) throws IOException {
        InetAddress addr = InetAddress.getByName(ip);
        System.out.println("Indirizzo = " + addr);
        Socket socket = new Socket(addr, port);
        System.out.println(socket);

        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        out.flush();
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    /**
     * Visualizza il menu principale e restituisce la scelta dell'utente.
     *
     * @return La scelta dell'utente (0-4)
     */
    private int menu() {
        System.out.println("\n===============================================");
        System.out.println("  MENU OPERAZIONI");
        System.out.println("===============================================\n");
        System.out.println("0) Carica dati da database");
        System.out.println("1) Esegui clustering da database");
        System.out.println("2) Salva risultati clustering su file");
        System.out.println("3) Carica clustering da file ed esegui");
        System.out.println("4) Esci\n");
        System.out.println("===============================================");

        int answer;
        do {
            System.out.print("Scegli un'opzione [0-4]: ");
            answer = Keyboard.readInt();
        } while (answer < 0 || answer > 4);
        return answer;
    }

    /**
     * Carica i cluster da un file esistente.
     *
     * @return Una stringa contenente i risultati del clustering
     * @throws SocketException Se si verifica un errore di socket
     * @throws ServerException Se il server restituisce un errore
     * @throws IOException Se si verifica un errore di I/O
     * @throws ClassNotFoundException Se una classe non viene trovata durante la deserializzazione
     */
    private String learningFromFile() throws SocketException, ServerException, IOException, ClassNotFoundException {
        out.writeInt(3);

        System.out.print("Inserisci il nome del file (senza estensione .dmp): ");
        String fileName = Keyboard.readString();
        writeString(fileName);
        out.flush();
        String result = readString();

        if (result.equals("OK")) {
            String clusterData = readString();
            System.out.println("\nOK - Clustering caricato da file!");
            return clusterData;
        } else {
            throw new ServerException(result);
        }
    }

    /**
     * Salva i dati di una tabella dal database.
     *
     * @throws SocketException Se si verifica un errore di socket
     * @throws ServerException Se il server restituisce un errore
     * @throws IOException Se si verifica un errore di I/O
     * @throws ClassNotFoundException Se una classe non viene trovata durante la deserializzazione
     */
    private void storeTableFromDb() throws SocketException, ServerException, IOException, ClassNotFoundException {
        out.writeInt(0);

        System.out.print("Inserisci il nome della tabella: ");
        String tabName = Keyboard.readString();
        writeString(tabName);
        out.flush();

        String result = readString();
        if (!result.equals("OK")) {
            throw new ServerException(result);
        }
    }

    /**
     * Esegue il clustering sui dati caricati dal database.
     *
     * @return Una stringa contenente i risultati del clustering
     * @throws SocketException Se si verifica un errore di socket
     * @throws ServerException Se il server restituisce un errore
     * @throws IOException Se si verifica un errore di I/O
     * @throws ClassNotFoundException Se una classe non viene trovata durante la deserializzazione
     */
    private String learningFromDbTable() throws SocketException, ServerException, IOException, ClassNotFoundException {
        out.writeInt(1);

        double r;
        do {
            System.out.print("Inserisci il valore di radius [0.0 - 1.0]: ");
            r = Keyboard.readDouble();
        } while (r < 0);

        out.writeDouble(r);
        out.flush();
        String result = readString();

        if (result.equals("OK")) {
            in.readInt(); // Legge numClusters (già stampato dal server)
            String clusterData = readString();

            System.out.println("\nOK - Clustering completato!");
            return clusterData;
        } else {
            throw new ServerException(result);
        }
    }

    /**
     * Salva i cluster generati in un file.
     *
     * @throws SocketException Se si verifica un errore di socket
     * @throws ServerException Se il server restituisce un errore
     * @throws IOException Se si verifica un errore di I/O
     * @throws ClassNotFoundException Se una classe non viene trovata durante la deserializzazione
     */
    private void storeClusterInFile() throws SocketException, ServerException, IOException, ClassNotFoundException {
        out.writeInt(2);
        out.flush();

        String result = readString();
        if (!result.equals("OK"))
            throw new ServerException(result);
    }

    /**
     * Metodo principale che gestisce l'interazione con l'utente.
     *
     * @param args Argomenti da riga di comando: [0] = IP del server, [1] = porta del server
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Errore: Argomenti mancanti. Uso: java MainTest <ip> <porta>");
            System.out.println("Esempio: java MainTest localhost 8080");
            return;
        }

        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        MainTest main = null;

        try {
            main = new MainTest(ip, port);
        } catch (IOException e) {
            System.out.println("Errore connessione: " + e.getMessage());
            return;
        }

        boolean running = true;
        while (running) {
            int menuAnswer = main.menu();

            switch (menuAnswer) {
                case 0: // Carica dati da database
                    try {
                        main.storeTableFromDb();
                        System.out.println("\nOK - Dataset caricato con successo!\n");
                    } catch (ServerException e) {
                        System.out.println("ERROR: " + e.getMessage());
                    } catch (SocketException | ClassNotFoundException e) {
                        System.out.println("Errore fatale: " + e.getMessage());
                        return;
                    } catch (IOException e) {
                        System.out.println("Errore I/O: " + e.getMessage());
                        return;
                    }
                    break;

                case 1: // Esegui clustering da database
                    try {
                        String result = main.learningFromDbTable();
                        System.out.println(result);
                    } catch (ServerException e) {
                        System.out.println("ERROR: " + e.getMessage());
                    } catch (SocketException | ClassNotFoundException e) {
                        System.out.println("Errore fatale: " + e.getMessage());
                        return;
                    } catch (IOException e) {
                        System.out.println("Errore I/O: " + e.getMessage());
                        return;
                    }
                    break;

                case 2: // Salva risultati clustering su file
                    try {
                        main.storeClusterInFile();
                        System.out.println("\nOK - Clustering salvato con successo!\n");
                    } catch (ServerException e) {
                        System.out.println("ERROR: " + e.getMessage());
                    } catch (SocketException | ClassNotFoundException e) {
                        System.out.println("Errore fatale: " + e.getMessage());
                        return;
                    } catch (IOException e) {
                        System.out.println("Errore I/O: " + e.getMessage());
                        return;
                    }
                    break;

                case 3: // Carica clustering da file ed esegui
                    try {
                        String result = main.learningFromFile();
                        System.out.println(result);
                    } catch (ServerException e) {
                        System.out.println("ERROR: " + e.getMessage());
                    } catch (SocketException | ClassNotFoundException e) {
                        System.out.println("Errore fatale: " + e.getMessage());
                        return;
                    } catch (FileNotFoundException e) {
                        System.out.println("ERROR: File non trovato - " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println("Errore I/O: " + e.getMessage());
                        return;
                    }
                    break;

                case 4: // Esci
                    System.out.println("\nChiusura connessione al server...");
                    System.out.println("Arrivederci!\n");
                    running = false;
                    break;

                default:
                    System.out.println("Opzione non valida!");
            }
        }
    }

    private void writeString(String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
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
            throw new EOFException("Messaggio incompleto ricevuto dal server");
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
