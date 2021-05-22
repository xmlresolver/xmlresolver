package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;
import org.xmlresolver.cache.CacheInfo;
import org.xmlresolver.cache.ResourceCache;
import org.xmlresolver.utils.URIUtils;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CacheTest extends CacheManager {
    private static final String cacheDir = "build/test-cache";

    XMLResolverConfiguration config = null;
    ResourceResolver resolver = null;
    ResourceCache cache = null;

    @Before
    public void setup() {
        File cache = clearCache(cacheDir);

        config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.CATALOG_CACHE, cache.getAbsolutePath());
        this.cache = new ResourceCache(config);
    }

    @Test
    public void defaultInfo() {
        assertNotNull(cache.getCacheInfo("^file:"));
        assertNotNull(cache.getCacheInfo("^jar:file:"));
        assertNotNull(cache.getCacheInfo("^classpath:"));
        assertNull(cache.getCacheInfo("fribble"));
        assertEquals(3, cache.getCacheInfoList().size());
    }

    @Test
    public void addCacheInfoDefault() {
        cache.addCacheInfo("^fribble:", true);
        assertEquals(4, cache.getCacheInfoList().size());
        CacheInfo info = cache.getCacheInfo("^fribble:");
        assertEquals("^fribble:", info.pattern);
        assertEquals(ResourceCache.cacheSize, info.cacheSize);
        assertEquals(ResourceCache.cacheSpace, info.cacheSpace);
        assertEquals(ResourceCache.deleteWait, info.deleteWait);
        assertEquals(ResourceCache.maxAge, info.maxAge);
        cache.removeCacheInfo("^fribble:");
        assertEquals(3, cache.getCacheInfoList().size());
        assertNull(cache.getCacheInfo("^fribble:"));
    }

    @Test
    public void addCacheInfoExplicit() {
        cache.addCacheInfo("^fribble:", true);
        cache.addCacheInfo("^frabble:", false, 123, 456, 789, 10);
        assertEquals(5, cache.getCacheInfoList().size());
        CacheInfo info = cache.getCacheInfo("^frabble:");
        assertEquals("^frabble:", info.pattern);
        assertEquals(456, info.cacheSize);
        assertEquals(789, info.cacheSpace);
        assertEquals(123, info.deleteWait);
        assertEquals(10, info.maxAge);
        cache.removeCacheInfo("^frabble:");
        assertEquals(4, cache.getCacheInfoList().size());
        assertNull(cache.getCacheInfo("^frabble:"));
        assertNotNull(cache.getCacheInfo("^fribble:"));
        cache.removeCacheInfo("^fribble:");
        assertEquals(3, cache.getCacheInfoList().size());
        assertNull(cache.getCacheInfo("^fribble:"));
    }

    @Test
    public void addCacheSave() {
        cache.addCacheInfo("^fribble:", true);
        cache.addCacheInfo("^frabble:", true, 123, 456, 789, 10);

        XMLResolverConfiguration secondConfig = new XMLResolverConfiguration();
        secondConfig.setFeature(ResolverFeature.CATALOG_CACHE, URIUtils.cwd().resolve(cacheDir).getPath());
        ResourceCache secondCache = new ResourceCache(secondConfig);

        assertEquals(5, secondCache.getCacheInfoList().size());
        CacheInfo info = secondCache.getCacheInfo("^frabble:");
        assertEquals("^frabble:", info.pattern);
        assertEquals(456, info.cacheSize);
        assertEquals(789, info.cacheSpace);
        assertEquals(123, info.deleteWait);
        assertEquals(10, info.maxAge);
        secondCache.removeCacheInfo("^frabble:");
        assertEquals(4, secondCache.getCacheInfoList().size());
        assertNull(secondCache.getCacheInfo("^frabble:"));
        assertNotNull(secondCache.getCacheInfo("^fribble:"));
        secondCache.removeCacheInfo("^fribble:");
        assertEquals(3, secondCache.getCacheInfoList().size());
        assertNull(secondCache.getCacheInfo("^fribble:"));
    }

    @Test
    public void testCacheURI() {
        cache.addCacheInfo("^fribble:", true);
        cache.addCacheInfo("^frabble:", false, 123, 456, 789, 10);
        cache.addCacheInfo("\\.dtd$", false);

        assertTrue(cache.cacheURI("http://example.com"));
        assertTrue(cache.cacheURI("fribble://example.com"));
        assertFalse(cache.cacheURI("frabble://example.com"));
        assertFalse(cache.cacheURI("file:///foo"));
        assertFalse(cache.cacheURI("jar:file:///foo"));
        assertFalse(cache.cacheURI("classpath:whatever"));

        assertTrue(cache.cacheURI("jar:http://example.com"));
        assertFalse(cache.cacheURI("http://examle.com/path/to/some.dtd"));

        cache.removeCacheInfo("^fribble:");
        cache.removeCacheInfo("^frabble:");
        cache.removeCacheInfo("\\.dtd$");
    }
}

