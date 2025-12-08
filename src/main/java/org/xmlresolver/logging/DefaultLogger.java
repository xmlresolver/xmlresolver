package org.xmlresolver.logging;

/**
 * The default logger logs to {@link System#err}.
 *
 * <p>In all cases where an unsupported value is provided for the log level,
 * the default level is "warn". See {@link LogLevels}.</p>
 */
public class DefaultLogger extends AbstractLogger {
    private int logLevel;

    /**
     * Construct a default logger with the specified initial logginglevel
     * @param level the log level
     */
    public DefaultLogger(int level) {
        logLevel = LogLevels.validNumber(level);
    }

    /**
     * Construct a default logger with the specified initial logginglevel
     * @param levelName the log level
     */
    public DefaultLogger(String levelName) {
        logLevel = LogLevels.levelNumber(LogLevels.validName(levelName));
    }

    /**
     * Change the log level.
     * @param level the log level
     */
    public void setLogLevel(int level) {
        logLevel = LogLevels.validNumber(level);
    }

    /**
     * Change the log level.
     * @param levelName the log level
     */
    public void setLogLevel(String levelName) {
        logLevel = LogLevels.levelNumber(levelName);
    }

    /**
     * Returns the current log level.
     * @return the log level
     */
    public int getLogLevel() {
        return logLevel;
    }

    /**
     * Returns the current log level name.
     * @return the log level name
     */
    public String getLogLevelName() {
        return LogLevels.levelName(logLevel);
    }

    @Override
    public void error(String message, Object... params) {
        if (logLevel <= LogLevels.ERROR) {
            System.err.println(format(message, params));
        }
    }

    @Override
    public void warn(String message, Object... params) {
        if (logLevel <= LogLevels.WARN) {
            System.err.println(format(message, params));
        }
    }

    @Override
    public void info(String message, Object... params) {
        if (logLevel <= LogLevels.INFO) {
            System.err.println(format(message, params));
        }
    }

    @Override
    public void debug(String message, Object... params) {
        if (logLevel <= LogLevels.DEBUG) {
            System.err.println(format(message, params));
        }
    }
}
