package org.xmlresolver.logging;

import java.util.Formatter;

/**
 * The resolver logger interface defines the features of a logging class for the resolver.
 *
 * <p>The {@link DefaultLogger} sends messages to standard error.
 * {@link SystemLogger} connects to a logging backend. Configuring the logging backend
 * is performed at runtime according to the mechanisms defined for that backend. That
 * level of configuration is outside the scope of the resolver logger.</p>
 *
 * <p>The log levels are "debug", "info", and "warn". Exactly what levels make sense
 * and how they're mapped to different backends is a little arbitrary. Debug level
 * messages describe in detail how a request is processed. Info level messages are
 * summaries of processing, likely one or two per request. Warning messages indicate
 * that an error has been detected. The resolver tries to be very forgiving of errors,
 * ignoring most conditions rather than throwing exceptions.</p>
 */
public interface ResolverLogger {
    /**
     * Log an error message.
     * @param message The message.
     * @param params The message parameters.
     */
    void error(String message, Object... params);

    /**
     * Log a warning message.
     * @param message The message.
     * @param params The message parameters.
     */
    void warn(String message, Object... params);

    /**
     * Process an informational message.
     * @param message The message.
     * @param params The message parameters.
     */
    void info(String message, Object... params);

    /**
     * Process a debug or "trace" message.
     * @param message The message.
     * @param params The message parameters.
     */
    void debug(String message, Object... params);
}
