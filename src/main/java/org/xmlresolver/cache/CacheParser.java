package org.xmlresolver.cache;

import org.xmlresolver.ResolverLogger;

import java.util.regex.Pattern;

/** Parse user-supplied strings into cache info values
 *
 */
public class CacheParser {
    protected static ResolverLogger logger = new ResolverLogger(CacheParser.class);

    private static final Pattern sizeK = Pattern.compile("^[0-9]+k$", Pattern.CASE_INSENSITIVE);
    private static final Pattern sizeM = Pattern.compile("^[0-9]+m$", Pattern.CASE_INSENSITIVE);
    private static final Pattern sizeG = Pattern.compile("^[0-9]+g$", Pattern.CASE_INSENSITIVE);
    private static final Pattern timeS = Pattern.compile("^[0-9]+s$", Pattern.CASE_INSENSITIVE);
    private static final Pattern timeM = sizeM; // same
    private static final Pattern timeH = Pattern.compile("^[0-9]+h$", Pattern.CASE_INSENSITIVE);
    private static final Pattern timeD = Pattern.compile("^[0-9]+d$", Pattern.CASE_INSENSITIVE);
    private static final Pattern timeW = Pattern.compile("^[0-9]+w$", Pattern.CASE_INSENSITIVE);

    public static long parseLong(String longStr, long defVal) {
        if (longStr == null) {
            return defVal;
        }

        try {
            return Long.parseLong(longStr);
        } catch (NumberFormatException nfe) {
            logger.log(ResolverLogger.ERROR, "Bad numeric value: %s", longStr);
            return defVal;
        }
    }

    public static long parseSizeLong(String longStr, long defVal) {
        if (longStr == null) {
            return defVal;
        }

        long units = 1;
        if (sizeK.matcher(longStr).matches()) {
            units = 1024;
            longStr = longStr.substring(0,longStr.length()-1);
        } else if (sizeM.matcher(longStr).matches()) {
            units = 1024*1000;
            longStr = longStr.substring(0,longStr.length()-1);
        } else if (sizeG.matcher(longStr).matches()) {
            units = 1024*1000*1000;
            longStr = longStr.substring(0,longStr.length()-1);
        }

        return parseLong(longStr, units, defVal);
    }

    public static long parseTimeLong(String longStr, long defVal) {
        if (longStr == null) {
            return defVal;
        }

        long units = 1;
        if (timeS.matcher(longStr).matches()) {
            longStr = longStr.substring(0,longStr.length()-1);
        } else if (timeM.matcher(longStr).matches()) {
            units = 60;
            longStr = longStr.substring(0,longStr.length()-1);
        } else if (timeH.matcher(longStr).matches()) {
            units = 3600;
            longStr = longStr.substring(0,longStr.length()-1);
        } else if (timeD.matcher(longStr).matches()) {
            units = 3600*24;
            longStr = longStr.substring(0,longStr.length()-1);
        } else if (timeW.matcher(longStr).matches()) {
            units = 3600*24*7;
            longStr = longStr.substring(0,longStr.length()-1);
        }

        return parseLong(longStr, units, defVal);
    }

    public static long parseLong(String longStr, long units, long defVal) {
        try {
            long val = Long.parseLong(longStr);
            return val * units;
        } catch (NumberFormatException nfe) {
            logger.log(ResolverLogger.ERROR, "Bad numeric value: %s", longStr);
            return defVal;
        }
    }
}
