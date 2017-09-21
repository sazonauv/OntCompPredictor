package uk.ac.manchester.cs.sazonau.model.regressor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;

import uk.ac.manchester.cs.sazonau.model.knowledge.MarkovProcess;
import uk.ac.manchester.cs.sazonau.model.knowledge.MarkovProcessEntry;
import uk.ac.manchester.cs.sazonau.ontology.features.OntFeatureVector;

public class NearestNeighbour {	
	
	// Knowledge base as a set of entries
	private LinkedList<MarkovProcess> data;
	
	// A test data set of entries
	private LinkedList<MarkovProcessEntry> test;
	
	private PriorityQueue<MarkovProcessEntry> queue;
	
	private ArrayList<MarkovProcessEntry> neighbours;
	
	private double[] distances;
	
	private int k;

	public NearestNeighbour(LinkedList<MarkovProcess> data, LinkedList<MarkovProcessEntry> test, int k) {
		super();
		this.data = data;
		this.test = test;		
		this.k = k;
		init();
	}
		
	public NearestNeighbour(ArrayList<OntFeatureVector> dataset, int k) {
		super();
		this.k = k;
	}

	private void init() {		
		int index = 0;
		for (MarkovProcess proc : data) {
			for (MarkovProcessEntry entry : proc) {
				entry.setID(index);								
				index++;
			}
		}
		distances = new double[index];
		Arrays.fill(distances, Double.MAX_VALUE);
		queue = new PriorityQueue<MarkovProcessEntry>(k, new StateComparator(distances));
		neighbours = new ArrayList<MarkovProcessEntry>();
	}
		
	private LinkedList<MarkovProcessEntry> closest(MarkovProcessEntry state, MarkovProcess proc, int[] fsel) {
		LinkedList<MarkovProcessEntry> entries = proc.getEntries();
		LinkedList<MarkovProcessEntry> closest = new LinkedList<MarkovProcessEntry>();
		int size = entries.size();

		if (difference(state, entries.getFirst(), fsel) <= 0) {
			for (int i=0; i<k; i++) {
				MarkovProcessEntry en = entries.get(i);
				distances[en.getID()] = distance(en, state, fsel);
				closest.add(en);
			}
		} else if (difference(state, entries.getLast(), fsel) >= 0) {
			for (int i=size-1; i>=size-k; i--) {
				MarkovProcessEntry en = entries.get(i);
				distances[en.getID()] = distance(en, state, fsel);
				closest.add(en);
			}
		} else {		
			int left = 0;
			int right = size;		
			while ((right-left) > k) {
				int mid = (left+right)/2;
				MarkovProcessEntry middle = entries.get(mid);
				if (difference(state, middle, fsel) > 0) {
					left = mid;
				} else {
					right = mid;
				}
			}
			for (int i=left; i<right; i++) {
				MarkovProcessEntry en = entries.get(i);
				distances[en.getID()] = distance(en, state, fsel);
				closest.add(en);
			}
		}
		return closest;
	}	
	
	private double difference(MarkovProcessEntry state, MarkovProcessEntry entry, int[] fsel) {
		Double[] st = state.getState();
		Double[] en = entry.getState();
		double diff = 0;
		for (int i=0; i<st.length; i++) {
			if (fsel[i] == 1) {
				diff += (st[i] - en[i]);
			}
		}
		return diff;
	}
	
	private double[] computeWeights() {
		double[] weights = new double[neighbours.size()];
		for (int i=0; i<weights.length; i++) {
			MarkovProcessEntry neighbour = neighbours.get(i);
			double dist = distances[neighbour.getID()];
			weights[i] = dist>0 ? 1/dist : Double.MAX_VALUE;
		}
		normalizeWeights(weights);
		return weights;
	}
	
	private void normalizeWeights(double[] weights) {
		double sum = 0;
		for (int i=0; i<weights.length; i++) {
			sum += weights[i];
		}
		for (int i=0; i<weights.length; i++) {
			weights[i] /= sum;
		}
	}
	
	private void resetDistances() {
		Arrays.fill(distances, Double.MAX_VALUE);
	}
	
	public double predict(MarkovProcessEntry state, int[] fsel) {
		queue.clear();
		neighbours.clear();
		for (MarkovProcess proc : data) {
			LinkedList<MarkovProcessEntry> closest = closest(state, proc, fsel);
			queue.addAll(closest);
		}
		
		double prediction = 0;
		
		for (int i=0; i<k; i++) {
			MarkovProcessEntry neighbour = queue.poll();
			neighbours.add(neighbour);
		}
		
		double[] weights = computeWeights();
		
		for (int i=0; i<k; i++) {
			MarkovProcessEntry neighbour = neighbours.get(i);
			prediction += weights[i]*neighbour.getTime();
		} 
		
		resetDistances();
		
		return prediction/k;
	}	
	
	private double distance(MarkovProcessEntry entry, MarkovProcessEntry state, int[] fsel) {
		double dist = 0;
		Double[] entryS = entry.getState();
		Double[] stateS = state.getState();
		for (int i=0; i<stateS.length; i++) {
			// Euclidian
			if (fsel[i] == 1) {
				dist += (entryS[i] - stateS[i])*(entryS[i] - stateS[i]);
			}
		}
		return Math.sqrt(dist);
	}
	
	public double computeError(int[] fsel) {
		double error = 0;
		int step = 90;
		int stepCount = 0;
		for (int i=9; i<test.size(); i+=step) {
			MarkovProcessEntry entry = test.get(i);
//			long st = System.currentTimeMillis();
			double pr = predict(entry, fsel);
//			long en = System.currentTimeMillis();
//			System.out.println("prediction delay: "+(en-st));
			error += (pr-entry.getTime())*(pr-entry.getTime());
			stepCount++;
		}
		return Math.sqrt(error/stepCount);
	}
	
	//======= Classification =======
	
}
