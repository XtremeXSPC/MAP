package com.map.stdgui;

import java.util.List;
import java.util.Objects;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
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
        StdWindow window = new StdWindow(title).size(width, height).minSize(420, 320).resizable(true).modal(true);
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
            BorderPane root = new BorderPane();

            VBox content = new VBox(15);
            content.setPadding(new Insets(20));
            content.setAlignment(Pos.TOP_CENTER);

            ScrollPane scrollPane = new ScrollPane(content);
            scrollPane.setFitToWidth(true);
            scrollPane.setPannable(true);
            root.setCenter(scrollPane);

            Label headingLabel = new Label(heading == null ? "" : heading);
            headingLabel.setWrapText(true);
            headingLabel.setMaxWidth(Double.MAX_VALUE);
            headingLabel.setAlignment(Pos.CENTER);
            headingLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            content.getChildren().add(headingLabel);

            if (subheading != null && !subheading.isBlank()) {
                Label subheadingLabel = new Label(subheading);
                subheadingLabel.setWrapText(true);
                subheadingLabel.setMaxWidth(Double.MAX_VALUE);
                subheadingLabel.setAlignment(Pos.CENTER);
                subheadingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");
                content.getChildren().add(subheadingLabel);
            }

            if (body != null && !body.isBlank()) {
                content.getChildren().add(new Separator());
                Label bodyLabel = new Label(body);
                bodyLabel.setWrapText(true);
                bodyLabel.setMaxWidth(Double.MAX_VALUE);
                bodyLabel.setAlignment(Pos.CENTER);
                bodyLabel.setStyle("-fx-text-alignment: center;");
                content.getChildren().add(bodyLabel);
            }

            for (Section section : sections) {
                content.getChildren().add(new Separator());
                content.getChildren().add(sectionBox(section));
            }

            if (!actions.isEmpty()) {
                content.getChildren().add(new Separator());
                for (Action action : actions) {
                    Hyperlink link = new Hyperlink(action.label());
                    link.setOnAction(event -> action.action().run());
                    content.getChildren().add(link);
                }
            }

            if (footer != null && !footer.isBlank()) {
                Label footerLabel = new Label(footer);
                footerLabel.setWrapText(true);
                footerLabel.setMaxWidth(Double.MAX_VALUE);
                footerLabel.setAlignment(Pos.CENTER);
                footerLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
                content.getChildren().add(footerLabel);
            }

            Button closeButton = new Button(closeLabel);
            closeButton.setOnAction(event -> closeAction.run());
            closeButton.setPrefWidth(100);

            HBox buttonBox = new HBox(closeButton);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(10, 20, 20, 20));
            root.setBottom(buttonBox);

            return StdView.of("info:" + (heading == null ? "" : heading), root);
        });
    }

    /* Renders a logical section as a vertical block with a bold title. */
    private static VBox sectionBox(Section section) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10));
        box.setMaxWidth(Double.MAX_VALUE);

        Label title = new Label(section.title());
        title.setWrapText(true);
        title.setStyle("-fx-font-weight: bold;");
        box.getChildren().add(title);

        for (String line : section.lines()) {
            Label label = new Label(line);
            label.setWrapText(true);
            label.setMaxWidth(Double.MAX_VALUE);
            box.getChildren().add(label);
        }

        return box;
    }
}
