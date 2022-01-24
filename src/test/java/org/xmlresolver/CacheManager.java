package org.xmlresolver;

import org.xmlresolver.utils.URIUtils;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class CacheManager {
    public static File clearCache(String relpath) {
        File cache = new File(URIUtils.cwd().getPath() + relpath);
        if (cache.exists()) {
            burnDirectory(cache);
        }
        return cache;
    }

    protected static void burnDirectory(File dir) {
        File[] contents = dir.listFiles();
        if (contents != null) {
            for (File file: contents) {
                burnDirectory(file);
            }
        }
        boolean deleted = dir.delete();
        assertTrue(deleted);
    }


}
