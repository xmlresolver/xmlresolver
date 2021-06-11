package org.xmlresolver;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;
import org.xmlresolver.sources.ResolverInputSource;
import org.xmlresolver.sources.ResolverLSInput;
import org.xmlresolver.sources.ResolverSAXSource;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import java.io.IOException;

public class Resolver implements URIResolver, EntityResolver, EntityResolver2, NamespaceResolver, LSResourceResolver {
    private static final ResolverLogger logger = new ResolverLogger(Resolver.class);
    protected ResourceResolverImpl resolver = null;

    /** Creates a new instance of Resolver.
     *
     * The default resolver is a new ResourceResolver that uses a static catalog shared by all threads.
     */
    public Resolver() {
        resolver = new ResourceResolverImpl();
    }

    /** Creates a new instance of a Resolver.
     *
     * Creates a resolver using a specific Catalog.
     *
     * @param config The configuration to use.
     */
    public Resolver(XMLResolverConfiguration config) {
        resolver = new ResourceResolverImpl(config);
    }

    /** Creates a new instance of a Resolver.
     *
     * Creates a resolver using a specific underlying ResourceResolver.
     *
     * @param resolver The resource resolver to use.
     */
    public Resolver(ResourceResolverImpl resolver) {
        this.resolver = resolver;
    }

    /** What version is this?
     *
     * Returns the version number of this resolver instance.
     *
     * @return The version number
     */
    public static String version() {
        return BuildConfig.VERSION;
    }

    /** Get the Catalog used by this resolver.
     *
     * @return The underlying catalog.
     */
    public XMLResolverConfiguration getConfiguration() {
        return resolver.getConfiguration();
    }

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        ResolvedResource rsrc = resolver.resolveURI(href, base);
        if (rsrc == null) {
            return null;
        }

        ResolverSAXSource source = new ResolverSAXSource(rsrc.getLocalURI(), new InputSource(rsrc.getInputStream()));
        source.setSystemId(rsrc.getResolvedURI().toString());
        return source;
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        ResolvedResource rsrc = null;
        if (type == null || "http://www.w3.org/TR/REC-xml".equals(type)) {
            logger.log(ResolverLogger.REQUEST, "resolveResource: XML: %s (baseURI: %s, publicId: %s)",
                    systemId, baseURI, publicId);
            rsrc = resolver.resolveEntity(null, publicId, systemId, baseURI);
        } else {
            logger.log(ResolverLogger.REQUEST, "resolveResource: %s, %s (namespace: %s, baseURI: %s, publicId: %s)",
                    type, systemId, namespaceURI, baseURI, publicId);

            String purpose = null;
            // If it looks like it's going to be used for validation, ...
            if ("http://www.w3.org/2001/XMLSchema".equals(type)
                    || "http://www.w3.org/XML/XMLSchema/v1.1".equals(type)
                    || "http://relaxng.org/ns/structure/1.0".equals(type)) {
                purpose = "http://www.rddl.org/purposes#schema-validation";
            }

            rsrc = resolver.resolveNamespace(systemId, baseURI, type, purpose);
        }

        if (rsrc == null) {
            return null;
        }

        return new ResolverLSInput(rsrc, publicId);
    }

    @Override
    public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
        ResolvedResource rsrc = resolver.resolveEntity(name, null, null, baseURI);
        if (rsrc == null) {
            return null;
        }

        ResolverInputSource source = new ResolverInputSource(rsrc.getLocalURI(), rsrc.getInputStream());
        source.setSystemId(rsrc.getResolvedURI().toString());
        return source;
    }

    @Override
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
        ResolvedResource rsrc = resolver.resolveEntity(name, publicId, systemId, baseURI);
        if (rsrc == null) {
            return null;
        }

        ResolverInputSource source = new ResolverInputSource(rsrc.getLocalURI(), rsrc.getInputStream());
        source.setSystemId(rsrc.getResolvedURI().toString());
        return source;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        ResolvedResource rsrc = resolver.resolveEntity(null, publicId, systemId, null);
        if (rsrc == null) {
            return null;
        }

        ResolverInputSource source = new ResolverInputSource(rsrc.getLocalURI(), rsrc.getInputStream());
        source.setSystemId(rsrc.getResolvedURI().toString());
        return source;
    }

    @Override
    public Source resolveNamespace(String uri, String nature, String purpose) throws TransformerException {
        ResolvedResource rsrc = resolver.resolveNamespace(uri, null, nature, purpose);
        if (rsrc == null) {
            return null;
        }

        ResolverSAXSource source = new ResolverSAXSource(rsrc.getLocalURI(), new InputSource(rsrc.getInputStream()));
        source.setSystemId(rsrc.getResolvedURI().toString());
        return source;
    }
}
