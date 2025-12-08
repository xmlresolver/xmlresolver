package org.xmlresolver.logging;

import org.xmlresolver.ResolverConfiguration;

import java.util.logging.Logger;

/**
 * The system logger interfaces to a logging backend via a logging backend.
 */
public class SystemLogger extends AbstractLogger {
    private final java.util.logging.Logger logger;

    /**
     * Initialize the logger using the default backend.
     *
     * <p>The default backend in this case is {@code Logger.getLogger(config.getClass().getName())}.</p>
     *
     * <p>This class doesn't actually use the provided resolver configuration, but it's
     * necessary to support the way loggers are instantiated by the configuration.</p>
     *
     * @param config The resolver configuration.
     */
    public SystemLogger(ResolverConfiguration config) {
        logger = Logger.getLogger(config.getClass().getName());
    }

    /**
     * Initialize the logger using an explicit {@link java.util.logging.Logger}.
     *
     * @param log The logger.
     */
    public SystemLogger(java.util.logging.Logger log) {
        logger = log;
    }

    @Override
    public void error(String message, Object... params) {
        logger.severe(format(message, params));
    }

    @Override
    public void warn(String message, Object... params) {
        logger.warning(format(message, params));
    }

    @Override
    public void info(String message, Object... params) {
        logger.info(format(message, params));
    }

    @Override
    public void debug(String message, Object... params) {
        logger.fine(format(message, params));
    }
}
