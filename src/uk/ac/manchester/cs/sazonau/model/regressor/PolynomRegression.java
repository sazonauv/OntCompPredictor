package uk.ac.manchester.cs.sazonau.model.regressor;

import java.util.LinkedList;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.CurveFitter;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.LevenbergMarquardtOptimizer;

import uk.ac.manchester.cs.sazonau.model.knowledge.MarkovProcess;
import uk.ac.manchester.cs.sazonau.model.knowledge.MarkovProcessEntry;
import uk.ac.manchester.cs.sazonau.ontology.features.Dataset;
import uk.ac.manchester.cs.sazonau.ontology.features.DatasetEntry;

public class PolynomRegression extends Regression {
	
	private double[][] points;
	
	private PolynomialFunction fitted;
	
	private int degree;	

	public PolynomRegression(Dataset dataset, int degree) {
		super();
		this.degree = degree;
		initPoints(dataset);
//		normalizePoints();
		initFixedFunction();
	}
	
	public PolynomRegression(Dataset dataset, int degree, double sizeLimit, double ontSize) {
		super();
		this.degree = degree;
		initPoints(dataset, sizeLimit, ontSize);
//		normalizePoints();
		initFixedFunction();
	}	
	
	public PolynomRegression(double[][] data, int degree) {
		super();
		this.degree = degree;
		initPoints(data);
		initFunction();
	}
	
	private void initPoints(double[][] data) {
		points = data;
	}
	
	private void initPoints(Dataset dataset) {
		points = new double[dataset.size()][2];
		for (int i=0; i<points.length; i++) {
			DatasetEntry en = dataset.get(i);
			points[i][0] = countAxioms(en.vector);
			points[i][1] = en.time;			
		}
	}
	
	private void initPoints(Dataset dataset, double sizeLimit, double ontSize) {
		// remove oversized points
		Dataset limited = new Dataset();
		for (DatasetEntry en : dataset) {
			double size = en.countAxioms();
			if (size/ontSize<=sizeLimit) {
				limited.add(en);
			}
		}
		// fill points
		if (limited.isEmpty() || limited.isTrivial()) {
			points = new double[dataset.size()][2];
			for (int i=0; i<points.length; i++) {
				DatasetEntry en = dataset.get(i);
				points[i][0] = countAxioms(en.vector);
				points[i][1] = en.time;			
			}
		} else {
			points = new double[limited.size()][2];
			for (int i=0; i<points.length; i++) {
				DatasetEntry en = limited.get(i);
				points[i][0] = countAxioms(en.vector);
				points[i][1] = en.time;			
			}
		}
	}	
	
	private void normalizePoints() {
		// find min
		double xmin = Integer.MAX_VALUE;
		for (int i=0; i<points.length; i++) {
			if (xmin > points[i][0]) {
				xmin = points[i][0];
			}
		}
		// set min to zero
		for (int i=0; i<points.length; i++) {
			points[i][0] -= xmin;
		}
	}
	
	private void initFunction() {
		CurveFitter fitter = new CurveFitter(new LevenbergMarquardtOptimizer());
		for (int i=0; i<points.length; i++) {
			fitter.addObservedPoint(points[i][0], points[i][1]);
		}
		
		double[] init = new double[degree+1];		
		double[] best = fitter.fit(new PolynomialFunction.Parametric(), init);
		fitted = new PolynomialFunction(best);
	}
	
	private void initFixedFunction() {
		PolynomialFunction[] fits = new PolynomialFunction[degree];
		for (int k=1; k<=fits.length; k++) {
			double xysum = 0;
			double xxsum = 0;
			for (int i=0; i<points.length; i++) {
				double x = 1;
				for (int d=0; d<k; d++) {
					x *= points[i][0];
				}
				xysum += x*points[i][1];
				xxsum += x*x;
			}
			double a = xysum/xxsum;
			double[] init = new double[k+1];
			init[k] = a;
			fits[k-1] = new PolynomialFunction(init);
		}
		
		double minErr = Integer.MAX_VALUE;
		for (int k=0; k<fits.length; k++) {
			double err = computeError(fits[k]);
			if (minErr > err) {
				minErr = err;
				fitted = fits[k];
			}
		}
		
	}
			
	public double predict(Double[] vec) {
		return fitted.value(countAxioms(vec));
	}
	
	public double predict(double size) {
		return fitted.value(size);
	}
	
	public void printCoefficients() {
		double[] coeffs = fitted.getCoefficients();
		for (int i=0; i<coeffs.length; i++) {
			System.out.print(coeffs[i]+", ");
		}
		System.out.println();
	}
	
	private double computeError(PolynomialFunction func) {
		double mse = 0;
		for (int i=0; i<points.length; i++) {
			double val = func.value(points[i][0]);
			mse += (val - points[i][1])*(val - points[i][1]);
		}
		return Math.sqrt(mse/points.length);
	}

	public PolynomialFunction getFitted() {
		return fitted;
	}
	
	public double getAngle() {
		double[] params = fitted.getCoefficients();
		// 0 is a constant, 1 is an angle
		return params[1];
	}
}
