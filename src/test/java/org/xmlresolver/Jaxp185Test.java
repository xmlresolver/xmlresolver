package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class Jaxp185Test {
    public static final String catalog1 = "src/test/resources/jaxp185.xml";
    XMLResolverConfiguration config = null;
    Resolver unrestrictedResolver = null;
    Resolver restrictedResolver = null;
    Resolver allowHttpResolver = null;
    Resolver allowHttpsResolver = null;
    Resolver allowHttpMergedResolver = null;
    Resolver allowHttpsMergedResolver = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        config.setFeature(ResolverFeature.MERGE_HTTPS, true);
        config.setFeature(ResolverFeature.ACCESS_EXTERNAL_ENTITY, "fake");
        config.setFeature(ResolverFeature.ACCESS_EXTERNAL_DOCUMENT, "fake");
        restrictedResolver = new Resolver(config);

        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        unrestrictedResolver = new Resolver(config);

        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        config.setFeature(ResolverFeature.MERGE_HTTPS, true);
        config.setFeature(ResolverFeature.ACCESS_EXTERNAL_ENTITY, "http");
        config.setFeature(ResolverFeature.ACCESS_EXTERNAL_DOCUMENT, "http");
        allowHttpMergedResolver = new Resolver(config);

        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        config.setFeature(ResolverFeature.MERGE_HTTPS, true);
        config.setFeature(ResolverFeature.ACCESS_EXTERNAL_ENTITY, "https");
        config.setFeature(ResolverFeature.ACCESS_EXTERNAL_DOCUMENT, "https");
        allowHttpsMergedResolver = new Resolver(config);

        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        config.setFeature(ResolverFeature.MERGE_HTTPS, false);
        config.setFeature(ResolverFeature.ACCESS_EXTERNAL_ENTITY, "http");
        config.setFeature(ResolverFeature.ACCESS_EXTERNAL_DOCUMENT, "http");
        allowHttpResolver = new Resolver(config);

        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        config.setFeature(ResolverFeature.MERGE_HTTPS, false);
        config.setFeature(ResolverFeature.ACCESS_EXTERNAL_ENTITY, "https");
        config.setFeature(ResolverFeature.ACCESS_EXTERNAL_DOCUMENT, "https");
        allowHttpsResolver = new Resolver(config);
    }

    @Test
    public void lookupSystemPass() {
        try {
            InputSource source = unrestrictedResolver.resolveEntity(null, "https://example.com/sample/1.0/sample.dtd");
            assertNotNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void lookupUriPassAbs() {
        try {
            Source source = unrestrictedResolver.resolve("https://example.com/sample/1.0/document.xml", null);
            assertNotNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void lookupUriPassRel() {
        try {
            Source source = unrestrictedResolver.resolve("document.xml", "https://example.com/sample/1.0/");
            assertNotNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void lookupSystemFailHttpsNotFake() {
        try {
            InputSource source = restrictedResolver.resolveEntity(null, "https://example.com/sample/1.0/sample.dtd");
            assertNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void lookupSystemFailHttpNotFake() {
        try {
            InputSource source = restrictedResolver.resolveEntity(null, "http://example.com/sample/1.0/sample.dtd");
            assertNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void lookupSystemPassFake() {
        try {
            InputSource source = restrictedResolver.resolveEntity(null, "fake://example.com/sample/1.0/sample.dtd");
            assertNotNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void lookupUriFailHttpsNotFake() {
        try {
            Source source = restrictedResolver.resolve("https://example.com/sample/1.0/document.xml", null);
            assertNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void lookupUriFailHttpNotFake() {
        try {
            Source source = restrictedResolver.resolve("http://example.com/sample/1.0/document.xml", null);
            assertNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void lookupUriPassFake() {
        try {
            Source source = restrictedResolver.resolve("fake://example.com/sample/1.0/document.xml", null);
            assertNotNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void lookupSystemMergedPassHttps() {
        try {
            InputSource source = allowHttpsMergedResolver.resolveEntity(null, "https://example.com/sample/1.0/sample.dtd");
            assertNotNull(source);
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

    @Test
    public void lookupSystemMergedPassHttp() {
        try {
            InputSource source = allowHttpsMergedResolver.resolveEntity(null, "http://example.com/sample/1.0/sample.dtd");
            assertNotNull(source);
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

    @Test
    public void lookupSystemPassHttps() {
        try {
            InputSource source = allowHttpsResolver.resolveEntity(null, "https://example.com/sample/1.0/sample.dtd");
            assertNotNull(source);
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

    @Test
    public void lookupSystemFailHttp() {
        try {
            InputSource source = allowHttpsResolver.resolveEntity(null, "http://example.com/sample/1.0/sample.dtd");
            assertNull(source);
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

    @Test
    public void lookupSystemFailHttps() {
        try {
            InputSource source = allowHttpResolver.resolveEntity(null, "https://example.com/sample/1.0/sample.dtd");
            assertNull(source);
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

    @Test
    public void lookupSystemPassHttp() {
        try {
            InputSource source = allowHttpResolver.resolveEntity(null, "http://example.com/sample/1.0/sample-http.dtd");
            assertNotNull(source);
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

// yyy

    @Test
    public void lookupUriMergedPassHttpsAbs() {
        try {
            Source source = allowHttpsMergedResolver.resolve("https://example.com/sample/1.0/document.xml", null);
            assertNotNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void lookupUriMergedPassHttpsRel() {
        try {
            Source source = allowHttpsMergedResolver.resolve("1.0/document.xml", "https://example.com/sample/");
            assertNotNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void lookupUriMergedPassHttp() {
        try {
            Source source = allowHttpsMergedResolver.resolve("http://example.com/sample/1.0/document.xml", null);
            assertNotNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void lookupUriPassHttps() {
        try {
            Source source = allowHttpsResolver.resolve("https://example.com/sample/1.0/document.xml", null);
            assertNotNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void lookupUriFailHttp() {
        try {
            Source source = allowHttpsResolver.resolve("http://example.com/sample/1.0/document.xml", null);
            assertNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void lookupUriFailHttps() {
        try {
            Source source = allowHttpResolver.resolve("https://example.com/sample/1.0/document.xml", null);
            assertNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void lookupUriPassHttp() {
        try {
            Source source = allowHttpResolver.resolve("http://example.com/sample/1.0/document-http.xml", null);
            assertNotNull(source);
        } catch (Exception ex) {
            fail();
        }
    }


}
