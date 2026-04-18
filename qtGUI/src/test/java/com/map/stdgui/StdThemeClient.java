package com.map.stdgui;

import java.nio.file.Path;

/**
 * Minimal client for StdTheme.
 */
public final class StdThemeClient {

    private StdThemeClient() {
        throw new AssertionError("Client class - do not instantiate");
    }

    /**
     * Exercises every public StdTheme method.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        StdGui.init();

        Path settings = Path.of(System.getProperty("java.io.tmpdir"), "stdtheme-client.properties");
        StdTheme theme = new StdTheme(settings);
        StdWindow window = new StdWindow("StdTheme Client")
                .content(StdView.text("theme-client", "Theme client"))
                .size(360, 160);

        System.out.println("Default settings: " + StdTheme.DEFAULT_SETTINGS_FILE);
        System.out.println("Shared manager: " + StdTheme.getDefault().getTheme());
        System.out.println("Themes: " + theme.themes());
        System.out.println("Font sizes: " + theme.fontSizes());
        System.out.println("Light: " + StdTheme.Theme.LIGHT.getDisplayName());
        System.out.println("Theme lookup: " + StdTheme.Theme.fromDisplayName("Dark"));
        System.out.println("Medium: " + StdTheme.FontSize.MEDIUM.getDisplayName());
        System.out.println("Medium pixels: " + StdTheme.FontSize.MEDIUM.getPixels());
        System.out.println("Font lookup: " + StdTheme.FontSize.fromDisplayName("Large (16px)"));

        theme.attach(window);
        theme.setTheme(StdTheme.Theme.LIGHT);
        theme.setTheme("Dark");
        theme.setFontSize(StdTheme.FontSize.MEDIUM);
        theme.setFontSize("Large (16px)");
        theme.toggleTheme();
        theme.reload();

        System.out.println("Current theme enum: " + theme.getCurrentTheme());
        System.out.println("Current theme: " + theme.getTheme());
        System.out.println("Current font enum: " + theme.getCurrentFontSize());
        System.out.println("Current font: " + theme.getFontSize());
        System.out.println("Dark mode: " + theme.isDarkMode());

        window.show();
        window.close();
        StdGui.exit();
    }
}
