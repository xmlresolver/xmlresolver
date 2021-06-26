package org.xmlresolver.catalog.entry;

import org.xml.sax.Locator;
import org.xmlresolver.ResolverLogger;

import java.net.URI;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;

public class EntryCatalog extends Entry {
    public static ResolverLogger logger = new ResolverLogger(EntryCatalog.class);
    public final boolean preferPublic;
    protected static final ArrayList<Entry> none = new ArrayList<>();
    protected final ArrayList<Entry> entries = new ArrayList<> ();
    protected final HashMap<Type, ArrayList<Entry>> typedEntries = new HashMap<>();
    protected Locator locator = null;

    public EntryCatalog(URI baseURI, String id, boolean prefer) {
        super(baseURI, id);
        this.preferPublic = prefer;
    }

    @Override
    public Type getType() {
        return Type.CATALOG;
    }

    public synchronized List<Entry> entries() {
        return entries;
    }

    public synchronized List<Entry> entries(Type type) {
        return typedEntries.getOrDefault(type, none);
    }

    protected synchronized void add(Entry entry) {
        entries.add(entry);

        if (!typedEntries.containsKey(entry.getType())) {
            typedEntries.put(entry.getType(), new ArrayList<>());
        }
        typedEntries.get(entry.getType()).add(entry);
    }

    protected synchronized void remove(Entry entry) {
        entries.remove(entry);
        if (typedEntries.containsKey(entry.getType())) {
            typedEntries.get(entry.getType()).remove(entry);
        }
    }

    public void setLocator(Locator locator) {
        this.locator = locator;
    }

    protected void error(String message, Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(baseURI);
        if (locator != null && locator.getLineNumber() > 0) {
            sb.append(":");
            sb.append(locator.getLineNumber());
            if (locator.getColumnNumber() > 0) {
                sb.append(":");
                sb.append(locator.getColumnNumber());
            }
        }
        sb.append(":");
        Formatter formatter = new Formatter(sb);
        formatter.format(message, params);
        logger.log(ResolverLogger.ERROR, sb.toString());
    }

    public EntryGroup addGroup(URI baseURI, String id, boolean prefer) {
        EntryGroup entry = new EntryGroup(baseURI, id, prefer);
        add(entry);
        return entry;
    }

    public EntryPublic addPublic(URI baseURI, String id, String publicId, String uri, boolean prefer) {
        EntryPublic entry = null;
        if (publicId != null && uri != null) {
            entry = new EntryPublic(baseURI, id, publicId, uri, prefer);
            add(entry);
        } else {
            error("Invalid public entry (missing publicId or uri attribute)");
        }
        return entry;
    }

    public EntrySystem addSystem(URI baseURI, String id, String systemId, String uri) {
        EntrySystem entry = null;
        if (systemId != null && uri != null) {
            entry = new EntrySystem(baseURI, id, systemId, uri);
            add(entry);
        } else {
            error("Invalid system entry (missing systemId or uri attribute)");
        }
        return entry;
    }

    public EntrySystemSuffix addSystemSuffix(URI baseURI, String id, String suffix, String uri) {
        EntrySystemSuffix entry = null;
        if (suffix != null && uri != null) {
            entry = new EntrySystemSuffix(baseURI, id, suffix, uri);
            add(entry);
        } else {
            error("Invalid systemSuffix entry (missing systemIdSuffix or uri attribute)");
        }
        return entry;
    }

    public EntryRewriteSystem addRewriteSystem(URI baseURI, String id, String startString, String prefix) {
        EntryRewriteSystem entry = null;
        if (startString != null && prefix != null) {
            entry = new EntryRewriteSystem(baseURI, id, startString, prefix);
            add(entry);
        } else {
            error("Invalid rewriteSystem entry (missing systemIdStartString or prefix attribute)");
        }
        return entry;
    }

    public EntryDelegateSystem addDelegateSystem(URI baseURI, String id, String startString, String catalog) {
        EntryDelegateSystem entry = null;
        if (startString != null && catalog != null) {
            entry = new EntryDelegateSystem(baseURI, id, startString, catalog);
            add(entry);
        } else {
            error("Invalid delegateSystem entry (missing systemIdStartString or catalog attribute)");
        }
        return entry;
    }

    public EntryDelegatePublic addDelegatePublic(URI baseURI, String id, String startString, String catalog, boolean prefer) {
        EntryDelegatePublic entry = null;
        if (startString != null && catalog != null) {
            entry = new EntryDelegatePublic(baseURI, id, startString, catalog, prefer);
            add(entry);
        } else {
            error("Invalid delegatePublic entry (missing publicIdStartString or catalog attribute)");
        }
        return entry;
    }

    public EntryUri addUri(URI baseURI, String id, String name, String uri, String nature, String purpose) {
        EntryUri entry = null;
        if (name != null && uri != null) {
            entry = new EntryUri(baseURI, id, name, uri, nature, purpose);
            add(entry);
        } else {
            error("Invalid uri entry (missing name or uri attribute)");
        }
        return entry;
    }

    public EntryRewriteUri addRewriteUri(URI baseURI, String id, String start, String prefix) {
        EntryRewriteUri entry = null;
        if (start != null && prefix != null) {
            entry = new EntryRewriteUri(baseURI, id, start, prefix);
            add(entry);
        } else {
            error("Invalid rewriteURI entry (missing uriStartString or prefix attribute)");
        }
        return entry;
    }

    public EntryUriSuffix addUriSuffix(URI baseURI, String id, String suffix, String uri) {
        EntryUriSuffix entry = null;
        if (suffix != null && uri != null) {
            entry = new EntryUriSuffix(baseURI, id, suffix, uri);
            add(entry);
        } else {
            error("Invalid uriSuffix entry (missing uriStartString or uri attribute)");
        }
        return entry;
    }

    public EntryDelegateUri addDelegateUri(URI baseURI, String id, String startString, String catalog) {
        EntryDelegateUri entry = null;
        if (startString != null && catalog != null) {
            entry = new EntryDelegateUri(baseURI, id, startString, catalog);
            add(entry);
        } else {
            error("Invalid delegateURI entry (missing uriStartString or catalog attribute)");
        }
        return entry;
    }

    public EntryNextCatalog addNextCatalog(URI baseURI, String id, String catalog) {
        EntryNextCatalog entry = null;
        if (catalog != null) {
            entry = new EntryNextCatalog(baseURI, id, catalog);
            add(entry);
        } else {
            error("Invalid nextCatalog entry (missing catalog attribute)");
        }
        return entry;
    }

    public EntryDoctype addDoctype(URI baseURI, String id, String name, String uri) {
        EntryDoctype entry = null;
        if (name != null && uri != null) {
            entry = new EntryDoctype(baseURI, id, name, uri);
            add(entry);
        } else {
            error("Invalid doctype entry (missing name or uri attribute)");
        }
        return entry;
    }

    public EntryDocument addDocument(URI baseURI, String id, String uri) {
        EntryDocument entry = null;
        if (uri != null) {
            entry = new EntryDocument(baseURI, id, uri);
            add(entry);
        } else {
            error("Invalid document entry (missing uri attribute)");
        }
        return entry;
    }

    public EntryDtddecl addDtdDecl(URI baseURI, String id, String publicId, String uri) {
        EntryDtddecl entry = null;
        if (publicId != null && uri != null) {
            entry = new EntryDtddecl(baseURI, id, publicId, uri);
            add(entry);
        } else {
            error("Invalid dtddecl entry (missing publicId or uri attribute)");
        }
        return entry;
    }

    public EntryEntity addEntity(URI baseURI, String id, String name, String uri) {
        EntryEntity entry = null;
        if (name != null && uri != null) {
            entry = new EntryEntity(baseURI, id, name, uri);
            add(entry);
        } else {
            error("Invalid entity entry (missing name or uri attribute)");
        }
        return entry;
    }

    public EntryLinktype addLinktype(URI baseURI, String id, String name, String uri) {
        EntryLinktype entry = null;
        if (name != null && uri != null) {
            entry = new EntryLinktype(baseURI, id, name, uri);
            add(entry);
        } else {
            error("Invalid linktype entry (missing name or uri attribute)");
        }
        return entry;
    }

    public EntryNotation addNotation(URI baseURI, String id, String name, String uri) {
        EntryNotation entry = null;
        if (name != null && uri != null) {
            entry = new EntryNotation(baseURI, id, name, uri);
            add(entry);
        } else {
            error("Invalid notation entry (missing name or uri attribute)");
        }
        return entry;
    }

    public EntrySgmldecl addSgmlDecl(URI baseURI, String id, String uri) {
        EntrySgmldecl entry = null;
        if (uri != null) {
            entry = new EntrySgmldecl(baseURI, id, uri);
            add(entry);
        } else {
            error("Invalid sgmldecl entry (uri attribute)");
        }
        return entry;
    }

    @Override
    public String toString() {
        return "catalog prefer=" + (preferPublic ? "public" : "system");
    }
}
