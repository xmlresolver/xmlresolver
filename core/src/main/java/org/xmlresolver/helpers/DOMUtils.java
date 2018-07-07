/*
 * DOMUtils.java
 *
 * Created on January 2, 2007, 12:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver.helpers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlresolver.Catalog;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

/** W3C DOM utility methods.
 *
 * @author ndw
 */
public class DOMUtils {
    
    /** Creates a new instance of DOMUtils */
    protected DOMUtils() {
    }

    /** Return the first element child of a node.
     *
     * <p>Like <code>getFirstChild</code> except that it ignores all node types
     * except element nodes.</p>
     *
     * @param parent The parent
     * @return The first child element
     */
    public static Element getFirstElement(Element parent) {
        Node child = parent.getFirstChild();
        while (child != null && child.getNodeType() != Node.ELEMENT_NODE) {
            child = child.getNextSibling();
        }
        return (Element) child;
    }
    
    /** Return the next element sibling of a node.
     *
     * <p>Like <code>getNextSibling</code> except that it ignores all node types
     * except element nodes.</p>
     *
     * @param current The current node
     * @return The next sibling
     */
    public static Element getNextElement(Element current) {
        Node child = current.getNextSibling();
        while (child != null && child.getNodeType() != Node.ELEMENT_NODE) {
            child = child.getNextSibling();
        }
        return (Element) child;
    }

    /** Performs xml:base calculations on a URI.
     *
     * <p>The initial <code>uriString</code> is made absolute with respect to any ancestor
     * base URIs, including those introduced by <code>xml:base</code> attriblutes. The base
     * URI of the document that contains the <code>node</code> is used as the absolute base URI
     * if no intervening absolute <code>xml:base</code> attributes are found.</p>
     *
     * @param node The node
     * @param uriString The URI to make absolute
     * @return The absolute URI
     */
    public static String makeAbsolute(Element node, String uriString) {
        Document doc = node.getOwnerDocument();
        return makeAbsolute(node, uriString, doc.getBaseURI());
    }
    
    /** Performs xml:base calculations on a URI.
     *
     * <p>The initial <code>uriString</code> is made absolute with respect to any ancestor
     * base URIs, including those introduced by <code>xml:base</code> attriblutes.</p>
     *
     * @param node The node
     * @param uriString The URI to make absolute
     * @param documentBaseURI The document base URI
     * @return The absolute URI
     */
    public static String makeAbsolute(Element node, String uriString, String documentBaseURI) {
        try {
            URI uri = new URI(uriString);
            if (uri.isAbsolute()) {
                return uri.toString();
            }

            Vector<Element> nodes = new Vector<Element>();
            nodes.add(node);

            Node parent = node.getParentNode();
            while (parent != null && parent.getNodeType() == Node.ELEMENT_NODE) {
                nodes.add(0, (Element) parent);
                parent = parent.getParentNode();
            }
            
            if (documentBaseURI == null) {
                // oh, well, let's hope for the best
                documentBaseURI = "file://" + uriString;
            }
            
            URI docuri = new URI(documentBaseURI);
            URI baseURI = docuri;

            for (Element xbnode : nodes) {
                if (xbnode.hasAttribute("xml:base")) {
                    baseURI = baseURI.resolve(new URI(xbnode.getAttribute("xml:base")));
                }
            }

            //System.out.println("baseURI: " + baseURI.toString());
            //System.out.println("uri: " + uri.toString());

            String path = baseURI.toASCIIString();
            int pos = path.indexOf("!");
            if (pos > 0 && (path.startsWith("jar:file:")
                        || path.startsWith("jar:http:") || path.startsWith("jar:https:"))) {
                // You can't resolve against jar: scheme URIs because they appear to be opaque.
                // I wonder if what follows is kosher...
                String subpath = path.substring(pos+1);
                String fakeURIstr = "http://example.com";
                if (subpath.startsWith("/")) {
                    fakeURIstr += subpath;
                } else {
                    fakeURIstr += "/" + subpath;
                }
                URI fakeURI = new URI(fakeURIstr);
                URI resURI = fakeURI.resolve(uri);
                fakeURIstr = path.substring(0,pos+1) + resURI.getPath();
                uri = new URI(fakeURIstr);
            } else {
                uri = baseURI.resolve(uri);
            }

            //System.out.println("resolved: " + uri.toString());

            String absuriString = uri.toString();
            if (absuriString.startsWith("file:/") && !absuriString.startsWith("file:///")) {
                absuriString = "file:///" + absuriString.substring(6);
            }
            return absuriString;
        } catch (URISyntaxException use) {
            // nevermind!
            return uriString;
        }
    }

    /** Returns true if and only if the node is an OASIS XML Catalog element named <code>localName</code>.
     *
     * @param node The node to test
     * @param localName The local name
     * @return True if the node has the specified local name in the catalog namespace
     */
    public static boolean catalogElement(Node node, String localName) {
        return (node.getNodeType() == Element.ELEMENT_NODE
                && localName.equals(node.getLocalName())
                && Catalog.NS_CATALOG.equals(node.getNamespaceURI()));
    }

    /** Returns the value of an attribute.
     *
     * <p>This method looks for the attribute by name. It is unwise to use this method except
     * for attributes in no namespace.</p>
     *
     * @param test The element on which to query the attribute value.
     * @param name The name of the attribute to query.
     *
     * @return The value of the attribute, or <code>null</code> if the attribute isn't present.
     */
    public static String attr(Element test, String name) {
        return test.hasAttribute(name) ? test.getAttribute(name) : null;
    }

    /** Returns the value of an attribute.
     *
     * @param test The element on which to query the attribute value.
     * @param uri The namespace name of the attribute to query.
     * @param localName The local name of the attribute to query.
     *
     * @return The value of the attribute, or <code>null</code> if the attribute isn't present.
     */
    public static String attr(Element test, String uri, String localName) {
        return test.hasAttributeNS(uri, localName) ? test.getAttributeNS(uri, localName) : null;
    }
    
}
