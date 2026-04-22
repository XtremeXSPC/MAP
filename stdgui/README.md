# StdGui

`StdGui` is a small JavaFX utility library inspired by Sedgewick and Wayne's
standard libraries. It hides common JavaFX plumbing behind compact APIs for
runtime dispatch, windows, views, dialogs, file choosers, background work,
themes, charts, tables, trees, status messages, shortcuts, and Swing embedding.

For a deeper walkthrough of the public API, design rationale, JPMS resource
rules, threading model, and extension patterns, see [USER_GUIDE.md](USER_GUIDE.md).

## Build

From the repository root:

```sh
mvn -q -pl stdgui -am test-compile
```

To install the library into your local Maven repository:

```sh
mvn -q -pl stdgui -am install
```

To build the GUI application together with the library:

```sh
mvn -q -pl qtGUI -am test-compile
```

The `stdgui` package build also attaches source and Javadoc JARs, so IDEs and
downstream projects can browse the API documentation after installation.

## Use From Another Project

After publishing or installing the artifact, add:

```xml
<dependency>
    <groupId>com.map</groupId>
    <artifactId>stdgui</artifactId>
    <version>1.0.0</version>
</dependency>
```

For modular applications:

```java
requires com.map.stdgui;
```

## Minimal Client

```java
import com.map.stdgui.StdGui;
import com.map.stdgui.StdView;
import com.map.stdgui.StdWindow;

public final class HelloStdGui {
    public static void main(String[] args) {
        StdGui.init();
        new StdWindow("Hello")
                .content(StdView.text("Hello", "StdGui is running."))
                .size(360, 180)
                .show();
    }
}
```

Applications that already extend `javafx.application.Application` do not need
to call `StdGui.init()` from `start(...)`; the JavaFX toolkit is already active.

## FXML Resources

If an application loads FXML through `StdView`, configure the class that owns
those resources during startup:

```java
StdView.configureResourceAnchor(MyApplication.class);
```

In modular applications, open the resource package to `com.map.stdgui`:

```java
opens views to com.map.stdgui;
```

## Themes

The library bundles minimal default light and dark stylesheets. Applications can
use their own resources by configuring the shared manager at startup:

```java
StdTheme.configureDefault(
        Path.of("my-app.properties"),
        MyApplication.class,
        "/styles/application.css",
        "/styles/dark-theme.css");
```

If those CSS files live in a named module, open their resource package too:

```java
opens styles to com.map.stdgui;
```
