/*
 * CatalogLookupTest.java
 *
 * Created on December 30, 2006, 9:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.XMLReader;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.fail;

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

        Assertions.assertEquals(1, config.getFeature(ResolverFeature.CATALOG_FILES).size());
        Assertions.assertEquals("./catalog.xml", config.getFeature(ResolverFeature.CATALOG_FILES).get(0));
        Assertions.assertEquals(ResolverFeature.PREFER_PUBLIC.getDefaultValue(), config.getFeature(ResolverFeature.PREFER_PUBLIC));
        Assertions.assertEquals(ResolverFeature.ALLOW_CATALOG_PI.getDefaultValue(), config.getFeature(ResolverFeature.ALLOW_CATALOG_PI));
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

        XMLResolverConfiguration config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

        List<String> s = config.getFeature(ResolverFeature.CATALOG_FILES);
        Assertions.assertEquals(3, s.size());
        Assertions.assertEquals("a", s.get(0));
        Assertions.assertEquals("b", s.get(1));
        Assertions.assertEquals("c", s.get(2));

        Assertions.assertEquals(true, config.getFeature(ResolverFeature.PREFER_PUBLIC));
        Assertions.assertEquals(true, config.getFeature(ResolverFeature.ALLOW_CATALOG_PI));

        // n.b. make a copy because System.setProperties does not!
        System.setProperties(copyProperties(savedProperties));
        System.setProperty("xmlresolver.properties", "");
        System.setProperty("xml.catalog.files", "d;e;f");
        System.setProperty("xml.catalog.prefer", "system");
        System.setProperty("xml.catalog.allowPI", "false");

        config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

        s = config.getFeature(ResolverFeature.CATALOG_FILES);
        Assertions.assertEquals(3, s.size());
        Assertions.assertEquals("d", s.get(0));
        Assertions.assertEquals("e", s.get(1));
        Assertions.assertEquals("f", s.get(2));

        Assertions.assertEquals(false, config.getFeature(ResolverFeature.PREFER_PUBLIC));
        Assertions.assertEquals(false, config.getFeature(ResolverFeature.ALLOW_CATALOG_PI));

        System.setProperties(copyProperties(savedProperties));
        System.setProperty("xmlresolver.properties", "");
        config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

        Assertions.assertEquals(1, config.getFeature(ResolverFeature.CATALOG_FILES).size());
        Assertions.assertEquals("./catalog.xml", config.getFeature(ResolverFeature.CATALOG_FILES).get(0));
        Assertions.assertEquals(ResolverFeature.PREFER_PUBLIC.getDefaultValue(), config.getFeature(ResolverFeature.PREFER_PUBLIC));
        Assertions.assertEquals(ResolverFeature.ALLOW_CATALOG_PI.getDefaultValue(), config.getFeature(ResolverFeature.ALLOW_CATALOG_PI));

        System.setProperties(copyProperties(savedProperties));
    }

    @Test
    public void testPropertyConfiguration1() {
        URL url = XMLResolver.class.getResource("/prop1.properties");
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.singletonList(url), null);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

        List<String> s = config.getFeature(ResolverFeature.CATALOG_FILES);
        Assertions.assertEquals(3, s.size());
        Assertions.assertEquals("a", s.get(0));
        Assertions.assertEquals("b", s.get(1));
        Assertions.assertEquals("c", s.get(2));

        Assertions.assertEquals(true, config.getFeature(ResolverFeature.PREFER_PUBLIC));
        Assertions.assertEquals(true, config.getFeature(ResolverFeature.ALLOW_CATALOG_PI));
    }

    @Test
    public void testPropertyConfiguration2() {
        URL url = XMLResolver.class.getResource("/prop2.properties");
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.singletonList(url), null);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

        URI uri = null;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            fail();
        }

        List<String> s = config.getFeature(ResolverFeature.CATALOG_FILES);
        Assertions.assertEquals(3, s.size());
        Assertions.assertEquals(uri.resolve("d").toString(), s.get(0));
        Assertions.assertEquals(uri.resolve("e").toString(), s.get(1));
        Assertions.assertEquals(uri.resolve("f").toString(), s.get(2));

        Assertions.assertEquals(false, config.getFeature(ResolverFeature.PREFER_PUBLIC));
        Assertions.assertEquals(false, config.getFeature(ResolverFeature.ALLOW_CATALOG_PI));
    }

    @Test
    public void testAdditionalSystemCatalogsConfiguration()
    {
        Properties savedProperties = copyProperties(System.getProperties());

        System.setProperty("xmlresolver.properties", "");
        System.setProperty("xml.catalog.files", "d;e");
        URL url = XMLResolver.class.getResource("/prop4.properties");
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.singletonList(url), null);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

        List<String> s = config.getFeature(ResolverFeature.CATALOG_FILES);
        Assertions.assertEquals(2, s.size());
        Assertions.assertEquals("d", s.get(0));
        Assertions.assertEquals("e", s.get(1));

        // n.b. make a copy because System.setProperties does not!
        System.setProperties(copyProperties(savedProperties));

        System.setProperty("xmlresolver.properties", "");
        System.setProperty("xml.catalog.additions", "d;e");
        url = XMLResolver.class.getResource("/prop4.properties");
        config = new XMLResolverConfiguration(Collections.singletonList(url), null);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

        s = config.getFeature(ResolverFeature.CATALOG_FILES);
        Assertions.assertEquals(5, s.size());
        Assertions.assertEquals("a", s.get(0));
        Assertions.assertEquals("b", s.get(1));
        Assertions.assertEquals("c", s.get(2));
        Assertions.assertEquals("d", s.get(3));
        Assertions.assertEquals("e", s.get(4));

        // n.b. make a copy because System.setProperties does not!
        System.setProperties(copyProperties(savedProperties));
    }

    @Test
    public void testAdditionalPropertyCatalogsConfiguration()
    {
        Properties savedProperties = copyProperties(System.getProperties());
        System.setProperty("xml.catalog.files", "x;y");

        URL url = XMLResolver.class.getResource("/prop1.properties");
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.singletonList(url), null);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

        List<String> s = config.getFeature(ResolverFeature.CATALOG_FILES);
        Assertions.assertEquals(3, s.size());
        Assertions.assertEquals("a", s.get(0));
        Assertions.assertEquals("b", s.get(1));
        Assertions.assertEquals("c", s.get(2));

        // n.b. make a copy because System.setProperties does not!
        System.setProperties(copyProperties(savedProperties));

        savedProperties = copyProperties(System.getProperties());
        System.setProperty("xml.catalog.files", "x;y");

        url = XMLResolver.class.getResource("/prop3.properties");
        config = new XMLResolverConfiguration(Collections.singletonList(url), null);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);

        s = config.getFeature(ResolverFeature.CATALOG_FILES);
        Assertions.assertEquals(4, s.size());
        Assertions.assertEquals("x", s.get(0));
        Assertions.assertEquals("y", s.get(1));
        Assertions.assertEquals("d", s.get(2));
        Assertions.assertEquals("e", s.get(3));

        // n.b. make a copy because System.setProperties does not!
        System.setProperties(copyProperties(savedProperties));
    }

    @Test
    public void testClasspathCatalogsEmpty()
    {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        List<String> s = config.getFeature(ResolverFeature.CATALOG_FILES);
        Assertions.assertEquals(0, s.size());

        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, true);
        s = config.getFeature(ResolverFeature.CATALOG_FILES);
        Assertions.assertTrue(s.size() >= 2); // in case there are more catalogs in jars
        boolean data1 = false;
        boolean data2 = false;
        for (String cat : s) {
            data1 = data1 || cat.contains("data1.jar!/org/xmlresolver/catalog.xml");
            data2 = data2 || cat.contains("data2.jar!/org/xmlresolver/catalog.xml");
        }
        Assertions.assertTrue(data1);
        Assertions.assertTrue(data2);
    }

    @Test
    public void testClasspathCatalogsDefault()
    {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), null);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        List<String> s = config.getFeature(ResolverFeature.CATALOG_FILES);
        Assertions.assertEquals(1, s.size());
        Assertions.assertEquals("./catalog.xml", s.get(0));

        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, true);
        s = config.getFeature(ResolverFeature.CATALOG_FILES);
        Assertions.assertTrue(s.size() >= 3); // in case there are more catalogs in jars
        Assertions.assertEquals("./catalog.xml", s.get(0));
        boolean data1 = false;
        boolean data2 = false;
        for (String cat : s) {
            data1 = data1 || cat.contains("data1.jar!/org/xmlresolver/catalog.xml");
            data2 = data2 || cat.contains("data2.jar!/org/xmlresolver/catalog.xml");
        }
        Assertions.assertTrue(data1);
        Assertions.assertTrue(data2);
    }

    @Test
    public void testClasspathCatalogsExplicit()
    {
        Properties savedProperties = copyProperties(System.getProperties());
        System.setProperty("xml.catalog.files", "x;y");

        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), null);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        List<String> s = config.getFeature(ResolverFeature.CATALOG_FILES);
        Assertions.assertEquals(2, s.size());
        Assertions.assertEquals("x", s.get(0));
        Assertions.assertEquals("y", s.get(1));

        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, true);
        s = config.getFeature(ResolverFeature.CATALOG_FILES);
        Assertions.assertTrue(s.size() >= 4); // in case there are more catalogs in jars
        Assertions.assertEquals("x", s.get(0));
        Assertions.assertEquals("y", s.get(1));
        boolean data1 = false;
        boolean data2 = false;
        for (String cat : s) {
            data1 = data1 || cat.contains("data1.jar!/org/xmlresolver/catalog.xml");
            data2 = data2 || cat.contains("data2.jar!/org/xmlresolver/catalog.xml");
        }
        Assertions.assertTrue(data1);
        Assertions.assertTrue(data2);

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
        Assertions.assertEquals(3, s.size());
        Assertions.assertEquals("x", s.get(0));
        Assertions.assertEquals("y", s.get(1));
        Assertions.assertEquals("a", s.get(2));

        config = new XMLResolverConfiguration(Collections.emptyList(), null);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, true);
        config.addCatalog("a");

        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        s = config.getFeature(ResolverFeature.CATALOG_FILES);
        Assertions.assertEquals(3, s.size());
        Assertions.assertEquals("x", s.get(0));
        Assertions.assertEquals("y", s.get(1));
        Assertions.assertEquals("a", s.get(2));

        config = new XMLResolverConfiguration(Collections.emptyList(), null);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, true);
        config.addCatalog("a");

        s = config.getFeature(ResolverFeature.CATALOG_FILES);
        Assertions.assertTrue(s.size() >= 5); // in case there are more catalogs in jars
        Assertions.assertEquals("x", s.get(0));
        Assertions.assertEquals("y", s.get(1));
        Assertions.assertEquals("a", s.get(2));

        boolean data1 = false;
        boolean data2 = false;
        for (String cat : s) {
            data1 = data1 || cat.contains("data1.jar!/org/xmlresolver/catalog.xml");
            data2 = data2 || cat.contains("data2.jar!/org/xmlresolver/catalog.xml");
        }
        Assertions.assertTrue(data1);
        Assertions.assertTrue(data2);

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
        Assertions.assertTrue(MySAXParserFactory.parserCount > 0);
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

        Assertions.assertNull(config.getFeature(ResolverFeature.SAXPARSERFACTORY_CLASS));

        XMLReader reader = config.getFeature(ResolverFeature.XMLREADER_SUPPLIER).get();
        Assertions.assertTrue(MyXMLReaderSupplier.parserCount > 0);

        if (value == null) {
            System.clearProperty(name);
        } else {
            System.setProperty(name, value);
        }
    }

}
