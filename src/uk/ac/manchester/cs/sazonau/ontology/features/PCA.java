package uk.ac.manchester.cs.sazonau.ontology.features;

import java.util.Arrays;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class PCA {
	
	public static double POVTHR = 0.85;	
	
	private double[] centre;
	
	private Matrix projection;
	
	private double[][] encodedData;
	
	private Dataset dataset;
	
	private int nComponents;

	public PCA(Dataset dataset) {
		super();
		init(dataset);
	}
	
	public PCA(Dataset dataset, int nComponents) {
		super();
		this.nComponents = nComponents;
		init(dataset);
	}
	
	private void preprocess(Dataset data) {
		this.dataset = new Dataset();
		for (int i=0; i<data.size(); i++) {
			if (i!=68 && i!=86 && i!=102 && i!=111 && i!=118)
				this.dataset.add(data.get(i));
		}			
	}
	
	private void init(Dataset data) {
		preprocess(data);
		double[][] Xm = centralize();
		// calculate the covariance matrix
		int N = dataset.size();
		Matrix C = new Matrix(Xm);
		Matrix S = C.times(C.transpose()).times(1.0/N);
		EigenvalueDecomposition edec = S.eig();		
		Matrix V = edec.getV();
		double[] eigenValues = edec.getRealEigenvalues();
		// sort eigenvalues descending
		AIComparator comp = new AIComparator(eigenValues, AIComparator.desc);
		Integer[] inds = comp.createIndexArray();
		Arrays.sort(inds, comp);
		// print
		printEigens(eigenValues, inds);

		// estimate the number of components
		if (nComponents<1) {			
			nComponents = estimateComponentsNumber(eigenValues, inds);
		}
		
		double[][] components = V.transpose().getArray();
		initProjection(components, inds);
		
		// encode the training dataset
		Matrix Z = projection.times(C);
		encodedData = Z.transpose().getArray();
	}
	
	private double[][] centralize() {
		int d = dataset.get(0).vector.length;
		int N = dataset.size();
		double[][] X = new double[d][N];
		double[][] Xm = new double[d][N];
		// centralize the dataset
		centre = new double[d];
		Arrays.fill(centre, 0);
		for (DatasetEntry en: dataset) {
			for (int i=0; i<d; i++) {
				centre[i] += en.vector[i];
			}
		}
		for (int i=0; i<d; i++) {
			centre[i] /= N;
		}
		for (int j=0; j<N; j++) {
			DatasetEntry en = dataset.get(j);
			for (int i=0; i<d; i++) {
				X[i][j] = en.vector[i];
				Xm[i][j] = en.vector[i] - centre[i];
			}
		}
		return Xm;
	}
	
	private void initProjection(double[][] components, Integer[] inds) {
		// init the projection matrix		
		double[][] eigenVectors = new double[nComponents][components[0].length];
		for (int i=0; i<nComponents; i++) {
			eigenVectors[i] = components[inds[i]];
		}
		// print PC1
		System.out.println("\nPC1:");
		for (int i=0; i<eigenVectors[0].length; i++) {
			System.out.println(eigenVectors[0][i]);
		}
		projection = new Matrix(eigenVectors);
	}
	
	public void printEigens(double[] eigenValues, Integer[] inds) {
		System.out.println("Variances: ");
		double sumv = 0;
		for (int i=0; i<eigenValues.length; i++) {
			sumv += eigenValues[i];
		}
		for (int i=0; i<eigenValues.length; i++) {
			System.out.println(eigenValues[inds[i]]/sumv);
		}
	}
	
	private static int estimateComponentsNumber(double[] eigenValues, Integer[] inds) {
		double sumv = 0;
		for (int i=0; i<eigenValues.length; i++) {
			sumv += eigenValues[i];
		}		
		double pov = 0;
		int k = 0;
		while (pov <= POVTHR) {
			pov += eigenValues[inds[k]]/sumv;			
			k++;
		}
		double recErr = 1 - pov;
		System.out.println("Components number = "+k+" / Reconstruction error = "+recErr);
		return k;
	}
	
	public Double[] encode(Double[] vector) {
		double[][] cvector = new double[vector.length][1];
		for (int i=0; i<vector.length; i++) {
			cvector[i][0] = vector[i] - centre[i];
		}
		Matrix X = new Matrix(cvector);
		Matrix Z = projection.times(X);
		double[][] zarr = Z.getArray();
		Double[] encoding = new Double[zarr.length];
		for (int i=0; i<zarr.length; i++) {
			encoding[i] = zarr[i][0];
		}
		return encoding;
	}
	
	public double[][] getEncodedData() {		
		return encodedData;
	}

	public int getnComponents() {
		return nComponents;
	}

	public Dataset getDataset() {
		return dataset;
	}
	
	

}
