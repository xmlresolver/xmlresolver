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
 * This class implements the {@link EntityResolver2} API.
 * <p>It's a separate class in order to avoid a compile-time dependency on the SAX
 * APIs for users of {@link XMLResolver} who don't use them. It's separate from the
 *  * implementation of {@link EntityResolver} so that the caller has control over
 *  * the API used.</p>
 */

public class SAX2Adapter implements EntityResolver2 {
    private final XMLResolver resolver;

    /**
     * Create a SAX2 adapter with the specified resolver.
     * @param resolver the resolver.
     */
    public SAX2Adapter(XMLResolver resolver) {
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

        if (resp == null) {
            return null;
        }

        // If we didn't find any resource in the catalog, and if ResolverFeature.ALWAYS_RESOLVE is true,
        // the default, then we'll be trying to return the baseURI as the external subset. That's
        // incoherent, so don't.
        if (resp.isResolved() && baseURI != null && baseURI.equals(resp.getURI().toString())) {
            return null;
        }

        ResolverInputSource source = null;
        if (resp.isResolved()) {
            source = new ResolverInputSource(resp);
            if (resp.getResolvedURI() != null) {
                source.setSystemId(resp.getResolvedURI().toString());
            }
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
            if (resp.getResolvedURI() != null) {
                source.setSystemId(resp.getResolvedURI().toString());
            }
        }

        return source;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return resolveEntity(null, publicId, null, systemId);
    }
}
