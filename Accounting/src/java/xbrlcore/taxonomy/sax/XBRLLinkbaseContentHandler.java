/*
 * Created on 26.07.2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package xbrlcore.taxonomy.sax;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import xbrlcore.constants.ExceptionConstants;
import xbrlcore.exception.TaxonomyCreationException;
import xbrlcore.linkbase.CalculationLinkbase;
import xbrlcore.linkbase.DefinitionLinkbase;
import xbrlcore.linkbase.LabelLinkbase;
import xbrlcore.linkbase.Linkbase;
import xbrlcore.linkbase.PresentationLinkbase;
import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import xbrlcore.xlink.Arc;
import xbrlcore.xlink.ExtendedLinkElement;
import xbrlcore.xlink.Locator;
import xbrlcore.xlink.Resource;

/**
 * @author d2504hd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class XBRLLinkbaseContentHandler implements ContentHandler {

	private Linkbase linkbase;

	private String extendedLinkRole;

	private DiscoverableTaxonomySet dts;

	private String linkbaseSource;

	private boolean inLabel = false;

	private Attributes resourceAtts;

	private Map arcMap;

	private Map namespaceMapping;

	public XBRLLinkbaseContentHandler() {

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
		// TODO Auto-generated method stub
		if (arcMap == null) {
			arcMap = new HashMap();
		} else {
			arcMap.clear();
		}
		if (namespaceMapping == null) {
			namespaceMapping = new HashMap();
		} else {
			namespaceMapping.clear();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		if (inLabel) {
			StringBuffer textBuffer = new StringBuffer();
			textBuffer.append(arg0, arg1, arg2);
			buildResource(textBuffer.toString());
		}

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
	public void setDocumentLocator(org.xml.sax.Locator arg0) {
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
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("labelLink")
				&& namespaceURI.equals("http://www.xbrl.org/2003/linkbase")) {
			/* create all arcs now */
			buildArcs();
			extendedLinkRole = null;
		} else if (localName.equals("presentationLink")
				&& namespaceURI.equals("http://www.xbrl.org/2003/linkbase")) {
			buildArcs();
			extendedLinkRole = null;
		} else if (localName.equals("definitionLink")
				&& namespaceURI.equals("http://www.xbrl.org/2003/linkbase")) {
			buildArcs();
			extendedLinkRole = null;
		} else if (localName.equals("calculationLink")
				&& namespaceURI.equals("http://www.xbrl.org/2003/linkbase")) {
			buildArcs();
			extendedLinkRole = null;
		} else if (localName.equals("label")
				&& namespaceURI.equals("http://www.xbrl.org/2003/linkbase")) {
			inLabel = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if (localName.equals("labelLink")
				&& namespaceURI.equals("http://www.xbrl.org/2003/linkbase")) {
			/* check whether we are in correct linkbase */
			if (!(linkbase instanceof LabelLinkbase)) {
				/** TODO: Exception werfen */
			}
			extendedLinkRole = atts.getValue("http://www.w3.org/1999/xlink",
					"role");
			linkbase.addExtendedLinkRole(extendedLinkRole);
			//FIXME: handle of roleRef now we have RoleType
		} else if (localName.equals("presentationLink")
				&& namespaceURI.equals("http://www.xbrl.org/2003/linkbase")) {
			/* check whether we are in correct linkbase */
			if (!(linkbase instanceof PresentationLinkbase)) {
				/** TODO: Exception werfen */
			}
			extendedLinkRole = atts.getValue("http://www.w3.org/1999/xlink",
					"role");
			linkbase.addExtendedLinkRole(extendedLinkRole);
		} else if (localName.equals("definitionLink")
				&& namespaceURI.equals("http://www.xbrl.org/2003/linkbase")) {
			/* check whether we are in correct linkbase */
			if (!(linkbase instanceof DefinitionLinkbase)) {
				/** TODO: Exception werfen */
			}
			extendedLinkRole = atts.getValue("http://www.w3.org/1999/xlink",
					"role");
			linkbase.addExtendedLinkRole(extendedLinkRole);
		} else if (localName.equals("calculationLink")
				&& namespaceURI.equals("http://www.xbrl.org/2003/linkbase")) {
			/* check whether we are in correct linkbase */
			if (!(linkbase instanceof CalculationLinkbase)) {
				/** TODO: Exception werfen */
			}
			extendedLinkRole = atts.getValue("http://www.w3.org/1999/xlink",
					"role");
			linkbase.addExtendedLinkRole(extendedLinkRole);
		}

		else if (localName.equals("loc")
				&& namespaceURI.equals("http://www.xbrl.org/2003/linkbase")) {
			String type = atts.getValue("http://www.w3.org/1999/xlink", "type");
			if (type.equals("locator")) {
				buildLocator(atts);
			} else {
				/** TODO: throw exception? */
			}
		} else if (localName.equals("label")
				&& namespaceURI.equals("http://www.xbrl.org/2003/linkbase")) {
			/* check whether we are in label linkbase */
			if (!(linkbase instanceof LabelLinkbase)) {
				/** TODO: Exception werfen */
			}
			String type = atts.getValue("http://www.w3.org/1999/xlink", "type");
			if (type.equals("resource")) {
				inLabel = true;
				resourceAtts = atts;
			} else {
				/** TODO: Exception werfen */
			}
		} else if (localName.equals("labelArc")
				&& namespaceURI.equals("http://www.xbrl.org/2003/linkbase")) {
			/* check whether we are in label linkbase */
			if (!(linkbase instanceof LabelLinkbase)) {
				/** TODO: Exception werfen */
			}

			/* create new object to save attribute data */
			arcMap.put(new Integer(arcMap.size()), new AttributesImpl(atts));
		} else if (localName.equals("presentationArc")
				&& namespaceURI.equals("http://www.xbrl.org/2003/linkbase")) {
			if (!(linkbase instanceof PresentationLinkbase)) {
				/** TODO: Exception werfen */
			}
			arcMap.put(new Integer(arcMap.size()), new AttributesImpl(atts));
		} else if (localName.equals("definitionArc")
				&& namespaceURI.equals("http://www.xbrl.org/2003/linkbase")) {
			if (!(linkbase instanceof DefinitionLinkbase)) {
				/** TODO: Exception werfen */
			}
			arcMap.put(new Integer(arcMap.size()), new AttributesImpl(atts));
		} else if (localName.equals("calculationArc")
				&& namespaceURI.equals("http://www.xbrl.org/2003/linkbase")) {
			if (!(linkbase instanceof CalculationLinkbase)) {
				/** TODO: Exception werfen */
			}
			arcMap.put(new Integer(arcMap.size()), new AttributesImpl(atts));
		}
	}

	private void buildArcs() {
		Iterator i = arcMap.keySet().iterator();
		while (i.hasNext()) {
			Attributes atts = (Attributes) arcMap.get(i.next());

			atts.getValue("http://www.w3.org/1999/xlink", "from");

			String fromAttribute = atts.getValue(
					"http://www.w3.org/1999/xlink", "from");
			String toAttribute = atts.getValue("http://www.w3.org/1999/xlink",
					"to");

			List fromElements = linkbase.getExtendedLinkElements(fromAttribute,
					extendedLinkRole, linkbaseSource);
			List toElements = linkbase.getExtendedLinkElements(toAttribute,
					extendedLinkRole, linkbaseSource);

			for (int fromNumber = 0; fromNumber < fromElements.size(); fromNumber++) {
				ExtendedLinkElement currFromElement = (ExtendedLinkElement) fromElements
						.get(fromNumber);
				for (int toNumber = 0; toNumber < toElements.size(); toNumber++) {
					ExtendedLinkElement currToElement = (ExtendedLinkElement) toElements
							.get(toNumber);
					String arcRole = atts.getValue(
							"http://www.w3.org/1999/xlink", "arcrole");
					String title = atts.getValue(
							"http://www.w3.org/1999/xlink", "title");
					String targetRole = atts.getValue(
							"http://xbrl.org/2005/xbrldt", "targetRole");
					String contextElement = atts.getValue(
							"http://xbrl.org/2005/xbrldt", "contextElement");
					String order = atts.getValue("order");
					String priority = atts.getValue("priority");
					String use = atts.getValue("use");
					String usable = atts.getValue(
							"http://xbrl.org/2005/xbrldt", "usable");
					String weight = atts.getValue("weight");
					Arc newArc = new Arc(extendedLinkRole);
					newArc.setAttributes(atts);
					newArc.setSourceElement(currFromElement);
					newArc.setTargetElement(currToElement);
					newArc.setArcrole(arcRole);
					newArc.setTitle(title);
					newArc.setTargetRole(targetRole);
					newArc.setContextElement(contextElement);
					if (order != null) {
						float floatOrder = new Float(order).floatValue();
						newArc.setOrder(floatOrder);
					}
					if (priority != null) {
						int intPriority = new Integer(priority).intValue();
						newArc.setPriorityAttribute(intPriority);
					}
					if (weight != null) {
						float floatWeight = new Float(weight).floatValue();
						newArc.setWeightAttribute(floatWeight);
					}
					if (usable != null && usable.equals("false")) {
						if (newArc.getTargetElement().isLocator()) {
							((Locator) newArc.getTargetElement())
									.setUsable(false);
						}
					}
					newArc.setUseAttribute(use);
					/** TODO: newArc.setAttributes() missing!! */

					linkbase.addArc(newArc);

				}
			}
		}

	}

	private void buildResource(String value) throws TaxonomyCreationException {
		String label = resourceAtts.getValue("http://www.w3.org/1999/xlink",
				"label");
		String title = resourceAtts.getValue("http://www.w3.org/1999/xlink",
				"title");
		/* a resource has to be created */
		String role = resourceAtts.getValue("http://www.w3.org/1999/xlink",
				"role");
		String id = resourceAtts.getValue("id");
		String lang = resourceAtts.getValue(
				"http://www.w3.org/XML/1998/namespace", "lang");
		Resource newResource = new Resource(label, linkbaseSource);
		newResource.setTitle(title);
		newResource.setRole(role);
		newResource.setId(id);
		newResource.setLang(lang);
		newResource.setValue(value);
		newResource.setExtendedLinkRole(extendedLinkRole);
		linkbase.addExtendedLinkElement(newResource);
	}

	private void buildLocator(Attributes atts) throws TaxonomyCreationException {
		String label = atts.getValue("http://www.w3.org/1999/xlink", "label");
		String title = atts.getValue("http://www.w3.org/1999/xlink", "title");

		/* a locator has to be created */
		String conceptName = atts.getValue("http://www.w3.org/1999/xlink", "href");
		Locator newLocator = new Locator(label, linkbaseSource);
		newLocator.setTitle(title);
		newLocator.setExtendedLinkRole(extendedLinkRole);
		/* try to obtain the concept */
		String elementId = conceptName.substring(conceptName.indexOf("#") + 1,
				conceptName.length());
		Concept concept = dts.getConceptByID(elementId);
		if (concept != null) {
			newLocator.setConcept(concept);
		} else {
			// the href does not point to a concept, but to another resource into another linkbase
			String targetLinkbase  = conceptName.substring(0, conceptName.indexOf('#'));
			Resource resource = new Resource(elementId, targetLinkbase);
			newLocator.setResource(resource);
			/* try to get an already existing resource */
			/** TODO: Wie?? */
			/*
			throw new TaxonomyCreationException(
					ExceptionConstants.EX_LINKBASE_LOCATOR_WITHOUT_REF + ": "
							+ elementId + " in Linkbase " + linkbaseSource);
			*/
		}
		linkbase.addExtendedLinkElement(newLocator);

	}

	public void setLinkbase(Linkbase linkbase) {
		this.linkbase = linkbase;
	}

	public void setDTS(DiscoverableTaxonomySet dts) {
		this.dts = dts;
	}

}