package com.map.stdgui;

import java.util.List;

/**
 * Minimal client for StdToolWindow.
 */
public final class StdToolWindowClient {

    private StdToolWindowClient() {
        throw new AssertionError("Client class - do not instantiate");
    }

    /**
     * Exercises every public StdToolWindow method.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        StdGui.init();

        StdToolWindow.Choice choice =
                new StdToolWindow.Choice("Mode:", List.of("A", "B"), 0, index -> System.out.println(index));
        StdToolWindow.Toggle toggle =
                new StdToolWindow.Toggle("Enabled", true, selected -> System.out.println(selected));
        StdToolWindow.Action refresh =
                new StdToolWindow.Action("Refresh", () -> System.out.println("refresh"));
        StdToolWindow.Action export =
                new StdToolWindow.Action("Export", () -> System.out.println("export"));

        StdToolWindow window = StdToolWindow.create("StdToolWindow Client", "Controls", List.of(choice),
                List.of(toggle), List.of(refresh), StdView.text("Tool Content", "Initial content"), "Ready",
                List.of(export), "Close", 480, 260);

        window.replaceContent(StdView.text("Tool Content", "Replacement content"));
        System.out.println("Wrapped window visible: " + window.window().isShowing());
        window.show();
        window.close();

        StdGui.exit();
    }
}
