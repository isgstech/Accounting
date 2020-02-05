package xbrlcore.junit.sax;

import java.util.Iterator;
import java.util.List;

import xbrlcore.linkbase.PresentationLinkbase;
import xbrlcore.linkbase.PresentationLinkbaseElement;
import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import junit.framework.TestCase;

public class PresentationLinkbaseTest extends TestCase {

	DiscoverableTaxonomySet p_dts;

	DiscoverableTaxonomySet t_dts;

	public void setUp() {
		p_dts = TestHelper.getPrimaryTaxonomy();
		t_dts = TestHelper.getTemplateTaxonomy();
	}

	public void testPresentationLinkbase() {
		assertNotNull(p_dts);

		PresentationLinkbase presentationLinkbase = p_dts
				.getPresentationLinkbase();

		List rootElementList = presentationLinkbase
				.getPresentationLinkbaseElementRoot(null);
		assertEquals(1, rootElementList.size());

		PresentationLinkbaseElement presElement = (PresentationLinkbaseElement) rootElementList
				.get(0);
		assertNotNull(presElement);
		assertEquals("p-pr_Produkte", presElement.getConcept().getId());

		List presentationList = presentationLinkbase.getPresentationList(
				presElement.getConcept(), null);
		assertEquals(11, presentationList.size());
	}

	public void testPresentationList1() {
		assertNotNull(p_dts);

		Concept pConcept1 = p_dts.getConceptByID("p-pr_Essen");
		Concept pConcept2 = p_dts.getConceptByID("p-pr_Wurst");
		Concept pConcept3 = p_dts.getConceptByID("p-pr_Fleisch");
		assertNotNull(pConcept1);
		assertNotNull(pConcept2);
		assertNotNull(pConcept3);

		PresentationLinkbase presentationLinkbase = p_dts
				.getPresentationLinkbase();
		List elementList = presentationLinkbase.getPresentationList(pConcept1,
				null);
		assertEquals(3, elementList.size());

		Concept lConcept1 = ((PresentationLinkbaseElement) elementList.get(0))
				.getConcept();
		Concept lConcept2 = ((PresentationLinkbaseElement) elementList.get(1))
				.getConcept();
		Concept lConcept3 = ((PresentationLinkbaseElement) elementList.get(2))
				.getConcept();

		assertEquals(pConcept1, lConcept1);
		assertEquals(pConcept2, lConcept2);
		assertEquals(pConcept3, lConcept3);
	}

	public void testPresentationIterator1() {
		assertNotNull(p_dts);

		Concept pConcept1 = p_dts.getConceptByID("p-pr_Produkte");

		PresentationLinkbase presentationLinkbase = p_dts
				.getPresentationLinkbase();
		Iterator pIterator = presentationLinkbase.iterator(pConcept1, null);
		int i = 0;
		while (pIterator.hasNext()) {
			PresentationLinkbaseElement pElement = (PresentationLinkbaseElement) pIterator
					.next();
			switch (i++) {
			case 0:
				assertEquals("p-pr_Produkte", pElement.getConcept().getId());
				break;
			case 1:
				assertEquals("p-pr_Essen", pElement.getConcept().getId());
				break;
			case 2:
				assertEquals("p-pr_Wurst", pElement.getConcept().getId());
				break;
			case 3:
				assertEquals("p-pr_Fleisch", pElement.getConcept().getId());
				break;
			case 4:
				assertEquals("p-pr_Getraenke", pElement.getConcept().getId());
				break;
			case 5:
				assertEquals("p-pr_Cola", pElement.getConcept().getId());
				break;
			case 6:
				assertEquals("p-pr_Fanta", pElement.getConcept().getId());
				break;
			case 7:
				assertEquals("p-pr_Bier", pElement.getConcept().getId());
				break;
			case 8:
				assertEquals("p-pr_Elektronik", pElement.getConcept().getId());
				break;
			case 9:
				assertEquals("p-pr_Computer", pElement.getConcept().getId());
				break;
			case 10:
				assertEquals("p-pr_Fernseher", pElement.getConcept().getId());
				break;
			}
		}
	}

	public void testPresentationList2() {
		try {
			assertNotNull(t_dts);

			Concept pConcept1 = t_dts.getConceptByID("d-ko_Kontinente");
			Concept pConcept2 = t_dts.getConceptByID("d-ko_Europa");
			Concept pConcept3 = t_dts.getConceptByID("d-ko_Asien");
			Concept pConcept4 = t_dts.getConceptByID("d-ko_Amerika");
			assertNotNull(pConcept1);
			assertNotNull(pConcept2);
			assertNotNull(pConcept3);
			assertNotNull(pConcept4);

			PresentationLinkbase presentationLinkbase = t_dts
					.getPresentationLinkbase();
			List elementList = presentationLinkbase
					.getPresentationList("d-ko-2006-12-31.xsd");
			assertEquals(4, elementList.size());
			Concept lConcept1 = ((PresentationLinkbaseElement) elementList
					.get(0)).getConcept();
			Concept lConcept2 = ((PresentationLinkbaseElement) elementList
					.get(1)).getConcept();
			Concept lConcept3 = ((PresentationLinkbaseElement) elementList
					.get(2)).getConcept();
			Concept lConcept4 = ((PresentationLinkbaseElement) elementList
					.get(3)).getConcept();

			assertEquals(pConcept1, lConcept1);
			assertEquals(pConcept2, lConcept2);
			assertEquals(pConcept3, lConcept3);
			assertEquals(pConcept4, lConcept4);

		} catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}

}