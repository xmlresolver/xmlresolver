package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlresolver.tools.ResolvingXMLReader;
import org.xmlresolver.utils.PublicId;
import org.xmlresolver.utils.URIUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Issue0184Test {
    public static final String catalog = "src/test/resources/empty.xml";

    XMLResolverConfiguration config = null;
    XMLResolver resolver = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration(catalog);
        resolver = new XMLResolver(config);
    }

    @Test
    public void parserTest() {
        try {
            ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
            reader.getResolver().config.setFeature(ResolverFeature.FIX_WINDOWS_SYSTEM_IDENTIFIERS, true);
            String filename = "src/test/iss0184/src/SBBVT0T-Deployment-Flat-mod.xml";
            InputSource source = new InputSource(filename);
            reader.parse(source);
        } catch (IOException | SAXException ex) {
            ex.printStackTrace();
            fail();
        }
    }


}
