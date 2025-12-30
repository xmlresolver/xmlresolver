package org.xmlresolver;

import org.xmlresolver.logging.ResolverLogger;
import org.xmlresolver.spi.SchemeResolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manage the connection to a resource.
 *
 * <p>The connection to a <code>file:</code>, <code>http:</code>, or <code>https:</code> can be
 * managed by creating the connection and then calling the <code>get()</code> method. Any other
 * schemes supported by Java's underlying {@link URLConnection} class will also work this way.</p>
 *
 * <p>It's also possible to construct an instance of this class and then set its properties directly.
 * This is how a {@link SchemeResolver} can provide the connection to a resource.</p>
 */
public class ResourceConnection {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    private static final Pattern CHARSET = Pattern.compile(";\\s*charset\\s*=\\s*([^;]+)");

    private InputStream stream = null;
    private URI uri = null;
    private URI redirect = null;
    private int statusCode = -1;
    private String contentType = null;
    private String encoding = null;
    private String etag = null;
    private Long lastModified = -1L;
    private Long date = -1L;
    private final Map<String,List<String>> headers = new HashMap<> ();
    private boolean connected = false;

    /**
     * ResourceConnection constructor.
     *
     * <p>The {@code headOnly} parameter is a bit of a hack. It's used internally when the
     * existence of the URI is being tested, but a stream is not desired.</p>
     *
     * @param initialUri The URI to access.
     */
    public ResourceConnection(/* not null */ URI initialUri) {
        uri = initialUri;
    }

    /**
     * Attempt to resolve the URI with a URLConnection
     *
     * @param config The XML Resolver configuration.
     */
    public void get(ResolverConfiguration config) {
        get(config, false);
    }

    /**
     * Attempt to resolve the URI with a URLConnection
     *
     * <p>The {@code headOnly} parameter is a bit of a hack. It's used internally when the
     * existence of the URI is being tested, but a stream is not desired.</p>
     *
     * @param config The XML Resolver configuration.
     * @param headOnly Do a "head only" request, returning headers and such but not a stream to read the resource.
     */
    public void get(ResolverConfiguration config, boolean headOnly) {
        try {
            HashSet<URI> visited = new HashSet<>();
            URI connUri = uri;
            URLConnection connection = null;

            // N.B. By design, HttpURLConnection won't follow redirects across different protocols. But we will.
            boolean tryAgain = true;
            while (tryAgain) {
                visited.add(connUri);

                URL url = connUri.toURL();
                connection = url.openConnection();

                if (connection instanceof HttpURLConnection) {
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    httpConnection.setInstanceFollowRedirects(true);
                    if (headOnly) {
                        httpConnection.setRequestMethod("HEAD");
                    } else {
                        httpConnection.setRequestMethod("GET");
                    }
                    httpConnection.connect();

                    statusCode = httpConnection.getResponseCode();
                    tryAgain = statusCode == HttpURLConnection.HTTP_MOVED_PERM || statusCode == HttpURLConnection.HTTP_MOVED_TEMP;

                    if (tryAgain) {
                        if (!headOnly) {
                            try {
                                httpConnection.getInputStream().close();
                            } catch (IOException ex) {
                                // ignore
                            }
                        }
                        String location = httpConnection.getHeaderField("Location");
                        connUri = URI.create(location);
                        if (visited.contains(connUri)) {
                            throw new IllegalArgumentException("Redirect URI already visited: " + connUri);
                        }
                    }
                } else {
                    tryAgain = false;
                    connection.connect();
                    statusCode = 200; // Assume it's "OK" if the connect() succeeded
                }
            }

            connected = true;
            if (headOnly) {
                if (connection.getInputStream() != null) {
                    connection.getInputStream().close();
                }
            } else {
                stream = connection.getInputStream();
            }

            contentType = connection.getContentType();
            encoding = getEncoding(contentType);
            etag = connection.getHeaderField("ETag");
            lastModified = connection.getLastModified();
            date = connection.getDate();

            Map<String,List<String>> connectionHeaders = connection.getHeaderFields();
            for (String key : connectionHeaders.keySet()) {
                if (key == null) { // used for the HTTP response code
                    continue;
                }
                String name = key.toLowerCase();
                headers.put(name, connectionHeaders.get(key));
            }

            redirect = connection.getURL().toURI();
            if (uri.equals(redirect)) {
                redirect = null;
            }
        } catch (URISyntaxException | IOException | IllegalArgumentException use) {
            ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
            logger.warn("Failed to %s: %s: %s", headOnly ? "HEAD" : "GET", uri, use.getMessage());
        }
    }

    /**
     * Get the stream.
     * @return A stream to read the resource, if one is available.
     */
    public InputStream getStream() {
        return stream;
    }

    /**
     * Set the stream.
     * <p>If the stream is set, the resource is considered connected.</p>
     * @param stream the stream to read from for this resource
     */
    public void setStream(InputStream stream) {
        this.stream = stream;
        this.connected = true;
    }

    /**
     * Get the headers.
     * @return A map of the headers.
     */
    public Map<String,List<String>> getHeaders() {
        return headers;
    }

    /**
     * Set the headers.
     * @param headers The headers.
     */
    public void setHeaders(Map<String,List<String>> headers) {
        this.headers.clear();
        this.headers.putAll(headers);
    }

    /**
     * Get the content type.
     * <p>The content type may be null if the resource didn't provide one.</p>
     * @return The content type.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Set the content type.
     * @param type The content type.
     */
    public void setContentType(String type) {
        contentType = type;
    }

    /**
     * Get the resource encoding.
     * @return The encoding, or null if the encoding is not specified.
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Set the encoding.
     * @param encoding The encoding.
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Get the ETag.
     * <p>ETags are only applicable to HTTP(S) resources.</p>
     * @return The Etag or null if there isn't one.
     */
    public String getEtag() {
        return etag;
    }

    /**
     * Set the ETag.
     * @param etag The ETag.
     */
    public void setEtag(String etag) {
        this.etag = etag;
    }

    /**
     * Get the URI of the request.
     * @return The URI of the request.
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Get the URI that was ultimately retrieved.
     * <p>In the case of HTTP(S) redirects, the final URI may differ from the requested URI. The final URI
     * should be used when computing base URIs.</p>
     * @return The final, redirected URI (or the request URI if no redirects occurred).
     */
    public URI getRedirect() {
        return redirect;
    }

    /**
     * Set the URI that was ultimately retrieved.
     * <p>In the case where the protocol supports redirects, the final URI may differ from the requested URI.</p>
     * @param redirect The redirect.
     */
    public void setRedirect(URI redirect) {
        this.redirect = redirect;
    }

    /**
     * Get the status code of the request.
     * <p>As a convenience to users, any successful request (even one from a filesystem or other scheme)
     * returns 200.</p>
     * @return The status code.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Set the status code of the request.
     * @param code The status code.
     */
    public void setStatusCode(int code) {
        statusCode = code;
    }

    /**
     * Get the last modified time.
     * @return The last modified time or -1 if no last modified time is available.
     */
    public long getLastModified() {
        return lastModified;
    }

    /**
     * Set the last modified time.
     * @param lastModified The last modified time.
     */
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Get the resource date.
     * @return The date or -1 if no date is available.
     */
    public long getDate() {
        return date;
    }

    /**
     * Set the resource date.
     * @param date The resource date.
     */
    public void setDate(long date) {
        this.date = date;
    }

    /**
     * Get the connected status.
     * <p>This status is true if a stream is provided.</p>
     * @return True if and only if a connection was successfully made to access the resource.
     */
    public boolean isConnected() {
        return connected;
    }

    private String getEncoding(String contentType) {
        // text/plain; charset=iso-8859-1
        if (contentType != null) {
            int pos = contentType.indexOf(";");
            if (pos >= 0) {
                contentType = contentType.substring(pos);
                Matcher match = CHARSET.matcher(contentType);
                if (match.find()) {
                    return match.group(1);
                }
            }
        }
        return null;
    }
}
