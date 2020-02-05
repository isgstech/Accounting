package TaxonomyBrowser;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;

import xbrlcore.dimensions.Dimension;
import xbrlcore.dimensions.Hypercube;
import xbrlcore.linkbase.CalculationLinkbase;
import xbrlcore.linkbase.DefinitionLinkbase;
import xbrlcore.linkbase.LabelLinkbase;
import xbrlcore.linkbase.PresentationLinkbase;
import xbrlcore.linkbase.PresentationLinkbaseElement;
import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import xbrlcore.xlink.Arc;
import xbrlcore.xlink.ExtendedLinkElement;
import xbrlcore.xlink.Locator;
import xbrlcore.xlink.Resource;

/**
 * UserObject which help to format correctly depending on the object's type
 * 
 * @author Nicolas Georges
 *
 */
public class XbrlTbUserObject{
	static LabelLinkbase ivLLB = null;
	static PresentationLinkbase ivPLB = null;
	static DiscoverableTaxonomySet dts = null;
	static DefinitionLinkbase ivDLB = null;
	static CalculationLinkbase ivCLB = null;	
	
	private Object piUserObject;
	
	public XbrlTbUserObject(){
	}
	
	public XbrlTbUserObject(Object aUserObject){
		piUserObject = aUserObject;
	}
	
	public Object getUserObject(){
		return piUserObject;
	}
	
	public String toString(){
	if (piUserObject==null) return "(null object)";
  	if ( piUserObject instanceof Concept ){
  		Concept lvConcept = (Concept)piUserObject; 
  		return getConceptLabel( lvConcept );
  	}else 
  	if ( piUserObject instanceof PresentationLinkbaseElement ){
  		PresentationLinkbaseElement bvPElement = (PresentationLinkbaseElement) piUserObject;
			return getConceptLabel( bvPElement.getConcept() );
  	}else 
  	if ( piUserObject instanceof Hypercube ){
  		Hypercube bvHypercube = (Hypercube) piUserObject;
  		return getConceptLabel( bvHypercube.getConcept() );
  	}else 
  	if( piUserObject instanceof Dimension ){
  		Dimension bvDimension = (Dimension) piUserObject;
  		return getConceptLabel( bvDimension.getConcept() );
  	}else 
  	if( piUserObject instanceof ExtendedLinkElement ){
  		ExtendedLinkElement bvExtendedLinkElement = (ExtendedLinkElement) piUserObject;
  		//return bvExtendedLinkElement.getTitle(); //+ ":" + ((Locator)bvExtendedLinkElement).getConcept().getName() + "," + ((Locator)bvExtendedLinkElement).isUsable() + "," + ((Locator)bvExtendedLinkElement).isResource();
  		if (bvExtendedLinkElement.isLocator()) 
  		{
  			return getConceptLabel( ((Locator)bvExtendedLinkElement).getConcept() );
  		}
  		if (bvExtendedLinkElement.isResource()){
  			return ((Resource)bvExtendedLinkElement).getLabel();
  		}
  		return bvExtendedLinkElement.getTitle();
  	}
  	if(piUserObject instanceof Arc){
  		Arc lArc = (Arc)piUserObject; 
  		return lArc.getSourceElement().getLabel() + "->" + lArc.getTargetElement().getLabel() ;
  	}

  	return piUserObject.toString();
	}
	
	public String getConceptLabel(Concept lvConcept){
		return ivLLB.getLabel( lvConcept , xbrlcore.constants.GeneralConstants.XBRL_ROLE_LABEL );
	}
	
	static public void setLabelLinkbase( LabelLinkbase lvLLB ){
		ivLLB = lvLLB;
	}
	
	public Map<String, String> getAttributesMapFromArc(String sExtendedLinkRole, String sPrefix){
		Map<String, String> lMap = new HashMap<String, String>();
		Arc lArc = new Arc( sExtendedLinkRole);
		if (lArc!=null){
			lMap.put(sPrefix+".ContextElement", lArc.getContextElement() ); 
			lMap.put(sPrefix+".ArcRole", lArc.getArcrole() );
			lMap.put(sPrefix+".Order", String.valueOf(lArc.getOrder()) );
			lMap.put(sPrefix+".ExtendedLinkRole", lArc.getExtendedLinkRole() );
			lMap.put(sPrefix+".PriorityAttribute", String.valueOf(lArc.getPriorityAttribute()) );
			lMap.put(sPrefix+".TargetRole", lArc.getTargetRole() );
			lMap.put(sPrefix+".Use", lArc.getUseAttribute() );
			lMap.put(sPrefix+".Weight", String.valueOf(lArc.getWeightAttribute()) );
			if (lArc.getSourceElement()!=null){
				lMap.put(sPrefix+".SourceElement", lArc.getSourceElement().getLabel() );
			}
			if (lArc.getTargetElement()!=null){
				lMap.put(sPrefix+".TargetElement", lArc.getTargetElement().getLabel() );
			}			
			Attributes lAttributes = lArc.getAttributes();
			for(int i=0; i<lAttributes.getLength();i++){				
				lMap.put( sPrefix+":"+i, lAttributes.getValue(i));
			}
		}
		return lMap;
	}
	
	public Map<String, String> getAttributesMapFromArc(Arc lArc, String sPrefix){
		Map<String, String> lMap = new HashMap<String, String>();
		if (lArc!=null){
			lMap.put(sPrefix+".ContextElement", lArc.getContextElement() ); 
			lMap.put(sPrefix+".ArcRole", lArc.getArcrole() );
			lMap.put(sPrefix+".Order", String.valueOf(lArc.getOrder()) );
			lMap.put(sPrefix+".ExtendedLinkRole", lArc.getExtendedLinkRole() );
			lMap.put(sPrefix+".PriorityAttribute", String.valueOf(lArc.getPriorityAttribute()) );
			lMap.put(sPrefix+".TargetRole", lArc.getTargetRole() );
			lMap.put(sPrefix+".Use", lArc.getUseAttribute() );
			lMap.put(sPrefix+".Weight", String.valueOf(lArc.getWeightAttribute()) );
			if (lArc.getSourceElement()!=null){
				lMap.put(sPrefix+".SourceElement", lArc.getSourceElement().getLabel() );
			}
			if (lArc.getTargetElement()!=null){
				lMap.put(sPrefix+".TargetElement", lArc.getTargetElement().getLabel() );
			}			
			Attributes lAttributes = lArc.getAttributes();
			for(int i=0; i<lAttributes.getLength();i++){				
				lMap.put( sPrefix+":"+i, lAttributes.getValue(i));
			}
		}
		return lMap;
	}
	
	public Map<String, String> getAttributesMapFromLocator(Locator locator, String sPrefix){
		Map<String, String> lMap = new HashMap<String, String>();
		if (locator!=null){
			lMap.put( sPrefix+".Id", locator.getId());
			lMap.put( sPrefix+".ConceptName", locator.getConcept().getName());
			lMap.put( sPrefix+".Label", locator.getLabel() );
			lMap.put( sPrefix+".LinkbaseSource", locator.getLinkbaseSource() );
			lMap.put( sPrefix+".Role", locator.getRole() );
			lMap.put( sPrefix+".Title", locator.getTitle() );
			if(locator.isResource()){
				Resource resource = locator.getResource();
				lMap.put( sPrefix+"::Resource.id", resource.getId() );
				lMap.put( sPrefix+"::Resource.label", resource.getLabel() );				
				lMap.put( sPrefix+"::Resource.lang", resource.getLang() );
				lMap.put( sPrefix+"::Resource.LinkbaseSource", resource.getLinkbaseSource() );
				lMap.put( sPrefix+"::Resource.Role", resource.getRole() );
				lMap.put( sPrefix+"::Resource.title", resource.getTitle() );
				lMap.put( sPrefix+"::Resource.value", resource.getValue() );
			}
			/*
			if(locator.isLocator()){
				Arc lArc = ivLLB.getArcForSourceLocator(locator);
				lMap.putAll( getAttributesMapFromArc(lArc, "ArcFromSource"));
				//lArc = ivLLB.getArcForTargetLocator(locator);
				//lMap.putAll( getAttributesMapFromArc(lArc, "ArcFromTarget"));
			}
			*/
			
		}
		return lMap;
	}
	
	public Map<String, String> getAttributesMapFromConcept(Concept lConcept, String sPrefix){
		Map<String, String> lMap = new HashMap<String, String>();
		if (lConcept!=null){
			lMap.put(sPrefix+".NodeType", "Concept" );
			lMap.put(sPrefix+".id", lConcept.getId() );
			lMap.put(sPrefix+".name", lConcept.getName() );
			lMap.put(sPrefix+".namespace", lConcept.getNamespace().toString() );
			lMap.put(sPrefix+".PeriodType", lConcept.getPeriodType() );
			lMap.put(sPrefix+".SubstitutionGroup", lConcept.getSubstitutionGroup() );
			lMap.put(sPrefix+".TaxonomySchemaName", lConcept.getTaxonomySchemaName() );
			lMap.put(sPrefix+".Type", lConcept.getType() );
			lMap.put(sPrefix+".TypedDomainRef", lConcept.getTypedDomainRef() != null ? lConcept.getTypedDomainRef() : "null" );
			lMap.put(sPrefix+".isAbstract", String.valueOf(lConcept.isAbstract()) );
			lMap.put(sPrefix+".isNillable", String.valueOf(lConcept.isNillable()) );
			lMap.put(sPrefix+".isExplicitDimension", String.valueOf(lConcept.isExplicitDimension()) );
			lMap.put(sPrefix+".isNumericItem", String.valueOf(lConcept.isNumericItem()) );
			lMap.put(sPrefix+".isTypedDimension", String.valueOf(lConcept.isTypedDimension()) );	
		}
		return lMap;
	}	
	
	public Map<String, String> getAttributesMap(){
		Map<String, String> lAttributes = new HashMap<String, String>();
		
		if(piUserObject instanceof Concept){			
			//Enum concepts attributes
			Concept lConcept = (Concept)piUserObject;
			lAttributes.putAll( getAttributesMapFromConcept( lConcept, "Concept") );	
		}
		
		if(piUserObject instanceof Hypercube){
			Hypercube lHypercube = (Hypercube)piUserObject;
			lAttributes.put("NodeType", "Hypercube" );
			lAttributes.put("ExtendedLinkRole", lHypercube.getExtendedLinkRole() );
			
			lAttributes.putAll( getAttributesMapFromConcept( lHypercube.getConcept() , "Concept") );			
			lAttributes.putAll( getAttributesMapFromArc( lHypercube.getExtendedLinkRole(), "Arc" ) );
			
		}
		
		if(piUserObject instanceof ExtendedLinkElement){
			ExtendedLinkElement lExLinkElement = (ExtendedLinkElement)piUserObject;
			lAttributes.put("NodeType", "ExtendedLinkElement" );
			lAttributes.put("id", lExLinkElement.getId() );
			lAttributes.put("ExtendedLinkRole", lExLinkElement.getExtendedLinkRole() );			
			lAttributes.put("Label", lExLinkElement.getLabel() );
			lAttributes.put("LinkbaseSource", lExLinkElement.getLinkbaseSource() );
			lAttributes.put("Role", lExLinkElement.getRole() );
			lAttributes.put("Title", lExLinkElement.getTitle() );
			lAttributes.put("isLocator", String.valueOf(lExLinkElement.isLocator()) );
			lAttributes.put("isResource", String.valueOf(lExLinkElement.isResource()) );
			
			lAttributes.putAll( getAttributesMapFromArc( lExLinkElement.getExtendedLinkRole(), "Arc" ) );
			if(lExLinkElement.isLocator()){
				lAttributes.putAll( getAttributesMapFromLocator((Locator)lExLinkElement, "Locator") );
			}
		}
		
		if(piUserObject instanceof Dimension){			
			//Enum concepts attributes
			Dimension lDimension = (Dimension)piUserObject;
			lAttributes.put("NodeType", "lDimension" );			
			if(lDimension.isTyped()){
				lAttributes.put("TypedElement", lDimension.getTypedElement().toString() );
			}			
			lAttributes.putAll( getAttributesMapFromConcept( lDimension.getConcept(), "Concept") );			
		}
		
		if ( piUserObject instanceof PresentationLinkbaseElement ){
			PresentationLinkbaseElement bvPElement = (PresentationLinkbaseElement) piUserObject;
			lAttributes.put("NodeType", "PresentationLinkbaseElement" );			
			lAttributes.put("ExtendedLinkRole", bvPElement.getExtendedLinkRole() );
			lAttributes.put("Level", String.valueOf(bvPElement.getLevel()) );
			lAttributes.put("NumDirectSuccessor", String.valueOf(bvPElement.getNumDirectSuccessor()) );
			lAttributes.put("PositionDeepestLevel", String.valueOf(bvPElement.getPositionDeepestLevel()) );
			lAttributes.putAll( getAttributesMapFromConcept( bvPElement.getConcept(), "Concept") );
			if (bvPElement.getParentElement()!=null){
				//lAttributes.put("ParentConcept", bvPElement.getParentElement().getId() );
				lAttributes.putAll( getAttributesMapFromConcept( bvPElement.getParentElement(), "ParentConcept") );
			}
			if (bvPElement.getLocator()!=null){
				Locator lLocator = bvPElement.getLocator();				
				lAttributes.putAll( getAttributesMapFromLocator(lLocator, "Locator" ) );
			}
			
			lAttributes.putAll( getAttributesMapFromArc( bvPElement.getExtendedLinkRole(), "Arc" ) );
		}
		
		if(piUserObject instanceof Arc){
			Arc bvArc = (Arc)piUserObject;
			lAttributes.putAll( getAttributesMapFromArc(bvArc, "Arc"));
		}
		
		return lAttributes;
	}
	
	public void export(PrintStream out){
		//It export the current node...
		out.println( piUserObject.getClass().getSimpleName() + "\t" + toString());
		if(piUserObject instanceof PresentationLinkbaseElement){
			PresentationLinkbaseElement bvPElement = (PresentationLinkbaseElement)piUserObject;
			Iterator<Concept> successors = browsePresentationLinkBaseElement(bvPElement, bvPElement.getLevel() - 1).iterator();
			while(successors.hasNext()){
				Concept successor = (Concept)successors.next();
				out.println( successor.getId() + "\t" + successor.getName() + "\t" + getConceptLabel(successor) );
			}
		}
		
		if(piUserObject instanceof Dimension){
			Iterator<ExtendedLinkElement> idomainmembers = browseDimension( (Dimension)piUserObject ).iterator();
			while( idomainmembers.hasNext() ){
				ExtendedLinkElement extLinkElement = (ExtendedLinkElement)idomainmembers.next();
				//out.println( extLinkElement.getId() + "\t" + extLinkElement.getTitle() + "\t" + extLinkElement.getLabel() );
				Concept concept = dts.getConceptByName( extLinkElement.getTitle() );				
				out.println( concept.getId() + "\t" + concept.getName() + "\t" + getConceptLabel(concept) );
			}
		}
		
	}

	public List<ExtendedLinkElement> browseDimension(Dimension dimension){
		List<ExtendedLinkElement> list = new LinkedList<ExtendedLinkElement>();
		Set<ExtendedLinkElement> lvDomainMemberSet = dimension.getDomainMemberSet();
		for(ExtendedLinkElement bvDomainMember : lvDomainMemberSet)
		{
			list.add( bvDomainMember );
		}						

		return list;
	}
	
	/**
	 * This method return a linear list of all the successors
	 * 
	 * @param pElement
	 * @return List<Concept>
	 * 	 
	 */
	public List<Concept> browsePresentationLinkBaseElement(PresentationLinkbaseElement pElement, long lvParentLevel){
		List<Concept> list = new LinkedList<Concept>();		
		if( pElement.getLevel() == lvParentLevel+1 ){		
			list.add( pElement.getConcept() );
			Iterator<Concept> successors = pElement.getSuccessorElements().iterator();
			while(successors.hasNext()){
				Concept successor = (Concept)successors.next();			
				Iterator<PresentationLinkbaseElement> isubElements = ivPLB.getPresentationList(successor, pElement.getExtendedLinkRole() ).iterator();
				if (!isubElements.hasNext()){
					list.add( successor ); 
				}
				while(isubElements.hasNext()){
					PresentationLinkbaseElement subElement = (PresentationLinkbaseElement)isubElements.next();
					List<Concept> subList = browsePresentationLinkBaseElement( subElement, pElement.getLevel() );
					list.addAll( subList );
				}
			}		
		}
		return list;
	}
	
	public static PresentationLinkbase getIvPLB() {
		return ivPLB;
	}

	public static void setIvPLB(PresentationLinkbase ivPLB) {
		XbrlTbUserObject.ivPLB = ivPLB;
	}

	public static DiscoverableTaxonomySet getDts() {
		return dts;
	}

	public static void setDts(DiscoverableTaxonomySet dts) {
		XbrlTbUserObject.dts = dts;
	}

	public static CalculationLinkbase getIvCLB() {
		return ivCLB;
	}

	public static void setIvCLB(CalculationLinkbase ivCLB) {
		XbrlTbUserObject.ivCLB = ivCLB;
	}

	public static DefinitionLinkbase getIvDLB() {
		return ivDLB;
	}

	public static void setIvDLB(DefinitionLinkbase ivDLB) {
		XbrlTbUserObject.ivDLB = ivDLB;
	}
	
}