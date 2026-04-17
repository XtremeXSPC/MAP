package com.map.stdgui;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Wrapper around FXML-backed or programmatic views.
 */
public final class StdView {

    private final String id;
    private final Parent root;
    private final Object controller;

    private StdView(String id, Parent root, Object controller) {
        this.id = id;
        this.root = root;
        this.controller = controller;
    }

    /**
     * Loads a view from an FXML resource path.
     *
     * @param resourcePath classpath resource path
     * @return loaded view
     */
    public static StdView load(String resourcePath) {
        Objects.requireNonNull(resourcePath, "resourcePath");
        return StdGui.callAndWait(() -> {
            URL resource = StdView.class.getResource(resourcePath);
            if (resource == null) {
                throw new IllegalArgumentException("FXML resource not found: " + resourcePath);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loadRoot(loader, resourcePath);
            return new StdView(resourcePath, root, loader.getController());
        });
    }

    /**
     * Creates a simple text-only view.
     *
     * @param title title label
     * @param body body text
     * @return generated text view
     */
    public static StdView text(String title, String body) {
        return StdGui.callAndWait(() -> {
            VBox root = new VBox(12);
            root.setPadding(new Insets(16));

            Label titleLabel = new Label(title == null ? "" : title);
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            Label bodyLabel = new Label(body == null ? "" : body);
            bodyLabel.setWrapText(true);

            root.getChildren().addAll(titleLabel, bodyLabel);
            return new StdView("text:" + (title == null ? "" : title), root, null);
        });
    }

    /**
     * Returns the original resource path or synthetic identifier of this view.
     *
     * @return stable view identifier
     */
    public String id() {
        return id;
    }

    /**
     * Returns the controller instance cast to the requested type.
     *
     * @param controllerType expected controller type
     * @param <T> controller type
     * @return controller instance
     */
    public <T> T controller(Class<T> controllerType) {
        Objects.requireNonNull(controllerType, "controllerType");
        if (controller == null) {
            throw new IllegalStateException("View has no controller: " + id);
        }
        if (!controllerType.isInstance(controller)) {
            throw new IllegalArgumentException(
                    "Controller for view " + id + " is not of type " + controllerType.getName());
        }
        return controllerType.cast(controller);
    }

    static StdView of(String id, Parent root) {
        Objects.requireNonNull(root, "root");
        return new StdView(id == null ? "view" : id, root, null);
    }

    Parent root() {
        return root;
    }

    private static Parent loadRoot(FXMLLoader loader, String resourcePath) {
        try {
            return loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load FXML resource: " + resourcePath, e);
        }
    }
}
