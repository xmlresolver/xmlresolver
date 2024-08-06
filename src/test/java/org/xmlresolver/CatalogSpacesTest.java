package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;
import org.xmlresolver.sources.ResolverInputSource;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.fail;

public class CatalogSpacesTest {
    public static final String catalog = "src/test/resources/spaces-catalog.xml";

    XMLResolverConfiguration config = null;
    XMLResolver resolver = null;

    @BeforeEach
    public void setup() {
        config = new XMLResolverConfiguration(catalog);
        resolver = new XMLResolver(config);

        // Make sure the Docker container is running where we expect.
        ResourceConnection conn = new ResourceConnection(config, URI.create("http://localhost:8222/docs/sample/sample.dtd"), true);
        Assertions.assertEquals(200, conn.getStatusCode());
    }

    @Test
    public void resolveSystem() {
        try {
            InputSource is = resolver.getEntityResolver().resolveEntity(null, "https://xmlresolver.org/ns/sample/sample.dtd");
            Assertions.assertNotNull(is);
            ResolverInputSource ris = (ResolverInputSource) is;
            Assertions.assertTrue(ris.getResolvedURI().toString().contains("/Sample%2010/"));
        } catch (Exception ex) {
            fail();
        }
    }
}

