package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntryDelegateUri extends Entry {
    public final String uriStart;
    public final URI catalog;

    public EntryDelegateUri(URI baseURI, String id, String start, String catalog) {
        super(baseURI, id);
        uriStart = start;
        this.catalog = baseURI.resolve(catalog);
    }

    @Override
    public Type getType() {
        return Type.DELEGATE_URI;
    }
}
