package xbrlcore.dimensions;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jdom.Element;

import xbrlcore.taxonomy.Concept;
import xbrlcore.xlink.ExtendedLinkElement;
import xbrlcore.xlink.Locator;

/**
 * This class encapsulates an XBRL Dimension as it is described by the
 * Dimensions 1.0 Specification which can be obtained from
 * http://www.xbrl.org/SpecRecommendations/. <br/><br/>Therefore a dimension
 * consists of one XBRL concept representing it as well as one or more domains
 * and a set of domain members. <br/><br/>Currently this class does not
 * distinguish between a "domain" and a "domain member", so the "domain" itself
 * is part of the "domain member set".
 * 
 * @author SantosEE
 */
public class Dimension implements Serializable, Cloneable {

    static final long serialVersionUID = 3163965042260005456L;

    private Concept concept;

    private Set<ExtendedLinkElement> domainMemberSet;

    private Element typedElement;

    /**
     * Constructor.
     * 
     * @param concept
     *            The XBRL concept representing this dimension.
     */
    public Dimension(Concept concept) {
        this.concept = concept;
        domainMemberSet = new HashSet<ExtendedLinkElement>();
    }

    /**
     * Indicates whether this dimension contains a certain domain member. It can
     * be specified whether the domain member must have the xbrldt:usable
     * attribute set to "true" (or not set at all) or not.
     * 
     * @param domainMember
     *            Concept representing the certain domain member
     * @param usable
     *            Indicates whether the xbrldt:usable attribute shall be taken
     *            into account. True if yes, false if no.
     * @return If usable is set to true, the method returns true if and only if
     *         the domain member is part of that dimension and has no
     *         xbrldt:usable attribute set to false (or no xbrldt:usable
     *         attribute at all). If usable is set to false, the method also
     *         returns true if the domain member has an xbrldt:usable attribute
     *         set to false.
     */
    public boolean containsDomainMember(Concept domainMember, boolean usable) {
        Iterator<ExtendedLinkElement> domainMemberListIterator = domainMemberSet.iterator();
        while (domainMemberListIterator.hasNext()) {
            ExtendedLinkElement currXLinkElement = domainMemberListIterator.next();
            if (currXLinkElement.isLocator()) {
                Locator currLoc = (Locator) currXLinkElement;
                if (currLoc.getConcept().equals(domainMember)) {
                    return usable ? currLoc.isUsable() : true;
                }
            }
        }
        return false;
    }

    /**
     * This method tests for "equality" between two Dimension objects. They are
     * equal if: <br/>- both dimensions are either explicit nor typed <br/>-
     * the concept which represents the dimensions are equal <br/>- the domain
     * member set of both Dimension objects are equal <br/>- if both Dimension
     * objects are representing typed dimensions, the schema element describing
     * these dimensions must be equal
     * 
     * @return True if both objects are equal, otherwise false.
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Dimension))
            return false;
        Dimension otherDim = (Dimension) obj;
        return isTyped() == otherDim.isTyped()
                && concept == otherDim.getConcept()
                && domainMemberSet.equals(otherDim.getDomainMemberSet())
                && (typedElement == null ? otherDim.getTypedElement() == null
                        : typedElement.equals(otherDim.getTypedElement()));
    }

    /**
     * @return A hash code of this object.
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + (isTyped() ? 1 : 0);
        hash = hash * 31 + concept.hashCode();
        hash = hash * 31 + domainMemberSet.hashCode();
        hash = hash * 31 + (typedElement != null ? typedElement.hashCode() : 0);
        return hash;
    }

    /**
     * @return A clone of the current Dimension object.
     */
    public Dimension clone() throws CloneNotSupportedException {
        Dimension d = (Dimension) super.clone();
        d.setConcept((Concept) concept.clone());
        d.setDomainMemberSet((Set<ExtendedLinkElement>) ((HashSet<ExtendedLinkElement>) domainMemberSet).clone());
        if (typedElement != null) {
            d.setTypedElement((Element) typedElement.clone());
        }
        return d;
    }

    /**
     * @return The Concept object representing the dimension.
     */
    public Concept getConcept() {
        return concept;
    }

    /**
     * @return True if the dimension is a typed dimension, otherwise false.
     */
    public boolean isTyped() {
        return concept.isTypedDimension();
    }

    /**
     * @return If the dimension is a typed dimension, the schema element
     *         representing the domain type is returned.
     */
    public Element getTypedElement() {
        return typedElement;
    }

    /**
     * @param element
     *            The schema element representing the domain type of this
     *            dimension.
     */
    public void setTypedElement(Element element) {
        typedElement = element;
    }

    /**
     * @param set
     *            Set of domain member, these must be
     *            xbrlcore.xlink.ExtendedLinkElement objects.
     */
    public void setDomainMemberSet(Set<ExtendedLinkElement> set) {
        domainMemberSet = set;
    }

    /**
     * @return Set of domain member, this is a list of
     *         xbrlcore.xlink.ExtendedLinkElement objects.
     */
    public Set<ExtendedLinkElement> getDomainMemberSet() {
        return domainMemberSet;
    }

    public void addDomainMemberSet(Set<ExtendedLinkElement> set) {
        domainMemberSet.addAll(set);
    }

    public void removeDomainMemberSet(Set<ExtendedLinkElement> set) {
        domainMemberSet.removeAll(set);
    }

    /**
     * @param concept
     *            The concept to set.
     */
    public void setConcept(Concept concept) {
        this.concept = concept;
    }
}
