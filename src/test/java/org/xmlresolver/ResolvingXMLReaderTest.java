/*
 * ResolvingXMLReaderTest.java
 * JUnit based test
 *
 * Created on January 11, 2007, 9:57 AM
 */

package org.xmlresolver;

import org.junit.Test;
import org.xmlresolver.tools.ResolvingXMLReader;

import java.util.Collections;

import static junit.framework.TestCase.fail;

/**
 *
 * @author ndw
 */
public class ResolvingXMLReaderTest {

    @Test
    public void testReader(){
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.ALLOW_CATALOG_PI, true);
        XMLResolver resolver = new XMLResolver(config);

        try {
            ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
            reader.parse("src/test/resources/documents/pitest.xml");
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void testForbiddenPI() {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.ALLOW_CATALOG_PI, false);
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("dummy.cat"));
        XMLResolver resolver = new XMLResolver(config);

        try {
            ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
            reader.parse("src/test/resources/documents/pitest.xml");
            fail();
        } catch (Exception ex) {
            // this is what's supposed to happen
        }
    }

    @Test
    public void testResolveInput() {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.ALLOW_CATALOG_PI, false);
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("classpath:org/xmlresolver/data/catalog.xml"));
        XMLResolver resolver = new XMLResolver(config);

        try {
            ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
            reader.parse("https://www.w3.org/2001/xml.xsd");
        } catch (Exception ex) {
            fail();
        }
    }
}
