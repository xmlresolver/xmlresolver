// FileURI.java - Construct a file: scheme URL

package org.xmlresolver.helpers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * Static method for dealing with file: URLs.
 *
 * <p>This class defines a static method that can be used to construct
 * an appropriate file: URI from parts. It's defined here so that it
 * can be reused throught the resolver.</p>
 *
 */
public abstract class FileURI {
    protected static Logger logger = Logger.getLogger("org.xmlresolver");

    protected FileURI() { }

    /**
     * Construct a <code>file:</code> URI for a path name.
     *
     * <p>URIs in the <code>file:</code> scheme can be constructed for paths on
     * the local file system. Several possibilities need to be considered:
     * </p>
     *
     * <ul>
     * <li>If the path does not begin with a slash, then it is assumed
     * to reside in the users current working directory
     * (System.getProperty("user.dir")).</li>
     * <li>On Windows machines, the current working directory uses
     * backslashes (\\, instead of /).</li>
     * <li>If the current working directory is "/", don't add an extra
     * slash before the base name.</li>
     * </ul>
     *
     * <p>This method is declared static so that other classes
     * can use it directly.</p>
     *
     * @param pathname The path name component for which to construct a URI.
     *
     * @return The appropriate <code>file:</code> URI or <code>null</code> if no such URI exists.
     */
  public static URI makeURI(String pathname) {
      String userdir = System.getProperty("user.dir");
      userdir = userdir.replace('\\', '/');

      String sep = "/";
      if (userdir.endsWith("/")) {
          sep = "";
      }

      try {
          if (pathname.startsWith("/")) {
              return new URI("file://" + pathname);
          }

          URI file = null;

          if (userdir.startsWith("/")) {
              file = new URI("file://" + userdir + sep + pathname);
          } else {
              file = new URI("file:///" + userdir + sep + pathname);
          }

          return file;
      } catch (URISyntaxException use) {
          logger.warning("Invalid URI syntax in base URI: " + pathname + " (in " + userdir + " with \"" + sep + "\")");
          return null;
      }

  }
}
