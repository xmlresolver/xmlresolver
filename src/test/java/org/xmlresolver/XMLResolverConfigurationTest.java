/*
 * CatalogLookupTest.java
 *
 * Created on December 30, 2006, 9:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * @author ndw
 */
public class XMLResolverConfigurationTest {
    @Test
    public void testEmptyConfiguration()
    {
        Properties savedProperties = copyProperties(System.getProperties());
        System.setProperty("xmlresolver.properties", "");
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        assertEquals(1, config.getFeature(ResolverFeature.CATALOG_FILES).size());
        assertEquals("./catalog.xml", config.getFeature(ResolverFeature.CATALOG_FILES).get(0));
        assertEquals(ResolverFeature.PREFER_PUBLIC.getDefaultValue(), config.getFeature(ResolverFeature.PREFER_PUBLIC));
        assertEquals(ResolverFeature.ALLOW_CATALOG_PI.getDefaultValue(), config.getFeature(ResolverFeature.ALLOW_CATALOG_PI));
        assertEquals(ResolverFeature.CACHE_DIRECTORY.getDefaultValue(), config.getFeature(ResolverFeature.CACHE_DIRECTORY));
        assertEquals(ResolverFeature.CACHE_UNDER_HOME.getDefaultValue(), config.getFeature(ResolverFeature.CACHE_UNDER_HOME));
        System.setProperties(copyProperties(savedProperties));
    }

    private Properties copyProperties(Properties props) {
        Properties copiedProperties = new Properties();
        for (Object propObj : props.keySet()) {
            if (propObj instanceof String) {
                String prop = (String) propObj;
                if (!prop.startsWith("xml.catalog.")) {
                    copiedProperties.put(prop, props.getProperty(prop));
                }
            }
        }
        return copiedProperties;
    }

    @Test
    public void testSystemPropertyConfiguration()
    {
        Properties savedProperties = copyProperties(System.getProperties());
        System.setProperty("xmlresolver.properties", "");
        System.setProperty("xml.catalog.files", "a;b;c");
        System.setProperty("xml.catalog.prefer", "public");
        System.setProperty("xml.catalog.allowPI", "true");
        System.setProperty("xml.catalog.cache", "/dev/null");
        System.setProperty("xml.catalog.cacheUnderHome", "true");
        System.setProperty("xml.catalog.cache.file", "true");
        System.setProperty("xml.catalog.cache.jar", "true");

        XMLResolverConfiguration config = new XMLResolverConfiguration();

        List<String> s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertEquals(3, s.size());
        assertEquals("a", s.get(0));
        assertEquals("b", s.get(1));
        assertEquals("c", s.get(2));

        assertEquals(true, config.getFeature(ResolverFeature.PREFER_PUBLIC));
        assertEquals(true, config.getFeature(ResolverFeature.ALLOW_CATALOG_PI));
        assertEquals("/dev/null", config.getFeature(ResolverFeature.CACHE_DIRECTORY));
        assertEquals(true, config.getFeature(ResolverFeature.CACHE_UNDER_HOME));

        // n.b. make a copy because System.setProperties does not!
        System.setProperties(copyProperties(savedProperties));
        System.setProperty("xmlresolver.properties", "");
        System.setProperty("xml.catalog.files", "d;e;f");
        System.setProperty("xml.catalog.prefer", "system");
        System.setProperty("xml.catalog.allowPI", "false");
        System.setProperty("xml.catalog.cache", "/dev/null");
        System.setProperty("xml.catalog.cacheUnderHome", "false");
        System.setProperty("xml.catalog.cache.file", "false");
        System.setProperty("xml.catalog.cache.jar", "false");

        config = new XMLResolverConfiguration();

        s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertEquals(3, s.size());
        assertEquals("d", s.get(0));
        assertEquals("e", s.get(1));
        assertEquals("f", s.get(2));

        assertEquals(false, config.getFeature(ResolverFeature.PREFER_PUBLIC));
        assertEquals(false, config.getFeature(ResolverFeature.ALLOW_CATALOG_PI));
        assertEquals("/dev/null", config.getFeature(ResolverFeature.CACHE_DIRECTORY));
        assertEquals(false, config.getFeature(ResolverFeature.CACHE_UNDER_HOME));

        System.setProperties(copyProperties(savedProperties));
        System.setProperty("xmlresolver.properties", "");
        config = new XMLResolverConfiguration();

        assertEquals(1, config.getFeature(ResolverFeature.CATALOG_FILES).size());
        assertEquals("./catalog.xml", config.getFeature(ResolverFeature.CATALOG_FILES).get(0));
        assertEquals(ResolverFeature.PREFER_PUBLIC.getDefaultValue(), config.getFeature(ResolverFeature.PREFER_PUBLIC));
        assertEquals(ResolverFeature.ALLOW_CATALOG_PI.getDefaultValue(), config.getFeature(ResolverFeature.ALLOW_CATALOG_PI));
        assertEquals(ResolverFeature.CACHE_DIRECTORY.getDefaultValue(), config.getFeature(ResolverFeature.CACHE_DIRECTORY));
        assertEquals(ResolverFeature.CACHE_UNDER_HOME.getDefaultValue(), config.getFeature(ResolverFeature.CACHE_UNDER_HOME));

        System.setProperties(copyProperties(savedProperties));
    }

    @Test
    public void testPropertyConfiguration1() {
        URL url = Resolver.class.getResource("/prop1.properties");
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.singletonList(url), null);

        List<String> s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertEquals(3, s.size());
        assertEquals("a", s.get(0));
        assertEquals("b", s.get(1));
        assertEquals("c", s.get(2));

        assertEquals(true, config.getFeature(ResolverFeature.PREFER_PUBLIC));
        assertEquals(true, config.getFeature(ResolverFeature.ALLOW_CATALOG_PI));
        assertEquals("/dev/null", config.getFeature(ResolverFeature.CACHE_DIRECTORY));
        assertEquals(true, config.getFeature(ResolverFeature.CACHE_UNDER_HOME));
    }

    @Test
    public void testPropertyConfiguration2() {
        URL url = Resolver.class.getResource("/prop2.properties");
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.singletonList(url), null);

        URI uri = null;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            fail();
        }

        List<String> s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertEquals(3, s.size());
        assertEquals(uri.resolve("d").toString(), s.get(0));
        assertEquals(uri.resolve("e").toString(), s.get(1));
        assertEquals(uri.resolve("f").toString(), s.get(2));

        assertEquals(false, config.getFeature(ResolverFeature.PREFER_PUBLIC));
        assertEquals(false, config.getFeature(ResolverFeature.ALLOW_CATALOG_PI));
        assertEquals("/dev/null", config.getFeature(ResolverFeature.CACHE_DIRECTORY));
        assertEquals(false, config.getFeature(ResolverFeature.CACHE_UNDER_HOME));
    }

    @Test
    public void testAdditionalSystemCatalogsConfiguration()
    {
        Properties savedProperties = copyProperties(System.getProperties());

        System.setProperty("xmlresolver.properties", "");
        System.setProperty("xml.catalog.files", "d;e");
        URL url = Resolver.class.getResource("/prop4.properties");
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.singletonList(url), null);

        List<String> s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertEquals(2, s.size());
        assertEquals("d", s.get(0));
        assertEquals("e", s.get(1));

        // n.b. make a copy because System.setProperties does not!
        System.setProperties(copyProperties(savedProperties));

        System.setProperty("xmlresolver.properties", "");
        System.setProperty("xml.catalog.additions", "d;e");
        url = Resolver.class.getResource("/prop4.properties");
        config = new XMLResolverConfiguration(Collections.singletonList(url), null);

        s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertEquals(5, s.size());
        assertEquals("a", s.get(0));
        assertEquals("b", s.get(1));
        assertEquals("c", s.get(2));
        assertEquals("d", s.get(3));
        assertEquals("e", s.get(4));

        // n.b. make a copy because System.setProperties does not!
        System.setProperties(copyProperties(savedProperties));
    }

    @Test
    public void testAdditionalPropertyCatalogsConfiguration()
    {
        Properties savedProperties = copyProperties(System.getProperties());
        System.setProperty("xml.catalog.files", "x;y");

        URL url = Resolver.class.getResource("/prop1.properties");
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.singletonList(url), null);

        List<String> s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertEquals(3, s.size());
        assertEquals("a", s.get(0));
        assertEquals("b", s.get(1));
        assertEquals("c", s.get(2));

        // n.b. make a copy because System.setProperties does not!
        System.setProperties(copyProperties(savedProperties));

        savedProperties = copyProperties(System.getProperties());
        System.setProperty("xml.catalog.files", "x;y");

        url = Resolver.class.getResource("/prop3.properties");
        config = new XMLResolverConfiguration(Collections.singletonList(url), null);

        s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertEquals(4, s.size());
        assertEquals("x", s.get(0));
        assertEquals("y", s.get(1));
        assertEquals("d", s.get(2));
        assertEquals("e", s.get(3));

        // n.b. make a copy because System.setProperties does not!
        System.setProperties(copyProperties(savedProperties));
    }
}
