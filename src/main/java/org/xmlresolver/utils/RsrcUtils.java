package org.xmlresolver.utils;

import java.util.List;
import java.util.Map;

public class RsrcUtils {
    public static String getHeader(String headerName, Map<String, List<String>> headers) {
        if (headerName == null || headers == null) {
            return null;
        }

        for (String name : headers.keySet()) {
            if (name.equalsIgnoreCase(headerName)) {
                return headers.get(name).get(0);
            }
        }

        return null;
    }
}
