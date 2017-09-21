package uk.ac.manchester.cs.sazonau.model.knowledge;

import java.util.LinkedList;
import java.util.ListIterator;

import uk.ac.manchester.cs.sazonau.model.regressor.Regression;

public class MarkovProcess implements Iterable<MarkovProcessEntry> {
	
	private Integer ID;
	
	private String reasoner;
	
	private String ontology;
	
	private LinkedList<MarkovProcessEntry> entries;	

	public MarkovProcess() {
		super();
		this.entries = new LinkedList<MarkovProcessEntry>();
	}

	public MarkovProcess(Integer ID, String reasoner, String ontology) {
		super();
		this.ID = ID;
		this.reasoner = reasoner;
		this.ontology = ontology;
		this.entries = new LinkedList<MarkovProcessEntry>();
	}
		
	public void addEntry(MarkovProcessEntry entry) {
		entries.add(entry);
	}
	
	public MarkovProcessEntry getEntry(int index) {
		return entries.get(index);
	}
	
	public MarkovProcessEntry getLast() {
		return entries.getLast();
	}
		
	public LinkedList<MarkovProcessEntry> getEntries() {
		return entries;
	}

	public Integer getID() {
		return ID;
	}
	
	public String getReasoner() {
		return reasoner;
	}

	public String getOntology() {
		return ontology;
	}

	@Override
	public ListIterator<MarkovProcessEntry> iterator() {
		return entries.listIterator();
	}
	
	public void remove(int index) {
		entries.remove(index);
	}
	
	public void remove(MarkovProcessEntry entry) {
		entries.remove(entry);
	}
	
	public void addAll(MarkovProcess proc) {
		entries.addAll(proc.getEntries());
	}
	
	public double getMaxEntrySize() {
		double max = -1;
		for (MarkovProcessEntry en: entries) {
			double size = Regression.countAxioms(en.getState());
			if (size>max) {
				max = size;
			}
		}
		return max;
	}
}
