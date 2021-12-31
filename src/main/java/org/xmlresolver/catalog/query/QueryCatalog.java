package org.xmlresolver.catalog.query;

import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.EntryCatalog;

import java.net.URI;
import java.util.ArrayList;

public abstract class QueryCatalog extends QueryResult {
    public QueryCatalog() {
        super();
    }

    @Override
    public boolean query() {
        return true;
    }

    public QueryResult search(CatalogManager manager) {
        ArrayList<URI> catalogs = new ArrayList<>(manager.catalogs());
        while (!catalogs.isEmpty()) {
            EntryCatalog catalog = manager.loadCatalog(catalogs.remove(0));
            boolean done = false;
            QueryCatalog query = this;
            while (!done) {
                QueryResult result = query.lookup(manager, catalog);
                done = result.resolved();
                catalogs = result.updatedCatalogSearchList(catalog, catalogs);

                if (result.query()) {
                    query = (QueryCatalog) result;
                } else {
                    done = true;
                    if (result.resolved()) {
                        return result;
                    }
                }
            }
        }
        return QueryResult.EMPTY_RESULT;
    }

    protected abstract QueryResult lookup(CatalogManager manager, EntryCatalog catalog);
}
