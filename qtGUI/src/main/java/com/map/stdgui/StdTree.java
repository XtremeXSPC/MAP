package com.map.stdgui;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Reusable tree helpers that hide TreeItem wiring and recursive tree operations.
 */
public final class StdTree {

    /** Immutable logical tree node. */
    public record Node(String label, List<Node> children) {
        public Node {
            Objects.requireNonNull(label, "label");
            children = List.copyOf(Objects.requireNonNull(children, "children"));
        }

        /**
         * Creates a leaf node.
         *
         * @param label node label
         */
        public Node(String label) {
            this(label, List.of());
        }
    }

    /** Handle for a bound tree view. */
    public interface Tree {

        /**
         * Replaces the tree root.
         *
         * @param root logical root node
         * @param showRoot true to show the root node
         */
        void root(Node root, boolean showRoot);

        /**
         * Registers a selection callback that receives the selected node label.
         *
         * @param action selection callback
         */
        void onSelect(Consumer<String> action);

        /**
         * Expands all non-leaf nodes.
         */
        void expandAll();

        /**
         * Collapses all non-leaf nodes.
         */
        void collapseAll();
    }

    private StdTree() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Binds reusable tree operations to a toolkit tree object.
     *
     * @param treeHandle native tree handle
     * @return bound tree operations
     */
    public static Tree bind(Object treeHandle) {
        Objects.requireNonNull(treeHandle, "treeHandle");
        if (!(treeHandle instanceof TreeView<?> treeView)) {
            throw new IllegalArgumentException("Expected a JavaFX TreeView handle");
        }

        @SuppressWarnings("unchecked")
        TreeView<String> typedTree = (TreeView<String>) treeView;
        return new FxTree(typedTree);
    }

    /**
     * Creates a tree view window.
     *
     * @param title window and view title
     * @param root root node
     * @param showRoot true to show the root node
     * @param width window width
     * @param height window height
     * @return tree window
     */
    public static StdWindow window(String title, Node root, boolean showRoot, double width, double height) {
        return new StdWindow(title).content(view(title, root, showRoot)).size(width, height);
    }

    /**
     * Creates an embeddable tree view.
     *
     * @param title view title
     * @param root root node
     * @param showRoot true to show the root node
     * @return tree view
     */
    public static StdView view(String title, Node root, boolean showRoot) {
        Objects.requireNonNull(root, "root");
        return StdGui.callAndWait(() -> {
            VBox container = new VBox(10);
            container.setPadding(new Insets(10));

            if (title != null && !title.isBlank()) {
                Label titleLabel = new Label(title);
                titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                container.getChildren().add(titleLabel);
            }

            TreeView<String> treeView = new TreeView<>();
            Tree tree = bind(treeView);
            tree.root(root, showRoot);
            VBox.setVgrow(treeView, Priority.ALWAYS);
            container.getChildren().add(treeView);

            return StdView.of("tree:" + (title == null ? "" : title), container);
        });
    }

    private static final class FxTree implements Tree {

        private final TreeView<String> treeView;

        private FxTree(TreeView<String> treeView) {
            this.treeView = treeView;
        }

        @Override
        public void root(Node root, boolean showRoot) {
            Objects.requireNonNull(root, "root");
            StdGui.runAndWait(() -> {
                treeView.setRoot(toTreeItem(root));
                treeView.setShowRoot(showRoot);
            });
        }

        @Override
        public void onSelect(Consumer<String> action) {
            Objects.requireNonNull(action, "action");
            StdGui.runAndWait(() -> treeView.getSelectionModel().selectedItemProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        if (newValue != null) {
                            action.accept(newValue.getValue());
                        }
                    }));
        }

        @Override
        public void expandAll() {
            StdGui.runAndWait(() -> expand(treeView.getRoot()));
        }

        @Override
        public void collapseAll() {
            StdGui.runAndWait(() -> collapse(treeView.getRoot()));
        }

        private static TreeItem<String> toTreeItem(Node node) {
            TreeItem<String> item = new TreeItem<>(node.label());
            item.setExpanded(true);
            for (Node child : node.children()) {
                item.getChildren().add(toTreeItem(child));
            }
            return item;
        }

        private static void expand(TreeItem<String> item) {
            if (item != null && !item.isLeaf()) {
                item.setExpanded(true);
                for (TreeItem<String> child : item.getChildren()) {
                    expand(child);
                }
            }
        }

        private static void collapse(TreeItem<String> item) {
            if (item != null && !item.isLeaf()) {
                item.setExpanded(false);
                for (TreeItem<String> child : item.getChildren()) {
                    collapse(child);
                }
            }
        }
    }
}
