/** Resolver logging.
 *
 * <p>The resolver logs configuration changes, resolution attempts, caching, and
 * a variety of other operations. Each of these log messages is sent to the
 * {@link ResolverLogger} in the configuration.</p>
 *
 * <p>The {@link DefaultLogger} sends messages to <code>System.err</code>. The
 * {@link SystemLogger} uses a logging backend. By default, it configures itself
 * to use any one of the logging backends supported by {@link org.slf4j.LoggerFactory}.
 * Alternatively, it may be configured directly with a
 * {@link java.util.logging.Logger}.</p>
 *
 */

 package org.xmlresolver.logging;
