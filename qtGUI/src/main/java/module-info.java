/**
 * QT Clustering GUI Module.
 * JavaFX GUI for Quality Threshold Clustering Algorithm.
 */
module qtGUI {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;  // Per SwingNode integration

    // Charting library
    requires org.knowm.xchart;

    // Logging
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;

    // UI enhancements
    requires org.controlsfx.controls;

    // Java base modules
    requires java.sql;
    requires java.desktop;  // Per Swing (JPanel, SwingUtilities, etc.)

    // Export packages for reflection (required by FXML)
    exports gui;
    exports gui.controllers;
    exports gui.services;
    exports gui.models;
    exports gui.utils;
    exports gui.charts;
    exports gui.dialogs;

    // Open packages to JavaFX for FXML controller injection
    opens gui to javafx.fxml;
    opens gui.controllers to javafx.fxml;
    opens gui.dialogs to javafx.fxml;
}
