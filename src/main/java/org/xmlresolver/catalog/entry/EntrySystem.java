package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntrySystem extends EntryResource {
    public final String systemId;

    public EntrySystem(URI baseURI, String id, String systemId, String uri) {
        super(baseURI, id, uri);

        if (systemId.startsWith("classpath:/")) {
            // classpath:/path/to/thing is the same as classpath:path/to/thing
            // normalize without the leading slash.
            this.systemId = "classpath:" + systemId.substring(11);
        } else {
            this.systemId = systemId;
        }
    }

    @Override
    public Type getType() {
        return Type.SYSTEM;
    }
}
