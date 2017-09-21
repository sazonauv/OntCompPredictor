package uk.ac.manchester.cs.sazonau.model.knowledge;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.ac.manchester.cs.sazonau.ontology.ReasonerLoader;
import uk.ac.manchester.cs.sazonau.ontology.features.Dataset;
import uk.ac.manchester.cs.sazonau.ontology.features.DatasetEntry;
import uk.ac.manchester.cs.sazonau.ontology.features.OntFeatureVector;

public class XMLDatasetWriter {

	private Document xmldoc;	
	
	private String reasoner;	

	public XMLDatasetWriter(String reasoner) {
		super();
		this.reasoner = reasoner;
	}

	private void createDocumentStub() {
		DocumentBuilder builder = null;		

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();		
		factory.setValidating(false);
		factory.setNamespaceAware(false);

		try {
			builder = factory.newDocumentBuilder();							
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		xmldoc = builder.newDocument();
	}

	public void createDataSetFile(OntFeatureVector[] vectors, double[] times) {
		createDocumentStub();
		fillDataSet(vectors, times);
		writeDataSet();
	}
	
	public void createDataSetFile(Dataset dataset, int i) {
		createDocumentStub();
		fillDataSet(dataset);
		writeDataSet(i);
	}

	private void fillDataSet(OntFeatureVector[] vectors, double[] times) {
		// create a root element
		Element rootEl = xmldoc.createElement(XMLDatasetI.ROOT);
		Attr reasAt = xmldoc.createAttribute(XMLDatasetI.REASONER);
		reasAt.setValue(reasoner);
		rootEl.setAttributeNode(reasAt);
		xmldoc.appendChild(rootEl);
		// add feature vectors
		for (int i=0; i<vectors.length; i++) {
			if (vectors[i] == null) {
				continue;
			}
			Element entryEl = xmldoc.createElement(XMLDatasetI.ONT);
			// id
			Attr entryId = xmldoc.createAttribute(XMLDatasetI.ID);
			entryId.setValue(Integer.toString(i));
			entryEl.setAttributeNode(entryId);
			// vector
			entryEl.appendChild(createStateElement(vectors[i].toArray()));
			// times
			entryEl.appendChild(createTimesElement(times[i]));
			
			rootEl.appendChild(entryEl);
		}
		
	}
	
	private void fillDataSet(Dataset dataset) {
		// create a root element
		Element rootEl = xmldoc.createElement(XMLDatasetI.ROOT);
		Attr reasAt = xmldoc.createAttribute(XMLDatasetI.REASONER);
		reasAt.setValue(reasoner);
		rootEl.setAttributeNode(reasAt);
		xmldoc.appendChild(rootEl);
		// add feature vectors
		for (int i=0; i<dataset.size(); i++) {
			DatasetEntry en = dataset.get(i); 
			if (en == null) {
				continue;
			}
			Element entryEl = xmldoc.createElement(XMLDatasetI.ONT);
			// id
			Attr entryId = xmldoc.createAttribute(XMLDatasetI.ID);
			entryId.setValue(Integer.toString(i));
			entryEl.setAttributeNode(entryId);
			// vector
			entryEl.appendChild(createStateElement(en.vector));
			// times
			entryEl.appendChild(createTimesElement(en.time));
			
			rootEl.appendChild(entryEl);
		}
		
	}

	private Element createStateElement(Double[] features) {
		Element stateEl = xmldoc.createElement(XMLDatasetI.VEC);		
		for (int i=0; i<features.length; i++) {
			String attrName = i<10 ? "s0"+i : "s"+i;
			Attr stateAttr = xmldoc.createAttribute(attrName);
			stateAttr.setValue(features[i].toString());
			stateEl.setAttributeNode(stateAttr);
		}
		return stateEl;
	}
	
	private Element createTimesElement(double val) {
		Element timesEl = xmldoc.createElement(XMLDatasetI.TIMES);
		// time
		Attr valAt = xmldoc.createAttribute(reasoner);
		valAt.setValue(Double.toString(val));
		timesEl.setAttributeNode(valAt);			
		
		return timesEl;
	}

	private void writeDataSet() {
		try {
			if(xmldoc != null) {
				// Preparing output
				Transformer t = TransformerFactory.newInstance().newTransformer();
				t.setOutputProperty(OutputKeys.INDENT, "yes");
				t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				t.setOutputProperty(OutputKeys.METHOD, "xml");
				//				t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "namespaceAnalysisReport.dtd");

				String filename = XMLDatasetI.FOLDER + "/" + 
						XMLDatasetI.ROOT + "_" + reasoner + ".xml";
				File output = new File(filename);

				// Serializing XML file
				StreamResult result = new StreamResult(output);
				DOMSource source = new DOMSource(xmldoc);
				t.transform(source, result);				
			}
			else {
				System.out.println("\tThe output document is NULL!");
			}
		}
		catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} 
		catch (TransformerException e) {
			e.printStackTrace();
		} 
		catch (DOMException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeDataSet(int i) {
		try {
			if(xmldoc != null) {
				// Preparing output
				Transformer t = TransformerFactory.newInstance().newTransformer();
				t.setOutputProperty(OutputKeys.INDENT, "yes");
				t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				t.setOutputProperty(OutputKeys.METHOD, "xml");
				//				t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "namespaceAnalysisReport.dtd");
				
				String num = null;
				if (i<10) {
					num = "00" + i;
				} else if (i>=10 && i<100) {
					num = "0" + i;
				} else {
					num = "" + i;
				}
				String filename = XMLDatasetI.FOLDER + "/modules/" + 
						reasoner + "/" + num + ".xml";
				File output = new File(filename);

				// Serializing XML file
				StreamResult result = new StreamResult(output);
				DOMSource source = new DOMSource(xmldoc);
				t.transform(source, result);				
			}
			else {
				System.out.println("\tThe output document is NULL!");
			}
		}
		catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} 
		catch (TransformerException e) {
			e.printStackTrace();
		} 
		catch (DOMException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
