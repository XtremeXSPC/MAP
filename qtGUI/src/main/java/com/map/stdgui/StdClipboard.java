package com.map.stdgui;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * Clipboard helpers that hide JavaFX clipboard primitives.
 */
public final class StdClipboard {

    private StdClipboard() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Copies the provided text into the system clipboard.
     *
     * @param text text to copy
     */
    public static void putText(String text) {
        StdGui.runAndWait(() -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(text == null ? "" : text);
            Clipboard.getSystemClipboard().setContent(content);
        });
    }

    /**
     * Returns true if the system clipboard currently contains text.
     *
     * @return true when clipboard text is present
     */
    public static boolean hasText() {
        return StdGui.callAndWait(() -> Clipboard.getSystemClipboard().hasString());
    }

    /**
     * Returns the current clipboard text, or an empty string when absent.
     *
     * @return clipboard text or empty string
     */
    public static String getText() {
        return StdGui.callAndWait(() -> {
            String value = Clipboard.getSystemClipboard().getString();
            return value == null ? "" : value;
        });
    }
}
