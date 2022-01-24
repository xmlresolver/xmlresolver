/*
 * URIUtils.java
 *
 * Created on December 27, 2006, 4:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/** URI utility methods.
 *
 * @author ndw
 */
public abstract class URIUtils {
    private static Boolean isWindows = null;

    private static boolean checkWindows() {
        if (isWindows == null) {
            String os = System.getProperty("os.name", "unknown").toLowerCase();
            isWindows = os.contains("win");
        }
        return isWindows;
    }

    private static String windowsPathURI(String uri) {
        if (!checkWindows()) {
            return uri;
        }
        String fixSlashes = uri.replaceAll("\\\\", "/");
        if (fixSlashes.length() >=2 && fixSlashes.charAt(1) == ':') {
            return "file:///" + fixSlashes;
        }
        return fixSlashes;
    }

    /**
     * Creates a URI for the users current working directory.
     *
     * In order that this method should neither raise an exception nor return <code>null</code>,
     * if the <code>user.dir</code> system property cannot be converted into a URI, the
     * file URI "<code>file:///</code>" is returned.
     *
     * @return a file: URI for the current working directory
     */
    public static URI cwd() {
        String dir = windowsPathURI(System.getProperty("user.dir"));
        if (!dir.endsWith("/")) {
            dir += "/";
        }
        try {
            return URIUtils.newURI(dir);
        } catch (URISyntaxException ex) {
            return URI.create("file:///"); // ¯\_(ツ)_/¯
        }
    }

    /**
     * Create a new URI, attempting to deal with the vagaries of file: URIs.
     *
     * Given something that looks like a file: URI or a path, return
     * a file: URI with a consistent number of leading "/". characters.
     * Any number of leading slashes are interpreted the same way.
     * (This is slightly at odds with specs, as file:///////path should
     * probably be an error. But this method "fixes it" to file:///path.)
     *
     * Strings that don't begin file: or /, are constructed with new URI() without
     * preprocessing. This will construct URIs for other schemes if the href
     * parameter is valid.
     *
     * This method will encode query strings, but that's ok for this application.
     *
     * @param href The string to be interpreted as a URI.
     * @return A URI constructed from the href parameter.
     * @throws URISyntaxException if the string cannot be converted into a URI.
     */
    public static URI newURI(String href) throws URISyntaxException {
        // This is a tiny bit complicated because I'm trying to avoid creating additional
        // string objects if I don't have to. I wonder if the effort is worth it.
        // Since I haven't measured it, "no".
        if (href.startsWith("file:")) {
            int pos = 5;
            while (pos <= href.length() && href.charAt(pos) == '/') {
                pos++;
            }
            if (pos > 5) {
                pos--;
            } else {
                pos = 0;
                href = "/" + href.substring(5);
            }
            if (href.contains("#")) {
                int hashpos = href.indexOf('#');
                String base = href.substring(pos, hashpos);
                String fragid = href.substring(hashpos + 1);
                return new URI("file", "", base, fragid);
            } else {
                return new URI("file", "", href.substring(pos), null);
            }
        } else if (href.startsWith("/")) {
            if (href.contains("#")) {
                int hashpos = href.indexOf('#');
                String base = href.substring(0, hashpos);
                String fragid = href.substring(hashpos + 1);
                return new URI("file", "", base, fragid);
            } else {
                return new URI("file", "", href, null);
            }
        } else {
            return new URI(href);
        }
    }

    /**
     * Resolve a URI against a base URI.
     *
     * <p>What's special here is that we take special care to attempt to resolve <code>jar:</code>
     * and <code>classpath:</code> URIs. The {@link URI} class doesn't handle those, but if
     * we're going to support them in catalogs, we need to do better.</p>
     *
     * @param baseURI The base URI.
     * @param uri The possibily relative URI to resolve against the base URI.
     * @return The resolved URI.
     * @throws IllegalArgumentException if the uri cannot be converted to a URI.
     */
    public static URI resolve(URI baseURI, String uri) {
        if (baseURI == null) {
            try {
                return new URI(uri);
            } catch (URISyntaxException use) {
                throw new IllegalArgumentException(use.getMessage(), use);
            }
        }

        // Escape URI characters...
        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < uri.length(); idx++) {
            char ch = uri.charAt(idx);
            if (ch <= ' ' || ch >= '~') {
                sb.append(String.format("%%%02X", (int) ch));
            } else {
                sb.append(ch);
            }
        }
        uri = windowsPathURI(sb.toString());

        URI resolved = URI.create(uri);
        if (resolved.isAbsolute()) {
            return resolved;
        }

        if ("jar".equals(baseURI.getScheme())) {
            String url = baseURI.toString();
            int pos = url.lastIndexOf("!");
            if (pos > 0) {
                String prefix = url.substring(0, pos+1);
                String path = url.substring(pos+1);
                URI fakeURI = null;
                if (path.startsWith("/")) {
                    fakeURI = URI.create("file://" + path);
                } else {
                    fakeURI = URI.create("file:///" + path);
                }
                fakeURI = fakeURI.resolve(uri);
                if (fakeURI.getPath().startsWith("/../")) {
                    throw new IllegalArgumentException("Attempt to navigate above root: " + prefix + fakeURI.getPath());
                }
                return URI.create(prefix + fakeURI.getPath());
            } else {
                return baseURI.resolve(uri);
            }
        } else if ("classpath".equals(baseURI.getScheme())) {
            String path = baseURI.toString().substring(10);
            URI fakeURI = null;
            if (path.startsWith("/")) {
                fakeURI = URI.create("file://" + path);
            } else {
                fakeURI = URI.create("file:///" + path);
            }
            fakeURI = fakeURI.resolve(uri);
            String cpath = fakeURI.getPath().substring(1);
            if (cpath.startsWith("../")) {
                throw new IllegalArgumentException("Attempt to navigate above root: classpath:" + cpath);
            }
            return URI.create("classpath:" + cpath);
        } else {
            return baseURI.resolve(uri);
        }
    }

    /**
     * Perform character normalization on a URI reference.
     *
     * @param uriref The URI reference.
     * @return The normalized URI reference.
     */
    public static String normalizeURI(String uriref) {
        if (uriref == null) {
            return null;
        }

        StringBuilder newRef = new StringBuilder();
        byte[] bytes = uriref.getBytes(StandardCharsets.UTF_8);

        for (byte aByte : bytes) {
            int ch = aByte & 0xFF;
            if ((ch <= 0x20)        // ctrl
                    || (ch > 0x7F)  // high ascii
                    || (ch == 0x22) // "
                    || (ch == 0x3C) // <
                    || (ch == 0x3E) // >
                    || (ch == 0x5C) // \
                    || (ch == 0x5E) // ^
                    || (ch == 0x60) // `
                    || (ch == 0x7B) // {
                    || (ch == 0x7C) // |
                    || (ch == 0x7D) // }
                    || (ch == 0x7F)) {
                newRef.append(encodedByte(ch));
            } else {
                newRef.append((char) aByte);
            }
        }

        return newRef.toString();
    }

    /**
     * Perform %-encoding on a single byte.
     *
     * @param b The 8-bit integer that represents th byte. (Bytes are signed
     * but encoding needs to look at the bytes unsigned.)
     * @return The %-encoded string for the byte in question.
     */
    private static String encodedByte(int b) {
        String hex = Integer.toHexString(b).toUpperCase();
        if (hex.length() < 2) {
            return "%0" + hex;
        } else {
            return "%" + hex;
        }
    }
}
