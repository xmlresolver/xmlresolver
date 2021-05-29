package org.xmlresolver.catalog.query;

import org.jetbrains.annotations.NotNull;
import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryDelegateSystem;
import org.xmlresolver.catalog.entry.EntryRewriteSystem;
import org.xmlresolver.catalog.entry.EntrySystem;
import org.xmlresolver.catalog.entry.EntrySystemSuffix;
import org.xmlresolver.utils.URIUtils;

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
        String compareSystem = manager.normalizedForComparison(systemId);

        String osname = System.getProperty("os.name").toLowerCase();
        boolean ignoreFScase = osname.contains("windows") || osname.contains("mac");
        String lowerCaseSystemId = compareSystem.toLowerCase();

        // <system>
        for (Entry raw : catalog.entries(Entry.Type.SYSTEM)) {
            EntrySystem entry = (EntrySystem) raw;
            String entrySystem = manager.normalizedForComparison(entry.systemId);
            if (entrySystem.equals(compareSystem) || (ignoreFScase && entrySystem.equalsIgnoreCase(compareSystem))) {
                return new QueryResult(entry.uri);
            }
        }

        // <rewriteSystem>
        EntryRewriteSystem rewrite = null;
        String rewriteStart = null;
        for (Entry raw : catalog.entries(Entry.Type.REWRITE_SYSTEM)) {
            EntryRewriteSystem entry = (EntryRewriteSystem) raw;
            String compareStart = manager.normalizedForComparison(entry.systemIdStart);
            if (compareSystem.startsWith(compareStart) || (ignoreFScase && lowerCaseSystemId.startsWith(compareStart.toLowerCase()))) {
                if (rewrite == null || compareStart.length() > rewriteStart.length()) {
                    rewrite = entry;
                    rewriteStart = compareStart;
                }
            }
        }
        if (rewrite != null) {
            URI resolved = URIUtils.resolve(rewrite.rewritePrefix, systemId.substring(rewrite.systemIdStart.length()+1));
            return new QueryResult(resolved);
        }

        // <systemSuffix>
        EntrySystemSuffix suffix = null;
        String systemSuffix = null;
        for (Entry raw : catalog.entries(Entry.Type.SYSTEM_SUFFIX)) {
            EntrySystemSuffix entry = (EntrySystemSuffix) raw;
            String compareSuffix = manager.normalizedForComparison(entry.systemIdSuffix);
            if (compareSystem.endsWith(compareSuffix) || (ignoreFScase && lowerCaseSystemId.endsWith(compareSuffix.toLowerCase()))) {
                if (suffix == null || compareSuffix.length() > systemSuffix.length()) {
                    suffix = entry;
                    systemSuffix = compareSuffix;
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
            String delegateStart = manager.normalizedForComparison(entry.systemIdStart);
            if (compareSystem.startsWith(delegateStart) || (ignoreFScase && lowerCaseSystemId.startsWith(delegateStart.toLowerCase()))) {
                int pos = 0;
                while (pos < delegated.size()
                        && delegateStart.length() <= manager.normalizedForComparison(delegated.get(pos).systemIdStart).length()) {
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
