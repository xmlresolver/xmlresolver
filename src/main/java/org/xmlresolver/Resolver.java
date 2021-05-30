package org.xmlresolver;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

/** Implements the {@link org.xml.sax.EntityResolver}, {@link org.xml.sax.ext.EntityResolver2},
 * {@link LSResourceResolver}
 * and {@link org.xmlresolver.NamespaceResolver}, and {@link javax.xml.transform.URIResolver} interfaces.
 *
 * <p>The StAX {@link javax.xml.stream.XMLResolver} interface is implemented by the
 * {@link org.xmlresolver.StAXResolver} class because the <code>resolveEntity</code> method
 * of the <code>XMLResolver</code> interface isn't compatible with the <code>EntityResolver2</code>
 * method of the same name.</p>
 *
 * @see org.xmlresolver.StAXResolver
 */
public class Resolver implements URIResolver, EntityResolver, EntityResolver2, NamespaceResolver, LSResourceResolver {
    private static final ResolverLogger logger = new ResolverLogger(Resolver.class);
    protected ResourceResolver resolver = null;

    /** Creates a new instance of Resolver.
     *
     * The default resolver is a new ResourceResolver that uses a static catalog shared by all threads.
     */
    public Resolver() {
        resolver = new ResourceResolver();
    }

    /** Creates a new instance of a Resolver.
     *
     * Creates a resolver using a specific Catalog.
     *
     * @param config The configuration to use.
     */
    public Resolver(XMLResolverConfiguration config) {
        resolver = new ResourceResolver(config);
    }

    /** Creates a new instance of a Resolver.
     *
     * Creates a resolver using a specific underlying ResourceResolver.
     *
     * @param resolver The resource resolver to use.
     */
    public Resolver(ResourceResolver resolver) {
        this.resolver = resolver;
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

    public Resource resolveResource(String href, String base)    {
        return resolver.resolveURI(href, base);
    }

    /** Implements the {@link javax.xml.transform.URIResolver} interface. */
    public Source resolve(String href, String base) throws TransformerException {
        Resource rsrc = resolveResource(href, base);
        if (rsrc == null) {
            return null;
        } else {
            SAXSource source = new SAXSource(new InputSource(rsrc.body()));
            boolean mask = resolver.getConfiguration().getFeature(ResolverFeature.MASK_JAR_URIS);
            String systemId = rsrc.uri().toString();
            if (mask && (systemId.startsWith("jar:") || systemId.startsWith("classpath:"))) {
                if (base == null) {
                    systemId = href;
                } else {
                    systemId = URI.create(base).resolve(href).toString();
                }
            }
            source.setSystemId(systemId);
            return source;
        }
    }

    /** Implements the {@link org.xmlresolver.NamespaceResolver} interface. */
    public Source resolveNamespace(String uri, String nature, String purpose) throws TransformerException {
        Resource rsrc = resolver.resolveNamespaceURI(uri, nature, purpose);
        if (rsrc == null) {
            return null;
        } else {
            SAXSource source = new SAXSource(new InputSource(rsrc.body()));
            boolean mask = resolver.getConfiguration().getFeature(ResolverFeature.MASK_JAR_URIS);
            String systemId = rsrc.uri().toString();
            if (mask && (systemId.startsWith("jar:") || systemId.startsWith("classpath:"))) {
                systemId = uri;
            }
            source.setSystemId(systemId);
            return source;
        }
    }

    /** Implements the {@link org.xml.sax.EntityResolver} interface. */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        Resource rsrc = resolver.resolvePublic(systemId, publicId);
        if (rsrc == null) {
            return null;
        } else {
            InputSource source = new InputSource(rsrc.body());
            boolean mask = resolver.getConfiguration().getFeature(ResolverFeature.MASK_JAR_URIS);
            String resolvedSystemId = rsrc.uri().toString();
            if (mask && (resolvedSystemId.startsWith("jar:") || resolvedSystemId.startsWith("classpath:"))) {
                resolvedSystemId = systemId;
            }
            source.setSystemId(resolvedSystemId);
            return source;
        }
    }

    /** Implements the {@link org.xml.sax.ext.EntityResolver2} interface. */
    public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
        Resource rsrc = resolver.resolveDoctype(name);
        if (rsrc == null) {
            return null;
        } else {
            InputSource source = new InputSource(rsrc.body());
            boolean mask = resolver.getConfiguration().getFeature(ResolverFeature.MASK_JAR_URIS);
            String systemId = rsrc.uri().toString();
            if (mask && (systemId.startsWith("jar:") || systemId.startsWith("classpath:"))) {
                if (baseURI != null) {
                    systemId = baseURI;
                }
            }
            source.setSystemId(systemId);
            return source;
        }
    }

    /** Implements the {@link org.xml.sax.ext.EntityResolver2} interface. */
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
        Resource rsrc = resolver.resolveEntity(name, publicId, systemId, baseURI);
        if (rsrc == null) {
           return null;
        } else {
            InputSource source = new InputSource(rsrc.body());
            boolean mask = resolver.getConfiguration().getFeature(ResolverFeature.MASK_JAR_URIS);
            String resolvedSystemId = rsrc.uri().toString();
            if (mask && (resolvedSystemId.startsWith("jar:") || resolvedSystemId.startsWith("classpath:"))) {
                if (baseURI == null) {
                    resolvedSystemId = systemId;
                } else {
                    resolvedSystemId = URI.create(baseURI).resolve(systemId).toString();
                }
            }
            source.setSystemId(resolvedSystemId);
            return source;
        }
    }

    /** Implements the {@link org.w3c.dom.ls.LSResourceResolver} interface. */
    public LSInput resolveResource(String type, String namespace, String publicId, String systemId, String baseURI) {
        Resource rsrc = resolver.resolveResource(type, namespace, publicId, systemId, baseURI);
        if (rsrc == null) {
            return null;
        } else {
            boolean mask = resolver.getConfiguration().getFeature(ResolverFeature.MASK_JAR_URIS);
            String resolvedSystemId = rsrc.uri().toString();
            if (mask && (resolvedSystemId.startsWith("jar:") || resolvedSystemId.startsWith("classpath:"))) {
                if (baseURI == null) {
                    resolvedSystemId = systemId;
                } else {
                    resolvedSystemId = URI.create(baseURI).resolve(systemId).toString();
                }
            }

            return new ResolverLSInput(rsrc, publicId, resolvedSystemId);
        }
    }

    static class ResolverLSInput implements LSInput {
        final Resource rsrc;
        final String publicId;
        final String systemId;

        public ResolverLSInput(Resource rsrc, String publicId, String systemId) {
            this.rsrc = rsrc;
            this.publicId = publicId;
            this.systemId = systemId;
        }

        public Reader getCharacterStream() {
            return new InputStreamReader(rsrc.body());
        }

        public void setCharacterStream(Reader reader) {
            throw new UnsupportedOperationException("Can't set character stream on resolver LSInput");
        }

        public InputStream getByteStream() {
            return rsrc.body();
        }

        public void setByteStream(InputStream inputStream) {
            throw new UnsupportedOperationException("Can't set byte stream on resolver LSInput");
        }

        public String getStringData() {
            // The data is in the stream
            return null;
        }

        public void setStringData(String string) {
            throw new UnsupportedOperationException("Can't set string data on resolver LSInput");
        }

        public String getSystemId() {
            return systemId;
        }

        public void setSystemId(String string) {
            throw new UnsupportedOperationException("Can't set system ID on resolver LSInput");
        }

        public String getPublicId() {
            return publicId;
        }

        public void setPublicId(String string) {
            throw new UnsupportedOperationException("Can't set public ID on resolver LSInput");
        }

        public String getBaseURI() {
            return rsrc.uri().toString();
        }

        public void setBaseURI(String string) {
            throw new UnsupportedOperationException("Can't set base URI on resolver LSInput");
        }

        public String getEncoding() {
            return null; // Unknown
        }

        public void setEncoding(String string) {
            throw new UnsupportedOperationException("Can't set encoding on resolver LSInput");
        }

        public boolean getCertifiedText() {
            return false;
        }

        public void setCertifiedText(boolean b) {
            throw new UnsupportedOperationException("Can't set certified text on resolver LSInput");
        }
    }
}
