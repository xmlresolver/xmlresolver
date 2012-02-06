/*
 * ResourceResolver.java
 *
 * Created on December 29, 2006, 7:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xmlresolver.helpers.DOMUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.methods.GetMethod;

/** Resolves resources in the catalog.
 *
 */
public class ResourceResolver {
    private static Logger logger = Logger.getLogger("org.xmlresolver");
    
    // the static catalog is initialized lazily. Maybe it is not neede.
    private static Catalog staticCatalog = null;
    
    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder builder = null;
    
    static {
      factory.setNamespaceAware(true);
      factory.setValidating(false);
      try {
        builder = factory.newDocumentBuilder();
      } catch (ParserConfigurationException e) {
        // nop
      }
    }
    
    private Catalog catalog = null;
    private ResourceCache cache = null;
    
    private static synchronized Catalog getStaticCatalog() {
      if (staticCatalog == null) {
        staticCatalog = new Catalog();
      }
      return staticCatalog;
    }
    
    /**
     * Creates a new instance of ResourceResolver.
     *
     * <p>By default, a static catalog initialized using the default properties is used by all ResourceResolvers.</p>
     */
    public ResourceResolver() {
        init(getStaticCatalog());
    }

    /**
     * Creates a new instance of ResourceResolver with the specified catalog.
     *
     * @param catalog The {@link Catalog} to use.
     */
    public ResourceResolver(Catalog catalog) {
        init(catalog);
    }

    /**
     * Sets the entity resolver for the underlying parser.
     *
     * <p>If necessary, the {@link #resolveNamespaceURI} method will attempt to parse the namespace
     * document looking for RDDL markup. This method lets you specify the entity resolver that
     * should be used for this parse.</p>
     *
     * <p>In the common case, the {@link Resolver} automatically makes itself the resolver for
     * any documents parsed by the <code>ResourceResolver</code> that it uses.</p>
     *
     * @param resolver The EntityResolver object.
     */
    public void setEntityResolver(EntityResolver resolver) {
        builder.setEntityResolver(resolver);
    }
    
    private void init(Catalog catalog) {
        this.catalog = catalog;
        cache = catalog.cache();
    }

    /** Returns the {@link Catalog} used by this ResourceResolver. */
    public Catalog getCatalog() {
        return catalog;
    }

    private Resource streamResult(CatalogResult resolved) {
        try {
            if (resolved.cached()) {
                return new Resource(resolved.body(), resolved.externalURI(), resolved.contentType());
            } else {
                return new Resource(resolved.body(), resolved.uri(), resolved.contentType());
            }
        } catch (MalformedURLException mue) {
            return null;
        } catch (IOException ioe) {
            return null;
        }
    }    
    
    private Resource cacheStreamURI(String resolved) {
        URL url = null;
        boolean suppressCT = false;

        try {
            URI uri = new URI(resolved);
            url = uri.toURL();

            String absuriString = url.toString();
            /* FIXME: DO THIS OR NOT!?!?
            if (absuriString.startsWith("file:/") && !absuriString.startsWith("file:///")) {
                absuriString = "file:///" + absuriString.substring(6);
            }
             */

            // don't report contentType for file: URIs; it's no better a guess now than later
            suppressCT = absuriString.startsWith("file:");

            int statusCode = 0;
            InputStream stream = null;
            String contentType = null;
            String rediruriString = absuriString;

            if (absuriString.startsWith("http:") || absuriString.startsWith("https:")) {
                // Use Apache HttpClient
                HttpClient client = new HttpClient();
                GetMethod get = new GetMethod(absuriString);
                // Provide custom retry handler is necessary
                get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                        new DefaultHttpMethodRetryHandler(3, false));
                statusCode = client.executeMethod(get);

                Header contentTypeHeader = get.getResponseHeader("Content-Type");
                HeaderElement[] elems = contentTypeHeader.getElements();
                if (elems == null || elems.length == 0) {
                    // This should never happen
                    contentType = "application/octet-stream";
                } else {
                    contentType = elems[0].getName();
                }

                if (statusCode == 200) {
                    stream = get.getResponseBodyAsStream();
                    rediruriString = get.getURI().toString();
                }
            } else {
                URLConnection connection = url.openConnection();
                connection.connect();
                contentType = connection.getContentType();
                stream = connection.getInputStream();
                statusCode = 200;
            }

            if (statusCode == 200) {
                if (cache != null && catalog.cacheSchemeURI(getScheme(absuriString)) && cache.cacheURI(absuriString)) {
                    String localName = cache.addURI(absuriString, suppressCT ? null: contentType, stream, rediruriString);
                    File localFile = new File(localName);
                    InputStream result = new FileInputStream(localFile);
                    return new Resource(result, rediruriString);
                } else {
                    return new Resource(stream, rediruriString);
                }
            } else {
                return null;
            }
        } catch (URISyntaxException use) {
            return null;
        } catch (IOException ioe) {
            return null;
        }
    }    
    
    private Resource cacheStreamNamespaceURI(String resolved, String nature, String purpose) {
        URL url = null;
        boolean suppressCT = false;

        try {
            URI uri = new URI(resolved);
            url = uri.toURL();

            String absuriString = url.toString();

            /* FIXME: DO THIS OR NOT!?!?
            if (absuriString.startsWith("file:/") && !absuriString.startsWith("file:///")) {
                absuriString = "file:///" + absuriString.substring(6);
            }
             */
            
            // don't report contentType for file: URIs; it's no better a guess now than later
            suppressCT = !absuriString.startsWith("file:");

            URLConnection connection = url.openConnection();
            connection.connect();

            if (cache != null && catalog.cacheSchemeURI(getScheme(absuriString)) && cache.cacheURI(absuriString)) {
                String localName = cache.addNamespaceURI(absuriString, nature, purpose,
                                                         suppressCT ? null : connection.getContentType(),
                                                         connection.getInputStream());
                File localFile = new File(localName);
                InputStream result = new FileInputStream(localFile);
                return new Resource(result, absuriString);
            } else {            
                return new Resource(connection.getInputStream(), absuriString);
            }
        } catch (URISyntaxException use) {
            return null;
        } catch (IOException ioe) {
            return null;
        }
    }    
    
    private Resource cacheStreamSystem(String resolved, String publicId) {
        URL url = null;
        boolean suppressCT = false;

        try {
            URI uri = new URI(resolved);
            url = uri.toURL();

            String absuriString = url.toString();

            /* FIXME: DO THIS OR NOT!?!?
            if (absuriString.startsWith("file:/") && !absuriString.startsWith("file:///")) {
                absuriString = "file:///" + absuriString.substring(6);
            }
             */

            // don't report contentType for file: URIs; it's no better a guess now than later
            suppressCT = !absuriString.startsWith("file:");
            
            URLConnection connection = url.openConnection();
            connection.connect();

            if (cache != null && catalog.cacheSchemeURI(getScheme(absuriString)) && cache.cacheURI(absuriString)) {
                String contentType = suppressCT ? null : connection.getContentType();
                InputStream is = connection.getInputStream();
                String localName = cache.addSystem(absuriString, publicId, contentType, is);
                InputStream result = new FileInputStream(new File(localName));
                return new Resource(result, absuriString);
            } else {                        
                return new Resource(connection.getInputStream(), absuriString);
            }
        } catch (URISyntaxException use) {
            return null;
        } catch (IOException ioe) {
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
     * <p>Note that the original URI is provided as the base URI in the returned {@link Resource}. This
     * means that relative URIs in the locally cached resource will still resolve correctly.</p>
     *
     * @param href The URI of the resource
     * @param base The base URI against which <code>href</code> should be made absolute, if necessary.
     *
     * @return The resource that represents the URI.
     */
    public Resource resolveURI(String href, String base) {
        logger.fine("resolveURI(" + href + "," + base + ")");
        String uri = href;
        CatalogResult resolved = catalog.lookupURI(uri);
        boolean skipCache = false;
        
        if (resolved == null && base != null) {
            try {
                URI auri = new URI(base);
                auri = auri.resolve(new URI(uri));
                uri = auri.toURL().toString();
                resolved = catalog.lookupURI(uri);
            } catch (URISyntaxException use) {
                resolved = null;
            } catch (MalformedURLException mue) {
                resolved = null;
            } catch (IllegalArgumentException iae) {
                // In case someone calls resolveURI("../some/local/path", null)
                resolved = null;
                skipCache = true;
            }
        }

        if (resolved == null) {
            if (skipCache) {
                return null;
            } else {
                return cacheStreamURI(uri);
            }
        }

        if (resolved.expired()) {
            return cacheStreamURI(uri);
        }

        return streamResult(resolved);
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
        logger.fine("resolvePublic(" + systemId + "," + publicId + ")");
        CatalogResult resolved = catalog.lookupPublic(systemId, publicId);
        if (resolved == null || resolved.expired()) {
            return cacheStreamSystem(systemId, publicId);
        } else {
            return streamResult(resolved);
        }
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
        logger.fine("resolveEntity(" + name + "," + systemId + "," + publicId + ")");
        CatalogResult resolved = catalog.lookupEntity(name, systemId, publicId);
        if (resolved == null || resolved.expired()) {
            return cacheStreamSystem(systemId, publicId);
        } else {
            return streamResult(resolved);
        }
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
        logger.fine("resolveNamespaceURI(" + uri + "," + nature + "," + purpose + ")");
        CatalogResult resolved = catalog.lookupNamespaceURI(uri, nature, purpose);
        if (resolved != null) {
            return streamResult(resolved);
        }
        
        // Suppose this is an XHTML RDDL document?
        if (builder == null) {
            return streamResult(new CatalogResult(uri, uri));
        }

        // Cache the underlying URI
        Resource nsResult = resolveURI(uri, null);

        String rddlURI = null;

        try {
            Document doc;
            synchronized (builder) {
              doc = builder.parse(nsResult.uri());
            }
            NodeList rsrcs = doc.getElementsByTagNameNS(Catalog.NS_RDDL, "resource");
            for (int pos = 0; rddlURI == null && pos < rsrcs.getLength(); pos++) {
                Element rsrc = (Element) rsrcs.item(pos);
                String rnature = DOMUtils.attr(rsrc, Catalog.NS_XLINK, "role");
                String rpurpose = DOMUtils.attr(rsrc, Catalog.NS_XLINK, "arcrole");
                String rhref = DOMUtils.attr(rsrc, Catalog.NS_XLINK, "href");
                
                if (rnature != null && rnature.equals(nature)
                    && rpurpose != null && rpurpose.equals(purpose)
                    && rhref != null) {
                    rddlURI = DOMUtils.makeAbsolute(rsrc, rhref, uri);
                }
            }
            
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        if (rddlURI == null) {
            return streamResult(new CatalogResult(uri, uri));
        }

        //return streamResult(rddlURI);
        return cacheStreamNamespaceURI(rddlURI, nature, purpose);
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
        logger.fine("resolveDoctype(" + name + ")");
        CatalogResult resolved = catalog.lookupDoctype(name, null, null);
        if (resolved == null) {
            return null;
        } else {
            return streamResult(resolved);
        }
    }

    private String getScheme(String uri) {
        int pos = uri.indexOf(":");
        if (pos >= 0) {
            return uri.substring(0, pos);
        } else {
            return null;
        }
    }
}
