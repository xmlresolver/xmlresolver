package org.xmlresolver.catalog.query;

import org.xmlresolver.catalog.entry.EntryCatalog;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Query for delegated URI entries.
 */
public class QueryDelegateUri extends QueryUri {
    protected final List<URI> catalogs;

    /**
     * QueryDelegateUri constructor.
     * <p>Represents the delegated catalogs that match the system identifier provided.</p>
     * @param uri The system identifier.
     * @param nature The RDDL nature.
     * @param purpose The RDDL purpose.
     * @param catalogs The catalogs.
     */
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
