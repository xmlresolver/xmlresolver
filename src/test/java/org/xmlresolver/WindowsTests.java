package org.xmlresolver;

import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlresolver.tools.ResolvingXMLReader;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.fail;

public class WindowsTests {
    @Test
    public void parseWithoutCatalog() {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.FIX_WINDOWS_SYSTEM_IDENTIFIERS, true);

        XMLResolver resolver = new XMLResolver(config);

        ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
        InputSource source = new InputSource("src/test/resources/windows.xml");
        try {
            reader.parse(source);
            fail();
        } catch (IOException | SAXException ex) {
            // expected an error
        }
    }

    @Test
    public void parseWithCatalog() {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.FIX_WINDOWS_SYSTEM_IDENTIFIERS, true);

        ArrayList<String> catalogs = new ArrayList<>();
        catalogs.add("src/test/resources/windows-catalog.xml");
        config.setFeature(ResolverFeature.CATALOG_FILES, catalogs);

        XMLResolver resolver = new XMLResolver(config);

        ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
        InputSource source = new InputSource("src/test/resources/windows.xml");
        try {
            reader.parse(source);
        } catch (IOException | SAXException ex) {
            fail();
        }


    }

    @Test
    public void parseWindowsFilename() {
        String cwd = System.getProperty("user.dir");

        if (!":".equals(cwd.substring(1,2))) {
            // Not a useful windows test
            return;
        }

        if (!cwd.endsWith("\\")) {
            cwd += "\\";
        }

        XMLResolverConfiguration config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.FIX_WINDOWS_SYSTEM_IDENTIFIERS, true);

        ArrayList<String> catalogs = new ArrayList<>();
        catalogs.add(cwd + "src\\test\\resources\\windows-catalog.xml");
        config.setFeature(ResolverFeature.CATALOG_FILES, catalogs);

        XMLResolver resolver = new XMLResolver(config);

        ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
        InputSource source = new InputSource("src/test/resources/windows.xml");
        try {
            reader.parse(source);
        } catch (IOException | SAXException ex) {
            fail();
        }


    }
}
