package org.xmlresolver.catalog.entry;

import org.xmlresolver.utils.URIUtils;

import java.net.URI;

public class EntryUriSuffix extends Entry {
    public final String uriSuffix;
    public final URI uri;

    public EntryUriSuffix(URI baseURI, String id, String suffix, String uri) {
        super(baseURI, id);
        this.uriSuffix = suffix;
        this.uri = URIUtils.resolve(baseURI, uri);
    }

    @Override
    public Type getType() {
        return Type.URI_SUFFIX;
    }
}
