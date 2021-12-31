package org.xmlresolver.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlresolver.Resolver;
import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.ResolverFeature;

/**
 * The system logger interfaces to a logging backend via a logging backend.
 *
 * <p>This class supports either configuration with {@link org.slf4j.Logger org.slf4j.Logger} or
 * configuration directly with a {@link java.util.logging.Logger java.util.logging.Logger}.</p>
 *
 * <p>This logger makes it easy to configure the resolver to log through a standard
 * logging framework, as might be present on a Java application server. By default
 * the logger uses the {@link org.slf4j.LoggerFactory org.slf4j.LoggerFactory} to create a logger. This logger
 * can be supported at runtime by a wide variety of concrete backend classes. For details
 * on how SLF4J finds a logging backend, see their documentation.</p>
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
    private final Logger logger;
    private final java.util.logging.Logger jlogger;

    /**
     * Initialize the logger using the default backend.
     *
     * <p>This class doesn't actually use the provided resolver configuration, but it's
     * necessary to support the way loggers are instantiated by the configuration.</p>
     *
     * @param config The resolver configuration.
     */
    public SystemLogger(ResolverConfiguration config) {
        logger = LoggerFactory.getLogger(Resolver.class);
        jlogger = null;
    }

    /**
     * Initialize the logger using an explicit {@link java.util.logging.Logger}.
     *
     * @param log The logger.
     */
    public SystemLogger(java.util.logging.Logger log) {
        logger = null;
        jlogger = log;
    }

    /**
     * Process a warning message with the underlying logging framework.
     * @param message The message.
     */
    @Override
    public void warn(String message) {
        if (jlogger != null) {
            jlogger.warning(message);
        } else {
            logger.warn(message);
        }
    }

    /**
     * Process an informational message with the underlying logging framework.
     * @param message The message.
     */
    @Override
    public void info(String message) {
        if (jlogger != null) {
            jlogger.info(message);
        } else {
            logger.info(message);
        }
    }

    /**
     * Process a debug message with the underlying logging framework.
     * @param message The message.
     */
    @Override
    public void debug(String message) {
        if (jlogger != null) {
            jlogger.fine(message);
        } else {
            logger.debug(message);
        }
    }
}
