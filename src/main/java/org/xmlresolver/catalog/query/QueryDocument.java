package org.xmlresolver.catalog.query;

import org.jetbrains.annotations.NotNull;
import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryDocument;

import java.net.URI;
import java.util.List;

public class QueryDocument extends QueryCatalog {
    public QueryDocument(List<URI> catalogs) {
        super(catalogs);
    }

    @Override
    protected @NotNull QueryResult lookup(CatalogManager manager, EntryCatalog catalog) {
        // <document>
        for (Entry raw : catalog.entries(Entry.Type.DOCUMENT)) {
            EntryDocument entry = (EntryDocument) raw;
            return new QueryResult(entry.uri);
        }

        // <nextCatalog>
        List<URI> next = nextCatalogs(catalog);
        if (!next.isEmpty()) {
            return new QueryDocument(next);
        }

        return QueryResult.EMPTY_RESULT;
    }
}
