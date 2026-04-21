package com.map.stdgui;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javafx.concurrent.Task;

/**
 * The {@code StdAsync} class provides static methods for running background
 * work and receiving GUI-safe callbacks.
 * <p>
 * Clients submit ordinary {@link Callable} instances or progress-reporting
 * work. The JavaFX {@code Task}, worker thread, progress properties, and event
 * handlers are encapsulated behind {@link StdJob}.
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

        private final Task<T> task;
        private final List<Consumer<T>> successCallbacks;
        private final List<Consumer<Throwable>> failureCallbacks;
        private final List<Runnable> cancelCallbacks;
        private final List<Consumer<StdProgress>> progressCallbacks;
        private final AtomicReference<T> successValue;
        private final AtomicReference<Throwable> failureValue;
        private final AtomicReference<StdProgress> lastProgress;

        /* Wires task lifecycle events into replayable callback lists. */
        private TaskStdJob(Task<T> task) {
            this.task = task;
            this.successCallbacks = new CopyOnWriteArrayList<>();
            this.failureCallbacks = new CopyOnWriteArrayList<>();
            this.cancelCallbacks = new CopyOnWriteArrayList<>();
            this.progressCallbacks = new CopyOnWriteArrayList<>();
            this.successValue = new AtomicReference<>();
            this.failureValue = new AtomicReference<>();
            this.lastProgress = new AtomicReference<>(new StdProgress(task.getProgress(), task.getMessage()));

            task.progressProperty().addListener((obs, oldValue, newValue) -> fireProgress());
            task.messageProperty().addListener((obs, oldValue, newValue) -> fireProgress());
            task.setOnSucceeded(event -> {
                T value = task.getValue();
                successValue.set(value);
                for (Consumer<T> callback : successCallbacks) {
                    callback.accept(value);
                }
            });
            task.setOnFailed(event -> {
                Throwable error = task.getException();
                failureValue.set(error);
                for (Consumer<Throwable> callback : failureCallbacks) {
                    callback.accept(error);
                }
            });
            task.setOnCancelled(event -> {
                for (Runnable callback : cancelCallbacks) {
                    callback.run();
                }
            });
        }

        @Override
        public StdJob<T> onSuccess(Consumer<T> action) {
            Objects.requireNonNull(action, "action");
            successCallbacks.add(action);
            T value = successValue.get();
            if (value != null) {
                StdGui.later(() -> action.accept(value));
            }
            return this;
        }

        @Override
        public StdJob<T> onFailure(Consumer<Throwable> action) {
            Objects.requireNonNull(action, "action");
            failureCallbacks.add(action);
            Throwable error = failureValue.get();
            if (error != null) {
                StdGui.later(() -> action.accept(error));
            }
            return this;
        }

        @Override
        public StdJob<T> onCancel(Runnable action) {
            Objects.requireNonNull(action, "action");
            cancelCallbacks.add(action);
            if (task.isCancelled()) {
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
