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
import org.xmlresolver.utils.URIUtils;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.HashSet;

/** An implementation of many resolver interfaces.
 *
 * <p>This class is probably the most common entry point to the XML Catalog resolver. It has a zero
 * argument constructor so it can be instantiated directly from its class name (for example, passed to
 * an application as a commend line argument or stored in a configuration file). When instantiated
 * this way, it will automatically be configured by system properties and an <code>xmlresolver.properties</code>
 * configuration file, if one exists.</p>
 *
 * <p>This class implements the {@link org.xml.sax.EntityResolver}, {@link org.xml.sax.ext.EntityResolver2},
 * {@link LSResourceResolver}
 * and {@link org.xmlresolver.NamespaceResolver}, and {@link javax.xml.transform.URIResolver} interfaces.</p>
 *
 * <p>The StAX {@link javax.xml.stream.XMLResolver} interface is implemented by the
 * {@link org.xmlresolver.StAXResolver} class because the <code>resolveEntity</code> method
 * of the <code>XMLResolver</code> interface isn't compatible with the <code>EntityResolver2</code>
 * method of the same name.</p>
 *
 * @see org.xmlresolver.StAXResolver
 */

public class Resolver implements URIResolver, EntityResolver, EntityResolver2, NamespaceResolver, LSResourceResolver {
    public static final String PURPOSE_SCHEMA_VALIDATION = "http://www.rddl.org/purposes#schema-validation";
    public static final String NATURE_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    public static final String NATURE_XML_SCHEMA_1_1 = "http://www.w3.org/2001/XMLSchema/v1.1";
    public static final String NATURE_RELAX_NG = "http://relaxng.org/ns/structure/1.0";

    private final ResolverLogger logger;
    protected final XMLResolverConfiguration config;
    protected final CatalogResolver resolver;

    /** Creates a new instance of Resolver.
     *
     * The default resolver is a new ResourceResolver that uses a static catalog shared by all threads.
     */
    public Resolver() {
        config = new XMLResolverConfiguration();
        resolver = new CatalogResolver(config);
        logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
    }

    /** Creates a new instance of a Resolver.
     *
     * Creates a resolver using a specific Catalog.
     *
     * @param config The configuration to use.
     */
    public Resolver(XMLResolverConfiguration config) {
        this.config = config;
        resolver = new CatalogResolver(config);
        logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
    }

    /** Creates a new instance of a Resolver.
     *
     * Creates a resolver using a specific underlying ResourceResolver.
     *
     * @param resolver The resource resolver to use.
     */
    public Resolver(CatalogResolver resolver) {
        config = resolver.getConfiguration();
        this.resolver = resolver;
        logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
    }

    /** What version is this?
     *
     * Returns the version number of this resolver instance.
     *
     * @return The version number
     */
    public static String version() {
        return BuildConfig.VERSION;
    }

    /** Get the Catalog used by this resolver.
     *
     * @return The underlying catalog.
     */
    public XMLResolverConfiguration getConfiguration() {
        return resolver.getConfiguration();
    }

    /** Get the underlying {@link CatalogResolver} used by this resolver.
     * @return The catalog resolver.
     */
    public CatalogResolver getCatalogResolver() {
        return resolver;
    }

    /** Implements the {@link javax.xml.transform.URIResolver} interface. */
    @Override
    public Source resolve(String href, String base) throws TransformerException {
        ResolvedResource rsrc = resolver.resolveURI(href, base);
        if (rsrc == null) {
            if (href == null || !config.getFeature(ResolverFeature.ALWAYS_RESOLVE)) {
                return null;
            }

            try {
                URI uri = base == null ? null : new URI(base);
                uri = uri == null ? new URI(href) : uri.resolve(href);
                rsrc = openConnection(uri);
            } catch (URISyntaxException | IOException ex) {
                if (resolver.getConfiguration().getFeature(ResolverFeature.THROW_URI_EXCEPTIONS)) {
                    throw new TransformerException(ex);
                }
                return null;
            }

            if (rsrc == null) {
                return null;
            }
        }

        ResolverSAXSource source = new ResolverSAXSource(rsrc);
        source.setSystemId(rsrc.getResolvedURI().toString());
        return source;
    }

    /** Implements the {@link org.w3c.dom.ls.LSResourceResolver} interface. */
    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        ResolvedResource rsrc = null;
        if (type == null || "http://www.w3.org/TR/REC-xml".equals(type)) {
            logger.log(AbstractLogger.REQUEST, "resolveResource: XML: %s (baseURI: %s, publicId: %s)",
                    systemId, baseURI, publicId);
            rsrc = resolver.resolveEntity(null, publicId, systemId, baseURI);
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

            if(systemId != null ) {
                rsrc = resolver.resolveNamespace(systemId, baseURI, type, purpose);
            }
        }

        if (rsrc == null) {
            return null;
        }

        return new ResolverLSInput(rsrc, publicId);
    }

    /** Implements the {@link org.xml.sax.ext.EntityResolver2} interface. */
    @Override
    public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
        ResolvedResource rsrc = resolver.resolveEntity(name, null, null, baseURI);
        if (rsrc == null) {
            return null;
        }

        ResolverInputSource source = new ResolverInputSource(rsrc);
        source.setSystemId(rsrc.getResolvedURI().toString());
        return source;
    }

    /** Implements the {@link org.xml.sax.ext.EntityResolver2} interface. */
    @Override
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
        ResolvedResource rsrc = resolver.resolveEntity(name, publicId, systemId, baseURI);
        if (rsrc == null) {
            if (systemId == null || !config.getFeature(ResolverFeature.ALWAYS_RESOLVE)) {
                return null;
            }
            rsrc = openConnection(systemId);
            if (rsrc == null) {
                return null;
            }
        }

        ResolverInputSource source = new ResolverInputSource(rsrc);
        source.setSystemId(rsrc.getResolvedURI().toString());
        return source;
    }

    /** Implements the {@link org.xml.sax.EntityResolver} interface. */
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        ResolvedResource rsrc = resolver.resolveEntity(null, publicId, systemId, null);
        if (rsrc == null) {
            if (systemId == null || !config.getFeature(ResolverFeature.ALWAYS_RESOLVE)) {
                return null;
            }
            rsrc = openConnection(systemId);
            if (rsrc == null) {
                return null;
            }
        }

        ResolverInputSource source = new ResolverInputSource(rsrc);
        source.setSystemId(rsrc.getResolvedURI().toString());
        return source;
    }

    /** Implements the {@link org.xmlresolver.NamespaceResolver} interface. */
    @Override
    public Source resolveNamespace(String uri, String nature, String purpose) throws TransformerException {
        ResolvedResource rsrc = resolver.resolveNamespace(uri, null, nature, purpose);
        if (rsrc == null) {
            return null;
        }

        ResolverSAXSource source = new ResolverSAXSource(rsrc.getLocalURI(), new InputSource(rsrc.getInputStream()));
        source.setSystemId(rsrc.getResolvedURI().toString());
        return source;
    }

    protected ResolvedResource openConnection(String absuri) throws IOException {
        try {
            return openConnection(URIUtils.cwd().resolve(absuri));
        } catch (IllegalArgumentException ex) {
            if (config.getFeature(ResolverFeature.THROW_URI_EXCEPTIONS)) {
                throw ex;
            }
            return null;
        }
    }

    protected ResolvedResource openConnection(URI originalURI) throws IOException {
        boolean throwExceptions = config.getFeature(ResolverFeature.THROW_URI_EXCEPTIONS);

        HashSet<URI> seen = new HashSet<>();
        int count = 100;

        URI absoluteURI = originalURI;
        URLConnection connection = null;
        boolean done = false;
        int code = 200;

        while (!done) {
            if (seen.contains(absoluteURI)) {
                if (throwExceptions) {
                    throw new IOException("Redirect loop on " + absoluteURI);
                }
                return null;
            }
            if (count <= 0) {
                if (throwExceptions) {
                    throw new IOException("Too many redirects on " + absoluteURI);
                }
                return null;
            }
            seen.add(absoluteURI);
            count--;

            try {
                connection = absoluteURI.toURL().openConnection();
                connection.connect();
            } catch (Exception ex) {
                if (throwExceptions) {
                    throw ex;
                }
                return null;
            }

            done = !(connection instanceof HttpURLConnection);
            if (!done) {
                HttpURLConnection conn = (HttpURLConnection) connection;
                code = conn.getResponseCode();
                if (code >= 300 && code < 400) {
                    String loc = conn.getHeaderField("location");
                    absoluteURI = absoluteURI.resolve(loc);
                } else {
                    done = true;
                }
            }
        }

        ResolvedResourceImpl rsrc = new ResolvedResourceImpl(originalURI, absoluteURI, connection.getInputStream(), code, connection.getHeaderFields());
        return rsrc;
    }
}
