/*
 * ResourceCacheTest.java
 * JUnit based test
 *
 * Created on January 4, 2007, 2:37 PM
 */

package org.xmlresolver;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import junit.framework.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/**
 *
 * @author ndw
 */
public class ResourceCacheTest extends TestCase {
    
    public ResourceCacheTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testInit() {
        ResourceCache cache = new ResourceCache("catalogs/cache");
        Element catalog = cache.catalog();
    }
    
    public void testAddURI() throws MalformedURLException, IOException {
        ResourceCache cache = new ResourceCache("catalogs/cache");
        String uri = "http://www.w3.org/2001/XMLSchema";
        ResourceConnection conn = new ResourceConnection(uri);
        cache.addURI(conn);
    }

    public void testAddSystem() throws MalformedURLException, IOException {
        ResourceCache cache = new ResourceCache("catalogs/cache");
        String systemId = "http://docbook.org/xml/4.5/docbookx.dtd";
        String publicId = "-//OASIS//DTD DocBook XML V4.5//EN";
        ResourceConnection conn = new ResourceConnection(systemId);
        cache.addSystem(conn, publicId);
    }
    
}
