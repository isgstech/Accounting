package TaxonomyBrowser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
  Table model class.
  <br/>
  <br/>
  This sample provides a model for the XBRL data display table.
  <br/>
  
  @author Nicolas Georges
*/
public abstract class XBRLTableModel extends AbstractTableModel {
  /**
   * 
   */
  private static final long serialVersionUID = 5664807540442063385L;
	
  XbrlTB ParentClass = null;
  /**
    Defines the table header.
  */
  private static final String[] columnNames = {"Attribute name","Attribute value"};

  /**
    Taxonomy element display column
  */
  private static final int COL_ELEMENT = 0;
  /**
    Instance value display column
  */
  //private static final int COL_INSTANCE = 1;

  /**
    Retention of taxonomy element
  */
  protected Vector<String> listElement = new Vector<String>(0);

  /**
    Retention of instance value
  */
  protected Vector<String> listValue   = new Vector<String>(0);

  /**
    Default constructor
  */
  public XBRLTableModel(){
    super();
  }

  /**
    Reads the XBRL nodes into the table model class and reports changes in the table.
    @param instance XBRL document to be entered in the table
  */
  public void setTable(XbrlTbUserObject currentNode){
    //Clears existing data.
    listElement.clear();
    listValue.clear();
    //Obtains taxonomy elements from the XBRL document in the order of the presentation links.
    //Enumeration myenum = null;
    Map<String, String> lAttributes = currentNode.getAttributesMap();
    List<String> lKeyList = new ArrayList(lAttributes.keySet());
    Collections.sort(lKeyList, String.CASE_INSENSITIVE_ORDER);
    for( String lsAttributeName : lKeyList){    	
    	String lsAttributeValue = lAttributes.get( lsAttributeName );
	    listElement.add( lsAttributeName );
	    listValue.add( lsAttributeValue );
    }    
   
    /*
    myenum = InstanceCreatorController.getResolvedAllPresentationElements(instance.getTaxonomySet());
    while(myenum.hasMoreElements()){
      ElementDecl element = (ElementDecl)myenum.nextElement();
      //Retains taxonomy elements.
      listElement.add(element);
      //Obtains the values corresponding to the taxonomy elements and retains them.
      InstanceElementList itemList = instance.getUserElementsByElementDecl(element);
      if(itemList.size() > 0) {
        boolean ok = false;
        for(int iIndex=0; iIndex<itemList.size(); iIndex++) {
          InstanceElement item = itemList.get(iIndex);
          if(item.getElementType() == InstanceElement.ELEMENT_TYPE_NON_NUMERIC_ITEM) {
            NonNumericItem nnItem = (NonNumericItem)item;
            String lang = nnItem.getLanguage();
            if(lang != null && lang.equals(java.util.Locale.getDefault().getLanguage())) {
              listValue.add(item);
              ok = true;
              break;
            }
          } else {
            listValue.add(item);
            ok = true;
            break;
          }
        }
        if(!ok) {
            listValue.add(itemList.get(0));
        }
      } else {
        listValue.add("");
      }      
      //Usually, multiple values can be retained for one taxonomy, but this sample does not support such use.
    }
    */
    //Reports any changes in the table contents.
    fireTableDataChanged();    
  }


  /**
    Sets the value for the cell located at columnIndex and rowIndex.
    <br/>
    *  Provide this statement within the class used for display.
    @param value New value
    @param row Row at which the value is to be changed
    @param col Column at which the value is to be changed
   */
  public abstract void setValueAt(Object value, int row, int col);

  /**
    Returns the number of columns.
    @return Number of columns (fixed to 2)
  */
  public int getColumnCount() {
    return columnNames.length;
  }
  /**
    Returns the header name.
    @param col Column position at which the header name is to be obtained
    @return Header name
  */
  public String getColumnName(int col) {
    return columnNames[col];
  }
  /**
    Returns the header class
    @param col Column position at which the class is to be obtained
    @return Header class (fixed to String)
   */
  public Class<String> getColumnClass(int col) {
    return String.class;
  }
  /**
    Returns the number of rows.
    @return Number of rows
  */
  public int getRowCount() {
    return listElement.size();
  }
  /**
    Returns the cell value.
    @return Number of rows
  */
  public Object getValueAt(int row, int col) {
    if (row>=listElement.size()){
      //This event should not occur but is included in an attempt to cover all possibilities
      return null;
    }
    if (col == COL_ELEMENT){
      return listElement.get(row);
    }else{
      return listValue.get(row);
    }
  }
  /**
    Returns information on whether the cell can be edited.
    @param row Row position to be checked
    @param col Column position to be checked
    @return true: Can be edited   false:  Cannot be edited
   */
  public boolean isCellEditable(int row, int col) {	
	return false;
	/*
    if (col < COL_INSTANCE) {
      return false;
    }else{
      if (listElement.get(row) == null){
        return false;
      }
      if(listElement.get(row) instanceof ElementDecl){
        ElementDecl elemDecl = (ElementDecl)listElement.get(row);
        return elemDecl.isItemDecl();
      }else{
        return false;
      }
    }
    */
  }

}

