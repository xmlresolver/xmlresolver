package org.xmlresolver.adapters;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xmlresolver.*;
import org.xmlresolver.logging.ResolverLogger;
import org.xmlresolver.sources.ResolverLSInput;

/**
 * This class implements the {@link LSResourceResolver} API.
 * <p>It's a separate class in order to avoid a compile-time dependency on the DOM
 * API for users of {@link XMLResolver} who don't use it.</p>
 */

public class LSResourceAdapter implements LSResourceResolver {
    private final XMLResolver resolver;
    private final ResolverLogger logger;

    /**
     * Construct an adapter with the specified resolver.
     * @param resolver the resolver.
     */
    public LSResourceAdapter(XMLResolver resolver) {
        if (resolver == null) {
            throw new NullPointerException();
        }
        this.resolver = resolver;
        this.logger = resolver.getConfiguration().getFeature(ResolverFeature.RESOLVER_LOGGER);
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        if (type == null || "http://www.w3.org/TR/REC-xml".equals(type)) {
            return resolveDTD(type, namespaceURI, publicId, systemId, baseURI);
        } else {
            return resolveSchema(type, namespaceURI, publicId, systemId, baseURI);
        }
    }

    private LSInput resolveDTD(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        if (systemId == null) {
            return null;
        }

        final ResourceRequest request;
        logger.debug("resolveResource: XML: %s (baseURI: %s, publicId: %s)",
                systemId, baseURI, publicId);

        // This isn't DTD_NATURE because there's no name in this API
        request = resolver.getRequest(systemId, baseURI, ResolverConstants.EXTERNAL_ENTITY_NATURE, ResolverConstants.VALIDATION_PURPOSE);
        request.setPublicId(publicId);

        ResourceResponse resp = resolver.resolve(request);
        return resolveResponse(resp, publicId);
    }

    private LSInput resolveSchema(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        logger.debug("resolveResource: %s, %s (namespace: %s, baseURI: %s, publicId: %s)",
                type, systemId, namespaceURI, baseURI, publicId);

        ResourceRequest request;
        String purpose = null;
        // If it looks like it's going to be used for validation, ...
        if (ResolverConstants.NATURE_XML_SCHEMA.equals(type)
                || ResolverConstants.NATURE_XML_SCHEMA_1_1.equals(type)
                || ResolverConstants.NATURE_RELAX_NG.equals(type)) {
            purpose = ResolverConstants.PURPOSE_SCHEMA_VALIDATION;
        }

        if (systemId != null) {
            request = resolver.getRequest(systemId, baseURI, type, purpose);
            request.setPublicId(publicId);
            ResourceResponse resp = resolver.resolve(request);
            if (resp != null && resp.isResolved()) {
                return resolveResponse(resp, publicId);
            }
        }

        request = resolver.getRequest(namespaceURI, baseURI, type, purpose);
        request.setPublicId(publicId);

        ResourceResponse resp = resolver.resolve(request);
        return resolveResponse(resp, publicId);
    }

    private LSInput resolveResponse(ResourceResponse resp, String publicId) {
        LSInput input = null;
        if (resp != null && resp.isResolved()) {
            input = new ResolverLSInput(resp, publicId);
        }

        return input;
    }
}
