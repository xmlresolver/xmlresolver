/*
 * Resolver2Test.java
 * JUnit based test
 *
 * Created on January 2, 2007, 9:14 AM
 */

package org.xmlresolver;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * Set of tests of using the catalogue and resolver on local files.
 */
public class ResolverLocalFileTest {
    public static final String catalog1 = "src/test/resources/localfilecat.xml";
    XMLResolverConfiguration config = null;
    Resolver resolver = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, false);
        config.setFeature(ResolverFeature.PREFER_PUBLIC, false);
        resolver = new Resolver(config);
    }

    @Test
    public void lookupLocalFile() throws TransformerException {
        //this is what will get often get called by Saxon for a local file
        Source result = resolver.resolve("IVOA-v1.0.vo-dml.xml", "");
        assertNotNull("should have returned a source",result);
        assertEquals("file:///Users/pharriso/Work/ivoa/vo-dml/models/ivoa/vo-dml/IVOA-v1.0.vo-dml.xml",result.getSystemId());
    }
    
    @Test
    public void lookupLocalFileNull() throws TransformerException {
        //if the base is unknown then perhaps this should be the call..
        Source result = resolver.resolve("IVOA-v1.0.vo-dml.xml", null);
        assertNotNull("should have returned a source",result);
        assertEquals("file:///Users/pharriso/Work/ivoa/vo-dml/models/ivoa/vo-dml/IVOA-v1.0.vo-dml.xml",result.getSystemId());
    }
    @Test
    public void lookupLocalFileURI() throws TransformerException {
        //this is probably what clients should call for a local file to be compliant
        Source result = resolver.resolve("file://./IVOA-asurl.vo-dml.xml", null);
        assertNotNull("should have returned a source",result);
        assertEquals("file:///Users/pharriso/Work/ivoa/vo-dml/models/ivoa/vo-dml/IVOA-v1.0.vo-dml.xml",result.getSystemId());

    }
    
    @Test
    public void lookupLocalFileURIblankBase() throws TransformerException {
        //this is probably what clients should call for a local file to be compliant
        Source result = resolver.resolve("file://./IVOA-asurl.vo-dml.xml", "");
        assertNotNull("should have returned a source",result);
        assertEquals("file:///Users/pharriso/Work/ivoa/vo-dml/models/ivoa/vo-dml/IVOA-v1.0.vo-dml.xml",result.getSystemId());

    }

    
    @Test
    public void lookupNotInCatalogue() throws TransformerException {
        //something not in the catalogue should just be returned if the resolver is to be used directly in saxon for instance
        Source result = resolver.resolve("afile.ext", "file:///root/");
        assertNotNull("should have returned a source",result);
        assertEquals("file:///root/file.ext",result.getSystemId());
      
    }
    
    @Test
    public void URITest() throws URISyntaxException {
        URI uri = new URI("file://./IVOA-v1.0.vo-dml.xml");
        assertNotNull(uri);
        System.out.println(uri.toString());
    }
    
}
