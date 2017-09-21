package uk.ac.manchester.cs.sazonau.ontology.decomp;

import java.util.LinkedList;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;

import uk.ac.manchester.cs.atomicdecomposition.Atom;

public class AtomBox {
	
	private int id;
	
	private boolean top = false;
	
	private boolean bottom = false;
		
	private LinkedList<AtomBox> dependents;
	
	private LinkedList<AtomBox> predecessors;
		
	private Set<OWLAxiom> axioms;
	
	private Set<OWLAxiom> module;

	public AtomBox(int id) {
		super();
		this.id = id;
		init();
	}
	
	private void init() {
		this.dependents = new LinkedList<AtomBox>();
		this.predecessors = new LinkedList<AtomBox>();
	}

	public boolean isTop() {
		return top;
	}

	public void setTop(boolean top) {
		this.top = top;
	}

	public boolean isBottom() {
		return bottom;
	}

	public void setBottom(boolean bottom) {
		this.bottom = bottom;
	}

	public LinkedList<AtomBox> getDependents() {
		return dependents;
	}

	public void setDependents(LinkedList<AtomBox> dependents) {
		this.dependents = dependents;
	}

	public Set<OWLAxiom> getAxioms() {
		return axioms;
	}

	public void setAxioms(Set<OWLAxiom> axioms) {
		this.axioms = axioms;
	}
	
	public boolean hasDependent(int id) {
		if (bottom) {
			return false;
		} else {
			for (AtomBox dep : dependents) {
				if (dep.getId() == id) {
					return true;
				}
			}
			return false;
		}
	}

	public int getId() {
		return id;
	}

	public LinkedList<AtomBox> getPredecessors() {
		return predecessors;
	}

	public void setPredecessors(LinkedList<AtomBox> predecessors) {
		this.predecessors = predecessors;
	}
	
	public void addDependent(AtomBox atom) {
		this.dependents.add(atom);
	}
	
	public void addPredecessor(AtomBox atom) {
		this.predecessors.add(atom);
	}

	@Override
	public boolean equals(Object obj) {
		return (this.id == ((AtomBox)obj).getId());
	}

	public Set<OWLAxiom> getModule() {
		return module;
	}

	public void setModule(Set<OWLAxiom> module) {
		this.module = module;
	}
	
	
}
