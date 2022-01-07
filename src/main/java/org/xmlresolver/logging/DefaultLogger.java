package org.xmlresolver.logging;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.ResolverFeature;

/**
 * The default logger logs to {@link System#err}.
 *
 * <p>By default, the {@link ResolverFeature#DEFAULT_LOGGER_LOG_LEVEL DEFAULT_LOGGER_LOG_LEVEL} feature determines
 * which messages are logged. The valid levels are "debug", "info", and "warn". An invalid level
 * is treated as "warn".</p>
 *
 * <p>If the level is set to "debug", all messages will be printed. If set to "info", info and warning
 * messages will be printed. If set to "warn", only warning messages will be printed.</p>
 *
 * <p>If the configuration's <code>DEFAULT_LOGGER_LOG_LEVEL</code> is changed, that change will be
 * detected by the logger and it will use that level going forward. The value can also be changed
 * directly by calling {@link #setLogLevel}. That level will remain in effect as long as the configuration
 * default does not change.
 */
public class DefaultLogger extends AbstractLogger {
    private final ResolverConfiguration config;
    private int logLevel;
    private String defaultLoggerLevel;
    private String currentLoggingLevel;

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
        this.config = config;
        setLogLevel(config.getFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL));
        defaultLoggerLevel = currentLoggingLevel;
    }

    /**
     * Get the current logging level.
     * @return the current logging level
     */
    public String getLogLevel() {
        checkLoggingLevel();
        switch (logLevel) {
            case AbstractLogger.INFO:
                return "info";
            case AbstractLogger.DEBUG:
                return "debug";
            case AbstractLogger.WARN:
                return "warn";
            default:
                return "unknown";
        }
    }

    /**
     * Set the current logging level.
     * @param level The logging level.
     */
    public void setLogLevel(String level) {
        currentLoggingLevel = level;

        if ("info".equalsIgnoreCase(level)) {
            logLevel = AbstractLogger.INFO;
        } else if ("debug".equalsIgnoreCase(level)) {
            logLevel = AbstractLogger.DEBUG;
        } else if ("warn".equalsIgnoreCase(level)) {
            logLevel = AbstractLogger.WARN;
        } else {
            System.err.println("Invalid default logger log level: " + level);
            logLevel = AbstractLogger.WARN;
            currentLoggingLevel = "warn";
        }
    }

    private void checkLoggingLevel() {
        // Blech
        String currentLevel = config.getFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL);
        if (currentLevel != null && !currentLevel.equals(defaultLoggerLevel)) {
            setLogLevel(currentLevel);
            defaultLoggerLevel = currentLoggingLevel;
        }
    }

    /**
     * Writes an informational message to {@link System#err}.
     * @param message The message.
     */
    @Override
    public void info(String message) {
        checkLoggingLevel();
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
        checkLoggingLevel();
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
        checkLoggingLevel();
        if (logLevel <= AbstractLogger.WARN) {
            System.err.println(message);
        }
    }
}
