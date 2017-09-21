package uk.ac.manchester.cs.sazonau.ontology.features;

import java.util.Arrays;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;

public class Solution {
	
	public static int maxvars = 170;
	public static double maxerr = 1;
	
	public static double maxmodsize;
		
	// a chromosome
	public int[] x;
	
	// a number of unselected variables
	public int vfalse;
	
	// a prediction fitness
	public double fitness;
		
	// a rank
	int rank;
	
	// a crowding distance
	double crowding;
	
	public Set<OWLAxiom> module;

	public Solution() {
		init();
	}
	
	public Solution(int[] x) {
		this.x = x;
	}
	
	private void init() {
		x = new int[maxvars];
		Arrays.fill(x, 0);
	}

	@Override
	protected Solution clone() {		
		return new Solution(Arrays.copyOf(x, x.length));
	}	

	@Override
	public String toString() {
		return "YES";//Arrays.toString(x);
	}
	
	@Override
	public boolean equals(Object obj) {
		Solution s = (Solution)obj;
		return (this.module.containsAll(s.module) &&
				s.module.containsAll(this.module));
	}
	
	public void clear() {
		module.clear();
	}
		 
}
