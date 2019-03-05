# XMLResolver: An enhanced XML resolver with XML Catalog support

The xmlresolver project provides an advanced implementation of the SAX
`EntityResolver`, the Transformer `URIResolver`, and a new
`NamespaceResolver`. The implementation uses the OASIS XML Catalogs V1.1
Standard to provide a mapping from public identifiers to local
resources.

In addition to enhanced support for RDDL-based namespace resolution,
the implementation supports automatic local caching of resources. This
provides the advantages of the catalog specification without requiring
users to manage the mapping by hand.

Applications can use the resolver directly or they can instantiate one
of a set of convenience classes to access parsers that automatically
implement these resolvers.

The goal of this project is to produce a clean, reasonably simple API
and a robust, thread-safe implementation.

See also: https://xmlresolver.org/

## Release notes

### 1.0.1, ?? March 2019

* Removed the default `xmlresolver.properties` file that shipped with the XML Resolver jar file.
  This fixes [issue 21](https://github.com/ndw/xmlresolver/issues/21) but introduces no change
  in behavior because the default values in the absence of a property configuration file are all
  the same as what was in that default properties file.

### 1.0.0, 2 March 2019

* Let’s call it 1.0.0!

### 0.99.1, 18 February 2019

* Added a DEBUG level message including the resolver version number
* Added an INFO level message for resolved identifiers
* Refactored a bit to remove some code duplication

### 0.99.0, 17 February 2019

This is also mostly a cleanup release.

* I simplified the app `Parse` class. It’s not intended to be a robust parser; it’s
  just a demo of the resolver. See http://github.com/ndw/xjparse for a more
  robust parser.
* I tweaked the way the `xmlresolver.properties` system property is used to
  find the properties file.
* I added support for locating the propertiese file with an `XMLRESOLVER_PROPERTIES`
  environment variable. Note that this must be a URI, not a bare filename.
* I added a few more tests.

If no one reports any problems, I really, truly am going to make a “1.0.0” release
“real soon now.”

### 0.14.0, 7 July 2018

This is mostly a cleanup release:

* The build scripts have been retooled to facilitate separating
  the core library from ancillary applications.
* Updated `org.apache.httpcomponents.httpclient` to version 4.5.5.
* Updated `org.slf4j.slf4j-api` to version 1.7.25.
* The way cache expiration is computed was reworked.
* The `Parse` sample application and the `CacheInfo` utility have
  been moved into a separate jar file, `xmlresolver-apps-[version].jar`.
* The core library is `xmlresolver-[version].jar`.
  There are no static `main()` methods in the core library.
* The core library no longer claims dependencies on logging
  frameworks beyond the SLF4J API. You may need to update your class
  path to provide the frameworks.
* The `log4j2.xml` configuration file is no longer in the core library jar.

If this release goes smoothly, I plan to release this library as version
“1.0.0” sometime this year.
