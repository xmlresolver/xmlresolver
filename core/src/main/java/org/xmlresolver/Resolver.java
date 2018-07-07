/*
 * Resolver.java
 *
 * Created on January 2, 2007, 9:02 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/** Implements {@link org.xml.sax.EntityResolver}, {@link javax.xml.transform.URIResolver},
 * and {@link NamespaceResolver}.
 */
public class Resolver implements URIResolver, EntityResolver, EntityResolver2, NamespaceResolver, LSResourceResolver {
    private static Logger logger = LoggerFactory.getLogger(Resolver.class);
    ResourceResolver resolver = null;
    
    /** Creates a new instance of Resolver.
     *
     * The default resolver is a new ResourceResolver that uses a static catalog shared by all threads.
     */
    public Resolver() {
        resolver = new ResourceResolver();
        resolver.setEntityResolver(this);
    }

    /** Creates a new instance of a Resolver.
     *
     * Creates a resolver using a specific Catalog.
     *
     * @param catalog The catalog to use.
     */
    public Resolver(Catalog catalog) {
        resolver = new ResourceResolver(catalog);
        resolver.setEntityResolver(this);
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

    /** Get the Catalog used by this resolver.
     *
     * @return The underlying catalog.
     */
    public Catalog getCatalog() {
        return resolver.getCatalog();
    }

    public Resource resolveResource(String href, String base) {
        if (href == null || "".equals(href)) {
            href = base;
            base = null;
        }
        logger.trace("resolveResource(" + href + "," + base + ")");
        Resource rsrc = resolver.resolveURI(href, base);

        logger.trace(href
                + (base == null ? "" : " (" + base + ")") + " => "
                + (rsrc == null ? href : rsrc.uri()));

        return rsrc;
    }

    /** Implements the {@link javax.xml.transform.URIResolver} interface. */
    public Source resolve(String href, String base) throws TransformerException {
        Resource rsrc = resolveResource(href, base);
        if (rsrc == null) {
            return null;
        } else {
            SAXSource source = new SAXSource(new InputSource(rsrc.body()));
            source.setSystemId(rsrc.uri());
            return source;
        }
    }

    /** Implements the {@link NamespaceResolver} interface. */
    public Source resolveNamespace(String uri, String nature, String purpose) throws TransformerException {
        logger.trace("resolveNamespace(" + uri + "," + nature + "," + purpose + ")");
        Resource rsrc = resolver.resolveNamespaceURI(uri, nature, purpose);

        logger.trace(uri + " (" + nature + "," + purpose + ") => "
                + (rsrc == null ? uri : rsrc.uri()));

        if (rsrc == null) {
            return null;
        } else {
            SAXSource source = new SAXSource(new InputSource(rsrc.body()));
            source.setSystemId(rsrc.uri());
            return source;
        }
    }

    /** Implements the {@link org.xml.sax.EntityResolver} interface. */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        logger.trace("resolveEntity(" + publicId + "," + systemId + ")");
        Resource rsrc = resolver.resolvePublic(systemId, publicId);
        
        logger.trace(systemId
                    + (publicId == null ? "" : " (" + publicId + ")") + " => "
                    + (rsrc == null ? systemId : rsrc.uri()));

        if (rsrc == null) {
            return null;
        } else {
            InputSource source = new InputSource(rsrc.body());
            source.setSystemId(rsrc.uri());
            return source;
        }
    }

    /** Implements the {@link org.xml.sax.ext.EntityResolver2} interface. */
    public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
        logger.trace("getExternalSubset(" + name + "," + baseURI + ")");
        Resource rsrc = resolver.resolveDoctype(name);

        logger.trace(baseURI
                + (name == null ? "" : " (" + name + ")") + " => "
                + (rsrc == null ? baseURI : rsrc.uri()));

        if (rsrc == null) {
            return null;
        } else {
            InputSource source = new InputSource(rsrc.body());
            source.setSystemId(rsrc.uri());
            return source;
        }
    }

    /** Implements the {@link org.xml.sax.ext.EntityResolver2} interface. */
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
        // We can do better than this, but for now...just get an absolute URI
        String absSystem = null;
        
        if (baseURI != null) {
            try {
                URI auri = new URI(baseURI);
                auri = auri.resolve(new URI(systemId));
                absSystem = auri.toURL().toString();
            } catch (URISyntaxException use) {
                // nop;
            } catch (MalformedURLException mue) {
                // nop;
            } catch (IllegalArgumentException iae) {
                // In case someone uses baseURI=null, systemId="../some/local/path"
                // nop;
            }
        } else {
          
            try {
              URL url = new URL(systemId);
              absSystem = url.toExternalForm();
            } catch (MalformedURLException e) {
              // noop
            }
          
        }

        logger.trace("resolveEntity(" + name + "," + publicId + "," + absSystem + ")");
        Resource rsrc = resolver.resolveEntity(name, absSystem, publicId);

        logger.trace(absSystem
                + (publicId == null ? "" : " (" + publicId + ")") + " => "
                + (rsrc == null ? absSystem : rsrc.uri()));

        if (rsrc == null) {
            return null;
        } else {
            InputSource source = new InputSource(rsrc.body());
            source.setSystemId(rsrc.uri());
            return source;
        }
    }

    /** Implements the {@link org.w3c.dom.ls.LSResourceResolver} interface. */
    public LSInput resolveResource(String type, String namespace, String publicId, String systemId, String baseURI) {
        // We can do better than this, but for now...just get an absolute URI
        String absSystem = null;
        
        if (baseURI != null) {
            try {
                URI auri = new URI(baseURI);
                auri = auri.resolve(new URI(systemId));
                absSystem = auri.toURL().toString();
            } catch (URISyntaxException use) {
                // nop;
            } catch (MalformedURLException mue) {
                // nop;
            } catch (IllegalArgumentException iae) {
                // In case someone uses baseURI=null, systemId="../some/local/path"
                // nop;
            }
        }

        Resource rsrc = null;
        if ("http://www.w3.org/TR/REC-xml".equals(type)) {
            logger.trace("resolveResource(XML," + publicId + "," + absSystem + ")");
            rsrc = resolver.resolvePublic(absSystem, publicId);
        } else if ("http://www.w3.org/2001/XMLSchema".equals(type)) {
            logger.trace("resolveResource(XMLSchema," + namespace + "," + absSystem + ")");
            rsrc = resolver.resolveURI(absSystem, baseURI);
        } else {
            return null;
        }

        logger.trace(absSystem
                + (publicId == null ? "" : " (" + publicId + ")")
                + (namespace == null ? "" : " (" + namespace + ")") + " => "
                + (rsrc == null ? absSystem : rsrc.uri()));

        if (rsrc == null) {
            return null;
        } else {
            return new ResolverLSInput(rsrc, publicId);
        }
    }

    class ResolverLSInput implements LSInput {
        Resource rsrc = null;
        String publicId = null;
        
        public ResolverLSInput(Resource rsrc, String publicId) {
            this.rsrc = rsrc;
            this.publicId = publicId;
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
            return rsrc.uri();
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
            return rsrc.uri();
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
