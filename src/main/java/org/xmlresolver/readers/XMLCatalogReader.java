/*
 * XMLCatalogReader.java
 *
 * Created on December 11, 2006, 1:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver.readers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

/** Reads OASIS XML Catalog files.
 *
 * @author ndw
 */
public class XMLCatalogReader {
    
    /** Creates a new instance of XMLCatalogReader */
    public XMLCatalogReader() {
    }

    /** Read a catalog from a source.
     *
     * @param source The catalog source
     * @return The parsed catalog or null if an error occurs
     */
    public Element readCatalog(Source source) {
        Element root = null;
        
        try {
            // Surely there must be a better way?
            if (source instanceof SAXSource) {
                root = readCatalogSAX((SAXSource) source);
            } else if (source instanceof DOMSource) {
                root = readCatalogDOM((DOMSource) source);
            } else if (source instanceof StAXSource) {
                root = readCatalogStAX((StAXSource) source);
            } else if (source instanceof StreamSource) {
                root = readCatalogStream((StreamSource) source);
            } else {
                return null;
            }
        } catch (ParserConfigurationException pce) {
            return null;
        } catch (SAXException se) {
            return null;
        } catch (IOException ioe) {
            return null;
        }
        
        System.out.println("Name: {" + root.getNamespaceURI() + "}" + root.getNodeName());
        
        if ("catalog".equals(root.getNodeName())
            && "urn:oasis:names:tc:entity:xmlns:xml:catalog".equals(root.getNamespaceURI())) {
            return root;
        }
        
        return null;
    }

    private Element readCatalogSAX(SAXSource source) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = null;

        if (source.getInputSource() != null) {
            doc = builder.parse(source.getInputSource());
        }
        
        if (source.getXMLReader() == null) {
            throw new UnsupportedOperationException("Unexpected XMLReader on SAXSource");
        }
        
        if (source.getSystemId() != null) {
            doc = builder.parse(source.getSystemId());
        }

        return doc == null ? null : doc.getDocumentElement();
    }

    private Element readCatalogDOM(DOMSource source) {
        Node node = source.getNode();
        if (node.getNodeType() == Node.DOCUMENT_NODE) { 
            return ((Document) node).getDocumentElement();
        } else if (node.getNodeType() == Node.ELEMENT_NODE) {
            return (Element) node;
        }
        
        return null;
    }

    private Element readCatalogStAX(StAXSource source) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = null;

        if (source.getXMLEventReader() != null) {
            throw new UnsupportedOperationException("Unexpected XMLEventReader on StAXSource");
        }
        
        if (source.getXMLStreamReader() == null) {
            throw new UnsupportedOperationException("Unexpected XMLStreamReader on StAXSource");
        }
        
        if (source.getSystemId() != null) {
            doc = builder.parse(source.getSystemId());
        }

        return doc == null ? null : doc.getDocumentElement();
    }

    private Element readCatalogStream(StreamSource source) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = null;

        if (source.getInputStream() != null) {
            doc = builder.parse(source.getInputStream());
        } else if (source.getReader() != null) {
            throw new UnsupportedOperationException("Unexpected Reader on StreamSource");
        } else if (source.getSystemId() != null) {
            doc = builder.parse(source.getSystemId());
        }

        return doc == null ? null : doc.getDocumentElement();
    }
    
}
