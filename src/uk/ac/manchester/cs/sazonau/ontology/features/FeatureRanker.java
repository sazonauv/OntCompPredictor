package uk.ac.manchester.cs.sazonau.ontology.features;

public class FeatureRanker {
		
	private int dim;
	
	private double[][] vectors;
	private double[] labels;

	public FeatureRanker(Dataset dataset) {
		super();		
		init(dataset);
	}
	
	private void init(Dataset dataset) {
		this.dim = dataset.get(0).vector.length;
		vectors = new double[dataset.size()][dim];
		labels = new double[vectors.length];
		for (int i=0; i<dataset.size(); i++) {
			DatasetEntry en = dataset.get(i);
			for (int j=0; j<en.vector.length; j++) {
				vectors[i][j] = en.vector[j];
			}
			labels[i] = en.time;
		}
	}
		
	public FeatureRanker(double[][] vectors, double[] labels) {
		super();
		this.vectors = vectors;
		this.labels = labels;
		this.dim = vectors[0].length;
	}

	public void rankAll() {		
		for (int f=0; f<dim; f++) {
			double rank = rank(f);
			System.out.println(rank);
		}
	}
	
	public double rank(int f) {
		double covf = cov(f);
		// the absolute value we need
		if (covf<0) {
			covf = -covf;
		}
		double stdf = std(f);
		double stdt = std(-1);
		if (stdf == 0) {
			return 0;
		} else {
			return covf/(stdf*stdt);
		}		
	}
	
	private double cov(int f) {
		double fmean = mean(f);
		double tmean = mean(-1);
		double cov = 0;		
		for (int i=0; i<vectors.length; i++) {
			cov += (vectors[i][f]-fmean)*(labels[i]-tmean);
		}		
		return cov;
	}
	
	private double std(int f) {
		double mean = mean(f);
		double dev = 0;
		if (f>=0 && f<dim) {
			for (int i=0; i<vectors.length; i++) {
				dev += (vectors[i][f]-mean)*(vectors[i][f]-mean);
			}
		} else {
			for (int i=0; i<labels.length; i++) {
				dev += (labels[i]-mean)*(labels[i]-mean);
			}
		}
		return Math.sqrt(dev);
	}	
	
	private double mean(int f) {
		double mean = 0;
		if (f>=0 && f<dim) {
			for (int i=0; i<vectors.length; i++) {				
				mean += vectors[i][f];
			}
		} else {
			for (int i=0; i<labels.length; i++) {				
				mean += labels[i];
			}
		}
		return mean/vectors.length;
	}

}
