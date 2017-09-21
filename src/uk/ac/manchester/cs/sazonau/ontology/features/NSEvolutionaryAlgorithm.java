package uk.ac.manchester.cs.sazonau.ontology.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import jjtraveler.Collect;

import uk.ac.manchester.cs.sazonau.model.regressor.NearestNeighbour;
import uk.ac.manchester.cs.sazonau.model.regressor.RewardCollector;
import uk.ac.manchester.cs.sazonau.ontology.ModuleExtractor;

public class NSEvolutionaryAlgorithm {	

	public final static int popsize = 100;
	private final static double p_c = 0.7;
	private static final int sout = 500;
	//	private static final int ND_SOUT=10;
	private static final int unranked = -1;
	
	private int maxiters;
	private double pergenome; // mutation rate per genome. So if this is set to 1.0 the expected number of mutations per individual is 1.0
	private int tsize1;  // tournament size
	private int tsize2;  // tournament size for selection of a poor individual to be replaced

	private ArrayList<Solution> P;

	private ArrayList<Solution> front;

	// elitism	
	private Solution[][] grid;
	private ArrayList<Solution> elite;

	private ModuleExtractor evaluator;

	private double[] flist;

	private int[] vlist;	

	public NSEvolutionaryAlgorithm(ModuleExtractor evaluator, int mAXITERS, double pergenome, int tsize1,
			int tsize2) {
		super();
		maxiters = mAXITERS;
		this.pergenome = pergenome;
		this.tsize1 = tsize1;
		this.tsize2 = tsize2;
		this.evaluator = evaluator;
		init();
	}	

	private void init() {
		P = new ArrayList<Solution>();
		front = new ArrayList<Solution>();
		elite = new ArrayList<Solution>();
		initializeFS();
		System.out.println("The population is initialized");
	}

	private void mutate(Solution c)	{
		double mrate=pergenome;
		int i;
		for(i=0; i<Solution.maxvars; i++) {
			if(Math.random()<mrate/Solution.maxvars) {
				c.x[i] = (c.x[i]==0 ? 1 : 0);
			}				
		}
	}

	private int tourn_worst() {
		int tourn_size = tsize2;
		int worst = -1;
		int worst_rank = unranked;
		int i = 0;
		int mem;
		double worst_crowding = Integer.MAX_VALUE;		

		while(i < tourn_size) {
			mem = (int)(Math.random()*P.size());
			// crowding-based
			Solution s = P.get(mem);
			if(s.rank > worst_rank) {
				worst_rank = s.rank;
				worst = mem;
				worst_crowding = s.crowding;
			}  else if (s.rank == worst_rank) {
				if (worst_crowding > s.crowding) {
					worst_crowding = s.crowding;
					worst = mem;
				}
			}			
			i++;
		}

		return worst;
	}


	private int tournament_select(int parent_a) {
		int tourn_size = tsize1;
		int fittest = -1;
		int i = 0;
		int mem;
		int best_rank = Integer.MAX_VALUE;
		double best_crowding = -1;		

		while (i < tourn_size) {
			mem = (int)(Math.random()*P.size());
			if (mem == parent_a) {
				continue;
			}
			// crowding-based
			Solution s = P.get(mem);
			if (s.rank < best_rank) {
				best_rank = s.rank;
				fittest = mem;
				best_crowding = s.crowding;
			} else if (s.rank == best_rank) {
				if (best_crowding < s.crowding) {
					best_crowding = s.crowding;
					fittest = mem;
				}
			}
			i++;
		}

		return fittest;
	}

	private void initialize() {		
		for(int i=0;i<popsize;i++) {
			Solution s = new Solution();
			for(int j=0;j<Solution.maxvars;j++)
			{				
				s.x[j] = Math.random()<0.5 ? 0 : 1; 				
			}
			evaluate(s);
			P.add(s);
		}
		ndsort();
	}
	
	private void initializeFS() {
		for(int i=0;i<popsize;i++) {
			Solution s = new Solution();
			int len = s.x.length;
			int block = (int)(Math.random()*len*0.2);
			// permute
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (int j=0; j<len; j++) {
				list.add(j);
			}
			Collections.shuffle(list);
			// set selected features
			for (int j=0; j<block; j++) {
				int b = s.x[list.get(j)];
				s.x[list.get(j)] = ((b==0) ? 1 : 0);
			}			
			evaluate(s);
//			System.out.println(i+"th individual is evaluated");
			P.add(s);
		}
		ndsort();
	}

	private Solution crossover(Solution p1, Solution p2) {
		// this function should implement uniform crossover
		Solution ch = new Solution();
		boolean[] mask = mask();
		for (int i=1; i<mask.length; i++) {
			if (mask[i]) {
				ch.x[i] = p1.x[i];
			} else {
				ch.x[i] = p2.x[i];
			}
		}
		return ch;
	}

	private static boolean[] mask() {
		boolean[] mask = new boolean[Solution.maxvars];
		for (int i=0; i<mask.length; i++) {
			if (Math.random()<0.5) {
				mask[i] = false;
			} else {
				mask[i] = true;
			}
		}
		return mask;
	}

	public void search(double ratio, int total) {

		Solution mutant = null;		

		int parent_a, parent_b;		

		for (int i=P.size(); i<=maxiters; i++) {			
			ndsort();

			parent_a = tournament_select(-1);
			Solution A = P.get(parent_a);

			if (Math.random() < p_c) { // this is p_c the crossover probability			
				parent_b = tournament_select(parent_a);
				mutant = crossover(A, P.get(parent_b)); // sexual reproduction
			} else
				mutant = A.clone(); // clone the parent 

			mutate(mutant); // always mutate
			evaluate(mutant);
			int worst = tourn_worst();
			P.remove(worst);
			P.add(mutant);			

			fillFront();
			double V = hypervolume();
			if (V/total > ratio) {
				break;
			}
		}
	}
	
	public void search() {

		Solution mutant = null;		

		int parent_a, parent_b;		

		for (int i=P.size(); i<=maxiters; i++) {			
			ndsort();

			parent_a = tournament_select(-1);
			Solution A = P.get(parent_a);

			if (Math.random() < p_c) { // this is p_c the crossover probability			
				parent_b = tournament_select(parent_a);
				mutant = crossover(A, P.get(parent_b)); // sexual reproduction
			} else
				mutant = A.clone(); // clone the parent 

			mutate(mutant); // always mutate
			evaluate(mutant);
			int worst = tourn_worst();
			P.remove(worst);
			P.add(mutant);			

			if (i%sout == 1) {
//				fillFront();
//				printFront();
//				int V = hypervolume();
//				System.out.println("\nHypervolume: "+V);				
			}
			
		}
		
		fillFront();
//		printFront();
//		int V = hypervolume();
//		System.out.println("\nHypervolume: "+V);
	}

	public void elitism(double cell_fit, int cell_var, double ratio, double total) {
		initGrid(cell_fit, cell_var);		

		for (int i=0; i<maxiters; i++) {
			fillGrid(cell_fit, cell_var);	
			removeDominated();
			eliteOffspring();			

			double V = elitevolume();
			System.out.println("iteration "+i+" ratio: "+V/total);
			if (V/total > ratio) {
				break;
			}
		}
	}
	
	public void elitism(double cell_fit, int cell_var) {
		initGrid(cell_fit, cell_var);

		for (int i=popsize; i<=maxiters; i++) {
			fillGrid(cell_fit, cell_var);	
			removeDominated();
			eliteOffspring();			

			if (i%sout == 1) {
//				P.addAll(elite);				
//				fillFront();
//				printFront();
//				int V = hypervolume();
//				System.out.println("\nHypervolume: "+V);				
			}			
		}
		
		P.addAll(elite);				
		fillFront();
		clearFront();
//		printFront();
//		int V = hypervolume();
//		System.out.println("\nHypervolume: "+V);
	}

	private void initGrid(double cell_fit, int cell_var) {
		grid = new Solution[Solution.maxvars/cell_var+1][(int)Math.ceil(Solution.maxerr/cell_fit)+1];		
	}

	private void fillGrid(double cell_fit, int cell_var) {
		for (Solution p : P) {
			int z1 = (int)(p.fitness/cell_fit);
			int z2 = p.vfalse/cell_var;
			Solution gp = grid[z2][z1];
			if (gp == null) {
				grid[z2][z1] = p;				
			} else if (p.fitness >= gp.fitness && p.vfalse >= gp.vfalse) {
				grid[z2][z1] = p;				
			}			
		}
	}

	private void printGrid() {
		System.out.println("Grid: ");
		for (int i=0; i<grid.length; i++) {
			for (int j=0; j<grid[0].length; j++) {
				int filled = (grid[i][j] != null ? 1 : 0);
				System.out.print(filled+" ");

			}
			System.out.println();
		}
	}

	private void removeDominated() {
		elite.clear();		
		int last = -1;
		for (int i=grid.length-1; i>=0; i--) {
			int maxj = -2;			
			for (int j=0; j<grid[0].length; j++) {
				if (grid[i][j] != null) {
					maxj = j;
					if (j < last) {
						grid[i][j].clear();
						grid[i][j] = null;											
					} else if (j == last) {
						elite.add(grid[i][j]);					
					} else {
						elite.add(grid[i][j]);
					}
				}
			}
			if (last <= maxj) {
				last = maxj;
			}
		}
	}
	
	private boolean existsHigher(int i, int j) {
		for (int k=i-1; k>=0; k--) {
			if (grid[k][j] != null) {
				return true;
			}
		}
		return false;
	}

	private void eliteOffspring() {
		int parent_a = -1;
		int parent_b = -1;
		Solution mutant = null;
		P.clear();

		for (int i=0; i<popsize; i++) {			
			parent_a = elite_select(-1);
			Solution A = elite.get(parent_a);
			if (Math.random() < p_c) { // this is p_c the crossover probability				
				parent_b = elite_select(parent_a);				
				mutant = crossover(A, elite.get(parent_b)); // sexual reproduction
			} else
				mutant = A.clone(); // clone the parent 

			mutate(mutant); // always mutate
			evaluate(mutant);
			P.add(mutant);			
		}

	}

	private int elite_select(int parent_a) {
		int mem = (int)(Math.random()*elite.size());
		int count = 0;
		while (mem == parent_a && count < 10) {
			mem = (int)(Math.random()*elite.size());
			count++;
		}
		return mem;
	}

	private void fillFront() {
		ndsort();
		front.clear();		
		for (Solution s : P) {
			if (s.rank == 1) {
				front.add(s);
			}
		}
	}

	public void printFront() {
		System.out.println("\nPareto front:");			
		for (Solution p : front) {			
			System.out.println(p + "=("+(p.fitness/Solution.maxerr)+","+p.vfalse+")");			
		};
	}

	public void evaluate(Solution s) {
		s.vfalse = 0;
		s.fitness = 0;
		for (int i=0; i<s.x.length; i++) {
			if (s.x[i] == 0) {
				s.vfalse++;
			}
		}
//		long st = System.currentTimeMillis();
		evaluator.calcSize(s);
//		long en = System.currentTimeMillis();
//		System.out.println("error computation delay: "+((en-st)/1000)+" fitness: "+s.fitness);
	}

	private void ndsort() {
		updateObjLists();
		// z1
		AIComparator comparator1 = new AIComparator(flist, AIComparator.desc);
		Integer[] indexes2 = comparator1.createIndexArray();
		Arrays.sort(indexes2, comparator1);
		// z2
		AIComparator comparator2 = new AIComparator(vlist, AIComparator.desc);		
		Arrays.sort(indexes2, comparator2);

		int iter = 0;
		while (!isNDSorted()) {
			iter++;
			double last = Double.NEGATIVE_INFINITY;
			// ranking
			Solution slast = null;
			for (int i=0; i<P.size(); i++) {
				Solution s = P.get(indexes2[i]);				
				if (s.rank != unranked) continue;
				double z1 = flist[indexes2[i]];				
				if (z1 > last) {					
					s.rank = iter;
					last = z1;					
				} else if (s.equals(slast)) {
					s.rank = slast.rank;
				}
				slast = s;
			}
			// crowding
//			Solution s0 = P.get(indexes2[0]);
//			Solution s1 = P.get(indexes2[1]);
//			s0.crowding = 4*(s1.fitness-s0.fitness)*
//					(s0.vfalse-s1.vfalse);
//			Solution sl1 = P.get(indexes2[P.size()-1]);
//			Solution sl2 = P.get(indexes2[P.size()-2]);
//			sl1.crowding = 4*(sl1.fitness-sl2.fitness)*
//					(sl2.vfalse-sl1.vfalse);
//
//			for (int i=1; i<P.size()-1; i++) {
//				Solution si = P.get(indexes2[i]);
//				Solution sim1 = P.get(indexes2[i-1]);
//				Solution sip1 = P.get(indexes2[i+1]);
//				si.crowding = 4*(sip1.fitness-sim1.fitness)*
//						(sim1.vfalse-sip1.vfalse);
//			}
		}
	}

	private boolean isNDSorted() {
		for (Solution s : P) {
			if (s.rank == unranked) {
				return false;
			}
		}
		return true;
	}

	private void updateObjLists() {
		flist = new double[P.size()];
		vlist = new int[P.size()];		
		for (int i=0; i<P.size(); i++) {
			Solution s = P.get(i);
			flist[i] = s.fitness;
			vlist[i] = s.vfalse;
			s.rank = unranked;
			s.crowding = -1;
		}
	}

	public Solution[] getAllSolutions() {
		return P.toArray(new Solution[P.size()]);
	}

	public ArrayList<Solution> getParetoFront() {
		return front;
	}
	
	private void clearFront() {
		HashSet<Solution> set = new HashSet<Solution>(front);
		front = new ArrayList<Solution>(set);
	}

	public double hypervolume() {
		double[] fits = new double[front.size()];
		int[] vfals = new int[front.size()];
		for (int i=0; i<front.size(); i++) {
			Solution s = front.get(i);
			fits[i] = s.fitness;
			vfals[i] = s.vfalse;
		}

		// z1
		AIComparator comparator1 = new AIComparator(fits, AIComparator.desc);
		Integer[] indexes2 = comparator1.createIndexArray();
		Arrays.sort(indexes2, comparator1);
		// z2
		AIComparator comparator2 = new AIComparator(vfals, AIComparator.desc);		
		Arrays.sort(indexes2, comparator2);

		double V = 0;
		double last = 0;
		for (int i=0; i<front.size(); i++) {
			double z1 = fits[indexes2[i]];				
			V += (z1 - last)*vfals[indexes2[i]];
			last = z1;
		}

		return V;
	}
	
	public double elitevolume() {
		P.addAll(elite);				
		fillFront();
		clearFront();
		return hypervolume();
	}

}
