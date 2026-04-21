package com.map.stdgui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;

/**
 * The {@code StdChart} class provides static methods for displaying simple bar
 * charts and scatter plots.
 * <p>
 * Chart data is described with immutable records. JavaFX axes, series, chart
 * nodes, snapshots, and PNG export details are created internally.
 */
public final class StdChart {

    /** One categorical bar-chart point. */
    public record BarPoint(String category, double value) {
        public BarPoint {
            Objects.requireNonNull(category, "category");
        }
    }

    /** One named bar-chart series. */
    public record BarSeries(String name, List<BarPoint> points) {
        public BarSeries {
            Objects.requireNonNull(name, "name");
            points = List.copyOf(Objects.requireNonNull(points, "points"));
        }
    }

    /** One grouped scatter-plot point. */
    public record ScatterPoint(double x, double y, String group) {
        public ScatterPoint {
            group = group == null || group.isBlank() ? "Series" : group;
        }
    }

    /** Scatter-plot model containing labels and grouped points. */
    public record ScatterModel(String xLabel, String yLabel, List<ScatterPoint> points) {
        public ScatterModel {
            points = List.copyOf(Objects.requireNonNull(points, "points"));
        }
    }

    private static final double DEFAULT_WIDTH  = 900.0;
    private static final double DEFAULT_HEIGHT = 700.0;

    /* This class provides only static methods. */
    private StdChart() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Creates a bar-chart window for one or more series of categorical data.
     *
     * @param title chart and window title
     * @param xLabel category-axis label
     * @param yLabel value-axis label
     * @param series chart series
     * @return chart window
     */
    public static StdWindow barChart(String title, String xLabel, String yLabel, List<BarSeries> series) {
        return new StdWindow(title).content(barChartView(title, xLabel, yLabel, series))
                .size(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Creates an embeddable bar-chart view for one or more series of categorical data.
     *
     * @param title chart title
     * @param xLabel category-axis label
     * @param yLabel value-axis label
     * @param series chart series
     * @return chart view
     */
    public static StdView barChartView(String title, String xLabel, String yLabel, List<BarSeries> series) {
        Objects.requireNonNull(series, "series");
        return StdGui.callAndWait(() -> {
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel(xLabel == null ? "" : xLabel);

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel(yLabel == null ? "" : yLabel);

            BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
            chart.setTitle(title == null ? "" : title);
            chart.setLegendVisible(series.size() > 1);
            chart.setAnimated(true);

            for (BarSeries barSeries : series) {
                XYChart.Series<String, Number> fxSeries = new XYChart.Series<>();
                fxSeries.setName(barSeries.name());
                for (BarPoint point : barSeries.points()) {
                    fxSeries.getData().add(new XYChart.Data<>(point.category(), point.value()));
                }
                chart.getData().add(fxSeries);
            }

            return StdView.of("bar-chart:" + (title == null ? "" : title), chart);
        });
    }

    /**
     * Creates a scatter-plot window for grouped two-dimensional points.
     *
     * @param title chart and window title
     * @param model scatter data model
     * @return chart window
     */
    public static StdWindow scatterPlot(String title, ScatterModel model) {
        return new StdWindow(title).content(scatterPlotView(title, model)).size(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Creates an embeddable scatter-plot view for grouped two-dimensional points.
     *
     * @param title chart title
     * @param model scatter data model
     * @return chart view
     */
    public static StdView scatterPlotView(String title, ScatterModel model) {
        Objects.requireNonNull(model, "model");
        return StdGui.callAndWait(() -> {
            NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel(model.xLabel() == null ? "" : model.xLabel());

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel(model.yLabel() == null ? "" : model.yLabel());

            ScatterChart<Number, Number> chart = new ScatterChart<>(xAxis, yAxis);
            chart.setTitle(title == null ? "" : title);
            chart.setAnimated(true);

            for (Map.Entry<String, List<ScatterPoint>> entry : groupBySeries(model.points()).entrySet()) {
                XYChart.Series<Number, Number> fxSeries = new XYChart.Series<>();
                fxSeries.setName(entry.getKey());
                for (ScatterPoint point : entry.getValue()) {
                    fxSeries.getData().add(new XYChart.Data<>(point.x(), point.y()));
                }
                chart.getData().add(fxSeries);
            }

            return StdView.of("scatter-plot:" + (title == null ? "" : title), chart);
        });
    }

    /**
     * Exports a chart window to a PNG file with the requested size.
     *
     * @param window chart window
     * @param file target PNG file
     * @param width image width in pixels
     * @param height image height in pixels
     */
    public static void exportPng(StdWindow window, Path file, int width, int height) {
        Objects.requireNonNull(window, "window");
        Objects.requireNonNull(file, "file");
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Export size must be positive");
        }

        StdGui.runAndWait(() -> {
            Scene scene = window.scene();
            if (scene == null || scene.getRoot() == null) {
                throw new IllegalStateException("Window has no chart content to export");
            }

            // Ensure CSS and layout are applied before snapshotting so exports
            // produce a correctly styled, laid-out image even when the window
            // has not yet been shown.
            scene.getRoot().applyCss();
            scene.getRoot().layout();

            WritableImage image = new WritableImage(width, height);
            scene.getRoot().snapshot(new SnapshotParameters(), image);
            try {
                Path parent = file.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file.toFile());
            } catch (IOException e) {
                throw new IllegalStateException("Unable to export chart to " + file, e);
            }
        });
    }

    /* Preserves input order while grouping scatter points into named series. */
    private static Map<String, List<ScatterPoint>> groupBySeries(List<ScatterPoint> points) {
        Map<String, List<ScatterPoint>> grouped = new LinkedHashMap<>();
        for (ScatterPoint point : points) {
            grouped.computeIfAbsent(point.group(), ignored -> new ArrayList<>()).add(point);
        }
        return grouped;
    }
}
