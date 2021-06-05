package org.xmlresolver.catalog.query;

import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.EntryCatalog;

import java.net.URI;
import java.util.List;

public class QueryDelegateUri extends QueryUri {
    public QueryDelegateUri(String uri, String nature, String purpose, List<URI> catalogs) {
        super(uri, nature, purpose, catalogs);
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
