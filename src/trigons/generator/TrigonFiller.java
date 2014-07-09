package trigons.generator;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import trigons.AppTrigons;
import trigons.puzzle.TSide;
import trigons.puzzle.TriPuzzle;
import trigons.puzzle.Trigon;
import trigons.puzzle.TrigonBorder;
import trigons.puzzle.TrigonPuzzle;
import trigons.solver.TrigonSolverOrFiller;
import trigons.solver.TrigonValueSet;
import trigons.solver.TrigonValueSet.TrigonValuePermutation;
import vordeka.util.ErrUtil;
import vordeka.util.StringUtil;
import vordeka.util.collection.Shuffler;
import vordeka.util.collection.ShufflingCollection;
import vordeka.util.exception.ImpossibleException;
import vordeka.util.list.ArrayWrapper;
import vordeka.util.list.SingleElementList;

/**
 * While the generator generates puzzles from scratch, the filler creates a puzzle
 * from a blank (or partially filled) layout.
 * @author Vordeka
 *
 */
public class TrigonFiller implements TrigonSolverOrFiller {
	
	private TrigonPuzzle puzzle;
	private HashMap<Trigon, TriSlot> trigons = new HashMap<Trigon, TriSlot>();
//	private HashMap<TrigonBorder, BorderPossibilitySet> borders = new HashMap<TrigonBorder, BorderPossibilitySet>();
	private Queue<Object> queue =
//			new ArrayDeque<Object>();
			new PriorityQueue<Object>(120, new Comparator<Object>(){
		@Override
		public int compare(Object o1, Object o2) {
			int p1 = priorityOf(o1);
			int p2 = priorityOf(o2);
			return p1-p2;
		}
		private int priorityOf(Object obj) {
			if(obj instanceof Trigon){
				return 0;
			} else if(obj instanceof TrigonBorder){
				return 1;
			} else if(obj instanceof TriSolution){
				return 2;
			} else if(obj instanceof TrigonValueGroup){
				return 3;
			} else {
				return -1;
			}
		}}); //*/
	private Set<Object> queueSet = new HashSet<Object>();
	private TriSolution[] triSolutions;
	private TrigonValueSet[][] triValueSets;
	private TrigonValueGroup[] valueGroups;
	/**
	 * Is true if the number of trigons equals the number of solutions.
	 */
	private boolean allSolutionsMustBeUsed;
	/**
	 * Only used if allSolutionsMustBeUsed is false.
	 */
	private int[] numTrigonsWithValue;
	/**
	 * This is set to false if at time any trigon has zero possibilities remaining. 
	 */
	private boolean isSolveable;
	private List<String> messages;
	
	
	/**
	 * When the solution limit is met, the solver terminates and stops trying to find
	 * more solutions. If the limit is set to zero or less, then it will not terminate on
	 * its own until it finds all possible solutions.
	 */
	public int solutionLimit = 1;
	public final int arbitrateDepth;
	public int arbitrationCount = 0;
	public int subSolversCount = 0;
	
	/**	<code>[0-3][] = 0:trigon, 1:border, 2:solution, 3:groups, 4:other<br />
		[][0-3] = 0:times processed, 1:times progress made, 2:objects enqueued, 3:perms eliminated</code>
	*/
	public static final int PROC_NONE = 0, PROC_TRIGON = 1, PROC_BORDER = 2, PROC_SOLUTION = 3, PROC_GROUP = 4;
	public static final int PSTAT_RUNS = 0, PSTAT_PROGRESS = 1, PSTAT_ENQUEUED = 2, PSTAT_ENQUEUE_ATTEMPTS = 3, PSTAT_ELIMINATED = 4;
	public static final String[] PROC_NAMES = "Other,Trigons,Borders,Solutions,Groups".split(",");
	public int[][] processCounts = new int[5][5];
	public long[] processTimes = new long[5];
	public int currentProcessType = PROC_NONE;
	
	private Random rand;

	public TrigonFiller(TrigonPuzzle puzzle, Random rand) {
		this.puzzle = new TriPuzzle(puzzle);
		this.rand = rand;
		triValueSets = TrigonValueSet.generateSolutionSets(puzzle.getMaxBorderValue());
		valueGroups = new TrigonValueGroup[triValueSets.length];
		for(int i=0; i<triValueSets.length; ++i){
			valueGroups[i] = new TrigonValueGroup(i);
		}
		arbitrateDepth = 0;
	}
	
	public TrigonFiller(TrigonFiller parent) {
		this.puzzle = parent.puzzle;
		this.rand = parent.rand;
		this.triValueSets = parent.triValueSets;
		this.allSolutionsMustBeUsed = parent.allSolutionsMustBeUsed;
		this.solutionLimit = parent.solutionLimit;
		this.arbitrateDepth = parent.arbitrateDepth+1;
		this.valueGroups = parent.valueGroups;
		this.currentIndent = parent.currentIndent;
	}
	
	public String dumpProcessCounts(){
		StringBuilder b = new StringBuilder();
		String[] rowNames = PROC_NAMES;
		int[] rowIndices = {1,2,3,4,0};
		for(int j=0; j<4; ++j){
			int i = rowIndices[j];
			b.append(rowNames[i]).append(": ").append(processCounts[i][PSTAT_RUNS]).append(" runs, ")
				.append(processCounts[i][PSTAT_PROGRESS]).append(" advances, ")
				.append(processCounts[i][PSTAT_ENQUEUED]).append('/')
				.append(processCounts[i][PSTAT_ENQUEUE_ATTEMPTS]).append(" objs enqueued, ")
				.append(processCounts[i][PSTAT_ELIMINATED]).append(" perms eliminated, ")
				.append(processTimes[i]).append("ms\n");
		}
		
		b.setLength(b.length()-1);
		return b.toString();
	}
	
	private List<TrigonPuzzle> solveDescendent(
			TrigonFiller parent, Trigon tri, TrigonValuePermutation arbPerm) {
		isSolveable = true;
		this.triSolutions = new TriSolution[parent.triSolutions.length];
		for(int i=0; i<triSolutions.length; ++i){
			this.triSolutions[i] = new TriSolution(parent.triSolutions[i].valueSet);
		}
		for(TriSlot set : parent.trigons.values()){
			this.trigons.put(set.tri, new TriSlot(set));
		}		
		TriSlot arbitrated = this.trigons.get(tri);
		Iterator<TrigonValuePermutation> itr = arbitrated.iterator();
		while(itr.hasNext()){
			TrigonValuePermutation perm = itr.next();
			if(perm != arbPerm){
				itr.remove();
			}
		}
		return solveRoutine();
	}
	
	private List<TrigonPuzzle> solveDescendent(
			TrigonFiller parent, Trigon tri, int triValue) {
		isSolveable = true;
		this.triSolutions = new TriSolution[parent.triSolutions.length];
		for(int i=0; i<triSolutions.length; ++i){
			this.triSolutions[i] = new TriSolution(parent.triSolutions[i].valueSet);
		}
		for(TriSlot set : parent.trigons.values()){
			this.trigons.put(set.tri, new TriSlot(set));
		}		
		TriSlot arbitrated = this.trigons.get(tri);
		Iterator<TrigonValuePermutation> itr = arbitrated.iterator();
		while(itr.hasNext()){
			TrigonValuePermutation perm = itr.next();
			if(perm.getTotal() != triValue){
				itr.remove();
			}
		}
//		enqueue(tri);
//		enqueue(tri.getLeftBorder());
//		enqueue(tri.getRightBorder());
//		enqueue(tri.getVerticalBorder());
		
		return solveRoutine();
	}
	
	public List<TrigonPuzzle> solve(){
		queue.clear();
		queueSet.clear();
		isSolveable = true;
		buildPossibilityList();
		eliminateBySetValues();
		eliminateForCorners();
		dumpState("Initial State");
		if(!this.isSolveable){
			return Collections.emptyList();
		}
		ErrUtil.debugMsg("Initial Perm Count: " + this.getRemainingTrigonPermCount());
		queue.addAll(puzzle.getTrigons());
		queue.addAll(puzzle.getBorders());
		queue.addAll(ArrayWrapper.wrap(triSolutions));
		queue.addAll(ArrayWrapper.wrap(valueGroups));
		queueSet.addAll(puzzle.getTrigons());
		queueSet.addAll(puzzle.getBorders());
		queueSet.addAll(ArrayWrapper.wrap(triSolutions));
		queueSet.addAll(ArrayWrapper.wrap(valueGroups));
		List<TrigonPuzzle> solutions =  solveRoutine();
		try {
			AppTrigons.flushDebugWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ErrUtil.debugMsg("Final Perm Count: " + this.getRemainingTrigonPermCount());
		return solutions;
	}	

	

	private List<TrigonPuzzle> solveRoutine() {
		while(this.isSolveable && !queue.isEmpty()){
			processNext();
		}
		if(!this.isSolveable){
//			ErrUtil.debugMsg("Result is unsolvable");
			queue.clear();
			queueSet.clear();
			return Collections.emptyList();
		}
		if(this.arbitrateDepth == 0){
			dumpState("After First Layer of Processing");
		}
		//	First check the solutions for 0 and 18 (or min and max, actually) and see if they 
		//	are placed. If not, then they are arbitrated first.
		TriSolution sol = triSolutions[triValueSets[0][0].id];
		if(sol.possTri.size() > 1){
			return arbitrate(sol);
		}
		sol = triSolutions[triValueSets[triValueSets.length-1][0].id];
		if(sol.possTri.size() > 1){
			return arbitrate(sol);
		}
		
		boolean solved = true;
		//	Search for any unsolved trigons, and if one is found, then arbitrate.
		
		TriSlot favorite = null;
		for(TriSlot set : ShufflingCollection.wrap(trigons.values(), rand)){
			if(set.possibilities.isEmpty()){
				throw new ImpossibleException("This should have been caught already");
			} else if(!set.isSolved()){
				if(favorite == null || favorite.hasMorePermsThan(set))
					favorite = set;
//				return arbitrate(set);
//				solved = false;
//				if(set.possibilities.size() > 1){
//					return arbitrate(set);
//				}
			}
		}
		if(favorite != null)
			return arbitrate(favorite);
		
		if(!solved){
			ErrUtil.debugMsg("Failed to find solution and cannot arbitrate any further");
			queue.clear();
			queueSet.clear();
			return Collections.emptyList();
		}
		dumpState("Final Solution");
		TriPuzzle puzz = new TriPuzzle(this.puzzle);
		List<TriSlot> sortedTrigons = new ArrayList<TriSlot>(trigons.values());
		Collections.sort(sortedTrigons, new Comparator<TriSlot>(){
			@Override
			public int compare(TriSlot t1, TriSlot t2) {
				Trigon o1 = t1.tri;
				Trigon o2 = t2.tri;
				if(o1.getY() == o2.getY())
					return o1.getX() - o2.getX();
				return o1.getY() - o2.getY();
			}});
		for(TriSlot set : sortedTrigons){
			TrigonValuePermutation perm = set.iterator().next();
			Trigon tri = puzz.getTrigon(set.tri.getX(), set.tri.getY());
//			if(tri.getLeftBorder().getValue() != perm.left && tri.getLeftBorder().getValue() >= 0)
//				ErrUtil.warningMsg("left " + tri + " " + perm);
//			if(tri.getRightBorder().getValue() != perm.right && tri.getRightBorder().getValue() >= 0)
//				ErrUtil.warningMsg("right " + tri + " " + perm);
//			if(tri.getVerticalBorder().getValue() != perm.vertical && tri.getVerticalBorder().getValue() >= 0)
//				ErrUtil.warningMsg("vertical " + tri + " " + perm);

			tri.getLeftBorder().setValue(perm.left);
			tri.getRightBorder().setValue(perm.right);
			tri.getVerticalBorder().setValue(perm.vertical);
			tri.setValue(perm.left + perm.right + perm.vertical);
//			ErrUtil.debugMsg(set.tri + " to " + tri);
		}
		return new SingleElementList<TrigonPuzzle>(puzz); 
	}
	
	/*private void processTrigonGroups() {
		//	This is a rather complex calculaton that searches for groups of trigons 
		//	that share an exclusive subset of the remaining possibilities.
		//	i.e. if there are only two trigons with the value 11 that could have possibilities 245 and 335,
		//	then those two trigons can't have any other possibilities.
		//	Examples - All these trigons have the value 11
		//	Example 1 - B & C form a subgroup because only they can have 245 and 335
		//	A - 056 146 155
		//	B - 056 146 155 245 355
		//	C - 146 155 245 355
		//	D - 056 146 155 
		//	E - 056 146 155
		//	Example 2 - B & C form a subgroup because 245 and 335 are the only values they can have
		//	A - 056 146 155 245 355
		//	B - 245 355
		//	C - 245 355
		//	D - 056 146 155 245 355
		//	E - 056 146 155 245 355
		
		//	Note: A superior way to evaluate this is simply to evaluate all possible 
		//	matchups of trigons to value sets, and keep track of which permutations 
		//	are actually possible on each trigon.
		for(int i=0; i<triValueSets.length; ++i){
			processTrigonGroups(i);
		}
		
		//	Before implementing this routine: Total Arbitrations: 5:44   Perms in First Layer: 535
		//	After: Total Arbitrations: 0:0   Perms in First Layer: 84
		//	Well damn! No errors. No bugs. Worked first time. Beautiful.
		
		//	[Update] The filler starts with every trigon having all permutations. That means that 
		 * the value groups are invalid for this processing method, and unfortunately, I don't see
		 * a way to run this process that isn't a brute-forcing of all possible solutions (which,
		 * upon reflection, is exactly what it does, I suppose).
		 * 
		 *  Arbitration is a better choice.
		 * 
		 * In any case, processing trigon groups is pointless since the only time I expect values
		 * to be assigned is when they are assigned along with permutations. (Unless I decide against
		 * doing that, and assign values instead of permutations).
	}*/

	private void processNext() {
		long start = System.currentTimeMillis();
		Object obj = queue.poll();
//		Object obj = getNextInQueue();
		boolean madeProgress;
//		log("Processing " + obj, 1);
		if(obj == null){
			throw new NullPointerException("Nulls not allowed in queue. How did it get in there?");
		} else if(obj instanceof Trigon){
			this.currentProcessType = PROC_TRIGON;
			madeProgress = processTrigon((Trigon)obj);
		} else if(obj instanceof TrigonBorder){
			this.currentProcessType = PROC_BORDER;
			madeProgress = processBorder((TrigonBorder)obj);
		} else if(obj instanceof TriSolution){
			this.currentProcessType = PROC_SOLUTION;
			madeProgress = processSolution((TriSolution)obj);
		} else if(obj instanceof TrigonValueGroup){
			this.currentProcessType = PROC_GROUP;
			madeProgress = processTrigonGroups(((TrigonValueGroup)obj).value);
		} else {
			throw new RuntimeException("Unexpected object in queue: " + obj.getClass().getName() + " \"" + obj + "\"");
		}
		queueSet.remove(obj);
//		log("Finished Processing " + obj, -1);
//		logIndent(-1);
		this.processTimes[currentProcessType] += System.currentTimeMillis() - start;
		++this.processCounts[currentProcessType][PSTAT_RUNS];
		if(madeProgress)
			++this.processCounts[currentProcessType][PSTAT_PROGRESS];
		this.currentProcessType = PROC_NONE;
	}
	
	private boolean processEntireTrigonGroup(){
		int solsUsed = 0;
		TriSolution[] sols = new TriSolution[triSolutions.length];
		//	This is a list of the permutations that need to be proven
		final long maxEstimate = 20000;	//	Note: 8^8 is the normal maximum possible. But that worst-case scenario almost never happens.
		//	Hang on...this is still only grouped by value...it might not be too bad...
		long timeEstimate = 1;
		@SuppressWarnings("unchecked")
		List<TriSlot>[] slotPerms = new List[sols.length];
		for(int i=0; i<triSolutions.length; ++i){
			TriSolution sol =  triSolutions[i];
			int size = sol.possTri.size();
			if(size > 1){
				sols[solsUsed] = sol;
				timeEstimate *= size;
				slotPerms[solsUsed] = new ArrayList<TriSlot>(sol.possTri);
				++solsUsed;
			}
		}
		
		if(timeEstimate > maxEstimate || solsUsed == 0) {
//			ErrUtil.debugMsg("Group " + triValue + " gets estimate of " + timeEstimate + (solsUsed == 0 ? " (Unnecessary)" : " (Rejected)"));
			return false;
		}
//		ErrUtil.debugMsg("Group " + triValue + " gets estimate of " + timeEstimate + " (Accepted) (used=" + solsUsed + ")");
		if(solsUsed < sols.length){
			sols = Arrays.copyOf(sols, solsUsed);
			slotPerms = Arrays.copyOf(slotPerms, solsUsed);
		}
		long start = System.currentTimeMillis();
		TriSlot[] picks = new TriSlot[sols.length];
		processTrigonGroupMatchup(0, sols, picks, slotPerms);
		boolean changesMade = false;
		int numChanges = 0;
		for(int i=0; i<sols.length; ++i){
			if(!slotPerms[i].isEmpty()){
				for(TriSlot tri : slotPerms[i]){
					tri.removeAllPerms(sols[i]);
//					enqueue(tri.tri);
//					enqueue(tri.tri.getLeftBorder());
//					enqueue(tri.tri.getRightBorder());
//					enqueue(tri.tri.getVerticalBorder());
//					enqueue(sols[i]);
					++numChanges;
					changesMade = true;
				}
			}
		}
		long actualTime = System.currentTimeMillis() - start;
		if(actualTime > 1)
			ErrUtil.debugMsg("Entire Group got estimate of " + timeEstimate + "    Actual time: " + actualTime + "ms    Eliminations: " + numChanges);
		return changesMade;
	}

	private boolean processTrigonGroups(int triValue) {
		int solsUsed = 0;
		TrigonValueSet[] sets = triValueSets[triValue];
		TriSolution[] sols = new TriSolution[sets.length];
		//	This is a list of the permutations that need to be proven
		final long maxEstimate = 20000;	//	Note: 8^8 is the normal maximum possible. But that worst-case scenario almost never happens.
		//	Hang on...this is still only grouped by value...it might not be too bad...
		long timeEstimate = 1;
		@SuppressWarnings("unchecked")
		List<TriSlot>[] slotPerms = new List[sols.length];
		for(int i=0; i<sets.length; ++i){
			TriSolution sol =  triSolutions[sets[i].id];
			int size = sol.possTri.size();
			if(size > 1){
				sols[solsUsed] = sol;
				timeEstimate *= size;
				slotPerms[solsUsed] = new ArrayList<TriSlot>(sol.possTri);
				++solsUsed;
			}
		}
		
		if(timeEstimate > maxEstimate || solsUsed == 0) {
//			ErrUtil.debugMsg("Group " + triValue + " gets estimate of " + timeEstimate + (solsUsed == 0 ? " (Unnecessary)" : " (Rejected)"));
			return false;
		}
//		ErrUtil.debugMsg("Group " + triValue + " gets estimate of " + timeEstimate + " (Accepted) (used=" + solsUsed + ")");
		if(solsUsed < sols.length){
			sols = Arrays.copyOf(sols, solsUsed);
			slotPerms = Arrays.copyOf(slotPerms, solsUsed);
		}
		long start = System.currentTimeMillis();
		TriSlot[] picks = new TriSlot[sols.length];
		processTrigonGroupMatchup(0, sols, picks, slotPerms);
		boolean changesMade = false;
		int numChanges = 0;
		for(int i=0; i<sols.length; ++i){
			if(!slotPerms[i].isEmpty()){
				for(TriSlot tri : slotPerms[i]){
					tri.removeAllPerms(sols[i]);
//					enqueue(tri.tri);
//					enqueue(tri.tri.getLeftBorder());
//					enqueue(tri.tri.getRightBorder());
//					enqueue(tri.tri.getVerticalBorder());
//					enqueue(sols[i]);
					++numChanges;
					changesMade = true;
				}
			}
		}
		long actualTime = System.currentTimeMillis() - start;
		if(actualTime > 1)
			ErrUtil.debugMsg("Group " + triValue + " got estimate of " + timeEstimate + "    Actual time: " + actualTime + "ms    Eliminations: " + numChanges);
		return changesMade;
	}

	private void processTrigonGroupMatchup(int index, TriSolution[] sols,
			TriSlot[] picks, 
			List<TriSlot>[] perms) {
		if(index == sols.length){
			for(int i=0; i<sols.length; ++i){
				perms[i].remove(picks[i]);
			}
		} else {
			TriSolution sol = sols[index];
outerLoop:
			for(TriSlot tri : sol){
				for(int i=0; i<index; ++i){
					if(picks[i] == tri){
						continue outerLoop;
					}
				}
				picks[index] = tri;
				processTrigonGroupMatchup(index + 1, sols, picks, perms);
			}
		}
		
	}

	public TrigonPuzzle getRefinedPuzzle() {
		TriPuzzle puzz = new TriPuzzle(this.puzzle);
//		List<TrigonPossibilitySet> sortedTrigons = new ArrayList<TrigonPossibilitySet>(trigons.values());
//		Collections.sort(sortedTrigons, new Comparator<TrigonPossibilitySet>(){
//			@Override
//			public int compare(TrigonPossibilitySet t1, TrigonPossibilitySet t2) {
//				Trigon o1 = t1.tri;
//				Trigon o2 = t2.tri;
//				if(o1.getY() == o2.getY())
//					return o1.getX() - o2.getX();
//				return o1.getY() - o2.getY();
//			}});
//		boolean[] possibleLeft = new boolean[this.puzzle.getMaxBorderValue()+1];
//		boolean[] possibleRight = new boolean[this.puzzle.getMaxBorderValue()+1];
		int possibleVal;
		boolean hasMultipleVals;
		for(Trigon tri : this.puzzle.getTrigons()){
			int value = tri.getLeftBorder().getValue() + tri.getRightBorder().getValue() + tri.getVerticalBorder().getValue();
			puzz.getTrigon(tri.getX(), tri.getY()).setValue(value);
		}
		for(TrigonBorder border : this.puzzle.getBorders()){
			hasMultipleVals = false;
			possibleVal = -1;
			TSide side = border.isVertical() ? TSide.VERT : TSide.RIGHT;
			Trigon tri = border.getFirstTrigon();
			if(tri == null){
				tri = border.getSecondTrigon();
				side = side.getOpposite();
			}
			
			for(TrigonValuePermutation perm : this.trigons.get(tri)){
				int val = perm.getValue(side);
				if(possibleVal == -1){
					possibleVal = val;
				} else if(possibleVal != val){
					hasMultipleVals = true;
					break;
				}
			}
			if(!hasMultipleVals && possibleVal >= 0){
				puzz.getBorder(tri.getX(), tri.getY(), side).setValue(possibleVal);
			}
		}
//		for(TrigonPossibilitySet set : sortedTrigons){
//			if(set.possibilities.size() == 1){
//				TrigonValuePermutation perm = set.possibilities.get(0);
//				Trigon tri = puzz.getTrigon(set.tri.getX(), set.tri.getY());
//	//			if(tri.getLeftBorder().getValue() != perm.left && tri.getLeftBorder().getValue() >= 0)
//	//				ErrUtil.warningMsg("left " + tri + " " + perm);
//	//			if(tri.getRightBorder().getValue() != perm.right && tri.getRightBorder().getValue() >= 0)
//	//				ErrUtil.warningMsg("right " + tri + " " + perm);
//	//			if(tri.getVerticalBorder().getValue() != perm.vertical && tri.getVerticalBorder().getValue() >= 0)
//	//				ErrUtil.warningMsg("vertical " + tri + " " + perm);
//	
//				tri.getLeftBorder().setValue(perm.left);
//				tri.getRightBorder().setValue(perm.right);
//				tri.getVerticalBorder().setValue(perm.vertical);
//	//			ErrUtil.debugMsg(set.tri + " to " + tri);
//			}
//		}
		return puzz;
	}

	/**
	 *	When two of a triangles borders are not shared with other triangles,
	 *	then the value of both of the unshared borders must be the same.
	 *	I'm not currently using any arrangements where that's possible,
	 *	but I'm implementing the rule anyway.
	 *	<p>
	 *	I don't know how official the rule is, so it may later be dependent
	 *	on the puzzle. 
	 */
	private void eliminateForCorners() {
		if(!isSolveable) return;
		for(TriSlot set : trigons.values()){
			Trigon tri = set.tri;
			boolean leftUnshared = !tri.getLeftBorder().isSharedBorder();
			boolean rightUnshared = !tri.getRightBorder().isSharedBorder();
			boolean vertUnshared = !tri.getVerticalBorder().isSharedBorder();
			if(leftUnshared && rightUnshared && vertUnshared){
				//	Only possibilities with all three values the same should be allowed, I guess.
				ErrUtil.warningMsg("Encountered a completely isolated trigon at (" + tri.getX() + "," + tri.getY() + ")");
				
				Iterator<TrigonValuePermutation> itr = set.iterator();
				while(itr.hasNext()){
					TrigonValuePermutation perm = itr.next();
					if(perm.left != perm.right || perm.right != perm.vertical){
						itr.remove();
					}
				}
			} else if(leftUnshared && rightUnshared){
				Iterator<TrigonValuePermutation> itr = set.iterator();
				while(itr.hasNext()){
					TrigonValuePermutation perm = itr.next();
					if(perm.left != perm.right){
						itr.remove();
					}
				}
			} else if(leftUnshared && vertUnshared){
				Iterator<TrigonValuePermutation> itr = set.iterator();
				while(itr.hasNext()){
					TrigonValuePermutation perm = itr.next();
					if(perm.left != perm.vertical){
						itr.remove();
					}
				}
			} else if(rightUnshared && vertUnshared){
				Iterator<TrigonValuePermutation> itr = set.iterator();
				while(itr.hasNext()){
					TrigonValuePermutation perm = itr.next();
					if(perm.right != perm.vertical){
						itr.remove();
					}
				}
			}
			
		}
	}

	/**
	 * If any borders have been given values already, those are taken into
	 * account and any possibilities that don't fit them are removed.
	 */
	private void eliminateBySetValues() {
		if(!isSolveable) return;
		for(TriSlot set : trigons.values()){
			Trigon tri = set.tri;
			int leftValue = tri.getLeftBorder().getValue();
			int rightValue = tri.getRightBorder().getValue();
			int vertValue = tri.getVerticalBorder().getValue();
			if(leftValue >= 0 || rightValue >= 0 || vertValue >= 0){
				Iterator<TrigonValuePermutation> itr = set.iterator();
				while(itr.hasNext()){
					TrigonValuePermutation perm = itr.next();
					if(!((leftValue < 0 || perm.left == leftValue) && 
							(rightValue < 0 || perm.right == rightValue) && 
							(vertValue < 0 || perm.vertical == vertValue))){
						itr.remove();
						
					}
				}
			}
		}
	}

	private List<TrigonPuzzle> arbitrate(TriSolution sol) {
		log("Arbitrating on " + sol.valueSet, 1);
//		arbitrateStarted(this, set);
//		ErrUtil.debugMsg("Arbitrate called");
		this.arbitrationCount = 1;
		List<TrigonPuzzle> solutions = new ArrayList<TrigonPuzzle>();
//		boolean[] hasAbritrated = new boolean[triValueSets.length];
		for(TriSlot slot : ShufflingCollection.wrap(sol.possTri, rand)){
//			int total = sol.getTotal();
//			if(hasAbritrated[total])
//				continue;
//			hasAbritrated[total] = true;
			boolean[] hasPerms = slot.possibilities.get(sol);
			for(int i : Shuffler.shuffledIndices(hasPerms.length, rand)){
				if(hasPerms[i]){
					TrigonValuePermutation perm = sol.valueSet.permutations[i];
					log("Arbitrating " + slot.tri + " = " + perm, 1);
					TrigonFiller descendent = new TrigonFiller(this);
					solutions.addAll(descendent.solveDescendent(this, slot.tri, perm));
					log("Finished Arbitrating " + slot.tri + " = " + perm, -1);
					this.arbitrationCount += descendent.arbitrationCount;
					this.subSolversCount += descendent.subSolversCount + 1;
					if(solutionLimit > 0 && solutions.size() >= solutionLimit){
						break;
					}
				}
			}
			if(solutionLimit > 0 && solutions.size() >= solutionLimit){
				break;
			}
		}
		log("Arbitration finished " + sol.valueSet, -1);
//		arbitrateEnded(this, solutions);
//		ErrUtil.debugMsg("Arbitrate returned");
		return solutions;
	}
	
	private List<TrigonPuzzle> arbitrateOld(TriSlot set) {
		log("Arbitrating on " + set.tri, 1);
//		arbitrateStarted(this, set);
//		ErrUtil.debugMsg("Arbitrate called");
		this.arbitrationCount = 1;
		
		List<TrigonPuzzle> solutions = new ArrayList<TrigonPuzzle>();
//		boolean[] hasAbritrated = new boolean[triValueSets.length];
		for(TriSolution sol : ShufflingCollection.wrap(set.possibilities.keySet(), rand)){
//			int total = sol.getTotal();
//			if(hasAbritrated[total])
//				continue;
//			hasAbritrated[total] = true;
			boolean[] hasPerms = set.possibilities.get(sol);
			for(int i : Shuffler.shuffledIndices(hasPerms.length, rand)){
				if(hasPerms[i]){
					TrigonValuePermutation perm = sol.valueSet.permutations[i];
					log("Arbitrating " + set.tri + " = " + perm, 1);
					TrigonFiller descendent = new TrigonFiller(this);
					solutions.addAll(descendent.solveDescendent(this, set.tri, perm));
					log("Finished Arbitrating " + set.tri + " = " + perm, -1);
					this.arbitrationCount += descendent.arbitrationCount;
					this.subSolversCount += descendent.subSolversCount + 1;
					if(solutionLimit > 0 && solutions.size() >= solutionLimit){
						break;
					}
				}
			}
			if(solutionLimit > 0 && solutions.size() >= solutionLimit){
				break;
			}
		}
		log("Arbitration finished " + set.tri, -1);
//		arbitrateEnded(this, solutions);
//		ErrUtil.debugMsg("Arbitrate returned");
		return solutions;
	}
	
	private List<TrigonPuzzle> arbitrate(TriSlot set) {
		log("Arbitrating on " + set.tri, 1);
//		arbitrateStarted(this, set);
//		ErrUtil.debugMsg("Arbitrate called");
		this.arbitrationCount = 1;
		
		TriSolution sol = ShufflingCollection.wrap(set.possibilities.keySet(), rand).iterator().next();
		
		List<TrigonPuzzle> solutions = Collections.emptyList();
//		boolean[] hasAbritrated = new boolean[triValueSets.length];
		
//			int total = sol.getTotal();
//			if(hasAbritrated[total])
//				continue;
//			hasAbritrated[total] = true;
		boolean[] hasPerms = set.possibilities.get(sol);
		for(int i : Shuffler.shuffledIndices(hasPerms.length, rand)){
			if(hasPerms[i]){
				TrigonValuePermutation perm = sol.valueSet.permutations[i];
				log("Arbitrating " + set.tri + " = " + perm, 1);
				TrigonFiller descendent = new TrigonFiller(this);
				solutions = descendent.solveDescendent(this, set.tri, perm);
				log("Finished Arbitrating " + set.tri + " = " + perm, -1);
				this.arbitrationCount += descendent.arbitrationCount;
				this.subSolversCount += descendent.subSolversCount + 1;
				break;
			}
		}
		
		log("Arbitration finished " + set.tri, -1);
//		arbitrateEnded(this, solutions);
//		ErrUtil.debugMsg("Arbitrate returned");
		return solutions;
	}

	private Object getNextInQueue() {
		Object obj = queue.poll();
		queueSet.remove(obj);
		return obj;
	}

	private void enqueue(TrigonValueSet set){
		enqueue(triSolutions[set.id]);
	}
	private void enqueue(TriSolution obj) {
		++processCounts[currentProcessType][PSTAT_ENQUEUE_ATTEMPTS];
		if(queueSet.add(obj)){
			queue.add(obj);
			++processCounts[currentProcessType][PSTAT_ENQUEUED];
			enqueue(valueGroups[obj.getTotal()]);
		}
	}
	private void enqueue(TrigonBorder obj) {
		++processCounts[currentProcessType][PSTAT_ENQUEUE_ATTEMPTS];
		if(queueSet.add(obj)){
			queue.add(obj);
			++processCounts[currentProcessType][PSTAT_ENQUEUED];
//			log("Border " + obj + " enqueued");
		}
	}
	private void enqueue(TriSlot slot){
		enqueue(slot.tri);
	}
	private void enqueue(Trigon obj) {
		++processCounts[currentProcessType][PSTAT_ENQUEUE_ATTEMPTS];
		if(queueSet.add(obj)){
			queue.add(obj);
			++processCounts[currentProcessType][PSTAT_ENQUEUED];
			enqueue(obj.getLeftBorder());
			enqueue(obj.getRightBorder());
			enqueue(obj.getVerticalBorder());
		}
	}

	private void enqueue(TrigonValueGroup obj) {
//		++processCounts[currentProcessType][PSTAT_ENQUEUE_ATTEMPTS];
//		if(queueSet.add(obj)){
//			queue.add(obj);
//			++processCounts[currentProcessType][PSTAT_ENQUEUED];
//		}
	}

	private boolean processSolution(TriSolution sol) {
		if(sol.possTri.isEmpty()){
			if(allSolutionsMustBeUsed){
				addFailureMessage("No possibile trigons left for solution " + sol);
				this.isSolveable = false;
			} else {
				//	Count the number of solutions that still have possible trigons left.
				//	If the number of solutions is less than the number of trigons with that value,
				//	then the puzzle is no longer solveable.
				int myTriValue = sol.valueSet.getTotal();
				int solCount = 0;
				for(TrigonValueSet set : this.triValueSets[myTriValue]){
					TriSolution ts = triSolutions[set.id];
					if(!ts.possTri.isEmpty())
						++solCount;
				}
				if(numTrigonsWithValue[myTriValue] > solCount){
					addFailureMessage("Too few solutions left for trigons with value " + myTriValue + 
							" (" + numTrigonsWithValue[myTriValue] + " trigons, " + solCount + " solutions)");
					this.isSolveable = false;
				}
			}
		} else if(sol.possTri.size() == 1){
			TriSlot tri = sol.iterator().next();
			Iterator<TrigonValuePermutation> itr2 = tri.iterator();
			boolean madeChanges = false;
			while(itr2.hasNext()){
				TrigonValuePermutation perm = itr2.next();
				if(perm.set != sol.valueSet){
					madeChanges = true;
					itr2.remove();
				}
			}
//			if(madeChanges){
//				enqueue(tri.tri);
//				enqueue(tri.tri.getLeftBorder());
//				enqueue(tri.tri.getRightBorder());
//				enqueue(tri.tri.getVerticalBorder());
//			}
			return madeChanges;
		}
		return false;
	}

	private boolean processBorder(TrigonBorder border) {
		boolean progressMade = false;
		//	Check each possible solution from both trigons to see if they are compatible.
		//	i.e. If one side can be 2, but the other can't be, then we can eliminate those 
		//	possibilities from the former.
		TriSlot first = trigons.get(border.getFirstTrigon());
		TriSlot second = trigons.get(border.getSecondTrigon());
		if(first == null || second == null) return false;
		
		boolean[] possibleOnFirst = new boolean[7];
		boolean[] possibleOnSecond = new boolean[7];
		Arrays.fill(possibleOnFirst, false);
		Arrays.fill(possibleOnSecond, false);
		if(border.isVertical()){
			for(TrigonValuePermutation perm : first){
				possibleOnFirst[perm.vertical] = true;
			}
			for(TrigonValuePermutation perm : second){
				possibleOnSecond[perm.vertical] = true;
			}
			Iterator<TrigonValuePermutation> itr = first.iterator();
			boolean madeChanges = false;
			while(itr.hasNext()){
				TrigonValuePermutation perm = itr.next();
				if(!possibleOnSecond[perm.vertical]){
					itr.remove();
					madeChanges = true;
//					enqueue(perm.set);
				}
			}
			if(madeChanges){
//				enqueue(first.tri);
//				enqueue(first.tri.getLeftBorder());
//				enqueue(first.tri.getRightBorder());
				progressMade = true;
			}
			itr = second.iterator();
			madeChanges = false;
			while(itr.hasNext()){
				TrigonValuePermutation perm = itr.next();
				if(!possibleOnFirst[perm.vertical]){
					itr.remove();
					madeChanges = true;
//					enqueue(perm.set);
				}
			}
			if(madeChanges){
//				enqueue(second.tri);
//				enqueue(second.tri.getLeftBorder());
//				enqueue(second.tri.getRightBorder());
				progressMade = true;
			}
		} else {
			for(TrigonValuePermutation perm : first){
				possibleOnFirst[perm.right] = true;
			}
			for(TrigonValuePermutation perm : second){
				possibleOnSecond[perm.left] = true;
			}
			Iterator<TrigonValuePermutation> itr = first.iterator();
			boolean madeChanges = false;
			while(itr.hasNext()){
				TrigonValuePermutation perm = itr.next();
				if(!possibleOnSecond[perm.right]){
					itr.remove();
					madeChanges = true;
//					enqueue(perm.set);
				}
			}
			if(madeChanges){
//				enqueue(first.tri);
//				enqueue(first.tri.getLeftBorder());
//				enqueue(first.tri.getVerticalBorder());
				progressMade = true;
			}
			itr = second.iterator();
			madeChanges = false;
			while(itr.hasNext()){
				TrigonValuePermutation perm = itr.next();
				if(!possibleOnFirst[perm.left]){
					itr.remove();
					madeChanges = true;
//					enqueue(perm.set);
				}
			}
			if(madeChanges){
//				enqueue(second.tri);
//				enqueue(second.tri.getVerticalBorder());
//				enqueue(second.tri.getRightBorder());
				progressMade = true;
			}
		}
		return progressMade;
	}


	private boolean processTrigon(Trigon tri) {
		TriSlot slot = trigons.get(tri);
		if(slot.possibilities.isEmpty()){
			this.addFailureMessage("Trigon {" + tri.getX() + "," + tri.getY() + ":" + tri.getValue() + "} cannot be solved");
			this.isSolveable = false;
			return false;
		} else if(slot.isSolved()){
			TriSolution sol = slot.possibilities.keySet().iterator().next();
			Iterator<TriSlot> itr = sol.iterator();
			while(itr.hasNext()){
				if(itr.next() != slot)
					itr.remove();
			}
		}
		//	I think that's about it, really.
		return false;
	}

	private void buildPossibilityList() {
		triSolutions = new TriSolution[triValueSets[triValueSets.length-1][0].id+1];
		for(int i=0; i<triValueSets.length; ++i){
			for(int j=0; j<triValueSets[i].length; ++j){
				TrigonValueSet set = triValueSets[i][j];
				triSolutions[set.id] = new TriSolution(set); 
			}
		}
		
		int[] numTrigonsWithvalue = new int[triValueSets.length];
		for(Trigon tri : puzzle.getTrigons()){
//			trigons.put(new GridPoint(tri.getX(), tri.getY()), new TrigonPossibilitySet(tri, triSolutions[tri.getValue()]));
			int triValue = tri.getValue();
			TriSlot triSet;
			if(triValue >= 0){
				++numTrigonsWithvalue[triValue];
				triSet = new TriSlot(tri, triValueSets[triValue]);
			} else {
				triSet = new TriSlot(tri, triSolutions);
			}
			
			trigons.put(tri, triSet);
			for(TriSolution set : triSet.possibilities.keySet()){
				set.add(triSet);
			}
		}
		for(int i=0; i<numTrigonsWithvalue.length; ++i){
			if(numTrigonsWithvalue[i] > triValueSets[i].length){
				addFailureMessage("More trigons with value " + i + " than solutions (" + numTrigonsWithvalue[i] + " vs " + triValueSets[i].length + ")");
				isSolveable = false;
			}
		}
		this.allSolutionsMustBeUsed = this.trigons.size() == triSolutions.length;
		if(!this.allSolutionsMustBeUsed){
			this.numTrigonsWithValue = numTrigonsWithvalue;
		}
	}

	/*public static List<TrigonPuzzle> findSolutions(TrigonPuzzle puzzle, int count){
		TrigonFiller solver = new TrigonFiller(puzzle);
		return solver.solve();
	}
	public static List<TrigonPuzzle> findAllSolutions(TrigonPuzzle puzzle, int count){
		TrigonFiller solver = new TrigonFiller(puzzle);
		return solver.solve();
	}*/
	
	class TriSlot implements Iterable<TrigonValuePermutation> {

		public final Trigon tri;
		public Map<TriSolution, boolean[]> possibilities = new HashMap<TriSolution, boolean[]>();
		
		public TriSlot(Trigon tri, TrigonValueSet[] trigonValueSets) {
			this.tri = tri;
			for(TrigonValueSet set : trigonValueSets){
				boolean[] hasPerms = new boolean[set.permutations.length];
				Arrays.fill(hasPerms, true);
				TriSolution sol = triSolutions[set.id];
				possibilities.put(sol, hasPerms);
				sol.add(this);
			}
		}

		public boolean hasMorePermsThan(TriSlot set) {
			Iterator<TrigonValuePermutation> itr1 = this.iterator();
			Iterator<TrigonValuePermutation> itr2 = set.iterator();
			
			while(itr1.hasNext()){
				if(!itr2.hasNext())
					return true;
				itr1.next();
				itr2.next();
			}
			return false;
		}

		public boolean isSolved() {
			Iterator<TrigonValuePermutation> itr = this.iterator();
			itr.next();
			return !itr.hasNext();
		}

		public TriSlot(TriSlot copyThis) {
			this.tri = copyThis.tri;
			for(Entry<TriSolution, boolean[]> entry : copyThis.possibilities.entrySet()){
				TriSolution sol = triSolutions[entry.getKey().valueSet.id];
				this.possibilities.put(sol, Arrays.copyOf(entry.getValue(), entry.getValue().length));
				sol.add(this);
			}
		}

		public TriSlot(Trigon tri, TriSolution[] triSolutions) {
			this.tri = tri;
			for(TriSolution sol : triSolutions){
				boolean[] hasPerms = new boolean[sol.valueSet.permutations.length];
				Arrays.fill(hasPerms, true);
				possibilities.put(sol, hasPerms);
				sol.add(this);
			}
		}

		public boolean remove(TrigonValuePermutation perm) {
			TriSolution sol = triSolutions[perm.set.id];
			boolean[] hasPerms = possibilities.get(sol);
			if(hasPerms == null || !hasPerms[perm.ordinal])
				return false;
//			log("Perm " + perm + " removed from " + tri, 1);
			enqueue(this);
//			logIndent(-1);
			++processCounts[currentProcessType][PSTAT_ELIMINATED];
			hasPerms[perm.ordinal] = false;
			for(int i=0; i<hasPerms.length; ++i){
				if(hasPerms[i]){
					return true;
				}
			}
			possibilities.remove(sol);
//			logIndent(1);
			sol.remove(this);
//			logIndent(-1);
			return true;
		}
		public int countPermsForSolution(TriSolution sol) {
			boolean[] hasPerms = possibilities.get(sol);
			if(hasPerms == null) return 0;
			int count = 0;
			for(int i=0; i<hasPerms.length; ++i){
				if(hasPerms[i]) ++count;
			}
			return count;
		}
		public boolean removeAllPerms(TriSolution sol){
			boolean[] hasPerms = possibilities.remove(sol);
			if(hasPerms != null){
//				log("Removing all perms from solution " + sol, 1);
				for(int i=0; i<hasPerms.length; ++i){
					if(hasPerms[i])
						++processCounts[currentProcessType][PSTAT_ELIMINATED];
				}
				enqueue(this);
				sol.remove(this);
//				logIndent(-1);
				return true;
			}
			return false;
		}

		@Override
		public Iterator<TrigonValuePermutation> iterator() {
			return new Iterator<TrigonValuePermutation>(){
				Iterator<Entry<TriSolution, boolean[]>> itr = possibilities.entrySet().iterator();
				Entry<TriSolution, boolean[]> currentEntry;
				int nextIndex; //	This is the index within the boolean array
				boolean nextIsReady = false;
				int lastIndex;
				
				{
					currentEntry = itr.next();
					nextIndex = -1;
					findNext();
				}
				
				private void findNext() {
					if(nextIsReady) return;
					boolean[] hasPerms = currentEntry.getValue();
					while(true){
						if(++nextIndex >= hasPerms.length){
							if(itr.hasNext()){
								nextIndex = 0;
								currentEntry = itr.next();
								hasPerms = currentEntry.getValue();
								lastIndex = -1;
							} else {
								nextIndex = -1;
								currentEntry = null;
								break;
							}
						}
						if(hasPerms[nextIndex])
							break;
					}
					nextIsReady = true;
				}
				
				@Override
				public boolean hasNext() {
					if(!nextIsReady)
						findNext();
					return nextIndex >= 0;
				}

				@Override
				public TrigonValuePermutation next() {
					if(currentEntry == null) throw new NoSuchElementException("Out of values");
					if(!nextIsReady)
						findNext();
					lastIndex = nextIndex;
					nextIsReady = false;
					return currentEntry.getKey().valueSet.permutations[nextIndex];
				}

				@Override
				public void remove() {
					if(lastIndex < 0) throw new NoSuchElementException("Must call next() before remove()");
					boolean[] hasPerms = currentEntry.getValue();
					if(!hasPerms[lastIndex])
						return;
//					log("Perm " + currentEntry.getKey().valueSet.permutations[lastIndex] + " removed from " + tri, 1);
					enqueue(TriSlot.this.tri);
//					logIndent(-1);
					++processCounts[currentProcessType][PSTAT_ELIMINATED];
					hasPerms[lastIndex] = false;
					lastIndex = -1;
					for(int i=0; i<hasPerms.length; ++i){
						if(hasPerms[i]){
							return;
						}
					}
					TriSolution sol = currentEntry.getKey();
					itr.remove();
//					logIndent(1);
					sol.remove(TriSlot.this);
//					logIndent(-1);
				}
			};
		}
		
	}
	
	class TriSolution implements Iterable<TriSlot> {
		public final TrigonValueSet valueSet;
		public Set<TriSlot> possTri = new HashSet<TriSlot>();
		
		public TriSolution(TrigonValueSet valueSet){
			this.valueSet = valueSet;
		}

		public void add(TriSlot triSet) {
			possTri.add(triSet);
		}

		public int getTotal() {
			return valueSet.getTotal();
		}

		public boolean remove(TriSlot tri) {
			if(possTri.remove(tri)){
				tri.removeAllPerms(this);
				enqueue(this);
				return true;
			}
			return false;
		}

		public String toString(){
			return valueSet.toString();
		}

		@Override
		public Iterator<TriSlot> iterator() {
			return new Iterator<TriSlot>(){
				Iterator<TriSlot> itr = possTri.iterator();
				TriSlot lastSlot;
				@Override
				public boolean hasNext() {
					return itr.hasNext();
				}

				@Override
				public TriSlot next() {
					lastSlot = itr.next();
					return lastSlot;
				}

				@Override
				public void remove() {
					itr.remove();
					lastSlot.removeAllPerms(TriSolution.this);
					lastSlot = null;
				}
				
			};
		}
	}
	
//	static class BorderPossibilitySet {
//
//		public TrigonBorder border;
//		public List<TrigonValuePermutation> possFirst = new ArrayList<TrigonValuePermutation>();
//		public List<TrigonValuePermutation> possSecond = new ArrayList<TrigonValuePermutation>();
//		
//		public BorderPossibilitySet(TrigonBorder tri,
//				TrigonPossibilitySet tri1,
//				TrigonPossibilitySet tri2) {
//			this.border = tri;
//			this.possFirst.addAll(tri1.possibilities);
//			this.possSecond.addAll(tri2.possibilities);
//		}
//		
//	}
	
	static class TrigonValueGroup {
		public final int value;

		public TrigonValueGroup(int value) {
			this.value = value;
		}
		
	}
	
	protected void addFailureMessage(String message){
		if(messages == null){
			messages = new ArrayList<String>();
		}
		messages.add("Fatal Error: " + message);
	}
	
	protected void addGeneralMessage(String message){
		if(messages == null){
			messages = new ArrayList<String>();
		}
		messages.add(message);
	}
	
	public List<String> getMessages(){
		if(messages == null)
			return Collections.emptyList();
		return Collections.unmodifiableList(messages);
	}
	
	public static String dumpSolver(TrigonFiller solver) {
		List<TriSlot> trigons = new ArrayList<TriSlot>(solver.trigons.values());
		StringBuilder b = new StringBuilder();
		Collections.sort(trigons, new Comparator<TriSlot>(){
			@Override
			public int compare(TriSlot t1, TriSlot t2) {
				Trigon o1 = t1.tri;
				Trigon o2 = t2.tri;
				if(o1.getY() == o2.getY())
					return o1.getX() - o2.getX();
				return o1.getY() - o2.getY();
			}});
		for(TriSlot set : trigons){
			Trigon tri = set.tri;
			b.append(tri.getX()).append(',').append(tri.getY()).append(':').append(tri.getValue())
				.append(' ').append(set.possibilities.size()).append("-{");
			if(!set.possibilities.isEmpty()){
				for(TrigonValuePermutation sol : set){
					b.append(sol.left).append(sol.right).append(sol.vertical);
					if(sol.set.getTotal() != tri.getValue()){
						b.append('!');
					}
					b.append(',');
				}
				b.setLength(b.length()-1);
			}
			b.append("}\n");
		}
		return b.toString();
	}
	
	private void dumpState(String string) {
//		try {
//			Writer w = AppTrigons.getDebugWriter();
//			w.append('\n');
//			w.append(string).append(":\n").append(dumpSolver(this));
//		} catch(IOException e){
//			ErrUtil.errMsg(e, "Writing to debug stream for arbitration start");
//		}
	}
	
	int currentIndent = 0;
	private void log(String message){
		log(message, 0);
	}
	private void log(String message, int changeIndent) {
		if(changeIndent < 0)
			currentIndent += changeIndent;
		String indent = StringUtil.repeat("    ", currentIndent);
		try {
			Writer w = AppTrigons.getDebugWriter();
			w.append(indent).append(message.replace("\n", "\n" + indent)).append("\n");
		} catch(IOException e){
			ErrUtil.errMsg(e, "Writing to debug stream for arbitration start");
		}
		if(changeIndent > 0)
			currentIndent += changeIndent;
	}
	private void logIndent(int changeIndent){
		currentIndent += changeIndent;
	}

	private void arbitrateStarted(TrigonFiller solver,
			TriSlot set) {
		try {
			Writer w = AppTrigons.getDebugWriter();
			w.append('\n');
			StringBuilder indent = new StringBuilder();
			for(int i=0; i<solver.arbitrateDepth; ++i){
				indent.append("    ");
			}
			w.append(indent).append("Arbitrate started on " + set.tri).append(":\n").append(dumpSolver(solver).replace("\n", indent + "\n"));
		} catch(IOException e){
			ErrUtil.errMsg(e, "Writing to debug stream for arbitration start");
		}
	}
	
	private void arbitrateEnded(TrigonFiller solver,
			List<TrigonPuzzle> solutions) {
		try {
			Writer w = AppTrigons.getDebugWriter();
			w.append('\n');
			for(int i=0; i<solver.arbitrateDepth; ++i){
				w.append("    ");
			}
			w.append("Arbitrate ended with " + solutions.size() + " solutions");
		} catch(IOException e){
			ErrUtil.errMsg(e, "Writing to debug stream for arbitration end");
		}
	}

	public Collection<TrigonValuePermutation> getPerms(int x, int y) {
		Trigon trigon = puzzle.getTrigon(x, y);
		if(trigon == null) return null;
		TriSlot set = trigons.get(trigon);
		Collection<TrigonValuePermutation> perms = new ArrayList<TrigonValuePermutation>();
		for(TrigonValuePermutation perm : set){
			perms.add(perm);
		}
		return perms;
	}

	public int getRemainingTrigonPermCount() {
		int count = 0;
		for(TriSlot set : trigons.values()){
			count += set.possibilities.size();
		}
		return count;
	}

	
}
