package org.xmlresolver.cache;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlresolver.*;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryUri;
import org.xmlresolver.logging.AbstractLogger;
import org.xmlresolver.utils.PublicId;
import org.xmlresolver.utils.URIUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Implements a resource cache.
 *
 * <p>Resources added to the cache are stored locally. Access to the cache is
 * through an OASIS XML Catalog returned by the <code>catalog()</code> method.
 * After a catalog search that includes the cache catalog, you can test if the
 * local copy of the resource is expired. If it is, it will be removed from
 * the cache.</p>
 *
 * <div>
 * <h2>Implementation details</h2>
 *
 * <p>The underlying cache is implemented using the filesystem. A cache directory
 * must be given when the cache is created. The <code>ResourceCache</code> must
 * have write access to the directory. The directory specified should be used
 * <em>exclusively</em> for the cache in order to avoid filename collisions and
 * other errors.</p>
 *
 * <p>The cache uses three subdirectories and two files:</p>
 *
 * <ul>
 * <li>Catalog entries for files in the cache are stored in the
 * <code>entry</code> directory.</li>
 * <li>The actual resources are stored in the <code>data</code>
 * directory.</li>
 * <li>Expired entries are stored in <code>expired</code>.</li>
 * <li>The <code>lock</code> file is used to maintain synchronized access
 * to the cache directory (multiple threads and even multiple applications
 * running perhaps in different VMs can share the same cache directory).
 * </li>
 * <li>The <code>control.xml</code> file can be used to configure the
 * cache.</li>
 * </ul>
 *
 * <p>When a resource is added to the cache, the data is stored in the
 * <code>data</code> directory
 * and a catalog entry is created in the <code>entry</code> directory.
 * After the entry has been added, the next request for the catalog will
 * include this entry.</p>
 *
 * <p>When a resource expires, its catalog entry file is moved to the
 * <code>expired</code> directory. The actual data is not removed right away
 * because other caches may still have that entry in their catalog.</p>
 *
 * <p>During catalog initialization, all of the the expired entries are scanned.
 * If any of them are older than a "delete wait" threshold, they are deleted
 * along with their data.</p>
 *
 * <p>The cache control file contains caching parameters:</p>
 *
 * <pre>&lt;cache-control xmlns="http://xmlresolver.org/ns/cache"
 * 	       max-age="86400" delete-wait="86400"
 * 	       size="1500" space="10m"&gt;
 * &lt;cache uri="http://www.w3.org/" max-age="172800" space="500k"/&gt;
 * &lt;no-cache uri="http://localhost/"/&gt;
 * &lt;cache uri="http://www.flickr.com/" max-age="0"/&gt;
 * &lt;cache uri="http://flickr.com/" max-age="0"/&gt;
 * &lt;/cache-control&gt;</pre>
 *
 * <p>If definitive information about the age of a resource cannot be
 * determined and it is older than "max-age", it will be treated as
 * out-of-date and its cached entry will be updated.</p>
 *
 * <p>Entries in the cache are sorted by age. If the cache exceeds "size"
 * entries or "space" bytes of storage, then the oldest entries are deleted.</p>
 *
 * <p>The <code>cache</code> and <code>no-cache</code> elements match URIs
 * by regular expression. If a <code>no-cache</code> entry matches, then the
 * URI is not cached. If no entry matches, the default is to cache with the
 * parameters specified on the <code>cache-control</code> element.</p>
 * </div>
 *
 * @author ndw
 */
public class ResourceCache extends CatalogManager {
    /** Length of time a cached resource remains available after it has been deleted. */
    public static final long deleteWait = 60*60*24*7; // 1 week
    /** The maximum size of the cache (in files). */
    public static final long cacheSize = 1000;
    /** The maximum size of the cache (in bytes). */
    public static final long cacheSpace = 1024 * 1000 * 10; // 10Mb
    /** The maximum age of a file in the cache. */
    public static final long maxAge = -1;
    /** The default cache pattern. */
    public static final String defaultPattern = ".*";
    /** Excluded patterns.
     *
     * <p>These patterns are excluded because they refer to files on the local filesystem.
     * If they are cached, then the resolver may resolve URIs from the cache that are "stale"
     * with respect to the files actually on the filesystem.</p>
     *
     * <p>The <code>jar:file:</code> and <code>classpath:</code> schemes are Java schemes.</p>
     * <p>The <code>path:</code> scheme as a .NET scheme (similar in effect to <code>classpath:</code>).</p>
     * <p>Both the Java and .NET schemes are excluded in case the cache is shared across languages.</p>
     */
    public static final String[] excludedPatterns = new String[]{"^file:", "^jar:file:", "^classpath:", "^path:"};

    private boolean loaded = false;
    private File cacheDir = null;
    private File dataDir = null;
    private File entryDir = null;
    private File expiredDir = null;

    private final CacheParser cacheParser;
    private final ArrayList<CacheInfo> cacheInfo = new ArrayList<> ();

    private CacheEntryCatalog catalog = null;
    private CacheInfo defaultCacheInfo = null;
    private String cacheVersion = null;

    public ResourceCache(ResolverConfiguration config) {
        super(config);

        if (!config.getFeature(ResolverFeature.CACHE_ENABLED)) {
            cacheDir = null;
            cacheParser = null;
            defaultCacheInfo = new CacheInfo(defaultPattern, false, deleteWait, cacheSize, cacheSpace, maxAge);
            return;
        }

        cacheParser = new CacheParser(config);

        // In case there is no control.xml file...
        defaultCacheInfo = new CacheInfo(defaultPattern, true, deleteWait, cacheSize, cacheSpace, maxAge);

        String dir = config.getFeature(ResolverFeature.CACHE_DIRECTORY);
        if (dir == null) {
            if (config.getFeature(ResolverFeature.CACHE_UNDER_HOME)) {
                dir = System.getProperty("user.home");
                if (dir != null) {
                    if (dir.endsWith("/")) {
                        dir += ".xmlresolver.org/cache";
                    } else {
                        dir += "/.xmlresolver.org/cache";
                    }
                }
            }
        }

        if (dir == null) {
            return;
        }

        File fDir = new File(dir);
        try {
            cacheDir = fDir.getCanonicalFile();
            logger.log(AbstractLogger.CACHE, "Cache dir: %s", cacheDir);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            if (!cacheDir.exists()) {
                logger.log(AbstractLogger.ERROR, "Cannot create cache directory: %s", cacheDir);
                cacheDir = null;
            }
        } catch (IOException ioe) {
            logger.log(AbstractLogger.ERROR, "IOException getting cache directory: %s", dir);
            cacheDir = null;
        }

        if (cacheDir == null) {
            return;
        }

        boolean update = false;
        boolean parsed = false;
        File control = new File(cacheDir, "control.xml");
        if (control.exists()) {
            try {
                SAXParserFactory spf = SAXParserFactory.newInstance();
                spf.setNamespaceAware(true);
                spf.setValidating(false);
                spf.setXIncludeAware(false);
                SAXParser parser = spf.newSAXParser();
                InputSource source = new InputSource(control.getAbsolutePath());
                parser.parse(source, new CacheHandler(deleteWait, cacheSize, cacheSpace, maxAge));
                parsed = true;

                // Caching file and classpath URIs is going to be confusing.
                // Unless these patterns are explicitly included, explicitly exclude them
                for (String pattern : excludedPatterns) {
                    CacheInfo info = getCacheInfo(pattern);
                    if (info == null) {
                        update = true;
                        cacheInfo.add(new CacheInfo(pattern, false));
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                logger.log(AbstractLogger.ERROR, "Failed to parse cache control file: %s", ex.getMessage());
            }
        } else {
            update = true;
        }

        if (!parsed) {
            // By default, do not cache the excluded patterns. If you do, changing
            // the files on your local filesystem will not have any effect when the documents
            // come from the cache!
            for (String pattern : excludedPatterns) {
                cacheInfo.add(new CacheInfo(pattern, false));
            }
        }

        if (update) {
            updateCacheControlFile();
        }
    }

    private void updateCacheControlFile() {
        if (cacheDir == null) {
            return;
        }

        File control = new File(cacheDir, "control.xml");
        try {
            PrintStream ps = new PrintStream(new FileOutputStream(control));
            ps.println("<cache-control version='2' xmlns='" + ResolverConstants.XMLRESOURCE_EXT_NS + "'>");
            for (CacheInfo info : cacheInfo) {
                if (info.cache) {
                    ps.print("<cache ");
                } else {
                    ps.print("<no-cache ");
                }
                ps.print("uri='" + CacheEntryCatalog.xmlEscape(info.pattern) + "'");
                ps.print(" delete-wait='" + info.deleteWait + "'");
                ps.print(" size='" + info.cacheSize + "'");
                ps.print(" space='" + info.cacheSpace + "'");
                ps.println(" max-age='" + info.maxAge + "'/>");
            }
            ps.println("</cache-control>\n");
            ps.close();
        } catch (SecurityException|IOException ex) {
            logger.log(AbstractLogger.CACHE, "Failed to write cache control file: %s: %s",
                    control.getAbsolutePath(), ex.getMessage());
        }
    }

    /** Returns the cache directory associated with this ResourceCache.
     *
     * @return The absolute path of the cache directory, or null if it could not be configured with the requested directory.
     */
    public String directory() {
        return cacheDir == null ? null : cacheDir.getAbsolutePath();
    }

    public List<CacheInfo> getCacheInfoList() {
        return new ArrayList<>(cacheInfo);
    }

    public CacheInfo getCacheInfo(String pattern) {
        if (pattern == null) {
            return null;
        }
        for (CacheInfo info: cacheInfo) {
            if (pattern.equals(info.pattern)) {
                return info;
            }
        }
        return null;
    }

    public CacheInfo getDefaultCacheInfo() {
        return defaultCacheInfo;
    }

    public CacheInfo addCacheInfo(String pattern, boolean cache) {
        return addCacheInfo(pattern, cache, deleteWait, cacheSize, cacheSpace, maxAge);
    }

    public CacheInfo addCacheInfo(String pattern, boolean cache, long deleteWait, long cacheSize, long cacheSpace, long maxAge) {
        CacheInfo info = new CacheInfo(pattern, cache, deleteWait, cacheSize, cacheSpace, maxAge);
        removeCacheInfo(pattern, false);
        cacheInfo.add(info);
        updateCacheControlFile();
        return info;
    }

    public void removeCacheInfo(String pattern) {
        removeCacheInfo(pattern, true);
    }

    private void removeCacheInfo(String pattern, boolean writeUpdate) {
        CacheInfo info = getCacheInfo(pattern);
        while (info != null) {
            cacheInfo.remove(info);
            info = getCacheInfo(pattern);
        }
        if (writeUpdate) {
            updateCacheControlFile();
        }
    }

    public List<CacheEntry> entries() {
        loadCache();
        return new ArrayList<>(catalog.cached);
    }

    /** Attempts to determine if the local copy is out of date.
     *
     * <p>N.B. Calling this function will remove expired entries from the cache!</p>
     *
     * <p>If the URI is an <code>http:</code> URI, a HEAD request is made and the
     * <code>cachedTime()</code> and <code>etag</code> are compared. If the resource
     * on the web is more recent, <code>true</code> is returned.</p>
     *
     * <p>If the resource isn't cached, isn't an <code>http:</code> URI, or an error
     * occurs attempting to get the HEAD, then <code>false</code> is returned.</p>
     *
     * @param local The local URI as found in cache.
     * @return True if and only if the resource is expired.
     */
    public boolean expired(URI local) {
        if (local == null) {
            return false;
        }

        // If we're explicitly offline, then nothing is expired.
        String offline = System.getProperty("xmlresolver.offline");
        if (offline != null && !"false".equals(offline) && !"0".equals(offline) && !"no".equals(offline)) {
            return false;
        }

        loadCache();
        if (cacheDir == null) {
            return true;
        }

        // Find the entry for this URI
        CacheEntry entry = null;
        for (CacheEntry search : catalog.cached) {
            if (local.equals(search.file.toURI())) {
                entry = search;
                break;
            }
        }

        if (entry == null) {
            // The URI isn't in the cache? This should never happen, but if it does,
            // the entry is definitely expired!
            return true;
        }

        // Find the cache info record for this entry
        CacheInfo info = null;
        for (int count = 0; info == null && count < cacheInfo.size(); count++) {
            CacheInfo chk = cacheInfo.get(count);
            if (chk.uriPattern.matcher(entry.uri.toString()).find()) {
                info = chk;
            }
        }

        if (info == null) {
            info = defaultCacheInfo;
        }

        // If this entry isn't supposed to be cached, then it's definitely expired.
        if (!info.cache) {
            // Cleanup the cache if someone changed a pattern
            CacheInfo cleanup = new CacheInfo(info.pattern, false, deleteWait, 0, 0, 0);
            flushCache(cleanup);
            return true;
        }

        // Find out how many matching entries are in the cache and how much space they occupy
        int cacheCount = 0;
        long cacheSize = 0;
        for (CacheEntry search : catalog.cached) {
            if (search.entry.getType() == Entry.Type.PUBLIC || search.expired) {
                // If it's public, it must also have a system entry, don't count it twice.
                // If we already expired it last time around, don't bother expiring it again.
                continue;
            }

            if (info.uriPattern.matcher(search.uri.toString()).find()) {
                cacheCount++;
                cacheSize += search.file.length();
            }
        }

        // Flush oldest entries...
        if (cacheCount > info.cacheSize || cacheSize > info.cacheSpace) {
            logger.log(AbstractLogger.CACHE, "Too many cache entries, or cache size too large: expiring oldest entries");
            flushCache(info);
        }

        if (entry.expired) {
            return true;
        }

        long cacheTime = entry.time;
        String cachedEtag = entry.etag();

        ResourceConnection rconn = new ResourceConnection(resolverConfiguration, entry.uri.toASCIIString(), true);
        rconn.close();
        String etag = rconn.getEtag();
        long lastModified = rconn.getLastModified();
        if ("".equals(etag)) {
            etag = null;
        }
        if (lastModified < 0 && (etag == null || cachedEtag == null)) {
            // Hmm. We're not sure when it was last modified?
            long maxAge = info.maxAge;
            if (maxAge >= 0) {
                long oldest = new Date().getTime() - (maxAge * 1000);
                if (maxAge == 0 || cacheTime < oldest) {
                    return true;
                }
            }
            lastModified = rconn.getDate();
        }

        if (rconn.getStatusCode() != 200) {
            logger.log(AbstractLogger.CACHE, "Not expired: %s (HTTP %d)", entry.uri, rconn.getStatusCode());
            return false;
        }

        boolean etagsDiffer = (etag != null && cachedEtag != null && !etag.equals(cachedEtag));

        if (lastModified < 0) {
            if (etagsDiffer) {
                logger.log(AbstractLogger.CACHE, "Expired: %s (no last-modified header, etags differ)", entry.uri);
                return true;
            } else {
                logger.log(AbstractLogger.CACHE, "Not expired: %s (no last-modified header, etags identical)", entry.uri);
                return false;
            }
        } else if (lastModified > cacheTime || etagsDiffer) {
            logger.log(AbstractLogger.CACHE, "Expired: %s", entry.uri);
            catalog.expire(entry);
            return true;
        } else {
            logger.log(AbstractLogger.CACHE, "Not expired: %s", entry.uri);
            return false;
        }
    }

    /** Returns true if the specified absolute URI should be cached.
     *
     * @param uri The URI
     * @return Whether or not it should be cached
     */
    public boolean cacheURI(String uri) {
        loadCache();
        if (cacheDir == null) {
            return false;
        }

        // If we get something that isn't an absolute URI, assume it
        // should be resolved against the cwd.
        uri = URIUtils.cwd().resolve(uri).toString();

        // Find the cache info record for this entry
        CacheInfo info = null;
        for (int count = 0; info == null && count < cacheInfo.size(); count++) {
            CacheInfo chk = cacheInfo.get(count);
            if (chk.uriPattern.matcher(uri).find()) {
                info = chk;
            }
        }

        if (info == null) {
            info = defaultCacheInfo;
        }

        logger.log(AbstractLogger.CACHE, "Cache cacheURI: %s (%s)", info.cache, uri);

        return info.cache;
    }

    @Override
    public URI lookupURI(String uri) {
        loadCache();
        if (cacheDir == null) {
            return null;
        }
        URI local = super.lookupURI(uri);
        return expired(local) ? null : local;
    }

    @Override
    public URI lookupNamespaceURI(String uri, String nature, String purpose) {
        loadCache();
        if (cacheDir == null) {
            return null;
        }
        URI local = super.lookupNamespaceURI(uri, nature, purpose);
        return expired(local) ? null : local;
    }

    @Override
    public URI lookupPublic(String systemId, String publicId) {
        loadCache();
        if (cacheDir == null) {
            return null;
        }
        URI local = super.lookupPublic(systemId, publicId);
        return expired(local) ? null : local;
    }

    @Override
    public URI lookupSystem(String systemId) {
        loadCache();
        if (cacheDir == null) {
            return null;
        }
        URI local = super.lookupSystem(systemId);
        return expired(local) ? null : local;
    }

    public CacheEntry cachedUri(URI uri) {
        return cachedNamespaceUri(uri, null, null);
    }

    public CacheEntry cachedNamespaceUri(URI uri, String nature, String purpose) {
        CacheEntry entry = findNamespaceCacheEntry(uri, nature, purpose);
        if (entry == null || entry.expired) {
            if (cacheURI(uri.toString())) {
                try {
                    ResourceConnection conn = new ResourceConnection(resolverConfiguration, uri.toString());
                    if (conn.getStream() != null && conn.getStatusCode() == 200) {
                        entry = addNamespaceURI(conn, nature, purpose);
                    }
                } catch (NoClassDefFoundError ncdfe) {
                    logger.log(AbstractLogger.ERROR, "Apache HTTP library classes apparently unavailable, not attempting to cache");
                    return null;
                }
            }
        }
        return entry;
    }

    public CacheEntry cachedSystem(URI systemId, String publicId) {
        CacheEntry entry = findSystemCacheEntry(systemId);
        if (entry == null || entry.expired) {
            if (cacheURI(systemId.toString())) {
                try {
                    ResourceConnection conn = new ResourceConnection(resolverConfiguration, systemId.toString());
                    if (conn.getStatusCode() == 200 && conn.getStream() != null) {
                        entry = addSystem(conn, publicId);
                    }
                } catch (NoClassDefFoundError ncdfe) {
                    logger.log(AbstractLogger.ERROR, "Apache HTTP library classes apparently unavailable, not attempting to cache");
                    return null;
                }
            }
        }
        return entry;
    }

    private synchronized void loadCache() {
        if (loaded) {
            return;
        }
        loaded = true;

        if (cacheDir == null) {
            return;
        }

        catalog = new CacheEntryCatalog(resolverConfiguration, cacheDir.toURI(), null, true);
        dataDir = new File(cacheDir, "data");
        entryDir = new File(cacheDir, "entry");
        expiredDir = new File(cacheDir, "expired");

        if ((!dataDir.exists() && !dataDir.mkdir())
                || (!entryDir.exists() && !entryDir.mkdir())
                || (!expiredDir.exists() && !expiredDir.mkdir())) {
            logger.log(AbstractLogger.CACHE, "Failed to setup data, entry, and expired directories in cache");
            cacheDir = null;
            return;
        }

        DirectoryLock lock = new DirectoryLock();
        try {
            lock.lock();
        } catch (IOException ex) {
            logger.log(AbstractLogger.CACHE, "Failed to obtain lock on cache: " + ex.getMessage());
            return;
        }

        cleanupCache();

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(false);
        spf.setXIncludeAware(false);
        EntryHandler handler = new EntryHandler(entryDir.toURI());
        File[] files = entryDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.canRead()) {
                    try {
                        SAXParser parser = spf.newSAXParser();
                        InputSource source = new InputSource(file.toURI().toASCIIString());
                        handler.setBaseURI(file.toURI());
                        parser.parse(source, handler);
                    } catch (ParserConfigurationException | SAXException | IOException ex) {
                        logger.log(AbstractLogger.CACHE, "Failed to read cache entry: " + file.toURI() + ": " + ex.getMessage());
                    }
                }
            }
        }

        lock.unlock();
    }

    private void cleanupCache() {
        long now = new Date().getTime();
        long threshold = 24 * 3600 * 1000; // Cleanup once a day
        long age = 0;

        File cleanup = new File(cacheDir, "cleanup");
        if (cleanup.exists()) {
            age = now - cleanup.lastModified();
        } else {
            try {
                OutputStreamWriter fos = new OutputStreamWriter(new FileOutputStream(cleanup));
                fos.write("The timestamp on this file indicates when the cache was last pruned.\n");
                fos.close();
            } catch (SecurityException | IOException cex) {
                logger.log(AbstractLogger.CACHE, "Failed to create cache cleanup file" + cleanup.getAbsolutePath());
                return;
            }
            age = threshold + 1;
        }

        if (age > threshold) {
            logger.log(AbstractLogger.CACHE, "Cleaning up expired cache entries");

            // if there are any expired entries that are too old, remove them
            File[] files = expiredDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    age = now - file.lastModified();
                    if (age > deleteWait * 1000) {
                        logger.log(AbstractLogger.CACHE, "Deleting expired entry: %s", file.getName());
                        if (!file.delete()) {
                            logger.log(AbstractLogger.CACHE, "Failed to delete expired cache entry: " + file.getAbsolutePath());
                        }
                    }
                }
            }

            // if there are any files in the data dir that don't have an entry, remove them
            files = dataDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    String entryName = file.getName();
                    entryName = entryName.substring(0, entryName.lastIndexOf("."));
                    File entry = new File(entryDir, entryName + ".xml");
                    if (!entry.exists()) {
                        entry = new File(expiredDir, entryName + ".xml");
                        if (!entry.exists()) {
                            logger.log(AbstractLogger.CACHE, "Deleting expired data: %s", file.getName());
                            if (!file.delete()) {
                                logger.log(AbstractLogger.CACHE, "Failed to delete expired cache entry: " + file.getAbsolutePath());
                            }
                        }
                    }
                }
            }

            // if there are any entries that don't have data associated with them, remove them
            files = entryDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    String entryName = file.getName();
                    entryName = entryName.substring(0, entryName.lastIndexOf("."));

                    boolean found = false;
                    File[] dfiles = dataDir.listFiles();
                    if (dfiles != null) {
                        for (int dpos = 0; !found && dpos < dfiles.length; dpos++) {
                            String dataName = dfiles[dpos].getName();
                            dataName = dataName.substring(0, dataName.lastIndexOf("."));
                            found = dataName.equals(entryName);
                        }
                    }

                    if (!found) {
                        logger.log(AbstractLogger.CACHE, "Deleting dangling entry: %s", file.getName());
                        if (!file.delete()) {
                            logger.log(AbstractLogger.CACHE, "Failed to delete expired cache entry: " + file.getAbsolutePath());
                        }
                    }
                }
            }

            if (!cleanup.setLastModified(now)) {
                logger.log(AbstractLogger.CACHE, "Failed to update last modified time of cache cleanup file");
            }
        }
    }

    /** Add a Namespace URI to the cache.
     *
     * <p>This method reads the supplied InputStream, storing the resource locally, and returns the name
     * of the local file where that body may be retrieved.</p>
     *
     * @param connection The URL connection.
     * @param nature The RDDL nature of the resource.
     * @param purpose the RDDL purpose of the resource.
     * @return The filename of the cached resource.
     * @throws IOException if an I/O error occurs
     */
    private CacheEntry addNamespaceURI(ResourceConnection connection, String nature, String purpose) {
        loadCache();
        if (cacheDir == null) {
            logger.log(AbstractLogger.CACHE, "Attempting to cache URI, but no cache is available");
            return null;
        }

        DirectoryLock lock = new DirectoryLock();
        try {
            lock.lock();
        } catch (IOException ex) {
            return null;
        }

        URI name = connection.getURI();
        if (nature == null && purpose == null) {
            logger.log(AbstractLogger.CACHE, "Caching resource for uri: %s", name);
        } else {
            logger.log(AbstractLogger.CACHE, "Caching resource for namespace: %s (nature: %s, purpose: %s)",
                    name, nature, purpose);
        }

        String contentType = connection.getContentType();
        InputStream resource = connection.getStream();

        File localFile = null;

        try {
            String basefn = cacheBaseName(name);

            File entryFile = new File(entryDir, basefn + ".xml");
            localFile = new File(dataDir, basefn + pickSuffix(name, contentType));
            // Now copy the input stream into our cache copy
            storeStream(resource, localFile);
            resource.close();
            String uri = localFile.getAbsolutePath();

            long now = new Date().getTime();
            Entry entry = catalog.addUri(entryFile.toURI(), name.toString(), uri, nature, purpose, now);
            entry.setProperty("contentType", contentType);
            entry.setProperty("time", ""+now);
            String prop = null;
            if (connection.getRedirect() != null) {
                prop = connection.getRedirect().toString();
            }
            if (prop != null) {
                entry.setProperty("redir", prop);
            }
            prop = connection.getEtag();
            if (prop != null) {
                entry.setProperty("etag", prop);
            }
            entry.setProperty("filesize", ""+localFile.length());
            entry.setProperty("filemodified", ""+localFile.lastModified());

            catalog.writeCacheEntry(entry, entryFile);
        } catch (NoSuchAlgorithmException nsae) {
            logger.log(AbstractLogger.CACHE, "Failed to obtain SHA-256 digest?");
        } catch (IOException ioe) {
            logger.log(AbstractLogger.ERROR, "Failed to cache resource '%s' to '%s'", name, localFile.getAbsolutePath());
        } finally {
            lock.unlock();
        }

        return findNamespaceCacheEntry(name, nature, purpose);
    }

    private synchronized CacheEntry addSystem(ResourceConnection connection, String publicId) {
        loadCache();
        if (cacheDir == null) {
            logger.log(AbstractLogger.CACHE, "Attempting to cache system ID, but no cache is available");
            return null;
        }

        DirectoryLock lock = new DirectoryLock();
        try {
            lock.lock();
        } catch (IOException ex) {
            logger.log(AbstractLogger.ERROR, "Failed to obtain directory lock to cache resource: %s", connection.getURI());
            return null;
        }

        URI name = connection.getURI();
        String contentType = connection.getContentType();
        InputStream resource = connection.getStream();

        logger.log(AbstractLogger.CACHE, "Caching systemId: %s", name);
        File localFile = null;

        try {
            String basefn = cacheBaseName(name);

            File entryFile = new File(entryDir, basefn + ".xml");
            localFile = new File(dataDir, basefn + pickSuffix(name, contentType));
            // Now copy the input stream into our cache copy
            storeStream(resource, localFile);
            resource.close();
            String uri = localFile.getAbsolutePath();

            long now = new Date().getTime();
            Entry entry = catalog.addSystem(entryFile.toURI(), name.toString(), uri, now);
            entry.setProperty("contentType", contentType);
            entry.setProperty("time", ""+now);
            String prop = null;
            if (connection.getRedirect() != null) {
                prop = connection.getRedirect().toString();
            };
            if (prop != null) {
                entry.setProperty("redir", prop);
            }
            prop = connection.getEtag();
            if (prop != null) {
                entry.setProperty("etag", prop);
            }
            entry.setProperty("filesize", ""+localFile.length());
            entry.setProperty("filemodified", ""+localFile.lastModified());

            catalog.writeCacheEntry(entry, entryFile);

            if (publicId != null) {
                basefn = cacheBaseName(PublicId.encodeURN(publicId));
                entryFile = new File(entryDir, basefn + ".xml");
                entry = catalog.addPublic(entryFile.toURI(), publicId, uri, now);
                entry.setProperty("contentType", contentType);
                entry.setProperty("time", ""+now);
                prop = null;
                if (connection.getRedirect() != null) {
                    prop = connection.getRedirect().toString();
                }
                if (prop != null) {
                    entry.setProperty("redir", prop);
                }
                prop = connection.getEtag();
                if (prop != null) {
                    entry.setProperty("etag", prop);
                }
                entry.setProperty("filesize", ""+localFile.length());
                entry.setProperty("filemodified", ""+localFile.lastModified());

                catalog.writeCacheEntry(entry, entryFile);
            }
        } catch (NoSuchAlgorithmException nsae) {
            logger.log(AbstractLogger.CACHE, "Failed to obtain SHA-256 digest?");
        } catch (IOException ioe) {
            logger.log(AbstractLogger.ERROR, "Failed to cache resource '%s' to '%s'", name, localFile.getAbsolutePath());
        } finally {
            lock.unlock();
        }

        return findSystemCacheEntry(name);
    }

    private CacheEntry findNamespaceCacheEntry(URI uri, String nature, String purpose) {
        if (uri == null) {
            return null;
        }

        loadCache();
        if (cacheDir == null) {
            return null;
        }

        // Find the entry for this URI
        for (CacheEntry search : catalog.cached) {
            if (search.entry.getType() == Entry.Type.URI && uri.equals(search.uri)) {
                EntryUri entry = (EntryUri) search.entry;
                if ((nature == null || entry.nature == null || nature.equals(entry.nature))
                        && (purpose == null || entry.purpose == null || purpose.equals(entry.purpose))) {
                    return search;
                }
            }
        }

        return null;
    }

    private CacheEntry findSystemCacheEntry(URI systemId) {
        if (systemId == null) {
            return null;
        }

        loadCache();
        if (cacheDir == null) {
            return null;
        }

        // Find the entry for this URI
        for (CacheEntry search : catalog.cached) {
            if (search.entry.getType() == Entry.Type.SYSTEM && systemId.equals(search.uri)) {
                return search;
            }
        }

        return null;
    }

    private void flushCache(CacheInfo info) {
        DirectoryLock lock = new DirectoryLock();
        try {
            lock.lock();
            catalog.flushCache(info.uriPattern, info.cacheSize, info.cacheSpace, expiredDir);
            lock.unlock();
        } catch (IOException ex) {
            logger.log(AbstractLogger.ERROR, "Failed to obtain lock to expire cache");
        }
    }

    private String cacheBaseName(URI name) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashbytes = digest.digest(name.toASCIIString().getBytes(StandardCharsets.UTF_8));
        StringBuilder hexbuilder = new StringBuilder(hashbytes.length * 2);
        for (byte b : hashbytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() < 2) {
                hexbuilder.append("0");
            }
            hexbuilder.append(hex);
        }
        return hexbuilder.toString();
    }

    private void storeStream(InputStream resource, File localFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(localFile);
        byte[] buf = new byte[8192];
        int read = resource.read(buf);
        while (read > 0) {
            fos.write(buf, 0, read);
            read = resource.read(buf);
        }
        fos.close();
    }

    private String pickSuffix(URI uri, String contentType) {
        String suffix = uri.toASCIIString();
        int pos = suffix.lastIndexOf(".");
        if (pos > 0) {
            suffix = suffix.substring(pos);
            if (suffix.length() <= 5) {
                return suffix;
            }
        }

        if (contentType == null) {
            return ".bin";
        }

        if ("application/xml-dtd".equals(contentType)) {
            return ".dtd";
        }

        if (contentType.contains("application/xml")) {
            return ".xml";
        }

        if (contentType.contains("text/html") || contentType.contains("application/html+xml")) {
            return ".html";
        }

        if (contentType.contains("text/plain")) {
            if (uri.toString().endsWith(".dtd")) {
                return ".dtd";
            }
            return ".txt";
        }

        return ".bin";
    }

    // ============================================================

    private class DirectoryLock {
        private RandomAccessFile lockFile = null;
        private FileChannel lockChannel = null;
        private FileLock lock = null;

        DirectoryLock() {
            try {
                File lockF = new File(cacheDir.toString()+"/lock");
                lockFile = new RandomAccessFile(lockF, "rw");
                lockChannel = lockFile.getChannel();
                lock = lockChannel.tryLock();
            } catch (IOException| OverlappingFileLockException ex) {
                // nop;
            }
        }

        boolean locked() {
            return lock != null;
        }

        void lock() throws IOException {
            while (lock == null) {
                try {
                    lock = lockChannel.lock();
                } catch (OverlappingFileLockException ex) {
                    try {
                        // Let's wait and see if this clears
                        Thread.sleep(500);
                    } catch (InterruptedException iex) {
                        // I don't care.
                    }
                }
            }
        }

        void unlock() {
            try {
                lock.release();
                lockFile.close();
                lock = null;
            } catch (IOException ioe) {
                // nop;
            }
        }
    }

    private class CacheHandler extends DefaultHandler {

        private final long default_deleteWait;
        private final long default_cacheSize;
        private final long default_cacheSpace;
        private final long default_maxAge;

        private boolean isControl = false;
        private int depth = 0;

        public CacheHandler(long deleteWait, long cacheSize, long cacheSpace, long maxAge) {
            default_deleteWait = deleteWait;
            default_cacheSize = cacheSize;
            default_cacheSpace = cacheSpace;
            default_maxAge = maxAge;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            long deleteWait = default_deleteWait;
            long cacheSize = default_cacheSize;
            long cacheSpace = default_cacheSpace;
            long maxAge = default_maxAge;

            if (depth == 0) {
                isControl = ResolverConstants.XMLRESOURCE_EXT_NS.equals(uri) && "cache-control".equals(localName);
                if (isControl) {
                    cacheVersion = attributes.getValue("", "version");
                    deleteWait = cacheParser.parseTimeLong(attributes.getValue("", "delete-wait"), default_deleteWait);
                    cacheSize = cacheParser.parseLong(attributes.getValue("", "size"), default_cacheSize);
                    cacheSpace = cacheParser.parseSizeLong(attributes.getValue("", "space"), default_cacheSpace);
                    maxAge = cacheParser.parseTimeLong(attributes.getValue("", "max-age"), default_maxAge);
                    defaultCacheInfo = new CacheInfo(defaultPattern, true, deleteWait, cacheSize, cacheSpace, maxAge);
                }
            }

            if (isControl && depth == 1 && ResolverConstants.XMLRESOURCE_EXT_NS.equals(uri)) {
                deleteWait = cacheParser.parseTimeLong(attributes.getValue("", "delete-wait"), default_deleteWait);
                cacheSize = cacheParser.parseLong(attributes.getValue("", "size"), default_cacheSize);
                cacheSpace = cacheParser.parseSizeLong(attributes.getValue("", "space"), default_cacheSpace);
                maxAge = cacheParser.parseTimeLong(attributes.getValue("", "max-age"), default_maxAge);
                String cacheRegex = attributes.getValue("", "uri");
                switch (localName) {
                    case "cache":
                        if (cacheRegex != null) {
                            CacheInfo info = new CacheInfo(cacheRegex, true, deleteWait, cacheSize, cacheSpace, maxAge);
                            cacheInfo.add(info);
                        }
                        break;
                    case "no-cache":
                        if (cacheRegex != null) {
                            CacheInfo info = new CacheInfo(cacheRegex, false, deleteWait, cacheSize, cacheSpace, maxAge);
                            cacheInfo.add(info);
                        }
                        break;
                    default:
                        logger.log(AbstractLogger.ERROR, "Unexpected element in cache control file: %s", localName);
                        break;
                }
            }

            depth++;
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            depth--;
        }

    }

    private class EntryHandler extends DefaultHandler {
        private boolean root = false;
        private URI baseURI = null;

        public EntryHandler(URI baseURI) {
            this.baseURI = baseURI;
        }

        public void setBaseURI(URI baseURI) {
            this.baseURI = baseURI;
        }

        @Override
        public void startDocument() {
            root = true;
        }

        @Override
        public void startElement(String nsuri, String localName, String qName, Attributes attributes) {
            if (!ResolverConstants.CATALOG_NS.equals(nsuri)) {
                root = false;
                return;
            }

            if ("catalog".equals(localName)) {
                // Ignore an outer 'catalog' wrapper. Technically, this will ignore an arbitrary
                // number of such wrappers, but let's not worry about that.
                return;
            }

            if (root) {
                root = false;
                String name = attributes.getValue("", "name");
                String uri = attributes.getValue("", "uri");
                long timestamp = -1;

                String longStr = attributes.getValue(ResolverConstants.XMLRESOURCE_EXT_NS, "time");
                if (longStr != null) {
                    try {
                        timestamp = Long.parseLong(longStr);
                    } catch (NumberFormatException ex) {
                        logger.log(AbstractLogger.ERROR, "Bad numeric value in cache file: %s", longStr);
                        return;
                    }
                }

                URI localURI;
                try {
                    localURI = URIUtils.newURI(uri);
                } catch (URISyntaxException ex) {
                    logger.log(AbstractLogger.ERROR, "Cached URI is invalid: %s", uri);
                    return;
                }

                File local = new File(localURI.getPath());
                if (!local.exists()) {
                    logger.log(AbstractLogger.CACHE, "Cached resource disappeared: %s", uri);
                    return;
                }

                Entry entry = null;
                switch (localName) {
                    case "uri":
                        String nature = attributes.getValue("", "nature");
                        String purpose = attributes.getValue("", "purpose");
                        entry = catalog.addUri(baseURI, name, uri, nature, purpose, timestamp);
                        break;
                    case "system":
                        String systemId = attributes.getValue("", "systemId");
                        entry = catalog.addSystem(baseURI, systemId, uri, timestamp);
                        break;
                    case "public":
                        String publicId = attributes.getValue("", "publicId");
                        entry = catalog.addPublic(baseURI, publicId, uri, timestamp);
                        break;
                    default:
                        logger.log(AbstractLogger.CACHE, "Unexpected cache entry: " + localName);
                        return;
                }

                for (int pos = 0; pos < attributes.getLength(); pos++) {
                    if (ResolverConstants.XMLRESOURCE_EXT_NS.equals(attributes.getURI(pos))) {
                        entry.setProperty(attributes.getLocalName(pos), attributes.getValue(pos));
                    }
                }

                entry.setProperty("filesize", ""+local.length());
                entry.setProperty("filemodified", ""+local.lastModified());
            }
        }
    }
}
