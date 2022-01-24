/*
 * Resource.java
 *
 * Created on January 9, 2007, 7:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import org.xmlresolver.utils.URIUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Represents a web resource.
 *
 * <p>An web resource consists of an absolute URI, an input stream, and a MIME content type.
 * In some circumstances, such as when the initial URI is a <code>file:</code> URI, the
 * content type may be unknown (<code>null</code>).</p>
 */
public class Resource {
    private final InputStream stream;
    private final URI uri;
    private final URI localURI;
    private final String contentType;

    /**
     * Creates a new instance of Resource.
     *
     * @param stream   The stream from which the resource can be read
     * @param uri      The URI
     * @param localURI The local URI, irrespective of what will be reported as the URI.
     */
    public Resource(InputStream stream, URI uri, URI localURI) {
        this.stream = stream;
        this.uri = uri;
        this.localURI = localURI;
        this.contentType = null;
    }

    /**
     * Creates a new instance of Resource.
     *
     * @param stream      The stream from which the resource can be read
     * @param uri         The URI
     * @param localURI    The local URI, irrespective of what will be reported as the URI.
     * @param contentType The content type
     */
    public Resource(InputStream stream, URI uri, URI localURI, String contentType) {
        this.stream = stream;
        this.uri = uri;
        this.localURI = localURI;
        this.contentType = contentType;
    }

    /**
     * Creates a new instance of Resource from just a URI.
     *
     * <p>This version has to use the default class loader to access <code>classpath:</code> URIs.
     * It is used by the zero-argument constructors for the {@link org.xmlresolver.loaders.XmlLoader}
     * and {@link org.xmlresolver.loaders.ValidatingXmlLoader}. When possible, pass the resolver
     * configuration to the constructor.</p>
     *
     * @param href The URI
     * @throws IOException for I/O errors
     * @throws URISyntaxException if the href cannot be converted to a URI
     */
    public Resource(String href) throws IOException, URISyntaxException {
        this(null, href);
    }

    /**
     * Creates a new instance of Resource from just a URI.
     *
     * <p>This version will use the configured class loader to access <code>classpath:</code> URIs.
     * </p>
     *
     * @param config The configuration (used to get the classloader)
     * @param href The URI
     * @throws IOException for I/O errors
     * @throws URISyntaxException if the href cannot be converted to a URI
     */
    public Resource(ResolverConfiguration config, String href) throws IOException, URISyntaxException {
        if (href.startsWith("data:")) {
            // This is a little bit crude; see RFC 2397
            uri = URIUtils.newURI(href);
            localURI = uri;
            String path = href.substring(5);
            int pos = path.indexOf(",");
            if (pos >= 0) {
                String mediatype = path.substring(0, pos);
                String data = path.substring(pos + 1);
                if (mediatype.endsWith(";base64")) {
                    // Base64 decode it
                    stream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
                    contentType = mediatype.substring(0, mediatype.length() - 7);
                } else {
                    // URL decode it
                    String charset = "UTF-8";
                    pos = mediatype.indexOf(";charset=");
                    if (pos > 0) {
                        charset = mediatype.substring(pos + 9);
                        pos = charset.indexOf(";");
                        if (pos >= 0) {
                            charset = charset.substring(0, pos);
                        }
                    }
                    data = URLDecoder.decode(data, charset);
                    stream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
                    contentType = "".equals(mediatype) ? null : mediatype;
                }
                return;
            } else {
                throw new URISyntaxException(href, "Comma separator missing");
            }
        }

        if (href.startsWith("classpath:")) {
            String path = href.substring(10);
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            // The URI class throws exceptions if you attempt to manipulate
            // classpath: URIs. Fair enough, given their ad hoc invention
            // by the Spring framework. We're going to cheat a little bit here
            // and replace the classpath: URI with the actual URI of the resource
            // found (if one is found). That means downstream processes will
            // have a "useful" URI. It still might not work, due to class loaders and
            // such, but at least it won't immediately blow up.
            ClassLoader loader;
            if (config == null) {
                loader = getClass().getClassLoader();
            } else {
                loader = config.getFeature(ResolverFeature.CLASSLOADER);
            }
            URL rsrc = loader.getResource(path);
            if (rsrc == null) {
                throw new IOException("Not found: " + href);
            } else {
                uri = URIUtils.newURI(rsrc.toString());
                localURI = uri;
                stream = rsrc.openStream();
                contentType = null;
                return;
            }
        }

        uri = URIUtils.newURI(href);
        localURI = uri;
        URLConnection conn = uri.toURL().openConnection();
        stream = conn.getInputStream();
        contentType = conn.getContentType();
    }

    /** Return the InputStream associated with the resource.
     *
     * <p>The stream returned is the actual stream used when creating the resource. Reading from this
     * stream changes the resource.</p>
     *
     * @return The stream
     */
    public InputStream body() {
        return stream;
    }

    /** Return the URI associated with the resource.
     *
     * <p>For resources that are cached, or from a jar file, this may be the "original" URI
     * instead of the actually resolved URI.</p>
     *
     * @return The URI
     */
    public URI uri() {
        return uri;
    }

    /** Return the local URI associated with the resource.
     *
     * <p>This is always the local URI, whether it's from a jar file or from the cache.</p>
     *
     * @return The URI
     */
    public URI localUri() {
        return localURI;
    }

    /** Return the resolved URI.
     *
     * <p>The resolved URI may be different from the local URI of the resource.</p>
     */

    /** Return the MIME content type associated with the resource.
     *
     * @return The content type
     */
    public String contentType() {
        return contentType;
    }
}
