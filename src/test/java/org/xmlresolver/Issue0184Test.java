package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlresolver.tools.ResolvingXMLReader;
import org.xmlresolver.utils.URIUtils;

import java.io.IOException;

import static org.junit.Assert.fail;

public class Issue0184Test {
    public static final String catalog = "src/test/resources/empty.xml";

    XMLResolverConfiguration config = null;
    Resolver resolver = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration(catalog);
        resolver = new Resolver(config);
    }

    @Test
    public void parserTest() {
        /*
         * This test passes on Windows but cannot pass on Mac/Linux systems.
         * Xerces doesn't resolve the system identifier against the base URI
         * because it doesn't think it's a relative path. The resolver can
         * sort out the slashes, but there's no catalog entry so it returns
         * null and Xerces fails with "URI has no protocol". Setting ALWAYS_RESOLVE
         * to true doesn't help because without the base URI, the resolver
         * can't open it either.
         */

        if (!URIUtils.isWindows()) {
            return;
        }

        try {
            ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
            // This test requires this feature!
            reader.getResolver().getConfiguration().setFeature(ResolverFeature.FIX_WINDOWS_SYSTEM_IDENTIFIERS, true);
            reader.getResolver().getConfiguration().setFeature(ResolverFeature.ALWAYS_RESOLVE, true);
            String filename = "src/test/iss0184/src/SBBVT0T-Deployment-Flat-mod.xml";
            InputSource source = new InputSource(filename);
            String cwd = URIUtils.cwd().getPath();
            if (!cwd.endsWith("/")) {
                cwd = cwd + "/";
            }
            source.setSystemId(cwd + filename);
            reader.parse(source);
        } catch (IOException | SAXException ex) {
            fail();
        }
    }


}
