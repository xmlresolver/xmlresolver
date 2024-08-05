package org.xmlresolver.adapters;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;
import org.xmlresolver.ResolverConstants;
import org.xmlresolver.ResourceRequest;
import org.xmlresolver.ResourceResponse;
import org.xmlresolver.XMLResolver;
import org.xmlresolver.sources.ResolverInputSource;

import java.io.IOException;

/**
 * This class implements the {@link EntityResolver} API.
 * <p>It's a separate class in order to avoid a compile-time dependency on the SAX
 * APIs for users of {@link XMLResolver} who don't use them. It's separate from the
 * implementation of {@link EntityResolver2} so that the caller has control over
 * the API used.</p>
 */

public class SAX1Adapter implements EntityResolver {
    private final XMLResolver resolver;

    public SAX1Adapter(XMLResolver resolver) {
        if (resolver == null) {
            throw new NullPointerException();
        }
        this.resolver = resolver;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        ResourceRequest request = resolver.getRequest(systemId, null, ResolverConstants.EXTERNAL_ENTITY_NATURE, ResolverConstants.ANY_PURPOSE);
        request.setPublicId(publicId);
        ResourceResponse resp = resolver.resolve(request);

        ResolverInputSource source = null;
        if (resp.isResolved()) {
            source = new ResolverInputSource(resp);
            source.setSystemId(resp.getResolvedURI().toString());
        }

        return source;
    }
}
