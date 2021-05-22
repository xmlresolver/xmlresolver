package org.xmlresolver.catalog.query;

import org.jetbrains.annotations.NotNull;
import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryNotation;
import org.xmlresolver.utils.PublicId;

import java.net.URI;
import java.util.List;

public class QueryNotation extends QueryCatalog {
    public final String notationName;
    public final String systemId;
    public final String publicId;

    public QueryNotation(String notationName, String systemId, String publicId, List<URI> catalogs) {
        super(catalogs);
        this.notationName = notationName;
        this.systemId = systemId;
        this.publicId = publicId == null ? null : PublicId.normalize(publicId);
    }

    @Override
    protected @NotNull QueryResult lookup(CatalogManager manager, EntryCatalog catalog) {
        QueryResult result = manager.search(new QueryPublic(systemId, publicId, catalog));
        if (result.resolved()) {
            return result;
        }

        // <notation>
        for (Entry raw : catalog.entries(Entry.Type.NOTATION)) {
            EntryNotation entry = (EntryNotation) raw;
            if (entry.name.equals(notationName)) {
                return new QueryResult(entry.uri);
            }
        }

        // <nextCatalog>
        List<URI> next = nextCatalogs(catalog);
        if (!next.isEmpty()) {
            return new QueryNotation(notationName, systemId, publicId, next);
        }

        return QueryResult.EMPTY_RESULT;
    }
}
