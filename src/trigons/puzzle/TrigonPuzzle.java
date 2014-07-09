package trigons.puzzle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import trigons.swing.TrigonUtil;
import vordeka.util.ErrUtil;
import vordeka.util.exception.ImpossibleException;

public abstract class TrigonPuzzle {
	
	private transient List<TrigonListener> listeners = new CopyOnWriteArrayList<TrigonListener>();
	
	public abstract int getMinX();
	public abstract int getMaxX();
	public abstract int getMinY();
	public abstract int getMaxY();
	
	public abstract Trigon getTrigon(int x, int y);
	public abstract TrigonBorder getBorder(int x, int y, TSide side);
	
	public abstract Trigon addTrigon(int x, int y, int value);
	public abstract Trigon removeTrigon(int x, int y);
	
	public boolean removeTrigon(Trigon trigon){
		if(trigon == null || !trigon.equals(getTrigon(trigon.getX(), trigon.getY())))
			return false;
		Trigon t = removeTrigon(trigon.getX(), trigon.getY());
		if(trigon.equals(t)) return true;
		if(t == null){ 
			ErrUtil.warningMsg("Removal failed unexpectedly: " + trigon);
			return false;
		}
		throw new ImpossibleException("Trigon removed does not match trigon retrieved");
	}
	
	public Trigon getNeighbor(Trigon t, TSide s) {
		return getNeighbor(t.getX(), t.getY(), s);
	}
	
	public Trigon getNeighbor(int x, int y, TSide s) {
		if(s.isLeft()){
			return getTrigon(x-1, y);
		} else if(s.isRight()){
			return getTrigon(x+1, y);
		} else {
			return getTrigon(x, y + (TrigonUtil.doesTrianglePointUp(x, y) ? 1 : -1));
		}
	}
	
	public void addTrigonListener(TrigonListener l){
		this.listeners.add(l);
	}
	
	public void removeTrigonListener(TrigonListener l){
		this.listeners.remove(l);
	}

	protected void fireTrigonChanged(Trigon trigon){
		for(TrigonListener l : listeners){
			l.trigonValueChanged(this, trigon);
		}
	}
	
	protected void fireBorderChanged(TrigonBorder border){
		for(TrigonListener l : listeners){
			l.borderValueChanged(this, border);
		}
	}
	
	protected void fireTrigonAdded(Trigon trigon){
		for(TrigonListener l : listeners){
			l.trigonAdded(this, trigon);
		}
	}
	
	protected void fireTrigonRemoved(Trigon trigon){
		for(TrigonListener l : listeners){
			l.trigonRemoved(this, trigon);
		}
	}
	
	protected void fireBordersAdded(Collection<? extends TrigonBorder> borders){
		if(borders.isEmpty()) return;
		for(TrigonListener l : listeners){
			l.bordersAdded(this, borders);
		}
	}
	
	protected void fireBordersRemoved(Collection<? extends TrigonBorder> borders){
		if(borders.isEmpty()) return;
		for(TrigonListener l : listeners){
			l.bordersRemoved(this, borders);
		}
	}
	

	protected void firePuzzleBoundsChanged() {
		for(TrigonListener l : listeners){
			l.puzzleBoundsChanged(this);
		}
	}
	
	public abstract Collection<? extends Trigon> getTrigons();
	public abstract Collection<? extends TrigonBorder> getBorders();
	
	//	Get puzzle settings.
	//	It makes sense for the puzzle settings to be stored with the puzzle.
	/**
	 * The maximum value that a TrigonBorder can have. This determines how many
	 * trigons are in a puzzle and what values they may have. (Usually this value is 6)
	 * <p>
	 * Note: The TrigonPuzzle will not necessarily validate values against this setting.
	 * The value of this setting does not guarantee anything about the other data stored
	 * in this puzzle.
	 * @return
	 * 		the maximum value a border can have
	 */
	public abstract int getMaxBorderValue();
	
	/**
	 * Tests if the corner rule is active. This rule states that when a triangle
	 * has two borders not shared with any other triangle, then both unshared borders
	 * must have the same value. (I have never seen a puzzle where this rule is not on)
	 * <p>
	 * Note: The TrigonPuzzle will not necessarily validate values against this setting.
	 * The value of this setting does not guarantee anything about the other data stored
	 * in this puzzle.
	 * @return
	 * 		true if unshared borders must have the same value, false otherwise
	 */
	public abstract boolean isCornerValuesMustMatch();
	
	/**
	 * Sets the maximum value that a TrigonBorder can have. This determines how many
	 * trigons are in a puzzle and what values they may have. (Defaults to 6)
	 * @param maxBorderValue
	 * 		the maximum value a border can have
	 */
	public abstract void setMaxBorderValue(int maxBorderValue);
	/**
	 * Sets if the corner rule is active. This rule states that when a triangle
	 * has two borders not shared with any other triangle, then both unshared borders
	 * must have the same value. (I have never seen a puzzle where this rule is not on)
	 * @param mustMatch
	 * 		true if unshared borders must have the same value, false otherwise
	 */
	public abstract void setCornerValuesMustMatch(boolean mustMatch);

	public void clear() {
		List<Trigon> trigons = new ArrayList<Trigon>(getTrigons());
		for(Trigon trigon : trigons){
			this.removeTrigon(trigon);
		}
	}
}
