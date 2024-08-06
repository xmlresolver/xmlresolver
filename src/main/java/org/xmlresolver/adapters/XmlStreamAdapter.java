package org.xmlresolver.adapters;

import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.xmlresolver.ResolverConstants;
import org.xmlresolver.ResourceRequest;
import org.xmlresolver.ResourceResponse;
import org.xmlresolver.XMLResolver;

import javax.xml.stream.XMLStreamException;

/**
 * This class implements the {@link javax.xml.stream.XMLResolver} API.
 * <p>It's a separate class in order to avoid a compile-time dependency on the StAX
 * API for users of {@link org.xmlresolver.XMLResolver} who don't use it.</p>
 */

public class XmlStreamAdapter implements javax.xml.stream.XMLResolver {
    private final org.xmlresolver.XMLResolver resolver;

    /**
     * Create a stream adapter with the specified configuration.
     * @param resolver the configuration.
     */
    public XmlStreamAdapter(org.xmlresolver.XMLResolver resolver) {
        if (resolver == null) {
            throw new NullPointerException();
        }
        this.resolver = resolver;
    }

    @Override
    public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException {
        ResourceRequest request = resolver.getRequest(systemID, baseURI, ResolverConstants.EXTERNAL_ENTITY_NATURE, ResolverConstants.ANY_PURPOSE);
        request.setPublicId(publicID);
        ResourceResponse resp = resolver.resolve(request);
        return resp.getInputStream();
    }
}
