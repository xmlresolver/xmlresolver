package org.xmlresolver;

import org.junit.Assert;
import org.junit.Test;

public class ConfigTest {

    private String renameProperty(String name) {
        String env = name.replaceAll("\\.", "_")
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toUpperCase();
        return env;
    }

    @Test
    public void testDot() {
        Assert.assertEquals("XML_RESOLVER", renameProperty("xml.resolver"));
    }

    @Test
    public void testDots() {
        Assert.assertEquals("XML_RESOLVER_TEST", renameProperty("xml.resolver.test"));
    }

    @Test
    public void testMixedCaseSingle() {
        Assert.assertEquals("XML_RESOLVER_TEST", renameProperty("xml.resolverTest"));
    }

    @Test
    public void testMixedCaseMultiple() {
        Assert.assertEquals("XML_CATALOG_PREFER_PROPERTY_FILE", renameProperty("xml.catalog.preferPropertyFile"));
    }
}
