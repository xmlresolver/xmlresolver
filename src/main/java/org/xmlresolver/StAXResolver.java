/*
 * StAXResolver.java
 *
 * Created on February 12, 2007, 2:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import org.xmlresolver.utils.URIUtils;

import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

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
        resolver = new ResourceResolver();
    }

    /** Creates a new instance of a StAXResolver.
     *
     * Creates a resolver using a specific Catalog.
     *
     * @param config The XML Resolver configuration to use.
     */
    public StAXResolver(XMLResolverConfiguration config) {
        resolver = new ResourceResolver(config);
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
    public XMLResolverConfiguration getConfiguration() {
        return resolver.getConfiguration();
    }

    /** Implements the {@link javax.xml.stream.XMLResolver} interface. */
    public Object resolveEntity(String publicId, String systemId, String baseURI, String namespace) throws XMLStreamException {
        // We can do better than this, but for now...just get an absolute URI

        String absSystem = systemId;
        if (baseURI != null) {
            try {
                URI auri = URIUtils.newURI(baseURI);
                auri = auri.resolve(URIUtils.newURI(systemId));
                absSystem = auri.toURL().toString();
            } catch (URISyntaxException | MalformedURLException use) {
                // nop;
            } catch (IllegalArgumentException iae) {
                // In case someone uses baseURI=null, systemId="../some/local/path"
                // nop;
            }
        }

        logger.log(ResolverLogger.REQUEST, "resolveEntity: %s/%s (%s)", absSystem, namespace, publicId);

        Resource rsrc = resolver.resolvePublic(absSystem, publicId);

        if (rsrc == null) {
            logger.log(ResolverLogger.RESPONSE, "resolvedEntity: %s/%s (%s) → null", absSystem, namespace, publicId);
            return null;
        } else {
            logger.log(ResolverLogger.RESPONSE, "resolvedEntity: %s/%s (%s) → %s",
                    absSystem, namespace, publicId, rsrc.uri());
            return rsrc.body();
        }
    }
}
