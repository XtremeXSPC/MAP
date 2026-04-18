package com.map.stdgui;

import java.util.List;
import javafx.scene.control.TreeView;

/**
 * Minimal client for StdTree.
 */
public final class StdTreeClient {

    private StdTreeClient() {
        throw new AssertionError("Client class - do not instantiate");
    }

    /**
     * Exercises every public StdTree method.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        StdGui.init();

        StdTree.Node root = new StdTree.Node("Results",
                List.of(new StdTree.Node("Cluster 1", List.of(new StdTree.Node("Tuple 0")))));

        TreeView<String> nativeTree = StdGui.callAndWait(TreeView::new);
        StdTree.Tree boundTree = StdTree.bind(nativeTree);
        boundTree.root(root, true);
        boundTree.onSelect(label -> System.out.println("Selected: " + label));
        boundTree.expandAll();
        boundTree.collapseAll();

        StdView view = StdTree.view("Results Tree", root, false);
        System.out.println("Tree view: " + view.id());

        StdWindow window = StdTree.window("Results Tree Window", root, false, 360, 240);
        window.show();
        window.close();

        StdGui.exit();
    }
}
