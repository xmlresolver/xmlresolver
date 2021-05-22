package org.xmlresolver.catalog.query;

import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.EntryCatalog;

import java.net.URI;
import java.util.List;

public class QueryDelegatePublic extends QueryPublic {
    public QueryDelegatePublic(String systemId, String publicId, List<URI> catalogs) {
        super(systemId, publicId, catalogs);
    }

    @Override
    public boolean resolved() {
        return true;
    }

    @Override
    public QueryResult search(CatalogManager manager) {
        while (!catalogs.isEmpty()) {
            EntryCatalog catalog = manager.loadCatalog(catalogs.remove(0));
            boolean done = false;
            QueryResult result = lookup(manager, catalog);
            while (!done) {
                if (result.resolved()) {
                    return result;
                }
                if (result.query()) {
                    result = manager.search((QueryCatalog) result);
                } else {
                    done = true;
                }
            }
        }

        // If the delegated search fails, do not continue from where delegation started
        return QueryResult.FINAL_RESULT;
    }
}
