package org.xmlresolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.transform.Source;
import java.util.Collections;

import static org.junit.Assert.fail;

public class NamespaceTest {
    public static final String SCHEMA_NATURE = "http://www.w3.org/2001/XMLSchema";
    public static final String VALIDATION_PURPOSE = "http://www.rddl.org/purposes#validation";

    public static XMLResolverConfiguration config = null;
    public static CatalogManager manager = null;
    Resolver resolver = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        resolver = new Resolver(config);
    }

    @Test
    public void xmlCatalogNamespace() {
        // The OASIS XML Catalogs namespace is provided by the data jar
        try {
            Source source = resolver.resolveNamespace("urn:oasis:names:tc:entity:xmlns:xml:catalog", SCHEMA_NATURE, VALIDATION_PURPOSE);
            Assert.assertNotNull(source);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            fail();
        }


    }

}
