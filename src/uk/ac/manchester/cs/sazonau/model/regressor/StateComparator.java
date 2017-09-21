package uk.ac.manchester.cs.sazonau.model.regressor;

import java.util.Comparator;

import uk.ac.manchester.cs.sazonau.model.knowledge.MarkovProcessEntry;

public class StateComparator implements Comparator<MarkovProcessEntry> {
	
	private double[] distances;	

	public StateComparator(double[] distances) {
		super();
		this.distances = distances;
	}

	@Override
	public int compare(MarkovProcessEntry o1, MarkovProcessEntry o2) {
		return Double.compare(distances[o1.getID()], distances[o2.getID()]);
	}

}
