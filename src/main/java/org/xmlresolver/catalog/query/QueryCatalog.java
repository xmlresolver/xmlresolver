package org.xmlresolver.catalog.query;

import org.jetbrains.annotations.NotNull;
import org.xmlresolver.ResolverLogger;
import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryNextCatalog;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public abstract class QueryCatalog extends QueryResult {
    protected static ResolverLogger logger = new ResolverLogger(QueryCatalog.class);
    protected final ArrayList<URI> catalogs = new ArrayList<>();

    public QueryCatalog(List<URI> catalogs) {
        super();
        this.catalogs.addAll(catalogs);
    }

    @Override
    public boolean query() {
        return true;
    }

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
        return QueryResult.EMPTY_RESULT;
    }

    public List<URI> nextCatalogs(EntryCatalog catalog) {
        // <nextCatalog>
        ArrayList<URI> next = new ArrayList<>();
        for (Entry raw : catalog.entries(Entry.Type.NEXT_CATALOG)) {
            next.add(((EntryNextCatalog) raw).catalog);
        }
        return next;
    }

    protected abstract @NotNull QueryResult lookup(CatalogManager manager, EntryCatalog catalog);
}
