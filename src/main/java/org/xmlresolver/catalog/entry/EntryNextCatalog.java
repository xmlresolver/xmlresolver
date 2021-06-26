package org.xmlresolver.catalog.entry;

import org.xmlresolver.utils.URIUtils;

import java.net.URI;

public class EntryNextCatalog extends Entry {
    public final URI catalog;

    public EntryNextCatalog(URI baseURI, String id, String catalog) {
        super(baseURI, id);
        this.catalog = URIUtils.resolve(baseURI, catalog);
    }

    @Override
    public Type getType() {
        return Type.NEXT_CATALOG;
    }

    @Override
    public String toString() {
        return "nextCatalog " + catalog;
    }
}
