package org.xmlresolver;

/**
 * A collection of constants.
 */
public final class ResolverConstants {
    private ResolverConstants() {
        // No constructor.
    }

    /** The XML Namespace name of OASIS XML Catalog files, "urn:oasis:names:tc:entity:xmlns:xml:catalog". */
    public static final String CATALOG_NS = "urn:oasis:names:tc:entity:xmlns:xml:catalog";
    /** The XML Namespace name of OASIS XML Catalog files, "urn:oasis:names:tc:entity:xmlns:xml:catalog". */
    public static final String TR9401_NS = "urn:oasis:names:tc:entity:xmlns:tr9401:catalog";
    /** The XML Namespace name of RDDL, "http://www.rddl.org/". */
    public static final String RDDL_NS ="http://www.rddl.org/";
    /** The XML Namespace name of XLink, "http://www.w3.org/1999/xlink". */
    public static final String XLINK_NS = "http://www.w3.org/1999/xlink";
    /** The (X)HTML Namespace name, "http://www.w3.org/1999/xhtml". */
    public static final String HTML_NS = "http://www.w3.org/1999/xhtml";
    /** The XML Namespace name, "http://www.w3.org/XML/1998/namespace". */
    public static final String XML_NS = "http://www.w3.org/XML/1998/namespace";
    /** The XML Namespace name of XML Resolver Catalog extensions, "http://xmlresolver.org/ns/catalog". */
    public static final String XMLRESOURCE_EXT_NS = "http://xmlresolver.org/ns/catalog";

    /** The schema validation purpose. */
    public static final String PURPOSE_SCHEMA_VALIDATION = "http://www.rddl.org/purposes#schema-validation";
    /** The XML Schema nature. */
    public static final String NATURE_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    /** The XML Schema 1.1 nature. */
    public static final String NATURE_XML_SCHEMA_1_1 = "http://www.w3.org/2001/XMLSchema/v1.1";
    /** The RELAX NG nature. */
    public static final String NATURE_RELAX_NG = "http://relaxng.org/ns/structure/1.0";

    /** The text nature. */
    public static final String TEXT_NATURE = "https://www.iana.org/assignments/media-types/text/plain";
    /** The binary nature. */
    public static final String BINARY_NATURE = "https://www.iana.org/assignments/media-types/application/octet-stream";
    /** The XML nature. */
    public static final String XML_NATURE = "https://www.iana.org/assignments/media-types/application/xml";
    /** The DTD nature. */
    public static final String DTD_NATURE = "https://www.iana.org/assignments/media-types/application/xml-dtd";
    /** The XML Schema nature. */
    public static final String SCHEMA_NATURE = "http://www.w3.org/2001/XMLSchema";
    /** The RELAX NG nature. */
    public static final String RELAXNG_NATURE = "http://relaxng.org/ns/structure/1.0";
    /** The XML external parsed entity nature. */
    public static final String EXTERNAL_ENTITY_NATURE = "https://www.iana.org/assignments/media-types/application/xml-external-parsed-entity";
    /** The symbolic name for "any" nature, {@code null}. */
    public static final String ANY_NATURE = null;

    /** The validation purpose. */
    public static final String VALIDATION_PURPOSE = "http://www.rddl.org/purposes#validation";
    /** The symbolic name for "any" purpose, {@code null}. */
    public static final String ANY_PURPOSE = null;
}
