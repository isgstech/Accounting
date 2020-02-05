package xbrlcore.junit.sax;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for xbrlcore.junit");
		//$JUnit-BEGIN$
		suite.addTestSuite(BaseTest.class);
		suite.addTestSuite(LabelLinkbaseTest.class);
		suite.addTestSuite(PresentationLinkbaseTest.class);
		suite.addTestSuite(DefinitionLinkbaseTest.class);
		//$JUnit-END$
		return suite;
	}

}