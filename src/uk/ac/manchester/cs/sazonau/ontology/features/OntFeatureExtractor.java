package uk.ac.manchester.cs.sazonau.ontology.features;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLEntity;

import uk.ac.manchester.cs.sazonau.ontology.decomp.AtomBox;
import uk.ac.manchester.cs.sazonau.ontology.walker.AxiomVisitor;
import uk.ac.manchester.cs.sazonau.ontology.walker.ExpressionVisitor;

public class OntFeatureExtractor {
	
	// for a sub-ontology
	private OntFeatureVector ontVector;	
	
	// for a current atom
	private OntFeatureVector atomVector;
	
	// visitors
	private ExpressionVisitor exprVisitor;
	private AxiomVisitor axiomVisitor;
	
	public OntFeatureExtractor() {		
		this.ontVector = new OntFeatureVector();
		this.atomVector = new OntFeatureVector();
		this.exprVisitor = new ExpressionVisitor();
		this.axiomVisitor = new AxiomVisitor();
	}

	// must be called once an ontology is grown
	public void extractFeatures(AtomBox atom) {		
		Set<OWLAxiom> axioms = atom.getAxioms();
//		int nDependents = atom.getDependents().size();
		extractFeatures(axioms);		
	}
	
	public void extractFeatures(OWLOntology ontology) {		
		Set<OWLAxiom> axioms = ontology.getAxioms();
		extractFeatures(axioms);
	}
	
	public void extractFeatures(Set<OWLAxiom> axioms) {		
		// clear the atom and visitors 
		atomVector.clear();
		exprVisitor.clear();
		axiomVisitor.clear();
		
		int sumDepth = 0;
//		Set<OWLEntity> signature = new HashSet<OWLEntity>();
		for (OWLAxiom axiom: axioms) {
			processAxiomSignature(axiom);
//			signature.addAll(axiom.getSignature());
			axiom.accept(axiomVisitor);
			Set<OWLClassExpression> nExprs = axiom.getNestedClassExpressions();
			int maxDepth = 0;
			if (nExprs.size() > 0) {				
				for (OWLClassExpression nExpr: nExprs) {
					int depth = processNesting(nExpr);
					if (maxDepth < depth) {
						maxDepth = depth;
					}
						    				
				}
			}
			sumDepth += maxDepth;
		}

		// update the atom vector
		atomVector.addExpressionVisitorData(exprVisitor);
		atomVector.addAxiomVisitorData(axiomVisitor);		
//		atomVector.nDependents += nDependents;
		atomVector.parsingDepth += sumDepth;
//		atomVector.signatureSize += signature.size();
	}
	
	private void processAxiomSignature(OWLAxiom axiom) {
		atomVector.nClasses += axiom.getClassesInSignature().size();
		atomVector.nObjectProperties += axiom.getObjectPropertiesInSignature().size();
		atomVector.nDataProperties += axiom.getDataPropertiesInSignature().size();		
	}
	
	public void processOntologySignature(OWLOntology ontology) {
		if (ontology != null) {
			int nDClasses = (ontology.getClassesInSignature() == null ? 0 : ontology.getClassesInSignature().size());
			int nDOProps = (ontology.getObjectPropertiesInSignature() == null ? 0 : ontology.getObjectPropertiesInSignature().size());
			int nDDProps = (ontology.getDataPropertiesInSignature() == null ? 0 : ontology.getDataPropertiesInSignature().size());
			atomVector.nDistinctClasses = nDClasses - ontVector.nDistinctClasses;
			atomVector.nDistinctObjectProperties = nDOProps - ontVector.nDistinctObjectProperties;
			atomVector.nDistinctDataProperties = nDDProps - ontVector.nDistinctDataProperties;
		}
	}
	
	private void processOntologySignature(Set<OWLAxiom> axioms) {
		if (axioms != null && axioms.size()>0) {
			Set<OWLClass> classes = new HashSet<OWLClass>();
			Set<OWLObjectProperty> objProps = new HashSet<OWLObjectProperty>();
			Set<OWLDataProperty> datProps = new HashSet<OWLDataProperty>();
			for (OWLAxiom ax : axioms) {
				classes.addAll(ax.getClassesInSignature());
				objProps.addAll(ax.getObjectPropertiesInSignature());
				datProps.addAll(ax.getDataPropertiesInSignature());
			}
			atomVector.nDistinctClasses = classes.size();
			atomVector.nDistinctObjectProperties = objProps.size();
			atomVector.nDistinctDataProperties = datProps.size();
		}
	}

	private int processNesting(OWLClassExpression expr) {		
		int maxDepth = 0;
		expr.accept(exprVisitor);
		Set<OWLClassExpression> nExprs = expr.getNestedClassExpressions();
		if (nExprs.size() > 0) {			
			for (OWLClassExpression nExpr: nExprs) {
				if (!nExpr.equals(expr)) {
					int depth = 1;
					depth += processNesting(nExpr);
					if (maxDepth < depth) {
						maxDepth = depth;
					}
				}
			}
		}
		return maxDepth;
	}
	
	public void updateOntVector() {
		updateOntVector(atomVector);
	}
	
	private void updateOntVector(OntFeatureVector vector) {
		ontVector.append(vector);		
	}

	public OntFeatureVector getOntVector() {
		return ontVector;
	}

	public OntFeatureVector getAtomVector() {
		return atomVector;
	}
	
	public void clear() {
		ontVector.clear();
		atomVector.clear();
		exprVisitor.clear();
		axiomVisitor.clear();
	}
	
	public int getSize(OWLOntology ontology) {		
		Set<OWLAxiom> axioms = ontology.getAxioms();
		return axioms.size();
	}
	
	public OntFeatureVector extract(OWLOntology ontology) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		for (OWLLogicalAxiom log : ontology.getLogicalAxioms()) {
			axioms.add(log);
		}		
		return extract(axioms);
	}
	
	public OntFeatureVector extract(Set<OWLAxiom> axioms) {
		extractFeatures(axioms);
		processOntologySignature(axioms);
		updateOntVector();
		return ontVector;
	}

	

}
