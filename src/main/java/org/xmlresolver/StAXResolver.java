/*
 * StAXResolver.java
 *
 * Created on February 12, 2007, 2:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;

/** Implements the {@link javax.xml.stream.XMLResolver} interface.
 *
 * <blockquote>
 *     <p>This interface is used to resolve resources during an XML parse. If an application wishes to perform
 *     custom entity resolution it must register an instance of this interface with the <code>XMLInputFactory</code>
 *     using the <code>setXMLResolver</code> method.</p>
 * </blockquote>
 *
 * This class is distinct from the {@link Resolver} class because the <code>resolveEntity</code> method
 * of the <code>XMLResolver</code> interface isn't compatible with the <code>EntityResolver2</code>
 * method of the same name.
 *
 * @see Resolver
 *
 */
public class StAXResolver implements XMLResolver {
    protected static ResolverLogger logger = new ResolverLogger(StAXResolver.class);
    ResourceResolver resolver = null;
    
    /** Creates a new instance of StAXResolver.
     *
     * The default resolver is a new ResourceResolver that uses a static catalog shared by all threads.
     */
    public StAXResolver() {
        resolver = new ResourceResolverImpl();
    }

    /** Creates a new instance of a StAXResolver.
     *
     * Creates a resolver using a specific Catalog.
     *
     * @param config The XML Resolver configuration to use.
     */
    public StAXResolver(XMLResolverConfiguration config) {
        resolver = new ResourceResolverImpl(config);
    }

    /** Creates a new instance of a StAXResolver.
     *
     * Creates a resolver using a specific underlying ResourceResolver.
     *
     * @param resolver The resource resolver to use.
     */
    public StAXResolver(ResourceResolver resolver) {
        this.resolver = resolver;
    }

    /** Get the configuration used by this resolver.
     *
     * @return The catalog
     */
    public ResolverConfiguration getConfiguration() {
        return resolver.getConfiguration();
    }

    /** Implements the {@link javax.xml.stream.XMLResolver} interface. */
    public Object resolveEntity(String publicId, String systemId, String baseURI, String namespace) throws XMLStreamException {
        logger.log(ResolverLogger.REQUEST, "resolveEntity: %s/%s (baseURI: %s, %s)",
                systemId, namespace, baseURI, publicId);

        ResolvedResource rsrc = resolver.resolveEntity(null, publicId, systemId, baseURI);

        if (rsrc == null) {
            logger.log(ResolverLogger.RESPONSE, "resolvedEntity: %s/%s (baseURI: %s, %s) → null",
                    systemId, namespace, baseURI, publicId);
            return null;
        } else {
            logger.log(ResolverLogger.RESPONSE, "resolvedEntity: %s/%s (baseURI: %s, %s) → %s",
                    systemId, namespace, baseURI, publicId, rsrc.getResolvedURI());
            return rsrc.getStream();
        }
    }
}
