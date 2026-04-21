package com.map.stdgui;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Minimal client for StdStatus.
 */
public final class StdStatusClient {

    private record Handles(HBox container, Label label) {
    }

    private StdStatusClient() {
        throw new AssertionError("Client class - do not instantiate");
    }

    /**
     * Exercises every public StdStatus method.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        StdGui.init();

        Handles handles = StdGui.callAndWait(() -> new Handles(new HBox(), new Label()));
        StdStatus status = StdStatus.of(handles.container(), handles.label());

        System.out.println("Success style: " + StdStatus.STYLE_SUCCESS);
        System.out.println("Warning style: " + StdStatus.STYLE_WARNING);
        System.out.println("Default hide timeout: " + StdStatus.DEFAULT_HIDE_AFTER_MILLIS);

        status.success("Saved");
        flush();
        status.warning("Check settings");
        flush();
        status.show("Custom", "custom-status", 0);
        flush();
        status.hide();
        flush();
        status.close();
        flush();

        System.out.println("Visible: " + handles.container().isVisible());
        System.out.println("Message: " + handles.label().getText());

        StdGui.exit();
    }

    private static void flush() {
        StdGui.runAndWait(() -> {
            // Flushes pending runLater work.
        });
    }
}
