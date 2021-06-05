package org.xmlresolver.catalog.query;

import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.EntryCatalog;

import java.net.URI;

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

    public QueryResult search(CatalogManager manager) {
        return this;
    }
}