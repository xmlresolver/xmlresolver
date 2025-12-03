package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class Issue262Test {
    @Test
    public void issue262a() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration();
            config.setFeature(ResolverFeature.PREFER_PUBLIC, true);
            config.setFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL, "debug");
            config.setFeature(ResolverFeature.CATALOG_FILES, List.of("classpath:/iss262.xml"));
            EntityResolver resolver = new Resolver(config);

            InputSource source = resolver.resolveEntity("-//EXAMPLE//TEST//EN", null);
            Assertions.assertNull(source.getSystemId());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

    @Test
    public void issue262b() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration();
            config.setFeature(ResolverFeature.PREFER_PUBLIC, true);
            config.setFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL, "debug");
            config.setFeature(ResolverFeature.CATALOG_FILES, List.of("classpath:/iss262.xml"));
            config.setFeature(ResolverFeature.MASK_JAR_URIS, false);
            EntityResolver resolver = new Resolver(config);

            InputSource source = resolver.resolveEntity("-//EXAMPLE//TEST//EN", null);
            Assertions.assertNotNull(source.getSystemId());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

    @Test
    public void issue262c() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration();
            config.setFeature(ResolverFeature.PREFER_PUBLIC, true);
            config.setFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL, "debug");
            config.setFeature(ResolverFeature.CATALOG_FILES, List.of("classpath:/iss262.xml"));
            XMLResolver resolver = new XMLResolver(config);

            InputSource source = resolver.getEntityResolver().resolveEntity("-//EXAMPLE//TEST//EN", null);
            Assertions.assertNull(source.getSystemId());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

    @Test
    public void issue262d() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration();
            config.setFeature(ResolverFeature.PREFER_PUBLIC, true);
            config.setFeature(ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL, "debug");
            config.setFeature(ResolverFeature.CATALOG_FILES, List.of("classpath:/iss262.xml"));
            XMLResolver resolver = new XMLResolver(config);

            InputSource source = resolver.getEntityResolver2().resolveEntity("-//EXAMPLE//TEST//EN", null);
            Assertions.assertNull(source.getSystemId());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }
}
