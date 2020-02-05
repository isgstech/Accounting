package xbrlcore.junit.base;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import xbrlcore.junit.sax.TestHelper;
import xbrlcore.linkbase.DefinitionLinkbase;
import xbrlcore.linkbase.PresentationLinkbase;
import xbrlcore.linkbase.PresentationLinkbaseElement;
import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import xbrlcore.xlink.Arc;
import xbrlcore.xlink.ExtendedLinkElement;
import xbrlcore.xlink.Locator;
import static org.junit.Assert.*;

/**
 * This JUnit test tests extended functions of linkbases, as for example whether
 * the correct order of the arcs is kept or whether overriding links are
 * working.
 * @author SantosEE
 */
public final class DTSExtendedLinkbaseTests {

    /**
     * DTS used in this test class.
     */
    private static DiscoverableTaxonomySet tTaxonomy;

    /**
     * DTS used in this test class.
     */
    private static DiscoverableTaxonomySet taxProhibitedLinkDTS;

    /**
     * DTS used in this test class.
     */
    private static DiscoverableTaxonomySet taxProhibitedLinkDTS2;

    /**
     * This method is executed before the test cases of this JUnit test.
     */
    @BeforeClass
    public static void setUp() {
        try {
            // DTSFactory taxonomyFactory = DTSFactory.get();
            // File prTaxonomyFile = new File(PATH + "t-2006-12-31.xsd");
            // File taxProhibitedLinkDTSFile = new File(PATH2
            // + "tax_prohibited_link_2.xsd");
            // tTaxonomy = taxonomyFactory.createTaxonomy(prTaxonomyFile);
            // taxProhibitedLinkDTS = taxonomyFactory
            // .createTaxonomy(taxProhibitedLinkDTSFile);
            // taxProhibitedLinkDTS2 = taxonomyFactory.createTaxonomy(new File(
            // PATH3 + "p2.xsd"));

            tTaxonomy = TestHelper
                .getDTS("xbrl/test/taxonomy_original/t-2006-12-31.xsd");
            taxProhibitedLinkDTS = TestHelper
                .getDTS("xbrl/test/linkbase_test/tax_prohibited_link_2.xsd");
            taxProhibitedLinkDTS2 = TestHelper
                .getDTS("xbrl/test/linkbase_test_2/p2.xsd");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error when creating taxonomy pr: " + ex.getMessage());
            System.out.println(ex.toString());
        }
    }

    /**
     * Tests whether prohibiting links are working.
     */
    @Test
    public void prohibitedLink() {
        try {
            assertNotNull(taxProhibitedLinkDTS);

            DefinitionLinkbase defLinkbase = taxProhibitedLinkDTS
                .getDefinitionLinkbase();

            /*
             * there are four arcs, but one prohibits another, so there must
             * only be two arcs in the base set
             */
            List<Arc> defArcList = defLinkbase.getArcBaseSet();
            assertEquals(2, defArcList.size());

            // there must not be a locator for concept b in the base set of arcs
            Concept b = taxProhibitedLinkDTS.getConceptByID("p0_b");
            assertNotNull(b);
            ExtendedLinkElement conBElem = defLinkbase
                .getExtendedLinkElementFromBaseSet(b, null);
            assertNull(conBElem);

            /*
             * there must be one prohibited and one prohibiting arc for the
             * standard extended link role
             */
            List<Arc> prohibitedArcs = defLinkbase.getProhibitedArcs();
            List<Arc> prohibitingArcs = defLinkbase.getProhibitingArcs();
            assertEquals(1, prohibitedArcs.size());
            assertEquals(1, prohibitingArcs.size());

        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            fail(ex.toString());
        }
    }

    /**
     * Tests whether overridden and overriding links are working.
     */
    @Test
    public void overrideLink() {
        try {
            PresentationLinkbase presLinkbase = tTaxonomy
                .getPresentationLinkbase();

            List<Arc> presXArcList = presLinkbase.getArcBaseSet(
                "http://www.xbrl.org/2003/arcrole/parent-child", null);

            /* three links all in all, but one is overriden! */
            assertEquals(2, presXArcList.size());

            Concept xbrlElement1 = tTaxonomy.getConceptByID("t_Element1");
            Concept xbrlElement3 = tTaxonomy.getConceptByID("t_Element3");
            assertNotNull(xbrlElement1);
            assertNotNull(xbrlElement3);

            ExtendedLinkElement xLinkElement1 = presLinkbase
                .getExtendedLinkElementFromBaseSet(xbrlElement3, null);
            assertNotNull(xLinkElement1);

            /* there are three arcs, but one is overriden! */
            List<ExtendedLinkElement> targetXLinkElements = presLinkbase
                .getTargetExtendedLinkElements(xbrlElement1, null);
            assertEquals(2, targetXLinkElements.size());

            List<Arc> overriddenArcs = presLinkbase.getOverridenArcs();
            assertNotNull(overriddenArcs);
            assertEquals(1, overriddenArcs.size());
            assertEquals("Element1_2", overriddenArcs.get(0)
                .getSourceElement().getLabel());
            assertEquals("Element3", overriddenArcs.get(0)
                .getTargetElement().getLabel());

            List<Arc> arcList = presLinkbase.getArcBaseSet();
            assertEquals(2, arcList.size());

            /* test whether the arc points to the right locator! */
            List<ExtendedLinkElement> targetElementList = presLinkbase
                .getTargetExtendedLinkElements(xbrlElement1, null);
            assertEquals(2, targetElementList.size());
            ExtendedLinkElement lastElement = targetElementList
                .get(1);
            assertEquals("Element3_2", lastElement.getLabel());

            /* there must be one overridden link */
            List<Arc> overridenLinks = presLinkbase
                .getOverridenArcs("http://www.xbrl.org/2003/role/link");
            assertEquals(1, overridenLinks.size());
            Arc overridenArc = overridenLinks.get(0);
            assertEquals("Element3", overridenArc.getTargetElement().getLabel());

            /*
             * there must not be only one locator for concept Element3 in the
             * base set of arcs
             */
            Concept elem3 = tTaxonomy.getConceptByID("t_Element3");
            assertNotNull(elem3);
            ExtendedLinkElement el = presLinkbase
                .getExtendedLinkElementFromBaseSet(elem3, null);
            assertNotNull(el);
            assertEquals("Element3_2", el.getLabel());

        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            fail(ex.toString());
        }
    }

    /**
     * Tests whether the correct order of the arcs is kept.
     */
    @Test
    public void linkOrder() {
        try {
            PresentationLinkbase presLinkbase = tTaxonomy
                .getPresentationLinkbase();

            Concept element1 = tTaxonomy.getConceptByID("t_Element1");
            Concept element2 = tTaxonomy.getConceptByID("t_Element2");
            Concept element3 = tTaxonomy.getConceptByID("t_Element3");
            Concept element4 = tTaxonomy.getConceptByID("t_Element4");
            List<ExtendedLinkElement> targetXLinkElements = presLinkbase
                .getTargetExtendedLinkElements(element1,
                    "http://www.xbrl.org/2003/role/link_order");

            final int NUMBER_OF_EXPECTED_XLINK_ELEMENTS = 3;
            assertEquals(NUMBER_OF_EXPECTED_XLINK_ELEMENTS, targetXLinkElements
                .size());

            ExtendedLinkElement xLink1 = targetXLinkElements
                .get(0);
            ExtendedLinkElement xLink2 = targetXLinkElements
                .get(1);
            ExtendedLinkElement xLink3 = targetXLinkElements
                .get(2);

            assertEquals(((Locator) xLink1).getConcept(), element4);
            assertEquals(((Locator) xLink2).getConcept(), element3);
            assertEquals(((Locator) xLink3).getConcept(), element2);

            List<PresentationLinkbaseElement> presentationLinkbaseElementList = presLinkbase
                .getPresentationList(tTaxonomy.getTopTaxonomy().getName(),
                    "http://www.xbrl.org/2003/role/link_order");

            final int NUMBER_OF_EXPECTED_PRES_ELEMENTS = 4;
            assertEquals(NUMBER_OF_EXPECTED_PRES_ELEMENTS,
                presentationLinkbaseElementList.size());

            PresentationLinkbaseElement presElem1 = presentationLinkbaseElementList
                .get(1);
            PresentationLinkbaseElement presElem2 = presentationLinkbaseElementList
                .get(2);
            PresentationLinkbaseElement presElem3 = presentationLinkbaseElementList
                .get(3);

            assertEquals(presElem1.getConcept(), element4);
            assertEquals(presElem2.getConcept(), element3);
            assertEquals(presElem3.getConcept(), element2);
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    /**
     * Tests whether a speciel combination of prohibited links is working.
     */
    @Test
    public void prohibitedLink2() {
        /*
         * Achtung: Bei diesem Test wurden in der Taxonomie in den
         * überschreibenden und den verbietenden Arc zwei Attribute eingefügt,
         * so dass sie nicht beachtet werden dürften (nicht-XLink Attribute
         * müssten gleich sein). Im Taxonomie-Editor lässt sich das nicht mehr
         * fehlerfrei öffnen weil es nicht mehr gegen das Schema validiert, für
         * diesen Test ist das aber ausreichend.
         */
        List<Arc> baseList = taxProhibitedLinkDTS2.getDefinitionLinkbase()
            .getArcBaseSet();
        final int NUMBER_OF_EXPECTED_ELEMENTS = 4;
        assertEquals(NUMBER_OF_EXPECTED_ELEMENTS, baseList.size());

        List<Arc> prohibitList = taxProhibitedLinkDTS2.getDefinitionLinkbase()
            .getProhibitedArcs();
        assertNull(prohibitList);
        List<Arc> overridingList = taxProhibitedLinkDTS2.getDefinitionLinkbase()
            .getOverridenArcs();
        assertNull(overridingList);
    }

}
