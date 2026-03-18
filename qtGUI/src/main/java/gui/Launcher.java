package gui;

/**
 * Launcher class per avviare l'applicazione JavaFX da uber JAR.
 *
 * Questo launcher risolve il problema "JavaFX runtime components are missing"
 * che si verifica quando si usa maven-shade-plugin per creare un uber JAR.
 *
 * Il problema nasce perché Application.launch() controlla la presenza dei moduli
 * JavaFX nel module-path, ma in un uber JAR tutto è nel classpath.
 *
 * Questa classe NON estende Application, quindi evita il controllo dei moduli
 * e delega l'avvio a MainApp che è una vera Application JavaFX.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.0.0
 */
public class Launcher {

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruttore privato per impedire l'istanziazione.
     * Questa è una classe launcher con solo metodo main.
     */
    private Launcher() {
        throw new AssertionError("Launcher class - do not instantiate");
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Entry point dell'applicazione.
     * Delega immediatamente a MainApp.main() per avviare JavaFX.
     *
     * @param args argomenti della linea di comando
     */
    public static void main(String[] args) {
        // Delega a MainApp che gestisce JavaFX Application.launch()
        MainApp.main(args);
    }
}

//===---------------------------------------------------------------------------===//
