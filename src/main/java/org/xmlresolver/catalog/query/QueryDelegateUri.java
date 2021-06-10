package org.xmlresolver.catalog.query;

import org.xmlresolver.catalog.entry.EntryCatalog;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class QueryDelegateUri extends QueryUri {
    protected final List<URI> catalogs;

    public QueryDelegateUri(String uri, String nature, String purpose, List<URI> catalogs) {
        super(uri, nature, purpose);
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
