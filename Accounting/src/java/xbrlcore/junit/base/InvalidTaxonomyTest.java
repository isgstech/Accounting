package xbrlcore.junit.base;

import org.junit.Test;

import xbrlcore.exception.TaxonomyCreationException;
import xbrlcore.junit.sax.TestHelper;
import static org.junit.Assert.*;

/**
 * This JUnit tests tries to load an invalid taxonomy, and it must fail.
 * @author Daniel Hamm
 */
public final class InvalidTaxonomyTest {

    /**
     * An error is expected when trying to load an invalid taxonomy. This
     * taxonomy is invalid because a hypercube item is not in the substitution
     * group xbrldt:hypercubeItem.
     */
    @Test
    public void hypercubeWrongSubstGroup() {
        try {
            // DTSFactory taxonomyFactory = DTSFactory.get();
            // File tTaxonomyFile = new
            // File(PATH+"t-st-invalid-2006-12-31.xsd");
            // tTaxonomy = taxonomyFactory.createTaxonomy(tTaxonomyFile);
            TestHelper
                .getDTS("xbrl/test/taxonomy_original/t-st-invalid-2006-12-31.xsd");
            fail("TaxonomyCreationException should have been thrown!");
        } catch (TaxonomyCreationException ex) {
            System.out.println("Expected exception: " + ex.getMessage());
        } catch (Exception ex) {
            fail("Error when creating taxonomy t: " + ex.getMessage());
        }
    }

}
