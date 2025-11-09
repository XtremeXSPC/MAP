package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Applicazione JavaFX principale per la GUI QT Clustering.
 * Punto di ingresso per l'interfaccia grafica utente.
 */
public class MainApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);
    private static final String APP_TITLE = "QT Clustering - Quality Threshold Algorithm";
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        try {
            logger.info("Avvio applicazione GUI QT Clustering...");

            // Carica il layout FXML principale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            Parent root = loader.load();

            // Crea la scena con il foglio di stile
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());

            // Configura keyboard shortcuts globali
            setupKeyboardShortcuts(scene);

            // Configura lo stage
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            // Mostra lo stage
            primaryStage.show();

            logger.info("Applicazione avviata con successo");

        } catch (Exception e) {
            logger.error("Impossibile avviare l'applicazione", e);
            throw new RuntimeException("Impossibile caricare la finestra principale", e);
        }
    }

    /**
     * Configura i keyboard shortcuts globali per l'applicazione.
     *
     * @param scene la scena su cui registrare gli shortcuts
     */
    private void setupKeyboardShortcuts(Scene scene) {
        logger.info("Configurazione keyboard shortcuts...");

        // Ctrl+N - Nuova analisi
        KeyCombination ctrlN = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(ctrlN, () -> {
            logger.debug("Shortcut Ctrl+N premuto");
            // L'azione sarà gestita dal MainController
        });

        // Ctrl+O - Apri file
        KeyCombination ctrlO = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(ctrlO, () -> {
            logger.debug("Shortcut Ctrl+O premuto");
        });

        // Ctrl+S - Salva
        KeyCombination ctrlS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(ctrlS, () -> {
            logger.debug("Shortcut Ctrl+S premuto");
        });

        // Ctrl+E - Export
        KeyCombination ctrlE = new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(ctrlE, () -> {
            logger.debug("Shortcut Ctrl+E premuto");
        });

        // Ctrl+Q - Esci
        KeyCombination ctrlQ = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(ctrlQ, () -> {
            logger.debug("Shortcut Ctrl+Q premuto - Chiusura applicazione");
            stop();
            javafx.application.Platform.exit();
        });

        // F1 - Help
        KeyCombination f1 = new KeyCodeCombination(KeyCode.F1);
        scene.getAccelerators().put(f1, () -> {
            logger.debug("Shortcut F1 premuto");
        });

        // F5 - Refresh/Ricarica
        KeyCombination f5 = new KeyCodeCombination(KeyCode.F5);
        scene.getAccelerators().put(f5, () -> {
            logger.debug("Shortcut F5 premuto");
        });

        logger.info("Keyboard shortcuts configurati: Ctrl+N, Ctrl+O, Ctrl+S, Ctrl+E, Ctrl+Q, F1, F5");
    }

    @Override
    public void stop() {
        logger.info("Chiusura applicazione...");
    }

    /**
     * Restituisce lo stage primario dell'applicazione.
     *
     * @return stage primario
     */
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
}
