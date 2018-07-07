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
    public static final String oasispublic = "-//OASIS//DTD Entity Resolution XML Catalog V1.0//EN";
    public static final String oasissystem = "http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd";

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

        if (oasispublic.equals(publicId) || oasissystem.equals(system.toASCIIString())) {
            URL dtd = BootstrapResolver.class.getResource("/oasis-xml-catalog.dtd");
            InputSource s = new InputSource(dtd.openStream());
            s.setSystemId(oasissystem);
            s.setPublicId(oasispublic);
            return s;
        }

        return null;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return resolveEntity(null, publicId, systemId, null);
    }
}
