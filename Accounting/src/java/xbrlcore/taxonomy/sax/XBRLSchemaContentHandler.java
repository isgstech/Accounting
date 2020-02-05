/*
 * Created on 25.05.2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package xbrlcore.taxonomy.sax;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jdom.Namespace;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.TaxonomySchema;

/**
 * @author d2504hd, Edward Wang
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class XBRLSchemaContentHandler implements ContentHandler {

	private TaxonomySchema taxonomySchema;

	private String taxonomyName;

	private Map namespaceMapping;

	private Map importedSchemaFiles;

	private Map linkbaseFiles;

	public void setTaxonomySchema(TaxonomySchema taxonomySchema) {
		this.taxonomySchema = taxonomySchema;
		this.taxonomyName = taxonomySchema.getName();
		importedSchemaFiles = new HashMap();
		linkbaseFiles = new HashMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
		if (namespaceMapping == null) {
			namespaceMapping = new HashMap();
		} else {
			namespaceMapping = new HashMap();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
	 */
	public void endPrefixMapping(String arg0) throws SAXException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	public void skippedEntity(String arg0) throws SAXException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public void setDocumentLocator(Locator arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String,
	 *      java.lang.String)
	 */
	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
	 *      java.lang.String)
	 */
	public void startPrefixMapping(String prefix, String url)
			throws SAXException {
		namespaceMapping.put(url, prefix);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void endElement(String arg0, String arg1, String arg2)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if (localName.equals("schema")) {
			String targetNamespaceURI = atts.getValue("targetNamespace");
			//try to get the prefix from the additionnal namespace instead of forging it
			String targetNamespacePrefix = null;
			/*
			for (int i = 0; i < atts.getLength(); i++){
				if (atts.getQName(i).startsWith("xmlns:")){
					if (atts.getValue(i).equalsIgnoreCase(targetNamespaceURI)){
						targetNamespacePrefix = atts.getQName(i).substring(atts.getQName(i).indexOf(':') + 1);
						break;
					}
				}
			}
			*/
		    Iterator ns = namespaceMapping.entrySet().iterator();
		    while (ns.hasNext()) {
		        Map.Entry pairs = (Map.Entry)ns.next();
		        if (pairs.getKey().toString().equalsIgnoreCase(targetNamespaceURI)){
		        	targetNamespacePrefix = pairs.getValue().toString();
		        	break;
		        }
		    }
			//if we did not found the "official" prefix, build one
			if (targetNamespacePrefix == null) 
				targetNamespacePrefix = "ns_"
					+ targetNamespaceURI.substring(targetNamespaceURI
							.lastIndexOf("/") + 1, targetNamespaceURI.length());
			taxonomySchema.setNamespace(Namespace.getNamespace(
					targetNamespacePrefix, targetNamespaceURI));
		} else if (localName.equals("element")
				&& namespaceURI.equals("http://www.w3.org/2001/XMLSchema")) {
			buildConcept(namespaceURI, localName, qName, atts);

		} else if (localName.equals("import")
				&& namespaceURI.equals("http://www.w3.org/2001/XMLSchema")) {
			importedSchemaFiles.put(atts.getValue("namespace"), atts
					.getValue("schemaLocation"));
		} else if (localName.equals("linkbaseRef")
				&& namespaceURI.equals("http://www.xbrl.org/2003/linkbase")) {
			linkbaseFiles.put(atts.getValue("http://www.w3.org/1999/xlink",
					"href"), atts.getValue("http://www.w3.org/1999/xlink",
					"role"));
		}

	}

	private void buildConcept(String namespaceURI, String localName,
			String qName, Attributes atts) {
		if (atts.getValue("name") == null || atts.getValue("id") == null)
			return;
		Concept concept = new Concept(atts.getValue("name"));
		concept.setId(atts.getValue("id"));
		concept.setType(atts.getValue("type"));
		concept.setSubstitutionGroup(atts.getValue("substitutionGroup"));
		concept.setPeriodType(atts.getValue(namespaceMapping
				.get("http://www.xbrl.org/2003/instance")
				+ ":periodType"));
		concept.setAbstract(atts.getValue("abstract") != null
				&& atts.getValue("abstract").equals("true"));
		concept.setNillable(atts.getValue("nillable") != null
				&& atts.getValue("nillable").equals("true"));
		concept.setTypedDomainRef(atts.getValue("http://xbrl.org/2005/xbrldt",
				"typedDomainRef"));
		concept.setTaxonomySchemaName(taxonomyName);
		concept.setNamespace(taxonomySchema.getNamespace());
		/** TODO: This exception must be thrown to the invoking method! */
		try {
			taxonomySchema.addConcept(concept);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println(ex.toString());
		}
	}

	public Map getImportedSchemaFiles() {
		return new HashMap(importedSchemaFiles);
	}

	public Map getLinkbaseFiles() {
		return new HashMap(linkbaseFiles);
	}
}