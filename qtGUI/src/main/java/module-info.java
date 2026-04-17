/**
 * Modulo GUI per QT Clustering. Interfaccia grafica JavaFX per l'algoritmo di Quality
 * Threshold Clustering.
 */
module qtGUI {
    // Moduli JavaFX
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing; // Per l'integrazione con SwingNode

    // Libreria per grafici
    requires org.knowm.xchart;

    // Logging
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;

    // Miglioramenti UI
    requires org.controlsfx.controls;

    // Moduli base Java
    requires transitive java.sql;
    requires transitive java.desktop; // Per Swing (JPanel, SwingUtilities, ecc.)

    // Esportazione pacchetti principali
    exports data;
    exports database;
    exports mining;
    exports gui;
    exports gui.controllers;
    exports gui.services;
    exports gui.models;
    exports gui.utils;
    exports gui.charts;
    exports gui.dialogs;
    exports com.map.stdgui;

    // Apertura pacchetti a JavaFX per l'iniezione dei controller FXML
    opens gui to javafx.fxml;
    opens gui.controllers to javafx.fxml;
    opens gui.dialogs to javafx.fxml;
}
