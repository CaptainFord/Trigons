package trigons.swing;

import java.awt.Polygon;

import vordeka.util.MathUtil;

public class Triangle extends Polygon {
	private static final long serialVersionUID = 3475842751045029142L;
	
	public Triangle(int x0, int y0, int x1, int y1, int x2, int y2){
		this.xpoints = new int[] {x0, x1, x2};
		this.ypoints = new int[] {y0, y1, y2};
		this.npoints = 3;
	}
	
	public Triangle() {
		this.xpoints = new int[3];
		this.ypoints = new int[3];
		this.npoints = 3;
	}
	
	public void set(int x0, int y0, int x1, int y1, int x2, int y2){
		this.xpoints = new int[] {x0, x1, x2};
		this.ypoints = new int[] {y0, y1, y2};
		this.npoints = 3;
	}

	/**
	 * Creates a triangle that fits the specified bounds and facing.
	 * <p>
	 * The first coordinate (x0,y0) is always the "tip", and it always winds clockwise.
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param pointsUp
	 * @return
	 */
	public static Triangle getTri(int x, int y, int width, int height, boolean pointsUp){
		int x0, y0, x1, y1, x2, y2;
		x0 = x + width / 2;
		if(pointsUp){
			y0 = y;
			x1 = x + width;
			y1 = y + height;
			x2 = x;
			y2 = y + height;
		} else {
			y0 = y + height;
			x1 = x;
			y1 = y;
			x2 = x + width;
			y2 = y;
		}
		return new Triangle(x0,y0,x1,y1,x2,y2);
	}

	public void setTri(int x, int y, int width, int height, boolean pointsUp) {
		int x0, y0, x1, y1, x2, y2;
		x0 = x + width / 2;
		if(pointsUp){
			y0 = y;
			x1 = x + width;
			y1 = y + height;
			x2 = x;
			y2 = y + height;
		} else {
			y0 = y + height;
			x1 = x;
			y1 = y;
			x2 = x + width;
			y2 = y;
		}
		this.set(x0,y0,x1,y1,x2,y2);
	}
	
	public int getLeft(){
		return MathUtil.min(xpoints);
	}
	
	public int getRight(){
		return MathUtil.max(xpoints);
	}
	
	public int getMiddleX(){
//		return MathUtil.median(xpoints);
		return (getLeft() + getRight()) / 2;
	}
	
	public int getMinY(){
		return MathUtil.min(ypoints);
	}
	
	public int getMaxY(){
		return MathUtil.max(ypoints);
	}
	
	public int getMiddleY(){
		return (getMinY() + getMaxY()) / 2;
	}
}
