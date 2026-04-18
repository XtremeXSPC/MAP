package com.map.stdgui;

/**
 * Minimal client for {@link StdWindow}.
 */
public final class StdWindowClient {

    private StdWindowClient() {
    }

    public static void main(String[] args) {
        StdGui.init();

        StdWindow window = new StdWindow("StdWindow Client").content(StdView.text("StdWindow", "Close this window"))
                .size(320, 180).resizable(false);
        window.showAndWait();

        StdGui.exit();
    }
}
