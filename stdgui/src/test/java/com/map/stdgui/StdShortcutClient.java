package com.map.stdgui;

import java.util.Map;

/**
 * Minimal client for StdShortcut.
 */
public final class StdShortcutClient {

    private StdShortcutClient() {
        throw new AssertionError("Client class - do not instantiate");
    }

    /**
     * Exercises every public StdShortcut method.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        StdGui.init();

        StdWindow window = new StdWindow("StdShortcut Client")
                .content(StdView.text("Shortcuts", "Try Ctrl+L, Ctrl+D, or F1."))
                .size(360, 160);
        window.show();

        StdShortcut.register(window, "Ctrl+L", () -> System.out.println("Lightweight shortcut"));
        StdShortcut.registerAll(window, Map.of(
                "Ctrl+D", () -> System.out.println("Mapped shortcut"),
                "F1", () -> System.out.println("Help shortcut")));
        StdShortcut.registerCurrent("Ctrl+W", window::close);

        window.close();
        StdGui.exit();
    }
}
