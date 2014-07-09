package trigons.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import trigons.puzzle.TSide;
import trigons.puzzle.Trigon;
import trigons.puzzle.TrigonBorder;
import trigons.swing.GridBorder;
import trigons.swing.JTrigon;
import trigons.swing.TrigonModel;
import trigons.swing.TrigonSelectionListener;
import trigons.swing.TrigonUtil;
import vordeka.util.ErrUtil;
import vordeka.util.model.GridPoint;

public class KeyboardInterface implements KeyListener, TrigonSelectionListener {
	
	public final AppFramework framework;
	protected JTrigon editor;
	protected GridBorder editedBorder;
	protected GridPoint editedTriangle;
	protected int typedNumber = -1;
	
	

	public KeyboardInterface(AppFramework framework) {
		this.framework = framework;
	}
	public void init(JTrigon trigon){
		this.editor = trigon;
		trigon.addSelectionListener(this);
		trigon.addKeyListener(this);
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(key >= KeyEvent.VK_0 && key <= KeyEvent.VK_9){
			numeralTyped(key - KeyEvent.VK_0);
		} else if(key >= KeyEvent.VK_NUMPAD0 && key <= KeyEvent.VK_NUMPAD9){
			numeralTyped(key - KeyEvent.VK_NUMPAD0);
		} else {
			switch(e.getKeyCode()){
			case KeyEvent.VK_DOWN:
				navigateDown();
				break;
			case KeyEvent.VK_UP:
				navigateUp();
				break;
			case KeyEvent.VK_RIGHT:
				navigateRight();
				break;
			case KeyEvent.VK_LEFT:
				navigateLeft();
				break;
			case KeyEvent.VK_ENTER:
				finishEditing();
				break;
			case KeyEvent.VK_ESCAPE:
				cancelEditing();
				break;
			case KeyEvent.VK_DELETE:
				deleteSelected();
				break;
			case KeyEvent.VK_SPACE:
				blankSelected();
				break;
			}
		}
	}

	private void blankSelected() {
		GridBorder border = editor.getSelectedBorder();
		GridPoint trigon = editor.getSelectedTrigon();
		if(border != null){
			TrigonBorder triBorder = framework.getPuzzle().getBorder(border.x, border.y, border.side);
			if(triBorder != null){
				triBorder.setValue(-1);
			}
		} else if(trigon != null){
			framework.getPuzzle().addTrigon(trigon.x, trigon.y, -1);
		}
	}
	private void deleteSelected() {
		GridBorder border = editor.getSelectedBorder();
//		TrigonModel model = editor.getModel();
		if(border == null){
			GridPoint trigon = editor.getSelectedTrigon();
			if(trigon == null)
				return;
			Trigon tri = framework.getPuzzle().getTrigon(trigon.x, trigon.y);
			if(tri == null)
				return;
			if(tri.getValue() < 0){
				framework.getPuzzle().removeTrigon(trigon.x, trigon.y);
			} else {
				tri.setValue(-1);
			}
		} else {
			TrigonBorder triBorder = framework.getPuzzle().getBorder(border.x, border.y, border.side);
			if(triBorder != null)
				triBorder.setValue(-1);
		}
	}
	private void finishEditing() {
		if(typedNumber == -1) return;
		if(editedBorder != null){
			TrigonBorder border = framework.getPuzzle().getBorder(editedBorder.x, editedBorder.y, editedBorder.side);
			if(border == null){
				ErrUtil.warningMsg("edited border does not exist");
			} else {
				border.setValue(typedNumber);
			}
		} else if(editedTriangle != null){
			Trigon tri = framework.getPuzzle().getTrigon(editedTriangle.x, editedTriangle.y);
			if(tri == null){
				framework.getPuzzle().addTrigon(editedTriangle.x, editedTriangle.y, typedNumber);
			} else {
				tri.setValue(typedNumber);
			}
		}
		editedBorder = null;
		editedTriangle = null;
		typedNumber = -1;
		editor.setSelectedText(null);
	}

	private void numeralTyped(int number) {
		boolean isTriangle = false;
		if(editedBorder == null && editedTriangle == null){
			editedBorder = framework.getTrigonViewer().getSelectedBorder();
			if(editedBorder == null){
				editedTriangle = framework.getTrigonViewer().getSelectedTrigon();
				if(editedTriangle == null)
					return;
				isTriangle = true;
			}
		} else {
			isTriangle = editedTriangle != null;
		}
		int maxValue = isTriangle ? 3 * framework.getPuzzle().getMaxBorderValue() : framework.getPuzzle().getMaxBorderValue();
		typedNumber = typedNumber == -1 ? number : number + typedNumber * 10;
		if(typedNumber > maxValue){
			cancelEditing();
			return;
		} else if(typedNumber == 0 || typedNumber * 10 > maxValue){
			finishEditing();
			return;
		} else {
			editor.setSelectedText(typedNumber + "_");
		}
	}

	private void navigateLeft() {
		GridBorder border = editor.getSelectedBorder();
		TrigonModel model = editor.getModel();
		if(border == null){
			GridPoint trigon = editor.getSelectedTrigon();
			if(trigon == null){
				int x = (model.getMinX() + model.getMaxX() - 1) / 2;
				int y = (model.getMinY() + model.getMaxY()) / 2;
				editor.setSelectedTrigon(x, y);
			}
			if(model.getBorderValueAt(trigon.x, trigon.y, TSide.LEFT) == -2){
				editor.setSelectedTrigon(trigon.x-1, trigon.y);
			} else {
				editor.setSelectedBorder(new GridBorder(trigon, TSide.LEFT));
			}
		} else {
			if(border.side.isVertical()){
				if(model.getBorderValueAt(border.x-2, border.y, TSide.VERT) == -2){
					return;	//	Do nothing.
				} else {
					editor.setSelectedBorder(border.x-2, border.y, border.side);
				}
			} else {
				if(border.side.isLeft()){
					editor.setSelectedTrigon(border.x-1, border.y);
				} else {
					editor.setSelectedTrigon(border.x, border.y);
				}
			}
		}
		
	}

	private void navigateRight() {
		GridBorder border = editor.getSelectedBorder();
		TrigonModel model = editor.getModel();
		if(border == null){
			GridPoint trigon = editor.getSelectedTrigon();
			if(trigon == null){
				int x = (model.getMinX() + model.getMaxX() + 1) / 2;
				int y = (model.getMinY() + model.getMaxY()) / 2;
				editor.setSelectedTrigon(x, y);
			}
			if(model.getBorderValueAt(trigon.x, trigon.y, TSide.RIGHT) == -2){
				editor.setSelectedTrigon(trigon.x+1, trigon.y);
			} else {
				editor.setSelectedBorder(new GridBorder(trigon, TSide.RIGHT));
			}
		} else {
			if(border.side.isVertical()){
				if(model.getBorderValueAt(border.x+2, border.y, TSide.VERT) == -2){
					return;	//	Do nothing.
				} else {
					editor.setSelectedBorder(border.x+2, border.y, border.side);
				}
			} else {
				if(border.side.isRight()){
					editor.setSelectedTrigon(border.x+1, border.y);
				} else {
					editor.setSelectedTrigon(border.x, border.y);
				}
			}
		}
	}

	private void navigateUp() {
		GridBorder border = editor.getSelectedBorder();
		TrigonModel model = editor.getModel();
		if(border == null){
			GridPoint trigon = editor.getSelectedTrigon();
			if(trigon == null){
				int x = (model.getMinX() + model.getMaxX()) / 2;
				int y = (model.getMinY() + model.getMaxY() - 1) / 2;
				editor.setSelectedTrigon(x, y);
			}
			boolean pointsUp = TrigonUtil.doesTrianglePointUp(trigon.x, trigon.y);
			if(pointsUp || model.getBorderValueAt(trigon.x, trigon.y, TSide.VERT) == -2){
				editor.setSelectedTrigon(trigon.x, trigon.y-1);
			} else {
				editor.setSelectedBorder(trigon.x, trigon.y, TSide.VERT);
			}
		} else {
			boolean pointsUp = TrigonUtil.doesTrianglePointUp(border.x, border.y);
			if(border.side.isVertical()){
				if(pointsUp){
					editor.setSelectedTrigon(border.x, border.y);
				} else {
					editor.setSelectedTrigon(border.x, border.y-1);
				}
			} else if(border.side.isLeft()){
				if(pointsUp){
					editor.setSelectedTrigon(border.x-1, border.y);
				} else {
					editor.setSelectedTrigon(border.x, border.y);
				}
			} else {
				if(pointsUp){
					editor.setSelectedTrigon(border.x+1, border.y);
				} else {
					editor.setSelectedTrigon(border.x, border.y);
				}
			}
		}
	}

	private void navigateDown() {
		GridBorder border = editor.getSelectedBorder();
		TrigonModel model = editor.getModel();
		if(border == null){
			GridPoint trigon = editor.getSelectedTrigon();
			if(trigon == null){
				int x = (model.getMinX() + model.getMaxX()) / 2;
				int y = (model.getMinY() + model.getMaxY() + 1) / 2;
				editor.setSelectedTrigon(x, y);
			}
			boolean pointsUp = TrigonUtil.doesTrianglePointUp(trigon.x, trigon.y);
			if(!pointsUp || model.getBorderValueAt(trigon.x, trigon.y, TSide.VERT) == -2){
				editor.setSelectedTrigon(trigon.x, trigon.y+1);
			} else {
				editor.setSelectedBorder(trigon.x, trigon.y, TSide.VERT);
			}
		} else {
			boolean pointsDown = !TrigonUtil.doesTrianglePointUp(border.x, border.y);
			if(border.side.isVertical()){
				if(pointsDown){
					editor.setSelectedTrigon(border.x, border.y);
				} else {
					editor.setSelectedTrigon(border.x, border.y+1);
				}
			} else if(border.side.isLeft()){
				if(pointsDown){
					editor.setSelectedTrigon(border.x-1, border.y);
				} else {
					editor.setSelectedTrigon(border.x, border.y);
				}
			} else {
				if(pointsDown){
					editor.setSelectedTrigon(border.x+1, border.y);
				} else {
					editor.setSelectedTrigon(border.x, border.y);
				}
			}
		}
	}


	@Override
	public void selectionChanged(JTrigon source) {
		cancelEditing();
	}

	private void cancelEditing() {
		editedBorder = null;
		editedTriangle = null;
		typedNumber = -1;
		framework.getTrigonViewer().setSelectedText(null);
	}

}
