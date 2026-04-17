package com.map.stdgui;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

/**
 * Standard dialogs that hide JavaFX alert and choice primitives.
 */
public final class StdDialog {

    private StdDialog() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Shows an informational dialog.
     *
     * @param title dialog title
     * @param message dialog content
     */
    public static void info(String title, String message) {
        info(title, null, message);
    }

    /**
     * Shows an informational dialog.
     *
     * @param title dialog title
     * @param header dialog header
     * @param message dialog content
     */
    public static void info(String title, String header, String message) {
        show(Alert.AlertType.INFORMATION, title, header, message);
    }

    /**
     * Shows a warning dialog.
     *
     * @param title dialog title
     * @param message dialog content
     */
    public static void warning(String title, String message) {
        warning(title, null, message);
    }

    /**
     * Shows a warning dialog.
     *
     * @param title dialog title
     * @param header dialog header
     * @param message dialog content
     */
    public static void warning(String title, String header, String message) {
        show(Alert.AlertType.WARNING, title, header, message);
    }

    /**
     * Shows an error dialog.
     *
     * @param title dialog title
     * @param message dialog content
     */
    public static void error(String title, String message) {
        error(title, null, message);
    }

    /**
     * Shows an error dialog.
     *
     * @param title dialog title
     * @param header dialog header
     * @param message dialog content
     */
    public static void error(String title, String header, String message) {
        show(Alert.AlertType.ERROR, title, header, message);
    }

    /**
     * Shows a confirmation dialog and returns true when the user accepts.
     *
     * @param title dialog title
     * @param message dialog content
     * @return true when the user accepts
     */
    public static boolean confirm(String title, String message) {
        return confirm(title, null, message);
    }

    /**
     * Shows a confirmation dialog and returns true when the user accepts.
     *
     * @param title dialog title
     * @param header dialog header
     * @param message dialog content
     * @return true when the user accepts
     */
    public static boolean confirm(String title, String header, String message) {
        return StdGui.callAndWait(() -> {
            Alert alert = buildAlert(Alert.AlertType.CONFIRMATION, title, header, message);
            return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
        });
    }

    /**
     * Shows a choice dialog and returns the selected value when present.
     *
     * @param title dialog title
     * @param prompt prompt label
     * @param defaultChoice default choice
     * @param choices possible choices
     * @param <T> choice type
     * @return selected value when present
     */
    public static <T> Optional<T> choose(String title, String prompt, T defaultChoice, List<T> choices) {
        return choose(title, null, prompt, defaultChoice, choices);
    }

    /**
     * Shows a choice dialog and returns the selected value when present.
     *
     * @param title dialog title
     * @param header dialog header
     * @param prompt prompt label
     * @param defaultChoice default choice
     * @param choices possible choices
     * @param <T> choice type
     * @return selected value when present
     */
    public static <T> Optional<T> choose(String title, String header, String prompt, T defaultChoice,
            List<T> choices) {
        Objects.requireNonNull(choices, "choices");
        if (choices.isEmpty()) {
            throw new IllegalArgumentException("choices must not be empty");
        }

        return StdGui.callAndWait(() -> {
            ChoiceDialog<T> dialog = new ChoiceDialog<>(defaultChoice, choices);
            dialog.setTitle(title);
            dialog.setHeaderText(header);
            dialog.setContentText(prompt);
            return dialog.showAndWait();
        });
    }

    private static void show(Alert.AlertType type, String title, String header, String message) {
        StdGui.runAndWait(() -> buildAlert(type, title, header, message).showAndWait());
    }

    private static Alert buildAlert(Alert.AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        return alert;
    }
}
