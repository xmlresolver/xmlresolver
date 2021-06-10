package org.xmlresolver;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlresolver.cache.CacheEntry;
import org.xmlresolver.cache.ResourceCache;
import org.xmlresolver.utils.URIUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.Stack;

public class ResourceResolverImpl implements ResourceResolver {
    private static final ResolverLogger logger = new ResolverLogger(ResourceResolverImpl.class);
    private XMLResolverConfiguration config = null;
    private ResourceCache cache = null;

    /**
     * Creates a new instance of ResourceResolver using a default configuration.
     */
    public ResourceResolverImpl() {
        this(new XMLResolverConfiguration());
    }

    /**
     * Creates a new instance of ResourceResolver with the specified resolver configuration.
     *
     * @param conf The {@link XMLResolverConfiguration} to use.
     * @throws NullPointerException if the configuration is null.
     */
    public ResourceResolverImpl(XMLResolverConfiguration conf) {
        config = conf;
        cache = conf.getFeature(ResolverFeature.CACHE);
    }

    /** Returns the {@link XMLResolverConfiguration} used by this ResourceResolver.
     *
     * @return The configuration.
     */
    public XMLResolverConfiguration getConfiguration() {
        return config;
    }

    private ResolvedResourceImpl resource(String requestURI, URI responseURI, CacheEntry cached) {
        try {
            if (cached == null) {
                return new ResolvedResourceImpl(config, URIUtils.newURI(requestURI), responseURI);
            } else {
                FileInputStream fs = new FileInputStream(cached.file);
                URI localURI = cached.file.toURI();
                boolean mask = config.getFeature(ResolverFeature.MASK_JAR_URIS);
                if (mask && ("jar".equals(localURI.getScheme()) || "classpath".equals(localURI.getScheme()))) {
                    localURI = responseURI;
                }
               return new ResolvedResourceImpl(requestURI, localURI, cached.file.toURI(), fs, cached.contentType());
            }
        } catch (IOException | URISyntaxException | IllegalArgumentException ex) {
            // IllegalArgumentException occurs if the (unresolved) URI is not absolute, for example.
            logger.log(ResolverLogger.ERROR, "Resolution failed: %s: %s", responseURI, ex.getMessage());
            return null;
        }
    }

    /** Resolve a URI.
     *
     * <p>The catalog is interrogated for the specified <code>href</code>. If it is found, its mapping is returned.
     * Otherwise, the <code>href</code> is made absolute with respect to the specified
     * <code>base</code> and the catalog is interrogated for the resulting absolute URI. If it is found,
     * its mapping is returned.</p>
     *
     * @param href The URI of the resource
     * @param baseURI The base URI against which <code>href</code> should be made absolute, if necessary.
     *
     * @return The resource that represents the URI or null.
     */
    @Override
    public ResolvedResource resolveURI(String href, String baseURI) {
        logger.log(ResolverLogger.REQUEST, "resolveURI: %s (base URI: %s)", href, baseURI);

        if (href == null || "".equals(href)) {
            href = baseURI;
            baseURI = null;
            if (href == null || "".equals(href)) {
                logger.log(ResolverLogger.RESPONSE, "resolveURI: null");
                return null;
            }
        }

        CatalogManager catalog = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI resolved = catalog.lookupURI(href);
        if (resolved != null) {
            logger.log(ResolverLogger.RESPONSE, "resolveURI: %s", resolved);
            return resource(href, resolved, cache.cachedUri(resolved));
        }

        String absolute = href;
        if (baseURI != null) {
            try {
                absolute = new URI(baseURI).resolve(href).toString();
                if (!href.equals(absolute)) {
                    resolved = catalog.lookupURI(absolute);
                    if (resolved != null) {
                        logger.log(ResolverLogger.RESPONSE, "resolveURI: %s", resolved);
                        return resource(absolute, resolved, cache.cachedUri(resolved));
                    }
                }
            } catch (URISyntaxException ex) {
                logger.log(ResolverLogger.ERROR, "URI syntax exception: %s (base URI: %s)", href, baseURI);
                logger.log(ResolverLogger.RESPONSE, "resolveURI: null");
                return null;
            }
        }

        if (cache.cacheURI(absolute)) {
            try {
                URI absuri = new URI(absolute);
                logger.log(ResolverLogger.RESPONSE, "resolveURI: cached %s", absolute);
                return resource(absolute, absuri, cache.cachedUri(absuri));
            } catch (URISyntaxException use) {
                logger.log(ResolverLogger.ERROR, "URI syntax exception: %s", absolute);
                logger.log(ResolverLogger.RESPONSE, "resolveURI: null");
                return null;
            }
        } else {
            logger.log(ResolverLogger.RESPONSE, "resolveURI: null");
            return null;
        }
    }

    @Override
    public ResolvedResource resolveEntity(String name, String publicId, String systemId, String baseURI) {
        if (name == null && publicId == null && systemId == null && baseURI == null) {
            logger.log(ResolverLogger.REQUEST, "resolveEntity: null");
            return null;
        }

        if (name != null && publicId == null && systemId == null) {
            logger.log(ResolverLogger.REQUEST, "resolveEntity: name: %s (%s)", name, baseURI);
            return resolveDoctype(name, baseURI);
        }

        if (name != null) {
            logger.log(ResolverLogger.REQUEST, "resolveEntity: %s %s (baseURI: %s, publicId: %s)",
                    name, systemId, baseURI, publicId);
        } else {
            logger.log(ResolverLogger.REQUEST, "resolveEntity: %s (baseURI: %s, publicId: %s)",
                    systemId, baseURI, publicId);
        }

        URI absSystem = null;

        CatalogManager catalog = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        ResolvedResourceImpl result = null;
        URI resolved = catalog.lookupEntity(name, systemId, publicId);
        if (resolved == null && systemId != null && config.getFeature(ResolverFeature.URI_FOR_SYSTEM)) {
            resolved = catalog.lookupURI(systemId);
        }
        if (resolved != null) {
            result = resource(systemId, resolved, cache.cachedSystem(resolved, publicId));
        } else {
            try {
                if (systemId != null) {
                    absSystem = new URI(systemId);
                }
                if (baseURI != null) {
                    absSystem = new URI(baseURI);
                    if (systemId != null) {
                        absSystem = absSystem.resolve(systemId);
                    }
                    resolved = catalog.lookupEntity(name, absSystem.toString(), publicId);
                    if (resolved == null && config.getFeature(ResolverFeature.URI_FOR_SYSTEM)) {
                        resolved = catalog.lookupURI(absSystem.toString());
                    }
                    if (resolved != null) {
                        result = resource(absSystem.toString(), resolved, cache.cachedSystem(resolved, publicId));
                    }
                }
            } catch (URISyntaxException ex) {
                logger.log(ResolverLogger.ERROR, "URI syntax exception: %s (base: %s)", systemId, baseURI);
                logger.log(ResolverLogger.RESPONSE, "resolveURI: null");
                return null;
            }
        }

        if (result != null) {
            logger.log(ResolverLogger.RESPONSE, "resolveEntity: %s", resolved);
            result.setName(name);
            return result;
        }

        if (absSystem == null) {
            logger.log(ResolverLogger.RESPONSE, "resolveEntity: null");
            return null;
        }

        if (cache.cacheURI(absSystem.toString())) {
            logger.log(ResolverLogger.RESPONSE, "resolveEntity: cached %s", absSystem);
            return resource(absSystem.toString(), absSystem, cache.cachedSystem(absSystem, publicId));
        }

        logger.log(ResolverLogger.RESPONSE, "resolveEntity: null");
        return null;
    }

    private ResolvedResource resolveDoctype(String name, String baseURI) {
        logger.log(ResolverLogger.REQUEST, "resolveDoctype: %s", name);
        CatalogManager catalog = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI resolved = catalog.lookupDoctype(name, null, null);
        if (resolved == null) {
            logger.log(ResolverLogger.RESPONSE, "resolveDoctype: null");
            return null;
        } else {
            logger.log(ResolverLogger.RESPONSE, "resolveDoctype: %s", resolved);
            ResolvedResourceImpl result = resource(null, resolved, cache.cachedSystem(resolved, null));
            if (result != null) {
                result.setName(name);
            }
            return result;
        }
    }

    /** Resolve a Namespace URI.
     *
     * <p>The catalog is interrogated for the specified namespace. If it is found, it is returned.</p>
     *
     * <p>The resolver attempts to parse the resource as an XML document. If it finds RDDL 1.0 markup,
     * it will attempt to locate the resource with the specified nature and purpose. That resource will
     * be downloaded and returned. If it cannot parse the document or cannot find the markup that
     * it is looking for, it will return the resource that represents the namespace URI.</p>
     *
     * @param href The URI of the resource.
     * @param baseURI The base URI of the resource.
     * @param nature The RDDL nature of the resource.
     * @param purpose The RDDL purpose of the resource.
     *
     * @return The resource that represents the URI or null.
     */
    @Override
    public ResolvedResource resolveNamespace(String href, String baseURI, String nature, String purpose) {
        logger.log(ResolverLogger.REQUEST, "resolveNamespace: %s (base: %s, nature: %s, purpose: %s)",
                href, baseURI, nature, purpose);
        CatalogManager catalog = config.getFeature(ResolverFeature.CATALOG_MANAGER);

        URI absolute = null;
        try {
            absolute = new URI(href);
        } catch (URISyntaxException use) {
            // nevermind
        }

        URI resolved = catalog.lookupNamespaceURI(href, nature, purpose);
        if (resolved == null && baseURI != null) {
            try {
                absolute = new URI(baseURI).resolve(href);
                // If we can make it absolute, we want it to be absolute from here on out
                href = absolute.toString();
                resolved = catalog.lookupNamespaceURI(href, nature, purpose);
            } catch (URISyntaxException use) {
                // nevermind
            }
        }

        CacheEntry cached = resolved == null ? null : cache.cachedNamespaceUri(resolved, nature, purpose);

        if (config.getFeature(ResolverFeature.PARSE_RDDL) && nature != null && purpose != null) {
            URI rddl = null;
            if (cached != null) {
                rddl = checkRddl(cached.file.toURI(), nature, purpose, cached.contentType());
                if (rddl != null) {
                    cached = cache.cachedUri(rddl);
                }
            } else {
                try {
                    if (resolved != null) {
                        rddl = checkRddl(resolved, nature, purpose, null);
                    } else {
                        rddl = checkRddl(URIUtils.newURI(href), nature, purpose, null);
                    }
                } catch (URISyntaxException ex) {
                    logger.log(ResolverLogger.ERROR, "URI syntax exception: " + href);
                    logger.log(ResolverLogger.RESPONSE, "resolveNamespace: null");
                    return null;
                }
            }

            if (rddl != null) {
                ResolvedResource local = resolveURI(rddl.toString(), href);
                if (local != null) {
                    logger.log(ResolverLogger.RESPONSE, "resolveNamespace: %s", local.getLocalURI());
                    return local;
                } else {
                    logger.log(ResolverLogger.RESPONSE, "resolveNamespace: %s", rddl);
                    return resource(href, rddl, cached);
                }
            }
        }

        if (resolved != null) {
            logger.log(ResolverLogger.RESPONSE, "resolveNamespace: %s", resolved);
            return resource(absolute == null ? null : absolute.toString(),
                    resolved, cache.cachedNamespaceUri(resolved, nature, purpose));
        } else {
            logger.log(ResolverLogger.RESPONSE, "resolveNamespace: null");
            return null;
        }
    }

    private URI checkRddl(URI uri, String nature, String purpose, String contentType) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            spf.setValidating(false);
            spf.setXIncludeAware(false);
            URLConnection conn = uri.toURL().openConnection();
            if (contentType == null) {
                contentType = conn.getContentType();
            }
            if (contentType != null
                    && (contentType.startsWith("text/html")
                    || contentType.startsWith("application/html+xml"))) {
                InputSource source = new InputSource(conn.getInputStream());
                RddlQuery handler = new RddlQuery(conn.getURL().toURI(), nature, purpose);
                SAXParser parser = spf.newSAXParser();
                parser.parse(source, handler);
                return handler.href();
            } else {
                return null;
            }
        } catch (ParserConfigurationException | SAXException | IOException | URISyntaxException ex) {
            logger.log(ResolverLogger.ERROR, "RDDL parse failed: %s: %s",
                    uri, ex.getMessage());
            return null;
        }
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
