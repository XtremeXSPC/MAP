package gui.utils;

//===---------------------------------------------------------------------------===//
// Importazioni Java standard.
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import data.Data;
import data.InvalidDataFormatException;
//===---------------------------------------------------------------------------===//

/**
 * Utility per caricare dataset standard da resources.
 * <p>
 * Supporta il caricamento di dataset predefiniti (es. Iris)
 * direttamente dalle risorse dell'applicazione.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.1.0
 */
public class DatasetLoader {

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruttore privato per impedire l'istanziazione.
     * Questa è una classe utility con soli metodi statici.
     */
    private DatasetLoader() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Carica Iris dataset da resources.
     *
     * <p>Il dataset Iris contiene 150 tuple con 4 attributi continui
     * (sepal_length, sepal_width, petal_length, petal_width) e 1 attributo
     * di classe (species) con 3 valori: setosa, versicolor, virginica.</p>
     *
     * @return Data object con Iris dataset
     * @throws IOException se file non trovato o errore I/O
     * @throws InvalidDataFormatException se formato CSV invalido
     */
    public static Data loadIrisDataset() throws IOException, InvalidDataFormatException {
        // Copia file da resources a file temporaneo
        InputStream resourceStream = DatasetLoader.class.getResourceAsStream("/datasets/iris.csv");

        if (resourceStream == null) {
            throw new IOException("Iris dataset non trovato in resources");
        }

        // Crea file temporaneo.
        Path tempFile = Files.createTempFile("iris", ".csv");

        try {
            Files.copy(resourceStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Carica dataset.
            Data data = new Data(tempFile.toString());

            return data;
        } finally {
            // Cleanup: rimuove file temporaneo.
            Files.deleteIfExists(tempFile);
            resourceStream.close();
        }
    }

    /**
     * Lista dataset disponibili.
     *
     * @return array di nomi dataset disponibili
     */
    public static String[] getAvailableDatasets() {
        return new String[] { "Iris", "PlayTennis (Hardcoded)" };
    }
}

//===---------------------------------------------------------------------------===//
