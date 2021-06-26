package org.xmlresolver.cache;

import java.util.regex.Pattern;

/**
 * Information about cached URIs.
 *
 * URI patterns (regular expression) can be included or excluded from the
 * cache selectively. The <code>CacheInfo</code> object provides the
 * parameters that will be applied when caching matching URIs.
 */
public class CacheInfo {
    /** Is this pattern cached? */
    public final boolean cache;
    /** The pattern (regular expression) that this CacheInfo matches. */
    public final String pattern;
    /** The compiled pattern. */
    public final Pattern uriPattern;
    /** How long are expired entries kept for this pattern? */
    public final long deleteWait;
    /** How many entries are allowed for this pattern? */
    public final long cacheSize;
    /** How much disk space may entries for this pattern occupy? */
    public final long cacheSpace;
    /** Entries older than <code>maxAge</code> will expire. */
    public final long maxAge;

    /** Create an entry with default parameters.
     * @param pattern The regular expression to match against.
     * @param cache Should this URI be included (true) or excluded (false) from the cache.
     * @see ResourceCache
     */
    protected CacheInfo(String pattern, boolean cache) {
        this(pattern, cache, ResourceCache.deleteWait, ResourceCache.cacheSize, ResourceCache.cacheSpace, ResourceCache.maxAge);
    }

    /** Create an entry with explicit parameters.
     * @param pattern The regular expression to match against.
     * @param cache Should this URI be included (true) or excluded (false) from the cache.
     * @param deleteWait How long are expired entries kept for this pattern?
     * @param cacheSize How many entries are allowed for this pattern?
     * @param cacheSpace How much disk space may entries for this pattern occupy?
     * @param maxAge Entries older than <code>maxAge</code> will expire.
     */
    protected CacheInfo(String pattern, boolean cache, long deleteWait, long cacheSize, long cacheSpace, long maxAge) {
        this.pattern = pattern;
        this.uriPattern = Pattern.compile(pattern);
        this.cache = cache;
        this.deleteWait = deleteWait;
        this.cacheSize = cacheSize;
        this.cacheSpace = cacheSpace;
        this.maxAge = maxAge;
    }

    @Override
    public String toString() {
        String str;
        if (cache) {
            str = "Cache include " + pattern;
        } else {
            str = "Cache exclude " + pattern;
        }

        return str;
    }
}
