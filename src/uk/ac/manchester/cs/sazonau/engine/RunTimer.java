package uk.ac.manchester.cs.sazonau.engine;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import org.mindswap.pellet.exceptions.TimerInterruptedException;
import org.semanticweb.HermiT.datatypes.UnsupportedDatatypeException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerRuntimeException;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;

import uk.ac.manchester.cs.sazonau.ontology.ReasonerLoader;

public class RunTimer {

	// a sub-ontology
	private OWLOntology ontology;

	private OWLOntologyManager manager;	

	private ThreadMXBean bean;

	private String reasonerName;

	public final static double MIN_TIME = 1e-9;

	public RunTimer(OWLOntologyManager manager, OWLOntology ontology, String reasonerName) {
		super();		
		this.manager = manager;
		this.ontology = ontology;
		this.reasonerName = reasonerName;
		init();
	}

	private void init() {
		this.bean = ManagementFactory.getThreadMXBean();			
	}

	public void growOntology(Set<OWLAxiom> axioms) throws ConcurrentModificationException {		
		List<OWLOntologyChange> changes = manager.addAxioms(ontology, axioms);
		manager.applyChanges(changes);		
	}



	public double measureTime(OWLReasoner reasoner) {				
		long start = bean.getCurrentThreadCpuTime();		
		try {
			reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
			//			reasoner.isConsistent();		
		} catch (ReasonerInterruptedException e) {				
			return finishWork(reasoner, start);
		} catch (TimerInterruptedException e) {
			return finishWork(reasoner, start);
		} catch (ReasonerInternalException e) {
			return finishWork(reasoner, start);
		} catch (OWLReasonerRuntimeException e) {
			return finishWork(reasoner, start);
		} catch (IllegalArgumentException e) {
			return finishWork(reasoner, start);		
		} catch (UnsupportedDatatypeException e) {
			return finishWork(reasoner, start);
		} catch (OutOfMemoryError e) {
			return finishWork(reasoner, start);
		} catch (StackOverflowError e) {
			return finishWork(reasoner, start);			
		} catch (Exception e) {
			return finishWork(reasoner, start);
		}
		return finishWork(reasoner, start);
	}

	private double finishWork(OWLReasoner reasoner, long start) {
		long end = bean.getCurrentThreadCpuTime();
		double time = (end-start)*MIN_TIME;
		if (reasoner != null) {
			try {
				reasoner.interrupt();
				reasoner.dispose();
			} catch (Exception e) {
//				System.out.println("Exception while reasoner interruption");
				// do nothing
				return time;
			}
		}
		return time;		
	}

	public double measureTime(long timeout) throws ConcurrentModificationException {
		ReasonerLoader rloader = new ReasonerLoader(reasonerName, ontology, true);
		OWLReasoner reasoner =  null;
		try {
			reasoner = rloader.getReasoner();
		} catch(Exception e) {
			System.out.println("Exception while loading the reasoner");
//			e.printStackTrace();
			return 0;
		}

		Timer timer = new Timer(true);
		timer.schedule(new InterruptReasonerTask(reasoner), timeout);

		return measureTime(reasoner);
	}

}
