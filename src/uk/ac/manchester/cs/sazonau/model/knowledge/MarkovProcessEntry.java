package uk.ac.manchester.cs.sazonau.model.knowledge;

import uk.ac.manchester.cs.sazonau.ontology.decomp.AtomBox;
import uk.ac.manchester.cs.sazonau.ontology.features.OntFeatureVector;

public class MarkovProcessEntry {
	
	private Integer ID;
	
	private Double[] state;
	
	private Double[] action;
	
	private Double reward;
	
	private Double time;
	
	private AtomBox atom;
	
	public MarkovProcessEntry(Integer ID) {
		this.ID = ID;
	}

	public MarkovProcessEntry(Integer ID, OntFeatureVector state, OntFeatureVector action,
			Double reward, Double time, AtomBox atom) {
		super();
		this.ID = ID;
		this.state = state.toArray();
		this.action = action.toArray();
		this.reward = reward;
		this.time = time;
		this.atom = atom;
	}

	public Double[] getState() {
		return state;
	}

	public Double[] getAction() {
		return action;
	}

	public Double getReward() {
		return reward;
	}	

	public Double getTime() {
		return time;
	}	

	public AtomBox getAtom() {
		return atom;
	}

	public Integer getID() {
		return ID;
	}	

	public void setID(Integer iD) {
		ID = iD;
	}

	public void setState(Double[] state) {
		this.state = state;
	}

	public void setAction(Double[] action) {
		this.action = action;
	}

	public void setReward(Double reward) {
		this.reward = reward;
	}

	public void setTime(Double time) {
		this.time = time;
	}

	public void setAtom(AtomBox atom) {
		this.atom = atom;
	}
		
}
