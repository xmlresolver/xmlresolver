/*
 * CatalogResult.java
 *
 * Created on January 13, 2007, 7:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

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
    
    /**
     * Creates a new instance of CatalogResult
     */
    public CatalogResult(String origURI, String uri) {
        this.uri = uri;
        this.origURI = origURI;
    }
    
    /**
     * Creates a new instance of CatalogResult
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
     * <p>This is the absolute URI identified by the matching catalog entry.</p>
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
        InputStream body = null;

        URL url = new URL(uri);
        URLConnection connection = url.openConnection();
        connection.connect();
        body = connection.getInputStream();

        return body;
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
        if (origURI == null || cache == null || !origURI.startsWith("http:")) {
            return false;
        }

        return cache.expired(origURI, uri, entry);
    }
}
