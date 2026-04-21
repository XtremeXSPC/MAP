package com.map.stdgui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.scene.Scene;

/**
 * The {@code StdTheme} class manages application theme and font-size settings.
 * <p>
 * A theme manager can be attached to one or more {@link StdWindow} instances.
 * It persists simple properties, reloads them on demand, and applies styles
 * without exposing JavaFX {@code Scene} or stylesheet mechanics to callers.
 */
public final class StdTheme {

    /** Default application settings file used by the shared theme manager. */
    public static final Path DEFAULT_SETTINGS_FILE = Path.of("stdgui.properties");

    /** Default light stylesheet bundled with the standalone library. */
    public static final String DEFAULT_LIGHT_STYLESHEET = "/com/map/stdgui/styles/application.css";

    /** Default dark stylesheet bundled with the standalone library. */
    public static final String DEFAULT_DARK_STYLESHEET = "/com/map/stdgui/styles/dark-theme.css";

    private static StdTheme defaultTheme;

    private final Path settingsFile;
    private final Class<?> resourceAnchor;
    private final String lightStylesheet;
    private final String darkStylesheet;
    private final Properties settings;
    private final List<StdWindow> windows;

    private volatile Theme currentTheme;
    private volatile FontSize currentFontSize;

    /**
     * Supported visual themes.
     */
    public enum Theme {
        /** Light theme. */
        LIGHT("Light"),
        /** Dark theme. */
        DARK("Dark");

        private final String displayName;

        Theme(String displayName) {
            this.displayName = displayName;
        }

        /**
         * Returns the user-facing theme name.
         *
         * @return display name
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Resolves a theme by display name, defaulting to light.
         *
         * @param displayName user-facing theme name
         * @return matching theme or light
         */
        public static Theme fromDisplayName(String displayName) {
            for (Theme theme : values()) {
                if (theme.displayName.equals(displayName)) {
                    return theme;
                }
            }
            return LIGHT;
        }
    }

    /**
     * Supported global font sizes.
     */
    public enum FontSize {
        /** Small font size. */
        SMALL("Small (12px)", 12),
        /** Medium font size. */
        MEDIUM("Medium (14px)", 14),
        /** Large font size. */
        LARGE("Large (16px)", 16),
        /** Extra-large font size. */
        XLARGE("X-Large (18px)", 18);

        private final String displayName;
        private final int pixels;

        FontSize(String displayName, int pixels) {
            this.displayName = displayName;
            this.pixels = pixels;
        }

        /**
         * Returns the user-facing font-size name.
         *
         * @return display name
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Returns the font size in CSS pixels.
         *
         * @return font size in pixels
         */
        public int getPixels() {
            return pixels;
        }

        /**
         * Resolves a font size by display name, defaulting to medium.
         *
         * @param displayName user-facing font-size name
         * @return matching font size or medium
         */
        public static FontSize fromDisplayName(String displayName) {
            for (FontSize fontSize : values()) {
                if (fontSize.displayName.equals(displayName)) {
                    return fontSize;
                }
            }
            return MEDIUM;
        }
    }

    /**
     * Creates a theme manager backed by the given properties file.
     *
     * @param settingsFile properties file used for theme persistence
     */
    public StdTheme(Path settingsFile) {
        this(settingsFile, StdTheme.class, DEFAULT_LIGHT_STYLESHEET, DEFAULT_DARK_STYLESHEET);
    }

    /**
     * Creates a theme manager backed by the given properties file and stylesheets.
     *
     * @param settingsFile properties file used for theme persistence
     * @param resourceAnchor class used to resolve stylesheet resources
     * @param lightStylesheet classpath resource path for the light theme
     * @param darkStylesheet classpath resource path for the dark theme
     */
    public StdTheme(Path settingsFile, Class<?> resourceAnchor, String lightStylesheet, String darkStylesheet) {
        this.settingsFile = Objects.requireNonNull(settingsFile, "settingsFile");
        this.resourceAnchor = Objects.requireNonNull(resourceAnchor, "resourceAnchor");
        this.lightStylesheet = normalizeStylesheet(lightStylesheet, DEFAULT_LIGHT_STYLESHEET);
        this.darkStylesheet = normalizeStylesheet(darkStylesheet, DEFAULT_DARK_STYLESHEET);
        this.settings = new Properties();
        this.windows = new CopyOnWriteArrayList<>();
        loadSettings();
    }

    /**
     * Returns the shared theme manager backed by {@link #DEFAULT_SETTINGS_FILE}.
     *
     * @return default theme manager
     */
    public static synchronized StdTheme getDefault() {
        if (defaultTheme == null) {
            defaultTheme = new StdTheme(DEFAULT_SETTINGS_FILE);
        }
        return defaultTheme;
    }

    /**
     * Replaces the shared theme manager with application-specific settings.
     *
     * @param settingsFile properties file used for theme persistence
     * @param resourceAnchor class used to resolve stylesheet resources
     * @param lightStylesheet classpath resource path for the light theme
     * @param darkStylesheet classpath resource path for the dark theme
     * @return configured default theme manager
     */
    public static synchronized StdTheme configureDefault(Path settingsFile, Class<?> resourceAnchor,
            String lightStylesheet, String darkStylesheet) {
        defaultTheme = new StdTheme(settingsFile, resourceAnchor, lightStylesheet, darkStylesheet);
        return defaultTheme;
    }

    /**
     * Attaches this theme manager to a window and applies the current settings.
     *
     * @param window window to theme
     * @return this theme manager for chaining
     */
    public StdTheme attach(StdWindow window) {
        Objects.requireNonNull(window, "window");
        windows.add(window);
        applyTo(window);
        return this;
    }

    /**
     * Detaches a previously attached window from this theme manager.
     * <p>
     * Detached windows keep whatever stylesheets and font size are already
     * applied but no longer receive future theme updates. Callers that close
     * short-lived windows should invoke this method to avoid leaking stage
     * references.
     *
     * @param window window previously passed to {@link #attach(StdWindow)}
     * @return this theme manager for chaining
     */
    public StdTheme detach(StdWindow window) {
        Objects.requireNonNull(window, "window");
        windows.remove(window);
        return this;
    }

    /**
     * Reloads theme settings from disk and reapplies them to attached windows.
     */
    public synchronized void reload() {
        loadSettings();
        applyToAttachedWindows();
    }

    /**
     * Sets the active theme and persists it.
     *
     * @param theme theme to apply
     */
    public synchronized void setTheme(Theme theme) {
        Objects.requireNonNull(theme, "theme");
        if (currentTheme == theme) {
            return;
        }
        currentTheme = theme;
        saveSettings();
        applyToAttachedWindows();
    }

    /**
     * Sets the active theme by display name and persists it.
     *
     * @param displayName user-facing theme name
     */
    public void setTheme(String displayName) {
        setTheme(Theme.fromDisplayName(displayName));
    }

    /**
     * Sets the active font size and persists it.
     *
     * @param fontSize font size to apply
     */
    public synchronized void setFontSize(FontSize fontSize) {
        Objects.requireNonNull(fontSize, "fontSize");
        if (currentFontSize == fontSize) {
            return;
        }
        currentFontSize = fontSize;
        saveSettings();
        applyToAttachedWindows();
    }

    /**
     * Sets the active font size by display name and persists it.
     *
     * @param displayName user-facing font-size name
     */
    public void setFontSize(String displayName) {
        setFontSize(FontSize.fromDisplayName(displayName));
    }

    /**
     * Returns the active theme enum.
     *
     * @return active theme
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Returns the active theme display name.
     *
     * @return active theme display name
     */
    public String getTheme() {
        return currentTheme.getDisplayName();
    }

    /**
     * Returns the active font-size enum.
     *
     * @return active font size
     */
    public FontSize getCurrentFontSize() {
        return currentFontSize;
    }

    /**
     * Returns the active font-size display name.
     *
     * @return active font-size display name
     */
    public String getFontSize() {
        return currentFontSize.getDisplayName();
    }

    /**
     * Returns the available theme display names.
     *
     * @return immutable list of theme names
     */
    public List<String> themes() {
        return Arrays.stream(Theme.values()).map(Theme::getDisplayName).toList();
    }

    /**
     * Returns the available font-size display names.
     *
     * @return immutable list of font-size names
     */
    public List<String> fontSizes() {
        return Arrays.stream(FontSize.values()).map(FontSize::getDisplayName).toList();
    }

    /**
     * Returns true when the active theme is dark.
     *
     * @return true for dark mode
     */
    public boolean isDarkMode() {
        return currentTheme == Theme.DARK;
    }

    /**
     * Toggles between the light and dark themes.
     */
    public void toggleTheme() {
        setTheme(isDarkMode() ? Theme.LIGHT : Theme.DARK);
    }

    /* Reads persisted settings and falls back to the documented defaults. */
    private synchronized void loadSettings() {
        settings.clear();
        if (Files.exists(settingsFile)) {
            try (InputStream input = Files.newInputStream(settingsFile)) {
                settings.load(input);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load theme settings from " + settingsFile, e);
            }
        }

        currentTheme = Theme.fromDisplayName(settings.getProperty("theme", Theme.LIGHT.getDisplayName()));
        currentFontSize = FontSize.fromDisplayName(
                settings.getProperty("fontSize", FontSize.MEDIUM.getDisplayName()));
    }

    /* Writes only the theme-related keys owned by this library class. */
    private synchronized void saveSettings() {
        settings.setProperty("theme", currentTheme.getDisplayName());
        settings.setProperty("fontSize", currentFontSize.getDisplayName());

        try (OutputStream output = Files.newOutputStream(settingsFile)) {
            settings.store(output, "stdgui theme settings");
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save theme settings to " + settingsFile, e);
        }
    }

    /* Pushes the current theme state to every window registered with attach(). */
    private void applyToAttachedWindows() {
        for (StdWindow window : windows) {
            applyTo(window);
        }
    }

    /* Applies both stylesheet and font size to one window on the GUI thread. */
    private void applyTo(StdWindow window) {
        StdGui.runAndWait(() -> {
            Scene scene = window.scene();
            if (scene == null) {
                return;
            }
            applyTheme(scene);
            applyFontSize(scene);
        });
    }

    /* Replaces only the configured theme stylesheets, preserving unrelated CSS. */
    private void applyTheme(Scene scene) {
        String lightUrl = resolveStylesheet(lightStylesheet);
        String darkUrl  = resolveStylesheet(darkStylesheet);

        scene.getStylesheets().removeAll(lightUrl, darkUrl);
        scene.getStylesheets().add(currentTheme == Theme.DARK ? darkUrl : lightUrl);
    }

    /* Resolves a configured classpath stylesheet to its external-form URL. */
    private String resolveStylesheet(String resourcePath) {
        URL resource = resourceAnchor.getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Theme stylesheet not found: " + resourcePath);
        }
        return resource.toExternalForm();
    }

    /* Stores the global font size on the scene root as a CSS declaration. */
    private void applyFontSize(Scene scene) {
        scene.getRoot().setStyle("-fx-font-size: " + currentFontSize.getPixels() + "px;");
    }

    /* Keeps resource paths absolute so Class#getResource resolves them predictably. */
    private static String normalizeStylesheet(String stylesheet, String fallback) {
        String resolved = stylesheet == null || stylesheet.isBlank() ? fallback : stylesheet;
        return resolved.startsWith("/") ? resolved : "/" + resolved;
    }
}
