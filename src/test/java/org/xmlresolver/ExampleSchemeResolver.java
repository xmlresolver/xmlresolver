package org.xmlresolver;

import org.xmlresolver.spi.SchemeResolver;
import org.xmlresolver.spi.SchemeResolverManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

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

            ResourceConnection conn = new ResourceConnection(uri);
            FileInputStream fis = new FileInputStream(path);
            conn.setStream(fis);

            ResourceResponse response = new ResourceResponse(request, uri);
            response.setConnection(conn);

            return response;
        }
    }
}
