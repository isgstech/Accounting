package xbrlcore.junit.dimensions;

import org.jdom.Element;
import org.junit.BeforeClass;
import org.junit.Test;

import xbrlcore.dimensions.Hypercube;
import xbrlcore.dimensions.MultipleDimensionType;
import xbrlcore.dimensions.Dimension;
import xbrlcore.dimensions.SingleDimensionType;
import xbrlcore.junit.sax.TestHelper;
import xbrlcore.linkbase.DefinitionLinkbase;
import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import static org.junit.Assert.*;

/**
 * This JUnit test tests whether dimensions can be derived from hypercube, and
 * whether the dimensional information of a definition linkbase is processed
 * correctly.
 * @author Daniel Hamm
 */
public final class DimensionTest {

    /**
     * DTS used in this test.
     */
    private static DiscoverableTaxonomySet tstTaxonomy;

    /**
     * This method is executed before all tests in this JUnit test.
     */
    @BeforeClass
    public static void setUp() {
        try {
            // DTSFactory taxonomyFactory = DTSFactory.get();
            // File tstTaxonomyFile = new File(PATH+"t-st-2006-12-31.xsd");
            // tstTaxonomy = taxonomyFactory.createTaxonomy(tstTaxonomyFile);
            tstTaxonomy = TestHelper
                .getDTS("xbrl/test/taxonomy_typed_dimension/t-st-2006-12-31.xsd");
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            fail("Fehler beim Erstellen der Taxonomy pr: " + ex.toString());
        }
    }

    /**
     * Tests whether Dimension objects can be derived correclty from Hypercube
     * objects.
     */
    @Test
    public void dimension() {
        Concept hypercubeEuropa = tstTaxonomy.getConceptByID("t-st_hcEuropa");
        assertNotNull(hypercubeEuropa);

        Hypercube hcEuropa = tstTaxonomy.getDefinitionLinkbase().getHypercube(
            hypercubeEuropa);
        assertNotNull(hcEuropa);

        Concept dimensionKontinent = tstTaxonomy
            .getConceptByID("t-st_KontinentDimension");
        Concept dimensionLaender = tstTaxonomy
            .getConceptByID("d-la_LaenderDimension");
        Concept dimensionAnzahl = tstTaxonomy
            .getConceptByID("t-st_AnzahlDimension");

        assertNotNull(dimensionKontinent);
        assertNotNull(dimensionLaender);
        assertNotNull(dimensionAnzahl);

        Dimension kontinentDimension = hcEuropa
            .getDimension(dimensionKontinent);
        Dimension laenderDimension = hcEuropa.getDimension(dimensionLaender);
        Dimension anzahlDimension = hcEuropa.getDimension(dimensionAnzahl);

        assertFalse(kontinentDimension.isTyped());
        assertFalse(laenderDimension.isTyped());
        assertTrue(anzahlDimension.isTyped());
    }

    /**
     * Tests whether the information of the definition linkbase is processed
     * correctly, that is whether a certain combination of dimensions / domain
     * member is allowed.
     */
    @Test
    public void dimensionAllowed() {
        try {
            DefinitionLinkbase defLinkbase = tstTaxonomy
                .getDefinitionLinkbase();

            Concept landDeutschland = tstTaxonomy
                .getConceptByID("d-la_Deutschland");
            Concept landJapan = tstTaxonomy.getConceptByID("d-la_Japan");
            Concept laenderDimension = tstTaxonomy
                .getConceptByID("d-la_LaenderDimension");

            Concept kontinentEuropa = tstTaxonomy.getConceptByID("d-ko_Europa");
            Concept kontinentDimension = tstTaxonomy
                .getConceptByID("t-st_KontinentDimension");

            Concept anzahlDimension = tstTaxonomy
                .getConceptByID("t-st_AnzahlDimension");

            // Set allElements = tstTaxonomy.getConcepts();
            // Iterator allElementsIterator = allElements.iterator();
            // while (allElementsIterator.hasNext()) {
            // Concept currConcept = (Concept) allElementsIterator.next();
            // System.out.println(currConcept.getId());
            // }

            assertNotNull(landDeutschland);
            assertNotNull(landJapan);
            assertNotNull(laenderDimension);
            assertNotNull(kontinentEuropa);
            assertNotNull(kontinentDimension);
            assertNotNull(anzahlDimension);

            MultipleDimensionType mdt = new MultipleDimensionType(
                laenderDimension, landDeutschland);
            mdt.addPredecessorDimensionDomain(new SingleDimensionType(
                kontinentDimension, kontinentEuropa));
            mdt.addPredecessorDimensionDomain(new SingleDimensionType(
                anzahlDimension, new Element("a")));

            Concept primaryItem = tstTaxonomy.getConceptByID("p-pr_Wurst");
            assertNotNull(primaryItem);

            assertTrue(defLinkbase.dimensionAllowed(primaryItem, mdt,
                "scenario"));
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            fail(ex.toString());
        }
    }

}
