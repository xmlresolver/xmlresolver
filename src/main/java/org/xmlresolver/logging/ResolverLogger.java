package org.xmlresolver.logging;

import java.util.Formatter;

/**
 * The resolver logger interface defines the features of a logging class for the resolver.
 *
 * <p>The resolver logs messages by calling {@link ResolverLogger#log}. The categories
 * are defined as constants in this class. Different log levels can be specified for
 * the different classes. Which levels result in output, and where that output goes
 * depends on the back end. {@link DefaultLogger} sends messages to standard error.
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
     * Returns the log level, "debug", "info", or worn", associated with a category.
     * @param cat The category.
     * @return The level. If no level has been configured for that category, the default is "debug".
     */
    String getCategory(String cat);

    /**
     * Set the log level for a category. After this call, messages in the specified category
     * will be logged at the specified level. Valid levels are "debug", "info", and "warn".
     * An invalid level is treated as "debug".
     * @param cat The category.
     * @param level The level.
     */
    void setCategory(String cat, String level);

    /**
     * Log a message.
     *
     * <p>The category is used to determine what level of logging is
     * expected for this message. The message is then formatted with its parameters
     * and logged.</p>
     *
     * <p>The actual logging is done by the {@link #warn}, {@link #info}, and {@link #debug} methods,
     * but they are never called directly by the resolver.</p>
     *
     * <p>The message and its parameters are formatted with {@link Formatter}.</p>
     *
     * @param cat The category.
     * @param message The message.
     * @param params The message parameters.
     */
    void log(String cat, String message, Object... params);

    /**
     * Process a warning message.
     * @param message The message.
     */
    void warn(String message);

    /**
     * Process an informational message.
     * @param message The message.
     */
    void info(String message);

    /**
     * Process a debug or "trace" message.
     * @param message The message.
     */
    void debug(String message);
}
