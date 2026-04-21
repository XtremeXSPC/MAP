package com.map.stdgui;

/**
 * Minimal client for {@link StdView}.
 */
public final class StdViewClient {

    private StdViewClient() {
    }

    public static void main(String[] args) {
        StdGui.init();

        StdView fxmlView = StdView.load("/stdgui/std-view-client.fxml");
        StdViewClientController controller = fxmlView.controller(StdViewClientController.class);
        StdView textView = StdView.text("StdView", "Generated content");

        System.out.println("FXML view id: " + fxmlView.id());
        System.out.println("FXML controller: " + controller.message());
        System.out.println("Text view id: " + textView.id());

        StdGui.exit();
    }
}
