package org.xmlresolver.logging;

import java.util.Formatter;
import java.util.HashMap;

/** The abstract logger implements some of the core functionality needed regardless of
 * how the messages are processed.
 */
public abstract class AbstractLogger implements ResolverLogger {
    /**
     * Nop constructor for AbstractLogger.
     * <p>Shut up, Javadoc.</p>
     */
    protected AbstractLogger() {
        // nop
    }

    protected String format(String message, Object... params) {
        if (params.length == 0) {
            return message;
        }

        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        formatter.format(message, params);
        return sb.toString();
    }
}
