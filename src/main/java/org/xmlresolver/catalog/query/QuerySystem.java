package org.xmlresolver.catalog.query;

import org.jetbrains.annotations.NotNull;
import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryDelegateSystem;
import org.xmlresolver.catalog.entry.EntryRewriteSystem;
import org.xmlresolver.catalog.entry.EntrySystem;
import org.xmlresolver.catalog.entry.EntrySystemSuffix;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuerySystem extends QueryCatalog {
    public final String systemId;
    public final boolean localOnly;

    public QuerySystem(String systemId, List<URI> catalogs, boolean local) {
        super(catalogs);

        if (systemId.startsWith("classpath:/")) {
            this.systemId = "classpath:" + systemId.substring(11);
        } else {
            this.systemId = systemId;
        }

        this.localOnly = local;
    }

    public QuerySystem(String systemId, EntryCatalog catalog) {
        this(systemId, Collections.singletonList(catalog.baseURI), true);
    }

    public QuerySystem(String systemId, List<URI> catalogs) {
        this(systemId, catalogs, false);
    }

    @Override
    public @NotNull QueryResult lookup(CatalogManager manager, EntryCatalog catalog) {
        String osname = System.getProperty("os.name").toLowerCase();
        boolean ignoreFScase = osname.contains("windows") || osname.contains("mac");
        String lowerCaseSystemId = systemId.toLowerCase();

        // <system>
        for (Entry raw : catalog.entries(Entry.Type.SYSTEM)) {
            EntrySystem entry = (EntrySystem) raw;
            if (entry.systemId.equals(systemId) || (ignoreFScase && entry.systemId.equalsIgnoreCase(systemId))) {
                return new QueryResult(entry.uri);
            }
        }

        // <rewriteSystem>
        EntryRewriteSystem rewrite = null;
        for (Entry raw : catalog.entries(Entry.Type.REWRITE_SYSTEM)) {
            EntryRewriteSystem entry = (EntryRewriteSystem) raw;
            if (systemId.startsWith(entry.systemIdStart) || (ignoreFScase && lowerCaseSystemId.startsWith(entry.systemIdStart.toLowerCase()))) {
                if (rewrite == null || entry.systemIdStart.length() > rewrite.systemIdStart.length()) {
                    rewrite = entry;
                }
            }
        }
        if (rewrite != null) {
            return new QueryResult(rewrite.rewritePrefix.resolve(systemId.substring(rewrite.systemIdStart.length())));
        }

        // <systemSuffix>
        EntrySystemSuffix suffix = null;
        for (Entry raw : catalog.entries(Entry.Type.SYSTEM_SUFFIX)) {
            EntrySystemSuffix entry = (EntrySystemSuffix) raw;
            if (systemId.endsWith(entry.systemIdSuffix) || (ignoreFScase && lowerCaseSystemId.endsWith(entry.systemIdSuffix.toLowerCase()))) {
                if (suffix == null || entry.systemIdSuffix.length() > suffix.systemIdSuffix.length()) {
                    suffix = entry;
                }
            }
        }
        if (suffix != null) {
            return new QueryResult(suffix.uri);
        }

        // <delegateSystem>
        ArrayList<EntryDelegateSystem> delegated = new ArrayList<>();
        for (Entry raw : catalog.entries(Entry.Type.DELEGATE_SYSTEM)) {
            EntryDelegateSystem entry = (EntryDelegateSystem) raw;
            if (systemId.startsWith(entry.systemIdStart) || (ignoreFScase && lowerCaseSystemId.startsWith(entry.systemIdStart.toLowerCase()))) {
                int pos = 0;
                while (pos < delegated.size() && entry.systemIdStart.length() <= delegated.get(pos).systemIdStart.length()) {
                    pos += 1;
                }
                delegated.add(pos, entry);
            }
        }
        if (!delegated.isEmpty()) {
            ArrayList<URI> catalogs = new ArrayList<>();
            for (EntryDelegateSystem entry : delegated) {
                catalogs.add(entry.catalog);
            }
            return new QueryDelegateSystem(systemId, catalogs);
        }

        // <nextCatalog>
        if (!localOnly) {
            List<URI> next = nextCatalogs(catalog);
            if (!next.isEmpty()) {
                return new QuerySystem(systemId, next);
            }
        }

        return QueryResult.EMPTY_RESULT;
    }
}
