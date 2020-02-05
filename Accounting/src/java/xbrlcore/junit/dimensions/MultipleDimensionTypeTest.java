package xbrlcore.junit.dimensions;

import java.util.Map;

import org.jdom.Element;
import org.junit.BeforeClass;
import org.junit.Test;

import xbrlcore.dimensions.MultipleDimensionType;
import xbrlcore.dimensions.SingleDimensionType;
import xbrlcore.junit.sax.TestHelper;
import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import static org.junit.Assert.*;

/**
 * This JUnit test tests whether the xbrlcore.dimensions.MultipleDimensionType
 * is working correctly.
 * @author Daniel Hamm
 */
public final class MultipleDimensionTypeTest {

    /**
     * DTS used in this test.
     */
    private static DiscoverableTaxonomySet stTaxonomy;

    /**
     * This method is executed before all tests of this test case.
     */
    @BeforeClass
    public static void setUp() {
        try {
            // DTSFactory taxonomyFactory = DTSFactory.get();
            // File stTaxonomyFile = new File(PATH + "t-st-2006-12-31.xsd");
            // stTaxonomy = taxonomyFactory.createTaxonomy(stTaxonomyFile);
            stTaxonomy = TestHelper
                .getDTS("xbrl/test/taxonomy_typed_dimension/t-st-2006-12-31.xsd");
            stTaxonomy.getTaxonomySchema("d-la-2006-12-31.xsd");
            stTaxonomy.getTaxonomySchema("d-ko-2006-12-31.xsd");
            stTaxonomy.getTaxonomySchema("p-pr-2006-12-31.xsd");
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            fail("Fehler beim Erstellen der Taxonomy pr: " + ex.toString());
        }
    }

    /**
     * This method tests whether the predecessor dimension / domain combinations
     * are working correctly.
     */
    @Test
    public void multipleDimensionType() {
        try {
            Concept landDeutschland = stTaxonomy
                .getConceptByID("d-la_Deutschland");
            Concept landJapan = stTaxonomy.getConceptByID("d-la_Japan");
            Concept laenderDimension = stTaxonomy
                .getConceptByID("d-la_LaenderDimension");

            Concept kontinentEuropa = stTaxonomy.getConceptByID("d-ko_Europa");
            Concept kontinentAsien = stTaxonomy.getConceptByID("d-ko_Asien");
            Concept kontinentDimension = stTaxonomy
                .getConceptByID("t-st_KontinentDimension");

            Concept kontinentDimension2 = stTaxonomy
                .getConceptByID("t-st_KontinentDimension");

            Concept anzahlDimension = stTaxonomy
                .getConceptByID("t-st_AnzahlDimension");

            assertNotNull(landDeutschland);
            assertNotNull(landJapan);
            assertNotNull(laenderDimension);
            assertNotNull(kontinentEuropa);
            assertNotNull(kontinentDimension);
            assertNotNull(anzahlDimension);

            MultipleDimensionType mdt = new MultipleDimensionType(
                laenderDimension, landDeutschland);
            /* kontinentDimension and kontinentDimension2 are the same elements! */
            mdt.addPredecessorDimensionDomain(new SingleDimensionType(
                kontinentDimension, kontinentEuropa));
            mdt.overrideDimensionDomain(new SingleDimensionType(
                kontinentDimension2, kontinentAsien));

            Map<Concept, Concept> map = mdt.getPredecessorDimensionDomainMap();
            Concept currKontinentElement = map.get(kontinentDimension);
            /*
             * domain kontinentAsien has overridden the domain kontinentEuropa
             * of dimension kontinentDimension
             */
            assertSame(kontinentAsien, currKontinentElement);

            /*
             * there must be onlyone predecessor dimension / domain combination,
             * since both dimension / domain combinations which have been added
             * refer to the same dimension!
             */
            assertEquals(1, mdt.getPredecessorDimensionDomainMap().size());

            MultipleDimensionType mdtClone = (MultipleDimensionType) mdt
                .clone();
            assertEquals(1, mdtClone.getPredecessorDimensionDomainMap().size());
            assertEquals(laenderDimension, mdtClone.getPresentDimensionDomain()
                .getDimensionConcept());
            assertEquals(landDeutschland, mdtClone.getPresentDimensionDomain()
                .getDomainMemberConcept());
            assertEquals(mdt, mdtClone);
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            fail(ex.toString());
        }
    }

    /**
     * TODO: Add comment.
     */
    @Test
    public void singleDimensionType() {
        try {
            Concept laenderDimension = stTaxonomy
                .getConceptByID("d-la_LaenderDimension");
            Concept landDeutschland = stTaxonomy
                .getConceptByID("d-la_Deutschland");
            SingleDimensionType sdt = new SingleDimensionType(laenderDimension,
                landDeutschland);
            SingleDimensionType sdtCopy = (SingleDimensionType) sdt.clone();
            assertEquals(sdtCopy.getDimensionConcept(), laenderDimension);
            assertEquals(sdtCopy.getDomainMemberConcept(), landDeutschland);
            assertTrue(sdt.equals(sdtCopy));

            Element e = new Element("Test");
            e.setText("Wert");
            SingleDimensionType sdt2 = new SingleDimensionType(
                laenderDimension, e);
            SingleDimensionType sdt2Copy = (SingleDimensionType) sdt2.clone();
            assertEquals(sdt2Copy.getDimensionConcept(), laenderDimension);
            assertEquals(sdt2Copy.getTypedDimensionElement().getName(), "Test");
            assertEquals(sdt2Copy.getTypedDimensionElement().getValue(), "Wert");
            assertTrue(sdt2.equals(sdt2Copy));
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            fail(ex.toString());
        }
    }

}
