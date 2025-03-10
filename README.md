# XMLResolver: An enhanced XML resolver with XML Catalog support

[![Build Status](https://github.com/xmlresolver/xmlresolver/actions/workflows/build-branch.yml/badge.svg)](https://github.com/xmlresolver/xmlresolver/actions/workflows/build-branch.yml)
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

For guidelines about how to migrate your application from the Apache Commons resolver to
this resolver, see documentation and examples in
https://github.com/xmlresolver/resolver-migration

## Version 6.x

Version 6.x is a significant refactoring and is not (wholly) backwards compatible with version 5.x.
(The underlying functionality is the same, but the API is slightly different.)
The version 5.x sources are now in the
[legacy_v5](https://github.com/xmlresolver/xmlresolver/tree/legacy_v5) branch. Important
bug fixes will be applied to the 5.x release for some time, but new development is
focused on the 6.x release.

Three main considerations drove the refactoring:

1. Correcting design errors. For example, using a
   `javax.xml.transform.Source` to return a non-XML resource.
2. Simplification of the design (removing the caching feature, for
example)
3. Bringing the Java and
   [C#](https://github.com/xmlresolver/xmlresolvercs) implementations
   into better alignment.

### What’s changed?(tl;dr)

Where previously you would have instantiated an
`org.xmlresolver.Resolver` and used it as the entity resolver for SAX
(and other) APIs, you should now instantiate an
`org.xmlresolver.XMLResolver`. This new object has methods for
performing catalog lookup and resource resolution. It also has methods that
return resolver APIs. See [Using an XML Resolver](https://xmlresolver.org/ch04).

Behind the scenes, the API has been reworked so that most operations
consist of constructing a request for some resource, asking the `XMLResolver` to either
(just) look it up in the catalog or resolve it, and returning a response.

## A note about version numbers

The XML Resolver API is often integrated into other projects and
products. On the one hand, this means that it’s valuable to publish
new releases early so that integrators can test them. On the other
hand, integrators quite reasonably want to make production releases
with only the most stable versions.

In an effort to make this easier, starting with version 6.x, the XML
Resolver releases will use an even/odd pattern version number strategy
to identify development and stable branches.

If the second number in the verion is even, that’s a work-in-progress,
stabalization release. Please test it, and report bugs. If the second
number is odd, that’s a stable release. (Test that and report bugs
too, obviously!)

In other words 6.0.x are stabalization releases. When the API is
deemed stable, there will be a 6.1.0 release. If more features are
developed or significant changes are undertaken, those will be
published in a series of 6.2.x releases before stabalizing in a 6.3.0
release. Etc.

## ChangeLog

### 6.0.13 / 5.3.0

* Changed the API so that an attempt to read a scheme that’s forbidden (by
  `ResolverFeature.ACCESS_EXTERNAL_ENTITY` or
  `ResolverFeature.ACCESS_EXTERNAL_DOCUMENT`) raises an
  `IllegalArgumentException` instead of returning `null`.
* Generally, the XML Resolver tries to avoid throwing exceptions, but in this
  case failing to do so opens a security vulnerability. Returning `null` often
  signals the underlying parser to simply load the resource with the original
  URI. This circumvents the attempt to limit access.

### 6.0.12

* Reworked the API to use interfaces for `ResourceRequest` and
  `ResourceResponse`. This makes writing a schema handler easier. This is a
  backwards incompatible change if you were directly accessing those objects.
  You have to access the `ResourceRequestImpl` and `ResourceResponseImpl`
  instead. On the plus side, the setters are now public on those methods.
* Added code that attempts to detect a Windows path (C:\path) passed as a
  catalog or property file name and avoid accidentally constructing a URI with
  the scheme “C”.

### 6.0.11

This version introduces a new API for registering a scheme resolver. This will
allow a resolver to be configured, for example, to handle custom URI schemes as
are sometimes found in products.

### API Changes

Several classes and interfaces are marked as deprecated. They were removed
for several early 6.0.x releases but have been restored for binary backwards
compatibility.

* `CatalogResover`, `Resolver`, `ResourceResolver`, `StAXResolver`,
   and `XercesResolver` are replaced by methods on `XMLResolver`.
* `Resource`, `ResolvedResource` and `ResolvedResourceImpl` are replaced, effectively, by
  `ResourceRequest` and `ResourceResponse`.
* All the classes related to caching.

The two main classes for users are `XMLResolverConfiguration` (largely
unchanged) and `XMLResolver`.

The new `XMLResolver` object has methods for querying the catalog and
resolving resources. It also has methods that return resolvers for
different APIs.

* `getURIResolver()` returns a `javax.xml.transform.URIResolver`
* `getLSResourceResolver()` returns a `org.w3c.dom.ls.LSResourceResolver`
* `getEntityResolver()` returns a `org.xml.sax.EntityResolver`
* `getEntityResolver2()` returns a `org.xml.sax.ext.EntityResolver2`
* `getXMLResolver()` returns a `javax.xml.stream.XMLResolver`

#### A note about ALWAYS_RESOLVE

The standard contract for the Java resolver APIs is that they return
`null` if the resolver doesn’t find a match. But on the modern web, lots
of URIs redirect (from `http:` to `https:` especially), and some
parsers don’t follow redirects. That causes the parse to fail in ways
that may not be easy for the user to fix.

By default, the XML Resolver will always resolve resources,
follow redirects, and return a stream. This deprives the parser of the
option to try something else, but means that redirects don’t cause the
parse to fail.

If your implementation wants to explicitly just check the catalog, at
the Java API level, you can use the `lookup` methods on `XMLResolver`.

### Behavior changes

The resolver class can be configured with either system properties or
a properties file. If a property is specified in both places, the
system property wins.

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

This catalog entry:

```
  <uri name="http://example.com/example.xml"
       uri="classpath:path/example-doc.xml"/>
```

maps the URI `http://example.com/example.xml` to a document with the path `path/example-doc.xml` on
the classpath. (Searches always begin at the root of the classpath segments, so
`path/example-doc.xml` and `/path/example-doc.xml` are equivalent.)

Suppose, for example, that your classpath
includes `/home/ndw/java/libs/example.jar`:

```
$ jar vtf /home/ndw/java/libs/example.jar
     0 Wed May 05 14:51:50 BST 2021 META-INF/
    25 Wed May 05 14:51:50 BST 2021 META-INF/MANIFEST.MF
     0 Wed May 05 14:51:48 BST 2021 org/
     0 Wed May 05 14:51:50 BST 2021 org/example/
  3262 Wed May 05 14:51:50 BST 2021 org/example/DWIM.class
  1831 Wed May 05 14:51:50 BST 2021 path/example-doc.xml
   219 Wed May 05 14:51:50 BST 2021 path/something-else.txt
```

Assuming that this JAR file is the first place on your classpath where
`path/example-doc.xml` occurs, then that’s the document that will be returned.

#### Lies, damned lies, and URIs

At this point, we expect the resolver to return that resource with a base URI of
`classpath:path/example-doc.xml`. Unfortunately, if we do that, any attempt to resolve
a URI against that document’s base URI (for example, if `example-doc.xml` contains an
XInclude with a relative `href` value), will immediately fail. It fails constructing the
URI long before it calls the resolver to attempt to retrieve it.

To avoid this, the resolver lies. It returns the resource with the base URI set to the
resolved location, `jar:file:///home/ndw/java/libs/example.jar!path/example-doc.xml`.
The URI class doesn’t resolve relative URIs against that base URI either, but at least
it doesn’t throw an exception.

The practical consequence of this is that the resolver never gets
asked to resolve URIs made absolute against either of these forms of
URI. If you put a document in a JAR file, make sure that all of its
relative references (includes, imports, etc.) will resolve correctly.
You can’t re-interpret them in the resolver.

### Support for additional catalog files

If a project uses a particular schema, or set of schemas, it may be
useful to add an additional catalog (or catalogs) to the user’s default
catalog file path. That’s not currently practical:
if both system properties and a property file are used to configure a
resolver, and the same setting appears in both places, either the
system property value is used (the default in 2.x) or the property
file value is used (the default in 1.x).

So in order to add additional catalog files, you’d have to work out
the current value, construct a new value incorporating both that
default and your new catalog(s), and specify that new value in the
`xml.catalog.files` system property.

To make this easier, see the `xml.catalog.additions` system property,
and the `catalog-additions` property file key. Both properties take
a list of catalog files. Those files will be added to the list defined
by the normal catalog settings.

### Support for validating catalog files

The resolver is tolerant of errors in catalog files by design. A
production application shouldn’t fall over because someone adds a bad
catalog file to the catalog path. Instead, the errant file is simply
ignored.

During development, it may be useful to take a more restrictive view.
Putting a typo in a catalog file is one common source of resolution
failures. More than once, I’ve spent time trying to track down a
resolver bug only to discover that I’d typed `systemid` instead of
`systemId` in a catalog file, or made some other similar error.

Obviously, editing your catalog files with a validating editor is one
simply remedy to this problem, but experience suggests that’s not
always what actually happens.

You can use the
[catalog-loader-class](https://xmlresolver.org/#xml.catalog.catalogLoaderClass) property
to specify an alternate catalog loader. If you specify,
`org.xmlresolver.loaders.ValidatingXmlLoader`, the catalog files will be validated
as they are loaded and validation errors will raise an exception.

In order to use this feature, you must have
[Jing](https://search.maven.org/artifact/org.relaxng/jing) version 20181222 on your classpath.

### Support for dynamically constructed catalog files

Starting in version 5.2.0, it is possible to construct a catalog
directly from a stream of SAX events, rather than by parsing an input
stream. (Shout out to @adamretter for providing this patch.)

This means that the catalog can be, for example, stored in a database
where it may not be conveniently accessible as a character stream, or
even stored in a completely novel format.

See [the JavaDoc API](https://xmlresolver.org/javadoc/org/xmlresolver/loaders/CatalogLoader.html#loadCatalog(java.net.URI,org.xmlresolver.utils.SaxProducer))
for more details.

## Building from Source Code

First clone the Git Repository:
```bash
$ git clone https://github.com/xmlresolver/xmlresolver.git
```

then enter the project folder, and pull in the Git sub-module `data`:
```bash
$ cd xmlresolver
$ git submodule sync
$ git submodule update --init
```

To compile the project and check its tests you must have Java 21. It is
easiest to do with the provided Gradle Wrapper (i.e. `./gradlew`), but
you can use your own installed version of Gradle if it’s compatible
with Gradle 8.5 (the version of Gradle used by this project at the
time of this writing).

to build the project:
```bash
$ ./gradlew build
```

## Testing

In order to run the tests, you must have Docker installed and you will need to
build the container that runs the web server.

More details T.B.D.
