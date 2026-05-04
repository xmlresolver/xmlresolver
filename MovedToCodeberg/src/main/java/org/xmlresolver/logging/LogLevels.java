package org.xmlresolver.logging;

import java.util.HashMap;
import java.util.Map;

/**
 * Supports the log level abstraction.
 * <p>There are six levels, "all" to print all messages, "debug" to print debug and higher level
 * messages, ..., "error" to print error messages, and "none" to print no messages at all.</p>
 * <p>In earlier releases, there was a much more complex system for managing messages. That's
 * all been simplified. Most messages are now either warnings or errors, or debug messages.</p>
 */
public class LogLevels {
    /** All messages. */
    public static final int ALL = 0;
    /** Debug and higher messages. */
    public static final int DEBUG = 1;
    /** Info and higher messages. */
    public static final int INFO = 2;
    /** Warn and higher messages. */
    public static final int WARN = 3;
    /** Error and higher messages. */
    public static final int ERROR = 4;
    /** No messages. */
    public static final int NONE = 5;
    private static final Object lock = new Object();

    private LogLevels() {
    }

    private static Map<Integer,String> numberToName = null;
    private static Map<String,Integer> nameToNumber = null;

    /**
     * Return a valid level number.
     * @param level the level
     * @return the level, if it's &gt;= ALL and &lt;= NONE, otherwise WARN
     */
    public static int validNumber(int level) {
        if (level >= ALL && level <= NONE) {
            return level;
        }
        return WARN;
    }

    /**
     * Returns the level number associated with the name.
     * @param name The level name.
     * @return The number associated with the name, if it's a valid name, otherwise WARN
     */
    public static int levelNumber(String name) {
        synchronized (lock) {
            if (nameToNumber == null) {
                nameToNumber = new HashMap<>();
                nameToNumber.put("all", ALL);
                nameToNumber.put("debug", DEBUG);
                nameToNumber.put("info", INFO);
                nameToNumber.put("warn", WARN);
                nameToNumber.put("error", ERROR);
                nameToNumber.put("none", NONE);
            }
        }
        return nameToNumber.getOrDefault(name, WARN);
    }

    /**
     * Return a valid level name.
     * @param name the level name
     * @return the level name if it's valid, otherwise "warn"
     */
    public static String validName(String name) {
        return levelName(levelNumber(name));
    }

    /**
     * Returns the level name associated with the level number.
     * @param level The level number.
     * @return The name associated with the level, if it's a valid level, otherwise "warn"
     */
    public static String levelName(int level) {
        synchronized (lock) {
            if (numberToName == null) {
                numberToName = new HashMap<>();
                numberToName.put(ALL, "all");
                numberToName.put(DEBUG, "debug");
                numberToName.put(INFO, "info");
                numberToName.put(WARN, "warn");
                numberToName.put(ERROR, "error");
                numberToName.put(NONE, "none");
            }
        }
        return numberToName.getOrDefault(level, "warn");
    }
}
