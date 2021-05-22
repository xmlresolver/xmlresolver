package org.xmlresolver.cache;

import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryPublic;
import org.xmlresolver.catalog.entry.EntrySystem;
import org.xmlresolver.catalog.entry.EntryUri;
import org.xmlresolver.utils.PublicId;

import java.io.File;
import java.net.URI;

public class CacheEntry {
    public final Entry entry;
    public final URI uri;
    public final File file;
    public final long time;
    public boolean expired;

    protected CacheEntry(EntryUri entry, long time) {
        this.entry = entry;
        this.uri = entry.baseURI.resolve(entry.name);
        this.file = new File(entry.uri.getPath());
        this.time = time;
    }

    protected CacheEntry(EntrySystem entry, long time) {
        this.entry = entry;
        this.uri = entry.baseURI.resolve(entry.systemId);
        this.file = new File(entry.uri.getPath());
        this.time = time;
    }

    protected CacheEntry(EntryPublic entry, long time) {
        this.entry = entry;
        this.uri = PublicId.encodeURN(entry.publicId);
        this.file = new File(entry.uri.getPath());
        this.time = time;
    }

    public String etag() {
        return entry.getProperty("etag");
    }
    public String contentType() {
        return entry.getProperty("contentType");
    }
    public URI location() {
        if (entry.getProperty("redir") != null) {
            return uri.resolve(entry.getProperty("redir"));
        } else {
            return uri;
        }
    }
}
