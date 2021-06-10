package org.xmlresolver;

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

public class ResolvedResourceImpl extends ResolvedResource {
    private String name = null;
    private final String requestURI;
    private final URI resolvedURI;
    private final URI localURI;
    private final InputStream inputStream;
    private final String contentType;

    public ResolvedResourceImpl(String req, URI res, URI local, InputStream stream, String ctype) {
        requestURI = req;
        resolvedURI = res;
        localURI = local;
        inputStream = stream;
        contentType = ctype;
    }

    public ResolvedResourceImpl(XMLResolverConfiguration config, URI req, URI res) throws IOException, URISyntaxException {
        requestURI = req.toString();

        boolean mask = config.getFeature(ResolverFeature.MASK_JAR_URIS);
        URI showResolvedURI = res;
        if (mask && ("jar".equals(showResolvedURI.getScheme()) || "classpath".equals(showResolvedURI.getScheme()))) {
            showResolvedURI = req;
        }

        if ("data".equals(res.getScheme())) {
            // This is a little bit crude; see RFC 2397
            resolvedURI = showResolvedURI;
            localURI = res;
            // Can't use URI accessors because they percent decode the string incorrectly.
            String path = res.toString().substring(5);
            int pos = path.indexOf(",");
            if (pos >= 0) {
                String mediatype = path.substring(0, pos);
                String data = path.substring(pos + 1);
                if (mediatype.endsWith(";base64")) {
                    // Base64 decode it
                    inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
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
                    inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
                    contentType = "".equals(mediatype) ? null : mediatype;
                }
                return;
            } else {
                throw new URISyntaxException(res.toString(), "Comma separator missing");
            }
        }

        if ("classpath".equals(res.getScheme())) {
            String path = res.getSchemeSpecificPart();
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
                throw new IOException("Not found: " + res.toString());
            } else {
                resolvedURI = showResolvedURI;
                localURI = res;
                inputStream = rsrc.openStream();
                contentType = null;
                return;
            }
        }

        resolvedURI = showResolvedURI;
        localURI = res;
        URLConnection conn = res.toURL().openConnection();
        inputStream = conn.getInputStream();
        contentType = conn.getContentType();
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRequestURI() {
        return requestURI;
    }

    @Override
    public URI getResolvedURI() {
        return resolvedURI;
    }

    @Override
    public URI getLocalURI() {
        return localURI;
    }

    @Override
    public InputStream getStream() {
        return inputStream;
    }

    @Override
    public String getContentType() {
        return contentType;
    }
}
