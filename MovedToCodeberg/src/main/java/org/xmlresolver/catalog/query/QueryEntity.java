package org.xmlresolver.catalog.query;

import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryEntity;
import org.xmlresolver.utils.PublicId;

/**
 * Query for entity catalog entries.
 */
public class QueryEntity extends QueryCatalog {
    /** The entity name. */
    public final String entityName;
    /** The system identifier. */
    public final String systemId;
    /** The public identifier. */
    public final String publicId;

    /**
     * QueryEntity constructor.
     * @param entityName The entity name.
     * @param systemId The system identifier.
     * @param publicId The public identifier.
     */
    public QueryEntity(String entityName, String systemId, String publicId) {
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

        // <entity>
        for (Entry raw : catalog.entries(Entry.Type.ENTITY)) {
            EntryEntity entry = (EntryEntity) raw;
            if (entry.name.equals(entityName)) {
                return new QueryResult(entry.uri);
            }
        }

        return QueryResult.EMPTY_RESULT;
    }
}
