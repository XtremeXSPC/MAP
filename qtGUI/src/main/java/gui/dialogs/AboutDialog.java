package gui.dialogs;

//===---------------------------------------------------------------------------===//
// Importazioni Java standard e utilita'.
import java.util.List;
import com.map.stdgui.StdDialog;
import com.map.stdgui.StdInfoWindow;
import com.map.stdgui.StdWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//===---------------------------------------------------------------------------===//

/**
 * Dialog "About" per visualizzare informazioni sull'applicazione.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.0.0
 */
public class AboutDialog {

    //===------------------------------ CONSTANTS ------------------------------===//

    // Logger per la classe AboutDialog.
    private static final Logger logger = LoggerFactory.getLogger(AboutDialog.class);

    // Costanti delle informazioni sull'applicazione.
    private static final String APP_NAME = "QT Clustering GUI";
    private static final String APP_VERSION = "1.0.0";
    private static final String APP_BUILD = "Beta 1";
    private static final String JAVA_VERSION = System.getProperty("java.version");
    private static final String JAVAFX_VERSION = System.getProperty("javafx.version", "21+");

    //===--------------------------- INSTANCE FIELDS ---------------------------===//

    // Finestra del dialog.
    private final StdWindow window;

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruisce il dialog About e inizializza la UI.
     */
    public AboutDialog() {
        this.window = StdInfoWindow.window("Informazioni su " + APP_NAME, APP_NAME,
                "Versione " + APP_VERSION + " - " + APP_BUILD,
                "Interfaccia grafica per l'algoritmo Quality Threshold Clustering",
                List.of(
                        new StdInfoWindow.Section("Tecnologie:",
                                List.of("Java: " + JAVA_VERSION, "JavaFX: " + JAVAFX_VERSION, "XChart: 3.8.5",
                                        "SLF4J + Logback")),
                        new StdInfoWindow.Section("Sviluppato per:",
                                List.of("Corso: Metodi Avanzati di Programmazione (MAP)",
                                        "Anno Accademico: 2024/2025",
                                        "Università degli Studi di Bari: Dipartimento di Informatica"))),
                List.of(new StdInfoWindow.Action("Documentazione Progetto",
                        () -> showInfo("Documentazione", "La documentazione completa è disponibile in docs/"))),
                "© 2024-2025 MAP Team. Tutti i diritti riservati.", 500, 550, "Chiudi");

        logger.info("AboutDialog inizializzato");
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Mostra il dialog.
     */
    public void show() {
        window.show();
    }

    //===--------------------------- PRIVATE HELPERS ---------------------------===//

    /**
     * Mostra un dialog informativo con titolo e messaggio.
     *
     * @param title titolo del dialogo
     * @param message messaggio da visualizzare
     */
    private void showInfo(String title, String message) {
        StdDialog.info(title, message);
    }
}

//===---------------------------------------------------------------------------===//
