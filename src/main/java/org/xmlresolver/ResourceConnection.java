package org.xmlresolver;

import org.xmlresolver.logging.AbstractLogger;
import org.xmlresolver.logging.ResolverLogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class handles attempts to access resources with <code>file:</code>,
 * <code>http</code>, and <code>https</code> URIs. It may support additional URI schemes,
 * if they're supported by Java's underlying {@link URLConnection} class.</p>
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
     * @param config The XML Resolver configuration.
     * @param initialUri The URI to access.
     * @param headOnly Do a "head only" request, returning headers and such but not a stream to read the resource.
     */

    public ResourceConnection(ResolverConfiguration config, URI initialUri, boolean headOnly) {
        try {
            HashSet<URI> visited = new HashSet<>();
            uri = initialUri;
            URI connUri = initialUri;
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
            logger.log(AbstractLogger.WARNING, "Failed to %s: %s: %s", headOnly ? "HEAD" : "GET", uri, use.getMessage());
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
     * Get the headers.
     * @return A map of the headers.
     * <p>Headers are only applicable to HTTP(S) resources.</p>
     */
    public Map<String,List<String>> getHeaders() {
        return headers;
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
     * Get the resource encoding.
     * @return The encoding, or null if the encoding is not specified.
     */
    public String getEncoding() {
        return encoding;
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
     * Get the status code of the request.
     * <p>As a convenience to users, any successful request (even one from a filesystem or other scheme)
     * returns 200.</p>
     * @return The status code.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Get the last modified time.
     * @return The last modified time or -1 if no last modified time is available.
     */
    public long getLastModified() {
        return lastModified;
    }

    /**
     * Get the resource date.
     * @return The date or -1 if no date is available.
     */
    public long getDate() {
        return date;
    }

    /**
     * Get the connected status.
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
