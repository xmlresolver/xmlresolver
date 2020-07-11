# XMLResolver: An enhanced XML resolver with XML Catalog support

[![Build Status](https://travis-ci.org/ndw/xmlresolver.svg?branch=master)](https://travis-ci.org/ndw/xmlresolver)
[![Java 8](https://img.shields.io/badge/java-8-blue.svg)](https://adoptopenjdk.net/)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.xmlresolver/xmlresolver/badge.svg)](https://search.maven.org/search?q=g:org.xmlresolver)

The xmlresolver project provides an advanced implementation of the SAX
`EntityResolver`, the Transformer `URIResolver`, and a new
`NamespaceResolver`. The implementation uses the OASIS XML Catalogs V1.1
Standard to provide a mapping from public identifiers to local
resources.

The xmlresolver can be found on [Maven Central](https://search.maven.org/search?q=g:org.xmlresolver) and has the coordinates:
```xml
<groupId>org.xmlresolver</groupId>
<artifactId>xmlresolver</artifactId>
```

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
