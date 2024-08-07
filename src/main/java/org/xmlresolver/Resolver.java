package org.xmlresolver;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;
import org.xmlresolver.logging.AbstractLogger;
import org.xmlresolver.logging.ResolverLogger;
import org.xmlresolver.sources.ResolverInputSource;
import org.xmlresolver.sources.ResolverLSInput;
import org.xmlresolver.sources.ResolverSAXSource;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * This class is a shim that provides a degree of compatibility with XML Resolver 5.0.
 * <p>Some applications create a resolver using reflection on {@code org.xmlresolver.Resolver}
 * to call the zero-argument constructor. This class is an attempt to provide that API.</p>
 * @deprecated 6.0.0
 */
public class Resolver implements URIResolver, LSResourceResolver, EntityResolver, EntityResolver2, NamespaceResolver {
    // I'm keeping these here because they're part of the public API.

    /** The schema validation purpose. */
    public static final String PURPOSE_SCHEMA_VALIDATION = ResolverConstants.PURPOSE_SCHEMA_VALIDATION;
    /** The XML Schema nature. */
    public static final String NATURE_XML_SCHEMA = ResolverConstants.NATURE_XML_SCHEMA;
    /** The XML Schema 1.1. nature. */
    public static final String NATURE_XML_SCHEMA_1_1 = ResolverConstants.NATURE_XML_SCHEMA_1_1;
    /** The RELAX NG nature. */
    public static final String NATURE_RELAX_NG = ResolverConstants.NATURE_RELAX_NG;

    private final ResolverLogger logger;
    /** The resolver configuration. */
    protected final XMLResolverConfiguration config;
    /** The resolver. */
    protected final XMLResolver resolver;
    /** The catalog resolver. */
    protected CatalogResolver catalogResolver = null;

    /**
     * Create a resolver with a default configuration.
     */
    public Resolver() {
        this(new XMLResolverConfiguration());
    }

    /**
     * Create a resolver with the specified configuration.
     * @param config the configuration.
     */
    public Resolver(XMLResolverConfiguration config) {
        this.config = config;
        logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        resolver = new XMLResolver(config);
    }

    /**
     * Create a resoluver using the specified underlying catalog resolver.
     * @param resolver the catalog resolver.
     */
    public Resolver(CatalogResolver resolver) {
        config = resolver.getConfiguration();
        logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        this.resolver = new XMLResolver(config);
    }

    /**
     * Get the resolver version.
     * @return the version.
     */
    public static String version() {
        return BuildConfig.VERSION;
    }

    /**
     * Get the resolver configuration.
     * @return the configuration.
     */
    public XMLResolverConfiguration getConfiguration() {
        return config;
    }

    /**
     * Get the resolver's underlying catalog resolver.
     * @return the catalog resolver.
     */
    public CatalogResolver getCatalogResolver() {
        if (catalogResolver == null) {
            catalogResolver = new CatalogResolver(config);
        }
        return catalogResolver;
    }

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        ResourceRequest req = resolver.getRequest(href, base);
        return resolverAdapter(req);
    }

    @Override
    public Source resolveNamespace(String uri, String nature, String purpose) throws TransformerException {
        ResourceRequest req = resolver.getRequest(uri, nature, purpose);
        return resolverAdapter(req);
    }

    private Source resolverAdapter(ResourceRequest req) throws TransformerException {
        ResourceResponse resp = resolver.resolve(req);

        if (!resp.isResolved()) {
            if (!config.getFeature(ResolverFeature.ALWAYS_RESOLVE)) {
                return null;
            }
            try {
                resp = ResourceAccess.getResource(req);
            } catch (URISyntaxException | IOException | IllegalArgumentException ex) {
                if (resolver.getConfiguration().getFeature(ResolverFeature.THROW_URI_EXCEPTIONS)) {
                    throw new TransformerException(ex);
                }
                return null;
            }
        }

        if (resp.isResolved()) {
            ResolverSAXSource source = new ResolverSAXSource(resp);
            source.setSystemId(resp.getResolvedURI().toString());
            return source;
        }

        return null;
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        ResourceRequest req = null;
        if (type == null || "http://www.w3.org/TR/REC-xml".equals(type)) {
            logger.log(AbstractLogger.REQUEST, "resolveResource: XML: %s (baseURI: %s, publicId: %s)",
                    systemId, baseURI, publicId);
            req = resolver.getRequest(systemId, baseURI, ResolverConstants.EXTERNAL_ENTITY_NATURE, ResolverConstants.ANY_PURPOSE);
            req.setPublicId(publicId);
        } else {
            logger.log(AbstractLogger.REQUEST, "resolveResource: %s, %s (namespace: %s, baseURI: %s, publicId: %s)",
                    type, systemId, namespaceURI, baseURI, publicId);

            String purpose = null;
            // If it looks like it's going to be used for validation, ...
            if (NATURE_XML_SCHEMA.equals(type)
                    || NATURE_XML_SCHEMA_1_1.equals(type)
                    || NATURE_RELAX_NG.equals(type)) {
                purpose = PURPOSE_SCHEMA_VALIDATION;
            }

            if (systemId != null) {
                req = resolver.getRequest(systemId, baseURI, type, purpose);
            }
        }

        if (req != null) {
            ResourceResponse resp = resolver.resolve(req);
            if (resp.isResolved()) {
                return new ResolverLSInput(resp, publicId);
            }
        }

        return null;
    }

    @Override
    public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
        ResourceRequest req = resolver.getRequest(baseURI, null, ResolverConstants.ANY_NATURE, ResolverConstants.ANY_PURPOSE);
        req.setEntityName(name);
        ResourceResponse resp = resolver.resolve(req);
        if (resp.isResolved()) {
            ResolverInputSource source = new ResolverInputSource(resp);
            source.setSystemId(resp.getResolvedURI().toString());
            return source;
        }

        return null;
    }

    @Override
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
        ResourceRequest req = resolver.getRequest(systemId, baseURI, ResolverConstants.EXTERNAL_ENTITY_NATURE, ResolverConstants.ANY_PURPOSE);
        req.setEntityName(name);
        req.setPublicId(publicId);
        ResourceResponse resp = resolver.resolve(req);
        if (resp.isResolved()) {
            ResolverInputSource source = new ResolverInputSource(resp);
            source.setSystemId(resp.getResolvedURI().toString());
            return source;
        }

        return null;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return resolveEntity(null, publicId, null, systemId);
    }
}
