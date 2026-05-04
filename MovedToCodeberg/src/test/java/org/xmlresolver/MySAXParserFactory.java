package org.xmlresolver;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MySAXParserFactory extends SAXParserFactory {
    private final SAXParserFactory factory;
    public static int parserCount = 0;

    public MySAXParserFactory() {
        factory = SAXParserFactory.newInstance();
    }

    @Override
    public SAXParser newSAXParser() throws ParserConfigurationException, SAXException {
        parserCount++;
        return factory.newSAXParser();
    }

    @Override
    public void setFeature(String name, boolean value) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        factory.setFeature(name, value);
    }

    @Override
    public boolean getFeature(String name) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        return factory.getFeature(name);
    }
}
