package TaxonomyBrowser;
/* 
* Xbrltb.java 
* 
* Created on 17. April 2007, 22:10 
*/
 
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.PropertyConfigurator;
import org.jdom.JDOMException;

import xbrlcore.constants.GeneralConstants;
import xbrlcore.dimensions.Dimension;
import xbrlcore.dimensions.Hypercube;
import xbrlcore.exception.TaxonomyCreationException;
import xbrlcore.exception.XBRLException;
import xbrlcore.instance.FileLoader;
import xbrlcore.instance.InstanceFactory;
import xbrlcore.linkbase.CalculationLinkbase;
import xbrlcore.linkbase.DefinitionLinkbase;
import xbrlcore.linkbase.LabelLinkbase;
import xbrlcore.linkbase.PresentationLinkbase;
import xbrlcore.linkbase.PresentationLinkbaseElement;
import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DTSFactory;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import xbrlcore.xlink.Arc;
import xbrlcore.xlink.ExtendedLinkElement;

/** 
* Taxonomy browser for XBRLCore
* 
* @author Nicolas Georges, Sébastien Kirche
*/ 
public class XbrlTB extends JFrame implements TreeSelectionListener
{ 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 29211963443635256L;

	static boolean ib_debug = false;
	  
	private JTree tree;
	private DefaultTreeModel model;
	private JTable table;
	private JTextField currentSelectionField;
	private JTextField taxoField;
	private JFileChooser fileChooser;
	  	
	DTSFactory ivFactory = null; 
	InstanceFactory iiFactory = null;
	DiscoverableTaxonomySet ivDTS = null;
	LabelLinkbase ivLLB = null;
	PresentationLinkbase ivPresentation = null;
	DefinitionLinkbase ivDefinition = null;
	File taxoEntryPoint;
	/** 
	* @param args the command line arguments : ENTRY POINT
	*/ 
	public static void main(String[] args){ 
		PropertyConfigurator.configure("log4j.properties");		
	    WindowUtilities.setNativeLookAndFeel();
		XbrlTB lvEx = new XbrlTB();						
		/**
		* @TODO Check if extension is XSD, if XML then open instance file and use its taxonomy
		*/
		if(args.length > 0){ 	//avoids exception if there is no args
			String TaxoFileName = args[0];		
			TaxoFileName = TaxoFileName.replace("\\", "/");		
			System.out.println( "Extracting data from file : " + TaxoFileName ); 	
			
			lvEx.ExtractTaxoThread( new File( TaxoFileName ));
			System.out.println();
		}
	} 
	
	/** Creates a new instance of the class */ 
	public XbrlTB(){ 
		super("XBRL Taxonomy Browser");
		ivFactory = DTSFactory.get(); 		
		iiFactory = InstanceFactory.get();
		CreateFrame();		
	}
	
	public void ChooseTaxonomy(){
	    if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
	        return;
	      }
	    File file = fileChooser.getSelectedFile();	    
	    ExtractTaxoThread( file );	    
	}
	
	public void ExtractTaxoThread(File taxo){
		taxoEntryPoint = taxo;
		new Thread(new Runnable(){
			public void run(){
				Extract();
			}
		}).start();
	}
	
	public void valueChanged(TreeSelectionEvent event){
		Object currentNode = tree.getLastSelectedPathComponent();
		if (currentNode == null) return ;
		String lvValue = currentNode.toString();
  	  	
		currentSelectionField.setText("Current Selection: " + lvValue);
    
		DefaultMutableTreeNode dtn = (DefaultMutableTreeNode)currentNode;
		if (dtn.getUserObject() instanceof XbrlTbUserObject){
			XBRLTableModel model = (XBRLTableModel)this.table.getModel();
			model.setTable((XbrlTbUserObject)dtn.getUserObject());
		}
	}	
	
	public void CreateFrame(){		
		fileChooser = new JFileChooser(System.getProperty("user.dir"));	    
		fileChooser.addChoosableFileFilter(new TaxonomyFilter());
		
		addWindowListener(new ExitListener());
		Container content = getContentPane();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Empty"); // with that we will have an empty tree without default nodes colors/sports/food
		model = new DefaultTreeModel(root);
		tree = new JTree(model);
		tree.addTreeSelectionListener(this);
    
		XBRLTableModel tableModel =	new XBRLTableModel(){

			private static final long serialVersionUID = -3084762345188824252L;

				//Describes the processing for entering values in the table model.
				public void setValueAt(Object value, int row, int col){
					/*if(listValue.get(row) instanceof Item) {
					//Obtains the current XBRL instance value.
					Item item = (Item)listValue.get(row);
					//Updates the input value to the XBRL instance.
					//If the value has been updated, true is returned; otherwise, false is returned.
					if (updateNewValue(item, value)){
					//Reports any changes in the table model.
					fireTableCellUpdated(row, col);
					}
					}
					*/
				}
		};
		//Listener registration. Registers listeners that receive notification if model information (cell value) has changed.
		//tableModel.addTableModelListener(this);   //This class receives a change in the model.
		//Table creation
		tableModel.ParentClass = this;
		table = new JTable(tableModel);
    
		//this.table.setDefaultRenderer(Object.class, new XBRLTableCellRenderer());
		//this.table.setDefaultEditor(Object.class, new XBRLTableCellEditor());
		//Inhibits column movement.
		table.getTableHeader().setReorderingAllowed(false);
		table.setPreferredScrollableViewportSize( new java.awt.Dimension(800,70) );
    
		currentSelectionField = new JTextField("Current Selection: NONE");    
   
		JSplitPane jpDetailPanel = new JSplitPane( JSplitPane.VERTICAL_SPLIT, 
												   new JScrollPane(tree), 
												   new JScrollPane(table) );
		//jpDetailPanel.set...
		JButton loadTaxo =  new JButton("Load Taxonomy");
		loadTaxo.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					ChooseTaxonomy();
				}
			});
    
		JButton exportSelectedNode = new JButton("Export");
		exportSelectedNode.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					exportSelectedNode();
				}
			});
    
		JLayeredPane taxoPane = new JLayeredPane(); 
		taxoPane.setLayout( new BoxLayout( taxoPane, BoxLayout.LINE_AXIS ) );
		taxoPane.add( new JLabel("Taxonomy : ") );
		taxoField = new JTextField("");
		taxoField.setEditable(false);
		taxoPane.add( taxoField );
		taxoPane.add( loadTaxo );    
		taxoPane.add( exportSelectedNode );
		content.add( taxoPane , BorderLayout.NORTH);
		content.add(jpDetailPanel, BorderLayout.CENTER);
		content.add(new JScrollPane(currentSelectionField), BorderLayout.SOUTH);    
		
		setSize(800, 600);
		setVisible(true);		
	}
	
	private void exportSelectedNode() {		
	  	Object currentNode = tree.getLastSelectedPathComponent();
	  	if (currentNode == null) return ;
	  	String lvValue = currentNode.toString();
	    DefaultMutableTreeNode dtn = (DefaultMutableTreeNode)currentNode;
    	currentNode = dtn.getUserObject();
    
	    currentSelectionField.setText("Exporting " + lvValue + "...");
	    
	    fileChooser.setSelectedFile( new File(lvValue+".txt") );
	    int returnValue = fileChooser.showSaveDialog( this );
	    if (returnValue == JFileChooser.CANCEL_OPTION ){
	    	currentSelectionField.setText( "Export of " + lvValue + " canceled.");
	    	return;
	    }
	    
	    //Export content...
	    try {
			FileOutputStream output =  new FileOutputStream( fileChooser.getSelectedFile() ) ;
			PrintStream outputstream = new PrintStream( output );
		    if (dtn.getUserObject() instanceof XbrlTbUserObject){
		    	//XBRLTableModel model = (XBRLTableModel)this.table.getModel();
		    	((XbrlTbUserObject)dtn.getUserObject()).export(outputstream);
		    }
		    else{
		    	outputstream.println( currentNode.toString() );
		    }
			
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    currentSelectionField.setText( lvValue + " exported. ");
	}
		
	public DefaultMutableTreeNode GetDimInfo(){
		DefaultMutableTreeNode lvDimInfo = new DefaultMutableTreeNode("Dimension Informations");
		DefaultMutableTreeNode lvSections= new DefaultMutableTreeNode("Sections");
		
		Set<String> lvExtendedLinkRoleSet = ivDefinition.getExtendedLinkRoles();
		for(String bvExtendedLinkRole : lvExtendedLinkRoleSet){
			String lsRoleName = bvExtendedLinkRole;
			lsRoleName = lsRoleName.substring( lsRoleName.lastIndexOf("/") +1);
			DefaultMutableTreeNode lvExtendedLinkRoleNode = new DefaultMutableTreeNode( lsRoleName );
			// xlink:arcrole="http://xbrl.org/int/dim/arcrole/all" for contextElement="scenario"
			List<Arc> lvArcSet = ivDefinition.getArcBaseSet( GeneralConstants.XBRL_ARCROLE_HYPERCUBE_ALL, bvExtendedLinkRole );
			DefaultMutableTreeNode lvScenarios = new DefaultMutableTreeNode("Scenarios");
			for(int i=0;i<lvArcSet.size();i++){
				Arc lArc =  lvArcSet.get(i);
				DefaultMutableTreeNode lvArcNode = new DefaultMutableTreeNode( new XbrlTbUserObject( lArc ) );
				
				DefaultMutableTreeNode lvArcSourceNode = new DefaultMutableTreeNode( new XbrlTbUserObject( lArc.getSourceElement() ) );
				DefaultMutableTreeNode lvArcTargetNode = new DefaultMutableTreeNode( new XbrlTbUserObject( lArc.getTargetElement() ) );
				lvArcNode.add( lvArcSourceNode );
				lvArcNode.add( lvArcTargetNode );
				lvScenarios.add( lvArcNode );
			}			
			lvExtendedLinkRoleNode.add( lvScenarios );
			if (lvScenarios.getChildCount()>0){
				lvSections.add( lvExtendedLinkRoleNode );
			}			
		}
		
		lvDimInfo.add( lvSections );
		return lvDimInfo;
	}
	
	public DefaultMutableTreeNode GetCalculationLinkbase(){
		DefaultMutableTreeNode lvCalculationNode = new DefaultMutableTreeNode("CalculationLinkbase");
		CalculationLinkbase lvCalculation = ivDTS.getCalculationLinkbase();
		if (lvCalculation!=null){
			XbrlTbUserObject.setIvCLB(lvCalculation);
			Set<Concept> lsConcepts = ivDTS.getConcepts();
			for( Concept bvConcept : lsConcepts ){
//				Map lmCalculations = lvCalculation.getCalculations(bvConcept, "http://www.xbrl.org/2003/role/link")
//				lmCalculations.entrySet()
				lvCalculationNode.add( new DefaultMutableTreeNode( new XbrlTbUserObject( bvConcept ) ) );
			}
		}
		return lvCalculationNode;
	}
	
	/**
	* @param ...
	*/	
	public DefaultMutableTreeNode GetDefinitionLinkbase(){
		DefaultMutableTreeNode lvDefinitionNode = new DefaultMutableTreeNode("DefinitionLinkbase");			
		if (ivDefinition != null){
			XbrlTbUserObject.setIvDLB(ivDefinition);
			DefaultMutableTreeNode lvHypercubeSetNode = new DefaultMutableTreeNode("HypercubeSet");
			Set<Hypercube> lvHypercubeSet = ivDefinition.getHypercubeSet();
			for (Hypercube bvHypercube : lvHypercubeSet){ 
				DefaultMutableTreeNode lvHypercubeNode = new DefaultMutableTreeNode( new XbrlTbUserObject(bvHypercube) );					
				Set<Dimension> lvDimensionSet = bvHypercube.getDimensionSet();
				for(Dimension bvDimension : lvDimensionSet){
					DefaultMutableTreeNode lvDimensionNode = new DefaultMutableTreeNode( new XbrlTbUserObject(bvDimension) );
					Set<ExtendedLinkElement> lvDomainMemberSet = bvDimension.getDomainMemberSet();
					for(ExtendedLinkElement bvDomainMember : lvDomainMemberSet){
						DefaultMutableTreeNode lvDomaineMemberNode = new DefaultMutableTreeNode( new XbrlTbUserObject(bvDomainMember) );
						lvDimensionNode.add( lvDomaineMemberNode );
					}						
					lvHypercubeNode.add( lvDimensionNode );
				}					
				lvHypercubeSetNode.add( lvHypercubeNode );
			}									
			lvDefinitionNode.add( lvHypercubeSetNode );
			
			//Now, look for DimensionConceptSet...
			DefaultMutableTreeNode lvDimensionSetNode = new DefaultMutableTreeNode("DimensionSet");
			Set<Concept> lvDimensionSet = ivDefinition.getDimensionConceptSet();
			for( Concept bvConcept : lvDimensionSet){
				DefaultMutableTreeNode lvDimensionNode = new DefaultMutableTreeNode( new XbrlTbUserObject(bvConcept) );
				Concept lvDimensionElement = ivDefinition.getDimensionElement(bvConcept);
				lvDimensionSetNode.add( lvDimensionNode );
				if (lvDimensionElement!=null){
					DefaultMutableTreeNode lvDimensionElementNode = new DefaultMutableTreeNode( new XbrlTbUserObject(lvDimensionElement) );
					lvDimensionSetNode.add( lvDimensionElementNode );
				}					
			}
			lvDefinitionNode.add( lvDimensionSetNode );
				
			//Now ExtendedLinkRoles
			DefaultMutableTreeNode lvExtendedLinkRolesNode = new DefaultMutableTreeNode("ExtendedLinkRoles");
			Set<String> lvExtendedLinkRoleSet = ivDefinition.getExtendedLinkRoles();
			for(String bvExtendedLinkRole : lvExtendedLinkRoleSet){
				DefaultMutableTreeNode lvExtendedLinkRoleNode = new DefaultMutableTreeNode(bvExtendedLinkRole);
				List<ExtendedLinkElement> lvExLinkElementList = ivDefinition.getExtendedLinkElementsFromBaseSet( bvExtendedLinkRole );
				for(int i=0; i<lvExLinkElementList.size(); i++ ){
					ExtendedLinkElement lvExLinkEle = lvExLinkElementList.get(i);
					DefaultMutableTreeNode lvExtendedLinkElementInRoleNode = new  DefaultMutableTreeNode( new XbrlTbUserObject( lvExLinkEle ));						
					lvExtendedLinkRoleNode.add( lvExtendedLinkElementInRoleNode );
				}
				// xlink:arcrole="http://xbrl.org/int/dim/arcrole/all" for contextElement="scenario"
				List<Arc> lvArcSet = ivDefinition.getArcBaseSet( bvExtendedLinkRole );
				for(int i=0;i<lvArcSet.size();i++){
					DefaultMutableTreeNode lvArcNode = new DefaultMutableTreeNode( new XbrlTbUserObject( lvArcSet.get(i) ) );					
					lvExtendedLinkRoleNode.add( lvArcNode );
				}
				lvExtendedLinkRolesNode.add( lvExtendedLinkRoleNode );
				
			}
			lvDefinitionNode.add( lvExtendedLinkRolesNode );

			//Now ExtendedLinkElements
			DefaultMutableTreeNode lvExtendedLinkElementsNode = new DefaultMutableTreeNode("ExtendedLinkElements");
			Set<ExtendedLinkElement> lvExtendedLinkElementSet = ivDefinition.getExtendedLinkElements();
			for(ExtendedLinkElement bvExtendedLinkElement : lvExtendedLinkElementSet){
				DefaultMutableTreeNode lvExtendedLinkElementNode = new DefaultMutableTreeNode(new XbrlTbUserObject(bvExtendedLinkElement) );
				lvExtendedLinkElementsNode.add( lvExtendedLinkElementNode );
			}
			lvDefinitionNode.add( lvExtendedLinkElementsNode );
				
			//Now ArcBaseSet
			DefaultMutableTreeNode lvArcBaseSetNode = new DefaultMutableTreeNode("ArcBaseSet");
			List<Arc> lvArcList = ivDefinition.getArcBaseSet();
			for(int i=0;i<lvArcList.size();i++){
				DefaultMutableTreeNode lvArcBaseNode = new DefaultMutableTreeNode( new XbrlTbUserObject( lvArcList.get(i) ) );
				lvArcBaseSetNode.add(lvArcBaseNode);
			}
			lvDefinitionNode.add( lvArcBaseSetNode );
		}
		else{
			System.out.println( "Empty List of DefinitionLinkbase" );
		}		
		
		return lvDefinitionNode;
	}
		
	/** 
	* @param ...
	*/ 	 		
	public DefaultMutableTreeNode GetPresentationLinkbase(){
		XbrlTbUserObject.setLabelLinkbase( ivLLB );
		XbrlTbUserObject.setIvPLB( ivPresentation );
		DefaultMutableTreeNode lvPresentationNode = new DefaultMutableTreeNode("PresentationLinkbase");
		DefaultMutableTreeNode lvExtendedLinkRole;		
		if (ivPresentation!=null){				
			Set<String> lvExtendedRoles = ivPresentation.getExtendedLinkRoles();
			for (String bvExtendedLinkRole : lvExtendedRoles){ 
				lvExtendedLinkRole = new DefaultMutableTreeNode( new XbrlTbUserObject(bvExtendedLinkRole) );
				System.out.println( "<ExtendedLinkRoles name=\"" + bvExtendedLinkRole + "\">" );
				EnumPresentationLinkBaseElements( ivPresentation.getPresentationLinkbaseElementRoot( bvExtendedLinkRole ), bvExtendedLinkRole, lvExtendedLinkRole, 0 );
				System.out.println( "</ExtendedLinkRoles>");
				lvPresentationNode.add( lvExtendedLinkRole );
			}				
		}
		else{
			System.out.println( "Empty List of PresentationLinkbase" );
		}
		
		return lvPresentationNode;
	}	
	
	/** 
	* @param ...
	*/ 	 			
	public void EnumPresentationLinkBaseElements( List<PresentationLinkbaseElement> lvPERoots, String asExtendedLinkRole, DefaultMutableTreeNode lvExtendedLinkRole, int lvParentLevel ){
		if (lvPERoots==null) return;
				
		Iterator<PresentationLinkbaseElement> liPERoots = lvPERoots.iterator();
		while( liPERoots.hasNext() ){				
			PresentationLinkbaseElement bvPElement = (PresentationLinkbaseElement) liPERoots.next();				
			Concept lvParentElement = bvPElement.getParentElement();				
			if (ib_debug) System.out.println( "<concept>" );
			if (ib_debug) System.out.println( bvPElement.getConcept().getName() 														
											  + "\t" + ivLLB.getLabel( bvPElement.getConcept() , xbrlcore.constants.GeneralConstants.XBRL_ROLE_LABEL )
											  + (ib_debug ? ""
												 + "\t" + bvPElement.getLevel() 
												 + "\t" + ((lvParentElement==null)?"":lvParentElement.getName())														
												 + "\t" + bvPElement.getConcept().getId()
												 + "\t" + bvPElement.getNumDirectSuccessor()
												 + "\t" + bvPElement.getNumSuccessorAtDeepestLevel()
												 + "\t" + bvPElement.getPositionDeepestLevel()										
												 : "" )
											  );
			
			if( bvPElement.getLevel() == lvParentLevel+1 ){
				DefaultMutableTreeNode lvConceptNode = new DefaultMutableTreeNode( new XbrlTbUserObject(bvPElement) );
				EnumConcepts( bvPElement.getSuccessorElements(), asExtendedLinkRole, lvConceptNode, bvPElement.getLevel() );										
				lvExtendedLinkRole.add( lvConceptNode );
			}				
			if (ib_debug) System.out.println( "</concept>" );				
		}		
	}

	/** 
	* @param ...
	*/ 	 			
	public void EnumConcepts( List<Concept> lvConcepts, String asExtendedLinkRole, DefaultMutableTreeNode lvConceptNode, int lvParentLevel ){
		Iterator<Concept> liConcepts = lvConcepts.iterator();
		if (ib_debug) System.out.println("<EnumConcepts>");
		while( liConcepts.hasNext() ){			
			Concept bvConcept = (Concept) liConcepts.next();

			//System.out.println( "[" + bvConcept.getName() +"]" );
			if (ib_debug) System.out.println("<enumconcept name=\"" + bvConcept.getName() +"\">" );			
			if (ivPresentation!=null){				
				EnumPresentationLinkBaseElements( ivPresentation.getPresentationList( bvConcept, asExtendedLinkRole ), asExtendedLinkRole, lvConceptNode/*lvChildConceptNode*/, lvParentLevel );
			}	
			if (ib_debug) System.out.println("</enumconcept>" );
		}
		if (ib_debug) System.out.println("</EnumConcepts>");
	}
		
	/**
	* @param afTaxonomyFile is the root file of the taxonomy to explore...
	*/
	public void Extract(){
		try{ 
			taxoField.setText( taxoEntryPoint.getAbsolutePath() );
			//initialize objects
			
			//uses our caching file loader
			iiFactory.setFileLoader(new FileLoader() );
			
			ivDTS = ivFactory.createTaxonomy(taxoEntryPoint);
			ivLLB = ivDTS.getLabelLinkbase();
			ivPresentation = ivDTS.getPresentationLinkbase();
			ivDefinition = ivDTS.getDefinitionLinkbase();
			XbrlTbUserObject.setDts(ivDTS);
			//GetPresentationLinkbase();
			//System.out.println( ivDTS.getTopTaxonomy().getImportedTaxonomyNames() );
			fillTree();
		}
		catch (TaxonomyCreationException ex){ 
			ex.printStackTrace(); 
			return;
		}
		catch (IOException ex){ 
			ex.printStackTrace(); 
			return;
		} 
		catch (JDOMException ex){ 
			ex.printStackTrace(); 
			return;
		} 		
		catch (XBRLException ex){ 
			ex.printStackTrace(); 
			return;
		} 
		/*
		catch(XBRLCoreException ex){
				ex.printStackTrace();
		}
		catch(java.lang.CloneNotSupportedException ex){
				ex.printStackTrace();
		}
		*/		
		
	}

	private void fillTree() {
	    DefaultMutableTreeNode root = new DefaultMutableTreeNode( "Linkbases" );	
		root.add( GetPresentationLinkbase() );
		root.add( GetDefinitionLinkbase() );
		root.add( GetCalculationLinkbase() );
		//TODO: labels
		//TODO: references
		root.add( GetDimInfo() );
		//root.add( GetImportedTaxonomy() );
		//root.add( GetProhibitedArc() );
		//...
		tree.setModel( new DefaultTreeModel(root) );   
	}	
 
} 
