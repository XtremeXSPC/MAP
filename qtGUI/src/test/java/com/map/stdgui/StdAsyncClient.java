package com.map.stdgui;

import java.util.concurrent.CountDownLatch;

/**
 * Minimal client for {@link StdAsync}.
 */
public final class StdAsyncClient {

    private StdAsyncClient() {
    }

    public static void main(String[] args) throws InterruptedException {
        StdGui.init();

        CountDownLatch done = new CountDownLatch(1);
        StdAsync.submit("stdasync-client", progress -> {
            progress.update(0.25, "quarter");
            progress.update(0.50, "half");
            progress.update(1.00, "done");
            return "ok";
        }).onProgress(value -> System.out.println("Progress: " + value))
                .onSuccess(result -> {
                    System.out.println("Result: " + result);
                    done.countDown();
                }).onFailure(error -> {
                    error.printStackTrace(System.err);
                    done.countDown();
                });

        done.await();
        StdGui.exit();
    }
}
