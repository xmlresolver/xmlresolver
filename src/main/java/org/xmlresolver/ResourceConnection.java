package org.xmlresolver;

import org.xmlresolver.logging.AbstractLogger;
import org.xmlresolver.logging.ResolverLogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;

public class ResourceConnection {
    private InputStream stream = null;
    private URI uri = null;
    private URI redirect = null;
    private int statusCode = -1;
    private String contentType = null;
    private String etag = null;
    private Long lastModified = -1L;
    private Long date = -1L;

    // The headOnly parameter is a bit of a hack, but it's convenient to reuse the
    // ResourceConnection logic in multiple places. To check properties, I only
    // want to make a HEAD request, so ...

    public ResourceConnection(ResolverConfiguration config, String resolved) {
        this(config, resolved, false);
    }

    public ResourceConnection(ResolverConfiguration config, String resolved, boolean headOnly) {
        try {
            HashSet<URI> visited = new HashSet<URI>();
            uri = org.xmlresolver.utils.URIUtils.newURI(resolved);
            URI connURI = uri;
            URLConnection connection = null;

            boolean tryAgain = true;
            while (tryAgain) {
                visited.add(connURI);

                URL url = connURI.toURL();
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
                        connURI = URI.create(location);
                        if (visited.contains(connURI)) {
                            throw new IllegalArgumentException("Redirect URI already visited: " + connURI);
                        }
                    }
                } else {
                    tryAgain = false;
                    connection.connect();
                    statusCode = 200; // Assume it's "OK" if the connect() succeeded
                }
            }

            if (headOnly) {
                if (connection.getInputStream() != null) {
                    connection.getInputStream().close();
                }
            } else {
                stream = connection.getInputStream();
            }

            contentType = connection.getContentType();
            etag = connection.getHeaderField("ETag");
            lastModified = connection.getLastModified();
            date = connection.getDate();

            redirect = connection.getURL().toURI();
            if (uri.equals(redirect)) {
                redirect = null;
            }
        } catch (URISyntaxException | IOException | IllegalArgumentException use) {
            ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
            logger.log(AbstractLogger.WARNING, "Failed to %s: %s: %s", headOnly ? "HEAD" : "GET", resolved, use.getMessage());
        }
    }

    public InputStream getStream() {
        return stream;
    }

    public String getContentType() {
        return contentType;
    }

    public String getEtag() {
        return etag;
    }

    public URI getUri() {
        return uri;
    }
    
    public URI getRedirect() {
        return redirect;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public long getLastModified() {
        return lastModified;
    }

    public long getDate() {
        return date;
    }

    public void close() {
        // nop now that httpClient libraries have been removed
    }
}
