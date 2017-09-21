package uk.ac.manchester.cs.sazonau.model.knowledge;

import java.io.CharArrayWriter;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import uk.ac.manchester.cs.sazonau.ontology.features.OntFeatureVector;

/**
 * Author: Viachaslau (Slava) Sazonau<br>
 * Example given on lecture 28-Sep-2012 was used as starting point for this class (thanks a lot!)<br>
 * The University of Manchester<br>
 * COMP60411 Semi-structured Data and the Web<br>
 * Date: 03-October-2012
 * 
 * SAX2 handler for parsing XML content.
 * It can work in one of two modes (version defines the mode):
 * 	1) String mode - evaluation is performed while parsing 
 * 					(just one simple string is stored and changed in memory!)
 * 	2) Tree mode - handler builds the structure of arithmetical expression in tree form, 
 * 					result is calculated only when parsing is finished
 */
class SAX2XMLHandler extends DefaultHandler {
	
	private LinkedList<MarkovProcess> base;

	// contents of a current tree node	  
	private CharArrayWriter contents = new CharArrayWriter();
	
	/**
	 * Default constructor
	 */
	public SAX2XMLHandler() {
		super();
		base = new LinkedList<MarkovProcess>();
	}	
	
	// to get notification of SAX Events: "Start Element"
	public void startElement( String namespaceURI,
			String localName,
			String qName,
			Attributes attr ) throws SAXException {
		contents.reset();
		
		if (qName.equals(XMLKnowledgeI.ROOT)) {
			int id = new Integer(attr.getValue(0));
			String reasoner = attr.getValue(2);
			String ontology = attr.getValue(1);			
			MarkovProcess process = new MarkovProcess(id, reasoner, ontology);
			base.add(process);
		}
		
		if (qName.equals(XMLKnowledgeI.ENTRY)) {
			int id = new Integer(attr.getValue(0));
			MarkovProcess process = base.getLast();			
			MarkovProcessEntry entry = new MarkovProcessEntry(id);
			process.addEntry(entry);
		}
		
		if (qName.equals(XMLKnowledgeI.STATE)) {
			OntFeatureVector vector = new OntFeatureVector();
			Double[] vec = vector.toArray();
			for (int i=0; i<attr.getLength(); i++) {
				vec[i] = new Double(attr.getValue(i));
			}
			MarkovProcessEntry entry = base.getLast().getLast();
			entry.setState(vec);
		}
		
		if (qName.equals(XMLKnowledgeI.ACTION)) {
			OntFeatureVector vector = new OntFeatureVector();
			Double[] vec = vector.toArray();
			for (int i=0; i<attr.getLength(); i++) {
				vec[i] = new Double(attr.getValue(i));
			}
			MarkovProcessEntry entry = base.getLast().getLast();
			entry.setAction(vec);
		}
		
		if (qName.equals(XMLKnowledgeI.REWARD)) {
			double reward = new Double(attr.getValue(0));
			MarkovProcessEntry entry = base.getLast().getLast();
			entry.setReward(reward);
		}
		
		if (qName.equals(XMLKnowledgeI.TIME)) {
			double time = new Double(attr.getValue(0));
			MarkovProcessEntry entry = base.getLast().getLast();
			entry.setTime(time);
		}
		
		
		
	}
	
	// to get notification of SAX Events: "End Element"
	public void endElement( String namespaceURI,
			String localName,
			String qName ) throws SAXException {
		
	}	   

	public void characters( char[] ch, int start, int length )
			throws SAXException {
		contents.write( ch, start, length );
	}

	public void startDocument( ) throws SAXException {

	}

	public void endDocument( ) throws SAXException {

	}	

	public void warning(SAXParseException e) throws SAXException {
		System.out.println("Warning: "); 
		printInfo(e);		
	}		
	public void error(SAXParseException e) throws SAXException {
		System.out.println("Error: "); 
		printInfo(e);		
	}
	public void fatalError(SAXParseException e) throws SAXException {
		System.out.println("Fatal error: "); 
		printInfo(e);		
	}	
	
	/**
	 * prints some useful info about caught exception
	 * @param e
	 */
	private void printInfo(SAXParseException e) {
		// printing more info about exception caught		
		System.out.println("   Line number: "+e.getLineNumber());
		System.out.println("   Column number: "+e.getColumnNumber());
		System.out.println("   Message: "+e.getMessage());
	}

	public LinkedList<MarkovProcess> getBase() {
		return base;
	}
	
	
}
