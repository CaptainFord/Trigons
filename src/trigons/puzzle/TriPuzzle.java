package trigons.puzzle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import vordeka.util.model.GridPoint;

public class TriPuzzle extends TrigonPuzzle {
	
	protected int minX, minY, maxX, maxY;
	protected Map<GridPoint,TrigonImpl> trigons = new HashMap<GridPoint,TrigonImpl>();
	protected int maxBorderValue = 6;
	protected boolean cornerValuesMustMatch = true;
	
	public TriPuzzle(){
		
	}
	
	
	
	@Override
	public int getMaxBorderValue() {
		return maxBorderValue;
	}



	@Override
	public boolean isCornerValuesMustMatch() {
		return cornerValuesMustMatch;
	}


	@Override
	public void setMaxBorderValue(int maxBorderValue) {
		this.maxBorderValue = maxBorderValue;
	}



	@Override
	public void setCornerValuesMustMatch(boolean mustMatch) {
		this.cornerValuesMustMatch = mustMatch;
	}


	/**
	 * Copies the specified puzzle.
	 * @param src
	 */
	public TriPuzzle(TrigonPuzzle src){
		for(Trigon t : src.getTrigons()){
			this.addTrigon(t.getX(), t.getY(), t.getValue());
		}
		for(TrigonBorder tb : src.getBorders()){
			this.getBorder(tb.getFirstTrigonX(), tb.getFirstTrigonY(), tb.isVertical() ? TSide.VERTICAL : TSide.RIGHT)
				.setValue(tb.getValue());
		}
	}

	@Override
	public int getMinX() {
		return minX;
	}

	@Override
	public int getMaxX() {
		return maxX;
	}

	@Override
	public int getMinY() {
		return minY;
	}

	@Override
	public int getMaxY() {
		return maxY;
	}
	
	protected void expandToPoint(int x, int y) {
		int[] old = {minX, maxX, minY, maxY};
		if(trigons.isEmpty()){
			minX = x;
			maxX = x;
			minY = y;
			maxY = y;
		} else {
			if(x < minX) minX = x;
			if(x > maxX) maxX = x;
			if(y < minY) minY = y;
			if(y > maxY) maxY = y;
		}
		if(minX != old[0] || maxX != old[1] || minY != old[2] || maxY != old[3]){
			this.firePuzzleBoundsChanged();
		}
	}
	
	protected void shrinkFromPoint(int x0, int y0) {
		int[] old = {minX, maxX, minY, maxY};
		if(trigons.isEmpty()){
			maxX = minX;
			maxY = minY;
		} else if(x0 == minX || x0 == maxX || y0 == minY || y0 == maxY){
			Iterator<TrigonImpl> itr = trigons.values().iterator();
			TrigonImpl t = itr.next();
			minX = maxX = t.getX();
			minY = maxY = t.getY();
			while(itr.hasNext()) {
				t = itr.next();
				int x = t.getX();
				int y = t.getY();
				if(x < minX) minX = x;
				if(x > maxX) maxX = x;
				if(y < minY) minY = y;
				if(y > maxY) maxY = y;
			}
		}
		if(minX != old[0] || maxX != old[1] || minY != old[2] || maxY != old[3]){
			this.firePuzzleBoundsChanged();
		}
	}

	@Override
	public Trigon getTrigon(int x, int y) {
		return trigons.get(new GridPoint(x, y));
	}
	

	@Override
	public TrigonBorder getBorder(int x, int y, TSide side) {
		Trigon t = trigons.get(new GridPoint(x, y));
		if(t == null){
			t = getNeighbor(x, y, side);
			side = side.getOpposite();
		}

		return t == null ? null : t.getBorder(side);
	}

	@Override
	public Trigon addTrigon(int x, int y, int value) {
		GridPoint g = new GridPoint(x, y);
		TrigonImpl t = trigons.get(g);
		if(t == null){
			this.expandToPoint(x, y);
			t = new TrigonImpl(this, x, y, value);
			trigons.put(g, t);
			Collection<? extends TrigonBorder> newBorders = assignBorders(t);
			this.fireTrigonAdded(t);
			this.fireBordersAdded(newBorders);
		} else {
			t.setValue(value);
		}
		return t;
	}



	@Override
	public Trigon removeTrigon(int x, int y) {
		GridPoint g = new GridPoint(x, y);
		TrigonImpl t = trigons.remove(g);
		if(t != null){
			Collection<? extends TrigonBorder> oldBorders = unassignBorders(t);
			this.fireTrigonRemoved(t);
			this.fireBordersRemoved(oldBorders);
		}
		return t;
	}
	
	protected Collection<? extends TrigonBorder> assignBorders(TrigonImpl t) {
		List<TrigonBorder> newBorders = new ArrayList<TrigonBorder>(3);
		for(TSide s : TSide.values()){
			Trigon neighbor =  getNeighbor(t, s);
			TriBorder b = (TriBorder) (neighbor == null ? null : 
				neighbor.getBorder(s.getOpposite()));
			if(b == null){
				b = TriBorder.newTriFor(this, t, s);
				newBorders.add(b);
			}
			assign(b, t, s);
		}
		return newBorders;
	}
	
	

	/**
	 * Assigns a trigon to a border. Ensures that no border has multiple trigons assigned to it. 
	 * @param b
	 * @param t
	 * @param s
	 */
	protected void assign(TriBorder b, TrigonImpl t, TSide s) {
		if(t.getBorder(s) != null)
			throw new IllegalStateException("Trigon already has a border assigned! (Border="
						+ b + ";Side=" + s +";Trigon=" + t + ";OldBorder="
						+ t.getBorder(s) + ")");
		boolean first;
		if(s.isVertical()){
			first = t.isPointedUp();
		} else {
			first = s.isRight();
		}
		if(first){
			if(b.first != null) 
				throw new IllegalStateException("Border already has first element! (Border="
						+ b + ";Side=" + s +";NewTrigon=" + t + ";Border.first="
						+ b.first + ")");
			b.first = t;
		} else {
			if(b.second != null) 
				throw new IllegalStateException("Border already has second element! (Border="
						+ b + ";Side=" + s +";NewTrigon=" + t + ";Border.second="
						+ b.second + ")");
			b.second = t;
		}
		t.borders[s.ordinal()] = b;
	}

	protected Collection<? extends TrigonBorder> unassignBorders(TrigonImpl t) {
		List<TrigonBorder> oldBorders = new ArrayList<TrigonBorder>(3);
		for(TSide s : TSide.values()){
			TriBorder b = t.getBorder(s);
			unassign(b, t, s);
			if(b.first == null && b.second == null)
				oldBorders.add(b);
		}
		return oldBorders;
	}

	protected void unassign(TriBorder b, TrigonImpl t, TSide s) {
		if(t.getBorder(s) != b)
			throw new IllegalStateException("Border is not assigned to this trigon! (Border="
						+ b + ";Side=" + s +";Trigon=" + t + ";OldBorder="
						+ t.getBorder(s) + ")");
		boolean first;
		if(s.isVertical()){
			first = t.isPointedUp();
		} else {
			first = !s.isLeft();
		}
		if(first){
			if(b.first != t) 
				throw new IllegalStateException("Trigon not assigned to this border! (Border="
						+ b + ";Side=" + s +";NewTrigon=" + t + ";Border.first="
						+ b.first + ")");
			b.first = null;
		} else {
			if(b.second != t) 
				throw new IllegalStateException("Trigon not assigned to this border! (Border="
						+ b + ";Side=" + s +";NewTrigon=" + t + ";Border.second="
						+ b.second + ")");
			b.second = null;
		}
		t.borders[s.ordinal()] = null;
	}

	@Override
	public Collection<? extends Trigon> getTrigons() {
		return trigons.values();
	}

	@Override
	public Collection<? extends TrigonBorder> getBorders() {
		Set<TrigonBorder> borders = new HashSet<TrigonBorder>();
//		int duplicates = 0;
		for(Trigon tri : getTrigons()){
			for(TSide side : TSide.values()){
				borders.add(tri.getBorder(side));
//					++duplicates;
			}
		}
//		ErrUtil.debugMsg(duplicates + " duplicates detected when retrieving borders");
		return borders;
	}


	public void readData(DataInputStream in) throws IOException{
		int trigonCount = in.readInt();
		for(int i=0; i<trigonCount; ++i){
			int x = in.readInt();
			int y = in.readInt();
			int value = in.readInt();
			
			TrigonImpl trigon = (TrigonImpl)addTrigon(x, y, value);
			trigon.readBorderValues(in);
		}
	}
	
	public void writeData(DataOutputStream out) throws IOException{
		out.writeInt(trigons.size());
		for(TrigonImpl trigon : trigons.values()){
			out.writeInt(trigon.x);
			out.writeInt(trigon.y);
			out.writeInt(trigon.value);
			trigon.writeBorderValues(out);
		}
		
	}


}
