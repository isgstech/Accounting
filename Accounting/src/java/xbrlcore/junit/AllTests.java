package xbrlcore.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import xbrlcore.junit.base.ConceptTest;
import xbrlcore.junit.base.DTSExtendedLinkbaseTests;
import xbrlcore.junit.base.DTSFactoryTest;
import xbrlcore.junit.base.InvalidTaxonomyTest;
import xbrlcore.junit.dimensions.DimensionTest;
import xbrlcore.junit.dimensions.MultipleDimensionTypeTest;
import xbrlcore.junit.instance.FactTest;
import xbrlcore.junit.instance.InstanceTestTmp;
import xbrlcore.junit.instance.XBRLInstanceFactoryTest;
import xbrlcore.junit.instance.XBRLInstanceTest;
import xbrlcore.junit.linkbase.CalculationLinkbaseTest;
import xbrlcore.junit.linkbase.DefinitionLinkbaseTest;
import xbrlcore.junit.linkbase.LabelLinkbaseTest;
import xbrlcore.junit.linkbase.PresentationLinkbaseTest;
import xbrlcore.junit.linkbase.XBRLLinkbaseTest;

/**
 * This is a test class which runs all JUnit tests registered with this project.
 * @author Daniel, Edward Wang
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { DTSFactoryTest.class, ConceptTest.class,
    XBRLLinkbaseTest.class, LabelLinkbaseTest.class,
    PresentationLinkbaseTest.class, DefinitionLinkbaseTest.class,
    CalculationLinkbaseTest.class, DTSExtendedLinkbaseTests.class,
    InvalidTaxonomyTest.class, DimensionTest.class, FactTest.class,
    XBRLInstanceTest.class, MultipleDimensionTypeTest.class,
    XBRLInstanceFactoryTest.class, InstanceTestTmp.class })
public final class AllTests {

    /**
     * This is a private constructor to prevent the utility class from being
     * instantiated.
     */
    private AllTests() {
    }
}
