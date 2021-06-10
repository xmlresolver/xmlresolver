package org.xmlresolver.catalog.query;

import org.xmlresolver.catalog.entry.EntryCatalog;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class QueryDelegatePublic extends QueryPublic {
    protected final List<URI> catalogs;

    public QueryDelegatePublic(String systemId, String publicId, List<URI> catalogs) {
        super(systemId, publicId);
        this.catalogs = catalogs;
    }

    @Override
    public boolean resolved() {
        return true;
    }

    protected ArrayList<URI> updatedCatalogSearchList(EntryCatalog catalog, ArrayList<URI> oldCatalogs) {
        // Delegation replaces the catalog list
        return new ArrayList<>(catalogs);
    }
}
