package com.map.stdgui;

/**
 * Immutable progress snapshot for asynchronous jobs.
 *
 * @param value fractional progress in the {@code [0.0, 1.0]} range when known
 * @param message optional progress message
 */
public record StdProgress(double value, String message) {
}
