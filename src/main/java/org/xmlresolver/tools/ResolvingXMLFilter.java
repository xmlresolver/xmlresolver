/*
 * ResolvingXMLFilter.java
 *
 * Created on January 5, 2007, 12:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver.tools;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xmlresolver.*;
import org.xmlresolver.logging.AbstractLogger;
import org.xmlresolver.logging.ResolverLogger;
import org.xmlresolver.utils.URIUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/** An implementation of {@link org.xml.sax.XMLFilter} that performs catalog resolution.
 *
 * <p>This class implements the <code>oasis-xml-catalog</code> processing instruction if the underlying
 * resolver allows it.</p>
 *
 * <p>Each instance constructed with the zero-argument constructor uses a shared, static
 * resolver. This avoids paying the instantiation cost each time a parse is created.</p>
 *
 * @author ndw
 */
public class ResolvingXMLFilter extends XMLFilterImpl {
    protected final ResolverLogger logger;

    /** Are oasis-xml-catalog PIs allowed by this catalog? */
    private boolean allowXMLCatalogPI = true;

    /** Are we in the prolog? Is an oasis-xml-catalog PI valid now? */
    private boolean processXMLCatalogPI = false;
    protected static XMLResolver staticResolver = null;
    protected XMLResolver resolver = null;
    private EntityResolver2 entityResolver = null;

    /** The base URI of the input document, if known. */
    private URI baseURI = null;

    /** Construct a filter with the default resolver. */
    public ResolvingXMLFilter() {
        super();
        if (staticResolver == null) {
            staticResolver = new XMLResolver();
        }
        resolver = staticResolver;
        entityResolver = resolver.getEntityResolver2();
        allowXMLCatalogPI = resolver.getConfiguration().getFeature(ResolverFeature.ALLOW_CATALOG_PI);
        logger = resolver.getConfiguration().getFeature(ResolverFeature.RESOLVER_LOGGER);
    }

    /** Construct an XML filter with the specified resolver.
     *
     * @param resolver The resolver
     */
    public ResolvingXMLFilter(XMLResolver resolver) {
        super();
        this.resolver = resolver;
        entityResolver = resolver.getEntityResolver2();
        allowXMLCatalogPI = resolver.getConfiguration().getFeature(ResolverFeature.ALLOW_CATALOG_PI);
        logger = resolver.getConfiguration().getFeature(ResolverFeature.RESOLVER_LOGGER);
    }

    /** Construct an XML filter with the specified parent and resolver.
     *
     * @param parent The parent reader
     * @param resolver The resolver
     */
    public ResolvingXMLFilter(XMLReader parent, XMLResolver resolver) {
        super(parent);
        this.resolver = resolver;
        entityResolver = resolver.getEntityResolver2();
        logger = resolver.getConfiguration().getFeature(ResolverFeature.RESOLVER_LOGGER);
    }

    /**
     * Provide access to the underlying Catalog.
     *
     * @return The underlying resolver
     */
    public XMLResolver getResolver() {
        return resolver;
    }

    /**
     * Parse a document.
     *
     * <p>If the input doesn't have an associated open stream, this class will
     * attempt to find the URI of the input in the catalog.</p>
     */
    public void parse(InputSource input) throws IOException, SAXException {
        XMLResolver localResolver = resolver;
        try {
            if (allowXMLCatalogPI) {
                // The parse may change the catalog list; isolate this configuration to this parse
                ResolverConfiguration config = resolver.getConfiguration();
                if (config instanceof XMLResolverConfiguration) {
                    config = new XMLResolverConfiguration((XMLResolverConfiguration) config);
                } else {
                    logger.log(AbstractLogger.CONFIG, "Temporary configuration could not inherit from current configuration");
                    config = new XMLResolverConfiguration();
                }
                resolver = new XMLResolver(config);
                entityResolver = resolver.getEntityResolver2();
            }
            processXMLCatalogPI = allowXMLCatalogPI;
            setupBaseURI(input.getSystemId());

            if (input.getByteStream() == null && input.getCharacterStream() == null) {
                InputSource src = entityResolver.resolveEntity(null, input.getSystemId());
                if (src != null) {
                    input.setByteStream(src.getByteStream());
                }
            }

            super.parse(input);
        } finally {
            resolver = localResolver;
            entityResolver = resolver.getEntityResolver2();
        }
    }

    /** SAX XMLReader API.
     *
     * @see #parse(InputSource)
     */
    public void parse(String systemId) throws IOException, SAXException {
        parse(new InputSource(systemId));
    }

    /**
     * Implements the <code>resolveEntity</code> method
     * for the SAX interface, using an underlying CatalogResolver
     * to do the real work.
     */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        InputSource is;
        is = entityResolver.resolveEntity(publicId, systemId);
        return is;
    }

    /** Implements the {@link org.xml.sax.ext.EntityResolver2} interface.
     *
     * @param name The entity name
     * @param publicId The entity public identifier
     * @param baseURI The base URI
     * @param systemId The entity system identifier
     * @return The resolved entity
     * @throws SAXException If a SAX error occurs
     * @throws IOException If an I/O error occurs
     */
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
        return entityResolver.resolveEntity(name, publicId, baseURI, systemId);
    }
    
    /** SAX DTDHandler API.
     *
     * <p>Captured here only to detect the end of the prolog so that
     * we can ignore subsequent oasis-xml-catalog PIs. Otherwise
     * the events are just passed through.</p>
     */
    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
        processXMLCatalogPI = false;
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
        processXMLCatalogPI = false;
        super.unparsedEntityDecl(name, publicId, systemId, notationName);
    }

    /** SAX ContentHandler API.
     *
     * <p>Captured here only to detect the end of the prolog so that
     * we can ignore subsequent oasis-xml-catalog PIs. Otherwise
     * the events are just passed through.</p>
     */
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        processXMLCatalogPI = false;
        super.startElement(uri,localName,qName,atts);
    }

    /** SAX ContentHandler API.
     *
     * <p>Detect and use the oasis-xml-catalog PI if it occurs.</p>
     */
    public void processingInstruction(String target, String pidata) throws SAXException {
        if (processXMLCatalogPI && target.equals("oasis-xml-catalog")) {
            URI catalog = null;
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
                            if (baseURI != null) {
                                catalog = baseURI.resolve(data);
                            } else {
                                catalog = URIUtils.newURI(data);
                            }

                            // I think this *must* be true at this point, but let's be defensive
                            if (resolver.getConfiguration() instanceof XMLResolverConfiguration) {
                                ((XMLResolverConfiguration) resolver.getConfiguration()).addCatalog(catalog.toString());
                            }
                        } catch (URISyntaxException mue) {
                            logger.log(AbstractLogger.WARNING, "URI syntax exception resolving PI catalog: %s", data);
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
        if (systemId == null) {
            return;
        }

        try {
            baseURI = URIUtils.newURI(systemId);
            if (!baseURI.isAbsolute()) {
                baseURI = URIUtils.cwd().resolve(baseURI);
            }
        } catch (URISyntaxException use) {
            systemId = URIUtils.normalizeURI(systemId);
            baseURI = URIUtils.cwd().resolve(systemId);
        }
    }
}
