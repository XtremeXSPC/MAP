package gui.dialogs;

//===---------------------------------------------------------------------------===//
// Importazioni JavaFX e utilita'.
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
//===---------------------------------------------------------------------------===//

/**
 * Dialog "About" per visualizzare informazioni sull'applicazione.
 * <p>
 * Questa classe gestisce:
 * <ul>
 *   <li>Creazione di uno {@link Stage} modale dedicato</li>
 *   <li>Composizione della UI con layout base (VBox/HBox)</li>
 *   <li>Visualizzazione di versione, build e tecnologie</li>
 *   <li>Link informativo e pulsante di chiusura</li>
 * </ul>
 * <p>
 * Il dialog viene costruito in modo programmatico (no FXML) per
 * mostrare un esempio minimale di UI JavaFX con layout e controlli.
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
    private final Stage stage;

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruisce il dialog About e inizializza la UI.
     */
    public AboutDialog() {
        this.stage = new Stage();
        initializeDialog();
    }

    //===--------------------------- PRIVATE METHODS ---------------------------===//

    /**
     * Inizializza lo stage e costruisce l'interfaccia grafica.
     */
    private void initializeDialog() {
        stage.setTitle("Informazioni su " + APP_NAME);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setWidth(500);
        stage.setHeight(550);
        stage.setResizable(false);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        // Icona applicazione (simbolo testuale come placeholder).
        Label iconLabel = new Label("🔬");
        iconLabel.setStyle("-fx-font-size: 48px;");

        // Nome applicazione.
        Label appNameLabel = new Label(APP_NAME);
        appNameLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Versione.
        Label versionLabel = new Label("Versione " + APP_VERSION);
        versionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

        // Build.
        Label buildLabel = new Label(APP_BUILD);
        buildLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        root.getChildren().addAll(iconLabel, appNameLabel, versionLabel, buildLabel);

        root.getChildren().add(new Separator());

        // Descrizione.
        Label descLabel = new Label("Interfaccia grafica per l'algoritmo Quality Threshold Clustering");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(450);
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setStyle("-fx-text-alignment: center;");

        root.getChildren().add(descLabel);

        root.getChildren().add(new Separator());

        // Informazioni tecniche.
        VBox techBox = new VBox(5);
        techBox.setAlignment(Pos.CENTER_LEFT);
        techBox.setPadding(new Insets(10));

        Label techLabel = new Label("Tecnologie:");
        techLabel.setStyle("-fx-font-weight: bold;");

        Label javaLabel = new Label("  • Java: " + JAVA_VERSION);
        Label javafxLabel = new Label("  • JavaFX: " + JAVAFX_VERSION);
        Label xchartLabel = new Label("  • XChart: 3.8.5");
        Label logbackLabel = new Label("  • SLF4J + Logback");

        techBox.getChildren().addAll(techLabel, javaLabel, javafxLabel, xchartLabel, logbackLabel);
        root.getChildren().add(techBox);

        root.getChildren().add(new Separator());

        // Autori e crediti.
        VBox creditsBox = new VBox(5);
        creditsBox.setAlignment(Pos.CENTER_LEFT);
        creditsBox.setPadding(new Insets(10));

        Label creditsLabel = new Label("Sviluppato per:");
        creditsLabel.setStyle("-fx-font-weight: bold;");

        Label courseLabel = new Label("  • Corso: Metodi Avanzati di Programmazione (MAP)");
        Label yearLabel = new Label("  • Anno Accademico: 2024/2025");
        Label uniLabel = new Label("  • Università degli Studi di Bari: Dipartimento di Informatica");

        creditsBox.getChildren().addAll(creditsLabel, courseLabel, yearLabel, uniLabel);
        root.getChildren().add(creditsBox);

        root.getChildren().add(new Separator());

        // Link documentazione.
        Hyperlink docsLink = new Hyperlink("Documentazione Progetto");
        docsLink.setOnAction(e -> {
            logger.info("Link documentazione cliccato");
            // In una implementazione reale, aprirebbe il browser.
            showInfo("Documentazione", "La documentazione completa è disponibile in docs/");
        });

        root.getChildren().add(docsLink);

        // Copyright.
        Label copyrightLabel = new Label("© 2024-2025 MAP Team. Tutti i diritti riservati.");
        copyrightLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
        root.getChildren().add(copyrightLabel);

        // Bottone chiudi.
        Button closeButton = new Button("Chiudi");
        closeButton.setOnAction(e -> stage.close());
        closeButton.setPrefWidth(100);

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.getChildren().add(closeButton);

        root.getChildren().add(buttonBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);

        logger.info("AboutDialog inizializzato");
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Mostra il dialog.
     */
    public void show() {
        stage.show();
    }

    //===--------------------------- PRIVATE HELPERS ---------------------------===//

    /**
     * Mostra un dialog informativo con titolo e messaggio.
     *
     * @param title titolo del dialogo
     * @param message messaggio da visualizzare
     */
    private void showInfo(String title, String message) {
        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

//===---------------------------------------------------------------------------===//
