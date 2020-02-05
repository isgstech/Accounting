package xbrlcore.junit.base;

import java.util.Set;

import org.jdom.Element;
import org.junit.BeforeClass;
import org.junit.Test;

import xbrlcore.junit.sax.TestHelper;
import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;

import static org.junit.Assert.*;

/**
 * This JUnit Test class tests methods and features of the class
 * xbrlcore.taxonomy.Concept.
 * @author Daniel Hamm, Edward Wang
 */

public final class ConceptTest {

    /**
     * DTS used in this test.
     */
    private static DiscoverableTaxonomySet prTaxonomy;

    /**
     * DTS used in this test.
     */
    private static DiscoverableTaxonomySet templateTaxonomy;

    /**
     * DTS used in this test.
     */
    private static DiscoverableTaxonomySet tmcTaxonomy;

    /**
     * This method is executed before the test cases of this JUnit test.
     */
    @BeforeClass
    public static void setUp() {
        try {
            // DTSFactory taxonomyFactory = DTSFactory.get();
            // File prTaxonomyFile = new File(PATH + "p-pr-2006-12-31.xsd");
            // prTaxonomy = taxonomyFactory.createTaxonomy(prTaxonomyFile);
            // templateTaxonomy = taxonomyFactory.createTaxonomy(new File(PATH
            // + "t-st-2006-12-31.xsd"));
            // tmcTaxonomy = taxonomyFactory.createTaxonomy(new File(COREP_PATH
            // + "t-mc-2005-12-31.xsd"));

            prTaxonomy = TestHelper
                    .getDTS("xbrl/test/taxonomy_original/p-pr-2006-12-31.xsd");
            templateTaxonomy = TestHelper
                    .getDTS("xbrl/test/taxonomy_original/t-st-2006-12-31.xsd");
            tmcTaxonomy = TestHelper
                    .getDTS("xbrl/taxonomies1.0/t-mc-2005-12-31.xsd");

        } catch (Exception ex) {
            System.err.println(ex.toString());
            ex.printStackTrace();
            fail("Error when creating taxonomy pr: " + ex.getMessage());
        }
    }

    /**
     * Tests core functions of Concept.
     */
    @Test
    public void conceptsCore() {
        final int NUMBER_OF_EXPECTED_CONCEPTS = 13;
        Set<Concept> conceptSet = prTaxonomy.getConcepts();

        /* 11 elements from p-pr and 2 elements from xbrldt */
        assertEquals(NUMBER_OF_EXPECTED_CONCEPTS, conceptSet.size());

        Concept essenElement = prTaxonomy.getConceptByID("p-pr_Essen");
        Concept wurstElement = prTaxonomy.getConceptByID("p-pr_Wurst");

        assertNotNull(essenElement);
        assertNotNull(wurstElement);

        assertEquals("Essen", essenElement.getName());
        assertEquals("p-pr_Essen", essenElement.getId());
        assertEquals("xbrli:stringItemType", essenElement.getType());
        assertEquals("xbrli:item", essenElement.getSubstitutionGroup());
        assertEquals("instant", essenElement.getPeriodType());
        assertTrue(essenElement.isAbstract());
        assertTrue(!wurstElement.isAbstract());
        assertTrue(essenElement.isNillable());
        assertEquals("p-pr-2006-12-31.xsd", essenElement
                .getTaxonomySchemaName());
    }

    /**
     * Tests whether concepts can be derived from a DTS.
     */
    @Test
    public void conceptsFromImportedTaxonomies() {
        Concept essenElement = templateTaxonomy.getConceptByID("p-pr_Wurst");
        Concept usaElement = templateTaxonomy.getConceptByID("d-la_USA");
        assertNotNull(essenElement);
        assertNotNull(usaElement);
        assertEquals("d-la-2006-12-31.xsd", usaElement.getTaxonomySchemaName());
    }

    /**
     * Tests typed dimensions.
     */
    @Test
    public void typedDimension() {
        try {
            assertNotNull(tmcTaxonomy);

            Concept dimConcept = tmcTaxonomy
                    .getConceptByID("t-mc_CommodityDimension");
            assertNotNull(dimConcept);

            Element typedElement = tmcTaxonomy
                    .getElementForTypedDimension(dimConcept);
            assertNotNull(typedElement);
            assertEquals("Commodity", typedElement.getName());
        } catch (Exception ex) {
            System.err.println(ex.toString());
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
}
