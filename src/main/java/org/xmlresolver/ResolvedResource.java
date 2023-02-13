package org.xmlresolver;

import org.xmlresolver.sources.ResolverResourceInfo;

import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** A resolved resource represents a successfully resolved resource.
 *
 * <p>While the {@link XMLCatalogResolver} interface simply maps from request parameters to URIs,
 * the resolver interfaces defined by SAX, DOM, etc. expect open streams to be returned. This
 * abstract class provides the information necessary to support those APIs.</p>
 *
 * <p>The "local" URI is always the URI returned by catalog resolution.
 * The "resolved" URI is <i>almost always</i> the same.
 * They can be different when catalog resolution returns a <code>jar:</code> or
 * <code>classpath:</code> URI. Those schemes are not supported by the {@link
 * java.net.URI} class in a useful way. This will cause problems if the
 * document returned contains relative URI references. Consider this
 * XSLT stylesheet:</p>
 *
 * <pre>&lt;xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 *                 version="3.0"&gt;
 *
 * &lt;xsl:import href="module.xsl"/&gt;
 *
 * &lt;/xsl:stylesheet&gt;</pre>
 *
 * <p>Suppose that it is referenced with the URI <code>http://example.com/xsl/style.xsl</code>
 * and the catalog contains this matching entry:
 *
 * <pre>&lt;uri name="http://example.com/xsl/style.xsl"
 *      uri="classpath:xsl/style.xsl"/&gt;</pre>
 *
 * <p>(An explicit <code>classpath:</code> URI is not the only way for this to arise, if
 * the URI was simply relative to the catalog and the catalog happened to
 * be found with a <code>jar:</code> or <code>classpath:</code> URI, that would have the same
 * effect.)</p>
 *
 * <p>If <code>classpath:xsl/style.xsl</code> is returned as the resolved URI, the XSLT
 * processor will attempt to resolve <code>module.xsl</code> against that as the base
 * URI. If this is done with just the <code>resolve()</code> method on <code>URI</code>, it won’t
 * work. {@link java.net.URI} doesn’t recognize <code>classpath:</code> as a relative
 * URI scheme. The situation is even worse with <code>jar:</code> URIs which have a
 * syntax that is possibly not even sanctioned by the relevant RFCs.</p>
 *
 * <p>In this case, the resolver might choose to return
 * <code>http://example.com/xsl/style.xsl</code> as the resolved URI. The XSLT processor
 * will then form <code>http://example.com/xsl/module.xsl</code> as the URI of the module
 * and, if the catalog author provided an entry for that as well, processing
 * can continue with all of the URIs resolved locally.</p>
 */

public abstract class ResolvedResource implements ResolverResourceInfo {
    /** The resolved URI.
     *
     * <p>This is the URI that should be reported as the resolved URI.</p>
     *
     * @return The resolved URI.
     */
    public abstract URI getResolvedURI();

    /** The local URI.
     *
     * <p>This is the URI that was used to retrieve the resource (to open the input stream). This
     * is usually, but not necessarily always, the same as the resolved URI.</p>
     *
     * @return The local URI.
     */
    public abstract URI getLocalURI();

    /** The input stream.
     *
     * <p>This is the input stream containing the resolved resource. This may return null, in which
     * case it is the application's responsibily to access the resource through its resolved URI.</p>
     *
     * @return The input stream that will return the content of the resolved resource.
     */
    public abstract InputStream getInputStream();

    /** The content type of the resource.
     *
     * <p>If the resolver knows the content type of the resource
     * (for example <code>application/xml</code>), it will be provided here.</p>
     *
     * @return The content type, possibly null.
     */
    public abstract String getContentType();

    /** The status code.
     *
     * <p>This is the status code for this resource. For http: requests, it should be the
     * code returned. For other resource types, it defaults to 200 for convenience.</p>
     *
     * @return The status code of the (final) request.
     */
    public int getStatusCode() {
        return 200;
    }

    /** The headers.
     *
     * <p>This is the set of headers returned for the resolved resource. This may be empty, for example,
     * if the URI was a file: URI. The headers are returned unchanged from the <code>URLConnection</code>,
     * so accessing them has to consider the case-insensitive nature of header names.</p>
     *
     * @return The headers associated with a resource.
     */
    public Map<String, List<String>> getHeaders() {
        return Collections.emptyMap();
    }
}
