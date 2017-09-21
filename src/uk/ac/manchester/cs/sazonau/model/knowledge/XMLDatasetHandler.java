package uk.ac.manchester.cs.sazonau.model.knowledge;

import java.io.CharArrayWriter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import uk.ac.manchester.cs.sazonau.ontology.features.Dataset;
import uk.ac.manchester.cs.sazonau.ontology.features.DatasetEntry;
import uk.ac.manchester.cs.sazonau.ontology.features.OntFeatureVector;

public class XMLDatasetHandler extends DefaultHandler {

	private Dataset dataset;
	
	// contents of a current tree node	  
	private CharArrayWriter contents = new CharArrayWriter();

	public Dataset getDataset() {
		return dataset;
	}

	// to get notification of SAX Events: "Start Element"
	public void startElement( String namespaceURI,
			String localName,
			String qName,
			Attributes attr ) throws SAXException {
		contents.reset();

		if (qName.equals(XMLDatasetI.ROOT)) {
			dataset = new Dataset();
		}

		if (qName.equals(XMLDatasetI.ONT)) {
			int id = new Integer(attr.getValue(0));
			DatasetEntry ex = new DatasetEntry();
			ex.id = id;
			dataset.add(ex);
		}

		if (qName.equals(XMLDatasetI.VEC)) {
			OntFeatureVector vector = new OntFeatureVector();
			Double[] vec = vector.toArray();
			for (int i=0; i<attr.getLength() && i<vec.length; i++) {
				vec[i] = new Double(attr.getValue(i));
			}
			DatasetEntry ex = dataset.get(dataset.size()-1);
			ex.vector = vec;
		}

		if (qName.equals(XMLDatasetI.TIMES)) {					
			double time = new Double(attr.getValue(0));			
			DatasetEntry ex = dataset.get(dataset.size()-1);
			ex.time = time;
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


}
