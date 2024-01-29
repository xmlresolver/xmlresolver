package org.xmlresolver.exceptions;

/**
 * An unavailable catalog exception.
 *
 * <p>This exception is raised when a catalog file raises an IO exception of
 * some kind, or if it's name is so badly formed that it can't be convered to a URI.
 * It is not raised for simple "file not found" IO errors.</p>
 */
public class CatalogUnavailableException extends RuntimeException {
    /** Construct an exception with a message about what went wrong.
     *
     * @param cause The underlying exception.
     */
    public CatalogUnavailableException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    /** Construct an exception with a message about what went wrong.
     *
     * @param message A description of the underlying cause.
     */
    public CatalogUnavailableException(String message) {
        super(message);
    }
}
