package org.xmlresolver.catalog.entry;

import org.xmlresolver.utils.URIUtils;

import java.net.URI;

public class EntrySystemSuffix extends Entry {
    public final String systemIdSuffix;
    public final URI uri;

    public EntrySystemSuffix(URI baseURI, String id, String suffix, String uri) {
        super(baseURI, id);
        this.systemIdSuffix = suffix;
        this.uri = URIUtils.resolve(baseURI, uri);
    }

    @Override
    public Type getType() {
        return Type.SYSTEM_SUFFIX;
    }
}
