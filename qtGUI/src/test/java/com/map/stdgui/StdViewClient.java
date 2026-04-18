package com.map.stdgui;

import gui.controllers.MainController;

/**
 * Minimal client for {@link StdView}.
 */
public final class StdViewClient {

    private StdViewClient() {
    }

    public static void main(String[] args) {
        StdGui.init();

        StdView fxmlView = StdView.load("/views/main.fxml");
        MainController controller = fxmlView.controller(MainController.class);
        StdView textView = StdView.text("StdView", "Generated content");

        System.out.println("FXML view id: " + fxmlView.id());
        System.out.println("FXML controller: " + controller.getClass().getName());
        System.out.println("Text view id: " + textView.id());

        StdGui.exit();
    }
}
