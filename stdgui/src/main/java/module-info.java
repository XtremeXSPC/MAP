/**
 * Reusable JavaFX abstractions inspired by Sedgewick and Wayne's standard
 * libraries.
 */
module com.map.stdgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;

    exports com.map.stdgui;
}
