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
 * This class implements the {@link EntityResolver} and {@link EntityResolver2} APIs.
 * <p>It's a separate class in order to avoid a compile-time dependency on the SAX
 * APIs for users of {@link XMLResolver} who don't use them.</p>
 *
 * <p>In version 6.0.6, the SAXAdapter was divided into two classes, SAX1Adapter and SAX2Adapter
 * so that creating an {@code EntityResolver} would not also, automatically create an
 * {@code EntityResolver2}. This class is not used, it exists just for backwards compatibility.</p>
 *
 * @deprecated 6.0.6
 * @see SAX1Adapter
 * @see SAX2Adapter
 */

public class SAXAdapter implements EntityResolver, EntityResolver2 {
    private final XMLResolver resolver;

    public SAXAdapter(XMLResolver resolver) {
        if (resolver == null) {
            throw new NullPointerException();
        }
        this.resolver = resolver;
    }

    @Override
    public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
        ResourceRequest request = resolver.getRequest(null, baseURI, ResolverConstants.DTD_NATURE, ResolverConstants.ANY_PURPOSE);
        request.setEntityName(name);
        ResourceResponse resp = resolver.resolve(request);

        ResolverInputSource source = null;
        if (resp.isResolved()) {
            source = new ResolverInputSource(resp);
            source.setSystemId(resp.getResolvedURI().toString());
        }

        return source;
    }

    @Override
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
        ResourceRequest request = resolver.getRequest(systemId, baseURI, ResolverConstants.EXTERNAL_ENTITY_NATURE, ResolverConstants.ANY_PURPOSE);
        request.setEntityName(name);
        request.setPublicId(publicId);
        ResourceResponse resp = resolver.resolve(request);

        ResolverInputSource source = null;
        if (resp.isResolved()) {
            source = new ResolverInputSource(resp);
            source.setSystemId(resp.getResolvedURI().toString());
        }

        return source;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return resolveEntity(null, publicId, null, systemId);
    }
}
