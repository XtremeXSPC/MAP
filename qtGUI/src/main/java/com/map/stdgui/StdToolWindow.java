package com.map.stdgui;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Reusable tool-style window with controls, central content, and footer actions.
 */
public final class StdToolWindow {

    /** Choice control rendered as a label and combo box. */
    public record Choice(String label, List<String> options, int selectedIndex, IntConsumer onSelect) {
        public Choice {
            Objects.requireNonNull(label, "label");
            options = List.copyOf(Objects.requireNonNull(options, "options"));
            Objects.requireNonNull(onSelect, "onSelect");
        }
    }

    /** Toggle control rendered as a check box. */
    public record Toggle(String label, boolean selected, Consumer<Boolean> onChange) {
        public Toggle {
            Objects.requireNonNull(label, "label");
            Objects.requireNonNull(onChange, "onChange");
        }
    }

    /** Button action rendered in the tool bar or footer. */
    public record Action(String label, Runnable action) {
        public Action {
            Objects.requireNonNull(label, "label");
            Objects.requireNonNull(action, "action");
        }
    }

    private final StdWindow window;
    private final BorderPane root;

    private StdToolWindow(StdWindow window, BorderPane root) {
        this.window = window;
        this.root = root;
    }

    /**
     * Creates a tool window.
     *
     * @param title window title
     * @param header header label
     * @param choices choice controls
     * @param toggles toggle controls
     * @param toolbarActions toolbar actions
     * @param content central content view
     * @param footerText footer summary text
     * @param footerActions footer actions
     * @param closeLabel close button label
     * @param width window width
     * @param height window height
     * @return tool window
     */
    public static StdToolWindow create(String title, String header, List<Choice> choices, List<Toggle> toggles,
            List<Action> toolbarActions, StdView content, String footerText, List<Action> footerActions,
            String closeLabel, double width, double height) {
        Objects.requireNonNull(choices, "choices");
        Objects.requireNonNull(toggles, "toggles");
        Objects.requireNonNull(toolbarActions, "toolbarActions");
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(footerActions, "footerActions");

        return StdGui.callAndWait(() -> {
            StdWindow window = new StdWindow(title).size(width, height);
            BorderPane root = new BorderPane();
            StdToolWindow toolWindow = new StdToolWindow(window, root);

            root.setTop(buildTopPanel(header, choices, toggles, toolbarActions));
            root.setCenter(content.root());
            root.setBottom(buildFooter(footerText, footerActions, closeLabel, toolWindow::close));

            window.content(StdView.of("tool-window:" + (title == null ? "" : title), root));
            return toolWindow;
        });
    }

    /**
     * Replaces the central content.
     *
     * @param content replacement content
     */
    public void replaceContent(StdView content) {
        Objects.requireNonNull(content, "content");
        StdGui.runAndWait(() -> root.setCenter(content.root()));
    }

    /**
     * Shows the window without blocking.
     */
    public void show() {
        window.show();
    }

    /**
     * Closes the window.
     */
    public void close() {
        window.close();
    }

    /**
     * Returns the underlying StdWindow for advanced library composition.
     *
     * @return wrapped window
     */
    public StdWindow window() {
        return window;
    }

    /**
     * Returns the native window handle for legacy compatibility.
     *
     * @return native window handle
     * @deprecated exposes the underlying toolkit object and should only be used by legacy adapters
     */
    @Deprecated
    public Object nativeWindow() {
        return window.nativeHandle();
    }

    private static VBox buildTopPanel(String header, List<Choice> choices, List<Toggle> toggles,
            List<Action> toolbarActions) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f0f0f0;");

        if (header != null && !header.isBlank()) {
            Label titleLabel = new Label(header);
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            panel.getChildren().add(titleLabel);
        }

        HBox controls = new HBox(15);
        controls.setPadding(new Insets(5, 0, 0, 0));

        for (Choice choice : choices) {
            controls.getChildren().add(new Label(choice.label()));

            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.getItems().addAll(choice.options());
            if (!choice.options().isEmpty()) {
                int selectedIndex = Math.max(0, Math.min(choice.selectedIndex(), choice.options().size() - 1));
                comboBox.getSelectionModel().select(selectedIndex);
            }
            comboBox.setOnAction(event -> choice.onSelect().accept(comboBox.getSelectionModel().getSelectedIndex()));
            controls.getChildren().add(comboBox);
        }

        for (Toggle toggle : toggles) {
            CheckBox checkBox = new CheckBox(toggle.label());
            checkBox.setSelected(toggle.selected());
            checkBox.setOnAction(event -> toggle.onChange().accept(checkBox.isSelected()));
            controls.getChildren().add(checkBox);
        }

        for (Action action : toolbarActions) {
            Button button = new Button(action.label());
            button.setOnAction(event -> action.action().run());
            controls.getChildren().add(button);
        }

        panel.getChildren().add(controls);
        return panel;
    }

    private static HBox buildFooter(String footerText, List<Action> footerActions, String closeLabel,
            Runnable closeAction) {
        HBox panel = new HBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f0f0f0;");

        Label infoLabel = new Label(footerText == null ? "" : footerText);
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        panel.getChildren().addAll(infoLabel, spacer);

        for (Action action : footerActions) {
            Button button = new Button(action.label());
            button.setOnAction(event -> action.action().run());
            panel.getChildren().add(button);
        }

        if (closeLabel != null && !closeLabel.isBlank()) {
            Button closeButton = new Button(closeLabel);
            closeButton.setOnAction(event -> closeAction.run());
            panel.getChildren().add(closeButton);
        }

        return panel;
    }
}
