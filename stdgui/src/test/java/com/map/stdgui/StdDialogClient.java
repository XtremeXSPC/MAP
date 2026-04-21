package com.map.stdgui;

import java.util.List;

/**
 * Minimal client for {@link StdDialog}.
 */
public final class StdDialogClient {

    private StdDialogClient() {
    }

    public static void main(String[] args) {
        StdGui.init();

        StdDialog.info("StdDialog", "Info", "Informational dialog test");
        StdDialog.warning("StdDialog", "Warning", "Warning dialog test");
        StdDialog.error("StdDialog", "Error", "Error dialog test");
        boolean confirmed = StdDialog.confirm("StdDialog", "Confirm", "Press OK to confirm");
        System.out.println("Confirmed: " + confirmed);
        System.out.println("Choice: "
                + StdDialog.choose("StdDialog", "Choose", "Pick one option", "A", List.of("A", "B", "C")));

        StdGui.exit();
    }
}
