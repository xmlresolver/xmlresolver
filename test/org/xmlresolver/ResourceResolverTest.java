/*
 * ResourceResolverTest.java
 * JUnit based test
 *
 * Created on December 30, 2006, 1:32 AM
 */

package org.xmlresolver;


import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ndw
 */
public class ResourceResolverTest {
    private static Catalog catalog = null;
    private static ResourceResolver resolver = null;
    private static Resolver entityResolver = null;

    @Before
    public void setUp() throws Exception {
        catalog = new Catalog("resources/test/catalogs/catalog.xml");
        resolver = new ResourceResolver(catalog);
        entityResolver = new Resolver(resolver);
        resolver.setEntityResolver(entityResolver);
    }

    /**
     * Test of resolveURI method, of class org.xmlresolver.ResourceResolver.
     */
    @Test
    public void testResolveURI() {
        String href = ".bibliography.xml";
        String base = "file:///home/nosuchuser/";
        
        Resource result = resolver.resolveURI(href, base);
        assertNotNull(result);
    }

    /**
     * Test of resolveURI method, of class org.xmlresolver.ResourceResolver.
     */
    @Test
    public void testResolveEntity() {
        String href = ".bibliography.xml";
        String base = "file:///home/nosuchuser/";

        Resource result = resolver.resolveEntity("bibliography", base + href,null);
        assertNotNull(result);
    }

    /**
     * Test of resolveNamespaceURI method, of class org.xmlresolver.ResourceResolver.
     */
    @Test
    public void testResolveNamespaceURI() throws Exception {
        ResourceResolver myResolver = new ResourceResolver(new Catalog("documents/catalog.xml"));
        
        String uri = "http://www.w3.org/2001/XMLSchema";
        String nature = "http://www.isi.edu/in-notes/iana/assignments/media-types/application/xml-dtd";
        String purpose = "http://www.rddl.org/purposes#validation";
        Resource result = myResolver.resolveNamespaceURI(uri, nature, purpose);
        
        assertNotNull(result);
    }
}
