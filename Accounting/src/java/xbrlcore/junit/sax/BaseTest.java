package xbrlcore.junit.sax;

import java.util.Iterator;
import java.util.Set;

import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import junit.framework.TestCase;

public class BaseTest extends TestCase {

	DiscoverableTaxonomySet p_dts;

	DiscoverableTaxonomySet t_dts;

	public void setUp() {
		p_dts = TestHelper.getPrimaryTaxonomy();
		t_dts = TestHelper.getTemplateTaxonomy();
	}

	public void testBasic() {
		assertNotNull(p_dts);

		Concept essenConcept = p_dts.getConceptByID("p-pr_Essen");
		assertNotNull(essenConcept);
		assertEquals("Essen", essenConcept.getName());
		assertEquals("xbrli:stringItemType", essenConcept.getType());
		assertEquals("xbrli:item", essenConcept.getSubstitutionGroup());
		assertEquals("instant", essenConcept.getPeriodType());
		assertTrue(essenConcept.isAbstract());
		assertTrue(essenConcept.isNillable());
		assertNull(essenConcept.getTypedDomainRef());
	}

	public void testImportedTaxonomies() {
		assertNotNull(t_dts);

		Set conceptSet = t_dts.getConcepts();
		Iterator conceptSetIterator = conceptSet.iterator();
		//		while (conceptSetIterator.hasNext()) {
		//			System.out.println((Concept) conceptSetIterator.next());
		//		}
		assertEquals(31, t_dts.getConcepts().size());

	}

}