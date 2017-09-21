package uk.ac.manchester.cs.sazonau.model.regressor;

import java.util.Arrays;

import uk.ac.manchester.cs.sazonau.ontology.features.Dataset;
import uk.ac.manchester.cs.sazonau.ontology.features.DatasetEntry;

public class SampleBinner {
	
	private double[] bins;
	
	private PolynomRegression regr;
	
	public SampleBinner(int nBins) {
		super();
		bins = new double[nBins];
		Arrays.fill(bins, 0);
	}

	public SampleBinner(Dataset data, double fullSize, double sizeLimit, int nBins) {
		super();
		initBins(data, fullSize, sizeLimit, nBins);
	}

	private void initBins(Dataset data, double fullSize, double sizeLimit, int nBins) {
		// a linear curve for filling gaps
		regr = new PolynomRegression(data, 1);
		bins = new double[nBins];
		int[] hist = new int[nBins];
		Arrays.fill(bins, 0);
		Arrays.fill(hist, 0);
		double step = sizeLimit/nBins;
		// fill bins
		for (DatasetEntry en : data) {
			double relSize = en.countAxioms()/fullSize;
			int bin = (int)(relSize/step);
			if (bin>=nBins) {
				continue;
			} else {
				bins[bin] += en.time;
				hist[bin]++;
			}
		}
		// fill gaps
		for (int i=0; i<bins.length; i++) {
			if (hist[i]==0) {
				bins[i] = regr.predict(i*step*fullSize);
				hist[i] = 1;
			}
		}
		// average points
		for (int i=0; i<bins.length; i++) {
			bins[i] /= hist[i];
		}
	}

	public double[] getBins() {
		return bins;
	}
	
	public double getAngle() {
		return regr.getAngle();
	}
	
	public void addBins(SampleBinner binner) {
		double[] addBins = binner.getBins();
		for (int i=0; i<bins.length; i++) {
			bins[i] += addBins[i];
		}
	}
	
	public void normalizeBins(int length) {
		for (int i=0; i<bins.length; i++) {
			bins[i] /= length;
		}
	}
}
