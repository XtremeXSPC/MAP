/**
 * QT Clustering GUI Module.
 * JavaFX GUI for Quality Threshold Clustering Algorithm.
 */
module qtGUI {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;

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

    // Export packages for reflection (required by FXML)
    exports gui;
    exports gui.controllers;
    exports gui.models;
    exports gui.services;
    exports gui.charts;
    exports gui.utils;

    // Open packages to JavaFX for FXML controller injection
    opens gui to javafx.fxml;
    opens gui.controllers to javafx.fxml;
}
