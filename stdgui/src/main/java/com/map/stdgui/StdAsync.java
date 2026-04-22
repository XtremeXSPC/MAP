package com.map.stdgui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javafx.concurrent.Task;

/**
 * The {@code StdAsync} class provides static methods for running background work
 * and receiving GUI-safe callbacks.
 * <p>
 * Clients submit ordinary {@link Callable} instances or progress-reporting work.
 * The JavaFX {@code Task}, worker thread, progress properties, and event handlers
 * are encapsulated behind {@link StdJob}.
 */
public final class StdAsync {

    /**
     * Sink used by background work to publish progress.
     */
    public interface ProgressSink {

        /**
         * Publishes fractional progress and an optional status message.
         *
         * @param progress fractional progress in the {@code [0.0, 1.0]} range
         * @param message optional progress message
         */
        void update(double progress, String message);
    }

    /**
     * Progress-reporting background work.
     *
     * @param <T> result type
     */
    public interface ProgressWork<T> {

        /**
         * Executes background work while reporting progress.
         *
         * @param progress progress sink
         * @return work result
         * @throws Exception on failure
         */
        T run(ProgressSink progress) throws Exception;
    }

    /* This class provides only static methods. */
    private StdAsync() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Starts background work and returns a handle for callbacks and cancellation.
     *
     * @param name worker-thread name
     * @param work background work
     * @param <T> result type
     * @return running job handle
     */
    public static <T> StdJob<T> submit(String name, Callable<T> work) {
        Objects.requireNonNull(work, "work");
        return submit(name, progress -> work.call());
    }

    /**
     * Starts progress-reporting background work and returns a handle for callbacks and cancellation.
     *
     * @param name worker-thread name
     * @param work background work
     * @param <T> result type
     * @return running job handle
     */
    public static <T> StdJob<T> submit(String name, ProgressWork<T> work) {
        Objects.requireNonNull(work, "work");

        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return work.run((progress, message) -> {
                    double clamped = Math.max(0.0, Math.min(1.0, progress));
                    updateProgress(clamped, 1.0);
                    if (message != null) {
                        updateMessage(message);
                    }
                });
            }
        };

        TaskStdJob<T> job = new TaskStdJob<>(task);
        Thread worker = new Thread(task);
        worker.setDaemon(true);
        worker.setName(name == null || name.isBlank() ? "stdgui-worker" : name);
        worker.start();
        return job;
    }

    /* Adapts a JavaFX Task to the public StdJob callback interface. */
    private static final class TaskStdJob<T> implements StdJob<T> {

        /* Terminal lifecycle state used to replay callbacks registered after completion. */
        private enum Terminal { NONE, SUCCEEDED, FAILED, CANCELLED }

        private final Task<T> task;
        private final Object lock;
        private final List<Consumer<T>> successCallbacks;
        private final List<Consumer<Throwable>> failureCallbacks;
        private final List<Runnable> cancelCallbacks;
        private final List<Consumer<StdProgress>> progressCallbacks;
        private final AtomicReference<StdProgress> lastProgress;

        /* Guarded by {@code lock}; {@code volatile} so unsynchronized reads are safe. */
        private volatile Terminal terminal;
        private T terminalValue;
        private Throwable terminalError;

        /* Wires task lifecycle events into replayable callback lists. */
        private TaskStdJob(Task<T> task) {
            this.task = task;
            this.lock = new Object();
            this.successCallbacks  = new ArrayList<>();
            this.failureCallbacks  = new ArrayList<>();
            this.cancelCallbacks   = new ArrayList<>();
            this.progressCallbacks = new CopyOnWriteArrayList<>();
            this.lastProgress = new AtomicReference<>(new StdProgress(task.getProgress(), task.getMessage()));
            this.terminal = Terminal.NONE;

            task.progressProperty().addListener((obs, oldValue, newValue) -> fireProgress());
            task.messageProperty().addListener((obs, oldValue, newValue) -> fireProgress());
            task.setOnSucceeded(event -> handleSucceeded());
            task.setOnFailed(event -> handleFailed());
            task.setOnCancelled(event -> handleCancelled());
        }

        @Override
        public StdJob<T> onSuccess(Consumer<T> action) {
            Objects.requireNonNull(action, "action");
            boolean replay = false;
            T value = null;
            synchronized (lock) {
                switch (terminal) {
                    case NONE      -> successCallbacks.add(action);
                    case SUCCEEDED -> { replay = true; value = terminalValue; }
                    case FAILED, CANCELLED -> { /* onSuccess never fires for non-success terminals */ }
                }
            }
            if (replay) {
                T captured = value;
                StdGui.later(() -> action.accept(captured));
            }
            return this;
        }

        @Override
        public StdJob<T> onFailure(Consumer<Throwable> action) {
            Objects.requireNonNull(action, "action");
            boolean replay = false;
            Throwable error = null;
            synchronized (lock) {
                switch (terminal) {
                    case NONE   -> failureCallbacks.add(action);
                    case FAILED -> { replay = true; error = terminalError; }
                    case SUCCEEDED, CANCELLED -> { /* onFailure never fires for non-failed terminals */ }
                }
            }
            if (replay) {
                Throwable captured = error;
                StdGui.later(() -> action.accept(captured));
            }
            return this;
        }

        @Override
        public StdJob<T> onCancel(Runnable action) {
            Objects.requireNonNull(action, "action");
            boolean replay = false;
            synchronized (lock) {
                switch (terminal) {
                    case NONE      -> cancelCallbacks.add(action);
                    case CANCELLED -> replay = true;
                    case SUCCEEDED, FAILED -> { /* onCancel never fires for non-cancelled terminals */ }
                }
            }
            if (replay) {
                StdGui.later(action);
            }
            return this;
        }

        @Override
        public StdJob<T> onProgress(Consumer<StdProgress> action) {
            Objects.requireNonNull(action, "action");
            progressCallbacks.add(action);
            StdProgress progress = lastProgress.get();
            if (progress != null) {
                StdGui.later(() -> action.accept(progress));
            }
            return this;
        }

        @Override
        public boolean cancel() {
            return task.cancel();
        }

        @Override
        public boolean isDone() {
            return task.isDone();
        }

        /* Latches the success terminal state, then dispatches the snapshot of callbacks. */
        private void handleSucceeded() {
            T value = task.getValue();
            List<Consumer<T>> snapshot;
            synchronized (lock) {
                terminal = Terminal.SUCCEEDED;
                terminalValue = value;
                snapshot = new ArrayList<>(successCallbacks);
                successCallbacks.clear();
            }
            for (Consumer<T> callback : snapshot) {
                callback.accept(value);
            }
        }

        /* Latches the failure terminal state, then dispatches the snapshot of callbacks. */
        private void handleFailed() {
            Throwable error = task.getException();
            List<Consumer<Throwable>> snapshot;
            synchronized (lock) {
                terminal = Terminal.FAILED;
                terminalError = error;
                snapshot = new ArrayList<>(failureCallbacks);
                failureCallbacks.clear();
            }
            for (Consumer<Throwable> callback : snapshot) {
                callback.accept(error);
            }
        }

        /* Latches the cancelled terminal state, then dispatches the snapshot of callbacks. */
        private void handleCancelled() {
            List<Runnable> snapshot;
            synchronized (lock) {
                terminal = Terminal.CANCELLED;
                snapshot = new ArrayList<>(cancelCallbacks);
                cancelCallbacks.clear();
            }
            for (Runnable callback : snapshot) {
                callback.run();
            }
        }

        /* Captures the latest progress snapshot and forwards it to listeners. */
        private void fireProgress() {
            StdProgress progress = new StdProgress(task.getProgress(), task.getMessage());
            lastProgress.set(progress);
            for (Consumer<StdProgress> callback : progressCallbacks) {
                callback.accept(progress);
            }
        }
    }
}
