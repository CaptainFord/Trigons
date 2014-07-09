package trigons.swing;

import java.util.Collection;

import trigons.puzzle.TSide;
import trigons.puzzle.Trigon;
import trigons.puzzle.TrigonBorder;
import trigons.puzzle.TrigonListener;
import trigons.puzzle.TrigonPuzzle;

public class DefaultTrigonModel extends AbstractTrigonModel {
	
	protected static final TSide SIDE[];
	static {
		SIDE = new TSide[3];
		SIDE[SIDE_LEFT] = TSide.LEFT;
		SIDE[SIDE_RIGHT] = TSide.RIGHT;
		SIDE[SIDE_VERT] = TSide.VERTICAL;
	}
	
	protected final Handler handler = new Handler();
	protected final TrigonPuzzle puzzle;

	public DefaultTrigonModel(TrigonPuzzle puzzle){
		this.puzzle = puzzle;
		puzzle.addTrigonListener(handler);
	}
	@Override
	public int getMinX() {
		return puzzle.getMinX();
	}

	@Override
	public int getMaxX() {
		return puzzle.getMaxX();
	}

	@Override
	public int getMinY() {
		return puzzle.getMinY();
	}

	@Override
	public int getMaxY() {
		return puzzle.getMaxY();
	}

	@Override
	public int getTriValueAt(int x, int y) {
		Trigon t = puzzle.getTrigon(x, y);
		return t == null ? -2 : t.getValue();
	}

	@Override
	public int getBorderValueAt(int x, int y, int side) {
		return getBorderValueAt(x, y, SIDE[side]);
	}
	
	@Override
	public int getBorderValueAt(int x, int y, TSide side) {
//		Trigon t = puzzle.getTrigon(x, y);
//		TrigonBorder b;
//		if(t == null){
//			Trigon neighbor = puzzle.getNeighbor(x, y, side);
//			if(neighbor == null){
//				return -2;
//			}
//			b = neighbor.getBorder(side.getOpposite());
//		} else {
//			b = t.getBorder(side);
//		}
		TrigonBorder b = puzzle.getBorder(x, y, side);
//		ErrUtil.debugOnce("getBorder(" + x + "," + y +"," + side +") = " + b + (b == null ? "" : "    value=" + b.getValue()));
		return b == null ? -2 : b.getValue();
	}

	class Handler implements TrigonListener {

		@Override
		public void trigonValueChanged(TrigonPuzzle puzzle, Trigon trigon) {
			DefaultTrigonModel.this.fireTrigonValueChanged(trigon);
		}

		@Override
		public void trigonAdded(TrigonPuzzle puzzle, Trigon trigon) {
			DefaultTrigonModel.this.fireTrigonValueChanged(trigon);
		}

		@Override
		public void trigonRemoved(TrigonPuzzle puzzle, Trigon trigon) {
			DefaultTrigonModel.this.fireTrigonValueChanged(trigon);
		}

		@Override
		public void borderValueChanged(TrigonPuzzle puzzle, TrigonBorder border) {
			DefaultTrigonModel.this.fireBorderValueChanged(border);
		}

		@Override
		public void bordersAdded(TrigonPuzzle puzzle,
				Collection<? extends TrigonBorder> newBorders) {
			for(TrigonBorder b : newBorders){
				DefaultTrigonModel.this.fireBorderValueChanged(b);
			}
		}

		@Override
		public void bordersRemoved(TrigonPuzzle puzzle,
				Collection<? extends TrigonBorder> oldBorders) {
			for(TrigonBorder b : oldBorders){
				DefaultTrigonModel.this.fireBorderValueChanged(b);
			}
		}

		@Override
		public void puzzleBoundsChanged(TrigonPuzzle puzzle) {
			DefaultTrigonModel.this.fireBoundsChanged();
		}
		
	}

	@Override
	public boolean hasTriangleAt(int x, int y) {
		return puzzle.getTrigon(x, y) != null;
	}

	
}
