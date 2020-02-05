package xbrlcore.instance;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jdom.Namespace;

import xbrlcore.constants.ExceptionConstants;
import xbrlcore.constants.NamespaceConstants;
import xbrlcore.dimensions.MultipleDimensionType;
import xbrlcore.exception.InstanceException;
import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import xbrlcore.taxonomy.TaxonomySchema;

/**
 * This class represents an XBRL instance document as it is described in the
 * XBRL 2.1 Specification, which can be obtained from
 * http://www.xbrl.org/SpecRecommendations/ <br/><br/>
 * 
 * @author SantosEE
 */

public class Instance implements Serializable {

	static final long serialVersionUID = -738607935995987975L;

	private String fileName;

	private String comment;

	private Set<DiscoverableTaxonomySet> setDts; /* all DiscoverableTaxonomySet objects this instance refers to */

	private Set<Fact> factSet; /* all facts contained in this instance */

    private Set<Tuple> tupleSet;

	private Map<String, InstanceContext> contextMap;

	private Map<String, InstanceUnit> unitMap;

	private Map<String, String> schemaLocationMap;

	private transient Set<Namespace> additionalNamespaceSet;

	private transient Namespace instanceNamespace;

	/**
	 * Constructor.
	 * 
	 * @param discoverableTaxonomySet
	 *            Set of discoverable taxonomy sets this instance refers to.
	 */
	/*
	 * TODO: Currently the library is not able to determine the DTS of an
	 * instance document as it is described in the Spec. This should be changed
	 * in later versions! Instead, all taxonomies the instance refers to are
	 * represented in a java.util.Set object.
	 */
	public Instance(Set<DiscoverableTaxonomySet> discoverableTaxonomySet) {
		setDts = discoverableTaxonomySet;

		/* set instance namespace (can be changed) */
		instanceNamespace = NamespaceConstants.XBRLI_NAMESPACE;

		/* initialise some objects */
		factSet = new HashSet<Fact>();
        tupleSet = new HashSet<Tuple>();
		additionalNamespaceSet = new HashSet<Namespace>();
		schemaLocationMap = new HashMap<String, String>();
		contextMap = new HashMap<String, InstanceContext>();
		unitMap = new HashMap<String, InstanceUnit>();
	}

	/**
	 * This method adds a discoverable taxonomy set and all its namespaces to
	 * this instance object.
	 * 
	 * @param dts
	 */
	public void addDiscoverableTaxonomySet(DiscoverableTaxonomySet dts) {
		setDts.add(dts);
		addNamespacesOfDts(dts);
	}

	/**
	 * Adds namespaces from all imported taxonomies to this Instance object.
	 *  
	 */
	public void setTaxonomyNamespaces() {
		/* set namespaces for the imported taxonomies */
		Iterator<DiscoverableTaxonomySet> setDtsIterator = setDts.iterator();
		while (setDtsIterator.hasNext()) {
			DiscoverableTaxonomySet currDts = setDtsIterator
					.next();
			addNamespacesOfDts(currDts);
		}
	}

	/**
	 * Adds namespaces of all taxonomies belonging to a specific DTS to this
	 * instance.
	 * 
	 * @param dts
	 *            Discoverable taxonomy set of which namespaces are added.
	 */
	private void addNamespacesOfDts(DiscoverableTaxonomySet dts) {
		Map<String, TaxonomySchema> taxonomiesMap = dts.getTaxonomyMap();
		Set<Entry<String, TaxonomySchema>> taxonomiesEntrySet = taxonomiesMap.entrySet();
		Iterator<Entry<String, TaxonomySchema>> taxonomiesIterator = taxonomiesEntrySet.iterator();
		while (taxonomiesIterator.hasNext()) {
			Map.Entry<String, TaxonomySchema> currEntry = taxonomiesIterator.next();
			TaxonomySchema currTaxSchema = currEntry.getValue();
			additionalNamespaceSet.add(currTaxSchema.getNamespace());
		}
	}

	/**
	 * Adds a fact to the instance. This method only works if either the context
	 * this fact refers to is already available or if this fact refers to a
	 * context at all. In the latter case, the context is added to the instance.
	 * 
	 * @param newFact
	 *            The fact which is added to the instance.
	 * @throws InstanceException
	 *             This exception is thrown if the fact does not refer to a
	 *             context. Also, the exception is thrown if some information in
	 *             the fact or the context is missing.
	 */
	public void addFact(Fact newFact) throws InstanceException {
		/* test the fact, its context and its unit (unit only when it's set) */
		if (testFact(newFact)
				&& testContext(newFact.getInstanceContext())
				&& (newFact.getInstanceUnit() == null || testUnit(newFact
						.getInstanceUnit()))) {
			/*
			 * Now check if this fact is already reported for this unit and
			 * context. If yes, the value is overriden.
			 */
			Fact tmpFact = getFact(newFact.getConcept(), newFact
					.getInstanceContext());
			if (tmpFact != null) {
				tmpFact.setValue(newFact.getValue());
			} else {
				addContext(newFact.getInstanceContext());
				if (newFact.getInstanceUnit() != null) {
					unitMap.put(newFact.getInstanceUnit().getId(), newFact
							.getInstanceUnit());
				}
				factSet.add(newFact);
			}
		}
	}

	/**
     * Adds a tuple to the instance. 
     * 
     * @param newTuple
     *            The tuple which is added to the instance.
     */
    public void addTuple(Tuple newTuple) {
        tupleSet.add(newTuple);
    }

    /**
	 * Adds a new context to the instance. Each context with the same ID can
	 * only be added once to an instance, an exception is thrown if newContext
	 * is already part of this instance.
	 * 
	 * @param newContext
	 *            The context which is added to the instance.
	 * @throws InstanceException
	 *             This exception is thrown if a context with that ID is already
	 *             part of the instance or if some information within the
	 *             context is missing.
	 */
	public void addContext(InstanceContext newContext) throws InstanceException {

		InstanceContext tmpCtx = contextMap.get(newContext
				.getId());
		if (tmpCtx != null) {
			if (!(tmpCtx.equals(newContext)))
				throw new InstanceException(
						ExceptionConstants.EX_INSTANCE_DOUBLE_CONTEXT);
			else
				return; /* context already available */
		} else {
			if (testContext(newContext)) {
				contextMap.put(newContext.getId(), newContext);
			} else
				throw new InstanceException(
						ExceptionConstants.EX_INSTANCE_CONTEXT_INFORMATION_MISSING);
		}
	}

	/**
	 * Adds a new unit to the instance. Each unit with the same ID can only be
	 * added once to an instance, an exception is thrown if newUnit is already
	 * part of this instance.
	 * 
	 * @param newUnit
	 *            The unit which is added to the instance.
	 * @throws InstanceException
	 *             This exception is thrown if a unit with that ID is already
	 *             part of the instance or if some information within the unit
	 *             is missing.
	 */
	public void addUnit(InstanceUnit newUnit) throws InstanceException {

		InstanceUnit tmpUnit = unitMap.get(newUnit.getId());
		if (tmpUnit != null) {
			if (!(tmpUnit.equals(newUnit)))
				throw new InstanceException(
						ExceptionConstants.EX_INSTANCE_DOUBLE_UNIT);
			else
				return; /* unit already available */
		} else {
			if (testUnit(newUnit)) {
				unitMap.put(newUnit.getId(), newUnit);
			} else
				throw new InstanceException(
						ExceptionConstants.EX_INSTANCE_UNIT_INFORMATION_MISSING);
		}
	}

	/**
	 * Returns a context with a specific ID. If the context is not available,
	 * NULL is returned.
	 * 
	 * @param id
	 *            ID of the context.
	 * @return InstanceContext object if available, otherwise NULL.
	 */
	public InstanceContext getContext(String id) {
		return contextMap.get(id);
	}

	/**
	 * Returns a unit with a specific ID. If the unit is not available, NULL is
	 * returned.
	 * 
	 * @param id
	 *            ID of the unit.
	 * @return InstanceUnit object if available, otherwise NULL.
	 */
	public InstanceUnit getUnit(String id) {
		return unitMap.get(id);
	}

	/**
	 * Returns a list of Facts which refer to a certain context.
	 * 
	 * @param context_id
	 *            ID of the context the returned facts refer to.
	 * @return List of facts which refer to a certain context.
	 */
	public Set<Fact> getFactsForContext(String context_id) {
		Set<Fact> contextFactSet = new HashSet<Fact>();
		Iterator<Fact> factListIterator = factSet.iterator();
		while (factListIterator.hasNext()) {
			Fact currFact = factListIterator.next();
			if (currFact.getInstanceContext().getId().equals(context_id)) {
				contextFactSet.add(currFact);
			}
		}
		return contextFactSet;
	}

	/**
	 * Returns the fact for a specific concept and a context.
	 * 
	 * @param primaryElement
	 *            The primary element the fact refers to.
	 * @param ctx
	 *            The specific context the fact refers to.
	 * @return The according reported fact.
	 */
	public Fact getFact(Concept primaryElement, InstanceContext ctx) {
		Iterator<Fact> factListIterator = factSet.iterator();
		while (factListIterator.hasNext()) {
			Fact currFact = factListIterator.next();
			if (currFact.getConcept().equals(primaryElement)) {
				if (currFact.getInstanceContext().equals(ctx)) {
					return currFact;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the fact for a specific concept and a dimension/domain
	 * combination this fact is reported for.
	 * 
	 * @param primaryElement
	 *            The primary element the fact refers to.
	 * @param mdt
	 *            Dimension/domain combination the fact is reported for.
	 * @param scen_seg
	 *            Determines whether the dimensional information of the
	 *            <scenario>or <segment>element of the context is taken. To
	 *            specify them, please use the constants DIM_SCENARIO and
	 *            DIM_SEGMENT of xbrlcore.constants.GeneralConstants.
	 * @return The according reported fact.
	 */
	public Fact getFact(Concept primaryElement, MultipleDimensionType mdt,
			int scen_seg) {
		Iterator<Fact> factListIterator = factSet.iterator();
		while (factListIterator.hasNext()) {
			Fact currFact = factListIterator.next();
			if (currFact.getConcept().equals(primaryElement)) {

				MultipleDimensionType tmpMDT = currFact.getInstanceContext()
						.getDimensionalInformation(scen_seg);
				if (mdt != null && tmpMDT != null) {
					if (tmpMDT.equals(mdt)) {
						return currFact;
					}
				} else if (mdt == null && tmpMDT == null) {
					return currFact;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the context for a specific dimension/domain combination which is
	 * part of that context.
	 * 
	 * @param mdt
	 *            Dimension/domain combination of the requested context.
	 * @param scen_seg
	 *            Determines whether the dimensional information of the
	 *            <scenario>or <segment>element of the context is taken. To
	 *            specify them, please use the constants DIM_SCENARIO and
	 *            DIM_SEGMENT of xbrlcore.constants.GeneralConstants.
	 * @return The according context.
	 */
	public InstanceContext getContext(MultipleDimensionType mdt, int scen_seg) {
		if (mdt == null)
			return null;
		Iterator<String> contextIterator = contextMap.keySet().iterator();
		while (contextIterator.hasNext()) {
			String currID = contextIterator.next();
			InstanceContext currCtx = contextMap.get(currID);
			if (currCtx.getDimensionalInformation(scen_seg) != null
					&& currCtx.getDimensionalInformation(scen_seg).equals(mdt)) {
				return currCtx;
			}
		}
		return null;
	}

	/**
	 * Removes a fact from the instance.
	 * 
	 * @param factToRemove
	 *            Fact which shall be removed.
	 */
	public void removeFact(Fact factToRemove) {
		factSet.remove(factToRemove);
	}

	/**
	 * Removes a fact from the instance.
	 * 
	 * @param primaryElement
	 *            The primary element the fact refers to.
	 * @param mdt
	 *            Dimension/domain combination the fact is reported for.
	 * @param scen_seg
	 *            Determines whether the dimensional information of the
	 *            <scenario>or <segment>element are taken. To specify them,
	 *            please use the constants DIM_SCENARIO and DIM_SEGMENT of
	 *            xbrlcore.constants.GeneralConstants.
	 */
	public void removeFact(Concept primaryElement, MultipleDimensionType mdt,
			int scen_seg) {
		removeFact(getFact(primaryElement, mdt, scen_seg));
	}

	/**
	 * Removes a context (and according facts) from the instance.
	 * 
	 * @param contextToRemove
	 *            Context that is removed.
	 */
	public void removeContext(InstanceContext contextToRemove) {
		Set<Fact> factsToRemoveSet = new HashSet<Fact>();
		Iterator<Fact> factIterator = factSet.iterator();
		/* collect facts to remove */
		while (factIterator.hasNext()) {
			Fact currFact = factIterator.next();
			if (currFact.getInstanceContext().equals(contextToRemove)) {
				factsToRemoveSet.add(currFact);
			}
		}
		/* remove the facts */
		Iterator<Fact> factsToRemoveIterator = factsToRemoveSet.iterator();
		while (factsToRemoveIterator.hasNext()) {
			Fact currFact = factsToRemoveIterator.next();
			removeFact(currFact);
		}
		/* remove the context */
		contextMap.remove(contextToRemove.getId());
	}

	/**
	 * Returns the URI of a certain namespace prefix.
	 * 
	 * @param namespacePrefix
	 *            The namespace prefix which the URI is desired for.
	 * @return The according namespace URI to the given prefix.
	 */
	public String getNamespaceURI(String namespacePrefix) {
		if (instanceNamespace.getPrefix().equals(namespacePrefix)) {
			return instanceNamespace.getURI();
		}
		Iterator<Namespace> additionalNamespaceListIterator = additionalNamespaceSet.iterator();
		while (additionalNamespaceListIterator.hasNext()) {
			Namespace currNamespace = additionalNamespaceListIterator.next();
			if (currNamespace.getPrefix().equals(namespacePrefix)) {
				return currNamespace.getURI();
			}
		}
		return null;
	}

	/**
	 * 
	 * @return Number of facts of this instance.
	 */
	public int getNumberOfFacts() {
		return factSet.size();
	}

	/**
	 * 
	 * @return Number of contexts of this instance.
	 */
	public int getNumberOfContexts() {
		return contextMap.size();
	}

	/**
	 * Checks whether all information is available within a fact. <br/>This
	 * means: <br/>- The value must not be null and must not have a length of 0.
	 * <br/>- The fact must refer to a context.
	 */
	/* TODO: Numeric items must have a unit! */
	private boolean testFact(Fact fact) throws InstanceException {
		if (fact == null) {
			throw new InstanceException(
					ExceptionConstants.EX_INSTANCE_FACT_INFORMATION_MISSING);
		}
        if(!fact.getConcept().isNillable()) {
	        if (fact.getValue() == null) {
				throw new InstanceException(
					ExceptionConstants.EX_INSTANCE_FACT_INFORMATION_MISSING);
			}
        }
		if (fact.getInstanceContext() == null) {
			throw new InstanceException(
					ExceptionConstants.EX_INSTANCE_FACT_INFORMATION_MISSING);
		}

        if (fact.getConcept().isNumericItem() && fact.getInstanceUnit() == null) {
        	throw new InstanceException(ExceptionConstants.EX_INSTANCE_FACT_UNIT_MISSING);
        }

		return true;
	}

	/**
	 * Checks whether all information is available within a context. <br/>This
	 * means: <br/>- The ID must not be null and must not have a length of 0.
	 * <br/>- The identifier element and scheme attribute must not be null and
	 * must not have a length of 0. <br/>- The period element must not be null
	 * and must not have a length of 0.<br/>
	 */
	private boolean testContext(InstanceContext context)
			throws InstanceException {
		if (context == null) {
			throw new InstanceException(
					ExceptionConstants.EX_INSTANCE_CONTEXT_INFORMATION_MISSING);
		}
		if (context.getId() == null || context.getId().length() == 0) {
			throw new InstanceException(
					ExceptionConstants.EX_INSTANCE_CONTEXT_INFORMATION_MISSING);
		}
		if (context.getIdentifier() == null
				|| context.getIdentifier().length() == 0
				|| context.getIdentifierScheme() == null
				|| context.getIdentifierScheme().length() == 0) {
			throw new InstanceException(
					ExceptionConstants.EX_INSTANCE_CONTEXT_INFORMATION_MISSING);
		}
		if ((context.getPeriodValue() == null || context.getPeriodValue()
				.length() == 0)
				&& (context.getPeriodStartDate() == null || context
						.getPeriodEndDate() == null)) {
			throw new InstanceException(
					ExceptionConstants.EX_INSTANCE_CONTEXT_INFORMATION_MISSING);
		}
		return true;
	}

	/**
	 * Checks whether all information is available within a unit. <br/>This
	 * means: <br/>- The ID must not be null and must not have a length of 0.
	 * <br/>- The value must not be null and must not have a length of 0. <br/>-
	 * The namespace URI must not be null and must not have a length of 0.<br/>
	 */
	private boolean testUnit(InstanceUnit unit) throws InstanceException {
		if (unit == null) {
			throw new InstanceException(
					ExceptionConstants.EX_INSTANCE_UNIT_INFORMATION_MISSING);
		}
		if (unit.getId() == null || unit.getId().length() == 0) {
			throw new InstanceException(
					ExceptionConstants.EX_INSTANCE_UNIT_INFORMATION_MISSING);
		}
		if (unit.getNamespaceURI() == null
				|| unit.getNamespaceURI().length() == 0) {
			throw new InstanceException(
					ExceptionConstants.EX_INSTANCE_UNIT_INFORMATION_MISSING);
		}
		if (unit.getValue() == null || unit.getValue().length() == 0) {
			throw new InstanceException(
					ExceptionConstants.EX_INSTANCE_UNIT_INFORMATION_MISSING);
		}

		return true;
	}

	/**
	 * Adds a namespace to the namespace list.
	 * 
	 * @param namespace_prefix
	 *            Prefix of the new namespace.
	 * @param namespace_uri
	 *            URI of the new namespace.
	 */
	public void addNamespace(String namespace_prefix, String namespace_uri) {
		Namespace newNamespace = Namespace.getNamespace(namespace_prefix,
				namespace_uri);
		if (additionalNamespaceSet == null)
			additionalNamespaceSet = new HashSet<Namespace>();
		additionalNamespaceSet.add(newNamespace);
	}

	/**
	 * Adds a namespace to the namespace list
	 * 
	 * @param newNamespace
	 *            New Namespace.
	 */
	public void addNamespace(Namespace newNamespace) {
		if (additionalNamespaceSet == null)
			additionalNamespaceSet = new HashSet<Namespace>();
		additionalNamespaceSet.add(newNamespace);
	}

	/**
	 * This method gets a namespace according to a specific URI.
	 * 
	 * @param namespaceURI
	 *            The URI of the namespace.
	 * @return The namespace which belongs to the specific schema file.
	 */
	public Namespace getNamespace(String namespaceURI) {
		if (instanceNamespace.getURI().equals(namespaceURI)) {
			return instanceNamespace;
		}
		Iterator<Namespace> additionalNamespaceIterator = additionalNamespaceSet.iterator();
		while (additionalNamespaceIterator.hasNext()) {
			Namespace currNamespace = additionalNamespaceIterator.next();
			if (currNamespace.getURI().equals(namespaceURI)) {
				return currNamespace;
			}
		}
		return null;
	}

	/**
	 * This method returns the correct taxonomy for a specific namespace. So the
	 * taxonomy is returned which has a targetNamespace of ns.
	 * 
	 * @param ns
	 *            The namespace for which the correct schema is returned.
	 * @return Returns the according taxonomy schema which describes a specific
	 *         (target)namespace.
	 */
	/* TODO: Unit test missing! */
	public TaxonomySchema getSchemaForURI(Namespace ns) {
		if (ns == null) {
			return null;
		}
		Iterator<DiscoverableTaxonomySet> dtsSetIterator = setDts.iterator();
		while (dtsSetIterator.hasNext()) {
			DiscoverableTaxonomySet currDts = dtsSetIterator
					.next();
			Map<String, TaxonomySchema> taxonomyMap = currDts.getTaxonomyMap();
			Iterator<Entry<String, TaxonomySchema>> taxonomyEntrySetIterator = taxonomyMap.entrySet()
					.iterator();
			while (taxonomyEntrySetIterator.hasNext()) {
				Map.Entry<String, TaxonomySchema> currEntry = taxonomyEntrySetIterator.next();
				TaxonomySchema currTaxonomy = currEntry.getValue();
				if (currTaxonomy.getNamespace().equals(ns)) {
					return currTaxonomy;
				}
			}
		}

		return null;
	}

	/**
	 * Adds a new schema location to the instance.
	 * 
	 * @param schemaURI
	 *            URI of the new schema location.
	 * @param schemaName
	 *            Name of the new schema location, that is the name of the
	 *            schema file.
	 */
	/* TODO: Check if/why we need schema locations in an instance file */
	public void addSchemaLocation(String schemaURI, String schemaName) {
		schemaLocationMap.put(schemaURI, schemaName);
	}

	/**
	 * @return Set with all the contexts in this instance (key - ID of the
	 *         context, value - according InstanceContext object).
	 */
	public Map<String, InstanceContext> getContextMap() {
		return contextMap;
	}

	/**
	 * @return List with all the facts in this instance.
	 */
	public Set<Fact> getFactSet() {
		return factSet;
	}

	/**
     * @return List with all the tuples in this instance.
     */
    public Set<Tuple> getTupleSet() {
        return tupleSet;
    }


    /**
	 * @return All taxonomies this instance refers to.
	 */
	public Set<DiscoverableTaxonomySet> getDiscoverableTaxonomySet() {
		return setDts;
	}

	/**
	 * @return Set with all the additional namespaces of the instance document.
	 *         This is a list of JDOM Namespace objects.
	 */
	public Set<Namespace> getAdditionalNamespaceSet() {
		return additionalNamespaceSet;
	}

	/**
	 * @return Namespace of the instance document.
	 */
	public Namespace getInstanceNamespace() {
		return instanceNamespace;
	}

	/**
	 * @param namespace
	 */
	public void setInstanceNamespace(Namespace namespace) {
		instanceNamespace = namespace;
	}

	/**
	 * @return Map which contains schema location information. Key is the schema
	 *         URI, value is the schema name.
	 */
	public Map<String, String> getSchemaLocationMap() {
		return schemaLocationMap;
	}

	/**
	 * @return Map which contains unit information. Key is the ID of the unit,
	 *         value is the according xbrlcore.instance.InstanceUnit object.
	 */
	public Map<String, InstanceUnit> getUnitMap() {
		return unitMap;
	}

	/**
	 * @return Returns the fileName.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            The fileName to set.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return Returns the comment.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            The comment to set.
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
}