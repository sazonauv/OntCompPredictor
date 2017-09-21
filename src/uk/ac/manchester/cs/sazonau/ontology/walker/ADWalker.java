package uk.ac.manchester.cs.sazonau.ontology.walker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import uk.ac.manchester.cs.sazonau.ontology.decomp.AtomBox;

public class ADWalker implements Walker {
		
	private AtomBox current;
	
	private Random random;
	
	private LinkedList<AtomBox> choices;
	
	private Set<AtomBox> chain;
	
	private String mode;
	
	public static final int NCHOICES = 1;		
			
	public ADWalker(AtomBox start, String mode) {
		super();		
		this.current = start;		
		this.random = new Random();
		choices = new LinkedList<AtomBox>();		
		chain = new HashSet<AtomBox>();
		this.mode = mode;
	}

	@Override
	public AtomBox next() {
		chain.add(current);
		List<AtomBox> toAdd = new ArrayList<AtomBox>();
		if (!current.isTop()) {
			toAdd.addAll(current.getPredecessors());
		}
		if (!current.isBottom()) {			
			toAdd.addAll(current.getDependents());
		}
		Collections.shuffle(toAdd);
		addChoices(toAdd);
		
		if (choices.isEmpty() || chain.containsAll(choices)) {			
			return null;
		} else {		
			return choose();
		}		
	}
	
	private AtomBox choose() {
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
	
	private void addChoices(List<AtomBox> atoms) {
		for (AtomBox atom: atoms) {
			if (!chain.contains(atom)) {
				choices.remove(atom);
				choices.add(atom);
			}
		}
	}
	
	public AtomBox getCurrent() {
		return current;
	}
		
	public void setCurrent(AtomBox atom) {
		current = atom;
	}
	
	public void printChoices() {
		String str = " [";
		for (AtomBox atom: choices) {
			str += (atom.getId() + ","); 
		}
		str += "] ";
		System.out.print(str);
	}

}
