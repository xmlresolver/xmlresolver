package org.xmlresolver;

import org.apache.xerces.util.SAXInputSource;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.xml.sax.InputSource;
import org.xmlresolver.sources.ResolverInputSource;

import java.io.IOException;

// Credit to Adam Retter for identifying this API and pointing me in the right direction.

public class XercesResolver extends Resolver implements org.apache.xerces.xni.parser.XMLEntityResolver {

    public XercesResolver() {
        super();
    }

    public XercesResolver(XMLResolverConfiguration config) {
        super(config);
    }

    public XercesResolver(CatalogResolver resolver) {
        super(resolver);
    }

    @Override
    public XMLInputSource resolveEntity(XMLResourceIdentifier xmlResourceIdentifier) throws XNIException, IOException {
        // We're trying to get an XSD file for validation. That's how we got here.
        final String uri;
        if (xmlResourceIdentifier.getExpandedSystemId() != null) {
            uri = xmlResourceIdentifier.getExpandedSystemId();
        } else {
            uri = xmlResourceIdentifier.getNamespace();
        }

        if (uri != null) {
            ResolvedResource rsrc = resolver.resolveNamespace(uri, null, NATURE_XML_SCHEMA, PURPOSE_SCHEMA_VALIDATION);
            if (rsrc != null) {
                InputSource isource = new ResolverInputSource(rsrc.getLocalURI(), rsrc.getInputStream());
                return new SAXInputSource(isource);
            }
        }

        return null;
    }
}
