/*
 * ResolvingXMLReader.java
 *
 * Created on January 5, 2007, 12:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver.tools;

import org.xmlresolver.Resolver;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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
     * <p>The default parser is configured to be namespace aware and non-validating.</p>
     */
    public ResolvingXMLReader() {
        super();
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(false);
        try {
            SAXParser parser = spf.newSAXParser();
            setParent(parser.getXMLReader());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Construct an XML Reader with the specified {@link javax.xml.parsers.SAXParserFactory} and default resolver.
     *
     * @param factory The factory
     */
    public ResolvingXMLReader(SAXParserFactory factory) {
        super();
        try {
            SAXParser parser = factory.newSAXParser();
            setParent(parser.getXMLReader());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Construct an XML Reader with the default {@link javax.xml.parsers.SAXParserFactory} and the specified resolver.
     *
     * The default parser is configured to be namespace aware and non-validating.
     *
     * @param resolver The resolver
     */
    public ResolvingXMLReader(Resolver resolver) {
        super(resolver);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(false);
        try {
            SAXParser parser = spf.newSAXParser();
            setParent(parser.getXMLReader());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Construct an XML Reader with the specified {@link javax.xml.parsers.SAXParserFactory} and resolver.
     *
     * @param factory The factory
     * @param resolver The resolver
     */
    public ResolvingXMLReader(SAXParserFactory factory, Resolver resolver) {
        super(resolver);
        try {
            SAXParser parser = factory.newSAXParser();
            setParent(parser.getXMLReader());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
