package uk.ac.manchester.cs.sazonau.ontology.decomp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import decomposition.AxiomWrapper;

import uk.ac.manchester.cs.atomicdecomposition.Atom;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposerOWLAPITOOLS;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

public class OWLAPIAtomicDecomposition {
	
	private LinkedHashMap<Atom, AtomBox> atomMap;
	
	private LinkedList<AtomBox> atoms;
	
	private LinkedList<AtomBox> bottoms;
	
	private LinkedList<AtomBox> tops;
	
	private AtomicDecomposerOWLAPITOOLS dec;
	
	public OWLAPIAtomicDecomposition(OWLOntology ont) {
		super();		
		init(ont);		
	}
	
	public OWLAPIAtomicDecomposition(Set<OWLAxiom> axioms) {
		super();		
		init(axioms);
	}
	
	private void init(Set<OWLAxiom> axioms) {
		List<OWLAxiom> axiomsList = new ArrayList<OWLAxiom>(axioms);
		long start = System.currentTimeMillis();
		dec = new AtomicDecomposerOWLAPITOOLS(axiomsList, ModuleType.BOT);
		long end = System.currentTimeMillis();
		System.out.println("AD finished in "+(end-start)+" msec");
		atomMap = new LinkedHashMap<Atom, AtomBox>();
		atoms = new LinkedList<AtomBox>();
		bottoms = new LinkedList<AtomBox>();
		tops = new LinkedList<AtomBox>();
		initAtoms(dec);
//		start = System.currentTimeMillis();
//		initModules();
//		end = System.currentTimeMillis();
//		System.out.println("modules are extracted in "+(end-start)+" msec");
	}

	private void init(OWLOntology ont) {
		long start = System.currentTimeMillis();
		dec = new AtomicDecomposerOWLAPITOOLS(ont, ModuleType.BOT);
		long end = System.currentTimeMillis();
		System.out.println("AD finished in "+(end-start)+" msec");
		atomMap = new LinkedHashMap<Atom, AtomBox>();
		atoms = new LinkedList<AtomBox>();
		bottoms = new LinkedList<AtomBox>();
		tops = new LinkedList<AtomBox>();
		initAtoms(dec);
//		start = System.currentTimeMillis();
//		initModules();
//		end = System.currentTimeMillis();
//		System.out.println("modules are extracted in "+(end-start)+" msec");
	}
	
	private void initAtoms(AtomicDecomposerOWLAPITOOLS dec) {
		setAxioms();
		setDependents();
		toList();		
	}
	
	private void setAxioms() {
		int count = 0;
		for (Atom at : dec.getAtoms()) {			
			AtomBox atom = new AtomBox(count);			
			atom.setAxioms(new HashSet<OWLAxiom>(at.getAxioms()));			
			atomMap.put(at, atom);
			count++;
		}
	}
	
	private void setDependents() {
		for (Atom at : atomMap.keySet()) {
			AtomBox atom = atomMap.get(at);						
			Set<Atom> deps = dec.getDependents(at);
			if (deps != null && deps.size() > 0) {
				for (Atom dep : deps) {
					if (dep != null) {
						AtomBox depAtom = atomMap.get(dep);
						if (!depAtom.equals(atom)) {
							atom.addDependent(depAtom);
							depAtom.addPredecessor(atom);
						}
					}
				}
			}
		}
	}	
	
	private void toList() {		
		for (Atom at : atomMap.keySet()) {
			AtomBox atom = atomMap.get(at);
			if (atom.getPredecessors().size() <= 0) {
				atom.setTop(true);
				tops.add(atom);
			}
			if (atom.getDependents().size() <= 0) {
				atom.setBottom(true);
				bottoms.add(atom);
			}
			atoms.add(atom);
		}
	}
	
	public void initModules() {
		for (AtomBox atom : atoms) {
			Set<OWLAxiom> module = atom.getModule();
			if (module == null) {
				module = new HashSet<OWLAxiom>();
				collectAxioms(atom, module);				
			}			
		}
//		for (int i=0; i<atoms.size(); i++) {
//			Atom at = dec.getAtomByID(i);
//			AtomBox atom = atomMap.get(at);
//			Set<OWLAxiom> module = new HashSet<OWLAxiom>();
//			Collection<AxiomWrapper> wraps = dec.getAtomModule(i);
//			for (AxiomWrapper wrap : wraps) {
//				module.add(wrap.getAxiom());
//			}
//			atom.setModule(module);
//		}
	}	
	
	private static void collectAxioms(AtomBox atom, Set<OWLAxiom> module) {		
		module.addAll(atom.getAxioms());
		atom.setModule(module);
		if (!atom.isBottom()) {
			for (AtomBox dep : atom.getDependents()) {
				Set<OWLAxiom> depModule = dep.getModule();
				if (depModule == null) {
					depModule = new HashSet<OWLAxiom>();
					collectAxioms(dep, depModule);
					module.addAll(depModule);
				} else {
					module.addAll(depModule);
				}
			}
		}
	}
	
	public Set<OWLAxiom> getModule(Set<OWLEntity> signature) {
		Collection<AxiomWrapper> wrappers = dec.getModule(signature, false, ModuleType.BOT);
		Set<OWLAxiom> module = new HashSet<OWLAxiom>();
		for (AxiomWrapper wrap : wrappers) {
			module.add(wrap.getAxiom());
		}
		return module;
	}
	
	public LinkedList<AtomBox> getAtoms() {
		return atoms;
	}
	
	public AtomBox getAtom(int id) {
		return atoms.get(id);
	}

	public LinkedList<AtomBox> getBottoms() {		
		return bottoms;
	}

	public LinkedList<AtomBox> getTops() {		
		return tops;
	}
	
}
