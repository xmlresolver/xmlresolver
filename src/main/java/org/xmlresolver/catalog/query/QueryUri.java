package org.xmlresolver.catalog.query;

import org.jetbrains.annotations.NotNull;
import org.xmlresolver.CatalogManager;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryDelegateUri;
import org.xmlresolver.catalog.entry.EntryRewriteUri;
import org.xmlresolver.catalog.entry.EntryUri;
import org.xmlresolver.catalog.entry.EntryUriSuffix;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class QueryUri extends QueryCatalog {
    public final String uri;
    public final String nature;
    public final String purpose;

    public QueryUri(String uri, String nature, String purpose, List<URI> catalogs) {
        super(catalogs);
        this.uri = uri;
        this.nature = nature;
        this.purpose = purpose;
    }

    @Override
    protected @NotNull QueryResult lookup(CatalogManager manager, EntryCatalog catalog) {
        // <uri>
        for (Entry raw : catalog.entries(Entry.Type.URI)) {
            EntryUri entry = (EntryUri) raw;
            if (entry.name.equals(uri)
                    && (nature == null || entry.nature == null || nature.equals(entry.nature))
                    && (purpose == null || entry.purpose == null || purpose.equals(entry.purpose))) {
                return new QueryResult(entry.uri);
            }
        }

        // <rewriteURI>
        EntryRewriteUri rewrite = null;
        for (Entry raw : catalog.entries(Entry.Type.REWRITE_URI)) {
            EntryRewriteUri entry = (EntryRewriteUri) raw;
            if (uri.startsWith(entry.uriStart)) {
                if (rewrite == null || entry.uriStart.length() > rewrite.uriStart.length()) {
                    rewrite = entry;
                }
            }
        }
        if (rewrite != null) {
            return new QueryResult(rewrite.rewritePrefix.resolve(uri.substring(rewrite.uriStart.length())));
        }

        // <uriSuffix>
        EntryUriSuffix suffix = null;
        for (Entry raw : catalog.entries(Entry.Type.URI_SUFFIX)) {
            EntryUriSuffix entry = (EntryUriSuffix) raw;
            if (uri.endsWith(entry.uriSuffix)) {
                if (suffix == null || entry.uriSuffix.length() > suffix.uriSuffix.length()) {
                    suffix = entry;
                }
            }
        }
        if (suffix != null) {
            return new QueryResult(suffix.uri);
        }

        // <delegateUri>
        ArrayList<EntryDelegateUri> delegated = new ArrayList<>();
        for (Entry raw : catalog.entries(Entry.Type.DELEGATE_URI)) {
            EntryDelegateUri entry = (EntryDelegateUri) raw;
            if (uri.startsWith(entry.uriStart)) {
                int pos = 0;
                while (pos < delegated.size() && entry.uriStart.length() <= delegated.get(pos).uriStart.length()) {
                    pos += 1;
                }
                delegated.add(pos, entry);
            }
        }
        if (!delegated.isEmpty()) {
            ArrayList<URI> catalogs = new ArrayList<>();
            for (EntryDelegateUri entry : delegated) {
                catalogs.add(entry.catalog);
            }
            return new QueryDelegateUri(uri, nature, purpose, catalogs);
        }

        // <nextCatalog>
        List<URI> next = nextCatalogs(catalog);
        if (!next.isEmpty()) {
            return new QueryUri(uri, nature, purpose, next);
        }

        return QueryResult.EMPTY_RESULT;
    }
}
