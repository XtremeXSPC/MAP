package gui;

//===---------------------------------------------------------------------------===//
// Importazioni JavaFX e utilita'.
import java.nio.file.Path;
import com.map.stdgui.StdGui;
import com.map.stdgui.StdShortcut;
import com.map.stdgui.StdTheme;
import com.map.stdgui.StdWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
//===---------------------------------------------------------------------------===//

/**
 * Applicazione JavaFX principale per la GUI QT Clustering.
 * <p>
 * Questa classe rappresenta il confine di bootstrap JavaFX dell'applicazione:
 * riceve lo {@link Stage} primario dal toolkit e collega la finestra principale
 * alle astrazioni riutilizzabili in {@code com.map.stdgui}.
 * <p>
 * Questa classe gestisce:
 * <ul>
 *   <li>Avvio della JavaFX Application e creazione dello stage principale</li>
 *   <li>Caricamento del layout FXML principale</li>
 *   <li>Inizializzazione della scena e dei shortcut globali</li>
 *   <li>Configurazione del tema tramite {@link StdTheme}</li>
 * </ul>
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.0.0
 */
public class MainApp extends Application {

    //===------------------------------ CONSTANTS ------------------------------===//

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);
    private static final String APP_TITLE = "QT Clustering - Quality Threshold Algorithm";
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;

    //===--------------------------- INSTANCE FIELDS ---------------------------===//

    private Stage primaryStage;

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruttore di default.
     * Invocato automaticamente da JavaFX quando viene lanciata l'applicazione.
     */
    public MainApp() {
        // Costruttore vuoto - l'inizializzazione avviene nel metodo start()
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Metodo di avvio dell'applicazione JavaFX.
     * <p>
     * Questo metodo viene chiamato automaticamente da JavaFX dopo
     * l'invocazione di Application.launch().
     *
     * @param stage lo stage primario fornito da JavaFX
     */
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        try {
            logger.info("Avvio applicazione GUI QT Clustering...");

            // Carica il layout FXML principale.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            Parent root = loader.load();

            // Crea la scena.
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

            // Configura lo stage.
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            // Mostra lo stage.
            primaryStage.show();

            StdWindow mainWindow = StdWindow.current();

            // Applica tema e font salvati usando le risorse CSS dell'applicazione.
            StdTheme.configureDefault(Path.of("qtgui.properties"), MainApp.class,
                    "/styles/application.css", "/styles/dark-theme.css").attach(mainWindow);

            // Configura keyboard shortcuts globali.
            setupKeyboardShortcuts(mainWindow);

            logger.info("Applicazione avviata con successo");

        } catch (Exception e) {
            logger.error("Impossibile avviare l'applicazione", e);
            throw new RuntimeException("Impossibile caricare la finestra principale", e);
        }
    }

    /**
     * Restituisce lo stage primario dell'applicazione.
     *
     * @return stage primario
     * @deprecated mantenuto solo per compatibilita' con eventuali client legacy;
     *             il nuovo codice deve usare le astrazioni di {@code com.map.stdgui}.
     */
    @Deprecated(since = "1.1.0", forRemoval = false)
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Metodo main per lanciare l'applicazione JavaFX.
     *
     * @param args argomenti da linea di comando
     */
    public static void main(String[] args) {
        logger.info("Lancio GUI QT Clustering...");
        launch(args);
    }

    //===---------------------------- SETUP METHODS ----------------------------===//

    /**
     * Configura i keyboard shortcuts globali per l'applicazione.
     *
     * @param window finestra su cui registrare gli shortcuts
     */
    private void setupKeyboardShortcuts(StdWindow window) {
        logger.info("Configurazione keyboard shortcuts...");

        // Ctrl+N - Nuova analisi.
        StdShortcut.register(window, "Ctrl+N", () -> {
            logger.debug("Shortcut Ctrl+N premuto");
            // L'azione sarà gestita dal MainController.
        });

        // Ctrl+O - Apri file.
        StdShortcut.register(window, "Ctrl+O", () -> {
            logger.debug("Shortcut Ctrl+O premuto");
        });

        // Ctrl+S - Salva.
        StdShortcut.register(window, "Ctrl+S", () -> {
            logger.debug("Shortcut Ctrl+S premuto");
        });

        // Ctrl+E - Export.
        StdShortcut.register(window, "Ctrl+E", () -> {
            logger.debug("Shortcut Ctrl+E premuto");
        });

        // Ctrl+Q - Esci.
        StdShortcut.register(window, "Ctrl+Q", () -> {
            logger.debug("Shortcut Ctrl+Q premuto - Chiusura applicazione");
            stop();
            StdGui.exit();
        });

        // F1 - Help.
        StdShortcut.register(window, "F1", () -> {
            logger.debug("Shortcut F1 premuto");
        });

        // F5 - Refresh/Ricarica.
        StdShortcut.register(window, "F5", () -> {
            logger.debug("Shortcut F5 premuto");
        });

        logger.info("Keyboard shortcuts configurati: Ctrl+N, Ctrl+O, Ctrl+S, Ctrl+E, Ctrl+Q, F1, F5");
    }

    @Override
    public void stop() {
        logger.info("Chiusura applicazione...");
    }
}

//===---------------------------------------------------------------------------===//
