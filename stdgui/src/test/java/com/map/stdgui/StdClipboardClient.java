package com.map.stdgui;

/**
 * Minimal client for {@link StdClipboard}.
 */
public final class StdClipboardClient {

    private StdClipboardClient() {
    }

    public static void main(String[] args) {
        StdGui.init();

        StdClipboard.putText("clipboard test");
        System.out.println("Has text: " + StdClipboard.hasText());
        System.out.println("Clipboard text: " + StdClipboard.getText());

        StdGui.exit();
    }
}
