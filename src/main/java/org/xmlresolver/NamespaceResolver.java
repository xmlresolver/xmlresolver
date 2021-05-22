/*
 * NamespaceResolver.java
 *
 * Created on January 2, 2007, 10:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

/**
 * Interface for resolving namespace URIs. This interface supports the RDDL notion of
 * a nature and purpose for a namespace.
 *
 * <p>The <code>NamespaceResolver</code> interface is an extension of the
 * <code>URIResolver</code> interface from the JAXP Transformer package.</p>
 *
 * <p>Use this <code>resolveNamespace</code> method in your applications
 * (instead of the <code>URIResolver</code>)
 * where you are attempting to find information about a namespace URI.</p>
 *
 * <p>The intent of this method is that it returns the
 * <code>Source</code> associated with the namespace <code>uri</code>
 * that has the specified <a href="http://www.rddl.org/">RDDL</a> nature
 * and purpose.</p>
 *
 * <p>The XML Resolver implements catalog extension attributes that allow
 * a user to specify the nature and purpose of a URI. It also parses RDDL
 * (1.0) documents.
 * </p>
 */
public interface NamespaceResolver {
    /** Resolve a namespace URI into a Source.
     *
     * <p>This method resolves a namespace URI, returning a resource that is associated with
     * the namespace name and has the specified nature and purpose.</p>
     *
     * <p>If no resource with a matching nature and purpose is found, a Source must be created
     * for the namespace URI.</p>
     *
     * @param uri The namespace URI.
     * @param nature The RDDL nature of the resource.
     * @param purpose The RDDL purpose of the resource.
     * @return A Source object, or null if the href cannot be resolved, and the processor should try to resolve the URI itself.
     * @throws TransformerException If an error occurs
     */
    public Source resolveNamespace(String uri, String nature, String purpose) throws TransformerException;
}
