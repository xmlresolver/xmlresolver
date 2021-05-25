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
import java.net.URLConnection;
import java.util.Stack;

/** Resolves resources in the catalog.
 *
 */
public class ResourceResolver {
    private static final ResolverLogger logger = new ResolverLogger(ResourceResolver.class);
    private XMLResolverConfiguration config = null;
    private ResourceCache cache = null;

    /**
     * Creates a new instance of ResourceResolver.
     *
     * <p>By default, a static catalog initialized using the default properties is used by all ResourceResolvers.</p>
     */
    public ResourceResolver() {
        this(new XMLResolverConfiguration());
    }

    /**
     * Creates a new instance of ResourceResolver with the specified resolver configuration.
     *
     * @param conf The {@link XMLResolverConfiguration} to use.
     */
    public ResourceResolver(XMLResolverConfiguration conf) {
        if (conf == null) {
            throw new NullPointerException("XMLResolverConfiguration is null");
        }
        config = conf;
        cache = new ResourceCache(config);
    }

    /** Returns the {@link XMLResolverConfiguration} used by this ResourceResolver.
     *
     * @return The catalog
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
                return new Resource(fs, uri, cached.contentType());
            }
        } catch (IOException | URISyntaxException | IllegalArgumentException ex) {
            // IllegalArgumentException occurs if the (unresolved) URI is not absolute, for example.
            logger.log(ResolverLogger.ERROR, "Resolution failed: %s: %s", uri, ex.getMessage());
            return null;
        }
    }

    /** Resolve a URI.
     *
     * <p>The catalog is interrogated for the specified <code>href</code>. If it is found, it is returned.
     * Otherwise, the <code>href</code> is made absolute with respect to the specified
     * <code>base</code> and the catalog is interrogated for the resulting absolute URI. If it is found,
     * it is returned.</p>
     *
     * <p>If the resource cannot be found locally, it is downloaded and added to the cache. The resulting
     * cached resource is returned.</p>
     *
     * <p>Note that the original URI is provided as the base URI in the returned {@link org.xmlresolver.Resource}. This
     * means that relative URIs in the locally cached resource will still resolve correctly.</p>
     *
     * @param href The URI of the resource
     * @param base The base URI against which <code>href</code> should be made absolute, if necessary.
     *
     * @return The resource that represents the URI.
     */
    public Resource resolveURI(String href, String base) {
        logger.log(ResolverLogger.REQUEST, "resolveURI: %s (base URI: %s)", href, base);

        if (href == null || "".equals(href)) {
            href = base;
            base = null;
            if (href == null || "".equals(href)) {
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
                        return streamResult(resolved, cache.cachedUri(resolved));
                    }
                }
            } catch (URISyntaxException ex) {
                logger.log(ResolverLogger.ERROR, "Failed to resolve URI: %s (base URI: %s)", href, base);
                return null;
            }

            if (cache.cacheURI(absolute.toString())) {
                return streamResult(absolute, cache.cachedUri(absolute));
            } else {
                return null;
            }
        }

        return streamResult(resolved, cache.cachedUri(resolved));
    }

    /** Resolve an external identifier.
     *
     * <p>The catalog is interrogated for the specified external identifier (using the system and public
     * identifiers provided). If it is found, it is returned.</p>
     *
     * <p>If the external identifier cannot be found locally, it is downloaded and added to the cache. The resulting
     * cached resource is returned.</p>
     *
     * <p>Note that the original system identifier is provided as the base URI in the returned {@link Resource}. This
     * means that relative URIs in the locally cached resource will still resolve correctly.</p>
     *
     * @param systemId The system identifier of the entity.
     * @param publicId The public identifier of the entity.
     *
     * @return The resource that represents the external identifier.
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
                    return streamResult(resolved, cache.cachedUri(resolved));
                } else {
                    return null;
                }
            } catch (URISyntaxException ex) {
                logger.log(ResolverLogger.ERROR, "URI syntax exception: " + systemId);
                return null;
            }
        }

        return streamResult(resolved, cache.cachedSystem(resolved, publicId));
    }

    /** Resolve an entity using it's system and public identifiers or its name.
     *
     * <p>The catalog is interrogated for the specified entity (using the system and public
     * identifiers provided and its name). If it is found, it is returned.
     *
     * <p>If the external identifier cannot be found locally, it is downloaded and added to the cache. The resulting
     * cached resource is returned.</p>
     *
     * <p>Note that the original system identifier is provided as the base URI in the returned {@link Resource}. This
     * means that relative URIs in the locally cached resource will still resolve correctly.</p>
     *
     * @param name The name of the entity.
     * @param systemId The system identifier of the entity.
     * @param publicId The public identifier of the entity.
     *
     * @return The resource that represents the entity.
     */
    public Resource resolveEntity(String name, String systemId, String publicId) {
        return resolveEntity(name, publicId, systemId, null);
    }

    public Resource resolveEntity(String name, String publicId, String systemId, String baseURI) {
        logger.log(ResolverLogger.REQUEST, "resolveEntity: %s %s (baseURI: %s, publicId: %s)",
                name, systemId, baseURI, publicId);
        CatalogManager catalog = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI resolved = catalog.lookupEntity(name, systemId, publicId);
        if (resolved != null) {
            return streamResult(resolved, cache.cachedSystem(resolved, publicId));
        }
        if (systemId == null) {
            return null;
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
                logger.log(ResolverLogger.ERROR, "URI syntax exception: %s", baseURI);
            }
            return null;
        }

        if (resolved == null) {
            if (cache.cacheURI(resolvedSystem.toString())) {
                return streamResult(resolvedSystem, cache.cachedSystem(resolvedSystem, publicId));
            }
            return null;
        } else {
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
            rsrc = resolveNamespaceURI(systemId, type, null);
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
     * <p>If the resource cannot be found locally, it is downloaded and added to the cache.</p>
     * 
     * <p>The resolver attempts to parse the resource as an XML document. If it finds RDDL 1.0 markup,
     * it will attempt to locate the resource with the specified nature and purpose. That resource will
     * be downloaded and returned. If it cannot parse the document or cannot find the markup that
     * it is looking for, it will return the resource that represents the namespace URI.</p>
     *
     * <p>Note that the original URI is provided as the base URI in the returned {@link Resource}. This
     * means that relative URIs in the locally cached resource will still resolve correctly.</p>
     *
     * @param uri The URI of the resource.
     * @param nature The RDDL nature of the resource.
     * @param purpose The RDDL purpose of the resource.
     *
     * @return The resource that represents the URI.
     */
    public Resource resolveNamespaceURI(String uri, String nature, String purpose) {
        logger.log(ResolverLogger.REQUEST, "resolveNamespace: %s (nature: %s, purpose: %s)",
                uri, nature, purpose);
        CatalogManager catalog = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI resolved = catalog.lookupNamespaceURI(uri, nature, purpose);
        if (resolved == null) {
            try {
                resolved = URIUtils.newURI(uri);
            } catch (URISyntaxException ex) {
                logger.log(ResolverLogger.ERROR, "URI syntax exception: " + uri);
                return null;
            }
        }

        CacheEntry cached = cache.cachedNamespaceUri(resolved, nature, purpose);
        if (cached != null) {
            // Wait, wait, what if this is a RDDL document?
            if (nature != null && purpose != null
                    && ("text/html".equals(cached.contentType())
                    || "application/html+xml".equals(cached.contentType()))) {
                URI rddl = checkRddl(cached, nature, purpose);
                if (rddl != null) {
                    cached = cache.cachedUri(rddl);
                }
            }
            try {
                FileInputStream fs = new FileInputStream(cached.file);
                return new Resource(fs, resolved, cached.contentType());
            } catch (IOException ex) {
                logger.log(ResolverLogger.ERROR, "Namespace URI resolution failed: %s: %s",
                        resolved, ex.getMessage());
                return null;
            }
        }

        // What if this is an XHTML RDDL document?

        try {
            URLConnection conn = resolved.toURL().openConnection();
            return new Resource(conn.getInputStream(), resolved, conn.getContentType());
        } catch (IOException ex) {
            logger.log(ResolverLogger.ERROR, "Namespace URI resolution failed: %s: %s",
                    resolved, ex.getMessage());
            return null;
        }
    }

    private URI checkRddl(CacheEntry cached, String nature, String purpose) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            spf.setValidating(false);
            spf.setXIncludeAware(false);
            FileInputStream is = new FileInputStream(cached.file);
            InputSource source = new InputSource(is);
            RddlQuery handler = new RddlQuery(cached.location(), nature, purpose);
            SAXParser parser = spf.newSAXParser();
            parser.parse(source, handler);
            return handler.href();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            logger.log(ResolverLogger.ERROR, "RDDL parse failed: %s: %s",
                    cached.file.getAbsolutePath(), ex.getMessage());
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
            return null;
        } else {
            try {
                URLConnection conn = resolved.toURL().openConnection();
                return new Resource(conn.getInputStream(), resolved, conn.getContentType());
            } catch (IOException ex) {
                logger.log(ResolverLogger.ERROR, "Doctype resolution failed: %s: %s",
                        resolved, ex.getMessage());
                return null;
            }
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
                System.err.println("CHECK: " + rnature + ": " + rpurpose);
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
