package uk.ac.manchester.cs.sazonau.ontology.decomp;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.manchester.cs.jfact.JFactReasoner;
import uk.ac.manchester.cs.jfact.split.ModuleType;
import uk.ac.manchester.cs.jfact.split.TOntologyAtom;

public class AtomicDecomposition {
	
	private JFactReasoner jfact;
	
	private LinkedList<AtomBox> atoms;
	
	public AtomicDecomposition(JFactReasoner jfact) {
		super();
		this.jfact = jfact;
		init();		
	}	
	
	private void init() {		
		int nAtoms = jfact.getAtomicDecompositionSize(false, ModuleType.M_BOT);
		atoms = new LinkedList<AtomBox>();
		initAtoms(nAtoms);
	}	

	private void initAtoms(int nAtoms) {		
		setAxioms(nAtoms);
		setDependents();		
		setPredecessors();		
	}
	
	private void setAxioms(int nAtoms) {
		for (int i=0; i<nAtoms; i++) {
			AtomBox atom = new AtomBox(i);
			atom.setAxioms(jfact.getAtomAxioms(i));			
			atoms.add(atom);
		}
	}	
	
	private void setDependents() {
		for (int i=0; i<atoms.size(); i++) {
			Set<AtomBox> dependents = new HashSet<AtomBox>();
			Set<TOntologyAtom> deps = jfact.getAtomDependents(i);
			if (deps.size() > 0) {
				for (TOntologyAtom dep : deps) {
					dependents.add(atoms.get(dep.getId()));				
				}
				atoms.get(i).setDependents(new LinkedList<AtomBox>(dependents));
			} else {
				atoms.get(i).setBottom(true);
				atoms.get(i).setDependents(new LinkedList<AtomBox>());
			}
		}
	}
	
	private void setPredecessors() {
		for (int i=0; i<atoms.size(); i++) {
			Set<AtomBox> preds = new HashSet<AtomBox>();
			for (AtomBox atom : atoms) {
				if (atom.hasDependent(i)) {
					preds.add(atom);
				}
			}
			if (preds.size() > 0) {
				atoms.get(i).setPredecessors(new LinkedList<AtomBox>(preds));
			} else {
				atoms.get(i).setTop(true);
				atoms.get(i).setPredecessors(new LinkedList<AtomBox>());
			}
		}
	}
			
	public JFactReasoner getJfact() {
		return jfact;
	}
	
	public LinkedList<AtomBox> getBottomAtoms() {
		LinkedList<AtomBox> bottoms = new LinkedList<AtomBox>();
		for (AtomBox atom : atoms) {
			if (atom.isBottom() && !atom.isTop()) {
				bottoms.add(atom);
			}
		}
		return bottoms;
	}
	
	public LinkedList<AtomBox> getTopAtoms() {
		LinkedList<AtomBox> tops = new LinkedList<AtomBox>();
		for (AtomBox atom : atoms) {
			if (atom.isTop() && !atom.isBottom()) {
				tops.add(atom);
			}
		}
		return tops;
	}
	
	public LinkedList<AtomBox> getTopBottomAtoms() {
		LinkedList<AtomBox> topBottoms = new LinkedList<AtomBox>();
		for (AtomBox atom : atoms) {
			if (atom.isTop() && atom.isBottom()) {
				topBottoms.add(atom);
			}
		}
		return topBottoms;
	}

	public LinkedList<AtomBox> getAtoms() {
		return atoms;
	}
	
	public AtomBox getAtom(int id) {
		return atoms.get(id);
	}
		
}
