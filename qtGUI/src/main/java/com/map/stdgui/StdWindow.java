package com.map.stdgui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Reusable window wrapper that hides Stage and Scene setup.
 */
public final class StdWindow {

    private final Stage stage;
    private final List<String> stylesheets;

    /**
     * Creates a new window with the given title.
     *
     * @param title window title
     */
    public StdWindow(String title) {
        this(StdGui.callAndWait(Stage::new), title);
    }

    private StdWindow(Stage stage, String title) {
        this.stage = stage;
        this.stylesheets = new ArrayList<>();
        StdGui.runAndWait(() -> {
            if (title != null) {
                stage.setTitle(title);
            }
        });
    }

    /**
     * Returns a wrapper for the currently focused application window.
     *
     * @return wrapper for the current window
     */
    public static StdWindow current() {
        return StdGui.callAndWait(() -> {
            Stage currentStage = null;
            Stage fallbackStage = null;

            for (Window window : Window.getWindows()) {
                if (!(window instanceof Stage stage) || !window.isShowing()) {
                    continue;
                }
                if (window.isFocused()) {
                    currentStage = stage;
                    break;
                }
                if (fallbackStage == null) {
                    fallbackStage = stage;
                }
            }

            Stage resolved = currentStage != null ? currentStage : fallbackStage;
            if (resolved == null) {
                throw new IllegalStateException("No active JavaFX window available");
            }
            return new StdWindow(resolved, resolved.getTitle());
        });
    }

    /**
     * Sets the content view displayed inside this window.
     *
     * @param view content view
     * @return this window for chaining
     */
    public StdWindow content(StdView view) {
        Objects.requireNonNull(view, "view");
        StdGui.runAndWait(() -> {
            if (stage.getScene() == null) {
                stage.setScene(new Scene(view.root()));
                applyStylesheets();
            } else {
                stage.getScene().setRoot(view.root());
            }
        });
        return this;
    }

    /**
     * Sets the window width and height.
     *
     * @param width window width
     * @param height window height
     * @return this window for chaining
     */
    public StdWindow size(double width, double height) {
        StdGui.runAndWait(() -> {
            stage.setWidth(width);
            stage.setHeight(height);
        });
        return this;
    }

    /**
     * Sets the minimum window width and height.
     *
     * @param width minimum width
     * @param height minimum height
     * @return this window for chaining
     */
    public StdWindow minSize(double width, double height) {
        StdGui.runAndWait(() -> {
            stage.setMinWidth(width);
            stage.setMinHeight(height);
        });
        return this;
    }

    /**
     * Marks the window as resizable or fixed-size.
     *
     * @param enabled true for resizable
     * @return this window for chaining
     */
    public StdWindow resizable(boolean enabled) {
        StdGui.runAndWait(() -> stage.setResizable(enabled));
        return this;
    }

    /**
     * Configures whether the window is modal.
     *
     * @param enabled true for application modal
     * @return this window for chaining
     */
    public StdWindow modal(boolean enabled) {
        StdGui.runAndWait(() -> {
            if (enabled && stage.getModality() == Modality.NONE) {
                stage.initModality(Modality.APPLICATION_MODAL);
            }
        });
        return this;
    }

    /**
     * Adds a stylesheet resource path to this window.
     *
     * @param resourcePath classpath CSS resource
     * @return this window for chaining
     */
    public StdWindow stylesheet(String resourcePath) {
        Objects.requireNonNull(resourcePath, "resourcePath");
        URL resource = StdWindow.class.getResource(resourcePath);
        if (resource == null) {
            throw new IllegalArgumentException("Stylesheet resource not found: " + resourcePath);
        }
        String externalForm = resource.toExternalForm();
        stylesheets.add(externalForm);
        StdGui.runAndWait(this::applyStylesheets);
        return this;
    }

    /**
     * Replaces the currently displayed content view.
     *
     * @param view replacement view
     */
    public void replaceContent(StdView view) {
        content(view);
    }

    /**
     * Replaces the current scene root with the given view.
     *
     * @param view replacement view
     */
    public void replaceRoot(StdView view) {
        Objects.requireNonNull(view, "view");
        StdGui.runAndWait(() -> {
            if (stage.getScene() == null) {
                stage.setScene(new Scene(view.root()));
                applyStylesheets();
            } else {
                stage.getScene().setRoot(view.root());
            }
        });
    }

    /**
     * Replaces the children of a named pane inside the current scene root.
     * Falls back to replacing the scene root when the target container is missing.
     *
     * @param regionId fx:id of the target container
     * @param view replacement view
     */
    public void replaceRegion(String regionId, StdView view) {
        Objects.requireNonNull(regionId, "regionId");
        Objects.requireNonNull(view, "view");

        StdGui.runAndWait(() -> {
            Scene scene = stage.getScene();
            if (scene == null) {
                stage.setScene(new Scene(view.root()));
                applyStylesheets();
                return;
            }

            Node target = scene.getRoot().lookup(regionId.startsWith("#") ? regionId : "#" + regionId);
            if (target instanceof Pane pane) {
                pane.getChildren().setAll(view.root());
                return;
            }

            scene.setRoot(view.root());
        });
    }

    /**
     * Shows the window without blocking the caller.
     */
    public void show() {
        StdGui.runAndWait(stage::show);
    }

    /**
     * Shows the window and blocks until it closes.
     */
    public void showAndWait() {
        StdGui.runAndWait(stage::showAndWait);
    }

    /**
     * Closes the window.
     */
    public void close() {
        StdGui.runAndWait(stage::close);
    }

    /**
     * Returns true if the window is currently visible.
     *
     * @return true when visible
     */
    public boolean isShowing() {
        return StdGui.callAndWait(stage::isShowing);
    }

    /**
     * Returns the native window handle for legacy compatibility.
     * <p>
     * New code should use StdWindow methods instead of casting this handle.
     *
     * @return native window handle
     * @deprecated exposes the underlying toolkit object and should only be used by legacy adapters
     */
    @Deprecated
    public Object nativeHandle() {
        return stage;
    }

    Scene scene() {
        return StdGui.callAndWait(stage::getScene);
    }

    private void applyStylesheets() {
        Scene scene = stage.getScene();
        if (scene == null) {
            return;
        }
        for (String stylesheet : stylesheets) {
            if (!scene.getStylesheets().contains(stylesheet)) {
                scene.getStylesheets().add(stylesheet);
            }
        }
    }
}
