package uk.ac.manchester.cs.sazonau.model.regressor;

import java.util.Arrays;
import java.util.LinkedList;

import uk.ac.manchester.cs.sazonau.model.knowledge.MarkovProcess;
import uk.ac.manchester.cs.sazonau.model.knowledge.MarkovProcessEntry;

public class RewardCollector {

	private LinkedList<MarkovProcessEntry> data;

	public RewardCollector(LinkedList<MarkovProcessEntry> data) {
		super();
		this.data = data;
	}

	public double collect(int f) {
		double reward = 0;
		for (MarkovProcessEntry entry : data) {
			double fadd = entry.getAction()[f];			
			if (fadd>0) {
				reward += entry.getReward()/fadd;
			}			
		}

		return reward/data.size();
	}

	public double collect(int[] fsel) {
		double reward = 0;
		for (MarkovProcessEntry entry : data) {
			Double[] action = entry.getAction();
			for (int i=0; i<action.length; i++) {
				if (fsel[i]==1 && action[i]>0) {
					reward += entry.getReward()/action[i];
				}
			}
		}

		return reward/data.size();
	}

	public double collect(Integer[] fsel) {
		double reward = 0;
		for (MarkovProcessEntry entry : data) {
			Double[] action = entry.getAction();
			for (int i=0; i<action.length; i++) {
				if (fsel[i]==1 && action[i]>0) {
					reward += entry.getReward()/action[i];
				}
			}
		}

		return reward/data.size();
	}


	public static Double[] additions(MarkovProcess proc) {
		Double[] vec = new Double[proc.getEntries().getLast().getAction().length];
		Arrays.fill(vec, 0);
		for (MarkovProcessEntry en : proc.getEntries()) {
			Double[] ac = en.getAction();
			for (int i=0; i<ac.length; i++) {
				vec[i] += ac[i];
			}
		}		
		return vec;
	}
	
	public static Double[] rewards(MarkovProcess proc) {
		Double[] vec = new Double[proc.getEntries().getLast().getAction().length];
		Arrays.fill(vec, 0.0);
		for (MarkovProcessEntry en : proc.getEntries()) {
			Double[] ac = en.getAction();
			double rew = en.getReward();
			for (int i=0; i<ac.length; i++) {
				vec[i] += (ac[i]>0 && rew>0) ? en.getReward()/ac[i] : 0;
			}
		}		
		return vec;
	}

}
