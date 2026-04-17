package com.map.stdgui;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javafx.application.Platform;

/**
 * Explicit JavaFX runtime and thread-dispatch helpers.
 */
public final class StdGui {

    private StdGui() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Starts the JavaFX runtime explicitly if it is not already running.
     */
    public static void init() {
        CountDownLatch startupLatch = new CountDownLatch(1);
        try {
            Platform.startup(startupLatch::countDown);
            await(startupLatch);
        } catch (IllegalStateException ignored) {
            // Toolkit already started by Application.launch() or a previous init().
        }
    }

    /**
     * Shuts down the JavaFX runtime explicitly.
     */
    public static void exit() {
        try {
            if (isUiThread()) {
                Platform.exit();
            } else {
                Platform.runLater(Platform::exit);
            }
        } catch (IllegalStateException ignored) {
            // Toolkit was not active.
        }
    }

    /**
     * Returns true if the current thread is the JavaFX Application Thread.
     *
     * @return true if on the JavaFX Application Thread
     */
    public static boolean isUiThread() {
        return Platform.isFxApplicationThread();
    }

    /**
     * Runs the action on the JavaFX Application Thread without blocking.
     *
     * @param action UI action to execute
     */
    public static void later(Runnable action) {
        Objects.requireNonNull(action, "action");
        if (isUiThread()) {
            action.run();
            return;
        }
        Platform.runLater(action);
    }

    /**
     * Runs the action on the JavaFX Application Thread and waits for completion.
     *
     * @param action UI action to execute
     */
    public static void runAndWait(Runnable action) {
        Objects.requireNonNull(action, "action");
        if (isUiThread()) {
            action.run();
            return;
        }

        FutureTask<Void> task = new FutureTask<>(() -> {
            action.run();
            return null;
        });
        Platform.runLater(task);
        waitFor(task);
    }

    /**
     * Computes a value on the JavaFX Application Thread and waits for the result.
     *
     * @param action computation to execute
     * @param <T> result type
     * @return computed value
     */
    public static <T> T callAndWait(java.util.concurrent.Callable<T> action) {
        Objects.requireNonNull(action, "action");
        if (isUiThread()) {
            return callDirectly(action);
        }

        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return waitFor(task);
    }

    private static <T> T callDirectly(java.util.concurrent.Callable<T> action) {
        try {
            return action.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("JavaFX action failed", e);
        }
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for JavaFX runtime", e);
        }
    }

    private static <T> T waitFor(FutureTask<T> task) {
        try {
            return task.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for JavaFX action", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException("JavaFX action failed", cause);
        }
    }
}
