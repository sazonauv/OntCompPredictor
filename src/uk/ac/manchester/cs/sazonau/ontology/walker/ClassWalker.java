package uk.ac.manchester.cs.sazonau.ontology.walker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.sazonau.ontology.decomp.AtomBox;

public class ClassWalker implements Walker {
	
	private OWLClass current;
	
	private Random random;
	
	private LinkedList<OWLClass> choices;
	
	private Set<OWLClass> chain;
	
	private OWLOntology ontology;
	
	private String mode;
	
	public static final int NCHOICES = 1;	
		
	public ClassWalker(OWLClass start, OWLOntology ontology, String mode) {
		super();
		this.current = start;
		this.ontology = ontology;
		this.random = new Random();
		choices = new LinkedList<OWLClass>();		
		chain = new HashSet<OWLClass>();
		this.mode = mode;
	}

	@Override
	public OWLClass next() {
		chain.add(current);		
		Set<OWLClass> toAdd = getClassesInClump(current.getReferencingAxioms(ontology));		
//		Collections.shuffle(toAdd);
		addChoices(toAdd);		
		if (choices.isEmpty() || chain.containsAll(choices)) {			
			return null;
		} else {		
			return choose();
		}		
	}
	
	private Set<OWLClass> getClassesInClump(Set<OWLAxiom> axioms) {
		Set<OWLClass> toAdd = new HashSet<OWLClass>();
		for (OWLAxiom ax : axioms) {
			Set<OWLClass> cls = ax.getClassesInSignature();
			for (OWLClass cl : cls) {
				toAdd.add(cl);
			}
		}
		return toAdd;
	}
		
	private OWLClass choose() {
		int choice = 0;
		int allChoices = choices.size();
		if (mode.equals(FULL_MEM) || allChoices<NCHOICES) {
			choice = random.nextInt(allChoices);
		} else if (mode.equals(SHORT_MEM)) {				
			choice = choices.size()-1 - random.nextInt(NCHOICES);
		}
		current = choices.remove(choice);
		return current;
	}
	
	private void addChoices(Set<OWLClass> cls) {
		for (OWLClass cl: cls) {
			if (!chain.contains(cl)) {
				choices.remove(cl);
				choices.add(cl);
			}
		}
	}
	
	public boolean isAllVisited(Set<OWLClass> cls) {
		return chain.containsAll(cls);
	}
	
	public void getNonVisited(List<OWLClass> classes) {
		classes.removeAll(chain);
	}

}
