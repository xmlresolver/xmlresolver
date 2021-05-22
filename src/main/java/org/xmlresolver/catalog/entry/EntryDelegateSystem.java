package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntryDelegateSystem extends Entry {
    public final String systemIdStart;
    public final URI catalog;

    public EntryDelegateSystem(URI baseURI, String id, String start, String catalog) {
        super(baseURI, id);
        systemIdStart = start;
        this.catalog = baseURI.resolve(catalog);
    }

    @Override
    public Type getType() {
        return Type.DELEGATE_SYSTEM;
    }
}
