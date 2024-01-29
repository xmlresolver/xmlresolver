package org.xmlresolver;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.fail;

public class FeatureTest {
    private void knownFeature(ResolverConfiguration config, ResolverFeature<?> feature) {
        Iterator<ResolverFeature<?>> iter = config.getFeatures();
        while (iter.hasNext()) {
            ResolverFeature<?> testFeature = iter.next();
            if (testFeature == feature) {
                return;
            }
        }
        System.err.println("Unknown feature: " + feature);
        fail();
    }

    private void booleanFeature(ResolverFeature<Boolean> feature) {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        knownFeature(config, feature);
        boolean orig = config.getFeature(feature);
        config.setFeature(feature, false);
        Assert.assertEquals(false, config.getFeature(feature));
        config.setFeature(feature, true);
        Assert.assertEquals(true, config.getFeature(feature));
        config.setFeature(feature, orig);
    }

    private void stringFeature(ResolverFeature<String> feature) {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        knownFeature(config, feature);
        String orig = config.getFeature(feature);
        config.setFeature(feature, "apple pie");
        Assert.assertEquals("apple pie", config.getFeature(feature));
        config.setFeature(feature, orig);
    }

    @Test
    public void testFeatureAllowCatalogPi() {
        booleanFeature(ResolverFeature.ALLOW_CATALOG_PI);
    }

    @Test
    public void testFeatureArchivedCatalogs() {
        booleanFeature(ResolverFeature.ARCHIVED_CATALOGS);
    }

    @Test
    public void testFeatureCatalogAdditions() {
        ResolverConfiguration config = new XMLResolverConfiguration();
        knownFeature(config, ResolverFeature.CATALOG_FILES);
        List<String> origCatalogs = config.getFeature(ResolverFeature.CATALOG_FILES);
        List<String> myCatalogs = Arrays.asList("CatOne", "CatTwo", "CatThree");
        config.setFeature(ResolverFeature.CATALOG_FILES, myCatalogs);

        knownFeature(config, ResolverFeature.CATALOG_ADDITIONS);
        List<String> origAdditional = config.getFeature(ResolverFeature.CATALOG_ADDITIONS);
        List<String> myList = Arrays.asList("One", "Two", "Three");
        config.setFeature(ResolverFeature.CATALOG_ADDITIONS, myList);
        List<String> current = config.getFeature(ResolverFeature.CATALOG_ADDITIONS);
        Assert.assertNotNull(current);
        Assert.assertEquals(myList.size(), current.size());
        Assert.assertArrayEquals(myList.toArray(), current.toArray());

        current = config.getFeature(ResolverFeature.CATALOG_FILES);
        // Kindof a hack, but make sure they're all present
        boolean found = false;
        for (String mine : myCatalogs) {
            for (String catalog : current) {
                found = found || mine.equals(catalog);
            }
        }
        for (String mine : myList) {
            for (String catalog : current) {
                found = found || mine.equals(catalog);
            }
        }
        Assert.assertTrue(found);

        config.setFeature(ResolverFeature.CATALOG_FILES, origCatalogs);
        config.setFeature(ResolverFeature.CATALOG_ADDITIONS, null);

        current = config.getFeature(ResolverFeature.CATALOG_ADDITIONS);
        Assert.assertEquals(0, current.size());
    }

    @Test
    public void testFeatureCatalogFiles() {
        ResolverConfiguration config = new XMLResolverConfiguration();
        knownFeature(config, ResolverFeature.CATALOG_FILES);
        List<String> orig = config.getFeature(ResolverFeature.CATALOG_FILES);
        List<String> myList = Arrays.asList("One", "Two", "Three");
        config.setFeature(ResolverFeature.CATALOG_FILES, myList);
        List<String> current = config.getFeature(ResolverFeature.CATALOG_FILES);

        // Kindof a hack, but make sure they're all present
        for (String mine : myList) {
            boolean found = false;
            for (String catalog : current) {
                found = found || mine.equals(catalog);
            }
            Assert.assertTrue(found);
        }

        // Setting CATALOG_FILES replaces the catalogs!

        List<String> newList = Arrays.asList("Alpha", "Beta", "Gamma");
        config.setFeature(ResolverFeature.CATALOG_FILES, newList);
        current = config.getFeature(ResolverFeature.CATALOG_FILES);

        // Kindof a hack, but make sure they're NOT present
        for (String mine : myList) {
            boolean found = false;
            for (String catalog : current) {
                found = found || mine.equals(catalog);
            }
            Assert.assertFalse(found);
        }

        // Kindof a hack, but make sure they're all present
        for (String mine : newList) {
            boolean found = false;
            for (String catalog : current) {
                found = found || mine.equals(catalog);
            }
            Assert.assertTrue(found);
        }

        config.setFeature(ResolverFeature.CATALOG_FILES, orig);
    }

    @Test
    public void testFeatureCatalogLoaderClass() {
        stringFeature(ResolverFeature.CATALOG_LOADER_CLASS);
    }

    @Test
    public void testFeatureCatalogManager() {
        ResolverConfiguration config = new XMLResolverConfiguration();
        knownFeature(config, ResolverFeature.CATALOG_MANAGER);
        CatalogManager manager = new CatalogManager(config);
        CatalogManager orig = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        config.setFeature(ResolverFeature.CATALOG_MANAGER, manager);
        Assert.assertEquals(manager, config.getFeature(ResolverFeature.CATALOG_MANAGER));
        config.setFeature(ResolverFeature.CATALOG_MANAGER, orig);
    }

    @Test
    public void testFeatureClassloader() {
        ClassLoader myLoader = new MyClassLoader();
        ResolverConfiguration config = new XMLResolverConfiguration();
        knownFeature(config, ResolverFeature.CLASSLOADER);
        ClassLoader orig = config.getFeature(ResolverFeature.CLASSLOADER);
        config.setFeature(ResolverFeature.CLASSLOADER, myLoader);
        Assert.assertEquals(myLoader, config.getFeature(ResolverFeature.CLASSLOADER));
        config.setFeature(ResolverFeature.CLASSLOADER, orig);
    }

    @Test
    public void testFeatureClasspathCatalogs() {
        booleanFeature(ResolverFeature.CLASSPATH_CATALOGS);
    }

    @Test
    public void testFeatureMaskJarUris() {
        booleanFeature(ResolverFeature.MASK_JAR_URIS);
    }

    @Test
    public void testFeatureMergeHttps() {
        booleanFeature(ResolverFeature.MERGE_HTTPS);
    }

    @Test
    public void testFeatureParseRddl() {
        booleanFeature(ResolverFeature.PARSE_RDDL);
    }

    @Test
    public void testFeaturePreferPropertyFile() {
        booleanFeature(ResolverFeature.PREFER_PROPERTY_FILE);
    }

    @Test
    public void testFeaturePreferPublic() {
        booleanFeature(ResolverFeature.PREFER_PUBLIC);
    }

    @Test
    public void testFeatureUriForSystem() {
        booleanFeature(ResolverFeature.URI_FOR_SYSTEM);
    }

    private static class MyClassLoader extends ClassLoader {
        MyClassLoader() {
        }
    }

}
