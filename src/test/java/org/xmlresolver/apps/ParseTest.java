package org.xmlresolver.apps;

import org.junit.Test;

public class ParseTest {
    @Test
    public void parseFail() {
        Parse parser = new Parse();
        String[] args = {"-s", "src/test/resources/doc.xml"};
        assert(!parser.run(args));
    }

    @Test
    public void parsePass() {
        Parse parser = new Parse();
        String[] args = { "-c", "src/test/resources/catalog.xml", "-s", "src/test/resources/doc.xml" };
        assert(parser.run(args));
    }
}
