package org.xmlresolver.catalog.query;

import org.xmlresolver.catalog.entry.EntryCatalog;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Query for delegate public entries.
 */
public class QueryDelegatePublic extends QueryPublic {
    protected final List<URI> catalogs;

    /**
     * QueryDelegatePublic constructor.
     * <p>Represents the delegated catalogs that match the system and public identifiers provided.</p>
     * @param systemId The system identifier.
     * @param publicId The public identifier.
     * @param catalogs The matching catalogs.
     */
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
