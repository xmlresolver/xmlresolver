package org.xmlresolver;

import org.junit.Test;
import org.xml.sax.*;
import org.xmlresolver.exceptions.CatalogUnavailableException;
import org.xmlresolver.utils.SaxProducer;
import org.xmlresolver.utils.URIUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class LoaderTest {

    @Test
    public void nonValidatingValidCatalog() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("classpath:/manual-catalog.xml"));
        config.setFeature(ResolverFeature.CATALOG_LOADER_CLASS, "org.xmlresolver.loaders.XmlLoader");
        CatalogManager manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI rsrc = manager.lookupSystem("https://xmlresolver.org/ns/sample/sample.dtd");
        assertNotNull(rsrc);
    }

    @Test
    public void nonValidatingInvalidCatalog() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("classpath:/invalid-catalog.xml"));
        config.setFeature(ResolverFeature.CATALOG_LOADER_CLASS, "org.xmlresolver.loaders.XmlLoader");
        CatalogManager manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI rsrc = manager.lookupSystem("https://xmlresolver.org/ns/sample/sample.dtd");
        assertNotNull(rsrc);
    }

    @Test
    public void validatingValidCatalog() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("classpath:/manual-catalog.xml"));
        config.setFeature(ResolverFeature.CATALOG_LOADER_CLASS, "org.xmlresolver.loaders.ValidatingXmlLoader");
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        CatalogManager manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI rsrc = manager.lookupSystem("https://xmlresolver.org/ns/sample/sample.dtd");
        assertNotNull(rsrc);
    }

    @Test
    public void validatingDtd10ValidCatalog() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("classpath:/dtd10catalog.xml"));
        config.setFeature(ResolverFeature.CATALOG_LOADER_CLASS, "org.xmlresolver.loaders.ValidatingXmlLoader");
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        CatalogManager manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI rsrc = manager.lookupSystem("https://xmlresolver.org/ns/sample/sample.dtd");
        assertNotNull(rsrc);
    }

    @Test
    public void validatingDtd11ValidCatalog() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("classpath:/dtd11catalog.xml"));
        config.setFeature(ResolverFeature.CATALOG_LOADER_CLASS, "org.xmlresolver.loaders.ValidatingXmlLoader");
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        CatalogManager manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI rsrc = manager.lookupSystem("https://xmlresolver.org/ns/sample/sample.dtd");
        assertNotNull(rsrc);
    }

    @Test
    public void validatingInvalidCatalog() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("classpath:/invalid-catalog.xml"));
        config.setFeature(ResolverFeature.CATALOG_LOADER_CLASS, "org.xmlresolver.loaders.ValidatingXmlLoader");
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        CatalogManager manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        try {
            URI rsrc = manager.lookupSystem("https://xmlresolver.org/ns/sample/sample.dtd");
            assertNotNull(rsrc);
            fail();
        } catch (CatalogUnavailableException ex) {
            // pass
        }
    }

    @Test
    public void validatingMissingCatalog() {
        // File not found isn't a validation error
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Arrays.asList("./not-a-catalog-in-sight.xml", "classpath:/manual-catalog.xml"));
        config.setFeature(ResolverFeature.CATALOG_LOADER_CLASS, "org.xmlresolver.loaders.ValidatingXmlLoader");
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        CatalogManager manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        try {
            URI rsrc = manager.lookupSystem("https://xmlresolver.org/ns/sample/sample.dtd");
            assertNotNull(rsrc);
        } catch (CatalogUnavailableException ex) {
            fail();
        }
    }

    @Test
    public void constructCatalogWithSAX() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        CatalogManager manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);

        // Using a constructed catalog requires two steps:
        // 1. You must add the catalog to the configuration
        // 2. You must arrange to load that catalog (associate a set of catalog entries with that URI).
        //
        // Simply parsing the catalog will not make it part of the configuration and it will not be consulted.

        URI caturi = URIUtils.cwd().resolve("src/test/resources/sample10/generated-catalog.xml");
        config.addCatalog(caturi.toString());

        SaxProducer producer = new CatalogProducer();
        manager.loadCatalog(caturi, producer);

        try {
            URI rsrc = manager.lookupSystem("https://xmlresolver.org/ns/sample/sample.dtd");
            assertNotNull(rsrc);
            assertTrue(rsrc.getPath().endsWith("/generated-sample.dtd"));
        } catch (Exception ex) {
            fail();
        }
    }

    private static class CatalogProducer implements SaxProducer {
        private static final String ns = "urn:oasis:names:tc:entity:xmlns:xml:catalog";

        @Override
        public void produce(ContentHandler contentHandler, DTDHandler dtdHandler, ErrorHandler errorHandler) throws IOException, SAXException {
            contentHandler.startDocument();
            contentHandler.startPrefixMapping("", ns);

            CatalogAttributes attr = new CatalogAttributes();
            attr.addAttribute("prefer", "public");

            contentHandler.startElement(ns, "catalog", "catalog", attr);

            attr = new CatalogAttributes();
            attr.addAttribute("systemId", "https://xmlresolver.org/ns/sample/sample.dtd")
                    .addAttribute("uri", "generated-sample.dtd");

            contentHandler.startElement(ns, "system", "system", attr);
            contentHandler.endElement(ns, "system", "system");
            contentHandler.endElement(ns, "catalog", "catalog");
            contentHandler.endPrefixMapping("");
            contentHandler.endDocument();;
        }
    }

    private static class CatalogAttributes implements Attributes {
        private final ArrayList<String> names = new ArrayList<>();
        private final ArrayList<String> values = new ArrayList<>();

        public CatalogAttributes addAttribute(String name, String value) {
            names.add(name);
            values.add(value);
            return this;
        }

        @Override
        public int getLength() {
            return names.size();
        }

        @Override
        public String getURI(int index) {
            return "";
        }

        @Override
        public String getLocalName(int index) {
            return names.get(index);
        }

        @Override
        public String getQName(int index) {
            return names.get(index);
        }

        @Override
        public String getType(int index) {
            return "CDATA";
        }

        @Override
        public String getValue(int index) {
            if (index < 0) {
                return null;
            }
            return values.get(index);
        }

        @Override
        public int getIndex(String uri, String localName) {
            if (uri == null || "".equals(uri)) {
                for (int pos = 0; pos < names.size(); pos++) {
                    if (localName.equals(names.get(pos))) {
                        return pos;
                    }
                }
            }
            return -1;
        }

        @Override
        public int getIndex(String qName) {
            for (int pos = 0; pos < names.size(); pos++) {
                if (qName.equals(names.get(pos))) {
                    return pos;
                }
            }
            return -1;
        }

        @Override
        public String getType(String uri, String localName) {
            return "CDATA";
        }

        @Override
        public String getType(String qName) {
            return "CDATA";
        }

        @Override
        public String getValue(String uri, String localName) {
            return getValue(getIndex(uri, localName));
        }

        @Override
        public String getValue(String qName) {
            return getValue(getIndex(qName));
        }
    }
}
