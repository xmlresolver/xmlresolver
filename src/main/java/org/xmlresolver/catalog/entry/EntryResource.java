package org.xmlresolver.catalog.entry;

import java.net.URI;

// A catalog entry that represents a resource that might be cached on disk.
public abstract class EntryResource extends Entry {
    public final URI uri;

    public EntryResource(URI baseURI, String id, String uri) {
        super(baseURI, id);
        this.uri = baseURI.resolve(uri);
    }
}

