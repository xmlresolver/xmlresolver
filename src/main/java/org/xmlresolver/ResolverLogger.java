package org.xmlresolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Formatter;
import java.util.HashMap;

/** This class centralizes the logging functionality so that it con be controlled a little more
 *  dynamically.
 *
 *  <p>In principle, I think this should be possible with the various logging framework
 *  mechanisms, but I've found them to be fiddly to setup. Gradle, in particular, seems to
 *  have very coarse distinctions.</p>
 *
 *  <p>Historically, I tried to use "trace" level logging for the really gory details, but that
 *  doesn't seem widely supported. This class mostly uses .debug and .info with other
 *  mechanisms to select which messages to print.</p>
 *
 *  <p>One argument against this approach is that it will require the evaluation of the logged
 *  messages even when the logging will eventually be ignored. Yes, that's true. But on the
 *  other hand, the resolver is probably going to do I/O, so it's not like concatenating
 *  a couple of strings is going to be the bottleneck!</p>
 */

public class ResolverLogger {
    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";
    public static final String TRACE = "trace";
    public static final String ERROR = "error";
    public static final String CACHE = "cache";
    public static final String CONFIG = "config";
    public static final String WARNING = "warning";

    private static final int DEBUG = 1;
    private static final int INFO = 2;
    private static final int WARN = 3;

    private final Logger logger;
    private final HashMap<String, Integer> categories = new HashMap<>();

    public ResolverLogger(Class<?> klass) {
        this.logger = LoggerFactory.getLogger(klass);
        String property = System.getProperty("xml.catalog.logging");
        if (property != null) {
            for (String prop : property.split(",\\s*")) {
                int pos = prop.indexOf(":");
                if (pos > 0) {
                    String cat = prop.substring(0, pos).trim();
                    String level = prop.substring(pos+1).trim();
                    setCategory(cat, level);
                }
            }
        }

    }

    public String getCategory(String cat) {
        if (categories.containsKey(cat)) {
            if (INFO == categories.get(cat)) {
                return "info";
            } else if (WARN == categories.get(cat)) {
                return "warn";
            }
        }
        return "debug";
    }

    public void setCategory(String cat, String level) {
        if ("info".equals(level)) {
            categories.put(cat, INFO);
        } else if ("warn".equals(level)) {
            categories.put(cat, WARN);
        } else {
            categories.put(cat, DEBUG);
            if (!"debug".equals(level)) {
                logger.info("Incorrect logging level specified: " + level + " treated as 'debug'");
            }
        }
    }

    public void log(String cat, String message, Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(cat);
        sb.append(": ");
        Formatter formatter = new Formatter(sb);

        if (params.length == 0) {
            formatter.format("%s", message);
        } else {
            formatter.format(message, params);
        }

        Integer deflevel = categories.getOrDefault("*", DEBUG);
        Integer level = categories.getOrDefault(cat, deflevel);

        switch (level) {
            case WARN:
                logger.warn(sb.toString());
                break;
            case INFO:
                logger.info(sb.toString());
                break;
            default:
                logger.debug(sb.toString());
        }
    }
}
