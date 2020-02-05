package xbrlcore.taxonomy;

import java.io.Serializable;

import org.jdom.Namespace;

import xbrlcore.constants.GeneralConstants;

/**
 * This class represents a single XBRL concept; which actually is a schema
 * element of the taxonomySchemaName. <br/><br/>
 * 
 * @author SantosEE
 */
public class Concept implements Serializable, Cloneable {

	static final long serialVersionUID = 3237499348079052902L;

	private String name;

	private String id;

	private String type;

	private String substitutionGroup;

	private String periodType;

	private boolean elementAbstract;

	private boolean nillable;

    private boolean fractionItem;

	private boolean numericItem;

	private String taxonomySchemaName;

	private String typedDomainRef;

	private String namespacePrefix;

	private String namespaceUri;

	/**
	 * 
	 * @param name
	 *            The name of the element.
	 */
	public Concept(String name) {
		this.name = name;
		elementAbstract = false;
		nillable = false;
        fractionItem = false;
		numericItem = true;
	}

	/**
	 * 
	 * @return ID of the element.
	 */
	public String toString() {
		if (id != null)
			return "ID: " + id + ", name: " + name;
		else
			return "ID: missing, name: " + name;
	}

	/**
	 * Tests whether this object is equal to another one.
	 * 
	 * @param obj
	 *            Object the current Concept is tested against.
	 * @return True if and only if obj is an Concept and the name of obj is the
	 *         same as the name of the current element, both IDs are the same
	 *         and both belong to the same schema file.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Concept))
			return false;
		Concept otherConcept = (Concept) obj;
		return ((name == null) ? otherConcept.getName() == null : name
				.equals(otherConcept.getName()))
				&& ((id == null) ? otherConcept.getId() == null : id
						.equals(otherConcept.getId()))
				&& ((taxonomySchemaName == null) ? otherConcept
						.getTaxonomySchemaName() == null : taxonomySchemaName
						.equals(otherConcept.getTaxonomySchemaName()));
	}

	/**
	 * 
	 * @return Returns a hash code of this object.
	 */
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (id != null ? id.hashCode() : 0);
		hash = hash * 31 + (name != null ? name.hashCode() : 0);
		hash = hash
				* 31
				+ (taxonomySchemaName != null ? taxonomySchemaName.hashCode()
						: 0);
		return hash;
	}

	/**
	 * Checks whether this concept represents a typed dimension.
	 * 
	 * @return True if this concept is a typed dimension, false otherwise.
	 */
	public boolean isTypedDimension() {
		if (substitutionGroup != null
				&& substitutionGroup
						.equals(GeneralConstants.XBRL_SUBST_GROUP_DIMENSION_ITEM)
				&& typedDomainRef != null) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether this concept represents an explicit dimension.
	 * 
	 * @return True if this concept is an explicit dimension, false otherwise.
	 */
	public boolean isExplicitDimension() {
		if (substitutionGroup
				.equals(GeneralConstants.XBRL_SUBST_GROUP_DIMENSION_ITEM)
				&& typedDomainRef == null) {
			return true;
		}
		return false;
	}

	/**
	 * @return True if the element is abstract, otherwise false.
	 */
	public boolean isAbstract() {
		return elementAbstract;
	}

	/**
	 * @return ID of the element.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return Name of the element.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return True if the element is nillable, otherwise false.
	 */
	public boolean isNillable() {
		return nillable;
	}

	/**
	 * @return Period type of the element.
	 */
	public String getPeriodType() {
		return periodType;
	}

	/**
	 * @return Substitution group of the element.
	 */
	public String getSubstitutionGroup() {
		return substitutionGroup;
	}

	/**
	 * @return Type of the element.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param b
	 *            True if element is abstract, otherwise false.
	 */
	public void setAbstract(boolean b) {
		elementAbstract = b;
	}

	/**
	 * @param b
	 *            True if the element is nillable, otherwise false.
	 */
	public void setNillable(boolean b) {
		nillable = b;
	}

	/**
	 * @param periodType
	 *            Period type of the element.
	 */
	public void setPeriodType(String periodType) {
		if (periodType != null
				&& (periodType.equals(GeneralConstants.CONTEXT_INSTANT) || periodType
						.equals(GeneralConstants.CONTEXT_DURATION))) {
			this.periodType = periodType;
		}
	}

	/**
	 * @param string
	 *            Substitution group of the element.
	 */
	public void setSubstitutionGroup(String string) {
		substitutionGroup = string;
		if(substitutionGroup != null && "xbrli:tuple".contains(substitutionGroup)) {
			numericItem = false;
		}
	}

	/**
	 * @param string
	 *            Type of the element.
	 */
	public void setType(String string) {
    	//TODO: try to find a better method to determine type for inherited types
    	// c.f corep concept p-mi_NumberOfOvershootingsDuringPrevious250WorkingDays from p-mi-2006-07-01.xsd
    	//           that is p-mi:numberOfOvershootingsItemType inherited from xbrli:nonNegativeIntegerItemType
		type = string;
		if(type == null || type.trim().length() == 0) {
			fractionItem = false;
			numericItem = false;
		} else if("xbrli:monetaryItemType/xbrli:sharesItemType/xbrli:pureItemType".contains(type)) {
			fractionItem = false;
			numericItem = true;
		} else if ("dt:nonNegativeMonetaryItemType".contains(type)) {
			fractionItem = false;
			numericItem = true;
		} else if("p-mi:multiplicationFactorItemType/p-mi:numberOfOvershootingsItemType".contains(type)) {
			fractionItem = false;
			numericItem = true;
		} else if("xbrli:decimalItemType/xbrli:floatItemType/xbrli:doubleItemType".contains(type)) {
			fractionItem = false;
			numericItem = true;
		} else if("xbrli:integerItemType/xbrli:nonPositiveIntegerItemType/xbrli:negativeIntegerItemType".contains(type)) {
			fractionItem = false;
			numericItem = true;
		} else if("xbrli:longItemType/xbrli:intItemType/xbrli:shortItemType/xbrli:byteItemType".contains(type)) {
			fractionItem = false;
			numericItem = true;
		} else if("xbrli:nonNegativeIntegerItemType/xbrli:unsignedLongItemType/xbrli:unsignedIntItemType".contains(type)) {
			fractionItem = false;
			numericItem = true;
		} else if("xbrli:unsignedShortItemType/xbrli:unsignedByteItemType/xbrli:positiveIntegerItemType".contains(type)) {
			fractionItem = false;
			numericItem = true;
		} else if("xbrli:fractionItemType".contains(type)) {
			fractionItem = true;
			numericItem = true;
		} else {
			fractionItem = false;
			numericItem = false;
		}

    }

	/**
	 * @return Taxonomy name this element belongs to.
	 */
	public String getTaxonomySchemaName() {
		return taxonomySchemaName;
	}

	/**
	 * @param taxonomyName
	 *            Taxonomy name this element belongs to.
	 */
	public void setTaxonomySchemaName(String taxonomyName) {
		this.taxonomySchemaName = taxonomyName;
	}

	/**
	 * @param id
	 *            ID of the concept.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return typedDomainRef attribute.
	 */
	public String getTypedDomainRef() {
		return typedDomainRef;
	}

	/**
	 * 
	 * @param typedDomainRef
	 *            typedDomainRef attribute.
	 */
	public void setTypedDomainRef(String typedDomainRef) {
		this.typedDomainRef = typedDomainRef;
	}

	/**
	 * 
	 * @param ns
	 *            Target namespace of the taxonomy schema this concept belongs
	 *            to.
	 */
	public void setNamespace(Namespace ns) {
		namespacePrefix = ns.getPrefix();
		namespaceUri = ns.getURI();
	}

	/**
	 * 
	 * @return Target namespace of the taxonomy schema this concept belongs to.
	 */
	public Namespace getNamespace() {
		return Namespace.getNamespace(namespacePrefix, namespaceUri);
	}

	/**
	 * @return A clone of this Concept object.
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
     * @return Returns the fractionItem.
     */
    public boolean isFractionItem() {
    	return fractionItem;
    }

    /**
     * @param fractionItem
     *            The fractionItem to set.
	 */
    public void setFractionItem(boolean fractionItem) {
        this.fractionItem = fractionItem;
    }

    /**
     * @return Returns the numericItem.
     */
	public boolean isNumericItem() {
    	return numericItem;
	}

	/**
	 * @param numericItem
	 *            The numericItem to set.
	 */
	public void setNumericItem(boolean numericItem) {
		this.numericItem = numericItem;
	}
}