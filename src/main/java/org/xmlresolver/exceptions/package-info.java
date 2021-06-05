/** Catalog resolver exceptions.
 *
 * <p>The {@link org.xmlresolver.loaders.ValidatingXmlLoader} raises exceptions
 * if it finds unreadable or invalid catalog files.</p>
 *
 * <p>Experience suggests that it's not too uncommon to have a list of catalog files
 * that contains paths that simply don't exist. Causing a validation failure in those cases seemed
 * too stringent. If attempting to load a catalog raises a <code>FileNotFoundException</code>,
 * that isn't a validation error.</p>
 *
 */

 package org.xmlresolver.exceptions;
