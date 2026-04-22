# StdGUI User Guide

StdGUI is a small JavaFX utility library for Java 21 applications. It is inspired by the style of Sedgewick and Wayne's
standard libraries: each class has a narrow responsibility, common actions are exposed through compact static methods,
and stateful concepts such as windows and themes use small instance objects.

The purpose of StdGUI is not to replace JavaFX. It is to make ordinary application code say what it wants to do without
constantly handling `Stage`, `Scene`, `Node`, `FXMLLoader`, `Platform.runLater`, `Task`, or native dialogs directly.

This guide explains how to use the library, why the API is shaped this way, and how to extend it without breaking the
abstraction boundary.

## Contents

- [StdGUI User Guide](#stdgui-user-guide)
  - [Contents](#contents)
  - [Design Philosophy](#design-philosophy)
    - [What StdGUI Hides](#what-stdgui-hides)
  - [Installation](#installation)
  - [Starting And Stopping The GUI Runtime](#starting-and-stopping-the-gui-runtime)
  - [Views And Windows](#views-and-windows)
    - [Replacing Content](#replacing-content)
    - [Current Window](#current-window)
  - [FXML And JPMS Resources](#fxml-and-jpms-resources)
    - [Rationale](#rationale)
  - [Themes And Styling](#themes-and-styling)
    - [Styling Choices](#styling-choices)
  - [Dialogs, Files, And Clipboard](#dialogs-files-and-clipboard)
  - [Background Work](#background-work)
    - [Rationale](#rationale-1)
  - [Charts](#charts)
  - [Tables, Trees, And Tabs](#tables-trees-and-tabs)
    - [Tables](#tables)
    - [Trees](#trees)
    - [Tabs](#tabs)
  - [Tool Windows And Info Windows](#tool-windows-and-info-windows)
  - [Status Messages And Shortcuts](#status-messages-and-shortcuts)
  - [Swing Interoperability](#swing-interoperability)
  - [Threading Model](#threading-model)
    - [Practical Guidelines](#practical-guidelines)
  - [Testing Clients](#testing-clients)
  - [Extending StdGUI](#extending-stdgui)
    - [Adding A New Library Class](#adding-a-new-library-class)
    - [Extending From An Application](#extending-from-an-application)
    - [Extension Heuristics](#extension-heuristics)
  - [Common Pitfalls](#common-pitfalls)
    - [FXML Works In Tests But Not From A Modular Consumer](#fxml-works-in-tests-but-not-from-a-modular-consumer)
    - [A Shortcut Registration Fails](#a-shortcut-registration-fails)
    - [Theme Changes Do Not Affect A Window](#theme-changes-do-not-affect-a-window)
    - [A Dialog Freezes The UI](#a-dialog-freezes-the-ui)
    - [A Background Task Touches UI State](#a-background-task-touches-ui-state)
    - [A Custom JavaFX Node Cannot Become A StdView Outside The Package](#a-custom-javafx-node-cannot-become-a-stdview-outside-the-package)
  - [Summary](#summary)

## Design Philosophy

StdGUI follows four rules.

1. Keep JavaFX behind the library boundary.
2. Use static methods for stateless operations.
3. Use small instance objects when state matters.
4. Make lifecycle and threading explicit.

This is why `StdDialog.info(...)` is static, but `StdWindow` is an object. Showing an alert has no persistent state after
it closes. A window has title, content, size, stylesheets, visibility, and future replacement operations, so it deserves
an instance API.

This also explains why the library does not launch JavaFX from a static initializer. Starting a GUI runtime is a lifecycle
decision. The caller should do it deliberately with `StdGui.init()` unless the application is already a JavaFX `Application`.

### What StdGUI Hides

| Library concept         | JavaFX internals hidden                                   |
| ----------------------- | --------------------------------------------------------- |
| `StdGui`                | `Platform.startup`, `Platform.runLater`, latches, futures |
| `StdView`               | `Parent`, `FXMLLoader`, generated view roots              |
| `StdWindow`             | `Stage`, `Scene`, modality, scene-root replacement        |
| `StdDialog`             | `Alert`, `ChoiceDialog`, blocking dialog result handling  |
| `StdFileDialog`         | `FileChooser`, `DirectoryChooser`, owner lookup           |
| `StdAsync` and `StdJob` | `Task`, worker threads, lifecycle handlers                |
| `StdTheme`              | Scene stylesheets, persisted properties, font-size CSS    |
| `StdChart`              | JavaFX chart nodes, axes, series, PNG snapshots           |
| `StdDataView`           | `TableView`, `TreeView`, `TabPane`, text areas            |
| `StdToolWindow`         | Tool layout panes, combo boxes, buttons, check boxes      |
| `StdStatus`             | Node visibility, style classes, auto-hide timers          |
| `StdShortcut`           | `KeyCombination` and scene accelerators                   |
| `StdSwingView`          | `SwingNode` and Swing EDT coordination                    |

## Installation

StdGUI is a Maven module named `stdgui`.

Build it from the repository root:

```sh
mvn -q -pl stdgui -am test-compile
```

Install it into the local Maven repository:

```sh
mvn -q -pl stdgui -am install
```

Use it from another Maven project:

```xml
<dependency>
    <groupId>com.map</groupId>
    <artifactId>stdgui</artifactId>
    <version>1.0.0</version>
</dependency>
```

StdGUI currently targets Java 21 and JavaFX 21.0.1, inherited from the parent project.

For a modular application, add:

```java
module my.application {
    requires com.map.stdgui;
}
```

If your application also declares JavaFX controllers, controls, or FXML files, you may need additional `requires` and
`opens` directives. The FXML section below gives the full pattern.

## Starting And Stopping The GUI Runtime

Use `StdGui` when your program is not already a JavaFX `Application`.

```java
import com.map.stdgui.StdGui;
import com.map.stdgui.StdView;
import com.map.stdgui.StdWindow;

public final class HelloStdGui {
    public static void main(String[] args) {
        StdGui.init();

        new StdWindow("Hello")
                .content(StdView.text("Hello", "StdGUI is running."))
                .size(360, 180)
                .show();
    }
}
```

Call `StdGui.exit()` when a command-line style client has finished:

```java
StdGui.exit();
```

If your application extends `javafx.application.Application`, do not call `StdGui.init()` inside `start(...)`. JavaFX has
already started the toolkit. You can still use the rest of the library:

```java
public final class MyApp extends Application {
    @Override
    public void start(Stage ignored) {
        new StdWindow("My App")
                .content(StdView.text("Ready", "Application started."))
                .size(480, 240)
                .show();
    }
}
```

The `Stage` parameter is not passed into the public StdGUI API. This is intentional: application code can treat StdGUI
windows as the stable surface.

## Views And Windows

A `StdView` is reusable GUI content. A `StdWindow` is a top-level window that can display a view.

```java
StdView view = StdView.text("Result", "The computation completed.");

StdWindow window = new StdWindow("Results")
        .content(view)
        .size(420, 220)
        .minSize(320, 160)
        .resizable(true);

window.show();
```

Use `show()` for non-blocking display and `showAndWait()` for modal or wizard-like flows where the caller should wait
until the window closes.

```java
new StdWindow("About")
        .content(StdView.text("About", "Version 1.0.0"))
        .size(360, 180)
        .modal(true)
        .showAndWait();
```

`modal(true)` and `modal(false)` must be configured before a window is first
shown. If either method is called after the stage is already visible, StdGUI
no-ops instead of exposing the toolkit exception.

### Replacing Content

Use `replaceContent(...)` when the whole window changes:

```java
window.replaceContent(StdView.text("Updated", "The view changed."));
```

Use `replaceRegion(...)` when an FXML or library-created view contains a pane
with an `fx:id`:

```java
window.replaceRegion("detailsPane", StdView.text("Details", "New content"));
```

If the target region cannot be found or is not a pane, StdGUI falls back to replacing the whole scene root. That behavior
favors keeping the application usable over failing because a region id changed.

### Current Window

`StdWindow.current()` returns a wrapper for the focused visible JavaFX stage. Repeated calls for the same stage return
the same `StdWindow` instance, so state such as registered stylesheets is preserved.

```java
StdShortcut.register(StdWindow.current(), "Ctrl+Q", StdGui::exit);
```

This method requires an already visible window. If there is no active window, it throws `IllegalStateException`.

## FXML And JPMS Resources

FXML is optional. Prefer the higher-level factories (`StdView.text`, `StdChart`, `StdDataView`, `StdToolWindow`) when
they are enough. Use FXML when you need a custom layout.

Configure a default resource anchor once at startup:

```java
StdView.configureResourceAnchor(MyApplication.class);
StdView mainView = StdView.load("/my/app/views/main.fxml");
```

Or use a per-call anchor when one application loads views from multiple
modules or class loaders:

```java
StdView preferences = StdView.load(PreferencesController.class,
        "/my/app/preferences/preferences.fxml");
```

The anchor is important under JPMS. Resource lookup is module-aware. In a named module, the package that owns the FXML
resource must be opened to `com.map.stdgui`, and the package that owns the controller must be opened to `javafx.fxml`.

```java
module my.application {
    requires com.map.stdgui;
    requires javafx.controls;
    requires javafx.fxml;

    exports my.app;
    opens my.app.views to com.map.stdgui;
    opens my.app.controllers to javafx.fxml;
}
```

If the FXML file and controller live in the same package, open that package to both modules:

```java
opens my.app.ui to com.map.stdgui, javafx.fxml;
```

Example FXML:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.Label?>
<?import javafx.scene.layout.VBox?>

<VBox spacing="8" xmlns="http://javafx.com/javafx/21"
      xmlns:fx="http://javafx.com/fxml/1"
      fx:controller="my.app.controllers.MainController">
    <Label text="Hello from FXML"/>
</VBox>
```

Access the controller without handling `FXMLLoader` directly:

```java
StdView view = StdView.load("/my/app/views/main.fxml");
MainController controller = view.controller(MainController.class);
```

### Rationale

StdGUI does not expose `FXMLLoader` because doing so would leak JavaFX into the public API. The application only needs
to know the logical resource path and, if needed, the expected controller type.

The explicit anchor is a deliberate design choice. It avoids fragile classpath guessing and makes modular resource
ownership visible.

## Themes And Styling

`StdTheme` manages a theme and font-size setting across one or more windows. It persists those choices to a properties
file and reapplies them when changed.

```java
Path settings = Path.of("my-app.properties");

StdTheme theme = StdTheme.configureDefault(
        settings,
        MyApplication.class,
        "/my/app/styles/application.css",
        "/my/app/styles/dark-theme.css");

StdWindow window = new StdWindow("Themed")
        .content(StdView.text("Theme", "This window follows StdTheme."))
        .size(420, 220);

theme.attach(window);
window.show();
```

Under JPMS, open the package that contains the CSS resources:

```java
opens my.app.styles to com.map.stdgui;
```

Change settings through enums:

```java
theme.setTheme(StdTheme.Theme.DARK);
theme.setFontSize(StdTheme.FontSize.LARGE);
```

Or through display names, which is useful when wiring UI preferences:

```java
theme.setTheme("Dark");
theme.setFontSize("Large (16px)");
```

Detach short-lived windows when they close:

```java
theme.detach(window);
window.close();
```

Detached windows keep their current CSS state, but they no longer receive future theme changes. This prevents a
long-lived theme manager from retaining closed windows.

### Styling Choices

StdGUI has a default light stylesheet and dark stylesheet bundled inside the library. Applications should prefer
`StdTheme.configureDefault(...)` for application-owned stylesheets because it accepts a resource anchor.

`StdWindow.stylesheet(...)` is useful for library-bundled stylesheets. For application-wide custom styling, `StdTheme`
is the more modular and explicit choice.

## Dialogs, Files, And Clipboard

Use `StdDialog` for message, confirmation, and choice dialogs.

```java
StdDialog.info("Import", "The file was imported successfully.");

boolean replace = StdDialog.confirm(
        "Replace data",
        "Existing results will be overwritten.");

Optional<String> mode = StdDialog.choose(
        "Mode",
        "Choose clustering mode",
        "Fast",
        List.of("Fast", "Accurate"));
```

Use `StdFileDialog` for paths:

```java
Optional<Path> input = StdFileDialog.openFile(
        "Open CSV",
        new StdFileDialog.Filter("CSV files", "*.csv"));

Optional<Path> output = StdFileDialog.saveFile(
        "Export PNG",
        "chart.png",
        new StdFileDialog.Filter("PNG images", "*.png"));

Optional<Path> directory = StdFileDialog.chooseDirectory(
        "Choose output directory",
        Path.of("."));
```

Use `StdClipboard` for text:

```java
StdClipboard.putText("Copied result");

if (StdClipboard.hasText()) {
    System.out.println(StdClipboard.getText());
}
```

These APIs return ordinary Java values: `boolean`, `Optional<T>`, `Path`, and `String`. Dialog objects and owner-window
resolution stay internal.

## Background Work

Use `StdAsync` when work should run off the GUI thread.

```java
StdAsync.submit("cluster-worker", progress -> {
    progress.update(0.25, "Loading data");
    Dataset data = loadData();

    progress.update(0.70, "Running clustering");
    Result result = runClustering(data);

    progress.update(1.00, "Done");
    return result;
}).onProgress(update -> {
    System.out.println(update.message() + " " + update.value());
}).onSuccess(result -> {
    StdDialog.info("Done", "Clustering completed.");
}).onFailure(error -> {
    StdDialog.error("Error", error.getMessage());
});
```

Every `StdJob` callback runs on the JavaFX Application Thread. That means it is safe to update windows, status labels,
and dialogs from `onSuccess`, `onFailure`, `onCancel`, and `onProgress`.

You can also submit a plain `Callable`:

```java
StdJob<Integer> job = StdAsync.submit("counter", () -> countRows(file));
```

Cancel when the user aborts:

```java
job.cancel();
```

### Rationale

JavaFX `Task` is powerful but pulls GUI framework details into application logic. StdGUI keeps the useful parts: background
execution, progress, success, failure, cancellation, and GUI-safe callbacks.

Callbacks are replayable after completion. If a job finishes quickly and a callback is registered just after completion,
the callback still runs with the terminal result.

## Charts

`StdChart` builds simple bar charts and scatter plots from immutable records.

```java
StdChart.BarSeries series = new StdChart.BarSeries(
        "Tuples",
        List.of(
                new StdChart.BarPoint("C1", 7),
                new StdChart.BarPoint("C2", 4)));

StdWindow chart = StdChart.barChart(
        "Cluster Sizes",
        "Cluster",
        "Tuples",
        List.of(series));

chart.show();
```

Use a view when the chart should be embedded in another window:

```java
StdView chartView = StdChart.barChartView(
        "Embedded Cluster Sizes",
        "Cluster",
        "Tuples",
        List.of(series));
```

Scatter plots group points by the `group` field:

```java
StdChart.ScatterModel model = new StdChart.ScatterModel(
        "x",
        "y",
        List.of(
                new StdChart.ScatterPoint(1.0, 2.0, "A"),
                new StdChart.ScatterPoint(2.0, 3.0, "A"),
                new StdChart.ScatterPoint(4.0, 1.0, "B")));

StdWindow scatter = StdChart.scatterPlot("Grouped Points", model);
```

Export a chart window to PNG:

```java
StdChart.exportPng(chart, Path.of("cluster-sizes.png"), 640, 480);
```

Before snapshotting, StdGUI applies CSS and layout to the chart root. This allows export from a window that has not yet been shown.

## Tables, Trees, And Tabs

`StdDataView` creates common data displays without exposing JavaFX controls.

### Tables

```java
StdDataView.TableModel table = new StdDataView.TableModel(
        List.of("Name", "Value"),
        List.of(
                List.of("radius", "0.5"),
                List.of("clusters", "3")));

StdWindow tableWindow = StdDataView.table("Settings", table, 420, 240);
tableWindow.show();
```

Rows shorter than the column list are padded with empty strings at display time. This makes rendering robust against
partially filled diagnostic tables.

### Trees

```java
StdDataView.TreeNode root = new StdDataView.TreeNode(
        "Root",
        List.of(new StdDataView.TreeNode("Cluster 1", List.of())));

StdView treeView = StdDataView.treeView("Clusters", root, true);
```

`StdTree` is a related lower-level helper for tree displays that need selection callbacks or expand/collapse controls.

```java
StdTree.Node root = new StdTree.Node("Root", List.of(
        new StdTree.Node("Child")));

StdWindow treeWindow = StdTree.window("Tree", root, true, 420, 260);
```

`StdTree.bind(Object treeHandle)` exists for FXML interop. It accepts an opaque handle so public signatures do not
mention JavaFX, then validates that the handle is a compatible tree view.

### Tabs

```java
StdView tableView = StdDataView.tableView("Settings", table);
StdView treeView = StdDataView.treeView("Clusters", root, true);

StdWindow tabs = StdDataView.tabs(
        "Results",
        List.of(
                new StdDataView.TabView("Table", tableView),
                new StdDataView.TabView("Tree", treeView)),
        520,
        320,
        "Close");
```

There is also a text-tab shortcut:

```java
StdWindow textTabs = StdDataView.tabs(
        "Report",
        Map.of(
                "Summary", "Completed",
                "Details", "All records processed."),
        420,
        240);
```

## Tool Windows And Info Windows

`StdToolWindow` is useful for small utility panels with controls, actions, and replaceable content.

```java
StdToolWindow.Choice mode = new StdToolWindow.Choice(
        "Mode:",
        List.of("Fast", "Accurate"),
        0,
        index -> System.out.println("Selected " + index));

StdToolWindow.Toggle enabled = new StdToolWindow.Toggle(
        "Enabled",
        true,
        selected -> System.out.println("Enabled: " + selected));

StdToolWindow.Action refresh = new StdToolWindow.Action(
        "Refresh",
        () -> System.out.println("Refresh"));

StdToolWindow window = StdToolWindow.create(
        "Tools",
        "Controls",
        List.of(mode),
        List.of(enabled),
        List.of(refresh),
        StdView.text("Content", "Initial content"),
        "Ready",
        List.of(),
        "Close",
        480,
        260);

window.show();
```

Replace only the central content:

```java
window.replaceContent(StdView.text("Content", "Updated content"));
```

Use `StdInfoWindow` for structured about/help/summary screens:

```java
StdWindow about = StdInfoWindow.window(
        "About",
        "QT Clustering",
        "Interactive clustering client",
        "This application analyzes tuple clusters.",
        List.of(new StdInfoWindow.Section(
                "Runtime",
                List.of("Java 21", "JavaFX 21"))),
        List.of(new StdInfoWindow.Action(
                "Project page",
                () -> StdClipboard.putText("https://example.org"))),
        "MAP project",
        520,
        420,
        "Close");

about.showAndWait();
```

## Status Messages And Shortcuts

`StdStatus` is designed for FXML-owned status areas. The public factory accepts opaque handles to keep JavaFX types out
of the signature, then validates them internally.

```java
StdStatus status = StdStatus.of(statusPane, statusLabel);

status.success("Saved successfully");
status.warning("The input file is incomplete");
status.show("Working...", "label-info", 0);
status.hide();
```

Use `close()` when the controller or window is no longer active:

```java
status.close();
```

`StdShortcut` registers keyboard shortcuts on a `StdWindow`.

```java
StdShortcut.register(window, "Ctrl+S", this::save);
StdShortcut.register(window, "Esc", window::close);
```

Register several at once:

```java
StdShortcut.registerAll(window, Map.of(
        "Ctrl+S", this::save,
        "Ctrl+Q", StdGui::exit));
```

Register on the focused window:

```java
StdShortcut.registerCurrent("F1", this::showHelp);
```

Register shortcuts after the window has content. A scene must exist before accelerators can be attached.

## Swing Interoperability

`StdSwingView` embeds a Swing component inside a `StdView`.

```java
StdSwingView swingView = StdSwingView.create("legacy-panel");

swingView.setContent(() -> {
    JPanel panel = new JPanel();
    panel.add(new JLabel("Legacy Swing content"));
    return panel;
});

new StdWindow("Swing")
        .content(swingView.view())
        .size(420, 240)
        .show();
```

Swing content is created on the Swing event dispatch thread. The surrounding view is created on the JavaFX Application Thread.

Use this for migration or specialized Swing widgets. Do not use it as the default way to build new StdGUI screens.

## Threading Model

Most StdGUI methods are safe to call from non-JavaFX threads. Internally they
use `StdGui.runAndWait(...)`, `StdGui.callAndWait(...)`, or `StdGui.later(...)`.

| Operation style           | Method examples                               | Threading behavior                                      |
| ------------------------- | --------------------------------------------- | ------------------------------------------------------- |
| Fire-and-forget UI update | `StdGui.later`, `StdStatus.show`              | Schedules work and returns                              |
| Blocking UI update        | `StdWindow.show`, `StdDialog.info`            | Runs on FX thread and waits                             |
| Blocking UI query         | `StdWindow.isShowing`, `StdClipboard.getText` | Computes on FX thread and returns a value               |
| Background work           | `StdAsync.submit`                             | Runs work on a daemon thread and callbacks on FX thread |
| Swing embedding           | `StdSwingView.setContent`                     | Creates Swing content on the Swing EDT                  |

### Practical Guidelines

- Call `StdGui.init()` before using StdGUI in a plain `main`.
- Do not call `StdGui.init()` from JavaFX `Application.start(...)`.
- Keep `StdAsync` work functions free of UI mutations.
- Update the UI from `StdJob` callbacks, not from the background work body.
- Avoid long-running work inside `StdGui.runAndWait(...)` or dialog callbacks.
- Register shortcuts only after the target window has content.
- Detach short-lived windows from `StdTheme` before discarding them.

## Testing Clients

Every public library class has a minimal client under:

```text
stdgui/src/test/java/com/map/stdgui
```

These clients follow the same spirit as Sedgewick and Wayne's stdlib test clients. They are small executable examples
that exercise the public methods.

Useful clients include:

| Client                | Demonstrates                                           |
| --------------------- | ------------------------------------------------------ |
| `StdGuiClient`        | explicit runtime startup and UI dispatch               |
| `StdViewClient`       | FXML loading and controller access from test resources |
| `StdWindowClient`     | content, size, fixed window, blocking show             |
| `StdThemeClient`      | theme persistence, attach, font sizes                  |
| `StdAsyncClient`      | progress, success, failure callback pattern            |
| `StdChartClient`      | bar charts, scatter plots, PNG export                  |
| `StdDataViewClient`   | tables, trees, tabs                                    |
| `StdToolWindowClient` | choices, toggles, actions, replacement content         |
| `StdDialogClient`     | info, warning, error, confirm, choose                  |
| `StdSwingViewClient`  | Swing embedding                                        |

Compile the clients:

```sh
mvn -q -pl stdgui -am test-compile
```

Run one client with the Maven exec plugin:

```sh
mvn -q -pl stdgui \
    -Dexec.mainClass=com.map.stdgui.StdChartClient \
    -Dexec.classpathScope=test \
    org.codehaus.mojo:exec-maven-plugin:3.2.0:java
```

Some clients open GUI windows and require a desktop session.

## Extending StdGUI

There are two extension styles.

1. Extend the library itself by adding a new class in `com.map.stdgui`.
2. Extend an application by using FXML and controllers while keeping JavaFX at the adapter boundary.

### Adding A New Library Class

When adding a new class to StdGUI, follow this checklist.

1. Choose one responsibility.
2. Use immutable records for input models when useful.
3. Return `StdView` for embeddable content.
4. Return `StdWindow` for ready-to-show top-level content.
5. Keep JavaFX classes out of public method signatures.
6. Build JavaFX nodes inside `StdGui.callAndWait(...)`.
7. Add a minimal client under `src/test/java/com/map/stdgui`.
8. Document threading behavior in Javadoc.

Example shape for a new embeddable component inside the library package:

```java
package com.map.stdgui;

import java.util.List;
import java.util.Objects;
import javafx.scene.control.ListView;

public final class StdList {
    public record Item(String label) {
        public Item {
            Objects.requireNonNull(label, "label");
        }
    }

    private StdList() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static StdView view(String title, List<Item> items) {
        Objects.requireNonNull(items, "items");
        return StdGui.callAndWait(() -> {
            ListView<String> list = new ListView<>();
            for (Item item : items) {
                list.getItems().add(item.label());
            }
            return StdView.of("list:" + (title == null ? "" : title), list);
        });
    }

    public static StdWindow window(String title, List<Item> items,
            double width, double height) {
        return new StdWindow(title).content(view(title, items)).size(width, height);
    }
}
```

Notice that `StdView.of(...)` is package-private. That is intentional. It lets library classes wrap JavaFX nodes without
making JavaFX nodes part of the public API.

If an external add-on outside `com.map.stdgui` needs to create arbitrary JavaFX-backed views, there are two design options.
The conservative option is to use FXML plus `StdView.load(...)`. The broader option is to introduce a new public extension
point, but that should be done deliberately because it may weaken the "no JavaFX in public API" rule.

### Extending From An Application

For application-specific screens, prefer FXML:

```java
StdView.configureResourceAnchor(MyApplication.class);
StdView screen = StdView.load("/my/app/views/search.fxml");
```

Keep controllers thin. They can translate UI events into domain calls and use StdGUI for dialogs, background work, status
messages, charts, and windows.

This gives a clean boundary:

| Layer               | Allowed knowledge                            |
| ------------------- | -------------------------------------------- |
| Domain model        | No JavaFX, no StdGUI                         |
| Application service | No JavaFX, optional StdGUI for notifications |
| FXML controller     | JavaFX handles allowed as adapter details    |
| StdGUI library      | JavaFX internals hidden and tested           |

### Extension Heuristics

Add to StdGUI when the behavior is reusable across projects: dialogs, generic data display, chart export, theme application,
background jobs.

Keep it in the application when the behavior knows domain vocabulary: cluster-specific labels, dataset-specific validation,
server commands, project-specific workflows.

Create a new record model when inputs are structured. Do not pass parallel lists unless the Java concept is naturally tabular.

Prefer a `view(...)` method plus a `window(...)` convenience method. This keeps composition flexible:

```java
StdView reusable = StdSomething.view(model);
StdWindow ready = StdSomething.window("Title", model, 640, 480);
```

## Common Pitfalls

### FXML Works In Tests But Not From A Modular Consumer

Test resources may be available on the test classpath even when packaged module resources are not. A downstream JPMS
consumer should be tested separately with `requires com.map.stdgui;`, application-owned FXML resources, and the correct
`opens` directives.

For FXML resources:

```java
opens my.app.views to com.map.stdgui;
```

For FXML controllers:

```java
opens my.app.controllers to javafx.fxml;
```

### A Shortcut Registration Fails

`StdShortcut` needs a scene. Set window content before registering shortcuts:

```java
StdWindow window = new StdWindow("Editor")
        .content(StdView.text("Editor", "Ready"))
        .size(640, 480);

StdShortcut.register(window, "Ctrl+S", this::save);
```

### Theme Changes Do Not Affect A Window

Attach the window after content has been set:

```java
StdWindow window = new StdWindow("Themed")
        .content(StdView.text("Theme", "Ready"))
        .size(400, 200);

StdTheme.getDefault().attach(window);
```

If the window is short-lived, detach it before discarding it.

### A Dialog Freezes The UI

Dialogs are blocking by design. Do not run long computations before closing the dialog callback. Use `StdAsync` for
computation and show dialogs from the job callbacks.

### A Background Task Touches UI State

Keep UI updates out of the `StdAsync.submit(...)` work body. Use callbacks:

```java
StdAsync.submit("work", () -> compute())
        .onSuccess(result -> window.replaceContent(render(result)));
```

### A Custom JavaFX Node Cannot Become A StdView Outside The Package

That is currently by design. External code should use FXML or existing StdGUI factories. If a reusable component is general
enough, add it to StdGUI itself so it can use the package-private `StdView.of(...)` bridge.

## Summary

StdGUI gives Java applications a compact GUI vocabulary:

- `StdGui` starts the runtime and handles thread dispatch.
- `StdView` represents content.
- `StdWindow` displays content.
- `StdTheme` applies consistent visual settings.
- `StdAsync` moves work off the GUI thread.
- Specialized classes handle dialogs, files, charts, data views, tools,
  status messages, shortcuts, and Swing interop.

The central rule is simple: application code should describe intent with plain Java values, and StdGUI should absorb the JavaFX ceremony.
