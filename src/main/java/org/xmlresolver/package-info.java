/** The XML (Catalog) Resolver.
 *
 * <p>The resolver maps external identifiers (document type declarations
 * and external parsed entities) and URIs found in XML documents to other
 * resources. This implementation supports additional interfaces to
 * perform redirection through RDDL documents and automated caching of
 * resources retrieved from the web.</p>
 *
 * <h2>TL;DR</h2>
 *
 * <p>Tell your processor to instantiate a {@link org.xmlresolver.Resolver} (it has a zero argument constructor
 * so that it can be instantiated just from its name) and use it as the entity and URI resolver.</p>
 *
 * <p>Tell your processor to instantiate a
 * {@link org.xmlresolver.tools.ResolvingXMLReader} for parsing.</p>
 *
 * <h2>L;OS (long; only skimmed)</h2>
 *
 * <p>For most users, the principle entry points to this API will be the
 * {@link org.xmlresolver.Resolver} class and the {@link org.xmlresolver.tools.ResolvingXMLReader}. These instantiate
 * a resolver that can be configured with either system properties or
 * a properties file.</p>
 *
 * <p>The {@link org.xmlresolver.Resolver} class implements a wide variety of resolver APIs:</p>
 *
 * <ul>
 * <li><a href="https://docs.oracle.com/javase/8/docs/api/javax/xml/transform/URIResolver.html"><code>javax.xml.transform.URIResolver</code></a>.</li>
 * <li><a href="https://docs.oracle.com/javase/8/docs/api/org/w3c/dom/ls/LSResourceResolver.html"><code>org.w3c.dom.ls.LSResourceResolver</code></a>.</li>
 * <li><a href="https://docs.oracle.com/javase/8/docs/api/org/xml/sax/EntityResolver.html"><code>org.xml.sax.EntityResolver</code></a>.</li>
 * <li><a href="https://docs.oracle.com/javase/8/docs/api/org/xml/sax/ext/EntityResolver2.html"><code>org.xml.sax.ext.EntityResolver2</code></a>.</li>
 * <li>{@link org.xmlresolver.NamespaceResolver}</li>
 * </ul>
 *
 * <p>The StAX <code>XMLResolver</code> interface
 * is incompatible with the <code>EntityResolver2</code>
 * interface, so it’s implemented in {@link org.xmlresolver.StAXResolver}:</p>
 *
 * <ul>
 * <li><a href="https://docs.oracle.com/javase/8/docs/api/javax/xml/stream/XMLResolver.html"><code>javax.xml.stream.XMLResolver</code></a></li>
 * </ul>
 *
 * <p>The <code>ResolvingXMLReader</code> class extends the SAX parser so that
 * it will automatically construct and use a resolver.
 *
 * <h2>Accessing the resolver</h2>
 *
 * <p>The resolver is configured with a {@link org.xmlresolver.ResolverConfiguration}, more specifically
 * in this release, an {@link org.xmlresolver.XMLResolverConfiguration}. This class allows you to configure
 * features of the resolver.</p>
 */

package org.xmlresolver;
