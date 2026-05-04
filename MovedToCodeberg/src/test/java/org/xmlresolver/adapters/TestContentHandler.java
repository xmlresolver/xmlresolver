package org.xmlresolver.adapters;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class TestContentHandler implements ContentHandler {
    public boolean smokeTest = false;
    public boolean pass = false;
    public boolean chapter = false;

    @Override
    public void setDocumentLocator(Locator locator) {
        // nop
    }

    @Override
    public void startDocument() throws SAXException {
        smokeTest = true;
    }

    @Override
    public void endDocument() throws SAXException {
        // nop
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        // nop
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        // nop
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        chapter = chapter || "chapter".equals(qName);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // nop
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String text = new String(ch, start, length);
        pass = pass || text.contains("Hello, world.");
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        // nop
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        // nop
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        // nop
    }
}
