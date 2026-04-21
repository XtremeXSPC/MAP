package com.map.stdgui;

import java.util.List;
import java.util.Objects;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The {@code StdInfoWindow} class provides static factory methods for modal
 * informational windows.
 * <p>
 * A caller supplies headings, sections, optional link actions, and footer text.
 * The JavaFX labels, links, buttons, and layout panes are created internally.
 */
public final class StdInfoWindow {

    /** A titled group of informational lines. */
    public record Section(String title, List<String> lines) {
        public Section {
            Objects.requireNonNull(title, "title");
            lines = List.copyOf(Objects.requireNonNull(lines, "lines"));
        }
    }

    /** A user-visible action rendered as an informational link. */
    public record Action(String label, Runnable action) {
        public Action {
            Objects.requireNonNull(label, "label");
            Objects.requireNonNull(action, "action");
        }
    }

    /* This class provides only static methods. */
    private StdInfoWindow() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Creates an informational modal window.
     *
     * @param title window title
     * @param heading primary heading
     * @param subheading optional subheading
     * @param body optional body text
     * @param sections informational sections
     * @param actions optional link actions
     * @param footer optional footer text
     * @param width window width
     * @param height window height
     * @param closeLabel close button label
     * @return informational window
     */
    public static StdWindow window(String title, String heading, String subheading, String body, List<Section> sections,
            List<Action> actions, String footer, double width, double height, String closeLabel) {
        StdWindow window = new StdWindow(title).size(width, height).resizable(false).modal(true);
        window.content(view(heading, subheading, body, sections, actions, footer,
                closeLabel == null || closeLabel.isBlank() ? "Close" : closeLabel, window::close));
        return window;
    }

    /* Composes all optional pieces into a single modal-friendly content view. */
    private static StdView view(String heading, String subheading, String body, List<Section> sections,
            List<Action> actions, String footer, String closeLabel, Runnable closeAction) {
        Objects.requireNonNull(sections, "sections");
        Objects.requireNonNull(actions, "actions");

        return StdGui.callAndWait(() -> {
            VBox root = new VBox(15);
            root.setPadding(new Insets(20));
            root.setAlignment(Pos.CENTER);

            Label headingLabel = new Label(heading == null ? "" : heading);
            headingLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            root.getChildren().add(headingLabel);

            if (subheading != null && !subheading.isBlank()) {
                Label subheadingLabel = new Label(subheading);
                subheadingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");
                root.getChildren().add(subheadingLabel);
            }

            if (body != null && !body.isBlank()) {
                root.getChildren().add(new Separator());
                Label bodyLabel = new Label(body);
                bodyLabel.setWrapText(true);
                bodyLabel.setMaxWidth(450);
                bodyLabel.setAlignment(Pos.CENTER);
                bodyLabel.setStyle("-fx-text-alignment: center;");
                root.getChildren().add(bodyLabel);
            }

            for (Section section : sections) {
                root.getChildren().add(new Separator());
                root.getChildren().add(sectionBox(section));
            }

            if (!actions.isEmpty()) {
                root.getChildren().add(new Separator());
                for (Action action : actions) {
                    Hyperlink link = new Hyperlink(action.label());
                    link.setOnAction(event -> action.action().run());
                    root.getChildren().add(link);
                }
            }

            if (footer != null && !footer.isBlank()) {
                Label footerLabel = new Label(footer);
                footerLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
                root.getChildren().add(footerLabel);
            }

            Button closeButton = new Button(closeLabel);
            closeButton.setOnAction(event -> closeAction.run());
            closeButton.setPrefWidth(100);

            HBox buttonBox = new HBox(closeButton);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(10, 0, 0, 0));
            root.getChildren().add(buttonBox);

            return StdView.of("info:" + (heading == null ? "" : heading), root);
        });
    }

    /* Renders a logical section as a vertical block with a bold title. */
    private static VBox sectionBox(Section section) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10));

        Label title = new Label(section.title());
        title.setStyle("-fx-font-weight: bold;");
        box.getChildren().add(title);

        for (String line : section.lines()) {
            box.getChildren().add(new Label(line));
        }

        return box;
    }
}
