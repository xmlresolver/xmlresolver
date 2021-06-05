/*
 * InstantiationTest.java
 *
 * Created on January 5, 2007, 1:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 *
 * @author ndw
 */
public class InstantiationTest {
    @Test
    public void testInstantiate() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String className = "org.xmlresolver.Resolver";
        Class rClass = Class.forName(className);
        Object resolver = rClass.newInstance();
        assertNotNull(resolver);
    }
}