package xbrlcore.instance;

import java.util.Currency;

import xbrlcore.constants.NamespaceConstants;

/**
 * This class creates some basic units often needed in instance files. These
 * units can be created by invoking static methods of this class.<br/><br/>
 * 
 * @author SantosEE
 */
public class InstanceUnitFactory {

	private static InstanceUnit unit4217EUR;

	private static InstanceUnit unitPure;

	/**
	 * 
	 * @return 4217 Euro unit.
	 */
	public static InstanceUnit getUnit4217EUR() {
		unit4217EUR = new InstanceUnit("EUR");
		unit4217EUR.setValue("EUR");
		unit4217EUR.setNamespaceURI(NamespaceConstants.ISO4217_NAMESPACE
				.getURI());
		return unit4217EUR;
	}

	/**
	 * @param isocode : the ISO 4217 code of the currency, see <a href="http://www.iso.org/iso/support/faqs/faqs_widely_used_standards/widely_used_standards_other/currency_codes/currency_codes_list-1.htm">ISO 4217 codes</a>
	 * @return an ISO 4217 compliant InstanceUnit
	 * @author Sébastien Kirche
	 */
	public static InstanceUnit getUnit4217(String isocode) {
		Currency cur;
		try {
			//use the java.util.Currency to check for correctness of the given code
			cur = Currency.getInstance(isocode); 
		} catch(IllegalArgumentException e) {
			cur = null; //TODO throw exception !
		} catch (NullPointerException e) {
			cur = null;
		}
		if (cur != null){
			InstanceUnit newUnit = new InstanceUnit(cur.getCurrencyCode());
			newUnit.setValue(isocode);
			newUnit.setNamespaceURI(NamespaceConstants.ISO4217_NAMESPACE.getURI());
			return newUnit;
		} else
			return null; //TODO throw exception !
	}
	
	/**
	 * 
	 * @return Unit for a pure item type.
	 */
	public static InstanceUnit getUnitPure() {
		unitPure = new InstanceUnit("PURE");
		unitPure.setValue("pure");
		unitPure.setNamespaceURI(NamespaceConstants.XBRLI_NAMESPACE.getURI());
		return unitPure;
	}

}
