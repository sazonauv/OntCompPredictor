package uk.ac.manchester.cs.sazonau.ontology;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * Author: Viachaslau Sazonau<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * 
 * The class used to process an ontology.
 */
public class OntologyProcessor {

	private OWLOntology ontology;
	private OWLOntologyManager manager;	


	private OWLDataFactory factory;
	private PrefixManager pm;

	public static final String base = "http://www.semanticweb.org/ontologies/2013/0/Paper-HM3.owl#";
	
	public OntologyProcessor(OWLOntology ontology, OWLOntologyManager manager) {
		super();
		this.ontology = ontology;
		this.manager = manager;			
		this.factory = manager.getOWLDataFactory();
		this.pm = new DefaultPrefixManager(base);		
	}

	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}

	public void setManager(OWLOntologyManager manager) {
		this.manager = manager;
	}
	
}
