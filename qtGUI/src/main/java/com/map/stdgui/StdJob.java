package com.map.stdgui;

import java.util.function.Consumer;

/**
 * The {@code StdJob} interface is a handle for asynchronous work submitted
 * through {@link StdAsync}.
 * <p>
 * It lets clients register lifecycle callbacks, observe progress, and request
 * cancellation without depending on JavaFX task classes.
 *
 * @param <T> result type
 */
public interface StdJob<T> {

    /**
     * Registers a success callback that runs on the JavaFX Application Thread.
     *
     * @param action success callback
     * @return this job
     */
    StdJob<T> onSuccess(Consumer<T> action);

    /**
     * Registers a failure callback that runs on the JavaFX Application Thread.
     *
     * @param action failure callback
     * @return this job
     */
    StdJob<T> onFailure(Consumer<Throwable> action);

    /**
     * Registers a cancellation callback that runs on the JavaFX Application Thread.
     *
     * @param action cancellation callback
     * @return this job
     */
    StdJob<T> onCancel(Runnable action);

    /**
     * Registers a progress callback that runs on the JavaFX Application Thread.
     *
     * @param action progress callback
     * @return this job
     */
    StdJob<T> onProgress(Consumer<StdProgress> action);

    /**
     * Attempts to cancel the running job.
     *
     * @return true when cancellation was requested successfully
     */
    boolean cancel();

    /**
     * Returns true once the job has completed, failed, or been cancelled.
     *
     * @return true when the job has finished
     */
    boolean isDone();
}
