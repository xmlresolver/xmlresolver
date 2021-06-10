package org.xmlresolver.catalog.query;

import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryDelegatePublic;
import org.xmlresolver.catalog.entry.EntryPublic;

import java.net.URI;
import java.util.ArrayList;

public class QueryPublic extends QueryCatalog {
    public final String systemId;
    public final String publicId;

    public QueryPublic(String systemId, String publicId) {
        super();
        this.systemId = systemId;
        this.publicId = publicId;
    }

    @Override
    protected QueryResult lookup(CatalogManager manager, EntryCatalog catalog) {
        if (systemId != null) {
            QuerySystem query = new QuerySystem(systemId);
            QueryResult result = query.lookup(manager, catalog);
            if (result.resolved()) {
                return result;
            }
        }

        if (publicId != null) {
            // <public>
            for (Entry raw : catalog.entries(Entry.Type.PUBLIC)) {
                EntryPublic entry = (EntryPublic) raw;
                if (entry.preferPublic || systemId == null) {
                    if (entry.publicId.equals(publicId)) {
                        return new QueryResult(entry.uri);
                    }
                }
            }

            // <delegatePublic>
            ArrayList<EntryDelegatePublic> delegated = new ArrayList<>();
            for (Entry raw : catalog.entries(Entry.Type.DELEGATE_PUBLIC)) {
                EntryDelegatePublic entry = (EntryDelegatePublic) raw;
                if (entry.preferPublic || systemId == null) {
                    if (publicId.startsWith(entry.publicIdStart)) {
                        int pos = 0;
                        while (pos < delegated.size() && entry.publicIdStart.length() <= delegated.get(pos).publicIdStart.length()) {
                            pos += 1;
                        }
                        delegated.add(pos, entry);
                    }
                }
            }
            if (!delegated.isEmpty()) {
                ArrayList<URI> catalogs = new ArrayList<>();
                for (EntryDelegatePublic entry : delegated) {
                    catalogs.add(entry.catalog);
                }
                return new QueryDelegatePublic(systemId, publicId, catalogs);
            }
        }

        return QueryResult.EMPTY_RESULT;
    }

}
