package org.xmlresolver.cache;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.ResolverConstants;
import org.xmlresolver.catalog.entry.*;
import org.xmlresolver.logging.AbstractLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.regex.Pattern;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class CacheEntryCatalog extends EntryCatalog {
    public final ArrayList<CacheEntry> cached = new ArrayList<>();
    private final URI baseURI;

    public CacheEntryCatalog(ResolverConfiguration config, URI baseURI, String id, boolean prefer) {
        super(config, baseURI, id, prefer);
        this.baseURI = baseURI;
    }

    @Override
    protected void add(Entry entry) {
        throw new IllegalStateException("Attempt to add entry to cache catalog");
    }

    protected void add(Entry entry, Long time) {
        int pos = 0;
        while (pos < cached.size()) {
            if (cached.get(pos).time > time) {
                break;
            }
            pos++;
        }

        switch (entry.getType()) {
            case URI:
                cached.add(pos, new CacheEntry(((EntryUri) entry), time));
                break;
            case SYSTEM:
                cached.add(pos, new CacheEntry(((EntrySystem) entry), time));
                break;
            case PUBLIC:
                cached.add(pos, new CacheEntry(((EntryPublic) entry), time));
                break;
            default:
                throw new IllegalArgumentException("Attempt to cache unknown entry type");
        };

        super.add(entry);
    }

    @Override
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
        logger.log(AbstractLogger.CACHE, sb.toString());
    }

    protected EntryUri addUri(URI baseURI, String name, String uri, String nature, String purpose, long timestamp) {
        EntryUri entry = null;
        if (name != null && uri != null) {
            entry = new EntryUri(config, baseURI, null, name, uri, nature, purpose);
            add(entry, timestamp);
        } else {
            error("Invalid uri entry (missing name or uri attribute)");
        }

        return entry;
    }

    protected EntryPublic addPublic(URI baseURI, String publicId, String uri, long timestamp) {
        EntryPublic entry = null;
        if (publicId != null && uri != null) {
            entry = new EntryPublic(config, baseURI, null, publicId, uri, true);
            add(entry, timestamp);
        } else {
            error("Invalid public entry (missing publicId or uri attribute)");
        }
        return entry;
    }

    protected EntrySystem addSystem(URI baseURI, String systemId, String uri, long timestamp) {
        EntrySystem entry = null;
        if (systemId != null && uri != null) {
            entry = new EntrySystem(config, baseURI, null, systemId, uri);
            add(entry, timestamp);
        } else {
            error("Invalid system entry (missing systemId or uri attribute)");
        }
        return entry;
    }

    protected void writeCacheEntry(Entry entry, File cacheFile) throws IOException {
        // Constructing XML with print statements is kind of grotty, but...
        try (PrintStream xml = new PrintStream(new FileOutputStream(cacheFile))) {
            switch (entry.getType()) {
                case URI:
                    EntryUri uri = (EntryUri) entry;
                    xml.println("<uri xmlns='" + ResolverConstants.CATALOG_NS + "'");
                    xml.println("     xmlns:xr='" + ResolverConstants.XMLRESOURCE_EXT_NS + "'");
                    xml.println("     name='" + xmlEscape(uri.name) + "'");
                    xml.println("     uri='" + xmlEscape(uri.uri.toString()) + "'");
                    if (uri.nature != null) {
                        xml.println("     nature='" + xmlEscape(uri.nature) + "'");
                    }
                    if (uri.purpose != null) {
                        xml.println("     purpose='" + xmlEscape(uri.purpose) + "'");
                    }
                    break;
                case SYSTEM:
                    EntrySystem system = (EntrySystem) entry;
                    xml.println("<system xmlns='" + ResolverConstants.CATALOG_NS + "'");
                    xml.println("        xmlns:xr='" + ResolverConstants.XMLRESOURCE_EXT_NS + "'");
                    xml.println("        systemId='" + xmlEscape(system.systemId) + "'");
                    xml.println("        uri='" + xmlEscape(system.uri.toString()) + "'");
                    break;
                case PUBLIC:
                    EntryPublic pub = (EntryPublic) entry;
                    xml.println("<public xmlns='" + ResolverConstants.CATALOG_NS + "'");
                    xml.println("        xmlns:xr='" + ResolverConstants.XMLRESOURCE_EXT_NS + "'");
                    xml.println("        publicId='" + xmlEscape(pub.publicId) + "'");
                    xml.println("        uri='" + xmlEscape(pub.uri.toString()) + "'");
                    break;
                default:
                    error("Attempt to cache unexpected entry type.");
            }
            for (String name : entry.getProperties().keySet()) {
                if (entry.getProperty(name) != null) {
                    xml.println("     xr:" + name + "='" + xmlEscape(entry.getProperty(name)) + "'");
                }
            }
            xml.println("/>");
        }
    }

    public static String xmlEscape(String line) {
        return line.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("'", "&apos;");
    }

    // N.B. This method must only be called when a directory lock is held!
    protected void flushCache(Pattern uriPattern, long maxCount, long maxSize, File expired) {
        ArrayList<Entry> newEntries = new ArrayList<>();
        HashMap<Type, ArrayList<Entry>> newTypedEntries = new HashMap<>();
        ArrayList<CacheEntry> newCache = new ArrayList<>();

        int cacheCount = 0;
        long cacheSize = 0;

        // N.B. The entries in the cache are sorted in date order, so as soon as we pass
        // a threshold, we can burn everything that follows...
        int last = 0;
        for (CacheEntry entry : cached) {
            boolean add = true;
            if (uriPattern.matcher(entry.uri.toString()).find()) {
                cacheCount++;
                cacheSize += entry.file.length();
                add = cacheCount <= maxCount && cacheSize <= maxSize;
            }
            if (add) {
                newCache.add(entry);
                newEntries.add(entry.entry);
                if (!newTypedEntries.containsKey(entry.entry.getType())) {
                    newTypedEntries.put(entry.entry.getType(), new ArrayList<>());
                }
                newTypedEntries.get(entry.entry.getType()).add(entry.entry);
            } else {
                logger.log(AbstractLogger.CACHE, "Expiring %s (%s)", entry.file.getAbsolutePath(), entry.uri);
                entry.expired = true;
                File entryFile = new File(entry.entry.baseURI);
                Path source = entryFile.toPath();
                Path target = new File(expired, entryFile.getName()).toPath();
                try {
                    Files.move(source, target, REPLACE_EXISTING);
                } catch (IOException ex) {
                    logger.log(AbstractLogger.ERROR, "Attempt to expire cache entry failed: %s: %s",
                            entry.file.getAbsolutePath(), ex.getMessage());
                }
            }
        }

        cached.clear();
        cached.addAll(newCache);

        entries.clear();
        entries.addAll(newEntries);

        typedEntries.clear();
        for (Type type : newTypedEntries.keySet()) {
            typedEntries.put(type, newTypedEntries.get(type));
        }
    }

    // N.B. This method must only be called when a directory lock is held!
    protected void expire(CacheEntry entry) {
        entry.expired = true;
        cached.remove(entry);
        super.remove(entry.entry);
    }
}
