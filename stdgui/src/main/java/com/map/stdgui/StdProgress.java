package com.map.stdgui;

/**
 * The {@code StdProgress} record is an immutable progress snapshot for
 * asynchronous jobs.
 * <p>
 * A value of {@code -1.0} may be used by the underlying toolkit when progress is
 * indeterminate.
 *
 * @param value fractional progress in the {@code [0.0, 1.0]} range when known
 * @param message optional progress message
 */
public record StdProgress(double value, String message) {
}
