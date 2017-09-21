package uk.ac.manchester.cs.sazonau.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.sazonau.model.knowledge.MarkovProcess;
import uk.ac.manchester.cs.sazonau.model.knowledge.MarkovProcessEntry;
import uk.ac.manchester.cs.sazonau.ontology.ModuleExtractor;
import uk.ac.manchester.cs.sazonau.ontology.ReasonerLoader;
import uk.ac.manchester.cs.sazonau.ontology.decomp.AtomBox;
import uk.ac.manchester.cs.sazonau.ontology.decomp.OWLAPIAtomicDecomposition;
import uk.ac.manchester.cs.sazonau.ontology.features.AIComparator;
import uk.ac.manchester.cs.sazonau.ontology.features.Dataset;
import uk.ac.manchester.cs.sazonau.ontology.features.DatasetEntry;
import uk.ac.manchester.cs.sazonau.ontology.features.NSEvolutionaryAlgorithm;
import uk.ac.manchester.cs.sazonau.ontology.features.OntFeatureExtractor;
import uk.ac.manchester.cs.sazonau.ontology.features.Solution;
import uk.ac.manchester.cs.sazonau.ontology.walker.ADWalker;
import uk.ac.manchester.cs.sazonau.ontology.walker.ClassWalker;
import uk.ac.manchester.cs.sazonau.ontology.walker.Walker;

public class PerformanceTester {	

	public static final double TFRAC = 0.9;
	
	public static final long TIMEOUT = 100;
	
	public static final long TIMEHALT = 1000;
	
	public static int MAXAXIOMS = 10000;

	public static int NEXPS = 3;
	
	public static double MAXSIZE = 0.1;

	public static int CHAINLENGTH = 20;
	
	public static String[] REASONERS = new String[]{
		ReasonerLoader.JFACT, ReasonerLoader.HERMIT, ReasonerLoader.PELLET};

	// in milliseconds
	private long timeout;

	private double[][] compTimes;

	private int[][] axiomAmounts;

	private LinkedList<MarkovProcess> knowledge;
	
	private Dataset dataset;
	
	private Dataset[] datasets;	

	private OWLOntologyManager manager;

	private OWLOntology ontology;
	
	private OWLOntology part;

	private String reasonerName;
	
	private OWLAPIAtomicDecomposition decomposition;	

	public PerformanceTester(OWLOntologyManager manager, OWLOntology ontology, 
			String reasonerName, long timeout) {
		super();		
		this.manager = manager;
		this.ontology = ontology;
		this.reasonerName = reasonerName;
		this.timeout = timeout;		
		init();
	}
	
	private void init() {
		this.compTimes = new double[CHAINLENGTH][NEXPS];		
		this.axiomAmounts = new int[CHAINLENGTH][NEXPS];
		this.knowledge = new LinkedList<MarkovProcess>();
		this.dataset = new Dataset();
		this.datasets = new Dataset[3];
		for (int i=0; i<datasets.length; i++) {
			datasets[i] = new Dataset();
		}
		
		initAD();
//		initCU();
	}
	
	private void initAD() {
		double maxAxioms = MAXAXIOMS;
		int nAxioms = ontology.getLogicalAxiomCount();
		if (nAxioms<maxAxioms) {
			decomposition = new OWLAPIAtomicDecomposition(ontology);			
		} else {			
			if (maxAxioms/nAxioms<MAXSIZE) {
				maxAxioms = (MAXSIZE+0.01)*nAxioms;
			}			
//			System.out.println("MAXSIZE = "+MAXSIZE);
			ModuleExtractor mextr = new ModuleExtractor(ontology);			
			Set<OWLAxiom> module = mextr.getAxioms(maxAxioms);		
			decomposition = new OWLAPIAtomicDecomposition(module);
		}	
	}
	
	private void initCU() {
		int maxAxioms = MAXAXIOMS;
		int nAxioms = ontology.getLogicalAxiomCount();
		if (nAxioms<maxAxioms) {			
			MAXSIZE = 0.2;
			part = ontology;
		} else {
			MAXSIZE = (double)maxAxioms/ontology.getLogicalAxiomCount();
			if (MAXSIZE>0.2) {
				MAXSIZE = 0.2;
			}
			System.out.println("MAXSIZE = "+MAXSIZE);
			ModuleExtractor mextr = new ModuleExtractor(ontology);			
			Set<OWLAxiom> module = mextr.getAxioms(maxAxioms);		
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			try {
				part = manager.createOntology(module);
			} catch (Exception e) {
				System.out.println("Ontology creation failed: check the extracted module");				
			} catch (OutOfMemoryError err) {
				System.out.println("OutOfMemoryError while ontology creation");				
			}
		}	
	}
	
	public int estimateChainLength() {
		
		LinkedList<AtomBox> atoms = decomposition.getAtoms();
		Random random = new Random();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ont = null;

		final int rmax = 10;
		int[] lens = new int[rmax];
		for (int r=0; r<rmax; r++) {
			double time = 0;
			int i = 0;

			AtomBox atom = atoms.get(random.nextInt(atoms.size()));
			ADWalker walker = new ADWalker(atom, Walker.SHORT_MEM);

			try {
				ont = manager.createOntology();
			} catch (OWLOntologyCreationException e) {
				e.printStackTrace();
			}

			RunTimer timer = new RunTimer(manager, ont, reasonerName);			
			AtomBox node = atom;

			while (time < TFRAC*timeout/1000) {
				i++;				
				// grow the ontology				
				try {
					timer.growOntology(node.getAxioms());
				} catch (ConcurrentModificationException e1) {
					// do nothing
				}				
				// get the next atom
				node = walker.next();
				if (node == null) {
					node = atoms.get(random.nextInt(atoms.size()));
				}
				// measure run time				
				try {
					time = timer.measureTime(timeout);
				} catch (ConcurrentModificationException e) {					
					// do nothing
				}
				System.out.println("time: "+time);
			}

			lens[r] = i;
			System.out.println("===Chain length: "+i);
		}

		double mean = 0;
		for (int len : lens) {
			mean += len;
		}
		mean /= rmax;
		CHAINLENGTH = (int)mean;
		return CHAINLENGTH;
	}

	public void execute() {
		knowledge.clear();
		LinkedList<AtomBox> atoms = decomposition.getAtoms();
		Random random = new Random();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ont = null;
		OntFeatureExtractor extractor = new OntFeatureExtractor();


//		nexps = -1;
//		int totalAtoms = decomposition.getAtoms().size();
//		Set<AtomBox> visitedAtoms = new HashSet<AtomBox>();
//		while (visitedAtoms.size() < COVER*totalAtoms) {
		for (int k=0; k<NEXPS; k++) {
//			nexps++;
			double time = 0;
			double prevtime = 0;
//			int i = 0;

//			System.out.println("=======EXP #"+nexps+"=======");
//			System.out.println("Coverage: "+ (double)visitedAtoms.size()/totalAtoms);
			AtomBox atom = atoms.get(random.nextInt(atoms.size()));
			ADWalker walker = new ADWalker(atom, Walker.SHORT_MEM);
			MarkovProcess process = new MarkovProcess(k, reasonerName, ontology.getOntologyID().getOntologyIRI().toString());			
			knowledge.add(process);

			try {
				ont = manager.createOntology();
			} catch (OWLOntologyCreationException e) {
				e.printStackTrace();
			}		

			RunTimer timer = new RunTimer(manager, ont, reasonerName);
			extractor.clear();

			AtomBox node = atom;
			int totalAxioms = 0;

//			while (time < TFRAC*timeout/1000) {
//				i++;				
			for (int i=0; i<CHAINLENGTH; i++) {
	//			visitedAtoms.add(node);
				// grow the ontology				
				try {
					timer.growOntology(node.getAxioms());
				} catch (ConcurrentModificationException e1) {
					// do nothing
				}

				//				axiomAmounts[i][nexps] = 
				totalAxioms = ont.getAxioms().size();

				// extract features from the current atom
				extractor.extractFeatures(node);
				extractor.processOntologySignature(ont);

				// get the next atom
				node = walker.next();
				if (node == null) {
					node = atoms.get(random.nextInt(atoms.size()));
				}
				
				if (time < TFRAC*timeout/1000) {
					// measure run time				
					try {
						time = timer.measureTime(timeout);
					} catch (ConcurrentModificationException e) {					
						// do nothing
					}				
					time = (time < RunTimer.MIN_TIME ? RunTimer.MIN_TIME : time);
				}
				compTimes[i][k] = time; //Math.log10(time/totalAxioms)+9;

				// capture the state, action and reward
				MarkovProcessEntry entry = new MarkovProcessEntry(i, extractor.getOntVector(), 
						extractor.getAtomVector(), 
						time - prevtime, time, node);
				//						i>0 ? (compTimes[i][nexps] - compTimes[i-1][nexps]) : 0, compTimes[i][nexps], node);
				process.addEntry(entry);

				// update the ontology feature vector
				extractor.updateOntVector();
				prevtime = time;

				// print to console
				System.out.print("atom: " + i + " time: " + time + " next: " + node.getId() + " #axioms: " + totalAxioms);
				//				walker.printChoices();
				String pr = "";
				if (node.isBottom()) {
					pr += " bottom,";
				}
				if (node.isTop()) {
					pr += " top,";
				}
				pr += (" deps: " + node.getDependents().size() + ",");
				pr += (" preds: " + node.getPredecessors().size() + ",");
				System.out.println(pr);
			}

		}
	}

	public double testOntology() {
		double mean = 0;
		int count = 0;
		for (int k=0; k<NEXPS; k++) {			
			RunTimer timer = new RunTimer(manager, ontology, reasonerName);
			// measure run time
			double time = 0;
			try {
				time = timer.measureTime(timeout);
				count++;
			} catch (Exception e) {
				System.out.println("Exception...");
				continue;
			} catch (Error e) {
				System.out.println("Error...");
				continue;
			}
			if (time > 0.95*timeout/1000) {
				System.out.println("timeout exceeded.");
				return timeout/1000;
			}
			mean += time;
			System.out.println("exp #"+k+" time: "+time);
		}
		if (count>0) {
			mean /= count;
		}
		System.out.println("Mean reasoning time: "+mean);
		return mean;
	}

	public void CUSamples() {	
		Set<OWLClass> clasSet = part.getClassesInSignature();
		if (clasSet.size()<1) {
			for (int r=0; r<REASONERS.length; r++) {
				datasets[r].addTrivialEntry();				
			}
			System.out.println("No classes...");
			return;
		}
		LinkedList<OWLClass> classes = new LinkedList<OWLClass>(clasSet);
		Random random = new Random();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();		
		
		double step = MAXSIZE/(CHAINLENGTH+1);
		double ontSize = ontology.getLogicalAxiomCount();
		
		for (int k=0; k<NEXPS; k++) {						

			OWLClass node = classes.get(random.nextInt(classes.size()));
			while (node == null) {
				node = classes.get(random.nextInt(classes.size()));
			}
			ClassWalker walker = new ClassWalker(node, part, Walker.SHORT_MEM);			
			
			Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
			axioms.addAll(node.getReferencingAxioms(part));
			
			// settings timeout flags
			boolean[] timeoutFlags = new boolean[]{
					false, false, false};
			
			int count = 0;
			int maxiter = 100;
			for (int i=0; i<CHAINLENGTH; i++) {
				// build a next module
				count = 0;
				while ((double)axioms.size()/ontSize <= (i+1)*step) {
					// get the next atom					
					node = walker.next();
					while (node == null) {
						node = classes.get(random.nextInt(classes.size()));
					}
					int len1 = axioms.size();
					axioms.addAll(node.getReferencingAxioms(part));
					int len2 = axioms.size();
					if (len2==len1) {
						count++;
					} else {
						count = 0;
					}
					if (count>=maxiter) {
						break;
					}
				}
				if (count>=maxiter) {
					break;
				}				
				// create an ontology				
				OWLOntology ont = null;
				try {
					ont = manager.createOntology(axioms);
				} catch (Exception e) {
					System.out.println("Ontology creation failed: check the extracted module");
					continue;
				} catch (OutOfMemoryError err) {
					System.out.println("OutOfMemoryError while ontology creation");
					break;
				}
				
				Double[] features = new OntFeatureExtractor().extract(axioms).toArray();
				
				// print to console
				System.out.println("repeat="+k+" module #" + i 
						+ " of size = " + (double)axioms.size()/ontSize);
				
				for (int r=0; r<REASONERS.length; r++) {
					if (timeoutFlags[r]) {
						continue;
					}
					// measure time
					double time = 0;
					RunTimer timer = new RunTimer(manager, ont, REASONERS[r]);
					// measure run time				
					try {
						time = timer.measureTime(timeout);
					} catch (Exception e) {					
						// do nothing
					} catch (OutOfMemoryError err) {
						System.out.println("OutOfMemoryError while time measuring");
						break;
					}
					// measure twice for the first time
					if (i==0) {
						try {
							time = timer.measureTime(timeout);
						} catch (Exception e) {					
							// do nothing
						} catch (OutOfMemoryError err) {
							System.out.println("OutOfMemoryError while time measuring");
							break;
						}
					}
					time = (time < RunTimer.MIN_TIME ? RunTimer.MIN_TIME : time);				

					DatasetEntry en = new DatasetEntry();
					en.time = time;
					en.vector = features;					
					datasets[r].add(en);

					// print to console
					System.out.println("\t"+REASONERS[r]+" time = " + time);

					if (time > TFRAC*timeout/1e3) {
						for (int j=i+1; j<CHAINLENGTH; j++) {
							datasets[r].add(en);
						}
						timeoutFlags[r] = true;
					}
				}
			}
		}
	}
	
	public void executeModules() {
		int sigLen = 100;
//		double frac = 0.7;
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ont = null;
		OntFeatureExtractor extractor = new OntFeatureExtractor();
		RunTimer timer = null;
		extractor.clear();
		Set<OWLEntity> sign = ontology.getSignature();
		ArrayList<OWLEntity> signList = new ArrayList<OWLEntity>(sign);
		ModuleExtractor modExtr = new ModuleExtractor(ontology);
				
		for (int i=0; i<CHAINLENGTH; i++) {
			sigLen = (int)(Math.random()*100);
			Collections.shuffle(signList);
			try {
				Set<OWLAxiom> mod = modExtr.getModule(new HashSet<OWLEntity>(signList.subList(0, sigLen)));
				ont = manager.createOntology(mod);
			} catch (Exception e) {
				System.out.println("Ontology creation failed: check the extracted module");
				e.printStackTrace();
			}
			
			// extract features from the current atom
			extractor.clear();
			extractor.extractFeatures(ont);
			extractor.processOntologySignature(ont);
			extractor.updateOntVector();
			
			// measure run time
			double time = 0;
			timer = new RunTimer(manager, ont, reasonerName);
			try {
				time = timer.measureTime(timeout);
			} catch (ConcurrentModificationException e) {					
				// do nothing
			}				
			time = (time < RunTimer.MIN_TIME ? RunTimer.MIN_TIME : time);
			System.out.println("Module #"+i+" of size "+
					(double)ont.getAxiomCount()/ontology.getAxiomCount()+": time="+time);
			
			DatasetEntry en = new DatasetEntry();
			en.time = time;
			en.vector = extractor.getOntVector().toArray();
			dataset.add(en);
		}

	}
	
	public void EAModules() throws Exception {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ont = null;		
		RunTimer timer = null;		
		ModuleExtractor modExtr = new ModuleExtractor(ontology);
				
		Solution.maxerr = 1;
		int signSize = ontology.getSignature().size();
		Solution.maxvars = (signSize<500 ? signSize : 500);
		Solution.maxmodsize = ontology.getLogicalAxiomCount();		
		
		int eatime = 100; // in seconds
		int maxIters = (int)(eatime*5e5/(NSEvolutionaryAlgorithm.popsize*Solution.maxmodsize));
		maxIters = (maxIters<100 ? maxIters : 100);
		maxIters = (maxIters<5 ? 5 : maxIters);
		System.out.println("#Axioms = "+Solution.maxmodsize+" / max iterations = "+maxIters);
		
		ArrayList<Solution> front = new ArrayList<Solution>();
		for (int r=0; r<1; r++) {
			System.out.println("======repeat #"+r);
			modExtr.shuffleSignature();
			NSEvolutionaryAlgorithm ea = new NSEvolutionaryAlgorithm(modExtr, maxIters, Solution.maxvars/100, 10, 10);	
			ea.elitism(Solution.maxerr/500, 1, 0.999999, Solution.maxerr*Solution.maxvars);			
			front.addAll(ea.getParetoFront());
		}
		// remove duplicates
		HashSet<Solution> set = new HashSet<Solution>(front);			
		front = new ArrayList<Solution>(set);
		// get smallest modules
		double[] sizes = new double[front.size()];
		for (int i=0; i<front.size(); i++) {
			sizes[i] = front.get(i).fitness;
		}
		AIComparator comp = new AIComparator(sizes, AIComparator.asc);
		Integer[] inds = comp.createIndexArray();
		Arrays.sort(inds, comp);
		int count = 100;
		ArrayList<Solution> smallest = new ArrayList<Solution>();
		for (int i=0; i<count && i<inds.length; i++) {
			smallest.add(front.get(inds[i]));
		}
		front = smallest;
		// measure times for modules
		double[] times = new double[front.size()];
		Double[][] vectors = new Double[front.size()][];
		for (int i=0; i<front.size(); i++) {
			try {				
				ont = manager.createOntology(front.get(i).module);					
			} catch (Exception e) {
				System.out.println("Ontology creation failed: check the extracted module");
				continue;
			} catch (OutOfMemoryError err) {
				System.out.println("OutOfMemoryError while ontology creation");
				break;
			}
			
			// extract features from the current atom
			OntFeatureExtractor extractor = new OntFeatureExtractor();
			extractor.clear();
			try {
				extractor.extractFeatures(ont);
				extractor.processOntologySignature(ont);
			} catch (OutOfMemoryError err) {
				System.out.println("OutOfMemoryError while feature extraction");
				continue;
			}
			extractor.updateOntVector();
			vectors[i] = extractor.getOntVector().toArray();

			// measure run time
			double time = 0;
			timer = new RunTimer(manager, ont, reasonerName);
			try {
				time = timer.measureTime(timeout);
			} catch (Exception e) {					
				// do nothing
			}		
			time = (time < RunTimer.MIN_TIME ? RunTimer.MIN_TIME : time);
			// measure twice for the first time
			if (i==0) {
				timer = new RunTimer(manager, ont, reasonerName);
				try {
					time = timer.measureTime(timeout);
				} catch (Exception e) {					
					// do nothing
				}
				time = (time < RunTimer.MIN_TIME ? RunTimer.MIN_TIME : time);
			}			
			times[i] = time;
			System.out.println("Module #"+i+" of size "+front.get(i).fitness+" time="+time);
			
			DatasetEntry en = new DatasetEntry();
			en.time = times[i];
			en.vector = vectors[i];
			dataset.add(en);
			
			if (time > TFRAC*timeout/1e3) {
				for (int j=i+1; j<front.size(); j++) {
					dataset.add(en);
				}
				break;
			}
		}		
	}
	
	public void ADSamples() throws Exception {		
		LinkedList<AtomBox> atoms = decomposition.getAtoms();
		Random random = new Random();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();		
		
		double step = MAXSIZE/(CHAINLENGTH+1);
		double ontSize = ontology.getLogicalAxiomCount();
		
		for (int k=0; k<NEXPS; k++) {						

			AtomBox node = atoms.get(random.nextInt(atoms.size()));
			while (node == null) {
				node = atoms.get(random.nextInt(atoms.size()));
			}
			ADWalker walker = new ADWalker(node, Walker.FULL_MEM);
			
			Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
			axioms.addAll(node.getAxioms());
			
			// settings timeout flags
			boolean[] timeoutFlags = new boolean[]{
					false, false, false};
			
			for (int i=0; i<CHAINLENGTH; i++) {
				// build a next module
				while ((double)axioms.size()/ontSize <= (i+1)*step) {
					// get the next atom
					node = null;//walker.next();
					while (node == null) {
						node = atoms.get(random.nextInt(atoms.size()));
					}
					axioms.addAll(node.getAxioms());
				}
				// create an ontology				
				OWLOntology ont = null;
				try {
					ont = manager.createOntology(axioms);
				} catch (Exception e) {
					System.out.println("Ontology creation failed: check the extracted module");
					continue;
				} catch (OutOfMemoryError err) {
					System.out.println("OutOfMemoryError while ontology creation");
					break;
				}
				
				Double[] features = new OntFeatureExtractor().extract(axioms).toArray();
				
				// print to console
				System.out.println("repeat="+k+" module #" + i 
						+ " of size = " + (double)axioms.size()/ontSize);
				
				for (int r=0; r<REASONERS.length; r++) {
					if (timeoutFlags[r]) {
						continue;
					}
					// measure time
					double time = 0;
					RunTimer timer = new RunTimer(manager, ont, REASONERS[r]);
					// measure run time				
					try {
						time = timer.measureTime(timeout);
					} catch (Exception e) {					
						// do nothing
					} catch (OutOfMemoryError err) {
						System.out.println("OutOfMemoryError while time measuring");
						break;
					}
					// measure twice for the first time
					if (i==0) {
						try {
							time = timer.measureTime(timeout);
						} catch (Exception e) {					
							// do nothing
						} catch (OutOfMemoryError err) {
							System.out.println("OutOfMemoryError while time measuring");
							break;
						}
					}
					time = (time < RunTimer.MIN_TIME ? RunTimer.MIN_TIME : time);				

					DatasetEntry en = new DatasetEntry();
					en.time = time;
					en.vector = features;					
					datasets[r].add(en);

					// print to console
					System.out.println("\t"+REASONERS[r]+" time = " + time);

					if (time > TFRAC*timeout/1e3) {
						for (int j=i+1; j<CHAINLENGTH; j++) {
							datasets[r].add(en);
						}
						timeoutFlags[r] = true;
					}
				}
				// if a single atom is the AD
				if (decomposition.getAtoms().size()<10) {
					break;
				}
			}			
		}
		
	}
	
	public void ADGenuineModules() throws Exception {		
		LinkedList<AtomBox> atoms = decomposition.getAtoms();		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();		
		
		double step = MAXSIZE/CHAINLENGTH;
		double ontSize = ontology.getLogicalAxiomCount();
		
		// sorting modules by sizes
		double[] modSizes = new double[atoms.size()];
		for (int i=0; i<modSizes.length; i++) {
			modSizes[i] = atoms.get(i).getModule().size()/ontSize;
		}		
		AIComparator comp = new AIComparator(modSizes, AIComparator.asc);
		Integer[] inds = comp.createIndexArray();
		Arrays.sort(inds, comp);
		
		// filling size bins
		AtomBox[][] moduleBins = new AtomBox[CHAINLENGTH][];
		int j=0;
		for (int i=0; i<moduleBins.length; i++) {
			int count=0;
			while (j+count<inds.length && modSizes[inds[j+count]]<(i+1)*step) {				
				count++;				
			}
			AtomBox[] bin = null;
			if (count>0) {
				bin = new AtomBox[count];
				for (int k=0; k<count; k++) {
					bin[k] = atoms.get(inds[j+k]);
				}
			}
			moduleBins[i] = bin;
			j += count;
		}
		
		// measuring times
		for (int i=0; i<moduleBins.length; i++) {
			if (moduleBins[i] != null) {
				List<AtomBox> arr = Arrays.asList(moduleBins[i]);
				Collections.shuffle(arr);
				for (int k=0; k<NEXPS && k<arr.size(); k++) {
					Set<OWLAxiom> axioms = arr.get(k).getModule();
					
					// create an ontology				
					OWLOntology ont = null;
					try {
						ont = manager.createOntology(axioms);
					} catch (Exception e) {
						System.out.println("Ontology creation failed: check the extracted module");
						continue;
					} catch (OutOfMemoryError err) {
						System.out.println("OutOfMemoryError while ontology creation");
						break;
					}
					// measure time
					double time = 0;
					RunTimer timer = new RunTimer(manager, ont, reasonerName);
					// measure run time				
					try {
						time = timer.measureTime(timeout);
					} catch (Exception e) {					
						// do nothing
					}				
					// measure twice for the first time
					if (i==0 && k==0) {
						try {
							time = timer.measureTime(timeout);
						} catch (ConcurrentModificationException e) {					
							// do nothing
						}
					}
					time = (time < RunTimer.MIN_TIME ? RunTimer.MIN_TIME : time);				
									
					DatasetEntry en = new DatasetEntry();
					en.time = time;
					en.vector = new OntFeatureExtractor().extract(axioms).toArray();
					dataset.add(en);

					// print to console
					System.out.println("repeat="+i+" module #" + k 
							+ " of size=" + (double)axioms.size()/ontSize + " time=" + time);
					
				}
			}
		}
	}
	
	public void greedyModules() throws Exception {		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ont = null;
		OWLOntology bestModule = null;
		OntFeatureExtractor extractor = new OntFeatureExtractor();
		RunTimer timer = null;		
		Set<OWLEntity> sign = ontology.getSignature();
		LinkedList<OWLEntity> signList = new LinkedList<OWLEntity>(sign);
		LinkedList<OWLEntity> subSign = new LinkedList<OWLEntity>();
		LinkedList<OWLEntity> inflSignList = getInfluencialSignature(signList);
		ModuleExtractor modExtr = new ModuleExtractor(ontology);
		
		// an upper bound
		for (int i=0; i<inflSignList.size()/2; i++) {
			OWLEntity bestEnt = null;
			Double[] bestVector = null;	
			double bestTime = -1;
			
			for (int j=0; j<inflSignList.size(); j++) {
				OWLEntity ent = inflSignList.get(j);
				if (!subSign.contains(ent)) {
					subSign.add(ent);
				} else {
					continue;
				}
				
				try {
					Set<OWLAxiom> mod = modExtr.getModule(new HashSet<OWLEntity>(subSign));
					ont = manager.createOntology(mod);					
				} catch (Exception e) {
					System.out.println("Ontology creation failed: check the extracted module");
//					e.printStackTrace();
				}

				// extract features from the current atom
				extractor.clear();
				try {
					extractor.extractFeatures(ont);
					extractor.processOntologySignature(ont);
				} catch (OutOfMemoryError err) {
					System.out.println("OutOfMemoryError while feature extraction");
					continue;
				}
				extractor.updateOntVector();

				// measure run time
				double time = 0;
				timer = new RunTimer(manager, ont, reasonerName);
				try {
					time = timer.measureTime(timeout);
				} catch (ConcurrentModificationException e) {					
					// do nothing
				}			
				time = (time < RunTimer.MIN_TIME ? RunTimer.MIN_TIME : time);
				if (i==0 && j==0) {
					time = RunTimer.MIN_TIME;
				}
				System.out.println("U Sig size="+subSign.size()+" time="+time);
				
				if (time > bestTime) {
					bestTime = time;
					bestVector = extractor.getOntVector().toArray();					
					bestEnt = ent;
					bestModule = ont;
				}				
				subSign.removeLast();
			}
			
			subSign.add(bestEnt);

			System.out.println("Module #"+i+" of size "+
					(double)bestModule.getAxiomCount()/ontology.getAxiomCount()+": time="+bestTime);

			DatasetEntry en = new DatasetEntry();
			en.time = bestTime;
			en.vector = bestVector;
			dataset.add(en);
		}
		// a lower bound
		subSign.clear();
		for (int i=0; i<inflSignList.size()/2; i++) {
			OWLEntity bestEnt = null;
			Double[] bestVector = null;	
			double bestTime = Integer.MAX_VALUE;
			
			for (int j=0; j<inflSignList.size(); j++) {
				OWLEntity ent = inflSignList.get(j);
				if (!subSign.contains(ent)) {
					subSign.add(ent);
				} else {
					continue;
				}
				
				try {
					Set<OWLAxiom> mod = modExtr.getModule(new HashSet<OWLEntity>(subSign));
					ont = manager.createOntology(mod);
				} catch (Exception e) {
					System.out.println("Ontology creation failed: check the extracted module");
//					e.printStackTrace();
				}

				// extract features from the current atom
				extractor.clear();
				try {
					extractor.extractFeatures(ont);
					extractor.processOntologySignature(ont);
				} catch (OutOfMemoryError err) {
					System.out.println("OutOfMemoryError while feature extraction");
					continue;
				}
				extractor.updateOntVector();

				// measure run time
				double time = 0;
				timer = new RunTimer(manager, ont, reasonerName);
				try {
					time = timer.measureTime(timeout);
				} catch (ConcurrentModificationException e) {					
					// do nothing
				}			
				time = (time < RunTimer.MIN_TIME ? RunTimer.MIN_TIME : time);
				System.out.println("L Sig size="+subSign.size()+" time="+time);
				
				if (time < bestTime && time > RunTimer.MIN_TIME) {
					bestTime = time;
					bestVector = extractor.getOntVector().toArray();					
					bestEnt = ent;
					bestModule = ont;
				}				
				subSign.removeLast();
			}
			
			subSign.add(bestEnt);

			System.out.println("Module #"+i+" of size "+
					(double)bestModule.getAxiomCount()/ontology.getAxiomCount()+": time="+bestTime);

			DatasetEntry en = new DatasetEntry();
			en.time = bestTime;
			en.vector = bestVector;
			dataset.add(en);
		}

	}

	private LinkedList<OWLEntity> getInfluencialSignature(LinkedList<OWLEntity> signList) {		
		LinkedList<OWLEntity> subSign = new LinkedList<OWLEntity>();
		ModuleExtractor modExtr = new ModuleExtractor(ontology);
		
//		for (int r=0; r<chainLength; r++) {
			int largest = -1;
			int[] sizes = new int[signList.size()];
			OWLEntity bestEnt = null;
			for (int i=0; i<signList.size(); i++) {
				OWLEntity ent = signList.get(i);
				if (!subSign.contains(ent)) {
					subSign.add(ent);
				} else {
					continue;
				}
				// get module size
				int size = -1;
				try {
					Set<OWLAxiom> mod = modExtr.getModule(new HashSet<OWLEntity>(subSign));					
					sizes[i] = size = mod.size();
				} catch (Exception e) {
					System.out.println("Ontology creation failed: check the extracted module");
//					e.printStackTrace();
				}
				
				if (size > largest) {
					largest = size;							
					bestEnt = ent;				
				}				
				subSign.removeLast();
			}
//			System.out.println("r="+r+" biggest size="+largest);
//			subSign.add(bestEnt);
//		}
		
			AIComparator comp = new AIComparator(sizes, AIComparator.desc);
			Integer[] inds = comp.createIndexArray();
			Arrays.sort(inds, comp);	

//			for (int i=0; i<chainLength; i++) {
//				subSign.add(signList.get(inds[i]));
//			}	
		
			double window = 0.1;
			if (signList.size()*window < CHAINLENGTH) {
				window = 1;
			}
			for (int r=0; r<CHAINLENGTH; r++) {
				largest = -1;				
				bestEnt = null;
				for (int i=0; i<inds.length*window; i++) {
					OWLEntity ent = signList.get(inds[i]);
					if (!subSign.contains(ent)) {
						subSign.add(ent);
					} else {
						continue;
					}
					// get module size
					int size = -1;
					try {					
						Set<OWLAxiom> mod = modExtr.getModule(new HashSet<OWLEntity>(subSign));					
						size = mod.size();
					} catch (Exception e) {
						System.out.println("Ontology creation failed: check the extracted module");
//						e.printStackTrace();
					}
					
					if (size > largest) {
						largest = size;							
						bestEnt = ent;				
					}				
					subSign.removeLast();
				}
				System.out.println("r="+r+" biggest size="+largest);
				subSign.add(bestEnt);
			}	
		
		
//		double[] sigArr = new double[signList.size()];
//		Arrays.fill(sigArr, 0.0);
//		for (OWLAxiom ax : ontology.getAxioms()) {
//			Set<OWLEntity> axSig = ax.getSignature();
//			for (OWLEntity ent : axSig) {
//				int index = signList.indexOf(ent);
//				if (index >= 0) {
//					sigArr[index]++;
//				}
//			}
//		}
//		
//		AIComparator comp = new AIComparator(sigArr, AIComparator.desc);
//		Integer[] inds = comp.createIndexArray();
//		Arrays.sort(inds, comp);	
//		
//		for (int i=0; i<chainLength; i++) {
//			subSign.add(signList.get(inds[i]));
//		}
		
		return subSign;
	}

	public double[][] getCompTimes() {
		return compTimes;
	}

	public int[][] getAxiomAmounts() {
		return axiomAmounts;
	}

	public LinkedList<MarkovProcess> getKnowledge() {
		return knowledge;
	}

	public String getReasonerName() {
		return reasonerName;
	}

	public void setReasonerName(String reasonerName) {
		this.reasonerName = reasonerName;		
	}

	public Dataset getDataset() {
		return dataset;
	}

	public Dataset[] getDatasets() {
		return datasets;
	}
	
	

}
