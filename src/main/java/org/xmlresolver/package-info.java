/**
 * <p>The XML Resolver. The resolver maps external identifiers (document type
 * declarations and external parsed entities) and URIs found in XML documents to
 * local resources. This implementation supports a new interface to perform
 * redirection through RDDL documents and automated caching of resources retrieved
 * from the web.</p>
 * <div>
 * <h1>Performing resolution</h1>
 * <p>For most users, the principle entry points to this
 * API will be the <code>Resolver</code> class. Programmers may also want to
 * take advantage of the <code>NamespaceResolver</code> interface.</p>
 * <div>
 * <h2>The <code>Resolver</code> Class</h2>
 * <p>The <code>Resolver</code> class implements the
 * <code>EntityResolver</code> interface (from <code>org.xml.sax</code>), the
 * <code>URIResolver</code> interface (from <code>javax.xml.Transformer</code>),
 * and the <code>NamespaceResolver</code> (described above).</p>
 * <p>Use instances of this class as the resolvers for parsers you create.
 * (Or construct parsers with the utility classes in the
 * <code>org.xmlresolvers.tools</code> package which perform catalog
 * resolution automatically.)</p>
 * </div>
 * <div>
 * <h2>The <code>NamespaceResolver</code> Interface</h2>
 * <p>The <code>NamespaceResolver</code> interface is an extension of the
 * <code>URIResolver</code> interface from the JAXP Transformer package.
 * It provides a single method:</p>
 * <pre>public Source resolveNamespace(String uri, String nature, String purpose)</pre>
 * <p>Use this method in your applications (instead of the <code>URIResolver</code>)
 * where you are attempting to find information about a namespace URI.</p>
 * <p>The intent of this method is that it returns the
 * <code>Source</code> associated with the namespace <code>uri</code>
 * that has the specified <a href="http://www.rddl.org/">RDDL</a> nature
 * and purpose.</p>
 * <p>The XML Resolver implements catalog extension attributes that allow
 * a user to specify the nature and purpose of a URI. It also parses RDDL
 * (1.0) documents.
 * </p>
 * </div>
 * </div>
 * <div>
 * <h1>Accessing the resolver</h1>
 * <p>Applications that wish to take advantage of the resolver more directly
 * may wish to interact with the remaining classes in the API:</p>
 * <ul>
 * <li>A <code>Catalog</code> implements the
 * <a href="http://www.oasis-open.org/apps/group_public/download.php/14809/xml-catalogs.html">OASIS XML Catalogs</a> Standard. It loads one or more
 * catalog files and provides methods for searching the catalog. A successful
 * search returns a <code>CatalogResult</code>.</li>
 * <li>A <code>ResourceResolver</code> provides direct access to resources from the catalog.
 * Where the methods in the <code>Resolver</code> always returns a
 * <code>Source</code> or <code>InputSource</code>, the methods in the
 * <code>ResourceResolver</code> return <code>Resource</code>s.
 * A <code>Resource</code> represents a web resource, it has a URI, a MIME
 * content type, and a body.</li>
 * <li>A <code>ResourceCache</code> performs automatic caching of resources.
 * It is used automatically by the <code>Catalog</code> and
 * <code>ResourceResolver</code> (and <code>Resolver</code>) classes when
 * caching is enabled by the catalog.
 * </li>
 * </ul>
 * </div>
 */
package org.xmlresolver;