package org.xmlresolver;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.function.Supplier;

public class MyXMLReaderSupplier {
    public static int parserCount = 0;

    public static Supplier<XMLReader> supplier() {
        return () -> {
            parserCount++;
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);
                factory.setValidating(false);
                factory.setXIncludeAware(false);
                SAXParser parser = factory.newSAXParser();
                return parser.getXMLReader();
            } catch (ParserConfigurationException | SAXException ex) {
                return null;
            }
        };
    }
}
