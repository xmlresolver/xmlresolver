package org.xmlresolver.catalog.query;

import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryDocument;

public class QueryDocument extends QueryCatalog {
    @Override
    protected QueryResult lookup(CatalogManager manager, EntryCatalog catalog) {
        // <document>
        for (Entry raw : catalog.entries(Entry.Type.DOCUMENT)) {
            EntryDocument entry = (EntryDocument) raw;
            return new QueryResult(entry.uri);
        }

        return QueryResult.EMPTY_RESULT;
    }

}
