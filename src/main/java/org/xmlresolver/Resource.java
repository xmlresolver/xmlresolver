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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
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
    private final String contentType;

    /** Creates a new instance of Resource.
     *
     * @param stream The stream from which the resource can be read
     * @param uri The URI
     */
    public Resource(InputStream stream, URI uri) {
        this.stream = stream;
        this.uri = uri;
        this.contentType = null;
    }

    /** Creates a new instance of Resource.
     *
     * @param stream The stream from which the resource can be read
     * @param uri The URI
     * @param contentType The content type
     */
    public Resource(InputStream stream, URI uri, String contentType) {
        this.stream = stream;
        this.uri = uri;
        this.contentType = contentType;
    }

    public Resource(String href) throws IOException, URISyntaxException {
        if (href.startsWith("data:")) {
            // This is a little bit crude; see RFC 2397
            uri = URIUtils.newURI(href);
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
            URL rsrc = getClass().getClassLoader().getResource(path);
            if (rsrc == null) {
                throw new IOException("Not found: " + href);
            } else {
                uri = URIUtils.newURI(rsrc.toString());
                stream = rsrc.openStream();
                contentType = null;
                return;
            }
        }

        uri = URIUtils.newURI(href);
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
     * @return The URI
     */
    public URI uri() {
        return uri;
    }

    /** Return the MIME content type associated with the resource.
     *
     * @return The content type
     */
    public String contentType() {
        return contentType;
    }
}
