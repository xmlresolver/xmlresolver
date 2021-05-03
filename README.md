# XMLResolver: An enhanced XML resolver with XML Catalog support

[![Build Status](https://travis-ci.org/ndw/xmlresolver.svg?branch=master)](https://travis-ci.org/ndw/xmlresolver)
[![Java 8](https://img.shields.io/badge/java-8-blue.svg)](https://adoptopenjdk.net/)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.xmlresolver/xmlresolver/badge.svg)](https://search.maven.org/search?q=g:org.xmlresolver)

*Attention: On 7 August 2020, I changed the default branch in this repository to `main`. 
If you’ve got a clone of this repo, you may want to update it. Apologies for the inconvenience.*

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

For guidelines about how to migrate your application from the Apache Commons resolver to
this resolver, see documentation and examples in https://github.com/ndw/resolver-migration/

## ChangeLog

### API Changes

Version 2.0.x introduces some API changes. The most common user-level APIs (`new Catalog()`,
`new Resolver()`, etc. are unchanged), but if you’re
extending or integrating with the resolver directly, you may have to change a few things.
I’ve added a `ResolverFeature` type to track features in a more typesafe way.

The `Configuration` object has been renamed `XMLResolverConfiguration`
and now implements the `ResolverConfiguration` interface.

Several methods on the configuration object have been removed. Their
values can be obtained by requesting the specific feature.

### Behavior changes

The resolver class can be configured with either system properties or
a properties file. In version 1.x, if a property file is used, the
values specified in that file always take precedence. 

This means that you can’t, for example, selectively override the
catalog file list for a single application by specifying a system
property.

On reflection, that seems backwards. In 2.x, if a property is
specified in both places, the system property wins.

To keep the existing behavior, set the boolean property
`prefer-property-file` to `true` in the property file. That
will preserve the former behavior. (The system property equivalent is
`xml.catalog.preferPropertyFile`.)

### Support for `data:` URIs

It is now possible to use `data:` URIs in the catalog. Data URIs are defined by
[RFC 2397](https://tools.ietf.org/html/rfc2397). For example, this catalog entry:

```
  <uri name="http://example.com/example.xml"
       uri="data:application/xml;base64,PGRvYz5JIHdhcyBhIGRhdGEgVVJJPC9kb2M+Cg=="/>
```

maps the URI `http://example.com/example.xml` to a short XML
document defined by that data URI (`<doc>I was a data URI</doc>`).

### Support for `classpath:` URIs

It is now possible to use `classpath:` URIs in the catalog. It is also
possible to use `classpath:` URIs in the catalog list. The
`classpath:` URI scheme seems to be defined
[somewhat informally](https://www.javarticles.com/2013/10/spring-classpath-resource.html) by
the Spring framework.

In brief, a `classpath:` URI is resolved by attempting to find a
document with the specified path in the classpath, including within
JAR files on the classpath.

For example, this catalog entry:

```
  <uri name="http://example.com/example.xml"
       uri="classpath:path/example-doc.xml"/>
```

maps the URI `http://example.com/example.xml` to a document with the path `path/example-doc.xml` on
the classpath. Searches always begin at the root of the classpath segments, so
`path/example-doc.xml` and `/path/example-doc.xml` are equivalent.

(The resolver also supports `classpath*:` but since it’s defined as
concatenating the resources identified, it’s of comparatively little
use in the XML case.)

### Support for additional catalog files

If a project uses a particular schema, or set of schemas, it may be
useful to add an additional catalog (or catalogs) to the users default
catalog file path. That’s not currently practical.

If both system properties and a property file are used to configure a
resolver, and the same setting appears in both places, either the
system property value is used (the default in 2.x) or the property
file value is used (the default in 1.x).

So in order to add additional catalog files, you’d have to work out
the current value, construct a new value incorporating both that
default and your new catalog(s), and specify that new value in the
`xml.catalog.files` system property.

To make this easier, the 2.0 release adds a new system property,
`xml.catalog.additions`, and a new property file key,
`catalog-additions`. Both properties take a list of catalog files.
Those files will be added to the list defined by the normal catalog
files properties.
