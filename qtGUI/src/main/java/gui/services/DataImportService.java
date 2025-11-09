package gui.services;

import data.Data;
import data.EmptyDatasetException;
import data.InvalidDataFormatException;
import database.DatabaseConnectionException;
import database.DbAccess;
import database.EmptySetException;
import database.NoValueException;
// import database.TableData;
// import database.TableSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servizio per l'importazione di dataset da diverse sorgenti.
 * Supporta caricamento da:
 * <ul>
 *   <li>Dataset hardcoded (PlayTennis)</li>
 *   <li>File CSV</li>
 *   <li>Database MySQL</li>
 * </ul>
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 */
public class DataImportService {

    private static final Logger logger = LoggerFactory.getLogger(DataImportService.class);

    /**
     * Enum per i tipi di sorgente dati supportati.
     */
    public enum DataSource {
        HARDCODED, CSV, DATABASE
    }

    /**
     * Carica il dataset hardcoded PlayTennis (14 tuple, 5 attributi).
     *
     * @return il dataset PlayTennis
     * @throws EmptyDatasetException se il dataset risulta vuoto
     */
    public Data loadHardcodedData() throws EmptyDatasetException {
        logger.info("Caricamento dataset hardcoded (PlayTennis)");

        try {
            Data data = new Data();
            logger.info("Dataset hardcoded caricato: {} tuple, {} attributi", data.getNumberOfExamples(),
                    data.getNumberOfExplanatoryAttributes());
            return data;

        } catch (Exception e) {
            logger.error("Errore durante caricamento dataset hardcoded", e);
            throw new EmptyDatasetException("Errore caricamento dataset hardcoded: " + e.getMessage());
        }
    }

    /**
     * Carica dataset da file CSV.
     * Il file deve avere:
     * - Prima riga: header con nomi attributi
     * - Righe successive: tuple di dati
     * - Separatore: virgola
     *
     * @param filePath percorso del file CSV
     * @return il dataset caricato dal CSV
     * @throws FileNotFoundException se il file non esiste
     * @throws InvalidDataFormatException se il formato CSV non è valido
     * @throws EmptyDatasetException se il file è vuoto
     * @throws IOException per errori di I/O generici
     */
    public Data loadDataFromCSV(String filePath)
            throws FileNotFoundException, InvalidDataFormatException, EmptyDatasetException, IOException {

        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Il percorso file non può essere vuoto");
        }

        logger.info("Caricamento dataset da CSV: {}", filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            // Leggi header
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.trim().isEmpty()) {
                throw new EmptyDatasetException("File CSV vuoto o senza header");
            }

            // TODO: Implementare parsing CSV completo
            // Per ora lanciamo eccezione NotImplemented
            logger.warn("Import CSV non ancora completamente implementato");
            throw new UnsupportedOperationException("Import CSV sarà implementato in una versione futura. "
                    + "Usa il dataset hardcoded o database per ora.");

        } catch (FileNotFoundException e) {
            logger.error("File CSV non trovato: {}", filePath, e);
            throw e;
        } catch (IOException e) {
            logger.error("Errore I/O durante lettura CSV", e);
            throw e;
        }
    }

    /**
     * Carica dataset da database MySQL.
     *
     * @param tableName nome della tabella da cui caricare
     * @param dbHost host del database (es. "localhost")
     * @param dbPort porta del database (es. 3306)
     * @param dbName nome del database
     * @param dbUser username
     * @param dbPassword password
     * @return il dataset caricato dal database
     * @throws DatabaseConnectionException se la connessione al DB fallisce
     * @throws EmptyDatasetException se la tabella è vuota
     * @throws SQLException per errori SQL generici
     */
    public Data loadDataFromDatabase(String tableName, String dbHost, int dbPort, String dbName, String dbUser,
            String dbPassword) throws DatabaseConnectionException, EmptyDatasetException, SQLException {

        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome tabella non può essere vuoto");
        }

        logger.info("Caricamento dataset da database: {}:{}/{} - tabella: {}", dbHost, dbPort, dbName, tableName);

        DbAccess db = null;

        try {
            // Connessione database
            db = new DbAccess(dbHost, String.valueOf(dbPort), dbName, dbUser, dbPassword);

            logger.info("Connessione database stabilita");

            // Carica dati usando il costruttore che accetta DbAccess esistente
            // IMPORTANTE: Usa la connessione gia creata, non ne crea una nuova
            Data data = new Data(db, tableName);

            logger.info("Dataset caricato dal database: {} tuple, {} attributi", data.getNumberOfExamples(),
                    data.getNumberOfExplanatoryAttributes());

            return data;

        } catch (DatabaseConnectionException e) {
            logger.error("Errore connessione database: {}", e.getMessage(), e);
            throw e;
        } catch (EmptySetException e) {
            logger.error("Tabella {} vuota o non trovata", tableName, e);
            throw new EmptyDatasetException("Tabella vuota: " + e.getMessage());
        } catch (NoValueException e) {
            logger.error("Valori mancanti in tabella {}", tableName, e);
            throw new SQLException("Errore valori database: " + e.getMessage());
        } catch (SQLException e) {
            logger.error("Errore SQL durante caricamento dati", e);
            throw e;
        } finally {
            // Chiudi connessione
            if (db != null) {
                db.closeConnection();
                logger.debug("Connessione database chiusa");
            }
        }
    }

    /**
     * Restituisce preview del dataset (prime N righe).
     *
     * @param data il dataset
     * @param maxRows numero massimo di righe da mostrare
     * @return stringa formattata con preview
     */
    public String getDatasetPreview(Data data, int maxRows) {
        if (data == null) {
            return "Nessun dataset caricato";
        }

        int numExamples = data.getNumberOfExamples();
        int numAttributes = data.getNumberOfExplanatoryAttributes();
        int rowsToShow = Math.min(maxRows, numExamples);

        StringBuilder preview = new StringBuilder();
        preview.append(String.format("Dataset: %d tuple, %d attributi\n\n", numExamples, numAttributes));
        preview.append("Prime ").append(rowsToShow).append(" righe:\n");
        preview.append("=".repeat(60)).append("\n");

        // Header attributi
        for (int j = 0; j < numAttributes; j++) {
            preview.append(data.getExplanatoryAttribute(j).getName());
            if (j < numAttributes - 1) {
                preview.append(", ");
            }
        }
        preview.append("\n");
        preview.append("-".repeat(60)).append("\n");

        // Dati
        for (int i = 0; i < rowsToShow; i++) {
            for (int j = 0; j < numAttributes; j++) {
                preview.append(data.getValue(i, j));
                if (j < numAttributes - 1) {
                    preview.append(", ");
                }
            }
            preview.append("\n");
        }

        if (rowsToShow < numExamples) {
            preview.append(String.format("\n... e altre %d righe\n", numExamples - rowsToShow));
        }

        return preview.toString();
    }

    /**
     * Testa la connessione al database.
     *
     * @param dbHost host del database
     * @param dbPort porta del database
     * @param dbName nome del database
     * @param dbUser username
     * @param dbPassword password
     * @return true se la connessione ha successo
     */
    public boolean testDatabaseConnection(String dbHost, int dbPort, String dbName, String dbUser, String dbPassword) {
        logger.info("Test connessione database: {}:{}/{}", dbHost, dbPort, dbName);

        DbAccess db = null;

        try {
            db = new DbAccess(dbHost, String.valueOf(dbPort), dbName, dbUser, dbPassword);

            logger.info("Test connessione riuscito");
            return true;

        } catch (DatabaseConnectionException e) {
            logger.error("Test connessione fallito: {}", e.getMessage());
            return false;

        } finally {
            if (db != null) {
                db.closeConnection();
            }
        }
    }
}
