package org.xmlresolver.sources;

import org.w3c.dom.ls.LSInput;
import org.xmlresolver.Resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

public class ResolverLSInput implements LSInput {
    public final URI resolvedURI;
    final Resource rsrc;
    final String publicId;
    final String systemId;

    public ResolverLSInput(Resource rsrc, String publicId, String systemId) {
        resolvedURI = rsrc.localUri();
        this.rsrc = rsrc;
        this.publicId = publicId;
        this.systemId = systemId;
    }

    public Reader getCharacterStream() {
        return new InputStreamReader(rsrc.body());
    }

    public void setCharacterStream(Reader reader) {
        throw new UnsupportedOperationException("Can't set character stream on resolver LSInput");
    }

    public InputStream getByteStream() {
        return rsrc.body();
    }

    public void setByteStream(InputStream inputStream) {
        throw new UnsupportedOperationException("Can't set byte stream on resolver LSInput");
    }

    public String getStringData() {
        // The data is in the stream
        return null;
    }

    public void setStringData(String string) {
        throw new UnsupportedOperationException("Can't set string data on resolver LSInput");
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String string) {
        throw new UnsupportedOperationException("Can't set system ID on resolver LSInput");
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String string) {
        throw new UnsupportedOperationException("Can't set public ID on resolver LSInput");
    }

    public String getBaseURI() {
        return rsrc.uri().toString();
    }

    public void setBaseURI(String string) {
        throw new UnsupportedOperationException("Can't set base URI on resolver LSInput");
    }

    public String getEncoding() {
        return null; // Unknown
    }

    public void setEncoding(String string) {
        throw new UnsupportedOperationException("Can't set encoding on resolver LSInput");
    }

    public boolean getCertifiedText() {
        return false;
    }

    public void setCertifiedText(boolean b) {
        throw new UnsupportedOperationException("Can't set certified text on resolver LSInput");
    }
}
