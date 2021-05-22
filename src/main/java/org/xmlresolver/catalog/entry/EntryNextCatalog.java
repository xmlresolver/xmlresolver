package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntryNextCatalog extends Entry {
    public final URI catalog;

    public EntryNextCatalog(URI baseURI, String id, String catalog) {
        super(baseURI, id);
        this.catalog = baseURI.resolve(catalog);
    }

    @Override
    public Type getType() {
        return Type.NEXT_CATALOG;
    }
}
