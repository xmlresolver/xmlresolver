package org.xmlresolver;

import java.net.URI;

/** The Catalog resolver interface.
 *
 * <p>The <code>CatalogResolver</code> interface exposes the catalog-based lookup
 * functionality of the OASIS Open XML Catalogs 1.1 specification. It supports
 * all of the normative catalog entries and several of the TR9401 entries
 * described in Appendix D.</p>
 *
 * <p>The API doesn't expose methods to access <code>catalog</code>, <code>group</code>, or
 * <code>nextCatalog</code> entries because they are internal to the resolution
 * process. A conformant catalog processor must support them.</p>
 *
 * <p>The <code>dtddecl</code>, <code>sgmldecl</code>, and <code>linktype</code>
 * entries are not supported because they have no useful purpose in XML. </p>
 */
public interface CatalogResolver {
    /** Lookup a URI.
     *
     * <p>This method searches <code>uri</code>, <code>rewriteURI</code>, <code>uriSuffix</code>,
     * and <code>delegateURI</code> catalog entries.</p>
     *
     * @param uri The request URI
     * @return The mapped URI, or null if no mapping was found.
     */
    public URI lookupURI(String uri);

    /** Lookup an external identifier by public and system identifier.
     *
     * <p>This method searches <code>public</code>, <code>delegatePublic</code>, <code>system</code>,
     * <code>delegateSystem</code>, <code>rewriteSystem</code>, and <code>systemSuffix</code>
     * catalog entries.</p>
     *
     * <p>If both public and system mappings exist, the match depends on the setting of the
     * "prefer public" option.</p>
     *
     * <p>Note that public identifiers can be encoded as URNs per RFC 3151. A call to:</p>
     *
     * <pre>lookupPublic("urn:publicid:-:OASIS:DTD+DocBook+XML+V4.1.2:EN", null)</pre>
     *
     * <p>Is precisely the same as a call to</p>
     *
     * <pre>lookupPublic(null, "-//OASIS//DTD DocBook XML V4.1.2//EN")</pre>
     *
     * <p>If both the public and system identifiers are <code>null</code>, <code>null</code> is returned.</p>
     *
     * @param systemId The system identifier, possibly null.
     * @param publicId The public identifier, possibly null.
     * @return The mapped URI, or null if no mapping was found.
     */
    public URI lookupPublic(String systemId, String publicId);

    /** Lookup an external identifier by system identifier.
     *
     * <p>This method searches <code>system</code>,
     * <code>delegateSystem</code>, <code>rewriteSystem</code>, and <code>systemSuffix</code>
     * catalog entries.</p>
     *
     * @param systemId The system identifier.
     * @return The mapped URI, or null if no mapping was found.
     */
    public URI lookupSystem(String systemId);

    /** Lookup a document type.
     *
     * <p>This method searches for a document type. A document type, for example:</p>
     *
     * <pre>&lt;!DOCTYPE book SYSTEM "urn:publicid:-:OASIS:DTD+DocBook+XML+V4.1.2:EN"&gt;</pre>
     *
     * <p>Consists of an entity name ("book") and optional system and public identifiers.
     * If either (or both) of system and public identifiers are provided, matches for those
     * identifiers are preferred over a match on the entity name.</p>
     *
     * <p>If no external identifier is provided, the <code>entityName</code> will be matched
     * against <code>doctype</code> catalog entries.</p>
     *
     * @param entityName The entity name.
     * @param systemId The system identifier, possibly null.
     * @param publicId The public identifier, possibly null.
     * @return The mapped URI, or null if no mapping was found.
     */
    public URI lookupDoctype(String entityName, String systemId, String publicId);

    /** Lookup an entity.
     *
     * <p>This method searches for an entity. An entity:</p>
     *
     * <pre>&lt;!ENTITY chapter1 SYSTEM "chap1.xml"&gt;</pre>
     *
     * <p>consists of an entity name ("chapter1") and an external identifier.
     * If either (or both) of system and public identifiers are provided, matches for those
     * identifiers are preferred over a match on the entity name.</p>
     *
     * <p>If no external identifier is provided, the <code>entityName</code> will be matched
     * against <code>entity</code> catalog entries.</p>
     *
     * @param entityName The entity name.
     * @param systemId The system identifier, possibly null.
     * @param publicId The public identifier, possibly null.
     * @return The mapped URI, or null if no mapping was found.
     */
    public URI lookupEntity(String entityName, String systemId, String publicId);

    /** Lookup a notation.
     *
     * <p>This method searches for a notation. A notation:</p>
     *
     * <pre>&lt;!NOTATION thing SYSTEM "thing.bin"&gt;</pre>
     *
     * <p>consists of a notation name ("thing") and optional system and public identifiers.
     * If either (or both) of system and public identifiers are provided, matches for those
     * identifiers are preferred over a match on the entity name.</p>
     *
     * <p>If no external identifier is provided, the <code>notationName</code> will be matched
     * against <code>notation</code> catalog entries.</p>
     *
     * @param notationName The notation name.
     * @param systemId The system identifier, possibly null.
     * @param publicId The public identifier, possibly null.
     * @return The mapped URI, or null if no mapping was found.
     */
    public URI lookupNotation(String notationName, String systemId, String publicId);

    /** Lookup a default document.
     *
     * @return The URI of the default document, or null if no mapping was found.
     *
     * <p>This method returns (the first) matching <code>document</code> entry.</p>
     */
    public URI lookupDocument();
}
