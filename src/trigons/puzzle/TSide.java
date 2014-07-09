package trigons.puzzle;

public enum TSide {
	LEFT,
	RIGHT,
	VERTICAL;
	
	public static final TSide TOP = VERTICAL,
			BOTTOM = VERTICAL,
			VERT = VERTICAL;
	
	public boolean isVertical(){
		return this == VERTICAL;
	}
	
	public boolean isHorizontal(){
		return this != VERTICAL;
	}
	
	public boolean isLeft(){
		return this == LEFT;
	}
	
	public boolean isRight(){
		return this == RIGHT;
	}

	public TSide getOpposite() {
		if(this == LEFT) return RIGHT;
		if(this == RIGHT) return LEFT;
		return VERTICAL;
	}
}
