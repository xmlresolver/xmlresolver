package org.xmlresolver.adapters;

import org.apache.xerces.impl.XMLEntityDescription;
import org.apache.xerces.impl.xs.XSDDescription;
import org.apache.xerces.util.SAXInputSource;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.XMLDTDDescription;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.xml.sax.InputSource;
import org.xmlresolver.*;
import org.xmlresolver.sources.ResolverInputSource;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * This class implements the {@link XMLEntityResolver} API.
 * <p>It's a separate class in order to avoid a compile-time dependency on the Xerces
 * API for users of {@link XMLResolver} who don't use it.</p>
 */

public class XercesXniAdapter implements XMLEntityResolver {
    private final XMLResolver resolver;

    public XercesXniAdapter(XMLResolver resolver) {
        if (resolver == null) {
            throw new NullPointerException();
        }
        this.resolver = resolver;
    }

    @Override
    public XMLInputSource resolveEntity(XMLResourceIdentifier resId) throws XNIException, IOException {
        // Xerces seems to call this API for all resolution. Let's see if we can work out what they're
        // looking for...
        if (resId instanceof XMLDTDDescription) {
            return resolveDTD((XMLDTDDescription) resId);
        } else if (resId instanceof XMLEntityDescription) {
            return resolveEntity((XMLEntityDescription) resId);
        } else if (resId instanceof XSDDescription) {
            return resolveSchema((XSDDescription) resId);
        }

        // Well whadda we do now?

        String publicId = resId.getPublicId();
        String systemId = resId.getLiteralSystemId();
        String baseURI = resId.getBaseSystemId();
        String namespace = resId.getNamespace();

        ResourceRequest request = null;
        ResourceResponse rsrc = null;
        // If the namespace isn't null, we've gone past the doctype declaration, so it's not an entity.
        // Otherwise, if publicId or systemId aren't null, try resolving an entity.
        if (namespace == null) {
            request = resolver.getRequest(systemId, baseURI, ResolverConstants.EXTERNAL_ENTITY_NATURE, ResolverConstants.ANY_PURPOSE);
            request.setPublicId(publicId);
            rsrc = resolver.resolve(request);
            if (!rsrc.isResolved()) {
                request = resolver.getRequest(resId.getExpandedSystemId(), baseURI, ResolverConstants.EXTERNAL_ENTITY_NATURE, ResolverConstants.ANY_PURPOSE);
                request.setPublicId(publicId);
                rsrc = resolver.resolve(request);
            }
        }

        if (rsrc == null || !rsrc.isResolved()) {
            request = resolver.getRequest(namespace, resId.getBaseSystemId(), ResolverConstants.NATURE_XML_SCHEMA, ResolverConstants.PURPOSE_SCHEMA_VALIDATION);
            rsrc = resolver.resolve(request);
        }

        if (rsrc == null || !rsrc.isResolved()) {
            request = resolver.getRequest(systemId, baseURI, ResolverConstants.ANY_NATURE, ResolverConstants.ANY_PURPOSE);
            request.setResolvingAsEntity(true);
            rsrc = safeOpenConnection(request);
        }

        SAXInputSource source = null;
        if (rsrc != null && rsrc.isResolved()) {
            source = new SAXInputSource(new ResolverInputSource(rsrc));
        }

        return source;
    }

    private ResourceResponse safeOpenConnection(ResourceRequest request) {
        // This is "safe" in the weird sense that it doesn't throw a checked exception
        if (resolver.getConfiguration().getFeature(ResolverFeature.ALWAYS_RESOLVE)) {
            try {
                return ResourceAccess.getResource(request);
            } catch (URISyntaxException | IOException err) {
                // What am I supposed to do about this now?
            }
        }
        return null;
    }

    private XMLInputSource resolveDTD(XMLDTDDescription resId) {
        ResourceRequest request = resolver.getRequest(resId.getLiteralSystemId(), resId.getBaseSystemId(),
                ResolverConstants.DTD_NATURE, ResolverConstants.VALIDATION_PURPOSE);
        request.setEntityName(resId.getRootName());
        request.setPublicId(resId.getPublicId());
        ResourceResponse rsrc = resolver.resolve(request);
        if (!rsrc.isResolved()) {
            ResourceRequest altRequest;
            altRequest = resolver.getRequest(resId.getExpandedSystemId(), resId.getBaseSystemId(),
                    ResolverConstants.DTD_NATURE, ResolverConstants.VALIDATION_PURPOSE);
            altRequest.setEntityName(resId.getRootName());
            altRequest.setPublicId(resId.getPublicId());
            rsrc = resolver.resolve(altRequest);
        }
        if (!rsrc.isResolved()) {
            rsrc = safeOpenConnection(request);
        }
        XMLInputSource source = null;
        if (rsrc != null && rsrc.isResolved()) {
            source = new SAXInputSource(new ResolverInputSource(rsrc));
        }
        return source;
    }

    private XMLInputSource resolveEntity(XMLEntityDescription resId) {
        String name = resId.getEntityName();
        if (name.startsWith("%") || name.startsWith("&")) {
            // Oh, please. The [expletive]?
            name = name.substring(1);
        }
        ResourceRequest request = resolver.getRequest(resId.getLiteralSystemId(), resId.getBaseSystemId(),
                ResolverConstants.EXTERNAL_ENTITY_NATURE, ResolverConstants.ANY_PURPOSE);
        request.setEntityName(name);
        request.setPublicId(resId.getPublicId());
        ResourceResponse rsrc = resolver.resolve(request);
        if (!rsrc.isResolved()) {
            ResourceRequest altRequest;
            altRequest = resolver.getRequest(resId.getExpandedSystemId(), resId.getBaseSystemId(),
                    ResolverConstants.EXTERNAL_ENTITY_NATURE, ResolverConstants.ANY_PURPOSE);
            altRequest.setEntityName(name);
            altRequest.setPublicId(resId.getPublicId());
            rsrc = resolver.resolve(altRequest);
        }
        if (rsrc == null) {
            rsrc = safeOpenConnection(request);
        }
        return rsrc == null ? null : new SAXInputSource(new ResolverInputSource(rsrc));
    }

    private XMLInputSource resolveSchema(XSDDescription resId) {
        ResourceRequest request = null;
        ResourceResponse rsrc = null;

        if (resId.getLiteralSystemId() != null) {
            // If there's a "system identifier" then there's either been a schema location
            // hint of some sort or this is an xsd:include. Try to resolve the URI.
            request = resolver.getRequest(resId.getLiteralSystemId(), resId.getBaseSystemId());
            rsrc = resolver.resolve(request);
            if (!rsrc.isResolved()) {
                rsrc = safeOpenConnection(request);
            }
        } else {
            // We don't want to do namespace resolution if there was a hint because
            // that would take us "back to the top" if some xs:include or xs:import
            // was 404.
            request = resolver.getRequest(resId.getNamespace(), resId.getBaseSystemId(), ResolverConstants.NATURE_XML_SCHEMA, ResolverConstants.PURPOSE_SCHEMA_VALIDATION);
            rsrc = resolver.resolve(request);
        }

        if (rsrc != null && rsrc.isResolved()) {
            InputSource source = new ResolverInputSource(rsrc);
            source.setSystemId(rsrc.getResolvedURI().toString());
            return new SAXInputSource(source);
        }

        return null;
    }
}
