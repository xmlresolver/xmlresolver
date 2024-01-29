/** The XML (Catalog) Resolver.
 *
 * <p>The resolver maps external identifiers (document type declarations
 * and external parsed entities) and URIs found in XML documents to other
 * resources. This implementation supports additional interfaces to
 * perform redirection through RDDL documents and automated caching of
 * resources retrieved from the web.</p>
 *
 * <h1>TL;DR</h1>
 *
 * <p>Instantiate an {@link org.xmlresolver.XMLResolver} (it has a zero argument constructor
 * so that it can be instantiated just from its name). Use the methods provided on the {@code XMLResolver}
 * to instantiate the resolver interfaces that you require.</p>
 *
 * <p>Tell your processor to instantiate a
 * {@link org.xmlresolver.tools.ResolvingXMLReader} for parsing.</p>
 *
 * <h1>L;OS (long; only skimmed)</h1>
 *
 * <p>For most users, the principle entry points to this API will be the
 * {@link org.xmlresolver.XMLResolver} class and the {@link org.xmlresolver.tools.ResolvingXMLReader}. These instantiate
 * a resolver that can be configured with either system properties or
 * a properties file.</p>
 *
 * <p>The {@link org.xmlresolver.XMLResolver} has methods to construct a wide variety of resolver interfaces:</p>
 *
 * <ul>
 * <li><a href="https://docs.oracle.com/javase/8/docs/api/javax/xml/transform/URIResolver.html"><code>javax.xml.transform.URIResolver</code></a> {@link org.xmlresolver.XMLResolver#getURIResolver}.</li>
 * <li><a href="https://docs.oracle.com/javase/8/docs/api/org/w3c/dom/ls/LSResourceResolver.html"><code>org.w3c.dom.ls.LSResourceResolver</code></a> {@link org.xmlresolver.XMLResolver#getLSResourceResolver()}.</li>
 * <li><a href="https://docs.oracle.com/javase/8/docs/api/org/xml/sax/EntityResolver.html"><code>org.xml.sax.EntityResolver</code></a> {@link org.xmlresolver.XMLResolver#getEntityResolver()}.</li>
 * <li><a href="https://docs.oracle.com/javase/8/docs/api/org/xml/sax/ext/EntityResolver2.html"><code>org.xml.sax.ext.EntityResolver2</code></a> {@link org.xmlresolver.XMLResolver#getEntityResolver2()}.</li>
 * <li>{@link javax.xml.stream.XMLResolver} {@link org.xmlresolver.XMLResolver#getXMLResolver()}</li>
 * </ul>
 *
 * <p>The <code>ResolvingXMLReader</code> class extends the SAX parser so that
 * it will automatically construct and use a resolver.
 *
 * <h1>Accessing the resolver</h1>
 *
 * <p>The resolver is configured with a {@link org.xmlresolver.ResolverConfiguration}, more specifically
 * in this release, an {@link org.xmlresolver.XMLResolverConfiguration}. This class allows you to configure
 * features of the resolver.</p>
 */

package org.xmlresolver;
