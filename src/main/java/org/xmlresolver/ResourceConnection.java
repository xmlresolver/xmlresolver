package org.xmlresolver;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.protocol.RedirectLocations;
import org.apache.hc.client5.http.utils.URIUtils;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.xmlresolver.logging.AbstractLogger;
import org.xmlresolver.logging.ResolverLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class handles attempts to access resources with <code>file:</code>,
 * <code>http</code>, and <code>https</code> URIs. It may support additional URI schemes,
 * if they're supported by Java's underlying {@link URLConnection} class.</p>
 *
 * <p>The HTTP(S) resources are accessed with Apache's HTTP Client libraries.</p>
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
    private Map<String,List<String>> headers = new HashMap<> ();
    private CloseableHttpClient httpclient = null;
    private boolean connected = false;

    /**
     * ResourceConnection constructor.
     *
     * <p>The {@code headOnly} parameter is a bit of a hack. It's used internally when the
     * existence of the URI is being tested, but a stream is not desired.</p>
     *
     * @param config The XML Resolver configuration.
     * @param uri The URI to access.
     * @param headOnly Do a "head only" request, returning headers and such but not a stream to read the resource.
     */
    public ResourceConnection(ResolverConfiguration config, URI uri, boolean headOnly) {
        try {
            URL url = uri.toURL();

            if ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme())) {
                // Use Apache HttpClient so that we can follow the redirect
                httpclient = HttpClients.createDefault();
                HttpClientContext context = HttpClientContext.create();

                HttpUriRequestBase httpreq = null;
                if (headOnly) {
                    httpreq = new HttpHead(uri);
                } else {
                    httpreq = new HttpGet(uri);
                }

                ClassicHttpResponse httpResponse = httpclient.execute(httpreq, context);
                HttpHost target = context.getHttpRoute().getTargetHost();
                RedirectLocations redirectLocations = context.getRedirectLocations();
                URI location = URIUtils.resolve(httpreq.getUri(), target, redirectLocations.getAll());
                if (uri.equals(location)) {
                    redirect = null;
                } else {
                    redirect = location;
                }

                connected = true;
                statusCode = httpResponse.getCode();
                contentType = getHeader(httpResponse, "Content-Type", "application/octet-stream");
                encoding = getEncoding(contentType);
                etag = getHeader(httpResponse, "Etag", null);
                if (!headOnly) {
                    stream = httpResponse.getEntity().getContent();
                }

                Header[] httpHeaders = httpResponse.getHeaders();
                if (httpHeaders != null) {
                    for (Header header : httpHeaders) {
                        String name = header.getName().toLowerCase();
                        if (!headers.containsKey(name)) {
                            headers.put(name, new ArrayList<>());
                        }
                        headers.get(name).add(header.getValue());

                        if ("last-modified".equals(name) && lastModified <= 0) {
                            try {
                                Date d = DATE_FORMAT.parse(header.getValue());
                                lastModified = d.getTime();
                            } catch (ParseException e) {
                                // nop
                            }
                        }

                        if ("date".equals(name) && date <= 0) {
                            try {
                                Date d = DATE_FORMAT.parse(header.getValue());
                                date = d.getTime();
                            } catch (ParseException e) {
                                // nop
                            }
                        }
                    }
                }
            } else {
                URLConnection connection = url.openConnection();
                connection.connect();

                if (headOnly) {
                    connection.getInputStream().close();
                } else {
                    stream = connection.getInputStream();
                }

                connected = true;
                contentType = connection.getHeaderField("Content-Type");
                encoding = getEncoding(contentType);
                etag = connection.getHeaderField("Etag");
                lastModified = connection.getLastModified();
                date = connection.getDate();

                // If some other protocol extends HttpUrlConnection, we can
                // get the response code from it.
                if (connection instanceof HttpURLConnection) {
                    statusCode = ((HttpURLConnection) connection).getResponseCode();
                } else {
                    statusCode = 200; // Assume it's "OK"
                }
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

    /**
     * Call {@code close()} on the underlying HTTP Client, if there is one.
     */
    public void close() {
        if (httpclient != null) {
            try {
                httpclient.close();
            } catch (IOException e) {
                // nop
            }
        }
    }

    private String getHeader(HttpResponse resp, String name, String def) {
        Header[] headers = resp.getHeaders(name);

        if (headers == null) {
            return def;
        }

        if (headers.length == 0) {
            // This should never happen
            return def;
        } else {
            return headers[0].getValue();
        }
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
