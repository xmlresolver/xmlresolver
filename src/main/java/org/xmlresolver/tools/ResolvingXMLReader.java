/*
 * ResolvingXMLReader.java
 *
 * Created on January 5, 2007, 12:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver.tools;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlresolver.Resolver;
import org.xmlresolver.ResolverFeature;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

/** An implementation of {@link org.xml.sax.XMLReader} that performs catalog resolution.
 *
 * <p>This class implements the <code>oasis-xml-catalog</code> processing instruction if the underlying
 * resolver allows it.</p>
 *
 * @author ndw
 */
public class ResolvingXMLReader extends ResolvingXMLFilter {
    /** Construct an XML Reader with the default {@link javax.xml.parsers.SAXParserFactory} and resolver.
     *
     * <p>If the reader is obtained with an {@link ResolverFeature#XMLREADER_SUPPLIER XMLREADER_SUPPLIER},
     * it is the users responsibility to configure the reader. If the parser is instantiated through
     * {@link ResolverFeature#SAXPARSERFACTORY_CLASS}, it will be configured to be
     * namespace aware and non-validating.</p>
     */
    public ResolvingXMLReader() {
        super();
        initialize(null);
    }

    /** Construct an XML Reader with the specified {@link javax.xml.parsers.SAXParserFactory} and default resolver.
     *
     * @param factory The factory
     */
    public ResolvingXMLReader(SAXParserFactory factory) {
        super();
        initialize(factory);
    }

    /** Construct an XML Reader with the default {@link javax.xml.parsers.SAXParserFactory} and the specified resolver.
     *
     * The default parser is configured to be namespace aware and non-validating.
     *
     * @param resolver The resolver
     */
    public ResolvingXMLReader(Resolver resolver) {
        super(resolver);
        initialize(null);
    }

    /** Construct an XML Reader with the specified {@link javax.xml.parsers.SAXParserFactory} and resolver.
     *
     * @param factory The factory
     * @param resolver The resolver
     */
    public ResolvingXMLReader(SAXParserFactory factory, Resolver resolver) {
        super(resolver);
        initialize(factory);
    }

    private void initialize(SAXParserFactory userFactory) {
        SAXParserFactory factory = userFactory;
        if (factory != null) {
            try {
                factory.setNamespaceAware(true);
                factory.setValidating(false);
                SAXParser parser = factory.newSAXParser();
                setParent(parser.getXMLReader());
                return;
            } catch (ParserConfigurationException | SAXException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }

        Supplier<XMLReader> readerSupplier = resolver.getConfiguration().getFeature(ResolverFeature.XMLREADER_SUPPLIER);
        if (readerSupplier != null) {
            setParent(readerSupplier.get());
            return;
        }

        // This should never happen, but fallback is fallback...
        factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        try {
            SAXParser parser = factory.newSAXParser();
            setParent(parser.getXMLReader());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
