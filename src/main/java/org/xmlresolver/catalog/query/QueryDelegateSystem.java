package org.xmlresolver.catalog.query;

import org.xmlresolver.catalog.entry.EntryCatalog;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Query for delegated system entries.
 */
public class QueryDelegateSystem extends QuerySystem {
    protected final List<URI> catalogs;

    /**
     * QueryDelegateSystem constructor.
     * <p>Represents the delegated catalogs that match the system identifier provided.</p>
     * @param systemId The system identifier.
     * @param catalogs The catalogs.
     */
    public QueryDelegateSystem(String systemId, List<URI> catalogs) {
        super(systemId);
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