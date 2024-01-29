/*
 * ResourceResolverTest.java
 * JUnit based test
 *
 * Created on December 30, 2006, 1:32 AM
 */

package org.xmlresolver;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlresolver.tools.ResolvingXMLReader;
import org.xmlresolver.utils.URIUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * @author ndw
 */
public class ClasspathTest {
    public static final List<String> catalogs = Arrays.asList("classpath:path/catalog.xml", "src/test/resources/cpcatalog.xml");
    public static XMLResolverConfiguration config = null;
    public static CatalogManager manager = null;
    public static XMLResolver resolver = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, catalogs);
        manager = new CatalogManager(config);
        resolver = new XMLResolver(config);
    }

    @Test
    public void testResolveUri() {
        try {
            URI baseURI = new URI("classpath:/my/class/path/");
            URI resolved = URIUtils.resolve(baseURI, "my-file.xml");
            Assert.assertEquals("classpath:my/class/path/my-file.xml", resolved.toString());
        } catch (Exception ex) {
            fail();
        }
    }


    @Test
    public void testLookup() {
        URI res = manager.lookupURI("http://example.com/example.xml");
        assertEquals(URI.create("classpath:path/example-doc.xml"), res);
    }

    @Test
    public void testResource() {
        XMLResolver resolver = new XMLResolver(config);
        ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
        try {
            reader.parse("src/test/resources/classpath.xml");
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

    @Test
    public void testClasspath() {
        String href = "classpath:path/example-doc.xml";

        String line = null;
        try {
            ResourceRequest request = resolver.getRequest(href, null);
            ResourceResponse resp = resolver.resolve(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getInputStream()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        assertEquals("<doc>test</doc>", line);
    }

    @Test
    public void testAlternateClassLoader() {
        String href = "classpath:path/example-doc.xml";
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        XMLResolverConfiguration localconfig = new XMLResolverConfiguration(config);
        localconfig.setFeature(ResolverFeature.CLASSLOADER, loader);
        XMLResolver localresolver = new XMLResolver(localconfig);

        String line = null;
        try {
            ResourceRequest request = localresolver.getRequest(href);
            ResourceResponse result = localresolver.resolve(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getInputStream()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        assertEquals("<doc>test</doc>", line);
    }


    @Test
    public void testClasspathSlash() {
        String href = "classpath:/path/example-doc.xml";

        String line = null;
        try {
            ResourceRequest request = resolver.getRequest(href, null);
            ResourceResponse resp = resolver.resolve(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getInputStream()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        assertEquals("<doc>test</doc>", line);
    }

    @Test
    public void testClasspathText() {
        String href = "test.txt";
        String base = "http://example.com/";

        String line = null;
        try {
            ResourceRequest request = resolver.getRequest(href, base);
            ResourceResponse resp = resolver.resolve(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getInputStream()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        assertEquals("This is a test.", line);
    }

    @Test
    public void testClasspathXml() {
        String href = "example.xml";
        String base = "http://example.com/";

        String line = null;
        try {
            ResourceRequest request = resolver.getRequest(href, base);
            ResourceResponse resp = resolver.resolve(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getInputStream()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        assertEquals("<doc>test</doc>", line);
    }

    @Test
    public void testClasspathCatalog() {
        XMLResolverConfiguration config = new XMLResolverConfiguration("classpath:org/xmlresolver/test/no-such-catalog.xml;classpath:path/catalog.xml");

        String href = "example.xml";
        String base = "http://example.com/";

        String line = null;
        try {
            ResourceRequest request = resolver.getRequest(href, base);
            ResourceResponse result = resolver.resolve(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getInputStream()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        assertEquals("<doc>test</doc>", line);

    }

}
