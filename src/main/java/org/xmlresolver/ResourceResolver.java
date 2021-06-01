/*
 * ResourceResolver.java
 *
 * Created on December 29, 2006, 7:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import org.jetbrains.annotations.NotNull;
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
import java.net.URL;
import java.net.URLConnection;
import java.util.Stack;

/** Resolves resources in the catalog.
 *
 * <p>This class integrates the CatalogManager and the ResourceCache.</p>
 *
 * <p>The resolve* methods use the CatalogManager to resolve the resource. If an entry is found
 * in the catalog, the mapped resource is returned.</p>
 *
 * <p>If an entry isn't found, and caching is not enabled (or does not apply to the resource requested),
 * null is returned.</p>
 *
 * <p>If caching is enabled, then the resource is retrieved and added to the cache (if necessary).
 * That cached resource is returned.</p>
 *
 * <p>There's a subtle difference between how mapped and cached resources are returned.
 * If the resource is mapped in a catalog, the base URI of the resource returned is the
 * mapped URI. That's consistent with what the resolver APIs expect.</p>
 *
 * <p>If a cached resource is returned, the base URI is unchanged, even though InputStream
 * for the resource is a local file. That's necessary because subsequent attempts to resolve
 * relative URIs against that resource must resolve against the original URI. These will, in turn,
 * go through the resolver and will be cached.</p>
 *
 * <p>Without caching, a request for http://example.com/ that isn't mapped in the catalog will
 * return null. The process using the resolver will then, most likely, retrieve http://example.com/ and
 * proceed.</p>
 *
 * <p>With caching, a request for http://example.com/ that isn't mapped in the catalog will cause
 * the resolver to download the resource and add it to the cache. Then that resource will be returned
 * with the base URI of http://example.com/. The process using the resolver wil then, most likely,
 * continue processing with that resource.</p>
 *
 * <p>In the latter case, the result is almost guaranteed to be the same as the former case, but
 * the process using the resolver will not notice that the resolution "missed". It's possible,
 * however unlikely, for this to result in different behavior.</p>
 *
 */
public class ResourceResolver {
    private static final ResolverLogger logger = new ResolverLogger(ResourceResolver.class);
    private XMLResolverConfiguration config = null;
    private ResourceCache cache = null;

    /**
     * Creates a new instance of ResourceResolver using a default configuration.
     */
    public ResourceResolver() {
        this(new XMLResolverConfiguration());
    }

    /**
     * Creates a new instance of ResourceResolver with the specified resolver configuration.
     *
     * @param conf The {@link XMLResolverConfiguration} to use.
     * @throws NullPointerException if the configuration is null.
     */
    public ResourceResolver(XMLResolverConfiguration conf) {
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

    private Resource streamResult(URI uri, CacheEntry cached) {
        try {
            if (cached == null) {
                return new Resource(uri.toString());
            } else {
                FileInputStream fs = new FileInputStream(cached.file);
                return new Resource(fs, uri, cached.file.toURI(), cached.contentType());
            }
        } catch (IOException | URISyntaxException | IllegalArgumentException ex) {
            // IllegalArgumentException occurs if the (unresolved) URI is not absolute, for example.
            logger.log(ResolverLogger.ERROR, "Resolution failed: %s: %s", uri, ex.getMessage());
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
     * @param base The base URI against which <code>href</code> should be made absolute, if necessary.
     *
     * @return The resource that represents the URI or null.
     */
    public Resource resolveURI(String href, String base) {
        logger.log(ResolverLogger.REQUEST, "resolveURI: %s (base URI: %s)", href, base);

        if (href == null || "".equals(href)) {
            href = base;
            base = null;
            if (href == null || "".equals(href)) {
                logger.log(ResolverLogger.RESPONSE, "resolveURI: null");
                return null;
            }
        }

        CatalogManager catalog = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI resolved = catalog.lookupURI(href);

        if (resolved == null) {
            URI absolute = null;

            try {
                if (base == null) {
                    absolute = new URI(href);
                } else {
                    absolute = new URI(base).resolve(href);
                }

                if (!href.equals(absolute.toString())) {
                    resolved = catalog.lookupURI(absolute.toString());
                    if (resolved != null) {
                        logger.log(ResolverLogger.RESPONSE, "resolveURI: %s", resolved);
                        return streamResult(resolved, cache.cachedUri(resolved));
                    }
                }
            } catch (URISyntaxException ex) {
                logger.log(ResolverLogger.ERROR, "URI syntax exception: %s (base URI: %s)", href, base);
                logger.log(ResolverLogger.RESPONSE, "resolveURI: null", href, base);
                return null;
            }

            if (cache.cacheURI(absolute.toString())) {
                logger.log(ResolverLogger.RESPONSE, "resolveURI: cached %s", absolute);
                return streamResult(absolute, cache.cachedUri(absolute));
            } else {
                logger.log(ResolverLogger.RESPONSE, "resolveURI: null");
                return null;
            }
        }

        logger.log(ResolverLogger.RESPONSE, "resolveURI: %s", resolved);
        return streamResult(resolved, cache.cachedUri(resolved));
    }

    /** Resolve an external identifier.
     *
     * <p>The catalog is interrogated for the specified external identifier (using the system and public
     * identifiers provided). If it is found, it is returned.</p>
     *
     * @param systemId The system identifier of the entity.
     * @param publicId The public identifier of the entity.
     *
     * @return The resource that represents the external identifier or null.
     */
    public Resource resolvePublic(String systemId, String publicId) {
        logger.log(ResolverLogger.REQUEST, "resolvePublic: %s (%s)", systemId, publicId);
        CatalogManager catalog = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI resolved = catalog.lookupPublic(systemId, publicId);
        if (resolved == null && config.getFeature(ResolverFeature.URI_FOR_SYSTEM)) {
            resolved = catalog.lookupURI(systemId);
        }
        if (resolved == null) {
            try {
                resolved = URIUtils.newURI(systemId);
                if (cache.cacheURI(resolved.toString())) {
                    logger.log(ResolverLogger.RESPONSE, "resolvePublic: cached %s", resolved);
                    return streamResult(resolved, cache.cachedUri(resolved));
                } else {
                    logger.log(ResolverLogger.RESPONSE, "resolvePublic: null");
                    return null;
                }
            } catch (URISyntaxException ex) {
                logger.log(ResolverLogger.ERROR, "URI syntax exception: " + systemId);
                logger.log(ResolverLogger.RESPONSE, "resolvePublic: null");
                return null;
            }
        }

        logger.log(ResolverLogger.RESPONSE, "resolvePublic: %s", resolved);
        return streamResult(resolved, cache.cachedSystem(resolved, publicId));
    }

    /** Resolve an entity using its system and public identifiers or its name.
     *
     * <p>The catalog is interrogated for the specified entity (using the system and public
     * identifiers provided and its name). If it is found, it is returned.</p>
     *
     * @param name The name of the entity.
     * @param systemId The system identifier of the entity.
     * @param publicId The public identifier of the entity.
     *
     * @return The resource that represents the entity or null.
     */
    public Resource resolveEntity(String name, String systemId, String publicId) {
        return resolveEntity(name, publicId, systemId, null);
    }

    /** Resolve an entity using its system and public identifiers or its name.
     *
     * <p>The catalog is interrogated for the specified entity (using the system and public
     * identifiers provided and its name). If it is found, it is returned.</p>
     *
     * <p>If it's not found and the system identifier is null, null is returned.</p>
     *
     * <p>If it's not found and the baseURI is not null, the system identifier is resolved
     * against the base URI and a second attempt is made to find the resource in the catalog
     * using the resolved system identifier.</p>
     *
     * @param name The name of the entity.
     * @param systemId The system identifier of the entity.
     * @param publicId The public identifier of the entity.
     * @param baseURI The base URI of the entity.
     *
     * @return The resource that represents the entity or null.
     */
    public Resource resolveEntity(String name, String publicId, String systemId, String baseURI) {
        if (name != null) {
            logger.log(ResolverLogger.REQUEST, "resolveEntity: %s %s (baseURI: %s, publicId: %s)",
                    name, systemId, baseURI, publicId);
        } else {
            logger.log(ResolverLogger.REQUEST, "resolveEntity: %s (baseURI: %s, publicId: %s)",
                    systemId, baseURI, publicId);
        }

        CatalogManager catalog = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI resolved = catalog.lookupEntity(name, systemId, publicId);

        if (resolved == null) {
            if (systemId == null) {
                logger.log(ResolverLogger.RESPONSE, "resolveEntity: null");
                return null;
            } else {
                if (config.getFeature(ResolverFeature.URI_FOR_SYSTEM)) {
                    resolved = catalog.lookupURI(systemId);
                }
            }
        }

        if (resolved != null) {
            logger.log(ResolverLogger.RESPONSE, "resolveEntity: %s", resolved);
            return streamResult(resolved, cache.cachedSystem(resolved, publicId));
        }

        URI resolvedSystem = null;

        try {
            if (baseURI != null) {
                resolvedSystem = URIUtils.newURI(baseURI).resolve(systemId);
            } else {
                resolvedSystem = URIUtils.newURI(systemId);
            }

            if (!systemId.equals(resolvedSystem.toString())) {
                resolved = catalog.lookupEntity(name, resolvedSystem.toString(), publicId);
            }

            if (resolved == null && config.getFeature(ResolverFeature.URI_FOR_SYSTEM)) {
                resolved = catalog.lookupURI(resolvedSystem.toString());
            }
        } catch (URISyntaxException ex) {
            if (baseURI == null) {
                logger.log(ResolverLogger.ERROR, "URI syntax exception: %s", systemId);
            } else {
                logger.log(ResolverLogger.ERROR, "URI syntax exception: %s (base URI: %s)", systemId, baseURI);
            }
            logger.log(ResolverLogger.RESPONSE, "resolveEntity: null");
            return null;
        }

        if (resolved == null) {
            if (cache.cacheURI(resolvedSystem.toString())) {
                logger.log(ResolverLogger.RESPONSE, "resolveEntity: cached %s", resolvedSystem);
                return streamResult(resolvedSystem, cache.cachedSystem(resolvedSystem, publicId));
            }
            logger.log(ResolverLogger.RESPONSE, "resolveEntity: null");
            return null;
        } else {
            logger.log(ResolverLogger.RESPONSE, "resolveEntity: %s");
            return streamResult(resolved, cache.cachedSystem(resolved, publicId));
        }
    }

    /** Supports the {@link org.w3c.dom.ls.LSResourceResolver} interface.
     *
     *
     * <blockquote>
     * <p>Allow the application to resolve external resources:</p>
     * <p>The LSParser will call this method before opening any external resource, including the external DTD subset,
     * external entities referenced within the DTD, and external entities referenced within the document element
     * (however, the top-level document entity is not passed to this method). The application may then request that
     * the LSParser resolve the external resource itself, that it use an alternative URI, or that it use an
     * entirely different input source.</p>
     *
     * <p>Application writers can use this method to redirect external system identifiers to secure and/or local URI,
     * to look up public identifiers in a catalogue, or to read an entity from a database or other input source
     * (including, for example, a dialog box).</p>
     * </blockquote>
     *
     * <p>If the type is XML (<code>http://www.w3.org/TR/REC-xml</code>), this method attempts to resolve the
     * resource with the public and system
     * identifiers provided (see <code>resolvePublic</code>). Otherwise, this method attempts to
     * resolve a URI (see <code>resolveNamespaceURI</code>) with the type as the "nature"
     * of the resource.</p>
     *
     * @param type The type of the resource being resolved. For XML [XML 1.0] resources (i.e. entities),
     *             applications must use the value "http://www.w3.org/TR/REC-xml". For XML Schema [XML Schema Part 1],
     *             applications must use the value "http://www.w3.org/2001/XMLSchema". Other types of resources
     *             are outside the scope of this specification and therefore should recommend an absolute
     *             URI in order to use this method.
     * @param namespace The namespace of the resource being resolved, e.g. the target namespace of the
     *                  XML Schema [XML Schema Part 1] when resolving XML Schema resources.
     * @param publicId The public identifier of the external entity being referenced, or null if no public
     *                 identifier was supplied or if the resource is not an entity.
     * @param systemId The system identifier, a URI reference [IETF RFC 2396], of the external resource
     *                 being referenced, or null if no system identifier was supplied.
     * @param baseURI The absolute base URI of the resource being parsed, or null if there is no base URI.
     * @return A LSInput object describing the new input source, or null to request that the parser
     * open a regular URI connection to the resource.
     * @see #resolvePublic
     * @see #resolveNamespaceURI
     *  */
    public Resource resolveResource(String type, String namespace, String publicId, String systemId, String baseURI) {
        Resource rsrc = null;
        if (type == null || "http://www.w3.org/TR/REC-xml".equals(type)) {
            logger.log(ResolverLogger.TRACE, "resolveResource: XML: %s (baseURI: %s, publicId: %s)",
                    systemId, baseURI, publicId);
            rsrc = resolvePublic(systemId, publicId);
            if (rsrc == null && baseURI != null) {
                try {
                    URI abs = URIUtils.newURI(baseURI).resolve(systemId);
                    rsrc = resolvePublic(abs.toString(), publicId);
                } catch (URISyntaxException ex) {
                    logger.log(ResolverLogger.ERROR, "URI syntax exception: " + baseURI);
                }
            }
        } else {
            logger.log(ResolverLogger.TRACE, "resolveResource: %s, %s (namespace: %s, baseURI: %s, publicId: %s)",
                    type, systemId, namespace, baseURI, publicId);

            String purpose = null;
            // If it looks like it's going to be used for validation, ...
            if ("http://www.w3.org/2001/XMLSchema".equals(type)
                || "http://www.w3.org/XML/XMLSchema/v1.1".equals(type)
                || "http://relaxng.org/ns/structure/1.0".equals(type)) {
                purpose = "http://www.rddl.org/purposes#schema-validation";
            }

            rsrc = resolveNamespaceURI(systemId, type, purpose);
            if (rsrc == null && baseURI != null) {
                try {
                    URI abs = URIUtils.newURI(baseURI).resolve(systemId);
                    rsrc = resolveNamespaceURI(abs.toString(), type, null);
                } catch (URISyntaxException ex) {
                    logger.log(ResolverLogger.ERROR, "URI syntax exception: " + baseURI);
                }
            }
        }

        return rsrc;
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
     * @param uri The URI of the resource.
     * @param nature The RDDL nature of the resource.
     * @param purpose The RDDL purpose of the resource.
     *
     * @return The resource that represents the URI or null.
     */
    public Resource resolveNamespaceURI(String uri, String nature, String purpose) {
        logger.log(ResolverLogger.REQUEST, "resolveNamespace: %s (nature: %s, purpose: %s)",
                uri, nature, purpose);
        CatalogManager catalog = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI resolved = catalog.lookupNamespaceURI(uri, nature, purpose);
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
                        rddl = checkRddl(URIUtils.newURI(uri), nature, purpose, null);
                    }
                } catch (URISyntaxException ex) {
                    logger.log(ResolverLogger.ERROR, "URI syntax exception: " + uri);
                    logger.log(ResolverLogger.RESPONSE, "resolveNamespace: null");
                    return null;
                }
            }

            if (rddl != null) {
                Resource local = resolveURI(rddl.toString(), uri.toString());
                if (local != null) {
                    logger.log(ResolverLogger.RESPONSE, "resolveNamespace: %s", local.localUri());
                    return local;
                } else {
                    logger.log(ResolverLogger.RESPONSE, "resolveNamespace: %s", rddl);
                    return streamResult(rddl, cached);
                }
            }
        }

        if (resolved != null) {
            logger.log(ResolverLogger.RESPONSE, "resolveNamespace: %s", resolved);
            return streamResult(resolved, cache.cachedNamespaceUri(resolved, nature, purpose));
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

    /** Resolve a document type.
     *
     * <p>The catalog is interrogated for an external subset to use for the specified root element.
     * If it is found, it is returned.</p>
     *
     * <p>If the external subset cannot be found locally, null is returned.
     *
     * @param name The name of the root element.
     *
     * @return The resource that represents the external subset.
     */
    public Resource resolveDoctype(String name) {
        logger.log(ResolverLogger.REQUEST, "resolveDoctype: %s", name);
        CatalogManager catalog = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI resolved = catalog.lookupDoctype(name, null, null);
        if (resolved == null) {
            logger.log(ResolverLogger.RESPONSE, "resolveDoctype: null");
            return null;
        } else {
            logger.log(ResolverLogger.RESPONSE, "resolveDoctype: %s", resolved);
            return streamResult(resolved, cache.cachedSystem(resolved, null));
        }
    }

    private static class RddlQuery extends DefaultHandler {
        private final String nature;
        private final String purpose;
        private URI found = null;
        private final Stack<URI> baseUriStack = new Stack<>();

        public RddlQuery(@NotNull URI baseURI, @NotNull String nature, @NotNull String purpose) {
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
