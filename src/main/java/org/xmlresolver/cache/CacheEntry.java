package org.xmlresolver.cache;

import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryPublic;
import org.xmlresolver.catalog.entry.EntrySystem;
import org.xmlresolver.catalog.entry.EntryUri;
import org.xmlresolver.utils.PublicId;

import java.io.File;
import java.net.URI;

/** An entry in the cache.
 *
 * Each entry in the cache is represented by a CacheEntry object that identifies the URI, local file,
 * and other details about the entry. This object represents the data, the actual cached resource.
 * It contains a pointer back to the XML Catalog entry for the resource.
 */

public class CacheEntry {
    /** The XML Catalog entry for this cached resource. */
    public final Entry entry;
    /** The URI of the resource. */
    public final URI uri;
    /** The local file where this resource is cached. */
    public final File file;
    /** The date when the resource was cached. */
    public final long time;
    /** Is this resource expired? */
    public boolean expired;

    /** Cache a URI.
     *
     * @param entry the catalog entry
     * @param time the timestamp.
     */
    protected CacheEntry(EntryUri entry, long time) {
        this.entry = entry;
        this.uri = entry.baseURI.resolve(entry.name);
        this.file = new File(entry.uri.getPath());
        this.time = time;
    }

    /** Cache a system entry.
     *
     * @param entry the catalog entry
     * @param time the timestamp.
     */
    protected CacheEntry(EntrySystem entry, long time) {
        this.entry = entry;
        this.uri = entry.baseURI.resolve(entry.systemId);
        this.file = new File(entry.uri.getPath());
        this.time = time;
    }

    /** Cache a public entry.
     *
     * @param entry the catalog entry
     * @param time the timestamp.
     */
    protected CacheEntry(EntryPublic entry, long time) {
        this.entry = entry;
        this.uri = PublicId.encodeURN(entry.publicId);
        this.file = new File(entry.uri.getPath());
        this.time = time;
    }

    /** The resource etag.
     *
     * See https://en.wikipedia.org/wiki/HTTP_ETag
     *
     * @return The resource etag, or null if no etag is available.
     */
    public String etag() {
        return entry.getProperty("etag");
    }

    /** The resource content type.
     *
     * @return The content type, or null if the content type is unknown.
     */
    public String contentType() {
        return entry.getProperty("contentType");
    }

    /** The location
     *
     * If the resource was redirected, this method returns the redirected location.
     *
     * @return The redirected location or the original URI if no redirection occurred.
     */
    public URI location() {
        if (entry.getProperty("redir") != null) {
            return uri.resolve(entry.getProperty("redir"));
        } else {
            return uri;
        }
    }

    /** A string representation for the entry. */
    @Override
    public String toString() {
        return file.getAbsolutePath();
    }
}
