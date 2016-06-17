package trigons;

import java.util.Random;

import trigons.swing.TrigonModelListener;
import trigons.puzzle.TSide;
import trigons.swing.TrigonModel;
import trigons.swing.TrigonUtil;

public class RandomTrigonModel implements TrigonModel {

	public RandomTrigonModel(){
		
	}
	
	@Override
	public void addTrigonListener(TrigonModelListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTrigonListener(TrigonModelListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMinX() {
		return 0;
	}

	@Override
	public int getMaxX() {
		return 5;
	}

	@Override
	public int getMinY() {
		return 0;
	}

	@Override
	public int getMaxY() {
		return 5;
	}

	@Override
	public int getTriValueAt(int x, int y) {
		Random r = new Random((long)x * 113 + y);
		return r.nextInt(6) + r.nextInt(6) + r.nextInt(6) + 3;
	}

	@Override
	public int getBorderValueAt(int x, int y, int side) {
		Random r;
		if(side == SIDE_VERT){
			if(TrigonUtil.doesTrianglePointUp(x, y)){
				r = new Random((long)x * 113 + y + 111231);
			} else {
				r = new Random((long)x * 113 + y-1 + 111231);
			}
		} else if(side == SIDE_LEFT){
			r = new Random((long)(x-1) * 113 + y + 1477);
			
		} else if(side == SIDE_RIGHT){
			r = new Random((long)x * 113 + y + 1477);
		} else {
			throw new IllegalArgumentException("Unknown side: " + side);
		}
		return Math.max(0, r.nextInt(10) - 4);
	}

	@Override
	public int getBorderValueAt(int x, int y, TSide side) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasTriangleAt(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

}
