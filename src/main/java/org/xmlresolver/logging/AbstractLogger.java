package org.xmlresolver.logging;

import java.util.Formatter;
import java.util.HashMap;

/** The abstract logger implements some of the core functionality needed regardless of
 * how the messages are processed.
 */

public abstract class AbstractLogger implements ResolverLogger {
    /** Requests for resource resolution. */
    public static final String REQUEST = "request";
    /** Responses describing how a request was resolved. */
    public static final String RESPONSE = "response";
    /** Trace (or debuggin) messages. */
    public static final String TRACE = "trace";
    /** Error messages. */
    public static final String ERROR = "error";
    /** Messages related to how the cache is used. */
    public static final String CACHE = "cache";
    /** Messages related to resolver configuration. */
    public static final String CONFIG = "config";
    /** Warning messages. */
    public static final String WARNING = "warning";

    protected static final int DEBUG = 1;
    protected static final int INFO = 2;
    protected static final int WARN = 3;
    protected static final int NONE = 4;

    protected final HashMap<String, Integer> categories = new HashMap<>();
    protected String catalogLogging = null;

    /** Initializes properties of the abstract class. */
    public AbstractLogger() {
        // nop
    }

    /**
     * Returns the log level, "debug", "info", worn", or "none" associated with a category.
     * @param cat The category.
     * @return The level. If no level has been configured for that category, the default is "debug".
     */
    public String getCategory(String cat) {
        if (categories.containsKey(cat)) {
            switch (categories.get(cat)) {
                case INFO:
                    return "info";
                case WARN:
                    return "warn";
                case NONE:
                    return "none";
                case DEBUG:
                    return "debug";
                default:
                    // this "can't" happen
                    break;
            }
        }
        return "debug";
    }

    /**
     * Set the log level for a category. After this call, messages in the specified category
     * will be logged at the specified level. Valid levels are "debug", "info", and "warn".
     * An invalid level is treated as "debug".
     * @param cat The category.
     * @param level The level.
     */
    public void setCategory(String cat, String level) {
        switch (level) {
            case "info":
                categories.put(cat, INFO);
                break;
            case "warn":
                categories.put(cat, WARN);
                break;
            case "debug":
                categories.put(cat, DEBUG);
                break;
            case "none":
                categories.put(cat, NONE);
                break;
            default:
                categories.put(cat, DEBUG);
                info("Incorrect logging level specified: " + level + " treated as 'debug'");
                break;
        }
    }

    protected String logMessage(String cat, String message, Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(cat);
        sb.append(": ");
        Formatter formatter = new Formatter(sb);

        if (params.length == 0) {
            formatter.format("%s", message);
        } else {
            formatter.format(message, params);
        }

        return sb.toString();
    }

    /**
     * Log a message.
     *
     * <p>The category is used to determine what level of logging is
     * expected for this message. The message is then formatted with its parameters
     * and logged.</p>
     *
     * <p>The message and its parameters are formatted with {@link Formatter}.</p>
     *
     * @param cat The category.
     * @param message The message.
     * @param params The message parameters.
     */
    public void log(String cat, String message, Object... params) {
        updateLoggingCategories();
        Integer deflevel = categories.getOrDefault("*", DEBUG);
        Integer level = categories.getOrDefault(cat, deflevel);

        switch (level) {
            case NONE:
                break;
            case WARN:
                warn(logMessage(cat, message, params));
                break;
            case INFO:
                info(logMessage(cat, message, params));
                break;
            default:
                debug(logMessage(cat, message, params));
        }
    }

    private void updateLoggingCategories() {
        String property = System.getProperty("xml.catalog.logging");
        if (property == null && catalogLogging == null) {
            return;
        }

        if (property == null) {
            categories.clear();
            return;
        }

        if (property.equals(catalogLogging)) {
            return;
        }

        catalogLogging = property;
        categories.clear();
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
