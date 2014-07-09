package vordeka.util.model;


/**
 * An alternate class for points. Essentially serves to differentiate
 * between points on the drawing canvas and points in a model.
 * @author Alex
 *
 */
public class GridPoint {
	public int x, y;
	
	public GridPoint(){
		
	}
	
	public GridPoint(int x, int y){
		this.x = x;
		this.y = y;
	}

	public GridPoint(GridPoint p) {
		this.x = p.x;
		this.y = p.y;
	}

	@Override
	public int hashCode() {
		return x * 31 + y;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GridPoint other = (GridPoint) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;

//		ErrUtil.debugMsg("Testing passed");
		return true;
	}
	
	
	public String toString(){
		return "(" + x + "," + y +")";
	}
	
}
