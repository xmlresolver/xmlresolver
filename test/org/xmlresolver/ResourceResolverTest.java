/*
 * ResourceResolverTest.java
 * JUnit based test
 *
 * Created on December 30, 2006, 1:32 AM
 */

package org.xmlresolver;


import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author ndw
 */
public class ResourceResolverTest extends TestCase {
    private static Catalog catalog = null;
    private static ResourceResolver resolver = null;
    private static Resolver entityResolver = null;
    
    protected void setUp() throws Exception {
        catalog = new Catalog("catalogs/catalog.xml;catalogs/cache/catalog.xml");
        resolver = new ResourceResolver(catalog);
        entityResolver = new Resolver(resolver);
        resolver.setEntityResolver(entityResolver);
    }

    /**
     * Test of resolveURI method, of class org.xmlresolver.ResourceResolver.
     */
    @Test
    public void testResolveURI() {
        System.out.println("resolveURI");
        
        String href = ".bibliography.xml";
        String base = "file:///home/ndw/";
        
        Resource result = resolver.resolveURI(href, base);
        assertNotNull(result);
    }
    
    /**
     * Test of resolveNamespaceURI method, of class org.xmlresolver.ResourceResolver.
     */
    @Test
    public void testResolveNamespaceURI() throws Exception {
        System.out.println("testResolveNamespaceURI");

        ResourceResolver myResolver = new ResourceResolver(new Catalog("documents/catalog.xml"));
        
        String uri = "http://www.w3.org/2001/XMLSchema";
        String nature = "http://www.isi.edu/in-notes/iana/assignments/media-types/application/xml-dtd";
        String purpose = "http://www.rddl.org/purposes#validation";
        Resource result = myResolver.resolveNamespaceURI(uri, nature, purpose);
        
        assertNotNull(result);
    }
}
