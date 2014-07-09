package trigons.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import trigons.puzzle.TSide;
import vordeka.util.ExceptionUtil;
import vordeka.util.exception.ImpossibleException;

public class TrigonValueSet {
	public static final TrigonValueSet[][] TRI_SETS;
	private static Object[] SETS = new Object[7];
	
	static {
		TRI_SETS = generateSolutionSets(6);
	}

	public final int id;
	/**
	 * The ordinal is within the set of trigons with a certain value. i.e. the 2nd solution
	 * for trigons with value 10.
	 */
	public final int ordinal;
	public final int a, b, c;
	public final TrigonValuePermutation[] permutations; 

	public TrigonValueSet(int id, int ordinal, int nextPermutationId, int a, int b, int c) {
		this.id = id;
		this.ordinal = ordinal;
		this.a = a;
		this.b = b;
		this.c = c;
		
		if(a == b){
			if(b == c){
				permutations = new TrigonValuePermutation[] {
						new TrigonValuePermutation(this, 0, nextPermutationId++, a, b, c),	//	3 3 3
					};
			} else {
				permutations = new TrigonValuePermutation[] {
						new TrigonValuePermutation(this, 0, nextPermutationId++, a, b, c),	//	3 3 1
						new TrigonValuePermutation(this, 1, nextPermutationId++, a, c, b),	//	3 1 3
						new TrigonValuePermutation(this, 2, nextPermutationId++, c, a, b),	//	1 3 3
					};
			}
		} else if(b == c){
			permutations = new TrigonValuePermutation[] {
					new TrigonValuePermutation(this, 0, nextPermutationId++, a, b, c),	//	3 1 1
					new TrigonValuePermutation(this, 1, nextPermutationId++, b, a, c),	//	1 3 1
					new TrigonValuePermutation(this, 2, nextPermutationId++, b, c, a),	//	1 1 3
				};
		} else {
			permutations = new TrigonValuePermutation[] {
				new TrigonValuePermutation(this, 0, nextPermutationId++, a, b, c),
				new TrigonValuePermutation(this, 1, nextPermutationId++, a, c, b),
				new TrigonValuePermutation(this, 2, nextPermutationId++, b, a, c),
				new TrigonValuePermutation(this, 3, nextPermutationId++, b, c, a),
				new TrigonValuePermutation(this, 4, nextPermutationId++, c, a, b),
				new TrigonValuePermutation(this, 5, nextPermutationId++, c, b, a),
			};
		}
		
	}
	
	public static TrigonValueSet[][] generateSolutionSets(int maxBorderValue) {
		if(maxBorderValue <= 0) throw new IllegalArgumentException("maxBorderValue must be positive (maxBorderValue=" + maxBorderValue + ")");
		if(maxBorderValue > 20) throw new IllegalArgumentException("maxBorderValue is absurdly large (>20) (maxBorderValue=" + maxBorderValue + ")");
		if(maxBorderValue >= SETS.length){
			SETS = Arrays.copyOf(SETS, maxBorderValue + 1);
		}
		if(SETS[maxBorderValue] == null){
			@SuppressWarnings("unchecked")
			List<TrigonValueSet>[] lists = new List[maxBorderValue * 3 + 1];
			for(int i=0; i<lists.length; ++i){
				lists[i] = new ArrayList<TrigonValueSet>();
			}
			
			int id = 0;
			int permutationId = 0;
			//	It's possible to predict exactly how large the buckets need to be, but that's more work than necessary.
			for(int i=0; i<=maxBorderValue; ++i){
				for(int j=0; j<=i; ++j){
					for(int k=0; k<=j; ++k){
						int total = i + j + k;
						int ordinal = lists[total].size();
						TrigonValueSet set = new TrigonValueSet(id++, ordinal, permutationId, k, j, i);
						permutationId += set.permutations.length;
						lists[total].add(set);
					}
				}
			}
			
			TrigonValueSet[][] set = new TrigonValueSet[lists.length][];
			for(int i=0; i<lists.length; ++i){
				set[i] = lists[i].toArray(new TrigonValueSet[lists[i].size()]);
			}
			SETS[maxBorderValue] = set;
		}
		return (TrigonValueSet[][]) SETS[maxBorderValue];
	}

	public int getValue(int index){
		ExceptionUtil.checkIndexLess(index, 3);
		switch(index){
		case 0: return a;
		case 1: return b;
		case 2: return c;
		default: throw new ImpossibleException("Illegal Index: " + index);
		}
	}
	
	public static class TrigonValuePermutation {
		public final TrigonValueSet set;
		public final int id;
		public final int ordinal;
		
		public final int left, right, vertical;
		
		public TrigonValuePermutation(TrigonValueSet set, int ordinal, int id,
				int left, int right, int vertical) {
			this.set = set;
			this.ordinal = ordinal;
			this.id = id;
			this.left = left;
			this.right = right;
			this.vertical = vertical;
		}
		public int hashCode(){
			return id;
		}
		public boolean equals(Object o){
			if(this == o) return true;
			if(!(o instanceof TrigonValuePermutation)) return false;
			TrigonValuePermutation set = (TrigonValuePermutation)o;
			return this.left == set.left && this.right == set.right && this.vertical == set.vertical && this.id == set.id;
		}
		
		public String toString(){
			return "{" + left + "," + right + "," + vertical +"}";
		}
		
		public int getValue(TSide side) {
			switch(side){
			case LEFT:
				return left;
			case RIGHT:
				return right;
			case VERTICAL:
				return vertical;
			default:
				throw new RuntimeException("Unknown side: " + side);
			}
		}
		public int getTotal() {
			return left + right + vertical;
		}
	}
	
	public int hashCode(){
		return id;
	}
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof TrigonValueSet)) return false;
		TrigonValueSet set = (TrigonValueSet)o;
		return this.a == set.a && this.b == set.b && this.c == set.c && this.id == set.id;
	}
	
	public String toString(){
		return "{" + a + "," + b + "," + c +"}";
	}

	public int getTotal() {
		return a + b + c;
	}
	
	public static String str(TrigonValueSet[][] sets){
		return str(sets, " ");
	}
	
	public static String str(TrigonValueSet[][] sets, String colSpacer){
		int rowCount = 0;
		StringBuilder b = new StringBuilder();
		for(int i=0; i<sets.length; ++i){
			//	Prints the column heads and counts the maximum number of rows
			b.append(i >= 10 ? " " : "  ").append(i).append(colSpacer);
			if(sets[i].length > rowCount)
				rowCount = sets[i].length;
		}
		b.setLength(b.length()-colSpacer.length());	//	Removes the extra spacer at the end
		for(int r=0; r<rowCount; ++r){
			b.append('\n');
			for(int c=0; c<sets.length; ++c){
				if(r < sets[c].length){
					TrigonValueSet set = sets[c][r];
					b.append(set.a).append(set.b).append(set.c).append(colSpacer);
				} else {
					b.append("   ").append(colSpacer);
				}
			}
			b.setLength(b.length()-colSpacer.length());	//	Removes the extra spacer at the end
		}
		return b.toString();
	}
}
