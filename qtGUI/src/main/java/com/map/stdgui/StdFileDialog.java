package com.map.stdgui;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * File and directory selection helpers that hide JavaFX chooser classes.
 */
public final class StdFileDialog {

    /**
     * Describes a selectable file extension filter.
     *
     * @param description user-facing description
     * @param patterns extension patterns such as {@code "*.csv"}
     */
    public record Filter(String description, String... patterns) {

        public Filter {
            Objects.requireNonNull(description, "description");
            Objects.requireNonNull(patterns, "patterns");
            if (patterns.length == 0) {
                throw new IllegalArgumentException("patterns must not be empty");
            }
        }
    }

    private StdFileDialog() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Opens a file-selection dialog and returns the selected file when present.
     *
     * @param title dialog title
     * @param filters optional extension filters
     * @return selected file when present
     */
    public static Optional<Path> openFile(String title, Filter... filters) {
        return StdGui.callAndWait(() -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle(title);
            applyFilters(chooser, filters);
            File file = chooser.showOpenDialog(resolveOwner());
            return file == null ? Optional.empty() : Optional.of(file.toPath());
        });
    }

    /**
     * Opens a save-file dialog and returns the selected destination when present.
     *
     * @param title dialog title
     * @param initialFileName suggested file name
     * @param filters optional extension filters
     * @return selected destination when present
     */
    public static Optional<Path> saveFile(String title, String initialFileName, Filter... filters) {
        return StdGui.callAndWait(() -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle(title);
            if (initialFileName != null && !initialFileName.isBlank()) {
                chooser.setInitialFileName(initialFileName);
            }
            applyFilters(chooser, filters);
            File file = chooser.showSaveDialog(resolveOwner());
            return file == null ? Optional.empty() : Optional.of(file.toPath());
        });
    }

    /**
     * Opens a directory-selection dialog and returns the selected directory when present.
     *
     * @param title dialog title
     * @param initialDirectory suggested initial directory
     * @return selected directory when present
     */
    public static Optional<Path> chooseDirectory(String title, Path initialDirectory) {
        return StdGui.callAndWait(() -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle(title);
            if (initialDirectory != null) {
                File directory = initialDirectory.toFile();
                if (directory.exists() && directory.isDirectory()) {
                    chooser.setInitialDirectory(directory);
                }
            }
            File file = chooser.showDialog(resolveOwner());
            return file == null ? Optional.empty() : Optional.of(file.toPath());
        });
    }

    private static void applyFilters(FileChooser chooser, Filter... filters) {
        if (filters == null) {
            return;
        }
        for (Filter filter : filters) {
            if (filter != null) {
                chooser.getExtensionFilters()
                        .add(new FileChooser.ExtensionFilter(filter.description(), filter.patterns()));
            }
        }
    }

    private static Window resolveOwner() {
        Window fallback = null;
        for (Window window : Window.getWindows()) {
            if (window.isFocused()) {
                return window;
            }
            if (fallback == null && window.isShowing()) {
                fallback = window;
            }
        }
        return fallback;
    }
}
