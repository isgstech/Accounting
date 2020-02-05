package xbrlcore.junit.linkbase;

import java.util.Iterator;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import xbrlcore.constants.GeneralConstants;
import xbrlcore.junit.sax.TestHelper;
import xbrlcore.linkbase.LabelLinkbase;
import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;

import static org.junit.Assert.*;

public final class LabelLinkbaseTest {
    /**
     * DTS used in this test case.
     */
    private static DiscoverableTaxonomySet prTaxonomy;

    /**
     * DTS used in this test case.
     */
    private static DiscoverableTaxonomySet tstTaxonomy;

    /**
     * LabelLinkbase used in this test case.
     */
    private static LabelLinkbase labelLinkbase;

    /**
     * DTS used in this test case.
     */
    private static DiscoverableTaxonomySet gvfdiTaxonomy;

    /**
     * This method is executed before the test cases of this JUnit test.
     */
    @BeforeClass
    public static void setUp() {
        try {
            // DTSFactory taxonomyFactory = DTSFactory.get();
            // File prTaxonomyFile = new File(PATH + "p-pr-2006-12-31.xsd");
            // File tstTaxonomyFile = new File(PATH + "t-st-2006-12-31.xsd");
            // prTaxonomy = taxonomyFactory.createTaxonomy(prTaxonomyFile);
            // tstTaxonomy = taxonomyFactory.createTaxonomy(tstTaxonomyFile);
            // labelLinkbase = prTaxonomy.getLabelLinkbase();
            // gvfdiTaxonomy = taxonomyFactory.createTaxonomy(new File(
            // NAT_PATH_GVFDI + "t-gvfdi-2005-06.xsd"));
            prTaxonomy = TestHelper
                .getDTS("xbrl/test/taxonomy_original/p-pr-2006-12-31.xsd");
            tstTaxonomy = TestHelper
                .getDTS("xbrl/test/taxonomy_original/t-st-2006-12-31.xsd");
            labelLinkbase = prTaxonomy.getLabelLinkbase();
            gvfdiTaxonomy = TestHelper
                .getDTS("xbrl/test/taxonomy_national/gvfdi/t-gvfdi-2005-06.xsd");
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            fail("Fehler beim Erstellen der Taxonomy pr: " + ex.getMessage());
        }
    }

    /**
     * TODO: Add comment.
     */
    @Test
    public void getLabel() {
        Concept elementWurst = prTaxonomy.getConceptByID("p-pr_Wurst");
        Concept elementBier = prTaxonomy.getConceptByID("p-pr_Bier");

        String wurstLabelDE = labelLinkbase.getLabel(elementWurst, "de", null);
        String wurstLabelEN = labelLinkbase.getLabel(elementWurst, "en",
            "http://www.xbrl.org/2003/role/link");

        String bierLabelDE = labelLinkbase.getLabel(elementBier, "de", null);
        String bierLabelES = labelLinkbase.getLabel(elementBier, "es",
            "http://www.xbrl.org/2003/role/link");

        assertEquals("Wurst", wurstLabelDE);
        assertEquals("Sausage", wurstLabelEN);
        assertEquals("Bier", bierLabelDE);
        assertNull(bierLabelES);
    }

    /**
     * TODO: Add comment.
     */
    @Test
    public void getLabelRole() {
        Concept elementWurst = prTaxonomy.getConceptByID("p-pr_Wurst");

        String wurstLabelDoku = labelLinkbase.getLabel(elementWurst, "de",
            GeneralConstants.XBRL_ROLE_LABEL_DOCUMENTATION, null);
        assertEquals("Eine andere Rolle", wurstLabelDoku);

        String wurstLabelNichts = labelLinkbase.getLabel(elementWurst, "de",
            "thisRoleDoesNotExist", null);
        assertNull(wurstLabelNichts);

        String wurstPositionLabel = labelLinkbase.getLabel(elementWurst,
            "http://xbrl.bundesbank.de/role/positionLabel");
        assertEquals("noch ne rolle", wurstPositionLabel);

        String wurstPositionLabel2 = tstTaxonomy.getLabelLinkbase().getLabel(
            elementWurst, "http://xbrl.bundesbank.de/role/positionLabel");
        assertEquals("noch ne rolle", wurstPositionLabel2);
    }

    /**
     * Add comment.
     */
    @Test
    public void nationalLabel() {
        assertNotNull(gvfdiTaxonomy);
        Concept firstConcept = gvfdiTaxonomy
            .getConceptByID("p-gvfdi_GVFDIDomain");
        assertNotNull(gvfdiTaxonomy.getLabelLinkbase().getLabel(firstConcept,
            "de", null));

        Set<Concept> conceptSet = gvfdiTaxonomy.getConcepts();
        Iterator<Concept> conceptIterator = conceptSet.iterator();
        while (conceptIterator.hasNext()) {
            Concept currConcept = (Concept) conceptIterator.next();
            assertNull(gvfdiTaxonomy.getLabelLinkbase().getLabel(currConcept,
                "en", null));
        }
        Set<String> languageSet = gvfdiTaxonomy.getLabelLinkbase().getLanguageSet();
        assertEquals(1, languageSet.size());
        assertTrue(gvfdiTaxonomy.getLabelLinkbase().containsLanguage("de"));
        assertFalse(gvfdiTaxonomy.getLabelLinkbase().containsLanguage("en"));
    }

    /**
     * Add comment.
     */
    @Test
    public void getLabel2() {
        Concept elementWurst = prTaxonomy.getConceptByID("p-pr_Wurst");
        assertNotNull(elementWurst);
        String label = labelLinkbase.getLabel(elementWurst,
            "http://xbrl.bundesbank.de/role/positionLabel");
        assertEquals("noch ne rolle", label);
    }
}
