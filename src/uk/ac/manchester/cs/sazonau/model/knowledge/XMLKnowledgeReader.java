package uk.ac.manchester.cs.sazonau.model.knowledge;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLKnowledgeReader {	
	
	public static LinkedList<MarkovProcess> read(File file) {
		BufferedInputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		SAX2XMLHandler handler = new SAX2XMLHandler();
		// Initialise factory for processing
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		SAXParser parser = null;
//		long begin = System.currentTimeMillis();
		try {  
			parser = factory.newSAXParser();			
			parser.parse(new InputSource(is),handler);
		} catch (ParserConfigurationException e) {
			System.out.println(e.toString());
			// This exception should also be thrown out,
			// however declaration of method computeResult must be changed in order to do this,
			// hence it would cause compilation errors in files which use this method.
			// Therefore, SAXEception is thrown here to match the method interface.			
		} catch (SAXException e) {
			System.out.println(e.toString());
			// thrown properly			
		} catch (IOException e) {
			System.out.println(e.toString());
			// This exception should also be thrown out,
			// however declaration of method computeResult must be changed in order to do this,
			// hence it would cause compilation errors in files which use this method.
			// Therefore, SAXEception is thrown here to match the method interface.			
		}
		
//		System.out.println("    .... parsed o.k.");		
		
//		long end = System.currentTimeMillis();
//		System.out.println("Execution time on your machine: " + (end - begin) + " millisec");
		
		return handler.getBase();
	}

}
