package uk.ac.manchester.cs.sazonau.engine;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.metrics.DLExpressivity;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWL2RLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.util.DLExpressivityChecker;

import uk.ac.manchester.cs.sazonau.model.knowledge.MarkovProcess;
import uk.ac.manchester.cs.sazonau.model.knowledge.MarkovProcessEntry;
import uk.ac.manchester.cs.sazonau.model.knowledge.XMLDatasetReader;
import uk.ac.manchester.cs.sazonau.model.knowledge.XMLDatasetWriter;
import uk.ac.manchester.cs.sazonau.model.knowledge.XMLKnowledgeReader;
import uk.ac.manchester.cs.sazonau.model.knowledge.XMLKnowledgeWriter;
import uk.ac.manchester.cs.sazonau.model.regressor.KNNClassifier;
import uk.ac.manchester.cs.sazonau.model.regressor.Lasso;
import uk.ac.manchester.cs.sazonau.model.regressor.LinearRegression;
import uk.ac.manchester.cs.sazonau.model.regressor.PolynomRegression;
import uk.ac.manchester.cs.sazonau.model.regressor.Regression;
import uk.ac.manchester.cs.sazonau.model.regressor.RewardCollector;
import uk.ac.manchester.cs.sazonau.model.regressor.SampleBinner;
import uk.ac.manchester.cs.sazonau.ontology.OntologyLoader;
import uk.ac.manchester.cs.sazonau.ontology.ReasonerLoader;
import uk.ac.manchester.cs.sazonau.ontology.decomp.AtomBox;
import uk.ac.manchester.cs.sazonau.ontology.decomp.OWLAPIAtomicDecomposition;
import uk.ac.manchester.cs.sazonau.ontology.features.AIComparator;
import uk.ac.manchester.cs.sazonau.ontology.features.Dataset;
import uk.ac.manchester.cs.sazonau.ontology.features.DatasetEntry;
import uk.ac.manchester.cs.sazonau.ontology.features.FeatureRanker;
import uk.ac.manchester.cs.sazonau.ontology.features.NSEvolutionaryAlgorithm;
import uk.ac.manchester.cs.sazonau.ontology.features.OntFeatureExtractor;
import uk.ac.manchester.cs.sazonau.ontology.features.OntFeatureVector;
import uk.ac.manchester.cs.sazonau.ontology.features.PCA;
import uk.ac.manchester.cs.sazonau.ontology.features.Solution;

public class Test {

	public static void main(String[] args){
		
//		extractFeatures(args[0]);
		
//		testAllOntologies();

//		featureStats(ReasonerLoader.JFACT);

//		testPrediction();
		
		testOntSize();
		
//		testLinearRegression();
		
//		writeDataset();
		
//		writeModules();
		
//		testModules();		
		
//		testAD();
		
//		testPCA();
		
//		rankFeatures();
		
//		printMaxModules();
		
//		compareAlgorithms();
		
//		analyseDataVolume();
		
//		compareADandCU();
		
//		timeAgainstSize();
		
		System.exit(0);
	}

	public static void comparisonTest(OWLOntologyManager manager, OWLOntology ontology) {
		long timeout = 10000;
		PerformanceTester ptester = new PerformanceTester(manager, ontology, ReasonerLoader.HERMIT, timeout);
		ptester.execute();
		double[][] ctimes1 = ptester.getCompTimes();
		//		int[][] naxioms1 = ptester.getAxiomAmounts();

		ptester = new PerformanceTester(manager, ontology, ReasonerLoader.HERMIT, timeout);
		ptester.CUSamples();
		double[][] ctimes2 = ptester.getCompTimes();
		//		int[][] naxioms2 = ptester.getAxiomAmounts();
		
		ExcelWriter.writeComparison(ctimes1, ctimes2);
	}	
			
	public static void writeModules(OWLOntologyManager manager, OWLOntology ontology, int i) throws Exception {
		long timeout = PerformanceTester.TIMEOUT*1000; // should be in msec	
		PerformanceTester ptester = new PerformanceTester(manager, ontology, ReasonerLoader.HERMIT, timeout);
		ptester.ADSamples();
		String[] reasoners = PerformanceTester.REASONERS;
		for (int j=0; j<reasoners.length; j++) {
			String reasoner = reasoners[j];
			Dataset dataset = ptester.getDatasets()[j];
			// get existing
			String dataFolder = "C:/Eclipse/workspace/OntCompPredictor/reports/modules/"+reasoner+"/";		
			File file = new File(dataFolder);
			File[] ontFiles = file.listFiles();
			if (i<ontFiles.length) {
				Dataset data = XMLDatasetReader.read(ontFiles[i]);
				dataset.addAll(data);
			}
			//
			if (dataset!=null && !dataset.isEmpty()) {
				XMLDatasetWriter writer = new XMLDatasetWriter(reasoner);
				writer.createDataSetFile(dataset, i);
			}
		}
	}
	
	public static void writeModules() {
		String ontFolder = "C:/_MSc/Dissertation/Nico/Ontologies/common/";
//		String ontFolder = "C:/_MSc/Dissertation/Ontologies/";		
		
		File file = new File(ontFolder);		
		File[] ontFiles = file.listFiles();		

		for (int i=111; i<112; i++) {
			File ontFile = ontFiles[i];
			System.out.println("\nOntology " + i + ": " + ontFile.getName());
			
			if (existsFile(i)) {
				System.out.println("...exists");
//				continue;
			}

			OntologyLoader loader = new OntologyLoader();    	    	
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology = null;
			try {
				ontology = loader.loadOntology(manager, IRI.create(ontFile));
			} catch (Exception e) {
				System.out.println("The ontology is not loaded");
				continue;
			}
			
			try {
				writeModules(manager, ontology, i);
			} catch (Exception e) {
				System.out.println("Writing modules failed");
				e.printStackTrace();
				continue;
			}
			
		}
	}
	
	public static boolean existsFile(int i) {
		String mFolder = "C:/Eclipse/workspace/OntCompPredictor/reports/modules/pellet/";	
		File mfile = new File(mFolder);		
		File[] mFiles = mfile.listFiles();
		boolean exists = false;
		for (int j=0; j<mFiles.length; j++) {
			File modFile = mFiles[j];
			if (i<10 && modFile.getName().indexOf("00"+i)>-1) {
				exists = true;				
			} else if (i>=10 && i<100 && modFile.getName().indexOf("0"+i)>-1) {
				exists = true;				
			} else if (i>=100 && modFile.getName().indexOf(""+i)>-1) {
				exists = true;				
			} else {				
			}
		}
		return exists;
	}

	public static void selectFeatures(String reasoner) {
		LinkedList<MarkovProcessEntry> base = new LinkedList<MarkovProcessEntry>();
		LinkedList<MarkovProcessEntry> test = new LinkedList<MarkovProcessEntry>();
		String rootDir = "/reports/";
		String workingDir = System.getProperty("user.dir");

		long bases = System.currentTimeMillis();

		File reasDir = new File(workingDir + rootDir + reasoner);
		File[] ontDirs = reasDir.listFiles();
		for (File ontDir : ontDirs) {
			File[] procFiles = ontDir.listFiles();
			for (int i=0; i<procFiles.length; i++) {
				File procFile = procFiles[i];				
				base.addAll(XMLKnowledgeReader.read(procFile).getLast().getEntries());
			}
			File procFile = procFiles[procFiles.length-1];					
			test.addAll(XMLKnowledgeReader.read(procFile).getLast().getEntries());
		}

		long basee = System.currentTimeMillis();		
		System.out.println("XML reading delay: " + (basee - bases));

		//		NearestNeighbour knn = new NearestNeighbour(base, test, 9);
		RewardCollector rc = new RewardCollector(base);
		Integer[] all = new Integer[new OntFeatureVector().toArray().length];		
		Arrays.fill(all, 1);
		double mz2 = rc.collect(all);
		System.out.println("max z2: "+mz2);
		Solution.maxerr = mz2;

		long knne = System.currentTimeMillis();
		System.out.println("Initialization delay: " + (knne - basee));

//		NSEvolutionaryAlgorithm ea = new NSEvolutionaryAlgorithm(rc, 400, 1, 10, 10);
//		ea.elitism(Solution.maxerr/100, 1, 0.95, Solution.maxerr*Solution.maxvars);
//		ea.printFront();		
//		ExcelWriter.writeFront(ea.getAllSolutions(), ea.getParetoFront(), reasoner);

		long prede = System.currentTimeMillis();
		System.out.println("\nPrediction delay: " + (prede - knne));
	}

	public static void featureStats(String reasoner) {
		LinkedList<MarkovProcessEntry> base = new LinkedList<MarkovProcessEntry>();		
		String rootDir = "/reports/";
		String workingDir = System.getProperty("user.dir");

		// read the last entry from each process
		File reasDir = new File(workingDir + rootDir + reasoner);
		File[] ontDirs = reasDir.listFiles();
		for (File ontDir : ontDirs) {
			File[] procFiles = ontDir.listFiles();
			for (int i=0; i<procFiles.length; i++) {
				File procFile = procFiles[i];						
				base.add(XMLKnowledgeReader.read(procFile).getLast().getEntries().getLast());
			}			
		}

		// build the histogram
		OntFeatureVector vec = new OntFeatureVector();
		int size = vec.toArray().length;
		double[] hist = new double[size];
		for (MarkovProcessEntry entry : base) {
			Double[] state = entry.getState();
			for (int i=0; i<hist.length; i++) {
				hist[i] += state[i];
			}
		}

		// normalize the histogram
		//		double sum = 0;
		//		for (int i=0; i<hist.length; i++) {
		//			sum += hist[i];
		//		}
		//		for (int i=0; i<hist.length; i++) {
		//			hist[i] /= sum;
		//		}

		ExcelWriter.write(hist, "Histogram");
	}

	public static void extractOntFeatures(OWLOntology ontology) {
		OntFeatureExtractor extractor = new OntFeatureExtractor();		
		extractor.extractFeatures(ontology);
		extractor.processOntologySignature(ontology);
		extractor.updateOntVector();
	}

	public static double measureOntTime(OWLOntologyManager manager, OWLOntology ontology) {
		long timeout = 1000000;
		PerformanceTester ptester = new PerformanceTester(manager, ontology, ReasonerLoader.PELLET, timeout);
		double time = ptester.testOntology();
		System.out.println("Mean reasoning time: " + time);
		return time;
	}

	public static void testAllOntologies() {
//		String ontFolder = "C:/_MSc/Dissertation/Ontologies/";
		String ontFolder = "C:/_MSc/Dissertation/Nico/Ontologies/common/";
		String mFolder = "C:/Eclipse/workspace/OntCompPredictor/reports/modules/pellet/";
		
		long timeout = PerformanceTester.TIMEHALT*1000; //should be in msec
		File file = new File(ontFolder);
		File mfile = new File(mFolder);
		File[] ontFiles = file.listFiles();
		File[] mFiles = mfile.listFiles();
		
		double[][] times = new double[ontFiles.length][3];
		
		for (int i=111; i<112; i++) {	
			File ontFile = ontFiles[i];
			System.out.println("\nOntology "+ i + ": " + ontFile.getName());	
			
			if (!existsFile(i)) {
				continue;
			}
			
			OntologyLoader loader = new OntologyLoader();    	    	
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology = null;
			try {
				ontology = loader.loadOntology(manager, IRI.create(ontFile));
			} catch (Exception e) {
				System.out.println("Ontology loading exception");
				continue;
			}
			//
			System.out.println("== JFact");
			PerformanceTester ptester = new PerformanceTester(manager, ontology, ReasonerLoader.JFACT, timeout);
			times[i][0] = ptester.testOntology();
//			
//			System.out.println("== Hermit");
//			ptester = new PerformanceTester(manager, ontology, ReasonerLoader.HERMIT, timeout);
//			times[i][1] = ptester.testOntology();
			
//			System.out.println("== Pellet");
//			PerformanceTester ptester = new PerformanceTester(manager, ontology, ReasonerLoader.PELLET, timeout);
//			times[i][2] = ptester.testOntology();
		}
		
		ExcelWriter.writeOntTests(ontFiles, times);
	}

	public static void writeKnowledgeBase() {
		String ontFolder = "C:/_MSc/Dissertation/Ontologies/";
		long timeout = 100000;		
		File file = new File(ontFolder);
		File[] ontFiles = file.listFiles();

		for (int i=74; i<75/*ontFiles.length*/; i++) {
			File ontFile = ontFiles[i];
			System.out.println("##### Ontology: " + ontFile.getName());
			OntologyLoader loader = new OntologyLoader();    	    	
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology = null;
			try {
				ontology = loader.loadOntology(manager, IRI.create(ontFile));
			} catch (Exception e) {
				continue;
			}
			//
			System.out.println("== JFact");
			PerformanceTester ptester = new PerformanceTester(manager, ontology, ReasonerLoader.JFACT, timeout);
			ptester.execute();
			LinkedList<MarkovProcess> processes = ptester.getKnowledge();
			for (MarkovProcess process: processes) {
				XMLKnowledgeWriter writer = new XMLKnowledgeWriter(process);
				writer.writeToFile(ReasonerLoader.JFACT, i);
			}
			//
			System.out.println("== Hermit");
			ptester.setReasonerName(ReasonerLoader.HERMIT);
			ptester.execute();
			processes = ptester.getKnowledge();
			for (MarkovProcess process: processes) {
				XMLKnowledgeWriter writer = new XMLKnowledgeWriter(process);
				writer.writeToFile(ReasonerLoader.HERMIT, i);
			}
			//
			System.out.println("== Pellet");
			ptester.setReasonerName(ReasonerLoader.PELLET);
			ptester.execute();
			processes = ptester.getKnowledge();
			for (MarkovProcess process: processes) {
				XMLKnowledgeWriter writer = new XMLKnowledgeWriter(process);
				writer.writeToFile(ReasonerLoader.PELLET, i);
			}
		}

	}

	public static LinkedList<MarkovProcess> readKnowledge(String reasoner) {
		LinkedList<MarkovProcess> base = new LinkedList<MarkovProcess>();

		String rootDir = "/reports/processes/";
		String workingDir = System.getProperty("user.dir");

		long bases = System.currentTimeMillis();

		File reasDir = new File(workingDir + rootDir + reasoner);

		File[] ontDirs = reasDir.listFiles();
		for (File ontDir : ontDirs) {
			File[] procFiles = ontDir.listFiles();
			for (int i=0; i<procFiles.length; i++) {
				File procFile = procFiles[i];				
				base.addAll(XMLKnowledgeReader.read(procFile));
			}			
		}

		long basee = System.currentTimeMillis();		
		System.out.println("XML reading delay: " + (basee - bases));

		return base;
	}

	public static void writeProcessDataset() {
//		LinkedList<MarkovProcess> jbase = readKnowledge(ReasonerLoader.JFACT);
		LinkedList<MarkovProcess> hbase = readKnowledge(ReasonerLoader.HERMIT);
//		LinkedList<MarkovProcess> pbase = readKnowledge(ReasonerLoader.PELLET);
		// 10 processes per each ontology
		int step = 10;
		int onts = hbase.size()/step;
//		OntFeatureVector[] jvectors = new OntFeatureVector[onts];
		OntFeatureVector[] hvectors = new OntFeatureVector[onts];
//		OntFeatureVector[] pvectors = new OntFeatureVector[onts];
		// Collect feature dynamics
		for (int i=0; i<onts; i++) {
			OntFeatureVector jvec = new OntFeatureVector();
			OntFeatureVector hvec = new OntFeatureVector();
			OntFeatureVector pvec = new OntFeatureVector();
			for (int j=i*step; j<(i+1)*step; j++) {
//				jvec.append(RewardCollector.rewards(jbase.get(j)));
				hvec.append(RewardCollector.rewards(hbase.get(j)));
//				pvec.append(RewardCollector.rewards(pbase.get(j)));
			}
//			jvectors[i] = jvec;
			hvectors[i] = hvec;
//			pvectors[i] = pvec;
		}
		
		// Read computation times
		double[][] times = ExcelReader.read("/reports/ont-labels-cor.xls");
		// JFact times
		double[] jtimes = new double[times.length];
		for (int i=0; i<times.length; i++) {
			jtimes[i] = times[i][0];
		}
		// Hermit times
		double[] htimes = new double[times.length];
		for (int i=0; i<times.length; i++) {
			htimes[i] = times[i][1];
		}
		// Pellet times
		double[] ptimes = new double[times.length];
		for (int i=0; i<times.length; i++) {
			ptimes[i] = times[i][2];
		}

		// JFact dataset
		XMLDatasetWriter 
//		writer = new XMLDatasetWriter(jvectors, jtimes, ReasonerLoader.JFACT);
//		writer.createDataSetFile();
		// Hermit dataset
		writer = new XMLDatasetWriter(ReasonerLoader.HERMIT);
		writer.createDataSetFile(hvectors, htimes);
		// Pellet dataset
//		writer = new XMLDatasetWriter(pvectors, ptimes, ReasonerLoader.PELLET);
//		writer.createDataSetFile();
	}

	public static void writeDataset() {
		String ontFolder = "C:/_MSc/Dissertation/Nico/Ontologies/common/";
//		String ontFolder = "C:/_MSc/Dissertation/Ontologies/";

		File file = new File(ontFolder);
		File[] ontFiles = file.listFiles();
		OntFeatureVector[] vectors = new OntFeatureVector[ontFiles.length];		

		for (int i=0; i<ontFiles.length; i++) {
			File ontFile = ontFiles[i];
			System.out.println("\nOntology "+ i + ": " + ontFile.getName());

			OntologyLoader loader = new OntologyLoader();    	    	
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology = null;
			try {
				ontology = loader.loadOntology(manager, IRI.create(ontFile));
			} catch (Exception e) {
				System.out.println("The ontology is not loaded");
				continue;
			}
			
			vectors[i] = new OntFeatureExtractor().extract(ontology);
			
			double nAxioms = ontology.getLogicalAxiomCount();
			double size = Regression.countAxioms(vectors[i].toArray());
			System.out.println("Real size/calculated size="+(nAxioms/size));
		}

		// Read computation times
		double[][] times = ExcelReader.read("/reports/ont-labels-nico.xls");
		// JFact times
		double[] jtimes = new double[times.length];
		for (int i=0; i<times.length; i++) {
			jtimes[i] = times[i][0];
		}
		// Hermit times
		double[] htimes = new double[times.length];
		for (int i=0; i<times.length; i++) {
			htimes[i] = times[i][1];
		}
		// Pellet times
		double[] ptimes = new double[times.length];
		for (int i=0; i<times.length; i++) {
			ptimes[i] = times[i][2];
		}

		// JFact dataset
		XMLDatasetWriter writer = new XMLDatasetWriter(ReasonerLoader.JFACT);
		writer.createDataSetFile(vectors, jtimes);
		// Hermit dataset
		writer = new XMLDatasetWriter(ReasonerLoader.HERMIT);
		writer.createDataSetFile(vectors, htimes);
		// Pellet dataset
		writer = new XMLDatasetWriter(ReasonerLoader.PELLET);
		writer.createDataSetFile(vectors, ptimes);
	}

	public static void testPrediction() {
		// read datasets
		String workingDir = System.getProperty("user.dir");
		File jdata = new File(workingDir+"/reports/dataset_jfact.xml");
		File hdata = new File(workingDir+"/reports/dataset_hermit.xml");
		File pdata = new File(workingDir+"/reports/dataset_pellet.xml");
		Dataset jdataset = XMLDatasetReader.read(jdata);
//		jdataset = jdataset.createSizeDataset();
		Dataset hdataset = XMLDatasetReader.read(hdata);
//		hdataset = hdataset.createSizeDataset();
		Dataset pdataset = XMLDatasetReader.read(pdata);
//		pdataset = pdataset.createSizeDataset();
		
//		// Read computation times
//		double[][] times = ExcelReader.read("/reports/ont-labels-nico.xls");
//		for (int i=0; i<times.length; i++) {
//			DatasetEntry jen = jdataset.get(i);
//			jen.time = times[i][0];
//			DatasetEntry hen = hdataset.get(i);
//			hen.time = times[i][1];
//			DatasetEntry pen = pdataset.get(i);
//			pen.time = times[i][2];
//		}		
		
		// KNN
		int k = 5;
		int maxK = 30;
		int[] fsel = new int[(new OntFeatureVector()).toArray().length];
		Arrays.fill(fsel, 1);
//		int[] fsel = new int[]{
//				1,	1,	0,	1,	1,	1,	1,	1,	1,	0,	0,	1,	0,	0,	0,	0,	0,	0,	1,	0,	1,	0,	0,	0,	0,	0,	1,	1,	0,	0,	1,	0,	1,	0,	0,	0,	0,	0,	0
//	
//		};
				
		// JFact prediction error		
		KNNClassifier knn = new KNNClassifier(jdataset, k);		
		System.out.println("JFact:");
		knn.estimateParameterAndError(maxK, fsel);
		// Hermit prediction error		
		knn = new KNNClassifier(hdataset, k);
		System.out.println("Hermit:");
		knn.estimateParameterAndError(maxK, fsel);
		// Pellet prediction error		
		knn = new KNNClassifier(pdataset, k);
		System.out.println("Pellet:");
		knn.estimateParameterAndError(maxK, fsel);
	}
	
	public static void testOntSize() {
		String ontFolder = "C:/_MSc/Dissertation/Ontologies/";
//		String ontFolder = "C:/_MSc/Dissertation/Nico/Ontologies/common/";

		File file = new File(ontFolder);
		File[] ontFiles = file.listFiles();
		double[] sizes = new double[ontFiles.length];
		String[] dlexps = new String[sizes.length];

		for (int i=0; i<ontFiles.length; i++) {			
			File ontFile = ontFiles[i];
			System.out.print("\nOntology " + i + ": " + ontFile.getName());

			OntologyLoader loader = new OntologyLoader();    	    	
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology = null;
			try {
				ontology = loader.loadOntology(manager, IRI.create(ontFile));
			} catch (Exception e) {
				System.out.println("The ontology is not loaded");
				continue;
			}
			
			// EL, QL, RL profiles
			OWL2ELProfile el = new OWL2ELProfile();
			OWLProfileReport rep = el.checkOntology(ontology);
			System.out.println("\nin EL: " + rep.isInProfile());
			
			OWL2QLProfile ql = new OWL2QLProfile();
			rep = ql.checkOntology(ontology);
			System.out.println("in QL: " + rep.isInProfile());
			
			OWL2RLProfile rl = new OWL2RLProfile();
			rep = rl.checkOntology(ontology);
			System.out.println("in RL: " + rep.isInProfile());
			
			// get DL expressivity
			Set<OWLOntology> ontset = new HashSet<OWLOntology>();
			ontset.add(ontology);
			DLExpressivityChecker checker = new DLExpressivityChecker(ontset);			
			
			dlexps[i] = checker.getDescriptionLogicName();
			
			// get size
			sizes[i] = ontology.getLogicalAxiomCount();
			System.out.println(" of size = "+sizes[i]);
		}
		System.out.println("Ontology sizes:");
		for (int i=0; i<sizes.length; i++) {
			System.out.println(sizes[i]);
		}
		
		System.out.println("Ontology DL expressivity:");
		for (int i=0; i<dlexps.length; i++) {
			System.out.println(dlexps[i]);
		}
				
		double maxSize = -1;		
		for (int i=0; i<sizes.length; i++) {
			if (maxSize<sizes[i]) {
				maxSize = sizes[i];				
			}
		}		
		// size hist
		int nBins = 20;
		double[] sizeHist = new double[nBins];
		for (int i=0; i<sizes.length; i++) {
			double relSize = sizes[i]/maxSize;
			int bin = (int)(relSize*nBins);
			if (bin==nBins) {
				sizeHist[nBins-1]++;
			} else {
				sizeHist[bin]++;
			}
		}
		double[] sizeBounds = new double[sizeHist.length];
		for (int i=0; i<sizeBounds.length; i++) {
			sizeBounds[i] = Math.floor((i+1)*maxSize/nBins);
		}
		double[][] sizeData = new double[sizeHist.length][2];
		for (int i=0; i<sizeData.length; i++) {
			sizeData[i][0] = sizeBounds[i];
			sizeData[i][1] = sizeHist[i];
		}
		ExcelWriter.write(sizeData, "Sizes");
		
		// expressivity hist		
		Set<String> dlexpSet = new HashSet<String>();
		for (int i=0; i<dlexps.length; i++) {
			dlexpSet.add(dlexps[i]);
		}
		Map<String, Integer> dlexpMap = new HashMap<String, Integer>();
		for (String key : dlexpSet) {
			dlexpMap.put(key, 0);
		}		
		for (int i=0; i<dlexps.length; i++) {
			Integer value = dlexpMap.get(dlexps[i]);
			dlexpMap.put(dlexps[i], value+1);
		}
		ExcelWriter.write(dlexpMap, "Expressivity");
		
		// Read computation times
		double[][] times = ExcelReader.read("/reports/ont-labels-nico.xls");
		double[][] rHist = new double[nBins][3];		
		for (int i=0; i<times.length; i++) {
			double jRelTime = times[i][0]/PerformanceTester.TIMEHALT;
			double hRelTime = times[i][1]/PerformanceTester.TIMEHALT;
			double pRelTime = times[i][2]/PerformanceTester.TIMEHALT;
			int jbin = (int)(jRelTime*nBins);
			int hbin = (int)(hRelTime*nBins);
			int pbin = (int)(pRelTime*nBins);
			if (jbin==nBins) {
				rHist[nBins-1][0]++;
			} else {
				rHist[jbin][0]++;
			}
			if (hbin==nBins) {
				rHist[nBins-1][1]++;
			} else {
				rHist[hbin][1]++;
			}
			if (pbin==nBins) {
				rHist[nBins-1][2]++;
			} else {
				rHist[pbin][2]++;
			}
		}
		ExcelWriter.write(rHist, "TimesHist");
	}
	
	public static void findMinOntSignature() {
		String ontFolder = "C:/_MSc/Dissertation/Ontologies/";

		File file = new File(ontFolder);
		File[] ontFiles = file.listFiles();
		
		int minSign = Integer.MAX_VALUE;

		for (int i=0; i<ontFiles.length; i++) {
			File ontFile = ontFiles[i];
			System.out.println("##### Ontology: " + ontFile.getName());

			OntologyLoader loader = new OntologyLoader();    	    	
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology = null;
			try {
				ontology = loader.loadOntology(manager, IRI.create(ontFile));
			} catch (Exception e) {
				System.out.println("The ontology is not loaded");
				continue;
			}
			
			int signSize = ontology.getSignature().size();
			if (signSize < minSign) {
				minSign = signSize;
			}
		}
		
		System.out.println("The minimal signature size: "+minSign);
	}
		
	public static void testModules() {
		String dataFolder = "C:/Eclipse/workspace/OntCompPredictor/reports/modules/jfact/";		
		File file = new File(dataFolder);
		File[] ontFiles = file.listFiles();
		double timeout = PerformanceTester.TIMEHALT;
		// read datasets
		String workingDir = System.getProperty("user.dir");
		File jfull = new File(workingDir+"/reports/dataset_jfact.xml");		
		Dataset jfullset = XMLDatasetReader.read(jfull);		
		// Read computation times
//		double[][] times = ExcelReader.read("/reports/ont-labels-nico.xls");
//		for (int i=0; i<times.length; i++) {
//			DatasetEntry jen = jfullset.get(i);
//			// 0 - JFact, 1 - Hermit, 2 - Pellet
//			jen.time = times[i][1];			
//		}
		jfullset.setLabels();
		
		PCA pca = new PCA(jfullset);
		jfullset.updateVectors(pca.getEncodedData());
//		jfullset.print();		
		
		double nerrors = 0;
		double mse = 0;
		
		for (int i=0; i<ontFiles.length; i++) {			
			File ontFile = ontFiles[i];
			String ontName = ontFile.getName();
			System.out.println("\nOntology " + i + ": " + ontName);
			// init regression
			Dataset ontData = XMLDatasetReader.read(ontFile);
			ontData.repairTimeout();
			ontData.addTrivialEntry();			
			
			PCA locPca = new PCA(ontData, 1);
			ontData.updateVectors(locPca.getEncodedData());
//			ontData.print();
			
			PolynomRegression regr = new PolynomRegression(ontData, 1);			
			System.out.println("Polynomial degree = " + regr.getFitted().degree());
//			LinearRegression regr = new LinearRegression(ontData);

			// prediction
			String fullNum = ontName.substring(0, ontName.indexOf("."));
			int id = Integer.parseInt(fullNum);
			DatasetEntry whole = jfullset.getById(id);
			if (whole == null) {
				continue;
			}

			double mean = regr.predict(whole.vector);
			if (mean>timeout) {
				mean = timeout+1;
			}
			int label = Dataset.estimateLabel(mean);
			if (label != whole.label) {
				nerrors++;
			}
			System.out.println("Ontology #"+i+":"+" prediction="+mean+" ("+label+")"+" actual="+whole.time+" ("+whole.label+")"
					+" | max mod size="+(ontData.maxSize()/whole.countAxioms()));
			mse += (mean - whole.time)*(mean - whole.time);
		}
				
		nerrors /= ontFiles.length;
		System.out.println("\nLinear Regression error (classification): " + nerrors);
		mse = Math.sqrt(mse/ontFiles.length);
		System.out.println("Linear Regression MSE: "+mse);
	}
	
	public static void testAD() {
//		String ontPath = "C:/_MSc/Dissertation/Ontologies/01-vaccine-ontology.owl.xml";
		String ontPath = "C:/_MSc/Dissertation/Nico/Ontologies/common/03.10J_Release.owl";
		File ontFile = new File(ontPath);
		
		OntologyLoader loader = new OntologyLoader();    	    	
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = null;
		try {
			ontology = loader.loadOntology(manager, IRI.create(ontFile));
		} catch (Exception e) {
			System.out.println("The ontology is not loaded");			
		}
		long start = System.currentTimeMillis();
		OWLAPIAtomicDecomposition ad = new OWLAPIAtomicDecomposition(ontology);
		long end = System.currentTimeMillis();
		System.out.println("AD finished in "+(end-start)+" msec");
	}

	public static void extractFeatures(String ontPath) {		

		File ontFile = new File(ontPath);

		OntologyLoader loader = new OntologyLoader();    	    	
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = null;
		try {
			ontology = loader.loadOntology(manager, IRI.create(ontFile));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("The ontology is not loaded");			
		}

		OntFeatureExtractor extr = new OntFeatureExtractor();
		Double[] features = extr.extract(ontology).toArray();
		for (int i=0; i<features.length; i++) {
			System.out.print(features[i].intValue() + " & ");
		}
	}
	
	public static void testPCA() {
		// read datasets
		String workingDir = System.getProperty("user.dir");
		File jdata = new File(workingDir+"/reports/dataset_jfact.xml");		
		Dataset jdataset = XMLDatasetReader.read(jdata);		
		
		PCA pca = new PCA(jdataset, 52);
		double[][] enc = pca.getEncodedData();
//		System.out.println("\nEncoding:");
//		for (int i=0; i<enc.length; i++) {			
//			System.out.println(enc[i][0]);			
//		}
		
		double[] sizes = new double[jdataset.size()];
		for (int i=0; i<sizes.length; i++) {
//			if (i!=68 && i!=86 && i!=102 && i!=111 && i!=118) {
				DatasetEntry en = jdataset.get(i);
				sizes[i] = en.countAxioms();
//			}
		}
		
		FeatureRanker ranker = new FeatureRanker(enc, sizes);
		System.out.println("\nPC ranks:");
		ranker.rankAll();
	}
	
	
	public static void rankFeatures() {
		// read datasets
		String workingDir = System.getProperty("user.dir");
		File jdata = new File(workingDir+"/reports/dataset_jfact.xml");
		File hdata = new File(workingDir+"/reports/dataset_hermit.xml");
		File pdata = new File(workingDir+"/reports/dataset_pellet.xml");
		Dataset jdataset = XMLDatasetReader.read(jdata);		
		Dataset hdataset = XMLDatasetReader.read(hdata);		
		Dataset pdataset = XMLDatasetReader.read(pdata);
		
		// Read computation times
		double[][] times = ExcelReader.read("/reports/ont-labels-nico.xls");
		for (int i=0; i<times.length; i++) {
			DatasetEntry jen = jdataset.get(i);
			jen.time = times[i][0];
			DatasetEntry hen = hdataset.get(i);
			hen.time = times[i][1];
			DatasetEntry pen = pdataset.get(i);
			pen.time = times[i][2];
		}
		
		FeatureRanker ranker = new FeatureRanker(jdataset);
		System.out.println("\nRanking all features - JFact");
		ranker.rankAll();
		
		ranker = new FeatureRanker(hdataset);
		System.out.println("\nRanking all features - Hermit");
		ranker.rankAll();
		
		ranker = new FeatureRanker(pdataset);
		System.out.println("\nRanking all features - Pellet");
		ranker.rankAll();
	}
	
	public static void printMaxModules() {
		String dataFolder = "C:/Eclipse/workspace/OntCompPredictor/reports/modules/pellet/";		
		File file = new File(dataFolder);
		File[] ontFiles = file.listFiles();
		
		// read datasets
		String workingDir = System.getProperty("user.dir");
		File jfull = new File(workingDir+"/reports/dataset_pellet.xml");		
		Dataset jfullset = XMLDatasetReader.read(jfull);
		jfullset.setLabels();
		
		for (int i=0; i<ontFiles.length; i++) {			
			File ontFile = ontFiles[i];
			String ontName = ontFile.getName();
			System.out.println("\nOntology " + i + ": " + ontName);
			// init regression
			Dataset ontData = XMLDatasetReader.read(ontFile);
			String fullNum = ontName.substring(0, ontName.indexOf("."));
			int id = Integer.parseInt(fullNum);
			DatasetEntry whole = jfullset.getById(id);
			System.out.println("\tmax module size = "+(ontData.maxSize()/whole.countAxioms()));
		}
	}
	
	public static void compareAlgorithms() {
		String reasoner = ReasonerLoader.JFACT;
		String NEFolder = "C:/Eclipse/workspace/OntCompPredictor/reports/NE modules 1/"+reasoner+"/";	
		String HEFolder = "C:/Eclipse/workspace/OntCompPredictor/reports/HE modules 1/"+reasoner+"/";
		String REFolder = "C:/Eclipse/workspace/OntCompPredictor/reports/RE modules 1/"+reasoner+"/";
		File nefile = new File(NEFolder);
		File[] neOntFiles = nefile.listFiles();
		File hefile = new File(HEFolder);
		File[] heOntFiles = hefile.listFiles();
		File refile = new File(REFolder);
		File[] reOntFiles = refile.listFiles();		
		// read datasets
		String workingDir = System.getProperty("user.dir");
		File fullFile = new File(workingDir+"/reports/dataset_jfact.xml");				
		Dataset fullSet = XMLDatasetReader.read(fullFile);		
		
		double sizeLimit = 0.1;
		int nBins = 20;
		
		double[][] neBins = new double[neOntFiles.length][nBins];
		double[][] heBins = new double[neOntFiles.length][nBins];
		double[][] reBins = new double[neOntFiles.length][nBins];
		
		double[] neAngles = new double[neOntFiles.length];
		double[] heAngles = new double[neOntFiles.length];
		double[] reAngles = new double[neOntFiles.length];
		
		// collect size bins
		for (int i=0; i<neOntFiles.length; i++) {			
			File neOntFile = neOntFiles[i];
			String ontName = neOntFile.getName();
			System.out.println("\nOntology " + i + ": " + ontName);
			File heOntFile = heOntFiles[i];
			File reOntFile = reOntFiles[i];
			// read datasets
			Dataset neOntData = XMLDatasetReader.read(neOntFile);
			neOntData.addTrivialEntry();
			Dataset heOntData = XMLDatasetReader.read(heOntFile);
			heOntData.addTrivialEntry();
			Dataset reOntData = XMLDatasetReader.read(reOntFile);
			reOntData.addTrivialEntry();
			
			DatasetEntry whole = fullSet.getById(i);
			// binning samples
			SampleBinner neBinner = new SampleBinner(neOntData, whole.countAxioms(), sizeLimit, nBins);
			neBins[i] = neBinner.getBins();
			neAngles[i] = neBinner.getAngle();
			SampleBinner heBinner = new SampleBinner(heOntData, whole.countAxioms(), sizeLimit, nBins);
			heBins[i] = heBinner.getBins();
			heAngles[i] = heBinner.getAngle();
			SampleBinner reBinner = new SampleBinner(reOntData, whole.countAxioms(), sizeLimit, nBins);
			reBins[i] = reBinner.getBins();
			reAngles[i] = reBinner.getAngle();
						
		}
		
		ExcelWriter.write(neBins, "NE");
		ExcelWriter.write(heBins, "HE");
		ExcelWriter.write(reBins, "RE");
		
		ExcelWriter.write(neAngles, "NE-angles");
		ExcelWriter.write(heAngles, "HE-angles");
		ExcelWriter.write(reAngles, "RE-angles");		
	}
	
	public static void compareADandCU() {
		String reasoner = ReasonerLoader.HERMIT;
		String NEFolder = "C:/Eclipse/workspace/OntCompPredictor/reports/NE modules/"+reasoner+"/";
		String CUFolder = "C:/Eclipse/workspace/OntCompPredictor/reports/CU modules/"+reasoner+"/";
		
		File nefile = new File(NEFolder);
		File[] neOntFiles = nefile.listFiles();
		File cufile = new File(CUFolder);
		File[] cuOntFiles = cufile.listFiles();
			
		// read datasets
		String workingDir = System.getProperty("user.dir");
		File fullFile = new File(workingDir+"/reports/dataset_hermit.xml");				
		Dataset fullSet = XMLDatasetReader.read(fullFile);		
		
		double sizeLimit = 0.1;
		int nBins = 20;
		
		double[][] neBins = new double[neOntFiles.length][nBins];
		double[][] cuBins = new double[neOntFiles.length][nBins];
		
		double[] neAngles = new double[neOntFiles.length];
		double[] cuAngles = new double[neOntFiles.length];		
		
		// collect size bins
		for (int i=0; i<neOntFiles.length; i++) {			
			File neOntFile = neOntFiles[i];
			String ontName = neOntFile.getName();
			System.out.println("\nOntology " + i + ": " + ontName);
			File cuOntFile = cuOntFiles[i];
			// read datasets
			Dataset neOntData = XMLDatasetReader.read(neOntFile);
			Dataset cuOntData = XMLDatasetReader.read(cuOntFile);
			
			DatasetEntry whole = fullSet.getById(i);
			
			// binning samples
			SampleBinner neBinner = new SampleBinner(neOntData, whole.countAxioms(), sizeLimit, nBins);
			neBins[i] = neBinner.getBins();
			neAngles[i] = neBinner.getAngle();
			SampleBinner cuBinner = new SampleBinner(cuOntData, whole.countAxioms(), sizeLimit, nBins);
			cuBins[i] = cuBinner.getBins();
			cuAngles[i] = cuBinner.getAngle();
		}
		
		ExcelWriter.write(neBins, "AD");
		ExcelWriter.write(cuBins, "CU");		
		
		ExcelWriter.write(neAngles, "AD-angles");
		ExcelWriter.write(cuAngles, "CU-angles");
				
	}
	
	public static void analyseDataVolume() {
		String dataFolder = "C:/Eclipse/workspace/OntCompPredictor/reports/modules/jfact/";		
		File file = new File(dataFolder);
		File[] ontFiles = file.listFiles();
		double timeout = PerformanceTester.TIMEHALT;
		// read datasets
		String workingDir = System.getProperty("user.dir");
		File jfull = new File(workingDir+"/reports/dataset_jfact.xml");		
		Dataset jfullset = XMLDatasetReader.read(jfull);		
		jfullset.setLabels();
				
		int nBins = 21;
		double step = 0.005;
		int degree = 1;
		
		double[] aces = new double[nBins];
		double[] mses = new double[nBins];
		
		for (int s=0; s<nBins; s++) {
			System.out.println("bin "+s);
			for (int i=0; i<ontFiles.length; i++) {			
				File ontFile = ontFiles[i];
				String ontName = ontFile.getName();
//				System.out.println("\nOntology " + i + ": " + ontName);
				// init regression
				Dataset ontData = XMLDatasetReader.read(ontFile);
				ontData.repairTimeout();
				ontData.addTrivialEntry();

				// prediction
				String fullNum = ontName.substring(0, ontName.indexOf("."));
				int id = Integer.parseInt(fullNum);
				DatasetEntry whole = jfullset.getById(id);
				if (whole == null) {
					continue;
				}

				PolynomRegression regr = new PolynomRegression(ontData, degree, (s+1)*step, whole.countAxioms());

				double mean = regr.predict(whole.vector);
				if (mean>timeout) {
					mean = timeout+1;
				}
				int label = Dataset.estimateLabel(mean);
				if (label != whole.label) {
					aces[s]++;
				}
				mses[s] += (mean - whole.time)*(mean - whole.time);
			}

		}	
		// normalize ACEs and MSEs
		for (int i=0; i<aces.length; i++) {
			aces[i] /= ontFiles.length;
		}
		for (int i=0; i<mses.length; i++) {
			mses[i] = Math.sqrt(mses[i]/ontFiles.length);
		}
		// write results
		ExcelWriter.write(aces, "ACEs");
		ExcelWriter.write(mses, "MSEs");
	}
	
	public static void timeAgainstSize() {
		// read datasets
		String workingDir = System.getProperty("user.dir");
		File jdata = new File(workingDir+"/reports/dataset_jfact.xml");
		File hdata = new File(workingDir+"/reports/dataset_hermit.xml");
		File pdata = new File(workingDir+"/reports/dataset_pellet.xml");
		// all 52 features
		Dataset jdataset = XMLDatasetReader.read(jdata);		
		Dataset hdataset = XMLDatasetReader.read(hdata);		
		Dataset pdataset = XMLDatasetReader.read(pdata);
		
		PCA pca = new PCA(jdataset, 1);
		double[][] enc = pca.getEncodedData();		
		// compute sizes
		double[] sizes = new double[enc.length];		
		for (int i=0; i<sizes.length; i++) {
			sizes[i] = enc[i][0];
		}
		// sort ontologies by size
		AIComparator comp = new AIComparator(sizes, AIComparator.asc);
		Integer[] inds = comp.createIndexArray();
		Arrays.sort(inds, comp);

		// collect the data in a single array
		double[][] timeSize = new double[inds.length][4];
		for (int i=0; i<timeSize.length; i++) {
			timeSize[i][0] = sizes[inds[i]];
			timeSize[i][1] = jdataset.get(inds[i]).time;
			timeSize[i][2] = hdataset.get(inds[i]).time;
			timeSize[i][3] = pdataset.get(inds[i]).time;
		}
		ExcelWriter.write(timeSize, "Time-PC1");
	}

}
