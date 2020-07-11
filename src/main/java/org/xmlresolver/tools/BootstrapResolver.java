package org.xmlresolver.tools;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by ndw on 5/27/15.
 */
public class BootstrapResolver implements EntityResolver, EntityResolver2 {
    // version in the file retrieved is "1.10", not "1.0"
    public static final String oasispublic10 = "-//OASIS//DTD Entity Resolution XML Catalog V1.0//EN";
    public static final String oasissystem10 = "http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd";

    // version in the file retrieved is "1.14", not "1.1"
    public static final String oasispublic11 = "-//OASIS//DTD XML Catalogs V1.1//EN";
    public static final String oasissystem11 = "http://www.oasis-open.org/committees/entity/release/1.1/catalog.dtd";

    @Override
    public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
        return null;
    }

    @Override
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
        URI system = URI.create("http://example.com/");

        try {
            if (baseURI != null) {
                system = new URI(baseURI);
                if (systemId != null) {
                    system = system.resolve(systemId);
                }
            }
        } catch (URISyntaxException e) {
            // nop
        }

        if (oasispublic10.equals(publicId) || oasissystem10.equals(system.toASCIIString())) {
            URL dtd = BootstrapResolver.class.getResource("/oasis-xml-catalog-1.0.dtd");
            InputSource s = new InputSource(dtd.openStream());
            s.setSystemId(oasissystem10);
            s.setPublicId(oasispublic10);
            return s;
        }

        if (oasispublic11.equals(publicId) || oasissystem11.equals(system.toASCIIString())) {
            URL dtd = BootstrapResolver.class.getResource("/oasis-xml-catalog-1.1.dtd");
            InputSource s = new InputSource(dtd.openStream());
            s.setSystemId(oasissystem11);
            s.setPublicId(oasispublic11);
            return s;
        }

        return null;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return resolveEntity(null, publicId, systemId, null);
    }
}
