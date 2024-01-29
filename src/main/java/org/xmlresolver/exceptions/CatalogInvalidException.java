package org.xmlresolver.exceptions;

/**
 * An invalid catalog exception.
 *
 * <p>This exception is raised when a catalog is invalid: when it doesn't satisfy the
 * constraints of the grammar or is not well-formed.</p>
 */
public class CatalogInvalidException extends CatalogUnavailableException {
    /** Construct an exception with a message about what went wrong.
     *
     * @param message A description of the underlying cause.
     */
    public CatalogInvalidException(String message) {
        super(message);
    }
}
