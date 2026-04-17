package com.map.stdgui;

import java.util.Objects;
import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.StackPane;

/**
 * Reusable Swing content bridge that hides SwingNode and EDT coordination.
 */
public final class StdSwingView {

    private final String id;
    private final SwingNode node;
    private final StdView view;

    private StdSwingView(String id, SwingNode node, StdView view) {
        this.id = id;
        this.node = node;
        this.view = view;
    }

    /**
     * Creates an empty Swing-backed view.
     *
     * @param id stable view identifier
     * @return Swing view handle
     */
    public static StdSwingView create(String id) {
        String resolvedId = id == null || id.isBlank() ? "swing-view" : id;
        return StdGui.callAndWait(() -> {
            SwingNode node = new SwingNode();
            StackPane root = new StackPane(node);
            return new StdSwingView(resolvedId, node, StdView.of(resolvedId, root));
        });
    }

    /**
     * Replaces the embedded Swing component, creating it on the Swing event dispatch thread.
     *
     * @param contentFactory Swing component factory
     */
    public void setContent(Supplier<? extends JComponent> contentFactory) {
        Objects.requireNonNull(contentFactory, "contentFactory");
        SwingUtilities.invokeLater(() -> node.setContent(contentFactory.get()));
    }

    /**
     * Returns this view as a reusable StdView.
     *
     * @return embeddable view
     */
    public StdView view() {
        return view;
    }

    /**
     * Returns the stable view identifier.
     *
     * @return view identifier
     */
    public String id() {
        return id;
    }
}
