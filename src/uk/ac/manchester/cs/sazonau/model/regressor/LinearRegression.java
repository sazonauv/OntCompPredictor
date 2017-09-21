package uk.ac.manchester.cs.sazonau.model.regressor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import uk.ac.manchester.cs.sazonau.model.knowledge.MarkovProcess;
import uk.ac.manchester.cs.sazonau.model.knowledge.MarkovProcessEntry;
import uk.ac.manchester.cs.sazonau.ontology.features.Dataset;
import uk.ac.manchester.cs.sazonau.ontology.features.DatasetEntry;
import uk.ac.manchester.cs.sazonau.ontology.features.OntFeatureVector;

public class LinearRegression extends Regression {
	
	private double[][] x;
	private double[] y;
	
	private OLSMultipleLinearRegression regression;
	
	private double[] params;
	
	private ArrayList<Integer> inds;
	
	private ArrayList<Integer> trainset;
	private ArrayList<Integer> testset;

	public LinearRegression(MarkovProcess process) {
		super();		
		init(process);
	}
	
	public LinearRegression(Dataset dataset) {
		super();		
		init(dataset);
	}
	
	private void init(MarkovProcess process) {		
		inds = new ArrayList<Integer>();
		preprocess(process);
		avoidSingularity(process);		
		LinkedList<MarkovProcessEntry> entries = process.getEntries();
		y = new double[entries.size()];
		x = new double[y.length][entries.getFirst().getState().length];
		for (int i=0; i<entries.size(); i++) {
			MarkovProcessEntry en = entries.get(i);
			y[i] = en.getTime();
			Double[] state = en.getState();
			for (int j=0; j<state.length; j++) {
				x[i][j] = state[j];
			}
		}
//		smoothing(y);
		regression = new OLSMultipleLinearRegression();
		regression.newSampleData(y, x);		
		try {
			params = regression.estimateRegressionParameters();
		} catch (SingularMatrixException e){
			e.printStackTrace();
		}
//		double sigma = regression.estimateRegressionStandardError();
//		System.out.println("Regression standard error: "+sigma);
	}
	
	private void init(Dataset dataset) {		
		inds = new ArrayList<Integer>();
		preprocess(dataset);
		y = new double[dataset.size()];
		x = new double[y.length][inds.size()];
		for (int i=0; i<y.length; i++) {
			DatasetEntry en = dataset.get(i);
			y[i] = en.time;			
			for (int j=0; j<x[0].length; j++) {
				x[i][j] = en.vector[inds.get(j)];
			}
		}
		avoidSingularity();
		regression = new OLSMultipleLinearRegression();
		regression.newSampleData(y, x);		
		try {
			params = regression.estimateRegressionParameters();
		} catch (SingularMatrixException e){
			e.printStackTrace();
		}
	}
	
	// Assumes a full-dim vector
	public double predict(Double[] vec) {
		double sum = 0;
		for (int i=0; i<inds.size(); i++) {
			sum += params[i+1]*vec[inds.get(i)];
		}
		return sum + params[0];
	}	

	// Assumes a cut vector
	public double predict(double[] vec) {
		double sum = 0;
		for (int i=0; i<vec.length; i++) {
			sum += params[i+1]*vec[inds.get(i)];
		}
		return sum + params[0];
	}

	private void avoidSingularity(MarkovProcess process) {		
		for (MarkovProcessEntry en : process) {
			Double[] state = en.getState();
			for (int i=0; i<state.length; i++) {
				if (state[i]==0) {					
					state[i] = Math.random()/1e6;
				}
			}
		}
	}
	
	private static void avoidSingularity(double[][] x) {
		for (int i=0; i<x.length; i++) {
			for (int j=0; j<x[0].length; j++) {
				if (x[i][j]==0) {					
					x[i][j] = Math.random()/1e6;
				}
			}
		}		
	}
	
	private void avoidSingularity() {
		for (int i=0; i<x.length; i++) {
			for (int j=0; j<x[0].length; j++) {
				if (x[i][j]==0) {					
					x[i][j] = Math.random();
				}
			}
		}		
	}
	
	private void preprocess(MarkovProcess process) {		
		Double[] lastState = process.getLast().getState();		
		for (int i=0; i<lastState.length; i++) {
//			if (lastState[i]>0) {
				inds.add(i);
//			}
		}
		
//		for (MarkovProcessEntry en : process) {
//			Double[] newState = new Double[inds.size()];
//			Double[] state = en.getState();
//			for (int i=0; i<newState.length; i++) {
//				newState[i] = state[inds.get(i)];
//			}
//			en.setState(newState);
//		}
	}
	
	private void preprocess(Dataset dataset) {
		// collect feature statistics
		double[] hist = new double[dataset.get(0).vector.length];
		for (DatasetEntry en : dataset) {
			for (int i=0; i<hist.length; i++) {
				hist[i] += en.vector[i];
			}
		}
		// remove "zero" features
		for (int i=0; i<hist.length; i++) {
			if (hist[i]>0) {
				inds.add(i);
			}
		}
	}
	
	private void smoothing(double[] y) {
		int winLen = 20;
		int procLen = 500;
		int nproc = y.length/procLen;
		
		for (int i=0; i<nproc; i++) {
			for (int j=0; j<procLen; j++) {
				double sum = 0;
				int count = 0;
				for (int k=((j-winLen/2 >= 0) ? j-winLen/2 : 0);
					k<((j+winLen/2 < procLen) ? j+winLen/2 : procLen)-1; k++) {
					sum += y[i*procLen+k];
					count++;
				}
				y[i*procLen+j] = sum/count;
			}
		}
	}
	
	public Double[] getParameters() {
		int len = new OntFeatureVector().toArray().length;
		Double[] pars = new Double[len];
		Arrays.fill(pars, 0.0);		
		for (int i=0; i<inds.size(); i++) {
			pars[inds.get(i)] = params[i+1];
		}
		return pars;
	}
	
	public void crossValidation() {
		int repeats = 1000;
		int folds = 10;
		trainset = new ArrayList<Integer>();
		testset = new ArrayList<Integer>();
		
		double[] errors = new double[folds*repeats];
		for (int r=0; r<repeats; r++) {
			ArrayList<Integer> ids = new ArrayList<Integer>();
			for (int i=0; i<x.length; i++) {
				ids.add(i);
			}
			Collections.shuffle(ids);
			
			int step = ids.size()/folds;
			
			for (int i=0; i<folds; i++) {			
				trainset.clear();
				testset.clear();
				// bounds
				int lbound = i*step;
				int rbound = (i == folds-1) ? ids.size()-1 : (i+1)*step-1;
				// update train and test sets
				for (int j=0; j<lbound; j++) {
					trainset.add(ids.get(j));
				}
				// test set
				for (int j=lbound; j<=rbound; j++) {
					testset.add(ids.get(j));
				}
				// train set
				for (int j=rbound+1; j<ids.size(); j++) {
					trainset.add(ids.get(j));
				}
				// compute an error				
				errors[r*folds+i] = computeError();
			}
		}
		// compute an average error
		double mean = 0;
		for (int i=0; i<errors.length; i++) {
			mean += errors[i];
		}
		mean /= errors.length;
		// compute the deviation
		double dev = 0;
		for (int i=0; i<errors.length; i++) {
			dev += (errors[i]-mean)*(errors[i]-mean);
		}
		dev = Math.sqrt(dev/errors.length);
		System.out.println("Cross-validation results: mean error="+mean+" deviation="+dev);
	}

	private double computeError() {
		double[][] xtest = new double[testset.size()][x[0].length];
		double[] ytest = new double[xtest.length];
		double[][] xtrain = new double[trainset.size()][x[0].length];
		double[] ytrain = new double[xtrain.length];		
		for (int i=0; i<trainset.size(); i++) {
			xtrain[i] = x[trainset.get(i)];
			ytrain[i] = y[trainset.get(i)];
		}
		for (int i=0; i<testset.size(); i++) {
			xtest[i] = x[testset.get(i)];
			ytest[i] = y[testset.get(i)];
		}
		// init regression
		regression = new OLSMultipleLinearRegression();
		regression.newSampleData(ytrain, xtrain);
		try {
			params = regression.estimateRegressionParameters();
		} catch (SingularMatrixException e){
			e.printStackTrace();
		}
		// get error
		double error = 0;
		for (int i=0; i<ytest.length; i++) {
			int label = Dataset.estimateLabel(predict(xtest[i]));
			int ltrue = Dataset.estimateLabel(ytest[i]);
			if (label != ltrue) {
				error++;
			}
		}
		return error/ytest.length;
	}
	
	public static void crossValidation(LinkedList<MarkovProcess> base,
			Dataset dataset) {
		int repeats = 10;
		int folds = 10;
		ArrayList<Integer> trainset = new ArrayList<Integer>();
		ArrayList<Integer> testset = new ArrayList<Integer>();
		
		double[] errors = new double[folds*repeats];
		for (int r=0; r<repeats; r++) {
			ArrayList<Integer> ids = new ArrayList<Integer>();
			for (int i=0; i<dataset.size(); i++) {
				ids.add(i);
			}
			Collections.shuffle(ids);
			
			int step = ids.size()/folds;
			
			for (int i=0; i<folds; i++) {			
				trainset.clear();
				testset.clear();
				// bounds
				int lbound = i*step;
				int rbound = (i == folds-1) ? ids.size()-1 : (i+1)*step-1;
				// update train and test sets
				for (int j=0; j<lbound; j++) {
					trainset.add(ids.get(j));
				}
				// test set
				for (int j=lbound; j<=rbound; j++) {
					testset.add(ids.get(j));
				}
				// train set
				for (int j=rbound+1; j<ids.size(); j++) {
					trainset.add(ids.get(j));
				}
				// compute an error				
				errors[r*folds+i] = LinearRegression.computeError(base, dataset, trainset, testset);
				System.out.println("repeat #"+r+" error: "+errors[r*folds+i]);
			}
		}
		// compute an average error
		double mean = 0;
		for (int i=0; i<errors.length; i++) {
			mean += errors[i];
		}
		mean /= errors.length;
		// compute the deviation
		double dev = 0;
		for (int i=0; i<errors.length; i++) {
			dev += (errors[i]-mean)*(errors[i]-mean);
		}
		dev = Math.sqrt(dev/errors.length);
		System.out.println("Cross-validation results: mean error="+mean+" deviation="+dev);
	}

	private static double computeError(LinkedList<MarkovProcess> base,
			Dataset dataset, ArrayList<Integer> trainset,
			ArrayList<Integer> testset) {
		int nsteps = 500*10;
		double[][] xtest = new double[testset.size()][dataset.get(0).vector.length];
		double[] ytest = new double[xtest.length];
		double[][] xtrain = new double[trainset.size()*nsteps][xtest[0].length];
		double[] ytrain = new double[xtrain.length];		
		for (int i=0; i<trainset.size(); i++) {
			MarkovProcess proc = base.get(trainset.get(i));
			for (int j=0; j<nsteps; j++) {
				MarkovProcessEntry en = proc.getEntry(j);
				Double[]  state = en.getState();
				for (int k=0; k<state.length; k++) {
					xtrain[i*nsteps+j][k] = state[k];
				}
				ytrain[i*nsteps+j] = en.getTime();
			}
		}
		for (int i=0; i<testset.size(); i++) {
			Double[] vec = dataset.get(testset.get(i)).vector;
			for (int j=0; j<vec.length; j++) {
				xtest[i][j] = vec[j];
			}
			ytest[i] = dataset.get(testset.get(i)).time;
		}
		avoidSingularity(xtrain);
		// init regression
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		regression.newSampleData(ytrain, xtrain);
		double[] params = null;
		try {
			params = regression.estimateRegressionParameters();
		} catch (SingularMatrixException e){
			e.printStackTrace();
		}
		// get error
		double error = 0;
		for (int i=0; i<ytest.length; i++) {
			int label = Dataset.estimateLabel(LinearRegression.predict(params, xtest[i]));
			int ltrue = Dataset.estimateLabel(ytest[i]);
			if (label != ltrue) {
				error++;
			}
		}
		return error/ytest.length;		
	}

	private static double predict(double[] params, double[] vec) {
		double sum = 0;
		for (int i=0; i<vec.length; i++) {
			sum += params[i+1]*vec[i];
		}
		return sum + params[0];
	}

	public double getRegressionError() {
		double resSum = regression.calculateResidualSumOfSquares();
		double averErr = Math.sqrt(resSum/y.length);
		double stdErr = regression.estimateRegressionStandardError();
		double variance = regression.estimateRegressandVariance();
		System.out.println("resSum="+resSum+" averErr="+averErr+" stdErr="+stdErr+" variance="+variance);
		return stdErr;
	}
	
}
