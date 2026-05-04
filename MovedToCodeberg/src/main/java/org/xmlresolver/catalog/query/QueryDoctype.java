package org.xmlresolver.catalog.query;

import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryDoctype;
import org.xmlresolver.utils.PublicId;

/**
 * Query for doctype catalog entries.
 */
public class QueryDoctype extends QueryCatalog {
    /** The doctype name. */
    public final String entityName;
    /** The system identifier. */
    public final String systemId;
    /** The public identifier. */
    public final String publicId;

    /**
     * QueryDoctype constructor.
     * @param entityName The entity (DTD) name.
     * @param systemId The system identifier.
     * @param publicId The public identifier.
     */
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
