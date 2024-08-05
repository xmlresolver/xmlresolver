package org.xmlresolver;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.sql.Array;
import java.util.ArrayList;

import static org.junit.Assert.fail;

public class AdapterTests {
    @Test
    public void saxAdapterTest() {
        // This is a test that the EntityResolver2 getExternalSubset() method can be used
        // to resolve a DTD.

        try {
            XMLResolver resolver = new XMLResolver();

            ArrayList<String> catalogs = new ArrayList<>();
            catalogs.add("src/test/resources/saxadapter-catalog.xml");
            resolver.getConfiguration().setFeature(ResolverFeature.CATALOG_FILES, catalogs);

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setEntityResolver(resolver.getEntityResolver2());

            MyHandler handler = new MyHandler();
            reader.setContentHandler(handler);

            reader.parse(new InputSource("src/test/resources/saxadapter.xml"));

            Assert.assertTrue(handler.success);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

    private class MyHandler extends DefaultHandler {
        public boolean success = false;

        @Override
        public void characters (char[] ch, int start, int length)
                throws SAXException
        {
            // In principle this could fail because SAX doesn't guarantee that the entire
            // text range will be in a single call. But for a document this small...
            // Seeing "Hello, world." tests that the DTD was successfully found by the resolver.
            if (start == 0 && length == 13) {
                char[] greet = new char[13];
                System.arraycopy(ch, start, greet, 0, length);
                String greeting = new String(greet);
                if (greeting.equals("Hello, world.")) {
                    success = true;
                }
            }
        }
    }


}
