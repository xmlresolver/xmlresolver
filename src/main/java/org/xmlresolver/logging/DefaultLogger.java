package org.xmlresolver.logging;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.ResolverFeature;

/**
 * The default logger logs to {@link System#err}.
 *
 * <p>The {@link ResolverFeature#DEFAULT_LOGGER_LOG_LEVEL DEFAULT_LOGGER_LOG_LEVEL} feature determines
 * which messages are logged. The valid levels are "debug", "info", and "warn". An invalid level
 * is treated as "warn".</p>
 *
 * <p>If the level is set to "debug", all messages will be printed. If set to "info", info and warning
 * messages will be printed. If set to "warn", only warning messages will be printed.</p>
 */
public class DefaultLogger extends AbstractLogger {
    private final int logLevel;

    /**
     * Initialize the logger.
     *
     * <p>The {@link ResolverFeature#DEFAULT_LOGGER_LOG_LEVEL DEFAULT_LOGGER_LOG_LEVEL} is obtained
     * from the configuration.</p>
     *
     * @param config The resolver configuration.
     */
    public DefaultLogger(ResolverConfiguration config) {
        super();

        String level = config.getFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL);
        if ("info".equalsIgnoreCase(level)) {
            logLevel = AbstractLogger.INFO;
        } else if ("debug".equalsIgnoreCase(level)) {
            logLevel = AbstractLogger.DEBUG;
        } else if ("warn".equalsIgnoreCase(level)) {
            logLevel = AbstractLogger.WARN;
        } else {
            System.err.println("Invalid default logger log level: " + level);
            logLevel = AbstractLogger.WARN;
        }
    }

    /**
     * Writes an informational message to {@link System#err}.
     * @param message The message.
     */
    @Override
    public void info(String message) {
        if (logLevel <= AbstractLogger.INFO) {
            System.err.println(message);
        }
    }

    /**
     * Writes a debug message to {@link System#err}.
     * @param message The message.
     */
    @Override
    public void debug(String message) {
        if (logLevel <= AbstractLogger.DEBUG) {
            System.err.println(message);
        }
    }

    /**
     * Writes a warning message to {@link System#err}.
     * @param message The message.
     */
    @Override
    public void warn(String message) {
        if (logLevel <= AbstractLogger.WARN) {
            System.err.println(message);
        }
    }
}
