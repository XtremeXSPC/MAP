package com.map.stdgui;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * The {@code StdDataView} class provides static methods for displaying
 * tabular, hierarchical, and tabbed data.
 * <p>
 * Clients describe data with immutable records and receive {@link StdView} or
 * {@link StdWindow} values. JavaFX table columns, tree items, tabs, and text
 * areas are built inside this class.
 */
public final class StdDataView {

    /** Tabular row-and-column data model. */
    public record TableModel(List<String> columns, List<List<String>> rows) {
        public TableModel {
            columns = List.copyOf(Objects.requireNonNull(columns, "columns"));
            rows = Objects.requireNonNull(rows, "rows").stream().map(List::copyOf).toList();
        }
    }

    /** Hierarchical labeled tree node. */
    public record TreeNode(String label, List<TreeNode> children) {
        public TreeNode {
            Objects.requireNonNull(label, "label");
            children = List.copyOf(Objects.requireNonNull(children, "children"));
        }
    }

    /** One tab containing a reusable StdView. */
    public record TabView(String title, StdView view) {
        public TabView {
            Objects.requireNonNull(title, "title");
            Objects.requireNonNull(view, "view");
        }
    }

    /* This class provides only static methods. */
    private StdDataView() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Creates a tabular view window for structured row-and-column data.
     *
     * @param title window and view title
     * @param model table data model
     * @param width window width
     * @param height window height
     * @return table window
     */
    public static StdWindow table(String title, TableModel model, double width, double height) {
        return new StdWindow(title).content(tableView(title, model)).size(width, height);
    }

    /**
     * Creates an embeddable tabular view for structured row-and-column data.
     *
     * @param title view title
     * @param model table data model
     * @return table view
     */
    public static StdView tableView(String title, TableModel model) {
        Objects.requireNonNull(model, "model");
        return StdGui.callAndWait(() -> {
            VBox root = new VBox(10);
            root.setPadding(new Insets(10));

            if (title != null && !title.isBlank()) {
                Label titleLabel = new Label(title);
                titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                root.getChildren().add(titleLabel);
            }

            TableView<List<String>> table = buildTable(model);
            VBox.setVgrow(table, Priority.ALWAYS);
            root.getChildren().add(table);

            return StdView.of("table:" + (title == null ? "" : title), root);
        });
    }

    /**
     * Creates a tree view window for hierarchical labeled data.
     *
     * @param title window and view title
     * @param root root tree node
     * @param showRoot true to show the root node
     * @param width window width
     * @param height window height
     * @return tree window
     */
    public static StdWindow tree(String title, TreeNode root, boolean showRoot, double width, double height) {
        return new StdWindow(title).content(treeView(title, root, showRoot)).size(width, height);
    }

    /**
     * Creates an embeddable tree view for hierarchical labeled data.
     *
     * @param title view title
     * @param root root tree node
     * @param showRoot true to show the root node
     * @return tree view
     */
    public static StdView treeView(String title, TreeNode root, boolean showRoot) {
        Objects.requireNonNull(root, "root");
        return StdGui.callAndWait(() -> {
            VBox container = new VBox(10);
            container.setPadding(new Insets(10));

            if (title != null && !title.isBlank()) {
                Label titleLabel = new Label(title);
                titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                container.getChildren().add(titleLabel);
            }

            TreeView<String> tree = new TreeView<>(buildTree(root));
            tree.setShowRoot(showRoot);
            VBox.setVgrow(tree, Priority.ALWAYS);
            container.getChildren().add(tree);

            return StdView.of("tree:" + (title == null ? "" : title), container);
        });
    }

    /**
     * Creates a tabbed text window where each map entry becomes one tab.
     *
     * @param title window title
     * @param tabs tab title to text body map
     * @param width window width
     * @param height window height
     * @return tabbed text window
     */
    public static StdWindow tabs(String title, Map<String, String> tabs, double width, double height) {
        List<TabView> tabViews = tabs.entrySet().stream()
                .map(entry -> new TabView(entry.getKey(), textAreaView(entry.getKey(), entry.getValue())))
                .toList();
        return tabs(title, tabViews, width, height);
    }

    /**
     * Creates a tabbed window from reusable views.
     *
     * @param title window title
     * @param tabs tab view definitions
     * @param width window width
     * @param height window height
     * @return tabbed window
     */
    public static StdWindow tabs(String title, List<TabView> tabs, double width, double height) {
        return new StdWindow(title).content(tabsView(title, tabs)).size(width, height);
    }

    /**
     * Creates a tabbed window from reusable views with a close button.
     *
     * @param title window title
     * @param tabs tab view definitions
     * @param width window width
     * @param height window height
     * @param closeLabel close button label
     * @return tabbed window
     */
    public static StdWindow tabs(String title, List<TabView> tabs, double width, double height, String closeLabel) {
        StdWindow window = new StdWindow(title).size(width, height);
        window.content(tabsView(title, tabs, closeLabel, window::close));
        return window;
    }

    /**
     * Creates an embeddable tabbed view from reusable views.
     *
     * @param title view title
     * @param tabs tab view definitions
     * @return tabbed view
     */
    public static StdView tabsView(String title, List<TabView> tabs) {
        return tabsView(title, tabs, null, null);
    }

    /**
     * Creates an embeddable tabbed view from reusable views with an optional close button.
     *
     * @param title view title
     * @param tabs tab view definitions
     * @param closeLabel optional close button label
     * @param closeAction optional close button action
     * @return tabbed view
     */
    public static StdView tabsView(String title, List<TabView> tabs, String closeLabel, Runnable closeAction) {
        Objects.requireNonNull(tabs, "tabs");
        return StdGui.callAndWait(() -> {
            BorderPane root = new BorderPane();
            root.setPadding(new Insets(10));

            TabPane tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

            for (TabView tabView : tabs) {
                Tab tab = new Tab(tabView.title());
                tab.setContent(tabView.view().root());
                tabPane.getTabs().add(tab);
            }

            root.setCenter(tabPane);

            if (closeLabel != null && closeAction != null) {
                Button closeButton = new Button(closeLabel);
                closeButton.setOnAction(event -> closeAction.run());

                HBox footer = new HBox(closeButton);
                footer.setAlignment(Pos.CENTER_RIGHT);
                footer.setPadding(new Insets(10, 0, 0, 0));
                root.setBottom(footer);
            }

            return StdView.of("tabs:" + (title == null ? "" : title), root);
        });
    }

    /* Builds a read-only text view used by map-based tab windows. */
    private static StdView textAreaView(String title, String text) {
        return StdGui.callAndWait(() -> {
            TextArea textArea = new TextArea(text == null ? "" : text);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            return StdView.of("text-tab:" + (title == null ? "" : title), textArea);
        });
    }

    /* Converts the logical table model into columns with safe row indexing. */
    private static TableView<List<String>> buildTable(TableModel model) {
        TableView<List<String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);

        for (int i = 0; i < model.columns().size(); i++) {
            final int columnIndex = i;
            TableColumn<List<String>, String> column = new TableColumn<>(model.columns().get(i));
            column.setCellValueFactory(data -> {
                List<String> row = data.getValue();
                String value = row != null && columnIndex < row.size() ? row.get(columnIndex) : "";
                return new SimpleStringProperty(value == null ? "" : value);
            });
            table.getColumns().add(column);
        }

        table.setItems(FXCollections.observableArrayList(model.rows()));
        return table;
    }

    /* Recursively converts immutable tree records into native tree items. */
    private static TreeItem<String> buildTree(TreeNode node) {
        TreeItem<String> item = new TreeItem<>(node.label());
        item.setExpanded(true);
        for (TreeNode child : node.children()) {
            item.getChildren().add(buildTree(child));
        }
        return item;
    }
}
