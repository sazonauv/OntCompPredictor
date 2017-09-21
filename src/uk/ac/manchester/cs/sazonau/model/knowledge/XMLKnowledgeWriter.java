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
import org.w3c.dom.Node;

import uk.ac.manchester.cs.sazonau.ontology.decomp.AtomBox;

public class XMLKnowledgeWriter {	

	private MarkovProcess process;

	private Document xmldoc;

	public XMLKnowledgeWriter(MarkovProcess process) {
		super();
		this.process = process;
		createDocumentStub();
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

	private void fillDocument() {
		// create a root element
		Element rootEl = xmldoc.createElement(XMLKnowledgeI.ROOT);
		// ID
		Attr idAttr = xmldoc.createAttribute(XMLKnowledgeI.ID);
		idAttr.setValue(process.getID().toString());
		rootEl.setAttributeNode(idAttr);
		// Reasoner
		Attr reasAttr = xmldoc.createAttribute(XMLKnowledgeI.REASONER);
		reasAttr.setValue(process.getReasoner());
		rootEl.setAttributeNode(reasAttr);
		// Ontology
		Attr ontAttr = xmldoc.createAttribute(XMLKnowledgeI.ONTOLOGY);
		ontAttr.setValue(process.getOntology());
		rootEl.setAttributeNode(ontAttr);
		xmldoc.appendChild(rootEl);
		// fill entries
		for (MarkovProcessEntry entry: process) {			
			Element entryEl = xmldoc.createElement(XMLKnowledgeI.ENTRY);
			Attr entryId = xmldoc.createAttribute(XMLKnowledgeI.ID);
			entryId.setValue(entry.getID().toString());
			entryEl.setAttributeNode(entryId);
			entryEl.appendChild(createStateElement(entry.getState()));			
			entryEl.appendChild(createActionElement(entry.getAction()));			
			entryEl.appendChild(createRewardElement(entry.getReward()));
			entryEl.appendChild(createTimeElement(entry.getTime()));
			entryEl.appendChild(createAtomElement(entry.getAtom()));
			rootEl.appendChild(entryEl);
		}
	}	

	private Element createStateElement(Double[] features) {
		Element stateEl = xmldoc.createElement(XMLKnowledgeI.STATE);		
		for (int i=0; i<features.length; i++) {
			String attrName = i<10 ? "s0"+i : "s"+i;
			Attr stateAttr = xmldoc.createAttribute(attrName);
			stateAttr.setValue(features[i].toString());
			stateEl.setAttributeNode(stateAttr);
		}
		return stateEl;
	}

	private Element createActionElement(Double[] features) {
		Element actionEl = xmldoc.createElement(XMLKnowledgeI.ACTION);		
		for (int i=0; i<features.length; i++) {
			String attrName = i<10 ? "a0"+i : "a"+i;
			Attr actionAttr = xmldoc.createAttribute(attrName);
			actionAttr.setValue(features[i].toString());
			actionEl.setAttributeNode(actionAttr);
		}
		return actionEl;
	}

	private Element createRewardElement(Double reward) {
		Element rewardEl = xmldoc.createElement(XMLKnowledgeI.REWARD);
		Attr rewardAttr = xmldoc.createAttribute("r");
		rewardAttr.setValue(reward.toString());
		rewardEl.setAttributeNode(rewardAttr);
		return rewardEl;
	}
	
	private Node createTimeElement(Double time) {
		Element timeEl = xmldoc.createElement(XMLKnowledgeI.TIME);
		Attr timeAttr = xmldoc.createAttribute("t");
		timeAttr.setValue(time.toString());
		timeEl.setAttributeNode(timeAttr);
		return timeEl;
	}
	
	private Node createAtomElement(AtomBox atom) {
		Element atomEl = xmldoc.createElement(XMLKnowledgeI.ATOM);
		Attr idAttr = xmldoc.createAttribute(XMLKnowledgeI.ID);
		idAttr.setValue(""+atom.getId());
		atomEl.setAttributeNode(idAttr);
		Attr depsAttr = xmldoc.createAttribute(XMLKnowledgeI.DEPS);
		depsAttr.setValue(""+atom.getDependents().size());
		atomEl.setAttributeNode(depsAttr);
		Attr predsAttr = xmldoc.createAttribute(XMLKnowledgeI.PREDS);
		predsAttr.setValue(""+atom.getPredecessors().size());
		atomEl.setAttributeNode(predsAttr);
		return atomEl;
	}

	public void writeToFile(String reasoner, int id) {
		fillDocument();
		try {
			if(xmldoc != null) {
				// Preparing output
				Transformer t = TransformerFactory.newInstance().newTransformer();
				t.setOutputProperty(OutputKeys.INDENT, "yes");
				t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				t.setOutputProperty(OutputKeys.METHOD, "xml");
//				t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "namespaceAnalysisReport.dtd");
				String folderName = XMLKnowledgeI.FOLDER + reasoner + "/" + id + "/";
				String fileName = folderName + XMLKnowledgeI.ROOT + process.getID() + ".xml";
				File folder = new File(folderName);
				if (!folder.exists()) {
					folder.mkdir();
				}
				File output = new File(fileName);
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
	
	public void writeToFile() {
		fillDocument();
		try {
			if(xmldoc != null) {
				// Preparing output
				Transformer t = TransformerFactory.newInstance().newTransformer();
				t.setOutputProperty(OutputKeys.INDENT, "yes");
				t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				t.setOutputProperty(OutputKeys.METHOD, "xml");
//				t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "namespaceAnalysisReport.dtd");
				
				String filename = XMLKnowledgeI.FOLDER + "/" + 
						XMLKnowledgeI.ROOT + process.getID() + ".xml";
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
