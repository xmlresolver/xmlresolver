package org.xmlresolver;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class CharsetTest {
    private void checkCharset(String ctype, String expected) {
        Pattern charset = Pattern.compile("^.*;\\s*charset=([^;]+).*$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = charset.matcher(ctype);
        if (matcher.matches()) {
            assertEquals(expected, matcher.group(1));
        } else {
            assertNull(expected);
        }
    }

    @Test
    public void checkCharset_utf8() {
        checkCharset("text/html; charset=utf-8", "utf-8");
    }

    @Test
    public void checkCHARSET_utf8() {
        checkCharset("text/html; CHARSET=utf-8", "utf-8");
    }

    @Test
    public void checkCharset_utf8_nosp() {
        checkCharset("text/html;charset=utf-8", "utf-8");
    }

    @Test
    public void checkCharset_utf8_opt() {
        checkCharset("text/html;    charset=utf-8; other=3", "utf-8");
    }

    @Test
    public void checkCharset_none() {
        checkCharset("text/html", null);
    }
}
