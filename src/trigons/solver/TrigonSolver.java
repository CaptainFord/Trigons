package trigons.solver;

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
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import trigons.AppTrigons;
import trigons.puzzle.TSide;
import trigons.puzzle.TriPuzzle;
import trigons.puzzle.Trigon;
import trigons.puzzle.TrigonBorder;
import trigons.puzzle.TrigonPuzzle;
import trigons.solver.TrigonValueSet.TrigonValuePermutation;
import vordeka.util.ErrUtil;
import vordeka.util.exception.ImpossibleException;
import vordeka.util.list.ArrayWrapper;
import vordeka.util.list.SingleElementList;

public class TrigonSolver implements TrigonSolverOrFiller {
	
	/*
	 * Before Prioritizing:
			Trigons: 384 runs, 0 advances, 0 objs enqueued, 0 perms eliminated
			Borders: 663 runs, 281 advances, 809 objs enqueued, 1742 perms eliminated
			Solutions: 344 runs, 23 advances, 55 objs enqueued, 51 perms eliminated
			Groups: 102 runs, 44 advances, 298 objs enqueued, 271 perms eliminated
		After Prioritizing:
			Trigons: 437 runs, 0 advances, 0 objs enqueued, 0 perms eliminated
			Borders: 799 runs, 281 advances, 1081 objs enqueued, 1859 perms eliminated
			Solutions: 433 runs, 28 advances, 112 objs enqueued, 136 perms eliminated
			Groups: 39 runs, 15 advances, 184 objs enqueued, 69 perms eliminated
		...I'm not sure whether or not that's an improvement. Fascinating, though...isn't it?
	 */
	private TrigonPuzzle puzzle;
	private HashMap<Trigon, TrigonPossibilitySet> trigons = new HashMap<Trigon, TrigonPossibilitySet>();
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
			} else if(obj instanceof TrigonSolution){
				return 2;
			} else if(obj instanceof TrigonValueGroup){
				return 3;
			} else {
				return -1;
			}
		}}); //*/
	private Set<Object> queueSet = new HashSet<Object>();
	private TrigonSolution[] triSolutions;
	private TrigonValueSet[][] triValueSets;
	private TrigonValueGroup[] valueGroups;
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
	 * If true, then this solver will terminate if a malformed puzzle is provided.
	 */
	public boolean allSolutionsMustBeUsed = true;
	/**
	 * When the solution limit is met, the solver terminates and stops trying to find
	 * more solutions. If the limit is set to zero or less, then it will not terminate on
	 * its own until it finds all possible solutions.
	 */
	public int solutionLimit = 10;
	public final int arbitrateDepth;
	public int arbitrationCount = 0;
	public int subSolversCount = 0;
	
	/**	<code>[0-3][] = 0:trigon, 1:border, 2:solution, 3:groups<br />
		[][0-3] = 0:times processed, 1:times progress made, 2:objects enqueued, 3:perms eliminated</code>
	*/
	public int[][] processCounts = new int[4][4];
	public int groupProcessCount = 0;
	public boolean arbitrationEnabled = true;

	public TrigonSolver(TrigonPuzzle puzzle) {
		this.puzzle = new TriPuzzle(puzzle);
		triValueSets = TrigonValueSet.generateSolutionSets(puzzle.getMaxBorderValue());
		valueGroups = new TrigonValueGroup[triValueSets.length];
		for(int i=0; i<triValueSets.length; ++i){
			valueGroups[i] = new TrigonValueGroup(i);
		}
//		ErrUtil.debugMsg("Value Sets:\n" + TrigonValueSet.str(triValueSets));
		arbitrateDepth = 0;
	}
	
	public TrigonSolver(TrigonSolver parent) {
		this.puzzle = parent.puzzle;
		this.triValueSets = parent.triValueSets;
		this.allSolutionsMustBeUsed = parent.allSolutionsMustBeUsed;
		this.solutionLimit = parent.solutionLimit;
		this.arbitrateDepth = parent.arbitrateDepth+1;
		this.valueGroups = parent.valueGroups;
	}
	
	public String dumpProcessCounts(){
		StringBuilder b = new StringBuilder();
		String[] rowNames = "Trigons,Borders,Solutions,Groups".split(",");
		for(int i=0; i<4; ++i){
			b.append(rowNames[i]).append(": ").append(processCounts[i][0]).append(" runs, ").append(processCounts[i][1])
				.append(" advances, ").append(processCounts[i][2]).append(" objs enqueued, ")
				.append(processCounts[i][3]).append(" perms eliminated\n");
		}
		
		b.setLength(b.length()-1);
		return b.toString();
	}

	private List<TrigonPuzzle> solveDescendent(
			TrigonSolver parent, Trigon tri, TrigonValuePermutation arbitratedPerm) {
		isSolveable = true;
		for(TrigonPossibilitySet set : parent.trigons.values()){
			this.trigons.put(set.tri, new TrigonPossibilitySet(set));
		}
		this.triSolutions = new TrigonSolution[parent.triSolutions.length];
		for(int i=0; i<triSolutions.length; ++i){
			this.triSolutions[i] = new TrigonSolution(parent.triSolutions[i].valueSet);
			for(TrigonPossibilitySet set : parent.triSolutions[i].possibleTrigons){
				this.triSolutions[i].possibleTrigons.add(this.trigons.get(set.tri));
			}
		}
		TrigonPossibilitySet arbitrated = this.trigons.get(tri);
		Iterator<TrigonValuePermutation> itr = arbitrated.possibilities.iterator();
		while(itr.hasNext()){
			TrigonValuePermutation perm = itr.next();
			if(perm != arbitratedPerm){
				itr.remove();
			}
			enqueue(triSolutions[perm.set.id]);
		}
		enqueue(tri);
		enqueue(tri.getLeftBorder());
		enqueue(tri.getRightBorder());
		enqueue(tri.getVerticalBorder());
		
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
		while(!queue.isEmpty()){
			Object obj = getNextInQueue();
			if(obj == null){
				throw new NullPointerException("Nulls not allowed in queue. How did it get in there?");
			} else if(obj instanceof Trigon){
				processTrigon((Trigon)obj);
			} else if(obj instanceof TrigonBorder){
				processBorder((TrigonBorder)obj);
			} else if(obj instanceof TrigonSolution){
				processSolution((TrigonSolution)obj);
			} else if(obj instanceof TrigonValueGroup){
				processTrigonGroups(((TrigonValueGroup)obj).value);
			} else {
				throw new RuntimeException("Unexpected object in queue: " + obj.getClass().getName() + " \"" + obj + "\"");
			}
			if(!this.isSolveable){
				break;
			}
		}
		if(!this.isSolveable){
			queue.clear();
			queueSet.clear();
			return Collections.emptyList();
		}
		if(this.arbitrateDepth == 0){
			dumpState("After First Layer of Processing");
		}
		//	Search for any unsolved trigons, and if one is found, then arbitrate.
		for(TrigonPossibilitySet set : trigons.values()){
			if(set.possibilities.isEmpty()){
				throw new ImpossibleException("This should have been caught already");
			} else if(set.possibilities.size() > 1){
				return arbitrate(set);
			}
		}
		dumpState("Final Solution");
		TriPuzzle puzz = new TriPuzzle(this.puzzle);
		List<TrigonPossibilitySet> sortedTrigons = new ArrayList<TrigonPossibilitySet>(trigons.values());
		Collections.sort(sortedTrigons, new Comparator<TrigonPossibilitySet>(){
			@Override
			public int compare(TrigonPossibilitySet t1, TrigonPossibilitySet t2) {
				Trigon o1 = t1.tri;
				Trigon o2 = t2.tri;
				if(o1.getY() == o2.getY())
					return o1.getX() - o2.getX();
				return o1.getY() - o2.getY();
			}});
		for(TrigonPossibilitySet set : sortedTrigons){
			TrigonValuePermutation perm = set.possibilities.get(0);
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
//			ErrUtil.debugMsg(set.tri + " to " + tri);
		}
		return new SingleElementList<TrigonPuzzle>(puzz); 
	}
	
	private void processTrigonGroups() {
		++groupProcessCount;
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
		
	}

	private void processTrigonGroups(int triValue) {
		++processCounts[3][0];
		TrigonValueSet[] sets = triValueSets[triValue];
		TrigonSolution[] sols = new TrigonSolution[sets.length];
		//	This is a list of the permuations that need to be proven
		@SuppressWarnings("unchecked")
		List<TrigonPossibilitySet>[] perms = new List[sols.length];
		for(int i=0; i<sets.length; ++i){
			sols[i] = triSolutions[sets[i].id];
			perms[i] = new ArrayList<TrigonPossibilitySet>(sols[i].possibleTrigons);	
		}
		TrigonPossibilitySet[] picks = new TrigonPossibilitySet[sols.length];
		processTrigonGroupMatchup(0, sols, picks, perms);
		boolean changesMade = false;
		for(int i=0; i<sets.length; ++i){
			if(!perms[i].isEmpty()){
				for(TrigonPossibilitySet tri : perms[i]){
					for(TrigonValuePermutation perm : sets[i].permutations){
						if(tri.possibilities.remove(perm)){
							++processCounts[3][3];
						}
					}
//					tri.possibilities.removeAll(ArrayWrapper.wrap(sets[i].permutations));
					processCounts[3][2] += enqueue(tri.tri);
					processCounts[3][2] += enqueue(tri.tri.getLeftBorder());
					processCounts[3][2] += enqueue(tri.tri.getRightBorder());
					processCounts[3][2] += enqueue(tri.tri.getVerticalBorder());
					processCounts[3][2] += enqueue(sols[i]);
					changesMade = true;
				}
			}
		}
		if(changesMade){
			++processCounts[3][1];
		}
	}

	private void processTrigonGroupMatchup(int index, TrigonSolution[] sols,
			TrigonPossibilitySet[] picks, 
			List<TrigonPossibilitySet>[] perms) {
		if(index == sols.length){
			for(int i=0; i<sols.length; ++i){
				perms[i].remove(picks[i]);
			}
		} else {
			TrigonSolution sol = sols[index];
outerLoop:
			for(TrigonPossibilitySet tri : sol.possibleTrigons){
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
		for(TrigonBorder border : this.puzzle.getBorders()){
			hasMultipleVals = false;
			possibleVal = -1;
			TSide side = border.isVertical() ? TSide.VERT : TSide.RIGHT;
			Trigon tri = border.getFirstTrigon();
			if(tri == null){
				tri = border.getSecondTrigon();
				side = side.getOpposite();
			}
			TrigonPossibilitySet set = this.trigons.get(tri);
			if(set != null){
				for(TrigonValuePermutation perm : set.possibilities){
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
		for(TrigonPossibilitySet set : trigons.values()){
			Trigon tri = set.tri;
			boolean leftUnshared = !tri.getLeftBorder().isSharedBorder();
			boolean rightUnshared = !tri.getRightBorder().isSharedBorder();
			boolean vertUnshared = !tri.getVerticalBorder().isSharedBorder();
			if(leftUnshared && rightUnshared && vertUnshared){
				//	Only possibilities with all three values the same should be allowed, I guess.
				ErrUtil.warningMsg("Encountered a completely isolated trigon at (" + tri.getX() + "," + tri.getY() + ")");
				
				Iterator<TrigonValuePermutation> itr = set.possibilities.iterator();
				while(itr.hasNext()){
					TrigonValuePermutation perm = itr.next();
					if(perm.left != perm.right || perm.right != perm.vertical){
						itr.remove();
					}
				}
			} else if(leftUnshared && rightUnshared){
				Iterator<TrigonValuePermutation> itr = set.possibilities.iterator();
				while(itr.hasNext()){
					TrigonValuePermutation perm = itr.next();
					if(perm.left != perm.right){
						itr.remove();
					}
				}
			} else if(leftUnshared && vertUnshared){
				Iterator<TrigonValuePermutation> itr = set.possibilities.iterator();
				while(itr.hasNext()){
					TrigonValuePermutation perm = itr.next();
					if(perm.left != perm.vertical){
						itr.remove();
					}
				}
			} else if(rightUnshared && vertUnshared){
				Iterator<TrigonValuePermutation> itr = set.possibilities.iterator();
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
		for(TrigonPossibilitySet set : trigons.values()){
			Trigon tri = set.tri;
			int leftValue = tri.getLeftBorder().getValue();
			int rightValue = tri.getRightBorder().getValue();
			int vertValue = tri.getVerticalBorder().getValue();
			if(leftValue >= 0 || rightValue >= 0 || vertValue >= 0){
				Iterator<TrigonValuePermutation> itr = set.possibilities.iterator();
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

	/**
	 * Iterates over all the possible applications of the possibility set.
	 * @param set
	 * @return
	 */
	private List<TrigonPuzzle> arbitrate(TrigonPossibilitySet set) {
		if(!this.arbitrationEnabled){
			return Collections.emptyList();
		}
//		arbitrateStarted(this, set);
//		ErrUtil.debugMsg("Arbitrate called");
		this.arbitrationCount = 1;
		List<TrigonPuzzle> solutions = new ArrayList<TrigonPuzzle>();
		for(TrigonValuePermutation perm : set.possibilities){
			TrigonSolver descendent = new TrigonSolver(this);
			solutions.addAll(descendent.solveDescendent(this, set.tri, perm));
			this.arbitrationCount += descendent.arbitrationCount;
			this.subSolversCount += descendent.subSolversCount + 1;
			if(solutionLimit > 0 && solutions.size() >= solutionLimit){
				break;
			}
		}
//		arbitrateEnded(this, solutions);
//		ErrUtil.debugMsg("Arbitrate returned");
		return solutions;
	}

	private Object getNextInQueue() {
		Object obj = queue.poll();
		queueSet.remove(obj);
		return obj;
	}

	private int enqueue(TrigonValueSet set){
		return enqueue(triSolutions[set.id]);
	}
	private int enqueue(TrigonSolution obj) {
		if(queueSet.add(obj)){
			queue.add(obj);
			return 1;
		}
		return 0;
	}
	private int enqueue(TrigonBorder obj) {
		if(queueSet.add(obj)){
			queue.add(obj);
			return 1;
		}
		return 0;
	}
	private int enqueue(Trigon obj) {
		if(queueSet.add(obj)){
			queue.add(obj);
			return enqueue(valueGroups[obj.getValue()]) + 1;
		}
		return 0;
	}

	private int enqueue(TrigonValueGroup obj) {
		if(queueSet.add(obj)){
			queue.add(obj);
			return 1;
		}
		return 0;
	}

	private void processSolution(TrigonSolution sol) {
		++processCounts[2][0];
		Iterator<TrigonPossibilitySet> itr = sol.possibleTrigons.iterator();
		while(itr.hasNext()){
			TrigonPossibilitySet tri = itr.next();
			boolean matched = false;
			for(TrigonValuePermutation perm : tri.possibilities){
				if(perm.set == sol.valueSet){
					matched = true;
					break;
				}
			}
			if(!matched){
				itr.remove();
			}
		}
		if(sol.possibleTrigons.isEmpty()){
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
					TrigonSolution ts = triSolutions[set.id];
					if(!ts.possibleTrigons.isEmpty())
						++solCount;
				}
				if(numTrigonsWithValue[myTriValue] > solCount){
					addFailureMessage("Too few solutions left for trigons with value " + myTriValue + 
							" (" + numTrigonsWithValue[myTriValue] + " trigons, " + solCount + " solutions)");
					this.isSolveable = false;
				}
			}
		} else if(sol.possibleTrigons.size() == 1){
			TrigonPossibilitySet tri = sol.possibleTrigons.get(0);
			Iterator<TrigonValuePermutation> itr2 = tri.possibilities.iterator();
			boolean madeChanges = false;
			while(itr2.hasNext()){
				TrigonValuePermutation perm = itr2.next();
				if(perm.set != sol.valueSet){
					madeChanges = true;
					itr2.remove();
					++processCounts[2][3];
				}
			}
			if(madeChanges){
				++processCounts[2][1];
				processCounts[2][2] += enqueue(tri.tri);
				processCounts[2][2] += enqueue(tri.tri.getLeftBorder());
				processCounts[2][2] += enqueue(tri.tri.getRightBorder());
				processCounts[2][2] += enqueue(tri.tri.getVerticalBorder());
			}
		}
	}

	private void processBorder(TrigonBorder border) {
		++processCounts[1][0];
		boolean progressMade = false;
		//	Check each possible solution from both trigons to see if they are compatible.
		//	i.e. If one side can be 2, but the other can't be, then we can eliminate those 
		//	possibilities from the former.
		TrigonPossibilitySet first = trigons.get(border.getFirstTrigon());
		TrigonPossibilitySet second = trigons.get(border.getSecondTrigon());
		if(first == null || second == null) return;
		boolean[] possibleOnFirst = new boolean[7];
		boolean[] possibleOnSecond = new boolean[7];
		Arrays.fill(possibleOnFirst, false);
		Arrays.fill(possibleOnSecond, false);
		if(border.isVertical()){
			for(TrigonValuePermutation perm : first.possibilities){
				possibleOnFirst[perm.vertical] = true;
			}
			for(TrigonValuePermutation perm : second.possibilities){
				possibleOnSecond[perm.vertical] = true;
			}
			Iterator<TrigonValuePermutation> itr = first.possibilities.iterator();
			boolean madeChanges = false;
			while(itr.hasNext()){
				TrigonValuePermutation perm = itr.next();
				if(!possibleOnSecond[perm.vertical]){
					itr.remove();
					++processCounts[1][3];
					madeChanges = true;
					processCounts[1][2] += enqueue(perm.set);
				}
			}
			if(madeChanges){
				processCounts[1][2] += enqueue(first.tri);
				processCounts[1][2] += enqueue(first.tri.getLeftBorder());
				processCounts[1][2] += enqueue(first.tri.getRightBorder());
				progressMade = true;
			}
			itr = second.possibilities.iterator();
			madeChanges = false;
			while(itr.hasNext()){
				TrigonValuePermutation perm = itr.next();
				if(!possibleOnFirst[perm.vertical]){
					itr.remove();
					++processCounts[1][3];
					madeChanges = true;
					processCounts[1][2] += enqueue(perm.set);
				}
			}
			if(madeChanges){
				processCounts[1][2] += enqueue(second.tri);
				processCounts[1][2] += enqueue(second.tri.getLeftBorder());
				processCounts[1][2] += enqueue(second.tri.getRightBorder());
				progressMade = true;
			}
		} else {
			for(TrigonValuePermutation perm : first.possibilities){
				possibleOnFirst[perm.right] = true;
			}
			for(TrigonValuePermutation perm : second.possibilities){
				possibleOnSecond[perm.left] = true;
			}
			Iterator<TrigonValuePermutation> itr = first.possibilities.iterator();
			boolean madeChanges = false;
			while(itr.hasNext()){
				TrigonValuePermutation perm = itr.next();
				if(!possibleOnSecond[perm.right]){
					itr.remove();
					++processCounts[1][3];
					madeChanges = true;
					processCounts[1][2] += enqueue(perm.set);
				}
			}
			if(madeChanges){
				processCounts[1][2] += enqueue(first.tri);
				processCounts[1][2] += enqueue(first.tri.getLeftBorder());
				processCounts[1][2] += enqueue(first.tri.getVerticalBorder());
				progressMade = true;
			}
			itr = second.possibilities.iterator();
			madeChanges = false;
			while(itr.hasNext()){
				TrigonValuePermutation perm = itr.next();
				if(!possibleOnFirst[perm.left]){
					itr.remove();
					++processCounts[1][3];
					madeChanges = true;
					processCounts[1][2] += enqueue(perm.set);
				}
			}
			if(madeChanges){
				processCounts[1][2] += enqueue(second.tri);
				processCounts[1][2] += enqueue(second.tri.getVerticalBorder());
				processCounts[1][2] += enqueue(second.tri.getRightBorder());
				progressMade = true;
			}
		}
		if(progressMade){
			++processCounts[1][1];
		}
	}


	private void processTrigon(Trigon tri) {
		++processCounts[0][0];
		TrigonPossibilitySet set = trigons.get(tri);
		if(set.possibilities.isEmpty()){
			this.addFailureMessage("Trigon {" + tri.getX() + "," + tri.getY() + ":" + tri.getValue() + "} cannot be solved");
			this.isSolveable = false;
			return;
		}
		//	I think that's about it, really.
	}

	private void buildPossibilityList() {
		triSolutions = new TrigonSolution[triValueSets[triValueSets.length-1][0].id+1];
		for(int i=0; i<triValueSets.length; ++i){
			for(int j=0; j<triValueSets[i].length; ++j){
				TrigonValueSet set = triValueSets[i][j];
				triSolutions[set.id] = new TrigonSolution(set); 
			}
		}
		
		int[] numTrigonsWithvalue = new int[triValueSets.length];
		for(Trigon tri : puzzle.getTrigons()){
//			trigons.put(new GridPoint(tri.getX(), tri.getY()), new TrigonPossibilitySet(tri, triSolutions[tri.getValue()]));
			TrigonPossibilitySet triSet = new TrigonPossibilitySet(tri, triValueSets[tri.getValue()]);
			trigons.put(tri, triSet);
			for(TrigonValueSet set : triValueSets[tri.getValue()]){
				triSolutions[set.id].possibleTrigons.add(triSet);
			}
			++numTrigonsWithvalue[tri.getValue()];
		}
		for(int i=0; i<numTrigonsWithvalue.length; ++i){
			if(numTrigonsWithvalue[i] > triValueSets[i].length){
				addFailureMessage("More trigons with value " + i + " than solutions (" + numTrigonsWithvalue[i] + " vs " + triValueSets[i].length + ")");
				isSolveable = false;
			} else if(numTrigonsWithvalue[i] < triValueSets[i].length && allSolutionsMustBeUsed){
				//	This should be a failure condition, I suppose. Otherwise the number of possible solutions will be outrageously high.
				//	Dealt with that problem by adding a solution limit, after which the solver terminates.
				addFailureMessage("Fewer trigons with value " + i + " than solutions (" + numTrigonsWithvalue[i] + " vs " + triValueSets[i].length + ")");
				isSolveable = false;
			}
		}
		if(!this.allSolutionsMustBeUsed){
			this.numTrigonsWithValue = numTrigonsWithvalue;
		}
	}
	
	public static TrigonSolver seekSolutionButDoNotArbitrate(TrigonPuzzle puzzle){
		TrigonSolver solver = new TrigonSolver(puzzle);
		solver.solutionLimit = 2;
		solver.allSolutionsMustBeUsed = false;
		solver.arbitrationEnabled  = false;
		return solver;
	}

	public static List<TrigonPuzzle> findSolutions(TrigonPuzzle puzzle){
		TrigonSolver solver = new TrigonSolver(puzzle);
		return solver.solve();
	}
	
	public static boolean isSolveable(TrigonPuzzle puzzle){
		TrigonSolver solver = new TrigonSolver(puzzle);
		solver.solutionLimit = 1;
		solver.allSolutionsMustBeUsed = false;
		return !solver.solve().isEmpty();
	}
	
	public static boolean hasSingleSolution(TrigonPuzzle puzzle){
		TrigonSolver solver = new TrigonSolver(puzzle);
		solver.solutionLimit = 2;
		solver.allSolutionsMustBeUsed = false;
		return solver.solve().size() == 1;
	}
	
	static class TrigonPossibilitySet {

		public final Trigon tri;
		public List<TrigonValuePermutation> possibilities = new ArrayList<TrigonValuePermutation>();
		
		public TrigonPossibilitySet(Trigon tri, TrigonValueSet[] trigonValueSets) {
			this.tri = tri;
			for(TrigonValueSet set : trigonValueSets){
				for(TrigonValuePermutation p : set.permutations){
					possibilities.add(p);
				}
			}
		}

		public TrigonPossibilitySet(TrigonPossibilitySet copyThis) {
			this.tri = copyThis.tri;
			this.possibilities.addAll(copyThis.possibilities);
		}
		
	}
	
	static class TrigonSolution {
		public final TrigonValueSet valueSet;
		public List<TrigonPossibilitySet> possibleTrigons = new ArrayList<TrigonPossibilitySet>();
		
		public TrigonSolution(TrigonValueSet valueSet){
			this.valueSet = valueSet;
		}
		
		public String toString(){
			return valueSet.toString();
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
	
	public static String dumpSolver(TrigonSolver solver) {
		List<TrigonPossibilitySet> trigons = new ArrayList<TrigonPossibilitySet>(solver.trigons.values());
		StringBuilder b = new StringBuilder();
		Collections.sort(trigons, new Comparator<TrigonPossibilitySet>(){
			@Override
			public int compare(TrigonPossibilitySet t1, TrigonPossibilitySet t2) {
				Trigon o1 = t1.tri;
				Trigon o2 = t2.tri;
				if(o1.getY() == o2.getY())
					return o1.getX() - o2.getX();
				return o1.getY() - o2.getY();
			}});
		for(TrigonPossibilitySet set : trigons){
			Trigon tri = set.tri;
			b.append(tri.getX()).append(',').append(tri.getY()).append(':').append(tri.getValue())
				.append(' ').append(set.possibilities.size()).append("-{");
			if(!set.possibilities.isEmpty()){
				for(TrigonValuePermutation sol : set.possibilities){
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

	private void arbitrateStarted(TrigonSolver solver,
			TrigonPossibilitySet set) {
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
	
	private void arbitrateEnded(TrigonSolver solver,
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
		TrigonPossibilitySet set = trigons.get(trigon);
		if(set == null){
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(set.possibilities);
	}

	public int getRemainingTrigonPermCount() {
		int count = 0;
		for(TrigonPossibilitySet set : trigons.values()){
			count += set.possibilities.size();
		}
		return count;
	}

	
}
