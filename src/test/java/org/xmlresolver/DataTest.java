package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;

public class DataTest {
    public static XMLResolverConfiguration config = null;
    public static CatalogManager manager = null;

    @Before
    public void setup() {
        String catalog = "classpath:org/xmlresolver/data/catalog.xml";
        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList(catalog));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
    }

    // These tests should be regenerated with the make-catalog.xsl stylesheet using the $generate-tests
    // option whenever the set of resources included in the data jar changes.

    @Test
    public void gen_lookupPublicd1e3() {
        URI result = manager.lookupPublic(null, "-//XML-DEV//ENTITIES RDDL QName Module 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e4() {
        URI result = manager.lookupSystem("http://www.rddl.org/rddl-qname-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e6() {
        URI result = manager.lookupPublic(null, "-//XML-DEV//ELEMENTS RDDL Resource 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e7() {
        URI result = manager.lookupSystem("http://www.rddl.org/rddl-resource-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e9() {
        URI result = manager.lookupPublic(null, "-//XML-DEV//DTD XHTML RDDL 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e10() {
        URI result = manager.lookupSystem("http://www.rddl.org/rddl-xhtml.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e12() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-arch-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e14() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-attribs-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e16() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-base-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e18() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-basic-form-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e20() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-basic-table-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e22() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-blkphras-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e24() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-blkstruct-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e26() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-charent-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e29() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-datatypes-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e31() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-events-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e33() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-framework-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e35() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-hypertext-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e37() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-image-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e39() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-inlphras-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e41() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-inlstruct-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e43() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-link-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e45() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-list-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e47() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-meta-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e49() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-notations-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e52() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-object-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e54() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-param-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e56() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-qname-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e58() {
        URI result = manager.lookupPublic(null, "-//XML-DEV//ENTITIES RDDL Document Model 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e59() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-rddl-model-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e61() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-struct-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e63() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-text-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e65() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml11.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e67() {
        URI result = manager.lookupPublic(null, "-//XML-DEV//ENTITIES XLink Module 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e68() {
        URI result = manager.lookupSystem("http://www.rddl.org/xlink-module-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUrid1e71() {
        URI result = manager.lookupURI("https://www.w3.org/1999/xlink.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUrid1e74() {
        URI result = manager.lookupURI("https://www.w3.org/XML/2008/06/xlink.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e76() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XMLSCHEMA 200102//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e77() {
        URI result = manager.lookupSystem("https://www.w3.org/2001/XMLSchema.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUrid1e80() {
        URI result = manager.lookupURI("https://www.w3.org/2001/XMLSchema.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e82() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XSD 1.0 Datatypes//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e84() {
        URI result = manager.lookupSystem("https://www.w3.org/2001/datatypes.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUrid1e87() {
        URI result = manager.lookupURI("https://www.w3.org/2001/xml.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e89() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD Specification V2.10//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e90() {
        URI result = manager.lookupSystem("https://www.w3.org/2002/xmlspec/dtd/2.10/xmlspec.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e92() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES uppercase aliases for HTML//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e93() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/html5-uppercase.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e95() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES HTML MathML Set//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e96() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/htmlmathml-f.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e98() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES HTML MathML Set//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e99() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/htmlmathml.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e101() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Arrow Relations//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e102() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isoamsa.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e104() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Binary Operators//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e105() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isoamsb.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e107() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Delimiters//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e108() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isoamsc.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e110() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Negated Relations//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e111() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isoamsn.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e114() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Ordinary//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e115() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isoamso.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e117() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Relations//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e118() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isoamsr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e120() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Box and Line Drawing//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e121() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isobox.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e123() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Russian Cyrillic//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e124() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isocyr1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e126() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Non-Russian Cyrillic//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e127() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isocyr2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e129() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Diacritical Marks//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e130() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isodia.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e132() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Greek Letters//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e133() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isogrk1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e135() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Monotoniko Greek//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e136() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isogrk2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e138() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Greek Symbols//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e139() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isogrk3.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e141() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Alternative Greek Symbols//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e142() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isogrk4.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e144() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Latin 1//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e145() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isolat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e148() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Latin 2//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e149() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isolat2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e151() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Math Alphabets: Fraktur//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e152() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isomfrk.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e154() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Math Alphabets: Open Face//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e155() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isomopf.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e157() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Math Alphabets: Script//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e158() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isomscr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e160() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Numeric and Special Graphic//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e161() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isonum.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e163() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Publishing//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e164() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isopub.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e166() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES General Technical//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e167() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isotech.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e169() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES MathML Aliases//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e170() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/mmlalias.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e172() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Additional MathML Symbols//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e173() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/mmlextra.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e175() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Predefined XML//EN///XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e176() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/predefined.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e178() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Combined Set//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e179() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/w3centities-f.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e182() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Combined Set//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e183() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/w3centities.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e185() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Latin for HTML//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e186() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/xhtml1-lat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e188() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Special for HTML//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e189() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/xhtml1-special.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e191() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Symbol for HTML//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e192() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/xhtml1-symbol.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e194() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Added Math Symbols: Arrow Relations//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e195() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isoamsa.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e197() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Added Math Symbols: Binary Operators//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e198() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isoamsb.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e200() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Added Math Symbols: Delimiters//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e201() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isoamsc.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e203() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Added Math Symbols: Negated Relations//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e204() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isoamsn.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e206() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Added Math Symbols: Ordinary//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e207() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isoamso.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e209() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Added Math Symbols: Relations//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e210() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isoamsr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e212() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Box and Line Drawing//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e213() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isobox.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e216() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Russian Cyrillic//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e217() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isocyr1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e219() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Non-Russian Cyrillic//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e220() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isocyr2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e222() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Diacritical Marks//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e223() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isodia.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e225() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Greek Letters//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e226() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isogrk1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e228() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Monotoniko Greek//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e229() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isogrk2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e231() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Greek Symbols//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e232() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isogrk3.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e234() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Alternative Greek Symbols//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e235() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isogrk4.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e237() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Added Latin 1//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e238() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isolat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e240() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Added Latin 2//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e241() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isolat2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e243() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Numeric and Special Graphic//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e244() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isonum.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e246() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Publishing//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e247() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isopub.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e250() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES General Technical//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e251() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isotech.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e253() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XSD 1.1//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e254() {
        URI result = manager.lookupSystem("https://www.w3.org/2009/XMLSchema/XMLSchema.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e256() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XSD 1.1 Datatypes//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e257() {
        URI result = manager.lookupSystem("https://www.w3.org/2009/XMLSchema/datatypes.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e259() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Animation//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e260() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-animation.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e262() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Animation Events Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e263() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-animevents-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e265() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Basic Clip//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e266() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-basic-clip.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e268() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Basic Filter//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e269() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-basic-filter.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e271() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Basic Font//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e272() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-basic-font.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e274() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Basic Graphics Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e275() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-basic-graphics-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e277() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Basic Paint Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e278() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-basic-paint-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e280() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Basic Structure//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e281() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-basic-structure.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e284() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Basic Text//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e285() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-basic-text.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e287() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Clip//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e288() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-clip.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e290() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Conditional Processing//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e291() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-conditional.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e293() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Container Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e294() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-container-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e296() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Core Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e297() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-core-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e299() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Cursor//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e300() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-cursor.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e302() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Datatypes//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e303() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-datatypes.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e305() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Document Events Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e306() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-docevents-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e308() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Extensibility//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e309() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-extensibility.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e311() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 External Resources Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e312() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-extresources-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e314() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Filter//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e315() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-filter.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e318() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Font//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e319() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-font.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e321() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Modular Framework//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e322() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-framework.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e324() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Gradient//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e325() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-gradient.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e327() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Graphical Element Events Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e328() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-graphevents-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e330() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Graphics Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e331() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-graphics-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e333() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Hyperlinking//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e334() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-hyperlink.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e336() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Image//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e337() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-image.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e339() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Marker//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e340() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-marker.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e342() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Mask//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e343() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-mask.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e345() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Paint Opacity Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e346() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-opacity-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e348() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Paint Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e349() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-paint-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e352() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Pattern//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e353() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-pattern.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e355() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Color Profile//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e356() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-profile.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e358() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Qualified Name//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e359() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-qname.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e361() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Scripting//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e362() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-script.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e364() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Shape//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e365() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-shape.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e367() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Structure//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e368() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-structure.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e370() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Style//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e371() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-style.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e373() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG Template Qualified Name//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e374() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-template-qname.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e376() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-template.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e378() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Text//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e379() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-text.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e381() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 View//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e382() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-view.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e385() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Viewport Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e386() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-viewport-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e388() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 XLink Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e389() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-xlink-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e391() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Attribute Collection//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e392() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-attribs.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e394() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Basic Attribute Collection//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e395() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-basic-attribs.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e397() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-basic-flat.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e399() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Basic Document Model//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e400() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-basic-model.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e402() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD SVG 1.1 Basic//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e403() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-basic.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e405() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-flat-20030114.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e407() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-flat.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e409() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Document Model//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e410() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-model.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e412() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD SVG 1.1 Template//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e413() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-template.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e416() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Tiny Attribute Collection//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e417() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-tiny-attribs.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e419() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-tiny-flat.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e421() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Tiny Document Model//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e422() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-tiny-model.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e424() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD SVG 1.1 Tiny//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e425() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-tiny.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e427() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD SVG 1.1//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e428() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e430() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Access Element 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e431() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-access-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e433() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Access Attribute Qnames 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e434() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-access-qname-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e436() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Java Applets 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e437() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-applet-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e439() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Base Architecture 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e440() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-arch-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e442() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Common Attributes 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e443() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-attribs-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e445() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Base Element 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e446() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-base-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e449() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Basic Forms 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e450() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-basic-form-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e452() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Basic Tables 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e453() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-basic-table-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e455() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Basic 1.0 Document Model 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e456() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-basic10-model-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e458() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Basic 1.1 Document Model 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e459() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-basic11-model-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e461() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML Basic 1.1//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e462() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-basic11.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e464() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML BIDI Override Element 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e465() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-bdo-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e467() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Block Phrasal 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e468() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-blkphras-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e470() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Block Presentation 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e471() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-blkpres-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e473() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Block Structural 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e474() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-blkstruct-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e476() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Character Entities 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e477() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-charent-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e479() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Client-side Image Maps 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e480() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-csismap-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e483() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Datatypes 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e484() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-datatypes-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e486() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Editing Elements 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e487() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-edit-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e489() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Intrinsic Events 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e490() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-events-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e492() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Forms 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e493() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-form-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e495() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Frames 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e496() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-frames-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e498() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Modular Framework 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e499() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-framework-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e501() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML HyperAttributes 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e502() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-hyperAttributes-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e504() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Hypertext 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e505() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-hypertext-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e507() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Inline Frame Element 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e508() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-iframe-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e510() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Images 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e511() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-image-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e513() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Inline Phrasal 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e514() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-inlphras-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e517() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Inline Presentation 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e518() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-inlpres-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e520() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Inline Structural 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e521() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-inlstruct-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e523() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Inline Style 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e524() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-inlstyle-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e526() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Inputmode 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e527() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-inputmode-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e529() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-lat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e531() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Legacy MarkUp 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e532() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-legacy-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e534() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Legacy Redeclarations 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e535() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-legacy-redecl-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e537() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Link Element 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e538() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-link-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e540() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Lists 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e541() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-list-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e543() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Metainformation 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e544() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-meta-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e546() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Metainformation 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e547() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-meta-2.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e550() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML MetaAttributes 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e551() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-metaAttributes-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e553() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Name Identifier 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e554() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-nameident-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e556() {
        URI result = manager.lookupPublic(null, "-//W3C//NOTATIONS XHTML Notations 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e557() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-notations-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e559() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Embedded Object 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e560() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-object-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e562() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Param Element 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e563() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-param-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e565() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Presentation 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e566() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-pres-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e568() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML-Print 1.0 Document Model 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e569() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-print10-model-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e571() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Qualified Names 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e572() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-qname-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e574() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML+RDFa Document Model 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e575() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-rdfa-model-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e577() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML RDFa Attribute Qnames 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e578() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-rdfa-qname-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e580() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Role Attribute 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e581() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-role-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e584() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Role Attribute Qnames 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e585() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-role-qname-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e587() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Ruby 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e588() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-ruby-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e590() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Scripting 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e591() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-script-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e593() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-special.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e595() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Server-side Image Maps 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e596() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-ssismap-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e598() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Document Structure 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e599() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-struct-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e601() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Style Sheets 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e602() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-style-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e604() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-symbol.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e606() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Tables 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e607() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-table-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e609() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Target 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e610() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-target-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e612() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Text 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e613() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-text-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e616() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML 1.0 Frameset//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e617() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml1-frameset.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e619() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML 1.0 Strict//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e620() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml1-strict.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e622() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML 1.0 Transitional//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e623() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml1-transitional.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e625() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML 1.1 Document Model 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e626() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml11-model-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e628() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML 1.1//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e629() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml11.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e631() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isoamsa.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e633() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isoamsb.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e635() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isoamsc.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e637() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isoamsn.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e639() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isoamso.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e641() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isoamsr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e644() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isobox.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e646() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isocyr1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e648() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isocyr2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e650() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isodia.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e652() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isogrk1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e654() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isogrk2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e656() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isogrk3.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e658() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isogrk4.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e660() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isolat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e662() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isolat2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e664() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isomfrk.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e667() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isomopf.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e669() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isomscr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e671() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isonum.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e673() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isopub.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e675() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isotech.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e677() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/mathml.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e679() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/mmlalias.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e681() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/mmlextra.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e683() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/html/lat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e685() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/html/special.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e687() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/html/symbol.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e690() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isoamsa.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e692() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isoamsb.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e694() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isoamsc.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e696() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isoamsn.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e698() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isoamso.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e700() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isoamsr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e702() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Box and Line Drawing for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e703() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isobox.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e705() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Russian Cyrillic for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e706() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isocyr1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e708() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Non-Russian Cyrillic for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e709() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isocyr2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e711() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Diacritical Marks for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e712() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isodia.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e714() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isogrk1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e717() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isogrk2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e719() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isogrk3.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e721() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isogrk4.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e723() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Latin 1 for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e724() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isolat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e726() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Latin 2 for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e727() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isolat2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e729() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Numeric and Special Graphic for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e730() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isonum.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e732() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Publishing for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e733() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isopub.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e735() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isotech.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e737() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Arrow Relations for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e738() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isoamsa.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e740() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Binary Operators for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e741() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isoamsb.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e743() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Delimiters for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e744() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isoamsc.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e747() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Negated Relations for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e748() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isoamsn.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e750() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Ordinary for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e751() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isoamso.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e753() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Relations for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e754() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isoamsr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e756() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Greek Symbols for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e757() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isogrk3.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e759() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isogrk4.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e761() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Math Alphabets: Fraktur for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e762() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isomfrk.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e764() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Math Alphabets: Open Face for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e765() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isomopf.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e767() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Math Alphabets: Script for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e768() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isomscr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e770() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES General Technical for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e771() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isotech.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e773() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isoamsa.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e775() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isoamsb.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e778() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isoamsc.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e780() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isoamsn.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e782() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isoamso.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e784() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isoamsr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e786() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isobox.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e788() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isocyr1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e790() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isocyr2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e792() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isodia.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e794() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isogrk3.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e796() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isolat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e798() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isolat2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e801() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isomfrk.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e803() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isomopf.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e805() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isomscr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e807() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isonum.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e809() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isopub.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e811() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isotech.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e813() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Aliases for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e814() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/mathml/mmlalias.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e816() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Extra for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e817() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/mathml/mmlextra.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e819() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/mathml2-a.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e821() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES MathML 2.0 Qualified Names 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e822() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/mathml2-qname-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e824() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e825() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/mathml2.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e828() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/mmlalias.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e830() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/mmlextra.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e832() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/xhtml-math11-f-a.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e834() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/xhtml-math11-f.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e836() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/xhtml-math11.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e838() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isoamsa.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e840() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isoamsb.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e842() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isoamsc.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e844() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isoamsn.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e846() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isoamso.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e848() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isoamsr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e851() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isobox.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e853() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isocyr1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e855() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isocyr2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e857() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isodia.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e859() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isogrk3.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e861() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isolat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e863() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isolat2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e865() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isomfrk.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e867() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isomopf.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e869() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isomscr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e871() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isonum.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e874() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isopub.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e876() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isotech.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e878() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES MathML 3.0 Qualified Names 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e879() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/mathml3-qname.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e881() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/mathml3.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e883() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/mmlalias.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e885() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/mmlextra.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e887() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD SVG 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e888() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e890() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Ruby 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e891() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/ruby/xhtml-ruby-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e893() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML Basic 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e894() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml-basic/xhtml-basic10.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e896() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML BIDI Override Element 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e897() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml-modularization/DTD/xhtml-bdo-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e899() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Client-side Image Maps 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e900() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml-modularization/DTD/xhtml-csismap-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e903() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Editing Elements 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e904() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml-modularization/DTD/xhtml-edit-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e906() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Legacy MarkUp 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e907() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml-modularization/DTD/xhtml-legacy-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e909() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Server-side Image Maps 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e910() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml-modularization/DTD/xhtml-ssismap-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e912() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Tables 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e913() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml-modularization/DTD/xhtml-table-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e915() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e917() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml1/DTD/xhtml-special.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e919() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml1/DTD/xhtml-symbol.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e921() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML 1.0 Frameset//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e922() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e924() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML 1.0 Strict//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e925() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicd1e927() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML 1.0 Transitional//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemd1e928() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUrid1e930() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xmlschema-1/XMLSchema.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUrid1e933() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xmlschema-2/datatypes.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUrid1e935() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xmlschema11-1/XMLSchema.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUrid1e937() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xmlschema11-2/datatypes.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUrid1e939() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xslt-30/schema-for-json.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUrid1e941() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xslt-30/schema-for-xslt30.rnc");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUrid1e943() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xslt-30/schema-for-xslt30.rng");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUrid1e945() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xslt-30/schema-for-xslt30.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUrid1e947() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xslt-30/xml-to-json.xsl");
        assertNotNull(result);
    }
}
