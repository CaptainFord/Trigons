package trigons.swing;

import trigons.puzzle.TSide;

public interface TrigonModel {
	public static final int SIDE_LEFT = 0;
	public static final int SIDE_TOP = 1, SIDE_BOTTOM = 1, SIDE_VERT = 1;
	public static final int SIDE_RIGHT = 2;
	
	void addTrigonListener(TrigonModelListener l);
	void removeTrigonListener(TrigonModelListener l);
	
	int getMinX();
	int getMaxX();
	int getMinY();
	int getMaxY();
	
	/**
	 * Gets the value of the triangle at the specified x, y coordinates.
	 * A value of -1 indicates that there is a triangle, but that it has no value.
	 * A value of -2 indicates that there is no triangle at that location.
	 * @param x
	 * @param y
	 * @return
	 * 		the value of the specified triangle, or -1 for no triangle
	 */
	int getTriValueAt(int x, int y);
	
	/**
	 * Gets the value filled in at one of the borders of a triangle. Returns -1 
	 * if the border has no value, or -2 if there is no border at that location.
	 * <p>
	 * Each border is shared by two triangles, and is accessible from either of them.
	 * </p>
	 * The value of <code>side</code> may be one of the constants SIDE_LEFT,
	 * SIDE_RIGHT, SIDE_TOP, SIDE_BOTTOM or SIDE_VERT. The latter three all
	 * have the same value -- it's just for clarity, since it's the only
	 * difference between the two possible orientations of triangles.
	 * @param x
	 * @param y
	 * @param side
	 * @return
	 * 		the value filled in at the specified border, -1 if no value,
	 * or -2 if no border
	 */
	int getBorderValueAt(int x, int y, int side);
	
	/**
	 * Gets the value filled in at one of the borders of a triangle. Returns -1 
	 * if the border has no value, or -2 if there is no border at that location.
	 * <p>
	 * Each border is shared by two triangles, and is accessible from either of them.
	 * @param x
	 * @param y
	 * @param side
	 * @return
	 * 		the value filled in at the specified border, -1 if no value,
	 * or -2 if no border
	 */
	int getBorderValueAt(int x, int y, TSide side);
	
	/**
	 * Tests if there is a trigon located at the specified grid coordinates.
	 * @param x
	 * @param y
	 * @return
	 * 		true if there is a trigon there, false otherwise
	 */
	boolean hasTriangleAt(int x, int y);	
}
