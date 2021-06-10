package org.xmlresolver.catalog.query;

import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryDoctype;
import org.xmlresolver.utils.PublicId;

public class QueryDoctype extends QueryCatalog {
    public final String entityName;
    public final String systemId;
    public final String publicId;

    public QueryDoctype(String entityName, String systemId, String publicId) {
        super();
        this.entityName = entityName;
        this.systemId = systemId;
        this.publicId = publicId == null ? null : PublicId.normalize(publicId);
    }

    @Override
    protected QueryResult lookup(CatalogManager manager, EntryCatalog catalog) {
        QueryPublic queryPublic = new QueryPublic(systemId, publicId);
        QueryResult result = queryPublic.lookup(manager, catalog);
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

        return QueryResult.EMPTY_RESULT;
    }
}
