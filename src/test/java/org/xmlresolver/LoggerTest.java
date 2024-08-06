package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xmlresolver.logging.DefaultLogger;
import org.xmlresolver.logging.ResolverLogger;
import org.xmlresolver.logging.SystemLogger;

import java.util.logging.Logger;

public class LoggerTest {
    @Test
    public void defaultLogger() {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        Assertions.assertNotNull(logger);
        Assertions.assertEquals(DefaultLogger.class, logger.getClass());
    }

    @Test
    public void changeConfigurationDefaultLoggingLevel() {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        Assertions.assertNotNull(logger);
        Assertions.assertEquals(DefaultLogger.class, logger.getClass());
        DefaultLogger deflog = (DefaultLogger) logger;
        String orig = config.getFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL);
        config.setFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL, "info");
        Assertions.assertEquals("info", deflog.getLogLevel());
        config.setFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL, "warn");
        Assertions.assertEquals("warn", deflog.getLogLevel());
        config.setFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL, orig);
    }

    @Test
    public void changeDefaultLoggerLoggingLevel() {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        Assertions.assertNotNull(logger);
        Assertions.assertEquals(DefaultLogger.class, logger.getClass());
        DefaultLogger deflog = (DefaultLogger) logger;
        String orig = config.getFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL);
        config.setFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL, "info");
        deflog.setLogLevel("warn");
        Assertions.assertEquals("warn", deflog.getLogLevel());
        Assertions.assertEquals("info", config.getFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL));

        deflog.setLogLevel("debug");
        Assertions.assertEquals("debug", deflog.getLogLevel());
        Assertions.assertEquals("info", config.getFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL));

        config.setFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL, "warn");
        Assertions.assertEquals("warn", deflog.getLogLevel());

        config.setFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL, orig);
    }

    @Test
    public void changeLoggingLevel() {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        Assertions.assertNotNull(logger);
        Assertions.assertEquals(DefaultLogger.class, logger.getClass());
        DefaultLogger deflog = (DefaultLogger) logger;
        String orig = config.getFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL);

        deflog.setLogLevel("info");
        Assertions.assertEquals("info", deflog.getLogLevel());
        deflog.setLogLevel("warn");
        Assertions.assertEquals("warn", deflog.getLogLevel());
        deflog.setLogLevel("debug");
        Assertions.assertEquals("debug", deflog.getLogLevel());
        deflog.setLogLevel("none");
        Assertions.assertEquals("none", deflog.getLogLevel());
        deflog.setLogLevel("spoon");
        Assertions.assertEquals("warn", deflog.getLogLevel());

        config.setFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL, orig);
    }

    @Test
    public void changeDefaultLoggingLevel() {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        Assertions.assertNotNull(logger);
        Assertions.assertEquals(DefaultLogger.class, logger.getClass());
        DefaultLogger deflog = (DefaultLogger) logger;
        String orig = config.getFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL);

        config.setFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL, "info");
        Assertions.assertEquals("info", deflog.getLogLevel());
        config.setFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL, "warn");
        Assertions.assertEquals("warn", deflog.getLogLevel());
        config.setFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL, "debug");
        Assertions.assertEquals("debug", deflog.getLogLevel());
        config.setFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL, "none");
        Assertions.assertEquals("none", deflog.getLogLevel());
        config.setFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL, "spoon");
        Assertions.assertEquals("warn", deflog.getLogLevel());

        config.setFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL, orig);
    }

    @Test
    public void systemLogger() {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.RESOLVER_LOGGER_CLASS, "org.xmlresolver.logging.SystemLogger");
        ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        Assertions.assertNotNull(logger);
        Assertions.assertEquals(SystemLogger.class, logger.getClass());
    }

    @Test
    public void javaSystemLogger() {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        java.util.logging.Logger jlogger = Logger.getLogger("testing");
        config.setFeature(ResolverFeature.RESOLVER_LOGGER, new SystemLogger(jlogger));
        ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        Assertions.assertNotNull(logger);
        Assertions.assertEquals(SystemLogger.class, logger.getClass());
        logger.log("WARN", "This is a test");
    }



}
