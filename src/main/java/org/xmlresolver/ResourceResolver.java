package org.xmlresolver;

/** The resource resolver interface.
 *
 * <p>On the one hand, the {@link Resolver} class in this API is a concrete class, not suitable
 * for independent implementation. But on the other, it's widely used and can be instantiated
 * from just its class name. (So it's handy as the name of a resolver class passed on the
 * command line or in a configuration file.)</p>

 * <p>Further complicating matters, there are conflicting interfaces for doing resolution.
 * In particular, the {@link javax.xml.stream.XMLResolver} and
 * {@link org.xml.sax.ext.EntityResolver2} interfaces
 * can't be implemented by the same class.</p>
 *
 * <p>This interface is an attempt to abstract away from the existing interfaces and expose
 * something that could be implemented independently that provides enough functionality t
 * implement all of the common resolver interfaces.</p>
 */


public interface ResourceResolver {
    /** Resolve a URI.
     *
     * <p>Attempts to resolve the specified href URI. If resolution fails, the href
     * URI is not already absolute, and the baseURI is
     * not null, the href value is made absolute against the baseURI and resolution
     * is attempted with the resulting absolute URI.</p>
     *
     * @param href The possibly relative URI to resolve.
     * @param baseURI The possibly null base URI to use if href is relative and not resolved.
     * @return The resolved resource, or null if resolution fails.
     */
    ResolvedResource resolveURI(String href, String baseURI);

    /** Resolve a namespace URI.
     *
     * <p>Attempts to resolve the specified href URI, optionally employing the baseURI as
     * described in the <code>resolveURI</code> method. This method may take the namespace
     * nature and purpose into consideration, for example by examining a RDDL document,
     * if its returned.</p>
     *
     * @param href The possibly relative URI to resolve.
     * @param base The possibly null base URI to use if href is relative and not resolved.
     * @param nature The possibly null nature URI.
     * @param purpose The possilby null purpose URI.
     * @return The resolved resource, or null if resolution fails.
     */
    ResolvedResource resolveNamespace(String href, String base, String nature, String purpose);

    /** Resolve external identifiers and other entity-like resources.
     *
     * <p>If the systemId is null and the baseURI is not, the systemId is taken to be the baseURI
     * and the baseURI is treated as if it were null.
     *
     * <p>If name, publicId and systemId are all null, null is returned.</p>
     *
     * <p>If systemId or publicId are not null, the method attempts to resolve an external identifier with
     * the specified external identifier and the (possibly null) name and publicId. (Because public
     * identifiers can be encoded in URNs, it's possible for this method to receive a publicId and
     * not a systemId, even in XML systems.)</p>
     *
     * <p>If name is not null, but systemId is, name is assumed to be document type name and
     * the method attempts to resolve an external identifier that matches. A <code>doctype</code> catalog
     * entry, for example.</p>
     *
     * <p>If the systemId is relative, resolution fails, and baseURI is not null, the systemId
     * is made absolute with respect to the baseURI and resolution is attempted a second time.</p>
     *
     * @param name The possibly null entity (or document type) name.
     * @param publicId The possibly null public identifier.
     * @param systemId The possibly relative system identifier to resolve.
     * @param baseURI The possibly null base URI to use if systemId is relative and not resolved.
     * @return The resolved resource, or null if resolution fails.
     */
    ResolvedResource resolveEntity(String name, String publicId, String systemId, String baseURI);

    /** The resolver configuration.
     *
     * <p>Returns the configuration of this resolver.</p>
     *
     * @return The resolver configuration.
     */
    ResolverConfiguration getConfiguration();
}
