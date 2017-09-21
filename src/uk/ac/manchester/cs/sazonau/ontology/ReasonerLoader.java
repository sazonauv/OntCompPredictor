package uk.ac.manchester.cs.sazonau.ontology;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import javax.management.RuntimeErrorException;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasonerFactory;
import uk.ac.manchester.cs.jfact.JFactFactory;
import au.csiro.snorocket.owlapi3.SnorocketReasoner;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import de.tudresden.inf.lat.jcel.owlapi.main.JcelReasonerFactory;
import eu.trowl.owlapi3.rel.reasoner.dl.RELReasonerFactory;

public class ReasonerLoader {
	private String reasonerName;
	private OWLOntology ont;
	private double creationTime;
	private ThreadMXBean bean;
	private boolean verbose;
	
	public static final String HERMIT = "hermit";
	public static final String FACT = "fact";
	public static final String PELLET = "pellet";
	public static final String JFACT = "jfact";
	public static final String ELK = "elk";
	public static final String SNOROCKET = "snorocket";
	public static final String JCEL = "jcel";
	public static final String TROWL = "trowl";
	
	
	/**
	 * Constructor
	 * @param Reasoner Name
	 * @param OWL Ontology
	 */
	public ReasonerLoader(String reasonerName, OWLOntology ont, boolean verbose) {
		bean = ManagementFactory.getThreadMXBean();
		this.reasonerName = reasonerName;
		this.verbose = verbose;
		this.ont = ont;
	}
	
	
	/**
	 * Get reasoner creation time (in seconds)
	 * @return Reasoner creation time (in seconds)
	 */
	public double getReasonerCreationTime() {
		return creationTime;
	}

	
	/**
	 * Create an OWL reasoner
	 * @return OWL Reasoner
	 */
	public OWLReasoner getReasoner() throws Exception {
		OWLReasonerConfiguration config = new SimpleConfiguration();
		OWLReasoner reasoner = null;
		ProfileChecker checker = new ProfileChecker(ont);		

		long start = bean.getCurrentThreadCpuTime();

		if(reasonerName.equalsIgnoreCase(HERMIT)) {
			reasoner = new Reasoner.ReasonerFactory().createReasoner(ont, config);
		}
		else if(reasonerName.equalsIgnoreCase(FACT)) {
			reasoner = new FaCTPlusPlusReasonerFactory().createReasoner(ont, config);			
		}
		else if(reasonerName.equalsIgnoreCase(PELLET)) {
			reasoner = new PelletReasonerFactory().createReasoner(ont, config); 
		}
		else if(reasonerName.equalsIgnoreCase(JFACT)) {
			reasoner = new JFactFactory().createReasoner(ont, config);
		}
		else if(reasonerName.equalsIgnoreCase(ELK)) {
			if(checker.isOWL2EL(ont))
				reasoner = new ElkReasonerFactory().createReasoner(ont, config);
			else
				throw new RuntimeException("Cannot initialize reasoner: Input ontology is not in the OWL 2 EL profile." +
						" Please choose an appropriate reasoner.");
		}
		else if(reasonerName.equalsIgnoreCase(SNOROCKET)) {
			if(checker.isOWL2EL(ont))
				reasoner = new SnorocketReasoner(ont, config);
			else
				throw new RuntimeException("Cannot initialize reasoner: Input ontology is not in the OWL 2 EL profile." +
						" Please choose an appropriate reasoner.");
		}
		else if(reasonerName.equalsIgnoreCase(JCEL)) {
			if(checker.isOWL2EL(ont))
				reasoner = new JcelReasonerFactory().createReasoner(ont, config);
			else
				throw new RuntimeException("Cannot initialize reasoner: Input ontology is not in the OWL 2 EL profile." +
						" Please choose an appropriate reasoner.");
		}
		else if(reasonerName.equalsIgnoreCase(TROWL)) {
			reasoner = new RELReasonerFactory().createReasoner(ont);
		}
		else {
			throw new RuntimeErrorException(new Error("Unknown reasoner: " + reasonerName + ". " +
					"Valid reasoners: Hermit | Fact | Pellet | JFact | ELK | jcel | SnoRocket | TrOWL ")); 
		}

		long end = bean.getCurrentThreadCpuTime();
		creationTime = (end-start)/1000000000.0;
		
//		if(verbose && reasoner != null) {
//			if(!reasonerName.equalsIgnoreCase("snorocket") && !reasonerName.equalsIgnoreCase("elk") &&
//					!reasonerName.equalsIgnoreCase("jfact"))
//				System.out.print(" Reasoner: " + reasonerName + " v" + reasoner.getReasonerVersion().getMajor() + "." + 
//					reasoner.getReasonerVersion().getMinor() + "." + reasoner.getReasonerVersion().getPatch());
//			else
//				System.out.println(" Reasoner: " + reasonerName);
//			System.out.println(" (creation time: " + creationTime + " seconds)");
//		}
		
		return reasoner; 
	}
}

