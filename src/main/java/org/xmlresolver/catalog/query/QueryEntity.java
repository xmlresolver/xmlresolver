package org.xmlresolver.catalog.query;

import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryEntity;
import org.xmlresolver.utils.PublicId;

public class QueryEntity extends QueryCatalog {
    public final String entityName;
    public final String systemId;
    public final String publicId;

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
