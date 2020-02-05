package xbrlcore.junit.sax;

import xbrlcore.linkbase.LabelLinkbase;
import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import junit.framework.TestCase;

public class LabelLinkbaseTest extends TestCase {

	DiscoverableTaxonomySet p_dts;

	DiscoverableTaxonomySet t_dts;

	public void setUp() {
		p_dts = TestHelper.getPrimaryTaxonomy();
		t_dts = TestHelper.getTemplateTaxonomy();
	}

	public void testLabelLinkbase() {
		assertNotNull(p_dts);

		LabelLinkbase labelLinkbase = p_dts.getLabelLinkbase();
		assertEquals(41, labelLinkbase.getExtendedLinkElements().size());

		Concept wurstConcept = p_dts.getConceptByID("p-pr_Wurst");
		assertNotNull(wurstConcept);
		String wurstLabel = labelLinkbase.getLabel(wurstConcept, "de",
				"http://www.xbrl.org/2003/role/label", null);
		assertEquals("Wurst", wurstLabel);

		Concept fleischConcept = p_dts.getConceptByID("p-pr_Fleisch");
		assertNotNull(fleischConcept);
		String fleischLabel = labelLinkbase.getLabel(fleischConcept, "de",
				"http://www.xbrl.org/2003/role/label",
				"http://xbrlcore/otherRole");
		String fleischLabelDocumentation = labelLinkbase.getLabel(
				fleischConcept, "de",
				"http://www.xbrl.org/2003/role/documentation", null);
		assertEquals("Fleisch", fleischLabel);
		assertEquals("Ein exquisites Stück Fleisch", fleischLabelDocumentation);

		Concept essenConcept = p_dts.getConceptByID("p-pr_Essen");
		assertNotNull(essenConcept);
		String essenLabel = labelLinkbase.getLabel(essenConcept, "en",
				"http://www.xbrl.org/2003/role/label", null);
		assertEquals("Food", essenLabel);
	}

}