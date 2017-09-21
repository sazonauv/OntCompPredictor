package uk.ac.manchester.cs.sazonau.ontology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import com.clarkparsia.modularity.ModularityUtils;

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.sazonau.ontology.features.Solution;

public class ModuleExtractor {	
	
	private LinkedList<OWLEntity> signature;
	private Set<OWLEntity> subSign;
	private OWLOntology ontology;

	public ModuleExtractor(OWLOntology ontology) {
		super();		
		this.ontology = ontology;
		this.signature = new LinkedList<OWLEntity>(ontology.getSignature());
		this.subSign = new HashSet<OWLEntity>();		
	}
	
	public Set<OWLAxiom> getModule(Set<OWLEntity> sign) {		
		return ModularityUtils.extractModule(ontology, sign, ModuleType.BOT);	
	}
	
	private Set<OWLAxiom> getModule() {
		return ModularityUtils.extractModule(ontology, subSign, ModuleType.BOT);
	}
	
	public Set<OWLAxiom> getModule(double maxAxioms) {
		shuffleSignature();		
		Set<OWLAxiom> module = null;		
		
		int nAxioms = 0;
		double ratio = maxAxioms/ontology.getLogicalAxiomCount();
		int len =  (int)(signature.size()*ratio);		
		int pos = 0;
		while (nAxioms<maxAxioms) {
			pos++;
//			System.out.println("module #"+pos+" is extracted...");			
			module = ModularityUtils.extractModule(ontology, 
					new HashSet<OWLEntity>(signature.subList(0, pos*len)), ModuleType.BOT);
			nAxioms = module.size();			
		}
		
		double stop = 0.1;		
		int left = 0;
		int right = pos*len;
		int middle = 0;
		
		while (right-left>stop*signature.size()) {
//			System.out.println("interval size="+(right-left));
			middle = (right-left)/2;
			module = ModularityUtils.extractModule(ontology, 
					new HashSet<OWLEntity>(signature.subList(0, middle)), ModuleType.BOT);					
			if (module.size()>maxAxioms) {
				right = middle;
			} else {
				left = middle;
			}
		}
		return module;
	}
	
	public Set<OWLAxiom> getAxioms(double maxAxioms) {		
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		Set<OWLLogicalAxiom> logicals = ontology.getLogicalAxioms();
		List<OWLAxiom> list = new ArrayList<OWLAxiom>();		
		for (OWLLogicalAxiom logical : logicals) {
			list.add(logical);
		}
		int maxShift = logicals.size()-(int)maxAxioms;
		int shift = (int)(Math.random()*maxShift);
//		Collections.shuffle(list);
		for (int i=shift; i<shift+maxAxioms; i++) {
			axioms.add(list.get(i));
		}		
		return axioms;
	}

	public void calcSize(Solution s) {
		subSign.clear();
		int binSize = (int)Math.ceil(signature.size()/s.x.length);
		for (int i=0; i<s.x.length; i++) {
			if (s.x[i]==1) {
				for (int j=i*binSize; j<(i+1)*binSize && j<signature.size(); j++) {
					subSign.add(signature.get(j));
				}
			}
		}
		// extract a module
		s.module = getModule();
		s.fitness = (s.module.size() < Solution.maxmodsize) ?
				(double)s.module.size()/Solution.maxmodsize : 1;
	}	
	
	public Set<OWLAxiom> getBigModule() {
		return getModule(new HashSet<OWLEntity>(signature));
	}
	
	public void shuffleSignature() {
		Collections.shuffle(signature);
	}

}
