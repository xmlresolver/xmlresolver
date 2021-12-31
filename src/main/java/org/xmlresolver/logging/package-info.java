/** Resolver logging.
 *
 * <p>The resolver logs configuration changes, resolution attempts, caching, and
 * a variety of other operations. Each of these log messages is sent to the
 * {@link org.xmlresolver.logging.ResolverLogger} in the configuration.</p>
 *
 * <p>The {@link org.xmlresolver.logging.DefaultLogger} sends messages to <code>System.err</code>. The
 * {@link org.xmlresolver.logging.SystemLogger} uses a logging backend. By default, it configures itself
 * to use any one of the logging backends supported by {@link org.slf4j.LoggerFactory}.
 * Alternatively, it may be configured directly with a
 * {@link java.util.logging.Logger}.</p>
 *
 * <p>To switch loggers, set the {@link org.xmlresolver.ResolverFeature#RESOLVER_LOGGER_CLASS}.</p>
 *
 */

 package org.xmlresolver.logging;
