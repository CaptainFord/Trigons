package trigons.swing;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import trigons.puzzle.Trigon;
import trigons.puzzle.TrigonBorder;

public abstract class AbstractTrigonModel implements TrigonModel {

	protected List<TrigonModelListener> listeners =
			new CopyOnWriteArrayList<TrigonModelListener>();
	
	@Override
	public void addTrigonListener(TrigonModelListener l) {
		listeners.add(l);
	}

	@Override
	public void removeTrigonListener(TrigonModelListener l) {
		listeners.remove(l);
	}
	
	protected void fireBorderValueChanged(TrigonBorder border){
		/*int x, y;
		Trigon t = border.getFirstTrigon();
		if(t == null){
			t = border.getSecondTrigon();
			if(t == null){
				ErrUtil.warningMsg("border is not attatched to any trigons: " + border);
				return;
			}
			x = t.getX();
			y = t.getY();
			if(border.isVertical()){
				--y;
			} else {
				--x;
			}
		} else {
			x = t.getX();
			y = t.getY();
		}*/
		fireBorderValueChanged(border.getFirstTrigonX(), border.getFirstTrigonY(), border.isVertical() ? SIDE_VERT : SIDE_RIGHT);
	}
	
	protected void fireTrigonValueChanged(Trigon trigon){
		int x = trigon.getX();
		int y = trigon.getY();
		for(TrigonModelListener l : listeners){
			l.trigonValueChanged(this, x, y);
		}
	}

	protected void fireBorderValueChanged(int x, int y, int side){
		if(listeners.isEmpty()) return;
		TrigonBorderEvent evt = new TrigonBorderEvent(this, x, y, side);
		for(TrigonModelListener l : listeners){
			l.trigonBorderValueChanged(evt);
		}
	}
	
	protected void fireTrigonValueChanged(int x, int y){
		for(TrigonModelListener l : listeners){
			l.trigonValueChanged(this, x, y);
		}
	}
	
	protected void fireBoundsChanged(){
		for(TrigonModelListener l : listeners){
			l.trigonPuzzleBoundsChanged(this);
		}
	}

}
