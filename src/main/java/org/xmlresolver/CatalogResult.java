/*
 * CatalogResult.java
 *
 * Created on January 13, 2007, 7:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;

/** Represents the result of a catalog search.
 *
 * @author ndw
 */
public class CatalogResult {
    private static Logger logger = LoggerFactory.getLogger(CatalogResult.class);
    private String origURI = null;
    private String uri = null;
    private String contentType = null;
    private long cacheTime = -1;
    private ResourceCache cache = null;
    private Element entry = null;

    /** Creates a new instance of CatalogResult
     *
     * @param origURI The original URI
     * @param uri The catalog result for the original URI
     */
    public CatalogResult(String origURI, String uri) {
        this.uri = uri;
        this.origURI = origURI;
    }

    /** Creates a new instance of CatalogResult
     *
     * @param origURI The original URI
     * @param uri The catalog result for the original URI
     * @param entry The cagalog entry
     * @param cache The cache entry
     */
    public CatalogResult(String origURI, String uri, Element entry, ResourceCache cache) {
        this.uri = uri;
        this.origURI = origURI;
        this.entry = entry;

        if (entry.hasAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "time")) {
            cacheTime = Long.parseLong(entry.getAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "time"));
        }

        if (entry.hasAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "contentType")) {
            contentType = entry.getAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "contentType");
        }

        if (cacheTime >= 0) {
            this.cache = cache;
        }
    }

    /** Returns the URI of the catalog result.
     *
     * This is the absolute URI identified by the matching catalog entry.
     *
     * @return The URI.
     */
    public String uri() {
        return uri;
    }

    /** Returns the URI of the resource.
     *
     * <p>This is the URI associated with the external entity or URI that the
     * application attempted to access.</p>
     *
     * @return The URI.
     */
    public String externalURI() {
        return origURI;
    }

    /** Returns the MIME content type of the result.
     *
     * <p>This will often be <code>null</code> which corresponds to an unknown content type.
     * In general, it is only cached resources, and only some of them, that have a known
     * content type.</p>
     *
     * @return The content type.
     */
    public String contentType() {
        return contentType;
    }

    /** Indicates if the result came from the cache.
     *
     * @return True if and only if the resource came from the cache.
     */
    public boolean cached() {
        return cache != null;
    }

    /** Returns an input stream for the result.
     *
     * <p>The URI is opened and its {@link InputStream} returned.</p>
     *
     * @throws MalformedURLException if the catalog URI is somehow invalid.
     * @throws IOException if the URI cannot be opened for reading.
     *
     * @return The InputStream
     */
    public InputStream body() throws MalformedURLException, IOException {
        if (uri.startsWith("data:")) {
            // This is a little bit crude; see RFC 2397
            int pos = uri.indexOf(",");
            if (pos > 0) {
                String mediatype = uri.substring(0, pos);
                String data = uri.substring(pos + 1);
                if (mediatype.endsWith(";base64")) {
                    // Base64 decode it
                    return new ByteArrayInputStream(Base64.getDecoder().decode(data));
                } else {
                    // URL decode it
                    String charset = "UTF-8";
                    pos = mediatype.indexOf(";charset=");
                    if (pos > 0) {
                        charset = mediatype.substring(pos + 9);
                        pos = charset.indexOf(";");
                        if (pos >= 0) {
                            charset = charset.substring(0, pos);
                        }
                    }
                    data = URLDecoder.decode(data, charset);
                    return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
                }
            }
            return null;
        }

        if (uri.startsWith("classpath:")) {
            String path = uri.substring(10);
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            // The URI class throws exceptions if you attempt to manipulate
            // classpath: URIs. Fair enough, given their ad hoc invention
            // by the Spring framework. We're going to cheat a little bit here
            // and replace the classpath: URI with the actual URI of the resource
            // found (if one is found). That means downstream processes will
            // have a "useful" URI. It still might not work, due to class loaders and
            // such, but at least it won't immediately blow up.
            URL rsrc = getClass().getClassLoader().getResource(path);
            if (rsrc == null) {
                return null;
            } else {
                uri = rsrc.toString();
                try {
                    return rsrc.openStream();
                } catch (IOException ee) {
                    return null;
                }
            }
        }

        // I don't think anyone is ever going to use this, feature, but for the sake
        // of completeness...
        if (uri.startsWith("classpath*:")) {
            String path = uri.substring(11);
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            Enumeration<URL> urls = getClass().getClassLoader().getResources(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bytes = new byte[8192];
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                InputStream is = url.openStream();
                int len = is.read(bytes);
                while (len >= 0) {
                    baos.write(bytes, 0, len);
                    len = is.read(bytes);
                }
                is.close();
            }

            return new ByteArrayInputStream(baos.toByteArray());
        }

        URL url = new URL(uri);
        URLConnection connection = url.openConnection();
        connection.connect();
        return connection.getInputStream();
    }
    
    /** Attempts to determine if the local copy (represented by this result) is out of date.
     *
     * <p>Queries the underlying cache ({@link ResourceCache#expired}) to determine if the local URI
     * in this result is expired. If it is, the cache entry will be expired and the resource should be
     * requested and added back into the cache.</p>
     *
     * @return True if and only if the resource is known to be expired.
     */
    public boolean expired() {
        if (origURI == null || cache == null || !(origURI.startsWith("http:") || origURI.startsWith("https:"))) {
            return false;
        }

        return cache.expired(origURI, uri, entry);
    }

    @Override
    public String toString() {
       return "CatalogResult(" + origURI + "," + uri + ")";
    }
}
