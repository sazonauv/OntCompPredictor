package uk.ac.manchester.cs.sazonau.ontology.walker;

import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

public class AxiomVisitor extends OWLAxiomVisitorAdapter {
	
	public int nSubClassOfAxioms;
	public int nDisjointClassesAxioms;
	public int nEquivalentClassesAxioms;
	public int nAsymmetricObjectPropertyAxioms;
	public int nSymmetricObjectPropertyAxioms;
	public int nInverseObjectPropertiesAxioms;
	public int nInverseFunctionalObjectPropertyAxioms;
	public int nFunctionalObjectPropertyAxioms;
	public int nTransitiveObjectPropertyAxioms;
	public int nFunctionalDataPropertyAxioms;
	public int nDataPropertyRangeAxioms;
	public int nDataPropertyDomainAxioms;
	public int nObjectPropertyDomainAxioms;
	public int nObjectPropertyRangeAxioms;
	public int nSubObjectPropertyOfAxioms; 
	public int nSubDataPropertyOfAxioms;
	public int nReflexiveObjectPropertyAxioms;
	public int nEquivalentObjectPropertiesAxioms;
	public int nEquivalentDataPropertiesAxioms;
	public int nDisjointObjectPropertiesAxioms;
	public int nDisjointDataPropertiesAxioms;
	
	public int nDifferentIndividualsAxioms;
	public int nSameIndividualAxioms;
	
	public int nClassAssertionAxioms;
	public int nObjectPropertyAssertionAxioms;
	public int nDataPropertyAssertionAxioms;
	
	public int nDisjointUnionAxioms;
	public int nHasKeyAxioms;
	public int nIrreflexiveObjectPropertyAxioms;
	public int nNegativeDataPropertyAssertionAxioms;
	public int nNegativeObjectPropertyAssertionAxioms;
	public int nSubPropertyChainOfAxioms;
	public int nSWRLRules;
	
	@Override
	public void visit(OWLSubClassOfAxiom axiom) {
		nSubClassOfAxioms++;
	}
	
	@Override
	public void visit(OWLDisjointClassesAxiom axiom) {
		nDisjointClassesAxioms++;
	}
	
	@Override
	public void visit(OWLEquivalentClassesAxiom axiom) {
		nEquivalentClassesAxioms++;
	}
	
	@Override
	public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
		nAsymmetricObjectPropertyAxioms++;
	}
	
	@Override
	public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
		nSymmetricObjectPropertyAxioms++;
	}
	
	@Override
	public void visit(OWLInverseObjectPropertiesAxiom axiom) {
		nInverseObjectPropertiesAxioms++;
	}
	
	@Override
	public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		nInverseFunctionalObjectPropertyAxioms++;
	}
	
	@Override
	public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
		nFunctionalObjectPropertyAxioms++;
	}
		
	@Override
	public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
		nTransitiveObjectPropertyAxioms++;
	}
	
	@Override
	public void visit(OWLFunctionalDataPropertyAxiom axiom) {
		nFunctionalDataPropertyAxioms++;
	}
	
	@Override
	public void visit(OWLDataPropertyRangeAxiom axiom) {
		nDataPropertyRangeAxioms++;
	}
	
	@Override
	public void visit(OWLDataPropertyDomainAxiom axiom) {
		nDataPropertyDomainAxioms++;
	}
	
	@Override
	public void visit(OWLObjectPropertyDomainAxiom axiom) {
		nObjectPropertyDomainAxioms++;
	}
	
	@Override
	public void visit(OWLObjectPropertyRangeAxiom axiom) {
		nObjectPropertyRangeAxioms++;
	}
	
	@Override
	public void visit(OWLSubObjectPropertyOfAxiom axiom) {
		nSubObjectPropertyOfAxioms++;
	}
	
	@Override
	public void visit(OWLSubDataPropertyOfAxiom axiom) {
		nSubDataPropertyOfAxioms++;
	}
	
	@Override
	public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
		nReflexiveObjectPropertyAxioms++;
	}
	
	@Override
	public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		nEquivalentObjectPropertiesAxioms++;
	}
	
	@Override
	public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
		nEquivalentDataPropertiesAxioms++;
	}
	
	@Override
	public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
		nDisjointObjectPropertiesAxioms++;
	}
	
	@Override
	public void visit(OWLDisjointDataPropertiesAxiom axiom) {
		nDisjointDataPropertiesAxioms++;
	}
	
	@Override
	public void visit(OWLDifferentIndividualsAxiom axiom) {
		nDifferentIndividualsAxioms++;
	}
	
	@Override
	public void visit(OWLSameIndividualAxiom axiom) {
		nSameIndividualAxioms++;
	}
	
	@Override
	public void visit(OWLClassAssertionAxiom axiom) {
		nClassAssertionAxioms++;
	}
	
	@Override
	public void visit(OWLObjectPropertyAssertionAxiom axiom) {
		nObjectPropertyAssertionAxioms++;
	}
	
	@Override
	public void visit(OWLDataPropertyAssertionAxiom axiom) {
		nDataPropertyAssertionAxioms++;
	}
	
	@Override
	public void visit(OWLDisjointUnionAxiom axiom) {
		nDisjointUnionAxioms++;
	}
			
	@Override
	public void visit(OWLHasKeyAxiom axiom) {
		nHasKeyAxioms++;
	}

	@Override
	public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		nIrreflexiveObjectPropertyAxioms++;
	}

	@Override
	public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		nNegativeDataPropertyAssertionAxioms++;
	}

	@Override
	public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		nNegativeObjectPropertyAssertionAxioms++;
	}

	@Override
	public void visit(OWLSubPropertyChainOfAxiom axiom) {
		nSubPropertyChainOfAxioms++;
	}

	@Override
	public void visit(SWRLRule rule) {
		nSWRLRules++;
	}

	public void clear() {
		nSubClassOfAxioms = 0;
		nDisjointClassesAxioms = 0;
		nEquivalentClassesAxioms = 0;
		nAsymmetricObjectPropertyAxioms = 0;
		nSymmetricObjectPropertyAxioms = 0;
		nInverseObjectPropertiesAxioms = 0;
		nInverseFunctionalObjectPropertyAxioms = 0;
		nFunctionalObjectPropertyAxioms = 0;
		nTransitiveObjectPropertyAxioms = 0;
		nFunctionalDataPropertyAxioms = 0;
		nDataPropertyRangeAxioms = 0;
		nDataPropertyDomainAxioms = 0;
		nObjectPropertyDomainAxioms = 0;
		nObjectPropertyRangeAxioms = 0;
		nSubObjectPropertyOfAxioms = 0;
		nSubDataPropertyOfAxioms = 0;
		nReflexiveObjectPropertyAxioms = 0;
		nEquivalentObjectPropertiesAxioms = 0;
		nEquivalentDataPropertiesAxioms = 0;
		nDisjointObjectPropertiesAxioms = 0;
		nDisjointDataPropertiesAxioms = 0;
		nDifferentIndividualsAxioms = 0;
		nSameIndividualAxioms = 0;
		nClassAssertionAxioms = 0;
		nObjectPropertyAssertionAxioms = 0;
		nDataPropertyAssertionAxioms = 0;		
		nDisjointUnionAxioms = 0;
		nHasKeyAxioms = 0;
		nIrreflexiveObjectPropertyAxioms = 0;
		nNegativeDataPropertyAssertionAxioms = 0;
		nNegativeObjectPropertyAssertionAxioms = 0;
		nSubPropertyChainOfAxioms = 0;
		nSWRLRules = 0;
	}

}
