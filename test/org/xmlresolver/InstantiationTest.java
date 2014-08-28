/*
 * InstantiationTest.java
 *
 * Created on January 5, 2007, 1:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import junit.framework.TestCase;
import org.junit.Test;

import javax.xml.transform.URIResolver;

/**
 *
 * @author ndw
 */
public class InstantiationTest extends TestCase {
    @Test
    public void testInstantiate() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String className = "org.xmlresolver.Resolver";
        Class rClass = Class.forName(className);
        Object resolver = (URIResolver) rClass.newInstance();
        assertNotNull(resolver);
    }
}