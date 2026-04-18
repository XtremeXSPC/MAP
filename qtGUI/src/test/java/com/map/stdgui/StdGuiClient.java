package com.map.stdgui;

/**
 * Minimal client for {@link StdGui}.
 */
public final class StdGuiClient {

    private StdGuiClient() {
    }

    public static void main(String[] args) {
        StdGui.init();

        StdGui.runAndWait(() -> System.out.println("StdGui.runAndWait() on FX thread"));
        String threadName = StdGui.callAndWait(() -> Thread.currentThread().getName());
        System.out.println("FX thread name: " + threadName);

        StdGui.later(() -> System.out.println("StdGui.later() scheduled successfully"));
        StdGui.exit();
    }
}
