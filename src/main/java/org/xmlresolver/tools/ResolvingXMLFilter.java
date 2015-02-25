/*
 * ResolvingXMLFilter.java
 *
 * Created on January 5, 2007, 12:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xmlresolver.Catalog;
import org.xmlresolver.Resolver;
import org.xmlresolver.helpers.FileURI;

/** An implementation of {@link org.xml.sax.XMLFilter} that performs catalog resolution.
 *
 * <p>This class implements the <code>oasis-xml-catalog</code> processing instruction if the underlying
 * resolver allows it.</p>
 *
 * @author ndw
 */
public class ResolvingXMLFilter extends XMLFilterImpl {
    /** Are we in the prolog? Is an oasis-xml-catalog PI valid now? */
    private boolean allowXMLCatalogPI = false;

    /** Has an oasis-xml-catalog PI been seen? */
    private boolean oasisXMLCatalogPI = false;

    private Resolver resolver = new Resolver();
    
    /** The base URI of the input document, if known. */
    private URL baseURL = null;

    /** Construct a filter with the default resolver. */
    public ResolvingXMLFilter() {
        super();
    }
    
    /** Construct an XML filter with the specified resolver. */
    public ResolvingXMLFilter(Resolver resolver) {
        super();
        this.resolver = resolver;
    }

    /** Construct an XML filter with the specified parent and resolver. */
    public ResolvingXMLFilter(XMLReader parent, Resolver resolver) {
        super(parent);
        this.resolver = resolver;
    }

    /**
     * Provide accessto the underlying Catalog.
     */
    public Resolver getResolver() {
        return resolver;
    }

    /**
     * SAX XMLReader API.
     *
     * <p>Note that the JAXP 1.1ea2 parser crashes with an InternalError if
     * it encounters a system identifier that appears to be a relative URI
     * that begins with a slash. For example, the declaration:</p>
     *
     * <pre>
     * &lt;!DOCTYPE book SYSTEM "/path/to/dtd/on/my/system/docbookx.dtd">
     * </pre>
     *
     * <p>would cause such an error. As a convenience, this method catches
     * that error and prints an explanation. (Unfortunately, it's not possible
     * to identify the particular system identifier that causes the problem.)
     * </p>
     *
     * <p>The underlying error is forwarded after printing the explanatory
     * message. The message is only every printed once and if
     * <code>suppressExplanation</code> is set to <code>false</code> before
     * parsing, it will never be printed.</p>
     */
    public void parse(InputSource input) throws IOException, SAXException {
        allowXMLCatalogPI = true;
        setupBaseURI(input.getSystemId());
        super.parse(input);
    }

    /** SAX XMLReader API.
     *
     * @see #parse(InputSource)
     */
    public void parse(String systemId) throws IOException, SAXException {
        allowXMLCatalogPI = true;
        setupBaseURI(systemId);
        super.parse(systemId);
    }

    /**
     * Implements the <code>resolveEntity</code> method
     * for the SAX interface, using an underlying CatalogResolver
     * to do the real work.
     */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        allowXMLCatalogPI = false;
        
        // FIXME: Handle oasis catalog PI here!
        
        return resolver.resolveEntity(publicId, systemId);
    }

    /** Implements the {@link org.xml.sax.ext.EntityResolver2} interface. */
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
        allowXMLCatalogPI = false;
        
        // FIXME: Handle oasis catalog PI here!
        
        return resolver.resolveEntity(name, publicId, baseURI, systemId);
    }
    
    /** SAX DTDHandler API.
     *
     * <p>Captured here only to detect the end of the prolog so that
     * we can ignore subsequent oasis-xml-catalog PIs. Otherwise
     * the events are just passed through.</p>
     */
    public void notationDecl(String name, String publicId, String systemId)
    throws SAXException {
        allowXMLCatalogPI = false;
        super.notationDecl(name,publicId,systemId);
    }

    /** SAX DTDHandler API.
     *
     * <p>Captured here only to detect the end of the prolog so that
     * we can ignore subsequent oasis-xml-catalog PIs. Otherwise
     * the events are just passed through.</p>
     */
    public void unparsedEntityDecl(String name,
                                   String publicId,
                                   String systemId,
                                   String notationName) throws SAXException {
        allowXMLCatalogPI = false;
        super.unparsedEntityDecl(name, publicId, systemId, notationName);
    }

    /** SAX ContentHandler API.
     *
     * <p>Captured here only to detect the end of the prolog so that
     * we can ignore subsequent oasis-xml-catalog PIs. Otherwise
     * the events are just passed through.</p>
     */
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        allowXMLCatalogPI = false;
        super.startElement(uri,localName,qName,atts);
    }

    /** SAX ContentHandler API.
     *
     * <p>Detect and use the oasis-xml-catalog PI if it occurs.</p>
     */
    public void processingInstruction(String target, String pidata) throws SAXException {
        if (allowXMLCatalogPI && target.equals("oasis-xml-catalog")) {
            URL catalog = null;
            String data = pidata;

            int pos = data.indexOf("catalog=");
            if (pos >= 0) {
                data = data.substring(pos+8);
                if (data.length() > 1) {
                    String quote = data.substring(0,1);
                    data = data.substring(1);
                    pos = data.indexOf(quote);
                    if (pos >= 0) {
                        data = data.substring(0, pos);
                        try {
                            if (baseURL != null) {
                                catalog = new URL(baseURL, data);
                            } else {
                                catalog = new URL(data);
                            }
                            
                            Catalog newCatalog = new Catalog(resolver.getCatalog().catalogList()+";"+catalog.toString());
                            resolver = new Resolver(newCatalog);
                        } catch (MalformedURLException mue) {
                            // nevermind
                        }
                    }
                }
            }
        } else {
            super.processingInstruction(target, pidata);
        }
    }

    /** Save the base URI of the document being parsed. */
    private void setupBaseURI(String systemId) {
        URL cwd = null;

        try {
            cwd = FileURI.makeURI("basename").toURL();
        } catch (MalformedURLException mue) {
            cwd = null;
        }

        try {
            baseURL = new URL(systemId);
        } catch (MalformedURLException mue) {
            if (cwd != null) {
                try {
                    baseURL = new URL(cwd, systemId);
                } catch (MalformedURLException mue2) {
                    // give up
                    baseURL = null;
                }
            } else {
                // give up
                baseURL = null;
            }
        }
    }
}
