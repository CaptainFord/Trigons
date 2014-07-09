package trigons.puzzle;

import trigons.swing.TrigonUtil;

public class TriBorder extends TrigonBorder {

	protected final TrigonPuzzle puzzle;
	protected final boolean vertical;
	protected final int firstTriX, firstTriY;
	protected Trigon first, second;
	protected int value = -1;
	
	public TriBorder(TrigonPuzzle puzzle, int firstTrigonX, int firstTrigonY, boolean vertical){
		this.puzzle = puzzle;
		this.firstTriX = firstTrigonX;
		this.firstTriY = firstTrigonY;
		this.vertical = vertical;
	}
	
	@Override
	public boolean isVertical() {
		return vertical;
	}
	

	@Override
	public int getFirstTrigonX() {
		return firstTriX;
	}

	@Override
	public int getFirstTrigonY() {
		return firstTriY;
	}

	@Override
	public int getSecondTrigonX() {
		return vertical ? firstTriX : firstTriX + 1;
	}

	@Override
	public int getSecondTrigonY() {
		return vertical ? firstTriY + 1 : firstTriY;
	}

	@Override
	public Trigon getFirstTrigon() {
		return first;
	}

	@Override
	public Trigon getSecondTrigon() {
		return second;
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public void setValue(int value) {
		if(value < -1) value = -1;
		if(this.value == value) return;
		this.value = value;
		puzzle.fireBorderChanged(this);
	}

	public static TriBorder newTriFor(TriPuzzle puzz, TrigonImpl t, TSide s) {
		if(s.isLeft()){
			return new TriBorder(puzz, t.x-1, t.y, false);
		} else if(s.isRight()){
			return new TriBorder(puzz, t.x, t.y, false);
		} else if(TrigonUtil.doesTrianglePointUp(t.x, t.y)){
			return new TriBorder(puzz, t.x, t.y, true);
		} else {
			return new TriBorder(puzz, t.x, t.y-1, true);
		}
	}

	public String toString(){
		StringBuilder b = new StringBuilder("{");
		b.append(firstTriX).append(',').append(firstTriY);
		
		b.append(',').append(isVertical() ? 'v' : 'h').append(':');
		
		int value = getValue();
		if(value < 0) 	b.append('_');
		else			b.append(value);
		
		return b.append('}').toString();
	}
}
