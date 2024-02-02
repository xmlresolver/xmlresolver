package org.xmlresolver;

import org.xmlresolver.logging.ResolverLogger;

/**
 * This class is a shim that provides a degree of compatibility with XML Resolver 5.0.
 * @deprecated 6.0.0
 */
public class CatalogResolver {
    private final ResolverLogger logger;
    private final XMLResolverConfiguration config;
    private final XMLResolver resolver;

    public CatalogResolver() {
        this(new XMLResolverConfiguration());
    }

    public CatalogResolver(XMLResolverConfiguration config) {
        this.config = config;
        logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        resolver = new XMLResolver(config);
    }

    public XMLResolverConfiguration getConfiguration() {
        return config;
    }

    public ResolvedResource resolveURI(String href, String baseURI) {
        System.err.println("CR resolveURI " + href);
        ResourceRequest req = resolver.getRequest(href, baseURI);
        ResourceResponse resp = resolver.resolve(req);
        System.err.println("RR: " + resp.getURI());

        if (resp.isResolved()) {
            return new ResolvedResource(resp);
        }
        return null;
    }

    public ResolvedResource resolveEntity(String name, String publicId, String systemId, String baseURI) {
        ResourceRequest req = resolver.getRequest(systemId, baseURI);
        req.setEntityName(name);
        req.setPublicId(publicId);
        ResourceResponse resp = resolver.resolve(req);
        if (resp.isResolved()) {
            return new ResolvedResource(resp);
        }
        return null;
    }

    public ResolvedResource resolveDoctype(String name, String baseURI) {
        ResourceRequest req = resolver.getRequest(baseURI, ResolverConstants.DTD_NATURE, ResolverConstants.ANY_PURPOSE);
        req.setEntityName(name);
        ResourceResponse resp = resolver.resolve(req);
        if (resp.isResolved()) {
            return new ResolvedResource(resp);
        }
        return null;
    }

    public ResolvedResource resolveNamespace(String uri, String baseURI, String nature, String purpose) {
        ResourceRequest req = resolver.getRequest(uri, baseURI, nature, purpose);
        ResourceResponse resp = resolver.resolve(req);
        if (resp.isResolved()) {
            return new ResolvedResource(resp);
        }
        return null;
    }
}