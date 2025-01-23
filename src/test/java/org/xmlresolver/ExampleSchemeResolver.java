package org.xmlresolver;

import org.xmlresolver.spi.SchemeResolver;
import org.xmlresolver.spi.SchemeResolverManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class ExampleSchemeResolver implements SchemeResolverManager {
    @Override
    public List<String> getKnownSchemes() {
        return List.of("test-scheme");
    }

    @Override
    public SchemeResolver getSchemeResolver(String scheme) {
        return new TestSchemeResolver();
    }

    public static class TestSchemeResolver implements SchemeResolver {
        @Override
        public ResourceResponse getResource(ResourceRequest request, URI uri) throws IOException {
            String path = uri.toString().substring(12);
            path = path.replaceAll("^/+", "");
            if (!path.startsWith("path/to/")) {
                return null;
            }
            path = System.getProperty("user.dir") + "/src/test/resources/" + path.substring(8);

            ResourceResponse response = new CustomResponse(request, uri, path);
            return response;
        }
    }

    public static class CustomResponse implements ResourceResponse {
        private final ResourceRequest request;
        private final URI uri;
        private final String path;

        public CustomResponse(ResourceRequest request, URI uri, String path) {
            this.request = request;
            this.uri = uri;
            this.path = path;
        }

        @Override
        public ResourceRequest getRequest() {
            return request;
        }

        @Override
        public ResourceConnection getConnection() {
            try {
                ResourceConnection conn = new ResourceConnection(uri);
                FileInputStream fis = new FileInputStream(path);
                conn.setStream(fis);
                return conn;
            } catch (IOException ex) {
                throw new RuntimeException("Failed to read " + path, ex);
            }
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public InputStream getInputStream() {
            try {
                FileInputStream fis = new FileInputStream(path);
                return fis;
            } catch (IOException ex) {
                throw new RuntimeException("Failed to read " + path, ex);
            }
        }

        @Override
        public boolean isRejected() {
            return false;
        }

        @Override
        public boolean isResolved() {
            return true;
        }

        @Override
        public URI getURI() {
            return uri;
        }

        @Override
        public URI getResolvedURI() {
            return URI.create("file://" + path);
        }

        @Override
        public URI getUnmaskedURI() {
            return getResolvedURI();
        }

        @Override
        public String getEncoding() {
            return null;
        }

        @Override
        public Map<String, List<String>> getHeaders() {
            return Map.of();
        }

        @Override
        public String getHeader(String name) {
            return null;
        }

        @Override
        public int getStatusCode() {
            return 200;
        }
    }
}
