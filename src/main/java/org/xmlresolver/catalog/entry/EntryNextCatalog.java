package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

public class EntryNextCatalog extends Entry {
    public final URI catalog;

    public EntryNextCatalog(ResolverConfiguration config, URI baseURI, String id, String catalog) {
        super(config, baseURI, id);
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
