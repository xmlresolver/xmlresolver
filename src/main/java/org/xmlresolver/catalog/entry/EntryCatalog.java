package org.xmlresolver.catalog.entry;

import org.xml.sax.Locator;
import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.logging.AbstractLogger;

import java.net.URI;
import java.util.*;

/**
 * A Catalog entry.
 * <p>The catalog entry is usually the root of a catalog file.</p>
 */

public class EntryCatalog extends Entry {
    /** Are public entries preferred over system ones? */
    public final boolean preferPublic;

    protected static final ArrayList<Entry> none = new ArrayList<>();
    protected final ArrayList<Entry> entries = new ArrayList<> ();
    protected final HashMap<Type, ArrayList<Entry>> typedEntries = new HashMap<>();
    protected Locator locator = null;

    /**
     * EntryCatalog constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param prefer Are public identifiers preferred?
     */
    public EntryCatalog(ResolverConfiguration config, URI baseURI, String id, boolean prefer) {
        super(config, baseURI, id);
        this.preferPublic = prefer;
    }

    @Override
    public Type getType() {
        return Type.CATALOG;
    }

    /**
     * Get all the entries in this catalog.
     * @return The entries.
     */
    public synchronized List<Entry> entries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Get all the entries of a particular type in this catalog.
     * @param type The entry type.
     * @return The entries.
     */
    public synchronized List<Entry> entries(Type type) {
        return Collections.unmodifiableList(typedEntries.getOrDefault(type, none));
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

    /**
     * Set the locator.
     * <p>The locator is advanced while parsing catalog files to keep track of the position of entries.
     * (This useful in error messages, it isn't relevant to catalog processing.)</p>
     * @param locator The locator.
     */
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
        logger.error(sb.toString());
    }

    /**
     * Add a group entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param prefer Are public identifiers preferred?
     * @return the entry added.
     */
    public EntryGroup addGroup(URI baseURI, String id, boolean prefer) {
        EntryGroup entry = new EntryGroup(config, baseURI, id, prefer);
        add(entry);
        return entry;
    }

    /**
     * Add a public entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param publicId The public identifier.
     * @param uri The URI for this entry.
     * @param prefer Are public identifiers preferred?
     * @return the entry added.
     */
    public EntryPublic addPublic(URI baseURI, String id, String publicId, String uri, boolean prefer) {
        EntryPublic entry = null;
        if (publicId != null && uri != null) {
            entry = new EntryPublic(config, baseURI, id, publicId, uri, prefer);
            add(entry);
        } else {
            error("Invalid public entry (missing publicId or uri attribute)");
        }
        return entry;
    }

    /**
     * Add a system entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param systemId The system identifier.
     * @param uri The URI for this entry.
     * @return the entry added.
     */
    public EntrySystem addSystem(URI baseURI, String id, String systemId, String uri) {
        EntrySystem entry = null;
        if (systemId != null && uri != null) {
            entry = new EntrySystem(config, baseURI, id, systemId, uri);
            add(entry);
        } else {
            error("Invalid system entry (missing systemId or uri attribute)");
        }
        return entry;
    }

    /**
     * Add a system suffix entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param suffix The system identifier suffix.
     * @param uri The URI for this entry.
     * @return the entry added.
     */
    public EntrySystemSuffix addSystemSuffix(URI baseURI, String id, String suffix, String uri) {
        EntrySystemSuffix entry = null;
        if (suffix != null && uri != null) {
            entry = new EntrySystemSuffix(config, baseURI, id, suffix, uri);
            add(entry);
        } else {
            error("Invalid systemSuffix entry (missing systemIdSuffix or uri attribute)");
        }
        return entry;
    }

    /**
     * Add a rewrite system entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param startString The system identifier prefix to match.
     * @param prefix The rewrite prefix for this entry.
     * @return the entry added.
     */
    public EntryRewriteSystem addRewriteSystem(URI baseURI, String id, String startString, String prefix) {
        EntryRewriteSystem entry = null;
        if (startString != null && prefix != null) {
            entry = new EntryRewriteSystem(config, baseURI, id, startString, prefix);
            add(entry);
        } else {
            error("Invalid rewriteSystem entry (missing systemIdStartString or prefix attribute)");
        }
        return entry;
    }

    /**
     * Add a delegate system entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param startString The system identifier prefix to match.
     * @param catalog The delegated catalog.
     * @return the entry added.
     */
    public EntryDelegateSystem addDelegateSystem(URI baseURI, String id, String startString, String catalog) {
        EntryDelegateSystem entry = null;
        if (startString != null && catalog != null) {
            entry = new EntryDelegateSystem(config, baseURI, id, startString, catalog);
            add(entry);
        } else {
            error("Invalid delegateSystem entry (missing systemIdStartString or catalog attribute)");
        }
        return entry;
    }

    /**
     * Add a delegate public entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param startString The public identifier prefix to match.
     * @param catalog The delegated catalog.
     * @param prefer Are public identifiers preferred?
     * @return the entry added.
     */
    public EntryDelegatePublic addDelegatePublic(URI baseURI, String id, String startString, String catalog, boolean prefer) {
        EntryDelegatePublic entry = null;
        if (startString != null && catalog != null) {
            entry = new EntryDelegatePublic(config, baseURI, id, startString, catalog, prefer);
            add(entry);
        } else {
            error("Invalid delegatePublic entry (missing publicIdStartString or catalog attribute)");
        }
        return entry;
    }

    /**
     * Add a uri entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param name The resource name (the URI to be matched)
     * @param nature The nature of the resource (may be null)
     * @param purpose The purpose of the resource (may be null)
     * @return the entry added.
     */
    public EntryUri addUri(URI baseURI, String id, String name, String uri, String nature, String purpose) {
        EntryUri entry = null;
        if (name != null && uri != null) {
            entry = new EntryUri(config, baseURI, id, name, uri, nature, purpose);
            add(entry);
        } else {
            error("Invalid uri entry (missing name or uri attribute)");
        }
        return entry;
    }

    /**
     * Add a rewrite uri entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param startString The uri prefix to match.
     * @param prefix The rewrite prefix for this entry.
     * @return the entry added.
     */
    public EntryRewriteUri addRewriteUri(URI baseURI, String id, String startString, String prefix) {
        EntryRewriteUri entry = null;
        if (startString != null && prefix != null) {
            entry = new EntryRewriteUri(config, baseURI, id, startString, prefix);
            add(entry);
        } else {
            error("Invalid rewriteURI entry (missing uriStartString or prefix attribute)");
        }
        return entry;
    }

    /**
     * Add a rewrite uri entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param endString The uri suffix to match.
     * @param uri The uri for this entry.
     * @return the entry added.
     */
    public EntryUriSuffix addUriSuffix(URI baseURI, String id, String endString, String uri) {
        EntryUriSuffix entry = null;
        if (endString != null && uri != null) {
            entry = new EntryUriSuffix(config, baseURI, id, endString, uri);
            add(entry);
        } else {
            error("Invalid uriSuffix entry (missing uriStartString or uri attribute)");
        }
        return entry;
    }

    /**
     * Add a delegate uri entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param startString The uri prefix to match.
     * @param catalog The delegated catalog.
     * @return the entry added.
     */
    public EntryDelegateUri addDelegateUri(URI baseURI, String id, String startString, String catalog) {
        EntryDelegateUri entry = null;
        if (startString != null && catalog != null) {
            entry = new EntryDelegateUri(config, baseURI, id, startString, catalog);
            add(entry);
        } else {
            error("Invalid delegateURI entry (missing uriStartString or catalog attribute)");
        }
        return entry;
    }

    /**
     * Add a next catalog entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param catalog The catalog to search next.
     * @return the entry added.
     */
    public EntryNextCatalog addNextCatalog(URI baseURI, String id, String catalog) {
        EntryNextCatalog entry = null;
        if (catalog != null) {
            entry = new EntryNextCatalog(config, baseURI, id, catalog);
            add(entry);
        } else {
            error("Invalid nextCatalog entry (missing catalog attribute)");
        }
        return entry;
    }

    /**
     * Add a doctype entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param name The doctype name.
     * @param uri The uri for this entry.
     * @return the entry added.
     */
    public EntryDoctype addDoctype(URI baseURI, String id, String name, String uri) {
        EntryDoctype entry = null;
        if (name != null && uri != null) {
            entry = new EntryDoctype(config, baseURI, id, name, uri);
            add(entry);
        } else {
            error("Invalid doctype entry (missing name or uri attribute)");
        }
        return entry;
    }

    /**
     * Add a document entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param uri The uri for this entry.
     * @return the entry added.
     */
    public EntryDocument addDocument(URI baseURI, String id, String uri) {
        EntryDocument entry = null;
        if (uri != null) {
            entry = new EntryDocument(config, baseURI, id, uri);
            add(entry);
        } else {
            error("Invalid document entry (missing uri attribute)");
        }
        return entry;
    }

    /**
     * Add a DTD declaration entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param publicId The public identifier.
     * @param uri The uri for this entry.
     * @return the entry added.
     */
    public EntryDtdDecl addDtdDecl(URI baseURI, String id, String publicId, String uri) {
        EntryDtdDecl entry = null;
        if (publicId != null && uri != null) {
            entry = new EntryDtdDecl(config, baseURI, id, publicId, uri);
            add(entry);
        } else {
            error("Invalid dtddecl entry (missing publicId or uri attribute)");
        }
        return entry;
    }

    /**
     * Add an entity declaration entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param name The entity name.
     * @param uri The uri for this entry.
     * @return the entry added.
     */
    public EntryEntity addEntity(URI baseURI, String id, String name, String uri) {
        EntryEntity entry = null;
        if (name != null && uri != null) {
            entry = new EntryEntity(config, baseURI, id, name, uri);
            add(entry);
        } else {
            error("Invalid entity entry (missing name or uri attribute)");
        }
        return entry;
    }

    /**
     * Add a link type declaration entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param name The entity name.
     * @param uri The uri for this entry.
     * @return the entry added.
     */
    public EntryLinktype addLinktype(URI baseURI, String id, String name, String uri) {
        EntryLinktype entry = null;
        if (name != null && uri != null) {
            entry = new EntryLinktype(config, baseURI, id, name, uri);
            add(entry);
        } else {
            error("Invalid linktype entry (missing name or uri attribute)");
        }
        return entry;
    }

    /**
     * Add a notation entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param name The notation name.
     * @param uri The uri for this entry.
     * @return the entry added.
     */
    public EntryNotation addNotation(URI baseURI, String id, String name, String uri) {
        EntryNotation entry = null;
        if (name != null && uri != null) {
            entry = new EntryNotation(config, baseURI, id, name, uri);
            add(entry);
        } else {
            error("Invalid notation entry (missing name or uri attribute)");
        }
        return entry;
    }

    /**
     * Add an SGML declaration entry to the catalog.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param uri The uri for this entry.
     * @return the entry added.
     */
    public EntrySgmldecl addSgmlDecl(URI baseURI, String id, String uri) {
        EntrySgmldecl entry = null;
        if (uri != null) {
            entry = new EntrySgmldecl(config, baseURI, id, uri);
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
