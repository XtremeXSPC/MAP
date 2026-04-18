package com.map.stdgui;

import java.nio.file.Path;

/**
 * Minimal client for {@link StdFileDialog}.
 */
public final class StdFileDialogClient {

    private StdFileDialogClient() {
    }

    public static void main(String[] args) {
        StdGui.init();

        System.out.println("Open file: "
                + StdFileDialog.openFile("Open File", new StdFileDialog.Filter("CSV", "*.csv")));
        System.out.println("Save file: "
                + StdFileDialog.saveFile("Save File", "example.txt", new StdFileDialog.Filter("TXT", "*.txt")));
        System.out.println("Directory: " + StdFileDialog.chooseDirectory("Choose Directory", Path.of(".")));

        StdGui.exit();
    }
}
