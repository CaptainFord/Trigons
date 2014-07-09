package trigons.swing;

import java.awt.Point;

public class TrigonBorderEvent {

	private TrigonModel model;
	private int x, y;
	private boolean vertical;

	public TrigonBorderEvent(TrigonModel model, int x, int y, int side){
		this.model = model;
		if(side == TrigonModel.SIDE_VERT){
			this.x = x;
			vertical = true;
			this.y = TrigonUtil.doesTrianglePointUp(x, y) ? y : y - 1;
		} else if(side == TrigonModel.SIDE_LEFT){
			this.x = x-1;
			this.y = y;
			vertical = false;
		}  else if(side == TrigonModel.SIDE_RIGHT){
			this.x = x;
			this.y = y;
			vertical = false;
		} else {
			throw new IllegalArgumentException("Unknown side: " + side);
		}
	}
	
	public boolean isHorizontalSide(){
		return !vertical;
	}
	
	public boolean isVerticalSide(){
		return vertical;
	}
	
	
	public int getFirstX(){
		return x;
	}
	
	public int getSecondX(){
		return vertical ? x : x+1;
	}
	
	public int getFirstY(){
		return y;
	}
	
	public int getSecondY(){
		return vertical ? y+1 : y;
	}
	
	public Point getFirstTriangle(){
		return new Point(getFirstX(), getFirstY());
	}
	
	public Point getSecondTriangle(){
		return new Point(getSecondX(), getSecondY());
	}
	
	public int getSideValue(){
		return model.getBorderValueAt(x, y, vertical ? 
				TrigonModel.SIDE_VERT : TrigonModel.SIDE_RIGHT);
	}
}
