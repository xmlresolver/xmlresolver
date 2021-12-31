package org.xmlresolver;

import org.junit.Test;
import org.xmlresolver.logging.DefaultLogger;
import org.xmlresolver.logging.ResolverLogger;
import org.xmlresolver.logging.SystemLogger;

import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LoggerTest {
    @Test
    public void defaultLogger() {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        assertNotNull(logger);
        assertEquals(DefaultLogger.class, logger.getClass());
    }

    @Test
    public void systemLogger() {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.RESOLVER_LOGGER_CLASS, "org.xmlresolver.logging.SystemLogger");
        ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        assertNotNull(logger);
        assertEquals(SystemLogger.class, logger.getClass());
    }

    @Test
    public void javaSystemLogger() {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        java.util.logging.Logger jlogger = Logger.getLogger("testing");
        config.setFeature(ResolverFeature.RESOLVER_LOGGER, new SystemLogger(jlogger));
        ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        assertNotNull(logger);
        assertEquals(SystemLogger.class, logger.getClass());
        logger.log("WARN", "This is a test");
    }



}
