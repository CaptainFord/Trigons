package trigons.puzzle;

import trigons.swing.TrigonUtil;

/**
 * Represents a triangle in a trigon puzzle.
 * <p>
 * Trigons are now hashable based on their position within the puzzle.
 * @author Vordeka
 *
 */
public abstract class Trigon {
	
	public abstract int getX();
	public abstract int getY();
	
	public abstract int getValue();
	public abstract void setValue(int value);
	
	public abstract TrigonBorder getBorder(TSide side);
	
	
	public TrigonBorder getLeftBorder(){
		return getBorder(TSide.LEFT);
	}
	
	public TrigonBorder getRightBorder(){
		return getBorder(TSide.RIGHT);
	}
	
	public TrigonBorder getVerticalBorder(){
		return getBorder(TSide.VERTICAL);
	}

	public String toString(){
		StringBuilder b = new StringBuilder();
		b.append('<').append(getX()).append(',').append(getY())
			.append(':').append(getValue()).append('=');
		for(TSide s : TSide.values()){
			TrigonBorder g = getBorder(s);
			int val = g == null ? -1 : g.getValue();
			if(val < 0) 	b.append('_');
			else			b.append(val);
		}
		return b.append('>').toString();
	}
	
	public boolean isPointedUp() {
		return TrigonUtil.doesTrianglePointUp(getX(), getY());
	}
	
	public int hashCode(){
		return getX() * 17 + getY();
	}
	
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof Trigon)) return false;
		Trigon tri = (Trigon)o;
		return getX() == tri.getX() && getY() == tri.getY();
	}
	
}
