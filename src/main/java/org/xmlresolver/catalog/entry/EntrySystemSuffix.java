package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntrySystemSuffix extends Entry {
    public final String systemIdSuffix;
    public final URI uri;

    public EntrySystemSuffix(URI baseURI, String id, String suffix, String uri) {
        super(baseURI, id);
        this.systemIdSuffix = suffix;
        this.uri = baseURI.resolve(uri);
    }

    @Override
    public Type getType() {
        return Type.SYSTEM_SUFFIX;
    }
}
