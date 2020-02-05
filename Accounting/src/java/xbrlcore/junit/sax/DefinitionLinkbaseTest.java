package xbrlcore.junit.sax;

import java.util.Set;

import xbrlcore.linkbase.DefinitionLinkbase;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import junit.framework.TestCase;

public class DefinitionLinkbaseTest extends TestCase {

	DiscoverableTaxonomySet t_dts;

	public void setUp() {
		t_dts = TestHelper.getTemplateTaxonomy();
		assertNotNull(t_dts);
	}

	public void testNumberOfHypercubes() {
		DefinitionLinkbase defLinkbase = t_dts.getDefinitionLinkbase();

		Set hSet = defLinkbase.getHypercubeSet();
		assertEquals(3, hSet.size());
	}
}