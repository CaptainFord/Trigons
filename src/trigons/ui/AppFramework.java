package trigons.ui;

import trigons.puzzle.TrigonPuzzle;
import trigons.swing.JTrigon;

public class AppFramework {
	private static final int MODE_VIEW = 0;
	private static final int MODE_EDIT = 1;
	private static final int MODE_PLAY = 2;

	public final ViewerActions viewerActions = new ViewerActions(this);
	public final EditorActions editorActions = new EditorActions(this);
	
	public final MainWindow mainWindow;
	
	public final KeyboardInterface keyInterface = new KeyboardInterface(this);
	
	private int currentMode;
	
	public AppFramework(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}
	public void finishInit(){
		viewerActions.init();
		editorActions.init();
		keyInterface.init(mainWindow.trigon);
	}

	public boolean isEditModeOn() {
		return true;
//		return currentMode == MODE_EDIT;
	}

	public void exitEditMode() {
		// TODO Auto-generated method stub
		
	}

	public void enterEditMode() {
		// TODO Auto-generated method stub
		
	}

	public TrigonPuzzle getPuzzle() {
		return mainWindow.getPuzzle();
	}
	public JTrigon getTrigonViewer() {
		return mainWindow.trigon;
	}
}
