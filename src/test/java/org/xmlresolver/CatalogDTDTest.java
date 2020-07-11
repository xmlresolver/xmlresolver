package org.xmlresolver;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Created by ndw on 5/27/15.
 */
public class CatalogDTDTest {
    @Test
    public void runCatalogTest() {
        String catalogFile = "src/test/resources/catalogs/dtd-catalog.xml";
        Catalog catalog = new Catalog(catalogFile);
        CatalogResult lookup = catalog.lookupURI("example.xml");
        System.err.println(lookup.uri());
    }
}
