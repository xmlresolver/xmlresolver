package org.xmlresolver;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlresolver.adapters.SAXAdapter;
import org.xmlresolver.adapters.LSResourceAdapter;
import org.xmlresolver.adapters.XercesXniAdapter;
import org.xmlresolver.adapters.XmlStreamAdapter;
import org.xmlresolver.logging.AbstractLogger;
import org.xmlresolver.logging.ResolverLogger;
import org.xmlresolver.sources.ResolverSAXSource;
import org.xmlresolver.utils.URIUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Stack;

/**
 * The XML Resolver.
 * <p>This class is the main entry point for the XML Resolver APIs. It provides methods to construct
 * resolver APIs for many parsers as well as methods to lookup catalog entries
 * or resolve resources through the catalog directly.</p>
 * <p>Use the {@link XMLResolverConfiguration} to control the configuration of the resolver.</p>
 */
public class XMLResolver {
    protected final ResolverConfiguration config;
    protected final ResolverLogger logger;

    /**
     * Construct a new resolver with a default configuration.
     */
    public XMLResolver() {
        this(new XMLResolverConfiguration());
    }

    /**
     * Construct a new resolver with the specified configuration.
     * @param config the configuration
     */
    public XMLResolver(ResolverConfiguration config) {
        this.config = config;
        logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
    }

    /**
     * Get the configuration associated with this resolver.
     * @return the configuration
     */
    public ResolverConfiguration getConfiguration() {
        return config;
    }

    /**
     * Construct a resource request for the given URI.
     * <p>This request will use the specified URI without a base URI and with a RDDL nature of "any"
     * and a RDDL purpose of "any".
     * @param uri The URI.
     * @return The request.
     */
    public ResourceRequest getRequest(String uri) {
        return getRequest(uri, null, ResolverConstants.ANY_NATURE, ResolverConstants.ANY_PURPOSE);
    }

    /**
     * Construct a resource request for the given URI and base URI.
     * <p>This request will use the specified URIs with a RDDL nature of "any"
     * and a RDDL purpose of "any".
     * @param uri The URI.
     * @param baseUri The base URI.
     * @return The request.
     */
    public ResourceRequest getRequest(String uri, String baseUri) {
        return getRequest(uri, baseUri, ResolverConstants.ANY_NATURE, ResolverConstants.ANY_PURPOSE);
    }

    /**
     * Construct a resource request for the given URI, nature, and purpose.
     * <p>This request will use the specified URI without a base URI using the RDDL nature and purpose
     * specified. See {@link ResolverConstants} for a list of natures and purposes.
     * @param uri The URI.
     * @param nature The RDDL nature.
     * @param purpose The RDDL purpose.
     * @return The request.
     */
    public ResourceRequest getRequest(String uri, String nature, String purpose) {
        return getRequest(uri, null, nature, purpose);
    }


    /**
     * Construct a resource request for the given URI, base URI, nature, and purpose.
     * <p>This request will use the specified URIs and the RDDL nature and purpose
     * specified. See {@link ResolverConstants} for a list of natures and purposes.
     * @param uri The URI.
     * @param baseUri The base URI.
     * @param nature The RDDL nature.
     * @param purpose The RDDL purpose.
     * @return The request.
     */
    public ResourceRequest getRequest(String uri, String baseUri, String nature, String purpose) {
        ResourceRequest request = new ResourceRequest(config, nature, purpose);
        request.setURI(uri);
        request.setBaseURI(baseUri);
        return request;
    }

    /**
     * Lookup an entity in the catalog.
     * <p>This method searches for the specified entity in the catalog.</p>
     * <p>This method does not attempt to
     * resolve the resource beyond finding it in the catalog.</p>
     * <p>Either the system or public identifier may be null, but at least one of them should have a value.</p>
     * @param publicId The system identifier.
     * @param systemId The public identifier.
     * @return The response.
     */
    public ResourceResponse lookupEntity(String publicId, String systemId) {
        return lookupEntity(publicId, systemId, null);
    }

    /**
     * Lookup an entity in the catalog.
     * <p>This method searches for the specified entity in the catalog.</p>
     * <p>This method does not attempt to
     * resolve the resource beyond finding it in the catalog.</p>
     * <p>Either the system or public identifier may be null, but at least one of them should have a value.</p>
     * <p>If lookup fails for the system identifier, it will be made absolute with respect to the base URI (if
     * possible) and the search will be repeated for the new URI.</p>
     * @param publicId The system identifier.
     * @param systemId The public identifier.
     * @param baseUri The base URI.
     * @return The response.
     */
    public ResourceResponse lookupEntity(String publicId, String systemId, String baseUri) {
        ResourceRequest request = getRequest(systemId, ResolverConstants.EXTERNAL_ENTITY_NATURE, ResolverConstants.ANY_PURPOSE);
        request.setURI(systemId);
        request.setBaseURI(baseUri);
        request.setPublicId(publicId);
        return lookup(request);
    }

    /**
     * Lookup a document type in the catalog.
     * <p>This method searches for the specified doctype in the catalog.</p>
     * <p>This method does not attempt to resolve the resource beyond finding it in the catalog.</p>
     * @param name The doctype name.
     * @return The response.
     */
    public ResourceResponse lookupDoctype(String name) {
        return lookupDoctype(name, null, null, null);
    }

    /**
     * Lookup a document type in the catalog.
     * <p>This method searches for the specified doctype in the catalog.</p>
     * <p>This method does not attempt to resolve the resource beyond finding it in the catalog.</p>
     * @param name The doctype name.
     * @param publicId The public identifier.
     * @param systemId The system identifier.
     * @return The response.
     */
    public ResourceResponse lookupDoctype(String name, String publicId, String systemId) {
        return lookupDoctype(name, null, null, null);
    }

    /**
     * Lookup a document type in the catalog.
     * <p>This method searches for the specified doctype in the catalog.</p>
     * <p>This method does not attempt to resolve the resource beyond finding it in the catalog.</p>
     * <p>Either the system or public identifier may be null, but at least one of them should have a value.</p>
     * <p>If lookup fails for the system identifier, it will be made absolute with respect to the base URI (if
     * possible) and the search will be repeated for the new URI.</p>
     * @param name The doctype name.
     * @param publicId The public identifier.
     * @param systemId The system identifier.
     * @param baseUri the base URI.
     * @return The response.
     */
    public ResourceResponse lookupDoctype(String name, String publicId, String systemId, String baseUri) {
        ResourceRequest request = getRequest(systemId, ResolverConstants.DTD_NATURE, ResolverConstants.ANY_PURPOSE);
        request.setEntityName(name);
        request.setPublicId(publicId);
        request.setBaseURI(baseUri);
        return lookup(request);
    }

    /**
     * Lookup a uri in the catalog.
     * <p>This method searches for the specified URI in the catalog using any RDDL nature or purpose.</p>
     * <p>This method does not attempt to resolve the resource beyond finding it in the catalog.</p>
     * @param href The URI.
     * @return The response.
     */
    public ResourceResponse lookupUri(String href) {
        return lookupUri(href, null);
    }

    /**
     * Lookup a URI in the catalog.
     * <p>This method searches for the specified URI in the catalog using any RDDL nature or purpose.</p>
     * <p>This method does not attempt to resolve the resource beyond finding it in the catalog.</p>
     * <p>If lookup fails for the {@code href}, it will be made absolute with respect to the base URI (if
     * possible) and the search will be repeated for the new URI.</p>
     * @param href The URI.
     * @param baseUri the base URI.
     * @return The response.
     */
    public ResourceResponse lookupUri(String href, String baseUri) {
        ResourceRequest request = getRequest(href, ResolverConstants.ANY_NATURE, ResolverConstants.ANY_PURPOSE);
        request.setURI(href);
        request.setBaseURI(baseUri);
        return lookup(request);
    }

    /**
     * Lookup a URI in the catalog.
     * <p>This method searches for the specified URI in the catalog using the specified RDDL nature and purpose.</p>
     * <p>This method does not attempt to resolve the resource beyond finding it in the catalog.</p>
     * @param href The URI.
     * @param nature the RDDL nature.
     * @param purpose the RDDL purpose.
     * @return The response.
     */
    public ResourceResponse lookupNamespace(String href, String nature, String purpose) {
        ResourceRequest request = getRequest(href, nature, purpose);
        request.setURI(href);
        return lookup(request);
    }

    /**
     * Lookup a resource request in the catalog.
     * <p>This method searches for the resource identified by the request.</p>
     * <p>This method does not attempt to resolve the resource beyond finding it in the catalog.</p>
     * @param request The request.
     * @return The response.
     */
    public ResourceResponse lookup(ResourceRequest request) {
        CatalogManager manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);

        if (ResolverConstants.DTD_NATURE.equals(request.nature)) {
            return lookupDtd(request, manager);
        }

        if (ResolverConstants.EXTERNAL_ENTITY_NATURE.equals(request.nature)) {
            return lookupEntity(request, manager);
        }

        ResourceResponse response = lookupUri(request, manager);

        if (response.isResolved()) {
            return response;
        }

        if (request.nature == null) { // == ResolverConstants.ANY_NATURE :-(
            // What about an entity?
            response = lookupEntity(request, manager);
            if (!response.isResolved() && request.getEntityName() != null) {
                // What about a DTD then?
                response = lookupDtd(request, manager);
            }
        }

        return response;
    }

    private ResourceResponse lookupDtd(ResourceRequest request, CatalogManager manager) {
        String name = request.getEntityName();
        String publicId = request.getPublicId();
        String systemId = request.getSystemId();
        String baseUri = request.getBaseURI();

        if (name == null) {
            throw new NullPointerException("Name must not be null for DTD lookup");
        }

        URI found = manager.lookupDoctype(name, systemId, publicId);
        if (found == null && baseUri != null) {
            URI absuri = makeAbsolute(request);
            if (absuri != null) {
                found = manager.lookupDoctype(name, absuri.toString(), publicId);
            }
        }

        return new ResourceResponse(request, found);
    }

    private ResourceResponse lookupEntity(ResourceRequest request, CatalogManager manager) {
        String name = request.getEntityName();
        String publicId = request.getPublicId();
        String systemId = request.getSystemId();
        String baseUri = request.getBaseURI();

        String allowed = config.getFeature(ResolverFeature.ACCESS_EXTERNAL_ENTITY);

        if (name == null && publicId == null && systemId == null && baseUri == null) {
            logger.log(AbstractLogger.REQUEST, "lookupEntity: null");
            return new ResourceResponse(request);
        }

        URI systemIdURI = makeUri(systemId);
        if (systemIdURI != null) {
            if (systemIdURI.isAbsolute()) {
                if (URIUtils.forbidAccess(allowed, systemId, config.getFeature(ResolverFeature.MERGE_HTTPS))) {
                    logger.log(AbstractLogger.REQUEST, "lookupEntity (access denied): %s", systemIdURI.toString());
                    return new ResourceResponse(request, true);
                }
            }
        }

        logger.log(AbstractLogger.REQUEST, "lookupEntity: %s%s (baseURI: %s, publicId: %s)", (name == null ? "" : name + " "), systemId, baseUri, publicId);

        URI resolved = null;
        resolved = manager.lookupEntity(name, systemId, publicId);
        if (resolved == null && systemId != null && config.getFeature(ResolverFeature.URI_FOR_SYSTEM)) {
            resolved = manager.lookupURI(systemId);
        }

        if (resolved != null) {
            return new ResourceResponse(request, resolved);
        }

        URI absSystem = makeAbsolute(request);
        if (absSystem != null) {
            if (URIUtils.forbidAccess(allowed, absSystem.toString(), config.getFeature(ResolverFeature.MERGE_HTTPS))) {
                logger.log(AbstractLogger.REQUEST, "lookupEntity: (access denied): null");
                return new ResourceResponse(request, true);
            }

            resolved = manager.lookupEntity(name, absSystem.toString(), publicId);
            if (resolved == null && config.getFeature(ResolverFeature.URI_FOR_SYSTEM)) {
                resolved = manager.lookupURI(absSystem.toString());
            }
        }

        if (resolved == null) {
            if (config.getFeature(ResolverFeature.ALWAYS_RESOLVE)) {
                if (absSystem == null) {
                    return new ResourceResponse(request);
                } else {
                    return new ResourceResponse(request, absSystem);
                }
            }
            return new ResourceResponse(request);
        }

        return new ResourceResponse(request, resolved);
    }

    private ResourceResponse lookupUri(ResourceRequest request, CatalogManager manager) {
        String systemId = request.getSystemId();
        String baseUri = request.getBaseURI();

        String allowed = config.getFeature(ResolverFeature.ACCESS_EXTERNAL_DOCUMENT);

        if (systemId == null && baseUri == null) {
            logger.log(AbstractLogger.REQUEST, "lookupUri: null");
            return new ResourceResponse(request);
        }

        if (systemId == null) {
            systemId = baseUri;
        }

        URI systemIdURI = makeUri(systemId);
        if (systemIdURI != null) {
            if (systemIdURI.isAbsolute()) {
                if (URIUtils.forbidAccess(allowed, systemId, config.getFeature(ResolverFeature.MERGE_HTTPS))) {
                    logger.log(AbstractLogger.REQUEST, "lookupUri (access denied): null");
                    return new ResourceResponse(request, true);
                }
            }
        }

        logger.log(AbstractLogger.REQUEST, "lookupUri: %s (baseURI: %s)", systemId, baseUri);

        URI resolved = manager.lookupNamespaceURI(systemId, request.nature, request.purpose);

        if (resolved != null) {
            return new ResourceResponse(request, resolved);
        }

        URI absSystem = makeAbsolute(request);
        if (absSystem != null) {
            if (URIUtils.forbidAccess(allowed, absSystem.toString(), config.getFeature(ResolverFeature.MERGE_HTTPS))) {
                logger.log(AbstractLogger.REQUEST, "lookupUri (access denied): null");
                return new ResourceResponse(request, true);
            }

            resolved = manager.lookupNamespaceURI(absSystem.toString(), request.nature, request.purpose);
        }

        if (resolved == null) {
            if (config.getFeature(ResolverFeature.ALWAYS_RESOLVE)) {
                if (absSystem == null) {
                    return new ResourceResponse(request);
                } else {
                    return new ResourceResponse(request, absSystem);
                }
            }
            return new ResourceResponse(request);
        }

        return new ResourceResponse(request, resolved);
    }

    /**
     * Attempts to resolve a request.
     * <p>This method searches for the resource identified by the request in the catalog.</p>
     * <p>If a matching
     * catalog entry is found, the URI provided by that entry is taken as the URI to use for resolution.
     * The resolver will then attempt to access that resource and return a stream that can be used to read it.
     * Any additional details provided by the response (content type, headers, etc.) are also made available.</p>
     * <p>If a matching entry <em>is not</em> found, the behavior depends on the
     * {@link ResolverFeature#ALWAYS_RESOLVE} feature setting. If it's true, the resolver will attempt to access
     * the original URI. Otherwise, it returns a response that indicates that resolution was unsuccessful.</p>
     * @param request The request.
     * @return The response.
     */
    public ResourceResponse resolve(ResourceRequest request) {
        ResourceResponse lookup = lookup(request);
        if (lookup.isRejected()) {
            return lookup;
        }

        boolean tryRddl = config.getFeature(ResolverFeature.PARSE_RDDL)
                && lookup.request.nature != null && lookup.request.purpose != null;

        try {
            if (tryRddl) {
                if (lookup.isResolved()) {
                    lookup = rddlLookup(lookup);
                } else {
                    if (lookup.request.getAbsoluteURI() != null) {
                        lookup = rddlLookup(lookup, lookup.request.getAbsoluteURI());
                    }
                }
            }

            if (!lookup.isResolved()) {
                if (config.getFeature(ResolverFeature.ALWAYS_RESOLVE)) {
                    return ResourceAccess.getResource(lookup);
                } else {
                    return lookup;
                }
            }

            return ResourceAccess.getResource(lookup);
        } catch (URISyntaxException | IOException ex) {
            boolean throwExceptions = config.getFeature(ResolverFeature.THROW_URI_EXCEPTIONS);
            if (throwExceptions) {
                logger.log(AbstractLogger.ERROR, ex.getMessage());
                throw new IllegalArgumentException(ex);
            }
            return lookup;
        }
    }

    private ResourceResponse rddlLookup(ResourceResponse lookup) {
        return rddlLookup(lookup, lookup.getResolvedURI());
    }

    private ResourceResponse rddlLookup(ResourceResponse lookup, URI resolved) {
        String nature = lookup.request.nature;
        String purpose = lookup.request.purpose;

        URI rddl = checkRddl(resolved, nature, purpose);
        if (rddl == null) {
            return lookup;
        }

        ResourceRequest rddlRequest = new ResourceRequest(config, ResolverConstants.ANY_NATURE, ResolverConstants.ANY_PURPOSE);
        rddlRequest.setURI(rddl.toString());
        rddlRequest.setBaseURI(resolved.toString());

        ResourceResponse resp = lookup(rddlRequest);
        if (!resp.isResolved()) {
            logger.log(AbstractLogger.RESPONSE, "RDDL %s: %s", resolved, rddl);
            return new ResourceResponse(lookup.request, rddl);
        }
        logger.log(AbstractLogger.RESPONSE, "RDDL %s: %s", resolved, resp.getURI());

        return resp;
    }

    private URI checkRddl(URI uri, String nature, String purpose) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            spf.setValidating(false);
            spf.setXIncludeAware(false);

            ResourceRequest req = new ResourceRequest(config, nature, purpose);
            req.setURI(uri.toString());

            ResourceResponse rsrc = ResourceAccess.getResource(req);
            String contentType = rsrc.getContentType();

            if (contentType != null
                    && (contentType.startsWith("text/html")
                    || contentType.startsWith("application/html+xml")
                    || contentType.startsWith("application/xhtml+xml"))) {
                InputSource source = new InputSource(rsrc.getInputStream());
                RddlQuery handler = new RddlQuery(rsrc.getURI(), nature, purpose);
                SAXParser parser = spf.newSAXParser();
                parser.parse(source, handler);
                return handler.href();
            } else {
                return null;
            }
        } catch (ParserConfigurationException | SAXException | IOException | IllegalArgumentException | URISyntaxException ex) {
            logger.log(AbstractLogger.ERROR, "RDDL parse failed: %s: %s",
                    uri, ex.getMessage());
            return null;
        }
    }

    /**
     * Get a {@link javax.xml.transform.URIResolver}.
     * @return A URI resolver.
     */
    public URIResolver getURIResolver() {
        return (href, base) -> {
            ResourceRequest request = getRequest(href, base);
            ResourceResponse resp = resolve(request);

            Source source = null;
            if (resp.isResolved()) {
                source = new ResolverSAXSource(resp);
                source.setSystemId(resp.getURI().toString());
            }

            return source;
        };
    }

    /**
     * Get a {@link SAXAdapter}.
     * @return An entity resolver.
     */
    public SAXAdapter getEntityResolver() {
        return getEntityResolver2();
    }

    /**
     * Get a {@link SAXAdapter}.
     * @return An entity resolver.
     */
    public SAXAdapter getEntityResolver2() {
        return new SAXAdapter(this);
    }

    /**
     * Get a {@link LSResourceAdapter}.
     * @return a resource resolver.
     */
    public LSResourceAdapter getLSResourceResolver() {
        return new LSResourceAdapter(this);
    }

    /**
     * Get an {@link XmlStreamAdapter}.
     * @return an XML resolver.
     */
    public javax.xml.stream.XMLResolver getXMLResolver() {
        return new XmlStreamAdapter(this);
    }

    /**
     * Get an {@link XercesXniAdapter}.
     * @return an entity resolver.
     */
    public XercesXniAdapter getXMLEntityResolver() {
        return new XercesXniAdapter(this);
    }

    private URI makeUri(String uri) {
        if (uri != null) {
            boolean throwExceptions = config.getFeature(ResolverFeature.THROW_URI_EXCEPTIONS);

            try {
                return new URI(uri);
            } catch (URISyntaxException ex) {
                logger.log(AbstractLogger.ERROR, "URI exception: %s", uri);
                if (throwExceptions) {
                    throw new IllegalArgumentException(ex);
                }
            }
        }

        return null;
    }

    private URI makeAbsolute(ResourceRequest request) {
        boolean throwExceptions = config.getFeature(ResolverFeature.THROW_URI_EXCEPTIONS);

        try {
            URI absuri = request.getAbsoluteURI();
            if (absuri != null && !absuri.toString().equals(request.getURI())) {
                return absuri;
            }
        } catch (URISyntaxException ex) {
            logger.log(AbstractLogger.ERROR, "URI exception: %s", ex.getMessage());
            if (throwExceptions) {
                throw new IllegalArgumentException(ex);
            }
        }

        return null;
    }

    private static class RddlQuery extends DefaultHandler {
        private final String nature;
        private final String purpose;
        private URI found = null;
        private final Stack<URI> baseUriStack = new Stack<>();

        public RddlQuery(URI baseURI, String nature, String purpose) {
            this.nature = nature;
            this.purpose = purpose;
            baseUriStack.push(baseURI);
        }

        public URI href() {
            return found;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (ResolverConstants.HTML_NS.equals(uri) && attributes.getValue("", "base") != null) {
                baseUriStack.push(baseUriStack.peek().resolve(attributes.getValue("", "base")));
            } else if (attributes.getValue(ResolverConstants.XML_NS, "base") != null) {
                baseUriStack.push(baseUriStack.peek().resolve(attributes.getValue(ResolverConstants.XML_NS, "base")));
            } else {
                baseUriStack.push(baseUriStack.peek());
            }

            if (found == null && ResolverConstants.RDDL_NS.equals(uri) && "resource".equals(localName)) {
                String rnature = attributes.getValue(ResolverConstants.XLINK_NS, "role");
                String rpurpose = attributes.getValue(ResolverConstants.XLINK_NS, "arcrole");
                String href = attributes.getValue(ResolverConstants.XLINK_NS, "href");
                if (nature.equals(rnature) && purpose.equals(rpurpose) && href != null) {
                    found = baseUriStack.peek().resolve(href);
                }
            }
        }

        @Override
        public void endElement (String uri, String localName, String qName) throws SAXException {
            baseUriStack.pop();
        }
    }
}
