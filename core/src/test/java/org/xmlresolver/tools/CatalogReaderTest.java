package org.xmlresolver.tools;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xmlresolver.Catalog;
import org.xmlresolver.CatalogResult;
import org.xmlresolver.CatalogSource;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by ndw on 5/19/15.
 */
public class CatalogReaderTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testInputSource() throws Exception {
        String cwd = System.getProperty("user.dir");
        String jarstr = "jar:file://" + cwd + "/src/test/resources/docbook.jar";
        String catstr = jarstr + "!/catalog.xml";
        URL url = new URL(catstr);
        InputSource is = new InputSource(url.openStream());
        is.setSystemId(catstr);

        CatalogSource cat = new CatalogSource.InputSourceCatalogSource(is);
        Element doc = cat.parse();
        assertNotNull(doc);

        url = new URL(catstr);
        is = new InputSource(url.openStream());
        is.setSystemId(catstr);

        Catalog catalog = new Catalog();
        cat = new CatalogSource.InputSourceCatalogSource(is);
        catalog.addSource(cat);

        CatalogResult r = catalog.lookupURI("http://xmlcalabash.com/pipelines/docbook.xpl");
        assertEquals(jarstr + "!/docbook.xpl", r.uri());
    }
}
