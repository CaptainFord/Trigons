package trigons.swing;

import trigons.puzzle.TSide;
import vordeka.util.exception.ImpossibleException;
import vordeka.util.model.GridPoint;

/**
 * Functions similarly to GridSpace, but specifies a triangle border rather than a triangle.
 * @author Vordeka
 *
 */
public class GridBorder implements Cloneable {
	public int x;
	public int y;
	public TSide side;
	
	public GridBorder(){
		
	}

	public GridBorder(int x, int y, TSide side) {
		this.x = x;
		this.y = y;
		this.side = side;
	}
	
	public GridBorder(GridPoint p, TSide side) {
		this.x = p.x;
		this.y = p.y;
		this.side = side;
	}

	public GridBorder(GridBorder copyThis) {
		this.x = copyThis.x;
		this.y = copyThis.y;
		this.side = copyThis.side;
	}
	
	public GridBorder clone(){
		try {
			return (GridBorder)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new ImpossibleException(e);
		}
	}

	public void set(int x, int y, TSide side) {
		this.x = x;
		this.y = y;
		this.side = side;
	}

	public boolean sameAs(int x, int y, TSide side) {
		if(this.x == x && this.y == y && this.side == side)
			return true;
		if(this.side == null) 
			return false;
		switch(this.side){
		case LEFT:
			return this.y == y && this.x == x + 1 && side.isRight();
		case RIGHT:
			return this.y == y && this.x == x - 1 && side.isLeft();
		case VERTICAL:
			if(!side.isVertical() || this.x != x)
				return false;
			if(TrigonUtil.doesTrianglePointUp(x, y)){
				return this.y == y + 1;
			} else {
				return this.y == y - 1;
			}
		default:
			throw new IllegalStateException("Unknown side type: " + this.side);
		}
	}

	public boolean sameAs(GridBorder border) {
		if(border == null) return false;
		return sameAs(border.x, border.y, border.side);
	}
	
	public int hashCode(){
		if(this.side == TSide.VERTICAL){
			if(TrigonUtil.doesTrianglePointUp(x, y)){
				return (x * 17 + y) << 1 + 1;
			} else {
				return (x * 17 + y - 1) << 1 + 1;
			}
		} else if(this.side == TSide.RIGHT){
			return ((x-1) * 17 + y) << 1;
		} else {
			return (x * 17 + y) << 1;
		}
	}
	
	public boolean equals(Object o){
		if(o == this) return true;
		if(!(o instanceof GridBorder)) return false;
		return sameAs((GridBorder)this);
	}
	
}
