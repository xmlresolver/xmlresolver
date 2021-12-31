package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

public class EntryUriSuffix extends Entry {
    public final String uriSuffix;
    public final URI uri;

    public EntryUriSuffix(ResolverConfiguration config, URI baseURI, String id, String suffix, String uri) {
        super(config, baseURI, id);
        this.uriSuffix = suffix;
        this.uri = URIUtils.resolve(baseURI, uri);
    }

    @Override
    public Type getType() {
        return Type.URI_SUFFIX;
    }

    @Override
    public String toString() {
        return "uriSuffix " + uriSuffix + Entry.rarr + uri;
    }
}
