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
        config.setFeature(ResolverFeature.CACHE, null);
        config.setFeature(ResolverFeature.CACHE_UNDER_HOME, false);
        manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
    }

    // These tests should be regenerated with the make-catalog.xsl stylesheet using the $generate-tests
    // option whenever the set of resources included in the data jar changes.

    @Test
    public void gen_lookupPublicid_0_0() {
        URI result = manager.lookupPublic(null, "-//XML-DEV//ENTITIES RDDL QName Module 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_0_1() {
        URI result = manager.lookupSystem("http://www.rddl.org/rddl-qname-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_1_0() {
        URI result = manager.lookupPublic(null, "-//XML-DEV//ELEMENTS RDDL Resource 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_1_1() {
        URI result = manager.lookupSystem("http://www.rddl.org/rddl-resource-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_2_0() {
        URI result = manager.lookupPublic(null, "-//XML-DEV//DTD XHTML RDDL 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_2_1() {
        URI result = manager.lookupSystem("http://www.rddl.org/rddl-xhtml.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_3_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-arch-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_4_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-attribs-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_5_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-base-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_6_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-basic-form-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_7_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-basic-table-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_8_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-blkphras-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_9_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-blkstruct-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_10_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-charent-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_11_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-datatypes-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_12_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-events-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_13_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-framework-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_14_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-hypertext-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_15_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-image-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_16_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-inlphras-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_17_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-inlstruct-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_18_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-link-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_19_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-list-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_20_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-meta-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_21_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-notations-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_22_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-object-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_23_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-param-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_24_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-qname-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_25_0() {
        URI result = manager.lookupPublic(null, "-//XML-DEV//ENTITIES RDDL Document Model 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_25_1() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-rddl-model-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_26_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-struct-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_27_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-text-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_28_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml11.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_29_0() {
        URI result = manager.lookupPublic(null, "-//XML-DEV//ENTITIES XLink Module 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_29_1() {
        URI result = manager.lookupSystem("http://www.rddl.org/xlink-module-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_30_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-lat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_31_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-special.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_32_0() {
        URI result = manager.lookupSystem("http://www.rddl.org/xhtml-symbol.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_33_0() {
        URI result = manager.lookupURI("https://www.w3.org/1999/xlink.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_34_0() {
        URI result = manager.lookupURI("https://www.w3.org/XML/2008/06/xlink.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_35_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XMLSCHEMA 200102//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_35_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2001/XMLSchema.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_36_0() {
        URI result = manager.lookupURI("https://www.w3.org/2001/XMLSchema.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_37_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XSD 1.0 Datatypes//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_37_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2001/datatypes.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_38_0() {
        URI result = manager.lookupURI("https://www.w3.org/2001/xml.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_39_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD Specification V2.10//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_39_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2002/xmlspec/dtd/2.10/xmlspec.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_40_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES uppercase aliases for HTML//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_40_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/html5-uppercase.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_41_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES HTML MathML Set//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_41_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/htmlmathml-f.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_42_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES HTML MathML Set//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_42_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/htmlmathml.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_43_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Arrow Relations//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_43_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isoamsa.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_44_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Binary Operators//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_44_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isoamsb.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_45_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Delimiters//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_45_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isoamsc.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_46_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Negated Relations//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_46_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isoamsn.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_47_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Ordinary//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_47_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isoamso.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_48_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Relations//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_48_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isoamsr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_49_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Box and Line Drawing//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_49_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isobox.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_50_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Russian Cyrillic//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_50_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isocyr1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_51_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Non-Russian Cyrillic//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_51_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isocyr2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_52_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Diacritical Marks//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_52_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isodia.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_53_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Greek Letters//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_53_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isogrk1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_54_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Monotoniko Greek//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_54_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isogrk2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_55_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Greek Symbols//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_55_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isogrk3.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_56_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Alternative Greek Symbols//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_56_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isogrk4.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_57_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Latin 1//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_57_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isolat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_58_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Latin 2//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_58_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isolat2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_59_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Math Alphabets: Fraktur//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_59_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isomfrk.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_60_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Math Alphabets: Open Face//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_60_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isomopf.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_61_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Math Alphabets: Script//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_61_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isomscr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_62_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Numeric and Special Graphic//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_62_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isonum.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_63_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Publishing//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_63_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isopub.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_64_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES General Technical//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_64_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/isotech.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_65_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES MathML Aliases//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_65_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/mmlalias.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_66_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Additional MathML Symbols//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_66_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/mmlextra.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_67_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Predefined XML//EN///XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_67_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/predefined.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_68_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Combined Set//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_68_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/w3centities-f.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_69_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Combined Set//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_69_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/w3centities.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_70_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Latin for HTML//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_70_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/xhtml1-lat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_71_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Special for HTML//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_71_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/xhtml1-special.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_72_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Symbol for HTML//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_72_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/2007/xhtml1-symbol.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_73_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Added Math Symbols: Arrow Relations//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_73_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isoamsa.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_74_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Added Math Symbols: Binary Operators//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_74_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isoamsb.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_75_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Added Math Symbols: Delimiters//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_75_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isoamsc.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_76_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Added Math Symbols: Negated Relations//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_76_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isoamsn.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_77_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Added Math Symbols: Ordinary//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_77_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isoamso.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_78_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Added Math Symbols: Relations//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_78_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isoamsr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_79_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Box and Line Drawing//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_79_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isobox.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_80_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Russian Cyrillic//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_80_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isocyr1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_81_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Non-Russian Cyrillic//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_81_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isocyr2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_82_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Diacritical Marks//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_82_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isodia.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_83_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Greek Letters//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_83_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isogrk1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_84_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Monotoniko Greek//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_84_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isogrk2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_85_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Greek Symbols//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_85_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isogrk3.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_86_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Alternative Greek Symbols//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_86_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isogrk4.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_87_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Added Latin 1//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_87_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isolat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_88_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Added Latin 2//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_88_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isolat2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_89_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Numeric and Special Graphic//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_89_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isonum.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_90_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES Publishing//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_90_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isopub.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_91_0() {
        URI result = manager.lookupPublic(null, "ISO 8879:1986//ENTITIES General Technical//EN//XML");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_91_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2003/entities/iso8879/isotech.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_92_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XSD 1.1//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_92_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2009/XMLSchema/XMLSchema.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_92_2() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xmlschema11-1/XMLSchema.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_92_3() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xmlschema11-2/XMLSchema.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_93_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XSD 1.1 Datatypes//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_93_1() {
        URI result = manager.lookupSystem("https://www.w3.org/2009/XMLSchema/datatypes.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_93_2() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xmlschema11-1/datatypes.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_93_3() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xmlschema11-2/datatypes.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_94_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Animation//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_94_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-animation.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_95_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Animation Events Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_95_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-animevents-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_96_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Basic Clip//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_96_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-basic-clip.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_97_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Basic Filter//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_97_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-basic-filter.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_98_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Basic Font//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_98_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-basic-font.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_99_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Basic Graphics Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_99_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-basic-graphics-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_100_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Basic Paint Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_100_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-basic-paint-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_101_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Basic Structure//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_101_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-basic-structure.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_102_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Basic Text//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_102_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-basic-text.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_103_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Clip//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_103_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-clip.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_104_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Conditional Processing//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_104_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-conditional.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_105_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Container Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_105_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-container-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_106_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Core Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_106_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-core-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_107_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Cursor//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_107_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-cursor.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_108_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Datatypes//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_108_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-datatypes.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_109_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Document Events Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_109_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-docevents-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_110_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Extensibility//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_110_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-extensibility.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_111_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 External Resources Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_111_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-extresources-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_112_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Filter//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_112_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-filter.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_113_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Font//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_113_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-font.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_114_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Modular Framework//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_114_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-framework.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_115_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Gradient//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_115_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-gradient.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_116_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Graphical Element Events Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_116_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-graphevents-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_117_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Graphics Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_117_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-graphics-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_118_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Hyperlinking//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_118_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-hyperlink.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_119_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Image//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_119_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-image.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_120_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Marker//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_120_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-marker.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_121_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Mask//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_121_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-mask.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_122_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Paint Opacity Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_122_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-opacity-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_123_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Paint Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_123_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-paint-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_124_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Pattern//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_124_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-pattern.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_125_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Color Profile//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_125_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-profile.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_126_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Qualified Name//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_126_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-qname.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_127_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Scripting//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_127_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-script.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_128_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Shape//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_128_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-shape.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_129_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Structure//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_129_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-structure.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_130_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Style//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_130_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-style.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_131_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG Template Qualified Name//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_131_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-template-qname.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_132_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-template.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_133_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 Text//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_133_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-text.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_134_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS SVG 1.1 View//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_134_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-view.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_135_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Viewport Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_135_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-viewport-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_136_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 XLink Attribute//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_136_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg-xlink-attrib.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_137_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Attribute Collection//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_137_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-attribs.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_138_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Basic Attribute Collection//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_138_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-basic-attribs.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_139_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-basic-flat.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_140_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Basic Document Model//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_140_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-basic-model.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_141_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD SVG 1.1 Basic//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_141_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-basic.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_142_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-flat-20030114.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_143_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-flat.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_144_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Document Model//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_144_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-model.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_145_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD SVG 1.1 Template//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_145_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-template.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_146_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Tiny Attribute Collection//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_146_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-tiny-attribs.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_147_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-tiny-flat.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_148_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES SVG 1.1 Tiny Document Model//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_148_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-tiny-model.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_149_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD SVG 1.1 Tiny//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_149_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-tiny.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_150_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD SVG 1.1//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_150_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_151_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Access Element 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_151_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-access-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_152_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Access Attribute Qnames 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_152_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-access-qname-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_153_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Java Applets 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_153_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-applet-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_154_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Base Architecture 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_154_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-arch-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_155_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Common Attributes 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_155_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-attribs-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_156_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Base Element 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_156_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-base-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_157_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Basic Forms 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_157_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-basic-form-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_158_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Basic Tables 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_158_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-basic-table-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_159_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Basic 1.0 Document Model 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_159_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-basic10-model-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_160_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Basic 1.1 Document Model 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_160_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-basic11-model-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_161_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML Basic 1.1//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_161_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-basic11.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_162_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML BIDI Override Element 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_162_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-bdo-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_163_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Block Phrasal 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_163_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-blkphras-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_164_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Block Presentation 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_164_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-blkpres-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_165_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Block Structural 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_165_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-blkstruct-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_166_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Character Entities 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_166_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-charent-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_167_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Client-side Image Maps 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_167_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-csismap-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_168_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Datatypes 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_168_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-datatypes-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_169_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Editing Elements 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_169_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-edit-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_170_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Intrinsic Events 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_170_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-events-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_171_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Forms 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_171_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-form-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_172_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Frames 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_172_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-frames-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_173_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Modular Framework 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_173_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-framework-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_174_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML HyperAttributes 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_174_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-hyperAttributes-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_175_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Hypertext 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_175_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-hypertext-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_176_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Inline Frame Element 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_176_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-iframe-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_177_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Images 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_177_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-image-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_178_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Inline Phrasal 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_178_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-inlphras-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_179_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Inline Presentation 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_179_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-inlpres-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_180_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Inline Structural 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_180_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-inlstruct-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_181_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Inline Style 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_181_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-inlstyle-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_182_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Inputmode 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_182_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-inputmode-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_183_0() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-lat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_184_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Legacy MarkUp 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_184_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-legacy-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_185_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Legacy Redeclarations 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_185_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-legacy-redecl-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_186_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Link Element 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_186_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-link-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_187_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Lists 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_187_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-list-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_188_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Metainformation 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_188_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-meta-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_189_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Metainformation 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_189_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-meta-2.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_190_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML MetaAttributes 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_190_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-metaAttributes-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_191_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Name Identifier 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_191_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-nameident-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_192_0() {
        URI result = manager.lookupPublic(null, "-//W3C//NOTATIONS XHTML Notations 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_192_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-notations-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_193_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Embedded Object 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_193_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-object-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_194_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Param Element 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_194_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-param-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_195_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Presentation 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_195_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-pres-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_196_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML-Print 1.0 Document Model 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_196_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-print10-model-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_197_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Qualified Names 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_197_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-qname-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_198_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML+RDFa Document Model 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_198_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-rdfa-model-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_199_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML RDFa Attribute Qnames 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_199_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-rdfa-qname-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_200_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Role Attribute 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_200_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-role-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_201_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML Role Attribute Qnames 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_201_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-role-qname-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_202_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Ruby 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_202_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-ruby-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_203_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Scripting 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_203_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-script-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_204_0() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-special.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_205_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Server-side Image Maps 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_205_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-ssismap-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_206_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Document Structure 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_206_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-struct-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_207_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Style Sheets 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_207_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-style-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_208_0() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-symbol.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_209_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Tables 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_209_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-table-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_210_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Target 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_210_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-target-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_211_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Text 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_211_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml-text-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_212_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML 1.0 Frameset//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_212_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml1-frameset.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_213_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML 1.0 Strict//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_213_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml1-strict.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_214_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML 1.0 Transitional//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_214_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml1-transitional.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_215_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES XHTML 1.1 Document Model 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_215_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml11-model-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_216_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML 1.1//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_216_1() {
        URI result = manager.lookupSystem("https://www.w3.org/MarkUp/DTD/xhtml11.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_217_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isoamsa.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_218_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isoamsb.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_219_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isoamsc.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_220_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isoamsn.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_221_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isoamso.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_222_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isoamsr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_223_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isobox.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_224_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isocyr1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_225_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isocyr2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_226_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isodia.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_227_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isogrk1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_228_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isogrk2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_229_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isogrk3.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_230_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isogrk4.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_231_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isolat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_232_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isolat2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_233_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isomfrk.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_234_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isomopf.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_235_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isomscr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_236_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isonum.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_237_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isopub.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_238_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/isotech.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_239_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/mathml.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_240_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/mmlalias.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_241_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml1/mmlextra.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_242_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/html/lat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_243_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/html/special.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_244_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/html/symbol.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_245_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isoamsa.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_246_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isoamsb.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_247_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isoamsc.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_248_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isoamsn.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_249_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isoamso.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_250_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isoamsr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_251_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Box and Line Drawing for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_251_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isobox.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_252_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Russian Cyrillic for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_252_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isocyr1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_253_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Non-Russian Cyrillic for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_253_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isocyr2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_254_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Diacritical Marks for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_254_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isodia.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_255_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isogrk1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_256_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isogrk2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_257_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isogrk3.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_258_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isogrk4.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_259_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Latin 1 for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_259_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isolat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_260_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Latin 2 for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_260_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isolat2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_261_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Numeric and Special Graphic for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_261_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isonum.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_262_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Publishing for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_262_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isopub.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_263_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso8879/isotech.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_264_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Arrow Relations for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_264_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isoamsa.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_265_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Binary Operators for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_265_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isoamsb.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_266_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Delimiters for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_266_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isoamsc.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_267_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Negated Relations for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_267_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isoamsn.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_268_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Ordinary for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_268_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isoamso.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_269_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Added Math Symbols: Relations for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_269_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isoamsr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_270_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Greek Symbols for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_270_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isogrk3.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_271_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isogrk4.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_272_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Math Alphabets: Fraktur for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_272_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isomfrk.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_273_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Math Alphabets: Open Face for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_273_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isomopf.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_274_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Math Alphabets: Script for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_274_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isomscr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_275_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES General Technical for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_275_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/iso9573-13/isotech.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_276_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isoamsa.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_277_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isoamsb.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_278_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isoamsc.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_279_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isoamsn.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_280_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isoamso.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_281_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isoamsr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_282_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isobox.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_283_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isocyr1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_284_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isocyr2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_285_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isodia.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_286_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isogrk3.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_287_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isolat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_288_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isolat2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_289_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isomfrk.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_290_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isomopf.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_291_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isomscr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_292_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isonum.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_293_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isopub.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_294_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/isotech.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_295_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Aliases for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_295_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/mathml/mmlalias.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_296_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Extra for MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_296_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/mathml/mmlextra.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_297_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/mathml2-a.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_298_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES MathML 2.0 Qualified Names 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_298_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/mathml2-qname-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_299_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD MathML 2.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_299_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/mathml2.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_300_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/mmlalias.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_301_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/mmlextra.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_302_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/xhtml-math11-f-a.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_303_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/xhtml-math11-f.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_304_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml2/xhtml-math11.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_305_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isoamsa.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_306_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isoamsb.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_307_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isoamsc.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_308_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isoamsn.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_309_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isoamso.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_310_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isoamsr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_311_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isobox.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_312_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isocyr1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_313_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isocyr2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_314_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isodia.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_315_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isogrk3.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_316_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isolat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_317_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isolat2.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_318_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isomfrk.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_319_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isomopf.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_320_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isomscr.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_321_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isonum.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_322_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isopub.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_323_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/isotech.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_324_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES MathML 3.0 Qualified Names 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_324_1() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/mathml3-qname.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_325_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/mathml3.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_326_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/mmlalias.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_327_0() {
        URI result = manager.lookupSystem("https://www.w3.org/Math/DTD/mathml3/mmlextra.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_328_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD SVG 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_328_1() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_329_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Ruby 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_329_1() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/ruby/xhtml-ruby-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_330_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML Basic 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_330_1() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml-basic/xhtml-basic10.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_331_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML BIDI Override Element 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_331_1() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml-modularization/DTD/xhtml-bdo-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_332_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Client-side Image Maps 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_332_1() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml-modularization/DTD/xhtml-csismap-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_333_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Editing Elements 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_333_1() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml-modularization/DTD/xhtml-edit-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_334_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Legacy MarkUp 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_334_1() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml-modularization/DTD/xhtml-legacy-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_335_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Server-side Image Maps 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_335_1() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml-modularization/DTD/xhtml-ssismap-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_336_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ELEMENTS XHTML Tables 1.0//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_336_1() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml-modularization/DTD/xhtml-table-1.mod");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_337_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Latin 1 for XHTML//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_337_1() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml-modularization/DTD/xhtml-lat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_338_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Symbols for XHTML//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_338_1() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml-modularization/DTD/xhtml-special.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_339_0() {
        URI result = manager.lookupPublic(null, "-//W3C//ENTITIES Special for XHTML//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_339_1() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml-modularization/DTD/xhtml-symbol.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_340_0() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_341_0() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml1/DTD/xhtml-special.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_342_0() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml1/DTD/xhtml-symbol.ent");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_343_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML 1.0 Frameset//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_343_1() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_344_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML 1.0 Strict//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_344_1() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_345_0() {
        URI result = manager.lookupPublic(null, "-//W3C//DTD XHTML 1.0 Transitional//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_345_1() {
        URI result = manager.lookupSystem("https://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_346_0() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xmlschema-1/XMLSchema.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_347_0() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xmlschema-2/datatypes.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_348_0() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xmlschema11-1/XMLSchema.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_349_0() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xmlschema11-2/datatypes.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_350_0() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xslt-30/schema-for-json.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_351_0() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xslt-30/schema-for-xslt30.rnc");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_352_0() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xslt-30/schema-for-xslt30.rng");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_353_0() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xslt-30/schema-for-xslt30.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_354_0() {
        URI result = manager.lookupURI("https://www.w3.org/TR/xslt-30/xml-to-json.xsl");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_355_0() {
        URI result = manager.lookupURI("https://www.w3.org/2007/schema-for-xslt20.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_356_0() {
        URI result = manager.lookupURI("https://xmlcatalogs.org/schema/1.1/catalog.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_356_1() {
        URI result = manager.lookupURI("http://www.oasis-open.org/committees/entity/release/1.1/catalog.xsd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_357_0() {
        URI result = manager.lookupURI("https://xmlcatalogs.org/schema/1.1/catalog.rnc");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_358_0() {
        URI result = manager.lookupURI("https://xmlcatalogs.org/schema/1.1/catalog.rng");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_358_1() {
        URI result = manager.lookupURI("http://www.oasis-open.org/committees/entity/release/1.1/catalog.rng");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupPublicid_359_0() {
        URI result = manager.lookupPublic(null, "-//OASIS//DTD XML Catalogs V1.1//EN");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_359_1() {
        URI result = manager.lookupSystem("https://xmlcatalogs.org/schema/1.1/catalog.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupSystemid_359_2() {
        URI result = manager.lookupSystem("http://www.oasis-open.org/committees/entity/release/1.1/catalog.dtd");
        assertNotNull(result);
    }

    @Test
    public void gen_lookupUriid_360_0() {
        URI result = manager.lookupURI("https://xmlresolver.org/data/resolver/succeeded/test/check.xml");
        assertNotNull(result);
    }
}
