package xbrlcore.junit.linkbase;

import java.util.Iterator;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import xbrlcore.junit.sax.TestHelper;
import xbrlcore.linkbase.PresentationLinkbase;
import xbrlcore.linkbase.PresentationLinkbaseElement;
import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import static org.junit.Assert.*;

/**
 * TODO: Add comment.
 * @author Daniel
 */
public final class PresentationLinkbaseTest {
    /**
     * DTS used in this test case.
     */
    private static DiscoverableTaxonomySet prTaxonomy;

    /**
     * Presentation linkbase used in this test case.
     */
    private static PresentationLinkbase presentationLinkbase;

    /**
     * This method is executed before the test cases in this JUnit test.
     */
    @BeforeClass
    public static void setUp() {
        try {
            // DTSFactory taxonomyFactory = DTSFactory.get();
            // File prTaxonomyFile = new File(PATH+"p-pr-2006-12-31.xsd");
            // prTaxonomy = taxonomyFactory.createTaxonomy(prTaxonomyFile);
            // presentationLinkbase = prTaxonomy.getPresentationLinkbase();

            prTaxonomy = TestHelper
                .getDTS("xbrl/test/taxonomy_original/p-pr-2006-12-31.xsd");
            presentationLinkbase = prTaxonomy.getPresentationLinkbase();
        } catch (Exception ex) {
            fail("Fehler beim Erstellen der Taxonomy pr: " + ex.getMessage());
        }
    }

    /**
     * TODO: Add comment.
     */
    @Test
    public void getPresentationLinkaseElement() {
        Concept elementEssen = prTaxonomy.getConceptByID("p-pr_Essen");
        Concept elementWurst = prTaxonomy.getConceptByID("p-pr_Wurst");
        Concept elementProdukte = prTaxonomy.getConceptByID("p-pr_Produkte");

        PresentationLinkbaseElement presElementEssen = presentationLinkbase
            .getPresentationLinkbaseElement(elementEssen,
                "http://www.xbrl.org/2003/role/link");
        PresentationLinkbaseElement presElementWurst = presentationLinkbase
            .getPresentationLinkbaseElement(elementWurst);
        PresentationLinkbaseElement presElementProdukte = presentationLinkbase
            .getPresentationLinkbaseElement(elementProdukte);

        assertNotNull(presElementEssen);
        assertNotNull(presElementWurst);
        assertNotNull(presElementProdukte);

        assertSame(elementEssen, presElementEssen.getConcept());
        assertSame(elementWurst, presElementWurst.getConcept());

        assertEquals("p-pr_Produkte", presElementEssen.getParentElement()
            .getId());
        assertSame(elementEssen, presElementWurst.getParentElement());
        assertNull(presElementProdukte.getParentElement());

        assertEquals(presElementProdukte.getLevel(), 1);
        assertEquals(presElementEssen.getLevel(), 2);
        assertEquals(presElementWurst.getLevel(), 3);

        assertEquals(7, presElementProdukte.getNumSuccessorAtDeepestLevel());
        assertEquals(2, presElementEssen.getNumSuccessorAtDeepestLevel());
        assertEquals(0, presElementWurst.getNumSuccessorAtDeepestLevel());

        assertEquals(3, presElementProdukte.getSuccessorElements().size());
        assertEquals(2, presElementEssen.getSuccessorElements().size());
        assertEquals(0, presElementWurst.getSuccessorElements().size());
    }

    /**
     * TODO: Add comment.
     */
    @Test
    public void getPresentationLinkbaseElementList() {
        try {
            List<PresentationLinkbaseElement> presentationLinkbaseElementList = presentationLinkbase
                .getPresentationList(prTaxonomy.getTopTaxonomy().getName(),
                    "http://www.xbrl.org/2003/role/link");
            assertNotNull(presentationLinkbaseElementList);
            presentationLinkbaseElementList = null;
            presentationLinkbaseElementList = presentationLinkbase
                .getPresentationList(prTaxonomy.getTopTaxonomy().getName());
            assertNotNull(presentationLinkbaseElementList);
            assertEquals(11, presentationLinkbaseElementList.size());

            Iterator<PresentationLinkbaseElement> presentationLinkbaseElementListIterator = presentationLinkbaseElementList
                .iterator();
            int i = 0;
            while (presentationLinkbaseElementListIterator.hasNext()) {
                PresentationLinkbaseElement currElement = (PresentationLinkbaseElement) presentationLinkbaseElementListIterator
                    .next();
                switch (i) {
                case 0:
                    assertEquals("p-pr_Produkte", currElement.getConcept()
                        .getId());
                    break;
                case 1:
                    assertEquals("p-pr_Essen", currElement.getConcept().getId());
                    break;
                case 2:
                    assertEquals("p-pr_Wurst", currElement.getConcept().getId());
                    break;
                case 3:
                    assertEquals("p-pr_Fleisch", currElement.getConcept()
                        .getId());
                    break;
                case 4:
                    assertEquals("p-pr_Getraenke", currElement.getConcept()
                        .getId());
                    break;
                case 5:
                    assertEquals("p-pr_Cola", currElement.getConcept().getId());
                    break;
                case 6:
                    assertEquals("p-pr_Fanta", currElement.getConcept().getId());
                    break;
                case 7:
                    assertEquals("p-pr_Bier", currElement.getConcept().getId());
                    break;
                case 8:
                    assertEquals("p-pr_Elektronik", currElement.getConcept()
                        .getId());
                    break;
                case 9:
                    assertEquals("p-pr_Computer", currElement.getConcept()
                        .getId());
                    break;
                case 10:
                    assertEquals("p-pr_Fernseher", currElement.getConcept()
                        .getId());
                }
                i++;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
}
