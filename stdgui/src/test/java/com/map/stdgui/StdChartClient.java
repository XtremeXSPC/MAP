package com.map.stdgui;

import java.nio.file.Path;
import java.util.List;

/**
 * Minimal client for StdChart.
 */
public final class StdChartClient {

    private StdChartClient() {
        throw new AssertionError("Client class - do not instantiate");
    }

    /**
     * Exercises every public StdChart method.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        StdGui.init();

        StdChart.BarSeries series = new StdChart.BarSeries("Tuples",
                List.of(new StdChart.BarPoint("C1", 7), new StdChart.BarPoint("C2", 4)));
        StdWindow barWindow = StdChart.barChart("Cluster Sizes", "Cluster", "Tuples", List.of(series));
        StdView barView = StdChart.barChartView("Embedded Cluster Sizes", "Cluster", "Tuples", List.of(series));
        System.out.println("Bar view: " + barView.id());

        StdChart.ScatterModel scatterModel = new StdChart.ScatterModel("x", "y",
                List.of(new StdChart.ScatterPoint(1.0, 2.0, "A"),
                        new StdChart.ScatterPoint(2.0, 3.0, "A"),
                        new StdChart.ScatterPoint(4.0, 1.0, "B")));
        StdWindow scatterWindow = StdChart.scatterPlot("Grouped Points", scatterModel);
        StdView scatterView = StdChart.scatterPlotView("Embedded Points", scatterModel);
        System.out.println("Scatter view: " + scatterView.id());

        StdChart.exportPng(barWindow, Path.of(System.getProperty("java.io.tmpdir"), "stdchart-client.png"), 640, 480);
        scatterWindow.close();
        barWindow.close();
        StdGui.exit();
    }
}
