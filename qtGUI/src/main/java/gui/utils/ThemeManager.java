package gui.utils;

//===---------------------------------------------------------------------------===//
// Importazioni Java e JavaFX.
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.Scene;
//===---------------------------------------------------------------------------===//

/**
 * Gestisce i temi e le dimensioni del font dell'applicazione.
 * <p>
 * <strong>Compatibilità legacy:</strong> la nuova astrazione riutilizzabile è
 * {@link com.map.stdgui.StdTheme}. Questa classe resta esportata per non
 * interrompere eventuali client esistenti che dipendono dal pacchetto
 * {@code gui.utils}.
 * <p>
 * Questa classe gestisce:
 * <ul>
 *   <li>Caricamento e persistenza delle preferenze tema/font</li>
 *   <li>Applicazione del CSS alla scena primaria</li>
 *   <li>Variazione dinamica della dimensione font</li>
 * </ul>
 * <p>
 * Implementata come Singleton per fornire un punto unico di configurazione UI.
 *
 * @author Lombardi Costantino
 * @version 1.0.0
 * @since 1.0.0
 * @deprecated usare {@link com.map.stdgui.StdTheme} per il nuovo codice.
 */
@Deprecated(since = "1.1.0", forRemoval = false)
public class ThemeManager {

    //===------------------------------ CONSTANTS ------------------------------===//

    private static final Logger logger = LoggerFactory.getLogger(ThemeManager.class);
    private static final String SETTINGS_FILE = "qtgui.properties";

    private static final String LIGHT_THEME_CSS = "/styles/application.css";
    private static final String DARK_THEME_CSS = "/styles/dark-theme.css";

    //===-------------------------------- ENUMS --------------------------------===//

    /**
     * Enum per i temi disponibili nell'applicazione.
     */
    public enum Theme {
        /** Tema chiaro (light mode). */
        LIGHT("Light"),
        /** Tema scuro (dark mode). */
        DARK("Dark");

        private final String displayName;

        /**
         * Costruttore enum Theme.
         *
         * @param displayName nome visualizzato del tema
         */
        Theme(String displayName) {
            this.displayName = displayName;
        }

        /**
         * Restituisce il nome visualizzato del tema.
         *
         * @return nome visualizzato
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Trova un tema dal suo nome visualizzato.
         *
         * @param displayName nome visualizzato del tema
         * @return il tema corrispondente, o LIGHT se non trovato
         */
        public static Theme fromDisplayName(String displayName) {
            for (Theme theme : values()) {
                if (theme.displayName.equals(displayName)) {
                    return theme;
                }
            }
            return LIGHT; // Default
        }
    }

    /**
     * Enum per le dimensioni del font disponibili nell'applicazione.
     */
    public enum FontSize {
        /** Font piccolo (12px). */
        SMALL("Small (12px)", 12),
        /** Font medio (14px). */
        MEDIUM("Medium (14px)", 14),
        /** Font grande (16px). */
        LARGE("Large (16px)", 16),
        /** Font extra-large (18px). */
        XLARGE("X-Large (18px)", 18);

        private final String displayName;
        private final int size;

        /**
         * Costruttore enum FontSize.
         *
         * @param displayName nome visualizzato della dimensione font
         * @param size dimensione font in pixel
         */
        FontSize(String displayName, int size) {
            this.displayName = displayName;
            this.size = size;
        }

        /**
         * Restituisce il nome visualizzato della dimensione font.
         *
         * @return nome visualizzato
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Restituisce la dimensione del font in pixel.
         *
         * @return dimensione in pixel
         */
        public int getSize() {
            return size;
        }

        /**
         * Trova una dimensione font dal suo nome visualizzato.
         *
         * @param displayName nome visualizzato della dimensione font
         * @return la dimensione font corrispondente, o MEDIUM se non trovata
         */
        public static FontSize fromDisplayName(String displayName) {
            for (FontSize fontSize : values()) {
                if (fontSize.displayName.equals(displayName)) {
                    return fontSize;
                }
            }
            return MEDIUM; // Default
        }
    }

    //===--------------------------- INSTANCE FIELDS ---------------------------===//

    private static ThemeManager instance;

    private Theme currentTheme;
    private FontSize currentFontSize;
    private Scene primaryScene;
    private Properties settings;

    //===---------------------------- CONSTRUCTORS -----------------------------===//

    /**
     * Costruttore privato (Singleton).
     */
    private ThemeManager() {
        this.settings = new Properties();
        loadSettings();
        this.currentTheme = Theme.fromDisplayName(settings.getProperty("theme", "Light"));
        this.currentFontSize = FontSize.fromDisplayName(settings.getProperty("fontSize", "Medium (14px)"));
    }

    //===--------------------------- PUBLIC METHODS ----------------------------===//

    /**
     * Restituisce l'istanza singleton del ThemeManager.
     *
     * @return istanza ThemeManager
     */
    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    /**
     * Imposta la scena primaria dell'applicazione.
     * Deve essere chiamato all'avvio dell'applicazione.
     *
     * @param scene la scena primaria
     */
    public void setPrimaryScene(Scene scene) {
        this.primaryScene = scene;
        applyTheme();
        applyFontSize();
    }

    //===---------------------------- SETTINGS I/O -----------------------------===//

    /**
     * Carica le impostazioni dal file properties.
     */
    private void loadSettings() {
        try {
            java.io.File settingsFile = new java.io.File(SETTINGS_FILE);
            if (settingsFile.exists()) {
                try (FileInputStream fis = new FileInputStream(settingsFile)) {
                    settings.load(fis);
                    logger.info("Impostazioni tema caricate da {}", SETTINGS_FILE);
                }
            } else {
                logger.info("Nessun file di impostazioni trovato, uso valori predefiniti");
            }
        } catch (IOException e) {
            logger.error("Impossibile caricare le impostazioni tema", e);
        }
    }

    /**
     * Salva le impostazioni correnti nel file properties.
     */
    private void saveSettings() {
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            settings.setProperty("theme", currentTheme.getDisplayName());
            settings.setProperty("fontSize", currentFontSize.getDisplayName());
            settings.store(fos, "QT Clustering GUI Settings - Theme");
            logger.info("Impostazioni tema salvate");
        } catch (IOException e) {
            logger.error("Impossibile salvare le impostazioni tema", e);
        }
    }

    //===-------------------------- THEME MANAGEMENT ---------------------------===//

    /**
     * Imposta il tema dell'applicazione.
     *
     * @param theme il tema da applicare
     */
    public void setTheme(Theme theme) {
        if (theme == null) {
            logger.warn("Tentativo di impostare tema null, ignorato");
            return;
        }

        if (this.currentTheme != theme) {
            this.currentTheme = theme;
            applyTheme();
            saveSettings();
            logger.info("Tema cambiato a: {}", theme.getDisplayName());
        }
    }

    /**
     * Imposta la dimensione del font dell'applicazione.
     *
     * @param fontSize la dimensione del font da applicare
     */
    public void setFontSize(FontSize fontSize) {
        if (fontSize == null) {
            logger.warn("Tentativo di impostare fontSize null, ignorato");
            return;
        }

        if (this.currentFontSize != fontSize) {
            this.currentFontSize = fontSize;
            applyFontSize();
            saveSettings();
            logger.info("Dimensione font cambiata a: {}", fontSize.getDisplayName());
        }
    }

    //===------------------------- CONVENIENCE METHODS -------------------------===//

    /**
     * Imposta il tema tramite nome display.
     *
     * @param themeDisplayName nome display del tema (es. "Light", "Dark")
     */
    public void setThemeByName(String themeDisplayName) {
        Theme theme = Theme.fromDisplayName(themeDisplayName);
        setTheme(theme);
    }

    /**
     * Imposta la dimensione del font tramite nome display.
     *
     * @param fontSizeDisplayName nome display della dimensione (es. "Medium (14px)")
     */
    public void setFontSizeByName(String fontSizeDisplayName) {
        FontSize fontSize = FontSize.fromDisplayName(fontSizeDisplayName);
        setFontSize(fontSize);
    }

    //===---------------------------- APPLY METHODS ----------------------------===//

    /**
     * Applica il tema corrente alla scena.
     */
    private void applyTheme() {
        if (primaryScene == null) {
            logger.warn("Scena primaria non impostata, impossibile applicare tema");
            return;
        }

        primaryScene.getStylesheets().clear();

        String cssResource;
        if (currentTheme == Theme.DARK) {
            cssResource = DARK_THEME_CSS;
        } else {
            cssResource = LIGHT_THEME_CSS;
        }

        try {
            String cssPath = getClass().getResource(cssResource).toExternalForm();
            primaryScene.getStylesheets().add(cssPath);
            logger.debug("CSS applicato: {}", cssResource);
        } catch (Exception e) {
            logger.error("Impossibile caricare il file CSS: {}", cssResource, e);
            // Fallback al tema light
            if (currentTheme == Theme.DARK) {
                try {
                    String fallbackPath = getClass().getResource(LIGHT_THEME_CSS).toExternalForm();
                    primaryScene.getStylesheets().add(fallbackPath);
                    logger.warn("Fallback al tema light");
                } catch (Exception fallbackException) {
                    logger.error("Impossibile caricare anche il tema light", fallbackException);
                }
            }
        }
    }

    /**
     * Applica la dimensione del font corrente alla scena.
     */
    private void applyFontSize() {
        if (primaryScene == null) {
            logger.warn("Scena primaria non impostata, impossibile applicare dimensione font");
            return;
        }

        String fontSizeStyle = String.format("-fx-font-size: %dpx;", currentFontSize.getSize());

        // Applica il font size globalmente
        primaryScene.getRoot().setStyle(fontSizeStyle);

        logger.debug("Dimensione font applicata: {}px", currentFontSize.getSize());
    }

    //===------------------------------ ACCESSORS ------------------------------===//

    /**
     * Restituisce il tema corrente.
     *
     * @return il tema corrente
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Restituisce la dimensione del font corrente.
     *
     * @return la dimensione del font corrente
     */
    public FontSize getCurrentFontSize() {
        return currentFontSize;
    }

    /**
     * Restituisce il nome display del tema corrente.
     *
     * @return nome display del tema
     */
    public String getCurrentThemeDisplayName() {
        return currentTheme.getDisplayName();
    }

    /**
     * Restituisce il nome display della dimensione font corrente.
     *
     * @return nome display della dimensione font
     */
    public String getCurrentFontSizeDisplayName() {
        return currentFontSize.getDisplayName();
    }

    /**
     * Verifica se il tema corrente è dark mode.
     *
     * @return true se dark mode attivo
     */
    public boolean isDarkMode() {
        return currentTheme == Theme.DARK;
    }

    /**
     * Alterna tra light e dark mode.
     */
    public void toggleTheme() {
        if (currentTheme == Theme.LIGHT) {
            setTheme(Theme.DARK);
        } else {
            setTheme(Theme.LIGHT);
        }
    }
}

//===---------------------------------------------------------------------------===//
