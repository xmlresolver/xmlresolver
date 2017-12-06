/*
 * ResourceCacheTest.java
 * JUnit based test
 *
 * Created on January 4, 2007, 2:37 PM
 */

package org.xmlresolver;

import org.junit.Test;
import org.w3c.dom.Element;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 *
 * @author ndw
 */
public class ResourceCacheTest {
    @Test
    public void testInit() {
        ResourceCache cache = new ResourceCache("catalogs/cache");
        Element catalog = cache.catalog();
    }

    @Test
    public void testAddURI() throws MalformedURLException, IOException {
        ResourceCache cache = new ResourceCache("src/test/resources/catalogs/cache");
        String uri = "http://www.w3.org/2001/XMLSchema";
        ResourceConnection conn = new ResourceConnection(uri);
        cache.addURI(conn);
    }

    @Test
    public void testAddSystem() throws MalformedURLException, IOException {
        ResourceCache cache = new ResourceCache("src/test/resources/catalogs/cache");
        String systemId = "http://docbook.org/xml/4.5/docbookx.dtd";
        String publicId = "-//OASIS//DTD DocBook XML V4.5//EN";
        ResourceConnection conn = new ResourceConnection(systemId);
        cache.addSystem(conn, publicId);
    }
    
}
