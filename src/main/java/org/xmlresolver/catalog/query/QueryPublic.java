package org.xmlresolver.catalog.query;

import org.jetbrains.annotations.NotNull;
import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryDelegatePublic;
import org.xmlresolver.catalog.entry.EntryPublic;
import org.xmlresolver.utils.PublicId;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueryPublic extends QueryCatalog {
    public final String systemId;
    public final String publicId;
    public final boolean localOnly;

    public QueryPublic(String systemId, String publicId, List<URI> catalogs, boolean local) {
        super(catalogs);
        this.systemId = systemId;
        this.publicId = publicId == null ? null : PublicId.normalize(publicId);
        this.localOnly = local;
    }

    public QueryPublic(String systemId, String publicId, EntryCatalog catalog) {
        this(systemId, publicId, Collections.singletonList(catalog.baseURI), true);
    }

    public QueryPublic(String systemId, String publicId, List<URI> catalogs) {
        this(systemId, publicId, catalogs, false);
    }

    @Override
    protected @NotNull QueryResult lookup(CatalogManager manager, EntryCatalog catalog) {
        if (systemId != null) {
            QueryResult result = manager.search(new QuerySystem(systemId, catalog));
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

        // <nextCatalog>
        if (!localOnly) {
            List<URI> next = nextCatalogs(catalog);
            if (!next.isEmpty()) {
                return new QueryPublic(systemId, publicId, next);
            }
        }

        return QueryResult.EMPTY_RESULT;
    }
}
