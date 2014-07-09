package trigons.puzzle;

public abstract class TrigonBorder {
	public abstract boolean isVertical();
	
	/**
	 * The first trigon is either the trigon to the north (if vertical), 
	 * or the west (if horizontal). 
	 * 
	 * @return
	 */
	public abstract Trigon getFirstTrigon();
	public abstract Trigon getSecondTrigon();
	
	public abstract int getValue();
	public abstract void setValue(int value);
	
	public boolean isHorizontal(){
		return !isVertical();
	}
	
	public Trigon getTopTrigon(){
		return getFirstTrigon();
	}
	
	public Trigon getBottomTrigon(){
		return getSecondTrigon();
	}
	
	public Trigon getLeftTrigon(){
		return getFirstTrigon();
	}
	
	public Trigon getRightTrigon(){
		return getSecondTrigon();
	}
	
	/**
	 * Returns Integer.MIN_VALUE if impossible to resolve
	 * @return
	 */
	public int getFirstTrigonX(){
		Trigon tri = getFirstTrigon();
		if(tri != null){
			return tri.getX();
		}
		tri = getSecondTrigon();
		if(tri != null){
			if(isHorizontal()){
				return tri.getX() - 1;
			} else {
				return tri.getX();
			}
		}
		return Integer.MIN_VALUE;
	}
	public int getFirstTrigonY(){
		Trigon tri = getFirstTrigon();
		if(tri != null){
			return tri.getY();
		}
		tri = getSecondTrigon();
		if(tri != null){
			if(isVertical()){
				return tri.getY() - 1;
			} else {
				return tri.getY();
			}
		}
		return Integer.MIN_VALUE;
	}
	
	public int getSecondTrigonX(){
		Trigon tri = getSecondTrigon();
		if(tri != null){
			return tri.getX();
		}
		tri = getFirstTrigon();
		if(tri != null){
			if(isHorizontal()){
				return tri.getX() + 1;
			} else {
				return tri.getX();
			}
		}
		return Integer.MIN_VALUE;
	}
	public int getSecondTrigonY(){
		Trigon tri = getSecondTrigon();
		if(tri != null){
			return tri.getY();
		}
		tri = getFirstTrigon();
		if(tri != null){
			if(isVertical()){
				return tri.getY() + 1;
			} else {
				return tri.getY();
			}
		}
		return Integer.MIN_VALUE;
	}
	
	public String toString(){
		StringBuilder b = new StringBuilder("{");

		int x, y;
		{
			Trigon trigon = getFirstTrigon();
			if(trigon == null){
				trigon = getSecondTrigon();
				if(trigon == null){
					x = Integer.MIN_VALUE;
					y = Integer.MIN_VALUE;
				} else {
					x = trigon.getX();
					y = trigon.getY();
					if(isVertical()){
						--y;
					} else {
						--x;
					}
				}
			} else {
				x = trigon.getX();
				y = trigon.getY();
			}
		}
		if(x == Integer.MIN_VALUE){
			b.append("?,?");
		} else {
			b.append(x).append(',').append(y);
		}
		b.append(',').append(isVertical() ? 'v' : 'h').append(':');
		
		int value = getValue();
		if(value < 0) 	b.append('_');
		else			b.append(value);
		
		return b.append('}').toString();
	}
	
	public int hashCode(){
		return ((getFirstTrigonX() * 17 + getFirstTrigonY()) << 1) + (isVertical() ? 1 : 0);
	}
	
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof TrigonBorder)) return false;
		TrigonBorder tb = (TrigonBorder)o;
		return this.isVertical() == tb.isVertical() && this.getFirstTrigonX() == tb.getFirstTrigonX() && this.getFirstTrigonY() == tb.getFirstTrigonY();
	}

	/**
	 * Tests if this border is shared between two trigons. If false, then
	 * it only belongs to one trigon (or zero, if the trigons it was attached 
	 * to were removed, in which case this border is no longer actually a part
	 *  of a puzzle).
	 * @return
	 * 		true if this border is shared by two trigons, false otherwise
	 */
	public boolean isSharedBorder() {
		return getFirstTrigon() != null && getSecondTrigon() != null;
	}

	/**
	 * Gets the side of this border in relation to its first trigon.
	 * @return
	 * 		the side of this border in its first trigon
	 */
	public TSide getFirstTrigonSide() {
		return this.isHorizontal() ? TSide.RIGHT : TSide.VERTICAL;
	}
	
	/**
	 * Gets the side of this border in relation to its second trigon.
	 * @return
	 * 		the side of this border in its second trigon
	 */
	public TSide getSecondTrigonSide() {
		return this.isHorizontal() ? TSide.LEFT : TSide.VERTICAL;
	}
}
