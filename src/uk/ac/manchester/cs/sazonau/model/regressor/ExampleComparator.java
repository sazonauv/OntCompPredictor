package uk.ac.manchester.cs.sazonau.model.regressor;

import java.util.Comparator;

import uk.ac.manchester.cs.sazonau.ontology.features.DatasetEntry;

public class ExampleComparator implements Comparator<DatasetEntry> {
	
	private double[] distances;	

	public ExampleComparator(double[] distances) {
		super();
		this.distances = distances;
	}
	
	@Override
	public int compare(DatasetEntry o1, DatasetEntry o2) {
		return Double.compare(distances[o1.id], distances[o2.id]);
	}

}