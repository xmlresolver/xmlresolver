/**
 * <p>Service provider interfaces.</p>
 *
 * <p>The XML Resolver uses an SPI to manage scheme resolvers. Scheme resolvers are
 * a pluggable interface for resolving arbitrary URI schemes.</p>
 *
 * <p>Suppose, for example, that you have a database product that supports a custom
 * URI scheme, <code>db:</code>. In other words, in the context of the database
 * product, a request for <code>db://someID</code> will return a resource.</p>
 *
 * <p>All well and good, but consider a catalog that maps to URIs of that scheme.</p>
 *
 * <pre>
 * &lt;public publicId="-//Sample//DTD Sample 1.0//EN"
 *         uri="db://sample-10-id"/&gt;
 * </pre>
 *
 * <p>This is problematic because the resolver can't resolve the <code>db:</code>-scheme
 * URI and combined with options like "always resolve" this may lead to errors.</p>
 *
 * <p>To support this use case, the resolver now allows custom scheme resolvers to
 * be provided. They can be installed programmatically:</p>
 *
 * <pre>
 * XmlResolverConfiguration config = ...
 * SchemeResolver dbSchemeResolver = ...
 * config.registerSchemeResolver("db", dbSchemeResolver)
 * ...
 * </pre>
 *
 * <p>Resolvers using this configuration will defer to {@code dbSchemeResolver} for
 * URIs with the <code>db:</code> scheme. More than one resolver can be associated
 * with a scheme, they will be tried in turn. If none succeed, processing falls back
 * to the default behavior of attempting to access the resource with standard Java APIs.</p>
 *
 * <p>Rather than installing the resolver programmatically, it can be done automatically
 * with an <a href="https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html">SPI</a>.</p>
 *
 * <ul>
 * <li>Create a {@code META-INF/services/org.xmlresolver.spi.SchemaResolverProvider} resource
 * that identifies an implementation of a {@code SchemaResolverProvider}.</li>
 * <li>The provider will be called to create a {@code SchemaResolverManager}.</li>
 * <li>The manager identifies the schemes supported and provides {@code SchemaResolvers} for them.
 * </li>
 * </ul>
 *
 * <p>Note that <em>all</em> of the providers are instantiated once when the XML Resolver
 * is initialized. This is a performance compromise because the the expectation is that
 * the resolver will be called many times and instantiating the SPI over-and-over again
 * could be expensive.</p>
 */
package org.xmlresolver.spi;
