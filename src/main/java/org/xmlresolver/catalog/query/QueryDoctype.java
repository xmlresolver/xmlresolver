package org.xmlresolver.catalog.query;

import org.jetbrains.annotations.NotNull;
import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryDoctype;
import org.xmlresolver.utils.PublicId;

import java.net.URI;
import java.util.List;

public class QueryDoctype extends QueryCatalog {
    public final String entityName;
    public final String systemId;
    public final String publicId;

    public QueryDoctype(String entityName, String systemId, String publicId, List<URI> catalogs) {
        super(catalogs);
        this.entityName = entityName;
        this.systemId = systemId;
        this.publicId = publicId == null ? null : PublicId.normalize(publicId);
    }

    @Override
    protected @NotNull QueryResult lookup(CatalogManager manager, EntryCatalog catalog) {
        QueryResult result = manager.search(new QueryPublic(systemId, publicId, catalog));
        if (result.resolved()) {
            return result;
        }

        // <doctype>
        for (Entry raw : catalog.entries(Entry.Type.DOCTYPE)) {
            EntryDoctype entry = (EntryDoctype) raw;
            if (entry.name.equals(entityName)) {
                return new QueryResult(entry.uri);
            }
        }

        // <nextCatalog>
        List<URI> next = nextCatalogs(catalog);
        if (!next.isEmpty()) {
            return new QueryDoctype(entityName, systemId, publicId, next);
        }

        return QueryResult.EMPTY_RESULT;
    }
}
