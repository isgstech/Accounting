package xbrlcore.linkbase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import xbrlcore.constants.ExceptionConstants;
import xbrlcore.constants.GeneralConstants;
import xbrlcore.dimensions.Hypercube;
import xbrlcore.dimensions.MultipleDimensionType;
import xbrlcore.dimensions.Dimension;
import xbrlcore.exception.TaxonomyCreationException;
import xbrlcore.exception.XBRLException;
import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import xbrlcore.taxonomy.TaxonomySchema;
import xbrlcore.xlink.Arc;
import xbrlcore.xlink.ExtendedLinkElement;
import xbrlcore.xlink.Locator;

/**
 * This class represents the definition linkbase of a DTS. This is most
 * important for so called template taxonomies, where the definition linkbase
 * indicates whether a certain combination of dimension/domain member (a
 * Hypercube) is allowed for a specific primary item or not. <br/><br/>For more
 * information please see the XBRL Dimensions 1.0 Specification, which can be
 * obtained from http://www.xbrl.org/SpecRecommendations/. <br/><br/>
 * 
 * @author SantosEE
 *  
 */
public class DefinitionLinkbase extends Linkbase {

    static final long serialVersionUID = 3859185244715363584L;

    Set<Hypercube> hypercubeSet; /* set of Hypercube objects */

    Set<Concept> dimensionConceptSet; /* set of Concept objects */

    /**
     * 
     * @param dts
     *            The taxonomy this linkbase belongs to.
     */
    public DefinitionLinkbase(DiscoverableTaxonomySet dts) {
        super(dts);
        hypercubeSet = new HashSet<Hypercube>();
        dimensionConceptSet = new HashSet<Concept>();
    }

    /**
     * This method builds the definition linkbase. More detailed, this method
     * creates the according Dimension and Hypercube objects. For each
     * dimension, it determines the correct domain member network.
     * 
     * @throws TaxonomyCreationException
     *             This exception is thrown if a wrong substitutionGroup is
     *             detected or if a domain member network of an explicit
     *             dimension could not be determined.
     */
    public void buildDefinitionLinkbase() throws TaxonomyCreationException,
            XBRLException {
        /* first select all dimensional items */
        Set<Concept> dimensionConcepts = getDts().getConceptBySubstitutionGroup(
                GeneralConstants.XBRL_SUBST_GROUP_DIMENSION_ITEM);
        Iterator<Concept> dimensionConceptsIterator = dimensionConcepts.iterator();
        while (dimensionConceptsIterator.hasNext()) {
            Concept dimensionElement = dimensionConceptsIterator
                    .next();
            dimensionConceptSet.add(dimensionElement);
        }

        /* now select all the hypercube items */
        Set<Concept> hypercubeConcepts = getDts().getConceptBySubstitutionGroup(
                GeneralConstants.XBRL_SUBST_GROUP_HYPERCUBE_ITEM);
        Iterator<Concept> hypercubeConceptsIterator = hypercubeConcepts.iterator();
        while (hypercubeConceptsIterator.hasNext()) {
            Concept hypercubeConcept = hypercubeConceptsIterator
                    .next();
            hypercubeSet.add(new Hypercube(hypercubeConcept));
        }

        Hypercube currHypercube = null;
        /* go through all the extended link roles and collect the hypercubes */
        Set<String> extendedLinkRoles = getExtendedLinkRoles();
        Iterator<String> extendedLinkRolesIterator = extendedLinkRoles.iterator();
        while (extendedLinkRolesIterator.hasNext()) {
            String currExtendedLinkRole = extendedLinkRolesIterator
                    .next();
            List<Arc> hasHypercubeArcs = getArcBaseSet(
                    GeneralConstants.XBRL_ARCROLE_HYPERCUBE_DIMENSION,
                    currExtendedLinkRole);
            Iterator<Arc> hasHypercubeArcsIterator = hasHypercubeArcs.iterator();
            while (hasHypercubeArcsIterator.hasNext()) {
                Arc currArc = hasHypercubeArcsIterator.next();
                /* from is the hypercube, to is the dimension */
                /* check if it's still the same hypercube element */
                Concept currHypercubeConcept = ((Locator) currArc
                        .getSourceElement()).getConcept();
                if (!currHypercubeConcept.getSubstitutionGroup().equals(
                        GeneralConstants.XBRL_SUBST_GROUP_HYPERCUBE_ITEM)) {
                    /* wrong substitution group, throw exception */
                    throw new TaxonomyCreationException(
                            ExceptionConstants.EX_HYPERCUBE_ITEM_WRONG_SUBSTITUTION_GROUP);
                }
                currHypercube = getHypercube(currHypercubeConcept);

                Concept currDimensionElement = ((Locator) currArc
                        .getTargetElement()).getConcept();
                if (!currDimensionElement.getSubstitutionGroup().equals(
                        GeneralConstants.XBRL_SUBST_GROUP_DIMENSION_ITEM)) {
                    /* wrong substitution group, throw exception */
                    throw new TaxonomyCreationException(
                            ExceptionConstants.EX_DIM_ITEM_WRONG_SUBSTITUTION_GROUP);
                }

                dimensionConceptSet.add(currDimensionElement);
                Dimension currXBRLDimension = new Dimension(
                        currDimensionElement);

                /* check whether it is an explicit dimension */
                if (!currDimensionElement.isTypedDimension()) {
                    /* determine the domain member of this dimension */
                    /*
                     * this is the complete sublist below the current dimension
                     * element
                     */
                    Set<ExtendedLinkElement> domainMemberXLinkSet = null;
                    /*
                     * the link role depends on the xbrldt:targetRole of the
                     * currXArc
                     */
                    /*
                     * it might happen the sublist must be fetched from another
                     * extended link role
                     */
                    /*
                     * the arcrole is NULL since different arcroles must be
                     * taken into account (e.g. hypercube-dimension and
                     * domain-member)
                     */
                    if (currArc.getTargetRole() == null) {
                        domainMemberXLinkSet = buildTargetNetwork(
                                currDimensionElement, null,
                                currExtendedLinkRole);
                    } else {
                        domainMemberXLinkSet = buildTargetNetwork(
                                currDimensionElement, null, currArc
                                        .getTargetRole());
                    }
                    /*
                     * if domainMemberXLinkList is null, an exception should be
                     * thrown (this cannot happen in an explicit dimension)
                     */
                    if (domainMemberXLinkSet == null) {
                        throw new TaxonomyCreationException(
                                ExceptionConstants.EX_NO_DOMAIN_MEMBER_NETWORK);
                    }

                    /*
                     * the domain member network must not be null in an explicit
                     * dimension
                     */
                    currXBRLDimension.setDomainMemberSet(domainMemberXLinkSet);
                }
                currHypercube.addDimension(currXBRLDimension);
            }
        }
    }

    /**
     * This method returns a hypercube the given Concept object refers to.
     * 
     * @param concept
     *            The Concept object representing the hypercube.
     * @return The hypercube which is represented by the given Concept object.
     */
    public Hypercube getHypercube(Concept concept) {
        Iterator<Hypercube> hypercubeListIterator = hypercubeSet.iterator();
        while (hypercubeListIterator.hasNext()) {
            Hypercube currHypercubeElement = hypercubeListIterator
                    .next();
            if (currHypercubeElement.getConcept().equals(concept)) {
                return currHypercubeElement;
            }
        }
        return null;
    }

    /**
     * Indicates whether a certain primary item is allowed for a specific
     * combination of dimensions/domain member or not. This information is
     * essential to model the white and grey cells of the according templates.
     * 
     * @param primaryConcept
     *            The Concept object representing the primary element.
     * @param currDimensionCombination
     *            The MultipleDimensionType object representing the current
     *            dimension and domain member as well as all previous dimensions
     *            and domain member.
     * @param contextElement
     *            "scenario" or "segment", depending on the part of the context
     *            that shall be tested.
     * @return True if the combination is allowed for the primary item,
     *         otherwise false.
     */
    public boolean dimensionAllowed(Concept primaryConcept,
            MultipleDimensionType currDimensionCombination,
            String contextElement) throws CloneNotSupportedException,
            XBRLException {
        /* now check every extended link role */
        List<String> targetRoleList = new ArrayList<String>();
        targetRoleList.add(0, GeneralConstants.XBRL_ARCROLE_HYPERCUBE_ALL);
        targetRoleList.add(1, GeneralConstants.XBRL_ARCROLE_HYPERCUBE_NOTALL);
        Iterator<String> extendedLinkRoleIterator = getExtendedLinkRoles().iterator();

        nextExtendedLinkRole: while (extendedLinkRoleIterator.hasNext()) {
            String currentExtendedLinkRole = extendedLinkRoleIterator.next();

            /* get all ..all and ..notAll links in the current link role */
            List<Arc> arcList = getArcBaseSet(targetRoleList, currentExtendedLinkRole);
            Iterator<Arc> arcListIterator = arcList.iterator();

            Map<Hypercube, String> hyperCubeMap = new HashMap<Hypercube, String>();

            while (arcListIterator.hasNext()) {
                Arc currentArc = arcListIterator.next();

                if (!currentArc.getContextElement().equals(contextElement))
                    continue;

                ExtendedLinkElement fromElement = currentArc.getSourceElement();
                ExtendedLinkElement toElement = currentArc.getTargetElement();

                boolean primaryItemInDomainMember = false;

                if (((Locator) fromElement).getConcept().equals(primaryConcept)) {
                    primaryItemInDomainMember = true;
                } else {
                    Set<ExtendedLinkElement> domainMemberNetwork = buildTargetNetwork(
                            ((Locator) fromElement).getConcept(),
                            "http://xbrl.org/int/dim/arcrole/domain-member",
                            currentExtendedLinkRole);
                    Iterator<ExtendedLinkElement> domainMemberNetworkIterator = domainMemberNetwork.iterator();
                    while (domainMemberNetworkIterator.hasNext()) {
                        ExtendedLinkElement currentDomainMember = domainMemberNetworkIterator
                                .next();
                        if (currentDomainMember.isLocator()
                                && ((Locator) currentDomainMember).getConcept()
                                        .equals(primaryConcept)) {
                            primaryItemInDomainMember = true;
                            break;
                        }
                    }
                }

                if (primaryItemInDomainMember) {
                    Concept toConcept = ((Locator) toElement).getConcept();
                    Hypercube currCube = getHypercube(toConcept);
                    hyperCubeMap.put(currCube, currentArc.getArcrole());
                }
            }

            Hypercube relevantHypercube = new Hypercube(null);

            Set<Entry<Hypercube, String>> hyperCubeEntrySet = hyperCubeMap.entrySet();
            Iterator<Entry<Hypercube, String>> hyperCubeIterator = hyperCubeEntrySet.iterator();
            while (hyperCubeIterator.hasNext()) {
                Map.Entry<Hypercube, String> currEntry = hyperCubeIterator.next();
                Hypercube currCube = (Hypercube) currEntry.getKey();
                String currArcrole = (String) currEntry.getValue();
                if (currCube.getConcept().getId().equals(
                        "d-cm-report_ReportHypercube"))
                    continue;

                if (currArcrole
                        .equals(GeneralConstants.XBRL_ARCROLE_HYPERCUBE_ALL)) {
                    relevantHypercube.addHypercube(currCube);
                } else if (currArcrole
                        .equals(GeneralConstants.XBRL_ARCROLE_HYPERCUBE_NOTALL)
                        && currCube
                                .hasDimensionDomainCombination(currDimensionCombination)) {
                    continue nextExtendedLinkRole;
                }
            }

            /*
             * now check relevant hypercube whether it contains all
             * dimensions/domain members of currDimensionCombination
             */
            if (!relevantHypercube
                    .hasDimensionDomainCombination(currDimensionCombination)) {
                continue nextExtendedLinkRole;
            }

            /* if we are here, we can return true */
            return true;

        }
        /* all extended link roles have been checked */
        return false;
    }

    /**
     * dimensionElement is a dimension element of substitution group
     * xbrldt:dimensionItem. The method determines the according dimension
     * taxonomy. There can be several dimension taxonomies, since one element
     * can be comprised of multiple dimensions (see COREP taxonomy t-cs for an
     * example).
     * 
     * @return A set with TaxonomySchema objects of which the dimension
     *         represented by dimensionElement is comprised.
     */
    public Set<TaxonomySchema> getDimensionTaxonomy(Concept dimensionElement) {
        /*
         * Now the dimension has to be determined. This can be done by searching
         * *any* extended link role of the definition linkbase. dimensionElement
         * must be the "from" element, the arcrole must be dimension-domain and
         * the "to" element belongs to the dimension
         */

        if (!dimensionElement.getSubstitutionGroup().equals(
                GeneralConstants.XBRL_SUBST_GROUP_DIMENSION_ITEM)) {
            return null;
        }

        Set<TaxonomySchema> resultSet = new HashSet<TaxonomySchema>();

        Iterator<String> extendedLinkRoleIterator = getExtendedLinkRoles().iterator();
        while (extendedLinkRoleIterator.hasNext()) {
            String currExtendedLinkRole = extendedLinkRoleIterator
                    .next();
            List<Arc> xArcList = getArcBaseSet(
                    GeneralConstants.XBRL_ARCROLE_DIMENSION_DOMAIN,
                    currExtendedLinkRole);
            Iterator<Arc> xArcListIterator = xArcList.iterator();
            while (xArcListIterator.hasNext()) {
                Arc currXArc = xArcListIterator.next();
                if (currXArc.getSourceElement().isLocator()
                        && currXArc.getTargetElement().isLocator()) {
                    Concept sourceConcept = ((Locator) currXArc
                            .getSourceElement()).getConcept();
                    Concept targetConcept = ((Locator) currXArc
                            .getTargetElement()).getConcept();

                    if (sourceConcept.equals(dimensionElement)) {
                        resultSet.add(getDts().getTaxonomySchema(
                                targetConcept.getTaxonomySchemaName()));
                    }
                }
            }
        }

        return resultSet;
    }

    /**
     * This method returns the dimension element (substitutionGroup
     * xbrldt:dimensionItem) to a given domain element by parsing the
     * Hypercubes.
     * 
     * @param domainElement
     *            Domain element for which the dimension element is searched.
     * @return The dimension element if it is found, null otherwise.
     */
    public Concept getDimensionElement(Concept domainElement) {
        if (domainElement == null)
            return null;
        Iterator<Hypercube> hypercubeIterator = hypercubeSet.iterator();
        while (hypercubeIterator.hasNext()) {
            Hypercube currHypercube = hypercubeIterator.next();
            Set<Dimension> dimensions = currHypercube.getDimensionSet();
            Iterator<Dimension> dimensionsIterator = dimensions.iterator();
            while (dimensionsIterator.hasNext()) {
                Dimension currDimension = (Dimension) dimensionsIterator.next();
                if (!currDimension.isTyped()
                        && currHypercube.containsDimensionDomain(currDimension
                                .getConcept(), domainElement)) {
                    return currDimension.getConcept();
                }
            }
        }
        return null;
    }

    /**
     * This method checks whether a specific concept is a *usable* domain member
     * to a specific dimension (not taking a specific hypercube into account) in
     * a hypercube which is an ALL-Hypercube within any extended link role.
     * 
     * @param dimensionConcept
     *            Possible domain member.
     * @param domainMemberConcept
     *            Dimension the possible domain member belongs to.
     * @return True if domainMemberConcept is a domain member of
     *         dimensionConcept.
     */
    public boolean isUsableDomainMemberOfDimension(Concept dimensionConcept,
            Concept domainMemberConcept) {
        Set<Hypercube> hypercubeSet = getHypercubeSet();
        Iterator<Hypercube> hypercubeIterator = hypercubeSet.iterator();
        while (hypercubeIterator.hasNext()) {
            Hypercube currHypercube = hypercubeIterator.next();
            if (currHypercube.containsUsableDimensionDomain(dimensionConcept,
                    domainMemberConcept)) {
                /*
                 * currHypercube must be an ALL-Hypercube within any extended
                 * link role
                 */
                Iterator<String> extendedLinkRoleIterator = getExtendedLinkRoles()
                        .iterator();
                while (extendedLinkRoleIterator.hasNext()) {
                    String currExtendedLinkRole = (String) extendedLinkRoleIterator
                            .next();
                    List<Arc> arcBaseSet = getArcBaseSet(
                            GeneralConstants.XBRL_ARCROLE_HYPERCUBE_ALL,
                            currExtendedLinkRole);
                    for (int j = 0; j < arcBaseSet.size(); j++) {
                        Arc currArc = (Arc) arcBaseSet.get(j);
                        if (currArc.getTargetElement().isLocator()
                                && ((Locator) currArc.getTargetElement())
                                        .getConcept().equals(
                                                currHypercube.getConcept())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 
     * @return Returns all hypercubes (represented by elements with the
     *         substitution group xbrldt:hypercubeItem) of this definition
     *         linkbase which contain at least one typed dimension. The Set
     *         contains Hypercube objects.
     */
    public Set<Hypercube> getTypedDimensionHypercubeSet() {
        Set<Hypercube> typedDimHypercubeSet = new HashSet<Hypercube>();
        Iterator<Hypercube> hypercubeSetIterator = hypercubeSet.iterator();
        while (hypercubeSetIterator.hasNext()) {
            Hypercube currHypercube = hypercubeSetIterator.next();
            if (currHypercube.containsTypedDimension()) {
                typedDimHypercubeSet.add(currHypercube);
            }
        }
        return typedDimHypercubeSet;
    }

    /**
     * @return Returns all dimension elements (substitution group
     *         xbrldt:dimensionItem) of this definition linkbase. The set
     *         contains Concept objects.
     */
    public Set<Concept> getDimensionConceptSet() {
        return dimensionConceptSet;
    }

    /**
     * @return Returns all hypercubes (represented by elements with the
     *         substitution group xbrldt:hypercubeItem) of this definition
     *         linkbase. The Set contains Hypercube objects.
     */
    public Set<Hypercube> getHypercubeSet() {
        return hypercubeSet;
    }
}
