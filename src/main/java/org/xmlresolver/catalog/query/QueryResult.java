package org.xmlresolver.catalog.query;

import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryNextCatalog;

import java.net.URI;
import java.util.ArrayList;

public class QueryResult {
    public static final QueryResult EMPTY_RESULT = new QueryResult();
    public static final QueryResult FINAL_RESULT = new QueryResult(null);
    private final boolean resolved;
    private URI result = null;

    protected QueryResult() {
        resolved = false;
    }

    public QueryResult(URI uri) {
        resolved = true;
        result = uri;
    }

    public boolean query() {
        return false;
    }

    public boolean resolved() {
        return resolved;
    }

    public URI uri() {
        return result;
    }

    protected ArrayList<URI> updatedCatalogSearchList(EntryCatalog catalog, ArrayList<URI> catalogs) {
        // <nextCatalog>
        ArrayList<URI> next = new ArrayList<>();
        for (Entry raw : catalog.entries(Entry.Type.NEXT_CATALOG)) {
            next.add(((EntryNextCatalog) raw).catalog);
        }
        next.addAll(catalogs);
        return next;
    }
}