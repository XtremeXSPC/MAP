package com.map.stdgui;

import java.util.List;

/**
 * Minimal client for StdInfoWindow.
 */
public final class StdInfoWindowClient {

    private StdInfoWindowClient() {
        throw new AssertionError("Client class - do not instantiate");
    }

    /**
     * Exercises every public StdInfoWindow method.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        StdGui.init();

        StdInfoWindow.Section section =
                new StdInfoWindow.Section("Runtime", List.of("Java", "JavaFX", "StdInfoWindow"));
        StdInfoWindow.Action action =
                new StdInfoWindow.Action("Details", () -> StdDialog.info("Details", "Action executed."));

        StdWindow window = StdInfoWindow.window("Info Client", "Info Client", "Version 1.0",
                "Reusable informational content.", List.of(section), List.of(action), "Footer", 420, 320, "Close");
        window.show();
        window.close();

        StdGui.exit();
    }
}
