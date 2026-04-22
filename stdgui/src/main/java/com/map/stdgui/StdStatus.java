package com.map.stdgui;

import java.util.Objects;
import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.util.Duration;

/**
 * The {@code StdStatus} class presents temporary status messages in an existing
 * status area.
 * <p>
 * It is useful for FXML-owned footers or banners: the caller passes opaque
 * handles once, then uses plain methods such as {@link #success(String)} and
 * {@link #warning(String)}. Visibility, style classes, timers, and GUI-thread
 * dispatching are internal details.
 */
public final class StdStatus implements AutoCloseable {

    /** Default CSS class for success messages. */
    public static final String STYLE_SUCCESS = "label-success";

    /** Default CSS class for warning messages. */
    public static final String STYLE_WARNING = "label-warning";

    /** Default auto-hide timeout used by success and warning messages. */
    public static final long DEFAULT_HIDE_AFTER_MILLIS = 3_000L;

    private final Node container;
    private final Labeled messageTarget;

    private PauseTransition hideTimer;
    private String appliedStyleClass;

    /* Validates the opaque handles once so later status updates are simple. */
    private StdStatus(Object container, Object messageTarget) {
        this.container = requireNode(container, "container");
        this.messageTarget = requireLabeled(messageTarget, "messageTarget");
    }

    /**
     * Creates a status presenter bound to an existing status container and label.
     * The arguments are intentionally opaque so callers do not need JavaFX types.
     *
     * @param container status area whose visibility is controlled
     * @param messageTarget text target whose message and style class are updated
     * @return bound status presenter
     */
    public static StdStatus of(Object container, Object messageTarget) {
        return new StdStatus(container, messageTarget);
    }

    /**
     * Shows a success message using the default success style and timeout.
     * Safe to call from any thread; UI work is dispatched to the JavaFX thread.
     *
     * @param message message to display
     */
    public void success(String message) {
        show(message, STYLE_SUCCESS, DEFAULT_HIDE_AFTER_MILLIS);
    }

    /**
     * Shows a warning message using the default warning style and timeout.
     * Safe to call from any thread; UI work is dispatched to the JavaFX thread.
     *
     * @param message message to display
     */
    public void warning(String message) {
        show(message, STYLE_WARNING, DEFAULT_HIDE_AFTER_MILLIS);
    }

    /**
     * Shows a status message and optionally hides it after the requested delay.
     * Safe to call from any thread; UI work is dispatched to the JavaFX thread.
     *
     * @param message message to display
     * @param styleClass CSS style class to apply to the message target
     * @param hideAfterMillis delay before hiding, or zero/negative to stay visible
     */
    public void show(String message, String styleClass, long hideAfterMillis) {
        Objects.requireNonNull(message, "message");
        StdGui.later(() -> showOnUiThread(message, styleClass, hideAfterMillis));
    }

    /**
     * Hides the status area immediately.
     * Safe to call from any thread; UI work is dispatched to the JavaFX thread.
     */
    public void hide() {
        StdGui.later(this::hideOnUiThread);
    }

    /**
     * Stops pending auto-hide work and hides the status area.
     * Safe to call from any thread; UI work is dispatched to the JavaFX thread.
     */
    @Override
    public void close() {
        hide();
    }

    /* Applies message text, visual state, and optional auto-hide on the GUI thread. */
    private void showOnUiThread(String message, String styleClass, long hideAfterMillis) {
        stopTimer();
        messageTarget.setText(message);
        removeStatusStyleClasses(styleClass);
        appliedStyleClass = normalizedStyleClass(styleClass);
        if (appliedStyleClass != null && !messageTarget.getStyleClass().contains(appliedStyleClass)) {
            messageTarget.getStyleClass().add(appliedStyleClass);
        }
        container.setVisible(true);
        container.setManaged(true);

        if (hideAfterMillis > 0) {
            hideTimer = new PauseTransition(Duration.millis(hideAfterMillis));
            hideTimer.setOnFinished(event -> hideOnUiThread());
            hideTimer.play();
        }
    }

    /* Hides both visual and layout participation for the status area. */
    private void hideOnUiThread() {
        stopTimer();
        container.setVisible(false);
        container.setManaged(false);
    }

    /* Cancels the previous auto-hide timer before a new status state is shown. */
    private void stopTimer() {
        if (hideTimer != null) {
            hideTimer.stop();
            hideTimer = null;
        }
    }

    /* Removes only transient status classes while preserving caller/FXML styling. */
    private void removeStatusStyleClasses(String nextStyleClass) {
        messageTarget.getStyleClass().removeAll(STYLE_SUCCESS, STYLE_WARNING);
        if (appliedStyleClass != null) {
            messageTarget.getStyleClass().remove(appliedStyleClass);
        }
        String normalizedNext = normalizedStyleClass(nextStyleClass);
        if (normalizedNext != null) {
            messageTarget.getStyleClass().remove(normalizedNext);
        }
    }

    /* Treats blank style names as absent to avoid adding empty CSS classes. */
    private static String normalizedStyleClass(String styleClass) {
        return styleClass == null || styleClass.isBlank() ? null : styleClass;
    }

    /* Narrows an opaque caller handle to the internal node type we need. */
    private static Node requireNode(Object value, String name) {
        if (value instanceof Node node) {
            return node;
        }
        throw new IllegalArgumentException(name + " must be a JavaFX node managed by the library");
    }

    /* Narrows an opaque caller handle to something that can display text. */
    private static Labeled requireLabeled(Object value, String name) {
        if (value instanceof Labeled labeled) {
            return labeled;
        }
        throw new IllegalArgumentException(name + " must be a JavaFX labeled control managed by the library");
    }
}
