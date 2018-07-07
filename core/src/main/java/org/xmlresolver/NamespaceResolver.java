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
 * Interface for resolving namespace URIs.
 */
public interface NamespaceResolver {
    /** Resolve a namespace URI into a Source.
     *
     * <p>This method resolves a namespace URI, returning a resource that is associated with
     * the namespace name and has the specified nature and purpose.</p>
     *
     * <p>If no resource with a matching nature and purpose is found, a Source is created
     * for the namespace URI.</p>
     *
     * @param uri The namespace URI.
     * @param nature The RDDL nature of the resource.
     * @param purpose The RDDL purpose of the resource.
     * @return A Source.
     * @throws TransformerException If an error occurs
     */
    public Source resolveNamespace(String uri, String nature, String purpose) throws TransformerException;
}
