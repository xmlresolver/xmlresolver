package org.xmlresolver.catalog.query;

import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryNextCatalog;

import java.net.URI;
import java.util.ArrayList;

/**
 * The result of querying a catalog.
 */
public class QueryResult {
    /** An empty result, no matching entries were found. */
    public static final QueryResult EMPTY_RESULT = new QueryResult();
    private final boolean resolved;
    private URI result = null;

    protected QueryResult() {
        resolved = false;
    }

    /**
     * Create a result with a given URI.
     * @param uri The URI.
     */
    public QueryResult(URI uri) {
        resolved = true;
        result = uri;
    }

    /**
     * Is this a query?
     * @return false, it is not.
     */
    public boolean query() {
        return false;
    }

    /**
     * Does this result represent a successful resolution?
     * @return True, if and only if the result is a successful resolution.
     */
    public boolean resolved() {
        return resolved;
    }

    /**
     * The URI of the resolution.
     * @return The URI.
     */
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