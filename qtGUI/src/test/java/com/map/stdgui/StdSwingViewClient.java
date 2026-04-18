package com.map.stdgui;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Minimal client for StdSwingView.
 */
public final class StdSwingViewClient {

    private StdSwingViewClient() {
        throw new AssertionError("Client class - do not instantiate");
    }

    /**
     * Exercises every public StdSwingView method.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        StdGui.init();

        StdSwingView swingView = StdSwingView.create("swing-client");
        swingView.setContent(() -> {
            JPanel panel = new JPanel();
            panel.add(new JLabel("Swing content"));
            return panel;
        });

        System.out.println("Swing view id: " + swingView.id());
        StdWindow window = new StdWindow("StdSwingView Client").content(swingView.view()).size(320, 180);
        window.show();
        window.close();

        StdGui.exit();
    }
}
