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

    /**
     * Construct a resolver with the default resolver configuration.
     */
    public CatalogResolver() {
        this(new XMLResolverConfiguration());
    }

    /**
     * Construct a resolver with the specified resolver configuration.
     * @param config The resolver configuration.
     */
    public CatalogResolver(XMLResolverConfiguration config) {
        this.config = config;
        logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        resolver = new XMLResolver(config);
    }

    /**
     * Get the configuration used by this resolver.
     * @return the configuration.
     */
    public XMLResolverConfiguration getConfiguration() {
        return config;
    }

    /**
     * Resolve a URI reference.
     * @param href the URI.
     * @param baseURI the base URI.
     * @return The resolved resource, or null if no resolution was possible.
     */
    public ResolvedResource resolveURI(String href, String baseURI) {
        ResourceRequest req = resolver.getRequest(href, baseURI);
        ResourceResponse resp = resolver.resolve(req);

        if (resp.isResolved()) {
            return new ResolvedResource(resp);
        }
        return null;
    }

    /**
     * Resolve an external parsed entity.
     * @param name the entity name.
     * @param publicId the public identifier.
     * @param systemId the system identifier.
     * @param baseURI the base URI.
     * @return The resolved resource, or {@code null} if no resolution was possible.
     */
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

    /**
     * Resolve a document type declaration.
     * @param name the doctype name.
     * @param baseURI The base URI of the document containing the doctype.
     * @return The resolved resource, or {@code null} if no resolution was possible.
     */
    public ResolvedResource resolveDoctype(String name, String baseURI) {
        ResourceRequest req = resolver.getRequest(baseURI, ResolverConstants.DTD_NATURE, ResolverConstants.ANY_PURPOSE);
        req.setEntityName(name);
        ResourceResponse resp = resolver.resolve(req);
        if (resp.isResolved()) {
            return new ResolvedResource(resp);
        }
        return null;
    }

    /**
     * Resolve a namespace URI.
     * @param uri the namespace URI.
     * @param baseURI the base URI.
     * @param nature the nature of the URI.
     * @param purpose the purpose of the URI.
     * @return The resolved resource, or {@code null} if no resolution was possible.
     */
    public ResolvedResource resolveNamespace(String uri, String baseURI, String nature, String purpose) {
        ResourceRequest req = resolver.getRequest(uri, baseURI, nature, purpose);
        ResourceResponse resp = resolver.resolve(req);
        if (resp.isResolved()) {
            return new ResolvedResource(resp);
        }
        return null;
    }
}