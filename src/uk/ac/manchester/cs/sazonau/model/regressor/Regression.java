package uk.ac.manchester.cs.sazonau.model.regressor;

import uk.ac.manchester.cs.sazonau.ontology.features.DatasetEntry;

public abstract class Regression {
	
	public abstract double predict(Double[] vec);	
	
	public static double countAxioms(Double[] vec) {
		if (vec.length == 1) {
			return vec[0];
		} else {
			double nAxioms = 0;
			for (int i=18; i<51; i++) {
				nAxioms += vec[i];
			}
			return nAxioms;
		}
	}

}
