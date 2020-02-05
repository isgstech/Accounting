package xbrlcore.junit.base;

import java.util.Map;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import xbrlcore.junit.sax.TestHelper;
import xbrlcore.linkbase.DefinitionLinkbase;
import xbrlcore.linkbase.LabelLinkbase;
import xbrlcore.linkbase.PresentationLinkbase;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import xbrlcore.taxonomy.TaxonomySchema;

/**
 * This JUnit test tests whether a DTS can be created by a DTSFactory.
 * @author SantosEE
 */
public final class DTSFactoryTest {

    /**
     * DTS used in this test.
     */
    private static DiscoverableTaxonomySet prTaxonomy;

    /**
     * This method is executed before the test cases of this JUnit test.
     */
    @BeforeClass
    public static void setUp() {
        try {
            // DTSFactory taxonomyFactory = DTSFactory.get();
            // File prTaxonomyFile = new File(PATH+"p-pr-2006-12-31.xsd");
            // prTaxonomy = taxonomyFactory.createTaxonomy(prTaxonomyFile);
            prTaxonomy = TestHelper
                .getDTS("xbrl/test/taxonomy_original/p-pr-2006-12-31.xsd");
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            fail("Error when creating taxonomy pr: " + ex.getMessage());
        }
    }

    /**
     * Tests whether a created DTS is not null.
     */
    @Test
    public void createTaxonomy() {
        assertNotNull(prTaxonomy);
    }

    /**
     * Tests whether the DTS contains all the necessary imported taxonomy
     * schemas.
     */
    @Test
    public void importedTaxonomies() {
        final int NUMBER_OF_EXPECTED_TAXONOMIES = 6;

        Map<String, TaxonomySchema> dts = prTaxonomy.getTaxonomyMap();
        assertEquals(NUMBER_OF_EXPECTED_TAXONOMIES, dts.size());

        TaxonomySchema xbrlInstance = prTaxonomy
            .getTaxonomySchema("http://www.xbrl.org/2003/xbrl-instance-2003-12-31.xsd");
        TaxonomySchema xbrldt = prTaxonomy
            .getTaxonomySchema("xbrldt-2005-11-07.xsd");
        TaxonomySchema xbrli = prTaxonomy
            .getTaxonomySchema("xbrl-instance-2003-12-31.xsd");
        TaxonomySchema xl = prTaxonomy.getTaxonomySchema("xl-2003-12-31.xsd");
        TaxonomySchema nonsense = prTaxonomy.getTaxonomySchema("notAvailable");

        assertNull(xbrlInstance);
        assertNotNull(xbrldt);
        assertNotNull(xbrli);
        assertNotNull(xl);
        assertNull(nonsense);

        TaxonomySchema prSchema = prTaxonomy
            .getTaxonomySchema("p-pr-2006-12-31.xsd");
        assertNotNull(prSchema);
        assertTrue(prSchema.importsTaxonomySchema(xbrldt));
        assertFalse(prSchema.importsTaxonomySchema(null));
    }

    /**
     * Tests whether the DTS contains all linkbases.
     */
    @Test
    public void allLinkbasesContained() {
        LabelLinkbase labelLinkbase = prTaxonomy.getLabelLinkbase();
        PresentationLinkbase presentationLinkbase = prTaxonomy
            .getPresentationLinkbase();
        DefinitionLinkbase definitionLinkbase = prTaxonomy
            .getDefinitionLinkbase();

        assertNotNull(labelLinkbase);
        assertNotNull(presentationLinkbase);
        assertNotNull(definitionLinkbase);
    }
}
