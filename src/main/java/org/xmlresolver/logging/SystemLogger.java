package org.xmlresolver.logging;

import org.xmlresolver.ResolverConfiguration;

import java.util.logging.Logger;

/**
 * The system logger interfaces to a logging backend via a logging backend.
 *
 * <p>This class uses {@link java.util.logging.Logger java.util.logging.Logger}.
 * In earlier releases, it supported SL4J directly, but that's been removed in
 * the interest of reducing the number of dependencies.
 * This logger makes it easy to configure the resolver to log through a standard
 * logging framework, as might be present on a Java application server.</p>
 *
 * <p>Alternatively, if you instantiate the <code>SystemLogger</code> with a
 * {@link java.util.logging.Logger java.util.logging.Logger} directly, it will use that.</p>
 *
 * <p>When instantiated with the {@link org.xmlresolver.ResolverFeature#RESOLVER_LOGGER_CLASS},
 * the default logging framework is always used. To use the {@link java.util.logging.Logger}
 * alternative, you must instantiate the logger yourself and set the
 * {@link org.xmlresolver.ResolverFeature#RESOLVER_LOGGER} feature yourself.</p>
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

    /**
     * Process a warning message with the underlying logging framework.
     * @param message The message.
     */
    @Override
    public void warn(String message) {
        logger.warning(message);
    }

    /**
     * Process an informational message with the underlying logging framework.
     * @param message The message.
     */
    @Override
    public void info(String message) {
        logger.info(message);
    }

    /**
     * Process a debug message with the underlying logging framework.
     * <p>This uses the "fine" method on the Logger.</p>
     * @param message The message.
     */
    @Override
    public void debug(String message) {
        logger.fine(message);
    }
}
