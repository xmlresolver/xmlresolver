package org.xmlresolver.catalog.query;

import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.EntryCatalog;

import java.net.URI;
import java.util.ArrayList;

/**
 * Query an XML Catalog.
 * <p>This is an abstract class that implements the mechanics of catalog searching.
 * Concrete implementations of this class implement a "lookup" method to perform a specific type of search.</p>
 */
public abstract class QueryCatalog extends QueryResult {
    /**
     * QueryCatalog constructor.
     */
    public QueryCatalog() {
        super();
    }

    /**
     * Is this a query?
     * @return true, it is.
     */
    @Override
    public boolean query() {
        return true;
    }

    /**
     * Perform the query by searching through all of the catalogs provided by the catalog manager.
     * @param manager The catalog manager that provides catalogs for this query.
     * @return The results.
     */
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
