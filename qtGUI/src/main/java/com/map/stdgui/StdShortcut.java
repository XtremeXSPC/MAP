package com.map.stdgui;

import java.util.Map;
import java.util.Objects;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;

/**
 * The {@code StdShortcut} class provides static methods for registering
 * keyboard shortcuts on {@link StdWindow} instances.
 * <p>
 * Shortcut strings such as {@code Ctrl+Q} are converted to toolkit-specific key
 * combinations internally.
 */
public final class StdShortcut {

    /* This class provides only static methods. */
    private StdShortcut() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Registers one keyboard shortcut on the given window.
     *
     * @param window target window
     * @param shortcut shortcut text such as {@code Ctrl+Q} or {@code F1}
     * @param action action to run on the JavaFX Application Thread
     */
    public static void register(StdWindow window, String shortcut, Runnable action) {
        Objects.requireNonNull(window, "window");
        Objects.requireNonNull(shortcut, "shortcut");
        Objects.requireNonNull(action, "action");

        StdGui.runAndWait(() -> {
            Scene scene = window.scene();
            if (scene == null) {
                throw new IllegalStateException("Window has no scene for shortcut registration");
            }
            scene.getAccelerators().put(KeyCombination.keyCombination(shortcut), action);
        });
    }

    /**
     * Registers keyboard shortcuts on the given window.
     *
     * @param window target window
     * @param shortcuts map from shortcut text to action
     */
    public static void registerAll(StdWindow window, Map<String, Runnable> shortcuts) {
        Objects.requireNonNull(shortcuts, "shortcuts");
        for (Map.Entry<String, Runnable> shortcut : shortcuts.entrySet()) {
            register(window, shortcut.getKey(), shortcut.getValue());
        }
    }

    /**
     * Registers one keyboard shortcut on the currently focused application window.
     *
     * @param shortcut shortcut text such as {@code Ctrl+Q} or {@code F1}
     * @param action action to run on the JavaFX Application Thread
     */
    public static void registerCurrent(String shortcut, Runnable action) {
        register(StdWindow.current(), shortcut, action);
    }
}
