package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
public class Issue31 {

    private static final String NS = "urn:oasis:names:tc:entity:xmlns:xml:catalog";

    private Resolver resolver;
    private String exp;

    @Before
    public void setUp() throws ParserConfigurationException {
        exp = getClass().getClassLoader().getResource("catalogs/catalog.xml").toString();

        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        final Document document = documentBuilderFactory.newDocumentBuilder().newDocument();
        final Element source = document.createElementNS(NS, "catalog");
        final Element urn = document.createElementNS(NS, "system");
        urn.setAttribute("systemId", "urn:oasis:names:tc:dita:rng:topic.rng");
        urn.setAttribute("uri", exp);
        source.appendChild(urn);
        final Element file = document.createElementNS(NS, "system");
        file.setAttribute("systemId", "https://example.com/topic.rng");
        file.setAttribute("uri", exp);
        source.appendChild(file);

        final Properties properties = new Properties();
        final XMLResolverConfiguration configuration = new XMLResolverConfiguration();
        configuration.loadPropertiesConfiguration(properties);
        final Vector<CatalogSource> catalogList = new Vector<>();
        catalogList.add(new CatalogSource.DomCatalogSource(source));
        final Catalog catalog = new Catalog(configuration, catalogList);
        resolver = new Resolver(catalog);
    }

    @Test
    public void urn() throws IOException, SAXException {
        final InputSource act = resolver.resolveEntity(null, null, "/", "urn:oasis:names:tc:dita:rng:topic.rng");
        // This fails because of the toURL() conversion
        assertEquals(exp, act.getSystemId());
    }

    @Test
    public void https() throws IOException, SAXException {
        final InputSource act = resolver.resolveEntity(null, null, "/", "https://example.com/topic.rng");
        assertEquals(exp, act.getSystemId());
    }
}
