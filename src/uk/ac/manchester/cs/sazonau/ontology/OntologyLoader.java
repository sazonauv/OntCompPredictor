package uk.ac.manchester.cs.sazonau.ontology;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologyLoader {

	public OWLOntology loadOntology(OWLOntologyManager manager, IRI ontologyIRI) throws Exception{
		OWLOntology ontology = null;    	
		ontology = manager.loadOntologyFromOntologyDocument(ontologyIRI);
		return  ontology;
	}

}
