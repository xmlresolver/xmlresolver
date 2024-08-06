/*
 * InstantiationTest.java
 *
 * Created on January 5, 2007, 1:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xmlresolver.utils.URIUtils;

import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ndw
 */
public class PropertyFileTest {
    @Test
    public void testRelativeCatalogs() throws Exception {
        URL pfile = URIUtils.cwd().resolve("src/test/resources/pfile-rel.properties").toURL();
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.singletonList(pfile), null);
        List<String> catalogs = config.getFeature(ResolverFeature.CATALOG_FILES);
        String s1 = URIUtils.cwd().resolve("src/test/resources/catalog.xml").toString();
        boolean f1 = false;
        String s2 = URIUtils.cwd().resolve("src/test/resources/a/catalog.xml").toString();
        boolean f2 = false;
        for (String cat : catalogs) {
            f1 = f1 || cat.endsWith(s1);
            f2 = f2 || cat.endsWith(s2);
        }
        Assertions.assertTrue(f1);
        Assertions.assertTrue(f2);
    }

    @Test
    public void testNotRelativeCatalogs() throws Exception {
        URL pfile = URIUtils.cwd().resolve("src/test/resources/pfile-abs.properties").toURL();
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.singletonList(pfile), null);
        List<String> catalogs = config.getFeature(ResolverFeature.CATALOG_FILES);

        String s1 = "./catalog.xml";
        boolean f1 = false;
        String s2 = "a/catalog.xml";
        boolean f2 = false;
        for (String cat : catalogs) {
            f1 = f1 || s1.equals(cat);
            f2 = f2 || s2.equals(cat);
        }
        Assertions.assertTrue(f1);
        Assertions.assertTrue(f2);
    }
}
