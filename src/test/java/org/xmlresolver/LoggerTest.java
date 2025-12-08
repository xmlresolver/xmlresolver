package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xmlresolver.logging.DefaultLogger;
import org.xmlresolver.logging.LogLevels;
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
        String orig = config.getFeature(ResolverFeature.LOGGER_LOG_LEVEL);
        config.setFeature(ResolverFeature.LOGGER_LOG_LEVEL, "info");
        Assertions.assertEquals("info", config.getFeature(ResolverFeature.LOGGER_LOG_LEVEL));
        config.setFeature(ResolverFeature.LOGGER_LOG_LEVEL, orig);
    }

    @Test
    public void changeLoggingLevel() {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        Assertions.assertNotNull(logger);
        Assertions.assertEquals(DefaultLogger.class, logger.getClass());
        DefaultLogger deflog = (DefaultLogger) logger;
        String orig = config.getFeature(ResolverFeature.LOGGER_LOG_LEVEL);

        deflog.setLogLevel("info");
        Assertions.assertEquals(LogLevels.INFO, deflog.getLogLevel());
        Assertions.assertEquals("info", deflog.getLogLevelName());
        deflog.setLogLevel("warn");
        Assertions.assertEquals(LogLevels.WARN, deflog.getLogLevel());
        Assertions.assertEquals("warn", deflog.getLogLevelName());
        deflog.setLogLevel("debug");
        Assertions.assertEquals(LogLevels.DEBUG, deflog.getLogLevel());
        Assertions.assertEquals("debug", deflog.getLogLevelName());
        deflog.setLogLevel("none");
        Assertions.assertEquals(LogLevels.NONE, deflog.getLogLevel());
        Assertions.assertEquals("none", deflog.getLogLevelName());
        deflog.setLogLevel("spoon");
        Assertions.assertEquals(LogLevels.WARN, deflog.getLogLevel());
        Assertions.assertEquals("warn", deflog.getLogLevelName());

        config.setFeature(ResolverFeature.LOGGER_LOG_LEVEL, orig);
    }

    @Test
    public void changeDefaultLoggerLoggingLevel() {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        Assertions.assertNotNull(logger);
        Assertions.assertEquals(DefaultLogger.class, logger.getClass());
        DefaultLogger deflog = (DefaultLogger) logger;
        String orig = config.getFeature(ResolverFeature.LOGGER_LOG_LEVEL);
        config.setFeature(ResolverFeature.LOGGER_LOG_LEVEL, "info");
        deflog.setLogLevel("warn");
        Assertions.assertEquals(LogLevels.WARN, deflog.getLogLevel());

        deflog.setLogLevel(LogLevels.DEBUG);
        Assertions.assertEquals("debug", deflog.getLogLevelName());

        config.setFeature(ResolverFeature.LOGGER_LOG_LEVEL, orig);
    }

    @Test
    public void changeDefaultLoggingLevel() {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        String orig = config.getFeature(ResolverFeature.LOGGER_LOG_LEVEL);
        config.setFeature(ResolverFeature.LOGGER_LOG_LEVEL, "none");
        ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        Assertions.assertNotNull(logger);
        Assertions.assertEquals(DefaultLogger.class, logger.getClass());
        DefaultLogger deflog = (DefaultLogger) logger;

        Assertions.assertEquals(LogLevels.NONE, deflog.getLogLevel());

        config.setFeature(ResolverFeature.LOGGER_LOG_LEVEL, "warning");
        Assertions.assertEquals(LogLevels.NONE, deflog.getLogLevel());

        config.setFeature(ResolverFeature.LOGGER_LOG_LEVEL, orig);
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
        logger.warn("This is a test");
    }

}
