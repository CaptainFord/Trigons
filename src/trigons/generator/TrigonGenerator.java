package trigons.generator;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import trigons.puzzle.TSide;
import trigons.puzzle.TriPuzzle;
import trigons.puzzle.TrigonPuzzle;
import trigons.solver.TrigonValueSet;
import trigons.solver.TrigonValueSet.TrigonValuePermutation;
import trigons.swing.TrigonUtil;
import vordeka.util.ErrUtil;
import vordeka.util.collection.ShufflingCollection;
import vordeka.util.list.ArrayWrapper;
import vordeka.util.model.GridPoint;

public class TrigonGenerator {
	private Random rand;
	public int maxBorderValue;
	public boolean cornerValuesMustMatch = true;
	
	public final Map<GridPoint, TriSlot> slots = new HashMap<GridPoint, TriSlot>();
	public final Set<TriSolution> unusedValueSets = new HashSet<TriSolution>();
	public final Set<TriSlot> unusedSlots = new HashSet<TriSlot>();
	public TriSolution[] sols;

	public TrigonGenerator(Random rand, int maxBorderValue){
		this.rand = rand;
		this.maxBorderValue = maxBorderValue;
	}

	public TrigonPuzzle generatePuzzle(boolean includeBorderValues){
		slots.clear();
		unusedValueSets.clear();
		unusedSlots.clear();
		TrigonValueSet[][] triValueSets = TrigonValueSet.generateSolutionSets(maxBorderValue);
		sols = new TriSolution[triValueSets[triValueSets.length-1][0].id+1];
		for(TrigonValueSet[] arr : triValueSets){
			for(TrigonValueSet set : arr){
				TriSolution sol = new TriSolution(set);
				sols[set.id] = sol;
				unusedValueSets.add(sol);
			}
		}
		
		//	Seed the puzzle with a starting trigon
		TriSlot seedSlot = new TriSlot(0, 0);
		TriSolution seedSet = sols[rand.nextInt(sols.length)];
		unusedValueSets.remove(seedSet);
		seedSlot.place(seedSet.perms[rand.nextInt(seedSet.perms.length)]);
		slots.put(new GridPoint(0, 0), seedSlot);
		unusedSlots.add(genSlot(-1, 0));
		unusedSlots.add(genSlot(1, 0));
		unusedSlots.add(genSlot(0, TrigonUtil.doesTrianglePointUp(0, 0) ? 1 : -1));
		genRoutine();
		TriPuzzle puzzle = new TriPuzzle();
		for(TriSlot slot : slots.values()){
			if(slot.placedPerm != null){
				puzzle.addTrigon(slot.x, slot.y, slot.placedPerm.set.set.getTotal());
				if(includeBorderValues){
					puzzle.getBorder(slot.x, slot.y, TSide.LEFT).setValue(slot.placedPerm.perm.left);
					puzzle.getBorder(slot.x, slot.y, TSide.RIGHT).setValue(slot.placedPerm.perm.right);
					puzzle.getBorder(slot.x, slot.y, TSide.VERTICAL).setValue(slot.placedPerm.perm.vertical);
				}
			}
		}
		return puzzle;
	}
	
	private void genRoutine() {
		Collection<TriSlot> shuffledSlots = ShufflingCollection.wrap(unusedSlots, rand);
		Collection<TriSolution> shuffledSols = ShufflingCollection.wrap(unusedValueSets, rand);
		while(!unusedValueSets.isEmpty()){
			TriSlot bestSlot = null;
			for(TriSlot slot : shuffledSlots){
				if(bestSlot == null){
					bestSlot = slot;
				} else if(isHigherPriority(slot, bestSlot)){
					bestSlot = slot;
				}
			}
			if(bestSlot == null){
				ErrUtil.warningMsg("Ran out of usable slots");
				break;
			}
			unusedSlots.remove(bestSlot);
			int[] constraints = bestSlot.getNeighboringValues();
			int[] totals = bestSlot.getNeighborTotals();
			TriSolution bestSol = null;
			int bestSolSum = 0;
			for(TriSolution sol : shuffledSols){
				if(matchesConstraints(sol.set, constraints)){
					int solTotal = sol.set.getTotal();
					int diffSum = 0;
					for(int i=0; i<totals.length; ++i){
						diffSum += Math.abs(solTotal - totals[i]);
					} 
					if(bestSol == null || diffSum > bestSolSum){
						bestSol = sol;
						bestSolSum = diffSum;
					}
				}
			}
			if(bestSol != null){
				TriPerm selectedPerm = null;
				for(TriPerm perm : ShufflingCollection.wrap(ArrayWrapper.wrap(bestSol.perms), rand)){
					if(bestSlot.willFit(perm)){
						selectedPerm = perm;
						break;
					}
				}
				if(selectedPerm == null){
					ErrUtil.warningMsg("Unable to find a permuation that fits! This should not happen.");
				} else {
					bestSlot.place(selectedPerm);
					genNeighbors(bestSlot);
					unusedValueSets.remove(bestSol);
				}
			}
		}
	}

	private void genNeighbors(TriSlot slot) {
		TriSlot s = genSlot(slot.x-1, slot.y);
		if(s.placedPerm == null) unusedSlots.add(s);
		s = genSlot(slot.x+1, slot.y);
		if(s.placedPerm == null) unusedSlots.add(s);
		s = genSlot(slot.x, TrigonUtil.doesTrianglePointUp(slot.x, slot.y) ? slot.y+1 : slot.y-1);
		if(s.placedPerm == null) unusedSlots.add(s);		
	}

	private boolean matchesConstraints(TrigonValueSet set, int[] constraints) {
		if(constraints.length == 3){
			return set.a == constraints[0] && set.b == constraints[1] && set.c == constraints[2];
		} else if(constraints.length == 2){
			if(set.a == constraints[0]){
				return set.b == constraints[1] || set.c == constraints[1];
			} else {
				return set.b == constraints[0] && set.c == constraints[1];
			}
		} else if(constraints.length == 1){
			return set.a == constraints[0] || set.b == constraints[0] || set.c == constraints[0];
		} else {
			return true;
		}
	}

	/**
	 * Tests if slot1 is higher priority than slot2
	 * @param slot1
	 * @param slot2
	 * @return
	 * 		true if slot1 is higher priority
	 */
	private boolean isHigherPriority(TriSlot slot1, TriSlot slot2) {
		int corner1 = slot1.getAdjacentCornerType();
		int corner2 = slot2.getAdjacentCornerType();
		if(corner1 != corner2){
//			if(corner1 == 1) 
//				return false;
//			if(corner2 == 1)
//				return true;
			return corner1 > corner2;
		}
		int dist1 = Math.abs(slot1.x) + Math.abs(slot1.y);
		int dist2 = Math.abs(slot2.x) + Math.abs(slot2.y);
		return dist1 < dist2;
	}

	private TriSlot genSlot(int x, int y) {
		GridPoint p = new GridPoint(x, y);
		TriSlot slot = slots.get(p);
		if(slot == null){
			slot = new TriSlot(x, y);
			slots.put(p, slot);
			assignNeighbors(slot);
//			buildPossibilityList(slot);
		}
		return slot;
	}

	private void assignNeighbors(TriSlot slot) {
		GridPoint p = new GridPoint(slot.x-1, slot.y);
		TriSlot neighbor = slots.get(p);
		if(neighbor != null){
			slot.left = neighbor;
			neighbor.right = slot;
		}
		p.x += 2;
		neighbor = slots.get(p);
		if(neighbor != null){
			slot.right = neighbor;
			neighbor.left = slot;
		}
		p.x -= 1;
		p.y += TrigonUtil.doesTrianglePointUp(slot.x, slot.y) ? +1 : -1;
		neighbor = slots.get(p);
		if(neighbor != null){
			slot.vert = neighbor;
			neighbor.vert = slot;
		}
	}

	/*private void buildPossibilityList(TriSlot slot) {
		if(slot.placedPerm != null) return;
		if(slot.possibilities != null){
			refinePossibilities()
		}
		slot.possibilities.clear();
		
		//	All slots, when created, should have exactly one neighbor with a placed value.
		//	If not, then just add all possible permutations, I guess
		if(slot.left != null && slot.left.placedPerm != null){
			int value = slot.le
		} else if(slot.right != null && slot.right.placedPerm != null){
			
		} else {
			for(TriSolution set : unusedValueSets){
				if(!set.used){
					for(TriPerm perm : set.perms){
						
					}
				}
			}
		}
	}*/
	
	public static class TriSlot {
		public final int x, y;
		public TriPerm placedPerm = null;
//		public List<TriPerm> possibilities;
		public TriSlot left, right, vert;
		public int cornerType;	//	 0 = no corner; 1 = corner with matched values; 2 = corner with mismatched values; 3 = isolated trigon
		
		public TriSlot(int x, int y){
			this.x = x;
			this.y = y;
		}

		public boolean willFit(TriPerm perm) {
			if(left != null && left.placedPerm != null && left.placedPerm.perm.right != perm.perm.left){
				return false;
			}
			if(right != null && right.placedPerm != null && right.placedPerm.perm.left != perm.perm.right){
				return false;
			}
			if(vert != null && vert.placedPerm != null && vert.placedPerm.perm.vertical != perm.perm.vertical){
				return false;
			}
			return true;
		}

		public int[] getNeighborTotals() {
			int[] values = new int[3];
			int count = 0;
			if(left != null && left.placedPerm != null){
				values[count++] = left.placedPerm.getTotal();
			}
			if(right != null && right.placedPerm != null){
				values[count++] = right.placedPerm.getTotal();
			}
			if(vert != null && vert.placedPerm != null){
				values[count++] = vert.placedPerm.getTotal();
			}
			values = Arrays.copyOf(values, count);
			Arrays.sort(values);
			return values;
		}

		public int[] getNeighboringValues() {
			int[] values = new int[3];
			int count = 0;
			if(left != null && left.placedPerm != null){
				values[count++] = left.placedPerm.perm.right;
			}
			if(right != null && right.placedPerm != null){
				values[count++] = right.placedPerm.perm.left;
			}
			if(vert != null && vert.placedPerm != null){
				values[count++] = vert.placedPerm.perm.vertical;
			}
			values = Arrays.copyOf(values, count);
			Arrays.sort(values);
			return values;
		}

		public void place(TriPerm perm) {
			this.placedPerm = perm;
//			this.possibilities = SingleElementList.wrap(perm);
			this.updateCornerType();
			if(left != null)
				left.updateCornerType();
			if(right != null)
				right.updateCornerType();
			if(vert != null)
				vert.updateCornerType();
		}

		private void updateCornerType() {
			this.cornerType = getCornerType();
		}

		private int getCornerType() {
			if(this.placedPerm == null){
				return 0;
			}
			TrigonValuePermutation perm = this.placedPerm.perm;
			if(left == null || left.placedPerm == null){
				if(right == null || right.placedPerm == null){
					if(vert == null || vert.placedPerm == null){
						return 3;
					}
					return perm.left == perm.right ? 1 : 2;
				} else if(vert == null || vert.placedPerm == null){
					return perm.left == perm.vertical ? 1 : 2;
				}
			} else if(right == null || right.placedPerm == null){
				if(vert == null || vert.placedPerm == null){
					return perm.right == perm.vertical ? 1 : 2;
				}
			}
			return 0;
		}
		
		public int getAdjacentCornerType(){
			int cornerType = 1;
			if(left != null && left.cornerType > cornerType){
				cornerType = left.cornerType;
			}
			if(right != null && right.cornerType > cornerType){
				cornerType = right.cornerType;
			}
			if(vert != null && vert.cornerType > cornerType){
				cornerType = vert.cornerType;
			}
			return cornerType;
		}
		
	}
	
	public static class TriSolution {
		public final TrigonValueSet set;
		public final TriPerm[] perms;
		public boolean used = false;
		
		public TriSolution(TrigonValueSet set) {
			this.set = set;
			perms = new TriPerm[set.permutations.length];
			for(int i=0; i<perms.length; ++i){
				perms[i] = new TriPerm(this, set.permutations[i]);
			}
		}
	}
	public static class TriPerm {
		public final TriSolution set;
		public final TrigonValuePermutation perm;
		
		public TriPerm(TriSolution set, TrigonValuePermutation perm) {
			this.set = set;
			this.perm = perm;
		}

		public int getTotal() {
			return perm.getTotal();
		}
		
		
	}
}
