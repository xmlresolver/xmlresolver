/*
 * ResolvingXMLReaderTest.java
 * JUnit based test
 *
 * Created on January 11, 2007, 9:57 AM
 */

package org.xmlresolver.tools;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlresolver.Resolver;
import org.xmlresolver.ResolverFeature;
import org.xmlresolver.XMLResolverConfiguration;

import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.Collections;

import static junit.framework.TestCase.fail;

/**
 *
 * @author ndw
 */
public class ResolvingXMLReaderTest {

    @Test
    public void testReader() throws IOException, SAXException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(true);

        try {
            ResolvingXMLReader reader = new ResolvingXMLReader(spf);
            reader.parse("src/test/resources/documents/pitest.xml");
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void testForbiddenPI() throws IOException, SAXException {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.ALLOW_CATALOG_PI, false);
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("dummy.cat"));
        Resolver resolver = new Resolver(config);

        try {
            ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
            reader.parse("src/test/resources/documents/pitest.xml");
            fail();
        } catch (Exception ex) {
            // this is what's supposed to happen
        }
    }
}
