package uk.ac.manchester.cs.sazonau.model.regressor;

import uk.ac.manchester.cs.sazonau.ontology.features.Dataset;
import uk.ac.manchester.cs.sazonau.ontology.features.DatasetEntry;
import edu.uci.lasso.LassoFit;
import edu.uci.lasso.LassoFitGenerator;

public class Lasso extends Regression {
	
	private LassoFit fit;
	
	private int maxFeatures;	

	public Lasso(Dataset dataset, int maxFeatures) {
		super();
		this.maxFeatures = maxFeatures;
		init(dataset);
	}

	private void init(Dataset dataset) {
		int nObservations = dataset.size();
		int nFeatures = dataset.get(0).vector.length;
		double[] y = new double[nObservations];		
		float[][] x = new float[y.length][nFeatures];
		for (int i=0; i<y.length; i++) {
			DatasetEntry en = dataset.get(i);
			y[i] = en.time;
			for (int j=0; j<x[0].length; j++) {
				x[i][j] = en.vector[j].floatValue();
			}
		}
		
		LassoFitGenerator fitGenerator = new LassoFitGenerator();
				
		try {
			fitGenerator.init(nFeatures, nObservations);
		} catch (Exception e) {
			System.out.println("Exception while LASSO initialization");
		}
		
		for (int i=0; i<y.length; i++) {
			fitGenerator.setObservationValues(i, x[i]);
			fitGenerator.setTarget(i, y[i]);
		}
		
		/*
		 * Generate the Lasso fit. The -1 arguments means that
		 * there would be no limit on the maximum number of 
		 * features per model
		 */
		fit = fitGenerator.fit(maxFeatures);
		System.out.println(fit);
	}

	@Override
	public double predict(Double[] vec) {
		
		return 0;
	}
	
	

}
