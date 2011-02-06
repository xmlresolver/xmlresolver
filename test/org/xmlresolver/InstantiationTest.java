/*
 * InstantiationTest.java
 *
 * Created on January 5, 2007, 1:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import javax.xml.transform.URIResolver;
import junit.framework.*;

/**
 *
 * @author ndw
 */
public class InstantiationTest extends TestCase {
    public InstantiationTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testInstantiate() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String className = "org.xmlresolver.Resolver";
        Class rClass = Class.forName(className);
        Object resolver = (URIResolver) rClass.newInstance();
        assertNotNull(resolver);
    }
}