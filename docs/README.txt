XMLResolver: An enhanced XML resolver with XML Catalog support

The xmlresolver project provides an advanced implementation of the SAX
EntityResolver, the Transformer URIResolver, and a new
NamespaceResolver. The implementation uses the OASIS XML Catalogs V1.1
Standard to provide a mapping from public identifiers to local
resources.

The goal of this project is to produce a clean, reasonably simple API
and a robust, thread-safe implementation.

Releases of the xmlresolver can be found on Maven Central or on GitHub
at https://github.com/xmlresolver/xmlresolver/releases

This is org.xmlresolver:xmlresolver:@@VERSION@@ 

In addition to enhanced support for RDDL-based namespace resolution,
the implementation supports automatic local caching of resources. This
provides the advantages of the catalog specification without requiring
users to manage the mapping by hand.

Applications can use the resolver directly or they can instantiate one
of a set of convenience classes to access parsers that automatically
implement these resolvers.

The jar files are in the ‘lib’ directory:

  xmlresolver-@@VERSION@@.jar contains the library module
  xmlresolver-apps-@@VERSION@@.jar contains a couple of example applications

See also: https://xmlresolver.org/
