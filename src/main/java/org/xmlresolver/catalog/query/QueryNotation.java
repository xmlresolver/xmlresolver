package org.xmlresolver.catalog.query;

import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryNotation;
import org.xmlresolver.utils.PublicId;

public class QueryNotation extends QueryCatalog {
    public final String notationName;
    public final String systemId;
    public final String publicId;

    public QueryNotation(String notationName, String systemId, String publicId) {
        super();
        this.notationName = notationName;
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

        // <notation>
        for (Entry raw : catalog.entries(Entry.Type.NOTATION)) {
            EntryNotation entry = (EntryNotation) raw;
            if (entry.name.equals(notationName)) {
                return new QueryResult(entry.uri);
            }
        }

        return QueryResult.EMPTY_RESULT;
    }
}
