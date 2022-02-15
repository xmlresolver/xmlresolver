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
import org.xml.sax.XMLReader;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

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
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

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
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

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
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

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
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

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
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

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
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

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
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

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
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

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
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

        s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertEquals(4, s.size());
        assertEquals("x", s.get(0));
        assertEquals("y", s.get(1));
        assertEquals("d", s.get(2));
        assertEquals("e", s.get(3));

        // n.b. make a copy because System.setProperties does not!
        System.setProperties(copyProperties(savedProperties));
    }

    @Test
    public void testClasspathCatalogsEmpty()
    {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        List<String> s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertEquals(0, s.size());

        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, true);
        s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertTrue(s.size() >= 2); // in case there are more catalogs in jars
        boolean data1 = false;
        boolean data2 = false;
        for (String cat : s) {
            data1 = data1 || cat.contains("data1.jar!/org/xmlresolver/catalog.xml");
            data2 = data2 || cat.contains("data2.jar!/org/xmlresolver/catalog.xml");
        }
        assertTrue(data1);
        assertTrue(data2);
    }

    @Test
    public void testClasspathCatalogsDefault()
    {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), null);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        List<String> s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertEquals(1, s.size());
        assertEquals("./catalog.xml", s.get(0));

        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, true);
        s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertTrue(s.size() >= 3); // in case there are more catalogs in jars
        assertEquals("./catalog.xml", s.get(0));
        boolean data1 = false;
        boolean data2 = false;
        for (String cat : s) {
            data1 = data1 || cat.contains("data1.jar!/org/xmlresolver/catalog.xml");
            data2 = data2 || cat.contains("data2.jar!/org/xmlresolver/catalog.xml");
        }
        assertTrue(data1);
        assertTrue(data2);
    }

    @Test
    public void testClasspathCatalogsExplicit()
    {
        Properties savedProperties = copyProperties(System.getProperties());
        System.setProperty("xml.catalog.files", "x;y");

        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), null);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        List<String> s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertEquals(2, s.size());
        assertEquals("x", s.get(0));
        assertEquals("y", s.get(1));

        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, true);
        s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertTrue(s.size() >= 4); // in case there are more catalogs in jars
        assertEquals("x", s.get(0));
        assertEquals("y", s.get(1));
        boolean data1 = false;
        boolean data2 = false;
        for (String cat : s) {
            data1 = data1 || cat.contains("data1.jar!/org/xmlresolver/catalog.xml");
            data2 = data2 || cat.contains("data2.jar!/org/xmlresolver/catalog.xml");
        }
        assertTrue(data1);
        assertTrue(data2);

        // n.b. make a copy because System.setProperties does not!
        System.setProperties(copyProperties(savedProperties));
    }

    @Test
    public void testClasspathCatalogsAdditional()
    {
        Properties savedProperties = copyProperties(System.getProperties());
        System.setProperty("xml.catalog.files", "x;y");

        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), null);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        config.addCatalog("a");

        List<String> s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertEquals(3, s.size());
        assertEquals("x", s.get(0));
        assertEquals("y", s.get(1));
        assertEquals("a", s.get(2));

        config = new XMLResolverConfiguration(Collections.emptyList(), null);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, true);
        config.addCatalog("a");

        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertEquals(3, s.size());
        assertEquals("x", s.get(0));
        assertEquals("y", s.get(1));
        assertEquals("a", s.get(2));

        config = new XMLResolverConfiguration(Collections.emptyList(), null);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, true);
        config.addCatalog("a");

        s = config.getFeature(ResolverFeature.CATALOG_FILES);
        assertTrue(s.size() >= 5); // in case there are more catalogs in jars
        assertEquals("x", s.get(0));
        assertEquals("y", s.get(1));
        assertEquals("a", s.get(2));

        boolean data1 = false;
        boolean data2 = false;
        for (String cat : s) {
            data1 = data1 || cat.contains("data1.jar!/org/xmlresolver/catalog.xml");
            data2 = data2 || cat.contains("data2.jar!/org/xmlresolver/catalog.xml");
        }
        assertTrue(data1);
        assertTrue(data2);

        // n.b. make a copy because System.setProperties does not!
        System.setProperties(copyProperties(savedProperties));
    }

    @Test
    public void testSAXParserFactoryClass() {
        String name = "xml.catalog.saxParserFactoryClass";
        String value = System.getProperty(name);
        System.setProperty(name, "org.xmlresolver.MySAXParserFactory");
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        XMLReader reader = config.getFeature(ResolverFeature.XMLREADER_SUPPLIER).get();
        assertTrue(MySAXParserFactory.parserCount > 0);
        if (value == null) {
            System.clearProperty(name);
        } else {
            System.setProperty(name, value);
        }
    }

    @Test
    public void testXmlReaderSupplier() {
        String name = "xml.catalog.saxParserFactoryClass";
        String value = System.getProperty(name);
        System.setProperty(name, "org.xmlresolver.MySAXParserFactory");
        XMLResolverConfiguration config = new XMLResolverConfiguration();

        Supplier<XMLReader> defaultSupplier = config.getFeature(ResolverFeature.XMLREADER_SUPPLIER);
        config.setFeature(ResolverFeature.XMLREADER_SUPPLIER, MyXMLReaderSupplier.supplier());

        assertNull(config.getFeature(ResolverFeature.SAXPARSERFACTORY_CLASS));

        XMLReader reader = config.getFeature(ResolverFeature.XMLREADER_SUPPLIER).get();
        assertTrue(MyXMLReaderSupplier.parserCount > 0);

        if (value == null) {
            System.clearProperty(name);
        } else {
            System.setProperty(name, value);
        }
    }

}
