package xbrlcore.taxonomy.sax;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import xbrlcore.linkbase.CalculationLinkbase;
import xbrlcore.linkbase.DefinitionLinkbase;
import xbrlcore.linkbase.LabelLinkbase;
import xbrlcore.linkbase.PresentationLinkbase;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import xbrlcore.taxonomy.TaxonomySchema;

/**
 * @author d2504hd
 * 
 * Edward Wang
 */
public class SAXBuilder {

	private DiscoverableTaxonomySet dts = null;

	private SAXParserFactory saxParserFactory = null;

	private SAXParser saxParser = null;

	private XMLReader xmlReader = null;

	private XBRLSchemaContentHandler xbrlSchemaContentHandler;

	private XBRLLinkbaseContentHandler xbrlLinkbaseContentHandler;

	private URI baseDir;

	private Set alreadyParsedNamespaces;

	private Map linkbaseFiles;

	private boolean topTaxonomy;

	private Map fixedSchemaFiles;

	/** TODO: Konstruktor sollte keine Exceptions werfen! */
	public SAXBuilder() throws ParserConfigurationException, SAXException {
		saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setNamespaceAware(true);
		saxParser = saxParserFactory.newSAXParser();

		fixedSchemaFiles = new HashMap();
		fixedSchemaFiles.put(
				"http://www.xbrl.org/2003/xbrl-instance-2003-12-31.xsd",
				"xbrl-instance-2003-12-31.xsd");
		fixedSchemaFiles.put("http://www.xbrl.org/2005/xbrldt-2005.xsd",
				"xbrldt-2005.xsd");
	}

	public DiscoverableTaxonomySet build(InputSource source)
			throws IOException, SAXException {
		topTaxonomy = true;
		xbrlSchemaContentHandler = new XBRLSchemaContentHandler();
		xbrlLinkbaseContentHandler = new XBRLLinkbaseContentHandler();

		baseDir = null;
		alreadyParsedNamespaces = null;
		linkbaseFiles = new HashMap();

		if (baseDir == null) {
			baseDir = URI.create(source.getSystemId().substring(0,
					source.getSystemId().lastIndexOf("/") + 1));
		}

		xmlReader = saxParser.getXMLReader();

		/* parse schemas */
		dts = new DiscoverableTaxonomySet();
		dts = parseSchema(source);

		/* parse linkbases */
		xbrlLinkbaseContentHandler.setDTS(dts);
		Iterator i = linkbaseFiles.keySet().iterator();
		while (i.hasNext()) {
			String fileName = (String) i.next();
			String role = (String) linkbaseFiles.get(fileName);
			parseLinkbases(new InputSource(fileName), role);
		}

		if (dts.getPresentationLinkbase() != null) {
			dts.getPresentationLinkbase().buildPresentationLinkbase();
		}
		if (dts.getDefinitionLinkbase() != null) {
			dts.getDefinitionLinkbase().buildDefinitionLinkbase();
		}

		return dts;

	}

	private void parseLinkbases(InputSource source, String role)
			throws SAXException, IOException {
		/** Label Linkbase */
		if (role.equals("http://www.xbrl.org/2003/role/labelLinkbaseRef")) {
			LabelLinkbase labelLinkbase = dts.getLabelLinkbase();
			if (labelLinkbase == null) {
				/**
				 * TODO: Das sollte geändert werden! --> Verweis von dts auf
				 * linkbase und zurück ?!?
				 */
				labelLinkbase = new LabelLinkbase(dts);
				dts.setLabelLinkbase(labelLinkbase);
			}
			xbrlLinkbaseContentHandler.setLinkbase(labelLinkbase);

			xmlReader.setContentHandler(xbrlLinkbaseContentHandler);
			xmlReader.parse(source);

		}
		/** Presentation Linkbase */
		else if (role
				.equals("http://www.xbrl.org/2003/role/presentationLinkbaseRef")) {
			PresentationLinkbase presentationLinkbase = dts
					.getPresentationLinkbase();
			if (presentationLinkbase == null) {
				presentationLinkbase = new PresentationLinkbase(dts);
				dts.setPresentationLinkbase(presentationLinkbase);
			}
			xbrlLinkbaseContentHandler.setLinkbase(presentationLinkbase);

			xmlReader.setContentHandler(xbrlLinkbaseContentHandler);
			xmlReader.parse(source);
		}
		/** Definition Linkbase */
		else if (role
				.equals("http://www.xbrl.org/2003/role/definitionLinkbaseRef")) {
			DefinitionLinkbase definitionLinkbase = dts.getDefinitionLinkbase();
			if (definitionLinkbase == null) {
				definitionLinkbase = new DefinitionLinkbase(dts);
				dts.setDefinitionLinkbase(definitionLinkbase);
			}
			xbrlLinkbaseContentHandler.setLinkbase(definitionLinkbase);

			xmlReader.setContentHandler(xbrlLinkbaseContentHandler);
			xmlReader.parse(source);
		}
		/** Calculation Linkbase */
		else if (role
				.equals("http://www.xbrl.org/2003/role/calculationLinkbaseRef")) {
			CalculationLinkbase calculationLinkbase = dts
					.getCalculationLinkbase();
			if (calculationLinkbase == null) {
				calculationLinkbase = new CalculationLinkbase(dts);
				dts.setCalculationLinkbase(calculationLinkbase);
			}
			xbrlLinkbaseContentHandler.setLinkbase(calculationLinkbase);

			xmlReader.setContentHandler(xbrlLinkbaseContentHandler);
			xmlReader.parse(source);
		}

	}

	private DiscoverableTaxonomySet parseSchema(InputSource source)
			throws SAXException, IOException {
		URI schemaDir = URI.create(source.getSystemId().substring(0,
				source.getSystemId().lastIndexOf("/") + 1));

		TaxonomySchema newSchema = new TaxonomySchema(dts);
		if (topTaxonomy) {
			dts.setTopTaxonomy(newSchema);
			topTaxonomy = false;
		}

		/**
		 * TODO: That is just a work-around. Think about how to implement it
		 * correctly with name and location; especially when creating the
		 * references in instance documents
		 */
		newSchema.setName(source.getSystemId().substring(
				source.getSystemId().lastIndexOf("/") + 1,
				source.getSystemId().length()));

		dts.addTaxonomy(newSchema);
		xbrlSchemaContentHandler.setTaxonomySchema(newSchema);

		xmlReader.setContentHandler(xbrlSchemaContentHandler);
		xmlReader.parse(source);

		/* get imported schemas */
		Map importedSchemaFiles = xbrlSchemaContentHandler
				.getImportedSchemaFiles();
		newSchema.setImportedTaxonomyNames(new HashSet(importedSchemaFiles
				.values()));

		/* remove all schema files that have already been parsed */
		if (alreadyParsedNamespaces == null) {
			alreadyParsedNamespaces = new HashSet(importedSchemaFiles.keySet());
		} else {
			Iterator i = alreadyParsedNamespaces.iterator();
			while (i.hasNext()) {
				importedSchemaFiles.remove(i.next());
			}
			alreadyParsedNamespaces.addAll(importedSchemaFiles.keySet());
		}

		/* get linkbase files */
		Map tmpLinkbaseFiles = xbrlSchemaContentHandler.getLinkbaseFiles();
		Iterator j = tmpLinkbaseFiles.keySet().iterator();
		while (j.hasNext()) {
			String fileName = (String) j.next();
			String role = (String) tmpLinkbaseFiles.get(fileName);
			linkbaseFiles.put(schemaDir.toString() + fileName, role);
		}

		/* parse imported schemas */
		Iterator i = importedSchemaFiles.keySet().iterator();

		while (i.hasNext()) {
			String currSchemaLocation = (String) importedSchemaFiles.get(i
					.next());

			URI uri = URI.create(currSchemaLocation);

			Iterator fixedSchemaFilesIterator = fixedSchemaFiles.keySet()
					.iterator();
			if (uri.isAbsolute()) {
				while (fixedSchemaFilesIterator.hasNext()) {
					String key = (String) fixedSchemaFilesIterator.next();
					String value = (String) fixedSchemaFiles.get(key);
					if (uri.toString().equals(key)) {
						parseSchema(new InputSource("schemaFiles/" + value));
					}
				}
			}

			else {
				parseSchema(new InputSource(baseDir.toString()
						+ currSchemaLocation));
			}

		}
		return dts;
	}
}