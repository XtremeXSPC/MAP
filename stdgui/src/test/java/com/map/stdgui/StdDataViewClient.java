package com.map.stdgui;

import java.util.List;
import java.util.Map;

/**
 * Minimal client for StdDataView.
 */
public final class StdDataViewClient {

    private StdDataViewClient() {
        throw new AssertionError("Client class - do not instantiate");
    }

    /**
     * Exercises every public StdDataView method.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        StdGui.init();

        StdDataView.TableModel model = new StdDataView.TableModel(
                List.of("Name", "Value"),
                List.of(List.of("radius", "0.5"), List.of("clusters", "3")));
        StdView tableView = StdDataView.tableView("Settings", model);
        StdWindow tableWindow = StdDataView.table("Settings Window", model, 420, 240);

        StdDataView.TreeNode tree = new StdDataView.TreeNode("Root",
                List.of(new StdDataView.TreeNode("Cluster 1",
                        List.of(new StdDataView.TreeNode("Tuple 0", List.of())))));
        StdView treeView = StdDataView.treeView("Cluster Tree", tree, true);
        StdWindow treeWindow = StdDataView.tree("Cluster Tree Window", tree, false, 420, 240);

        StdView tabsView = StdDataView.tabsView("Views", List.of(
                new StdDataView.TabView("Table", tableView),
                new StdDataView.TabView("Tree", treeView)));
        StdWindow tabsWindow = StdDataView.tabs("Views Window",
                List.of(new StdDataView.TabView("Tabs", tabsView)), 520, 320, "Close");
        StdWindow textTabs = StdDataView.tabs("Text Tabs",
                Map.of("Summary", "Completed", "Details", "All methods exercised."), 420, 240);

        tableWindow.close();
        treeWindow.close();
        tabsWindow.close();
        textTabs.close();
        StdGui.exit();
    }
}
