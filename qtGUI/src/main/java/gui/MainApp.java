package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
